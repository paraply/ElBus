package se.elbus.oaakee.Fragments;

/**
 * Created by TH on 2015-10-06.
 */
public class Ticket {
    /*
    Time will be set when ticket is created.
     */
    public final long mValidTo;

    /**
     * Amount of milliseconds the ticket will be valid for.
     */
    public final long VALID_TIME = 65000;

    public Ticket() {
        /*
        TODO: Implement a better way of getting the current time.
         */
        mValidTo = System.currentTimeMillis() + VALID_TIME;
    }
}
