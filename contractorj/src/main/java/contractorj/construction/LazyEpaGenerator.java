package contractorj.construction;

import com.google.common.collect.Sets;
import contractorj.construction.corral.CorralRunner;
import contractorj.construction.corral.QueryResult;
import contractorj.construction.corral.RunnerResult;
import contractorj.construction.queries.Answer;
import contractorj.construction.queries.Query;
import contractorj.construction.queries.invariant.ExceptionBreaksInvariantQuery;
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

        try {
            driverExecutorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            queriesExecutorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return epa;
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

        final Future<Answer> exceptionBreaksInvariantFuture = runExceptionBreaksInvariantQuery(state, action);

        final Set<NecessaryActionStatus> necessaryActionStatuses = getActionStatuses(
                state,
                action
        );

        ensureConsistentActionStatuses(state, action, necessaryActionStatuses);

        final Set<Action> necessarilyEnabledActions = necessaryActionStatuses.stream()
                .filter(necessaryActionStatus -> necessaryActionStatus.necessarilyEnabled.equals(Answer.YES))
                .map(necessaryActionStatus -> necessaryActionStatus.action)
                .collect(Collectors.toSet());

        final Set<Action> necessarilyDisabledActions = necessaryActionStatuses.stream()
                .filter(necessaryActionStatus -> necessaryActionStatus.necessarilyDisabled.equals(Answer.YES))
                .map(necessaryActionStatus -> necessaryActionStatus.action)
                .collect(Collectors.toSet());

        final Set<Transition> enabledTransitions = getTransitionsFromStateWithAction(
                state,
                action,
                necessarilyEnabledActions,
                necessarilyDisabledActions
        );

        analiseExceptionBreaksInvariantAnswer(state, action, exceptionBreaksInvariantFuture);

        for (final Transition enabledTransition : enabledTransitions) {

            final State targetState = enabledTransition.target;

            enqueueStateIfNecessary(targetState);

            epa.addTransition(enabledTransition);
        }

    }

    private Future<Answer> runExceptionBreaksInvariantQuery(final State state, final Action transition) {

        final Query exceptionBreaksInvariantQuery = new ExceptionBreaksInvariantQuery(state, transition, invariant);
        return submitQuery(exceptionBreaksInvariantQuery);
    }

    private void analiseExceptionBreaksInvariantAnswer(final State state,
                                                       final Action transition,
                                                       final Future<Answer> exceptionBreaksInvariantFuture) {

        final Answer exceptionBreaksInvariantAnswer = getAnswer(exceptionBreaksInvariantFuture);

        if (exceptionBreaksInvariantAnswer.equals(Answer.NO)) {
            return;
        }

        epa.addTransition(new Transition(
                state,
                getErrorState(),
                transition,
                exceptionBreaksInvariantAnswer.equals(Answer.MAYBE),
                true
        ));
    }

    private void ensureConsistentActionStatuses(final State state,
                                                final Action transition,
                                                final Set<NecessaryActionStatus> actionsStatus) {

        final List<Action> conflictingActions = actionsStatus.stream()
                .filter(necessaryActionStatus -> necessaryActionStatus.necessarilyDisabled.equals(Answer.YES)
                        && necessaryActionStatus.necessarilyEnabled.equals(Answer.YES))
                .map(necessaryActionStatus -> necessaryActionStatus.action)
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

        return combinations.stream()
                .map(maybeEnabledActions -> {

                    final State targetState = createTargetState(
                            necessarilyEnabledActions,
                            necessarilyDisabledActions,
                            uncertainActions,
                            maybeEnabledActions
                    );

                    final Query notThrowingTransitionQuery = new NotThrowingTransitionQuery(state, action, targetState,
                            invariant);

                    final Query throwingTransitionQuery = new ThrowingTransitionQuery(state, action,
                            targetState, invariant);

                    return new TransitionQueriesFutures(
                            action,
                            targetState,
                            submitQuery(notThrowingTransitionQuery),
                            submitQuery(throwingTransitionQuery)
                    );
                })
                .flatMap(transitionQueriesFutures -> {

                    final Optional<Transition> notThrowingTransition = getTransitionFromQuery(
                            state,
                            action,
                            transitionQueriesFutures.targetState,
                            getAnswer(transitionQueriesFutures.notThrowingTransitionAnswer),
                            false
                    );

                    final Optional<Transition> throwingTransition = getTransitionFromQuery(
                            state,
                            action,
                            transitionQueriesFutures.targetState,
                            getAnswer(transitionQueriesFutures.throwingTransitionAnswer),
                            true
                    );

                    return Stream.of(notThrowingTransition, throwingTransition)
                            .filter(Optional::isPresent)
                            .map(Optional::get);
                })
                .collect(Collectors.toSet());
    }

    private Answer getAnswer(Future<Answer> future) {

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<Transition> getTransitionFromQuery(State source, Action transitionAction, State target,
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

    private Set<NecessaryActionStatus> getActionStatuses(final State state, final Action transition) {

        return actions.stream()
                .map(action -> new NecessaryActionFuture(
                        action,
                        new NecessarilyEnabledActionQuery(state, transition, action, invariant),
                        new NecessarilyDisabledActionQuery(state, transition, action, invariant)
                ))
                .map(NecessaryActionFuture::get)
                .collect(Collectors.toSet());
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

    private class NecessaryActionFuture {

        private final Action action;

        private final Future<Answer> enabledFutureResult;

        private final Future<Answer> disabledFutureResult;

        private NecessaryActionFuture(final Action action,
                                      final NecessarilyEnabledActionQuery necessarilyEnabledActionQuery,
                                      final NecessarilyDisabledActionQuery necessarilyDisabledActionQuery) {

            this.action = action;
            this.enabledFutureResult = submitQuery(necessarilyEnabledActionQuery);
            this.disabledFutureResult = submitQuery(necessarilyDisabledActionQuery);
        }

        public NecessaryActionStatus get() {

            try {
                return new NecessaryActionStatus(
                        action,
                        enabledFutureResult.get(),
                        disabledFutureResult.get()
                );
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }

        }

    }

    private static class NecessaryActionStatus {

        public final Action action;

        public final Answer necessarilyEnabled;

        public final Answer necessarilyDisabled;

        private NecessaryActionStatus(final Action action,
                                      final Answer necessarilyEnabled,
                                      final Answer necessarilyDisabled) {

            this.action = action;
            this.necessarilyEnabled = necessarilyEnabled;
            this.necessarilyDisabled = necessarilyDisabled;
        }
    }

    private static class TransitionQueriesFutures {

        public final Action transition;

        public final State targetState;

        public final Future<Answer> notThrowingTransitionAnswer;

        public final Future<Answer> throwingTransitionAnswer;

        private TransitionQueriesFutures(final Action transition,
                                         final State targetState,
                                         final Future<Answer> notThrowingTransitionAnswer,
                                         final Future<Answer> throwingTransitionAnswer) {

            this.transition = transition;
            this.targetState = targetState;
            this.notThrowingTransitionAnswer = notThrowingTransitionAnswer;
            this.throwingTransitionAnswer = throwingTransitionAnswer;
        }
    }

}
