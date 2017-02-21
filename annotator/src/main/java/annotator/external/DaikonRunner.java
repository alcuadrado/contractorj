package annotator.external;

import com.google.common.io.Files;
import contractorj.util.CommandsRunner;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DaikonRunner {

  private static final File DAIKON_RESULTS_FILE = new File("RegressionTestDriver.inv.gz");

  private static final File CACHED_RESULTS_FILE = new File("cached-daikon-results.txt");

  private static final File DAIKON_LAST_RUN = new File("daikon-last-run.txt");

  private final File daikonJar;

  private final File hamcrestCoreJar;

  private final File jUnitJar;

  public DaikonRunner(final File daikonJar, final File hamcrestCoreJar, final File jUnitJar) {
    this.daikonJar = daikonJar;
    this.hamcrestCoreJar = hamcrestCoreJar;
    this.jUnitJar = jUnitJar;
  }

  public void runDaikon(final File classSourceFile, final File classesFolder) {

    final String firstCommand =
        "java"
            + " -Xmx20g"
            + " -cp '"
            + daikonJar.getAbsolutePath()
            + ":"
            + hamcrestCoreJar.getAbsolutePath()
            + ":"
            + jUnitJar.getAbsolutePath()
            + ":"
            + classesFolder.getAbsolutePath()
            + "'"
            + " daikon.Chicory"
            + " --heap-size=20g"
            + " RegressionTestDriver";

    final String secondCommand =
        "java"
            + " -Xmx20g"
            + " -cp '"
            + daikonJar.getAbsolutePath()
            + "'"
            + " -ea daikon.Daikon"
            + " ./RegressionTestDriver.dtrace.gz";

    CommandsRunner.runtAndReturnOutput(firstCommand);
    CommandsRunner.runtAndReturnOutput(secondCommand);

    try {
      Files.write(classSourceFile.getAbsolutePath(), DAIKON_LAST_RUN, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public String readDaikonResults() {

    try {

      if (CACHED_RESULTS_FILE.exists()
          && CACHED_RESULTS_FILE.lastModified() > DAIKON_RESULTS_FILE.lastModified()) {

        return Files.toString(CACHED_RESULTS_FILE, StandardCharsets.UTF_8);
      }

      final String results =
          CommandsRunner.runtAndReturnOutput(
              "java -cp '"
                  + daikonJar.getAbsolutePath()
                  + "' daikon.PrintInvariants --format java "
                  + DAIKON_RESULTS_FILE);

      Files.write(results, CACHED_RESULTS_FILE, StandardCharsets.UTF_8);

      return results;

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean shouldRunDaikon(final File classSourceFile) {

    if (!DAIKON_LAST_RUN.exists()) {
      return true;
    }

    try {
      final String lastRunContent = Files.toString(DAIKON_LAST_RUN, StandardCharsets.UTF_8).trim();

      if (!lastRunContent.equals(classSourceFile.getAbsolutePath())) {
        return true;
      }

      if (!DAIKON_RESULTS_FILE.exists()) {
        return true;
      }

      return classSourceFile.lastModified() >= DAIKON_RESULTS_FILE.lastModified();

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
