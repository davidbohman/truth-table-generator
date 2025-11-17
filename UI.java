import javax.swing.*;

public class UI {
    JFrame frame;
    
    int windowWidth;
    int windowHeight;


    UI(int windowWidth, int windowHeight){

        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;

        frame = new JFrame("Boolean Calculator");
        frame.setSize(windowWidth, windowHeight);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Adding components to the window
        frame.add(completePanel());


        frame.setVisible(true);
    }

    private JPanel completePanel(){

        JLabel label = new JLabel("Enter your name:");
        JTextField textField = new JTextField(15);
        JButton button = new JButton("Say Hello");
        JLabel output = new JLabel("");

        // Add action when button is clicked
        button.addActionListener(e -> {
            String name = textField.getText();
            output.setText("Hello, " + name + "!");
        });


        JPanel panel = new JPanel();
        panel.add(label);
        panel.add(textField);
        panel.add(button);
        panel.add(output);

        return panel;
    }

}
