package examples.smtp_server.src;

import examples.smtp_server.src.services.smtp.SMTPProcessor;

/*
    VERSION FINAL.

    Mock:
        *   El smtp server venia con una clase que logueaba los eventos en un archivo.
            Esas clases las generé vacias. Sino tenia problemas para traducir y agregaba cosas no necesarias al analisis.
            Son Log y LogFactory.

        *   ConfigurationManager es una clase que servia para leer la configuracion del servidor.
            Agregue codigo en java que al ser traducido generarían algo no deterministico (en boogie/corral).
            Si se ejecuta realmente, no se si el código de relleno funcionaría no deterministicamente.

        *   DeliveryService es una clase que se usa para verificar las direcciones. Utiliza cosas de red.
            Tambien las rellene con cosas que en boogie las harian no deterministicas.
            Al ejecutarse en java no garantizo eso.
            El acceptAddress retorna siempre true porque el false no aporta nada a la EPA.

        *   EmailAddress es una clase que le podes pedir el nombre de la dirección y cosas del estilo.
            Siempre retornan strings. Hardcodee valores porque daba igual por el soporte que tenemos de ellas en boogie/corral.
            Si se quiere ejecutar en Java habria que modificarse esto.

        Elimine todas las clases que no eran utilizadas por la clase SMTPProcessor, para no generar demasiado código en boogie.

        SMTP_Processor empieza en run. Es un hilo que interactua con el cliente con un socket. Los paquetes se procesan en handleCommands que tienen un while true

        Esta EPA esta basada en el método handleCommands para poder reflejar el protocolo SMTP en la EPA generada.
        handleCommands es un while que lee del socket los paquetes.

        Para el estudio cree handleCommand(...) que ya recibe por parametros los datos del paquete y no es un loop.
        Las strings las pase a numeros por una cuestion de soporte de boogie/corral.

        El mensaje de QUIT lanza una excepción para matar al hilo (no es una manera muy elegante la que eligieron). Para la epa intrudcí un booleano que refleja eso.
        Tambien una variable global que guarda el ultimo mensaje recibido. En la función handleCommandS (la original) lastCommand es una variable local.

        Hay excepciones que no estaran reflejadas en la EPA porque no tenemos soporte para los try/catch, solo para los throws.
        Por ejemplo la exepeción que se lanza en el catch de read() dentro de handleCommands(...) nunca es alcanzada en el analisis de corral.


        Comandos esperados por el RFC 5321 - Simple Mail Transfer Protocol
                Esta EPA            Guido               Hernán
        Helo      x                  x
        Ehlo                                              x
        Mail      x                  x                    x
        RCPT      x                  x                    x
        DATA      x                  x                    x
        RSET      x                  x                    x
        EXPN
        NOOP      x                  x                    x
        QUIT      x                                       x
        VRFY                         x                    x

No esta en el RFC el comando AUTH pero Hernan lo muestra en su EPA
Guido en su EPA muestra una transición VRFY pero no es algo soportado por esta implementación de SMTP. Guido dice basarse en este código al igual que Hernán.
Una cosa a considerar es que ellos nunca pudieron analizar directamente el código en Java. Al menos Guido debio hacerlo en C inspirandose en esta implementación, sino me equivoco.

En resumen esta clase es un wrapper al SmtpProcessor en particular del metodo que recibe los paquetes y que los procesa.
El envio de un comando, se simularia ejecutando una funcion pública de esta clase.
Ej: quiero ver que pasa cuando envio un HELO, ejecuto el metodo publico con ese nombre.

Esta clase no se podria ejecutar de manera real porque habria que establecer el socket.
Si se llama directamente handleCommand como hago yo, habria cosas no inicializadas.
 */
public class SmtpFacade_SmtpProcessor {

    SMTPProcessor smtpProcessor;

    public SmtpFacade_SmtpProcessor(){
        smtpProcessor =  new SMTPProcessor();
    }

    // las igualades del estilo smptProcessor.QUIT == 2 y etc, estan porque las EPAs no tienen memoria.
    // sus valores se setean en el constructor, no son constantes y entonces en los demas estados pueden cambiar sus valores (para las EPAS pero no en su ejecución real)
    // el implementador decidió no hacerlas constantes aun cuando nunca sus valores cambian.
    public boolean inv() {
        return  smtpProcessor != null &&
                smtpProcessor.lastCommand >= 0 && smtpProcessor.lastCommand <= 8 &&
                smtpProcessor.NONE == 0 &&
                smtpProcessor.HELO == 1 &&
                smtpProcessor.QUIT == 2 &&
                smtpProcessor.MAIL_FROM == 3 &&
                smtpProcessor.RCPT_TO == 4 &&
                smtpProcessor.DATA == 5 &&
                smtpProcessor.DATA_FINISHED == 6 &&
                smtpProcessor.RSET == 7 &&
                smtpProcessor.EHLO == 8 &&
                smtpProcessor.NOOP == 9 &&
                ( (smtpProcessor.lastCommand != smtpProcessor.QUIT && !smtpProcessor.fin) ||  (smtpProcessor.lastCommand == smtpProcessor.QUIT && smtpProcessor.fin == true));
    }


    public boolean HELO_pre(String argument) {
        return argument != null ;
    }
    public boolean HELO_pre() {return !smtpProcessor.fin;}
    public void HELO(String argument){
        smtpProcessor.handleCommand(smtpProcessor.HELO, argument);
    }

    public boolean NOOP_pre() {
        return !smtpProcessor.fin;
    }
    public void NOOP(String argument){
        smtpProcessor.handleCommand(smtpProcessor.NOOP, argument);
    }

    public boolean RSET_pre() {
        return !smtpProcessor.fin;
    }
    public void RSET(String argument){
        smtpProcessor.handleCommand(smtpProcessor.RSET, argument);
    }

    // pre: inputString.startsWith("MAIL FROM") &&  ( lastCommand == HELO || lastCommand == NONE || lastCommand == RSET || lastCommand == EHLO)
    public boolean MAIL_FROM_pre() {
        return ( smtpProcessor.lastCommand == smtpProcessor.HELO || smtpProcessor.lastCommand == smtpProcessor.NONE || smtpProcessor.lastCommand == smtpProcessor.RSET || smtpProcessor.lastCommand == smtpProcessor.EHLO) && !smtpProcessor.fin;
    }
    public void MAIL_FROM(String argument){
        smtpProcessor.handleCommand(smtpProcessor.MAIL_FROM, argument);
    }

    // pre: inputString.toUpperCase().startsWith( "RCPT TO:" ) && ( lastCommand == MAIL_FROM || lastCommand == RCPT_TO )
    public boolean RCPT_TO_pre() {
        return ( smtpProcessor.lastCommand == smtpProcessor.MAIL_FROM || smtpProcessor.lastCommand == smtpProcessor.RCPT_TO ) && !smtpProcessor.fin;
    }
    public void RCPT_TO(String argument) {
        smtpProcessor.handleCommand(smtpProcessor.RCPT_TO, argument);
    }

    // pre: if( lastCommand == RCPT_TO && message.getToAddresses().size() > 0 )
    public boolean DATA_pre(){
        return smtpProcessor.lastCommand == smtpProcessor.RCPT_TO && !smtpProcessor.fin;
    }
    public void DATA(String argument){
        smtpProcessor.handleCommand(smtpProcessor.DATA, argument);
    }

    public boolean QUIT_pre() { return !smtpProcessor.fin;}
    public void QUIT(String argument){
        smtpProcessor.handleCommand(smtpProcessor.QUIT, argument);
    }
}
