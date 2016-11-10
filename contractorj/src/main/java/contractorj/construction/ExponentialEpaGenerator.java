package contractorj.construction;

import com.google.common.collect.Sets;
import contractorj.construction.corral.QueryRunner;
import contractorj.model.Action;
import contractorj.model.Epa;
import contractorj.model.State;
import j2bpl.Class;
import j2bpl.Method;
import contractorj.util.CombinationsGenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Generates an EPA with the trivial exponential algorithm.
 */
public class ExponentialEpaGenerator {


    private final Class theClass;

    private final String baseTranslation;

    private final int numberOfThreads;

    private final ExecutorService executorService;

    private final BlockingQueue<Query> queriesQueue = new LinkedBlockingQueue<>();

    private final Epa epa;

    public ExponentialEpaGenerator(Class theClass, String baseTranslation, int numberOfThreads) {
        this.theClass = theClass;
        epa = new Epa(theClass.getQualifiedJavaName());
        this.baseTranslation = baseTranslation;
        this.numberOfThreads = numberOfThreads;
        executorService = Executors.newFixedThreadPool(numberOfThreads);
    }

    public Epa generateEpa() {

        final long timeTrack0 = System.nanoTime();

        final ActionsExtractor actionsExtractor = new ActionsExtractor(theClass);
        final Set<Action> actions = actionsExtractor.getInstanceActions();
        final Method invariant = actionsExtractor.getInvariant();

        final CombinationsGenerator<Action> combinationsGenerator = new CombinationsGenerator<>();

        final Set<State> allStates = new HashSet<>();

        for (Set<Action> actionSet : combinationsGenerator.combinations(actions)) {
            final State state = new State(actionSet, Sets.difference(actions, actionSet));
            allStates.add(state);
        }

        for (State from : allStates) {

            for (State to : allStates) {

                if (from == to) {
                    continue;
                }

                generateQueriesForPairOfStates(invariant, from, to);
            }
        }

        final State constructorsState = new State(actionsExtractor.getConstructorActions(), new HashSet<Action>());

        for (State state : allStates) {

            for (final Action action : constructorsState.enabledActions) {
                queriesQueue.add(new Query("TRANSITION_", constructorsState, action, state, invariant));
            }

            generateQueriesForPairOfStates(invariant, state, state);
        }

        final long timeTrack1 = System.nanoTime();

        final int numberOfQueries = queriesQueue.size();

        System.out.println("Generating queries: " + secondsBetweenNanos(timeTrack0, timeTrack1));
        System.out.println("Number of queries: " + numberOfQueries);

        startRunners();

        try {
            executorService.shutdown();
            executorService.awaitTermination(100, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        final long timeTrack2 = System.nanoTime();

        System.out.println("Running queries: " + secondsBetweenNanos(timeTrack1, timeTrack2));
        System.out.println("Total time: " + secondsBetweenNanos(timeTrack0, timeTrack2));

        return epa;
    }

    private float secondsBetweenNanos(long start, long end) {

        final long total = end - start;
        return total / 1_000_000_000f;
    }

    private void startRunners() {
        for (int i = 0; i < numberOfThreads; i++) {
            final QueryRunner queryRunner = new QueryRunner(baseTranslation, epa, queriesQueue);
            executorService.execute(queryRunner);
        }
    }

    private void generateQueriesForPairOfStates(Method invariant, State from, State to) {

        for (Action transition : from.enabledActions) {
            queriesQueue.add(new Query("TRANSITION_", from, transition, to, invariant));
        }
    }

}
