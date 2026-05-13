package com.mycompany.cinema_system_management.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.mycompany.cinema_system_management.DAO.KhuyenMaiDAO;
import com.mycompany.cinema_system_management.models.KhuyenMai;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.List;

public class QuanLyKhuyenMai extends JPanel {

    private JTable tblKhuyenMai;
    private DefaultTableModel model;
    private JLabel lblTotal, lblActive, lblExpired, lblUpcoming;
    private final KhuyenMaiDAO khuyenMaiDAO = new KhuyenMaiDAO();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public QuanLyKhuyenMai() {
        initComponents();
        setupTableStyle();
        loadDataToTable();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 20));
        setBackground(new Color(242, 245, 250));
        setBorder(new EmptyBorder(25, 35, 25, 35));

        // --- 1. HEADER SECTION ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);
        JLabel lblTitle = new JLabel("Quản lý Khuyến mãi Staff");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        
        JButton btnThem = new JButton("+ Thêm mã khuyến mãi mới");
        btnThem.setBackground(new Color(37, 99, 235));
        btnThem.setForeground(Color.WHITE);
        btnThem.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThem.setPreferredSize(new Dimension(240, 45));
        btnThem.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderWidth: 0; focusWidth: 0;");
        btnThem.addActionListener(e -> showAddDialog());

        pnlHeader.add(lblTitle, BorderLayout.WEST);
        pnlHeader.add(btnThem, BorderLayout.EAST);

        // --- 2. STATS CARDS ---
        JPanel pnlStats = new JPanel(new GridLayout(1, 4, 15, 0));
        pnlStats.setOpaque(false);
        lblTotal = new JLabel("0"); lblActive = new JLabel("0"); lblExpired = new JLabel("0"); lblUpcoming = new JLabel("0");
        
        pnlStats.add(createCard("TỔNG MÃ", lblTotal, "📊", new Color(37, 99, 235)));
        pnlStats.add(createCard("ĐANG CHẠY", lblActive, "⚡", new Color(16, 185, 129)));
        pnlStats.add(createCard("HẾT HẠN", lblExpired, "🚫", new Color(239, 68, 68)));
        pnlStats.add(createCard("SẮP TỚI", lblUpcoming, "📅", new Color(245, 158, 11)));

        // --- 3. TABLE SECTION ---
        JPanel pnlTableCard = new JPanel(new BorderLayout());
        pnlTableCard.setBackground(Color.WHITE);
        pnlTableCard.putClientProperty(FlatClientProperties.STYLE, "arc: 20");
        pnlTableCard.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ĐÃ ĐỔI: "MÔ TẢ" -> "TÊN CHƯƠNG TRÌNH"
        String[] columns = {"MÃ KM", "TÊN CHƯƠNG TRÌNH", "MỨC GIẢM", "SỐ LƯỢNG", "TRẠNG THÁI", "THAO TÁC"};
        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tblKhuyenMai = new JTable(model);
        tblKhuyenMai.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblKhuyenMai.rowAtPoint(e.getPoint());
                int col = tblKhuyenMai.columnAtPoint(e.getPoint());
                if (col == 5 && row != -1) {
                    Rectangle rect = tblKhuyenMai.getCellRect(row, col, false);
                    int xInCell = e.getX() - rect.x;
                    int maKM = Integer.parseInt(model.getValueAt(row, 0).toString().replace("KM", ""));
                    if (xInCell < rect.width / 2) handleEditAction(maKM);
                    else handleDeleteAction(maKM, row);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tblKhuyenMai);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        pnlTableCard.add(scroll, BorderLayout.CENTER);

        JPanel pnlCenter = new JPanel(new BorderLayout(0, 20));
        pnlCenter.setOpaque(false);
        pnlCenter.add(pnlStats, BorderLayout.NORTH);
        pnlCenter.add(pnlTableCard, BorderLayout.CENTER);

        add(pnlHeader, BorderLayout.NORTH);
        add(pnlCenter, BorderLayout.CENTER);
    }

    private void setupTableStyle() {
        tblKhuyenMai.setRowHeight(70);
        tblKhuyenMai.setShowVerticalLines(false);
        tblKhuyenMai.setGridColor(new Color(241, 245, 249));
        tblKhuyenMai.getTableHeader().setPreferredSize(new Dimension(0, 50));
        tblKhuyenMai.getTableHeader().setBackground(Color.WHITE);
        tblKhuyenMai.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        TableColumnModel colModel = tblKhuyenMai.getColumnModel();
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        colModel.getColumn(0).setCellRenderer(centerRenderer);
        colModel.getColumn(0).setMaxWidth(80);
        
        // ĐÃ SỬA: CĂN LỀ DÀI RA (400px) CHO TÊN CHƯƠNG TRÌNH
        colModel.getColumn(1).setPreferredWidth(400); 
        colModel.getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                l.setFont(new Font("Segoe UI", Font.BOLD, 14));
                l.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0)); // Thụt lề 30px cho thoáng
                return l;
            }
        });

        colModel.getColumn(2).setCellRenderer(centerRenderer);
        colModel.getColumn(3).setCellRenderer(centerRenderer);
        colModel.getColumn(4).setCellRenderer(new StatusBadgeRenderer());
        colModel.getColumn(5).setPreferredWidth(130);
        colModel.getColumn(5).setCellRenderer(new ActionRenderer());
    }

    public void loadDataToTable() {
        model.setRowCount(0);
        List<KhuyenMai> list = khuyenMaiDAO.getAllKhuyenMai();
        if (list != null) {
            lblTotal.setText(String.valueOf(list.size()));
            lblActive.setText(String.valueOf(list.stream().filter(k -> "Đang chạy".equals(k.getTrangThai())).count()));
            for (KhuyenMai km : list) {
                model.addRow(new Object[]{
                    "KM" + km.getMaKM(), km.getTenKM(),
                    km.getGiaTriGiam() > 100 ? String.format("%,.0fđ", km.getGiaTriGiam()) : km.getGiaTriGiam() + "%",
                    km.getSoLuongConLai(), km.getTrangThai(), ""
                });
            }
        }
    }

    private void showAddDialog() {
        JDialog d = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Cấu hình Khuyến mãi", true);
        d.setLayout(new BorderLayout()); d.setSize(520, 650); d.setLocationRelativeTo(this);
        JPanel p = new JPanel(new BorderLayout(0, 15)); p.setBackground(Color.WHITE); p.setBorder(new EmptyBorder(25, 30, 25, 30));
        
        JLabel lblT = new JLabel("Tạo mã khuyến mãi"); lblT.setFont(new Font("Segoe UI", Font.BOLD, 24));
        JPanel pnlForm = new JPanel(new GridBagLayout()); pnlForm.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints(); gbc.fill = 2; gbc.insets = new Insets(8, 0, 8, 0); gbc.weightx = 1.0;

        JTextField txtTen = createStyledField("Nhập tên...");
        JTextField txtMa = createStyledField("SUMMER24");
        JTextField txtGiam = createStyledField("Ví dụ: 10");
        JTextField txtSL = createStyledField("Ví dụ: 100");
        JTextField txtBD = createStyledField("DD/MM/YYYY");
        JTextField txtKT = createStyledField("DD/MM/YYYY");

        gbc.gridy = 0; pnlForm.add(createInputGroup(" Tên chương trình", txtTen), gbc);
        JPanel r2 = new JPanel(new GridLayout(1, 2, 15, 0)); r2.setOpaque(false);
        r2.add(createInputGroup(" Mã KM", txtMa)); r2.add(createInputGroup(" Số lượng", txtSL));
        gbc.gridy = 1; pnlForm.add(r2, gbc);
        gbc.gridy = 2; pnlForm.add(createInputGroup(" Mức giảm giá (%)", txtGiam), gbc);
        JPanel r3 = new JPanel(new GridLayout(1, 2, 15, 0)); r3.setOpaque(false);
        r3.add(createInputGroup(" Ngày bắt đầu", txtBD)); r3.add(createInputGroup(" Ngày kết thúc", txtKT));
        gbc.gridy = 3; pnlForm.add(r3, gbc);

        JButton btn = new JButton("+ Thêm mã khuyến mãi"); btn.setBackground(new Color(37, 99, 235)); btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(0, 50)); btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.addActionListener(e -> {
            try {
                KhuyenMai km = new KhuyenMai();
                km.setTenKM(txtTen.getText());
                km.setGiaTriGiam(Double.parseDouble(txtGiam.getText()));
                km.setSoLuongConLai(Integer.parseInt(txtSL.getText()));
                km.setNgayBD(new java.sql.Date(dateFormat.parse(txtBD.getText()).getTime()));
                km.setNgayKT(new java.sql.Date(dateFormat.parse(txtKT.getText()).getTime()));
                if (khuyenMaiDAO.insertKhuyenMai(km)) { loadDataToTable(); d.dispose(); }
            } catch (Exception ex) { JOptionPane.showMessageDialog(d, "Lỗi định dạng!"); }
        });

        p.add(lblT, BorderLayout.NORTH); p.add(pnlForm, BorderLayout.CENTER); p.add(btn, BorderLayout.SOUTH);
        d.add(p); d.setVisible(true);
    }

    private void handleEditAction(int maKM) {
        KhuyenMai km = khuyenMaiDAO.getAllKhuyenMai().stream().filter(k -> k.getMaKM() == maKM).findFirst().orElse(null);
        if (km == null) return;
        JDialog d = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Chỉnh sửa", true);
        d.setLayout(new BorderLayout()); d.setSize(520, 680); d.setLocationRelativeTo(this);
        JPanel p = new JPanel(new BorderLayout(0, 20)); p.setBackground(Color.WHITE); p.setBorder(new EmptyBorder(30, 30, 25, 30));
        
        // ĐÃ THÊM: TIÊU ĐỀ LẤP ĐẦY KHOẢNG TRỐNG
        JPanel pnlHeaderEdit = new JPanel(new GridLayout(2, 1, 0, 5));
        pnlHeaderEdit.setOpaque(false);
        JLabel lblT = new JLabel("Chỉnh sửa mã khuyến mãi");
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 24));
        JLabel lblS = new JLabel("Cập nhật lại thông tin chi tiết và thời hạn cho mã KM" + maKM);
        lblS.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblS.setForeground(Color.GRAY);
        pnlHeaderEdit.add(lblT); pnlHeaderEdit.add(lblS);
        
        JTextField txtTen = createStyledField(""); txtTen.setText(km.getTenKM());
        JTextField txtGiam = createStyledField(""); txtGiam.setText(String.valueOf(km.getGiaTriGiam()));
        JTextField txtSL = createStyledField(""); txtSL.setText(String.valueOf(km.getSoLuongConLai()));
        JTextField txtBD = createStyledField(""); txtBD.setText(dateFormat.format(km.getNgayBD()));
        JTextField txtKT = createStyledField(""); txtKT.setText(dateFormat.format(km.getNgayKT()));

        JPanel pnlForm = new JPanel(new GridBagLayout()); pnlForm.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints(); gbc.fill = 2; gbc.weightx = 1; gbc.gridy = 0;
        pnlForm.add(createInputGroup(" Tên chương trình", txtTen), gbc);
        JPanel r2 = new JPanel(new GridLayout(1, 2, 15, 0)); r2.setOpaque(false);
        r2.add(createInputGroup(" Mức giảm", txtGiam)); r2.add(createInputGroup(" Số lượng", txtSL));
        gbc.gridy = 1; pnlForm.add(r2, gbc);
        JPanel r3 = new JPanel(new GridLayout(1, 2, 15, 0)); r3.setOpaque(false);
        r3.add(createInputGroup(" Ngày bắt đầu", txtBD)); r3.add(createInputGroup(" Ngày kết thúc", txtKT));
        gbc.gridy = 2; pnlForm.add(r3, gbc);

        JButton btn = new JButton("Lưu thay đổi"); 
        btn.setBackground(new Color(30, 58, 138)); btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setPreferredSize(new Dimension(0, 50)); 
        btn.addActionListener(e -> {
            try {
                km.setTenKM(txtTen.getText()); km.setGiaTriGiam(Double.parseDouble(txtGiam.getText()));
                km.setSoLuongConLai(Integer.parseInt(txtSL.getText()));
                km.setNgayBD(new java.sql.Date(dateFormat.parse(txtBD.getText()).getTime()));
                km.setNgayKT(new java.sql.Date(dateFormat.parse(txtKT.getText()).getTime()));
                if (khuyenMaiDAO.updateKhuyenMai(km)) { loadDataToTable(); d.dispose(); }
            } catch (Exception ex) { JOptionPane.showMessageDialog(d, "Lỗi format!"); }
        });
        
        p.add(pnlHeaderEdit, BorderLayout.NORTH);
        p.add(pnlForm, BorderLayout.CENTER); 
        p.add(btn, BorderLayout.SOUTH);
        d.add(p); d.setVisible(true);
    }

    private void handleDeleteAction(int maKM, int row) {
    // 1. Khởi tạo Dialog không viền
    JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "", true);
    dialog.setUndecorated(true);
    dialog.setSize(480, 350);
    dialog.setLocationRelativeTo(this);
    dialog.setBackground(new Color(0, 0, 0, 0)); // Trong suốt để thấy bo góc

    // 2. Panel viền ngoài (Bo góc 40px)
    JPanel pnlBorder = new JPanel(new BorderLayout());
    pnlBorder.setBackground(Color.WHITE);
    pnlBorder.putClientProperty(FlatClientProperties.STYLE, "arc: 40; background: #ffffff;");
    
    // Panel nội dung bên trong
    JPanel pnlContent = new JPanel(new BorderLayout(0, 20));
    pnlContent.setOpaque(false);
    pnlContent.setBorder(new EmptyBorder(35, 40, 30, 40));

    // --- A. ICON CẢNH BÁO (Hình tam giác đỏ nhạt y hệt ảnh) ---
    JPanel pnlIcon = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int size = 75;
            int x = (getWidth() - size) / 2;
            // Vẽ vòng tròn nền đỏ cực nhạt
            g2.setColor(new Color(254, 226, 226));
            g2.fillOval(x, 0, size, size);
            
            // Vẽ dấu chấm than trong tam giác (Unicode hoặc thủ công)
            g2.setColor(new Color(220, 38, 38));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 45));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString("!", (getWidth() - fm.stringWidth("!")) / 2, 52);
        }
    };
    pnlIcon.setPreferredSize(new Dimension(0, 80));
    pnlIcon.setOpaque(false);

    // --- B. VĂN BẢN XÁC NHẬN ---
    String tenKM = model.getValueAt(row, 1).toString();
    
    JLabel lblTitle = new JLabel("Xác nhận xóa khuyến mãi", SwingConstants.CENTER);
    lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
    lblTitle.setForeground(new Color(15, 23, 42));

    JLabel lblMsg = new JLabel("<html><div style='text-align: center; width: 320px;'>"
            + "Xác nhận xóa khuyến mãi? Hành động này không thể hoàn tác. "
            + "Mọi thông tin liên quan đến mã <b>" + tenKM + "</b> sẽ bị gỡ bỏ khỏi hệ thống."
            + "</div></html>", SwingConstants.CENTER);
    lblMsg.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    lblMsg.setForeground(new Color(100, 116, 139));

    JPanel pnlText = new JPanel(new GridLayout(2, 1, 0, 10));
    pnlText.setOpaque(false);
    pnlText.add(lblTitle);
    pnlText.add(lblMsg);

    // --- C. HÀNG NÚT BẤM (Đồng bộ Style) ---
    JPanel pnlButtons = new JPanel(new GridLayout(1, 2, 20, 0));
    pnlButtons.setOpaque(false);

    JButton btnCancel = new JButton("Hủy bỏ");
    btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 14));
    btnCancel.putClientProperty(FlatClientProperties.STYLE, "arc: 12; background: #ffffff; borderWidth: 1; borderColor: #cbd5e1; focusWidth: 0;");
    btnCancel.addActionListener(e -> dialog.dispose());

    JButton btnDelete = new JButton("Xác nhận xóa");
    btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 14));
    btnDelete.setBackground(new Color(220, 38, 38));
    btnDelete.setForeground(Color.WHITE);
    btnDelete.putClientProperty(FlatClientProperties.STYLE, "arc: 12; borderWidth: 0; focusWidth: 0;");
    
    btnDelete.addActionListener(e -> {
        if (khuyenMaiDAO.deleteKhuyenMai(maKM)) {
            loadDataToTable();
            dialog.dispose();
        }
    });

    pnlButtons.add(btnCancel);
    pnlButtons.add(btnDelete);

    // Lắp ráp các thành phần
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

    private JPanel createInputGroup(String labelText, JTextField field) {
        JPanel p = new JPanel(new BorderLayout(0, 8)); p.setOpaque(false);
        JLabel l = new JLabel(labelText); l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JPanel wrapper = new JPanel(new BorderLayout()); wrapper.setBackground(new Color(248, 250, 252));
        wrapper.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1), new EmptyBorder(2, 5, 2, 5)));
        field.setBackground(new Color(248, 250, 252)); field.setBorder(null);
        wrapper.add(field); p.add(l, BorderLayout.NORTH); p.add(wrapper, BorderLayout.CENTER);
        return p;
    }

    private JTextField createStyledField(String hint) {
        JTextField f = new JTextField(); f.setPreferredSize(new Dimension(0, 40));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        f.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, hint);
        f.putClientProperty(FlatClientProperties.STYLE, "arc: 10; focusWidth: 0;");
        return f;
    }

    class ActionRenderer extends JPanel implements TableCellRenderer {
        public ActionRenderer() {
            setOpaque(true); setLayout(new FlowLayout(FlowLayout.CENTER, 25, 22));
            JLabel edit = new JLabel("\u270E"); edit.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 22)); edit.setForeground(new Color(37, 99, 235));
            JLabel del = new JLabel("\uD83D\uDDD1"); del.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 22)); del.setForeground(new Color(239, 68, 68));
            add(edit); add(del);
        }
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean h, int r, int c) {
            setBackground(isS ? t.getSelectionBackground() : Color.WHITE); return this;
        }
    }

    class StatusBadgeRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean h, int r, int c) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(t, v, isS, h, r, c);
            label.setHorizontalAlignment(0);
            label.setForeground("Đang chạy".equals(v) ? new Color(22, 163, 74) : Color.RED);
            label.setText("● " + v.toString().toUpperCase());
            return label;
        }
    }
}