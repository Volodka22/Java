package md2html.markdown;

import md2html.ParseException;

public class LinkParseException extends ParseException {

    public LinkParseException(int pos) {
        super("Link has form: [text](link)", pos);
    }
}
