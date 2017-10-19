package examples.translation.inheritance;

public class Carnivoro extends Mamifero {

  @Override
  public void comer() {
    comida = comida * 3;
  }

  public static void unCarnivoroComiendo() {
    Carnivoro carnivoro = new Carnivoro();

    Mamifero.plancharIfParaComer(carnivoro);

    if (carnivoro.comida == 3) {
      int i = 0;
      i = 8; // esto lo voy a reemplazar por un assert
    }
  }
}
