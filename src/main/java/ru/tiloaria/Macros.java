package ru.tiloaria;

class Macros {
    private String name;
    private String varName;
    private String replacement;

    Macros(String name, String varName, String replacement) {
        this.name = name;
        this.varName = varName;
        this.replacement = replacement;
    }

    String getName() {
        return name;
    }

    String getReplacedString(String expr) throws ParseExprException {
        int firstPlace = expr.indexOf(name + '(');
        int balance = 1;
        int openBrPos = firstPlace + name.length();
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
