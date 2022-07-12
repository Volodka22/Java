package expression.parser;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface CharSource {
    boolean hasNext();
    boolean hasNext(int pass);
    char next();
    char next(int pass);
    int getPos();
    ParseException error(final String message);
}
