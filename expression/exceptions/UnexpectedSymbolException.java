package expression.exceptions;

public class UnexpectedSymbolException extends ParseException {
    public UnexpectedSymbolException(int position, String expected, char found) {
        super("Expected " + expected + " but found '" + found + "'", position);
    }
}
