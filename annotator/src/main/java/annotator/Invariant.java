package annotator;

import java.util.List;

abstract class Invariant {

    private final List<String> conditions;

    protected Invariant(final List<String> conditions) {

        this.conditions = conditions;
    }

    public String toMethod() {

        final String body = "return "
                + conditions.stream()
                .map(s -> "(" + s + ")")
                .reduce((s1, s2) -> s1 + " && \n        " + s2)
                .orElse("true")
                + ";";

        return "{\n    " + body + "\n}";
    }

    @Override
    public String toString() {

        final String conditionsList = conditions.stream()
                .map(s -> "    " + s)
                .reduce((s1, s2) -> s1 + "\n" + s2)
                .orElse("");

        return "{\n" + conditionsList + "\n}";
    }

}
