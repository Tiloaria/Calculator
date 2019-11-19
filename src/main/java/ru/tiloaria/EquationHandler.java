package ru.tiloaria;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.HashMap;
import java.util.Map;

public class EquationHandler {
    private HashMap<String, Double> variables = new HashMap<>();
    private Map<String, Macros> macros = new HashMap<>();//TODO create class MyMarcos with clearer behavior
    private int newVarNameNum;

    public EquationHandler() {
        setConstants();
    }

    /**
     * Parse expression via creating parser
     * @param expr expression with macros or operations
     * @return string with result for print
     * @throws ParseExprException in case of incorrect syntax
     * @throws MathException in case of impossibility to calculate
     */
    public String eval(String expr) throws ParseExprException, MathException {
        if (isMacros(expr)) {
            return addMacros(expr);
        }
        expr = replaceMacros(expr);//TODO make correct replacement
        VariableTree newVar = parseVar(expr);
        if (newVar != null) {
            return addVar(newVar);
        }
        CalcTree tree = parseExpr(expr);
        if (tree != null) {
            return addVar(new VariableTree(genNewVarName(), tree));
        }
        throw new ParseExprException("Incorrect requests syntax");
    }

    private String genNewVarName() {
        while (variables.containsKey("x" + String.valueOf(newVarNameNum))) {
            newVarNameNum++;
        }
        return "x" + newVarNameNum;
    }

    private static String format(double d) {
        if ((d - (long) d) > Eps) {
            return String.format("%s", d);
        }
        return String.format("%d", (long) d);
    }

    private static ExpressionsParser getParser(String str) {
        CharStream input = CharStreams.fromString(str);
        ExpressionsLexer lexer = new ExpressionsLexer(input);
        CommonTokenStream lex = new CommonTokenStream(lexer);
        ExpressionsParser parser = new ExpressionsParser(lex);
        parser.removeErrorListeners();
        return parser;
    }

    private static CalcTree parseExpr(String str) {
        return getParser(str).expr().tree;
    }

    private static VariableTree parseVar(String str) {
        return getParser(str).decl().var;
    }

    private static boolean isMacros(String str) {
        return getParser(str).macros().macrTree != null;
    }

    private String addVar(VariableTree var) throws MathException, ParseExprException {
        Double res = var.tree.eval(variables);
        variables.put(var.variable, res);
        return var.variable + " = " + format(res);
    }

    private String addMacros(String expr) {
        int eqPlace = expr.indexOf('=');
        int funNameEnd = expr.indexOf('(');
        String macrName = expr.substring(1, funNameEnd);
        String varName = expr.substring(funNameEnd + 1, eqPlace - 1);
        String replName = expr.substring(eqPlace + 1);
        macros.put(macrName, new Macros(macrName, varName, '(' + replName + ')'));
        return '$' + macrName;
    }

    private static final double Eps = 1e-7;

    private String replaceMacros(String expr) throws ParseExprException, MathException {
        final int operationsLimit = 100;
        int numOfOperations = 0;
        boolean wereChanges = true;
        while (wereChanges) {
            wereChanges = false;
            for (Macros macros : macros.values()) {
                if (expr.contains(macros.getName() + '(')) {
                    wereChanges = true;
                    expr = macros.getReplacedString(expr);
                    numOfOperations++;
                }
            }
            if (numOfOperations > operationsLimit) {
                throw new MathException("Expression has unlimited recursion or has to mach uses of macros");
            }
        }
        return expr;
    }

    private void setConstants() {
        variables.put("Pi", Math.PI);
        variables.put("E", Math.E);
    }
}
