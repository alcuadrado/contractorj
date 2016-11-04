package contractorj.epas.gen;

import com.google.common.base.Joiner;
import contractorj.epas.Action;
import contractorj.epas.State;
import j2bpl.Method;
import j2bpl.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Query {

    private final Collection<Action> actions;

    private final Method invariant;

    private final State from;

    private final State to;

    private final Action transition;

    public Query(Collection<Action> actions, Method invariant, State from, State to, Action transition) {

        this.actions = actions;
        this.invariant = invariant;
        this.from = from;
        this.to = to;
        this.transition = transition;
    }

    public State getFrom() {

        return from;
    }

    public State getTo() {

        return to;
    }

    public Action getTransition() {

        return transition;
    }

    public String getName() {

        final String baseName = "from_" + getStateName(from) + "_to_" + getStateName(to) + "_via_"
                + transition.method.getBaseJavaName();

        return StringUtils.scapeIllegalIdentifierCharacters(baseName);
    }

    private String getStateName(State state) {

        final ArrayList<String> names = new ArrayList<>(state.actions.size());

        for (Action action : state.actions) {
            names.add(action.method.getBaseJavaName());
        }

        Collections.sort(names);

        return "#" + Joiner.on("~").join(names) + "#";
    }

    public String getBoogieCode() {

        final StringBuilder query = new StringBuilder();

        query.append("procedure ")
                .append(getName())
                .append("(")
                .append(getArgumentsDeclarationsList())
                .append(") {\n")

                .append("\n")

                .append(StringUtils.indentList(getVariablesDeclarations()))
                .append("\n")

                .append("\n")

                .append(StringUtils.indent("call initialize_globals();"))
                .append("\n")

                .append("\n")

                .append(StringUtils.indentList(getInvariantAndPreconditionsCalls(false)))

                .append("\n")

                .append("\n")

                .append(StringUtils.indent("if ("))
                .append(getStateIfGuard(from))
                .append(") {\n")

                .append(StringUtils.indent(StringUtils.indent(getInnerIfBody())))

                .append("\n")

                .append(StringUtils.indent("}"))
                .append("\n")

                .append("\n")
                .append(StringUtils.indent("assert $Exception == null;"))
                .append("\n")

                .append("}");

        return query.toString();
    }

    private List<String> getVariablesDeclarations() {

        final ArrayList<String> variablesDeclarations = new ArrayList<>(actions.size() + 1);

        variablesDeclarations.add("var retInv : bool;");

        for (Action action : actions) {
            variablesDeclarations.add("var " + getLocalVariableForContract(action) + " : bool;");
        }

        if (transition.method.hasReturnType()) {
            final String declarations = "var " + getLocalVariableForMethod(transition.method) + " : "
                    + transition.method.getTranslatedReturnType() + ";";
            variablesDeclarations.add(declarations);
        }

        return variablesDeclarations;
    }

    private String getStateIfGuard(State state) {

        final ArrayList<String> conditions = new ArrayList<>(actions.size() + 1);

        conditions.add("retInv");

        for (Action action : actions) {

            final String conditionVar = "ret_" + action.precondition.getTranslatedName();

            if (state.actions.contains(action)) {
                conditions.add(conditionVar);
            } else {
                conditions.add("!" + conditionVar);
            }
        }

        return Joiner.on(" && ").join(conditions);
    }

    private String getTransitionCall() {

        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("call ");

        if (transition.method.hasReturnType()) {
            stringBuilder.append(getLocalVariableForMethod(transition.method))
                    .append(" := ");
        }

        return stringBuilder.append(transition.method.getTranslatedName())
                .append("(")
                .append(Joiner.on(", ").join(getArgumentNames(transition, false)))
                .append(");")
                .toString();
    }

    private List<String> getInvariantAndPreconditionsCalls(boolean isAfterCallingTransition) {

        final ArrayList<String> calls = new ArrayList<>(actions.size() + 1);

        calls.add("call retInv := " + invariant.getTranslatedName() + "($this);");

        for (Action action : actions) {

            final String call = "call " + getLocalVariableForContract(action) + " := "
                    + action.precondition.getTranslatedName() + "("
                    + Joiner.on(", ").join(getArgumentNames(action, isAfterCallingTransition))
                    + ");";

            calls.add(call);
        }

        return calls;
    }

    private String getLocalVariableForContract(Action action) {

        return getLocalVariableForMethod(action.precondition);
    }

    private String getLocalVariableForMethod(Method method) {

        return "ret_" + method.getTranslatedName();
    }

    private String getArgumentsDeclarationsList() {

        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("$this : Ref");

        for (Action action : actions) {

            final List<String> translatedArgumentTypes = action.method.getTranslatedArgumentTypes();

            for (int i = 1; i < translatedArgumentTypes.size(); i++) {

                stringBuilder.append(", ")
                        .append(getArgumentNameForContract(action, i, false))
                        .append(" : ")
                        .append(translatedArgumentTypes.get(i));

                stringBuilder.append(", ")
                        .append(getArgumentNameForContract(action, i, true))
                        .append(" : ")
                        .append(translatedArgumentTypes.get(i));
            }
        }

        return stringBuilder.toString();
    }

    private String getArgumentNameForContract(Action action, int argumentIndex, boolean isAfterCallingTransition) {

        return action.method.getBaseJavaName() + argumentIndex + (isAfterCallingTransition ? "After" : "");
    }

    private List<String> getArgumentNames(Action action, boolean isAfterCallingTransition) {

        final ArrayList<String> names = new ArrayList<>(action.method.getTranslatedArgumentTypes().size() + 1);
        names.add("$this");

        for (int i = 1; i < action.method.getTranslatedArgumentTypes().size(); i++) {
            names.add(getArgumentNameForContract(action, i, isAfterCallingTransition));
        }
        return names;
    }

    private String getInnerIfBody() {

        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("\n")

                .append(getTransitionCall())
                .append("\n")

                .append("\n")

                .append(Joiner.on("\n").join(getInvariantAndPreconditionsCalls(true)))
                .append("\n")

                .append("\n")

                .append("if (")
                .append(getStateIfGuard(to))
                .append(") {\n")

                .append(StringUtils.indent("query_assertion:"))
                .append("\n")
                .append(StringUtils.indent(StringUtils.indent("assert false;")))
                .append("\n")

                .append("}");

        return stringBuilder.toString();
    }
}
