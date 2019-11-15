package ru.tiloaria;

public class ParseExprException extends Exception {
    public ParseExprException() {
        super("Parse exception in expression");
    }

    public ParseExprException(String s) {
        super(s);
    }
}
