package j2bpl;

import soot.Pack;
import soot.PackManager;
import soot.Transform;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Optional;

public class Translator {

    /**
     * Translates all the classes in the class path.
     *
     * @param pathToClassPath The path to the class path.
     * @param pathToRrJar     The path to the rt.jar lib.
     *
     * @see <a href="http://docs.oracle.com/javase/7/docs/technotes/tools/solaris/jdkfiles.html#jdk1.7.0_lib">rt.jar</a>
     */
    public void translate(String pathToClassPath, final String pathToRrJar) {

        final PrintStream originalOut = System.out;
        final PrintStream originalErr = System.err;

        final ByteArrayOutputStream outReplacement = new ByteArrayOutputStream();
        final ByteArrayOutputStream errReplacement = new ByteArrayOutputStream();

        System.setOut(new PrintStream(outReplacement));
        System.setErr(new PrintStream(errReplacement));

        try {

            Pack pack = PackManager.v().getPack("jtp");

            pack.add(new Transform("jtp.bpl", J2BplTransformer.getInstance()));

            final String[] args = {
                    "-keep-line-number",
                    "-pp",
                    "-cp",
                    pathToRrJar + ":" + pathToClassPath,
                    "-f",
                    "jimple",
                    "-d",
                    "./dump",
                    "-src-prec",
                    "class",
                    "-process-path",
                    pathToClassPath
            };

            soot.Main.main(args);
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
