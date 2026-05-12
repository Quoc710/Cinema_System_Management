package com.mycompany.cinema_system_management.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.mycompany.cinema_system_management.dao.MarketplaceDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;
import java.text.DecimalFormat;

public class BuyTicketDialog extends JDialog {

    private String buyerUsername;
    private int maPass;
    private double giaPass;
    
    private double totalAmountToPay;
    private double totalPayoutToSeller;

    private DecimalFormat df = new DecimalFormat("#,### đ");

    public BuyTicketDialog(JFrame parent, String buyerUsername, int maPass, String tenPhim, String hinhAnh, String thongTinSuat, String viTriGhe, double giaPass) {
        super(parent, "Confirm Purchase", true);
        this.buyerUsername = buyerUsername;
        this.maPass = maPass;
        this.giaPass = giaPass;

        // Tính toán nghiệp vụ tài chính
        double platformFee = giaPass * 0.05;
        this.totalAmountToPay = giaPass + platformFee; // Người mua trả: Giá bán + 5% phí
        this.totalPayoutToSeller = giaPass - platformFee - 5000; // Người bán nhận: Giá bán - 5% phí - 5000 phí xử lý

        setSize(400, 480);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(25, 30, 20, 30));
        mainPanel.setOpaque(false);

        // Header
        JLabel lblTitle = new JLabel("Confirm Purchase");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(lblTitle);
        mainPanel.add(Box.createVerticalStrut(20));

        // Thông tin tài sản (Vé)
        JPanel assetInfo = new JPanel(new BorderLayout(15, 0));
        assetInfo.setOpaque(false);
        assetInfo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblPoster = new JLabel();
        lblPoster.setPreferredSize(new Dimension(65, 90));
        try {
            URL url = getClass().getResource("/images/" + hinhAnh);
            if (url != null) lblPoster.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(65, 90, Image.SCALE_SMOOTH)));
            else { lblPoster.setOpaque(true); lblPoster.setBackground(new Color(226, 232, 240)); }
        } catch (Exception e) {}
        lblPoster.putClientProperty(FlatClientProperties.STYLE, "arc: 8");

        JPanel textInfo = new JPanel(new GridLayout(3, 1));
        textInfo.setOpaque(false);
        JLabel lblName = new JLabel(tenPhim);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 15));
        JLabel lblShow = new JLabel(thongTinSuat);
        lblShow.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblShow.setForeground(new Color(100, 116, 139));
        
        JLabel lblSeat = new JLabel(" SEAT " + viTriGhe + " ");
        lblSeat.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblSeat.setOpaque(true);
        lblSeat.setBackground(new Color(241, 245, 249));
        lblSeat.setForeground(new Color(71, 85, 105));
        lblSeat.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        
        JPanel seatWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        seatWrapper.setOpaque(false);
        seatWrapper.add(lblSeat);

        textInfo.add(lblName);
        textInfo.add(lblShow);
        textInfo.add(seatWrapper);

        assetInfo.add(lblPoster, BorderLayout.WEST);
        assetInfo.add(textInfo, BorderLayout.CENTER);
        mainPanel.add(assetInfo);
        mainPanel.add(Box.createVerticalStrut(25));

        // Bảng kê chi phí
        mainPanel.add(createRow("Ticket Subtotal", df.format(giaPass), false));
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createRow("Platform Fee (5%)", df.format(platformFee), false));
        mainPanel.add(Box.createVerticalStrut(15));
        
        JPanel divider = new JPanel();
        divider.setPreferredSize(new Dimension(0, 1));
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        divider.setBackground(new Color(226, 232, 240));
        mainPanel.add(divider);
        mainPanel.add(Box.createVerticalStrut(15));

        mainPanel.add(createRow("Total Amount", df.format(totalAmountToPay), true));
        mainPanel.add(Box.createVerticalStrut(30));

        // Action Buttons
        JButton btnAuthorize = new JButton("AUTHORIZE TRANSACTION");
        btnAuthorize.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAuthorize.setBackground(new Color(2, 99, 224));
        btnAuthorize.setForeground(Color.WHITE);
        btnAuthorize.setPreferredSize(new Dimension(0, 45));
        btnAuthorize.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnAuthorize.putClientProperty(FlatClientProperties.STYLE, "arc: 5; borderWidth: 0; focusWidth: 0;");
        btnAuthorize.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAuthorize.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCancel.setBackground(Color.WHITE);
        btnCancel.setForeground(new Color(15, 23, 42));
        btnCancel.setPreferredSize(new Dimension(0, 40));
        btnCancel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnCancel.putClientProperty(FlatClientProperties.STYLE, "arc: 5; borderWidth: 1; borderColor: #E2E8F0; focusWidth: 0;");
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnCancel.addActionListener(e -> dispose());
        btnAuthorize.addActionListener(e -> executeTransaction());

        mainPanel.add(btnAuthorize);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(btnCancel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Disclaimer
        JLabel lblDisclaimer = new JLabel("<html><center>By authorizing, you agree to CineMarket's Secondary Asset Purchase Terms.<br>No refunds are available for verified resale items.</center></html>");
        lblDisclaimer.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        lblDisclaimer.setForeground(new Color(148, 163, 184));
        lblDisclaimer.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(lblDisclaimer);

        setContentPane(mainPanel);
    }

    private JPanel createRow(String label, String value, boolean isTotal) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblL = new JLabel(label);
        lblL.setFont(new Font("Segoe UI", isTotal ? Font.BOLD : Font.PLAIN, isTotal ? 18 : 13));
        lblL.setForeground(isTotal ? new Color(15, 23, 42) : new Color(100, 116, 139));
        
        JLabel lblV = new JLabel(value);
        lblV.setFont(new Font("Segoe UI", Font.BOLD, isTotal ? 18 : 13));
        lblV.setForeground(isTotal ? new Color(2, 99, 224) : new Color(100, 116, 139));

        row.add(lblL, BorderLayout.WEST);
        row.add(lblV, BorderLayout.EAST);
        return row;
    }

    // ĐÃ FIX: Chỉ gọi hàm DAO chuẩn từ Backend, không tự code SQL bậy bạ ở View nữa!
    private void executeTransaction() {
        MarketplaceDAO marketDAO = new MarketplaceDAO();
        
        // Gọi thẳng hàm xử lý chuẩn đã có log Sao kê
        String result = marketDAO.thanhToanVeCho(maPass, buyerUsername);

        if ("SUCCESS".equals(result)) {
            JOptionPane.showMessageDialog(this, "Giao dịch thành công! Vé đã được chuyển vào My Ticket Assets.", "Xác nhận", JOptionPane.INFORMATION_MESSAGE);
            dispose(); // Đóng form popup sau khi mua xong
        } else {
            // Nếu lỗi (ví dụ thiếu tiền, vé không tồn tại) thì nó văng câu thông báo lên
            JOptionPane.showMessageDialog(this, result, "Giao dịch từ chối", JOptionPane.ERROR_MESSAGE);
        }
    }
}