import junit.framework.Assert;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Test;
import ru.tiloaria.*;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.cos;

public class CalculatorTests {
    private static final double delta = 0.000001;

    private void runTest(String expr, Double res) {
        runTest(expr, res, new HashMap<String, Double>());
    }

    private void runTest(String expr, Double res, Map<String, Double> map) {
        CalcTree sumTree;
        try {
            sumTree = parseExpr(expr);
            assert sumTree != null;
            Assert.assertEquals(res, sumTree.eval(map), delta);
        } catch (ParseExprException | MathException e) {
            System.err.println(e.getMessage());
        }
    }

    private void runBrokenTest(String expr) throws MathException, ParseExprException {
        CalcTree tree = parseExpr(expr);
        tree.eval(new HashMap<String, Double>());
    }

    private static CalcTree parseExpr(String str) throws ParseExprException {
        CharStream input = CharStreams.fromString(str);
        ExpressionsLexer lexer = new ExpressionsLexer(input);
        CommonTokenStream lex = new CommonTokenStream(lexer);
        ExpressionsParser parser = new ExpressionsParser(lex);
        parser.removeErrorListeners();
        CalcTree res = parser.expr().tree;
        if (res == null) {
            throw new ParseExprException("Can't parse expression");
        }
        return res;
    }

    @Test
    public void constVal() {
        runTest("1", 1.0);
        runTest("-2", -2.0);
    }

    @Test
    public void binOperationTests() {
        runTest("2+3", 5.0);
        runTest("2*3", 6.0);
        runTest("2^3", 8.0);
        runTest("2/3", 2.0 / 3.0);
        runTest("2.0+2.0", 4.0);
        runTest("2.0*4.0", 8.0);
        runTest("12.0^2.0", 144.0);
        runTest("22.0/3.0", 22.0 / 3.0);
    }

    @Test
    public void unarOperationTests() {
        runTest("ln(1)", 0.0);
        runTest("sin(0)", 0.0);
    }

    @Test
    public void priorityTests() {
        runTest("2^2^3", 256.0);
        runTest("(2+2)*2", 8.0);
        runTest("2/(12/3)", 0.5);
        runTest("7-(3*2)", 1.0);
        runTest("7-3*2", 1.0);
    }

    @Test
    public void replaceVariablesTests() {
        HashMap<String, Double> map = new HashMap<>();
        map.put("y", 2.0);
        runTest("y+2", 4.0, map);
    }

    @Test(expected = MathException.class)
    public void mathExceptionsTests() throws MathException, ParseExprException {
        runBrokenTest("2/0");
        runBrokenTest("ln(-1)");
        runBrokenTest("myUndeclaredFunc(3)");
    }

    @Test(expected = MathException.class)
    public void recursionTests() throws MathException, ParseExprException {
        EquationHandler h = new EquationHandler();
        h.eval("$r(x)=f(x)");
        h.eval("$f(x)=r(x)");
        h.eval("r(x)");
    }

    @Test(expected = ParseExprException.class)
    public void parseExceptionsTests() throws MathException, ParseExprException {
        runBrokenTest("(3+2");
        runBrokenTest("1^-1");
        runBrokenTest("y+5");
        runBrokenTest("2y+5");
        runBrokenTest("2 ** 2.0");
    }

    @Test
    public void addMacrosTests() throws MathException, ParseExprException {
        EquationHandler h = new EquationHandler();
        h.eval("$r(x)=x+2");
        Assert.assertEquals("x0 = 8", h.eval("r(2)*2"));
        h.eval("1+1");
        Assert.assertEquals("x2 = 12", h.eval("r(r(x1))*2"));//2+2+2*2
    }

    @Test
    public void createVariableTest() throws MathException, ParseExprException {
        EquationHandler h = new EquationHandler();
        h.eval("y = 5 * 5");
        Assert.assertEquals("x0 = " + cos(25), h.eval("cos(y)"));
    }


    @Test
    public void changeVariableTest() throws MathException, ParseExprException {
        EquationHandler h = new EquationHandler();
        h.eval("2 + 2");
        Assert.assertEquals("x0 = 9", h.eval("x0 = 3 * 3"));
    }

    @Test
    public void changeMacrosTests() throws MathException, ParseExprException {
        EquationHandler h = new EquationHandler();
        h.eval("$r(x)=x+2");
        Assert.assertEquals("x0 = 0.75", h.eval("r(1)/4"));
        h.eval("$r(x)=x+0.5");
        Assert.assertEquals("x1 = 2.5", h.eval("r(2)"));
    }


    @Test
    public void numerationTests() throws MathException, ParseExprException {
        EquationHandler h = new EquationHandler();
        Assert.assertEquals("x0 = 0", h.eval("sin(Pi)"));
        h.eval("$r(x0)=x0^2+2");
        Assert.assertEquals("x1 = 33", h.eval("r(3)*3"));
        h.eval("2+2");
        Assert.assertEquals("x3 = 64", h.eval("x2^3"));
    }
}
