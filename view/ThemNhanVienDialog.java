package com.mycompany.cinema_system_management.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.mycompany.cinema_system_management.DAO.PhanQuyenDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ThemNhanVienDialog extends JDialog {
    private JTextField txtHoTen, txtSDT, txtUser;
    private JPasswordField txtPass;
    private JComboBox<String> cbVaiTro;
    private boolean isSuccess = false;

    public ThemNhanVienDialog(Frame parent) {
        super(parent, "CẤP TÀI KHOẢN NHÂN VIÊN", true);
        initDesign();
    }

    private void initDesign() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // Header
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setOpaque(false);
        pnlTop.setBorder(new EmptyBorder(25, 35, 15, 35));
        JLabel lblHeader = new JLabel("Thêm nhân viên mới");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 24)); 
        pnlTop.add(lblHeader, BorderLayout.NORTH);

        // Form Body (Bỏ Email)
        JPanel pnlBody = new JPanel(new GridBagLayout());
        pnlBody.setOpaque(false);
        pnlBody.setBorder(new EmptyBorder(0, 35, 20, 35));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 8, 10, 8);
        gbc.weightx = 0.5;

        txtHoTen = createStyledField("Nhập họ và tên...");
        txtSDT = createStyledField("Số điện thoại liên lạc");
        txtUser = createStyledField("Tên đăng nhập hệ thống");
        txtPass = new JPasswordField();
        txtPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPass.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "••••••••");
        txtPass.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true; arc: 8; background: #F8FAFC; borderWidth: 1; borderColor: #E2E8F0;");

        cbVaiTro = new JComboBox<>(new String[]{"Nhân viên quản lý", "Quản lý kho", "Admin hệ thống"});
        cbVaiTro.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbVaiTro.putClientProperty(FlatClientProperties.STYLE, "arc: 8; background: #F8FAFC; borderWidth: 1; borderColor: #E2E8F0;");

        // Hàng 1: Họ tên - SĐT
        addComponent(pnlBody, "HỌ TÊN", txtHoTen, gbc, 0, 0);
        addComponent(pnlBody, "SỐ ĐIỆN THOẠI", txtSDT, gbc, 1, 0);

        // Hàng 2: Username - Vai trò
        addComponent(pnlBody, "USERNAME", txtUser, gbc, 0, 1);
        addComponent(pnlBody, "VAI TRÒ TRUY CẬP", cbVaiTro, gbc, 1, 1);
        
        // Hàng 3: Password (Chiếm hết 2 cột)
        gbc.gridwidth = 2; 
        addComponent(pnlBody, "MẬT KHẨU TẠM THỜI", txtPass, gbc, 0, 2);

        // Footer
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 20));
        pnlFooter.setBackground(Color.WHITE);
        pnlFooter.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(241, 245, 249)));
        
        JButton btnHuy = new JButton("Hủy");
        JButton btnXacNhan = new JButton("Xác nhận thêm");
        btnXacNhan.setBackground(new Color(37, 99, 235));
        btnXacNhan.setForeground(Color.WHITE);
        btnXacNhan.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnXacNhan.setPreferredSize(new Dimension(180, 40));

        btnXacNhan.addActionListener(e -> {
            String hoTen = txtHoTen.getText().trim();
            String sdt = txtSDT.getText().trim();
            String user = txtUser.getText().trim();
            String pass = new String(txtPass.getPassword());
            int role = cbVaiTro.getSelectedIndex() + 2;

            // Truyền "" vào cột Email trong Database
            if(new PhanQuyenDAO().insertNhanVien(user, pass, role, hoTen, "", sdt)) {
                isSuccess = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu vào Database!");
            }
        });
        
        btnHuy.addActionListener(e -> dispose());
        pnlFooter.add(btnHuy);
        pnlFooter.add(btnXacNhan);

        add(pnlTop, BorderLayout.NORTH);
        add(pnlBody, BorderLayout.CENTER);
        add(pnlFooter, BorderLayout.SOUTH);

        pack();
        setSize(700, 520); // Thu nhỏ chiều cao lại vì bớt 1 hàng
        setLocationRelativeTo(null);
    }

    private void addComponent(JPanel pnl, String label, JComponent comp, GridBagConstraints gbc, int x, int y) {
        gbc.gridx = x; gbc.gridy = y;
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(71, 85, 105)); 
        comp.setPreferredSize(new Dimension(comp.getPreferredSize().width, 38));
        p.add(lbl, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        pnl.add(p, gbc);
        gbc.gridwidth = 1;
    }

    private JTextField createStyledField(String hint) {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, hint);
        f.putClientProperty(FlatClientProperties.STYLE, "arc: 8; background: #F8FAFC; borderWidth: 1; borderColor: #E2E8F0; focusWidth: 0; margin: [0, 8, 0, 8]");
        return f;
    }

    public boolean isSuccess() { return isSuccess; }
}