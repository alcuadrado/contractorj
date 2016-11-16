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
import j2bpl.Method;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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

public class LazyEpaGenerator {

    public static final String ANSI_RESET = "\u001B[0m";

    public static final String ANSI_BLACK = "\u001B[30m";

    public static final String ANSI_RED = "\u001B[31m";

    public static final String ANSI_GREEN = "\u001B[32m";

    public static final String ANSI_YELLOW = "\u001B[33m";

    public static final String ANSI_BLUE = "\u001B[34m";

    public static final String ANSI_PURPLE = "\u001B[35m";

    public static final String ANSI_CYAN = "\u001B[36m";

    public static final String ANSI_WHITE = "\u001B[37m";

    private final Class theClass;

    private final String baseTranslation;

    private final int numberOfThreads;

    private final CorralRunner corralRunner;

    private ThreadLocal<File> boogieFile = new ThreadLocal<>();

    private Set<Action> actions;

    private Method invariant;

    private ExecutorService driverExecutorService;

    private ExecutorService queriesExecutorService;

    private Set<State> statesAlreadyEnqueued;

    private Epa epa;

    private Phaser phaser;

    public LazyEpaGenerator(Class theClass, String baseTranslation, final int numberOfThreads) {

        this.theClass = theClass;
        this.baseTranslation = baseTranslation;
        this.numberOfThreads = numberOfThreads;
        corralRunner = new CorralRunner("/Users/pato/facultad/tesis/tools/corral/bin/Debug/corral.exe");
    }

    public Epa generateEpa() {

        epa = new Epa(theClass.getQualifiedJavaName());

        driverExecutorService = Executors.newCachedThreadPool();
        queriesExecutorService = Executors.newFixedThreadPool(numberOfThreads);
        statesAlreadyEnqueued = Sets.newHashSet();
        phaser = new Phaser();
        phaser.register();

        final ActionsExtractor actionsExtractor = new ActionsExtractor(theClass);
        actions = actionsExtractor.getInstanceActions();
        invariant = actionsExtractor.getInvariant();

        final State initialState = new State(actionsExtractor.getConstructorActions(), Sets.newHashSet());

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

                analiseStateAndAction(state, transition);
                phaser.arrive();

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

                    final Query query = createTransitionQuery(state, action, targetState);
                    return new TargetStateQueryFuture(
                            targetState,
                            submitQuery(query)
                    );
                })
                .map(targetStateQueryFuture -> {

                    final Result queryResult;

                    try {
                        queryResult = targetStateQueryFuture.resultFuture.get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }

                    if (queryResult.isError()) {
                        return null;
                    }

                    return new Transition(
                            state,
                            targetStateQueryFuture.targetState,
                            action,
                            !queryResult.equals(Result.BUG_IN_QUERY),
                            false);
                })
                .filter(transition1 -> transition1 != null)
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

            analiseState(state);
            phaser.arrive();

        });
    }

    private Query createTransitionQuery(final State source, final Action transition, final State target) {

        return new Query(Query.Type.TRANSITION_QUERY, source, transition, target, invariant, Query.TransitionThrows.NEVER_THROWS); //TODO: Fix TransitionThrows
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

            final Result result = runQuery(query);

            phaser.arrive();
            return result;
        });
    }

    private Query createNecessarilyEnabledQuery(final State state, final Action transition, final Action testedAction) {

        final State targetState = new State(Sets.newHashSet(testedAction), Sets.newHashSet());

        //TODO: Fix TransitionThrows
        return new Query(Query.Type.NECESSARY_ENABLED_QUERY, state, transition, targetState, invariant, Query.TransitionThrows.NEVER_THROWS);
    }

    private Query createNecessarilyDisabledQuery(final State state, final Action transition, Action testedAction) {

        final State targetState = new State(Sets.newHashSet(), Sets.newHashSet(testedAction));

        //TODO: Fix TransitionThrows
        return new Query(Query.Type.NECESSARY_DISABLED_QUERY, state, transition, targetState, invariant, Query.TransitionThrows.NEVER_THROWS);
    }

    private Result runQuery(final Query query) {

        final String absolutePathToBoogieSourceFile = appendToBoogieFile(query.getBoogieCode());

        final CorralRunner.RunnerResult runnerResult = corralRunner.run(absolutePathToBoogieSourceFile,
                query.getName());

        final Result result = runnerResult.queryResult;

        final String colorEscapeSequence = getColorEscapeSequenceForResult(result);

        String toPrint = colorEscapeSequence +
                "mono /Users/pato/facultad/tesis/tools/corral/bin/Debug/corral.exe '/main:"
                + query.getName() + "' /recursionBound:10 /trackAllVars " + absolutePathToBoogieSourceFile;

        if (result.isError()) {
            toPrint += result + "\n\n" + result.toString() + "\n\n" + runnerResult.output;
            System.exit(1);
        }

        System.out.println(toPrint + ANSI_RESET + "\n");

        return result;
    }

    private String getColorEscapeSequenceForResult(final Result result) {

        final String colorEscapeSequence;
        switch (result) {

            case BUG_IN_QUERY:
                colorEscapeSequence = ANSI_GREEN;
                break;

            case MAYBE_BUG:
                colorEscapeSequence = ANSI_BLUE;
                break;

            case BROKEN_INVARIANT:
                colorEscapeSequence = ANSI_PURPLE;
                break;

            case TRANSITION_MAY_NOT_THROW:
            case TRANSITION_MAY_THROW:
                colorEscapeSequence = ANSI_CYAN;
                break;

            case PRES_OR_INV_MAY_THROW:
                colorEscapeSequence = ANSI_RED;
                break;

            default:
                colorEscapeSequence = ANSI_RESET;
        }

        return colorEscapeSequence;
    }

    private String appendToBoogieFile(String boogieCode) {

        File file = boogieFile.get();

        if (file == null) {

            try {
                file = File.createTempFile("epa-" + Thread.currentThread().getName(), ".bpl");
                appendToFile(file, baseTranslation);
                boogieFile.set(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            System.out.println(file.getAbsolutePath());
        }

        appendToFile(file, boogieCode);

        return file.getAbsolutePath();
    }

    private void appendToFile(final File file, final String content) {

        try (
                final FileWriter fw = new FileWriter(file, true);
                final BufferedWriter bw = new BufferedWriter(fw);
                final PrintWriter out = new PrintWriter(bw)
        ) {
            out.print(content + "\n\n");
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    private static class TargetStateQueryFuture {

        public final State targetState;

        public final Future<Result> resultFuture;

        private TargetStateQueryFuture(final State targetState, final Future<Result> resultFuture) {

            this.targetState = targetState;
            this.resultFuture = resultFuture;
        }
    }

}
