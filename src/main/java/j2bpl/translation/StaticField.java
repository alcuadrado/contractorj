package j2bpl.translation;

import soot.SootField;

public class StaticField {

    private final SootField sootField;

    private final Class theClass;

    public StaticField(Class theClass, SootField sootField) {
        assert sootField.isStatic();
        this.sootField = sootField;
        this.theClass = theClass;
    }

    public StaticField(SootField sootField) {
        this(new Class(sootField.getDeclaringClass()), sootField);
    }

    public String getTranslatedName() {
        return theClass.getTranslatedName() + "." + StringUtils.scapeIllegalIdentifierCharacters(sootField.getName());
    }

    public String getTranslatedDeclaration() {
        return "var " + getTranslatedName() + " : " + TypeTranslator.translate(sootField.getType()) + ";";
    }

}
