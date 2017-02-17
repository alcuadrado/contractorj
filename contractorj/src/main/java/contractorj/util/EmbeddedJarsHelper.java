package contractorj.util;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class EmbeddedJarsHelper {

  /**
   * Moves an embedded jar to a temporal folder.
   *
   * <p>Note: The jar mut be in the classpath' resource's root with ".jar" replaced in its name by
   * "-jar"
   *
   * @param originalJarName The original jar name.
   * @return The new file with the jar.
   */
  public File moveToTemporalFolder(final String originalJarName) {

    final String resourceName = "/" + originalJarName.replace(".jar", "-jar");

    final URL url = Object.class.getResource(resourceName);

    if (url == null) {
      throw new IllegalArgumentException(
          "Couldn't find jar " + originalJarName + " as resource " + resourceName);
    }

    try {

      final File tempDir = Files.createTempDir();
      final File outputFile = new File(tempDir, originalJarName);
      Resources.asByteSource(url).copyTo(Files.asByteSink(outputFile));

      return outputFile;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
