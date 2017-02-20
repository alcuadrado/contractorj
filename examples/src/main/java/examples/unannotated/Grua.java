package examples.unannotated;

public class Grua {

  public boolean hayContainer;
  public boolean containerCerrado;
  public boolean containerVacio;

  public Grua() {
    hayContainer = false;
  }

  public void tomarContainer() {

    if (hayContainer) {
      throw new RuntimeException("Ya hay container");
    }

    hayContainer = true;
    containerCerrado = false;
    containerVacio = true;
  }

  public void cargar(int peso) {

    if (!hayContainer) {
      throw new RuntimeException("No hay container");
    }

    if (containerCerrado) {
      throw new RuntimeException("Container cerrado");
    }

    containerVacio = false;
  }

  public void vaciar() {

    if (!hayContainer) {
      throw new RuntimeException("No hay container");
    }

    if (containerCerrado) {
      throw new RuntimeException("Container cerrado");
    }

    if (containerVacio) {
      throw new RuntimeException("Container vacio");
    }

    containerVacio = true;
  }

  public void cerrar() {

    if (!hayContainer) {
      throw new RuntimeException("No hay container");
    }

    if (containerCerrado) {
      throw new RuntimeException("Container cerrado");
    }

    if (containerVacio) {
      throw new RuntimeException("Container vacio");
    }

    containerCerrado = true;
  }

  public void abrir() {

    if (!hayContainer) {
      throw new RuntimeException("No hay container");
    }

    if (!containerCerrado) {
      throw new RuntimeException("Container abierto");
    }

    containerCerrado = false;
  }

  public void despachar() {

    if (!hayContainer) {
      throw new RuntimeException("No hay container");
    }

    if (!containerCerrado) {
      throw new RuntimeException("Container abierto");
    }

    hayContainer = false;
  }
}
