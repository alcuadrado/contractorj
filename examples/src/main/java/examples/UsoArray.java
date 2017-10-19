package examples;

public class UsoArray {

  public int[] arreglo;
  public int index;
  public int size;
  public int j;

  UsoArray() {
    j = 0;
    index = 0;
    size = 4;
    arreglo = new int[size];
  }

  public void add(int i) {
    arreglo[index] = i;
    index++;
  }

  public void conCinco() {
    j++;
  }

  public void sinCinco() {
    j--;
  }

  public boolean add_pre() {
    return index + 1 < size;
  }

  public boolean conCinco_pre() {
    boolean result = false;

    for (int i = 0; i < index; i++) {
      if (arreglo[i] == 5) return true;
    }

    return result;
  }

  public boolean sinCinco_pre() {
    return !conCinco_pre();
  }

  public boolean inv() {
    return arreglo != null && size == 4 && index >= 0 && index < size;
  }
}
