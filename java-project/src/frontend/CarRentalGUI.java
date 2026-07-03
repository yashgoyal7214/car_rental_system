package frontend;

import backend.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.sql.*;
import java.util.regex.*;
import javax.swing.Timer;

public class CarRentalGUI extends JFrame {

    private JTextField nameField, phoneField, addressField, distanceField;
    private JComboBox<String> vehicleBox;

    private BookingService bookingService;
    private VehicleService vehicleService;
    private DriverService driverService;

    private boolean darkMode = true;

    private final Color LIGHT_BG_START = new Color(150, 200, 255);
    private final Color LIGHT_BG_END = new Color(200, 230, 255);
    private final Color PANEL_BG_LIGHT = new Color(250, 250, 250);
    private final Color FIELD_BG_LIGHT = new Color(245, 245, 245);
    private final Color TEXT_LIGHT = new Color(25, 25, 25);

    private final Color DARK_BG_START = new Color(18, 18, 18);
    private final Color DARK_BG_END = new Color(28, 28, 28);
    private final Color PANEL_BG_DARK = new Color(40, 40, 40);
    private final Color FIELD_BG_DARK = new Color(50, 50, 50);
    private final Color TEXT_DARK = new Color(230, 230, 230);

    private final Color ORANGE = new Color(255, 140, 0);
    private final Color ORANGE_DARK = new Color(220, 110, 0);
    private final Color ORANGE_GLOW = new Color(255, 165, 77, 140);

    private JPanel backgroundPanel;
    private JPanel formPanel;
    private JLabel titleLabel;
    private GlowButton bookBtn, viewBookingBtn, viewVehicleBtn, viewDriverBtn, exitBtn;
    private JToggleButton modeToggle;

    public CarRentalGUI() {

        bookingService = new BookingService();
        vehicleService = new VehicleService();
        driverService = new DriverService();

        setTitle("Car Rental Booking System");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setIconImage(generateCarIcon(64, 64, darkMode ? Color.WHITE : Color.BLACK));

        initUI();
        applyTheme(); // apply initial theme
        setVisible(true);
    }

