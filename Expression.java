
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;



public class Expression {

    private final static int EXTRA_PRECEDENCE = 5;
    private final static int HIGHEST_PRECEDENCE = 4;
    public final static String XOR = "⊕" ;

    Variable var;
    NotOp nTerm;
    AndOp aTerm;
    XorOp xorTerm;
    OrOp oTerm;
    int precedence;
    int currentValue;

    Expression(String var){
        this.var = new Variable(var);
        this.aTerm = null;
        this.oTerm = null;
        this.nTerm = null;
        this.xorTerm = null;
        this.precedence = 0;
        this.currentValue = 0;
    }

    Expression(AndOp aTerm){
        this.aTerm = aTerm;
        this.var = null;
        this.oTerm = null;
        this.nTerm = null;
        this.xorTerm = null;
        this.precedence = HIGHEST_PRECEDENCE-1;
        this.currentValue = 0;
    }


    Expression(OrOp oTerm){
        this.oTerm = oTerm;
        this.aTerm = null;
        this.var = null;
        this.nTerm = null;
        this.xorTerm = null;
        this.precedence = HIGHEST_PRECEDENCE-3;
        this.currentValue = 0;
    }

    Expression(NotOp nTerm){
        this.nTerm = nTerm;
        this.aTerm = null;
        this.oTerm = null;
        this.var = null;
        this.xorTerm = null;
        this.precedence = HIGHEST_PRECEDENCE;
        this.currentValue = 0;
    }

    Expression(XorOp xorTerm){
        this.nTerm = null;
        this.aTerm = null;
        this.oTerm = null;
        this.var = null;
        this.xorTerm = xorTerm;
        this.precedence = HIGHEST_PRECEDENCE-2;
        this.currentValue = 0;
    }

    static class Variable{
        String varName;
        Variable(String name){
            this.varName = name;
        }
    }

    static class NotOp{
        Expression expr;

        NotOp(Expression expr){
            this.expr = expr;
        }
    }

    static class AndOp{
        Expression lTerm;
        Expression rTerm;
        AndOp(Expression lTerm, Expression rTerm){
            this.lTerm = lTerm;
            this.rTerm = rTerm;
        }
    }

    static class XorOp{
        Expression lTerm;
        Expression rTerm;
        XorOp(Expression lTerm, Expression rTerm){
            this.lTerm = lTerm;
            this.rTerm = rTerm;
        }

    }

    static class OrOp{
        Expression lTerm;
        Expression rTerm;
        OrOp(Expression lTerm, Expression rTerm){
            this.lTerm = lTerm;
            this.rTerm = rTerm;
        }
    }




    
    //Evaluates expression based on currently assigned values
    public static int evalExpr(Expression expr){

        // if expression is a variable 
        if(expr.var != null) return expr.currentValue;
        
        int result = -1; //If minus one returns then something is wrong

        if(expr.aTerm != null){
            result = evalExpr(expr.aTerm.lTerm) == 1 && evalExpr(expr.aTerm.rTerm) == 1 ? 1 : 0;  
        }
        if(expr.oTerm != null){
            result = evalExpr(expr.oTerm.lTerm) == 1 || evalExpr(expr.oTerm.rTerm) == 1 ? 1 : 0;
        }
        if(expr.xorTerm != null){
            result = evalExpr(expr.xorTerm.lTerm) + evalExpr(expr.xorTerm.rTerm) == 1 ? 1 : 0;
        }
        if(expr.nTerm != null){
            result = evalExpr(expr.nTerm.expr) == 1 ? 0 : 1;
        }
        return result;
    }

    //Checks if a sub expression contains a certain variable
    public static boolean containsVariable(String varName, Expression expr){

        if(expr.var != null && expr.var.varName.equals(varName)) return true;
        else if(expr.aTerm != null){
            return containsVariable(varName, expr.aTerm.lTerm) || containsVariable(varName, expr.aTerm.rTerm);
        }
        else if(expr.oTerm != null){
            return containsVariable(varName, expr.oTerm.lTerm) || containsVariable(varName, expr.oTerm.rTerm);
        }
        else if(expr.xorTerm != null){
            return containsVariable(varName, expr.xorTerm.lTerm) || containsVariable(varName, expr.xorTerm.rTerm);
        }
        else if(expr.nTerm != null){
            return containsVariable(varName, expr.nTerm.expr);
        }
        else return false;
    }

