package examples.translation.inheritance;

public class Hijo extends Padre {

    @Override
    public void metodoA(){
        h=100;
        super.metodoA();
    }

    public void newTipado(){
        Padre p = new Hijo();
        p.metodoA();
    }

    // no invoca correctamente.
    /*procedure examples.translation.inheritance.Hijo#newTipado($this : Ref)
    {
        var r0 : Ref;
        var $r1 : Ref;
        var r2 : Ref;




        examples.translation.inheritance.Hijo#newTipado_0:
    r0 := $this;
        call $r1 := Alloc();
        call examples.translation.inheritance.Hijo#?init?($r1);
        if ($Exception != null) {
            return;
        }
        r2 := $r1;
        call examples.translation.inheritance.Padre#metodoA(r2);
        if ($Exception != null) {
            return;
        }
        return;

    }*/

    public void aliasComparacionValores(){
        Hijo h = new Hijo();
        Padre p = h;
        int res = 0;

        if (p.j == h.j){
            res = 4 + 4; // lo cambie por un assert false.
        }
    }
    // Program has potential bug: True bug
    /*procedure examples.translation.inheritance.Hijo#aliasComparacionValores($this : Ref)
    {
        var r0 : Ref;
        var $r1 : Ref;
        var r2 : Ref;
        var r3 : Ref;
        var z0 : bool;
        var $i0 : int;
        var $i1 : int;
        var b2 : int;




        examples.translation.inheritance.Hijo#aliasComparacionValores_0:
    r0 := $this;
        call $r1 := Alloc();
        call examples.translation.inheritance.Hijo#?init?($r1);
        if ($Exception != null) {
            return;
        }
        r2 := $r1;
        r3 := r2;
        z0 := false;
        $i0 := Union2Int(Read($Heap, r3, examples.translation.inheritance.Padre#j));
        $i1 := Union2Int(Read($Heap, r2, examples.translation.inheritance.Padre#j));
        if ($i0 != $i1) {
            goto examples.translation.inheritance.Hijo#aliasComparacionValores_2;
        }

        examples.translation.inheritance.Hijo#aliasComparacionValores_1:
    assert false;

        examples.translation.inheritance.Hijo#aliasComparacionValores_2:
    return;

    }*/

    public void aliasComparacionValores2(){
        Hijo h = new Hijo();
        Padre pi = h;
        h.j++;
        pi.j++;

        int res = 0;
        if (pi.j == 2 && h.j == 2){
            res = 4 + 4; // lo cambie por un assert false.
        }
    }
    // Program has potential bug: True bug
    /*procedure examples.translation.inheritance.Hijo#aliasComparacionValores2($this : Ref)
    {
        var r0 : Ref;
        var $r1 : Ref;
        var r2 : Ref;
        var r3 : Ref;
        var z0 : bool;
        var $i0 : int;
        var $i1 : int;
        var $i2 : int;
        var $i3 : int;
        var $i4 : int;
        var $i5 : int;
        var b6 : int;




        examples.translation.inheritance.Hijo#aliasComparacionValores2_0:
    r0 := $this;
        call $r1 := Alloc();
        call examples.translation.inheritance.Hijo#?init?($r1);
        if ($Exception != null) {
            return;
        }
        r2 := $r1;
        r3 := r2;
        $i0 := Union2Int(Read($Heap, r2, examples.translation.inheritance.Padre#j));
        $i1 := $i0 + 1;
        assume Union2Int(Int2Union($i1)) == $i1;
        $Heap := Write($Heap, r2, examples.translation.inheritance.Padre#j, Int2Union($i1));
        $i2 := Union2Int(Read($Heap, r3, examples.translation.inheritance.Padre#j));
        $i3 := $i2 + 1;
        assume Union2Int(Int2Union($i3)) == $i3;
        $Heap := Write($Heap, r3, examples.translation.inheritance.Padre#j, Int2Union($i3));
        z0 := false;
        $i4 := Union2Int(Read($Heap, r3, examples.translation.inheritance.Padre#j));
        if ($i4 != 2) {
            goto examples.translation.inheritance.Hijo#aliasComparacionValores2_3;
        }

        examples.translation.inheritance.Hijo#aliasComparacionValores2_1:
    $i5 := Union2Int(Read($Heap, r2, examples.translation.inheritance.Padre#j));
        if ($i5 != 2) {
            goto examples.translation.inheritance.Hijo#aliasComparacionValores2_3;
        }

        examples.translation.inheritance.Hijo#aliasComparacionValores2_2:
    assert false;

        examples.translation.inheritance.Hijo#aliasComparacionValores2_3:
    return;

    }*/

    public void aliasComparacionValores3(){
        Hijo h = new Hijo();
        Padre pi = h;
        h.j++;
        pi.j++;

        int res = 0;
        if (pi.j == 1 && h.j == 2){
            res = 4 + 4; // lo cambio por un assert false
        }
    }

