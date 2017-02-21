package annotator.external;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import contractorj.util.CommandsRunner;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavacRunner {

  public static final Pattern PACKAGE_PATTERN = Pattern.compile("package\\s+(?<name>.*?)\\s*;");

  private final String classPath;

  private final File classesFolder;

  public JavacRunner(final List<File> libs, final File classesFolder) {

    this.classesFolder = classesFolder;

    classPath =
        libs.stream().map(File::getAbsolutePath).reduce((s1, s2) -> s1 + ":" + s2).orElse("");
  }

  public void compile(final File file) {

    compile(Lists.newArrayList(file));
  }

  public void compile(final List<File> files) {

    final String filesList =
        files.stream().map(File::getAbsolutePath).reduce((s, s2) -> s + " " + s2).get();

    final String command = "javac -source 1.7 -target 1.7 -g -cp '" + classPath + "' " + filesList;
    CommandsRunner.runtAndReturnOutput(command);

    files.forEach(this::moveClassFile);
  }

  private void moveClassFile(final File file) {

    final Optional<String> classPackage = getPackage(file);

    final File destinationFolder;

    if (classPackage.isPresent()) {
      destinationFolder = new File(classesFolder, classPackage.get().replace(".", "/"));
    } else {
      destinationFolder = classesFolder;
    }

    final File classFile = new File(file.getAbsolutePath().replace(".java", ".class"));

    try {

      final File destinationFile = new File(destinationFolder, classFile.getName());

      Files.createParentDirs(destinationFile);

      Files.move(classFile, destinationFile);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private Optional<String> getPackage(final File javaSourceFile) {

    final String source;

    try {
      source = Files.toString(javaSourceFile, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    final Matcher matcher = PACKAGE_PATTERN.matcher(source);

    if (!matcher.find()) {
      return Optional.empty();
    }

    return Optional.of(matcher.group("name"));
  }
}
