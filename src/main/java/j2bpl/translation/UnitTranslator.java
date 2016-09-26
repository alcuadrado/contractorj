package j2bpl.translation;

import soot.BooleanType;
import soot.SootField;
import soot.Type;
import soot.Value;
import soot.jimple.*;

public class UnitTranslator extends AbstractStmtSwitch {

    private final StringBuilder stringBuilder = new StringBuilder();

    private final LocalMethod method;

    private final BasicBlock basicBlock;

    public UnitTranslator(LocalMethod method, BasicBlock basicBlock) {
        this.method = method;
        this.basicBlock = basicBlock;
    }

    @Override
    public void caseAssignStmt(AssignStmt stmt) {

        final Value leftOp = stmt.getLeftOp();
        final Value rightOp = stmt.getRightOp();

        if (leftOp instanceof InstanceFieldRef) {
            translateAssignmentToField((InstanceFieldRef) leftOp, rightOp);
            return;
        }

        if (rightOp instanceof NewArrayExpr) {
            translateArrayCreation(leftOp, ((NewArrayExpr) rightOp));
            return;
        }

        if (leftOp instanceof ArrayRef) {
            translateAssignmentToArray(((ArrayRef) leftOp), rightOp);
            return;
        }

        if (rightOp instanceof NewExpr || rightOp instanceof InvokeExpr || rightOp instanceof DivExpr) {
            stringBuilder.append("call ");
        }

        String rightOptTranslation = translateValue(rightOp);

        // We need a special case for booleans because they are 0 and 1 in jimple, but that doesn't
        // type check in boogie.

        if (leftOp.getType() == BooleanType.v() && rightOp instanceof IntConstant) {
            final int value = ((IntConstant) rightOp).value;
            rightOptTranslation = value == 0 ? "false" : "true";
        }

        final String translatedLeftOp = translateValue(leftOp);

        stringBuilder.append(translatedLeftOp)
                .append(" := ")
                .append(rightOptTranslation)
                .append(";");

        if (rightOp instanceof InvokeExpr || rightOp instanceof DivExpr) {
            stringBuilder.append("\n")
                    .append("if ($Exception != null) {\n")
                    .append(StringUtils.indent("return;"))
                    .append("\n")
                    .append("}");
        }
    }

    private void translateArrayCreation(Value leftOp, NewArrayExpr newArrayExpr) {

        final String translatedRef = translateValue(leftOp);
        final String translatedSize = translateValue(newArrayExpr.getSize());

        stringBuilder.append("call ")
                .append(translatedRef)
                .append(" := Alloc();\n")
                .append("assume $ArrayLength(")
                .append(translatedRef)
                .append(") == ")
                .append(translatedSize)
                .append(";");
    }

    private void translateAssignmentToArray(ArrayRef arrayRef, Value value) {
        final String translatedValue = translateValue(value);
        final String translatedIndex = translateValue(arrayRef.getIndex());
        final String translatedRef = translateValue(arrayRef.getBase());

        translateTransformationToUnion(arrayRef.getType(), value, translatedValue);
        final String toUnion = getToUnion(arrayRef.getType(), value, translatedValue);

        stringBuilder.append("assert ")
                .append(translatedRef)
                .append(" != null;\n")
                .append("$ArrayContents := $ArrayContents[")
                .append(translatedRef)
                .append(" := $ArrayContents[")
                .append(translatedRef)
                .append("][")
                .append(translatedIndex)
                .append(" := ")
                .append(toUnion)
                .append("]];");
    }

    private String translateValue(Value value) {
        final ValueTranslator translator = new ValueTranslator();
        value.apply(translator);
        return translator.getTranslation();
    }

    private void translateTransformationToUnion(Type type, Value value, String translatedValue) {

        final String translateType = TypeTranslator.translate(type);

        if (translateType.equals("int")) {

            stringBuilder.append("assume Union2Int(Int2Union(")
                    .append(translatedValue)
                    .append(")) == ")
                    .append(translatedValue)
                    .append(";\n");

            return;
        }


        if (type == BooleanType.v() && value instanceof IntConstant) {
            final boolean booleanValue = ((IntConstant) value).value != 0;

            stringBuilder.append("assume Union2Bool(Bool2Union(")
                    .append(booleanValue)
                    .append(")) == ")
                    .append(booleanValue)
                    .append(";\n");

            return;
        }

        if (translateType.equals("Ref")) {
            //Do nothing
            return;
        }

        throw new UnsupportedOperationException("Can't transform type " + type + " to Union");
    }

