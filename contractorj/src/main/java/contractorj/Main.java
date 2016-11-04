package contractorj;

import contractorj.epas.Epa;

import contractorj.epas.gen.ExponentialEpaGenerator;
import j2bpl.translation.Class;
import j2bpl.translation.J2BplTransformer;
import soot.Pack;
import soot.PackManager;
import soot.Transform;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Main {

    public static void main(String[] args) throws IOException {

        final String implementationVersion = Runtime.class.getPackage().getImplementationVersion();

        if (implementationVersion.startsWith("1.8")) {
            System.err.println("You are using Java 8, which is not supported by Soot.");
            System.exit(1);
        }

        if (args.length != 3) {
            System.err.println("Missing arguments: class-files-dir qualified-name-of-class dot-file");
            System.exit(1);
        }

        final String pathToClassFilesFolder = args[0];
        final String className = args[1];
        final File outputFile = new File(args[2]);

        final J2BplTransformer j2BplTransformer = J2BplTransformer.getInstance();

        runTranslation(j2BplTransformer, pathToClassFilesFolder);

        final Class classToMakeEpa = j2BplTransformer.getClass(className);

        if (classToMakeEpa == null) {
            throw new IllegalArgumentException("Can't find class " + className);
        }

        final ExponentialEpaGenerator exponentialEpaGenerator =
                new ExponentialEpaGenerator(classToMakeEpa, j2BplTransformer.getTranslation());

        final Epa epa = exponentialEpaGenerator.generateEpa();

        Files.write(outputFile.toPath(), epa.toDot().getBytes());
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
