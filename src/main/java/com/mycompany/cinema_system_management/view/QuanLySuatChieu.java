package com.mycompany.cinema_system_management.view;

import javax.swing.*;
import java.awt.*;

public class QuanLySuatChieu extends JPanel {
    public QuanLySuatChieu() {
        setBackground(new Color(236, 252, 241)); // Màu xanh lá nhạt
        setLayout(new GridBagLayout());
        JLabel label = new JLabel("TRANG QUẢN LÝ SUẤT CHIẾU");
        label.setFont(new Font("Inter", Font.BOLD, 24));
        add(label);
    }
}