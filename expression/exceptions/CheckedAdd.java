package expression.exceptions;

import expression.Add;
import expression.ModifyExpression;

public class CheckedAdd extends Add {
    public CheckedAdd(ModifyExpression a, ModifyExpression b) {
        super(a, b);
    }

    @Override
    public int operate(int a, int b) {
        OverflowException.checkAdd(a, b);
        return super.operate(a, b);
    }

    @Override
    public double operate(double a, double b) {
        throw new DoubleOperationException();
    }
}
