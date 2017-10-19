package contractorj.construction;

import com.google.common.collect.Sets;
import contractorj.Main;
import contractorj.construction.corral.CorralRunner;
import contractorj.construction.corral.QueryResult;
import contractorj.construction.corral.RunnerResult;
import contractorj.construction.queries.Answer;
import contractorj.construction.queries.Query;
import contractorj.construction.queries.invariant.ExceptionBreaksInvariantQuery;
import contractorj.construction.queries.invariant.TransitionBreaksInvariantQuery;
import contractorj.construction.queries.necessary_actions.GlobalNecessarilyDisabledActionQuery;
import contractorj.construction.queries.necessary_actions.GlobalNecessarilyEnabledActionQuery;
import contractorj.construction.queries.necessary_actions.NecessarilyDisabledActionQuery;
import contractorj.construction.queries.necessary_actions.NecessarilyEnabledActionQuery;
import contractorj.construction.queries.transition.NotThrowingTransitionQuery;
import contractorj.construction.queries.transition.ThrowingTransitionQuery;
import contractorj.model.Action;
import contractorj.model.Epa;
import contractorj.model.State;
import contractorj.model.Transition;
import contractorj.util.CombinationsGenerator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jbct.model.Class;

public class LazyEpaGenerator extends EpaGenerator {

  private final File logFile;
  private ExecutorService driverExecutorService;

  private ExecutorService queriesExecutorService;

  private Set<State> statesAlreadyEnqueued;

  private Epa epa;

  private Phaser phaser;

  private DebugLog debugLog;

  public LazyEpaGenerator(
      final String baseTranslation,
      final int numberOfThreads,
      final CorralRunner corralRunner,
      File logFile) {

    super(baseTranslation, numberOfThreads, corralRunner);

    this.logFile = logFile;
  }

  Map<String, List<String>> dependencies_active = new Hashtable<String, List<String>>();
  Map<String, List<String>> dependencies_disable = new Hashtable<String, List<String>>();

  /*
   * Start the generation of a PEPA. The initial state has all constructors enabled.
   */
  @Override
  protected Epa generateEpaImplementation(final Class theClass) {

    try {

      debugLog = new DebugLog();

      Runtime.getRuntime().addShutdownHook(new Thread(this::printLog));

      driverExecutorService = Executors.newCachedThreadPool();
      queriesExecutorService = Executors.newFixedThreadPool(numberOfThreads);
      statesAlreadyEnqueued = Sets.newHashSet();
      phaser = new Phaser();
      phaser.register();

      final State initialState = new State(constructors, Sets.newHashSet());

      epa = new Epa(theClass.getQualifiedJavaName(), initialState);

      if (Main.globalNecessaryQueriesEnable) globalNecessaryQueries();

      debugLog.addInitialState(initialState);
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
      printLog();
    }
  }

  private void globalNecessaryQuery(Action mainAction, Action testedAction) {
    Set enabledActions = new HashSet();
    enabledActions.add(mainAction);
    Set disabledActions = new HashSet();

    State state = new State(enabledActions, disabledActions);

    GlobalNecessarilyEnabledActionQuery necessarilyEnabledActionQuery =
        new GlobalNecessarilyEnabledActionQuery(state, mainAction, testedAction, invariant);
    GlobalNecessarilyDisabledActionQuery necessarilyDisabledActionQuery =
        new GlobalNecessarilyDisabledActionQuery(state, mainAction, testedAction, invariant);

    Answer enabledAnswer = getAnswer(necessarilyEnabledActionQuery);
    Answer disabledAnswer = getAnswer(necessarilyDisabledActionQuery);

    if (enabledAnswer.equals(Answer.YES) && disabledAnswer.equals(Answer.YES)) {
      System.err.println(
          "Inconsistent necessity of action "
              + testedAction
              + " in state "
              + state
              + " after "
              + mainAction);
      System.exit(1);
    }

    if (enabledAnswer.equals(Answer.YES)) {
      List<String> enabledActionsList = dependencies_active.get(mainAction.toString());
      enabledActionsList.add(testedAction.toString());
    }

    if (disabledAnswer.equals(Answer.YES)) {
      List<String> disabledActionsList = dependencies_disable.get(mainAction.toString());
      disabledActionsList.add(testedAction.toString());
    }
  }

