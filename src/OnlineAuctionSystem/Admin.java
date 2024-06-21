package OnlineAuctionSystem;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.Objects;
import java.util.Vector;

public class Admin {
    private JButton startButton;
    private JLabel timerLabel;
    private JPanel adminPanel;
    private JButton ADDITEMButton;
    private JTable table_1;
    private JTextField nameData;
    private JTextField priceData;
    private JTextField path;
    private JButton Select_image;
    private JButton close;
    private JLabel image_label;
    private JButton Delete_Auction_Data;
    public static String adminNameData = "", adminPriceData = "";
    public static ImageIcon adminImageData;
    JFrame adminF = new JFrame();
    Timer timer;
    public static int sec = 60;

    public Admin() {
        adminF = new JFrame("Online Auction System");
        adminF.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        adminF.setContentPane(adminPanel);
        adminF.pack();
        adminF.setLocationRelativeTo(null);
        tableData();
        adminF.setVisible(true);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startTimer();
                timer.start();
            }
        });

        ADDITEMButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (nameData.getText().equals("") || path.getText().equals("") || priceData.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "Please Fill All Fields to add Record.");
                } else {
                    String sql = "INSERT INTO auction (ITEM_NAME, IMAGE, PRICE) VALUES (?, ?, ?)";
                    try {
                        File f = new File(path.getText());
                        InputStream inputStream = new FileInputStream(f);
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Online_Auction_System", "root", "recreation123");
                        PreparedStatement statement = connection.prepareStatement(sql);
                        statement.setString(1, nameData.getText());
                        statement.setBlob(2, inputStream);
                        statement.setString(3, priceData.getText());
                        statement.executeUpdate();
                        JOptionPane.showMessageDialog(null, "DETAILS ADDED SUCCESSFULLY");
                        nameData.setText("");
                        priceData.setText("");
                        image_label.setIcon(null);
                        path.setText("");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage());
                    }
                    tableData();
                }
            }
        });

        Select_image.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("*.IMAGE", "jpg", "png");
                fileChooser.addChoosableFileFilter(filter);
                int rs = fileChooser.showSaveDialog(null);
                if (rs == JFileChooser.APPROVE_OPTION) {
                    File selectedImage = fileChooser.getSelectedFile();
                    path.setText(selectedImage.getAbsolutePath());
                    image_label.setIcon(resize(path.getText()));
                }
            }
        });

        table_1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                DefaultTableModel dm = (DefaultTableModel) table_1.getModel();
                int selectedRow = table_1.getSelectedRow();
                adminNameData = dm.getValueAt(selectedRow, 0).toString();
                nameData.setText(adminNameData);
                byte[] img = (byte[]) dm.getValueAt(selectedRow, 1);
                ImageIcon imageIcon = new ImageIcon(img);
                Image im = imageIcon.getImage();
                Image newimg = im.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                ImageIcon finalPic = new ImageIcon(newimg);
                adminImageData = finalPic;
                image_label.setIcon(adminImageData);
                adminPriceData = dm.getValueAt(selectedRow, 2).toString();
                priceData.setText(adminPriceData);
            }
        });

        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adminF.dispose();
            }
        });

        Delete_Auction_Data.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table_1.getSelectedRow();
                if (selectedRow != -1) {
                    String itemName = table_1.getValueAt(selectedRow, 0).toString();
                    deleteAuctionItem(itemName);
                    tableData();
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a row to delete.");
                }
            }
        });
    }

    public ImageIcon resize(String path) {
        ImageIcon myImg = new ImageIcon(path);
        Image image = myImg.getImage();
        Image newImage = image.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        return new ImageIcon(newImage);
    }

    public void startTimer() {
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sec--;
                if (sec == -1) {
                    timer.stop();
                    tableData();
                } else if (sec >= 0 && sec < 10) {
                    timerLabel.setText("00:0" + sec);
                } else {
                    timerLabel.setText("00:" + sec);
                }
            }
        });
    }

    public void tableData() {
        try {
            String sql = "SELECT * FROM auction";
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Online_Auction_System", "root", "recreation123");
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            table_1.setModel(buildTableModel(rs));
        } catch (Exception ex1) {
            JOptionPane.showMessageDialog(null, ex1.getMessage());
        }
    }

    public static DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();

        Vector<String> columnNames = new Vector<>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        Vector<Vector<Object>> data = new Vector<>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }

        return new DefaultTableModel(data, columnNames);
    }

    public void deleteAuctionItem(String itemName) {
        String sql = "DELETE FROM auction WHERE ITEM_NAME = ?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Online_Auction_System", "root", "recreation123");
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, itemName);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(null, "Item deleted successfully!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error deleting item: " + ex.getMessage());
        }
    }
}
