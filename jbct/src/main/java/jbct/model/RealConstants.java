package jbct.model;

import soot.*;
import soot.jimple.*;

import java.util.HashSet;
import java.util.Iterator;

public class RealConstants {
    private static RealConstants ourInstance = new RealConstants();

    public static RealConstants getInstance() {
        return ourInstance;
    }

    private HashSet<RealConstant> realConstants;

    private RealConstants() {
       realConstants = new HashSet<RealConstant>();
    }

    private void addRealConstant(RealConstant c){
        realConstants.add(c);
    }

    // returns a string that has a declaration for each Real constant found in Java.
    public String realConstantDefinitions(){
        StringBuilder sb = new StringBuilder();

        for (RealConstant rc : realConstants){
            sb.append("const unique ");
            sb.append(getRealConstantName(rc));
            sb.append(" : Real;");
            sb.append(System.lineSeparator());
        }

        return sb.toString();
    }

    // this could be cached.
    // returns the name in boogie of a constant found in java
    public String getRealConstantName(RealConstant c){

        if (!realConstants.contains(c)){
            throw new RuntimeException("Asked for not found constant.");
        }

        StringBuilder sb = new StringBuilder();

        sb.append("$real_literal_");

        double value;
        if (c instanceof DoubleConstant)
            value = ((DoubleConstant) c).value;
        else if (c instanceof FloatConstant)
            value =  ((FloatConstant)c).value;
        else
            throw new RuntimeException("Not expected type");

        String sValue = String.valueOf(value);

        if (sValue.endsWith(".0"))
            sValue = sValue.replace(".0", "");
        else
            sValue = sValue.replace(".", "$");

        sb.append(sValue);
        sb.append("_0");

        return sb.toString();
    }
    
      // This is done because we will create an unique Real value for each Double constant found.
    public void findRealConstantsInMethods(JimpleBody jimpleBody){
        for (Unit unit : jimpleBody.getUnits()) {
            Iterator<ValueBox> it = unit.getUseBoxes().iterator();

            while (it.hasNext()){
                ValueBox vb = it.next();
                Value value = vb.getValue();

                if (value instanceof Constant &&
                        (value.getType() == DoubleType.v() ||value.getType() == FloatType.v())){
                    RealConstants.getInstance().addRealConstant((RealConstant)value);
                }
            }
        }
    }
}