    //takes a string as input and assembles the expression
    public static List<Expression> parseToExpr(String input){
        List<String> tokens = tokanize(input);
        List<Expression> finalExpression = new ArrayList<>();
        List<Expression> expressionBreakdown = new LinkedList<>();

        //HasMap for making sure that reoccuring variables don't get multiple instances
        HashMap <String, Expression> variables = new HashMap<>();

        int additionalPrecedence = 0;
        for(String token : tokens){
            if(token.contains("(")){
                additionalPrecedence += EXTRA_PRECEDENCE;
            }
            else if(token.contains(")")){
                additionalPrecedence -= EXTRA_PRECEDENCE;
            }
            else if(token.contains("!")){
                Expression expr = new Expression(new NotOp(null));
                expr.precedence += additionalPrecedence;
                finalExpression.add(expr);
            }
            else if(token.contains("*")){
                Expression expr = new Expression(new AndOp(null, null));
                expr.precedence += additionalPrecedence;
                finalExpression.add(expr);
            }
            else if(token.contains(XOR)){
                Expression expr = new Expression(new XorOp(null, null));
                expr.precedence += additionalPrecedence;
                finalExpression.add(expr);
            }
            else if(token.contains("+")){
                Expression expr = new Expression(new OrOp(null, null));
                expr.precedence += additionalPrecedence;
                finalExpression.add(expr);
            }
            else{
                if(variables.containsKey(token)){
                    Expression variable = variables.get(token);
                    finalExpression.add(variable);
                }
                else{
                    Expression variable = new Expression(token);
                    variables.put(token, variable);
                    finalExpression.add(variable);
                    expressionBreakdown.add(variable);
                }
            }
        }

        
        System.out.println(tokens);
        System.out.println(finalExpression);

        //Loop that connects expressions in correct order based on precedence
        while(finalExpression.size() > 1){

            Expression currentOp = null;
            int opIndex = -1;
            int highestPrecedence = 0;

            //Finding subexpression with highest precedence 
            for(int i = 0; i < finalExpression.size(); i++){

                Expression currentExpr = finalExpression.get(i);
                int currPrec = currentExpr.precedence;

                //Finding empty operator expression with highest precedence
                if(currPrec > highestPrecedence && currentExpr.emptyOperator()){
                    highestPrecedence = currPrec;
                    opIndex = i;
                    currentOp = finalExpression.get(i);
                }
            }

            //Combining an operator with an expression reducing the total size of the final expression list
            if(currentOp.nTerm != null){
                currentOp.nTerm.expr = finalExpression.remove(opIndex + 1);
            }
            if(currentOp.aTerm != null){
                currentOp.aTerm.lTerm = finalExpression.remove(opIndex + 1);
                currentOp.aTerm.rTerm = finalExpression.remove(opIndex - 1);
            }
            if(currentOp.xorTerm != null){
                currentOp.xorTerm.lTerm = finalExpression.remove(opIndex + 1);
                currentOp.xorTerm.rTerm = finalExpression.remove(opIndex - 1);
            }
            if(currentOp.oTerm != null){
                currentOp.oTerm.lTerm = finalExpression.remove(opIndex + 1);
                currentOp.oTerm.rTerm = finalExpression.remove(opIndex - 1);
            }

            //Adds subexpression to the expression breakdown list
            expressionBreakdown.add(currentOp);
        }
        
        return expressionBreakdown;
    }

    //Checks if expression has empty arguments (Only returns true for operator expressions)
    private boolean emptyOperator()
    {   
        return this.nTerm != null && this.nTerm.expr == null
            || this.aTerm != null && (this.aTerm.lTerm == null && this.aTerm.rTerm == null)
            || this.xorTerm != null && (this.xorTerm.lTerm == null && this.xorTerm.rTerm == null)
            || this.oTerm != null && (this.oTerm.lTerm == null && this.oTerm.rTerm == null);   
    }

    //Tokanzies an input string into variables and operators
    public static List<String> tokanize (String input){
        List <String> tokens = new ArrayList<>();
        
        char[]inputArr = input.trim().replaceAll(" ", "").toCharArray();
        
        String varBuilder = "";

        for(char c : inputArr){
            if(c == '*' || c == XOR.charAt(0) ||c == '+' || c == '!' || c == '('|| c == ')'){
                if(!varBuilder.isBlank())tokens.add(varBuilder);
                tokens.add("" + c);
                varBuilder = ""; //Reset var builder
            }
            else{
                varBuilder += c; //Add char to variable name
            }

        }
        //Add the last variable
        if(!varBuilder.isBlank())tokens.add(varBuilder);

        return tokens;
    }

    //Calculates the number of variables in an expression
    public static int numOfVariables(Expression expr){
        if(expr.aTerm != null){
            return numOfVariables(expr.aTerm.lTerm) + numOfVariables(expr.aTerm.rTerm);
        }
        else if(expr.oTerm != null){
            return numOfVariables(expr.oTerm.lTerm) + numOfVariables(expr.oTerm.rTerm);
        }
        else if(expr.xorTerm != null){
            return numOfVariables(expr.xorTerm.lTerm) + numOfVariables(expr.xorTerm.rTerm);
        }
        else if(expr.nTerm != null){
            return numOfVariables(expr.nTerm.expr);
        }
        else{
            return 1;
        }
    }

    //Calculates the number of variables in a queue split up into finalExpression
    public static int numOfVariables(List<Expression> subExprs){
        int counter = 0;
        for(Expression expr : subExprs){
            if(expr.aTerm == null && expr.oTerm == null && expr.nTerm == null && expr.xorTerm == null) counter++;
        }
        return counter;
    }

    @Override
    public String toString(){
        return exprToString(this, HIGHEST_PRECEDENCE);
    }

    //Help method for parsing an expression to a string
    private static String exprToString(Expression expr, int precedenceCheck){

        String result = "";
        boolean addParenthesis = false;

        if(expr == null) return result;

        if(expr.precedence > precedenceCheck){
            precedenceCheck += EXTRA_PRECEDENCE;
            addParenthesis = true;
        }
    
        if(expr.aTerm != null){
            result = exprToString(expr.aTerm.rTerm, precedenceCheck) + " * " + exprToString(expr.aTerm.lTerm, precedenceCheck);
        }
        else if(expr.oTerm != null){
            result = exprToString(expr.oTerm.rTerm, precedenceCheck) + " + " + exprToString(expr.oTerm.lTerm, precedenceCheck);
        }
        else if(expr.xorTerm != null){
            result = exprToString(expr.xorTerm.rTerm, precedenceCheck) + " "+ XOR + " " + exprToString(expr.xorTerm.lTerm, precedenceCheck);
        }
        else if(expr.nTerm != null){
            result = "!" + exprToString(expr.nTerm.expr, precedenceCheck);
        }
        else{
            result = expr.var.varName;
        }

        if(addParenthesis){
            result = "(" + result + ")";
        }

        return result;
    }





}
