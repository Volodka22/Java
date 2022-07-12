package md2html.markdown;

import java.util.List;

public abstract class AbstractParagraphableContainer extends AbstractContainer implements Paragraphable{
    AbstractParagraphableContainer(List<Paragraphable> list) {
        super(list);
    }
}
