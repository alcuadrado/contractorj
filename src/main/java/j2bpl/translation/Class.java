package j2bpl.translation;

import soot.SootClass;
import soot.SootField;

import java.util.*;

public class Class {

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

    public String getTranslatedName() {

        final String packageName = sootClass.getJavaPackageName();
        final String className = sootClass.getJavaStyleName();

        if (packageName.isEmpty()) {
            return className;
        }

        return StringUtils.scapeIllegalIdentifierCharacters(sootClass.getJavaPackageName() + "." + className);
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
