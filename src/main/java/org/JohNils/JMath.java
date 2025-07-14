package org.JohNils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JMath {
    private final static Map<String, Double> Variables = new HashMap<>();
    private final static Map<String, String> Functions = new HashMap<>();

    private static final Pattern VariableDefine = Pattern.compile("\\w+\\s*=\\s*-?\\d+(\\.\\d+)?([eE][-+]?\\d+)?");
    private static final Pattern FunctionDefine = Pattern.compile("^\\s*\\w+\\s*\\(\\s*\\w+\\s*\\)\\s*=");

    private static final List<Character> numerals = List.of('-','0','1','2','3','4','5','6','7','8','9', '.', ',');

    private static final String varAssign = "\\w+\\s*=\\s*-?\\d+(\\.\\d+)?([eE][-+]?\\d+)?";
    private static final String funcDef = "\\w+\\s*\\(\\s*\\w+\\s*\\)\\s*=\\s*[^;]+";
    private static final String mathExpr = "[-+*/^()\\w\\d\\.]+";

    private static final String combinedPattern = ".*(" + varAssign + "|" + funcDef + "|" + mathExpr + ").*";

    private static final Pattern containMathPattern = Pattern.compile(combinedPattern);
    private static final List<String> embeddedFunctions = List.of(
            "sin", "cos", "tan",
            "asin", "acos", "atan",
            "log", "ln", "sqrt",
            "abs", "floor", "ceil", "round",
            "exp", "sign"
    );

    private static boolean print = true;

    public static void eval(final String str) {
        Variables.clear();
        Functions.clear();
        setEmbedded();
        print = true;

        String[] sections = str.split(";");

        for (String section: sections) {
            evalSection(section.trim());
        }
    }
    private static void setEmbedded() {
        Variables.put("e", Math.E);
        Variables.put("pi", Math.PI);     // lowercase for consistency
        Variables.put("π", Math.PI);      // symbol alias for pi
        Variables.put("tau", 2 * Math.PI); // τ = 2π
        Variables.put("phi", 1.6180339887);  // golden ratio (phi)
        Variables.put("φ", 1.6180339887);  // golden ratio (phi)

        for (String function: embeddedFunctions) {
            Functions.put(function, "x:x"); //x:x for fallback
        }
    }

    public static boolean containsMath(String input) {
        return containMathPattern.matcher(input).matches();
    }

    public static void evalSection(String section) {

        if (VariableDefine.matcher(section).find()) {
            String[] args = section.split("=");

            Variables.put(args[0].trim(), evalExpression(args[1].trim()));

            return;
        }

        if (FunctionDefine.matcher(section).find()) {
            String[] args = section.split("=");
            if (args.length != 2) return;

            String name = args[0].split("\\(")[0].trim();
            String CHAR = args[0].split("\\(")[1].split("\\)")[0];
            String Expression = args[1];

            Functions.put(name, CHAR.trim() + ":" + Expression.trim());

            return;
        }

        double solution = evalExpression(section);
        if(print) Main.window.textField.setEval(" = " + solution);
    }

    public static double evalExpression(final String str) {
        char[] chars = str.toCharArray();
        ArrayList<Token> tokens = new ArrayList<>();


        //Tokenization
        for (int i = 0;i < chars.length;i++) {

            switch (chars[i]) {
                case '0','1','2','3','4','5','6','7','8','9' -> {
                    StringBuilder valueBuilder = new StringBuilder();
                    valueBuilder.append(chars[i]);

                    int peek = 1;
                    while (i + peek < chars.length && numerals.contains(chars[i + peek])) {
                        valueBuilder.append(chars[i + peek]);
                        peek++;
                    }
                    i += peek - 1;

                    tokens.add(new Token(TokenType.VALUE, valueBuilder.toString()));
                }

                case '+' -> tokens.add(new Token(TokenType.ADD, null));
                case '-' -> {
                    if (i + 1 < chars.length && numerals.contains(chars[i + 1])) {

                        StringBuilder valueBuilder = new StringBuilder();
                        valueBuilder.append(chars[i]);

                        int peek = 1;
                        while (i + peek < chars.length && numerals.contains(chars[i + peek])) {
                            valueBuilder.append(chars[i + peek]);
                            peek++;
                        }
                        i += peek - 1;

                        tokens.add(new Token(TokenType.VALUE, valueBuilder.toString()));
                        continue;
                    }

                    tokens.add(new Token(TokenType.SUB, null));
                }
                case '*' -> tokens.add(new Token(TokenType.MUL, null));
                case '/' -> tokens.add(new Token(TokenType.DIV, null));
                case '^' -> tokens.add(new Token(TokenType.EXP, null));
                case '(' -> tokens.add(new Token(TokenType.BRO, null));
                case ')' -> tokens.add(new Token(TokenType.BRC, null));

                default -> {
                    if (!(
                            (chars[i] >= 'A' && chars[i] <= 'Z') ||
                                    (chars[i] >= 'a' && chars[i] <= 'z') ||
                                    chars[i] == '(' ||
                                    chars[i] == ')'
                    )) continue;

                    StringBuilder nameBuilder = new StringBuilder();

                    nameBuilder.append(chars[i]);

                    int peek = 1;
                    while (i + peek < chars.length && ((chars[i + peek] >= 'A' && chars[i + peek] <= 'Z') || (chars[i + peek] >= 'a' && chars[i + peek] <= 'z') || chars[i + peek] == '(' || chars[i + peek] == ')' || numerals.contains(chars[i + peek]))) {
                        nameBuilder.append(chars[i + peek]);
                        peek++;
                    }
                    i += peek - 1;

                    String[] check = nameBuilder.toString().split("\\(");

                    if (check.length > 1) {
                        try {
                            tokens.add(new Token(TokenType.FNC, check[0] + ":" + check[1].split("\\)")[0]));
                        }
                        catch (IndexOutOfBoundsException e) {
                            print = false;
                        }
                        continue;
                    }

                    i -= (int) check[0].chars().filter(ch -> ch == ')').count();
                    tokens.add(new Token(TokenType.VAR, check[0].replace(")","")));
                }
            }
        }


        //Treeification
        ExprNode root = getNode(tokens);
        return solve(root);
    }

    private static double solve(ExprNode node) {
        if (node instanceof ValueNode) return ((ValueNode) node).value;

        if (!(node instanceof OpNode)) {
            print = false;
            return 0;
        }

        OpNode opNode = (OpNode) node;

        double left = solve(opNode.left);
        double right = solve(opNode.right);

        return switch (opNode.operator) {
            case ADD -> left + right;
            case SUB -> left - right;
            case MUL -> left * right;
            case DIV -> left / right;
            case EXP -> Math.pow(left, right);

            default -> 0.0;
        };
    }

    private static ExprNode getNode(List<Token> tokens) {

        List<Token> listwithoutBR = tokens.stream()
                .filter(token -> token.type() != TokenType.BRC && token.type() != TokenType.BRO)
                .toList();

        if (listwithoutBR.size()==1) {
            return switch (listwithoutBR.getFirst().type()) {
                case VALUE -> new ValueNode(Double.parseDouble(listwithoutBR.getFirst().value()));

                case VAR -> {
                    if(Variables.get(listwithoutBR.getFirst().value()) == null) {
                        print = false;
                        yield new ValueNode(0);
                    }
                    yield new ValueNode(Variables.get(listwithoutBR.getFirst().value()));
                }

                case FNC -> {
                    double f = function(listwithoutBR.getFirst().value());
                    yield new ValueNode(f);
                }

                default -> null;
            };
        }

        ArrayList<Integer> levels = new ArrayList<>();

        int level = 0;
        for (int i = tokens.size()-1;i >= 0;i--) {
            if  (tokens.get(i).type() == TokenType.BRC) {
                levels.addFirst(null);
                level += 4;
                continue;
            }
            if (tokens.get(i).type() == TokenType.BRO) {
                levels.addFirst(null);
                level -= 4;
                continue;
            }


            switch (tokens.get(i).type()) {
                case ADD, SUB -> levels.addFirst(level + 1);
                case MUL, DIV -> levels.addFirst(level + 2);
                case EXP -> levels.addFirst(level + 3);

                default -> levels.addFirst(null);
            }
        }

        int lowest = Integer.MAX_VALUE;
        int lowestIndex = -1;
        for (int i = levels.size()-1;i > 0;i--) {
            if  (levels.get(i) == null) continue;

            if (levels.get(i) == 1) {
                lowest = 1;
                lowestIndex = i;
                break;
            }
            if (levels.get(i) < lowest) {
                lowest = levels.get(i);
                lowestIndex = i;
            }
        }

        if (lowestIndex == -1) return null;

        return new OpNode(tokens.get(lowestIndex).type(),getNode(tokens.subList(0, lowestIndex)), getNode(tokens.subList(lowestIndex + 1, tokens.size())));
    }

    private static double function(String fnc) {
        //fnc = "name:number or expression"
        String[] args = fnc.split(":");
        String f = Functions.get(args[0]);
        if (f == null || args.length != 2) {
            print = false;
            return 0;
        }
        //Embedded
        if (embeddedFunctions.contains(args[0]) && f.equals("x:x")) {
            double value = evalExpression(args[1]);

            return switch (args[0]) {
                case "sin"   -> Math.sin(value);
                case "cos"   -> Math.cos(value);
                case "tan"   -> Math.tan(value);
                case "asin"  -> Math.asin(value);
                case "acos"  -> Math.acos(value);
                case "atan"  -> Math.atan(value);
                case "log"   -> Math.log10(value);
                case "ln"    -> Math.log(value);
                case "sqrt"  -> Math.sqrt(value);
                case "abs"   -> Math.abs(value);
                case "floor" -> Math.floor(value);
                case "ceil"  -> Math.ceil(value);
                case "round" -> Math.round(value);
                case "exp"   -> Math.exp(value);
                case "sign"  -> Math.signum(value);
                default      -> value; //x:x
            };
        }

        String[] function = f.split(":");

        Double s = Variables.get(function[0]);

        Variables.put(function[0],evalExpression(args[1]));

        double solution = evalExpression(function[1]);

        Variables.remove(function[0]);
        if (s != null) Variables.put(function[0], s);

        return solution;
    }
}

record Token(TokenType type,String value) {

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", value='" + value + '\'' +
                '}';
    }
}

enum TokenType {
    VALUE,
    ADD, // +
    SUB, // -
    MUL, // *
    DIV, // /
    EXP, // ^
    BRO, // (
    BRC, // )
    VAR, // for Example: a=2; a
    FNC; // for Example: f(x)=x; f(2)
}

abstract class ExprNode {

}

class ValueNode extends ExprNode {
    double value;
    ValueNode(double value) { this.value = value; }

    @Override
    public String toString() {
        return "ValueNode{" +
                "value=" + value +
                '}';
    }
}

class OpNode extends ExprNode {
    TokenType operator;
    ExprNode left;
    ExprNode right;
    OpNode(TokenType operator, ExprNode left, ExprNode right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return "OpNode{" +
                "operator=" + operator +
                ", left=" + left +
                ", right=" + right +
                '}';
    }
}