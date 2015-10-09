package se.elbus.oaakee.Fragments;

/**
 * Created by TH on 2015-10-06.
 */
public class Card {

    private double charge = 200;

    /**
     * @return the current charge
     */
    public double getCharge() {
        return charge;
    }

    /**
     * @param amount is the amount we want to use
     * @return the new charge on the card
     */
    public double useCharge(double amount) {
        if(charge >= amount){
            this.charge -= amount;
        }
        return charge;
    }
}
