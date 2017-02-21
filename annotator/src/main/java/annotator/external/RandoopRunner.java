package annotator.external;

import contractorj.util.CommandsRunner;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class RandoopRunner {

  private final File randoopJar;

  public RandoopRunner(final File randoopJar) {
    this.randoopJar = randoopJar;
  }

  public void run(final File classesDir, final String qualifiedClassName) {

    final String command = getCommand(classesDir, qualifiedClassName);
    CommandsRunner.runtAndReturnOutput(command);
  }

  private String getCommand(final File classesDir, final String qualifiedClassName) {

    return "java"
        + " -ea"
        + " -cp '"
        + randoopJar.getAbsolutePath()
        + ":"
        + classesDir.getAbsolutePath()
        + "'"
        + " randoop.main.Main"
        + " gentests"
        + " --testclass="
        + qualifiedClassName
        + " --timelimit=180"
        + " --junit-reflection-allowed=false";
  }

  public List<File> getResultFiles() {

    try {
      return Files.list(new File(".").toPath())
          .map(Path::toFile)
          .filter(file -> file.getName().startsWith("RegressionTest"))
          .filter(file -> file.getName().endsWith(".java"))
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
