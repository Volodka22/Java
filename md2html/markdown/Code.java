package md2html.markdown;

import java.util.List;

public class Code extends AbstractParagraphableContainer {

    public Code(List<Paragraphable> list) {
        super(list);
    }

    private static final String openHtmlTag = "<code>";
    private static final String closeHtmlTag = "</code>";

    @Override
    protected String getOpenHtmlTag() {
        return openHtmlTag;
    }

    @Override
    protected String getCloseHtmlTag() {
        return closeHtmlTag;
    }
}
