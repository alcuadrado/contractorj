package contractorj.construction;

import contractorj.model.Action;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import jbct.model.Class;
import jbct.model.Method;

public class ActionsExtractor {

  private static final String INVARIANT_METHOD_NAME = "inv()";

  private final Class theClass;

  private final Set<String> methodNames;

  private final Map<String, Method> methodsMap = new HashMap<>();

  private final Set<Action> instanceActions = new HashSet<>();

  private final Set<Action> constructorActions = new HashSet<>();

  private Method invariant;

  public Set<Action> getInstanceActions() {

    return instanceActions;
  }

  public Set<Action> getConstructorActions() {

    return constructorActions;
  }

  public Method getInvariant() {

    return invariant;
  }

  public ActionsExtractor(Class theClass, Set<String> methodNames) {

    this.theClass = theClass;
    this.methodNames = recomputeMethodNames(methodNames);

    computeMethodsMap();
    searchInvariant();
    generateActions();
  }

  /** Some method names may not have its params, but if it's not overloaded that's ok */
  private Set<String> recomputeMethodNames(Set<String> methodNames) {

    return methodNames
        .stream()
        .map(
            name -> {
              if (name.contains("(")) {
                return name;
              }

              final List<Method> methods =
                  theClass
                      .getMethods()
                      .stream()
                      .filter(method -> method.getBaseJavaName().equals(name))
                      .collect(Collectors.toList());

              if (methods.size() != 1) {
                throw new RuntimeException(
                    "If a method name is provided without parms " + "it can't be overloaded.");
              }

              return methods.get(0).getJavaNameWithArgumentTypes();
            })
        .collect(Collectors.toSet());
  }

  private void generateActions() {

    final HashSet<String> ignoredMethods = new HashSet<>();
    final HashSet<String> missingMethods = new HashSet<>(methodNames);

    for (String methodName : methodsMap.keySet()) {

      if (methodName.equals(INVARIANT_METHOD_NAME)) {
        continue;
      }

      if (isPreconditionName(methodName)) {
        continue;
      }

      final Method method = methodsMap.get(methodName);

      if (method.isStatic()) {
        continue;
      }

      final String statePreconditionMethodName = getStatePreconditionMethodName(method);
      final Method statePrecondition = methodsMap.getOrDefault(statePreconditionMethodName, null);

      final Method paramsPrecondition;

      if (hasNonThisParameters(method)) {

        final String paramsPreconditionMethodName = getParamsPreconditionMethodName(method);
        paramsPrecondition = methodsMap.getOrDefault(paramsPreconditionMethodName, null);
      } else {
        paramsPrecondition = null;
      }

      validatePreconditions(method, statePrecondition, paramsPrecondition);

      final Action action = new Action(method, statePrecondition, paramsPrecondition);

      if (!methodNames.isEmpty() && !methodNames.contains(methodName)) {
        ignoredMethods.add(methodName);
        continue;
      }

      missingMethods.remove(methodName);

      if (method.isConstructor()) {
        constructorActions.add(action);
        continue;
      }

      instanceActions.add(action);
    }

    if (missingMethods.size() > 0) {
      throw new RuntimeException(
          "Not all methods extracted.\n"
              + "Missing methods: "
              + Arrays.toString(missingMethods.toArray())
              + "\n"
              + "Ignored methods: "
              + Arrays.toString(ignoredMethods.toArray()));
    }
  }

  private boolean isPreconditionName(final String methodName) {

    return methodName.contains("_pre(");
  }

  private void validatePreconditions(
      final Method method, final Method statePrecondition, final Method paramsPrecondition) {

    if (statePrecondition != null) {

      final String statePreconditionName = statePrecondition.getJavaNameWithArgumentTypes();

      if (statePrecondition.getParameterTypes().size() > 0) {
        throw new IllegalArgumentException(
            "State precondition " + statePreconditionName + " must have no argument.");
      }

      if (!statePrecondition.hasReturnType()
          || !statePrecondition.getTranslatedReturnType().equals("bool")) {
        throw new IllegalArgumentException(
            "Precondition " + statePreconditionName + " must return a boolean");
      }
    }

    if (paramsPrecondition != null) {

      final String paramsPreconditionName = paramsPrecondition.getJavaNameWithArgumentTypes();

      if (!paramsPrecondition.hasReturnType()
          || !paramsPrecondition.getTranslatedReturnType().equals("bool")) {
        throw new IllegalArgumentException(
            "Precondition " + paramsPreconditionName + " must return a boolean");
      }

      if (!method.getParameterTypes().equals(paramsPrecondition.getParameterTypes())) {
        throw new IllegalArgumentException(
            "Parameters precondition "
                + paramsPreconditionName
                + " must have the same arguments as its method.");
      }
    }
  }

  private boolean hasNonThisParameters(final Method method) {

    return method.getParameterTypes().size() > 0;
  }

  private String getStatePreconditionMethodName(final Method method) {

    return method.getJavaNameWithArgumentTypes().replaceAll("\\(.*\\)", "_pre()");
  }

  private String getParamsPreconditionMethodName(final Method method) {

    return method.getJavaNameWithArgumentTypes().replace("(", "_pre(");
  }

  private void searchInvariant() {

    if (!methodsMap.containsKey(INVARIANT_METHOD_NAME)) {
      throw new UnsupportedOperationException(
          "Invariant method name missing. It must be named: " + INVARIANT_METHOD_NAME);
    }

    invariant = methodsMap.get(INVARIANT_METHOD_NAME);

    if (!invariant.getTranslatedReturnType().equals("bool")) {
      throw new IllegalArgumentException("Invariant method must return a boolean");
    }

    if (invariant.getTranslatedArgumentTypes().size() != 1) {
      throw new IllegalArgumentException("Invariant method must have 0 arity");
    }
  }

  private void computeMethodsMap() {

    for (Method method : theClass.getMethods()) {

      final String methodName = method.getJavaNameWithArgumentTypes();

      methodsMap.put(methodName, method);
    }
  }
}
