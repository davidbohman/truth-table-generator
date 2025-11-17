import java.util.ArrayList;
import java.util.List;

public class TruthTable {

    //List of all variables, sub expressions and final expression
    List<Expression> expressionBreakdown;

    //Main expression
    Expression expr;

    //Amount of variables
    int numberOfVariables;

    //Table dimensions
    int rows;
    int columns;
    String[][] table;

    TruthTable(String input){
        this.expressionBreakdown = Expression.parseToExpr(input);
        this.expr = expressionBreakdown.getLast();
        this.numberOfVariables = Expression.numOfVariables(expressionBreakdown);
        this.columns = expressionBreakdown.size();
        this.rows = ((int) Math.pow(2, numberOfVariables)) + 1;
        this.table = new String[this.rows][this.columns];
        buildTable();
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
                expressionBreakdown.get(j).currentValue = Integer.parseInt(this.table[i][j]);
            }
            //Evaluate expressions with current row values
            for(int j = this.numberOfVariables; j < this.columns; j++){
                int result = Expression.evalExpr(this.expressionBreakdown.get(j));
                this.table[i][j] = "" + result;
            }
        }
    }

    //Prints table along with some information
    public void printTable(){
        
        System.out.println("\nExpression: " + this.expr);
        System.out.println("Number of variables: " + this.numberOfVariables);
        System.out.println("Rows: " + this.rows + " and Cols: " + this.columns + "\n\n");

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
