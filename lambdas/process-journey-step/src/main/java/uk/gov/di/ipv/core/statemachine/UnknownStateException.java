package uk.gov.di.ipv.core.statemachine;

public class UnknownStateException extends Throwable {
    public UnknownStateException() {
        super();
    }

    public UnknownStateException(String message) {
        super(message);
    }

    public UnknownStateException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownStateException(Throwable cause) {
        super(cause);
    }
}
