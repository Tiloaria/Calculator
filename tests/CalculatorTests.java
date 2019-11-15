import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.tiloaria.CalcTree;
import ru.tiloaria.EquationHandler;
import ru.tiloaria.MathException;
import ru.tiloaria.ParseExprException;

import java.util.HashMap;

public class CalculatorTests {
    private static final double delta = 0.000001;

    void runTest(String expr, Double res) {
        runTest(expr, res, new HashMap<>());
    }

    void runTest(String expr, Double res, HashMap<String, Double> map) {
        CalcTree sumTree = null;
        try {
            sumTree = CalcTree.parseFrom(expr, map);
            assert sumTree != null;
            Assert.assertEquals(res, sumTree.eval(), delta);
        } catch (ParseExprException | MathException e) {
            e.printStackTrace();
        }
    }

    void runBrokenTest(String expr) throws MathException, ParseExprException {
        CalcTree.parseFrom(expr, new HashMap<>());
    }

    @Before
    public void refreshObjects() {
        EquationHandler.clearVariables();
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
        runTest("2/3", 2.0/3.0);
        runTest("2.0+2.0", 4.0);
        runTest("2.0*4.0", 8.0);
        runTest("12.0^2.0", 144.0);
        runTest("22.0/3.0", 22.0/3.0);
        //runTest("sin(Pi)", 0.0);
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
        runTest("4/(12/3)", 1.0);
    }

    @Test
    public void replaceVariablesTests() {
        HashMap<String, Double> map = new HashMap<>();
        map.put("y", 2.0);
        runTest("y+2", 4.0, map);
    }

    @Test(expected = MathException.class)//TODO - check why it don't throw exceptions
    public void mathExceptionsTests() throws MathException, ParseExprException {
        runBrokenTest("2/0");
        runBrokenTest("ln(-1)");
    }

    @Test(expected = ParseExprException.class)
    public void parseExceptionsTests() throws MathException, ParseExprException {
        runBrokenTest("(3+2");
        runBrokenTest("1^-1");
        runBrokenTest("y+5");
    }

    @Test
    public void addMacrosTests() throws MathException, ParseExprException {
        new EquationHandler("$r(x)=x+2");
        EquationHandler h1 = new EquationHandler("r(2)*2");
        Assert.assertEquals("x0 = 6.0", h1.eval());
        new EquationHandler("2+2");
        EquationHandler h2 = new EquationHandler("r(r(2))*2");
        Assert.assertEquals("x2 = 8.0", h2.eval());
    }

    @Test
    public void multiTests() throws MathException, ParseExprException {
        EquationHandler h0 = new EquationHandler("sin(Pi)");
        Assert.assertEquals("x0 = " + Math.sin(Math.PI), h0.eval());
        new EquationHandler("$r(x0)=x0^2+2");
        EquationHandler h1 = new EquationHandler("r(3)*3");
        Assert.assertEquals("x1 = 15.0", h1.eval());
        new EquationHandler("2+2");
        EquationHandler h3 = new EquationHandler("x2^3");
        Assert.assertEquals("x3 = 64.0", h3.eval());
    }
}
