package contractorj.construction;

import com.google.common.collect.Sets;
import contractorj.construction.corral.CorralRunner;
import contractorj.construction.corral.QueryResult;
import contractorj.construction.corral.RunnerResult;
import contractorj.construction.queries.Answer;
import contractorj.construction.queries.Query;
import contractorj.construction.queries.invariant.ExceptionBreaksInvariantQuery;
import contractorj.construction.queries.invariant.TransitionBreaksInvariantQuery;
import contractorj.construction.queries.necessary_actions.NecessarilyDisabledActionQuery;
import contractorj.construction.queries.necessary_actions.NecessarilyEnabledActionQuery;
import contractorj.construction.queries.transition.NotThrowingTransitionQuery;
import contractorj.construction.queries.transition.ThrowingTransitionQuery;
import contractorj.model.Action;
import contractorj.model.Epa;
import contractorj.model.State;
import contractorj.model.Transition;
import contractorj.util.CombinationsGenerator;
import j2bpl.Class;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LazyEpaGenerator extends EpaGenerator {

    private ExecutorService driverExecutorService;

    private ExecutorService queriesExecutorService;

    private Set<State> statesAlreadyEnqueued;

    private Epa epa;

    private Phaser phaser;

    public LazyEpaGenerator(final String baseTranslation, final int numberOfThreads, final CorralRunner corralRunner) {

        super(baseTranslation, numberOfThreads, corralRunner);
    }

    @Override
    protected Epa generateEpaImplementation(final Class theClass) {

        final Thread.UncaughtExceptionHandler oldUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();

        try {

            // We change the default uncaught exception handler to manage exception in parallel streams.
            Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
                System.err.println("Uncaught exception in thread " + t.getName() + ": " + e.getMessage());
                e.printStackTrace(System.err);
                System.exit(1);
            });

            driverExecutorService = Executors.newCachedThreadPool();
            queriesExecutorService = Executors.newFixedThreadPool(numberOfThreads);
            statesAlreadyEnqueued = Sets.newHashSet();
            phaser = new Phaser();
            phaser.register();

            final State initialState = new State(constructors, Sets.newHashSet());

            epa = new Epa(theClass.getQualifiedJavaName(), initialState);

            enqueueStateIfNecessary(initialState);

            phaser.arriveAndAwaitAdvance();
            driverExecutorService.shutdown();
            queriesExecutorService.shutdown();

            driverExecutorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            queriesExecutorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

            return epa;

        } catch (InterruptedException e) {

            throw new RuntimeException(e);

        } finally {
            Thread.setDefaultUncaughtExceptionHandler(oldUncaughtExceptionHandler);
        }
    }

    private void analiseState(final State state) {

        for (final Action transition : state.enabledActions) {

            phaser.register();

            driverExecutorService.submit(() -> {

                try {
                    analiseStateAndAction(state, transition);
                } catch (Exception exception) {
                    System.err.println("Unhandled exception on thread " + Thread.currentThread().getName() + ":"
                            + exception.getMessage());
                    exception.printStackTrace();
                } finally {
                    phaser.arrive();
                }

            });

        }

    }

    private void analiseStateAndAction(final State state, final Action action) {

        final InvariantQueriesFuture invariantQueriesFuture = new InvariantQueriesFuture(state, action);

        final Set<NecessaryActionResult> necessaryActionResults = getNecessaryActionResults(
                state,
                action
        );

        ensureConsistentNecessaryActionResults(state, action, necessaryActionResults);

        final Set<Action> necessarilyEnabledActions = necessaryActionResults.stream()
                .filter(necessaryActionResult -> necessaryActionResult.necessarilyEnabled.equals(Answer.YES))
                .map(necessaryActionResult -> necessaryActionResult.action)
                .collect(Collectors.toSet());

        final Set<Action> necessarilyDisabledActions = necessaryActionResults.stream()
                .filter(necessaryActionResult -> necessaryActionResult.necessarilyDisabled.equals(Answer.YES))
                .map(necessaryActionResult -> necessaryActionResult.action)
                .collect(Collectors.toSet());

        final Set<Transition> enabledTransitions = getTransitionsFromStateWithAction(
                state,
                action,
                necessarilyEnabledActions,
                necessarilyDisabledActions
        );

        analiseInvariantBreakage(state, action, invariantQueriesFuture);

        for (final Transition enabledTransition : enabledTransitions) {

            final State targetState = enabledTransition.target;

            enqueueStateIfNecessary(targetState);

            epa.addTransition(enabledTransition);
        }

    }

    private void analiseInvariantBreakage(final State state,
                                          final Action transition,
                                          final InvariantQueriesFuture invariantQueriesFuture) {

        final InvariantQueriesResult invariantQueriesResult = invariantQueriesFuture.get();

        final Answer transitionBreaksInvariant = invariantQueriesResult.transitionBreaksInvariant;

        if (!transitionBreaksInvariant.equals(Answer.NO)) {
            epa.addTransition(new Transition(
                    state,
                    getErrorState(),
                    transition,
                    transitionBreaksInvariant.equals(Answer.MAYBE),
                    false
            ));
        }

        final Answer exceptionBreaksInvariant = invariantQueriesResult.exceptionBreaksInvariant;

        if (!exceptionBreaksInvariant.equals(Answer.NO)) {
            epa.addTransition(new Transition(
                    state,
                    getErrorState(),
                    transition,
                    exceptionBreaksInvariant.equals(Answer.MAYBE),
                    true
            ));
        }

    }

    private void ensureConsistentNecessaryActionResults(final State state,
                                                        final Action transition,
                                                        final Set<NecessaryActionResult> actionsStatus) {

        final List<Action> conflictingActions = actionsStatus.stream()
                .filter(necessaryActionResult -> necessaryActionResult.necessarilyDisabled.equals(Answer.YES)
                        && necessaryActionResult.necessarilyEnabled.equals(Answer.YES))
                .map(necessaryActionResult -> necessaryActionResult.action)
                .collect(Collectors.toList());

        if (!conflictingActions.isEmpty()) {
            throw new IllegalStateException("Going through transition " + transition.toString() + " from state "
                    + state + " makes " + Arrays.toString(conflictingActions.toArray())
                    + " both enabled and disabled.");
        }
    }

    private Set<Transition> getTransitionsFromStateWithAction(final State state,
                                                              final Action action,
                                                              final Set<Action> necessarilyEnabledActions,
                                                              final Set<Action> necessarilyDisabledActions) {

        final Set<Action> uncertainActions = Sets.difference(
                Sets.difference(actions, necessarilyEnabledActions),
                necessarilyDisabledActions
        );

        final CombinationsGenerator<Action> combinationsGenerator = new CombinationsGenerator<>();
        final Set<Set<Action>> combinations = combinationsGenerator.combinations(uncertainActions);

        return combinations.parallelStream()
                .map(maybeEnabledActions -> createTargetState(
                        necessarilyEnabledActions,
                        necessarilyDisabledActions,
                        uncertainActions,
                        maybeEnabledActions
                ))
                .map(targetState -> new TransitionQueriesResults(
                        action,
                        targetState,
                        getAnswer(new NotThrowingTransitionQuery(state, action, targetState, invariant)),
                        getAnswer(new ThrowingTransitionQuery(state, action, targetState, invariant))
                ))
                .flatMap(transitionQueriesResults -> {

                    final Optional<Transition> notThrowingTransition = getTransitionFromQueryResult(
                            state,
                            action,
                            transitionQueriesResults.targetState,
                            transitionQueriesResults.notThrowingTransitionAnswer,
                            false
                    );

                    final Optional<Transition> throwingTransition = getTransitionFromQueryResult(
                            state,
                            action,
                            transitionQueriesResults.targetState,
                            transitionQueriesResults.throwingTransitionAnswer,
                            true
                    );

                    return Stream.of(notThrowingTransition, throwingTransition)
                            .filter(Optional::isPresent)
                            .map(Optional::get);
                })
                .collect(Collectors.toSet());
    }

    private Optional<Transition> getTransitionFromQueryResult(State source, Action transitionAction, State target,
                                                              Answer answer, boolean throwing) {

        if (answer.equals(Answer.NO)) {
            return Optional.empty();
        }

        final Transition transition = new Transition(
                source,
                target,
                transitionAction,
                answer.equals(Answer.MAYBE),
                throwing
        );

        return Optional.of(transition);
    }

    private State getErrorState() {

        return new State(Sets.newHashSet(), Sets.newHashSet());
    }

    private synchronized void enqueueStateIfNecessary(State state) {

        if (statesAlreadyEnqueued.contains(state)) {
            return;
        }

        statesAlreadyEnqueued.add(state);

        phaser.register();

        driverExecutorService.submit(() -> {

            try {
                analiseState(state);
            } catch (Exception exception) {
                System.err.println("Unhandled exception on thread " + Thread.currentThread().getName() + ":"
                        + exception.getMessage());
                exception.printStackTrace();
            } finally {
                phaser.arrive();
            }

        });
    }

    private State createTargetState(
            final Set<Action> necessarilyEnabledActions,
            final Set<Action> necessarilyDisabledActions,
            final Set<Action> uncertainActions,
            final Set<Action> maybeEnabledActions) {

        final Set<Action> enabledActions = Sets.union(necessarilyEnabledActions, maybeEnabledActions);

        final Set<Action> disabledActions = Sets.union(
                necessarilyDisabledActions,
                Sets.difference(uncertainActions, maybeEnabledActions)
        );

        return new State(enabledActions, disabledActions);
    }

    private Set<NecessaryActionResult> getNecessaryActionResults(final State state, final Action transition) {

        return actions.parallelStream()
                .map(testedAction -> {
                    final NecessarilyEnabledActionQuery necessarilyEnabledActionQuery =
                            new NecessarilyEnabledActionQuery(state, transition, testedAction, invariant);
                    final NecessarilyDisabledActionQuery necessarilyDisabledActionQuery =
                            new NecessarilyDisabledActionQuery(state, transition, testedAction, invariant);

                    return new NecessaryActionResult(
                            testedAction,
                            getAnswer(necessarilyEnabledActionQuery),
                            getAnswer(necessarilyDisabledActionQuery)
                    );
                })
                .collect(Collectors.toSet());
    }

    private Answer getAnswer(final Query query) {

        try {
            return submitQuery(query).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private Future<Answer> submitQuery(final Query query) {

        phaser.register();
        return queriesExecutorService.submit(() -> {

            try {
                final RunnerResult runnerResult = runQuery(query);
                final QueryResult queryResult = runnerResult.queryResult;

                return query.getAnswer(queryResult);
            } finally {
                phaser.arrive();
            }
        });
    }

    private static class NecessaryActionResult {

        public final Action action;

        public final Answer necessarilyEnabled;

        public final Answer necessarilyDisabled;

        private NecessaryActionResult(final Action action,
                                      final Answer necessarilyEnabled,
                                      final Answer necessarilyDisabled) {

            this.action = action;
            this.necessarilyEnabled = necessarilyEnabled;
            this.necessarilyDisabled = necessarilyDisabled;
        }
    }

    private static class TransitionQueriesResults {

        public final Action transition;

        public final State targetState;

        public final Answer notThrowingTransitionAnswer;

        public final Answer throwingTransitionAnswer;

        private TransitionQueriesResults(final Action transition,
                                         final State targetState,
                                         final Answer notThrowingTransitionAnswer,
                                         final Answer throwingTransitionAnswer) {

            this.transition = transition;
            this.targetState = targetState;
            this.notThrowingTransitionAnswer = notThrowingTransitionAnswer;
            this.throwingTransitionAnswer = throwingTransitionAnswer;
        }
    }

    private class InvariantQueriesFuture {

        private final Future<Answer> transitionBreaksInvariantFuture;

        private final Future<Answer> exceptionBreaksInvariantFuture;

        public InvariantQueriesFuture(State state, Action transition) {

            this(
                    new TransitionBreaksInvariantQuery(state, transition, invariant),
                    new ExceptionBreaksInvariantQuery(state, transition, invariant)
            );

        }

        private InvariantQueriesFuture(final TransitionBreaksInvariantQuery transitionBreaksInvariantQuery,
                                       final ExceptionBreaksInvariantQuery exceptionBreaksInvariantQuery) {

            this.transitionBreaksInvariantFuture = submitQuery(transitionBreaksInvariantQuery);
            this.exceptionBreaksInvariantFuture = submitQuery(exceptionBreaksInvariantQuery);
        }

        public InvariantQueriesResult get() {

            try {
                return new InvariantQueriesResult(
                        transitionBreaksInvariantFuture.get(),
                        exceptionBreaksInvariantFuture.get()
                );
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private static class InvariantQueriesResult {

        public Answer transitionBreaksInvariant;

        public Answer exceptionBreaksInvariant;

        public InvariantQueriesResult(final Answer transitionBreaksInvariant,
                                      final Answer exceptionBreaksInvariant) {

            this.transitionBreaksInvariant = transitionBreaksInvariant;
            this.exceptionBreaksInvariant = exceptionBreaksInvariant;
        }
    }

}
