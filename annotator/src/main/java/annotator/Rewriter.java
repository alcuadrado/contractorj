package annotator;

import com.github.javaparser.ast.CompilationUnit;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Rewriter {

  public File writeAnnotatedClass(
      final File annotatedSourceDir,
      final File classSourceFile,
      final CompilationUnit compilationUnit,
      final List<Invariant> invariants) {

    String sourceCode = compilationUnit.toString();
    sourceCode = sourceCode.substring(0, sourceCode.lastIndexOf("}"));
    sourceCode = sourceCode.trim();

    for (final Invariant invariant : invariants) {
      sourceCode += "\n\n    " + invariant.toMethod().replace("\n", "\n    ");
    }

    sourceCode +=
        "\n"
            + "\n"
            + "    private static int $$size(Object[] arr) {\n"
            + "        return arr.length;\n"
            + "    }\n"
            + "\n"
            + "    private static int $$size(java.util.Collection col) {\n"
            + "        return col.size();\n"
            + "    }\n";

    sourceCode += "\n}\n";

    final File annotatedFile = new File(annotatedSourceDir, classSourceFile.getName());

    try {
      Files.createParentDirs(annotatedFile);
      Files.write(sourceCode, annotatedFile, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return annotatedFile;
  }
}
