package com.mycompany.cinema_system_management.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.mycompany.cinema_system_management.DAO.PhanQuyenDAO;
import com.mycompany.cinema_system_management.models.TaiKhoan;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class QuanLyPhanQuyen extends JPanel {
    private JTable tblTaiKhoan;
    private DefaultTableModel model;
    private JLabel lblTotal, lblAdmin, lblManager, lblWarehouse;
    private final PhanQuyenDAO phanQuyenDAO = new PhanQuyenDAO();

    public QuanLyPhanQuyen() {
        initComponents();
        setupTableStyle();
        loadDataToTable();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 20));
        setBackground(new Color(242, 245, 250));
        setBorder(new EmptyBorder(25, 35, 25, 35));

        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);
        JLabel lblTitle = new JLabel("Quản lý Phân quyền Staff");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        JButton btnThem = new JButton("+ Thêm nhân viên mới");
        btnThem.setBackground(new Color(37, 99, 235)); btnThem.setForeground(Color.WHITE);
        btnThem.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThem.setPreferredSize(new Dimension(200, 45));
        btnThem.addActionListener(e -> {
            ThemNhanVienDialog d = new ThemNhanVienDialog((Frame) SwingUtilities.getWindowAncestor(this));
            d.setVisible(true); if (d.isSuccess()) loadDataToTable();
        });
        pnlHeader.add(lblTitle, BorderLayout.WEST); pnlHeader.add(btnThem, BorderLayout.EAST);

        JPanel pnlStats = new JPanel(new GridLayout(1, 4, 15, 0));
        pnlStats.setOpaque(false);
        lblTotal = new JLabel("0"); lblAdmin = new JLabel("0"); lblManager = new JLabel("0"); lblWarehouse = new JLabel("0");
        pnlStats.add(createCard("TỔNG", lblTotal, "👥", new Color(37, 99, 235)));
        pnlStats.add(createCard("ADMIN", lblAdmin, "🛡️", new Color(239, 68, 68)));
        pnlStats.add(createCard("QUẢN LÝ", lblManager, "💼", new Color(16, 185, 129)));
        pnlStats.add(createCard("KHO", lblWarehouse, "📦", new Color(245, 158, 11)));

        JPanel pnlTableCard = new JPanel(new BorderLayout());
        pnlTableCard.setBackground(Color.WHITE); pnlTableCard.putClientProperty(FlatClientProperties.STYLE, "arc: 20");
        pnlTableCard.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        model = new DefaultTableModel(new String[]{"MÃ TK", "TÊN ĐĂNG NHẬP", "VAI TRÒ HIỆN TẠI", "THAO TÁC"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblTaiKhoan = new JTable(model);
        tblTaiKhoan.addMouseListener(new MouseAdapter() {
    @Override
    public void mouseClicked(MouseEvent e) {
        int row = tblTaiKhoan.getSelectedRow();
        int col = tblTaiKhoan.getSelectedColumn();

        // Chỉ xử lý nếu click vào cột số 3 (Thao tác)
        if (col == 3 && row != -1) {
            // Lấy các thông tin cần thiết từ model
            int maTK = (int) model.getValueAt(row, 0);
            String user = (String) model.getValueAt(row, 1);
            String role = (String) model.getValueAt(row, 2);

            // 1. Lấy vị trí click chuột tương đối trong cell Thao tác
            Rectangle rect = tblTaiKhoan.getCellRect(row, col, false);
            int xTrongCell = e.getX() - rect.x; // Tọa độ X tính từ mép trái của cột Thao tác

            // 2. Chia đôi cột: Bên trái là Sửa (Bút), Bên phải là Xóa (Thùng rác)
            if (xTrongCell < rect.width / 2) {
                // --- HÀNH ĐỘNG: SỬA ---
                int maVT = role.equals("Admin") ? 4 : (role.equals("Quản lý kho") ? 3 : 2);
                ChinhSuaNhanVienDialog d = new ChinhSuaNhanVienDialog(
                    (Frame) SwingUtilities.getWindowAncestor(QuanLyPhanQuyen.this), 
                    maTK, user, maVT
                );
                d.setVisible(true);
                if (d.isSuccess()) loadDataToTable();
                
            } else {
                // --- HÀNH ĐỘNG: XÓA ---
                handleDelete(maTK);
            }
        }
    }
});

        JScrollPane scroll = new JScrollPane(tblTaiKhoan);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        pnlTableCard.add(scroll, BorderLayout.CENTER);

        JPanel pnlCenter = new JPanel(new BorderLayout(0, 20));
        pnlCenter.setOpaque(false);
        pnlCenter.add(pnlStats, BorderLayout.NORTH); pnlCenter.add(pnlTableCard, BorderLayout.CENTER);
        add(pnlHeader, BorderLayout.NORTH); add(pnlCenter, BorderLayout.CENTER);
    }

    private void setupTableStyle() {
    tblTaiKhoan.setRowHeight(70); // Chiều cao hàng thoáng để icon không bị đè
    tblTaiKhoan.setShowVerticalLines(false);
    tblTaiKhoan.setGridColor(new Color(241, 245, 249));
    
    // Custom Header
    JTableHeader header = tblTaiKhoan.getTableHeader();
    header.setPreferredSize(new Dimension(0, 50));
    header.setFont(new Font("Segoe UI", Font.BOLD, 14));
    header.setBackground(Color.WHITE);

    TableColumnModel colModel = tblTaiKhoan.getColumnModel();
    
    // --- 1. CỘT MÃ TK: Nhỏ gọn, căn giữa ---
    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
    centerRenderer.setHorizontalAlignment(JLabel.CENTER);
    colModel.getColumn(0).setCellRenderer(centerRenderer);
    colModel.getColumn(0).setPreferredWidth(80);
    colModel.getColumn(0).setMaxWidth(100);

    // --- 2. CỘT TÊN ĐĂNG NHẬP: Trọng tâm, Font đậm, Thụt lề trái 30px ---
    colModel.getColumn(1).setPreferredWidth(300);
    colModel.getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
            label.setFont(new Font("Segoe UI", Font.BOLD, 15)); 
            label.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0)); // Thụt lề để không dính vách
            return label;
        }
    });

    // --- 3. CỘT VAI TRÒ: Font vừa, Căn trái thụt lề ---
    colModel.getColumn(2).setPreferredWidth(250);
    colModel.getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            label.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
            return label;
        }
    });

    // --- 4. CỘT THAO TÁC: Bóp nhỏ, Căn giữa Icon tuyệt đối ---
    colModel.getColumn(3).setCellRenderer(new ActionRenderer());
    colModel.getColumn(3).setPreferredWidth(120);
    colModel.getColumn(3).setMaxWidth(130);
}

    public void loadDataToTable() {
        model.setRowCount(0);
        List<TaiKhoan> list = phanQuyenDAO.getAllTaiKhoan();
        if (list != null) {
            lblTotal.setText(String.valueOf(list.size()));
            lblAdmin.setText(String.valueOf(list.stream().filter(tk -> tk.getMaVaiTro() == 4).count()));
            lblManager.setText(String.valueOf(list.stream().filter(tk -> tk.getMaVaiTro() == 2).count()));
            lblWarehouse.setText(String.valueOf(list.stream().filter(tk -> tk.getMaVaiTro() == 3).count()));
            for (TaiKhoan tk : list) model.addRow(new Object[]{tk.getMaTK(), tk.getUsername(), getRoleName(tk.getMaVaiTro()), ""});
        }
    }

    private String getRoleName(int m) { return switch (m) { case 2 -> "Nhân viên quản lý"; case 3 -> "Quản lý kho"; case 4 -> "Admin"; default -> "Staff"; }; }
    
    private void handleDelete(int maTK) {
    // 1. Khởi tạo Dialog không viền để tự vẽ bo góc
    JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "", true);
    dialog.setUndecorated(true);
    dialog.setSize(460, 320);
    dialog.setLocationRelativeTo(this);
    dialog.setBackground(new Color(0, 0, 0, 0)); // Trong suốt để thấy bo góc

    // 2. Panel bao ngoài tạo hiệu ứng bo góc và viền
    JPanel pnlBorder = new JPanel(new BorderLayout());
    pnlBorder.setBackground(Color.WHITE);
    pnlBorder.putClientProperty(FlatClientProperties.STYLE, "arc: 40; background: #ffffff;");
    
    // Panel nội dung bên trong
    JPanel pnlContent = new JPanel(new BorderLayout(0, 20));
    pnlContent.setOpaque(false);
    pnlContent.setBorder(new EmptyBorder(30, 35, 25, 35));

    // --- A. ICON CẢNH BÁO (Vòng tròn đỏ nhạt + Dấu X) ---
    JPanel pnlIcon = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Vẽ vòng tròn nền đỏ nhạt
            g2.setColor(new Color(254, 226, 226));
            g2.fillOval(getWidth()/2 - 35, 0, 70, 70);
            // Vẽ icon thùng rác hoặc dấu X đỏ đậm
            g2.setColor(new Color(220, 38, 38));
            g2.setFont(new Font("Segoe UI Symbol", Font.BOLD, 30));
            g2.drawString("\uD83D\uDDD1", getWidth()/2 - 16, 45); // Icon thùng rác Unicode
        }
    };
    pnlIcon.setPreferredSize(new Dimension(0, 80));
    pnlIcon.setOpaque(false);

    // --- B. VĂN BẢN (Tiêu đề & Cảnh báo) ---
    JLabel lblTitle = new JLabel("Xác nhận xóa nhân viên?", SwingConstants.CENTER);
    lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
    lblTitle.setForeground(new Color(15, 23, 42));

    JLabel lblMsg = new JLabel("<html><div style='text-align: center; width: 300px;'>"
            + "Hành động này không thể hoàn tác. Mọi thông tin và quyền hạn truy cập của nhân viên này sẽ bị gỡ bỏ hoàn toàn."
            + "</div></html>", SwingConstants.CENTER);
    lblMsg.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    lblMsg.setForeground(new Color(100, 116, 139));

    JPanel pnlText = new JPanel(new GridLayout(2, 1, 0, 10));
    pnlText.setOpaque(false);
    pnlText.add(lblTitle);
    pnlText.add(lblMsg);

    // --- C. HÀNG NÚT BẤM ---
    JPanel pnlButtons = new JPanel(new GridLayout(1, 2, 20, 0));
    pnlButtons.setOpaque(false);

    JButton btnCancel = new JButton("Hủy");
    btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 14));
    btnCancel.putClientProperty(FlatClientProperties.STYLE, "arc: 12; background: #ffffff; borderWidth: 1; borderColor: #cbd5e1; focusWidth: 0;");
    btnCancel.addActionListener(e -> dialog.dispose());

    JButton btnDelete = new JButton("Xác nhận xóa");
    btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 14));
    btnDelete.setBackground(new Color(220, 38, 38)); // Màu đỏ rực như ảnh
    btnDelete.setForeground(Color.WHITE);
    btnDelete.putClientProperty(FlatClientProperties.STYLE, "arc: 12; borderWidth: 0; focusWidth: 0;");
    
    btnDelete.addActionListener(e -> {
        if (phanQuyenDAO.deleteTaiKhoan(maTK)) {
            loadDataToTable();
            dialog.dispose();
            // Tùy chọn: JOptionPane.showMessageDialog(this, "Đã xóa!");
        }
    });

    pnlButtons.add(btnCancel);
    pnlButtons.add(btnDelete);

    // Lắp ráp
    pnlContent.add(pnlIcon, BorderLayout.NORTH);
    pnlContent.add(pnlText, BorderLayout.CENTER);
    pnlContent.add(pnlButtons, BorderLayout.SOUTH);

    pnlBorder.add(pnlContent);
    dialog.add(pnlBorder);
    dialog.setVisible(true);
}

    private JPanel createCard(String title, JLabel lblVal, String icon, Color accent) {
        JPanel card = new JPanel(new BorderLayout()); card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(15, 20, 15, 20)); card.putClientProperty(FlatClientProperties.STYLE, "arc: 15");
        JLabel lblT = new JLabel(title); lblT.setFont(new Font("Segoe UI", Font.BOLD, 11)); lblT.setForeground(Color.GRAY);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 24)); lblVal.setForeground(accent);
        JPanel pnlText = new JPanel(new GridLayout(2, 1)); pnlText.setOpaque(false); pnlText.add(lblT); pnlText.add(lblVal);
        JLabel lblI = new JLabel(icon); lblI.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 28));
        card.add(pnlText, BorderLayout.WEST); card.add(lblI, BorderLayout.EAST);
        return card;
    }

   class ActionRenderer extends JPanel implements TableCellRenderer {
    public ActionRenderer() {
        setOpaque(true);
        // FlowLayout.CENTER là mấu chốt để icon không bị lệch sang phải
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 22)); 
        
        JLabel lblEdit = new JLabel("\u270E"); 
        lblEdit.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 22));
        lblEdit.setForeground(new Color(37, 99, 235));
        
        JLabel lblDel = new JLabel("\uD83D\uDDD1"); 
        lblDel.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 22));
        lblDel.setForeground(new Color(239, 68, 68));
        
        add(lblEdit);
        add(lblDel);
    }

    @Override
    public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
        setBackground(isS ? t.getSelectionBackground() : Color.WHITE);
        return this;
    }
}
}