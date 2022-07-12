package md2html.markdown;

import java.util.List;

public class Content extends AbstractParagraphableContainer {
    public Content(List<Paragraphable> list) {
        super(list);
    }


    private static final String openHtmlTag = "";
    private static final String closeHtmlTag = "";

    @Override
    protected String getOpenHtmlTag() {
        return openHtmlTag;
    }

    @Override
    protected String getCloseHtmlTag() {
        return closeHtmlTag;
    }
}
