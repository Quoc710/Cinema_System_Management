package com.mycompany.cinema_system_management.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Header extends JPanel {

    public Header() {
        // h-16 bg-white border-b border-slate-200
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 64));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        // --- BÊN TRÁI: Ô TÌM KIẾM ---
        JPanel pnlLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 12));
        pnlLeft.setOpaque(false);

        JTextField txtSearch = new JTextField(25);
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm kiếm hệ thống...");
        // Style bo góc và nền xám nhạt giống Tailwind
        txtSearch.putClientProperty(FlatClientProperties.STYLE, "arc: 12; background: #F8FAFC; borderWidth: 0");
        
        // Thêm icon kính lúp vào thanh Search
        txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, 
                new FlatSVGIcon("icons/search.svg", 16, 16));
        
        pnlLeft.add(txtSearch);
        add(pnlLeft, BorderLayout.WEST);

        // --- BÊN PHẢI: THÔNG BÁO & USER ---
        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        pnlRight.setOpaque(false);
        pnlRight.setBorder(new EmptyBorder(0, 0, 0, 20));

        // 1. Icon Chuông (Bell)
        JButton btnBell = new JButton(new FlatSVGIcon("icons/bell.svg", 20, 20));
        btnBell.setContentAreaFilled(false);
        btnBell.setBorderPainted(false);
        btnBell.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 2. Thông tin User (Admin User & Super Admin)
        JPanel pnlUserText = new JPanel(new GridLayout(2, 1));
        pnlUserText.setOpaque(false);
        
        JLabel lblName = new JLabel("Admin User");
        lblName.setFont(new Font("Inter", Font.BOLD, 13));
        lblName.setHorizontalAlignment(SwingConstants.RIGHT);
        
        JLabel lblRole = new JLabel("SUPER ADMIN");
        lblRole.setFont(new Font("Inter", Font.BOLD, 9));
        lblRole.setForeground(new Color(148, 163, 184));
        lblRole.setHorizontalAlignment(SwingConstants.RIGHT);

        pnlUserText.add(lblName);
        pnlUserText.add(lblRole);

        // 3. Avatar bo tròn (Sử dụng ảnh trong resources/images)
        JLabel lblAvatar = new JLabel();
        lblAvatar.setPreferredSize(new Dimension(40, 40));
        // Bạn có thể thay bằng ảnh thật trong folder images
        lblAvatar.setIcon(new FlatSVGIcon("icons/user.svg", 35, 35)); 

        pnlRight.add(btnBell);
        pnlRight.add(pnlUserText);
        pnlRight.add(lblAvatar);

        add(pnlRight, BorderLayout.EAST);
    }
}