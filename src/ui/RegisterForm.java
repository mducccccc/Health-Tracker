package ui;

import db.DAO;
import util.Theme;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class RegisterForm extends JFrame {

    private JTextField txtUsername, txtFullName, txtBirthDate, txtHeight;
    private JPasswordField txtPassword, txtConfirm;
    private JComboBox<String> cbGender;
    private JLabel lblError;

    public RegisterForm() {
        initUI();
    }

    private void initUI() {
        setTitle("Health Tracker - Đăng ký");
        setSize(500, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel main = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(15, 23, 42), 0, getHeight(), new Color(30, 27, 75));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        JLabel lblTitle = new JLabel("Tạo tài khoản", SwingConstants.CENTER);
        lblTitle.setFont(Theme.FONT_TITLE);
        lblTitle.setForeground(Theme.TEXT_PRIMARY);
        lblTitle.setBounds(0, 24, 500, 36);
        main.add(lblTitle);

        JPanel card = Theme.createCard();
        card.setLayout(null);
        card.setBounds(40, 75, 420, 600);
        main.add(card);

        int y = 20;

        card.add(makeLabel("Họ và tên (ít nhất 2 ký tự)", 24, y)); y += 22;
        txtFullName = makeField(); txtFullName.setBounds(24, y, 372, 40); card.add(txtFullName); y += 52;

        card.add(makeLabel("Tên đăng nhập (4-20 ký tự, không dấu)", 24, y)); y += 22;
        txtUsername = makeField(); txtUsername.setBounds(24, y, 372, 40); card.add(txtUsername); y += 52;

        card.add(makeLabel("Mật khẩu (ít nhất 6 ký tự)", 24, y)); y += 22;
        txtPassword = new JPasswordField(); styleField(txtPassword);
        txtPassword.setBounds(24, y, 372, 40); card.add(txtPassword); y += 52;

        card.add(makeLabel("Xác nhận mật khẩu", 24, y)); y += 22;
        txtConfirm = new JPasswordField(); styleField(txtConfirm);
        txtConfirm.setBounds(24, y, 372, 40); card.add(txtConfirm); y += 52;

        card.add(makeLabel("Ngày sinh (yyyy-MM-dd)", 24, y)); y += 22;
        txtBirthDate = makeField(); txtBirthDate.setText("2004-01-01");
        txtBirthDate.setBounds(24, y, 175, 40); card.add(txtBirthDate);

        card.add(makeLabel("Giới tính", 211, y - 22));
        cbGender = new JComboBox<>(new String[]{"Nam", "Nữ"});
        cbGender.setFont(Theme.FONT_BODY);
        cbGender.setBackground(Theme.BG_CARD2);
        cbGender.setForeground(Theme.TEXT_PRIMARY);
        cbGender.setBounds(211, y, 185, 40); card.add(cbGender); y += 52;

        card.add(makeLabel("Chiều cao (cm) — từ 50 đến 250", 24, y)); y += 22;
        txtHeight = makeField(); txtHeight.setText("170");
        txtHeight.setBounds(24, y, 372, 40); card.add(txtHeight); y += 52;

        lblError = new JLabel("", SwingConstants.CENTER);
        lblError.setFont(Theme.FONT_SMALL);
        lblError.setForeground(new Color(248, 113, 113));
        lblError.setBounds(0, y, 420, 20); card.add(lblError); y += 24;

        JButton btnReg = Theme.createButton("Đăng ký", Theme.ACCENT_GREEN);
        btnReg.setBounds(24, y, 372, 44); card.add(btnReg);

        btnReg.addActionListener(e -> doRegister());

        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        backPanel.setOpaque(false);
        backPanel.setBounds(0, 660, 500, 30);
        JLabel lbl = new JLabel("Đã có tài khoản?");
        lbl.setForeground(Theme.TEXT_SECONDARY);
        lbl.setFont(Theme.FONT_SMALL);
        JButton btnBack = new JButton("Đăng nhập");
        btnBack.setFont(Theme.FONT_SMALL.deriveFont(Font.BOLD));
        btnBack.setForeground(Theme.ACCENT_BLUE);
        btnBack.setBorderPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> { dispose(); new LoginForm(); });
        backPanel.add(lbl); backPanel.add(btnBack);
        main.add(backPanel);

        setContentPane(main);
        setVisible(true);
    }

    private JLabel makeLabel(String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(Theme.FONT_SMALL);
        lbl.setForeground(Theme.TEXT_SECONDARY);
        lbl.setBounds(x, y, 372, 20);
        return lbl;
    }

    private JTextField makeField() {
        JTextField tf = new JTextField();
        styleField(tf);
        return tf;
    }

    private void styleField(JTextField tf) {
        tf.setFont(Theme.FONT_BODY);
        tf.setForeground(Theme.TEXT_PRIMARY);
        tf.setBackground(Theme.BG_CARD2);
        tf.setCaretColor(Theme.TEXT_PRIMARY);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER, 1),
            BorderFactory.createEmptyBorder(0, 12, 0, 12)));
    }

    private void doRegister() {
        String fullName  = txtFullName.getText().trim();
        String username  = txtUsername.getText().trim();
        String password  = new String(txtPassword.getPassword());
        String confirm   = new String(txtConfirm.getPassword());
        String dob       = txtBirthDate.getText().trim();
        String gender    = (String) cbGender.getSelectedItem();
        String heightStr = txtHeight.getText().trim();

        // Validate họ tên
        if (fullName.isEmpty()) {
            lblError.setText("Vui lòng nhập họ và tên!"); return;
        }
        if (fullName.length() < 2) {
            lblError.setText("Họ và tên phải có ít nhất 2 ký tự!"); return;
        }

        // Validate username
        if (username.isEmpty()) {
            lblError.setText("Vui lòng nhập tên đăng nhập!"); return;
        }
        if (username.length() < 4 || username.length() > 20) {
            lblError.setText("Tên đăng nhập phải từ 4 đến 20 ký tự!"); return;
        }
        if (!username.matches("[a-zA-Z0-9_]+")) {
            lblError.setText("Tên đăng nhập chỉ dùng chữ, số và dấu gạch dưới!"); return;
        }

        // Validate mật khẩu
        if (password.isEmpty()) {
            lblError.setText("Vui lòng nhập mật khẩu!"); return;
        }
        if (password.length() < 6) {
            lblError.setText("Mật khẩu phải có ít nhất 6 ký tự!"); return;
        }
        if (!password.equals(confirm)) {
            lblError.setText("Mật khẩu xác nhận không khớp!"); return;
        }

        // Validate ngày sinh
        if (!isValidDate(dob)) {
            lblError.setText("Ngày sinh không hợp lệ! Định dạng: yyyy-MM-dd"); return;
        }

        // Validate chiều cao
        if (heightStr.isEmpty()) {
            lblError.setText("Vui lòng nhập chiều cao!"); return;
        }
        float height;
        try {
            height = Float.parseFloat(heightStr);
        } catch (NumberFormatException e) {
            lblError.setText("Chiều cao phải là số (vd: 170)!"); return;
        }
        if (height < 50 || height > 250) {
            lblError.setText("Chiều cao phải từ 50 đến 250 cm!"); return;
        }

        lblError.setText("");
        boolean ok = DAO.register(username, password, fullName, dob, gender, height);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Đăng ký thành công! Hãy đăng nhập.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new LoginForm();
        } else {
            lblError.setText("Tên đăng nhập đã tồn tại hoặc lỗi!");
        }
    }

    private boolean isValidDate(String s) {
        if (s == null || s.trim().isEmpty()) return false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try { sdf.parse(s.trim()); return true; }
        catch (ParseException e) { return false; }
    }
}

