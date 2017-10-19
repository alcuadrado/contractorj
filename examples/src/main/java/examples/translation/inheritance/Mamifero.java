package examples.translation.inheritance;

public class Mamifero {
  public int oxigeno;
  public int comida;

  public Mamifero() {
    oxigeno = 1;
    comida = 1;
  }

  public void respirar() {
    oxigeno++;
  }

  // En realidad esto deberia ser abstracto
  public void comer() {}

  public static void plancharIfParaComer(Mamifero m) {
    m.comer();
  }
}
