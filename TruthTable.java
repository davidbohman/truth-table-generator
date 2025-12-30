import java.util.ArrayList;
import java.util.List;


/**
 * Represents a truth table for a given boolean expression.
 * 
 * <p>This class takes a boolean expression as in String format as input, parses it into an abstract syntax tree (AST)
 * using {@link Expression}, identifies all variables, and constructs a complete truth table.
 * The table includes the values of all sub-expressions, as well as the main expression.</p>
 * 
 * <p>It supports printing the table in a nicely formatted manner.</p>
 */
public class TruthTable {

    //Main expression
    private Expression expr;

    //List of all sub expressions and final expression (including variables)
    private List<Expression> expressionBreakdown;

    //List of only variables
    private List<Expression.Variable> variables;

    //Amount of variables
    private int numberOfVariables;

    //Table dimensions
    private int rows;
    private int columns;

    //Actual table containing values
    private String[][] table;


    /**
     * Constructs a {@code TruthTable} for the given boolean expression.
     *
     * @param input the boolean expression as a string, e.g. "A * (!B + C)"
     * @throws IllegalArgumentException if the input expression is invalid
     */
    TruthTable(String input){
        this.expr = Expression.parseToExpression(input);
        this.expressionBreakdown = expr.getSubExpressions();
        this.variables = expr.getVariables();
        this.numberOfVariables = variables.size();
        this.columns = expressionBreakdown.size();
        this.rows = ((int) Math.pow(2, numberOfVariables)) + 1;
        this.table = new String[this.rows][this.columns];
        buildTable();
    }

    /**
     * Returns the truth table as a 2D array of strings.
     * Each row represents a combination of variable values and evaluated expressions.
     *
     * @return the truth table
     */
    public String[][] getTable() {
        return this.table;
    }

    //Builds the whole table by assigning and evaluating values at all indexes
    private void buildTable(){
        //Adding sub expressions to top row 
        for(int i = 0; i < columns; i++) this.table[0][i] = expressionBreakdown.get(i).toString();

        int zeroIntervall = (rows-1) / 2;
       
        int setterFlag = 1;
        String setter = "" + 0 % 2;
        //Adding values to the variables
        for(int varI = 0; varI < numberOfVariables; varI++){
            for(int i = 0; i < this.rows-1; i++){
                if(i % zeroIntervall == 0){
                    setterFlag++;
                    setter = "" + (setterFlag % 2);
                }
                this.table[i+1][varI] = setter;
            }
            zeroIntervall = zeroIntervall / 2;
        }

        //Evaluating expressionBreakdown
        evaluateExpressions();
    }
    
    //Evaluates expressions row by row
    private void evaluateExpressions(){
        for(int i = 1; i < this.rows; i++){

            //Assign current rows variale values
            for(int j = 0; j < this.numberOfVariables; j++){
                variables.get(j).setCurrentValue(Integer.parseInt(this.table[i][j]));
            }

            //Evaluate expressions with current row values
            for(int j = this.numberOfVariables; j < this.columns; j++){
                int result = this.expressionBreakdown.get(j).evaluateExpression();
                this.table[i][j] = "" + result;
            }
        }
    }

    /**
     * Prints the truth table in a nicely formatted manner, including sub-expression headers and
     * aligned columns.
     */
    public void printTable(){
        
        List<Integer> cellWidth = new ArrayList<>();
        
        for(Expression expr : this.expressionBreakdown){
            int length = expr.toString().length();
            cellWidth.add(length);
        }


        //Printing variables and expressions
        StringBuilder topRowBuilder = new StringBuilder();
        for(int j = 0; j < this.columns; j++){
            topRowBuilder.append("  " + table[0][j] + "  |");
        }
        
        String topRow = topRowBuilder.toString();
        System.out.println(topRow);

        //Creating line that seperates rows
        String lineBreak = (new StringBuilder()).repeat("-", topRow.length()).toString();

        System.out.println(lineBreak); //Line break

        //Printing assigned and evaluated values
        for(int row = 1; row < this.rows; row++){      
            for(int col = 0; col < this.columns; col++){
                
                //Determening padding
                
                String paddingLeft = (new StringBuilder()).repeat(" ", (cellWidth.get(col)/2)).toString();
                String paddingRight = paddingLeft;
            
                System.out.print("  " + paddingLeft + this.table[row][col] + paddingRight + "  |");
            }

            System.out.println("\n" + lineBreak);
        }

        
    }

}
