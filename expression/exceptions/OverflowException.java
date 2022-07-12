package expression.exceptions;

public class OverflowException extends EvaluateException {
    public OverflowException() {
        super("Overflow");
    }

    public static void checkMultiplyThrow(int x, int y) {
        if (checkMultiply(x, y)) {
            throw new OverflowException();
        }
    }

    public static boolean checkMultiply(int x, int y) {
        if (x == 0 || y == 0) {
            return false;
        }
        int result = x * y;
        return result / x != y || result / y != x;
    }


    public static void checkAdd(int x, int y) {
        if (y > 0 && Integer.MAX_VALUE - y < x || y < 0 && Integer.MIN_VALUE - y > x) {
            throw new OverflowException();
        }
    }

    public static void checkSubtract(int x, int y) {
        if (y > 0 && Integer.MIN_VALUE + y > x || y < 0 && Integer.MAX_VALUE + y < x) {
            throw new OverflowException();
        }
    }

    public static void checkDivide(int x, int y) {
        if (x == Integer.MIN_VALUE && y == -1) {
            throw new OverflowException();
        }
    }

    public static void checkNegate(int x) {
        if (x == Integer.MIN_VALUE) {
            throw new OverflowException();
        }
    }
}
