package expression.exceptions;

import expression.ModifyExpression;
import expression.parser.Negate;

public class CheckedNegate extends Negate {
    public CheckedNegate(ModifyExpression a) {
        super(a);
    }

    @Override
    public int operate(int a) {
        OverflowException.checkNegate(a);
        return super.operate(a);
    }

    @Override
    public double operate(double a) {
        throw new DoubleOperationException();
    }
}
