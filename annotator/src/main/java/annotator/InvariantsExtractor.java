package annotator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InvariantsExtractor {

    static final Pattern idfentifiersPattern = Pattern.compile(
            "[a-zA-Z$_][a-zA-Z$_0-9]*(\\.[a-zA-Z$_][a-zA-Z$_0-9]*)*"
    );

    private static final String EXIT_INV_NAME = "EXIT";

    private static final String OBJECT_INV_NAME = "OBJECT";

    private final Map<String, List<String>> statePreconditionsForMethod = new HashMap<>();

    private final Map<Method, List<String>> paramsPreconditionsForMethod = new HashMap<>();

    private List<String> objectInvariantConditions;

    public List<Invariant> computeInvariants(final String daikonOutput, final CompilationUnit compilationUnit) {

        computeInvariantsConditions(daikonOutput, compilationUnit);

        final List<Invariant> invariants = Lists.newArrayList(new ObjectInvariant(objectInvariantConditions));

        invariants.addAll(getStatePreconditions());
        invariants.addAll(getParamsPreconditions());

        return invariants;
    }

    private List<StatePrecondition> getStatePreconditions() {

        final List<StatePrecondition> statePreconditions = new ArrayList<>(statePreconditionsForMethod.size());

        for (final String methodName : statePreconditionsForMethod.keySet()) {

            final List<String> conditions = statePreconditionsForMethod.get(methodName);
            final StatePrecondition statePrecondition = new StatePrecondition(conditions, methodName);

            statePreconditions.add(statePrecondition);
        }
        return statePreconditions;
    }

    private List<ParamsPrecondition> getParamsPreconditions() {

        final List<ParamsPrecondition> paramsPreconditions = new ArrayList<>(paramsPreconditionsForMethod.size());

        for (final Method method : paramsPreconditionsForMethod.keySet()) {

            final List<String> conditions = paramsPreconditionsForMethod.get(method);
            final ParamsPrecondition paramsPrecondition = new ParamsPrecondition(conditions, method);

            paramsPreconditions.add(paramsPrecondition);
        }

        return paramsPreconditions;
    }

    private void computeInvariantsConditions(final String daikonOutput, final CompilationUnit compilationUnit) {

        final String[] parts = daikonOutput.split("={3,}");

        for (String part : parts) {

            part = part.trim();

            if (!part.startsWith(CompilationUnitHelper.getQualifiedClassName(compilationUnit))
                    || part.contains(":" + EXIT_INV_NAME)) {
                continue;
            }

            final String[] lines = part.split("\\r?\\n");
            final String header = lines[0];

            final List<String> conditions = processConditions(
                    Stream.of(lines)
                            .map(String::trim)
                            .skip(1)
                            .collect(Collectors.toList())
            );

            if (conditions.size() == 0) {
                continue;
            }

            if (header.endsWith(OBJECT_INV_NAME)) {
                objectInvariantConditions = conditions;
                continue;
            }

            final String methodName = getMethodNameFromInvariantDeclaration(lines[0]);
            final List<String> paramTypes = getParametersTypesFromInvariantDeclaration(header);

            final List<String> statePreconditions = getStatePreconditions(methodName);

            final List<String> paramsPreconditions;

            if (paramTypes.size() != 0) {
                paramsPreconditions = getMethodPreconditions(compilationUnit, methodName, paramTypes);
            } else {
                paramsPreconditions = null;
            }

            for (final String condition : conditions) {

                if (onlyAccessInternalState(condition)) {
                    statePreconditions.add(condition);
                    continue;
                }

                if (paramsPreconditions != null && onlyAccessParameters(condition)) {
                    paramsPreconditions.add(condition);
                    continue;
                }

                System.out.println(
                        "Condition " + condition + " ignored because it access both parameters and internal state"
                );
            }
        }
    }

    private List<String> getMethodPreconditions(final CompilationUnit compilationUnit, final String methodName,
                                                final List<String> paramTypes) {

        final List<Method> methodsByName = getMethodsByName(compilationUnit, methodName);

        final List<Method> matchedMethodDeclarations = methodsByName.stream()
                .filter(method -> method.getNumberOfParameters() == paramTypes.size())
                .collect(Collectors.toList());

        if (matchedMethodDeclarations.size() > 1) {
            throw new UnsupportedOperationException("Annotator can't handle methods overloading with same number " +
                    "of arguments.");
        }

        final Method method = matchedMethodDeclarations.get(0);

        if (!paramsPreconditionsForMethod.containsKey(method)) {
            paramsPreconditionsForMethod.put(method, new ArrayList<>());
        }

        return paramsPreconditionsForMethod.get(method);
    }

    private List<String> getStatePreconditions(final String methodName) {

        if (!statePreconditionsForMethod.containsKey(methodName)) {
            statePreconditionsForMethod.put(methodName, new ArrayList<>());
        }

        return statePreconditionsForMethod.get(methodName);
    }

    private boolean onlyAccessInternalState(final String condition) {

        return getIdentifiers(condition)
                .stream()
                .filter(s -> !s.equals("$$size"))
                .allMatch(s -> s.startsWith("this."));
    }

    private boolean onlyAccessParameters(final String condition) {

        return getIdentifiers(condition)
                .stream()
                .noneMatch(s -> s.startsWith("this."));
    }

    private List<String> getIdentifiers(final String condition) {

        final Matcher matcher = idfentifiersPattern.matcher(condition);

        final ArrayList<String> identifiers = new ArrayList<>();
        while (matcher.find()) {
            final String identifier = matcher.group();
            identifiers.add(identifier);
        }

        return identifiers;
    }

    private List<String> getParametersTypesFromInvariantDeclaration(final String invariantDeclaration) {

        final String paramsList = invariantDeclaration.substring(
                invariantDeclaration.indexOf("(") + 1,
                invariantDeclaration.indexOf(")")
        );

        return Stream.of(paramsList.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private String getMethodNameFromInvariantDeclaration(final String invariantDeclaration) {

        final String qualifiedName = invariantDeclaration.substring(0, invariantDeclaration.indexOf("("));

        return qualifiedName.substring(qualifiedName.lastIndexOf(".") + 1);
    }

    private List<Method> getMethodsByName(final CompilationUnit compilationUnit, final String name) {

        final ClassOrInterfaceDeclaration theClass = compilationUnit.getClassByName(
                CompilationUnitHelper.getClassName(compilationUnit)
        );

        final Stream<Method> methodsStream = theClass.getMembers()
                .stream()
                .filter(bodyDeclaration -> bodyDeclaration instanceof MethodDeclaration)
                .map(bodyDeclaration -> Method.fromMethodDeclaration((MethodDeclaration) bodyDeclaration));

        final Stream<Method> constructorsStream = theClass.getMembers()
                .stream()
                .filter(bodyDeclaration -> bodyDeclaration instanceof ConstructorDeclaration)
                .map(bodyDeclaration -> Method.fromConstructorDeclaration((ConstructorDeclaration) bodyDeclaration));

        return Stream.concat(methodsStream, constructorsStream)
                .filter(method -> method.getSimpleName().equals(name))
                .collect(Collectors.toList());
    }

    private List<String> processConditions(final List<String> conditions) {

        return conditions.stream()
                .map(this::replaceSize)
                .filter(s -> !s.contains("daikon"))
                .filter(s -> !isClassNamesCheckCondition(s))
                .collect(Collectors.toList());
    }

    private boolean isClassNamesCheckCondition(final String condition) {

        return condition.contains(".getName() ")
                && condition.endsWith(".getName()")
                && (condition.contains("getClass()") || condition.contains("class"));
    }

    private String replaceSize(String condition) {

        return condition.replace("daikon.Quant.size(", "$$size(");
    }

}
