package contractorj.epas.gen;

import contractorj.corral.QueryRunner;
import contractorj.epas.Action;
import contractorj.epas.Epa;
import contractorj.epas.State;
import j2bpl.translation.Class;
import j2bpl.translation.Method;
import contractorj.util.CombinationsGenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ExponentialEpaGenerator {

    private static final int THREADS = 1;//Runtime.getRuntime().availableProcessors();

    private final Class theClass;

    private final String baseTranslation;

    private final ExecutorService executorService = Executors.newFixedThreadPool(THREADS);

    private final BlockingQueue<Query> queriesQueue = new LinkedBlockingQueue<>();

    private final Epa epa;

    public ExponentialEpaGenerator(Class theClass, String baseTranslation) {

        this.theClass = theClass;
        epa = new Epa(theClass.getQualifiedJavaName());
        this.baseTranslation = baseTranslation;
    }

    public Epa generateEpa() {

        final long timeTrack0 = System.nanoTime();

        final ContractsExtractor contractsExtractor = new ContractsExtractor(theClass);
        final Set<Action> actions = contractsExtractor.getActions();
        final Method invariant = contractsExtractor.getInvariant();

        CombinationsGenerator<Action> combinationsGenerator = new CombinationsGenerator<>();

        final Set<State> allStates = new HashSet<>();

        for (Set<Action> actionSet : combinationsGenerator.combinations(actions)) {
            final State state = new State(actionSet);
            allStates.add(state);
        }

        for (State from : allStates) {

            for (State to : allStates) {

                if (from == to) {
                    continue;
                }

                generateQueriesForPairOfStates(actions, invariant, from, to);
            }
        }

        for (State from : allStates) {
            generateQueriesForPairOfStates(actions, invariant, from, from);
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

        for (int i = 0; i < THREADS; i++) {
            final QueryRunner queryRunner = new QueryRunner(baseTranslation, epa, queriesQueue);
            executorService.execute(queryRunner);
        }
    }

    private void generateQueriesForPairOfStates(Set<Action> actions, Method invariant, State from, State to) {

        for (Action transition : from.actions) {

            final Query query = new Query(actions, invariant, from, to, transition);
            queriesQueue.add(query);
        }
    }

}
