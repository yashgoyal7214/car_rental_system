package backend;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class DriverService {
    public void showDrivers() {
        JFrame frame = new JFrame("👨‍✈ Driver Table");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new java.awt.BorderLayout());

        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Driver ID", "Name", "Phone", "Status"}, 0);
        
        JTable table = new JTable(model);
        table.setAutoCreateRowSorter(true); // enable sorting

        frame.add(new JScrollPane(table), java.awt.BorderLayout.CENTER);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM Driver";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("driver_id"),
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("status")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                "⚠ Error loading driver data:\n" + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }

        frame.setVisible(true);
    }
}

