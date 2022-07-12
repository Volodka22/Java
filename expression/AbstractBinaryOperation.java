package expression;

import java.util.Objects;

public abstract class AbstractBinaryOperation implements ModifyExpression, BinaryOperate {

    protected ModifyExpression a;
    protected ModifyExpression b;
    protected boolean brackets;


    public AbstractBinaryOperation(ModifyExpression a, ModifyExpression b) {
        this.a = a;
        this.b = b;
        brackets = false;
        setBracketsInChildren();
    }

    public void setBrackets(ModifyExpression a) {
        if (a instanceof AbstractBinaryOperation) {
            AbstractBinaryOperation c = (AbstractBinaryOperation) a;
            if (c.getPriority().compareTo(getPriority()) < 0) {
                c.setBrackets();
            }
        }
    }

    public void setBracketsInChildren() {
        setBrackets(a);
        setBrackets(b);
        if (b instanceof AbstractBinaryOperation) {
            if ((((AbstractBinaryOperation) b).getPriority() == getPriority()
                    && (isNoAssociate() || ((AbstractBinaryOperation) b).isClosedForEq()))) {
                ((AbstractBinaryOperation) b).setBrackets();
            }
        }
    }

    public void setBrackets() {
        brackets = true;
    }

    @Override
    public String toString() {
        return String.format("(%s %s %s)", a, getOp(), b);
    }

    @Override
    public String toMiniString() {
        if (brackets) {
            return String.format("(%s %s %s)", a.toMiniString(), getOp(), b.toMiniString());
        }
        return String.format("%s %s %s", a.toMiniString(), getOp(), b.toMiniString());
    }

    @Override
    public double evaluate(double x) {
        return operate(a.evaluate(x), b.evaluate(x));
    }

    @Override
    public int evaluate(int x) {
        return operate(a.evaluate(x), b.evaluate(x));
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return operate(a.evaluate(x, y, z), b.evaluate(x, y, z));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractBinaryOperation)) return false;
        AbstractBinaryOperation that = (AbstractBinaryOperation) o;
        return this.a.equals(that.a) && this.b.equals(that.b) && this.getOp().equals(that.getOp());
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b, getOp());
    }
}
