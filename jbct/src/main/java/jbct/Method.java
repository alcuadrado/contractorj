package jbct;

import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import soot.RefType;
import soot.SootMethod;
import soot.Type;
import soot.VoidType;
import soot.jimple.JimpleBody;

public abstract class Method {

  private static final HashMap<String, LocalMethod> localMethodsFactoryCache = new HashMap<>();

  private Class theClass;

  protected final SootMethod sootMethod;

  public static Method create(Class theClass, SootMethod sootMethod) {

    if (!theClass.isApplicationClass()) {

      final ExternalMethod externalMethod = new ExternalMethod(theClass, sootMethod);

      theClass.addMethod(externalMethod);

      return externalMethod;
    }

    final String translatedMethodName = getTranslatedMethodName(theClass, sootMethod);

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

    return mangleMethodName(sootMethod, baseName);
  }

  /** Mangles the name of a method. */
  private static String mangleMethodName(SootMethod sootMethod, String nonMangledName) {

    final StringBuilder stringBuilder = new StringBuilder(nonMangledName);

    @SuppressWarnings("unchecked")
    final List<Type> parameterTypes = sootMethod.getParameterTypes();

    for (final Type parameterType : parameterTypes) {

      final String paramTypeName;

      if (parameterType instanceof RefType) {

        final RefType refType = (RefType) parameterType;
        final Class aClass = Class.create(refType.getSootClass());
        paramTypeName = aClass.getQualifiedJavaName();

      } else {
        paramTypeName = TypeTranslator.translate(parameterType);
      }

      stringBuilder.append("$").append(StringUtils.scapeIllegalIdentifierCharacters(paramTypeName));
    }

    return stringBuilder.toString();
  }

  @SuppressWarnings("unchecked")
  public List<Type> getParameterTypes() {

    return sootMethod.getParameterTypes();
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

    @SuppressWarnings("unckeched")
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
