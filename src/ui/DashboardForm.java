package ui;

import db.DAO;
import model.*;
import util.Theme;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

public class DashboardForm extends JFrame {

    private User currentUser;
    private JPanel contentPanel;
    private JButton activeBtn;

    // Sidebar buttons
    private JButton btnDashboard, btnWeight, btnWater, btnSleep, btnHeight, btnLogout;

    public DashboardForm(User user) {
        this.currentUser = user;
        initUI();
    }

    private void initUI() {
        setTitle("Health Tracker - " + currentUser.getFullName());
        setSize(1100, 720);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Theme.BG_DARK);

        add(buildSidebar(), BorderLayout.WEST);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Theme.BG_DARK);
        add(contentPanel, BorderLayout.CENTER);

        showDashboard();
        setVisible(true);
    }

    // ===================== SIDEBAR =====================
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(null);
        sidebar.setBackground(Theme.BG_CARD);
        sidebar.setPreferredSize(new Dimension(220, 720));

        JLabel lblApp = new JLabel("[♥]  Health Tracker");
        lblApp.setFont(Theme.FONT_HEADING);
        lblApp.setForeground(Theme.TEXT_PRIMARY);
        lblApp.setBounds(20, 28, 185, 30);
        sidebar.add(lblApp);

        JPanel userCard = new JPanel(null);
        userCard.setBackground(Theme.BG_CARD2);
        userCard.setBounds(12, 72, 196, 64);
        JLabel avatar = new JLabel("", SwingConstants.CENTER);
        avatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        avatar.setBounds(8, 8, 40, 48);
        userCard.add(avatar);
        JLabel lblName = new JLabel(currentUser.getFullName());
        lblName.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD));
        lblName.setForeground(Theme.TEXT_PRIMARY);
        lblName.setBounds(54, 10, 138, 22);
        userCard.add(lblName);
        JLabel lblUname = new JLabel("@" + currentUser.getUsername());
        lblUname.setFont(Theme.FONT_SMALL);
        lblUname.setForeground(Theme.TEXT_SECONDARY);
        lblUname.setBounds(54, 34, 138, 18);
        userCard.add(lblUname);
        sidebar.add(userCard);

        JLabel sep = new JLabel("MENU");
        sep.setFont(new Font("Segoe UI", Font.BOLD, 10));
        sep.setForeground(Theme.TEXT_SECONDARY);
        sep.setBounds(20, 152, 100, 18);
        sidebar.add(sep);

        btnDashboard = makeSidebarBtn("[H]  Tổng quan",  175);
        btnWeight    = makeSidebarBtn("[=]  Cân nặng",   225);
        btnWater     = makeSidebarBtn("[~]  Uống nước",  275);
        btnSleep     = makeSidebarBtn("[z]  Giấc ngủ",   325);
        btnHeight    = makeSidebarBtn("[↕]  Chiều cao",  375);

        sidebar.add(btnDashboard);
        sidebar.add(btnWeight);
        sidebar.add(btnWater);
        sidebar.add(btnSleep);
        sidebar.add(btnHeight);

        btnDashboard.addActionListener(e -> { setActive(btnDashboard); showDashboard(); });
        btnWeight.addActionListener(e    -> { setActive(btnWeight);    showWeight(); });
        btnWater.addActionListener(e     -> { setActive(btnWater);     showWater(); });
        btnSleep.addActionListener(e     -> { setActive(btnSleep);     showSleep(); });
        btnHeight.addActionListener(e    -> { setActive(btnHeight);    showHeight(); });

        btnLogout = makeSidebarBtn("  Đăng xuất", 640);
        btnLogout.setForeground(new Color(248, 113, 113));
        sidebar.add(btnLogout);
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginForm();
        });

        return sidebar;
    }

    private JButton makeSidebarBtn(String text, int y) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (this == activeBtn) {
                    g2.setColor(new Color(99, 102, 241, 60));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.setColor(Theme.ACCENT_BLUE);
                    g2.fillRoundRect(0, 6, 4, 28, 4, 4);
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(255,255,255,15));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                }
                g2.setFont(Theme.FONT_BODY);
                g2.setColor(this == activeBtn ? Theme.TEXT_PRIMARY :
                            getForeground() != null ? getForeground() : Theme.TEXT_SECONDARY);
                g2.drawString(getText(), 20, getHeight()/2 + 5);
                g2.dispose();
            }
        };
        btn.setBounds(8, y, 204, 42);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setForeground(Theme.TEXT_SECONDARY);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void setActive(JButton btn) {
        activeBtn = btn;
        repaint();
    }

    // ===================== DASHBOARD =====================
    private void showDashboard() {
        setActive(btnDashboard);
        contentPanel.removeAll();

        JPanel panel = new JPanel(null);
        panel.setBackground(Theme.BG_DARK);

        JLabel title = new JLabel("Xin chào, " + currentUser.getFullName() + "! ");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_PRIMARY);
        title.setBounds(30, 24, 700, 36);
        panel.add(title);

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd/MM/yyyy");
        JLabel dateLabel = new JLabel(sdf.format(new java.util.Date()));
        dateLabel.setFont(Theme.FONT_SMALL);
        dateLabel.setForeground(Theme.TEXT_SECONDARY);
        dateLabel.setBounds(30, 60, 300, 20);
        panel.add(dateLabel);

        // Stats cards
        WeightLog latest     = DAO.getLatestWeight(currentUser.getId());
        int waterToday       = DAO.getTodayWater(currentUser.getId());
        SleepLog latestSleep = DAO.getLatestSleep(currentUser.getId());
        HeightLog latestH    = DAO.getLatestHeight(currentUser.getId());

        String weightVal = latest != null ? latest.getWeightKg() + " kg" : "Chưa có";
        String bmiVal    = latest != null ? String.format("%.1f", latest.getBmi()) + " (" + latest.getBmiCategory() + ")" : "—";
        String waterVal  = waterToday + " ml / " + 2000 + " ml";
        String sleepVal  = latestSleep != null ? String.format("%.1fh", latestSleep.getDurationHours()) : "Chưa có";
        String sleepQ    = latestSleep != null ? latestSleep.getQuality() : "—";
        String heightVal = latestH != null ? String.format("%.1f cm", latestH.getHeightCm()) : (currentUser.getHeightCm() > 0 ? String.format("%.1f cm", currentUser.getHeightCm()) : "Chưa có");
        String heightCat = latestH != null ? latestH.getHeightCategory() : "—";

        // Row 1: Cân nặng + Nước
        panel.add(makeDashCard("[=]", "Cân nặng",       weightVal, bmiVal,   Theme.ACCENT_BLUE,   30,  100, 375, 140));
        panel.add(makeDashCard("[~]", "Nước hôm nay",   waterVal,  waterToday >= 2000 ? "[v] Đủ rồi!" : "Cần uống thêm", Theme.ACCENT_CYAN, 420, 100, 375, 140));
        // Row 2: Giấc ngủ + Chiều cao
        panel.add(makeDashCard("[z]", "Giấc ngủ gần nhất", sleepVal, "Chất lượng: " + sleepQ, Theme.ACCENT_PINK,   30,  260, 375, 140));
        panel.add(makeDashCard("[↕]", "Chiều cao",      heightVal, "Phân loại: " + heightCat, Theme.ACCENT_GREEN, 420, 260, 375, 140));

        // Quick actions
        JLabel lblQuick = new JLabel("Thao tác nhanh");
        lblQuick.setFont(Theme.FONT_HEADING);
        lblQuick.setForeground(Theme.TEXT_PRIMARY);
        lblQuick.setBounds(30, 430, 300, 28);
        panel.add(lblQuick);

        JButton qWeight = Theme.createButton("+ Ghi cân nặng", Theme.ACCENT_BLUE);
        qWeight.setBounds(30, 468, 160, 44);
        panel.add(qWeight);
        qWeight.addActionListener(e -> { setActive(btnWeight); showWeight(); });

        JButton qWater = Theme.createButton("+ Uống nước", Theme.ACCENT_CYAN);
        qWater.setBounds(205, 468, 145, 44);
        panel.add(qWater);
        qWater.addActionListener(e -> { setActive(btnWater); showWater(); });

        JButton qSleep = Theme.createButton("+ Ghi ngủ", Theme.ACCENT_PINK);
        qSleep.setBounds(365, 468, 130, 44);
        panel.add(qSleep);
        qSleep.addActionListener(e -> { setActive(btnSleep); showSleep(); });

        JButton qHeight = Theme.createButton("+ Chiều cao", Theme.ACCENT_GREEN);
        qHeight.setBounds(510, 468, 130, 44);
        panel.add(qHeight);
        qHeight.addActionListener(e -> { setActive(btnHeight); showHeight(); });

        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel makeDashCard(String icon, String title, String value, String sub, Color accent, int x, int y, int w, int h) {
        JPanel card = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, getWidth(), 4, 4, 4);
                g2.dispose();
            }
        };
        card.setBounds(x, y, w, h);

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        lblIcon.setBounds(16, 14, 42, 36);
        card.add(lblIcon);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(Theme.FONT_SMALL);
        lblTitle.setForeground(Theme.TEXT_SECONDARY);
        lblTitle.setBounds(60, 16, 300, 18);
        card.add(lblTitle);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblValue.setForeground(accent);
        lblValue.setBounds(16, 58, w - 32, 28);
        card.add(lblValue);

        JLabel lblSub = new JLabel(sub);
        lblSub.setFont(Theme.FONT_SMALL);
        lblSub.setForeground(Theme.TEXT_SECONDARY);
        lblSub.setBounds(16, 94, w - 32, 20);
        card.add(lblSub);

        return card;
    }

    // ===================== WEIGHT =====================
    private void showWeight() {
        setActive(btnWeight);
        contentPanel.removeAll();
        contentPanel.add(new WeightPanel(currentUser), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // ===================== WATER =====================
    private void showWater() {
        setActive(btnWater);
        contentPanel.removeAll();
        contentPanel.add(new WaterPanel(currentUser), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // ===================== SLEEP =====================
    private void showSleep() {
        setActive(btnSleep);
        contentPanel.removeAll();
        contentPanel.add(new SleepPanel(currentUser), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // ===================== HEIGHT =====================
    private void showHeight() {
        setActive(btnHeight);
        contentPanel.removeAll();
        contentPanel.add(new HeightPanel(currentUser), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
