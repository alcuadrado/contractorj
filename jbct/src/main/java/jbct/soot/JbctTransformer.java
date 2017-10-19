package jbct.soot;

import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;
import jbct.exceptions.UnsupportedTypeException;
import jbct.model.*;
import jbct.model.Class;
import jbct.utils.StringUtils;
import soot.*;
import soot.jimple.*;

public class JbctTransformer extends BodyTransformer {

  private static JbctTransformer instance = new JbctTransformer();

  public static JbctTransformer getInstance() {

    return instance;
  }

  private JbctTransformer() {}

  private final Set<Class> classes = new HashSet<>();

  private final Map<SootMethod, Method> methodsMap = new HashMap<>();

  private boolean skippedMethods(SootMethod sootMethod) {
    if (sootMethod.getDeclaration().contentEquals("public volatile java.lang.Object array()"))
      return true;

    return false;
  }

  @Override
  protected void internalTransform(Body abstractBody, String phaseName, Map options) {
    final SootMethod sootMethod = abstractBody.getMethod();

    if (skippedMethods(sootMethod)) return;

    final SootClass sootClass = sootMethod.getDeclaringClass();

    final Class theClass = Class.create(sootClass);
    final Method method = Method.create(theClass, sootMethod);

    classes.add(theClass);
    methodsMap.put(sootMethod, method);

    findCalledMethods(((JimpleBody) abstractBody));
    RealConstants.getInstance().findRealConstantsInMethods((JimpleBody) abstractBody);
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

    if (skippedMethods(sootMethod)) return;

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
    stringBuilder.append(RealConstants.getInstance().realConstantDefinitions());
    final ArrayList<Class> classes = Lists.newArrayList(this.classes);

    classes.sort(Comparator.comparing(Class::getTranslatedName));

    for (Class aClass : classes) {
      stringBuilder.append("\n").append(aClass.getTranslation()).append("\n");

      for (StaticField staticField : aClass.getStaticFields()) {
        try {
          stringBuilder.append("\n").append(staticField.getTranslatedDeclaration()).append("\n");
        } catch (UnsupportedTypeException e) {
          // We ignore the static field's whose types we don't support
        }
      }

      for (InstanceField instanceField : aClass.getInstanceFields()) {
        stringBuilder.append("\n").append(instanceField.getTranslatedDeclaration()).append("\n");
      }
    }

    for (String var : getStringConstantVars()) {
      stringBuilder.append("\n").append("const unique ").append(var).append(" : Ref;").append("\n");
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
