package examples.translation;

import com.sun.xml.internal.ws.api.message.ExceptionHasMessage;

public class TryCatch {

    // no se soporta traducir try catch
    // nunca se ejcuta el catch
    public void tryCatchNormal(){
        try {
            throw new Exception();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void tryCatchAnidado(){
        try {
            throw new Exception();
        } catch (Exception e) {
            try {
                throw new Exception();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    // Lo unico que se modela de excepciones es que ante un throw new RuntimeException se setee la variable Exception != null
    // no hay logica para los saltos dentro de un try catch finally
    // los saltos al finally si andan
    public void throwRuntimeException(){
        throw new RuntimeException();
    }

     public void throwRuntimeExceptionConTryCatch(){
        try {
            throw new RuntimeException();
        }catch (RuntimeException ex){
            ex.printStackTrace();
        }

        // no se soporta tampoco el try catch con runtime exception

        /*procedure examples.translation.TryCatch#throwRuntimeExceptionConTryCatch($this : Ref)
        {
            var r0 : Ref;
            var $r1 : Ref;
            var r2 : Ref;
            var $r3 : Ref;

            examples.translation.TryCatch#throwRuntimeExceptionConTryCatch_0:
        r0 := $this;

            examples.translation.TryCatch#throwRuntimeExceptionConTryCatch_1:
        call $r1 := Alloc();

            examples.translation.TryCatch#throwRuntimeExceptionConTryCatch_2:
        call java.lang.RuntimeException#?init?($r1);
            if ($Exception != null) {
                return;
            }

            examples.translation.TryCatch#throwRuntimeExceptionConTryCatch_3:
        $Exception := $r1;
            return;

            examples.translation.TryCatch#throwRuntimeExceptionConTryCatch_4:
        $r3 := $Exception;
            r2 := $r3;
            call java.lang.Throwable#printStackTrace(r2);
            if ($Exception != null) {
                return;
            }
            return;

        }*/
    }

    public void trycatchfinally(){
        try{
            int t = 5 +5;
        } catch (Exception ex){
            int c = 3 + 3;
        }finally {
            int f = 2+2;
        }

        // Maneja bien saltar al finally
        // no maneja saltar al catch
        // genera un bloque no entiendo que hace:
        //              examples.translation.TryCatch#trycatchfinally_7:
        /*
        procedure examples.translation.TryCatch#trycatchfinally($this : Ref)
        {
            var r0 : Ref;
            var b0 : int;
            var b1 : int;
            var r1 : Ref;
            var b2 : int;
            var b3 : int;
            var $r2 : Ref;
            var r3 : Ref;
            var b4 : int;
            var $r4 : Ref;

            examples.translation.TryCatch#trycatchfinally_0:
                r0 := $this;

            examples.translation.TryCatch#trycatchfinally_1:
                b0 := 10;

            examples.translation.TryCatch#trycatchfinally_2:
                b3 := 4;
                goto examples.translation.TryCatch#trycatchfinally_8;

            examples.translation.TryCatch#trycatchfinally_3:
                $r2 := $Exception;

            examples.translation.TryCatch#trycatchfinally_4:
                r3 := $r2;

            examples.translation.TryCatch#trycatchfinally_5:
                b1 := 6;

            examples.translation.TryCatch#trycatchfinally_6:
                b4 := 4;
                goto examples.translation.TryCatch#trycatchfinally_8;

            examples.translation.TryCatch#trycatchfinally_7:
                $r4 := $Exception;
                r1 := $r4;
                b2 := 4;
                $Exception := r1;
                return;

            examples.translation.TryCatch#trycatchfinally_8:
                return;
                 */
    }

    public void trycatchfinallythrow(){
        try{
            int t = 5 +5;
            throw new RuntimeException();
        } catch (RuntimeException ex){
            int c = 3 + 3;
        }finally {
            int f = 2+2;
        }

        /*
            procedure examples.translation.TryCatch#trycatchfinallythrow($this : Ref)
            {
                var r0 : Ref;
                var b0 : int;
                var b1 : int;
                var r1 : Ref;
                var b2 : int;
                var $r2 : Ref;
                var $r3 : Ref;
                var r4 : Ref;
                var b3 : int;
                var $r5 : Ref;

                examples.translation.TryCatch#trycatchfinallythrow_0:
                    r0 := $this;

                examples.translation.TryCatch#trycatchfinallythrow_1:
                    b0 := 10;

                examples.translation.TryCatch#trycatchfinallythrow_2:
                    call $r2 := Alloc();

                examples.translation.TryCatch#trycatchfinallythrow_3:
                    call java.lang.RuntimeException#?init?($r2);
                    if ($Exception != null) {
                        return;
                    }

                examples.translation.TryCatch#trycatchfinallythrow_4:
                    $Exception := $r2;
                    return;

                examples.translation.TryCatch#trycatchfinallythrow_5:
                    $r3 := $Exception;

                examples.translation.TryCatch#trycatchfinallythrow_6:
                    r4 := $r3;

                examples.translation.TryCatch#trycatchfinallythrow_7:
                    b1 := 6;

                examples.translation.TryCatch#trycatchfinallythrow_8:
                    b3 := 4;
                    goto examples.translation.TryCatch#trycatchfinallythrow_10;

                examples.translation.TryCatch#trycatchfinallythrow_9:
                    $r5 := $Exception;
                    r1 := $r5;
                    b2 := 4;
                    $Exception := r1;
                    return;

                examples.translation.TryCatch#trycatchfinallythrow_10:
                    return;

            }

         */
    }
}
