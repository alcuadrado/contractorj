package j2bpl.translation;

import com.google.common.io.Resources;
import j2bpl.Main;
import soot.*;
import soot.jimple.InvokeExpr;
import soot.jimple.JimpleBody;
import soot.jimple.Stmt;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
                    addCalledMethod(stmt.getInvokeExpr());
                } catch (RuntimeException e) {
                    // Do nothing: this just means that no invoke expr was present
                }
            }
        }
    }

    private void addCalledMethod(InvokeExpr invokeExpr) {

        if (invokeExpr == null) {
            return;
        }

        final SootMethod sootMethod = invokeExpr.getMethod();
        final SootClass sootClass = sootMethod.getDeclaringClass();
        final Class theClass = Class.create(sootClass);
        classes.add(theClass);
        methodsMap.put(sootMethod, Method.create(theClass, sootMethod));
    }

    private String getPrelude() {

        final URL resource = Main.class.getClassLoader().getResource("prelude.bpl");
        assert resource != null;

        try {
            return Resources.toString(resource, Charset.forName("UTF-8"));
        } catch (IOException e) {
            // This should not happen
            throw new RuntimeException(e);
        }
    }

    public String getTranslation() {

        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(getPrelude());

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

        for (Method method : methodsMap.values()) {
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

        for (Method method : methodsMap.values()) {

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

    public Method getMethod(SootMethod sootMethod) {
        return methodsMap.get(sootMethod);
    }

}
