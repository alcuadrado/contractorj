package examples.translation;

public class BitTest {
  public void negationBit(int x) {
    int z = ~x;
  }

  public void xorBit(int x, int y) {
    int z = x ^ y;
  }

  public void andBit(int x, int y) {
    int z = x & y;
  }

  public void orBit(int x) {
    int z = x | x;
  }

  public void shiftLeft(int x, int y) {
    int z = x << y;
  }

  public void rightLeft(int x, int y) {
    int z = x >> y;
  }

  public void right2Left(int x, int y) {
    int z = x >>> y;
  }
}
