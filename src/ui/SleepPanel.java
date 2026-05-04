package ui;

import db.DAO;
import model.SleepLog;
import model.User;
import util.Theme;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class SleepPanel extends JPanel {

    private User user;
    private JTextField txtSleepTime, txtWakeTime;
    private JComboBox<String> cbQuality;
    private JTextField txtNote;
    private JLabel lblDuration;
    private JTable table;
    private DefaultTableModel tableModel;
    private List<SleepLog> currentList;

    public SleepPanel(User user) {
        this.user = user;
        setLayout(null);
        setBackground(Theme.BG_DARK);
        initUI();
        loadData();
    }

    private void initUI() {
        JLabel title = new JLabel("[z]  Theo dõi giấc ngủ");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_PRIMARY);
        title.setBounds(24, 20, 400, 36);
        add(title);

        // === INPUT CARD ===
        JPanel inputCard = Theme.createCard();
        inputCard.setLayout(null);
        inputCard.setBounds(24, 68, 340, 380);
        add(inputCard);

        JLabel lblIn = new JLabel("Ghi giấc ngủ");
        lblIn.setFont(Theme.FONT_HEADING);
        lblIn.setForeground(Theme.TEXT_PRIMARY);
        lblIn.setBounds(16, 16, 280, 24);
        inputCard.add(lblIn);

        // Mặc định giờ ngủ = hôm qua 22:00, giờ thức = hôm nay 06:00
        SimpleDateFormat dtFmt = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Calendar cal = java.util.Calendar.getInstance();
        String today = dtFmt.format(cal.getTime());
        cal.add(java.util.Calendar.DAY_OF_MONTH, -1);
        String yesterday = dtFmt.format(cal.getTime());

        addLabel(inputCard, "Giờ đi ngủ (yyyy-MM-dd HH:mm)", 16, 50);
        txtSleepTime = makeField(yesterday + " 22:00");
        txtSleepTime.setBounds(16, 72, 308, 40);
        inputCard.add(txtSleepTime);

        addLabel(inputCard, "Giờ thức dậy (yyyy-MM-dd HH:mm)", 16, 122);
        txtWakeTime = makeField(today + " 06:00");
        txtWakeTime.setBounds(16, 144, 308, 40);
        inputCard.add(txtWakeTime);

        lblDuration = new JLabel(" Thời gian ngủ: —");
        lblDuration.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD));
        lblDuration.setForeground(Theme.ACCENT_PINK);
        lblDuration.setBounds(16, 192, 308, 24);
        inputCard.add(lblDuration);

        javax.swing.event.DocumentListener dl = new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { calcDuration(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { calcDuration(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { calcDuration(); }
        };
        txtSleepTime.getDocument().addDocumentListener(dl);
        txtWakeTime.getDocument().addDocumentListener(dl);

        addLabel(inputCard, "Chất lượng giấc ngủ", 16, 224);
        cbQuality = new JComboBox<>(new String[]{ "Rất tốt", "Tốt", "Bình thường", "Tệ" });
        cbQuality.setFont(Theme.FONT_BODY);
        cbQuality.setBackground(Theme.BG_CARD2);
        cbQuality.setForeground(Theme.TEXT_PRIMARY);
        cbQuality.setBounds(16, 246, 308, 40);
        inputCard.add(cbQuality);

        addLabel(inputCard, "Ghi chú", 16, 294);
        txtNote = makeField("Ghi chú...");
        txtNote.setBounds(16, 316, 308, 40);
        inputCard.add(txtNote);

        JButton btnSave = Theme.createButton("  Lưu giấc ngủ", Theme.ACCENT_PINK);
        btnSave.setBounds(16, 370, 308, 44);
        inputCard.setSize(340, 428);
        inputCard.add(btnSave);
        btnSave.addActionListener(e -> saveSleep());

        // === DELETE BUTTON ===
        JButton btnDelete = Theme.createButton("  Xóa dòng đã chọn", new Color(220, 53, 69));
        btnDelete.setBounds(24, 510, 340, 40);
        add(btnDelete);
        btnDelete.addActionListener(e -> deleteSelected());

        // === STATS CARD ===
        JPanel statsCard = Theme.createCard();
        statsCard.setLayout(null);
        statsCard.setBounds(24, 562, 340, 100);
        add(statsCard);

        JLabel lblStats = new JLabel("[#] Trung bình 7 ngày gần nhất");
        lblStats.setFont(Theme.FONT_SMALL);
        lblStats.setForeground(Theme.TEXT_SECONDARY);
        lblStats.setBounds(16, 14, 300, 18);
        statsCard.add(lblStats);

        JLabel lblAvg = new JLabel(getAvgSleepLabel());
        lblAvg.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblAvg.setForeground(Theme.ACCENT_PINK);
        lblAvg.setBounds(16, 38, 300, 32);
        statsCard.add(lblAvg);

        JLabel lblIdeal = new JLabel("Lý tưởng: 7 - 9 tiếng mỗi đêm");
        lblIdeal.setFont(Theme.FONT_SMALL);
        lblIdeal.setForeground(Theme.TEXT_SECONDARY);
        lblIdeal.setBounds(16, 72, 300, 18);
        statsCard.add(lblIdeal);

        // === TABLE ===
        JPanel tableCard = Theme.createCard();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBounds(380, 68, 490, 594);
        add(tableCard);

        JLabel lblHist = new JLabel("  [=]  Lịch sử 14 đêm (chọn dòng để xóa)");
        lblHist.setFont(Theme.FONT_HEADING);
        lblHist.setForeground(Theme.TEXT_PRIMARY);
        lblHist.setPreferredSize(new Dimension(490, 40));
        tableCard.add(lblHist, BorderLayout.NORTH);

        String[] cols = { "Ngày ngủ", "Thức dậy", "Thời gian", "Chất lượng", "Ghi chú" };
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        styleTable();

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Theme.BG_CARD);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
        tableCard.add(scroll, BorderLayout.CENTER);
    }

    private void calcDuration() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            java.util.Date sleep = sdf.parse(txtSleepTime.getText().trim());
            java.util.Date wake = sdf.parse(txtWakeTime.getText().trim());
            long diff = wake.getTime() - sleep.getTime();
            if (diff > 0) {
                double hours = diff / (1000.0 * 60 * 60);
                lblDuration.setText(String.format(" Thời gian ngủ: %.1f tiếng", hours));
                lblDuration.setForeground(
                        hours >= 7 ? Theme.ACCENT_GREEN : hours >= 5 ? Theme.ACCENT_ORANGE : new Color(248, 113, 113));
            } else {
                lblDuration.setText(" Giờ thức dậy phải sau giờ đi ngủ!");
                lblDuration.setForeground(new Color(248, 113, 113));
            }
        } catch (Exception e) {
            lblDuration.setText(" Thời gian ngủ: —");
            lblDuration.setForeground(Theme.ACCENT_PINK);
        }
    }

    private void saveSleep() {
        String sleepStr = txtSleepTime.getText().trim();
        String wakeStr = txtWakeTime.getText().trim();
        String quality = (String) cbQuality.getSelectedItem();
        String note = txtNote.getText().trim();

        // Validate không rỗng
        if (sleepStr.isEmpty() || wakeStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nhập đầy đủ giờ ngủ và thức!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate định dạng datetime
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        sdf.setLenient(false);
        java.util.Date sleepDate, wakeDate;
        try {
            sleepDate = sdf.parse(sleepStr);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Giờ đi ngủ không đúng định dạng!\nĐịnh dạng: yyyy-MM-dd HH:mm (vd: 2025-05-01 22:30)", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            wakeDate = sdf.parse(wakeStr);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Giờ thức dậy không đúng định dạng!\nĐịnh dạng: yyyy-MM-dd HH:mm (vd: 2025-05-02 06:00)", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate giờ thức > giờ ngủ
        if (!wakeDate.after(sleepDate)) {
            JOptionPane.showMessageDialog(this, "Giờ thức dậy phải sau giờ đi ngủ!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate thời gian ngủ hợp lý (không quá 24 tiếng)
        double hours = (wakeDate.getTime() - sleepDate.getTime()) / (1000.0 * 60 * 60);
        if (hours > 24) {
            JOptionPane.showMessageDialog(this, "Thời gian ngủ không thể hơn 24 tiếng!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean ok = DAO.addSleep(user.getId(), sleepStr + ":00", wakeStr + ":00", quality, note);
        if (ok) {
            JOptionPane.showMessageDialog(this, "[v] Đã lưu giấc ngủ!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "[x] Lỗi khi lưu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Hãy chọn một dòng trong bảng để xóa!", "Chưa chọn dòng", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa bản ghi giấc ngủ ngày " + tableModel.getValueAt(row, 0) + "?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        SleepLog selected = currentList.get(row);
        boolean ok = DAO.deleteSleep(selected.getId());
        if (ok) {
            JOptionPane.showMessageDialog(this, "Đã xóa bản ghi!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi khi xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadData() {
        tableModel.setRowCount(0);
        currentList = DAO.getSleepHistory(user.getId());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm");
        for (SleepLog s : currentList) {
            tableModel.addRow(new Object[]{
                    sdf.format(s.getSleepTime()),
                    sdf.format(s.getWakeTime()),
                    String.format("%.1f tiếng", s.getDurationHours()),
                    s.getQuality(),
                    s.getNote() != null ? s.getNote() : ""
            });
        }
    }

    private String getAvgSleepLabel() {
        List<SleepLog> list = DAO.getSleepHistory(user.getId());
        if (list.isEmpty()) return "Chưa có dữ liệu";
        int count = Math.min(7, list.size());
        double total = 0;
        for (int i = 0; i < count; i++) total += list.get(i).getDurationHours();
        return String.format("%.1f tiếng / đêm", total / count);
    }

    private void styleTable() {
        table.setFont(Theme.FONT_BODY);
        table.setForeground(Theme.TEXT_PRIMARY);
        table.setBackground(Theme.BG_CARD);
        table.setRowHeight(36);
        table.setGridColor(Theme.BORDER);
        table.setSelectionBackground(new Color(236, 72, 153, 80));
        table.setSelectionForeground(Theme.TEXT_PRIMARY);
        table.getTableHeader().setFont(Theme.FONT_SMALL.deriveFont(Font.BOLD));
        table.getTableHeader().setBackground(Theme.BG_CARD2);
        table.getTableHeader().setForeground(Theme.TEXT_SECONDARY);

        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, r, c);
                String v = val != null ? val.toString() : "";
                lbl.setForeground(v.equals("Rất tốt") ? Theme.ACCENT_GREEN
                        : v.equals("Tốt") ? Theme.ACCENT_CYAN
                        : v.equals("Bình thường") ? Theme.ACCENT_ORANGE : new Color(248, 113, 113));
                lbl.setBackground(sel ? new Color(236, 72, 153, 80) : Theme.BG_CARD);
                return lbl;
            }
        });
    }

    private JTextField makeField(String text) {
        JTextField tf = new JTextField(text);
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
        lbl.setBounds(x, y, 308, 20);
        p.add(lbl);
    }
}
