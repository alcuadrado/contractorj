package j2bpl;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

import soot.SootMethod;
import soot.Type;
import soot.VoidType;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ExternalMethod extends Method {



    private static final Set<String> hardCodedMethodsTranslatedNames = Sets.newHashSet(
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

            "java.util.Arrays.copyOf$Ref$int"
    );

    private Set<String> methodsWithHardcodedBooleanReturnType = Sets.newHashSet(
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
            "java.util.ArrayList#add$java.lang.Object",
            "java.util.ArrayList#remove$java.lang.Object"
    );

    public ExternalMethod(Class theClass, SootMethod sootMethod) {

        super(theClass, sootMethod);
    }

    public boolean isHardCoded() {
        final String translatedName = getTranslatedName();
        return hardCodedMethodsTranslatedNames.contains(translatedName)
                || methodsWithHardcodedBooleanReturnType.contains(translatedName);
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

        final StringBuilder stringBuilder = new StringBuilder()
                .append("procedure ")
                .append("{:extern} ")
                .append(getTranslatedName())
                .append("(")
                .append(Joiner.on(", ").join(getTranslatedParametersList()))
                .append(")");

        if (sootMethod.getReturnType() != VoidType.v()) {
            stringBuilder.append(" returns (r : ")
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
