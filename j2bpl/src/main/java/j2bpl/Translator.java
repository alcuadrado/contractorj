package j2bpl;

import soot.Pack;
import soot.PackManager;
import soot.Transform;

import java.io.File;

public class Translator {

    public void translate(String pathToClassPath) {

        ensureJava7();

        Pack pack = PackManager.v().getPack("jtp");

        pack.add(new Transform("jtp.bpl", J2BplTransformer.getInstance()));

        final String javaHome = System.getProperty("java.home");

        final File rtFile = new File(new File(javaHome, "lib"), "rt.jar");

        final String[] args = {
                "-keep-line-number",
                "-pp",
                "-cp",
                rtFile.getAbsolutePath() + ":" + pathToClassPath,
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

    }

    public Class getTranslatedClass(String className) {
        return J2BplTransformer.getInstance().getClass(className);
    }

    public String getTranslation() {
        return J2BplTransformer.getInstance().getTranslation();
    }

    private static void ensureJava7() {

        final String implementationVersion = Runtime.class.getPackage().getImplementationVersion();

        if (!implementationVersion.startsWith("1.7")) {
            throw new RuntimeException("J2Bpl can't be run with your jre because Soot doesn't support java >= 8.");
        }
    }


}
