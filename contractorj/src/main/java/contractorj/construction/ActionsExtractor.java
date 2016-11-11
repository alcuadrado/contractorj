package contractorj.construction;

import contractorj.model.Action;
import j2bpl.Class;
import j2bpl.Method;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Extracts all the instanceActions of a class and its invariant.
 */
public class ActionsExtractor {

    private final static String INVARIANT_METHOD_NAME = "inv()";

    private final Class theClass;

    private final Map<String, Set<Method>> methodsMap;

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

    public ActionsExtractor(Class theClass) {

        this.theClass = theClass;

        methodsMap = getMethodsMap();

        searchInvariant();
        generateActions();
    }

    private void generateActions() {

        for (String methodName : methodsMap.keySet()) {

            final String preconditionMethodName = getPreconditionMethodName(methodName);

            if (!methodsMap.containsKey(preconditionMethodName)) {
                continue;
            }

            final Set<Method> methods = methodsMap.get(methodName);
            final Set<Method> preconditions = methodsMap.get(preconditionMethodName);

            if (methods.size() != 1 || preconditions.size() != 1) {
                throw new IllegalStateException("More than one method or precondition with same name and arguments");
            }

            final Method method = methods.iterator().next();
            final Method precondition = preconditions.iterator().next();

            if (!precondition.hasReturnType() || !precondition.getTranslatedReturnType().equals("bool")) {
                throw new IllegalArgumentException("Precondition " + preconditionMethodName + " must return a boolean");
            }

            if (!method.getParameterTypes().equals(precondition.getParameterTypes())) {
                throw new IllegalArgumentException("Precondition " + preconditionMethodName + " must have the same " +
                        "arguments as its method.");
            }

            final Action action = new Action(precondition, method);

            if (method.isConstructor()) {
                constructorActions.add(action);
            } else {
                instanceActions.add(action);
            }
        }
    }

    private void searchInvariant() {

        if (!methodsMap.containsKey(INVARIANT_METHOD_NAME)) {
            throw new UnsupportedOperationException("Invariant method name missing. It must be named: "
                    + INVARIANT_METHOD_NAME);
        }

        if (methodsMap.get(INVARIANT_METHOD_NAME).size() != 1) {
            throw new UnsupportedOperationException("Exactly one invariant method is needed");
        }

        invariant = methodsMap.get(INVARIANT_METHOD_NAME).iterator().next();

        if (!invariant.getTranslatedReturnType().equals("bool")) {
            throw new IllegalArgumentException("Invariant method must return a boolean");
        }

        if (invariant.getTranslatedArgumentTypes().size() != 1) {
            throw new IllegalArgumentException("Invariant method must have 0 arity");
        }
    }

    private String getPreconditionMethodName(String methodName) {

        return methodName.replace("(", "_pre(");
    }

    private Map<String, Set<Method>> getMethodsMap() {

        final Map<String, Set<Method>> instanceMethodsMap = new HashMap<>();

        for (Method method : theClass.getMethods()) {

            final String methodName = method.getJavaNameWithArgumentTypes();

            if (!instanceMethodsMap.containsKey(methodName)) {
                instanceMethodsMap.put(methodName, new HashSet<>());
            }

            instanceMethodsMap.get(methodName).add(method);
        }

        return instanceMethodsMap;
    }

}
