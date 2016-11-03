package j2bpl.epas.gen;

import j2bpl.epas.Contract;
import j2bpl.translation.Class;
import j2bpl.translation.Method;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ContractsExtractor {

    private final static String INVARIANT_METHOD_NAME = "inv";

    private final Class theClass;

    private final Set<Contract> contracts = new HashSet<>();

    private Method invariant;

    public Set<Contract> getContracts() {

        return contracts;
    }

    public Method getInvariant() {

        return invariant;
    }

    public ContractsExtractor(Class theClass) {

        this.theClass = theClass;

        final Map<String, Set<Method>> instanceMethodsMap = getInstanceMethodsMap();

        searchInvariant(instanceMethodsMap);
        generateContacts(instanceMethodsMap);
    }

    private void generateContacts(Map<String, Set<Method>> instanceMethodsMap) {

        for (String methodName : instanceMethodsMap.keySet()) {

            final String preconditionMethodName = getPreconditionMethodName(methodName);

            if (!instanceMethodsMap.containsKey(preconditionMethodName)) {
                continue;
            }

            final Set<Method> methods = instanceMethodsMap.get(methodName);
            final Set<Method> preconditions = instanceMethodsMap.get(preconditionMethodName);

            if (methods.size() != 1 || preconditions.size() != 1) {
                throw new UnsupportedOperationException("Overload is not supported in EPA methods.");
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

            final Contract contract = new Contract(precondition, method);
            contracts.add(contract);
        }
    }

    private void searchInvariant(Map<String, Set<Method>> instanceMethodsMap) {

        final String qualifiedInvariantMethodName = theClass.getQualifiedJavaName() + "#" + INVARIANT_METHOD_NAME;

        if (!instanceMethodsMap.containsKey(qualifiedInvariantMethodName)) {
            throw new UnsupportedOperationException("Invariant method name missing. It must be named: "
                    + qualifiedInvariantMethodName);
        }

        if (instanceMethodsMap.get(qualifiedInvariantMethodName).size() != 1) {
            throw new UnsupportedOperationException("Exactly one invariant method is needed");
        }

        invariant = instanceMethodsMap.get(qualifiedInvariantMethodName).iterator().next();

        if (!invariant.getTranslatedReturnType().equals("bool")) {
            throw new IllegalArgumentException("Invariant method must return a boolean");
        }

        if (invariant.getTranslatedArgumentTypes().size() != 1) {
            throw new IllegalArgumentException("Invariant method must have 0 arity");
        }
    }

    private String getPreconditionMethodName(String methodName) {

        return methodName + "_pre";
    }

    private Map<String, Set<Method>> getInstanceMethodsMap() {

        final Map<String, Set<Method>> instanceMethodsMap = new HashMap<>();

        for (Method method : theClass.getMethods()) {

            if (method.isStatic()) {
                continue;
            }

            if (!instanceMethodsMap.containsKey(method.getJavaName())) {
                instanceMethodsMap.put(method.getJavaName(), new HashSet<Method>());
            }

            instanceMethodsMap.get(method.getJavaName()).add(method);
        }

        return instanceMethodsMap;
    }

}
