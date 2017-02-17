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

  public boolean TomarContainer_pre() {
    return !hayContainer;
  }

  public void TomarContainer() {
    hayContainer = true;
    containerCerrado = false;
    containerVacio = true;
  }

  public boolean Cargar_pre() {
    return hayContainer && !containerCerrado;
  }

  public void Cargar(int peso) {
    containerVacio = false;
  }

  public boolean Vaciar_pre() {
    return hayContainer && !containerCerrado && !containerVacio;
  }

  public void Vaciar() {
    containerVacio = true;
  }

  public boolean Cerrar_pre() {
    return hayContainer && !containerCerrado && !containerVacio;
  }

  public void Cerrar() {
    containerCerrado = true;
  }

  public boolean Abrir_pre() {
    return hayContainer && containerCerrado;
  }

  public void Abrir() {
    containerCerrado = false;
  }

  public boolean Despachar_pre() {
    return hayContainer && containerCerrado;
  }

  public void Despachar() {
    hayContainer = false;
  }
}
