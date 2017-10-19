package jbct.model;

import com.google.common.base.Joiner;
import java.util.LinkedList;
import java.util.List;
import jbct.soot.TypeTranslator;
import soot.SootMethod;
import soot.Type;
import soot.VoidType;

public class AbstractMethod extends Method {

  protected AbstractMethod(Class theClass, SootMethod sootMethod) {
    super(theClass, sootMethod);
  }

  @Override
  public String getTranslatedProcedure() {
    final StringBuilder stringBuilder =
        new StringBuilder()
            .append("procedure ")
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

  /*protected AbstractMethod(Class theClass, JimpleBody jimpleBody) {
      super(theClass, jimpleBody.getMethod());
      //this.body = jimpleBody;
      //generateNamesForReturnVariables();
  }*/

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

  @Override
  public boolean isClassInitializer() {
    return false;
  }
}
