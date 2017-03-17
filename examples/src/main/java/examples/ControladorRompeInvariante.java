package examples;

public class ControladorRompeInvariante {

  public boolean hayCaja;
  public boolean cajaCerrada;
  public boolean cajaVacia;

  public ControladorRompeInvariante() {
    hayCaja = false;
  }

  public boolean inv() {
    return !hayCaja || !(cajaVacia && cajaCerrada);
  }

  public boolean tomarCaja_pre() {
    return !hayCaja;
  }

  public void tomarCaja() {
    hayCaja = true;
    cajaCerrada = false;
    cajaVacia = true;
  }

  public boolean cargar_pre() {
    return hayCaja && !cajaCerrada;
  }

  public void cargar(int peso) {
    cajaVacia = false;
  }

  public boolean vaciar_pre() {
    return hayCaja && !cajaCerrada && !cajaVacia;
  }

  public void vaciar() {
    cajaVacia = true;
  }

  public boolean cerrar_pre() {
    return hayCaja && !cajaCerrada;
  }

  public void cerrar() {
    cajaCerrada = true;
  }

  public boolean abrir_pre() {
    return hayCaja && cajaCerrada;
  }

  public void abrir() {
    cajaCerrada = false;
  }

  public boolean despachar_pre() {
    return hayCaja && cajaCerrada;
  }

  public void despachar() {
    hayCaja = false;
  }
}
