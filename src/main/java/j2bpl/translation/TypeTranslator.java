package j2bpl.translation;

import soot.*;

public class TypeTranslator {

    public static String translate(Type type) {
        if (type == IntType.v()
                || type == ShortType.v()
                || type == ByteType.v()
                || type == LongType.v()) {
            return "int";
        }

        if (type == BooleanType.v()) {
            return "bool";
        }

        if (type instanceof RefType) {
            return "Ref";
        }

        if (type instanceof ArrayType) {
            return "Ref";
        }

        throw new UnsupportedOperationException("Unsupported type: " + type.toString());
    }

}
