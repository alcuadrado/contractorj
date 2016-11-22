package contractorj;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import contractorj.construction.EpaGenerator;
import contractorj.construction.LazyEpaGenerator;
import contractorj.construction.queries.Query;
import contractorj.construction.corral.CorralRunner;
import contractorj.model.Epa;
import contractorj.serialization.DotEpaSerializer;
import contractorj.serialization.XmlEpaSerializer;
import j2bpl.Class;
import j2bpl.Translator;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.Optional;

public class Main {

    private static String pathToClassPath;

    private static String className;

    private static File dotOutputFile;

    private static File xmlOutputFile;

    private static int numberOfThreads = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) throws IOException {

        parseArguments(args);

        final Translator translator = new Translator();

        translator.translate(pathToClassPath, getRtJarPath());

        final Optional<Class> classToMakeEpa = translator.getTranslatedClass(className);

        if (!classToMakeEpa.isPresent()) {
            throw new IllegalArgumentException("Can't find class " + className);
        }

        final EpaGenerator epaEpaGenerator = new LazyEpaGenerator(
                translator.getTranslation(),
                numberOfThreads,
                new CorralRunner("/Users/pato/facultad/tesis/tools/corral/bin/Debug/corral.exe")
        );

        final Epa epa = epaEpaGenerator.generateEpa(classToMakeEpa.get());

        System.out.println("Total running time: " + formatDuration(epaEpaGenerator.getTotalTime()));
        System.out.println("Time running queries: " + formatDuration(epaEpaGenerator.getTotalQueryingTime()));
        System.out.println("Total number of queries: " + epaEpaGenerator.getTotalNumberOfQueries());

        System.out.println("Types of queries:");

        for (final java.lang.Class<? extends Query> queryClass : epaEpaGenerator.getQueryClasses()) {

            System.out.println("\t" + queryClass.getSimpleName());

            System.out.println("\t\tNumber of queries: " + epaEpaGenerator.getNumberOfQueriesByClass(queryClass));

            System.out.println("\t\tTime running queries: "
                    + formatDuration(epaEpaGenerator.getQueryingTimeByClass(queryClass)));

            System.out.println("\t\tAverage running time: "
                    + formatDuration(epaEpaGenerator.getAverageQueryingTimeByClass(queryClass)));

            System.out.println("");
        }

        final DotEpaSerializer dotEpaSerializer = new DotEpaSerializer();
        final XmlEpaSerializer xmlEpaSerializer = new XmlEpaSerializer();

        dotEpaSerializer.serializeToFile(epa, dotOutputFile);
        xmlEpaSerializer.serializeToFile(epa, xmlOutputFile);
    }

    private static void parseArguments(final String[] args) {

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

        Option xmlPathOption = new Option("x", "xml", true, "The path to the xml output");
        xmlPathOption.setRequired(true);
        options.addOption(xmlPathOption);

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

        pathToClassPath = cmd.getOptionValue("cp");
        className = cmd.getOptionValue("c");
        dotOutputFile = new File(cmd.getOptionValue("d"));
        xmlOutputFile = new File(cmd.getOptionValue("x"));

        if (cmd.hasOption('t')) {
            numberOfThreads = Integer.valueOf(cmd.getOptionValue('t'));
        }
    }

    private static String getRtJarPath() throws IOException {

        final URL url = Main.class.getResource("/java7-rt.jar");

        final File outputFile = File.createTempFile("temporal-rt-jar", "rt.jar");
        Resources.asByteSource(url).copyTo(Files.asByteSink(outputFile));

        return outputFile.getAbsolutePath();
    }

    private static String formatDuration(Duration duration) {

        final long millis = duration.toMillis();

        if (duration.compareTo(Duration.ofMinutes(1)) >= 0) {

            return millis / 60_000F + "m";
        }

        return millis / 1000F + "s";
    }

}
