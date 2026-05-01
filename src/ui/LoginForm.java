package ui;

import db.DAO;
import model.User;
import util.Theme;

import javax.swing.*;
import java.awt.*;

public class LoginForm extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnRegister;
    private JLabel lblError;

    public LoginForm() {
        Theme.applyDarkTheme();
        initUI();
    }

    private void initUI() {
        setTitle("Health Tracker - Đăng nhập");
        setSize(480, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Gradient background
                GradientPaint gp = new GradientPaint(0, 0, new Color(15, 23, 42),
                        0, getHeight(), new Color(30, 27, 75));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Decorative circles
                g2.setColor(new Color(99, 102, 241, 40));
                g2.fillOval(-60, -60, 200, 200);
                g2.fillOval(320, 380, 180, 180);
            }
        };
        mainPanel.setOpaque(false);

        // Logo/Icon area
        JLabel lblIcon = new JLabel("♥", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI", Font.BOLD, 56));
        lblIcon.setForeground(new Color(236, 72, 153));
        lblIcon.setBounds(0, 50, 480, 70);
        mainPanel.add(lblIcon);

        // Title
        JLabel lblTitle = new JLabel("Health Tracker", SwingConstants.CENTER);
        lblTitle.setFont(Theme.FONT_TITLE.deriveFont(28f));
        lblTitle.setForeground(Theme.TEXT_PRIMARY);
        lblTitle.setBounds(0, 125, 480, 36);
        mainPanel.add(lblTitle);

        JLabel lblSub = new JLabel("Theo dõi sức khỏe mỗi ngày", SwingConstants.CENTER);
        lblSub.setFont(Theme.FONT_SMALL);
        lblSub.setForeground(Theme.TEXT_SECONDARY);
        lblSub.setBounds(0, 162, 480, 22);
        mainPanel.add(lblSub);

        // Card panel
        JPanel card = Theme.createCard();
        card.setLayout(null);
        card.setBounds(60, 210, 360, 290);
        mainPanel.add(card);

        // Username
        JLabel lblUser = new JLabel("Tên đăng nhập");
        lblUser.setFont(Theme.FONT_SMALL);
        lblUser.setForeground(Theme.TEXT_SECONDARY);
        lblUser.setBounds(24, 20, 200, 20);
        card.add(lblUser);

        txtUsername = createTextField();
        txtUsername.setBounds(24, 42, 312, 42);
        card.add(txtUsername);

        // Password
        JLabel lblPass = new JLabel("Mật khẩu");
        lblPass.setFont(Theme.FONT_SMALL);
        lblPass.setForeground(Theme.TEXT_SECONDARY);
        lblPass.setBounds(24, 96, 200, 20);
        card.add(lblPass);

        txtPassword = new JPasswordField();
        styleTextField(txtPassword);
        txtPassword.setBounds(24, 118, 312, 42);
        card.add(txtPassword);

        // Error label
        lblError = new JLabel("", SwingConstants.CENTER);
        lblError.setFont(Theme.FONT_SMALL);
        lblError.setForeground(new Color(248, 113, 113));
        lblError.setBounds(0, 168, 360, 20);
        card.add(lblError);

        // Login button
        btnLogin = Theme.createButton("Đăng nhập", Theme.ACCENT_BLUE);
        btnLogin.setBounds(24, 196, 312, 44);
        card.add(btnLogin);

        btnLogin.addActionListener(e -> doLogin());

        txtPassword.addActionListener(e -> doLogin());

        // Register link
        JPanel regPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        regPanel.setOpaque(false);
        regPanel.setBounds(0, 510, 480, 30);
        JLabel lblRegText = new JLabel("Chưa có tài khoản?");
        lblRegText.setFont(Theme.FONT_SMALL);
        lblRegText.setForeground(Theme.TEXT_SECONDARY);
        btnRegister = new JButton("Đăng ký ngay");
        btnRegister.setFont(Theme.FONT_SMALL.deriveFont(Font.BOLD));
        btnRegister.setForeground(Theme.ACCENT_BLUE);
        btnRegister.setBorderPainted(false);
        btnRegister.setContentAreaFilled(false);
        btnRegister.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        regPanel.add(lblRegText);
        regPanel.add(btnRegister);
        mainPanel.add(regPanel);

        btnRegister.addActionListener(e -> openRegister());

        setContentPane(mainPanel);
        setVisible(true);
    }

    private JTextField createTextField() {
        JTextField tf = new JTextField();
        styleTextField(tf);
        return tf;
    }

    private void styleTextField(JTextField tf) {
        tf.setFont(Theme.FONT_BODY);
        tf.setForeground(Theme.TEXT_PRIMARY);
        tf.setBackground(Theme.BG_CARD2);
        tf.setCaretColor(Theme.TEXT_PRIMARY);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)));
    }

    private void doLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        User user = DAO.login(username, password);
        if (user != null) {
            dispose();
            new DashboardForm(user);
        } else {
            lblError.setText("Sai tên đăng nhập hoặc mật khẩu!");
            txtPassword.setText("");
        }
    }

    private void openRegister() {
        dispose();
        new RegisterForm();
    }
}
