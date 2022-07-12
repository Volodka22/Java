package md2html.markdown;

import java.util.List;

public class Strikeout extends AbstractParagraphableContainer {
    public Strikeout(List<Paragraphable> list) {
        super(list);
    }

    private static final String openHtmlTag = "<s>";
    private static final String closeHtmlTag = "</s>";

    @Override
    protected String getOpenHtmlTag() {
        return openHtmlTag;
    }

    @Override
    protected String getCloseHtmlTag() {
        return closeHtmlTag;
    }

    @Override
    public void toHtml(StringBuilder stringBuilder) {
        super.toHtml(stringBuilder);
    }


}
