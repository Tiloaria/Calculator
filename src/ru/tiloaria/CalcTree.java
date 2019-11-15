package ru.tiloaria;

import java.util.ArrayList;
import java.util.HashMap;

import static ru.tiloaria.CalcTree.NodeType.*;

public class CalcTree {
    private NodeType type;
    private Double result = null;
    private ArrayList<CalcTree> children = new ArrayList<>();

    /**
     * @return result of expression if it's possible to evaluate it, otherwise null
     * @throws MathException in case of incorrect math action
     */
    public Double eval() throws MathException {
        Double res1 = children.size() > 0 ? children.get(0).eval() : null;
        Double res2 = children.size() > 1 ? children.get(1).eval() : null;
        switch (type) {
            case VAL:
                break;
            case VAR:
                break;
            case SIN:
                result = Math.sin(res1);
                break;
            case COS:
                result = Math.cos(res1);
                break;
            case LN:
                result = Math.log(res1);
                break;
            case PL:
                result = res1 + res2;
                break;
            case MIN:
                result = res1 - res2;
                break;
            case MULT:
                result = res1 * res2;
                break;
            case DIV:
                result = res1 / res2;
                break;
            case EXP:
                result = Math.pow(res1, res2);
                break;
        }
        if (Double.isNaN(result) || Double.isInfinite(result)) {
            throw new MathException("Incorrect operation, ln or /");
        }
        return result;
    }

    /**
     * @param input string which contains expression for parsing
     * @return tree with operations in nodes and values in leaves
     * @throws ParseExprException in case of malformed input expression
     */
    public static CalcTree parseFrom(String input, HashMap<String, Double> variables) throws ParseExprException {
        if (input.matches("\\(.+\\)")) {
            return parseFrom(input.substring(1, input.length() - 1), variables);
        }

        if (isVal(input)) {
            return new CalcTree(VAL, Double.valueOf(input));
        }

        if (isVar(input)) {
            if (!variables.containsKey(input)) {
                throw new ParseExprException("Undefined variable: " + input);
            }
            return new CalcTree(VAR, variables.get(input));
        }

        BinaryOpPosition binaryOpPosition = getBinaryOpPosition(input);
        ArrayList<CalcTree> child = new ArrayList<>();
        if (binaryOpPosition != null) {
            child.add(parseFrom(input.substring(0, binaryOpPosition.pos), variables));
            child.add(parseFrom(input.substring(binaryOpPosition.pos + 1), variables));
            return new CalcTree(binaryOpPosition.type, child);

        }

        if (input.matches("ln\\(.+\\)")) {
            child.add(parseFrom(input.substring(3, input.length() - 1), variables));
            return new CalcTree(LN, child);
        }
        if (input.matches("sin\\(.+\\)") || input.matches("cos\\(.+\\)")) {
            child.add(parseFrom(input.substring(4, input.length() - 1), variables));
            return new CalcTree(input.charAt(0) == 's' ? SIN : COS, child);
        }
        throw new ParseExprException();
    }

    protected enum NodeType {
        VAL,
        VAR,
        PL,
        MIN,
        MULT,
        DIV,
        SIN,
        COS,
        LN,
        EXP
    }

    private static boolean isVar(String s) {
        return s.matches("[a-zA-Z][a-zA-Z\\d]*");
    }

    private static boolean isVal(String s) {
        return s.matches("-?\\d+(\\.\\d+)?");
    }

    private static class BinaryOpPosition {
        NodeType type;
        int pos;

        BinaryOpPosition(NodeType type, int pos) {
            this.type = type;
            this.pos = pos;
        }
    }

    private static BinaryOpPosition getBinaryOpPosition(String input) throws ParseExprException {
        int balance = 0;
        BinaryOpPosition lastOpPos = null;
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if (ch == '(') {
                balance++;
            }
            if (ch == ')') {
                balance--;
            }
            if (balance == 0) {
                if (ch == '+' || ch == '-') {
                    return new BinaryOpPosition(ch == '+' ? PL : MIN, i);
                }
                if ((ch == '*' || ch == '/') && (lastOpPos == null || lastOpPos.type == EXP)) {
                    lastOpPos = new BinaryOpPosition(ch == '*' ? MULT : DIV, i);
                }
                if (ch == '^' && lastOpPos == null) {
                    lastOpPos = new BinaryOpPosition(EXP, i);
                }
            }
        }
        if (balance != 0) {
            throw new ParseExprException(String.format("Incorrect parentheses sequence in substring : %s", input));
        }
        return lastOpPos;
    }

    private CalcTree(NodeType type, Double result) {
        this.type = type;
        this.result = result;
    }

    private CalcTree(NodeType type, ArrayList<CalcTree> children) {
        this.type = type;
        this.children = children;
    }

}
