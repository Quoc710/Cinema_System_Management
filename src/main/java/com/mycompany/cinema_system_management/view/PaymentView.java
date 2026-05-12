package com.mycompany.cinema_system_management.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import com.mycompany.cinema_system_management.dao.HoaDonDAO;
import com.mycompany.cinema_system_management.utils.DatabaseConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class PaymentView extends JFrame {

    private String currentUsername;
    private String tenPhim;
    private String thongTinSuat;
    private List<String> danhSachGhe;
    private double tongTienVe;
    private Map<String, Integer> snackCart;
    private double totalAmount;
    private JFrame previousFrame;

    private String hinhAnhPhim = "";
    private DecimalFormat df = new DecimalFormat("#,### đ");
    
    private double tienGiam = 0;
    private JLabel lblDiscountVal;
    private JLabel lblTotalFinalVal;
    private JComboBox<String> cbPromo;

    public PaymentView(String username, String tenPhim, String thongTinSuat, List<String> danhSachGhe, 
                       double tongTienVe, Map<String, Integer> snackCart, double totalAmount, JFrame previousFrame) {
        this.currentUsername = username;
        this.tenPhim = tenPhim;
        this.thongTinSuat = thongTinSuat;
        this.danhSachGhe = danhSachGhe;
        this.tongTienVe = tongTienVe;
        this.snackCart = snackCart;
        this.totalAmount = totalAmount;
        this.previousFrame = previousFrame;

        fetchHinhAnhPhim();

        FlatLightLaf.setup();
        setTitle("CineMarket - Review & Confirm");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        getContentPane().setBackground(new Color(248, 250, 252));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);
        mainPanel.add(createNavbar(), BorderLayout.NORTH);

        JPanel contentWrapper = new JPanel(new BorderLayout(30, 20));
        contentWrapper.setOpaque(false);
        contentWrapper.setBorder(new EmptyBorder(20, 80, 40, 80));

        contentWrapper.add(createTopHeader(), BorderLayout.NORTH);
        
        JPanel splitPanel = new JPanel(new BorderLayout(30, 0));
        splitPanel.setOpaque(false);
        
        splitPanel.add(createLeftPanel(), BorderLayout.CENTER);
        splitPanel.add(createRightPanel(), BorderLayout.EAST);

        contentWrapper.add(splitPanel, BorderLayout.CENTER);
        mainPanel.add(contentWrapper, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private void fetchHinhAnhPhim() {
        String sql = "SELECT HINHANH FROM PHIM WHERE TENPHIM = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenPhim);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                hinhAnhPhim = rs.getString("HINHANH");
            }
        } catch (Exception e) {}
    }

    private Vector<String> fetchKhuyenMai() {
        Vector<String> kmList = new Vector<>();
        kmList.add("Chọn mã khuyến mãi...");
        String sql = "SELECT TENKM FROM KHUYENMAI WHERE SOLUONGCONLAI > 0 AND SYSDATE >= NGAYBD AND SYSDATE <= NGAYKT";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                kmList.add(rs.getString("TENKM"));
            }
        } catch (Exception e) {}
        return kmList;
    }

    private JPanel createNavbar() {
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.X_AXIS));
        nav.setBackground(Color.WHITE);
        nav.setBorder(new EmptyBorder(15, 40, 15, 40));
        nav.setPreferredSize(new Dimension(0, 70));

        JLabel lblLogo = new JLabel("CineMarket");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        nav.add(lblLogo);
        nav.add(Box.createRigidArea(new Dimension(40, 0)));
        nav.add(createNavLink("Movies"));
        nav.add(Box.createHorizontalGlue());

        JLabel lblAccount = new JLabel("Account: " + currentUsername);
        lblAccount.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nav.add(lblAccount);
        return nav;
    }

    private JLabel createNavLink(String text) {
        JLabel link = new JLabel(text);
        link.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        link.setForeground(Color.GRAY);
        link.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return link;
    }

    private JPanel createTopHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel lblTitle = new JLabel("Review & Confirm");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        header.add(lblTitle, BorderLayout.WEST);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(header, BorderLayout.NORTH);
        
        JPanel line = new JPanel();
        line.setPreferredSize(new Dimension(0, 3));
        line.setBackground(new Color(37, 99, 235)); 
        wrapper.add(line, BorderLayout.SOUTH);

        return wrapper;
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        JPanel orderCard = new JPanel();
        orderCard.setLayout(new BoxLayout(orderCard, BoxLayout.Y_AXIS));
        orderCard.setBackground(Color.WHITE);
        orderCard.putClientProperty(FlatClientProperties.STYLE, "arc: 12; borderWidth: 1; borderColor: #E2E8F0;");
        orderCard.setBorder(new EmptyBorder(25, 25, 25, 25));
        orderCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblOrderTitle = new JLabel("🎫 Order Details");
        lblOrderTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblOrderTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        orderCard.add(lblOrderTitle);
        orderCard.add(Box.createVerticalStrut(20));

        JPanel movieInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        movieInfo.setOpaque(false);
        movieInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        movieInfo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        
        JLabel lblPoster = new JLabel();
        lblPoster.setPreferredSize(new Dimension(110, 150));
        try {
            URL url = getClass().getResource("/images/" + hinhAnhPhim);
            if (url != null) lblPoster.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(110, 150, Image.SCALE_SMOOTH)));
        } catch (Exception e) {}
        lblPoster.putClientProperty(FlatClientProperties.STYLE, "arc: 8");

        JPanel textInfo = new JPanel(new GridLayout(4, 1, 0, 5));
        textInfo.setOpaque(false);
        textInfo.setBorder(new EmptyBorder(0, 20, 0, 0));
        JLabel lblMName = new JLabel(tenPhim);
        lblMName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        JLabel lblMTime = new JLabel("🕒 " + thongTinSuat);
        lblMTime.setForeground(new Color(71, 85, 105));
        JLabel lblSeats = new JLabel("SEATS: " + String.join(", ", danhSachGhe));
        lblSeats.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSeats.setForeground(new Color(37, 99, 235));

        textInfo.add(lblMName); textInfo.add(lblMTime); textInfo.add(lblSeats);
        movieInfo.add(lblPoster); movieInfo.add(textInfo);
        
        orderCard.add(movieInfo);
        orderCard.add(Box.createVerticalStrut(20));
        
        JPanel divider = new JPanel(); divider.setPreferredSize(new Dimension(0, 1)); divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1)); divider.setBackground(new Color(226, 232, 240));
        orderCard.add(divider);
        orderCard.add(Box.createVerticalStrut(20));

        orderCard.add(createLineItem("Standard Admission (x" + danhSachGhe.size() + ")", df.format(tongTienVe)));
        orderCard.add(Box.createVerticalStrut(10));
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT GIABAN FROM SANPHAM WHERE TENSP = ?";
            for (Map.Entry<String, Integer> entry : snackCart.entrySet()) {
                String snackName = entry.getKey();
                int qty = entry.getValue();
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, snackName);
                ResultSet rs = ps.executeQuery();
                if(rs.next()) {
                    double lineTotal = rs.getDouble("GIABAN") * qty;
                    orderCard.add(createLineItem(snackName + " (x" + qty + ")", df.format(lineTotal)));
                    orderCard.add(Box.createVerticalStrut(10));
                }
            }
        } catch (Exception e) {}

        leftPanel.add(orderCard);
        leftPanel.add(Box.createVerticalStrut(20));

        JPanel paymentCard = new JPanel(new BorderLayout());
        paymentCard.setBackground(Color.WHITE);
        paymentCard.putClientProperty(FlatClientProperties.STYLE, "arc: 12; borderWidth: 1; borderColor: #E2E8F0;");
        paymentCard.setBorder(new EmptyBorder(15, 25, 15, 25));
        paymentCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        paymentCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100)); 

        JPanel pTop = new JPanel(new BorderLayout()); pTop.setOpaque(false);
        JLabel lblPayTitle = new JLabel("💳 Payment Method"); lblPayTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        pTop.add(lblPayTitle, BorderLayout.WEST);
        
        JPanel payBox = new JPanel(new BorderLayout());
        payBox.setOpaque(false);
        payBox.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        JPanel innerPayBox = new JPanel(new BorderLayout());
        innerPayBox.setBackground(new Color(248, 250, 252));
        innerPayBox.putClientProperty(FlatClientProperties.STYLE, "arc: 8; borderWidth: 1; borderColor: #3B82F6;");
        innerPayBox.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        JLabel lblVisa = new JLabel("Ví CineMarket Pay");
        lblVisa.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel lblChange = new JLabel("Change");
        lblChange.setForeground(new Color(37, 99, 235)); lblChange.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        innerPayBox.add(lblVisa, BorderLayout.WEST); innerPayBox.add(lblChange, BorderLayout.EAST);
        payBox.add(innerPayBox, BorderLayout.CENTER);
        
        paymentCard.add(pTop, BorderLayout.NORTH);
        paymentCard.add(payBox, BorderLayout.CENTER);

        leftPanel.add(paymentCard);
        return leftPanel;
    }

    private JPanel createLineItem(String name, String price) {
        JPanel p = new JPanel(new BorderLayout()); p.setOpaque(false); p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblN = new JLabel(name); lblN.setForeground(new Color(71, 85, 105));
        JLabel lblP = new JLabel(price); lblP.setForeground(new Color(71, 85, 105));
        p.add(lblN, BorderLayout.WEST); p.add(lblP, BorderLayout.EAST);
        return p;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);
        rightPanel.setPreferredSize(new Dimension(380, 0));

        JPanel summaryCard = new JPanel();
        summaryCard.setLayout(new BoxLayout(summaryCard, BoxLayout.Y_AXIS));
        summaryCard.setBackground(Color.WHITE);
        summaryCard.putClientProperty(FlatClientProperties.STYLE, "arc: 12; borderWidth: 1; borderColor: #E2E8F0;");
        summaryCard.setBorder(new EmptyBorder(25, 25, 25, 25));
        summaryCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitle = new JLabel("Order Summary"); lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        summaryCard.add(lblTitle); summaryCard.add(Box.createVerticalStrut(20));

        JLabel lblPromo = new JLabel("Khuyến mãi"); lblPromo.setFont(new Font("Segoe UI", Font.PLAIN, 12)); lblPromo.setAlignmentX(Component.LEFT_ALIGNMENT);
        summaryCard.add(lblPromo); summaryCard.add(Box.createVerticalStrut(5));
        
        JPanel promoBox = new JPanel(new BorderLayout(10, 0)); promoBox.setOpaque(false); promoBox.setAlignmentX(Component.LEFT_ALIGNMENT); promoBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        Vector<String> dsKhuyenMai = fetchKhuyenMai();
        cbPromo = new JComboBox<>(dsKhuyenMai);
        cbPromo.putClientProperty(FlatClientProperties.STYLE, "arc: 8;");
        
        JButton btnApply = new JButton("Áp dụng"); btnApply.putClientProperty(FlatClientProperties.STYLE, "arc: 8; background: #F1F5F9;");
        
        btnApply.addActionListener(e -> {
            String selectedKM = cbPromo.getSelectedItem().toString();
            if (selectedKM.equals("Chọn mã khuyến mãi...")) {
                tienGiam = 0;
            } else {
                String sql = "SELECT GIATRIGIAM FROM KHUYENMAI WHERE TENKM = ?";
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, selectedKM);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        tienGiam = rs.getDouble("GIATRIGIAM");
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
            }
            lblDiscountVal.setText("- " + df.format(tienGiam));
            double finalTotal = totalAmount - tienGiam;
            if (finalTotal < 0) finalTotal = 0;
            lblTotalFinalVal.setText(df.format(finalTotal));
        });

        promoBox.add(cbPromo, BorderLayout.CENTER); promoBox.add(btnApply, BorderLayout.EAST);
        summaryCard.add(promoBox); summaryCard.add(Box.createVerticalStrut(25));

        JLabel lblSubtotalVal = new JLabel(df.format(totalAmount));
        summaryCard.add(createTotalRow("Subtotal", lblSubtotalVal, false));
        summaryCard.add(Box.createVerticalStrut(10));
        
        lblDiscountVal = new JLabel("- 0 đ");
        JPanel discountRow = createTotalRow("Discount", lblDiscountVal, false);
        ((JLabel)discountRow.getComponent(0)).setForeground(new Color(220, 38, 38)); lblDiscountVal.setForeground(new Color(220, 38, 38));
        summaryCard.add(discountRow);
        summaryCard.add(Box.createVerticalStrut(15));
        
        JPanel divider = new JPanel(); divider.setPreferredSize(new Dimension(0, 1)); divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1)); divider.setBackground(new Color(226, 232, 240));
        summaryCard.add(divider); summaryCard.add(Box.createVerticalStrut(15));

        lblTotalFinalVal = new JLabel(df.format(totalAmount));
        summaryCard.add(createTotalRow("Total", lblTotalFinalVal, true));
        summaryCard.add(Box.createVerticalStrut(25));

        // NÚT CONFIRM MỚI SẼ SHOW RÕ LỖI SQL LÊN MÀN HÌNH NẾU CÓ
        JButton btnConfirm = new JButton("Confirm Payment");
        btnConfirm.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnConfirm.setBackground(new Color(37, 99, 235)); 
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setPreferredSize(new Dimension(0, 45));
        btnConfirm.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnConfirm.putClientProperty(FlatClientProperties.STYLE, "arc: 8; borderWidth: 0; focusWidth: 0;");
        btnConfirm.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConfirm.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        btnConfirm.addActionListener(e -> {
            String selectedKM = cbPromo.getSelectedItem().toString();
            HoaDonDAO dao = new HoaDonDAO();
            
            // GỌI HÀM VÀ HỨNG CHUỖI TRẢ VỀ
            String result = dao.thanhToanGiaoDich(
                currentUsername, tenPhim, thongTinSuat, danhSachGhe, tongTienVe, snackCart, selectedKM
            );
            
            if (result.equals("SUCCESS")) {
                JOptionPane.showMessageDialog(this, "Giao dịch hoàn tất.\nVé và hóa đơn dịch vụ đã được ghi nhận vào hệ thống.");
                new CustomerHome(currentUsername).setVisible(true);
                dispose();
            } else {
                // VĂNG LỖI CỤ THỂ LÊN MẶT ĐỂ DEBGUG!
                JOptionPane.showMessageDialog(this, result, "Giao dịch thất bại", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        summaryCard.add(btnConfirm);
        summaryCard.add(Box.createVerticalStrut(15));

        JButton btnCancel = new JButton("Hủy giao dịch");
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnCancel.setBackground(new Color(220, 53, 69)); 
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setPreferredSize(new Dimension(0, 45));
        btnCancel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnCancel.putClientProperty(FlatClientProperties.STYLE, "arc: 8; borderWidth: 0; focusWidth: 0;");
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnCancel.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Nếu xác nhận, bạn sẽ phải thực hiện lại quá trình mua vé từ đầu.\nBạn có chắc chắn muốn hủy giao dịch này?",
                    "Xác nhận hủy",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                new CustomerHome(currentUsername).setVisible(true);
                dispose();
            }
        });

        summaryCard.add(btnCancel);
        summaryCard.add(Box.createVerticalStrut(15));
        
        JLabel lblTerms = new JLabel("<html><center>By clicking confirm, you agree to our Terms<br>of Sale and Cinema Partner policies.</center></html>");
        lblTerms.setFont(new Font("Segoe UI", Font.PLAIN, 11)); lblTerms.setForeground(Color.GRAY); lblTerms.setAlignmentX(Component.CENTER_ALIGNMENT);
        summaryCard.add(lblTerms);

        rightPanel.add(summaryCard);
        return rightPanel;
    }

    private JPanel createTotalRow(String title, JLabel lblV, boolean isBold) {
        JPanel p = new JPanel(new BorderLayout()); p.setOpaque(false); p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblT = new JLabel(title);
        lblT.setFont(new Font("Segoe UI", isBold ? Font.BOLD : Font.PLAIN, isBold ? 18 : 14));
        lblV.setFont(new Font("Segoe UI", isBold ? Font.BOLD : Font.PLAIN, isBold ? 18 : 14));
        if(isBold) lblV.setForeground(new Color(37, 99, 235));
        p.add(lblT, BorderLayout.WEST); p.add(lblV, BorderLayout.EAST);
        return p;
    }
}