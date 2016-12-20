package j2bpl;

import com.google.common.collect.Lists;
import soot.Pack;
import soot.PackManager;
import soot.Transform;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Optional;

public class Translator {

    /**
     * Translates all the classes in the class path.
     *
     * @param classPath   The classpath
     * @param pathToRrJar The path to the rt.jar lib.
     *
     * @see <a href="http://docs.oracle.com/javase/7/docs/technotes/tools/solaris/jdkfiles.html#jdk1.7.0_lib">rt.jar</a>
     */
    public void translate(String classPath, final String pathToRrJar) {

        if (classPath.contains(":") || classPath.contains(".jar")) {
            throw new UnsupportedOperationException("J2Bpl only supports a single directory as classpath.");
        }

        final PrintStream originalOut = System.out;
        final PrintStream originalErr = System.err;

        final ByteArrayOutputStream outReplacement = new ByteArrayOutputStream();
        final ByteArrayOutputStream errReplacement = new ByteArrayOutputStream();

        System.setOut(new PrintStream(outReplacement));
        System.setErr(new PrintStream(errReplacement));

        try {

            Pack pack = PackManager.v().getPack("jtp");

            pack.add(new Transform("jtp.bpl", J2BplTransformer.getInstance()));

            final String completeClassPath = pathToRrJar + ":" + classPath;

            soot.Main.main(new String[]{
                    "-keep-line-number",
                    "-cp",
                    completeClassPath,
                    "-f",
                    "jimple",
                    "-d",
                    "./dump",
                    "-src-prec",
                    "class",
                    "-process-path",
                    classPath
            });

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

        return J2BplTransformer.getInstance().getClass(className);
    }

    public String getTranslation() {

        return J2BplTransformer.getInstance().getTranslation();
    }

}
