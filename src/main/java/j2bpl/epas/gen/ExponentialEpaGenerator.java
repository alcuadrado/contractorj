package j2bpl.epas.gen;

import j2bpl.corral.CorralRunner;
import j2bpl.corral.Result;
import j2bpl.epas.Contract;
import j2bpl.epas.Edge;
import j2bpl.epas.Epa;
import j2bpl.epas.State;
import j2bpl.translation.Class;
import j2bpl.translation.Method;
import j2bpl.util.CombinationsGenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
        final Set<Contract> contracts = contractsExtractor.getContracts();
        final Method invariant = contractsExtractor.getInvariant();

        CombinationsGenerator<Contract> combinationsGenerator = new CombinationsGenerator<>();

        final Set<State> allStates = new HashSet<>();

        for (Set<Contract> contractSet : combinationsGenerator.combinations(contracts)) {
            final State state = new State(contractSet);
            allStates.add(state);
        }

        for (State from : allStates) {

            for (State to : allStates) {

                if (from == to) {
                    continue;
                }

                generateQueriesForPairOfStates(contracts, invariant, from, to);
            }
        }

        for (State from : allStates) {
            generateQueriesForPairOfStates(contracts, invariant, from, from);
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

    private void generateQueriesForPairOfStates(Set<Contract> contracts, Method invariant, State from, State to) {

        for (Contract transition : from.contracts) {

            final Query query = new Query(contracts, invariant, from, to, transition);

            if (!query.getName().equals("from_#add~previous~remove#_to_##_via_remove") &&
                    !query.getName().equals("from_#add~previous~remove#_to_#add#_via_remove")) {
                continue;
            }

            queriesQueue.add(query);
        }
    }

    private static class QueryRunner implements Runnable {

        private final File boogieFile;

        private final CorralRunner corralRunner;

        private final Epa epa;

        private final BlockingQueue<Query> queriesQueue;

        public QueryRunner(String baseTranslation, Epa epa, BlockingQueue<Query> queriesQueue) {

            this.epa = epa;

            this.queriesQueue = queriesQueue;

            corralRunner = new CorralRunner("/Users/pato/facultad/tesis/tools/corral/bin/Debug/corral.exe");

            try {
                boogieFile = File.createTempFile("epa-", ".bpl");
                System.out.println(boogieFile.getAbsolutePath());
                appendToBoogieFile(baseTranslation);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        @Override
        public void run() {

            try {
                while (!queriesQueue.isEmpty()) {

                    final Query query;

                    try {
                        query = queriesQueue.poll(50, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        continue;
                    }

                    appendToBoogieFile("\n" + query.getBoogieCode());

                    final String queryName = query.getName();

                    final Result result = corralRunner.run(boogieFile.getAbsolutePath(), queryName);

                    if (result.equals(Result.TRUE_BUG)) {
                        final Edge edge = new Edge(query.getFrom(), query.getTo(), query.getTransition(), false);
                        epa.addEdge(edge);
                    }

                    if (result.equals(Result.MAYBE_BUG)) {
                        final Edge edge = new Edge(query.getFrom(), query.getTo(), query.getTransition(), true);
                        epa.addEdge(edge);
                    }

                    final long lastRunTime = corralRunner.getLastRunTime();

                    final float v = lastRunTime / 1_000_000_000f;

                    System.out.println(queryName + ": " + result + " (t: " + v + "s - thread: " + Thread.currentThread().getName() + ")");
                }
            } catch (RuntimeException exception) {
                System.err.println("Uncaught exception on thread " + Thread.currentThread().getName() + ":");
                System.err.println(exception.getMessage());
                exception.printStackTrace(System.err);
                System.exit(1);
            }
        }

        private void appendToBoogieFile(String boogieCode) {

            try (
                    final FileWriter fw = new FileWriter(boogieFile, true);
                    final BufferedWriter bw = new BufferedWriter(fw);
                    final PrintWriter out = new PrintWriter(bw)
            ) {
                out.print(boogieCode + "\n");
                out.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
