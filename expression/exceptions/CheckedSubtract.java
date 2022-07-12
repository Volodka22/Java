package expression.exceptions;

import expression.ModifyExpression;
import expression.Subtract;

public class CheckedSubtract extends Subtract {
    public CheckedSubtract(ModifyExpression a, ModifyExpression b) {
        super(a, b);
    }

    @Override
    public int operate(int a, int b) {
        OverflowException.checkSubtract(a, b);
        return super.operate(a, b);
    }

    @Override
    public double operate(double a, double b) {
        throw new DoubleOperationException();
    }
}
