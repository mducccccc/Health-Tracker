package ui;

import db.DAO;
import model.*;
import util.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class HealthScorePanel extends JPanel {

    private User currentUser;

    // Score components (0–100 each)
    private int bmiScore;
    private int sleepScore;
    private int waterScore;
    private int totalScore;

    // Animation
    private int animatedScore = 0;
    private Timer animTimer;

    // Detail strings
    private String bmiDetail = "—";
    private String sleepDetail = "—";
    private String waterDetail = "—";
    private String bmiStatus = "Chưa có dữ liệu";
    private String sleepStatus = "Chưa có dữ liệu";
    private String waterStatus = "Chưa có dữ liệu";

    public HealthScorePanel(User user) {
        this.currentUser = user;
        setLayout(null);
        setBackground(Theme.BG_DARK);
        calculateScores();
        buildUI();
        startAnimation();
    }

    // ===================== TÍNH ĐIỂM =====================
    private void calculateScores() {
        // --- BMI Score (40 điểm) ---
        WeightLog w = DAO.getLatestWeight(currentUser.getId());
        if (w != null) {
            float bmi = w.getBmi();
            bmiDetail = String.format("BMI hiện tại: %.1f (%s)", bmi, w.getBmiCategory());
            if (bmi >= 18.5 && bmi < 25.0) {
                bmiScore = 40;
                bmiStatus = "Rất tốt – Cân nặng lý tưởng!";
            } else if ((bmi >= 17.0 && bmi < 18.5) || (bmi >= 25.0 && bmi < 27.5)) {
                bmiScore = 28;
                bmiStatus = "Khá – Gần mức lý tưởng";
            } else if ((bmi >= 15.0 && bmi < 17.0) || (bmi >= 27.5 && bmi < 30.0)) {
                bmiScore = 15;
                bmiStatus = "Trung bình – Cần cải thiện";
            } else {
                bmiScore = 5;
                bmiStatus = "Yếu – Cần điều chỉnh chế độ ăn";
            }
        } else {
            bmiScore = 0;
            bmiDetail = "Chưa có dữ liệu cân nặng";
            bmiStatus = "Hãy ghi cân nặng để tính điểm";
        }

        // --- Sleep Score (35 điểm) ---
        SleepLog sl = DAO.getLatestSleep(currentUser.getId());
        if (sl != null) {
            double hours = sl.getDurationHours();
            String quality = sl.getQuality();
            sleepDetail = String.format("Ngủ %.1f giờ  Chất lượng: %s", hours, quality);

            int baseScore;
            if (hours >= 7 && hours <= 9)
                baseScore = 25;
            else if ((hours >= 6 && hours < 7) || (hours > 9 && hours <= 10))
                baseScore = 17;
            else if ((hours >= 5 && hours < 6) || (hours > 10 && hours <= 11))
                baseScore = 10;
            else
                baseScore = 4;

            int qualityBonus = 0;
            if ("Tốt".equals(quality))
                qualityBonus = 10;
            else if ("Bình thường".equals(quality))
                qualityBonus = 6;
            else if ("Kém".equals(quality))
                qualityBonus = 2;

            sleepScore = Math.min(35, baseScore + qualityBonus);

            if (sleepScore >= 30)
                sleepStatus = "Rất tốt  Ngủ đủ và chất lượng!";
            else if (sleepScore >= 20)
                sleepStatus = "Khá  Nên cải thiện giấc ngủ";
            else if (sleepScore >= 10)
                sleepStatus = "Trung bình  Cố gắng ngủ 7-9 tiếng";
            else
                sleepStatus = "Yếu  Giấc ngủ cần cải thiện nhiều";
        } else {
            sleepScore = 0;
            sleepDetail = "Chưa có dữ liệu giấc ngủ";
            sleepStatus = "Hãy ghi giấc ngủ để tính điểm";
        }

        // --- Water Score (25 điểm) ---
        int waterMl = DAO.getTodayWater(currentUser.getId());
        waterDetail = String.format("Hôm nay đã uống: %d ml / 2000 ml", waterMl);
        double waterRatio = waterMl / 2000.0;
        if (waterRatio >= 1.0) {
            waterScore = 25;
            waterStatus = "Rất tốt  Uống đủ nước!";
        } else if (waterRatio >= 0.75) {
            waterScore = 18;
            waterStatus = "Khá  Uống thêm một chút nữa";
        } else if (waterRatio >= 0.5) {
            waterScore = 12;
            waterStatus = "Trung bình  Uống nhiều nước hơn";
        } else if (waterRatio >= 0.25) {
            waterScore = 6;
            waterStatus = "Yếu  Bạn thiếu nước nghiêm trọng";
        } else {
            waterScore = 0;
            waterStatus = "Chưa uống nước hôm nay!";
        }

        totalScore = bmiScore + sleepScore + waterScore;
    }

    // ===================== BUILD UI =====================
    private void buildUI() {
        // Title
        JLabel title = new JLabel("Điểm Sức Khỏe Hôm Nay");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_PRIMARY);
        title.setBounds(30, 24, 500, 36);
        add(title);

        JLabel subtitle = new JLabel("Tổng hợp từ BMI · Giấc ngủ · Lượng nước");
        subtitle.setFont(Theme.FONT_SMALL);
        subtitle.setForeground(Theme.TEXT_SECONDARY);
        subtitle.setBounds(30, 60, 500, 20);
        add(subtitle);

        // ---- Score Ring Card ----
        ScoreRingPanel ringPanel = new ScoreRingPanel();
        ringPanel.setBounds(30, 95, 280, 280);
        add(ringPanel);

        // ---- Grade label ----
        JLabel gradeLabel = new JLabel(getGradeText(totalScore), SwingConstants.CENTER);
        gradeLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        gradeLabel.setForeground(getScoreColor(totalScore));
        gradeLabel.setBounds(30, 385, 280, 24);
        add(gradeLabel);

        JLabel tipsLabel = new JLabel(getHealthTip(totalScore), SwingConstants.CENTER);
        tipsLabel.setFont(Theme.FONT_SMALL);
        tipsLabel.setForeground(Theme.TEXT_SECONDARY);
        tipsLabel.setBounds(30, 410, 280, 18);
        add(tipsLabel);

        // ---- Breakdown cards (right side) ----
        add(makeBreakdownCard("  Chỉ số BMI", bmiDetail, bmiStatus, bmiScore, 40, Theme.ACCENT_BLUE, 330, 95));
        add(makeBreakdownCard("  Giấc ngủ", sleepDetail, sleepStatus, sleepScore, 35, Theme.ACCENT_PINK, 330, 235));
        add(makeBreakdownCard("  Nước uống", waterDetail, waterStatus, waterScore, 25, Theme.ACCENT_CYAN, 330, 375));

        // ---- Formula note ----
        JPanel noteCard = makeNoteCard();
        noteCard.setBounds(330, 515, 490, 80);
        add(noteCard);
    }

    // ---- Score Ring (drawn with Graphics2D) ----
    private class ScoreRingPanel extends JPanel {
        ScoreRingPanel() {
            setOpaque(false);
            setLayout(null);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Card background
            g2.setColor(Theme.BG_CARD);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

            int cx = getWidth() / 2;
            int cy = getHeight() / 2;
            int radius = 90;
            int stroke = 16;

            // Track ring
            g2.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(Theme.BG_CARD2);
            g2.drawOval(cx - radius, cy - radius, radius * 2, radius * 2);

            // Score arc
            Color scoreColor = getScoreColor(animatedScore);
            g2.setColor(scoreColor);
            double angle = animatedScore / 100.0 * 360.0;
            g2.draw(new Arc2D.Double(cx - radius, cy - radius, radius * 2, radius * 2,
                    90, -angle, Arc2D.OPEN));

            // Score number
            g2.setFont(new Font("Segoe UI", Font.BOLD, 48));
            g2.setColor(scoreColor);
            FontMetrics fm = g2.getFontMetrics();
            String scoreStr = String.valueOf(animatedScore);
            g2.drawString(scoreStr, cx - fm.stringWidth(scoreStr) / 2, cy + fm.getAscent() / 2 - 8);

            // "/100" label
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            g2.setColor(Theme.TEXT_SECONDARY);
            g2.drawString("/100", cx - 18, cy + 28);

            g2.dispose();
        }
    }

    // ---- Breakdown card ----
    private JPanel makeBreakdownCard(String title, String detail, String status,
            int score, int maxScore, Color accent, int x, int y) {
        JPanel card = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, getWidth(), 4, 4, 4);
                g2.dispose();
            }
        };
        card.setBounds(x, y, 490, 125);
        card.setOpaque(false);

        // Title
        JLabel lTitle = new JLabel(title);
        lTitle.setFont(Theme.FONT_HEADING);
        lTitle.setForeground(Theme.TEXT_PRIMARY);
        lTitle.setBounds(16, 14, 300, 22);
        card.add(lTitle);

        // Score badge
        JLabel lScore = new JLabel(score + " / " + maxScore + " pt");
        lScore.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lScore.setForeground(accent);
        lScore.setHorizontalAlignment(SwingConstants.RIGHT);
        lScore.setBounds(330, 14, 144, 22);
        card.add(lScore);

        // Detail
        JLabel lDetail = new JLabel(detail);
        lDetail.setFont(Theme.FONT_SMALL);
        lDetail.setForeground(Theme.TEXT_SECONDARY);
        lDetail.setBounds(16, 44, 458, 18);
        card.add(lDetail);

        // Mini progress bar
        JPanel barBg = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_CARD2);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                if (maxScore > 0) {
                    int fillW = (int) ((score / (double) maxScore) * getWidth());
                    g2.setColor(accent);
                    g2.fillRoundRect(0, 0, fillW, getHeight(), 8, 8);
                }
                g2.dispose();
            }
        };
        barBg.setBounds(16, 70, 458, 10);
        barBg.setOpaque(false);
        card.add(barBg);

        // Status
        JLabel lStatus = new JLabel(status);
        lStatus.setFont(Theme.FONT_SMALL);
        lStatus.setForeground(accent);
        lStatus.setBounds(16, 88, 458, 18);
        card.add(lStatus);

        return card;
    }

    // ---- Formula note card ----
    private JPanel makeNoteCard() {
        JPanel card = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 41, 59));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(99, 102, 241, 80));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };
        card.setOpaque(false);

        JLabel formula = new JLabel(
                "Công thức: Điểm = BMI (40pt) + Giấc ngủ (35pt) + Nước uống (25pt)");
        formula.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formula.setForeground(Theme.TEXT_PRIMARY);
        formula.setBounds(16, 14, 458, 22);
        card.add(formula);

        JLabel note = new JLabel("Dữ liệu được lấy từ lần ghi gần nhất. Uống nước được tính theo hôm nay.");
        note.setFont(Theme.FONT_SMALL);
        note.setForeground(Theme.TEXT_SECONDARY);
        note.setBounds(16, 42, 458, 18);
        card.add(note);

        return card;
    }

    // ---- Animation ----
    private void startAnimation() {
        animatedScore = 0;
        animTimer = new Timer(12, e -> {
            if (animatedScore < totalScore) {
                animatedScore++;
                repaint();
            } else {
                animTimer.stop();
            }
        });
        animTimer.start();
    }

    // ---- Helpers ----
    private Color getScoreColor(int score) {
        if (score >= 80)
            return new Color(34, 197, 94); // xanh lá
        if (score >= 60)
            return new Color(234, 179, 8); // vàng
        if (score >= 40)
            return new Color(251, 146, 60); // cam
        return new Color(239, 68, 68); // đỏ
    }

    private String getGradeText(int score) {
        if (score >= 90)
            return " Xuất sắc  Bạn đang rất khỏe mạnh!";
        if (score >= 75)
            return " Tốt  Tiếp tục duy trì nhé!";
        if (score >= 55)
            return " Trung bình  Cần cải thiện một số mặt";
        if (score >= 30)
            return " Yếu  Hãy chú ý hơn đến sức khỏe";
        return " Rất yếu  Hãy bắt đầu theo dõi sức khỏe ngay";
    }

    private String getHealthTip(int score) {
        if (score >= 90)
            return "Giữ vững thói quen tốt này mỗi ngày!";
        if (score >= 75)
            return "Thêm vài ly nước và ngủ đủ giấc là hoàn hảo.";
        if (score >= 55)
            return "Hãy uống đủ nước và điều chỉnh giờ ngủ.";
        if (score >= 30)
            return "Ưu tiên giấc ngủ và cân bằng chế độ ăn.";
        return "Hãy bắt đầu bằng cách uống 1 ly nước ngay bây giờ!";
    }
}
