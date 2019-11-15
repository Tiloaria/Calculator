package ru.tiloaria;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class EquationHandler {
    private static HashMap<String, Double> variables = new HashMap<>();
    private static Set<Macros> macros = new HashSet<>();
    private static int newVarNameNum;
    private String result = "";

    private static String genNewVarName() {
        while (variables.containsKey("x" + String.valueOf(newVarNameNum))) {
            newVarNameNum++;
        }
        return "x" + newVarNameNum;
    }

    public EquationHandler(String expr) throws MathException, ParseExprException {
        setConstants();
        expr = expr.replaceAll("\n|\r| ", "");
        expr = replaceMacros(expr);
        if (expr.matches("[^=]+")) {
            CalcTree tree = CalcTree.parseFrom(expr, variables);
            String newVarName = genNewVarName();
            Double res = tree.eval();
            variables.put(newVarName, res);
            result = newVarName + " = " + String.valueOf(res);
            return;
        }
        if (expr.matches("[a-zA-Z][a-zA-Z0-9]+=[^=$]+")) {
            int eqPlace = expr.indexOf('=');
            CalcTree treeR = CalcTree.parseFrom(expr.substring(eqPlace + 1), variables);
            String varName = expr.substring(0, eqPlace);
            Double res = treeR.eval();
            variables.put(varName, res);
            result = varName + " = " + String.valueOf(res);
            return;
        }
        if (expr.matches("\\$[a-zA-Z][a-zA-Z0-9]*\\([a-zA-Z][a-zA-Z0-9]*\\)+=[^=$]+")) {
            int eqPlace = expr.indexOf('=');
            int funNameEnd = expr.indexOf('(');
            String macrName = expr.substring(1, funNameEnd);
            String varName = expr.substring(funNameEnd + 1, eqPlace - 1);
            String replName = expr.substring(eqPlace + 1);
            macros.add(new Macros(macrName, replName, varName));
            result = '$' + macrName;
            return;
        }
        throw new ParseExprException("Incorrect requests syntax");
    }

    public String eval() {
        return result;
    }

    public static void clearVariables() {
        variables = new HashMap<>();
        newVarNameNum = 0;
        setConstants();
        macros = new HashSet<>();
    }

    private class Macros {
        private String macrosName;
        private String replacement;
        private String varName;

        public Macros(String macrosName, String replacement, String varName) {
            this.macrosName = macrosName;
            this.replacement = replacement;
            this.varName = varName;
        }

        public String getReplacedString(String expr) throws ParseExprException {
            int firstPlace = expr.indexOf(macrosName + '(');
            int balance = 1;
            int openBrPos = firstPlace + macrosName.length();
            int closeBrPos = openBrPos;
            while (balance != 0 && closeBrPos < expr.length() - 1) {
                closeBrPos++;
                if (expr.charAt(closeBrPos) == '(') {
                    balance++;
                }
                if (expr.charAt(closeBrPos) == ')') {
                    balance--;
                }
            }
            if (closeBrPos == expr.length()) {
                throw new ParseExprException();
            }
            String val = expr.substring(openBrPos + 1, closeBrPos);
            String repl = replacement.replace(varName, val);
            return expr.substring(0, firstPlace) + repl + expr.substring(closeBrPos + 1);
        }
    }

    private String replaceMacros(String expr) throws ParseExprException {
        boolean wereChanges = true;
        while (wereChanges) {
            wereChanges = false;
            for (Macros macros : macros) {
                if (expr.contains(macros.macrosName + '(')) {
                    wereChanges = true;
                    expr = macros.getReplacedString(expr);
                }
            }
        }
        return expr;
    }

    private static void setConstants() {
        variables.put("Pi", Math.PI);
        variables.put("E", Math.E);
    }
}
