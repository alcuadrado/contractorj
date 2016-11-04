package contractorj.corral;

import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class CorralRunner {

    private static final Logger logger = Logger.getLogger(CorralRunner.class.getName());

    private final String pathToCorral;

    private final int recursionBound;

    private long lastRunningTime = -1;

    public CorralRunner(String pathToCorral) {

        this(pathToCorral, 10);
    }

    public CorralRunner(String pathToCorral, int recursionBound) {

        this.pathToCorral = pathToCorral;
        this.recursionBound = recursionBound;
    }

    public Result run(String pathToBoogieSourcecode, String mainMethod) {

        final List<String> command = Lists.newArrayList(pathToCorral, "/main:" + mainMethod,
                "/recursionBound:" + recursionBound, "/trackAllVars", pathToBoogieSourcecode);

        if (!isWindows()) {
            command.add(0, "mono");
        }

        final String[] commandArray = command.toArray(new String[command.size()]);

        final Runtime runtime = Runtime.getRuntime();
        Process process = null;

        try {

            final long startTime = System.nanoTime();

            process = runtime.exec(commandArray);
            final int exitStatus = process.waitFor();

            final long endTime = System.nanoTime();

            lastRunningTime = endTime - startTime;

            if (exitStatus != 0) {
                throw new RuntimeException();
            }

        } catch (IOException | InterruptedException | RuntimeException e) {

            String processErr = "Couldn't read stderr";

            if (process != null) {

                try (final InputStream in = process.getInputStream();
                     final InputStreamReader inr = new InputStreamReader(in)) {
                    processErr = CharStreams.toString(inr);
                } catch (IOException err) {
                    // Do nothing
                }

            }

            throw new RuntimeException("Error when executing Corral with: " + Arrays.toString(commandArray) + "\n"
                    + processErr);
        }

        final String processOutput;

        try (final InputStream in = process.getInputStream();
             final InputStreamReader inr = new InputStreamReader(in)) {
            processOutput = CharStreams.toString(inr);
        } catch (IOException e) {
            throw new RuntimeException("Error reading corral output");
        }

        if (processOutput.contains("Program has no bugs")) {
            return Result.NO_BUG;
        }

        if (processOutput.contains("True bug")) {

            if (processOutput.contains("query_assertion")) {
                return Result.TRUE_BUG;
            }

            return Result.APPLICATION_BUG;
        }

        return Result.MAYBE_BUG;
    }

    public long getLastRunTime() {

        return lastRunningTime;
    }

    private boolean isWindows() {

        return System.getProperty("os.name").toLowerCase().contains("win");
    }
}
