package viewer;

import org.sqlite.SQLiteDataSource;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.File;
import java.sql.*;

public class SQLiteViewer extends JFrame {
    private final JTextField fileNameTextField;
    private final JComboBox<String> tablesComboBox;
    private final JTextArea queryTextArea;
    JTable table;
    JButton executeQueryButton;

    SQLiteDataSource dataSource;

    public SQLiteViewer() {
        dataSource = new SQLiteDataSource();

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
//        tablesComboBox.setEnabled(false);
        add(tablesComboBox);

        queryTextArea = new JTextArea();
        queryTextArea.setName("QueryTextArea");
        queryTextArea.setPreferredSize(new Dimension(560, 250));
        queryTextArea.setEnabled(false);
        add(queryTextArea);

        executeQueryButton = new JButton("Execute");
        executeQueryButton.setName("ExecuteQueryButton");
        executeQueryButton.setPreferredSize(new Dimension(80, 30));
        executeQueryButton.addActionListener(this::executeQuery);
        executeQueryButton.setEnabled(false);
        add(executeQueryButton);

        table = new JTable();
        table.setName("Table");
        JScrollPane scrollPain = new JScrollPane(table);
        scrollPain.setPreferredSize(new Dimension(655, 500));
        add(scrollPain);

        setVisible(true);
    }

    private void executeQuery(ActionEvent event) {
        try (Connection connection = dataSource.getConnection()){
            Statement statement = connection.createStatement();
            ResultSet rows = statement.executeQuery(queryTextArea.getText());
            DefaultTableModel model = new DefaultTableModel();
            ResultSetMetaData metaData = rows.getMetaData();
            for (int i = 0; i < metaData.getColumnCount(); i++) {
                model.addColumn(metaData.getColumnName(i + 1));
            }
            while (rows.next()) {
                Object[] row = new Object[rows.getMetaData().getColumnCount()];
                for (int i = 0; i < row.length; i++) {
                    row[i] = rows.getObject(i + 1);
                }
                model.addRow(row);
            }
            table.setModel(model);
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(new Frame(), exception.getMessage());
        }
    }

    private void tableChange(ItemEvent event) {
        if (event.getStateChange() == ItemEvent.SELECTED) {
            queryTextArea.setText("SELECT * FROM " + event.getItem() + ";");
        }
    }

    private void openDatabase(ActionEvent actionEvent) {
        if (! new File(fileNameTextField.getText().trim()).exists()) {
            fileNotFound();
            return;
        }
        dataSource.setUrl("jdbc:sqlite:" + fileNameTextField.getText().trim());
        try (Connection connection = dataSource.getConnection()){
            Statement statement = connection.createStatement();
            final String tablesQuery = "SELECT name FROM sqlite_master WHERE type ='table' AND name NOT LIKE 'sqlite_%';";
            ResultSet tables = statement.executeQuery(tablesQuery);
            tablesComboBox.removeAllItems();
            while (tables.next()) {
                tablesComboBox.addItem(tables.getString(1));
            }
            tablesComboBox.setEnabled(true);
            queryTextArea.setEnabled(true);
            executeQueryButton.setEnabled(true);
        } catch (SQLException exception) {
            fileNotFound();
        }
    }

    private void fileNotFound() {
        JOptionPane.showMessageDialog(new Frame(), "Wrong file name!");
        tablesComboBox.setEnabled(false);
        queryTextArea.setEnabled(false);
        executeQueryButton.setEnabled(false);
    }
}
