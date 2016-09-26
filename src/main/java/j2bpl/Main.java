package j2bpl;

import j2bpl.translation.J2BplTransformer;
import soot.Pack;
import soot.PackManager;
import soot.Transform;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class Main {

    public static void main(String[] args) throws IOException {

        if (args.length == 0) {
            System.err.println("Missing arguments: class-files-dir [output-file]");
            System.exit(1);
        }

        final String pathToClassFilesFolder = args[0];

        final PrintStream outputStream;

        if (args.length > 1) {
            final File file = new File(args[1]);

            if (!file.exists()) {
                assert file.createNewFile();
            }

            outputStream = new PrintStream(file);
        } else {
            outputStream = System.out;
        }

        final J2BplTransformer j2BplTransformer = J2BplTransformer.getInstance();

        runTranslation(j2BplTransformer, pathToClassFilesFolder);

        outputStream.print(j2BplTransformer.getTranslation());
    }

    private static void runTranslation(J2BplTransformer j2BplTransformer, String pathToClassFilesFolder) {

        Pack pack = PackManager.v().getPack("jtp");

        pack.add(new Transform("jtp.bpl", j2BplTransformer));

        final String javaHome = System.getProperty("java.home");

        final File rtFile = new File(new File(javaHome, "lib"), "rt.jar");

        final String[] args = {
                "-keep-line-number",
                "-pp",
                "-cp",
                rtFile.getAbsolutePath() + ":" + pathToClassFilesFolder,
                "-f",
                "jimple",
                "-d",
                "./dump",
                "-src-prec",
                "class",
                "-process-path",
                pathToClassFilesFolder
        };

        soot.Main.main(args);
    }

}
