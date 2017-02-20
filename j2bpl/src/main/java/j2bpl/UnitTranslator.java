package j2bpl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import soot.BooleanType;
import soot.SootField;
import soot.Type;
import soot.Value;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.DivExpr;
import soot.jimple.GotoStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.ParameterRef;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.ThisRef;
import soot.jimple.ThrowStmt;

public class UnitTranslator extends AbstractStmtSwitch {

  private final StringBuilder stringBuilder = new StringBuilder();

  private final LocalMethod method;

  private final BasicBlock basicBlock;

  private final List<String> translation = new LinkedList<>();

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

    final StringBuilder stringBuilder = new StringBuilder();

    if (rightOp instanceof NewExpr || rightOp instanceof InvokeExpr || rightOp instanceof DivExpr) {
      stringBuilder.append("call ");
    }

    final String rightOptTranslation;

    // We need a special case for booleans because they are 0 and 1 in jimple, but that doesn't
    // type check in boogie.

    if (leftOp.getType() == BooleanType.v() && rightOp instanceof IntConstant) {
      final int value = ((IntConstant) rightOp).value;
      rightOptTranslation = value == 0 ? "false" : "true";
    } else {
      rightOptTranslation = translateValue(rightOp);
    }

    final String translatedLeftOp = translateValue(leftOp);

    stringBuilder.append(translatedLeftOp).append(" := ").append(rightOptTranslation).append(";");

    translation.add(stringBuilder.toString());

    if (rightOp instanceof InvokeExpr || rightOp instanceof DivExpr) {
      translation.add("if ($Exception != null) {");
      translation.add(StringUtils.indent("return;"));
      translation.add("}");
    }
  }

  private void translateArrayCreation(Value leftOp, NewArrayExpr newArrayExpr) {

    final String translatedRef = translateValue(leftOp);
    final String translatedSize = translateValue(newArrayExpr.getSize());

    translation.add("call " + translatedRef + " := Alloc();");
    translation.add("assume $ArrayLength(" + translatedRef + ") == " + translatedSize + ";");
  }

  private void translateAssignmentToArray(ArrayRef arrayRef, Value value) {

    final String translatedValue = translateValue(value);
    final String translatedIndex = translateValue(arrayRef.getIndex());
    final String translatedRef = translateValue(arrayRef.getBase());

    translateTransformationToUnion(arrayRef.getType(), value, translatedValue);
    final String toUnion = getToUnion(arrayRef.getType(), value, translatedValue);

    translation.add("if ( " + translatedRef + " == null) { call $Exception := Alloc(); return; }");
    translation.add(
        "$ArrayContents := $ArrayContents["
            + translatedRef
            + " := $ArrayContents["
            + translatedRef
            + "]["
            + translatedIndex
            + " := "
            + toUnion
            + "]];");
  }

  private String translateValue(Value value) {

    final ValueTranslator translator = new ValueTranslator();
    value.apply(translator);
    return translator.getTranslation();
  }

  private void translateTransformationToUnion(Type type, Value value, String translatedValue) {

    final String translateType = TypeTranslator.translate(type);

    if (translateType.equals("int")) {

      translation.add(
          "assume Union2Int(Int2Union(" + translatedValue + ")) == " + translatedValue + ";");
      return;
    }

    if (type == BooleanType.v()) {

      if (value instanceof IntConstant) {
        final boolean booleanValue = ((IntConstant) value).value != 0;
        translatedValue = String.valueOf(booleanValue);
      }

      translation.add(
          "assume Union2Bool(Bool2Union(" + translatedValue + ")) == " + translatedValue + ";");
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

    if (type == BooleanType.v()) {

      if (value instanceof IntConstant) {

        final boolean booleanValue = ((IntConstant) value).value != 0;
        translatedValue = String.valueOf(booleanValue);
      }

      return "Bool2Union(" + translatedValue + ")";
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

    translation.add(
        "$Heap := Write($Heap, "
            + translatedRef
            + ", "
            + instanceField.getTranslatedName()
            + ", "
            + getToUnion(type, value, translatedValue)
            + ");");
  }

  @Override
  public void caseThrowStmt(ThrowStmt stmt) {

    final String translatedValue = translateValue(stmt.getOp());

    translation.add("$Exception := " + translatedValue + ";");
    translation.add("return;");
  }

  @Override
  public void caseIfStmt(IfStmt stmt) {

    final BasicBlock successorBasicBlock = basicBlock.getSuccessorBasicBlock(stmt.getTarget());
    final String translatedValue = translateValue(stmt.getCondition());

    translation.add("if (" + translatedValue + ") {");
    translation.add(StringUtils.indent("goto " + successorBasicBlock.getLabel() + ";"));
    translation.add("}");
  }

  @Override
  public void caseReturnStmt(ReturnStmt stmt) {

    final Value value = stmt.getOp();

    if (method.getTranslatedReturnType().equals("bool") && value instanceof IntConstant) {
      final IntConstant intValue = (IntConstant) value;
      translation.add("r := " + (intValue.value == 0 ? "false" : "true") + ";");
    } else {
      final String translatedValue = translateValue(value);
      translation.add("r := " + translatedValue + ";");
    }

    translation.add("return;");
  }

  @Override
  public void caseIdentityStmt(IdentityStmt stmt) {

    final String translatedLeftOp = translateValue(stmt.getLeftOp());
    final Value rightOp = stmt.getRightOp();

    if (rightOp instanceof ThisRef) {

      translation.add(translatedLeftOp + " := $this;");
      return;
    }

    if (rightOp instanceof CaughtExceptionRef) {

      translation.add(translatedLeftOp + " := $Exception;");
      return;
    }

    if (rightOp instanceof ParameterRef) {
      // Do nothing: this is translated as a parameter.
      return;
    }

    throw new UnsupportedOperationException(
        "Can't handle this identity statement: "
            + stmt
            + " with rightOp class:"
            + rightOp.getClass().getSimpleName());
  }

  @Override
  public void caseReturnVoidStmt(ReturnVoidStmt stmt) {

    translation.add("return;");
  }

  @Override
  public void caseGotoStmt(GotoStmt stmt) {

    final BasicBlock successorBasicBlock = basicBlock.getSuccessorBasicBlock(stmt.getTarget());
    translation.add("goto " + successorBasicBlock.getLabel() + ";");
  }

  @Override
  public void caseInvokeStmt(InvokeStmt stmt) {

    final Optional<String> returnVariableName = method.getGeneratedReturnVariableName(stmt);

    final InvokeExpr invokeExpr = stmt.getInvokeExpr();

    String callInstruction = "call ";

    if (returnVariableName.isPresent()) {
      callInstruction += returnVariableName.get() + " := ";
    }

    callInstruction += translateValue(invokeExpr) + ";";

    translation.add(callInstruction);
    translation.add("if ($Exception != null) {");
    translation.add(StringUtils.indent("return;"));
    translation.add("}");
  }

  @Override
  public void defaultCase(Object obj) {

    throw new UnsupportedOperationException(
        "Unsupported statement of type "
            + obj.getClass().getName()
            + " in basic block "
            + basicBlock.getLabel());
  }

  public List<String> getTranslation() {

    return translation;
  }
}
