package com.mycompany.cinema_system_management.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Sidebar extends JPanel {

    // 1. Khai báo các nút bấm làm biến toàn cục để AdminDashboard có thể truy cập
    private JButton btnDashboard;
    private JButton btnSuatChieu;
    private JButton btnKhuyenMai;
    private JButton btnKhoHang;
    private JButton btnPhanQuyen;

    public Sidebar() {
        setPreferredSize(new Dimension(260, 0));
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(226, 232, 240)));

        // Header Sidebar
        JPanel pnlHeader = new JPanel(new GridLayout(2, 1));
        pnlHeader.setOpaque(false);
        pnlHeader.setBorder(new EmptyBorder(30, 25, 30, 25));

        JLabel lblLogo = new JLabel("Hệ thống Cinema");
        lblLogo.setFont(new Font("Inter", Font.BOLD, 20));
        lblLogo.setForeground(new Color(37, 99, 235));

        pnlHeader.add(lblLogo);
        add(pnlHeader, BorderLayout.NORTH);

        // Menu
        JPanel pnlMenu = new JPanel();
        pnlMenu.setLayout(new BoxLayout(pnlMenu, BoxLayout.Y_AXIS));
        pnlMenu.setOpaque(false);
        pnlMenu.setBorder(new EmptyBorder(0, 15, 0, 15));

        // 2. Khởi tạo và gán vào các biến đã khai báo
        btnDashboard = createMenuItem("Bảng điều khiển", "layout-dashboard", true);
        btnSuatChieu = createMenuItem("Quản lý Suất chiếu", "calendar", false);
        btnKhuyenMai = createMenuItem("Quản lý Khuyến mãi", "tag", false);
        btnKhoHang = createMenuItem("Quản lý Kho hàng", "package", false);
        btnPhanQuyen = createMenuItem("Quản lý Phân quyền", "shield-check", false);

        // 3. Thêm các nút vào Panel Menu
        pnlMenu.add(btnDashboard);
        pnlMenu.add(Box.createVerticalStrut(5));
        pnlMenu.add(btnSuatChieu);
        pnlMenu.add(Box.createVerticalStrut(5));
        pnlMenu.add(btnKhuyenMai);
        pnlMenu.add(Box.createVerticalStrut(5));
        pnlMenu.add(btnKhoHang);
        pnlMenu.add(Box.createVerticalStrut(5));
        pnlMenu.add(btnPhanQuyen);

        add(pnlMenu, BorderLayout.CENTER);
    }

    // 4. Các hàm Getter để file AdminDashboard gọi lấy nút
    public JButton getBtnDashboard() { return btnDashboard; }
    public JButton getBtnSuatChieu() { return btnSuatChieu; }
    public JButton getBtnKhuyenMai() { return btnKhuyenMai; }
    public JButton getBtnKhoHang() { return btnKhoHang; }
    public JButton getBtnPhanQuyen() { return btnPhanQuyen; }

    private JButton createMenuItem(String text, String iconName, boolean active) {
        // Khởi tạo Icon
        FlatSVGIcon icon = new FlatSVGIcon("icons/" + iconName + ".svg", 18, 18);
        Color textColor = active ? new Color(37, 99, 235) : new Color(100, 116, 139);
        
        // Ép icon màu theo chữ
        icon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> textColor));

        JButton btn = new JButton(text);
        btn.setIcon(icon);
        btn.setIconTextGap(12);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50)); 
        btn.setBorder(new javax.swing.border.EmptyBorder(0, 15, 0, 15));

        // Style FlatLaf
        String style = "arc: 10; borderWidth: 0; focusWidth: 0; " +
                       (active ? "background: #EFF6FF" : "background: #FFFFFF");
        
        btn.putClientProperty(FlatClientProperties.STYLE, style);
        btn.setForeground(textColor);
        btn.setFont(new Font("Inter", active ? Font.BOLD : Font.PLAIN, 14));
        
        return btn;
    }
}