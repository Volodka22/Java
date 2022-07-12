package expression.parser;

import expression.*;
import expression.exceptions.UnexpectedSymbolException;

import java.nio.charset.UnsupportedCharsetException;
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
        private final List<PriorityConst> priorityList = List.of(PriorityConst.values());

        Map<String, PriorityConst> priorityFromString = Map.of(
                "+", PriorityConst.ADD,
                "-", PriorityConst.ADD,
                "*", PriorityConst.MUL,
                "/", PriorityConst.MUL,
                "^", PriorityConst.XOR,
                "&", PriorityConst.AND,
                "|", PriorityConst.OR
        );

        public Parser(final CharSource source) {
            super(source);
            nextChar();
        }

        public ModifyExpression parseExpression() {
            ModifyExpression expression = parse(0);
            skipWhitespace();
            if (!eof())
                throw new UnsupportedCharsetException("Unexpected symbols at the end");
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

        private ModifyExpression buildBinaryOperation(String op, ModifyExpression a, ModifyExpression b) {
            switch (op) {
                case "+":
                    return new Add(a, b);
                case "/":
                    return new Divide(a, b);
                case "*":
                    return new Multiply(a, b);
                case "-":
                    return new Subtract(a, b);
                case "^":
                    return new Xor(a, b);
                case "|":
                    return new Or(a, b);
                case "&":
                    return new And(a, b);
                default:
                    throw new UnsupportedOperationException("Impossible");
            }
        }

        private void parseNextBinaryOperation() {
            skipWhitespace();
            if (test('-')) {
                lastOp = "-";
            } else if (test('+')) {
                lastOp = "+";
            } else if (test('/')) {
                lastOp = "/";
            } else if (test('*')) {
                lastOp = "*";
            } else if (test('^')) {
                lastOp = "^";
            } else if (test('|')) {
                lastOp = "|";
            } else if (test('&')) {
                lastOp = "&";
            } else {
                throw error("Incorrect binary operation");
            }
            priorityLastOp = priorityFromString.get(lastOp);
        }

        private ModifyExpression parseNext() {
            skipWhitespace();

            if (test('-')) {
                if (isDigit()) {
                    return parseNumber(true);
                } else {
                    return new Negate(parseNext());
                }
            } else if (test("flip")) {
                return new Flip(parseNext());
            } else if (test("low")) {
                return new Low(parseNext());
            } else if (test('(')) {
                ModifyExpression parseExpression = parse(0);
                skipWhitespace();
                expect(')');
                readOperation();
                return parseExpression;
            } else if (isDigit()) {
                return parseNumber(false);
            } else if (isLetter()) {
                return parseVariable();
            } else {
                throw new UnexpectedSymbolException(getPosition(), "Variable, number or unary operator", ch);
            }
        }

        private boolean isLetter() {
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
            while (isLetter()) {
                sb.append(ch);
                nextChar();
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
                throw error("Invalid number " + sb);
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
            } else {
                throw error("Invalid number");
            }
        }
    }
}
