package expression;

public class Subtract extends AbstractBinaryOperation {
    public Subtract(ModifyExpression a, ModifyExpression b) {
        super(a, b);
    }


    @Override
    public int operate(int a, int b) {
        return a - b;
    }

    @Override
    public double operate(double a, double b) {
        return a - b;
    }

    @Override
    public boolean isNoAssociate() {
        return true;
    }

    @Override
    public boolean isClosedForEq() {
        return false;
    }

    @Override
    public PriorityConst getPriority() {
        return PriorityConst.ADD;
    }

    @Override
    public String getOp() {
        return "-";
    }
}
