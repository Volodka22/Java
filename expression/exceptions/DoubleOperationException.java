package expression.exceptions;

public class DoubleOperationException extends UnsupportedOperationException {
    DoubleOperationException() {
        super("Double is not support in this parser");
    }
}
