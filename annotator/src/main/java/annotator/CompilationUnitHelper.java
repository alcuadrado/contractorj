package annotator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.nodeTypes.NodeWithModifiers;

public class CompilationUnitHelper {

  static String getClassName(final CompilationUnit compilationUnit) {

    return compilationUnit
        .getTypes()
        .stream()
        .filter(NodeWithModifiers::isPublic)
        .map(TypeDeclaration::getName)
        .map(SimpleName::toString)
        .findFirst()
        .get();
  }

  static String getQualifiedClassName(final CompilationUnit compilationUnit) {

    final String className = getClassName(compilationUnit);

    if (!compilationUnit.getPackage().isPresent()) {
      return className;
    }

    final String packageName =
        compilationUnit.getPackage().map(PackageDeclaration::getPackageName).get();

    return packageName + "." + className;
  }
}