    // traducción - Program has no bugs.
    /*procedure examples.translation.inheritance.Hijo#aliasComparacionValores3($this : Ref)
    {
        var r0 : Ref;
        var $r1 : Ref;
        var r2 : Ref;
        var r3 : Ref;
        var z0 : bool;
        var $i0 : int;
        var $i1 : int;
        var $i2 : int;
        var $i3 : int;
        var $i4 : int;
        var $i5 : int;
        var b6 : int;

        examples.translation.inheritance.Hijo#aliasComparacionValores3_0:
    r0 := $this;
        call $r1 := Alloc();
        call examples.translation.inheritance.Hijo#?init?($r1);
        if ($Exception != null) {
            return;
        }
        r2 := $r1;
        r3 := r2;
        $i0 := Union2Int(Read($Heap, r2, examples.translation.inheritance.Padre#j));
        $i1 := $i0 + 1;
        assume Union2Int(Int2Union($i1)) == $i1;
        $Heap := Write($Heap, r2, examples.translation.inheritance.Padre#j, Int2Union($i1));
        $i2 := Union2Int(Read($Heap, r3, examples.translation.inheritance.Padre#j));
        $i3 := $i2 + 1;
        assume Union2Int(Int2Union($i3)) == $i3;
        $Heap := Write($Heap, r3, examples.translation.inheritance.Padre#j, Int2Union($i3));
        z0 := false;
        $i4 := Union2Int(Read($Heap, r3, examples.translation.inheritance.Padre#j));
        if ($i4 != 1) {
            goto examples.translation.inheritance.Hijo#aliasComparacionValores3_3;
        }

        examples.translation.inheritance.Hijo#aliasComparacionValores3_1:
    $i5 := Union2Int(Read($Heap, r2, examples.translation.inheritance.Padre#j));
        if ($i5 != 2) {
            goto examples.translation.inheritance.Hijo#aliasComparacionValores3_3;
        }

        examples.translation.inheritance.Hijo#aliasComparacionValores3_2:
    assert false;

        examples.translation.inheritance.Hijo#aliasComparacionValores3_3:
    return;

    }*/

    public void aliasComparacionRef(){
        Hijo h = new Hijo();
        Padre p = h;
        int res = 0;

        if (p == h){
            res = 4 + 4; // lo cambie por un assert false.
        }
    }
    //  Program has potential bug: True bug
    /*procedure examples.translation.inheritance.Hijo#aliasComparacionRef($this : Ref)
    {
        var r0 : Ref;
        var $r1 : Ref;
        var r2 : Ref;
        var r3 : Ref;
        var z0 : bool;
        var b0 : int;


        examples.translation.inheritance.Hijo#aliasComparacionRef_0:
    r0 := $this;
        call $r1 := Alloc();
        call examples.translation.inheritance.Hijo#?init?($r1);
        if ($Exception != null) {
            return;
        }
        r2 := $r1;
        r3 := r2;
        z0 := false;
        if (r3 != r2) {
            goto examples.translation.inheritance.Hijo#aliasComparacionRef_2;
        }

        examples.translation.inheritance.Hijo#aliasComparacionRef_1:
    assert false;

        examples.translation.inheritance.Hijo#aliasComparacionRef_2:
    return;

    }*/

    public void aliasParametro(Hijo hijo){
        Hijo h = new Hijo();
        int res = 0;

        if (hijo == h){
            res = 4 + 4;// assert false
        }
    }

    //Program has potential bug: True bug
    /*procedure examples.translation.inheritance.Hijo#aliasParametro$examples.translation.inheritance.Hijo($this : Ref, param00 : Ref)
    {
        var r0 : Ref;
        var r1 : Ref;
        var $r2 : Ref;
        var r3 : Ref;
        var z0 : bool;
        var b0 : int;


        r1 := param00;

        examples.translation.inheritance.Hijo#aliasParametro$examples.translation.inheritance.Hijo_0:
        r0 := $this;

        call $r2 := Alloc();
        call examples.translation.inheritance.Hijo#?init?($r2);
        if ($Exception != null) {
            return;
        }
        r3 := $r2;
        z0 := false;
        if (r1 != r3) {
            goto examples.translation.inheritance.Hijo#aliasParametro$examples.translation.inheritance.Hijo_2;
        }

        examples.translation.inheritance.Hijo#aliasParametro$examples.translation.inheritance.Hijo_1:
        assert false;

        examples.translation.inheritance.Hijo#aliasParametro$examples.translation.inheritance.Hijo_2:
        return;

    }*/

    public void aliasParametro2(Padre padre){
        Hijo h = new Hijo();
        int res = 0;

        if (padre == h){
            res = 4 + 4;
        }
    }

    /*
     //Program has potential bug: True bug
    procedure examples.translation.inheritance.Hijo#aliasParametro2$examples.translation.inheritance.Padre($this : Ref, param00 : Ref)
{
    var r0 : Ref;
    var r1 : Ref;
    var $r2 : Ref;
    var r3 : Ref;
    var z0 : bool;
    var b0 : int;


    r1 := param00;

    examples.translation.inheritance.Hijo#aliasParametro2$examples.translation.inheritance.Padre_0:
        r0 := $this;

        call $r2 := Alloc();
        call examples.translation.inheritance.Hijo#?init?($r2);
        if ($Exception != null) {
            return;
        }
        r3 := $r2;
        z0 := false;
        if (r1 != r3) {
            goto examples.translation.inheritance.Hijo#aliasParametro2$examples.translation.inheritance.Padre_2;
        }

    examples.translation.inheritance.Hijo#aliasParametro2$examples.translation.inheritance.Padre_1:
        assert false;

    examples.translation.inheritance.Hijo#aliasParametro2$examples.translation.inheritance.Padre_2:
        return;

}
     */

    // Esto no puede compilar.
    /*public void aliasParametro3(LinkedList list){
        Hijo h = new Hijo();
        int res = 0;


        if (list == h){
            res = 4 + 4;
        }
    }*/
}
