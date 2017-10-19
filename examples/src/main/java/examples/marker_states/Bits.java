package examples.marker_states;

/** Created by Usuario on 04/07/2017. */
public class Bits {

  static ByteOrder byteOrder() {
    //if (byteOrder == null)
    //    throw new Error("Unknown byte order");
    //return byteOrder;
    return ByteOrder.BIG_ENDIAN; // ESTO ESTA MAL HAY QUE HACERLO NO DETERMINISTICO CON LITTLE
  }
}
