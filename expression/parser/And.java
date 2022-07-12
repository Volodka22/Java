package expression.parser;

import expression.AbstractBinaryOperation;
import expression.ModifyExpression;
import expression.PriorityConst;

public class And extends AbstractBinaryOperation {

    public And(ModifyExpression a, ModifyExpression b) {
        super(a, b);
    }

    @Override
    public int operate(int a, int b) {
        return (a & b);
    }

    @Override
    public double operate(double a, double b) {
        throw new UnsupportedOperationException("Not for double");
    }

    @Override
    public boolean isNoAssociate() {
        return false;
    }

    @Override
    public boolean isClosedForEq() {
        return false;
    }

    @Override
    public PriorityConst getPriority() {
        return PriorityConst.AND;
    }

    @Override
    public String getOp() {
        return "&";
    }
}