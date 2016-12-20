package annotator;

import java.util.List;

class ParamsPrecondition extends Invariant {

    private final Method method;

    ParamsPrecondition(final List<String> conditions, final Method method) {

        super(conditions);
        this.method = method;
    }

    @Override
    public String toString() {

        return "ParamsPrecondition(for=" + method.getSimpleName() + ") " + super.toString();
    }

    @Override
    public String toMethod() {

        final String declaration = method.getDeclarationAsString(
                false,
                false,
                true
        );

        final String paramsList = declaration.substring(
                declaration.indexOf("(") + 1,
                declaration.indexOf(")")
        );



        return "public "
                + (method.isConstructor() ? "static " : "")
                + "boolean " + method.getSimpleName() + "_pre(" + paramsList +") " + super.toMethod();
    }
}
