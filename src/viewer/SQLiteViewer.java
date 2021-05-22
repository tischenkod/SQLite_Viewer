package viewer;

import javax.swing.*;
import java.awt.*;

public class SQLiteViewer extends JFrame {

    public SQLiteViewer() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 900);
        setLayout(new FlowLayout());
        setResizable(false);
        setLocationRelativeTo(null);

        setTitle("SQLite Viewer");

        JTextField fileNameTextField = new JTextField();
        fileNameTextField.setName("FileNameTextField");
        fileNameTextField.setPreferredSize(new Dimension(500, 25));
        fileNameTextField.setVisible(true);
        add(fileNameTextField);
        JButton openFileButton = new JButton("Open");
        openFileButton.setName("OpenFileButton");
        openFileButton.setSize(100, 15);
        openFileButton.setVisible(true);
        add(openFileButton);
        setVisible(true);
    }
}
