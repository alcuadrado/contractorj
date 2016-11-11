package j2bpl;

import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import soot.Body;
import soot.BodyTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InvokeExpr;
import soot.jimple.JimpleBody;
import soot.jimple.Stmt;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class J2BplTransformer extends BodyTransformer {

    private static J2BplTransformer instance = new J2BplTransformer();

    public static J2BplTransformer getInstance() {

        return instance;
    }

    private J2BplTransformer() {

    }

    private final Set<Class> classes = new HashSet<>();

    private final Map<SootMethod, Method> methodsMap = new HashMap<>();

    @Override
    protected void internalTransform(Body abstractBody, String phaseName, Map options) {

        final SootMethod sootMethod = abstractBody.getMethod();
        final SootClass sootClass = sootMethod.getDeclaringClass();

        final Class theClass = Class.create(sootClass);
        final Method method = Method.create(theClass, sootMethod);

        classes.add(theClass);
        methodsMap.put(sootMethod, method);

        findCalledMethods(((JimpleBody) abstractBody));

    }

    private void findCalledMethods(JimpleBody jimpleBody) {

        for (Unit unit : jimpleBody.getUnits()) {
            if (unit instanceof Stmt) {
                try {
                    final Stmt stmt = (Stmt) unit;
                    final InvokeExpr invokeExpr = stmt.getInvokeExpr();
                    if (invokeExpr != null) {
                        addCalledMethod(invokeExpr);
                    }
                } catch (RuntimeException e) {
                    // Do nothing: this just means that no invoke expr was present
                }
            }
        }
    }

    private void addCalledMethod(InvokeExpr invokeExpr) {
        final SootMethod sootMethod = invokeExpr.getMethod();
        final SootClass sootClass = sootMethod.getDeclaringClass();
        final Class theClass = Class.create(sootClass);
        classes.add(theClass);
        methodsMap.put(sootMethod, Method.create(theClass, sootMethod));
    }

    private String getPrelude() {

        final URL resource = this.getClass().getClassLoader().getResource("prelude.bpl");
        assert resource != null;

        try {
            return Resources.toString(resource, Charset.forName("UTF-8"));
        } catch (IOException e) {
            // This should not happen
            throw new RuntimeException(e);
        }
    }

    public Optional<Class> getClass(String className) {

        return classes.stream()
                .filter(aClass -> aClass.getQualifiedJavaName().equals(className))
                .findFirst();
    }

    public String getTranslation() {

        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(getPrelude());

        final ArrayList<Class> classes = Lists.newArrayList(this.classes);

        Collections.sort(classes, (o1, o2) -> o1.getTranslatedName().compareTo(o2.getTranslatedName()));

        for (Class aClass : classes) {
            stringBuilder.append("\n")
                    .append(aClass.getTranslation())
                    .append("\n");

            for (StaticField staticField : aClass.getStaticFields()) {
                stringBuilder.append("\n")
                        .append(staticField.getTranslatedDeclaration())
                        .append("\n");
            }

            for (InstanceField instanceField : aClass.getInstanceFields()) {
                stringBuilder.append("\n")
                        .append(instanceField.getTranslatedDeclaration())
                        .append("\n");
            }
        }

        stringBuilder.append("\n")
                .append(getGlobalInitializationProcedure())
                .append("\n");

        for (Method method : getMethodsInOrder()) {
            stringBuilder.append("\n")
                    .append(method.getTranslatedProcedure())
                    .append("\n");
        }

        return stringBuilder.toString();
    }

    private String getGlobalInitializationProcedure() {

        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("procedure initialize_globals() {\n")
                .append(StringUtils.indent("$Exception := null;"))
                .append("\n");

        for (Class aClass : classes) {
            stringBuilder.append(StringUtils.indent("call "))
                    .append(aClass.getTranslatedName())
                    .append(" := Alloc();\n");

        }

        for (Method method : getMethodsInOrder()) {

            if (method.isClassInitializer()) {

                stringBuilder.append(StringUtils.indent("call "))
                        .append(method.getTranslatedName())
                        .append("();\n")
                        .append(StringUtils.indent("assert $Exception == null;"))
                        .append("\n");
            }
        }

        stringBuilder.append("}");

        return stringBuilder.toString();
    }

    private List<Method> getMethodsInOrder() {

        final Collection<Method> values = methodsMap.values();
        final ArrayList<Method> methods = Lists.newArrayList(values);
        Collections.sort(methods, (o1, o2) -> o1.getTranslatedName().compareTo(o2.getTranslatedName()));

        return methods;
    }

    public Method getMethod(SootMethod sootMethod) {

        return methodsMap.get(sootMethod);
    }

}
