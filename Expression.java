import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;



/**
 * Abstract base class representing a boolean expression.
 * Supports binary operations (AND, OR, XOR) and unary operation (NOT),
 * as well as variables.
 *
 * Subclasses must implement:
 * - {@link #evaluateExpression()} for computing the expression value.
 * - {@link #subExprToString(Operator)} for converting to a string with correct precedence.
 * - {@link #retriveSubExpression(List)} for retrieving subexpressions in the AST.
 */
public abstract class Expression {
    
    // ----- ABSTRACT METHODS ----------

    //Helper to toString method
    protected abstract String subExprToString(Operator previousOp);

    //Helper to getSubExpressions()
    protected abstract void retriveSubExpression(List<Expression> breakdown);

    /**
     * @return The size of expression, how many layers it has
     */
    public abstract int expressionSize();
    
    /**
     * Evaluates expression based on the variables current value
     * @return 1 or 0
     */
    public abstract int evaluateExpression();


    // --------- UTILITY ----------------
    
    @Override
    public String toString(){
        return this.subExprToString(Operator.OR); //Starts with 'OR' becuase it got the lowest precedence
    }

    /**
     * Creates a list of subExpressions from caller expression, the list will be sorted by variables coming first
     * in lexographical order and then subexpression based on expression size
     * @return List of sub expressions
     */
    public List<Expression> getSubExpressions(){
        List<Expression> breakdown = new ArrayList<>();
        this.retriveSubExpression(breakdown);

        //Sorts the list of subexpressions for a nicer presentation, variables should always come first
        breakdown.sort(new Comparator<Expression>() {
            @Override
            public int compare(Expression expr1, Expression expr2){
                if((expr1 instanceof Variable) && (expr2 instanceof Variable)){
                    return ((Variable) expr1).name.compareTo(((Variable) expr2).name);
                }
                return Integer.compare(expr1.expressionSize(), expr2.expressionSize());   
            }
        });

        return breakdown;
    }

    /**
     * Returns a list containing all the variables in caller expression
     * @return List of variables 
     */
    public List<Variable> getVariables(){
        List<Variable> variables = new ArrayList<>();
        List<Expression> subExpressions = this.getSubExpressions();

        for(Expression expr : subExpressions){
            if(expr instanceof Variable) variables.add((Variable) expr);
            //Could add a break here bacues it's always sorted
        }
        return variables;
    }

    
    
    // ----- SUB CLASSES ---------------

    /**
    * Represents a binary operation between two expressions.
    */
    static class BinaryOp extends Expression{
        private Operator op;
        private Expression left;
        private Expression right;

        BinaryOp(Operator op, Expression left, Expression right){
            this.op = op;
            this.left = left;
            this.right = right;
        }

        @Override
        protected String subExprToString(Operator previousOp){
            String result = this.left.subExprToString(this.op) + " " + this.op.symbol + " " + this.right.subExprToString(this.op);
            
            if(previousOp.precedence > this.op.precedence) return "(" + result + ")";

            return result;
            
        }

        @Override
        protected void retriveSubExpression(List<Expression> breakdown){
            breakdown.add(this);
            right.retriveSubExpression(breakdown);
            left.retriveSubExpression(breakdown);
        }

        @Override
        public int evaluateExpression(){
            int l = this.left.evaluateExpression();
            int r = this.right.evaluateExpression();
            return this.op.eval(l, r);
        }

        @Override
        public int expressionSize(){
            return 1 + this.left.expressionSize() + this.right.expressionSize();
        }

    }

    /**
    * Represents a unary operation applied to a single expression.
    */
    static class UnaryOp extends Expression{
        Operator op;
        Expression expr;

        UnaryOp(Operator op, Expression expr){
            this.op = op;
            this.expr = expr;
        }

        @Override
        protected String subExprToString(Operator previousOp){
            String result = this.op.symbol + this.expr.subExprToString(this.op);
            
            //Check if the higher level expression had lower precendence
            if(previousOp.precedence > this.op.precedence)
                 return "(" + result + ")";
            
            return  result;
        }

        @Override
        protected void retriveSubExpression(List<Expression> breakdown){
            breakdown.addFirst(this);
            expr.retriveSubExpression(breakdown);
        }


        @Override
        public int evaluateExpression(){
            return this.op.eval(this.expr.evaluateExpression(), 0); // Ignores the 0 value in NOT enum
        }

        @Override
        public int expressionSize(){
            return 1 + this.expr.expressionSize();
        }
    }

    /**
    * Represents a variable, will always be a leaf node in expression AST
    */
    static class Variable extends Expression{
        
