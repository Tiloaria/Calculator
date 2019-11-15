package ru.tiloaria;

public class ParseExprException extends Exception {
    public ParseExprException() {
        super("Can't parse expression");
    }

    public ParseExprException(String s) {
        super(s);
    }
}
