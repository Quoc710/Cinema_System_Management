package com.mycompany.cinema_system_management.view;

import javax.swing.*;
import java.awt.*;

public class QuanLyPhanQuyen extends JPanel {
    public QuanLyPhanQuyen() {
        setBackground(new Color(238, 242, 255)); // Màu xanh dương nhạt
        setLayout(new GridBagLayout());
        JLabel label = new JLabel("TRANG PHÂN QUYỀN HỆ THỐNG");
        label.setFont(new Font("Inter", Font.BOLD, 24));
        add(label);
    }
}