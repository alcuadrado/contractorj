package contractorj.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

public class CommandsRunner {

  public static String runtAndReturnOutput(final String lineToRun, final boolean newDirAsCWD) {

    final CommandLine commandLine = CommandLine.parse(lineToRun);

    final DefaultExecutor executor = new DefaultExecutor();

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);

    executor.setStreamHandler(streamHandler);

    // Once Corral founds a counterexample, it writes it as file.
    // There is no argument in Corral to change the output file name.
    // If there is more than one thread executing Corral then Corral's exitValue can be 0.
    // Windows fails if there is more than 1 thread writing the same file
    // ContractorJ doesn't need the file because it uses the outputStream

    Path dir = null;
    if (newDirAsCWD) {
      try {
        dir = Files.createTempDirectory("cwd_");
        executor.setWorkingDirectory(dir.toFile());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    try {
      final int exitValue = executor.execute(commandLine);

      if (exitValue != 0) {
        throw new RuntimeException(
            "Error executing " + lineToRun + "\n " + outputStream.toString());
      }

    } catch (IOException e) {
      throw new RuntimeException("Error executing " + lineToRun + "\n " + outputStream.toString());
    }

    if (newDirAsCWD && dir != null) {
      for (File file : dir.toFile().listFiles()) {
        if (!file.isDirectory()) file.delete();
      }
      dir.toFile().delete();
    }

    return outputStream.toString();
  }
}
