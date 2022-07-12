package expression;

import java.util.NoSuchElementException;
import java.util.Objects;

public class Variable implements ModifyExpression {
    public String value;

    public Variable(String x) {
        value = x;
    }

    @Override
    public int evaluate(int x) {
        return x;
    }

    @Override
    public String toMiniString() {
        return value;
    }

    @Override
    public String toString() {
        return toMiniString();
    }

    @Override
    public double evaluate(double x) {
        return x;
    }

    @Override
    public int evaluate(int x, int y, int z) {
        switch (value) {
            case "x":
                return x;
            case "y":
                return y;
            case "z":
                return z;
            default:
                throw new NoSuchElementException();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Variable)) return false;
        Variable variable = (Variable) o;
        return Objects.equals(value, variable.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
