package j2bpl.translation;

import com.google.common.base.Joiner;
import soot.*;
import soot.jimple.*;

import java.util.ArrayList;
import java.util.List;

public class ValueTranslator extends AbstractJimpleValueSwitch {

    private final StringBuilder stringBuilder = new StringBuilder();

    @Override
    public void caseLocal(Local v) {
        stringBuilder.append(v.getName());
    }

    @Override
    public void caseIntConstant(IntConstant v) {
        stringBuilder.append(v.value);
    }

    @Override
    public void caseAddExpr(AddExpr v) {
        v.getOp1().apply(this);
        stringBuilder.append(" + ");
        v.getOp2().apply(this);
    }

    @Override
    public void caseSubExpr(SubExpr v) {
        v.getOp1().apply(this);
        stringBuilder.append(" - ");
        v.getOp2().apply(this);
    }

    @Override
    public void caseMulExpr(MulExpr v) {
        v.getOp1().apply(this);
        stringBuilder.append(" * ");
        v.getOp2().apply(this);
    }

    @Override
    public void caseDivExpr(DivExpr v) {
        stringBuilder.append("division(");
        v.getOp1().apply(this);
        stringBuilder.append(", ");
        v.getOp2().apply(this);
        stringBuilder.append(")");
    }

    @Override
    public void caseNeExpr(NeExpr v) {

        if (v.getOp2().getType() == BooleanType.v() && v.getOp1() instanceof IntConstant) {
            final int value = ((IntConstant) v.getOp1()).value;
            stringBuilder.append(value == 0 ? "false" : "true");
        } else {
            v.getOp1().apply(this);
        }

        stringBuilder.append(" != ");

        if (v.getOp1().getType() == BooleanType.v() && v.getOp2() instanceof IntConstant) {
            final int value = ((IntConstant) v.getOp2()).value;
            stringBuilder.append(value == 0 ? "false" : "true");
        } else {
            v.getOp2().apply(this);
        }
    }

    @Override
    public void caseGtExpr(GtExpr v) {
        v.getOp1().apply(this);
        stringBuilder.append(" > ");
        v.getOp2().apply(this);
    }

    @Override
    public void caseGeExpr(GeExpr v) {
        v.getOp1().apply(this);
        stringBuilder.append(" >= ");
        v.getOp2().apply(this);
    }

    @Override
    public void caseLeExpr(LeExpr v) {
        v.getOp1().apply(this);
        stringBuilder.append(" <= ");
        v.getOp2().apply(this);
    }

    @Override
    public void caseEqExpr(EqExpr v) {
        if (v.getOp2().getType() == BooleanType.v() && v.getOp1() instanceof IntConstant) {
            final int value = ((IntConstant) v.getOp1()).value;
            stringBuilder.append(value == 0 ? "false" : "true");
        } else {
            v.getOp1().apply(this);
        }

        stringBuilder.append(" == ");

        if (v.getOp1().getType() == BooleanType.v() && v.getOp2() instanceof IntConstant) {
            final int value = ((IntConstant) v.getOp2()).value;
            stringBuilder.append(value == 0 ? "false" : "true");
        } else {
            v.getOp2().apply(this);
        }
    }

    @Override
    public void caseLtExpr(LtExpr v) {
        v.getOp1().apply(this);
        stringBuilder.append(" < ");
        v.getOp2().apply(this);
    }

    @Override
    public void caseVirtualInvokeExpr(VirtualInvokeExpr v) {
        translateInvokeExpr(v);
    }

    @Override
    public void caseStaticInvokeExpr(StaticInvokeExpr v) {
        translateInvokeExpr(v);
    }

    @Override
    public void caseNewExpr(NewExpr v) {
        stringBuilder.append("Alloc()");
    }

    @Override
    public void caseNullConstant(NullConstant v) {
        stringBuilder.append("null");
    }

    @Override
    public void caseStaticFieldRef(StaticFieldRef v) {
        final StaticField staticField = new StaticField(v.getField());
        stringBuilder.append(staticField.getTranslatedName());
    }

