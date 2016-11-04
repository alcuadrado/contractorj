package contractorj;

import contractorj.epas.Epa;

import contractorj.epas.gen.ExponentialEpaGenerator;
import j2bpl.translation.Class;
import j2bpl.translation.InstanceField;
import j2bpl.translation.J2BplTransformer;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import soot.Pack;
import soot.PackManager;
import soot.Transform;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Main {

    public static void main(String[] args) throws IOException {

        ensureJava7();

        Options options = new Options();

        Option classpathOption = new Option("cp", "classpath", true,
                "Path to the classpath with the classes to analyze");
        classpathOption.setRequired(true);
        options.addOption(classpathOption);

        Option classNameOption = new Option("c", "class", true, "The class from which the EPA will be created");
        classNameOption.setRequired(true);
        options.addOption(classNameOption);

        Option dotPathOption = new Option("d", "dot", true, "The path to the dot output");
        dotPathOption.setRequired(true);
        options.addOption(dotPathOption);

        Option threadsOption = new Option("t", "threads", true,
                "The number of threads to use (default: the number of cores)");
        threadsOption.setType(Number.class);
        options.addOption(threadsOption);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        final CommandLine cmd;



        try {
            cmd = parser.parse(options, args);
            cmd.getParsedOptionValue("t");
        } catch (ParseException e) {
            System.out.println("Error " + e.getMessage());
            formatter.printHelp("ContractorJ", options);
            System.exit(1);
            return;
        }

        final String pathToClassPath = cmd.getOptionValue("cp");
        final String className = cmd.getOptionValue("c");
        final File outputFile = new File(cmd.getOptionValue("d"));

        final Integer defaultThreads = Runtime.getRuntime().availableProcessors();
        final int numberOfThreads = Integer.valueOf(cmd.getOptionValue("t", defaultThreads.toString()));

        final J2BplTransformer j2BplTransformer = J2BplTransformer.getInstance();

        runTranslation(j2BplTransformer, pathToClassPath);

        final Class classToMakeEpa = j2BplTransformer.getClass(className);

        if (classToMakeEpa == null) {
            throw new IllegalArgumentException("Can't find class " + className);
        }

        final ExponentialEpaGenerator exponentialEpaGenerator =
                new ExponentialEpaGenerator(classToMakeEpa, j2BplTransformer.getTranslation(), numberOfThreads);

        final Epa epa = exponentialEpaGenerator.generateEpa();

        Files.write(outputFile.toPath(), epa.toDot().getBytes());
    }

    private static void ensureJava7() {

        final String implementationVersion = Runtime.class.getPackage().getImplementationVersion();

        if (!implementationVersion.startsWith("1.7")) {
            System.err.println("ContractorJ cant be run with your jre because Soot doesn't support java >= 8.");
            System.exit(1);
        }
    }

    private static void runTranslation(J2BplTransformer j2BplTransformer, String pathToClassPath) {

        Pack pack = PackManager.v().getPack("jtp");

        pack.add(new Transform("jtp.bpl", j2BplTransformer));

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

}
