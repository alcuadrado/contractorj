package examples;

public class ControladorLanzaExcepcion {

  public boolean hayCaja;
  public boolean cajaCerrada;
  public boolean cajaVacia;

  public ControladorLanzaExcepcion() {
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

    if (peso > 1000) {
      throw new RuntimeException("Carga demasiado pesada");
    }
  }

  public boolean vaciar_pre() {
    return hayCaja && !cajaCerrada && !cajaVacia;
  }

  public void vaciar() {
    cajaVacia = true;
  }

  public boolean cerrar_pre() {
    return hayCaja && !cajaCerrada && !cajaVacia;
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
