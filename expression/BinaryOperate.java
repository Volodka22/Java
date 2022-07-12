package expression;

public interface BinaryOperate extends BinaryOperationProperties {
    int operate(int a, int b);

    double operate(double a, double b);
}
