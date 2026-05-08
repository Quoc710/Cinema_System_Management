package com.mycompany.cinema_system_management.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import com.mycompany.cinema_system_management.dao.TaiKhoanDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DangKyFrame extends JFrame {

    private JTextField txtUsername, txtHoTen, txtEmail, txtSdt;
    private JPasswordField txtPassword, txtXacNhan;
    private JButton btnDangKy;

    public DangKyFrame() {
        // 1. Khởi tạo FlatLaf
        FlatLightLaf.setup();
        
        setTitle("Cinema Enterprise - Đăng ký");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 720);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        setContentPane(mainPanel);

        // --- GIAO DIỆN ---
        
        // Brand Header
        JLabel lblBrand = new JLabel("CINEMA ENTERPRISE");
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblBrand.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblBrand.setForeground(new Color(2, 62, 138));
        mainPanel.add(lblBrand);
        mainPanel.add(Box.createVerticalStrut(25));

        // Header Title
        JLabel lblHeader = new JLabel("Tạo tài khoản mới");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(lblHeader);
        mainPanel.add(Box.createVerticalStrut(15));

        // Các ô nhập liệu
        mainPanel.add(createLabel("Họ và tên"));
        txtHoTen = createStyledTextField("Nguyễn Anh Quốc");
        mainPanel.add(txtHoTen);
        
        mainPanel.add(createLabel("Tên đăng nhập"));
        txtUsername = createStyledTextField("quocuit2026");
        mainPanel.add(txtUsername);

        mainPanel.add(createLabel("Email"));
        txtEmail = createStyledTextField("quoc@uit.edu.vn");
        mainPanel.add(txtEmail);

        mainPanel.add(createLabel("Số điện thoại"));
        txtSdt = createStyledTextField("090xxxxxxx");
        mainPanel.add(txtSdt);

        mainPanel.add(createLabel("Mật khẩu"));
        txtPassword = createStyledPasswordField();
        mainPanel.add(txtPassword);

        mainPanel.add(createLabel("Xác nhận mật khẩu"));
        txtXacNhan = createStyledPasswordField();
        mainPanel.add(txtXacNhan);
        
        mainPanel.add(Box.createVerticalStrut(20));

        // Nút Đăng ký
        btnDangKy = new JButton("Đăng ký tài khoản →");
        btnDangKy.setBackground(new Color(2, 62, 138));
        btnDangKy.setForeground(Color.WHITE);
        btnDangKy.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDangKy.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnDangKy.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDangKy.putClientProperty(FlatClientProperties.STYLE, "arc: 15");
        btnDangKy.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(btnDangKy);

        // --- PHẦN QUAY LẠI ĐĂNG NHẬP (MỚI THÊM) ---
        mainPanel.add(Box.createVerticalStrut(15));
        
        JLabel lblBackToLogin = new JLabel("<html>Đã có tài khoản? <font color='#023E8A'><b>Đăng nhập ngay</b></font></html>");
        lblBackToLogin.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblBackToLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblBackToLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(lblBackToLogin);

        // --- XỬ LÝ SỰ KIỆN ---

        // 1. Click nút Đăng ký
        btnDangKy.addActionListener(e -> xuLyDangKy());

        // 2. Click dòng chữ Quay lại Đăng nhập
        lblBackToLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose(); // Đóng form hiện tại
                new DangNhapFrame().setVisible(true); // Mở lại form Đăng nhập
            }
        });
    }

    // Các hàm trợ giúp thiết kế (Giữ nguyên như cũ)
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setBorder(new EmptyBorder(8, 0, 4, 0));
        return label;
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField();
        field.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        field.putClientProperty(FlatClientProperties.STYLE, "arc: 10; padding: 5,10,5,10");
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "********");
        field.putClientProperty(FlatClientProperties.STYLE, "arc: 10; padding: 5,10,5,10");
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        return field;
    }

    private void xuLyDangKy() {
        String user = txtUsername.getText();
        String pass = new String(txtPassword.getPassword());
        String xacNhan = new String(txtXacNhan.getPassword());
        String hoTen = txtHoTen.getText();
        String email = txtEmail.getText();
        String sdt = txtSdt.getText();

        if (user.isEmpty() || pass.isEmpty() || hoTen.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Điền đủ thông tin quan trọng đã ní!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!pass.equals(xacNhan)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        TaiKhoanDAO dao = new TaiKhoanDAO();
        if (dao.dangKyKhachHang(user, pass, hoTen, email, sdt)) {
            JOptionPane.showMessageDialog(this, "Đăng ký thành công! Hãy đăng nhập để mua vé.");
            dispose();
            new DangNhapFrame().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi! Tên đăng nhập hoặc SĐT đã có người dùng.", "Thất bại", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DangKyFrame().setVisible(true));
    }
}