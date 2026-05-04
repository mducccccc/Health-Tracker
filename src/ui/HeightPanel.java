package ui;

import db.DAO;
import model.HeightLog;
import model.User;
import util.Theme;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class HeightPanel extends JPanel {

    private User user;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtHeight, txtDate, txtNote;
    private JLabel lblCategory, lblCurrentHeight;
    private List<HeightLog> currentList;

    public HeightPanel(User user) {
        this.user = user;
        setLayout(null);
        setBackground(Theme.BG_DARK);
        initUI();
        loadData();
    }

    private void initUI() {
        // Title
        JLabel title = new JLabel("[|]  Theo dõi chiều cao");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.ACCENT_GREEN);
        title.setBounds(24, 20, 400, 36);
        add(title);

        // === INPUT CARD ===
        JPanel inputCard = Theme.createCard();
        inputCard.setLayout(null);
        inputCard.setBounds(24, 68, 320, 460);
        add(inputCard);

        JLabel lblInput = new JLabel("Ghi chiều cao mới");
        lblInput.setFont(Theme.FONT_HEADING);
        lblInput.setForeground(Theme.TEXT_PRIMARY);
        lblInput.setBounds(16, 16, 280, 24);
        inputCard.add(lblInput);

        // Current height info
        lblCurrentHeight = new JLabel("Chiều cao hiện tại: " +
                (user.getHeightCm() > 0 ? String.format("%.1f cm", user.getHeightCm()) : "Chưa có"));
        lblCurrentHeight.setFont(Theme.FONT_SMALL);
        lblCurrentHeight.setForeground(Theme.ACCENT_GREEN);
        lblCurrentHeight.setBounds(16, 46, 285, 20);
        inputCard.add(lblCurrentHeight);

        // Height field
        addLabel(inputCard, "Chiều cao (cm) — từ 50 đến 250 cm", 16, 74);
        txtHeight = makeField("0.0");
        txtHeight.setBounds(16, 96, 285, 40);
        inputCard.add(txtHeight);

        // Category display
        lblCategory = new JLabel("Phân loại: —");
        lblCategory.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblCategory.setForeground(Theme.ACCENT_GREEN);
        lblCategory.setBounds(16, 144, 285, 24);
        inputCard.add(lblCategory);

        txtHeight.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateCategory();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateCategory();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateCategory();
            }
        });

        // Date
        addLabel(inputCard, "Ngày (yyyy-MM-dd)", 16, 176);
        txtDate = makeField(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        txtDate.setBounds(16, 198, 285, 40);
        inputCard.add(txtDate);

        // Note
        addLabel(inputCard, "Ghi chú", 16, 246);
        txtNote = makeField("Ghi chú...");
        txtNote.setBounds(16, 268, 285, 40);
        inputCard.add(txtNote);

        // Info box
        JPanel infoBox = new JPanel(null);
        infoBox.setBackground(new Color(52, 211, 153, 30));
        infoBox.setBounds(16, 320, 285, 80);
        infoBox.setBorder(BorderFactory.createLineBorder(Theme.ACCENT_GREEN, 1));
        inputCard.add(infoBox);

        JLabel infoTitle = new JLabel("Phân loại chiều cao (người lớn VN)");
        infoTitle.setFont(Theme.FONT_SMALL.deriveFont(Font.BOLD));
        infoTitle.setForeground(Theme.ACCENT_GREEN);
        infoTitle.setBounds(8, 8, 270, 18);
        infoBox.add(infoTitle);

        String[] cats = { "< 150: Thấp  |  150-160: Trung bình thấp",
                "160-175: Trung bình  |  175-185: Cao  |  >185: Rất cao" };
        for (int i = 0; i < cats.length; i++) {
            JLabel c = new JLabel(cats[i]);
            c.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            c.setForeground(Theme.TEXT_SECONDARY);
            c.setBounds(8, 28 + i * 18, 270, 16);
            infoBox.add(c);
        }

        JButton btnAdd = Theme.createButton("  Lưu chiều cao", Theme.ACCENT_GREEN);
        btnAdd.setBounds(16, 412, 285, 44);
        inputCard.add(btnAdd);
        btnAdd.addActionListener(e -> saveHeight());

        // === DELETE BUTTON + TABLE ===
        JPanel tableCard = Theme.createCard();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBounds(360, 68, 510, 530);
        add(tableCard);

        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setBackground(Theme.BG_CARD);
        tableHeader.setPreferredSize(new Dimension(510, 44));

        JLabel lblHist = new JLabel("  [|]  Lịch sử chiều cao (chọn dòng để xóa)");
        lblHist.setFont(Theme.FONT_HEADING);
        lblHist.setForeground(Theme.TEXT_PRIMARY);
        tableHeader.add(lblHist, BorderLayout.CENTER);

        JButton btnDel = new JButton(" Xóa");
        btnDel.setFont(Theme.FONT_SMALL.deriveFont(Font.BOLD));
        btnDel.setForeground(new Color(248, 113, 113));
        btnDel.setBackground(Theme.BG_CARD2);
        btnDel.setBorderPainted(false);
        btnDel.setFocusPainted(false);
        btnDel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnDel.setPreferredSize(new Dimension(80, 44));
        tableHeader.add(btnDel, BorderLayout.EAST);
        tableCard.add(tableHeader, BorderLayout.NORTH);

        String[] cols = { "Ngày", "Chiều cao (cm)", "Phân loại", "Ghi chú" };
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(tableModel);
        styleTable();

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBackground(Theme.BG_CARD);
        scroll.getViewport().setBackground(Theme.BG_CARD);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
        tableCard.add(scroll, BorderLayout.CENTER);

        btnDel.addActionListener(e -> deleteSelected());
    }

    private void updateCategory() {
        try {
            float h = Float.parseFloat(txtHeight.getText().trim());
            HeightLog temp = new HeightLog();
            temp.setHeightCm(h);
            String cat = temp.getHeightCategory();
            lblCategory.setText("Phân loại: " + cat);
            lblCategory.setForeground(
                    h < 150 ? new Color(248, 113, 113)
                            : h < 160 ? Theme.ACCENT_ORANGE
                                    : h < 175 ? Theme.ACCENT_GREEN
                                            : h < 185 ? Theme.ACCENT_CYAN
                                                    : Theme.ACCENT_BLUE);
        } catch (NumberFormatException ex) {
            lblCategory.setText("Phân loại: —");
            lblCategory.setForeground(Theme.TEXT_SECONDARY);
        }
    }

    private void saveHeight() {
        String heightStr = txtHeight.getText().trim();
        String dateStr = txtDate.getText().trim();
        String note = txtNote.getText().trim();

        // Validate chiều cao
        if (heightStr.isEmpty()) {
            showError("Vui lòng nhập chiều cao!");
            return;
        }
        float height;
        try {
            height = Float.parseFloat(heightStr);
        } catch (NumberFormatException ex) {
            showError("Chiều cao phải là số (vd: 170.5)!");
            return;
        }
        if (height < 50 || height > 250) {
            showError("Chiều cao phải từ 50 đến 250 cm!");
            return;
        }

        // Validate ngày
        if (!isValidDate(dateStr)) {
            showError("Ngày không hợp lệ! Định dạng: yyyy-MM-dd (vd: 2025-05-01)");
            return;
        }

        boolean ok = DAO.addHeight(user.getId(), height, dateStr, note);
        if (ok) {
            // Cập nhật chiều cao vào bảng users để tính BMI
            DAO.updateUserHeight(user.getId(), height);
            user.setHeightCm(height);
            lblCurrentHeight.setText("Chiều cao hiện tại: " + String.format("%.1f cm", height));
            JOptionPane.showMessageDialog(this, "[v] Đã lưu chiều cao!\nChiều cao mới sẽ được dùng để tính BMI.",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "[x] Lỗi khi lưu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Hãy chọn một dòng trong bảng để xóa!", "Chưa chọn dòng",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa bản ghi chiều cao ngày " + tableModel.getValueAt(row, 0) + "?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION)
            return;

        HeightLog selected = currentList.get(row);
        boolean ok = DAO.deleteHeight(selected.getId());
        if (ok) {
            JOptionPane.showMessageDialog(this, "Đã xóa bản ghi!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi khi xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadData() {
        tableModel.setRowCount(0);
        currentList = DAO.getHeightHistory(user.getId());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (HeightLog h : currentList) {
            tableModel.addRow(new Object[] {
                    sdf.format(h.getLogDate()),
                    String.format("%.1f cm", h.getHeightCm()),
                    h.getHeightCategory(),
                    h.getNote() != null ? h.getNote() : ""
            });
        }
    }

    private boolean isValidDate(String s) {
        if (s == null || s.trim().isEmpty())
            return false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            sdf.parse(s.trim());
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
    }

    private void styleTable() {
        table.setFont(Theme.FONT_BODY);
        table.setForeground(Theme.TEXT_PRIMARY);
        table.setBackground(Theme.BG_CARD);
        table.setRowHeight(36);
        table.setGridColor(Theme.BORDER);
        table.setSelectionBackground(new Color(52, 211, 153, 80));
        table.setSelectionForeground(Theme.TEXT_PRIMARY);
        table.getTableHeader().setFont(Theme.FONT_SMALL.deriveFont(Font.BOLD));
        table.getTableHeader().setBackground(Theme.BG_CARD2);
        table.getTableHeader().setForeground(Theme.TEXT_SECONDARY);
        table.setShowGrid(true);

        // Color height category column
        table.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int r,
                    int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, r, c);
                String v = val != null ? val.toString() : "";
                lbl.setForeground(
                        v.equals("Thấp") ? new Color(248, 113, 113)
                                : v.equals("Trung bình thấp") ? Theme.ACCENT_ORANGE
                                        : v.equals("Trung bình") ? Theme.ACCENT_GREEN
                                                : v.equals("Cao") ? Theme.ACCENT_CYAN
                                                        : Theme.ACCENT_BLUE);
                lbl.setBackground(sel ? new Color(52, 211, 153, 80) : Theme.BG_CARD);
                return lbl;
            }
        });
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
