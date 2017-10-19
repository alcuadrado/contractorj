package examples.translation.inheritance;

interface Bicycle {

  //  wheel revolutions per minute

  public void changeCadence(int newValue);

  public void changeGear(int newValue);

  public void speedUp(int increment);

  public void applyBrakes(int decrement);
}
