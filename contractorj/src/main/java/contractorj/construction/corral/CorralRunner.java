package contractorj.construction.corral;

import contractorj.util.CommandsRunner;

import java.time.Duration;
import java.time.LocalDateTime;

public class CorralRunner {

  private final String pathToCorral;

  private final int recursionBound;

  public CorralRunner(String pathToCorral) {

    this(pathToCorral, 3);
  }

  public CorralRunner(String pathToCorral, int recursionBound) {

    this.pathToCorral = pathToCorral;
    this.recursionBound = recursionBound;
  }

  public String getConsoleCommandToRun(String pathToBoogieSourcecode, String mainMethod) {

    return (!isWindows() ? "mono " : "")
        + "'"
        + pathToCorral
        + "' '/main:"
        + mainMethod
        + "' /recursionBound:"
        + recursionBound
        + " /trackAllVars '" // This is not really necessary, but makes it a little faster
        + pathToBoogieSourcecode
        + "'";
  }

  public RunnerResult run(String pathToBoogieSourcecode, String mainMethod) {

    final String consoleCommandToRun = getConsoleCommandToRun(pathToBoogieSourcecode, mainMethod);

    final LocalDateTime start = LocalDateTime.now();

    final String processOutput = CommandsRunner.runtAndReturnOutput(consoleCommandToRun, isWindows());

    final LocalDateTime end = LocalDateTime.now();

    final QueryResult queryQueryResult;

    if (processOutput.contains("Program has no bugs")) {

      queryQueryResult = QueryResult.NO_BUG;

    } else if (processOutput.contains("True bug")) {

      if (!processOutput.contains("query_assertion")) {
        throw new RuntimeException("Unexpected bug running " + consoleCommandToRun);
      }

      queryQueryResult = QueryResult.TRUE_BUG;

    } else {
      queryQueryResult = QueryResult.MAYBE_BUG;
    }

    return new RunnerResult(
        queryQueryResult, Duration.between(start, end), processOutput, consoleCommandToRun);
  }

  private boolean isWindows() {

    return System.getProperty("os.name").toLowerCase().contains("win");
  }
}
