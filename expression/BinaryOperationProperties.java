package expression;

public interface BinaryOperationProperties extends OperationProperties {
    boolean isNoAssociate();

    boolean isClosedForEq();

    PriorityConst getPriority();
}
