package jbct.model;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

import java.util.*;

import jbct.soot.TypeTranslator;
import soot.SootMethod;
import soot.Type;
import soot.VoidType;

public class ExternalMethod extends Method {

  // holds external methods that will be translated as non deterministic (their return value - exception value is not modified)
  private static final Set<ExternalMethod> nonHardcodedExternalMethods = new HashSet<ExternalMethod>();

  public static void addExternalMethodForDeclaration(ExternalMethod m){
    nonHardcodedExternalMethods.add(m);
  }

  public static void writeExternalMethodDeclarations(StringBuilder sb){

    for (ExternalMethod m : nonHardcodedExternalMethods){
      sb.append("// external method translated as non deterministic - exception variable is not modified.");
      sb.append(m.getTranslatedProcedure());
    }

  }

  /*private static final Set<String> hardCodedMethodsTranslatedNames =
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

  private Set<String> methodsWithHardcodedIntReturnType =
          Sets.newHashSet(
                  "java.lang.String#charAt$int", // for stringtokenizer example
                  "java.lang.String#indexOf$int", // for stringtokenizer example
                  "java.lang.String#codePointAt$int",// for stringtokenizer example
                  "java.lang.Character.charCount$int" // for stringtokenizer example
          );

  private Set<String> methodsWithHardcodedBooleanReturnType =
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

  private Set<String> methodsWithHardcodedRefReturnType =
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
  }*/

  public ExternalMethod(Class theClass, SootMethod sootMethod) {

    super(theClass, sootMethod);
  }



  @Override
  public String getTranslatedReturnType() {

    if (methodsWithHardcodedBooleanReturnType.contains(getTranslatedName())) {
      return "bool";
    }

    return super.getTranslatedReturnType();
  }

  @Override
  public String getTranslatedProcedure() {

    if (isHardCoded()) {
      return "// Skipping hardcoded method " + getTranslatedName();
    }

    final StringBuilder stringBuilder =
        new StringBuilder()
            .append("procedure ")
            .append("{:extern} ")
            .append(getTranslatedName())
            .append("(")
            .append(Joiner.on(", ").join(getTranslatedParametersList()))
            .append(")");

    if (sootMethod.getReturnType() != VoidType.v()) {
      stringBuilder
          .append(" returns (r : ")
          .append(TypeTranslator.translate(sootMethod.getReturnType()))
          .append(")");
    }

    stringBuilder.append(";");

    return stringBuilder.toString();
  }

  @Override
  public boolean isClassInitializer() {

    return false;
  }

  private List<String> getTranslatedParametersList() {

    final LinkedList<String> parameters = new LinkedList<>();

    if (!isStatic()) {
      parameters.add("$this : Ref");
    }

    @SuppressWarnings("unchecked")
    final List<Type> parameterTypes = sootMethod.getParameterTypes();

    for (int i = 0; i < parameterTypes.size(); i++) {
      final String translatedType = TypeTranslator.translate(parameterTypes.get(i));
      final String translation = String.format("param%02d : %s", i, translatedType);
      parameters.add(translation);
    }

    return parameters;
  }
}
