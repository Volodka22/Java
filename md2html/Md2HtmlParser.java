package md2html;


import md2html.markdown.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Md2HtmlParser extends BaseParser {

    protected Md2HtmlParser(String input) {
        super(new StringSource(input));
    }


    Map<Character, String> specialSymbols = Map.of(
            '&', "&amp;",
            '<', "&lt;",
            '>', "&gt;"
    );

    List<String> tags = List.of("**", "*", "`", "--", "__", "[", "]", "_");


    public String parse() {
        StringBuilder paragraphs = new StringBuilder();
        skipLineSeparator();
        while (!eof()) {
            StringBuilder a = new StringBuilder();
            int cnt = checkHeader();
            if (cnt != 0) {
                parseHeader(cnt).toHtml(a);
            } else {
                parseParagraph().toHtml(a);
            }
            paragraphs.append(a.toString()).append('\n');
            skipLineSeparator();
        }
        return paragraphs.toString();
    }

    private void skipLineSeparator() {
        while (isLineSeparator(ch)) {
            nextChar();
        }
    }

    private Paragraph parseParagraph() {
        return new Paragraph(List.of(parseContent("")));
    }

    private Header parseHeader(int cnt) {
        return new Header(List.of(parseContent("")), cnt);
    }

    private int checkHeader() {
        int cnt = 0;
        while (test('#')) {
            cnt++;
        }
        if (cnt == 0 || Character.isWhitespace(ch)) {
            if (cnt != 0) {
                skipWhitespace();
            }
            return cnt;
        }
        goBack(cnt + 1);
        return 0;
    }


    private Paragraphable parseContent(String lastTag) {
        ArrayList<Paragraphable> paragraphables = new ArrayList<>();
        while (!eof() && !checkEndParagraph()) {
            String tag = nextTag();
            if (tag.equals("[")) {
                Paragraphable text = parseContent("]");
                if (!test('(')) {
                    throw new LinkParseException(getPosition());
                }
                StringBuilder link = new StringBuilder();

                while (!test(')')) {
                    if (eof()) {
                        throw new LinkParseException(getPosition());
                    }
                    link.append(parseWord());
                }

                paragraphables.add(new Link(List.of(text), link.toString()));

            } else if (tag.equals("")) {
                String sb = parseWord();
                paragraphables.add(new Text(sb));
            } else if (tag.equals(lastTag)) {
                switch (lastTag) {
                    case ("**"):
                    case ("__"):
                        return new Strong(paragraphables);
                    case ("--"):
                        return new Strikeout(paragraphables);
                    case ("*"):
                    case ("_"):
                        return new Emphasis(paragraphables);
                    case ("`"):
                        return new Code(paragraphables);
                    case ("]"):
                        return new Content(paragraphables);
                    default:
                        throw error("Impossible");
                }
            } else {
                paragraphables.add(parseContent(tag));
            }
        }
        if (lastTag.equals("]")) {
            throw new LinkParseException(getPosition());
        }
        return new Content(List.of(new Text(lastTag), new Content(paragraphables)));
    }

    public String nextTag() {
        for (String i : tags) {
            if (test(i)) {
                return i;
            }
        }
        return "";
    }

    public String parseWord() {
        StringBuilder sb = new StringBuilder();
        if (ch == '\\') {
            nextChar();
            sb.append(ch);
            nextChar();
        } else {
            boolean isSpecSymbol = false;
            for (char i : specialSymbols.keySet()) {
                if (test(i)) {
                    sb.append(specialSymbols.get(i));
                    isSpecSymbol = true;
                }
            }
            if (!isSpecSymbol) {
                sb.append(ch);
                nextChar();
            }
        }
        while (!isLineSeparator(ch) && Character.isWhitespace(ch) || Character.isLetter(ch)) {
            sb.append(ch);
            nextChar();
        }
        return sb.toString();
    }

    private boolean checkEndParagraph() {
        if (isLineSeparator(ch)) {
            nextChar();
            if (isLineSeparator(ch) || eof()) {
                return true;
            } else {
                goBack(2);
            }
        }
        return false;
    }

    private boolean isLineSeparator(char ch) {
        return ch == 10 || Character.getType(ch) == Character.LINE_SEPARATOR;
    }

}
