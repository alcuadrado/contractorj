package examples.translation;

public class RealTest {

  public void testDouble() {
    double x = 1;
  }

  public void testDouble(double x) {
    x = 40.5 + x;

    x = 1 + x;

    testDouble(60.9);
    testDouble(60);
  }

  public void testRealArray() {
    double[] array = {1.5, 2.4, 3.3, 4.1};

    array[0] = array[2] + 3.14;
  }

  public void testRestaFloatIntFloat(float a, int b, float c) {
    a = b - c; // en boogie b deberia ser casteado a Real
  }

  public void testDoubleParam(double x) {
    instDouble = x;
  }

  public void testDoubleParam2(int x) {
    instDouble = x;
  }

  public void testRestaDoubleIntDouble(double a, int b, double c) {
    a = b - c; // en boogie b deberia ser casteado a Real
  }

  public void testRestaDoubleIntInt(double a, int b, int c) {
    a = b - c; // en boogie b y c deberian ser casteados a real
  }

  public void testRestaDoubleDoubleDouble(double a, double b, double c) {
    a = b - c; // no deberia pasar nada mas que sumar las constantes.
  }

  public void testRestaIntIntDouble(int a, int b, double c) {
    a = b - (int) c; // en boogie b deberia ser casteado a Real
  }

  public void testRestaIntDoubleDouble(int a, double b, double c) {
    a = (int) (b - c); // en boogie b y c deberian ser casteados a real
  }

  public void testRestaIntIntInt(int a, int b, int c) {
    a = b - c; // no deberia pasar nada mas que sumar las constantes.
  }

  public void testRestaDoubleDoubleInt(double a, double b) {
    a = b - unMetodoQueDevuelveInt();
  }

  int unMetodoQueDevuelveInt() {
    return (int) Math.random();
  }

  public void testDoubleMult(double a, int b, double c) {
    a = b * c;
  }

  public void testEqual(double a, double c, int b) {
    boolean x;
    if (a == b) x = true;
  }

  public void testNotEqual(double a, double c, int b) {
    boolean x;
    if (a != b) x = true;
  }

  public void testGreater(double a, double c, int b) {
    boolean x;
    if (a > b) x = true;
  }

  public void testLessThan(double a, double c, int b) {
    boolean x;
    if (a < b) x = true;
  }

  public void testModuloDouble(double a, double b) {
    double x = a % b;
  }

  public void testModuloDoubleInt(double a, int b) {
    double x = a % b;
  }

  public void testModuloInt(int a, int b) {
    int x = a % b;
  }

  double instDouble = 0;
}
