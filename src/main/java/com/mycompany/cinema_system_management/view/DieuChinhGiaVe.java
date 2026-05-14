package com.mycompany.cinema_system_management.view;

import com.mycompany.cinema_system_management.DAO.BangGiaVeDAO;
import com.mycompany.cinema_system_management.models.BangGiaVe;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class DieuChinhGiaVe extends JPanel {

    private BangGiaVeDAO giaVeDAO = new BangGiaVeDAO();
    private JTable table;
    private DefaultTableModel tableModel;
    private List<BangGiaVe> danhSachGiaVe;
    private JScrollPane scrollPane;

    public DieuChinhGiaVe() {
        setLayout(new BorderLayout());
        setBackground(new Color(241, 245, 249));
        setBorder(new EmptyBorder(25, 25, 25, 25));

        add(createHeader(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);

        loadDuLieu();
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(0, 80));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Bảng Giá Vé");
        title.setFont(new Font("Inter", Font.BOLD, 30));
        title.setForeground(new Color(15, 23, 42));

        JLabel sub = new JLabel("Quản lý và cập nhật giá vé theo loại ghế và định dạng phim.");
        sub.setFont(new Font("Inter", Font.PLAIN, 14));
        sub.setForeground(new Color(100, 116, 139));

        left.add(title);
        left.add(Box.createVerticalStrut(8));
        left.add(sub);

        panel.add(left, BorderLayout.WEST);
        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        tableModel = new DefaultTableModel(new String[]{"MÃ BG", "Loại Ghế", "Định Dạng", "Đơn Giá (đ)", "Thao Tác"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        table = new JTable(tableModel);
        setupModernTable(table);

        TableColumnModel colModel = table.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(70);  
        colModel.getColumn(1).setPreferredWidth(120); 
        colModel.getColumn(2).setPreferredWidth(100);  
        colModel.getColumn(3).setPreferredWidth(150); 
        
        TableColumn actionColumn = colModel.getColumn(4);
        actionColumn.setCellRenderer(new ActionRenderer());
        actionColumn.setCellEditor(new ActionEditor(new JCheckBox()));
        actionColumn.setPreferredWidth(80);
        actionColumn.setMinWidth(80);
        actionColumn.setMaxWidth(80);

        scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void setupModernTable(JTable table) {
        table.setRowHeight(60); 
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(226, 232, 240));
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(new Color(30, 41, 59));
        table.setBackground(Color.WHITE);
        table.setFont(new Font("Inter", Font.PLAIN, 14));
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Inter", Font.BOLD, 13));
        header.setBackground(new Color(249, 250, 251));
        header.setPreferredSize(new Dimension(0, 45));
        header.setForeground(new Color(71, 85, 105));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount() - 1; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private void loadDuLieu() {
        danhSachGiaVe = giaVeDAO.getDanhSachGiaVe();
        tableModel.setRowCount(0);
        for (BangGiaVe giaVe : danhSachGiaVe) {
            tableModel.addRow(new Object[]{
                giaVe.getMaBG(),
                formatLoaiGhe(giaVe.getLoaiGhe()),
                formatDinhDang(giaVe.getDinhDang()),
                String.format("%,.0f", giaVe.getDonGia()),
                ""
            });
        }
    }

    private String formatLoaiGhe(String loaiGhe) {
        if (loaiGhe == null) return "";
        return loaiGhe.equalsIgnoreCase("thuong") ? "Thường" : 
               loaiGhe.equalsIgnoreCase("doi") ? "Đôi" : loaiGhe;
    }

    private String formatDinhDang(String dinhDang) {
        if (dinhDang == null) return "";
        return dinhDang.toUpperCase();
    }

    private void openSuaGiaVeDialog(BangGiaVe giaVe) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Cập Nhật Giá Vé", true);
        dialog.setSize(400, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(30, 30, 10, 30));
        mainPanel.setBackground(Color.WHITE);

        Font labelFont = new Font("Inter", Font.BOLD, 13);
        Color labelColor = new Color(100, 116, 139);

        // UI Components cho Dialog
        mainPanel.add(createFieldInfo("MÃ BẢNG GIÁ", String.valueOf(giaVe.getMaBG()), labelFont, labelColor));
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(createFieldInfo("LOẠI GHẾ", formatLoaiGhe(giaVe.getLoaiGhe()), labelFont, labelColor));
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(createFieldInfo("ĐỊNH DẠNG", formatDinhDang(giaVe.getDinhDang()), labelFont, labelColor));
        mainPanel.add(Box.createVerticalStrut(20));

        JLabel lblDonGia = new JLabel("ĐƠN GIÁ MỚI (VNĐ)");
        lblDonGia.setFont(labelFont);
        lblDonGia.setForeground(labelColor);
        mainPanel.add(lblDonGia);
        mainPanel.add(Box.createVerticalStrut(8));

        JTextField txtDonGia = new JTextField(String.format("%.0f", giaVe.getDonGia()));
        txtDonGia.setFont(new Font("Inter", Font.BOLD, 16));
        txtDonGia.setPreferredSize(new Dimension(0, 40));
        txtDonGia.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtDonGia.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        mainPanel.add(txtDonGia);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 20));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnHuy = new JButton("Hủy");
        styleButton(btnHuy, new Color(241, 245, 249), new Color(71, 85, 105));
        
        JButton btnLuu = new JButton("Cập nhật");
        styleButton(btnLuu, new Color(37, 99, 235), Color.WHITE);

        btnLuu.addActionListener(e -> {
            try {
                double giaMoi = Double.parseDouble(txtDonGia.getText().trim());
                if (giaMoi < 0) throw new NumberFormatException();
                
                giaVe.setDonGia(giaMoi);
                if (giaVeDAO.updateGiaVe(giaVe)) {
                    JOptionPane.showMessageDialog(dialog, "Cập nhật thành công!");
                    loadDuLieu();
                    dialog.dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập đơn giá hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnHuy.addActionListener(e -> dialog.dispose());

        buttonPanel.add(btnHuy);
        buttonPanel.add(btnLuu);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JPanel createFieldInfo(String label, String value, Font font, Color color) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        JLabel lbl = new JLabel(label + ": ");
        lbl.setFont(font);
        lbl.setForeground(color);
        JLabel val = new JLabel(value);
        val.setFont(new Font("Inter", Font.BOLD, 14));
        p.add(lbl, BorderLayout.WEST);
        p.add(val, BorderLayout.CENTER);
        return p;
    }

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Inter", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(100, 38));
    }

    // --- RENDERER: Hiển thị cây bút ---
    class ActionRenderer extends JPanel implements TableCellRenderer {
        private final JButton btnEdit;

        public ActionRenderer() {
            setLayout(new GridBagLayout()); // Căn giữa tuyệt đối
            setOpaque(true);
            btnEdit = new JButton("✏️");
            btnEdit.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
            btnEdit.setContentAreaFilled(false);
            btnEdit.setBorderPainted(false);
            btnEdit.setFocusPainted(false);
            btnEdit.setForeground(new Color(37, 99, 235));
            add(btnEdit);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return this;
        }
    }

    // --- EDITOR: Xử lý khi nhấn vào cây bút ---
    class ActionEditor extends DefaultCellEditor {
        private final JPanel panel;
        private final JButton btnEdit;
        private int currentRow;

        public ActionEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new JPanel(new GridBagLayout());
            panel.setOpaque(true);

            btnEdit = new JButton("✏️");
            btnEdit.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
            btnEdit.setContentAreaFilled(false);
            btnEdit.setBorderPainted(false);
            btnEdit.setFocusPainted(false);
            btnEdit.setForeground(new Color(37, 99, 235));
            btnEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btnEdit.addActionListener(e -> {
                fireEditingStopped();
                if (currentRow < danhSachGiaVe.size()) {
                    openSuaGiaVeDialog(danhSachGiaVe.get(currentRow));
                }
            });
            panel.add(btnEdit);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.currentRow = row;
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }
}