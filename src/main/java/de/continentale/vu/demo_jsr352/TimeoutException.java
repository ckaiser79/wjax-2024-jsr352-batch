package de.continentale.vu.demo_jsr352;

public class TimeoutException extends RuntimeException {

    public TimeoutException(final String message) {
        super(message);
    }
}
