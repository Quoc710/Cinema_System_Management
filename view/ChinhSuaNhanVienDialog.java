package com.mycompany.cinema_system_management.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.mycompany.cinema_system_management.DAO.PhanQuyenDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ChinhSuaNhanVienDialog extends JDialog {
    // Khai báo DAO để sử dụng
    private final PhanQuyenDAO phanQuyenDAO = new PhanQuyenDAO();
    
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JComboBox<String> cbVaiTro;
    private int maTK;
    private boolean isSuccess = false;

    public ChinhSuaNhanVienDialog(Frame parent, int maTK, String username, int maVaiTro) {
        super(parent, "Chỉnh sửa thông tin nhân viên", true);
        this.maTK = maTK;
        initDesign(username, maVaiTro);
    }

    private void initDesign(String username, int maVaiTro) {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // --- Header ---
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setOpaque(false);
        pnlTop.setBorder(new EmptyBorder(25, 30, 10, 30));
        JLabel lblTitle = new JLabel("Chỉnh sửa tài khoản");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        pnlTop.add(lblTitle, BorderLayout.NORTH);

        // --- Body ---
        JPanel pnlBody = new JPanel(new GridBagLayout());
        pnlBody.setOpaque(false);
        pnlBody.setBorder(new EmptyBorder(10, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        gbc.insets = new Insets(10, 10, 10, 10); 
        gbc.weightx = 0.5;

        txtUser = new JTextField(username); 
        txtUser.setEditable(false); // Username không cho sửa
        txtUser.putClientProperty(FlatClientProperties.STYLE, "background: #F1F5F9;");
        
        txtPass = new JPasswordField();
        txtPass.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true; arc: 8;");
        
        cbVaiTro = new JComboBox<>(new String[]{"Nhân viên quản lý", "Quản lý kho", "Admin"});
        cbVaiTro.setSelectedIndex(maVaiTro - 2);

        addComp(pnlBody, "TÊN ĐĂNG NHẬP (CỐ ĐỊNH)", txtUser, gbc, 0, 0);
        addComp(pnlBody, "VAI TRÒ MỚI", cbVaiTro, gbc, 1, 0);
        gbc.gridwidth = 2;
        addComp(pnlBody, "MẬT KHẨU MỚI (ĐỂ TRỐNG NẾU KHÔNG ĐỔI)", txtPass, gbc, 0, 1);

        // --- Footer ---
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 20));
        pnlFooter.setBackground(new Color(250, 251, 253));
        
        JButton btnLuu = new JButton("Lưu thay đổi");
        btnLuu.setBackground(new Color(37, 99, 235)); 
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setPreferredSize(new Dimension(150, 40));
        btnLuu.addActionListener(e -> {
            String pass = new String(txtPass.getPassword());
            int role = cbVaiTro.getSelectedIndex() + 2;
            // Gọi đúng biến phanQuyenDAO đã khai báo ở trên
            if (phanQuyenDAO.updateNhanVien(maTK, pass, role)) {
                isSuccess = true; 
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi cập nhật!");
            }
        });

        pnlFooter.add(new JButton("Hủy") {{ addActionListener(x -> dispose()); }});
        pnlFooter.add(btnLuu);

        add(pnlTop, BorderLayout.NORTH); 
        add(pnlBody, BorderLayout.CENTER); 
        add(pnlFooter, BorderLayout.SOUTH);
        
        pack(); 
        setSize(600, 420); 
        setLocationRelativeTo(null);
    }

    private void addComp(JPanel p, String l, JComponent c, GridBagConstraints g, int x, int y) {
        g.gridx = x; g.gridy = y;
        JPanel pn = new JPanel(new BorderLayout(0, 5)); pn.setOpaque(false);
        JLabel lbl = new JLabel(l); lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        c.setPreferredSize(new Dimension(c.getPreferredSize().width, 38));
        pn.add(lbl, BorderLayout.NORTH); pn.add(c, BorderLayout.CENTER);
        p.add(pn, g); g.gridwidth = 1;
    }
    
    public boolean isSuccess() { return isSuccess; }
}