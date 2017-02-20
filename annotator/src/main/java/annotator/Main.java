package annotator;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.google.common.collect.Lists;
import contractorj.util.EmbeddedJarsHelper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {

  private final CompilationUnit compilationUnit;

  private File classSourceFile;

  private final Compiler compiler;

  private String pathToCorral;

  private File classPath;

  private File dotOutputFile;

  private File xmlOutputFile;

  private final File randoopJar;

  private final File daikonJar;

  private final File hamcrestCoreJar;

  private final File jUnitJar;

  public static void main(String[] args) {

    final Main main = new Main(args);

    main.createEpa();
  }

  private Main(String[] args) {

    final EmbeddedJarsHelper embeddedJarsHelper = new EmbeddedJarsHelper();

    daikonJar = embeddedJarsHelper.moveToTemporalFolder("daikon.jar");
    hamcrestCoreJar = embeddedJarsHelper.moveToTemporalFolder("hamcrest-core-1.3.jar");
    jUnitJar = embeddedJarsHelper.moveToTemporalFolder("junit-4.12.jar");
    randoopJar = embeddedJarsHelper.moveToTemporalFolder("randoop-all-3.0.8.jar");

    parseArguments(args);

    compiler = initCompiler(classPath);

    try {
      compilationUnit = JavaParser.parse(classSourceFile);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private Compiler initCompiler(final File classesDir) {

    final List<File> classPathParts = Lists.newArrayList(hamcrestCoreJar, jUnitJar);

    classPathParts.add(classesDir);

    return new Compiler(classPathParts, classesDir);
  }

  private void createEpa() {

    final String qualifiedClassName = CompilationUnitHelper.getQualifiedClassName(compilationUnit);

    System.out.println("Compiling your class");
    compiler.compile(classSourceFile);

    final DaikonRunner daikonRunner = new DaikonRunner(daikonJar, hamcrestCoreJar, jUnitJar);

    if (daikonRunner.shouldRunDaikon(classSourceFile)) {

      final RandoopRunner randoopRunner = new RandoopRunner(randoopJar);

      System.out.println("Deleting any previous Randoop generated test");
      deleteRandoopGeneratedSources();

      System.out.println("Running Randoop");
      randoopRunner.run(classPath, qualifiedClassName);

      System.out.println("Compiling Randoop's results");
      compiler.compile(randoopRunner.getResultFiles());

      System.out.println("Running Daikon");
      daikonRunner.runDaikon(classSourceFile, classPath);

      System.out.println("Deleting randoop files");
      deleteRandoopClasses();
    }

    System.out.println("Reading Daikon's results");
    final String daikonResults = daikonRunner.readDaikonResults();

    final InvariantsExtractor invariantsExtractor = new InvariantsExtractor();

    System.out.println("Computing invariants");
    final List<Invariant> invariants =
        invariantsExtractor.computeInvariants(daikonResults, compilationUnit);

    final Rewriter rewriter = new Rewriter();

    System.out.println("Writing annotated class");

    final File annotatedSourceDir = new File("annotated_source");
    final File annotatedClass =
        rewriter.writeAnnotatedClass(
            annotatedSourceDir, classSourceFile, compilationUnit, invariants);

    System.out.println("Compiling annotated class");
    compiler.compile(annotatedClass);

    System.out.println("Running ContractorJ");
    runContractorJ(qualifiedClassName);
  }

  private void runContractorJ(final String qualifiedClassName) {

    final String[] args = {
      "--classpath",
      classPath.getAbsolutePath(),
      "--corral",
      pathToCorral,
      "--class",
      qualifiedClassName,
      "--dot",
      dotOutputFile.getAbsolutePath(),
      "--xml",
      xmlOutputFile.getAbsolutePath()
    };

    try {
      contractorj.Main.main(args);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void deleteRandoopGeneratedSources() {
    try {
      Files.list(new File(".").toPath())
      .map(Path::toFile)
      .filter(file -> file.getName().startsWith("RegressionTest"))
      .filter(file -> file.getName().endsWith(".java"))
      .forEach(File::delete);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void deleteRandoopClasses() {

    try {

      Files.list(classPath.toPath())
          .map(Path::toFile)
          .filter(file -> file.getName().startsWith("RegressionTest"))
          .filter(file -> file.getName().endsWith(".class"))
          .forEach(File::delete);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void parseArguments(final String[] args) {

    final Options options = new Options();

    final Option classpathOption =
        new Option("cp", "classpath", true, "Path to the classpath with the classes to analyze");
    classpathOption.setRequired(true);
    options.addOption(classpathOption);

    final Option corralPathOption = new Option("co", "corral", true, "The path to corral.exe");
    corralPathOption.setRequired(true);
    options.addOption(corralPathOption);

    final Option classSourceFileOption =
        new Option(
            "c", "class", true, "The source of the class from which the EPA will be created");
    classSourceFileOption.setRequired(true);
    options.addOption(classSourceFileOption);

    final Option dotPathOption = new Option("d", "dot", true, "The path to the dot output");
    dotPathOption.setRequired(true);
    options.addOption(dotPathOption);

    final Option xmlPathOption = new Option("x", "xml", true, "The path to the xml output");
    xmlPathOption.setRequired(true);
    options.addOption(xmlPathOption);

    final CommandLineParser parser = new DefaultParser();
    final HelpFormatter formatter = new HelpFormatter();

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

    pathToCorral = cmd.getOptionValue("co");
    classPath = new File(cmd.getOptionValue("cp"));
    classSourceFile = new File(cmd.getOptionValue("c"));
    dotOutputFile = new File(cmd.getOptionValue("d"));
    xmlOutputFile = new File(cmd.getOptionValue("x"));
  }
}
