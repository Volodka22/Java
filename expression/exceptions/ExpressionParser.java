package expression.exceptions;


import expression.Const;
import expression.ModifyExpression;
import expression.PriorityConst;
import expression.Variable;
import expression.parser.BaseParser;
import expression.parser.CharSource;
import expression.parser.Negate;
import expression.parser.StringSource;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class ExpressionParser implements Parser {
    public ModifyExpression parse(final String source) {
        return parse(new StringSource(source));
    }

    public static ModifyExpression parse(CharSource source) {
        return new Parser(source).parseExpression();
    }

    private static class Parser extends BaseParser {

        private String lastOp = "";
        private PriorityConst priorityLastOp = null;
        private final List<PriorityConst> priorityList = Arrays.asList(PriorityConst.values());

        List<String> variableName = List.of("x", "y", "z");
        List<Character> simpleBinaryOperators = List.of('+', '-', '*', '/');
        List<String> binaryOperators = List.of("lcm", "gcd");

        Map<String, PriorityConst> priorityFromString = Map.of(
                "+", PriorityConst.ADD,
                "-", PriorityConst.ADD,
                "*", PriorityConst.MUL,
                "/", PriorityConst.MUL,
                "^", PriorityConst.XOR,
                "&", PriorityConst.AND,
                "|", PriorityConst.OR,
                "gcd", PriorityConst.GCD,
                "lcm", PriorityConst.GCD
        );

        public Parser(final CharSource source) {
            super(source);
            nextChar();
        }

        public ModifyExpression parseExpression() {
            ModifyExpression expression = parse(0);
            skipWhitespace();
            if (!eof()) {
                throw new UnexpectedSymbolException(getPosition(), "EOF", ch);
            }
            return expression;
        }

        private ModifyExpression parse(int priority) {
            skipWhitespace();
            if (priority == priorityList.size()) {
                return parseNext();
            }
            ModifyExpression current = parseNext();
            for (int i = priorityList.size() - 1; i >= priority; i--) {
                while (priorityLastOp == priorityList.get(i)) {
                    current = buildBinaryOperation(lastOp, current, parse(i + 1));
                }
            }
            return current;
        }

        private ModifyExpression parseUnaryOperation() {
            skipWhitespace();
            if (test('-')) {
                if (isDigit()) {
                    return parseNumber(true);
                }
                return new Negate(parseNext());
            } else if (test("sqrt")) {
                return new Sqrt(parseNext());
            } else if (test("abs")) {
                return new Abs(parseNext());
            }
            return null;
        }

        private ModifyExpression buildBinaryOperation(String op, ModifyExpression a, ModifyExpression b) {
            switch (op) {
                case "+":
                    return new CheckedAdd(a, b);
                case "/":
                    return new CheckedDivide(a, b);
                case "*":
                    return new CheckedMultiply(a, b);
                case "-":
                    return new CheckedSubtract(a, b);
                case "lcm":
                    return new Lcm(a, b);
                case "gcd":
                    return new Gcd(a, b);
                default:
                    throw new OperationNotFoundException();
            }
        }

        private void parseNextBinaryOperation() {
            skipWhitespace();
            for (char i : simpleBinaryOperators) {
                if (test(i)) {
                    lastOp = String.valueOf(i);
                    priorityLastOp = priorityFromString.get(lastOp);
                    return;
                }
            }
            for (String i : binaryOperators) {
                if (test(i)) {
                    lastOp = i;
                    priorityLastOp = priorityFromString.get(lastOp);
                    return;
                }
            }
            throw new OperationNotFoundException();
        }

        private ModifyExpression parseNext() {
            skipWhitespace();
            ModifyExpression unaryOperation = parseUnaryOperation();
            if (unaryOperation != null) {
                return unaryOperation;
            } else if (test('(')) {
                ModifyExpression parseExpression = parse(0);
                skipWhitespace();
                expect(')');
                readOperation();
                return parseExpression;
            } else if (isDigit()) {
                return parseNumber(false);
            } else if (isChar()) {
                return parseVariable();
            } else {
                throw new UnexpectedSymbolException(getPosition(), "Variable, number, '(' or unary operator", ch);
            }
        }

        private boolean isChar() {
            return between('a', 'z') || between('A', 'Z');
        }

        private boolean isDigit() {
            return between('0', '9');
        }

        private void readOperation() {
            skipWhitespace();
            if (eof()) {
                priorityLastOp = null;
                lastOp = "";
                return;
            }
            if (ch == ')') {
                priorityLastOp = null;
                lastOp = "";
                return;
            }
            parseNextBinaryOperation();
        }

        private ModifyExpression parseVariable() {
            skipWhitespace();
            final StringBuilder sb = new StringBuilder();
            while (isChar()) {
                sb.append(ch);
                nextChar();
            }

            if (!variableName.contains(sb.toString())) {
                throw new UnsupportedVariableNameException(getPosition());
            }
            readOperation();
            return new Variable(sb.toString());
        }

        private ModifyExpression parseNumber(boolean isNegative) {
            skipWhitespace();
            final StringBuilder sb = new StringBuilder();
            if (isNegative) sb.append("-");
            copyInteger(sb);
            readOperation();
            try {
                return new Const(Integer.parseInt(sb.toString()));
            } catch (NumberFormatException e) {
                throw new VeryBigNumberException(getPosition());
            }
        }

        private void copyDigits(final StringBuilder sb) {
            while (isDigit()) {
                sb.append(ch);
                nextChar();
            }
        }

        private void copyInteger(final StringBuilder sb) {
            if (test('0')) {
                sb.append('0');
            } else if (between('1', '9')) {
                copyDigits(sb);
            }
        }
    }
}