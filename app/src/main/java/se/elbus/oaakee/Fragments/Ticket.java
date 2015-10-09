package se.elbus.oaakee.Fragments;

/**
 * Created by TH on 2015-10-06.
 */
public class Ticket {
    /*
    Time will be set when ticket is created.
     */
    public final long mValidTo;

    public Ticket() {
        mValidTo = System.currentTimeMillis() + (90 * 60 * 1000); // Always 90 minutes for now.
    }
}
