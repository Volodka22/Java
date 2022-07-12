package expression;

import expression.exceptions.DivideByZeroException;
import expression.exceptions.ExpressionParser;
import expression.exceptions.OverflowException;


public class Main {
    public static void main(String[] args) {
        ExpressionParser expressionParser = new ExpressionParser();
        String test = "1000000*x*x*x*x*x/(x-1)";
        ModifyExpression modifyExpression = expressionParser.parse(test);
        for (int i = 0; i <= 10; i++) {
            try {
                System.out.println(modifyExpression.evaluate(i));
            } catch (OverflowException e) {
                System.out.println("overflow");
            } catch (DivideByZeroException e) {
                System.out.println("division by zero");
            }
        }
    }
}
