package expression.exceptions;

import expression.Divide;
import expression.ModifyExpression;

public class CheckedDivide extends Divide {
    public CheckedDivide(ModifyExpression a, ModifyExpression b) {
        super(a, b);
    }

    @Override
    public double operate(double a, double b) {
        throw new DoubleOperationException();
    }

    @Override
    public int operate(int a, int b) {
        if (b == 0) {
            throw new DivideByZeroException();
        }
        OverflowException.checkDivide(a, b);
        return super.operate(a, b);
    }
}
