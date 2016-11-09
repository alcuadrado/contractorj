package j2bpl;

import soot.SootClass;
import soot.SootField;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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

        final LinkedList<StaticField> staticFields = new LinkedList<>();

        for (SootField sootField : sootClass.getFields()) {
            if (sootField.isStatic()) {
                staticFields.add(new StaticField(this, sootField));
            }
        }

        return staticFields;
    }

    public Collection<InstanceField> getInstanceFields() {

        final LinkedList<InstanceField> instanceFields = new LinkedList<>();

        for (SootField sootField : sootClass.getFields()) {
            if (!sootField.isStatic()) {
                instanceFields.add(new InstanceField(this, sootField));
            }
        }

        return instanceFields;
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

        return "var " + getTranslatedName() + " : Ref;";
    }
}