    private void initUI() {
        // ===== Background panel with subtle gradient =====
        backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                Color c1 = darkMode ? DARK_BG_START : LIGHT_BG_START;
                Color c2 = darkMode ? DARK_BG_END : LIGHT_BG_END;
                GradientPaint gp = new GradientPaint(0, 0, c1, 0, getHeight(), c2);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        add(backgroundPanel);

        // ===== Top bar (title + toggle) =====
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        // Title with small padding (keep emoji if you like)
        titleLabel = new JLabel("  Car Rental Booking System");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Auto-generated small sports car icon next to title (paint onto a label)
        JLabel iconLabel = new JLabel(new ImageIcon(generateCarIcon(36, 24, darkMode ? Color.WHITE : Color.BLACK)));
        iconLabel.setBorder(new EmptyBorder(8, 12, 8, 8));
        JPanel leftTitle = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftTitle.setOpaque(false);
        leftTitle.add(iconLabel);
        leftTitle.add(titleLabel);

        // Toggle switch area (right)
        modeToggle = createModeToggle();
        JPanel rightControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 10));
        rightControls.setOpaque(false);
        rightControls.add(modeToggle);

        topBar.add(leftTitle, BorderLayout.WEST);
        topBar.add(rightControls, BorderLayout.EAST);
        backgroundPanel.add(topBar, BorderLayout.NORTH);

        // ===== Main form card (rounded panel) =====
        formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);

        RoundedPanel card = new RoundedPanel(22);
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(26, 36, 26, 36));
        card.setPreferredSize(new Dimension(820, 420));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 16, 12, 16);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        // ---> Customer Name
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.25;
        card.add(makeLabel("Customer Name:"), gbc);
        nameField = makeTextField();
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.weightx = 0.75;
        card.add(nameField, gbc);
        row++;

        // ---> Phone
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.25;
        card.add(makeLabel("Phone:"), gbc);
        phoneField = makeTextField();
        gbc.gridx = 1;
        gbc.gridy = row;
        card.add(phoneField, gbc);
        row++;

        // ---> Address
        gbc.gridx = 0;
        gbc.gridy = row;
        card.add(makeLabel("Address:"), gbc);
        addressField = makeTextField();
        gbc.gridx = 1;
        gbc.gridy = row;
        card.add(addressField, gbc);
        row++;

        // ---> Vehicle selection
        gbc.gridx = 0;
        gbc.gridy = row;
        card.add(makeLabel("Select Vehicle:"), gbc);
        vehicleBox = new JComboBox<>();
        styleComboBox(vehicleBox);
        loadVehicles();
        gbc.gridx = 1;
        gbc.gridy = row;
        card.add(vehicleBox, gbc);
        row++;

        // ---> Distance
        gbc.gridx = 0;
        gbc.gridy = row;
        card.add(makeLabel("Distance (km):"), gbc);
        distanceField = makeTextField();
        gbc.gridx = 1;
        gbc.gridy = row;
        card.add(distanceField, gbc);
        row++;

        // Buttons row 1 (3 buttons)
        JPanel btnRow1 = new JPanel(new GridLayout(1, 3, 18, 0));
        btnRow1.setOpaque(false);
        bookBtn = new GlowButton("Book Vehicle");
        viewBookingBtn = new GlowButton("View All Bookings");
        exitBtn = new GlowButton("Exit");
        btnRow1.add(bookBtn);
        btnRow1.add(viewBookingBtn);
        btnRow1.add(exitBtn);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        card.add(btnRow1, gbc);
        row++;

        // Buttons row 2 (2 buttons)
        JPanel btnRow2 = new JPanel(new GridLayout(1, 2, 18, 0));
        btnRow2.setOpaque(false);
        viewVehicleBtn = new GlowButton("View Vehicles");
        viewDriverBtn = new GlowButton("View Drivers");
        btnRow2.add(viewVehicleBtn);
        btnRow2.add(viewDriverBtn);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        card.add(btnRow2, gbc);
        row++;

        formPanel.add(card);
        backgroundPanel.add(formPanel, BorderLayout.CENTER);

        // ===== Action listeners (preserve behaviour) =====
        bookBtn.addActionListener(e -> bookVehicle());
        viewBookingBtn.addActionListener(e -> bookingService.showBookings());
        viewVehicleBtn.addActionListener(e -> vehicleService.showVehicles());
        viewDriverBtn.addActionListener(e -> driverService.showDrivers());
        exitBtn.addActionListener(e -> System.exit(0));
    }

    // ---------------- Theme toggle (animated-ish) ----------------
    private JToggleButton createModeToggle() {
        JToggleButton toggle = new JToggleButton();
        toggle.setFocusable(false);
        toggle.setPreferredSize(new Dimension(110, 36));
        toggle.setFont(new Font("SansSerif", Font.PLAIN, 13));

        // Initial text
        toggle.setText(darkMode ? "Dark Mode" : "Light Mode");

        toggle.addActionListener(e -> {
            darkMode = toggle.isSelected() ? false : true; // invert (selected = light)
            // sync text and icon tint
            toggle.setText(darkMode ? "Dark Mode" : "Light Mode");
            // update frame icon color to match theme
            setIconImage(generateCarIcon(64, 64, darkMode ? Color.WHITE : Color.BLACK));
            applyTheme();
            repaint();
        });

        // set default selected consistent with darkMode
        toggle.setSelected(!darkMode);

        return toggle;
    }

    // ---------------- Apply colors to components ----------------
    private void applyTheme() {
        // Title color and icon label color handled by icon image
        titleLabel.setForeground(darkMode ? TEXT_DARK : TEXT_LIGHT);

        // update background repaint
        backgroundPanel.repaint();

        // update card and nested components
        Component[] comps = formPanel.getComponents();
        for (Component c : comps) {
            if (c instanceof RoundedPanel) {
                RoundedPanel rp = (RoundedPanel) c;
                rp.setBackground(darkMode ? PANEL_BG_DARK : PANEL_BG_LIGHT);
                updateChildrenTheme(rp, darkMode);
            } else {

                if (c instanceof JPanel) {
                    for (Component child : ((JPanel) c).getComponents()) {
                        if (child instanceof RoundedPanel) {
                            RoundedPanel rp = (RoundedPanel) child;
                            rp.setBackground(darkMode ? PANEL_BG_DARK : PANEL_BG_LIGHT);
                            updateChildrenTheme(rp, darkMode);
                        }
                    }
                }
            }
        }
        modeToggle.setBackground(darkMode ? PANEL_BG_DARK : PANEL_BG_LIGHT);
        modeToggle.setForeground(darkMode ? TEXT_DARK : TEXT_LIGHT);

        Color base = darkMode ? ORANGE : ORANGE;
        Color hover = darkMode ? ORANGE_DARK : ORANGE_DARK;
        for (GlowButton gb : new GlowButton[] { bookBtn, viewBookingBtn, viewVehicleBtn, viewDriverBtn, exitBtn }) {
            if (gb != null) {
                gb.setBaseColors(base, hover, ORANGE_GLOW);
                gb.updateTheme(darkMode);
            }
        }

        updateChildrenTheme(formPanel, darkMode);
    }

    private void updateChildrenTheme(Container container, boolean dark) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JLabel) {
                ((JLabel) comp).setForeground(dark ? TEXT_DARK : TEXT_LIGHT);
            } else if (comp instanceof JTextField) {
                JTextField tf = (JTextField) comp;
                tf.setBackground(dark ? FIELD_BG_DARK : FIELD_BG_LIGHT);
                tf.setForeground(dark ? TEXT_DARK : TEXT_LIGHT);
                tf.setBorder(new LineBorder(dark ? new Color(100, 100, 100) : new Color(180, 180, 180), 2, true));
            } else if (comp instanceof JComboBox) {
                JComboBox<?> cb = (JComboBox<?>) comp;
                cb.setBackground(dark ? FIELD_BG_DARK : FIELD_BG_LIGHT);
                cb.setForeground(dark ? TEXT_DARK : TEXT_LIGHT);
                cb.setBorder(new LineBorder(dark ? new Color(100, 100, 100) : new Color(180, 180, 180), 2, true));
            } else if (comp instanceof Container) {
                updateChildrenTheme((Container) comp, dark);
            }
        }
    }

    // ---------------- Factory helpers ----------------
    private JLabel makeLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setForeground(darkMode ? TEXT_DARK : TEXT_LIGHT);
        return label;
    }

    private JTextField makeTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("SansSerif", Font.PLAIN, 16));
        tf.setBorder(new LineBorder(darkMode ? new Color(100, 100, 100) : new Color(180, 180, 180), 2, true));
        tf.setBackground(darkMode ? FIELD_BG_DARK : FIELD_BG_LIGHT);
        tf.setForeground(darkMode ? TEXT_DARK : TEXT_LIGHT);
        tf.setPreferredSize(new Dimension(260, 38));
        return tf;
    }

    private void styleComboBox(JComboBox<String> box) {
        box.setFont(new Font("SansSerif", Font.PLAIN, 16));
        box.setBackground(darkMode ? FIELD_BG_DARK : FIELD_BG_LIGHT);
        box.setForeground(darkMode ? TEXT_DARK : TEXT_LIGHT);
        box.setBorder(new LineBorder(darkMode ? new Color(100, 100, 100) : new Color(180, 180, 180), 2, true));
    }

    // ---------------- Load vehicles unchanged ----------------
    private void loadVehicles() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT vehicle_id, model, rate_per_km FROM Vehicle");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (vehicleBox != null) {
                    vehicleBox.addItem(
                            rs.getInt("vehicle_id") + " - " +
                                    rs.getString("model") +
                                    " (₹" + rs.getDouble("rate_per_km") + "/km)");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------------- Validation + booking (keeps behaviour) ----------------
    private boolean validateForm() {
        boolean ok = true;

        Color normalBorder = darkMode ? new Color(100, 100, 100) : new Color(180, 180, 180);
        LineBorder lb = new LineBorder(normalBorder, 2, true);
        nameField.setBorder(lb);
        phoneField.setBorder(lb);
        addressField.setBorder(lb);
        distanceField.setBorder(lb);

        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();
        String distanceText = distanceField.getText().trim();
        Object selectedVehicle = vehicleBox.getSelectedItem();

        if (name.isEmpty()) {
            markInvalid(nameField, "Name is required.");
            ok = false;
        }
        if (phone.isEmpty()) {
            markInvalid(phoneField, "Phone is required.");
            ok = false;
        } else if (!Pattern.matches("\\d{10}", phone)) {
            markInvalid(phoneField, "Phone must be 10 digits.");
            ok = false;
        }
        if (address.isEmpty()) {
            markInvalid(addressField, "Address is required.");
            ok = false;
        }
        if (selectedVehicle == null) {
            JOptionPane.showMessageDialog(this, "Please select a vehicle.", "Validation", JOptionPane.WARNING_MESSAGE);
            ok = false;
        }
        if (distanceText.isEmpty()) {
            markInvalid(distanceField, "Distance is required.");
            ok = false;
        } else {
            try {
                double d = Double.parseDouble(distanceText);
                if (d <= 0) {
                    markInvalid(distanceField, "Distance must be positive.");
                    ok = false;
                }
            } catch (NumberFormatException ex) {
                markInvalid(distanceField, "Distance must be a number.");
                ok = false;
            }
        }
        return ok;
    }

    private void markInvalid(JTextField f, String message) {
        f.setBorder(new LineBorder(Color.RED.darker(), 2, true));
        f.setToolTipText(message);
        JOptionPane.showMessageDialog(this, message, "Validation", JOptionPane.WARNING_MESSAGE);
    }

    private void bookVehicle() {
        if (!validateForm())
            return;
        try {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String address = addressField.getText().trim();
            String selectedVehicle = (String) vehicleBox.getSelectedItem();
            double distance = Double.parseDouble(distanceField.getText().trim());

            int vehicleId = Integer.parseInt(selectedVehicle.split(" - ")[0]);

            double total = bookingService.createBooking(
                    name, phone, address, vehicleId, distance);

            JOptionPane.showMessageDialog(this,
                    "✅ Booking Successful!\nTotal Amount: ₹" + total,
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            nameField.setText("");
            phoneField.setText("");
            addressField.setText("");
            distanceField.setText("");
            if (vehicleBox.getItemCount() > 0)
                vehicleBox.setSelectedIndex(0);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Error while booking! Check console.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private Image generateCarIcon(int w, int h, Color color) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setComposite(AlphaComposite.Clear);
        g2.fillRect(0, 0, w, h);
        g2.setComposite(AlphaComposite.SrcOver);

        g2.setStroke(new BasicStroke(Math.max(1f, w * 0.06f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(color);

        int pad = Math.max(2, w / 12);
        int left = pad, right = w - pad;
        int top = h / 4, bottom = h - pad - 6;

        Path2D.Double body = new Path2D.Double();
        body.moveTo(left + 4, bottom - 6);
        body.curveTo(left + 14, top, right - 60, top, right - 40, bottom - 10);
        body.lineTo(right - 8, bottom - 10);
        body.quadTo(right - 6, bottom - 10, right - 2, bottom - 6);
        g2.draw(body);

        Path2D.Double roof = new Path2D.Double();
        roof.moveTo(left + 36, top + 6);
        roof.quadTo(left + 54, top - 6, left + 84, top + 8);
        g2.draw(roof);

        int wheelR = Math.max(4, h / 8);
        int wheelY = bottom - 2;
        int wheel1X = left + 28;
        int wheel2X = right - 36;
        g2.drawOval(wheel1X - wheelR, wheelY - wheelR, wheelR * 2, wheelR * 2);
        g2.drawOval(wheel2X - wheelR, wheelY - wheelR, wheelR * 2, wheelR * 2);

        g2.dispose();
        return img;
    }

    private static class GlowButton extends JButton {
        private Color baseColor = new Color(255, 140, 0);
        private Color hoverColor = new Color(220, 110, 0);
        private Color glowColor = new Color(255, 165, 77, 120);
        private float glowAlpha = 0f;
        private Timer fadeIn, fadeOut, pulseTimer;
        private boolean hovering = false;
        private boolean darkModeLocal = true;

        GlowButton(String text) {
            super(text);
            setFont(new Font("SansSerif", Font.BOLD, 16));
            setForeground(Color.BLACK);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setBorder(new EmptyBorder(12, 18, 12, 18));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            initTimers();
            setupListeners();
        }

        private void initTimers() {
            fadeIn = new Timer(10, e -> {
                glowAlpha = Math.min(0.9f, glowAlpha + 0.06f);
                repaint();
            });
            fadeOut = new Timer(10, e -> {
                glowAlpha = Math.max(0f, glowAlpha - 0.06f);
                repaint();
            });
            pulseTimer = new Timer(50, e -> {

                float p = (float) (0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 250.0));
                glowAlpha = 0.45f * p + 0.45f;
                repaint();
            });
        }

        private void setupListeners() {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hovering = true;
                    if (fadeOut.isRunning())
                        fadeOut.stop();
                    fadeIn.start();
                    pulseTimer.start();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hovering = false;
                    if (fadeIn.isRunning())
                        fadeIn.stop();
                    pulseTimer.stop();
                    fadeOut.start();
                }

                @Override
                public void mousePressed(MouseEvent e) {

                    setLocation(getX(), getY() + 1);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    setLocation(getX(), getY() - 1);
                }
            });
        }

        void setBaseColors(Color base, Color hover, Color glow) {
            this.baseColor = base;
            this.hoverColor = hover;
            this.glowColor = glow;
            setBackground(base);
        }

        void updateTheme(boolean darkMode) {
            this.darkModeLocal = darkMode;
            setForeground(darkMode ? Color.BLACK : Color.BLACK);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            RoundRectangle2D.Float rr = new RoundRectangle2D.Float(0, 0, w, h, 14, 14);

            if (glowAlpha > 0.001f) {

                float cx = w / 2f, cy = h / 2f;
                float radius = Math.max(w, h) * 0.9f;
                float[] dist = { 0f, 0.5f, 1f };
                Color[] colors = {
                        new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(),
                                (int) (glowAlpha * 180)),
                        new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(),
                                (int) (glowAlpha * 90)),
                        new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), 0) };
                try {
                    RadialGradientPaint rgp = new RadialGradientPaint(new Point2D.Float(cx, cy), radius, dist, colors);
                    g2.setPaint(rgp);
                    g2.fill(new Ellipse2D.Float(-w * 0.35f, -h * 0.35f, w * 1.7f, h * 1.7f));
                } catch (Exception ex) {
                    g2.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(),
                            (int) (glowAlpha * 120)));
                    g2.fillOval(-w / 3, -h / 3, w + w / 2, h + h / 2);
                }
            }

            Color fill = hovering ? hoverColor : baseColor;
            g2.setPaint(fill);
            g2.fill(rr);

            g2.setStroke(new BasicStroke(1.2f));
            g2.setColor(new Color(0, 0, 0, 30));
            g2.draw(rr);

            FontMetrics fm = g2.getFontMetrics(getFont());
            String text = getText();
            int tx = (w - fm.stringWidth(text)) / 2;
            int ty = (h - fm.getHeight()) / 2 + fm.getAscent();

            g2.setColor(getForeground());
            g2.setFont(getFont());
            g2.drawString(text, tx, ty);

            g2.dispose();
        }
    }

    // ---------------- RoundedPanel helper ----------------
    private static class RoundedPanel extends JPanel {
        private int radius;

        RoundedPanel(int radius) {
            super();
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ---------------- Main ----------------
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> new CarRentalGUI());
    }
}