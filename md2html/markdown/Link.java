package md2html.markdown;

import java.util.List;

public class Link extends AbstractParagraphableContainer {

    private String openHtmlTag = "";
    private String closeHtmlTag = "";

    public Link(List<Paragraphable> list, String link) {
        super(list);
        openHtmlTag = "<a href='" + link + "'>";
        closeHtmlTag = "</a>";
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
