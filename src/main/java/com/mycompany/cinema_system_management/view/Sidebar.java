package com.mycompany.cinema_system_management.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Sidebar extends JPanel {

    private JButton btnDashboard, btnSuatChieu, btnKhuyenMai, btnKhoHang, btnPhanQuyen, btnGiaVe;
    // Danh sách để quản lý tất cả các nút menu
    private List<JButton> menuButtons = new ArrayList<>();

    public Sidebar() {
        setPreferredSize(new Dimension(260, 0));
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(226, 232, 240)));

        JPanel pnlHeader = new JPanel(new GridLayout(2, 1));
        pnlHeader.setOpaque(false);
        pnlHeader.setBorder(new EmptyBorder(30, 25, 30, 25));

        JLabel lblLogo = new JLabel("Hệ thống Cinema");
        lblLogo.setFont(new Font("Inter", Font.BOLD, 20));
        lblLogo.setForeground(new Color(37, 99, 235));

        pnlHeader.add(lblLogo);
        add(pnlHeader, BorderLayout.NORTH);

        JPanel pnlMenu = new JPanel();
        pnlMenu.setLayout(new BoxLayout(pnlMenu, BoxLayout.Y_AXIS));
        pnlMenu.setOpaque(false);
        pnlMenu.setBorder(new EmptyBorder(0, 15, 0, 15));

        // Khởi tạo các nút
        btnDashboard = createMenuItem("Bảng điều khiển", "layout-dashboard", true);
        btnSuatChieu = createMenuItem("Quản lý Suất chiếu", "calendar", false);
        btnKhuyenMai = createMenuItem("Quản lý Khuyến mãi", "tag", false);
        btnKhoHang = createMenuItem("Quản lý Kho hàng", "package", false);
        btnPhanQuyen = createMenuItem("Quản lý Phân quyền", "shield-check", false);
        btnGiaVe = createMenuItem("Điều chỉnh giá vé", "wallet-2", false);

        // Thêm vào danh sách quản lý
        menuButtons.add(btnDashboard);
        menuButtons.add(btnSuatChieu);
        menuButtons.add(btnKhuyenMai);
        menuButtons.add(btnKhoHang);
        menuButtons.add(btnPhanQuyen);
        menuButtons.add(btnGiaVe);

        // Thêm sự kiện click để đổi màu cho từng nút
        for (JButton btn : menuButtons) {
            btn.addActionListener(e -> setActiveButton(btn));
        }

        // Thêm vào UI
        for (JButton btn : menuButtons) {
            pnlMenu.add(btn);
            pnlMenu.add(Box.createVerticalStrut(5));
        }

        add(pnlMenu, BorderLayout.CENTER);
    }

    // --- HÀM QUAN TRỌNG: Cập nhật màu sắc khi Click ---
    private void setActiveButton(JButton activeBtn) {
        for (JButton btn : menuButtons) {
            boolean isActive = (btn == activeBtn);
            
            // 1. Cập nhật màu chữ
            Color textColor = isActive ? new Color(37, 99, 235) : new Color(100, 116, 139);
            btn.setForeground(textColor);
            btn.setFont(new Font("Inter", isActive ? Font.BOLD : Font.PLAIN, 14));

            // 2. Cập nhật màu Icon
            FlatSVGIcon icon = (FlatSVGIcon) btn.getIcon();
            icon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> textColor));

            // 3. Cập nhật màu nền (Style FlatLaf)
            String style = "arc: 10; borderWidth: 0; focusWidth: 0; " +
                           (isActive ? "background: #EFF6FF" : "background: #FFFFFF");
            btn.putClientProperty(FlatClientProperties.STYLE, style);
            
            btn.repaint();
        }
    }

    // Các hàm Getter
    public JButton getBtnDashboard() { return btnDashboard; }
    public JButton getBtnSuatChieu() { return btnSuatChieu; }
    public JButton getBtnKhuyenMai() { return btnKhuyenMai; }
    public JButton getBtnKhoHang() { return btnKhoHang; }
    public JButton getBtnPhanQuyen() { return btnPhanQuyen; }
    public JButton getBtnGiaVe() { return btnGiaVe; }

    private JButton createMenuItem(String text, String iconName, boolean active) {
        FlatSVGIcon icon = new FlatSVGIcon("icons/" + iconName + ".svg", 18, 18);
        Color textColor = active ? new Color(37, 99, 235) : new Color(100, 116, 139);
        icon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> textColor));

        JButton btn = new JButton(text);
        btn.setIcon(icon);
        btn.setIconTextGap(12);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btn.setBorder(new EmptyBorder(0, 15, 0, 15));

        String style = "arc: 10; borderWidth: 0; focusWidth: 0; " +
                       (active ? "background: #EFF6FF" : "background: #FFFFFF");
        btn.putClientProperty(FlatClientProperties.STYLE, style);
        btn.setForeground(textColor);
        btn.setFont(new Font("Inter", active ? Font.BOLD : Font.PLAIN, 14));

        return btn;
    }
}