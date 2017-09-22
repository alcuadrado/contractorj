package jbct.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import jbct.utils.StringUtils;
import soot.SootClass;
import soot.SootField;

public class Class {

  private static Map<SootClass, Class> classes = new HashMap<>();

  private final SootClass sootClass;

  private final HashSet<Method> methods = new HashSet<>();

  public static Class create(SootClass sootClass) {

    if (!classes.containsKey(sootClass)) {
      classes.put(sootClass, new Class(sootClass));
    }

    return classes.get(sootClass);
  }

  private Class(SootClass sootClass) {

    this.sootClass = sootClass;
  }

  public String getBaseJavaName() {
    return sootClass.getJavaStyleName();
  }

  public String getTranslatedName() {

    return StringUtils.scapeIllegalIdentifierCharacters(getQualifiedJavaName());
  }

  public String getQualifiedJavaName() {

    final String packageName = sootClass.getJavaPackageName();
    final String className = sootClass.getJavaStyleName();

    if (packageName.isEmpty()) {
      return className;
    }

    return packageName + "." + className;
  }

  public boolean isApplicationClass() {

    return sootClass.isApplicationClass();
  }

  public Collection<StaticField> getStaticFields() {

    return sootClass
        .getFields()
        .stream()
        .filter(SootField::isStatic)
        .map(sootField -> new StaticField(this, sootField))
        .collect(Collectors.toList());
  }

  public Collection<InstanceField> getInstanceFields() {

    return sootClass
        .getFields()
        .stream()
        .filter(sootField -> !sootField.isStatic())
        .map(sootField -> new InstanceField(this, sootField))
        .collect(Collectors.toList());
  }

  public void addMethod(Method method) {

    methods.add(method);
  }

  public Set<Method> getMethods() {

    return methods;
  }

  @Override
  public boolean equals(Object other) {

    if (this == other) {
      return true;
    }

    if (!(other instanceof Class)) {
      return false;
    }

    final Class aClass = (Class) other;

    return Objects.equals(getTranslatedName(), aClass.getTranslatedName());
  }

  @Override
  public int hashCode() {

    return Objects.hash(getTranslatedName());
  }

  public String getTranslation() {

    return "const unique " + getTranslatedName() + " : Ref;";
  }
}
