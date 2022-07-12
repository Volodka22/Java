package expression.exceptions;

public class OperationNotFoundException extends EvaluateException {
    public OperationNotFoundException() {
        super("Operation not found");
    }
}
