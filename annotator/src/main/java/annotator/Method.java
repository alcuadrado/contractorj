package annotator;

import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.EqualsVisitor;

import java.util.Objects;

class Method {

    private final MethodDeclaration methodDeclaration;

    private final ConstructorDeclaration constructorDeclaration;

    private EqualsVisitor simpleName;

    public static Method fromMethodDeclaration(MethodDeclaration methodDeclaration) {

        return new Method(methodDeclaration, null);
    }

    public static Method fromConstructorDeclaration(ConstructorDeclaration constructorDeclaration) {

        return new Method(null, constructorDeclaration);
    }

    private Method(final MethodDeclaration methodDeclaration,
                   final ConstructorDeclaration constructorDeclaration) {

        this.methodDeclaration = methodDeclaration;
        this.constructorDeclaration = constructorDeclaration;
    }

    public int getNumberOfParameters() {

        if (methodDeclaration != null) {
            return methodDeclaration.getParameters().size();
        }

        return constructorDeclaration.getParameters().size();
    }

    public String getSimpleName() {

        if (methodDeclaration != null) {
            return methodDeclaration.getName().toString();
        }

        return constructorDeclaration.getName().toString();
    }

    public String getDeclarationAsString(final boolean includingModifiers, final boolean includingThrows,
                                         final boolean includingParameterName) {

        if (methodDeclaration != null) {
            return methodDeclaration.getDeclarationAsString(includingModifiers, includingThrows,
                    includingParameterName);
        }

        return constructorDeclaration.getDeclarationAsString(includingModifiers, includingThrows,
                includingParameterName);
    }

    public boolean isConstructor() {
        return constructorDeclaration != null;
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }
        if (!(o instanceof Method)) {
            return false;
        }
        final Method method = (Method) o;
        return Objects.equals(methodDeclaration, method.methodDeclaration) &&
                Objects.equals(constructorDeclaration, method.constructorDeclaration);
    }

    @Override
    public int hashCode() {

        return Objects.hash(methodDeclaration, constructorDeclaration);
    }

    @Override
    public String toString() {

        return getDeclarationAsString(true, true, true);
    }
}
