package contractorj.construction;

import com.google.common.base.Joiner;
import contractorj.model.Action;
import contractorj.model.State;
import j2bpl.Method;
import j2bpl.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Query {

    public enum AssertionLabels {
        QUERY,
        INVARIANT
    }

    public enum Type {
        TRANSITION_QUERY,
        NECESSARY_ENABLED_QUERY,
        NECESSARY_DISABLED_QUERY
    }

    public enum TransitionThrows {
        THROWS,
        DOES_NOT_THROW,
        EXCEPTION_IGNORED
    }

    private static final String TARGET_ARGS_SUFFIX = "After";

    private final State source;

    private final Action transition;

    private final State target;

    private final Method invariant;

    public final Type type;

    private final TransitionThrows transitionThrows;

    private final boolean assumeInvariant;

    public Query(Type type,
                 State source,
                 Action transition,
                 State target,
                 Method invariant,
                 TransitionThrows transitionThrows,
                 final boolean assumeInvariant) {

        this.source = source;
        this.transition = transition;
        this.target = target;
        this.invariant = invariant;
        this.type = type;
        this.transitionThrows = transitionThrows;
        this.assumeInvariant = assumeInvariant;

        if (!source.enabledActions.contains(transition)) {
            throw new IllegalArgumentException("Invalid query: transition must be in source's enabled actions.");
        }
    }

    public String getName() {

        final String partsSeparator = "__________";

        final StringBuilder name = new StringBuilder()
                .append(type.toString())

                .append(partsSeparator)
                .append("from")
                .append(partsSeparator)

                .append(getStateName(source));

        if (type.equals(Type.TRANSITION_QUERY)) {

            name.append(partsSeparator)
                    .append("to")
                    .append(partsSeparator)

                    .append(getStateName(target));
        } else {

            name.append(partsSeparator)
                    .append("checking")
                    .append(partsSeparator);

            final Action checkingAction;

            if (type.equals(Type.NECESSARY_ENABLED_QUERY)) {
                checkingAction = target.enabledActions.iterator().next();
            } else {
                checkingAction = target.disabledActions.iterator().next();

                name.append("NOT_");
            }

            name.append(checkingAction.method.getBaseJavaName());
        }

        name.append(partsSeparator)
                .append("via")
                .append(partsSeparator)

                .append(transition.method.getJavaNameWithArgumentTypes())

                .append(partsSeparator)
                .append(transitionThrows.toString());

        return StringUtils.scapeIllegalIdentifierCharacters(name.toString());
    }

    private String getStateName(State state) {

        final String joiner = "____";

        if (!state.enabledActions.isEmpty()) {
            return getJoinedActionNames(state.enabledActions, joiner);
        }

        return "EMPTY_STATE";
    }

    private String getJoinedActionNames(Set<Action> actions, String joiner) {

        final List<String> names = actions.stream()
                .map(action -> action.method.getJavaNameWithArgumentTypes())
                .sorted()
                .collect(Collectors.toList());

        return Joiner.on(joiner).join(names);
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

                .append(AssertionLabels.INVARIANT).append(":\n");

        if (assumeInvariant) {
            queryBody.append(StringUtils.indent("assume "));
        } else {
            queryBody.append(StringUtils.indent("assert "));
        }

        queryBody.append(invariantResultVariableName).append(";\n")
                .append("\n")
                .append("\n");

        // This is an optimization. If we are throwing an exception and not assuming the invariant, we only care about
        // the invariant's assert, so we omit this one.
        // TODO: No, this is not an optimization, we use it in LazyEpaGenerator's
        //      {@code throwingNotPreservingInvariantResult.equals(Result.MAYBE_BUG)}
        if (assumeInvariant || !transitionThrows.equals(TransitionThrows.THROWS)) {
            queryBody.append(getQueryAssertion()).append("\n");
        }

        final StringBuilder query = new StringBuilder()
                .append("procedure ").append(getName()).append("(").append(getQueryArgumentsDeclaration())
                .append(") {\n")
                .append("\n")
                .append(StringUtils.indent(queryBody.toString())).append("\n")
                .append("}");

        return query.toString();
    }

    private String getQueryAssertion() {

        final StringBuilder assertion = new StringBuilder();

        assertion.append(AssertionLabels.QUERY).append(":\n")
                .append(StringUtils.indent("assert "));

        if (type.equals(Type.TRANSITION_QUERY)) {
            assertion.append("!(");
        }

        assertion.append(getStateGuard(target));

        if (type.equals(Type.TRANSITION_QUERY)) {
            assertion.append(")");
        }

        assertion.append(";");

        return assertion.toString();
    }

    private String getLocalVariablesDeclaration() {

        final List<String> declarations = getLocalVariables().stream()
                .map(argument -> "var " + argument.name + " : " + argument.translatedType + ";")
                .collect(Collectors.toList());

        return Joiner.on("\n").join(declarations);
    }

    private List<Variable> getLocalVariables() {

        final List<Variable> variables = new ArrayList<>();

        if (!isThisVariableAnArgument()) {
            variables.add(getThisVariable());
        }

        variables.add(getVariableForMethodResult(invariant).get());

        final Optional<Variable> transitionResultVariable = getVariableForMethodResult(transition.method);
        if (transitionResultVariable.isPresent()) {
            variables.add(transitionResultVariable.get());
        }

        Stream.concat(source.getAllActions().stream(), target.getAllActions().stream())
                .map(action -> getVariableForMethodResult(action.precondition))
                .map(Optional::get)
                .distinct()
                .forEach(variables::add);

        return variables;
    }

    public TransitionThrows getTransitionThrows() {

        return transitionThrows;
    }

    private Optional<Variable> getVariableForMethodResult(Method method) {

        if (!method.hasReturnType()) {
            return Optional.empty();
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
                .append(");\n");

        if (method.equals(transition.method)) {

            switch (transitionThrows) {
                case DOES_NOT_THROW:
                    stringBuilder.append("assume $Exception == null;");
                    break;

                case THROWS:
                    stringBuilder.append("assume $Exception != null;\n")
                            .append("$Exception := null;");
                    break;

                case EXCEPTION_IGNORED:
                    stringBuilder.append("$Exception := null;");
                    break;
            }

        } else {
            stringBuilder.append("assume $Exception == null;");
        }

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

    private String getStateGuardCalls(State state, String argumentNamesSuffix) {

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

    private String getStateGuard(final State state) {

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
