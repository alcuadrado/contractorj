package examples;

public class GruaLanzaExcepcion {

  public boolean hayContainer;
  public boolean containerCerrado;
  public boolean containerVacio;

  public GruaLanzaExcepcion() {
    hayContainer = false;
  }

  public boolean inv() {
    return !hayContainer || !(containerVacio && containerCerrado);
  }

  public boolean tomarContainer_pre() {
    return !hayContainer;
  }

  public void tomarContainer() {
    hayContainer = true;
    containerCerrado = false;
    containerVacio = true;
  }

  public boolean cargar_pre() {
    return hayContainer && !containerCerrado;
  }

  public void cargar(int peso) {
    containerVacio = false;

    if (peso > 1000) {
      throw new RuntimeException("Carga demasiado pesada");
    }
  }

  public boolean vaciar_pre() {
    return hayContainer && !containerCerrado && !containerVacio;
  }

  public void vaciar() {
    containerVacio = true;
  }

  public boolean cerrar_pre() {
    return hayContainer && !containerCerrado && !containerVacio;
  }

  public void cerrar() {
    containerCerrado = true;
  }

  public boolean abrir_pre() {
    return hayContainer && containerCerrado;
  }

  public void abrir() {
    containerCerrado = false;
  }

  public boolean despachar_pre() {
    return hayContainer && containerCerrado;
  }

  public void despachar() {
    hayContainer = false;
  }
}
