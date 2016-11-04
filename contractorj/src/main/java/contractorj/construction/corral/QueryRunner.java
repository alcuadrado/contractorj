package contractorj.construction.corral;

import contractorj.model.Transition;
import contractorj.model.Epa;
import contractorj.construction.Query;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class QueryRunner implements Runnable {

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
                    final Transition transition = new Transition(query.getFrom(), query.getTo(), query.getTransition(), false);
                    epa.addEdge(transition);
                }

                if (result.equals(Result.MAYBE_BUG)) {
                    final Transition transition = new Transition(query.getFrom(), query.getTo(), query.getTransition(), true);
                    epa.addEdge(transition);
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
