import java.util.Collection;
import java.util.Map;

/**
 * Represents a logical operator used in boolean expressions.
 * Each operator has a symbol, precedence, associativity, arity (number of operands),
 * and a method to evaluate its boolean logic using integer values 0 (false) and 1 (true).
 */
public enum Operator {

    /** Logical NOT operator (unary). */
    NOT("!", 4, false, 1) {
        @Override
        public int eval(int a, int b) {
            return a == 1 ? 0 : 1;
        }
    },

    /** Logical AND operator (binary). */
    AND("*", 3, true, 2) {
        @Override
        public int eval(int a, int b) {
            return a + b == 2 ? 1 : 0;
        }
    },

    /** Logical XOR operator (binary). */
    XOR("⊕", 2, true, 2) {
        @Override
        public int eval(int a, int b) {
            return a + b == 1 ? 1 : 0;
        }
    },

    /** Logical OR operator (binary). */
    OR("+", 1, true, 2) {
        @Override
        public int eval(int a, int b) {
            return a + b >= 1 ? 1 : 0;
        }
    };

    /** Symbol used for the operator in expressions. */
    final String symbol;

    /** Operator precedence (higher means evaluated first). */
    final int precedence;

    /** True if the operator is left-associative. */
    final boolean leftAssoc;

    /** Number of operands the operator takes (1 = unary, 2 = binary). */
    final int arity;

    /** Map for quickly looking up operators by their symbol. */
    private static final Map<String, Operator> BY_SYMBOL =
        Map.of(
            "!", NOT,
            "*", AND,
            "⊕", XOR,
            "+", OR
        );

    /**
     * Returns the Operator corresponding to the given symbol.
     * @param s the symbol string
     * @return the Operator, or null if no operator matches
     */
    public static Operator fromSymbol(String s) {
        return BY_SYMBOL.get(s);
    }

    /** Returns all defined operators. */
    public static Collection<Operator> allOperators(){
        return BY_SYMBOL.values();
    }

    /** Constructor for enum constants. */
    Operator(String symbol, int precedence, boolean leftAssoc, int arity) {
        this.symbol = symbol;
        this.precedence = precedence;
        this.leftAssoc = leftAssoc;
        this.arity = arity;
    }

    /** Evaluates the operator with given operand values (0 or 1). */
    public abstract int eval(int a, int b);
}