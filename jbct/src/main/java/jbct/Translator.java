package jbct;

import com.google.common.collect.Lists;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.List;
import java.util.Optional;
import jbct.model.Class;
import jbct.soot.JbctTransformer;
import soot.Pack;
import soot.PackManager;
import soot.Transform;

public class Translator {

  /**
   * Translates all the classes in the class path.
   *
   * @param classPath The classpath
   * @param pathToRrJar The path to the rt.jar lib.
   * @see <a
   *     href="http://docs.oracle.com/javase/7/docs/technotes/tools/solaris/jdkfiles.html#jdk1.7.0_lib">rt.jar</a>
   */
  public void translate(String classPath, final File pathToRrJar, final boolean dumpJimple) {

    if (classPath.contains(":") || classPath.contains(".jar")) {
      throw new UnsupportedOperationException(
          "JBCT only supports a single directory as classpath.");
    }

    final PrintStream originalOut = System.out;
    final PrintStream originalErr = System.err;

    final ByteArrayOutputStream outReplacement = new ByteArrayOutputStream();
    final ByteArrayOutputStream errReplacement = new ByteArrayOutputStream();

    System.setOut(new PrintStream(outReplacement));
    System.setErr(new PrintStream(errReplacement));

    try {

      Pack pack = PackManager.v().getPack("jtp");

      pack.add(new Transform("jtp.bpl", JbctTransformer.getInstance()));

      final String completeClassPath = pathToRrJar.getAbsolutePath() + File.pathSeparator + classPath;

      final List<String> args =
          Lists.newArrayList(
              "-keep-line-number",
                  "-allow-phantom-refs",
              "-cp",
              completeClassPath,
              "-f",
              "jimple",
              "-src-prec",
              "class",
              "-process-path",
              classPath);

      if (dumpJimple) {
        args.add("-d");
        args.add("/Users/pato/facultad/tesis/contractorj/dump");
      }

      soot.Main.main(args.toArray(new String[args.size()]));

    } catch (Exception exception) {

      originalOut.print(outReplacement.toString());
      originalErr.print(errReplacement.toString());

      throw exception;

    } finally {
      System.setOut(originalOut);
      System.setErr(originalErr);
    }
  }

  public Optional<Class> getTranslatedClass(String className) {

    return JbctTransformer.getInstance().getClass(className);
  }

  public String getTranslation() {

    return JbctTransformer.getInstance().getTranslation();
  }
}
