package com.mycompany.cinema_system_management.view;

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
        FlatLightLaf.setup();
        setTitle("Cinema Enterprise - Đăng nhập");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1000, 700));
        setLocationRelativeTo(null);

        JPanel basePanel = new JPanel(new GridLayout(1, 2));
        basePanel.setBackground(Color.WHITE);

        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(new Color(241, 245, 249));

        JLabel lblImage = new JLabel();
        lblImage.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            java.net.URL imgUrl = getClass().getResource("/images/manhinnregister.jpg");
            if (imgUrl != null) {
                ImageIcon icon = new ImageIcon(new ImageIcon(imgUrl).getImage().getScaledInstance(800, 1000, Image.SCALE_SMOOTH));
                lblImage.setIcon(icon);
            }
        } catch (Exception e) {}
        imagePanel.add(lblImage, BorderLayout.CENTER);

        JPanel formWrapper = new JPanel(new GridBagLayout());
        formWrapper.setBackground(Color.WHITE);

        JPanel innerFormPanel = new JPanel();
        innerFormPanel.setBackground(Color.WHITE);
        innerFormPanel.setLayout(new BoxLayout(innerFormPanel, BoxLayout.Y_AXIS));
        innerFormPanel.setPreferredSize(new Dimension(400, 450));
        innerFormPanel.setMaximumSize(new Dimension(400, 450));

        JLabel lblHeader = new JLabel("Đăng nhập");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        innerFormPanel.add(lblHeader);
        innerFormPanel.add(Box.createVerticalStrut(10));

        JLabel lblSub = new JLabel("Chào mừng bạn trở lại với hệ thống quản lý vé phim.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblSub.setForeground(Color.GRAY);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        innerFormPanel.add(lblSub);
        innerFormPanel.add(Box.createVerticalStrut(40));

        innerFormPanel.add(createLabel("Tên đăng nhập"));
        txtUsername = createStyledTextField("Nhập username...");
        innerFormPanel.add(txtUsername);
        innerFormPanel.add(Box.createVerticalStrut(20));

        JPanel passHeaderPanel = new JPanel(new BorderLayout());
        passHeaderPanel.setBackground(Color.WHITE);
        passHeaderPanel.setMaximumSize(new Dimension(400, 25));
        passHeaderPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblPass = new JLabel("Mật khẩu");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        JLabel lblForgot = new JLabel("<html><font color='#023E8A'>Quên mật khẩu?</font></html>");
        lblForgot.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblForgot.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        passHeaderPanel.add(lblPass, BorderLayout.WEST);
        passHeaderPanel.add(lblForgot, BorderLayout.EAST);
        innerFormPanel.add(passHeaderPanel);

        txtPassword = createStyledPasswordField();
        innerFormPanel.add(txtPassword);
        innerFormPanel.add(Box.createVerticalStrut(35));

        btnDangNhap = new JButton("Đăng nhập →");
        btnDangNhap.setBackground(new Color(2, 62, 138));
        btnDangNhap.setForeground(Color.WHITE);
        btnDangNhap.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnDangNhap.setMaximumSize(new Dimension(400, 45));
        btnDangNhap.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDangNhap.putClientProperty(FlatClientProperties.STYLE, "arc: 15");
        btnDangNhap.setAlignmentX(Component.LEFT_ALIGNMENT);
        innerFormPanel.add(btnDangNhap);

        innerFormPanel.add(Box.createVerticalStrut(25));

        JLabel lblSignup = new JLabel("<html>Chưa có tài khoản? <font color='#023E8A'><b>Đăng ký ngay</b></font></html>");
        lblSignup.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSignup.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblSignup.setAlignmentX(Component.LEFT_ALIGNMENT);
        innerFormPanel.add(lblSignup);

        formWrapper.add(innerFormPanel);

        basePanel.add(imagePanel);
        basePanel.add(formWrapper);
        setContentPane(basePanel);

        btnDangNhap.addActionListener(e -> xuLyDangNhap());

        lblSignup.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new DangKyFrame().setVisible(true);
            }
        });
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setBorder(new EmptyBorder(8, 0, 4, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField();
        field.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        field.putClientProperty(FlatClientProperties.STYLE, "arc: 10; padding: 5,10,5,10");
        field.setMaximumSize(new Dimension(400, 45));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "********");
        field.putClientProperty(FlatClientProperties.STYLE, "arc: 10; padding: 5,10,5,10");
        field.setMaximumSize(new Dimension(400, 45));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }

    private void xuLyDangNhap() {
        String user = txtUsername.getText();
        String pass = new String(txtPassword.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ thông tin!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }

        TaiKhoanDAO dao = new TaiKhoanDAO();
        int vaiTro = dao.kiemTraDangNhap(user, pass);

        // Trong file DangNhapFrame.java, hàm xuLyDangNhap()
    if (vaiTro == 1) {
        JOptionPane.showMessageDialog(this, "Đăng nhập thành công!");
        dispose();
        // Truyền username từ ô nhập liệu vào đây
        new CustomerHome(txtUsername.getText()).setVisible(true); 
    }
    else {
            JOptionPane.showMessageDialog(this, "Sai tên đăng nhập hoặc mật khẩu!", "Lỗi truy cập", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // 1. Dán 3 dòng làm mịn và set Font vào ĐÂY, NGAY DÒNG ĐẦU TIÊN
    System.setProperty("awt.useSystemAAFontSettings", "on");
    System.setProperty("swing.aatext", "true");
    UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 14));

    // 2. Sau đó mới gọi FlatLaf (nếu có)
    com.formdev.flatlaf.FlatLightLaf.setup();

    // 3. Cuối cùng mới là code mở màn hình của Java
    java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
            new DangNhapFrame().setVisible(true);
        }
    });
    }
}