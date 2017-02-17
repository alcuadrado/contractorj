package j2bpl;

import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import soot.Local;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.VoidType;
import soot.jimple.IdentityStmt;
import soot.jimple.InvokeStmt;
import soot.jimple.JimpleBody;
import soot.jimple.ParameterRef;
import soot.jimple.StringConstant;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.ExceptionalBlockGraph;

public class LocalMethod extends Method {

  private final HashMap<String, Local> parameterNamesToLocals = new HashMap<>();

  private final HashMap<InvokeStmt, String> generatedReturnVariableNames = new HashMap<>();

  private final JimpleBody body;

  protected LocalMethod(Class theClass, JimpleBody jimpleBody) {

    super(theClass, jimpleBody.getMethod());
    this.body = jimpleBody;
    generateNamesForReturnVariables();
  }

  private void generateNamesForReturnVariables() {

    for (Unit unit : body.getUnits()) {

      if (unit instanceof InvokeStmt) {

        final InvokeStmt invokeStmt = (InvokeStmt) unit;

        final SootMethod method = invokeStmt.getInvokeExpr().getMethod();

        if (method.getReturnType() != VoidType.v()) {
          generatedReturnVariableNames.put(invokeStmt, "$ret$" + invokeStmt.hashCode());
        }
      }
    }
  }

  private List<String> getTranslatedParametersList() {

    final LinkedList<String> parameters = new LinkedList<>();

    if (!isStatic()) {
      parameters.add("$this : Ref");
    }

    int i = 0;

    for (final IdentityStmt identityStmt : getIdentityStatements()) {

      final Value rightOp = identityStmt.getRightOp();
      final Value leftOp = identityStmt.getLeftOp();

      if (!(rightOp instanceof ParameterRef) || !(leftOp instanceof Local)) {
        continue;
      }

      final ParameterRef paramRef = (ParameterRef) rightOp;
      final Local local = (Local) leftOp;

      final String parameterName = "param" + String.format("%02d", i);

      parameterNamesToLocals.put(parameterName, local);

      final String translatedType = TypeTranslator.translate(paramRef.getType());

      parameters.add(parameterName + " : " + translatedType);

      i++;
    }

    return parameters;
  }

  private List<IdentityStmt> getIdentityStatements() {

    return body.getUnits()
        .stream()
        .filter(unit -> unit instanceof IdentityStmt)
        .map(unit -> (IdentityStmt) unit)
        .collect(Collectors.toList());
  }

  @Override
  public String getTranslatedProcedure() {

    final List<String> translatedParametersList = getTranslatedParametersList();

    final StringBuilder stringBuilder =
        new StringBuilder()
            .append("procedure ")
            .append(getTranslatedName())
            .append("(")
            .append(Joiner.on(", ").join(translatedParametersList))
            .append(")");

    if (sootMethod.getReturnType() != VoidType.v()) {
      stringBuilder
          .append(" returns (r : ")
          .append(TypeTranslator.translate(sootMethod.getReturnType()))
          .append(")");
    }

    stringBuilder
        .append("\n")
        .append("{\n")
        .append(StringUtils.indentList(getTranslatedLocalDeclarationsList()))
        .append("\n")
        .append(StringUtils.indentList(getGeneratedLocalDeclarationsList()))
        .append("\n\n")
        .append(StringUtils.indentList(translateParametersAssignments()))
        .append("\n\n")
        .append(StringUtils.indentList(getTranslatedInstructions()))
        .append("\n")
        .append("}");

    return stringBuilder.toString();
  }

  private List<String> translateParametersAssignments() {

    final ArrayList<String> assignments = new ArrayList<>();

    for (String paramName : parameterNamesToLocals.keySet()) {
      final Local local = parameterNamesToLocals.get(paramName);
      assignments.add(local.getName() + " := " + paramName + ";");
    }

    return assignments;
  }

  @Override
  public boolean isClassInitializer() {

    return sootMethod.isEntryMethod() && !sootMethod.isMain();
  }

  private List<String> getTranslatedInstructions() {

    final ExceptionalBlockGraph blocksGraph = new ExceptionalBlockGraph(body);

    final List<String> translatedInstructions = new ArrayList<>();

    for (final Block block : blocksGraph) {
      final BasicBlock basicBlock = BasicBlock.create(this, block);
      translatedInstructions.addAll(basicBlock.getTranslatedInstructions());
      translatedInstructions.add("");
    }

    return translatedInstructions;
  }

  private List<String> getTranslatedLocalDeclarationsList() {

    return body.getLocals()
        .stream()
        .map(this::translateLocalDeclaration)
        .collect(Collectors.toList());
  }

  private String translateLocalDeclaration(Local local) {

    return "var " + local.getName() + " : " + TypeTranslator.translate(local.getType()) + ";";
  }

  public Optional<String> getGeneratedReturnVariableName(InvokeStmt invokeStmt) {

    return Optional.ofNullable(generatedReturnVariableNames.get(invokeStmt));
  }

  private List<String> getGeneratedLocalDeclarationsList() {

    final LinkedList<String> declarations = new LinkedList<>();

    for (InvokeStmt invokeStmt : generatedReturnVariableNames.keySet()) {

      final SootMethod invokedSootMethod = invokeStmt.getInvokeExpr().getMethod();

      final Class invokedClass = Class.create(invokedSootMethod.getDeclaringClass());
      final Method invokedMethod = Method.create(invokedClass, invokedSootMethod);

      declarations.add(
          "var "
              + generatedReturnVariableNames.get(invokeStmt)
              + " : "
              + invokedMethod.getTranslatedReturnType()
              + ";");
    }

    return declarations;
  }

  public List<String> getStringConstantVars() {

    final List<String> vars = new ArrayList<>();

    for (Unit unit : body.getUnits()) {
      for (ValueBox valueBox : unit.getUseBoxes()) {
        final Value value = valueBox.getValue();

        if (value instanceof StringConstant) {
          vars.add(ValueTranslator.getStringConstantVarName((StringConstant) value));
        }
      }
    }

    return vars;
  }
}
