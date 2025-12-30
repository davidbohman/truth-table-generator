import java.awt.Color;
import java.awt.Component;
import java.util.Stack; 
import javax.swing.*; 
import javax.swing.plaf.ColorUIResource; 
import javax.swing.table.DefaultTableCellRenderer; 
import javax.swing.table.TableCellRenderer; 

/**
 * Graphical User Interface for the Truth Table Generator application.
 * 
 * <p>This class creates a window that allows users to input a boolean expression,
 * generate a truth table, and display it in a JTable. It also handles exceptions
 * for invalid expressions and displays them to the user.</p>
 * 
 * <p>The UI includes:</p>
 * <ul>
 *     <li>A text field for inputting expressions</li>
 *     <li>Buttons for generating truth tables and inserting XOR symbols</li>
 *     <li>A scrollable JTable to display the resulting truth table</li>
 *     <li>Red-colored error messages for invalid input</li>
 * </ul>
 */
public class UI { 
    private JFrame frame;  
    private JScrollPane currentTruthTable; 
    private Stack<JLabel> activeExceptions; 
    private JLabel currentException; 

    /**
     * Constructs the UI with the given window dimensions, initializes all components,
     * and sets up action listeners for buttons.
     *
     * @param windowWidth  width of the window in pixels
     * @param windowHeight height of the window in pixels
     */
    UI(int width, int height)
    { 
        //Window setup
        frame = new JFrame("Truth Table Generator"); 
        frame.setSize(width, height); 
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 

        //Main panel
        JPanel panel = new JPanel(); 

        //Buttons and input at the top
        JLabel label = new JLabel("Enter your expression:"); 
        JTextField textField = new JTextField(15); 
        JButton xorButton = new JButton("⊕"); 
        JButton generateTableButton = new JButton("Generate Truth Table"); 
        activeExceptions = new Stack<>(); JLabel output = new JLabel(""); 
        
        panel.add(label); 
        panel.add(textField); 
        panel.add(xorButton); 
        panel.add(generateTableButton); 
        panel.add(output); 

        // Functionality to XOR button
        xorButton.addActionListener(e -> { 
            textField.setText(textField.getText() + "⊕"); 
        }); 

        // Generates actual truth table 
        generateTableButton.addActionListener(e -> {
            String userInput = textField.getText(); 
            try
            { 
                //Retrieve evaluated table from user input
                String[][] generatedData = (new TruthTable(userInput)).getTable();
                
                //Convert and fix up JTable
                JTable truthTable = new JTable(generatedData, generatedData[0]); 
                resizeColumns(truthTable); 
                DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
                centerRenderer.setHorizontalAlignment(JLabel.CENTER); 
                truthTable.setDefaultRenderer(Object.class, centerRenderer); 
                truthTable.setShowGrid(true); truthTable.setShowHorizontalLines(true); 
                truthTable.setShowVerticalLines(true); truthTable.setGridColor(new ColorUIResource(0, 0,0)); 
                truthTable.setTableHeader(null); JScrollPane newTruthTable = new JScrollPane(truthTable); 

                //Utalized if there already exisists a printed table
                newTruthTable.setPreferredSize(truthTable.getPreferredSize()); 
                if(currentTruthTable == null){ 
                    currentTruthTable = newTruthTable; 
                } else { 
                    panel.remove(currentTruthTable);
                     currentTruthTable = newTruthTable; 
                } 

                panel.add(currentTruthTable); 

                //Removes old exception labels 
                while(!activeExceptions.isEmpty()){
                    panel.remove(activeExceptions.pop()); 
                } 
            }
            catch(IllegalArgumentException error)
            { 
                //Adds exception label
                currentException = new JLabel(" | " + error.getMessage());
                currentException.setForeground(Color.RED); 
                activeExceptions.push(currentException); 
                panel.add(currentException); 
            }

            //Updates current panel and frame
            panel.revalidate(); 
            panel.repaint(); }); 
            frame.add(panel); 
            frame.setVisible(true); 
        } 

    //Help function for giving the table correct proportions
    private static void resizeColumns(JTable table) {
        for (int col = 0; col < table.getColumnCount(); col++) { 
            int width = 50; 
            // minimum 
            for (int row = 0; row < table.getRowCount(); row++){ 
                TableCellRenderer renderer = table.getCellRenderer(row, col);
                Component comp = table.prepareRenderer(renderer, row, col); 
                width = Math.max(comp.getPreferredSize().width + 10, width); 
            } 
            table.getColumnModel().getColumn(col).setPreferredWidth(width); 
        } 
    }
}