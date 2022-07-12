package expression.parser;

import expression.ModifyExpression;
import expression.PriorityConst;

public class Negate extends AbstractUnaryOperation {
    public Negate(ModifyExpression a) {
        super(a);
    }

    @Override
    public int operate(int a) {
        return -a;
    }

    @Override
    public double operate(double a) {
        return -a;
    }

    @Override
    public String getOp() {
        return "-";
    }

}
