package md2html.markdown;

import java.util.List;

abstract class AbstractContainer implements Element {
    private final List<Paragraphable> list;

    AbstractContainer(List<Paragraphable> list) {
        this.list = list;
    }

    protected abstract String getOpenHtmlTag();

    protected abstract String getCloseHtmlTag();

    @Override
    public void toHtml(StringBuilder stringBuilder) {
        stringBuilder.append(getOpenHtmlTag());
        for (Html i : list) {
            i.toHtml(stringBuilder);
        }
        stringBuilder.append(getCloseHtmlTag());
    }
}
