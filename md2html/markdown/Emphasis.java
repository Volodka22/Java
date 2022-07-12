package md2html.markdown;

import java.util.List;

public class Emphasis extends AbstractParagraphableContainer {

    private static final String openHtmlTag = "<em>";
    private static final String closeHtmlTag = "</em>";

    public Emphasis(List<Paragraphable> list) {
        super(list);
    }

    @Override
    protected String getOpenHtmlTag() {
        return openHtmlTag;
    }

    @Override
    protected String getCloseHtmlTag() {
        return closeHtmlTag;
    }

}
