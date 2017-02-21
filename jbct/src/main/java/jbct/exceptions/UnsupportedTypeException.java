package jbct.exceptions;

import soot.Type;

public class UnsupportedTypeException extends RuntimeException {

  public UnsupportedTypeException(Type type) {
    super("Unsupported type " + type.toString());
  }

}
