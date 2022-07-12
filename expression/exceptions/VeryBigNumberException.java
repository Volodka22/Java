package expression.exceptions;

public class VeryBigNumberException extends ParseException {
    public VeryBigNumberException(int pos) {
        super("Number is very big", pos);
    }
}
