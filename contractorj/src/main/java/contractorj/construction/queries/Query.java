package contractorj.construction.queries;

import com.google.common.base.Joiner;
import contractorj.construction.corral.QueryResult;
import contractorj.model.Action;
import contractorj.model.State;
import j2bpl.Method;
import j2bpl.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class Query {

    public enum AssertionLabels {
        QUERY,
        INVARIANT
    }

    protected static final String AFTER_TRANSITION_ARGS_SUFFIX = "After";

    protected static final String NAME_PART_SEPARATOR = "_________";

    protected final State source;

    protected final Action transition;

    protected final Method invariant;

    protected Query(State source,
                    Action transition,
                    Method invariant) {

        this.source = source;
        this.transition = transition;
        this.invariant = invariant;

        if (!source.enabledActions.contains(transition)) {
            throw new IllegalArgumentException("Invalid query: transition must be in source's enabled actions.");
        }
    }

    public abstract Answer getAnswer(final QueryResult queryResult);

    protected abstract String getQueryCore();

    public State getSource() {

        return source;
    }

    public Action getTransition() {

        return transition;
    }

    public String getName() {

        final String name = "from" + NAME_PART_SEPARATOR + getStateName(source) + NAME_PART_SEPARATOR
                + "via" + NAME_PART_SEPARATOR + transition.method.getJavaNameWithArgumentTypes();

        return StringUtils.scapeIllegalIdentifierCharacters(name);
    }

    protected String getStateName(State state) {

        final String joiner = "____";

        return state.enabledActions.stream()
                .map(action -> action.method.getJavaNameWithArgumentTypes())
                .reduce((s1, s2) -> s1 + joiner + s2)
                .orElse("ERROR");
    }

    public String getBoogieCode() {

        final StringBuilder queryBody = new StringBuilder();

        queryBody
                .append(getLocalVariablesDeclaration()).append("\n")
                .append("\n")
                .append("\n")

                .append("call initialize_globals();\n")
                .append("\n")
                .append("\n");

        if (!source.isConstructorsState()) {
            queryBody
                    .append(getInvariantCall()).append("\n")
                    .append("\n");
        }

        queryBody
                .append(getStateGuardCalls(source, "")).append("\n")
                .append("\n")
                .append("\n");

        if (!source.isConstructorsState()) {
            queryBody.append(getInvariantAssumption()).append(";\n");
        }

        queryBody
                .append(getStateGuardAssumption(source)).append("\n")
                .append("\n")
                .append("\n")

                .append(getCall(transition, "", false)).append("\n")
                .append("\n")
                .append("\n")

                .append(getInvariantCall()).append("\n")
                .append("\n")

                .append(getQueryCore()).append("\n")
                .append("\n")
                .append("\n");

        final StringBuilder query = new StringBuilder()
                .append("procedure ").append(getName()).append("(").append(getQueryArgumentsDeclaration())
                .append(") {\n")
                .append("\n")
                .append(StringUtils.indent(queryBody.toString())).append("\n")
                .append("}");

        return query.toString();
    }

    protected Variable getInvariantReturnVariable() {

        return getVariableForMethodResult(invariant).get();
    }

    private String getLocalVariablesDeclaration() {

        final List<String> declarations = getLocalVariables().stream()
                .map(argument -> "var " + argument.name + " : " + argument.translatedType + ";")
                .collect(Collectors.toList());

        return Joiner.on("\n").join(declarations);
    }

    protected List<Variable> getLocalVariables() {

        final List<Variable> variables = new ArrayList<>();

        if (!isThisVariableAnArgument()) {
            variables.add(getThisVariable());
        }

        variables.add(getVariableForMethodResult(invariant).get());

        final Optional<Variable> transitionResultVariable = getVariableForMethodResult(transition.method);
        if (transitionResultVariable.isPresent()) {
            variables.add(transitionResultVariable.get());
        }

        source.getAllActions().stream()
                .map(action -> getVariableForMethodResult(action.precondition))
                .map(Optional::get)
                .distinct()
                .forEach(variables::add);

        return variables;
    }

    protected Optional<Variable> getVariableForMethodResult(Method method) {

        if (!method.hasReturnType()) {
            return Optional.empty();
        }

        return Optional.of(new Variable(method.getTranslatedReturnType(), "ret_" + method.getTranslatedName()));
    }

    protected List<Variable> getArgumentListForMethod(Method method, String nameSuffix) {

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
                .append(");\n");

        if (method.equals(transition.method)) {

            stringBuilder.append(getTransitionCallExceptionHandling());

        } else {
            stringBuilder.append("assume $Exception == null;");
        }

        return stringBuilder.toString();
    }

    protected abstract String getTransitionCallExceptionHandling();

    protected String getCall(Action action, String argumentNamesSuffix, boolean callPre) {

        final List<Variable> arguments = getArgumentListForMethod(action.method, argumentNamesSuffix);

        if (callPre) {

            if (!action.method.isStatic() && action.precondition.isStatic()) {
                arguments.remove(0);
            }

            return getCall(action.precondition, arguments);
        }

        return getCall(action.method, arguments);
    }

    protected String getInvariantAssertion() {

        return "assert " + getInvariantReturnVariable().name + ";";
    }

    protected String getInvariantAssumption() {

        return "assume " + getInvariantReturnVariable().name + ";";
    }

    protected String getNegatedStateGuardAssertion(State state) {
        return "assert !(" + getStateGuard(state) + ");";
    }

    protected String getStateGuardAssumption(State state) {
        return "assume (" + getStateGuard(state) + ");";
    }

    protected List<Variable> getQueryArguments() {

        final List<Variable> arguments = new ArrayList<>();

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

        return arguments;
    }

    private Variable getThisVariable() {

        return new Variable("Ref", "$this");
    }

    private boolean isThisVariableAnArgument() {

        return !transition.method.isConstructor();
    }

    private String getQueryArgumentsDeclaration() {

        final List<String> declarations = getQueryArguments().stream()
                .map(argument -> argument.name + " : " + argument.translatedType)
                .collect(Collectors.toList());

        return Joiner.on(", ").join(declarations);
    }

    private List<String> getNames(List<Variable> variables) {

        return variables.stream()
                .map(variable -> variable.name)
                .collect(Collectors.toList());
    }

    //TODO: sacar invariant de acá
    protected String getStateGuardCalls(State state, String argumentNamesSuffix) {

        final List<String> calls = new ArrayList<>();

        if (!state.isConstructorsState()) {
            calls.add(getInvariantCall());
        }

        state.getAllActions().forEach(action -> calls.add(getCall(action, argumentNamesSuffix, true)));

        return Joiner.on("\n\n").join(calls);
    }

    private String getInvariantCall() {

        final ArrayList<Variable> arguments = new ArrayList<>();
        arguments.add(getThisVariable());
        return getCall(invariant, arguments);
    }

    protected String getStateGuard(final State state) {

        final List<String> atoms = new ArrayList<>();

        state.enabledActions.stream()
                .map(action -> getVariableForMethodResult(action.precondition))
                .map(Optional::get)
                .map(variable -> variable.name)
                .forEach(atoms::add);

        state.disabledActions.stream()
                .map(action -> getVariableForMethodResult(action.precondition))
                .map(Optional::get)
                .map(variable -> "!" + variable.name)
                .forEach(atoms::add);

        return Joiner.on(" && ").join(atoms);
    }

}
