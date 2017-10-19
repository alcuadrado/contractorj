package examples.translation.inheritance;

public class Herbivoro extends Mamifero {

  @Override
  public void comer() {
    comida++;
  }

  public void testInvocacionesVirtuales(Herbivoro herb) {
    herb.comer();
    herb
        .respirar(); // SOOT SABE QUE ESTA EXPRESION DE INVOCACION VIRTUAL LLEVA AL METODO Mamifero.respirar()
  }

  /* JIMPLE
  public void testInvocacionesVirtuales(examples.translation.inheritance.Herbivoro)
  {
      examples.translation.inheritance.Herbivoro r0, r1;

      r0 := @this: examples.translation.inheritance.Herbivoro;
      r1 := @parameter0: examples.translation.inheritance.Herbivoro;
      virtualinvoke r1.<examples.translation.inheritance.Herbivoro: void comer()>();
      virtualinvoke r1.<examples.translation.inheritance.Herbivoro: void respirar()>();
      return;
  }*/
  /* BOOGIE
     procedure examples.translation.inheritance.Herbivoro#testInvocacionesVirtuales$examples.translation.inheritance.Herbivoro($this : Ref, param00 : Ref)
     {
         var r0 : Ref;
         var r1 : Ref;


         r1 := param00;

         examples.translation.inheritance.Herbivoro#testInvocacionesVirtuales$examples.translation.inheritance.Herbivoro_0:
             r0 := $this;

             call examples.translation.inheritance.Herbivoro#comer(r1);
             if ($Exception != null) {
                 return;
             }
             call examples.translation.inheritance.Mamifero#respirar(r1);
             if ($Exception != null) {
                 return;
             }
             return;
     }
  */

  public void testInvocacionesVirtuales2(Mamifero m) {
    m.respirar();
    m.comer();
  }

  public static void unHerbivoroComiendo() {
    Herbivoro herb = new Herbivoro();

    Mamifero.plancharIfParaComer(herb);

    if (herb.comida == 2) {
      int i = 0;
      i = 8; // esto lo voy a reemplazar por un assert
    }
  }
  // para testear como se traducen estas funciones de la clase Object
  public void testObjectClass() {
    this.notifyAll();
  }
}
