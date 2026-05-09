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
        FlatLightLaf.setup();
        
        setTitle("Cinema Enterprise - Đăng ký");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // --- MỞ RỘNG FULL MÀN HÌNH MẶC ĐỊNH ---
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Phóng to toàn màn hình
        setMinimumSize(new Dimension(1000, 700)); // Kích thước tối thiểu để không vỡ form
        setLocationRelativeTo(null);
        
        // Container chính chia 2 cột bằng nhau
        JPanel basePanel = new JPanel(new GridLayout(1, 2));
        basePanel.setBackground(Color.WHITE);

        // ==========================================
        // CỘT TRÁI: HÌNH ẢNH BANNER
        // ==========================================
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(241, 245, 249)); 

        JLabel lblImage = new JLabel();
        lblImage.setHorizontalAlignment(SwingConstants.CENTER);
        
        try {
            java.net.URL imgUrl = getClass().getResource("/images/manhinnregister.jpg");
            if (imgUrl != null) {
                // Tăng scale để ảnh nét hơn khi full màn hình
                ImageIcon icon = new ImageIcon(new ImageIcon(imgUrl).getImage().getScaledInstance(800, 1000, Image.SCALE_SMOOTH));
                lblImage.setIcon(icon);
            }
        } catch (Exception e) {}

        leftPanel.add(lblImage, BorderLayout.CENTER);

        // ==========================================
        // CỘT PHẢI: FORM ĐĂNG KÝ (ĐÃ CANH GIỮA VÀ KHÓA KÍCH THƯỚC)
        // ==========================================
        // Dùng GridBagLayout làm Wrapper để luôn giữ form ở chính giữa vùng trắng
        JPanel rightWrapper = new JPanel(new GridBagLayout());
        rightWrapper.setBackground(Color.WHITE);

        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        // Ép cứng độ rộng form là 400px để nó không bị phình to khi full màn hình
        rightPanel.setPreferredSize(new Dimension(400, 650)); 
        rightPanel.setMaximumSize(new Dimension(400, 650));

        // Brand Header
        JLabel lblBrand = new JLabel("CINEMA ENTERPRISE");
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblBrand.setAlignmentX(Component.LEFT_ALIGNMENT); // Ép canh trái toàn bộ
        lblBrand.setForeground(new Color(2, 62, 138));
        rightPanel.add(lblBrand);
        rightPanel.add(Box.createVerticalStrut(25));

        // Header Title
        JLabel lblHeader = new JLabel("Tạo tài khoản mới");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPanel.add(lblHeader);
        rightPanel.add(Box.createVerticalStrut(20));

        // Các ô nhập liệu
        rightPanel.add(createLabel("Họ và tên"));
        txtHoTen = createStyledTextField("Nguyễn Anh Quốc");
        rightPanel.add(txtHoTen);
        
        rightPanel.add(createLabel("Tên đăng nhập"));
        txtUsername = createStyledTextField("quocuit2026");
        rightPanel.add(txtUsername);

        rightPanel.add(createLabel("Email"));
        txtEmail = createStyledTextField("quoc@uit.edu.vn");
        rightPanel.add(txtEmail);

        rightPanel.add(createLabel("Số điện thoại"));
        txtSdt = createStyledTextField("090xxxxxxx");
        rightPanel.add(txtSdt);

        rightPanel.add(createLabel("Mật khẩu"));
        txtPassword = createStyledPasswordField();
        rightPanel.add(txtPassword);

        rightPanel.add(createLabel("Xác nhận mật khẩu"));
        txtXacNhan = createStyledPasswordField();
        rightPanel.add(txtXacNhan);
        
        rightPanel.add(Box.createVerticalStrut(25));

        // Nút Đăng ký
        btnDangKy = new JButton("Đăng ký tài khoản →");
        btnDangKy.setBackground(new Color(2, 62, 138));
        btnDangKy.setForeground(Color.WHITE);
        btnDangKy.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnDangKy.setMaximumSize(new Dimension(400, 45)); // Khóa chiều rộng theo form
        btnDangKy.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDangKy.putClientProperty(FlatClientProperties.STYLE, "arc: 15");
        btnDangKy.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPanel.add(btnDangKy);

        // Phần chuyển hướng Đăng nhập
        rightPanel.add(Box.createVerticalStrut(20));
        
        JLabel lblBackToLogin = new JLabel("<html>Đã có tài khoản? <font color='#023E8A'><b>Đăng nhập ngay</b></font></html>");
        lblBackToLogin.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblBackToLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblBackToLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPanel.add(lblBackToLogin);

        // Nhúng Form vào Wrapper để canh giữa
        rightWrapper.add(rightPanel);

        // ==========================================
        // TÍCH HỢP 2 CỘT VÀO CONTAINER CHÍNH
        // ==========================================
        basePanel.add(leftPanel);
        basePanel.add(rightWrapper);
        setContentPane(basePanel);

        // Xử lý sự kiện
        btnDangKy.addActionListener(e -> xuLyDangKy());

        lblBackToLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose(); 
                new DangNhapFrame().setVisible(true); 
            }
        });
    }

    // --- CÁC HÀM TRỢ GIÚP (Đã chỉnh ép khung canh trái) ---
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setBorder(new EmptyBorder(8, 0, 4, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT); // Canh trái đồng bộ
        return label;
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField();
        field.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        field.putClientProperty(FlatClientProperties.STYLE, "arc: 10; padding: 5,10,5,10");
        field.setMaximumSize(new Dimension(400, 40)); // Khóa cứng chiều rộng 400px
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "********");
        field.putClientProperty(FlatClientProperties.STYLE, "arc: 10; padding: 5,10,5,10");
        field.setMaximumSize(new Dimension(400, 40)); // Khóa cứng chiều rộng 400px
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
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