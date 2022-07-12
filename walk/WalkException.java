package info.kgeorgiy.ja.smaglii.walk;

public class WalkException extends Exception {

    WalkException(String message, Exception e) {
        super(message + ". " + e.getMessage(), e.getCause());
    }

    WalkException(String message) {
        super(message);
    }
}
