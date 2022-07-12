package expression.parser;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class StringSource implements CharSource {
    private final String data;
    private int pos;

    public StringSource(final String data) {
        this.data = data;
    }

    @Override
    public int getPos() {
        return pos;
    }

    @Override
    public boolean hasNext() {
        return pos < data.length();
    }

    @Override
    public boolean hasNext(int pass) {
        return pos + pass - 1 < data.length();
    }

    @Override
    public char next() {
        return data.charAt(pos++);
    }

    @Override
    public char next(int pass) {
        return data.charAt(pos + pass - 1);
    }

    @Override
    public ParseException error(final String message) {
        return new ParseException(message, pos);
    }
}
