package jbct.soot;

import jbct.exceptions.UnsupportedTypeException;
import soot.*;

public class TypeTranslator {

  public static String translate(Type type) {

    if (type == IntType.v()
        || type == ShortType.v()
        || type == ByteType.v()
        || type == LongType.v()
        || type == CharType.v()) {
      return "int";
    }

    if (type == BooleanType.v()) {
      return "bool";
    }

    if (type == DoubleType.v() || type == FloatType.v()) {
      return "Real";
    }

    if (type instanceof RefType) {
      return "Ref";
    }

    if (type instanceof ArrayType) {
      return "Ref";
    }

    if (type instanceof NullType) {
      return "Ref";
    }

    throw new UnsupportedTypeException(type);
  }
}
