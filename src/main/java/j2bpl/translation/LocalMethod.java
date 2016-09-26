package j2bpl.translation;

import com.google.common.base.Joiner;
import soot.*;
import soot.jimple.IdentityStmt;
import soot.jimple.InvokeStmt;
import soot.jimple.JimpleBody;
import soot.jimple.ParameterRef;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.ExceptionalBlockGraph;

import java.util.*;

public class LocalMethod extends Method {

    private final List<Local> localsUsedAsParameters = new ArrayList<>();

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

        for (final IdentityStmt identityStmt : getIdentityStatements()) {

            final Value rightOp = identityStmt.getRightOp();
            final Value leftOp = identityStmt.getLeftOp();

            if (!(rightOp instanceof ParameterRef) || !(leftOp instanceof Local)) {
                continue;
            }

            final ParameterRef paramRef = (ParameterRef) rightOp;
            final Local local = (Local) leftOp;

            final String translatedType = TypeTranslator.translate(paramRef.getType());

            parameters.add(local.getName() + " : " + translatedType);

            localsUsedAsParameters.add(local);
        }

        return parameters;
    }

    private List<IdentityStmt> getIdentityStatements() {

        final LinkedList<IdentityStmt> statements = new LinkedList<>();

        for (Unit unit : body.getUnits()) {
            if (unit instanceof IdentityStmt) {
                statements.add((IdentityStmt) unit);
            }
        }

        return statements;
    }

    @Override
    public String getTranslatedProcedure() {

        final List<String> translatedParametersList = getTranslatedParametersList();

        final StringBuilder stringBuilder = new StringBuilder()
                .append("procedure ")
                .append(getTranslatedName())
                .append("(")
                .append(Joiner.on(", ").join(translatedParametersList))
                .append(")");

        if (sootMethod.getReturnType() != VoidType.v()) {
            stringBuilder.append(" returns (r : ")
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
                .append(StringUtils.indentList(getTranslatedInstructions()))
                .append("\n")
                .append("}");


        final List<String> translatedParametersNames = new ArrayList<>(translatedParametersList.size());

        for (String translation : translatedParametersList) {
            final String[] parts = translation.split(":");
            translatedParametersNames.add(parts[0]);
        }

        stringBuilder
                .append("\n\n")

                .append("procedure ")
                .append(getTranslatedName() + "_instrumented")
                .append("(")
                .append(Joiner.on(", ").join(translatedParametersList))
                .append(")\n")

                .append("{\n");

        if (sootMethod.getReturnType() != VoidType.v()) {
            stringBuilder
                    .append(StringUtils.indent("var ret : "))
                    .append(TypeTranslator.translate(sootMethod.getReturnType()))
                    .append(";\n");
        }

        stringBuilder
                .append(StringUtils.indent("call initialize_globals();"))
                .append("\n")

                .append(StringUtils.indent("call "));

        if (sootMethod.getReturnType() != VoidType.v()) {
            stringBuilder.append("ret := ");
        }

        stringBuilder
                .append(getTranslatedName())
                .append("(")
                .append(Joiner.on(", ").join(translatedParametersNames))
                .append(");\n")

                .append(StringUtils.indent("assert $Exception == null;"))
                .append("\n")

                .append("}");

        return stringBuilder.toString();
    }

    @Override
    public boolean isExternalMethod() {
        return false;
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
        final ArrayList<String> translatedLocals = new ArrayList<>();

        for (final Local local : body.getLocals()) {

            if (localsUsedAsParameters.contains(local)) {
                continue;
            }

            translatedLocals.add(translateLocalDeclaration(local));
        }

        return translatedLocals;
    }

    private String translateLocalDeclaration(Local local) {
        return "var " + local.getName() + " : " + TypeTranslator.translate(local.getType()) + ";";
    }

    public String getGeneratedReturnVariableName(InvokeStmt invokeStmt) {
        return generatedReturnVariableNames.get(invokeStmt);
    }

    private List<String> getGeneratedLocalDeclarationsList() {
        final LinkedList<String> declarations = new LinkedList<>();

        for (InvokeStmt invokeStmt : generatedReturnVariableNames.keySet()) {
            final Type returnType = invokeStmt.getInvokeExpr().getMethod().getReturnType();
            declarations.add(
                    "var " + getGeneratedReturnVariableName(invokeStmt) + " : " + TypeTranslator.translate(returnType)
                            + ";"
            );
        }

        return declarations;
    }
}
