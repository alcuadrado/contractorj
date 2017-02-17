package examples;

public class Grua {

  public boolean hayContainer;
  public boolean containerCerrado;
  public boolean containerVacio;

  public Grua() {
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
