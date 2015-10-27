package se.elbus.oaakee.buses;

/**
 * This exception is used to signal when the Buses.java class isn't correctly initialized.
 */
public class BusesNotLoadedException extends RuntimeException {

    public BusesNotLoadedException(String s) {
        super(s);
    }
}
