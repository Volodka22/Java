package md2html.markdown;

import java.util.List;

public class Paragraph extends AbstractContainer {

    private static final String openHtmlTag = "<p>";
    private static final String closeHtmlTag = "</p>";

    public Paragraph(List<Paragraphable> list) {
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
