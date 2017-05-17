package examples;

import java.util.Collection;
import java.util.HashSet;
import java.util.Stack;

public class Cast {
    public void casteo_uno(){
        Integer j = 3; // no se modelan los valores de los Integers
        Integer h = 4;

        int resultado = j + h;

        // java.lang.Integer.valueOf$int(..) esta modelada como extern
        /*
        procedure examples.Cast#casteo_uno($this : Ref)
        {
            var r0 : Ref;
            var r1 : Ref;
            var r2 : Ref;
            var i0 : int;
            var $i1 : int;
            var $i2 : int;




            examples.Cast#casteo_uno_0:
                r0 := $this;
                call r1 := java.lang.Integer.valueOf$int(3);
                if ($Exception != null) {
                    return;
                }
                call r2 := java.lang.Integer.valueOf$int(4);
                if ($Exception != null) {
                    return;
                }
                call $i1 := java.lang.Integer#intValue(r1);
                if ($Exception != null) {
                    return;
                }
                call $i2 := java.lang.Integer#intValue(r2);
                if ($Exception != null) {
                    return;
                }
                i0 := $i1 + $i2;
                return;

        }
         */
    }

    public void casteo_dos(){ // Exception in thread "main" jbct.exceptions.UnsupportedTypeException: Unsupported type float

        //float unFloat = 10.5f;
        //double unDouble = unFloat;
    }

    public void casteo_tres(Object ob){
        Collection<Integer> unaColeccion = ((Collection<Integer>)ob);
        unaColeccion.add(15);

        ((Stack<HashSet>)ob).push(new HashSet());

        /*
            procedure examples.Cast#casteo_tres$java.lang.Object($this : Ref, param00 : Ref)
            {
                var r0 : Ref;
                var r1 : Ref;
                var $r2 : Ref;
                var $r3 : Ref;
                var $r4 : Ref;
                var $r5 : Ref;
                var $ret$814245389 : bool;
                var $ret$1120670624 : Ref;

                r1 := param00;

                examples.Cast#casteo_tres$java.lang.Object_0:
                    r0 := $this;

                    $r2 := r1;
                    call $r3 := java.lang.Integer.valueOf$int(15);
                    if ($Exception != null) {
                        return;
                    }
                    call $ret$814245389 := java.util.Collection#add$java.lang.Object($r2, $r3);
                    if ($Exception != null) {
                        return;
                    }
                    $r4 := r1;
                    call $r5 := Alloc();
                    call java.util.HashSet#?init?($r5);
                    if ($Exception != null) {
                        return;
                    }
                    call $ret$1120670624 := java.util.Stack#push$java.lang.Object($r4, $r5);
                    if ($Exception != null) {
                        return;
                    }
                    return;

            }

         */
    }

}
