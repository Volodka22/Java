package expression.exceptions;

import expression.ModifyExpression;
import expression.PriorityConst;
import expression.parser.AbstractUnaryOperation;

public class Abs extends AbstractUnaryOperation {
    public Abs(ModifyExpression a) {
        super(a);
    }

    @Override
    public int operate(int a) {
        return ExpressionMath.abs(a);
    }

    @Override
    public double operate(double a) {
        throw new DoubleOperationException();
    }

    @Override
    public String getOp() {
        return "abs";
    }

}
