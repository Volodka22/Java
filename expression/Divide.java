package expression;

public class Divide extends AbstractBinaryOperation {
    public Divide(ModifyExpression a, ModifyExpression b) {
        super(a, b);
    }


    @Override
    public int operate(int a, int b) {
        return a / b;
    }

    @Override
    public double operate(double a, double b) {
        return a / b;
    }

    @Override
    public boolean isNoAssociate() {
        return true;
    }

    @Override
    public boolean isClosedForEq() {
        return true;
    }

    @Override
    public PriorityConst getPriority() {
        return PriorityConst.MUL;
    }

    @Override
    public String getOp() {
        return "/";
    }
}