    private String getToUnion(Type type, Value value, String translatedValue) {
        final String translateType = TypeTranslator.translate(type);

        if (translateType.equals("int")) {
            return "Int2Union(" + translatedValue + ")";
        }

        if (type == BooleanType.v() && value instanceof IntConstant) {

            final boolean booleanValue = ((IntConstant) value).value != 0;

            return "Bool2Union(" + (booleanValue ? "true" : "false") + ")";
        }

        if (translateType.equals("Ref")) {
            return translatedValue;
        }

        throw new UnsupportedOperationException("Can't transform type " + type + " to Union");
    }

    private void translateAssignmentToField(InstanceFieldRef instanceFieldRef, Value value) {

        final SootField field = instanceFieldRef.getField();

        final InstanceField instanceField = new InstanceField(field);

        final Type type = field.getType();

        final String translatedValue = translateValue(value);
        final String translatedRef = translateValue(instanceFieldRef.getBase());

        translateTransformationToUnion(type, value, translatedValue);

        stringBuilder.append("$Heap := Write($Heap, ")
                .append(translatedRef)
                .append(", ")
                .append(instanceField.getTranslatedName())
                .append(", ")
                .append(getToUnion(type, value, translatedValue))
                .append(");");
    }

    @Override
    public void caseThrowStmt(ThrowStmt stmt) {
        stringBuilder.append("$Exception := ")
                .append(translateValue(stmt.getOp()))
                .append(";\n")
                .append("return;");
    }

    @Override
    public void caseIfStmt(IfStmt stmt) {

        final BasicBlock successorBasicBlock = basicBlock.getSuccessorBasicBlock(stmt.getTarget());

        stringBuilder.append("if (")
                .append(translateValue(stmt.getCondition()))
                .append(") {\n")
                .append(StringUtils.indent("goto " + successorBasicBlock.getLabel() + ";"))
                .append("\n")
                .append("}");
    }

    @Override
    public void caseReturnStmt(ReturnStmt stmt) {
        stringBuilder.append("r := ")
                .append(translateValue(stmt.getOp()))
                .append(";\n")
                .append("return;");
    }

    @Override
    public void caseIdentityStmt(IdentityStmt stmt) {

        final String translatedLeftOp = translateValue(stmt.getLeftOp());
        final Value rightOp = stmt.getRightOp();

        if (rightOp instanceof ThisRef) {
            stringBuilder.append(translatedLeftOp)
                    .append(" := ")
                    .append("$this")
                    .append(";");

            return;
        }

        if (rightOp instanceof CaughtExceptionRef) {
            stringBuilder.append(translatedLeftOp)
                    .append(" := ")
                    .append("$Exception")
                    .append(";");

            return;
        }


        if (rightOp instanceof ParameterRef) {
            // Do nothing: this is translated as a parameter.
            return;
        }

        throw new UnsupportedOperationException("Can't handle this identity statement: " + stmt
                + " with rightOp class:" + rightOp.getClass().getSimpleName());
    }

    @Override
    public void caseReturnVoidStmt(ReturnVoidStmt stmt) {
        stringBuilder.append("return;");
    }

    @Override
    public void caseGotoStmt(GotoStmt stmt) {

        final BasicBlock successorBasicBlock = basicBlock.getSuccessorBasicBlock(stmt.getTarget());

        stringBuilder.append("goto ")
                .append(successorBasicBlock.getLabel())
                .append(";");
    }

    @Override
    public void caseInvokeStmt(InvokeStmt stmt) {

        final String returnVariableName = method.getGeneratedReturnVariableName(stmt);

        final InvokeExpr invokeExpr = stmt.getInvokeExpr();

        stringBuilder.append("call ");

        if (returnVariableName != null) {
            stringBuilder.append(returnVariableName)
                    .append(" := ");
        }

        stringBuilder.append(translateValue(invokeExpr))
                .append(";\n")
                .append("if ($Exception != null) {\n")
                .append(StringUtils.indent("return;"))
                .append("\n")
                .append("}");
    }

    @Override
    public void defaultCase(Object obj) {
        throw new UnsupportedOperationException("Unsupported statement of type "
                + obj.getClass().getName() + " in basic block " + basicBlock.getLabel());
    }

    public String getTranslation() {
        return stringBuilder.toString();
    }
}


