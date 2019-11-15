package ru.tiloaria;

import java.util.Scanner;

public class ConsoleCalculator {

    public ConsoleCalculator() {
    }

    public static void main(String[] args) {
        printUsage();
        ConsoleCalculator consoleCalculator = new ConsoleCalculator();
        while (true) {
            try {
                Scanner scanner = new Scanner(System.in);
                String expr = scanner.nextLine();
                expr = expr.replaceAll("\n|\r| ", "");
                if (expr.equals("help")) {
                    printUsage();
                }
                if (expr.equals("q")) {
                    return;
                }
                EquationHandler handler = new EquationHandler(expr);
                System.out.println(handler.eval());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void printUsage() {
        System.out.println("Hi, this is a smart calculator,\n" +
                "You can use +, -, *, /, ln(), sin(), cos(), ^\n" +
                "Please split double with '.', use all functions with ()\n" +
                "Also, you can set variables and reuse them in next expressions\n" +
                "There are some included constants such as Pi and E\n" +
                "It can also save macros, please use syntax $<MacName>(<varName>)=<replacement>\n" +
                "Print \"help\" to see this instruction and \"q\" for exit");

    }
}
