package se.elbus.oaakee.Buses;

/**
 * This exception is used to signal when the Buses.java class isn't correctly initialized. Created
 * by TH on 2015-09-28.
 */
public class BusesNotLoadedException extends RuntimeException {

    public BusesNotLoadedException(String s) {
        super(s);
    }
}
