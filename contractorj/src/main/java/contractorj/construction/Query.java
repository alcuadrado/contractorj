package contractorj.construction;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import contractorj.model.Action;
import contractorj.model.State;
import j2bpl.Method;
import j2bpl.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Query {

    public static final String QUERY_ASSERTION_LABEL = "query_assertion";

    public static final String APPLICATION_BUG_LABEL = "application_bug";

    public static final String UNHANDLED_EXCEPTION_LABEL = "unhandled_exception";

    private static final String TARGET_ARGS_SUFFIX = "After";

    private final String namePrefix;

    private final State source;

    private final Action transition;

    private final State target;

    private final Method invariant;

    public Query(final String namePrefix, State source, Action transition, State target, Method invariant) {

        this.namePrefix = namePrefix;

        this.source = source;
        this.transition = transition;
        this.target = target;
        this.invariant = invariant;

        if (!source.enabledActions.contains(transition)) {
            throw new IllegalArgumentException("Invalid query: transition must be in source's enabled actions.");
        }
    }

    public String getName() {

        final String baseName = namePrefix + "from__________" + getStateName(source)
                + "__________to__________" + getStateName(target) + "__________via__________"
                + transition.method.getJavaNameWithArgumentTypes();

        return StringUtils.scapeIllegalIdentifierCharacters(baseName);
    }

    private String getStateName(State state) {

        final String joiner = "____";

        if (!state.enabledActions.isEmpty()) {
            return getJoinedActionNames(state.enabledActions, joiner);
        }

        if (!state.disabledActions.isEmpty()) {
            return "NOT_" + getJoinedActionNames(state.disabledActions, joiner);
        }

        return "EMPTY_STATE";
    }

    private String getJoinedActionNames(Set<Action> actions, String joiner) {

        final ArrayList<String> names = new ArrayList<>(actions.size());

        for (Action action : actions) {
            names.add(action.method.getJavaNameWithArgumentTypes());
        }

        Collections.sort(names);

        return Joiner.on(joiner).join(names);
    }

    public State getSource() {

        return source;
    }

    public State getTarget() {

        return target;
    }

    public Action getTransition() {

        return transition;
    }

    public String getBoogieCode() {

        final StringBuilder queryBody = new StringBuilder();

        final String invariantResultVariableName = getVariableForMethodResult(invariant).get().name;

        queryBody
                .append(getLocalVariablesDeclaration()).append("\n")
                .append("\n")
                .append("\n")

                .append("call initialize_globals();\n")
                .append("\n")
                .append("\n")

                .append(getStateGuardCalls(source, "")).append("\n")
                .append("\n")
                .append("\n");

        if (!source.isConstructorsState()) {
            queryBody
                    .append("assume ").append(getVariableForMethodResult(invariant).get().name).append(";\n");
        }

        queryBody
                .append("assume (").append(getStateGuard(source)).append(");\n")
                .append("\n")
                .append("\n")

                .append(getCall(transition, "", false)).append("\n")
                .append("\n")
                .append("\n")

                .append(getStateGuardCalls(target, TARGET_ARGS_SUFFIX)).append("\n")
                .append("\n")
                .append("\n")

                .append(APPLICATION_BUG_LABEL + ":\n")
                .append(StringUtils.indent("assert ")).append(invariantResultVariableName).append(";\n")
                .append("\n")

                .append(QUERY_ASSERTION_LABEL + ":\n")
                .append(StringUtils.indent("assert !(")).append(getStateGuard(target)).append(");\n")
                .append("\n")

                .append(UNHANDLED_EXCEPTION_LABEL + ":\n")
                .append(StringUtils.indent("assert $Exception == null;")).append("\n");

        final StringBuilder query = new StringBuilder();

        query.append("procedure ").append(getName()).append("(").append(getQueryArgumentsDeclaration()).append(") {\n")
                .append("\n")
                .append(StringUtils.indent(queryBody.toString())).append("\n")
                .append("}");

        return query.toString();
    }

    private String getLocalVariablesDeclaration() {

        final Set<Variable> variables = getLocalVariables();
        final ArrayList<String> declarations = new ArrayList<>(variables.size());

        for (final Variable argument : variables) {
            declarations.add("var " + argument.name + " : " + argument.translatedType + ";");
        }

        return Joiner.on("\n").join(declarations);
    }

    private Set<Variable> getLocalVariables() {

        final HashSet<Variable> variables = new HashSet<>();

        if (!isThisVariableAnArgument()) {
            variables.add(getThisVariable());
        }

        variables.add(getVariableForMethodResult(invariant).get());

        final Optional<Variable> transitionResultVariable = getVariableForMethodResult(transition.method);
        if (transitionResultVariable.isPresent()) {
            variables.add(transitionResultVariable.get());
        }

        for (final Action action : source.getAllActions()) {
            variables.add(getVariableForMethodResult(action.precondition).get());
        }

        for (final Action action : target.getAllActions()) {
            variables.add(getVariableForMethodResult(action.precondition).get());
        }

        return variables;
    }

    private Optional<Variable> getVariableForMethodResult(Method method) {

        if (!method.hasReturnType()) {
            return Optional.absent();
        }

        return Optional.of(new Variable(method.getTranslatedReturnType(), "ret_" + method.getTranslatedName()));
    }

    private List<Variable> getArgumentListForMethod(Method method, String nameSuffix) {

        final List<String> translatedArgumentTypes = method.getTranslatedArgumentTypes();

        final List<Variable> arguments = new ArrayList<>(translatedArgumentTypes.size());

        if (!method.isStatic()) {
            translatedArgumentTypes.remove(0);
            arguments.add(getThisVariable());
        }

        for (int i = 0; i < translatedArgumentTypes.size(); i++) {
            final Variable variable = new Variable(
                    translatedArgumentTypes.get(i),
                    method.getTranslatedName() + "$arg" + i + nameSuffix
            );
            arguments.add(variable);
        }

        return arguments;
    }

    private String getCall(Method method, List<Variable> arguments) {

        final Optional<Variable> returnVariable = getVariableForMethodResult(method);

        final StringBuilder stringBuilder = new StringBuilder();

        if (method.isConstructor()) {
            stringBuilder.append("call $this := Alloc();\n");
        }

        stringBuilder.append("call ");

        if (returnVariable.isPresent()) {
            stringBuilder.append(returnVariable.get().name)
                    .append(" := ");
        }

        stringBuilder.append(method.getTranslatedName())
                .append("(")
                .append(Joiner.on(", ").join(getNames(arguments)))
                .append(");\n")
                .append("if ($Exception != null) { goto ").append(UNHANDLED_EXCEPTION_LABEL).append(" ; }");

        return stringBuilder.toString();
    }

    private String getCall(Action action, String argumentNamesSuffix, boolean callPre) {

        final List<Variable> arguments = getArgumentListForMethod(action.method, argumentNamesSuffix);

        if (callPre) {

            if (!action.method.isStatic() && action.precondition.isStatic()) {
                arguments.remove(0);
            }

            return getCall(action.precondition, arguments);
        }

        return getCall(action.method, arguments);
    }

    private List<Variable> getQueryArguments() {

        final List<Variable> arguments = new ArrayList<>();

        if (transition.method.isConstructor()) {
            int i = 0;
        }

        if (isThisVariableAnArgument()) {
            arguments.add(getThisVariable());
        }

        for (final Action action : source.getAllActions()) {
            final List<Variable> actionArguments = getArgumentListForMethod(action.method, "");

            if (!action.method.isStatic()) {
                actionArguments.remove(0);
            }

            arguments.addAll(actionArguments);
        }

        for (final Action action : target.getAllActions()) {
            final List<Variable> actionArguments = getArgumentListForMethod(
                    action.method,
                    TARGET_ARGS_SUFFIX
            );

            if (!action.method.isStatic()) {
                actionArguments.remove(0);
            }

            arguments.addAll(actionArguments);
        }

        return arguments;
    }

    private Variable getThisVariable() {

        return new Variable("Ref", "$this");
    }

    private boolean isThisVariableAnArgument() {

        return !transition.method.isConstructor();
    }

    private String getQueryArgumentsDeclaration() {

        final List<Variable> queryArguments = getQueryArguments();
        final ArrayList<String> declarations = new ArrayList<>(queryArguments.size());

        for (final Variable argument : queryArguments) {
            declarations.add(argument.name + " : " + argument.translatedType);
        }

        return Joiner.on(", ").join(declarations);
    }

    private List<String> getNames(List<Variable> variables) {

        final ArrayList<String> names = new ArrayList<>(variables.size());
        for (final Variable variable : variables) {
            names.add(variable.name);
        }

        return names;
    }

    private String getStateGuardCalls(State state, String argumentNamesSuffix) {

        final List<String> calls = new ArrayList<>();

        if (!state.isConstructorsState()) {
            calls.add(getInvariantCall());
        }

        for (final Action action : state.getAllActions()) {
            calls.add(getCall(action, argumentNamesSuffix, true));
        }

        return Joiner.on("\n\n").join(calls);
    }

    private String getInvariantCall() {

        final ArrayList<Variable> arguments = new ArrayList<>();
        arguments.add(getThisVariable());
        return getCall(invariant, arguments);
    }

    private String getStateGuard(final State state) {

        final List<String> atoms = new ArrayList<>();

        for (final Action action : state.enabledActions) {
            atoms.add(getVariableForMethodResult(action.precondition).get().name);
        }

        for (final Action action : state.disabledActions) {
            atoms.add("!" + getVariableForMethodResult(action.precondition).get().name);
        }

        return Joiner.on(" && ").join(atoms);
    }

    private static class Variable {

        public final String translatedType;

        public final String name;

        private Variable(final String translatedType, final String name) {

            this.translatedType = translatedType;
            this.name = name;
        }

        @Override
        public boolean equals(final Object o) {

            if (this == o) {
                return true;
            }
            if (!(o instanceof Variable)) {
                return false;
            }
            final Variable variable = (Variable) o;
            return Objects.equals(translatedType, variable.translatedType) &&
                    Objects.equals(name, variable.name);
        }

        @Override
        public int hashCode() {

            return Objects.hash(translatedType, name);
        }
    }
}
