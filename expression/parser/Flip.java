package expression.parser;

import expression.ModifyExpression;
import expression.PriorityConst;

public class Flip extends AbstractUnaryOperation {
    public Flip(ModifyExpression a) {
        super(a);
    }

    @Override
    public int operate(int a) {
        int res = 0;
        while (a != 0) {
            res <<= 1;
            res |= a & 1;
            a >>>= 1;
        }
        return res;
    }

    @Override
    public double operate(double a) {
        throw new UnsupportedOperationException("Not for double");
    }

    @Override
    public String getOp() {
        return "flip";
    }

}
