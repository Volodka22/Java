package md2html.markdown;

import java.util.List;

public class Header extends AbstractContainer {

    private final String openHtmlTag;
    private final String closeHtmlTag;

    public Header(List<Paragraphable> list, int number) {
        super(list);
        StringBuilder header = new StringBuilder("h" + number);
        openHtmlTag = "<" + header + ">";
        closeHtmlTag = "</" + header + ">";
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
