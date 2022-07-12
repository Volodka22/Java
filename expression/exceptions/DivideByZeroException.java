package expression.exceptions;

public class DivideByZeroException extends EvaluateException {
    public DivideByZeroException() {
        super("Division by zero");
    }
}
