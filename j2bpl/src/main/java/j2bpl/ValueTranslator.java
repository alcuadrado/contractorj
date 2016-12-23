package j2bpl;

import com.google.common.base.Joiner;

import soot.BooleanType;
import soot.Local;
import soot.SootField;
import soot.SootMethod;
import soot.Value;
import soot.jimple.AbstractJimpleValueSwitch;
import soot.jimple.AddExpr;
import soot.jimple.AndExpr;
import soot.jimple.ArrayRef;
import soot.jimple.CastExpr;
import soot.jimple.ClassConstant;
import soot.jimple.DivExpr;
import soot.jimple.DynamicInvokeExpr;
import soot.jimple.EqExpr;
import soot.jimple.GeExpr;
import soot.jimple.GtExpr;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.IntConstant;
import soot.jimple.InterfaceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.LeExpr;
import soot.jimple.LengthExpr;
import soot.jimple.LtExpr;
import soot.jimple.MulExpr;
import soot.jimple.NeExpr;
import soot.jimple.NewExpr;
import soot.jimple.NullConstant;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.StringConstant;
import soot.jimple.SubExpr;
import soot.jimple.VirtualInvokeExpr;

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
    public void caseCastExpr(CastExpr v) {

        v.getOp().apply(this);
    }

    @Override
    public void caseAndExpr(final AndExpr v) {

        final String op1Translated = translateValue(v.getOp1());
        final String op2Translated = translateValue(v.getOp2());

        stringBuilder.append(op1Translated)
                .append(" && ")
                .append(op2Translated);
    }

    @Override
    public void defaultCase(Object v) {

        throw new UnsupportedOperationException("Unsupported value type " + v.getClass().getName());
    }

    @Override
    public void caseStringConstant(StringConstant v) {
        stringBuilder.append(getStringConstantVarName(v));
    }

    @Override
    public void caseSpecialInvokeExpr(SpecialInvokeExpr v) {

        translateInvokeExpr(v);
    }

    @Override
    public void caseInterfaceInvokeExpr(InterfaceInvokeExpr v) {

        final Class methodClass = Class.create(v.getMethod().getDeclaringClass());
        final Method theMethod = Method.create(methodClass, v.getMethod());

        if (theMethod instanceof ExternalMethod) {
            if (((ExternalMethod) theMethod).isHardCoded()) {
                translateInvokeExpr(v);
                return;
            }
        }

        super.caseInterfaceInvokeExpr(v);
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

    public static String getStringConstantVarName(StringConstant stringConstant) {
        return "stringConstant_" + stringConstant.value.hashCode()
                + "_" + stringConstant.value.replaceAll("[^a-zA-Z0-9]", "_");
    }

}