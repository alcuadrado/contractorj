package j2bpl;

import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import soot.Body;
import soot.BodyTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InvokeExpr;
import soot.jimple.JimpleBody;
import soot.jimple.Stmt;

public class J2BplTransformer extends BodyTransformer {

  private static J2BplTransformer instance = new J2BplTransformer();

  public static J2BplTransformer getInstance() {

    return instance;
  }

  private J2BplTransformer() {}

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

    return classes
        .stream()
        .filter(aClass -> aClass.getQualifiedJavaName().equals(className))
        .findFirst();
  }

  public String getTranslation() {

    final StringBuilder stringBuilder = new StringBuilder();

    stringBuilder.append(getPrelude());

    final ArrayList<Class> classes = Lists.newArrayList(this.classes);

    classes.sort(Comparator.comparing(Class::getTranslatedName));

    for (Class aClass : classes) {
      stringBuilder.append("\n").append(aClass.getTranslation()).append("\n");

      for (StaticField staticField : aClass.getStaticFields()) {
        try {
          stringBuilder.append("\n").append(staticField.getTranslatedDeclaration()).append("\n");
        } catch (RuntimeException e) {
          // Do nothing
          // TODO: This ignores fields of types that we don't understand.
        }
      }

      for (InstanceField instanceField : aClass.getInstanceFields()) {
        stringBuilder.append("\n").append(instanceField.getTranslatedDeclaration()).append("\n");
      }
    }

    for (String var : getStringConstantVars()) {
      stringBuilder.append("\n").append("var ").append(var).append(" : Ref;").append("\n");
    }

    stringBuilder.append("\n").append(getGlobalInitializationProcedure()).append("\n");

    for (Method method : getMethodsInOrder()) {
      stringBuilder.append("\n").append(method.getTranslatedProcedure()).append("\n");
    }

    return stringBuilder.toString();
  }

  private String getGlobalInitializationProcedure() {

    final StringBuilder stringBuilder = new StringBuilder();

    stringBuilder
        .append("procedure initialize_globals() {\n")
        .append(StringUtils.indent("$Exception := null;"))
        .append("\n");

    for (Class aClass : classes) {
      stringBuilder
          .append(StringUtils.indent("call "))
          .append(aClass.getTranslatedName())
          .append(" := Alloc();\n");
    }

    for (Method method : getMethodsInOrder()) {

      if (method.isClassInitializer()) {

        stringBuilder
            .append(StringUtils.indent("call "))
            .append(method.getTranslatedName())
            .append("();\n")
            .append(StringUtils.indent("assert $Exception == null;"))
            .append("\n");
      }
    }

    for (String var : getStringConstantVars()) {
      stringBuilder
          .append(StringUtils.indent("call "))
          .append(var)
          .append(" := Alloc();")
          .append("\n");
    }

    stringBuilder.append("}");

    return stringBuilder.toString();
  }

  private List<Method> getMethodsInOrder() {

    final Collection<Method> values = methodsMap.values();
    final ArrayList<Method> methods = Lists.newArrayList(values);
    methods.sort(Comparator.comparing(Method::getTranslatedName));

    return methods;
  }

  public Method getMethod(SootMethod sootMethod) {

    return methodsMap.get(sootMethod);
  }

  public Collection<String> getStringConstantVars() {

    return getMethodsInOrder()
        .stream()
        .filter(method -> method instanceof LocalMethod)
        .map(method -> ((LocalMethod) method))
        .flatMap(method -> method.getStringConstantVars().stream())
        .collect(Collectors.toSet());
  }
}
