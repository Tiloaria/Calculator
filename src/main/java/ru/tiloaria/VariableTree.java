package ru.tiloaria;

public class VariableTree {
    String variable;
    CalcTree tree;

    public VariableTree(String variable, CalcTree tree) {
        this.variable = variable;
        this.tree = tree;
    }
}
