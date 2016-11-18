package contractorj.construction;

import com.google.common.collect.Sets;
import contractorj.construction.corral.CorralRunner;
import contractorj.construction.corral.Result;
import contractorj.model.Action;
import contractorj.model.Epa;
import contractorj.model.State;
import contractorj.model.Transition;
import contractorj.util.CombinationsGenerator;
import j2bpl.Class;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

        final Set<ActionStatus> actionsStatus = getNecessarilyEnabledOrDisabledActions(
                state,
                action
        );

        final List<Action> conflictingActions = actionsStatus.stream()
                .filter(actionStatus -> actionStatus.necessarilyDisabled && actionStatus.necessarilyEnabled)
                .map(actionStatus -> actionStatus.action)
                .collect(Collectors.toList());

        if (!conflictingActions.isEmpty()) {
            throw new IllegalStateException("Going through transition " + action.toString() + " from state "
                    + state + " makes " + Arrays.toString(conflictingActions.toArray())
                    + " both enabled and disabled.");
        }

        final Set<Action> necessarilyEnabledActions = actionsStatus.stream()
                .filter(actionStatus -> actionStatus.necessarilyEnabled)
                .map(actionStatus -> actionStatus.action)
                .collect(Collectors.toSet());

        final Set<Action> necessarilyDisabledActions = actionsStatus.stream()
                .filter(actionStatus -> actionStatus.necessarilyDisabled)
                .map(actionStatus -> actionStatus.action)
                .collect(Collectors.toSet());

        final Set<Action> uncertainActions = Sets.difference(
                Sets.difference(actions, necessarilyEnabledActions),
                necessarilyDisabledActions
        );

        final CombinationsGenerator<Action> combinationsGenerator = new CombinationsGenerator<>();
        final Set<Set<Action>> combinations = combinationsGenerator.combinations(uncertainActions);

        final Set<Transition> enabledTransitions = combinations.stream()
                .map(maybeEnabledActions -> {

                    final State targetState = createTargetState(
                            necessarilyEnabledActions,
                            necessarilyDisabledActions,
                            uncertainActions,
                            maybeEnabledActions
                    );

                    final Query queryNotThrowing = createTransitionQuery(state, action, targetState, false, false);
                    final Query queryThrowingPreservingInvariant = createTransitionQuery(state, action, targetState,
                            true, true);
                    final Query queryThrowingNotPreservingInvariant = createTransitionQuery(state, action, targetState,
                            true, false);

                    return new TargetStateQueryFutures(
                            action,
                            targetState,
                            submitQuery(queryNotThrowing),
                            submitQuery(queryThrowingPreservingInvariant),
                            submitQuery(queryThrowingNotPreservingInvariant)
                    );
                })
                .flatMap(targetStateQueryFutures -> {

                    final Result notThrowingResult;
                    final Result throwingPreservingInvariantResult;
                    final Result throwingNotPreservingInvariantResult;

                    try {
                        notThrowingResult = targetStateQueryFutures.futureResultWithoutThrowing.get();
                        throwingPreservingInvariantResult = targetStateQueryFutures.
                                futureResultThrowingPreservingInvariant.get();
                        throwingNotPreservingInvariantResult = targetStateQueryFutures.
                                futureResultThrowingNotPreservingInvariant.get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }

                    final List<Transition> transitions = new ArrayList<>();

                    if (notThrowingResult.equals(Result.BROKEN_INVARIANT)) {
                        System.out.println("Application bug: calling method " + targetStateQueryFutures.transition
                                + " from state " + state + " breaks the invariant");
                        System.exit(1);
                    }

                    if (!notThrowingResult.equals(Result.NO_BUG)) {
                        transitions.add(new Transition(
                                state,
                                targetStateQueryFutures.targetState,
                                action,
                                !notThrowingResult.equals(Result.BUG_IN_QUERY),
                                false
                        ));
                    }

                    if (throwingNotPreservingInvariantResult.equals(Result.BROKEN_INVARIANT)) {
                        transitions.add(new Transition(
                                state,
                                new State(Sets.newHashSet(), Sets.newHashSet()),
                                action,
                                false,
                                true
                        ));
                    }

                    if (throwingNotPreservingInvariantResult.equals(Result.MAYBE_BUG)) {
                        transitions.add(new Transition(
                                state,
                                new State(Sets.newHashSet(), Sets.newHashSet()),
                                action,
                                true,
                                true
                        ));
                    }

                    if (throwingPreservingInvariantResult.equals(Result.BUG_IN_QUERY)
                            || throwingPreservingInvariantResult.equals(Result.MAYBE_BUG)) {
                        transitions.add(new Transition(
                                state,
                                targetStateQueryFutures.targetState,
                                action,
                                !throwingPreservingInvariantResult.equals(Result.BUG_IN_QUERY),
                                true
                        ));
                    }

                    return transitions.stream();
                })
                .collect(Collectors.toSet());

        for (final Transition enabledTransition : enabledTransitions) {

            final State targetState = enabledTransition.target;

            enqueueStateIfNecessary(targetState);

            epa.addTransition(enabledTransition);
        }

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

    private Set<ActionStatus> getNecessarilyEnabledOrDisabledActions(final State state, final Action transition) {

        return actions.stream()
                .map(action -> {

                    final Query enabledQuery = createNecessarilyEnabledQuery(state, transition, action);
                    final Future<Result> enabledFuture = submitQuery(enabledQuery);

                    final Query disabledQuery = createNecessarilyDisabledQuery(state, transition, action);
                    final Future<Result> disabledFuture = submitQuery(disabledQuery);

                    return new NecessaryActionFutureResult(action, enabledFuture, disabledFuture);
                })
                .map(necessaryActionFutureResult -> {

                    final Result enabledResult;
                    final Result disabledResult;
                    try {
                        enabledResult = necessaryActionFutureResult.enabledFutureResult.get();
                        disabledResult = necessaryActionFutureResult.disabledFutureResult.get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }

                    return new ActionStatus(
                            necessaryActionFutureResult.action,
                            enabledResult.equals(Result.NO_BUG),
                            disabledResult.equals(Result.NO_BUG)
                    );
                })
                .filter(actionStatus -> actionStatus.necessarilyEnabled || actionStatus.necessarilyDisabled)
                .collect(Collectors.toSet());
    }

    private Future<Result> submitQuery(final Query query) {

        phaser.register();
        return queriesExecutorService.submit(() -> {

            try {
                return runQuery(query);
            } finally {
                phaser.arrive();
            }
        });
    }

    private Query createTransitionQuery(final State source, final Action transition, final State target,
                                        boolean throwException, boolean assumeInvariant) {

        if (throwException) {
            return new Query(Query.Type.TRANSITION_QUERY, source, transition, target, invariant,
                    Query.TransitionThrows.THROWS, assumeInvariant);
        }

        return new Query(Query.Type.TRANSITION_QUERY, source, transition, target, invariant,
                Query.TransitionThrows.DOES_NOT_THROW, assumeInvariant);
    }

    private Query createNecessarilyEnabledQuery(final State state, final Action transition, final Action testedAction) {

        final State targetState = new State(Sets.newHashSet(testedAction), Sets.newHashSet());

        return new Query(Query.Type.NECESSARY_ENABLED_QUERY, state, transition, targetState, invariant,
                Query.TransitionThrows.EXCEPTION_IGNORED, false);
    }

    private Query createNecessarilyDisabledQuery(final State state, final Action transition, Action testedAction) {

        final State targetState = new State(Sets.newHashSet(), Sets.newHashSet(testedAction));

        return new Query(Query.Type.NECESSARY_DISABLED_QUERY, state, transition, targetState, invariant,
                Query.TransitionThrows.EXCEPTION_IGNORED, false);
    }

    private static class ActionStatus {

        public final Action action;

        public final boolean necessarilyEnabled;

        public final boolean necessarilyDisabled;

        private ActionStatus(final Action action, final boolean necessarilyEnabled, final boolean necessarilyDisabled) {

            this.action = action;
            this.necessarilyEnabled = necessarilyEnabled;
            this.necessarilyDisabled = necessarilyDisabled;
        }
    }

    private static class NecessaryActionFutureResult {

        public final Action action;

        public final Future<Result> enabledFutureResult;

        public final Future<Result> disabledFutureResult;

        private NecessaryActionFutureResult(
                final Action action,
                final Future<Result> enabledFutureResult,
                final Future<Result> disabledFutureResult) {

            this.action = action;
            this.enabledFutureResult = enabledFutureResult;
            this.disabledFutureResult = disabledFutureResult;
        }
    }

    private static class TargetStateQueryFutures {

        public final Action transition;

        public final State targetState;

        public final Future<Result> futureResultWithoutThrowing;

        public final Future<Result> futureResultThrowingPreservingInvariant;

        public final Future<Result> futureResultThrowingNotPreservingInvariant;

        private TargetStateQueryFutures(final Action transition,
                                        final State targetState,
                                        final Future<Result> futureResultWithoutThrowing,
                                        final Future<Result> futureResultThrowingPreservingInvariant,
                                        final Future<Result> futureResultThrowingNotPreservingInvariant) {

            this.transition = transition;
            this.targetState = targetState;
            this.futureResultWithoutThrowing = futureResultWithoutThrowing;
            this.futureResultThrowingPreservingInvariant = futureResultThrowingPreservingInvariant;
            this.futureResultThrowingNotPreservingInvariant = futureResultThrowingNotPreservingInvariant;
        }
    }

}
