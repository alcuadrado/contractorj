package contractorj.construction.corral;

import contractorj.construction.Query;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class CorralRunner {

    private final String pathToCorral;

    private final int recursionBound;

    public CorralRunner(String pathToCorral) {

        this(pathToCorral, 10);
    }

    public CorralRunner(String pathToCorral, int recursionBound) {

        this.pathToCorral = pathToCorral;
        this.recursionBound = recursionBound;
    }

    public String getConsoleCommandToRun(String pathToBoogieSourcecode, String mainMethod) {
        return  (!isWindows() ? "mono " : "") + "'" + pathToCorral + "' '/main:" + mainMethod
                + "' /recursionBound:" + recursionBound + " /trackAllVars '" + pathToBoogieSourcecode + "'";
    }

    public RunnerResult run(String pathToBoogieSourcecode, String mainMethod) {

        final String consoleCommandToRun = getConsoleCommandToRun(pathToBoogieSourcecode, mainMethod);

        final LocalDateTime start = LocalDateTime.now();

        final String processOutput = runtAndReturnOutput(consoleCommandToRun);

        final LocalDateTime end = LocalDateTime.now();

        final Result queryResult;

        if (processOutput.contains("Program has no bugs")) {

            queryResult = Result.NO_BUG;

        } else if (processOutput.contains("True bug")) {

            if (processOutput.contains(Query.AssertionLabels.QUERY.toString())) {

                queryResult = Result.BUG_IN_QUERY;

            } else if (processOutput.contains(Query.AssertionLabels.INVARIANT.toString())) {

                queryResult = Result.BROKEN_INVARIANT;

            } else {
                throw new RuntimeException("Unrecognized bug class");
            }
        } else {
            queryResult = Result.MAYBE_BUG;
        }

        return new RunnerResult(queryResult, Duration.between(start, end), processOutput);
    }

    private String runtAndReturnOutput(final String lineToRun) {

        final CommandLine commandLine = CommandLine.parse(lineToRun);

        final DefaultExecutor executor = new DefaultExecutor();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);

        executor.setStreamHandler(streamHandler);

        try {
            final int exitValue = executor.execute(commandLine);

            if (exitValue != 0) {
                throw new RuntimeException("Error executing " + lineToRun + "\n " + outputStream.toString());
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return outputStream.toString();
    }

    private boolean isWindows() {

        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    public static class RunnerResult {

        public final Result queryResult;

        public final Duration runningTime;

        public final String output;

        private RunnerResult(final Result queryResult, final Duration runningTime, final String output) {

            this.queryResult = queryResult;
            this.runningTime = runningTime;
            this.output = output;
        }
    }
}
