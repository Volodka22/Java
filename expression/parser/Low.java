package expression.parser;

import expression.ModifyExpression;
import expression.PriorityConst;

public class Low extends AbstractUnaryOperation {
    public Low(ModifyExpression a) {
        super(a);
    }

    @Override
    public int operate(int a) {
        return Integer.lowestOneBit(a);
    }

    @Override
    public double operate(double a) {
        throw new UnsupportedOperationException("Not for double");
    }

    @Override
    public String getOp() {
        return "low";
    }

}