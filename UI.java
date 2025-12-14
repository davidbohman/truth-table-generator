import java.awt.Color;
import java.awt.Component;
import java.util.Stack; 
import javax.swing.*; 
import javax.swing.plaf.ColorUIResource; 
import javax.swing.table.DefaultTableCellRenderer; 
import javax.swing.table.TableCellRenderer; 

public class UI { 
    JFrame frame; 
    int windowWidth; 
    int windowHeight; 
    JScrollPane currentTruthTable; 
    Stack<JLabel> activeExceptions; 
    JLabel currentException; 

    UI(int windowWidth, int windowHeight)
    { 
        //Window setup
        this.windowWidth = windowWidth; 
        this.windowHeight = windowHeight; 
        frame = new JFrame("Truth Table Generator"); 
        frame.setSize(windowWidth, windowHeight); 
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
                String[][] generatedData = (new TruthTable(userInput)).table;
                
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