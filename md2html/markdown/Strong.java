package md2html.markdown;

import java.util.List;

public class Strong extends AbstractParagraphableContainer {

    private static final String openHtmlTag = "<strong>";
    private static final String closeHtmlTag = "</strong>";

    public Strong(List<Paragraphable> list) {
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
