package ru.tiloaria;

import java.util.ArrayList;
import java.util.Map;

public class CalcTree {
    private NodeType type;
    private String value = null;
    private ArrayList<CalcTree> children = new ArrayList<>();

    /**
     * @return result of expression if it's possible to evaluate it, otherwise null
     * @throws MathException in case of incorrect math action
     */
    public Double eval(Map<String, Double> variables) throws MathException, ParseExprException {
        Double res1 = children.size() > 0 ? children.get(0).eval(variables) : null;
        Double res2 = children.size() > 1 ? children.get(1).eval(variables) : null;
        Double result = null;
        switch (type) {
            case VAL:
                result = Double.valueOf(value);
                break;
            case VAR:
                if (!variables.containsKey(value)) {
                    throw new ParseExprException("Variable '" + value + "' is not in scope");
                }
                result = variables.get(value);
                break;
            case SIN:
                result = Math.sin(res1);
                break;
            case COS:
                result = Math.cos(res1);
                break;
            case LN:
                if (Math.abs(res1) < 0) {
                    throw new MathException("Getting log from number < 0 prohibited");
                }
                result = Math.log(res1);
                break;
            case PLUS:
                result = res1 + res2;
                break;
            case MINUS:
                result = res1 - res2;
                break;
            case MULT:
                result = res1 * res2;
                break;
            case DIV:
                if (Math.abs(res2) < Eps) {
                    throw new MathException("Dividing to 0 is strictly prohibited");
                }
                result = res1 / res2;
                break;
            case POW:
                result = Math.pow(res1, res2);
                break;
            case FUNC:
                throw new MathException("Function '" + value + "' is not declared");
        }
        if (Double.isNaN(result) || Double.isInfinite(result)) {
            throw new MathException("Invalid operation");
        }
        return result;
    }

    public CalcTree(NodeType type, ArrayList<CalcTree> children) {
        this.type = type;
        this.children = children;
    }

    public CalcTree(NodeType type, String value) {
        this.type = type;
        this.value = value;
    }

    private static final double Eps = 1e-7;
}
