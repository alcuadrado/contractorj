package contractorj.construction.queries;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import contractorj.construction.corral.QueryResult;
import contractorj.model.Action;
import contractorj.model.State;
import contractorj.model.Transition;
import j2bpl.Method;
import j2bpl.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Query {

  protected static final String NAME_PART_SEPARATOR = "_________";

  protected final State source;

  protected final Action mainAction;

  protected final Method invariant;

  protected Query(State source, Action mainAction, Method invariant) {

    this.source = source;
    this.mainAction = mainAction;
    this.invariant = invariant;

    if (!source.getEnabledActions().contains(mainAction)) {
      throw new IllegalArgumentException(
          "Invalid query: mainAction must be in source's enabled actions.");
    }
  }

  public abstract Answer getAnswer(final QueryResult queryResult);

  protected abstract String getQueryCore();

  public abstract Optional<Transition> getTransition(Answer answer);

  protected abstract String getMainActionCallExceptionHandling();

  public State getSource() {

    return source;
  }

  public Action getMainAction() {
    return mainAction;
  }

  public String getName() {

    final String name =
        "from"
            + NAME_PART_SEPARATOR
            + getStateName(source)
            + NAME_PART_SEPARATOR
            + "via"
            + NAME_PART_SEPARATOR
            + mainAction.getMethod().getJavaNameWithArgumentTypes();

    return StringUtils.scapeIllegalIdentifierCharacters(name);
  }

  protected String getStateName(State state) {

    final String joiner = "____";

    return state
        .getEnabledActions()
        .stream()
        .map(action -> action.getMethod().getJavaNameWithArgumentTypes())
        .reduce((s1, s2) -> s1 + joiner + s2)
        .orElse("EMPTY");
  }

  public String getBoogieCode() {

    final StringBuilder queryBody = new StringBuilder();

    queryBody
        .append(getLocalVariablesDeclaration())
        .append("\n")
        .append("\n")
        .append("\n")
        .append("call initialize_globals();\n")
        .append("\n")
        .append("\n");

    if (!source.isConstructorsState()) {
      queryBody.append(getInvariantCall()).append("\n").append("\n");
    }

    queryBody.append(getStateGuardCalls(source)).append("\n").append("\n").append("\n");

    if (!source.isConstructorsState()) {
      queryBody.append(getInvariantAssumption()).append("\n");
    }

    queryBody.append(getStateGuardAssumption(source)).append("\n").append("\n").append("\n");

    getMainActionParamsPreconditionCall()
        .ifPresent(
            call ->
                queryBody
                    .append(call)
                    .append("\n")
                    .append(getMainActionParamsPreconditionAssumption())
                    .append("\n")
                    .append("\n")
                    .append("\n"));

    queryBody
        .append(getMainActionCall())
        .append("\n")
        .append("\n")
        .append("\n")
        .append(getInvariantCall())
        .append("\n")
        .append("\n")
        .append(getQueryCore())
        .append("\n");

    final StringBuilder query =
        new StringBuilder()
            .append("procedure ")
            .append(getName())
            .append("(")
            .append(getQueryArgumentsDeclaration())
            .append(") {\n")
            .append("\n")
            .append(StringUtils.indent(queryBody.toString()))
            .append("\n")
            .append("}");

    return query.toString();
  }

  private Variable getInvariantReturnVariable() {

    return getVariableForMethodResult(invariant).get();
  }

  private String getLocalVariablesDeclaration() {

    final List<String> declarations =
        getLocalVariables()
            .stream()
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

    getVariableForMethodResult(mainAction).ifPresent(variables::add);
    getVariableForParamsPreconditionResult(mainAction).ifPresent(variables::add);

    getStateGuardVariables(source).distinct().forEach(variables::add);

    return variables;
  }

  protected Stream<Variable> getStateGuardVariables(State state) {

    return state
        .getAllActions()
        .stream()
        .map(this::getVariableForStatePreconditionResult)
        .filter(Optional::isPresent)
        .map(Optional::get);
  }

  private Optional<Variable> getVariableForMethodResult(Action action) {

    return getVariableForMethodResult(action.getMethod());
  }

  protected Optional<Variable> getVariableForStatePreconditionResult(Action action) {

    final Optional<Method> statePrecondition = action.getStatePrecondition();

    if (!statePrecondition.isPresent()) {
      return Optional.empty();
    }

    return getVariableForMethodResult(statePrecondition.get(), "state_pre_");
  }

  private Optional<Variable> getVariableForParamsPreconditionResult(Action action) {

    final Optional<Method> paramsPrecondition = action.getParamsPrecondition();

    if (!paramsPrecondition.isPresent()) {
      return Optional.empty();
    }

    return getVariableForMethodResult(paramsPrecondition.get(), "params_pre_");
  }

  private Optional<Variable> getVariableForMethodResult(Method method) {

    return getVariableForMethodResult(method, "");
  }

  private Optional<Variable> getVariableForMethodResult(Method method, String prefix) {

    if (!method.hasReturnType()) {
      return Optional.empty();
    }

    final Variable variable =
        new Variable(
            method.getTranslatedReturnType(), prefix + "ret_" + method.getTranslatedName());

    return Optional.of(variable);
  }

  private List<Variable> getMainActionMethodArguments() {

    final Method method = mainAction.getMethod();

    final List<String> translatedArgumentTypes = method.getTranslatedArgumentTypes();
    translatedArgumentTypes.remove(0); // Remove this' type

    final List<Variable> arguments = new ArrayList<>(translatedArgumentTypes.size());
    arguments.add(getThisVariable());

    for (int i = 0; i < translatedArgumentTypes.size(); i++) {

      final Variable variable =
          new Variable(translatedArgumentTypes.get(i), method.getTranslatedName() + "$arg" + i);

      arguments.add(variable);
    }

    return arguments;
  }

  private List<Variable> getMainActionParamsPreconditionArguments() {

    final List<Variable> mainActionMethodArguments = getMainActionMethodArguments();

    if (mainAction.getMethod().isConstructor()) {
      mainActionMethodArguments.remove(0);
    }

    return mainActionMethodArguments;
  }

  protected Optional<String> getStatePreconditionCall(Action action) {

    return action
        .getStatePrecondition()
        .map(
            method ->
                getCall(
                    method,
                    method.isStatic()
                        ? Lists.newArrayList()
                        : Lists.newArrayList(getThisVariable()),
                    getVariableForStatePreconditionResult(action)));
  }

  private String getMainActionCall() {

    final Method method = mainAction.getMethod();

    return getCall(method, getMainActionMethodArguments(), getVariableForMethodResult(mainAction));
  }

  private Optional<String> getMainActionParamsPreconditionCall() {

    return mainAction
        .getParamsPrecondition()
        .map(
            method ->
                getCall(
                    method,
                    getMainActionParamsPreconditionArguments(),
                    getVariableForParamsPreconditionResult(mainAction)));
  }

  private String getInvariantCall() {

    return getCall(
        invariant,
        Lists.newArrayList(getThisVariable()),
        Optional.of(getInvariantReturnVariable()));
  }

  private String getCall(
      Method method, List<Variable> arguments, final Optional<Variable> returnVariable) {

    final StringBuilder stringBuilder = new StringBuilder();

    if (method.isConstructor()) {
      stringBuilder.append("call $this := Alloc();\n");
    }

    stringBuilder.append("call ");

    if (returnVariable.isPresent()) {
      stringBuilder.append(returnVariable.get().name).append(" := ");
    }

    stringBuilder
        .append(method.getTranslatedName())
        .append("(")
        .append(Joiner.on(", ").join(getNames(arguments)))
        .append(");\n");

    if (method.equals(mainAction.getMethod())) {

      stringBuilder.append(getMainActionCallExceptionHandling());

    } else {
      stringBuilder.append("assume $Exception == null;");
    }

    return stringBuilder.toString();
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

  private String getStateGuardAssumption(State state) {

    return "assume (" + getStateGuard(state) + ");";
  }

  private String getMainActionParamsPreconditionAssumption() {
    return "assume " + getVariableForParamsPreconditionResult(mainAction).get().name + ";";
  }

  private List<Variable> getQueryArguments() {

    final List<Variable> arguments = getMainActionMethodArguments();

    if (!isThisVariableAnArgument()) {
      arguments.remove(0);
    }

    return arguments;
  }

  private Variable getThisVariable() {

    return new Variable("Ref", "$this");
  }

  private boolean isThisVariableAnArgument() {

    return !mainAction.getMethod().isConstructor();
  }

  private String getQueryArgumentsDeclaration() {

    final List<String> declarations =
        getQueryArguments()
            .stream()
            .map(argument -> argument.name + " : " + argument.translatedType)
            .collect(Collectors.toList());

    return Joiner.on(", ").join(declarations);
  }

  private List<String> getNames(List<Variable> variables) {

    return variables.stream().map(variable -> variable.name).collect(Collectors.toList());
  }

  protected String getStateGuardCalls(State state) {

    return state
        .getAllActions()
        .stream()
        .map(this::getStatePreconditionCall)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .reduce((s1, s2) -> s1 + "\n\n" + s2)
        .orElse("");
  }

  private String getStateGuard(final State state) {

    final List<String> atoms = new ArrayList<>();

    state
        .getEnabledActions()
        .stream()
        .map(this::getVariableForStatePreconditionResult)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(variable -> variable.name)
        .forEach(atoms::add);

    state
        .getDisabledActions()
        .stream()
        .map(this::getVariableForStatePreconditionResult)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(variable -> "!" + variable.name)
        .forEach(atoms::add);

    return atoms.stream().reduce((s1, s2) -> s1 + " && " + s2).orElse("true");
  }
}
