package com.mycompany.cinema_system_management.view;

import javax.swing.*;
import java.awt.*;

public class QuanLyKhuyenMai extends JPanel {
    public QuanLyKhuyenMai() {
        setBackground(new Color(255, 241, 242)); // Màu hồng nhạt
        setLayout(new GridBagLayout());
        JLabel label = new JLabel("TRANG QUẢN LÝ KHUYẾN MÃI");
        label.setFont(new Font("Inter", Font.BOLD, 24));
        add(label);
    }
}