package expression;

public class Multiply extends AbstractBinaryOperation {
    public Multiply(ModifyExpression a, ModifyExpression b) {
        super(a, b);
    }

    @Override
    public int operate(int a, int b) {
        return a * b;
    }

    @Override
    public double operate(double a, double b) {
        return a * b;
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
        return PriorityConst.MUL;
    }

    @Override
    public String getOp() {
        return "*";
    }
}
