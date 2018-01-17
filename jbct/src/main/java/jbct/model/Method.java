package jbct.model;

import com.google.common.base.Joiner;

import java.util.*;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import jbct.soot.TypeTranslator;
import jbct.utils.StringUtils;
import soot.RefType;
import soot.SootMethod;
import soot.Type;
import soot.VoidType;
import soot.jimple.JimpleBody;

public abstract class Method {

  private static final HashMap<String, LocalMethod> localMethodsFactoryCache = new HashMap<>();

  private static final HashMap<String, AbstractMethod> AbstractMethodsFactoryCache =
      new HashMap<>();

  private Class theClass;

  protected final SootMethod sootMethod;

  public static Method create(Class theClass, SootMethod sootMethod) {

    if (!theClass.isApplicationClass()) {

      final ExternalMethod externalMethod = new ExternalMethod(theClass, sootMethod);

      theClass.addMethod(externalMethod);

      return externalMethod;
    }

    final String translatedMethodName = getTranslatedMethodName(theClass, sootMethod);

    if (!sootMethod.isConcrete()) {

      if (!AbstractMethodsFactoryCache.containsKey(translatedMethodName)) {
        final AbstractMethod abstractMethod = new AbstractMethod(theClass, sootMethod);
        theClass.addMethod(abstractMethod);
        AbstractMethodsFactoryCache.put(translatedMethodName, abstractMethod);
      }

      return AbstractMethodsFactoryCache.get(translatedMethodName);
    }

    if (!localMethodsFactoryCache.containsKey(translatedMethodName)) {
      final JimpleBody jimpleBody = (JimpleBody) sootMethod.getActiveBody();
      final LocalMethod localMethod = new LocalMethod(theClass, jimpleBody);

      theClass.addMethod(localMethod);

      localMethodsFactoryCache.put(translatedMethodName, localMethod);
    }

    return localMethodsFactoryCache.get(translatedMethodName);
  }

  /**
   * Returns the translated name of the method.
   *
   * <p>
   *
   * <p>The name is the method's fully qualified name with boogie-identifier illegal characters
   * escaped, and mangled with its parameter types.
   */
  private static String getTranslatedMethodName(Class theClass, SootMethod sootMethod) {

    final String baseName =
        theClass.getTranslatedName()
            + (sootMethod.isStatic() ? "." : "#")
            + StringUtils.scapeIllegalIdentifierCharacters(sootMethod.getName());

    final String mangledMethodName = mangleMethodName(sootMethod, baseName);
    return mangledMethodName;
  }

  /** Mangles the name of a method. */
  private static String mangleMethodName(SootMethod sootMethod, String nonMangledName) {

    final StringBuilder stringBuilder = new StringBuilder(nonMangledName);

    @SuppressWarnings("unchecked")
    final List<Type> parameterTypes = sootMethod.getParameterTypes();

    for (final Type parameterType : parameterTypes) {

      // there could be 2 methods with different arity
      // void foo(int i) & void foo(byte i)
      // if we use the translated type for procedure name they will collide
      // int is translated to int and byte is translated to int
      // in this way we use the type name given by soot.
      final String paramTypeName = parameterType.toString();

      stringBuilder.append("$").append(StringUtils.scapeIllegalIdentifierCharacters(paramTypeName));
    }

    return stringBuilder.toString();
  }

  @SuppressWarnings("unchecked")
  public List<Type> getParameterTypes() {

    return sootMethod.getParameterTypes();
  }

  protected static final Set<String> hardCodedMethodsTranslatedNames =
          Sets.newHashSet(
                  "java.util.Collection#size",
                  "java.util.Collection#clear",
                  "java.util.List#size",
                  "java.util.List#clear",
                  "java.util.LinkedList#?init?",
                  "java.util.LinkedList#size",
                  "java.util.LinkedList#clear",
                  "java.util.ArrayList#?init?",
                  "java.util.ArrayList#?init?$int",
                  "java.util.ArrayList#size",
                  "java.util.ArrayList#clear",
                  //"java.util.Arrays.copyOf$Ref$int",
                  "java.util.Arrays.copyOf$java.lang.Object??$int",
                  "java.lang.String#length");