        String name;
        int currentValue;

        Variable(String name){
            this.name = name;
            this.currentValue = 0;
        }

        public void setCurrentValue(int currentValue) {
            this.currentValue = currentValue;
        }

        @Override
        protected String subExprToString(Operator previousOp){
            //Always leaf node
            return this.name;
        }

        @Override
        protected void retriveSubExpression(List<Expression> breakdown){
            if(breakdown.contains(this)) return;
            else breakdown.add(this);
        }

        @Override
        public int evaluateExpression(){
            return this.currentValue;
        }

        @Override
        public int expressionSize(){
            return 1;
        }
    }


    // ------- PARSER -------------

    //Returns string with completed regex to tokenize operators, paranthesis and variables
    private static String getRegex(){

        StringBuilder operatorSymbols = new StringBuilder();
        for(Operator op : Operator.allOperators()){
            operatorSymbols.append(op.symbol);
        }
        String operators = operatorSymbols.toString();
        return "(?=[" + operators + "()])|(?<=[" + operators + "()])";
    }

    /**
     * Parses input String into an expression, tokenizes string and then uses the Shunting yard algorithm to build AST
     * @param input - Written expression
     * @return Assembeled expression in the form of a AST
     */
    public static Expression parseToExpression(String input){
        
        if(input == null || input.trim().isEmpty()) throw new IllegalArgumentException("Input expression cannot be empty");


        //Tokenizes the input string, keeps variables with full names and operators separete
        String[] tokens = input.replaceAll("\\s+", "").split(getRegex());

        //Shunting yard algorithm
        List<Expression> output = new ArrayList<>();
        Stack<String> operators = new Stack<>();

        //Used to not add duplicate variables
        HashMap<String, Variable> addedVariables = new HashMap<>();
        
        //Building expression tree with shunnting yard algorithm
        for(String token : tokens){
            
            Operator currentOp = Operator.fromSymbol(token);

            //If token is an operator
            if(currentOp != null){
                while(!operators.isEmpty()){
                    
                    String topOfStack = operators.peek();

                    if(topOfStack.contains("(")) break;

                    Operator topOfStackOp = Operator.fromSymbol(topOfStack); //Convert to operator enum

                    if(
                        (topOfStackOp.precedence > currentOp.precedence)
                        || topOfStackOp.precedence == currentOp.precedence && currentOp.leftAssoc
                    ) 
                    {
                        addOpExprToOutput(Operator.fromSymbol(operators.pop()), output);
                    }
                    else break;
                }
                
                operators.push(token);
            }
            //If token is openning paranthesis
            else if(token.contains("(")) operators.push(token);
            //If token is closing paranthesis
            else if(token.contains(")")){

                if(operators.isEmpty() || !operators.contains("(")) 
                    throw new IllegalArgumentException("Can't begin with closening parenthesis");

                while(!operators.peek().contains("(")){
                    addOpExprToOutput(Operator.fromSymbol(operators.pop()), output);
                    if(operators.isEmpty()) throw new IllegalArgumentException("Uneven parenthesis");
                }
                operators.pop(); //Removes "("
            }
            //Else token is a variable
            else{
                if(addedVariables.containsKey(token)) output.add(addedVariables.get(token));
                else{  
                    Variable var = new Variable(token);
                    output.add(var);
                    addedVariables.put(token, var);
                }
            }
        }

        while(!operators.isEmpty()){
            if(operators.peek().contains("(") || operators.contains(")"))
                throw new IllegalArgumentException("Uneven parenthesis");
            addOpExprToOutput(Operator.fromSymbol(operators.pop()), output);
        }

        if(output.isEmpty()) throw new IllegalArgumentException("Input expression cannot be empty");
        return output.removeLast();


        //Behöver se till att variabler med samma namn inte läggs till två gånger
        // Kanske kan använda en hashMap?
    }

    //Will only enter this if operatorToken is valid operator, helper to parseToExpression method
    private static void addOpExprToOutput(Operator op, List<Expression> output){
        
        Expression expr = null;
        
        if(op.arity == 1){
            if(output.size() < op.arity) throw new IllegalArgumentException("Invalid Input, not enough operands");
            expr = new UnaryOp(op, output.removeLast());
            output.add(expr);
        }
        else if(op.arity == 2){
            if(output.size() < op.arity) throw new IllegalArgumentException("Invalid Input, not enough operands");
            Expression exprR = output.removeLast();
            Expression exprL = output.removeLast();
            expr = new BinaryOp(op, exprL, exprR);
            output.add(expr);
        }
    }
}
