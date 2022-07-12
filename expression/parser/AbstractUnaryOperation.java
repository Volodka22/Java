package expression.parser;

import expression.*;

import java.util.Objects;

public abstract class AbstractUnaryOperation implements ModifyExpression, UnaryOperate {

    protected final ModifyExpression a;

    public AbstractUnaryOperation(ModifyExpression a) {
        this.a = a;
    }

    @Override
    public String toString() {
        return String.format("(%s %s)", getOp(), a);
    }

    @Override
    public String toMiniString() {
        return String.format("(%s %s)", getOp(), a);
    }

    @Override
    public double evaluate(double x) {
        return operate(a.evaluate(x));
    }

    @Override
    public int evaluate(int x) {
        return operate(a.evaluate(x));
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return operate(a.evaluate(x, y, z));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractUnaryOperation)) return false;
        AbstractUnaryOperation that = (AbstractUnaryOperation) o;
        return this.a.equals(that.a) && this.getOp().equals(that.getOp());
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, getOp());
    }
}