  protected Set<String> methodsWithHardcodedIntReturnType =
          Sets.newHashSet(""
                  //"java.lang.String#charAt$int", // for stringtokenizer example
                  //"java.lang.String#indexOf$int", // for stringtokenizer example
                  //"java.lang.String#codePointAt$int",// for stringtokenizer example
                  //"java.lang.Character.charCount$int" // for stringtokenizer example
          );

  protected Set<String> methodsWithHardcodedBooleanReturnType =
          Sets.newHashSet(
                  "java.util.Collection#remove$int",
                  "java.util.Collection#add$java.lang.Object",
                  "java.util.Collection#remove$java.lang.Object",
                  "java.util.List#remove$int",
                  "java.util.List#add$java.lang.Object",
                  "java.util.List#remove$java.lang.Object",
                  "java.util.LinkedList#remove$int",
                  "java.util.LinkedList#add$java.lang.Object",
                  "java.util.LinkedList#remove$java.lang.Object",
                  "java.util.ArrayList#remove$int",
                  "java.util.ArrayList#add$java.lang.Object");

  protected Set<String> methodsWithHardcodedRefReturnType =
          Sets.newHashSet(//"java.util.Map#get$java.lang.Object",
                  // "java.util.Enumeration#nextElement",
                  //"java.util.Iterator#next"
                  // "java.util.Map#put$java.lang.Object$java.lang.Object"
          );


  public boolean isHardCoded() {
    final String translatedName = getTranslatedName();

    return hardCodedMethodsTranslatedNames.contains(translatedName)
            || methodsWithHardcodedBooleanReturnType.contains(translatedName)
            || methodsWithHardcodedRefReturnType.contains(translatedName)
            || methodsWithHardcodedIntReturnType.contains(translatedName);
  }

  public abstract String getTranslatedProcedure();

  public abstract boolean isClassInitializer();

  protected Method(Class theClass, SootMethod sootMethod) {

    this.theClass = theClass;
    this.sootMethod = sootMethod;
  }

  public String getTranslatedName() {

    return getTranslatedMethodName(theClass, sootMethod);
  }

  public String getJavaName() {

    return theClass.getQualifiedJavaName() + (isStatic() ? "." : "#") + sootMethod.getName();
  }

  public String getBaseJavaName() {

    return sootMethod.getName();
  }

  public String getJavaNameWithArgumentTypes() {

    @SuppressWarnings("unchecked")
    final List<Type> parameterTypes = sootMethod.getParameterTypes();
    final List<String> typeNames =
        parameterTypes.stream().map(Type::toString).collect(Collectors.toList());

    final String baseName =
        sootMethod.isConstructor() ? theClass.getBaseJavaName() : sootMethod.getName();

    return baseName + "(" + Joiner.on(", ").join(typeNames) + ")";
  }

  public boolean isStatic() {

    return sootMethod.isStatic();
  }

  public boolean isConstructor() {

    return sootMethod.isConstructor();
  }

  public boolean hasReturnType() {

    return sootMethod.getReturnType() != VoidType.v();
  }

  public String getTranslatedReturnType() {

    return TypeTranslator.translate(sootMethod.getReturnType());
  }

  public List<String> getTranslatedArgumentTypes() {

    final ArrayList<String> translatedArguments = new ArrayList<>();

    if (!isStatic()) {
      translatedArguments.add("Ref");
    }

    @SuppressWarnings("unchecked")
    final Iterator<Type> iterator = sootMethod.getParameterTypes().iterator();

    while (iterator.hasNext()) {
      final Type type = iterator.next();
      translatedArguments.add(TypeTranslator.translate(type));
    }

    return translatedArguments;
  }

  @Override
  public boolean equals(Object other) {

    if (this == other) {
      return true;
    }

    if (!(other instanceof Method)) {
      return false;
    }

    final Method method = (Method) other;

    return Objects.equals(getTranslatedName(), method.getTranslatedName());
  }

  @Override
  public int hashCode() {

    return Objects.hash(getTranslatedName());
  }

  @Override
  public String toString() {

    return getClass().getSimpleName() + "<" + getTranslatedName() + ">";
  }
}
