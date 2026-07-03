package backend;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class VehicleService {
    public void showVehicles() {

        JFrame frame = new JFrame("🚘 Vehicle Table");
        frame.setSize(700, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new java.awt.BorderLayout());

        DefaultTableModel model = new DefaultTableModel(
                new String[] { "ID", "Model", "Capacity", "Status", "Rate/km", "Driver ID" }, 0);

        JTable table = new JTable(model);
        frame.add(new JScrollPane(table), java.awt.BorderLayout.CENTER);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM Vehicle";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getInt("vehicle_id"),
                        rs.getString("model"),
                        rs.getInt("capacity"),
                        rs.getString("status"),
                        rs.getDouble("rate_per_km"),
                        rs.getObject("driver_id")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                    "⚠ Error loading vehicle data:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        frame.setVisible(true);
    }
}
