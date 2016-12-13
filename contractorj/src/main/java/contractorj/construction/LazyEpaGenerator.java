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

        state.getEnabledActions().forEach(action ->
                runOnDriverExecutorService(() -> analiseStateAndAction(state, action))
        );
    }

    private void runOnDriverExecutorService(AsyncTask asyncTask) {

        phaser.register();
        driverExecutorService.submit(() -> {

            try {
                asyncTask.run();
            } catch (Exception exception) {
                System.err.println("Unhandled exception on thread " + Thread.currentThread().getName() + ":"
                        + exception.getMessage());
                exception.printStackTrace();
            } finally {
                phaser.arrive();
            }

        });
    }

    private void analiseStateAndAction(final State state, final Action mainAction) {

        final Set<NecessaryActionResult> necessaryActionResults = getNecessaryActionResults(
                state,
                mainAction
        );

        ensureConsistentNecessaryActionResults(state, mainAction, necessaryActionResults);

        final Set<Action> necessarilyEnabledActions = necessaryActionResults.stream()
                .filter(necessaryActionResult -> necessaryActionResult.necessarilyEnabled.equals(Answer.YES))
                .map(necessaryActionResult -> necessaryActionResult.testedAction)
                .collect(Collectors.toSet());

        final Set<Action> necessarilyDisabledActions = necessaryActionResults.stream()
                .filter(necessaryActionResult -> necessaryActionResult.necessarilyDisabled.equals(Answer.YES))
                .map(necessaryActionResult -> necessaryActionResult.testedAction)
                .collect(Collectors.toSet());

        final Stream<Query> queryStream = Stream.concat(
                getInvariantTestQueries(state, mainAction),
                getTransitionQueries(state, mainAction, necessarilyEnabledActions, necessarilyDisabledActions)
        );

        queryStream.forEach(query -> runOnDriverExecutorService(() -> {

            final Optional<Transition> maybeTransition = query.getTransition(getAnswer(query));

            if (maybeTransition.isPresent()) {

                final Transition transition = maybeTransition.get();

                enqueueStateIfNecessary(transition.getTarget());
                epa.addTransition(transition);
            }

        }));
    }

    private void ensureConsistentNecessaryActionResults(final State state,
                                                        final Action mainAction,
                                                        final Set<NecessaryActionResult> necessaryActionResults) {

        final List<Action> conflictingActions = necessaryActionResults.stream()
                .filter(necessaryActionResult -> necessaryActionResult.necessarilyDisabled.equals(Answer.YES)
                        && necessaryActionResult.necessarilyEnabled.equals(Answer.YES))
                .map(necessaryActionResult -> necessaryActionResult.testedAction)
                .collect(Collectors.toList());

        if (!conflictingActions.isEmpty()) {
            throw new IllegalStateException("Going through testedAction " + mainAction.toString() + " from state "
                    + state + " makes " + Arrays.toString(conflictingActions.toArray())
                    + " both enabled and disabled.");
        }
    }

    private Stream<Query> getInvariantTestQueries(final State state, final Action mainAction) {

        return Stream.of(
                new TransitionBreaksInvariantQuery(state, mainAction, invariant),
                new ExceptionBreaksInvariantQuery(state, mainAction, invariant)
        );
    }

    private Stream<Query> getTransitionQueries(final State state,
                                               final Action mainAction,
                                               final Set<Action> necessarilyEnabledActions,
                                               final Set<Action> necessarilyDisabledActions) {

        final Set<Action> uncertainActions = Sets.difference(
                Sets.difference(actions, necessarilyEnabledActions),
                necessarilyDisabledActions
        );

        final CombinationsGenerator<Action> combinationsGenerator = new CombinationsGenerator<>();
        final Set<Set<Action>> combinations = combinationsGenerator.combinations(uncertainActions);

        return combinations.stream()
                .map(maybeEnabledActions -> createTargetState(
                        necessarilyEnabledActions,
                        necessarilyDisabledActions,
                        uncertainActions,
                        maybeEnabledActions
                ))
                .flatMap(targetState -> {

                    final NotThrowingTransitionQuery notThrowingTransitionQuery = new NotThrowingTransitionQuery(
                            state,
                            mainAction,
                            targetState,
                            invariant
                    );

                    final ThrowingTransitionQuery throwingTransitionQuery = new ThrowingTransitionQuery(
                            state,
                            mainAction,
                            targetState,
                            invariant
                    );

                    return Stream.of(notThrowingTransitionQuery, throwingTransitionQuery);
                });
    }

    private synchronized void enqueueStateIfNecessary(State state) {

        if (statesAlreadyEnqueued.contains(state)) {
            return;
        }

        statesAlreadyEnqueued.add(state);

        runOnDriverExecutorService(() -> analiseState(state));
    }

    private State createTargetState(final Set<Action> necessarilyEnabledActions,
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

    private Set<NecessaryActionResult> getNecessaryActionResults(final State state, final Action mainAction) {

        return actions.parallelStream()
                .map(testedAction -> {

                    if (!testedAction.getStatePrecondition().isPresent()) {
                        return new NecessaryActionResult(
                                testedAction,
                                Answer.YES,
                                Answer.NO
                        );
                    }

                    final NecessarilyEnabledActionQuery necessarilyEnabledActionQuery =
                            new NecessarilyEnabledActionQuery(state, mainAction, testedAction, invariant);
                    final NecessarilyDisabledActionQuery necessarilyDisabledActionQuery =
                            new NecessarilyDisabledActionQuery(state, mainAction, testedAction, invariant);

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

        public final Action testedAction;

        public final Answer necessarilyEnabled;

        public final Answer necessarilyDisabled;

        private NecessaryActionResult(final Action testedAction,
                                      final Answer necessarilyEnabled,
                                      final Answer necessarilyDisabled) {

            this.testedAction = testedAction;
            this.necessarilyEnabled = necessarilyEnabled;
            this.necessarilyDisabled = necessarilyDisabled;
        }
    }

    private interface AsyncTask {

        void run();

    }

}
