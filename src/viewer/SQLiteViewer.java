package viewer;

import org.sqlite.SQLiteDataSource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteViewer extends JFrame {
    JTextField fileNameTextField;
    JComboBox<String> tablesComboBox;
    JTextArea queryTextArea;

    public SQLiteViewer() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 900);
        setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));
        setResizable(false);
        setLocationRelativeTo(null);

        setTitle("SQLite Viewer");

        fileNameTextField = new JTextField();
        fileNameTextField.setName("FileNameTextField");
        fileNameTextField.setPreferredSize(new Dimension(575, 25));
        add(fileNameTextField);

        JButton openFileButton = new JButton("Open");
        openFileButton.setName("OpenFileButton");
        openFileButton.setSize(100, 15);
        openFileButton.addActionListener(this::openDatabase);
        add(openFileButton);

        tablesComboBox = new JComboBox<>();
        tablesComboBox.setName("TablesComboBox");
        tablesComboBox.setPreferredSize(new Dimension(655, 20));
        tablesComboBox.addItemListener(this::tableChange);
        add(tablesComboBox);

        queryTextArea = new JTextArea();
        queryTextArea.setName("QueryTextArea");
        queryTextArea.setPreferredSize(new Dimension(560, 250));
        add(queryTextArea);

        JButton executeQueryButton = new JButton("Execute");
        executeQueryButton.setName("ExecuteQueryButton");
        executeQueryButton.setPreferredSize(new Dimension(80, 30));
        add(executeQueryButton);

        setVisible(true);
    }

    private void tableChange(ItemEvent event) {
        if (event.getStateChange() == ItemEvent.SELECTED) {
            queryTextArea.setText("SELECT * FROM " + event.getItem() + ";");
        }
    }

    private void openDatabase(ActionEvent actionEvent) {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl("jdbc:sqlite:" + fileNameTextField.getText().trim());
        try (Connection connection = dataSource.getConnection()){
            Statement statement = connection.createStatement();
            final String tablesQuery = "SELECT name FROM sqlite_master WHERE type ='table' AND name NOT LIKE 'sqlite_%';";
            ResultSet tables = statement.executeQuery(tablesQuery);
            tablesComboBox.removeAllItems();
            while (tables.next()) {
                tablesComboBox.addItem(tables.getString(1));
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
