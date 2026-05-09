package com.mycompany.cinema_system_management.view;

import javax.swing.*;
import java.awt.*;

public class QuanLyKhoHang extends JPanel {
    public QuanLyKhoHang() {
        setBackground(new Color(254, 249, 195)); // Màu vàng nhạt
        setLayout(new GridBagLayout());
        JLabel label = new JLabel("TRANG QUẢN LÝ KHO HÀNG (GREENFOOD)");
        label.setFont(new Font("Inter", Font.BOLD, 24));
        add(label);
    }
}