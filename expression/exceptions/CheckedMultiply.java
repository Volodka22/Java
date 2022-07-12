package expression.exceptions;

import expression.ModifyExpression;
import expression.Multiply;

public class CheckedMultiply extends Multiply {
    public CheckedMultiply(ModifyExpression a, ModifyExpression b) {
        super(a, b);
    }

    @Override
    public double operate(double a, double b) {
        throw new DoubleOperationException();
    }

    @Override
    public int operate(int a, int b) {
        OverflowException.checkMultiplyThrow(a, b);
        return super.operate(a, b);
    }
}
