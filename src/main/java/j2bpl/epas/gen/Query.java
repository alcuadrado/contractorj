package j2bpl.epas.gen;

import com.google.common.base.Joiner;
import j2bpl.epas.Contract;
import j2bpl.epas.State;
import j2bpl.translation.Method;
import j2bpl.translation.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Query {

    private final Collection<Contract> contracts;

    private final Method invariant;

    private final State from;

    private final State to;

    private final Contract transition;

    public Query(Collection<Contract> contracts, Method invariant, State from, State to, Contract transition) {

        this.contracts = contracts;
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

    public Contract getTransition() {

        return transition;
    }

    public String getName() {

        final String baseName = "from_" + getStateName(from) + "_to_" + getStateName(to) + "_via_"
                + transition.method.getBaseJavaName();

        return StringUtils.scapeIllegalIdentifierCharacters(baseName);
    }

    private String getStateName(State state) {

        final ArrayList<String> names = new ArrayList<>(state.contracts.size());

        for (Contract contract : state.contracts) {
            names.add(contract.method.getBaseJavaName());
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

        final ArrayList<String> variablesDeclarations = new ArrayList<>(contracts.size() + 1);

        variablesDeclarations.add("var retInv : bool;");

        for (Contract contract : contracts) {
            variablesDeclarations.add("var " + getLocalVariableForContract(contract) + " : bool;");
        }

        if (transition.method.hasReturnType()) {
            final String declarations = "var " + getLocalVariableForMethod(transition.method) + " : "
                    + transition.method.getTranslatedReturnType() + ";";
            variablesDeclarations.add(declarations);
        }

        return variablesDeclarations;
    }

    private String getStateIfGuard(State state) {

        final ArrayList<String> conditions = new ArrayList<>(contracts.size() + 1);

        conditions.add("retInv");

        for (Contract contract : contracts) {

            final String conditionVar = "ret_" + contract.precondition.getTranslatedName();

            if (state.contracts.contains(contract)) {
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

        final ArrayList<String> calls = new ArrayList<>(contracts.size() + 1);

        calls.add("call retInv := " + invariant.getTranslatedName() + "($this);");

        for (Contract contract : contracts) {

            final String call = "call " + getLocalVariableForContract(contract) + " := "
                    + contract.precondition.getTranslatedName() + "("
                    + Joiner.on(", ").join(getArgumentNames(contract, isAfterCallingTransition))
                    + ");";

            calls.add(call);
        }

        return calls;
    }

    private String getLocalVariableForContract(Contract contract) {

        return getLocalVariableForMethod(contract.precondition);
    }

    private String getLocalVariableForMethod(Method method) {

        return "ret_" + method.getTranslatedName();
    }

    private String getArgumentsDeclarationsList() {

        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("$this : Ref");

        for (Contract contract : contracts) {

            final List<String> translatedArgumentTypes = contract.method.getTranslatedArgumentTypes();

            for (int i = 1; i < translatedArgumentTypes.size(); i++) {

                stringBuilder.append(", ")
                        .append(getArgumentNameForContract(contract, i, false))
                        .append(" : ")
                        .append(translatedArgumentTypes.get(i));

                stringBuilder.append(", ")
                        .append(getArgumentNameForContract(contract, i, true))
                        .append(" : ")
                        .append(translatedArgumentTypes.get(i));
            }
        }

        return stringBuilder.toString();
    }

    private String getArgumentNameForContract(Contract contract, int argumentIndex, boolean isAfterCallingTransition) {

        return contract.method.getBaseJavaName() + argumentIndex + (isAfterCallingTransition ? "After" : "");
    }

    private List<String> getArgumentNames(Contract contract, boolean isAfterCallingTransition) {

        final ArrayList<String> names = new ArrayList<>(contract.method.getTranslatedArgumentTypes().size() + 1);
        names.add("$this");

        for (int i = 1; i < contract.method.getTranslatedArgumentTypes().size(); i++) {
            names.add(getArgumentNameForContract(contract, i, isAfterCallingTransition));
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
