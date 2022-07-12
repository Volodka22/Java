package expression.exceptions;

public class EvaluateException extends RuntimeException {
    public EvaluateException(String type) {
        super(type);
    }
}