package expression.exceptions;

import expression.AbstractBinaryOperation;
import expression.ModifyExpression;
import expression.PriorityConst;

public class Gcd extends AbstractBinaryOperation {
    public Gcd(ModifyExpression a, ModifyExpression b) {
        super(a, b);
    }

    @Override
    public int operate(int a, int b) {
        return ExpressionMath.gcd(a, b);
    }

    @Override
    public double operate(double a, double b) {
        throw new DoubleOperationException();
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
    public String getOp() {
        return "gcd";
    }

    @Override
    public PriorityConst getPriority() {
        return PriorityConst.GCD;
    }
}