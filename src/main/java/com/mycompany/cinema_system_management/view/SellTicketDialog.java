package com.mycompany.cinema_system_management.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.mycompany.cinema_system_management.utils.DatabaseConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;

public class SellTicketDialog extends JDialog {
    private JLabel lblPayout;
    private JLabel lblPlatFee;
    private JTextField txtAskingPrice;
    private DecimalFormat df = new DecimalFormat("#,### đ");

    private MyTicketsView parentView;
    private String currentUsername;
    private int selectedRow;
    private String tenPhim, viTriGhe;

    public SellTicketDialog(MyTicketsView parent, String username, int selectedRow, String tenPhim, String hinhAnh, String info, String seat, String originalPrice) {
        super(parent, "Đăng bán vé", true);
        this.parentView = parent;
        this.currentUsername = username;
        this.selectedRow = selectedRow;
        this.tenPhim = tenPhim;
        this.viTriGhe = seat;

        setSize(460, 600);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(25, 30, 25, 30));
        mainPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("List Ticket for Marketplace");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(lblTitle);
        mainPanel.add(Box.createVerticalStrut(20));

        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(Color.WHITE);
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 12; borderWidth: 1; borderColor: #E2E8F0;");
        card.setBorder(new EmptyBorder(15, 15, 15, 15));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblPoster = new JLabel();
        lblPoster.setPreferredSize(new Dimension(80, 110));
        try {
            URL url = getClass().getResource("/images/" + hinhAnh);
            if(url != null) lblPoster.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(80, 110, Image.SCALE_SMOOTH)));
        } catch (Exception e) {}
        lblPoster.putClientProperty(FlatClientProperties.STYLE, "arc: 8");

        JPanel infoPanel = new JPanel(new GridLayout(4, 1));
        infoPanel.setOpaque(false);
        JLabel lblTag = new JLabel("ACTIVE ASSET"); lblTag.setFont(new Font("Segoe UI", Font.BOLD, 10)); lblTag.setForeground(new Color(37, 99, 235));
        JLabel lblName = new JLabel(tenPhim); lblName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        JLabel lblShow = new JLabel(info); lblShow.setFont(new Font("Segoe UI", Font.PLAIN, 12)); lblShow.setForeground(Color.GRAY);
        JLabel lblSeat = new JLabel("Seat: " + seat); lblSeat.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        infoPanel.add(lblTag); infoPanel.add(lblName); infoPanel.add(lblShow); infoPanel.add(lblSeat);
        card.add(lblPoster, BorderLayout.WEST); card.add(infoPanel, BorderLayout.CENTER);
        mainPanel.add(card);
        mainPanel.add(Box.createVerticalStrut(20));

        JPanel pricePanel = new JPanel(new BorderLayout()); pricePanel.setOpaque(false); pricePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblOrigText = new JLabel("Original Price"); lblOrigText.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JLabel lblOrigVal = new JLabel(originalPrice); lblOrigVal.setForeground(Color.GRAY); lblOrigVal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pricePanel.add(lblOrigText, BorderLayout.WEST); pricePanel.add(lblOrigVal, BorderLayout.EAST);
        mainPanel.add(pricePanel);
        mainPanel.add(Box.createVerticalStrut(20));

        JLabel lblAsk = new JLabel("Your Asking Price"); lblAsk.setFont(new Font("Segoe UI", Font.BOLD, 13)); lblAsk.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(lblAsk);
        mainPanel.add(Box.createVerticalStrut(8));

        txtAskingPrice = new JTextField();
        txtAskingPrice.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtAskingPrice.setPreferredSize(new Dimension(0, 45));
        txtAskingPrice.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        txtAskingPrice.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "0 đ");
        txtAskingPrice.putClientProperty(FlatClientProperties.STYLE, "arc: 8; margin: 0,10,0,10; borderWidth: 1; borderColor: #E2E8F0;");
        txtAskingPrice.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(txtAskingPrice);
        
        JLabel lblWarn = new JLabel("Listings higher than 120% of original value may require verification.");
        lblWarn.setFont(new Font("Segoe UI", Font.PLAIN, 11)); lblWarn.setForeground(Color.GRAY); lblWarn.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(lblWarn);
        mainPanel.add(Box.createVerticalStrut(20));

        JPanel feesPanel = new JPanel();
        feesPanel.setLayout(new BoxLayout(feesPanel, BoxLayout.Y_AXIS));
        feesPanel.setBackground(new Color(248, 250, 252));
        feesPanel.putClientProperty(FlatClientProperties.STYLE, "arc: 8; borderWidth: 1; borderColor: #E2E8F0;");
        feesPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        feesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        feesPanel.add(createRow("Platform Fee (5%)", "-0 đ", false, true));
        feesPanel.add(Box.createVerticalStrut(8));
        feesPanel.add(createRow("Processing Fee", "-5,000 đ", false, true));
        feesPanel.add(Box.createVerticalStrut(15));
        
        JPanel divider = new JPanel(); divider.setPreferredSize(new Dimension(0, 1)); divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1)); divider.setBackground(new Color(226, 232, 240));
        feesPanel.add(divider); feesPanel.add(Box.createVerticalStrut(15));

        feesPanel.add(createRow("Total Payout", "0 đ", true, false));
        mainPanel.add(feesPanel);
        mainPanel.add(Box.createVerticalStrut(30));

        JPanel actions = new JPanel(new GridLayout(1, 2, 15, 0)); actions.setOpaque(false); actions.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton btnCancel = new JButton("Cancel"); btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 14)); btnCancel.setBackground(Color.WHITE); btnCancel.putClientProperty(FlatClientProperties.STYLE, "arc: 8; borderWidth: 1; borderColor: #E2E8F0;");
        btnCancel.addActionListener(e -> dispose());
        
        JButton btnConfirm = new JButton("Confirm & List"); btnConfirm.setFont(new Font("Segoe UI", Font.BOLD, 14)); btnConfirm.setBackground(new Color(37, 99, 235)); btnConfirm.setForeground(Color.WHITE); btnConfirm.putClientProperty(FlatClientProperties.STYLE, "arc: 8; borderWidth: 0;");
        
        btnConfirm.addActionListener(e -> {
            String input = txtAskingPrice.getText().replaceAll("[^0-9]", "");
            if(input.isEmpty() || input.equals("0")) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập giá bán hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            double asking = Double.parseDouble(input);
            
            if (sellTicketInDB(asking)) {
                JOptionPane.showMessageDialog(this, "Vé đã được đẩy lên Chợ thành công!");
                parentView.updateTicketStatusUI(selectedRow, "Đang rao bán");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi kết nối CSDL, đăng bán thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        actions.add(btnCancel); actions.add(btnConfirm);
        mainPanel.add(actions);

        txtAskingPrice.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { calc(); }
            public void removeUpdate(DocumentEvent e) { calc(); }
            public void changedUpdate(DocumentEvent e) { calc(); }
            private void calc() {
                try {
                    String input = txtAskingPrice.getText().replaceAll("[^0-9]", "");
                    if(input.isEmpty()) input = "0";
                    double asking = Double.parseDouble(input);
                    double platFee = asking * 0.05;
                    double processFee = 5000;
                    double payout = asking - platFee - processFee;
                    if(payout < 0) payout = 0;
                    
                    lblPlatFee.setText("-" + df.format(platFee));
                    lblPayout.setText(df.format(payout));
                } catch (Exception ex) {}
            }
        });

        setContentPane(mainPanel);
    }

    private JPanel createRow(String text, String val, boolean isTotal, boolean isRed) {
        JPanel p = new JPanel(new BorderLayout()); p.setOpaque(false);
        JLabel l1 = new JLabel(text); l1.setFont(new Font("Segoe UI", isTotal ? Font.BOLD : Font.PLAIN, isTotal ? 16 : 13));
        JLabel l2 = new JLabel(val); l2.setFont(new Font("Segoe UI", isTotal ? Font.BOLD : Font.PLAIN, isTotal ? 20 : 13));
        if(isRed) l2.setForeground(new Color(220, 38, 38));
        if(isTotal) l2.setForeground(new Color(37, 99, 235));
        
        if (text.contains("Platform")) lblPlatFee = l2;
        if (text.contains("Payout")) lblPayout = l2;

        p.add(l1, BorderLayout.WEST); p.add(l2, BorderLayout.EAST);
        return p;
    }

    private boolean sellTicketInDB(double askingPrice) {
        String sqlGetIDs = "SELECT v.MAVE, k.MAKH FROM VE v " +
                           "JOIN GHENGOI g ON v.MAGHE = g.MAGHE " +
                           "JOIN LICHCHIEU lc ON v.MALICHCHIEU = lc.MALICHCHIEU " +
                           "JOIN PHIM p ON lc.MAPHIM = p.MAPHIM " +
                           "JOIN KHACHHANG k ON v.MAKHACHHANG = k.MAKH " +
                           "JOIN TAIKHOAN t ON k.MATK = t.MATK " +
                           "WHERE t.USERNAME = ? AND p.TENPHIM = ? AND g.TENGHE = ?";
                           
        String sqlUpdateVe = "UPDATE VE SET TRANGTHAIVE = 2 WHERE MAVE = ?";
        String sqlInsertPass = "INSERT INTO TRANGPASSVE (MAVE, MAKH_BAN, GIAPASS, TRANGTHAI_PASS) VALUES (?, ?, ?, 1)";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); 
            int maVe = -1, maKhachHang = -1;
            
            // ĐÃ FIX: Lọc bỏ phần loại ghế "(VIP)" hoặc "(THUONG)" ra khỏi tên ghế
            String tenGheThucTe = this.viTriGhe;
            if (tenGheThucTe.contains(" (")) {
                tenGheThucTe = tenGheThucTe.split(" \\(")[0].trim();
            }

            try (PreparedStatement ps1 = conn.prepareStatement(sqlGetIDs)) {
                ps1.setString(1, currentUsername);
                ps1.setString(2, tenPhim);
                ps1.setString(3, tenGheThucTe); 
                ResultSet rs = ps1.executeQuery();
                if (rs.next()) {
                    maVe = rs.getInt("MAVE");
                    maKhachHang = rs.getInt("MAKH");
                }
            }
            
            if (maVe != -1) {
                try (PreparedStatement ps2 = conn.prepareStatement(sqlUpdateVe)) {
                    ps2.setInt(1, maVe);
                    ps2.executeUpdate();
                }
                try (PreparedStatement ps3 = conn.prepareStatement(sqlInsertPass)) {
                    ps3.setInt(1, maVe);
                    ps3.setInt(2, maKhachHang);
                    ps3.setDouble(3, askingPrice);
                    ps3.executeUpdate();
                }
                conn.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}