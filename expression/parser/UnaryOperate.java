package expression.parser;

import expression.OperationProperties;

public interface UnaryOperate extends OperationProperties {
    int operate(int a);

    double operate(double a);
}