  private void globalNecessaryQueries() {

    for (Action a : actions) {
      List l1 = Collections.synchronizedList(new LinkedList());
      List l2 = Collections.synchronizedList(new LinkedList());

      dependencies_disable.put(a.toString(), l1);
      dependencies_active.put(a.toString(), l2);
    }
    ExecutorService executor = Executors.newCachedThreadPool();

    actions
        .stream()
        .forEach(
            mainAction -> {
              actions
                  .stream()
                  .forEach(
                      testedAction -> {
                        executor.submit(
                            () -> {
                              globalNecessaryQuery(mainAction, testedAction);
                            });
                      });
            });

    executor.shutdown();

    try {

      executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private void printLog() {
    try (final PrintWriter debugFile = new PrintWriter(logFile)) {
      debugLog.writeLog(debugFile);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  /*
   * Parallelize the exploration task.
   * The exploration task consists for a given state s and action act (enabled in the state)
   * finding new reachable states from the s and act
   * @param state
   */
  private void analiseState(final State state) {

    state
        .getEnabledActions()
        .forEach(action -> runOnDriverExecutorService(() -> analiseStateAndAction(state, action)));
  }

  private void runOnDriverExecutorService(AsyncTask asyncTask) {

    phaser.register();
    driverExecutorService.submit(
        () -> {
          try {
            asyncTask.run();
          } catch (Exception exception) {
            System.err.println(
                "Unhandled exception on thread "
                    + Thread.currentThread().getName()
                    + ":"
                    + exception.getMessage());
            exception.printStackTrace();
            System.exit(1);
          } finally {
            phaser.arrive();
          }
        });
  }

  /**
   * All transitions from state (first argument) using mainAction (second argument) are calculated.
   * Destinations from those transitions are enqueued to repeat the exploration process from the new
   * states (target states).
   *
   * <p>Steps: Actions always enabled after transition from state using mainAction are calculated as
   * necessaryEnabledActions Actions always disabled after transition from state using mainAction
   * are calculated as necessaryDisabledActions Queries that check whether if a transition from
   * state using mainAction breaks the invariant are created (not performed) Note that the are two
   * kinds of invariant violation. One is raising an exception and the other is with no exception
   * raised. These queries could generate a transition to the error state. Queries that check valid
   * transitions (invariant is preserved) from state using mainAction are created (not performed)
   * Note that in order to generate new possible transitions, actions not present in
   * necessaryEnabledAction and necessaryDisabledActions must be considered. A new state represents
   * a set of enabled and disabled actions. These queries could generate new states and transitions
   * to the EPA. Finally all queries are performed. If a query answers yes or maybe then the
   * transition is added to the EPA.
   *
   * @param state
   * @param mainAction
   */
  private void analiseStateAndAction(final State state, final Action mainAction) {

    final Set<NecessaryActionResult> necessaryActionResults =
        getNecessaryActionResults(state, mainAction);

    final Set<Action> necessarilyEnabledActions =
        necessaryActionResults
            .stream()
            .filter(
                necessaryActionResult ->
                    necessaryActionResult.necessarilyEnabled.equals(Answer.YES))
            .map(necessaryActionResult -> necessaryActionResult.testedAction)
            .collect(Collectors.toSet());

    final Set<Action> necessarilyDisabledActions =
        necessaryActionResults
            .stream()
            .filter(
                necessaryActionResult ->
                    necessaryActionResult.necessarilyDisabled.equals(Answer.YES))
            .map(necessaryActionResult -> necessaryActionResult.testedAction)
            .collect(Collectors.toSet());

    final Stream<Query> queryStream =
        Stream.concat(
            getInvariantTestQueries(state, mainAction),
            getTransitionQueries(
                state, mainAction, necessarilyEnabledActions, necessarilyDisabledActions));

    queryStream.forEach(
        query ->
            runOnDriverExecutorService(
                () -> {
                  final Optional<Transition> maybeTransition =
                      query.getTransition(getAnswer(query));

                  if (maybeTransition.isPresent()) {

                    final Transition transition = maybeTransition.get();

                    final boolean enqueued = enqueueStateIfNecessary(transition.getTarget());
                    epa.addTransition(transition);

                    if (enqueued) {
                      debugLog.logEnqueuedTransition(transition);
                    }
                  }
                }));
  }

  /**
   * A stream with two invariant queries is returned The first query verifies a transition breaking
   * the invariant with no exception raised. The second query verifies a transition breaking the
   * invariant with exception raised.
   *
   * @param state
   * @param mainAction
   * @return A stream with two invariant queries.
   */
  private Stream<Query> getInvariantTestQueries(final State state, final Action mainAction) {

    return Stream.of(
        new TransitionBreaksInvariantQuery(state, mainAction, invariant),
        new ExceptionBreaksInvariantQuery(state, mainAction, invariant));
  }

  /**
   * Queries that check transitions to other states are created.
   *
   * <p>Remember: A state in the EPA represents a set of enabled actions and a set of disabled
   * actions. Those sets are disjoint and the union is equal to the action set (input problem).
   * Actions that are always enabled/disabled after transition from state (first argument) using
   * mainAction (second argument) are known. From the uncertain actions set all possible subsets
   * (combinations of elements) are calculated. uncertain actions are those that neither are in
   * necessarilyEnabledActions nor necessarilyDisabledActions The combinations are whether they are
   * disabled or enabled. Finally queries that verify if transition state -> mainAction ->
   * targetState are created and returned. targetState is a state (possibly new) generated by the
   * combinations of the uncertain actions. Note that necessarilyEnabledActions and
   * necessarilyDisabledActions help in reducing the number of elements in uncertainActions. In this
   * way fewer combinations are tested
   *
   * @param state
   * @param mainAction
   * @param necessarilyEnabledActions
   * @param necessarilyDisabledActions
   * @return queries verifiers of new transitions.
   */
  private Stream<Query> getTransitionQueries(
      final State state,
      final Action mainAction,
      final Set<Action> necessarilyEnabledActions,
      final Set<Action> necessarilyDisabledActions) {

    final Set<Action> uncertainActions =
        Sets.difference(
            Sets.difference(actions, necessarilyEnabledActions), necessarilyDisabledActions);

    final CombinationsGenerator<Action> combinationsGenerator = new CombinationsGenerator<>();
    final Set<Set<Action>> combinations = combinationsGenerator.combinations(uncertainActions);

    return combinations
        .stream()
        .map(
            maybeEnabledActions ->
                createTargetState(
                    necessarilyEnabledActions,
                    necessarilyDisabledActions,
                    uncertainActions,
                    maybeEnabledActions))
        .flatMap(
            targetState -> {
              final NotThrowingTransitionQuery notThrowingTransitionQuery =
                  new NotThrowingTransitionQuery(state, mainAction, targetState, invariant);

              final ThrowingTransitionQuery throwingTransitionQuery =
                  new ThrowingTransitionQuery(state, mainAction, targetState, invariant);

              return Stream.of(notThrowingTransitionQuery, throwingTransitionQuery);
            });
  }

  /**
   * Schedules exploration from a state.
   *
   * @param state
   * @return true if the state has not been explored yet, otherwise false.
   */
  private synchronized boolean enqueueStateIfNecessary(State state) {

    if (statesAlreadyEnqueued.contains(state)) {
      return false;
    }

    statesAlreadyEnqueued.add(state);

    runOnDriverExecutorService(() -> analiseState(state));

    return true;
  }

  /**
   * maybeEnabledActions are uncertain answers. maybeEnabledAction are part of a combinatorial
   * process. check when this method is used.
   *
   * @param necessarilyEnabledActions
   * @param necessarilyDisabledActions
   * @param uncertainActions
   * @param maybeEnabledActions
   * @return A new State based on the given parameters.
   */
  private State createTargetState(
      final Set<Action> necessarilyEnabledActions,
      final Set<Action> necessarilyDisabledActions,
      final Set<Action> uncertainActions,
      final Set<Action> maybeEnabledActions) {

    final Set<Action> enabledActions = Sets.union(necessarilyEnabledActions, maybeEnabledActions);

    final Set<Action> disabledActions =
        Sets.union(
            necessarilyDisabledActions, Sets.difference(uncertainActions, maybeEnabledActions));

    return new State(enabledActions, disabledActions);
  }

  /**
   * @param state
   * @param mainAction
   * @return A set of NecessaryActionResult indicating if an action is always enabled/disabled
   */
  private Set<NecessaryActionResult> getNecessaryActionResults(
      final State state, final Action mainAction) {

    return actions
        .parallelStream()
        .map(
            testedAction -> {
              if (!testedAction.getStatePrecondition().isPresent()) {
                return new NecessaryActionResult(testedAction, Answer.YES, Answer.NO);
              }

              NecessarilyEnabledActionQuery necessarilyEnabledActionQuery;
              NecessarilyDisabledActionQuery necessarilyDisabledActionQuery;

              Answer enabledAnswer = Answer.YES;
              Answer disabledAnswer = Answer.YES;

              if (Main.globalNecessaryQueriesEnable) {
                if (dependencies_active
                    .getOrDefault(mainAction.toString(), new LinkedList())
                    .contains(testedAction.toString())) {
                  enabledAnswer = Answer.YES;
                  disabledAnswer = Answer.NO;
                } else if (dependencies_disable
                    .getOrDefault(mainAction.toString(), new LinkedList())
                    .contains(testedAction.toString())) {
                  disabledAnswer = Answer.YES;
                  enabledAnswer = Answer.NO;
                } else { // default case
                  necessarilyEnabledActionQuery =
                      new NecessarilyEnabledActionQuery(state, mainAction, testedAction, invariant);
                  enabledAnswer = getAnswer(necessarilyEnabledActionQuery);
                  necessarilyDisabledActionQuery =
                      new NecessarilyDisabledActionQuery(
                          state, mainAction, testedAction, invariant);
                  disabledAnswer = getAnswer(necessarilyDisabledActionQuery);
                }
              } else { // defualt case
                necessarilyEnabledActionQuery =
                    new NecessarilyEnabledActionQuery(state, mainAction, testedAction, invariant);
                enabledAnswer = getAnswer(necessarilyEnabledActionQuery);
                necessarilyDisabledActionQuery =
                    new NecessarilyDisabledActionQuery(state, mainAction, testedAction, invariant);
                disabledAnswer = getAnswer(necessarilyDisabledActionQuery);
              }

              if (enabledAnswer.equals(Answer.YES) && disabledAnswer.equals(Answer.YES)) {
                System.err.println(
                    "Inconsistent necessity of action "
                        + testedAction
                        + " in state "
                        + state
                        + " after "
                        + mainAction);
                System.exit(1);
              }

              return new NecessaryActionResult(testedAction, enabledAnswer, disabledAnswer);
            })
        .collect(Collectors.toSet());
  }

  /**
   * @param query
   * @return the answer of the performed query
   */
  private Answer getAnswer(final Query query) {

    try {
      return submitQuery(query).get();
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  /*
   * @param query
   * @return the promise of an answer
   */
  private Future<Answer> submitQuery(final Query query) {

    phaser.register();
    return queriesExecutorService.submit(
        () -> {
          try {
            final RunnerResult runnerResult = runQuery(query);
            final QueryResult queryResult = runnerResult.queryResult;

            debugLog.logQuery(query, runnerResult);

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

    private NecessaryActionResult(
        final Action testedAction,
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
