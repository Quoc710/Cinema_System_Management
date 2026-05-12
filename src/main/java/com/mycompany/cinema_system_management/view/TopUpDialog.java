package com.mycompany.cinema_system_management.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.mycompany.cinema_system_management.utils.DatabaseConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.DecimalFormat;

public class TopUpDialog extends JDialog {
    private JLabel lblTotal;
    private JTextField txtAmount;
    private DecimalFormat df = new DecimalFormat("#,### đ");
    
    private String currentUsername;
    private MyTicketsView parentView; // Lưu lại trang gốc để gọi hàm refresh số dư

    public TopUpDialog(MyTicketsView parent, String username, String currentBalance) {
        super(parent, "Nạp tiền vào ví", true);
        this.parentView = parent;
        this.currentUsername = username;
        
        setSize(400, 380);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(25, 30, 25, 30));
        mainPanel.setOpaque(false);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel lblTitle = new JLabel("Nạp tiền vào ví");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.add(lblTitle, BorderLayout.WEST);
        mainPanel.add(header);
        mainPanel.add(Box.createVerticalStrut(20));

        // Balance
        JPanel balancePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        balancePanel.setOpaque(false);
        JLabel lblBalText = new JLabel("Số dư hiện tại: ");
        lblBalText.setForeground(new Color(100, 116, 139));
        JLabel lblBalVal = new JLabel(currentBalance);
        lblBalVal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblBalVal.setForeground(new Color(37, 99, 235));
        balancePanel.add(lblBalText);
        balancePanel.add(lblBalVal);
        mainPanel.add(balancePanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Input Amount
        txtAmount = new JTextField();
        txtAmount.setFont(new Font("Segoe UI", Font.BOLD, 24));
        txtAmount.setPreferredSize(new Dimension(100, 60));
        txtAmount.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "0 đ");
        txtAmount.putClientProperty(FlatClientProperties.STYLE, "arc: 8; margin: 0,15,0,15; borderWidth: 1; borderColor: #E2E8F0;");
        mainPanel.add(txtAmount);
        mainPanel.add(Box.createVerticalStrut(20));

        // Summary
        JPanel summary = new JPanel();
        summary.setLayout(new BoxLayout(summary, BoxLayout.Y_AXIS));
        summary.setOpaque(false);
        
        JPanel row1 = new JPanel(new BorderLayout()); row1.setOpaque(false);
        JLabel lblT1 = new JLabel("Số tiền nạp:"); lblT1.setForeground(Color.GRAY);
        JLabel lblV1 = new JLabel("0 đ"); lblV1.setFont(new Font("Segoe UI", Font.BOLD, 13));
        row1.add(lblT1, BorderLayout.WEST); row1.add(lblV1, BorderLayout.EAST);
        
        JPanel divider = new JPanel();
        divider.setPreferredSize(new Dimension(0, 1));
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        divider.setBackground(new Color(226, 232, 240));

        JPanel row2 = new JPanel(new BorderLayout()); row2.setOpaque(false);
        JLabel lblT2 = new JLabel("Tổng thanh toán:"); lblT2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotal = new JLabel("0 đ"); lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotal.setForeground(new Color(37, 99, 235));
        row2.add(lblT2, BorderLayout.WEST); row2.add(lblTotal, BorderLayout.EAST);

        summary.add(row1);
        summary.add(Box.createVerticalStrut(10));
        summary.add(divider);
        summary.add(Box.createVerticalStrut(10));
        summary.add(row2);
        mainPanel.add(summary);
        mainPanel.add(Box.createVerticalStrut(30));

        // Buttons
        JPanel actions = new JPanel(new GridLayout(1, 2, 15, 0));
        actions.setOpaque(false);
        
        JButton btnCancel = new JButton("Hủy");
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancel.setBackground(Color.WHITE);
        btnCancel.putClientProperty(FlatClientProperties.STYLE, "arc: 8; borderWidth: 1; borderColor: #E2E8F0;");
        btnCancel.addActionListener(e -> dispose());

        JButton btnConfirm = new JButton("Xác nhận");
        btnConfirm.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnConfirm.setBackground(new Color(37, 99, 235));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.putClientProperty(FlatClientProperties.STYLE, "arc: 8; borderWidth: 0;");
        
        // SỰ KIỆN NẠP TIỀN VÀO DB
        btnConfirm.addActionListener(e -> {
            String input = txtAmount.getText().replaceAll("[^0-9]", "");
            if(input.isEmpty() || input.equals("0")) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số tiền lớn hơn 0 đ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            double amount = Double.parseDouble(input);
            if(napTienVaoDatabase(amount)) {
                JOptionPane.showMessageDialog(this, "Nạp thành công " + df.format(amount) + " vào ví!");
                parentView.loadSoDuViTuDB(); // Yêu cầu trang ngoài load lại số dư
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Nạp tiền thất bại. Lỗi kết nối CSDL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        actions.add(btnCancel); actions.add(btnConfirm);
        mainPanel.add(actions);

        // Realtime calculation logic
        txtAmount.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { update(); }
            public void removeUpdate(DocumentEvent e) { update(); }
            public void changedUpdate(DocumentEvent e) { update(); }
            private void update() {
                try {
                    String input = txtAmount.getText().replaceAll("[^0-9]", "");
                    if(input.isEmpty()) input = "0";
                    double amount = Double.parseDouble(input);
                    String formatted = df.format(amount);
                    lblV1.setText(formatted);
                    lblTotal.setText(formatted);
                } catch (Exception ex) {
                    lblV1.setText("0 đ"); lblTotal.setText("0 đ");
                }
            }
        });

        setContentPane(mainPanel);
    }

    // --- HÀM KẾT NỐI ORACLE ĐỂ CỘNG TIỀN ---
    private boolean napTienVaoDatabase(double amount) {
        String sql = "UPDATE KHACHHANG SET SODUVI = SODUVI + ? WHERE MATK = (SELECT MATK FROM TAIKHOAN WHERE USERNAME = ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, amount);
            ps.setString(2, currentUsername);
            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}