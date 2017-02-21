package jbct;

import soot.SootField;

public class InstanceField {

  private final SootField sootField;

  private final Class theClass;

  public InstanceField(Class theClass, SootField sootField) {

    assert !sootField.isStatic();
    this.sootField = sootField;
    this.theClass = theClass;
  }

  public InstanceField(SootField sootField) {

    this(Class.create(sootField.getDeclaringClass()), sootField);
  }

  public String getTranslatedName() {

    return theClass.getTranslatedName()
        + "#"
        + StringUtils.scapeIllegalIdentifierCharacters(sootField.getName());
  }

  public String getTranslatedDeclaration() {

    return "const unique " + getTranslatedName() + " : Field;";
  }
}
