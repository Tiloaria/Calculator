grammar Expressions;

@header {
import ru.tiloaria.NodeType;
import ru.tiloaria.VariableTree;
import org.antlr.v4.runtime.misc.Pair;
import java.util.Arrays;
}

macros returns [CalcTree macrTree]
    : DOL VAR LBR VAR RBR EQ expr {
        $macrTree = $expr.tree;
    };

decl returns [VariableTree var]
    : VAR EQ expr {
        $var = new VariableTree($VAR.text, $expr.tree);
    };

expr returns [CalcTree tree]
    : exprMult PLUS expr {
        $tree = new CalcTree(NodeType.PLUS, new ArrayList<CalcTree>(Arrays.asList($exprMult.tree, $expr.tree)));
    }
    | exprMult MINUS expr {
        $tree = new CalcTree(NodeType.MINUS, new ArrayList<CalcTree>(Arrays.asList($exprMult.tree, $expr.tree)));
    }
    | exprMult {
        $tree = $exprMult.tree;
    };

exprMult returns [CalcTree tree]
    : exprPow MULT expr {
        $tree = new CalcTree(NodeType.MULT, new ArrayList<CalcTree>(Arrays.asList($exprPow.tree, $expr.tree)));
    }
    | exprPow DIV expr {
        $tree = new CalcTree(NodeType.DIV, new ArrayList<CalcTree>(Arrays.asList($exprPow.tree, $expr.tree)));
    }
    | exprPow {
        $tree = $exprPow.tree;
    };

exprPow returns [CalcTree tree]
    : exprOther POW expr {
        $tree = new CalcTree(NodeType.POW, new ArrayList<CalcTree>(Arrays.asList($exprOther.tree, $expr.tree)));
    }
    | exprOther {
        $tree = $exprOther.tree;
    };

exprOther returns [CalcTree tree]
    : SIN exprWithBrac {
        $tree = new CalcTree(NodeType.SIN, new ArrayList<CalcTree>(Arrays.asList($exprWithBrac.tree)));
    }
    | COS exprWithBrac {
        $tree = new CalcTree(NodeType.COS, new ArrayList<CalcTree>(Arrays.asList($exprWithBrac.tree)));
    }
    | LN exprWithBrac {
        $tree = new CalcTree(NodeType.LN, new ArrayList<CalcTree>(Arrays.asList($exprWithBrac.tree)));
    }
    | VAR exprWithBrac {
        $tree = new CalcTree(NodeType.FUNC, $VAR.text);
    }
    | exprWithBrac {
        $tree = $exprWithBrac.tree;
    }
    | VAR {
        $tree = new CalcTree(NodeType.VAR, $VAR.text);
    }
    | VAL {
        $tree = new CalcTree(NodeType.VAL, $VAL.text);
    };

exprWithBrac returns [CalcTree tree]
    : LBR expr RBR  {
        $tree = $expr.tree;
    };

DOL: '$';
LBR:'(';
RBR:')';
EQ: '=';
PLUS: '+';
MINUS: '-';
MULT: '*';
DIV: '/';
SIN: 'sin';
COS: 'cos';
LN: 'ln';
POW: '^';
VAL: '-'?[0-9]+('.'[0-9]*)?;
VAR: [a-zA-Z][a-zA-Z0-9]*;
WS: [ \r\n\t] + -> skip;