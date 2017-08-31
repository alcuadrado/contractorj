package examples.smtp_server.src;

import examples.smtp_server.src.services.smtp.SMTPProcessor;

/*
    ESTE FUE EL PRIMER INTENTO
    NO ES EL FINAL.
 */
public class SmtpFacade {

    SMTPProcessor smtpProcessor;

    //SMTP Commands
    public int NONE = 0;
    public int HELO = 1;
    public int QUIT = 2;
    public int MAIL_FROM = 3;
    public int RCPT_TO = 4;
    public int DATA = 5;
    public int DATA_FINISHED = 6;
    public int RSET = 7;
    public int EHLO = 8;

    public int lastCommand;
//    public boolean readIncome;


    public SmtpFacade(){
        smtpProcessor =  new SMTPProcessor();
        lastCommand = NONE;
        //readIncome = false;
    }


    public boolean inv() {
        return lastCommand >= 0 && lastCommand <= 8 &&
                NONE == 0 &&
                HELO == 1 &&
                QUIT == 2 &&
                MAIL_FROM == 3 &&
                RCPT_TO == 4 &&
                DATA == 5 &&
                DATA_FINISHED == 6 &&
                RSET == 7 &&
                EHLO == 8;
    }

    /*public boolean readIncomeMessage_pre() {
        return !readIncome;
    }
    void readIncomeMessage(){
        readIncome = true;
    }*/

    public boolean HELO_pre() {
        return true;
    }
    public void HELO(){
        lastCommand = HELO;
        //readIncome = false;
    }

    public boolean NOOP_pre() {
        return true;
    }
    public void NOOP(){

    }

    public boolean RSET_pre() {
        return true;
    }
    public void RSET(){
        lastCommand = RSET;
    }

    // pre: inputString.startsWith("MAIL FROM") &&  ( lastCommand == HELO || lastCommand == NONE || lastCommand == RSET || lastCommand == EHLO)
    public boolean MAIL_FROM_pre() {
        return ( lastCommand == HELO || lastCommand == NONE || lastCommand == RSET || lastCommand == EHLO);
    }
    public void MAIL_FROM(){
        /*if( handleMailFrom( inputString ) ) {
            lastCommand = MAIL_FROM;
        }
        else {
            write(MESSAGE_COMMAND_ORDER_INVALID);
        }*/
        lastCommand = MAIL_FROM;
    }

    // pre: inputString.toUpperCase().startsWith( "RCPT TO:" ) && ( lastCommand == MAIL_FROM || lastCommand == RCPT_TO )
    public boolean RCPT_TO_pre() {
        return ( lastCommand == MAIL_FROM || lastCommand == RCPT_TO );
    }
    public void RCPT_TO(){
        lastCommand = RCPT_TO;
    }

    // pre: if( lastCommand == RCPT_TO && message.getToAddresses().size() > 0 )
    public boolean DATA_pre(){
        return lastCommand == RCPT_TO ;
    }

    public void DATA(){
        lastCommand = RSET;
    }

}
