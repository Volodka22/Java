package expression.exceptions;

public class SqrtOfNegNumException extends EvaluateException {
    public SqrtOfNegNumException() {
        super("Sqrt of negate number");
    }
}
