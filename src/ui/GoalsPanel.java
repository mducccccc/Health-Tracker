package ui;

import db.DAO;
import model.*;
import util.Theme;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class GoalsPanel extends JPanel {

    private User user;
    private JComboBox<String> cmbType;
    private JTextField txtTarget, txtDeadline;
    private JLabel lblTargetHint;
    private JPanel goalsListPanel;
    private List<Goal> currentGoals;

    // Stats labels
    private JLabel lblTotal, lblActive, lblAchieved, lblFailed;

    private static final String[] GOAL_TYPES = {"Giảm cân", "Tăng cân", "Uống nước", "Giấc ngủ"};

    public GoalsPanel(User user) {
        this.user = user;
        setLayout(null);
        setBackground(Theme.BG_DARK);
        initUI();
        loadData();
    }

    private void initUI() {
        // Title
        JLabel title = new JLabel("[◎]  Mục tiêu sức khỏe");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_PRIMARY);
        title.setBounds(24, 20, 500, 36);
        add(title);

        JLabel subtitle = new JLabel("Đặt mục tiêu và theo dõi tiến độ của bạn");
        subtitle.setFont(Theme.FONT_SMALL);
        subtitle.setForeground(Theme.TEXT_SECONDARY);
        subtitle.setBounds(24, 54, 400, 18);
        add(subtitle);

        // === INPUT CARD ===
        JPanel inputCard = Theme.createCard();
        inputCard.setLayout(null);
        inputCard.setBounds(24, 82, 320, 310);
        add(inputCard);

        JLabel lblInput = new JLabel("Thêm mục tiêu mới");
        lblInput.setFont(Theme.FONT_HEADING);
        lblInput.setForeground(Theme.TEXT_PRIMARY);
        lblInput.setBounds(16, 16, 280, 24);
        inputCard.add(lblInput);

        // Goal type
        addLabel(inputCard, "Loại mục tiêu", 16, 50);
        cmbType = new JComboBox<>(GOAL_TYPES);
        cmbType.setFont(Theme.FONT_BODY);
        cmbType.setBackground(Theme.BG_CARD2);
        cmbType.setForeground(Theme.TEXT_PRIMARY);
        cmbType.setBounds(16, 72, 285, 38);
        inputCard.add(cmbType);

        // Target value
        lblTargetHint = new JLabel("Cân nặng mục tiêu (kg)");
        lblTargetHint.setFont(Theme.FONT_SMALL);
        lblTargetHint.setForeground(Theme.TEXT_SECONDARY);
        lblTargetHint.setBounds(16, 118, 285, 20);
        inputCard.add(lblTargetHint);
        txtTarget = makeField("0.0");
        txtTarget.setBounds(16, 140, 285, 38);
        inputCard.add(txtTarget);

        cmbType.addActionListener(e -> updateTargetHint());

        // Deadline
        addLabel(inputCard, "Hạn hoàn thành (yyyy-MM-dd)", 16, 186);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.MONTH, 1);
        txtDeadline = makeField(sdf.format(cal.getTime()));
        txtDeadline.setBounds(16, 208, 285, 38);
        inputCard.add(txtDeadline);

        // Add button
        JButton btnAdd = Theme.createButton("  Thêm mục tiêu", Theme.ACCENT_GREEN);
        btnAdd.setBounds(16, 258, 285, 42);
        inputCard.add(btnAdd);
        btnAdd.addActionListener(e -> addGoal());

        // === STATS CARD ===
        JPanel statsCard = Theme.createCard();
        statsCard.setLayout(null);
        statsCard.setBounds(24, 406, 320, 160);
        add(statsCard);

        JLabel lblStats = new JLabel("Thống kê");
        lblStats.setFont(Theme.FONT_HEADING);
        lblStats.setForeground(Theme.TEXT_PRIMARY);
        lblStats.setBounds(16, 14, 200, 24);
        statsCard.add(lblStats);

        lblTotal = makeStatLabel(statsCard, "Tổng mục tiêu:", "0", Theme.TEXT_PRIMARY, 46);
        lblActive = makeStatLabel(statsCard, "Đang thực hiện:", "0", Theme.ACCENT_BLUE, 72);
        lblAchieved = makeStatLabel(statsCard, "Đạt được:", "0", Theme.ACCENT_GREEN, 98);
        lblFailed = makeStatLabel(statsCard, "Thất bại:", "0", new Color(248, 113, 113), 124);

        // === GOALS LIST ===
        JPanel listCard = Theme.createCard();
        listCard.setLayout(new BorderLayout());
        listCard.setBounds(360, 82, 500, 484);
        add(listCard);

        JPanel listHeader = new JPanel(new BorderLayout());
        listHeader.setBackground(Theme.BG_CARD);
        listHeader.setPreferredSize(new Dimension(500, 44));

        JLabel lblGoals = new JLabel("  Danh sách mục tiêu");
        lblGoals.setFont(Theme.FONT_HEADING);
        lblGoals.setForeground(Theme.TEXT_PRIMARY);
        listHeader.add(lblGoals, BorderLayout.CENTER);

        JButton btnRefresh = Theme.createButton("Kiểm tra", Theme.ACCENT_BLUE);
        btnRefresh.setPreferredSize(new Dimension(100, 44));
        listHeader.add(btnRefresh, BorderLayout.EAST);
        btnRefresh.addActionListener(e -> loadData());

        listCard.add(listHeader, BorderLayout.NORTH);

        goalsListPanel = new JPanel();
        goalsListPanel.setLayout(new BoxLayout(goalsListPanel, BoxLayout.Y_AXIS));
        goalsListPanel.setBackground(Theme.BG_CARD);

        JScrollPane scroll = new JScrollPane(goalsListPanel);
        scroll.setBackground(Theme.BG_CARD);
        scroll.getViewport().setBackground(Theme.BG_CARD);
        scroll.setBorder(BorderFactory.createEmptyBorder(4, 8, 8, 8));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        listCard.add(scroll, BorderLayout.CENTER);
    }

    // ===================== ACTIONS =====================

    private void addGoal() {
        String type = (String) cmbType.getSelectedItem();
        String targetStr = txtTarget.getText().trim();
        String deadlineStr = txtDeadline.getText().trim();

        // Validate target
        float target;
        try {
            target = Float.parseFloat(targetStr);
        } catch (NumberFormatException ex) {
            showError("Giá trị mục tiêu phải là số!"); return;
        }
        if (target <= 0) {
            showError("Giá trị mục tiêu phải lớn hơn 0!"); return;
        }

        // Validate specific ranges
        if (("Giảm cân".equals(type) || "Tăng cân".equals(type)) && (target < 20 || target > 300)) {
            showError("Cân nặng mục tiêu phải từ 20 đến 300 kg!"); return;
        }
        if ("Uống nước".equals(type) && (target < 500 || target > 10000)) {
            showError("Lượng nước mục tiêu phải từ 500 đến 10000 ml!"); return;
        }
        if ("Giấc ngủ".equals(type) && (target < 1 || target > 24)) {
            showError("Số giờ ngủ mục tiêu phải từ 1 đến 24!"); return;
        }

        // Validate deadline
        if (!isValidDate(deadlineStr)) {
            showError("Ngày không hợp lệ! Định dạng: yyyy-MM-dd"); return;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            java.util.Date d = sdf.parse(deadlineStr);
            if (d.before(new java.util.Date())) {
                showError("Hạn hoàn thành phải là ngày trong tương lai!"); return;
            }
        } catch (ParseException ex) {
            showError("Ngày không hợp lệ!"); return;
        }

        boolean ok = DAO.addGoal(user.getId(), type, target, deadlineStr);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Đã thêm mục tiêu!", "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } else {
            showError("Lỗi khi lưu mục tiêu!");
        }
    }

    private void deleteGoal(Goal goal) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Xóa mục tiêu \"" + goal.getGoalType() + " - " + goal.getTargetValue() + goal.getUnit() + "\"?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        if (DAO.deleteGoal(goal.getId())) {
            loadData();
        } else {
            showError("Lỗi khi xóa!");
        }
    }

    // ===================== AUTO CHECK =====================

    private void autoCheckGoals() {
        if (currentGoals == null) return;
        for (Goal g : currentGoals) {
            if (!"Đang thực hiện".equals(g.getStatus())) continue;

            boolean achieved = isGoalAchieved(g);
            if (achieved) {
                DAO.updateGoalStatus(g.getId(), "Đạt được");
                g.setStatus("Đạt được");
            } else if (g.isExpired()) {
                DAO.updateGoalStatus(g.getId(), "Thất bại");
                g.setStatus("Thất bại");
            }
        }
    }

    private boolean isGoalAchieved(Goal g) {
        switch (g.getGoalType()) {
            case "Giảm cân":
                WeightLog w1 = DAO.getLatestWeight(user.getId());
                return w1 != null && w1.getWeightKg() <= g.getTargetValue();
            case "Tăng cân":
                WeightLog w2 = DAO.getLatestWeight(user.getId());
                return w2 != null && w2.getWeightKg() >= g.getTargetValue();
            case "Uống nước":
                return DAO.getTodayWater(user.getId()) >= g.getTargetValue();
            case "Giấc ngủ":
                SleepLog s = DAO.getLatestSleep(user.getId());
                return s != null && s.getDurationHours() >= g.getTargetValue();
            default: return false;
        }
    }

    private float getCurrentValue(Goal g) {
        switch (g.getGoalType()) {
            case "Giảm cân":
            case "Tăng cân":
                WeightLog w = DAO.getLatestWeight(user.getId());
                return w != null ? w.getWeightKg() : 0;
            case "Uống nước":
                return DAO.getTodayWater(user.getId());
            case "Giấc ngủ":
                SleepLog s = DAO.getLatestSleep(user.getId());
                return s != null ? (float) s.getDurationHours() : 0;
            default: return 0;
        }
    }

    private int getProgress(Goal g) {
        float current = getCurrentValue(g);
        float target = g.getTargetValue();
        if (target <= 0) return 0;

        if ("Đạt được".equals(g.getStatus())) return 100;

        switch (g.getGoalType()) {
            case "Giảm cân":
                if (current <= target) return 100;
                return (int) Math.min(100, Math.max(0, (target / current) * 100));
            case "Tăng cân":
            case "Uống nước":
            case "Giấc ngủ":
                if (current >= target) return 100;
                return (int) Math.min(100, (current / target) * 100);
            default: return 0;
        }
    }

    // ===================== LOAD DATA =====================

    private void loadData() {
        currentGoals = DAO.getGoals(user.getId());
        autoCheckGoals();
        // Reload after auto-check may have changed statuses
        currentGoals = DAO.getGoals(user.getId());

        // Update stats
        int total = currentGoals.size();
        int active = 0, achieved = 0, failed = 0;
        for (Goal g : currentGoals) {
            switch (g.getStatus()) {
                case "Đang thực hiện": active++; break;
                case "Đạt được": achieved++; break;
                case "Thất bại": failed++; break;
            }
        }
        lblTotal.setText(String.valueOf(total));
        lblActive.setText(String.valueOf(active));
        lblAchieved.setText(String.valueOf(achieved));
        lblFailed.setText(String.valueOf(failed));

        // Rebuild goals list
        goalsListPanel.removeAll();

        if (currentGoals.isEmpty()) {
            goalsListPanel.add(Box.createVerticalStrut(40));
            JLabel empty = new JLabel("Chưa có mục tiêu nào. Hãy thêm mục tiêu đầu tiên!");
            empty.setFont(Theme.FONT_BODY);
            empty.setForeground(Theme.TEXT_SECONDARY);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            goalsListPanel.add(empty);
        } else {
            for (Goal g : currentGoals) {
                goalsListPanel.add(makeGoalCard(g));
                goalsListPanel.add(Box.createVerticalStrut(8));
            }
        }

        goalsListPanel.revalidate();
        goalsListPanel.repaint();
    }

    // ===================== GOAL CARD =====================

    private JPanel makeGoalCard(Goal goal) {
        int progress = getProgress(goal);
        float current = getCurrentValue(goal);
        Color statusColor = getStatusColor(goal.getStatus());
        Color accentColor = getTypeColor(goal.getGoalType());

        JPanel card = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_CARD2);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                // Left accent bar
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0, 5, getHeight(), 4, 4);
                g2.dispose();
            }
        };
        card.setMaximumSize(new Dimension(480, 135));
        card.setMinimumSize(new Dimension(480, 135));
        card.setPreferredSize(new Dimension(480, 135));
        card.setOpaque(false);

        // Icon + Type
        JLabel lblType = new JLabel(goal.getIcon() + "  " + goal.getGoalType());
        lblType.setFont(Theme.FONT_HEADING);
        lblType.setForeground(accentColor);
        lblType.setBounds(16, 10, 200, 24);
        card.add(lblType);

        // Status badge
        JLabel lblStatus = new JLabel(goal.getStatus()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(statusColor.getRed(), statusColor.getGreen(), statusColor.getBlue(), 40));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setFont(Theme.FONT_SMALL.deriveFont(Font.BOLD));
                g2.setColor(statusColor);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        lblStatus.setBounds(310, 10, 110, 24);
        lblStatus.setOpaque(false);
        card.add(lblStatus);

        // Current vs Target
        String currentStr;
        if ("Uống nước".equals(goal.getGoalType())) {
            currentStr = String.format("Hiện tại: %.0f %s  →  Mục tiêu: %.0f %s",
                    current, goal.getUnit(), goal.getTargetValue(), goal.getUnit());
        } else {
            currentStr = String.format("Hiện tại: %.1f %s  →  Mục tiêu: %.1f %s",
                    current, goal.getUnit(), goal.getTargetValue(), goal.getUnit());
        }
        JLabel lblValues = new JLabel(currentStr);
        lblValues.setFont(Theme.FONT_SMALL);
        lblValues.setForeground(Theme.TEXT_SECONDARY);
        lblValues.setBounds(16, 38, 400, 18);
        card.add(lblValues);

        // Progress bar
        JPanel progressBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Track
                g2.setColor(Theme.BG_DARK);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                // Fill
                int fillW = (int) (getWidth() * progress / 100.0);
                if (fillW > 0) {
                    GradientPaint gp = new GradientPaint(0, 0, accentColor, fillW, 0, accentColor.brighter());
                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, fillW, getHeight(), 10, 10);
                }
                g2.dispose();
            }
        };
        progressBar.setBounds(16, 62, 350, 12);
        progressBar.setOpaque(false);
        card.add(progressBar);

        JLabel lblProgress = new JLabel(progress + "%");
        lblProgress.setFont(Theme.FONT_SMALL.deriveFont(Font.BOLD));
        lblProgress.setForeground(accentColor);
        lblProgress.setBounds(372, 58, 48, 18);
        card.add(lblProgress);

        // Deadline
        String deadlineStr = "";
        if (goal.getDeadline() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            long days = goal.getDaysRemaining();
            if ("Đang thực hiện".equals(goal.getStatus())) {
                deadlineStr = "Hạn: " + sdf.format(goal.getDeadline())
                        + (days <= 0 ? "  (Đã hết hạn!)" : "  (Còn " + days + " ngày)");
            } else {
                deadlineStr = "Hạn: " + sdf.format(goal.getDeadline());
            }
        }
        JLabel lblDeadline = new JLabel(deadlineStr);
        lblDeadline.setFont(Theme.FONT_SMALL);
        long daysLeft = goal.getDaysRemaining();
        lblDeadline.setForeground(
                daysLeft <= 0 ? new Color(248, 113, 113) :
                daysLeft <= 3 ? Theme.ACCENT_ORANGE : Theme.TEXT_SECONDARY);
        lblDeadline.setBounds(16, 82, 320, 18);
        card.add(lblDeadline);

        // Delete button
        JButton btnDel = new JButton("Xóa") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(248, 113, 113, 40) : new Color(0, 0, 0, 0));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setFont(Theme.FONT_SMALL.deriveFont(Font.BOLD));
                g2.setColor(new Color(248, 113, 113));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btnDel.setBounds(420, 100, 55, 28);
        btnDel.setBorderPainted(false);
        btnDel.setContentAreaFilled(false);
        btnDel.setFocusPainted(false);
        btnDel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnDel.addActionListener(e -> deleteGoal(goal));
        card.add(btnDel);

        // Manual achieve button (only for active goals)
        if ("Đang thực hiện".equals(goal.getStatus())) {
            JButton btnDone = new JButton("Đánh dấu đạt") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getModel().isRollover() ? Theme.ACCENT_GREEN.darker() : new Color(34, 197, 94, 40));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.setFont(Theme.FONT_SMALL.deriveFont(Font.BOLD));
                    g2.setColor(Theme.ACCENT_GREEN);
                    FontMetrics fm = g2.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(getText())) / 2;
                    int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                    g2.drawString(getText(), x, y);
                    g2.dispose();
                }
            };
            btnDone.setBounds(16, 104, 110, 26);
            btnDone.setBorderPainted(false);
            btnDone.setContentAreaFilled(false);
            btnDone.setFocusPainted(false);
            btnDone.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnDone.addActionListener(e -> {
                DAO.updateGoalStatus(goal.getId(), "Đạt được");
                loadData();
            });
            card.add(btnDone);
        }

        return card;
    }

    // ===================== HELPERS =====================

    private void updateTargetHint() {
        String type = (String) cmbType.getSelectedItem();
        switch (type) {
            case "Giảm cân": lblTargetHint.setText("Cân nặng mục tiêu (kg) — 20 ~ 300"); break;
            case "Tăng cân": lblTargetHint.setText("Cân nặng mục tiêu (kg) — 20 ~ 300"); break;
            case "Uống nước": lblTargetHint.setText("Lượng nước mục tiêu (ml/ngày) — 500 ~ 10000"); break;
            case "Giấc ngủ": lblTargetHint.setText("Số giờ ngủ mục tiêu — 1 ~ 24"); break;
        }
    }

    private Color getStatusColor(String status) {
        switch (status) {
            case "Đang thực hiện": return Theme.ACCENT_BLUE;
            case "Đạt được": return Theme.ACCENT_GREEN;
            case "Thất bại": return new Color(248, 113, 113);
            default: return Theme.TEXT_SECONDARY;
        }
    }

    private Color getTypeColor(String type) {
        switch (type) {
            case "Giảm cân": return Theme.ACCENT_PINK;
            case "Tăng cân": return Theme.ACCENT_ORANGE;
            case "Uống nước": return Theme.ACCENT_CYAN;
            case "Giấc ngủ": return new Color(167, 139, 250); // purple
            default: return Theme.ACCENT_BLUE;
        }
    }

    private JLabel makeStatLabel(JPanel parent, String label, String value, Color valueColor, int y) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(Theme.FONT_BODY);
        lbl.setForeground(Theme.TEXT_SECONDARY);
        lbl.setBounds(16, y, 180, 22);
        parent.add(lbl);

        JLabel val = new JLabel(value);
        val.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD));
        val.setForeground(valueColor);
        val.setBounds(200, y, 100, 22);
        parent.add(val);
        return val;
    }

    private boolean isValidDate(String s) {
        if (s == null || s.trim().isEmpty()) return false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try { sdf.parse(s.trim()); return true; }
        catch (ParseException e) { return false; }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Lỗi", JOptionPane.WARNING_MESSAGE);
    }

    private JTextField makeField(String placeholder) {
        JTextField tf = new JTextField(placeholder);
        tf.setFont(Theme.FONT_BODY);
        tf.setForeground(Theme.TEXT_PRIMARY);
        tf.setBackground(Theme.BG_CARD2);
        tf.setCaretColor(Theme.TEXT_PRIMARY);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)));
        return tf;
    }

    private void addLabel(JPanel p, String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(Theme.FONT_SMALL);
        lbl.setForeground(Theme.TEXT_SECONDARY);
        lbl.setBounds(x, y, 285, 20);
        p.add(lbl);
    }
}
