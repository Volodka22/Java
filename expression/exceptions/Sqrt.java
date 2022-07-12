package expression.exceptions;

import expression.ModifyExpression;
import expression.PriorityConst;
import expression.parser.AbstractUnaryOperation;

public class Sqrt extends AbstractUnaryOperation {
    public Sqrt(ModifyExpression a) {
        super(a);
    }

    @Override
    public int operate(int a) {
        return ExpressionMath.sqrt(a);
    }

    @Override
    public double operate(double a) {
        throw new DoubleOperationException();
    }

    @Override
    public String getOp() {
        return "sqrt";
    }

}
