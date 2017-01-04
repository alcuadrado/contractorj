package examples;

public class ATM {

    public boolean theCardIn;
    public boolean carHalfway;
    public boolean passwordGiven;
    public int card;
    public int passwd;

    public ATM()
    {
        theCardIn = false;
        carHalfway = false;
        passwordGiven = false;
        card = 0;
        passwd = 0;
    }

    public boolean inv() {
        return !(theCardIn && carHalfway);
    }

    public boolean InsertCard_pre(int c) {
        return c > 0;
    }

    public boolean InsertCard_pre() {
        return !theCardIn;
    }

    public void InsertCard(int c)
    {
        theCardIn = true;
        card = c;
    }

    public boolean EnterPassword_pre(int q) {
        return q > 0;
    }

    public boolean EnterPassword_pre() {
        return !passwordGiven;
    }

    public void EnterPassword(int q)
    {
        passwordGiven = true;
        passwd = q;
    }

    public boolean TakeCard_pre() {
        return carHalfway;
    }

    public void TakeCard()
    {
        carHalfway = false;
        theCardIn = false;
    }

    public boolean DisplayMainScreen_pre() {
        return !theCardIn && !carHalfway;
    }

    public void DisplayMainScreen()
    {
    }

    public boolean RequestPassword_pre() {
        return !passwordGiven;
    }

    public void RequestPassword()
    {
    }

    public boolean EjectCard_pre() {
        return theCardIn;
    }

    public void EjectCard()
    {
        theCardIn = false;
        carHalfway = true;
        card = 0;
        passwd = 0;
        passwordGiven = false;
    }

    public boolean RequestTakeCard_pre() {
        return carHalfway;
    }

    public void RequestTakeCard()
    {
    }

    public boolean CanceledMessage_pre() {
        return theCardIn;
    }

    public void CanceledMessage()
    {
    }


}
