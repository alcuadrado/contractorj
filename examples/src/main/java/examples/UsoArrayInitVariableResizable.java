package examples;

public class UsoArrayInitVariableResizable {

  public int[] arreglo;
  public int index;
  public int j;
  public int capacity;

  UsoArrayInitVariableResizable(int c) {
    j = 0;
    index = 0;
    capacity = c;
    arreglo = new int[capacity];
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
    return index + 1 < capacity;
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
    return arreglo != null && index >= 0 && index < capacity;
  }
}
