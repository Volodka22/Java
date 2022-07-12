package md2html.markdown;

public class Text implements Paragraphable {
    String text;

    public Text(String text) {
        this.text = text;
    }

    @Override
    public void toHtml(StringBuilder stringBuilder) {
        stringBuilder.append(text);
    }
}
