/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.cinema_system_management.view;

/**
 *
 * @author Dell
 */
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import com.mycompany.cinema_system_management.dao.TaiKhoanDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DangNhapFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnDangNhap;

    public DangNhapFrame() {
        // 1. Khởi tạo FlatLaf
        FlatLightLaf.setup();
        
        setTitle("Cinema Enterprise - Đăng nhập");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500); // Form đăng nhập ngắn hơn form đăng ký
        setLocationRelativeTo(null);
        
        // Panel chính
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        setContentPane(mainPanel);

        // --- GIAO DIỆN ---
        
        // Tiêu đề
        JLabel lblHeader = new JLabel("Đăng nhập");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(lblHeader);
        
        JLabel lblSub = new JLabel("Chào mừng bạn trở lại với hệ thống quản lý vé phim.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(Color.GRAY);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(lblSub);
        mainPanel.add(Box.createVerticalStrut(30));

        // Ô nhập Tên đăng nhập (Trong DB mình xài Username nên tui để label là Tên đăng nhập cho chuẩn)
        mainPanel.add(createLabel("Tên đăng nhập"));
        txtUsername = new JTextField();
        txtUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập username...");
        txtUsername.putClientProperty(FlatClientProperties.STYLE, "arc: 10; padding: 5,10,5,10");
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        mainPanel.add(txtUsername);
        mainPanel.add(Box.createVerticalStrut(15));

        // Ô nhập Mật khẩu
        JPanel passHeaderPanel = new JPanel(new BorderLayout());
        passHeaderPanel.setBackground(Color.WHITE);
        passHeaderPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        passHeaderPanel.add(createLabel("Mật khẩu"), BorderLayout.WEST);
        
        JLabel lblForgot = new JLabel("<html><font color='#023E8A'>Quên mật khẩu?</font></html>");
        lblForgot.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblForgot.setCursor(new Cursor(Cursor.HAND_CURSOR));
        passHeaderPanel.add(lblForgot, BorderLayout.EAST);
        mainPanel.add(passHeaderPanel);

        txtPassword = new JPasswordField();
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "********");
        txtPassword.putClientProperty(FlatClientProperties.STYLE, "arc: 10; padding: 5,10,5,10");
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        mainPanel.add(txtPassword);
        mainPanel.add(Box.createVerticalStrut(10));

        // Checkbox Ghi nhớ
        JCheckBox chkRemember = new JCheckBox("Ghi nhớ đăng nhập");
        chkRemember.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkRemember.setBackground(Color.WHITE);
        mainPanel.add(chkRemember);
        mainPanel.add(Box.createVerticalStrut(25));

        // Nút Đăng nhập
        btnDangNhap = new JButton("Đăng nhập →");
        btnDangNhap.setBackground(new Color(2, 62, 138)); // Xanh Navy
        btnDangNhap.setForeground(Color.WHITE);
        btnDangNhap.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDangNhap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnDangNhap.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDangNhap.putClientProperty(FlatClientProperties.STYLE, "arc: 15");
        btnDangNhap.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(btnDangNhap);

        // Link chuyển sang Đăng ký
        mainPanel.add(Box.createVerticalStrut(20));
        JLabel lblRegisterLink = new JLabel("<html>Chưa có tài khoản? <font color='#023E8A'><b>Đăng ký ngay</b></font></html>");
        lblRegisterLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblRegisterLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(lblRegisterLink);

        // --- XỬ LÝ SỰ KIỆN ---

        // 1. Click nút Đăng Nhập -> Gọi Oracle
        btnDangNhap.addActionListener(e -> xuLyDangNhap());

        // 2. Click chữ "Đăng ký ngay" -> Mở form Đăng ký
        lblRegisterLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose(); // Đóng form Đăng Nhập
                new DangKyFrame().setVisible(true); // Mở form Đăng Ký
            }
        });
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setBorder(new EmptyBorder(5, 0, 5, 0));
        return label;
    }

    private void xuLyDangNhap() {
    String user = txtUsername.getText();
    String pass = new String(txtPassword.getPassword());

    if (user.isEmpty() || pass.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        return;
    }

    TaiKhoanDAO dao = new TaiKhoanDAO();
    int vaiTro = dao.kiemTraDangNhap(user, pass);

    if (vaiTro == 1) {
        dispose();
        new CustomerHome().setVisible(true);
//    } else if (vaiTro == 2 || vaiTro == 3)  {
//        dispose();
//        new AdminDashboard().setVisible(true);
    // }
    }
    else {
        JOptionPane.showMessageDialog(this, "Sai tên đăng nhập hoặc mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DangNhapFrame().setVisible(true));
    }
}
