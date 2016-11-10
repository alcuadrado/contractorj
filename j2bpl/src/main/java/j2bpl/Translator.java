package j2bpl;

import soot.Pack;
import soot.PackManager;
import soot.Transform;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Translator {

    public static final String JAVA_HOME_BIN_PATH = "/usr/libexec/java_home";

    public void translate(String pathToClassPath) {

        Pack pack = PackManager.v().getPack("jtp");

        pack.add(new Transform("jtp.bpl", J2BplTransformer.getInstance()));

        final File rtFile = new File(new File(new File(getJava7Home(), "jre"), "lib"), "rt.jar");

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

    private String getJava7Home() {

        final File javaHomeBin = new File(JAVA_HOME_BIN_PATH);

        if (javaHomeBin.exists()) {
            try {
                final ProcessBuilder processBuilder = new ProcessBuilder(JAVA_HOME_BIN_PATH, "-v", "1.7*");
                final Process process;
                process = processBuilder.start();

                final int statusCode = process.waitFor();

                if (statusCode == 0) {
                    return new BufferedReader(new InputStreamReader(process.getInputStream()))
                            .readLine()
                            .trim();
                }
            } catch (IOException | InterruptedException e) {
                // Do nothing and try another method.
            }
        }

        final String java7Home = System.getenv("JAVA7_HOME");

        if (java7Home != null) {
            return java7Home;
        }

        throw new IllegalStateException("No Java 7 found. Try setting JAVA7_HOME env variable.");
    }

}
