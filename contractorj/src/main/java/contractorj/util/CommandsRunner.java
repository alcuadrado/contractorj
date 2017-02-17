package contractorj.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

public class CommandsRunner {

  public static String runtAndReturnOutput(final String lineToRun) {

    final CommandLine commandLine = CommandLine.parse(lineToRun);

    final DefaultExecutor executor = new DefaultExecutor();

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);

    executor.setStreamHandler(streamHandler);

    try {
      final int exitValue = executor.execute(commandLine);

      if (exitValue != 0) {
        throw new RuntimeException(
            "Error executing " + lineToRun + "\n " + outputStream.toString());
      }

    } catch (IOException e) {
      throw new RuntimeException("Error executing " + lineToRun + "\n " + outputStream.toString());
    }

    return outputStream.toString();
  }
}
