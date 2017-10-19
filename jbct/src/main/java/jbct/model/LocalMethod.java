package jbct.model;

import com.google.common.base.Joiner;
import java.util.*;
import java.util.stream.Collectors;
import jbct.soot.TypeTranslator;
import jbct.soot.ValueTranslator;
import jbct.utils.StringUtils;
import soot.Local;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.VoidType;
import soot.jimple.*;
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

  // This method is called at the instance's constructor
  // it parses and finds every function call
  // for every function call a ret variable is created in boogie
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

  // for every parameter in the method a local boogie variable is created
  // mapping between local boggie variable and argument is stored
  private List<String> getTranslatedParametersList() {

    final LinkedList<String> parameters = new LinkedList<>();

    if (!isStatic()) {
      parameters.add("$this : Ref");
    }

    int i = 0;

    // debemos recorrer todas las asignaciones de parametros a variables locales del body en jimple
    // por ejemplo: r1 =  @parameter0 : java.util.List
    // asumimos que hay 1 asignacion por parametro del método
    // en i contamos la aparición de cada asignación
    // Al encontrar la i-esima aparición de una asignación al parametro relacionado se lo llama param0i o parami
    // y se guarda en la lista de retorno la aridad como string parameterName : translatedType
    // siendo translatedType el tipo traducido
    // Se aprovecha esta pasada de las asignaciones para vincular en un hash
    // parameterName -> local

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

  // returns a list of statements that are assignments
  // if i am not wrong that's an identitystatement
  private List<IdentityStmt> getIdentityStatements() {

    return body.getUnits()
        .stream()
        .filter(unit -> unit instanceof IdentityStmt)
        .map(unit -> (IdentityStmt) unit)
        .collect(Collectors.toList());
  }

  @Override
  // returns the boogie code of the procedure
  // 1) procedure's parameters are calculated and used in the procedure declaration
  // 2) local variables are written
  // 3) local variables for procedure invocations
  // 4) write assignments of parameters to local variables
  // 5) translate each instruction from the jimple body
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
        // las declaraciones de las variables locales con las traducciones de sus tipos
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

  // assigns parameters to local variables
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

  // creates a local variable for the result of each call
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
