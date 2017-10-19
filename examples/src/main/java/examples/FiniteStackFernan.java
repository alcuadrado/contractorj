package examples;

/* ESTADISTICAS SIN LOS QUERIES GLOBALES

Total running time: 1.5922667m
Time running queries: 11.736716m
Total number of queries: 238
Types of queries:
	NecessarilyEnabledActionQuery
		Number of queries: 80
		Time running queries: 3.7005668m
		Average running time: 2.775s

	ExceptionBreaksInvariantQuery
		Number of queries: 16
		Time running queries: 39.608s
		Average running time: 2.475s

	TransitionBreaksInvariantQuery
		Number of queries: 16
		Time running queries: 40.355s
		Average running time: 2.522s

	NotThrowingTransitionQuery
		Number of queries: 23
		Time running queries: 1.2403667m
		Average running time: 3.235s

	NecessarilyDisabledActionQuery
		Number of queries: 80
		Time running queries: 4.4448833m
		Average running time: 3.333s

	ThrowingTransitionQuery
		Number of queries: 23
		Time running queries: 1.0181834m
		Average running time: 2.656s

 */

/* ESTADISTICA CON LOS QUERIES GLOBALES

Total running time: 1.3779334m
Time running queries: 9.937817m
Total number of queries: 208
Types of queries:
	NecessarilyEnabledActionQuery
		Number of queries: 35
		Time running queries: 1.55715m
		Average running time: 2.669s

	ThrowingTransitionQuery
		Number of queries: 23
		Time running queries: 58.507s
		Average running time: 2.543s

	NotThrowingTransitionQuery
		Number of queries: 23
		Time running queries: 1.19325m
		Average running time: 3.112s

	GlobalNecessarilyEnabledActionQuery
		Number of queries: 30
		Time running queries: 1.4647167m
		Average running time: 2.929s

	NecessarilyDisabledActionQuery
		Number of queries: 35
		Time running queries: 1.7949667m
		Average running time: 3.077s

	ExceptionBreaksInvariantQuery
		Number of queries: 16
		Time running queries: 38.29s
		Average running time: 2.393s

	GlobalNecessarilyDisabledActionQuery
		Number of queries: 30
		Time running queries: 1.65855m
		Average running time: 3.317s

	TransitionBreaksInvariantQuery
		Number of queries: 16
		Time running queries: 39.354s
		Average running time: 2.459s

 */

public class FiniteStackFernan {
  private final int capacity = 5;
  private int size;
  private String[] data;

  public boolean inv() {
    return size >= 0 && size <= capacity && data.length == capacity;
  }

  public FiniteStackFernan() {
    size = 0;
    data = new String[capacity];
  }

  public int Count() {
    return size;
  }

  public boolean Push_pre() {
    return this.Count() < capacity;
  }

  public boolean Push_pre(String item) {
    return item != null;
  }

  public void Push(String item) {
    if (item == null) throw new RuntimeException();

    if (item == "") // ¿Por que no esta en el pre?
    throw new RuntimeException();

    data[size++] = item;
  }

  public boolean Pop_pre() {
    return this.Count() > 0;
  }

  public String Pop() {
    --size;
    return data[size];
  }

  public boolean Contains_pre() {
    return true;
  }

  public boolean Contains(String item) {
    boolean result = false;
    for (int i = 0; !result && i < size; i++) {
      result = data[i] == item;
    }
    return result;
  }

  public boolean Clear_pre() {
    return true;
  }

  public void Clear() {
    size = 0;
  }

  public boolean Peek_pre() {
    return this.Count() > 0;
  }

  public String Peek() {
    return data[size - 1];
  }
}
