package ui;

import db.DAO;
import model.User;
import model.WaterLog;
import util.Theme;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class WaterPanel extends JPanel {

    private User user;
    private JLabel lblTotal, lblPercent;
    private WaterProgressBar progressBar;
    private JPanel logListPanel;
    private List<WaterLog> currentLogs;
    private final int GOAL_ML = 2000;

    public WaterPanel(User user) {
        this.user = user;
        setLayout(null);
        setBackground(Theme.BG_DARK);
        initUI();
        loadData();
    }

    private void initUI() {
        JLabel title = new JLabel("Nước - Theo dõi uống nước");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.ACCENT_CYAN);
        title.setBounds(24, 20, 500, 36);
        add(title);

        // === PROGRESS CARD ===
        JPanel progressCard = Theme.createCard();
        progressCard.setLayout(null);
        progressCard.setBounds(24, 68, 340, 260);
        add(progressCard);

        JLabel lblGoal = new JLabel("Mục tiêu hôm nay: " + GOAL_ML + " ml");
        lblGoal.setFont(Theme.FONT_SMALL);
        lblGoal.setForeground(Theme.TEXT_SECONDARY);
        lblGoal.setBounds(16, 16, 300, 20);
        progressCard.add(lblGoal);

        lblTotal = new JLabel("0 ml");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTotal.setForeground(Theme.ACCENT_CYAN);
        lblTotal.setBounds(16, 42, 300, 48);
        progressCard.add(lblTotal);

        lblPercent = new JLabel("0%");
        lblPercent.setFont(Theme.FONT_HEADING);
        lblPercent.setForeground(Theme.TEXT_SECONDARY);
        lblPercent.setBounds(16, 88, 100, 24);
        progressCard.add(lblPercent);

        progressBar = new WaterProgressBar();
        progressBar.setBounds(16, 120, 308, 36);
        progressCard.add(progressBar);

        JLabel lblTip = new JLabel("Uống đủ nước giúp cơ thể khỏe mạnh!");
        lblTip.setFont(Theme.FONT_SMALL);
        lblTip.setForeground(Theme.TEXT_SECONDARY);
        lblTip.setBounds(16, 168, 308, 18);
        progressCard.add(lblTip);

        JLabel lblStatus = new JLabel("Hãy nhớ uống nước nhé!");
        lblStatus.setFont(Theme.FONT_SMALL);
        lblStatus.setForeground(Theme.ACCENT_CYAN);
        lblStatus.setBounds(16, 196, 308, 20);
        progressCard.add(lblStatus);

        // === QUICK ADD BUTTONS ===
        JPanel quickCard = Theme.createCard();
        quickCard.setLayout(null);
        quickCard.setBounds(24, 344, 340, 220);
        add(quickCard);

        JLabel lblQuick = new JLabel("Thêm nhanh");
        lblQuick.setFont(Theme.FONT_HEADING);
        lblQuick.setForeground(Theme.TEXT_PRIMARY);
        lblQuick.setBounds(16, 14, 200, 24);
        quickCard.add(lblQuick);

        int[] amounts = { 150, 200, 330, 500 };
        String[] icons = { "Cà phê", "Ly nhỏ", "Chai nhỏ", "Chai lớn" };
        int bx = 16;
        for (int i = 0; i < amounts.length; i++) {
            final int ml = amounts[i];
            JButton btn = makeQuickBtn(icons[i] + "\n" + ml + "ml", ml);
            btn.setBounds(bx, 50, 70, 60);
            quickCard.add(btn);
            bx += 75;
        }

        // Custom amount
        addLabel(quickCard, "Số ml tùy chỉnh (1 - 5000 ml)", 16, 122);
        JTextField txtCustom = makeField("Nhập số ml...");
        txtCustom.setBounds(16, 142, 200, 40);
        quickCard.add(txtCustom);

        JButton btnCustom = Theme.createButton("Thêm", Theme.ACCENT_CYAN);
        btnCustom.setBounds(224, 142, 100, 40);
        quickCard.add(btnCustom);
        btnCustom.addActionListener(e -> {
            String val = txtCustom.getText().trim();
            if (val.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số ml!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int ml;
            try {
                ml = Integer.parseInt(val);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Số ml phải là số nguyên (vd: 250)!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (ml < 1 || ml > 5000) {
                JOptionPane.showMessageDialog(this, "Số ml phải từ 1 đến 5000!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            addWater(ml);
            txtCustom.setText("");
        });

        // === LOG LIST ===
        JPanel listCard = Theme.createCard();
        listCard.setLayout(new BorderLayout());
        listCard.setBounds(380, 68, 460, 496);
        add(listCard);

        JPanel listHeader = new JPanel(new BorderLayout());
        listHeader.setBackground(Theme.BG_CARD);
        listHeader.setPreferredSize(new Dimension(460, 44));

        JLabel lblLog = new JLabel("  Lịch sử hôm nay (chọn để xóa)");
        lblLog.setFont(Theme.FONT_HEADING);
        lblLog.setForeground(Theme.TEXT_PRIMARY);
        listHeader.add(lblLog, BorderLayout.CENTER);

        JButton btnDel = new JButton("Xóa");
        btnDel.setFont(Theme.FONT_SMALL.deriveFont(Font.BOLD));
        btnDel.setForeground(new Color(248, 113, 113));
        btnDel.setBackground(Theme.BG_CARD2);
        btnDel.setBorderPainted(false);
        btnDel.setFocusPainted(false);
        btnDel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnDel.setPreferredSize(new Dimension(60, 44));
        listHeader.add(btnDel, BorderLayout.EAST);
        listCard.add(listHeader, BorderLayout.NORTH);

        logListPanel = new JPanel();
        logListPanel.setLayout(new BoxLayout(logListPanel, BoxLayout.Y_AXIS));
        logListPanel.setBackground(Theme.BG_CARD);

        JScrollPane scroll = new JScrollPane(logListPanel);
        scroll.setBackground(Theme.BG_CARD);
        scroll.getViewport().setBackground(Theme.BG_CARD);
        scroll.setBorder(BorderFactory.createEmptyBorder(4, 8, 8, 8));
        listCard.add(scroll, BorderLayout.CENTER);

        btnDel.addActionListener(e -> deleteSelectedLog());
    }

    // Biến lưu index đang được chọn
    private int selectedLogIndex = -1;

    private JButton makeQuickBtn(String label, int ml) {
        JButton btn = new JButton("<html><center>" + label.replace("\n", "<br>") + "</center></html>") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? Theme.ACCENT_CYAN.darker() : Theme.BG_CARD2);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(Theme.FONT_SMALL);
        btn.setForeground(Theme.TEXT_PRIMARY);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> addWater(ml));
        return btn;
    }

    private void addWater(int ml) {
        boolean ok = DAO.addWater(user.getId(), ml);
        if (ok) loadData();
        else JOptionPane.showMessageDialog(this, "Lỗi khi lưu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private void deleteSelectedLog() {
        if (currentLogs == null || currentLogs.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xóa!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (selectedLogIndex < 0 || selectedLogIndex >= currentLogs.size()) {
            JOptionPane.showMessageDialog(this, "Hãy chọn một bản ghi trong danh sách để xóa!", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }
        WaterLog log = currentLogs.get(selectedLogIndex);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        int confirm = JOptionPane.showConfirmDialog(this,
                "Xóa bản ghi " + log.getAmountMl() + " ml lúc " + sdf.format(log.getLogTime()) + "?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        boolean ok = DAO.deleteWater(log.getId());
        if (ok) {
            selectedLogIndex = -1;
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi khi xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadData() {
        int total = DAO.getTodayWater(user.getId());
        double pct = Math.min((double) total / GOAL_ML * 100, 100);

        lblTotal.setText(total + " ml");
        lblPercent.setText(String.format("%.0f%%", pct));
        progressBar.setPercent((int) pct);

        // Reload logs
        logListPanel.removeAll();
        currentLogs = DAO.getTodayWaterLogs(user.getId());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        for (int i = 0; i < currentLogs.size(); i++) {
            final int idx = i;
            WaterLog log = currentLogs.get(i);

            JPanel row = new JPanel(null);
            row.setMaximumSize(new Dimension(440, 44));
            row.setMinimumSize(new Dimension(440, 44));
            row.setPreferredSize(new Dimension(440, 44));
            row.setBackground(i == selectedLogIndex ? new Color(99, 102, 241, 40) : Theme.BG_CARD);
            row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JLabel timeLabel = new JLabel(sdf.format(log.getLogTime()));
            timeLabel.setFont(Theme.FONT_SMALL);
            timeLabel.setForeground(Theme.TEXT_SECONDARY);
            timeLabel.setBounds(12, 12, 60, 20);
            row.add(timeLabel);

            JLabel mlLabel = new JLabel(log.getAmountMl() + " ml");
            mlLabel.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD));
            mlLabel.setForeground(Theme.ACCENT_CYAN);
            mlLabel.setBounds(80, 12, 200, 20);
            row.add(mlLabel);

            JLabel selLabel = new JLabel(idx == selectedLogIndex ? "  [đã chọn]" : "");
            selLabel.setFont(Theme.FONT_SMALL);
            selLabel.setForeground(Theme.ACCENT_ORANGE);
            selLabel.setBounds(270, 12, 130, 20);
            row.add(selLabel);

            JSeparator sep = new JSeparator();
            sep.setForeground(Theme.BORDER);
            sep.setBounds(0, 43, 440, 1);
            row.add(sep);

            row.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    selectedLogIndex = (selectedLogIndex == idx) ? -1 : idx;
                    loadData();
                }
            });

            logListPanel.add(row);
        }

        if (currentLogs.isEmpty()) {
            JLabel empty = new JLabel("  Chưa có ghi chép hôm nay", SwingConstants.CENTER);
            empty.setFont(Theme.FONT_BODY);
            empty.setForeground(Theme.TEXT_SECONDARY);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            logListPanel.add(Box.createVerticalStrut(20));
            logListPanel.add(empty);
        }

        logListPanel.revalidate();
        logListPanel.repaint();
    }

    // Custom progress bar
    class WaterProgressBar extends JPanel {
        private int percent = 0;

        WaterProgressBar() { setOpaque(false); }

        void setPercent(int p) { this.percent = p; repaint(); }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Theme.BG_CARD2);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            int filled = (int) (getWidth() * percent / 100.0);
            if (filled > 0) {
                GradientPaint gp = new GradientPaint(0, 0, Theme.ACCENT_CYAN, filled, 0, Theme.ACCENT_BLUE);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, filled, getHeight(), getHeight(), getHeight());
            }
            g2.dispose();
        }
    }

    private JTextField makeField(String hint) {
        JTextField tf = new JTextField(hint);
        tf.setFont(Theme.FONT_BODY);
        tf.setForeground(Theme.TEXT_SECONDARY);
        tf.setBackground(Theme.BG_CARD2);
        tf.setCaretColor(Theme.TEXT_PRIMARY);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)));
        tf.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (tf.getText().equals(hint)) {
                    tf.setText("");
                    tf.setForeground(Theme.TEXT_PRIMARY);
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (tf.getText().isEmpty()) {
                    tf.setText(hint);
                    tf.setForeground(Theme.TEXT_SECONDARY);
                }
            }
        });
        return tf;
    }

    private void addLabel(JPanel p, String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(Theme.FONT_SMALL);
        lbl.setForeground(Theme.TEXT_SECONDARY);
        lbl.setBounds(x, y, 300, 20);
        p.add(lbl);
    }
}
