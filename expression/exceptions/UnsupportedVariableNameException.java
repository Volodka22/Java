package expression.exceptions;

public class UnsupportedVariableNameException extends ParseException {
    public UnsupportedVariableNameException(int pos) {
        super("It is unsupported variable`s name", pos);
    }
}
