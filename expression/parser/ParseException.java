package expression.parser;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class ParseException extends RuntimeException {
    public ParseException(String exception, int pos) {
        super(exception + ", on position " + pos);
    }
}
