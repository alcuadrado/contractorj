package jbct.soot;

import soot.ArrayType;
import soot.BooleanType;
import soot.ByteType;
import soot.IntType;
import soot.LongType;
import soot.RefType;
import soot.ShortType;
import soot.Type;

import jbct.exceptions.UnsupportedTypeException;

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

    throw new UnsupportedTypeException(type);
  }
}