    @Override
    public void caseClassConstant(ClassConstant v) {
        final String qualifiedClassName = v.getValue().replace("/", ".");
        final String escaped = StringUtils.scapeIllegalIdentifierCharacters(qualifiedClassName);
        stringBuilder.append(escaped);
    }

    @Override
    public void caseArrayRef(ArrayRef v) {
        final String translatedType = TypeTranslator.translate(v.getType());
        final String translatedRef = translateValue(v.getBase());
        final String translatedIndex = translateValue(v.getIndex());

        final String arrayContent = "$ArrayContents[" + translatedRef + "][" + translatedIndex + "]";

        if (translatedType.equals("int")) {
            stringBuilder.append("Union2Int(")
                    .append(arrayContent)
                    .append(")");

            return;
        }

        if (translatedType.equals("bool")) {
            stringBuilder.append("Union2Bool(")
                    .append(arrayContent)
                    .append(")");

            return;
        }

        if (translatedType.equals("Ref")) {
            stringBuilder.append(arrayContent);
            return;
        }

        throw new UnsupportedOperationException("Unsupported array access of type " + v.getType());
    }

    @Override
    public void caseInstanceFieldRef(InstanceFieldRef v) {

        final SootField sootField = v.getField();
        final InstanceField instanceField = new InstanceField(sootField);
        final String translatedType = TypeTranslator.translate(sootField.getType());
        final String refTranslation = translateValue(v.getBase());


        if (translatedType.equals("int")) {

            stringBuilder.append("Union2Int(Read($Heap, ")
                    .append(refTranslation)
                    .append(", ")
                    .append(instanceField.getTranslatedName())
                    .append("))");

            return;
        }

        if (translatedType.equals("bool")) {

            stringBuilder.append("Union2Bool(Read($Heap, ")
                    .append(refTranslation)
                    .append(", ")
                    .append(instanceField.getTranslatedName())
                    .append("))");

            return;
        }

        if (translatedType.equals("Ref")) {

            stringBuilder.append("Read($Heap, ")
                    .append(refTranslation)
                    .append(", ")
                    .append(instanceField.getTranslatedName())
                    .append(")");

            return;
        }

        throw new UnsupportedOperationException("Unsupported instance field access: " + v);
    }

    @Override
    public void caseLengthExpr(LengthExpr v) {
        final String refTranslation = translateValue(v.getOp());

        stringBuilder.append("$ArrayLength(")
                .append(refTranslation)
                .append(")");
    }

    @Override
    public void defaultCase(Object v) {
        throw new UnsupportedOperationException("Unsupported value type " + v.getClass().getName());
    }


    @Override
    public void caseSpecialInvokeExpr(SpecialInvokeExpr v) {
        translateInvokeExpr(v);
    }

    private void translateInvokeExpr(InvokeExpr invokeExpr) {

        if (invokeExpr instanceof DynamicInvokeExpr) {
            throw new RuntimeException("invokedynamic not supported.");
        }

        final List<String> translatedArgumentsList = new ArrayList<>();

        if (invokeExpr instanceof InstanceInvokeExpr) {
            final Value instance = ((InstanceInvokeExpr) invokeExpr).getBase();
            translatedArgumentsList.add(translateValue(instance));
        }

        for (final Value argument : invokeExpr.getArgs()) {
            translatedArgumentsList.add(translateValue(argument));
        }

        final SootMethod sootMethod = invokeExpr.getMethod();
        final Method calledMethod = J2BplTransformer.getInstance().getMethod(sootMethod);

        stringBuilder.append(calledMethod.getTranslatedName())
                .append("(")
                .append(Joiner.on(", ").join(translatedArgumentsList))
                .append(")");
    }

    public String getTranslation() {
        return stringBuilder.toString();
    }

    private String translateValue(Value value) {
        final ValueTranslator translator = new ValueTranslator();
        value.apply(translator);
        return translator.getTranslation();
    }

}