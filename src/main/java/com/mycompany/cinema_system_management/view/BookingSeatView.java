package com.mycompany.cinema_system_management.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import com.mycompany.cinema_system_management.utils.DatabaseConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class BookingSeatView extends JFrame {

    private String currentUsername;
    private String tenPhim;
    private String thongTinSuat;
    private String tenPhong;
    private String hinhAnh;

    private List<String> danhSachGheDB; 
    private List<String> selectedSeatsList = new ArrayList<>(); 
    private int selectedSeatsCount = 0;
    
    // UI Elements cập nhật realtime
    private JLabel lblTotalValue;
    private JLabel lblTicketCount;
    private JLabel lblTicketPriceValue; // Đã thêm biến để update giá tiền từng dòng
    private JLabel lblMSeats;
    
    private DecimalFormat vndFormat = new DecimalFormat("#,### đ");

    public BookingSeatView(String username, String tenPhim, String thongTinSuat, String tenPhong, String hinhAnh) {
        this.currentUsername = username;
        this.tenPhim = tenPhim;
        this.thongTinSuat = thongTinSuat;
        this.tenPhong = tenPhong;
        this.hinhAnh = hinhAnh; 

        fetchSeatsFromDB();

        FlatLightLaf.setup();
        setTitle("CineMarket - Select Seats");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        getContentPane().setBackground(new Color(248, 250, 252));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);
        mainPanel.add(createNavbar(), BorderLayout.NORTH);

        JPanel contentWrapper = new JPanel(new BorderLayout(40, 20));
        contentWrapper.setOpaque(false);
        contentWrapper.setBorder(new EmptyBorder(20, 60, 40, 60));

        contentWrapper.add(createTopHeader(), BorderLayout.NORTH);
        
        contentWrapper.add(createSeatMapPanel(), BorderLayout.CENTER);
        contentWrapper.add(createSummaryPanel(), BorderLayout.EAST);

        mainPanel.add(contentWrapper, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    private void fetchSeatsFromDB() {
        danhSachGheDB = new ArrayList<>();
        String sql = "SELECT g.TENGHE FROM GHENGOI g JOIN PHONGCHIEU p ON g.MAPHONG = p.MAPHONG WHERE p.TENPHONG = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, this.tenPhong);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                danhSachGheDB.add(rs.getString("TENGHE"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối Database khi tải ghế!");
        }
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
        nav.add(createNavLink("Movies", true));
        nav.add(Box.createRigidArea(new Dimension(20, 0)));
        nav.add(createNavLink("Marketplace", false));
        nav.add(Box.createHorizontalGlue());

        JLabel lblAccount = new JLabel("Account: " + currentUsername);
        lblAccount.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nav.add(lblAccount);

        return nav;
    }

    private JLabel createNavLink(String text, boolean isActive) {
        JLabel link = new JLabel(text);
        link.setFont(new Font("Segoe UI", isActive ? Font.BOLD : Font.PLAIN, 14));
        link.setForeground(isActive ? new Color(37, 99, 235) : Color.GRAY);
        link.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return link;
    }

    private JPanel createTopHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel leftContent = new JPanel();
        leftContent.setLayout(new BoxLayout(leftContent, BoxLayout.Y_AXIS));
        leftContent.setOpaque(false);

        JButton btnBack = new JButton("<- Back");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBack.setForeground(new Color(2, 62, 138));
        btnBack.setBackground(new Color(239, 246, 255));
        btnBack.putClientProperty(FlatClientProperties.STYLE, "arc: 15; borderWidth: 0; focusWidth: 0; margin: 4,12,4,12;");
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnBack.addActionListener(e -> {
            new MovieDetailView(currentUsername, tenPhim, hinhAnh).setVisible(true);
            dispose();
        });

        leftContent.add(btnBack);
        leftContent.add(Box.createVerticalStrut(15));

        JLabel lblTitle = new JLabel("Select Seats");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblSub = new JLabel(tenPhim + " • " + thongTinSuat);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblSub.setForeground(new Color(100, 116, 139));
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        leftContent.add(lblTitle);
        leftContent.add(lblSub);

        JPanel stepper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        stepper.setOpaque(false);
        stepper.add(createStepIndicator("1", "Seats", true));
        stepper.add(createLine());
        stepper.add(createStepIndicator("2", "Snacks", false));
        stepper.add(createLine());
        stepper.add(createStepIndicator("3", "Payment", false));

        header.add(leftContent, BorderLayout.WEST);
        header.add(stepper, BorderLayout.EAST);
        return header;
    }

    private JPanel createLine() {
        JPanel line = new JPanel();
        line.setPreferredSize(new Dimension(50, 2));
        line.setBackground(new Color(226, 232, 240));
        return line;
    }

    private JPanel createStepIndicator(String number, String text, boolean isActive) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        p.setOpaque(false);

        JLabel lblNum = new JLabel(number, SwingConstants.CENTER);
        lblNum.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblNum.setOpaque(true);
        lblNum.setPreferredSize(new Dimension(24, 24));
        
        if (isActive) {
            lblNum.setBackground(new Color(13, 71, 161)); 
            lblNum.setForeground(Color.WHITE);
        } else {
            lblNum.setBackground(Color.WHITE); 
            lblNum.setForeground(new Color(148, 163, 184));
            p.putClientProperty(FlatClientProperties.STYLE, "borderWidth: 1; borderColor: #E2E8F0;");
        }
        lblNum.putClientProperty(FlatClientProperties.STYLE, "arc: 999;");

        JLabel lblText = new JLabel(text);
        lblText.setFont(new Font("Segoe UI", isActive ? Font.BOLD : Font.PLAIN, 13));
        lblText.setForeground(isActive ? new Color(15, 23, 42) : new Color(148, 163, 184));

        p.add(lblNum); p.add(lblText);
        return p;
    }

    // =========================================================
    // SƠ ĐỒ GHẾ (Đã dọn dẹp sạch sẽ dòng số)
    // =========================================================
    private JPanel createSeatMapPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.putClientProperty(FlatClientProperties.STYLE, "arc: 12; borderWidth: 1; borderColor: #E2E8F0;");
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));

        JPanel screenPanel = new JPanel();
        screenPanel.setLayout(new BoxLayout(screenPanel, BoxLayout.Y_AXIS));
        screenPanel.setOpaque(false);
        screenPanel.setBorder(new EmptyBorder(0, 0, 30, 0));

        JPanel screenLine = new JPanel();
        screenLine.setPreferredSize(new Dimension(0, 4));
        screenLine.setMaximumSize(new Dimension(300, 4)); 
        screenLine.setBackground(new Color(203, 213, 225));
        screenLine.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblScreen = new JLabel("SCREEN", SwingConstants.CENTER);
        lblScreen.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblScreen.setForeground(new Color(148, 163, 184));
        lblScreen.setBorder(new EmptyBorder(10, 0, 10, 0));
        lblScreen.setAlignmentX(Component.CENTER_ALIGNMENT);

        screenPanel.add(screenLine);
        screenPanel.add(lblScreen);
        panel.add(screenPanel, BorderLayout.NORTH);

        JPanel seatGrid = new JPanel(new GridLayout(4, 15, 12, 12)); 
        seatGrid.setOpaque(false);

        String[] rows = {"A", "B", "C", "D"};
        for (String row : rows) {
            JLabel lblRow = new JLabel(row, SwingConstants.CENTER);
            lblRow.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblRow.setForeground(new Color(148, 163, 184));
            seatGrid.add(lblRow);

            for (int i = 1; i <= 14; i++) {
                if (i == 4 || i == 11) { 
                    seatGrid.add(new JLabel());
                    continue;
                }

                String tenGheHT = row + i;
                JToggleButton btnSeat = new JToggleButton();
                btnSeat.setPreferredSize(new Dimension(38, 38));
                
                if (!danhSachGheDB.contains(tenGheHT)) {
                    btnSeat.setVisible(false);
                } else {
                    btnSeat.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    btnSeat.putClientProperty(FlatClientProperties.STYLE, "arc: 8; background: #FFFFFF; borderColor: #CBD5E1; borderWidth: 1; selectedBackground: #023E8A;");
                    
                    btnSeat.addActionListener(e -> {
                        if (btnSeat.isSelected()) {
                            selectedSeatsCount++;
                            selectedSeatsList.add(tenGheHT); 
                        } else {
                            selectedSeatsCount--;
                            selectedSeatsList.remove(tenGheHT);
                        }
                        updateOrderSummary();
                    });
                }
                seatGrid.add(btnSeat);
            }
        }

        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        legendPanel.setOpaque(false);
        legendPanel.setBorder(new EmptyBorder(40, 0, 0, 0));
        legendPanel.add(createLegendItem("Available", new Color(255, 255, 255), new Color(203, 213, 225)));
        legendPanel.add(createLegendItem("Selected", new Color(2, 62, 138), new Color(2, 62, 138)));
        legendPanel.add(createLegendItem("Occupied", new Color(226, 232, 240), new Color(226, 232, 240)));
        
        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerWrapper.setOpaque(false);
        centerWrapper.add(seatGrid);
        panel.add(centerWrapper, BorderLayout.CENTER);
        panel.add(legendPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createLegendItem(String text, Color bgColor, Color borderColor) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        p.setOpaque(false);
        JLabel box = new JLabel();
        box.setOpaque(true);
        box.setBackground(bgColor);
        box.setPreferredSize(new Dimension(20, 20));
        box.setBorder(BorderFactory.createLineBorder(borderColor));
        
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        p.add(box); p.add(lbl);
        return p;
    }

    private JPanel createSummaryPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.setBorder(new EmptyBorder(0, 30, 0, 0)); 
        container.setPreferredSize(new Dimension(380, 0)); 

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JPanel summary = new JPanel(new BorderLayout());
        summary.setBackground(Color.WHITE);
        summary.putClientProperty(FlatClientProperties.STYLE, "arc: 12; borderWidth: 1; borderColor: #E2E8F0;");
        summary.setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel summaryInner = new JPanel();
        summaryInner.setLayout(new BoxLayout(summaryInner, BoxLayout.Y_AXIS));
        summaryInner.setOpaque(false);

        JLabel lblTitle = new JLabel("Order Summary");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        summaryInner.add(lblTitle);
        summaryInner.add(Box.createVerticalStrut(20));

        JPanel movieInfo = new JPanel(new BorderLayout(15, 0));
        movieInfo.setOpaque(false);
        
        JLabel lblPoster = new JLabel();
        lblPoster.setPreferredSize(new Dimension(60, 85));
        
        // --- CHỖ LOAD ẢNH ---
        try {
            // Check đường dẫn nếu bị lỗi null
            System.out.println("Đang tìm ảnh tại: /images/" + hinhAnh);
            URL url = getClass().getResource("/images/" + hinhAnh); 
            if (url != null) {
                lblPoster.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(60, 85, Image.SCALE_SMOOTH)));
            } else {
                lblPoster.setOpaque(true);
                lblPoster.setBackground(Color.LIGHT_GRAY); 
            }
        } catch (Exception e) {}
        lblPoster.putClientProperty(FlatClientProperties.STYLE, "arc: 8");

        JPanel textInfo = new JPanel(new GridLayout(3, 1));
        textInfo.setOpaque(false);
        JLabel lblMName = new JLabel(tenPhim);
        lblMName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        lblMSeats = new JLabel("Please select seats");
        lblMSeats.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblMSeats.setForeground(new Color(100, 116, 139));
        
        JLabel lblMTime = new JLabel(thongTinSuat);
        lblMTime.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblMTime.setForeground(new Color(100, 116, 139));
        
        textInfo.add(lblMName); textInfo.add(lblMSeats); textInfo.add(lblMTime);
        movieInfo.add(lblPoster, BorderLayout.WEST);
        movieInfo.add(textInfo, BorderLayout.CENTER);
        
        summaryInner.add(movieInfo);
        summaryInner.add(Box.createVerticalStrut(25));

        // Khởi tạo các label tính tiền
        lblTicketCount = new JLabel("Tickets (0x)");
        lblTicketPriceValue = new JLabel("0 đ"); // FIX: Đã tạo biến cho giá tiền từng dòng
        lblTotalValue = new JLabel("0 đ"); 
        
        summaryInner.add(createBillRow(lblTicketCount, lblTicketPriceValue, false)); // FIX: Truyền biến vào đây
        summaryInner.add(Box.createVerticalStrut(10));
        
        JPanel divider = new JPanel();
        divider.setPreferredSize(new Dimension(0, 1));
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        divider.setBackground(new Color(226, 232, 240));
        summaryInner.add(divider);
        summaryInner.add(Box.createVerticalStrut(20));

        summaryInner.add(createBillRow(new JLabel("Total Price"), lblTotalValue, true));
        summaryInner.add(Box.createVerticalStrut(25));

        JButton btnNext = new JButton("Next: Snacks ->");
        btnNext.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnNext.setBackground(new Color(13, 71, 161)); 
        btnNext.setForeground(Color.WHITE);
        btnNext.setPreferredSize(new Dimension(0, 45));
        btnNext.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnNext.putClientProperty(FlatClientProperties.STYLE, "arc: 8; borderWidth: 0; focusWidth: 0;");
        btnNext.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnNext.addActionListener(e -> {
            if(selectedSeatsCount == 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất 1 ghế để tiếp tục!");
                return;
            }
            new BookingSnackView(currentUsername, tenPhim).setVisible(true);
            dispose();
        });

        summaryInner.add(btnNext);
        summary.add(summaryInner, BorderLayout.CENTER);
        content.add(summary);
        
        content.add(Box.createVerticalStrut(20));

        JPanel locPanel = new JPanel(new BorderLayout(15, 0));
        locPanel.setBackground(Color.WHITE);
        locPanel.putClientProperty(FlatClientProperties.STYLE, "arc: 12; borderWidth: 1; borderColor: #E2E8F0;");
        locPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        locPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel lblLocIcon = new JLabel("📍"); 
        lblLocIcon.setFont(new Font("Segoe UI", Font.PLAIN, 24));

        JPanel locText = new JPanel(new GridLayout(2, 1));
        locText.setOpaque(false);
        JLabel lblLocName = new JLabel(tenPhong + " - CineMarket Official"); 
        lblLocName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JLabel lblLocAdd = new JLabel("450 Enterprise Way, Floor 4");
        lblLocAdd.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblLocAdd.setForeground(new Color(100, 116, 139));

        locText.add(lblLocName);
        locText.add(lblLocAdd);

        locPanel.add(lblLocIcon, BorderLayout.WEST);
        locPanel.add(locText, BorderLayout.CENTER);

        content.add(locPanel);
        content.add(Box.createVerticalGlue());

        container.add(content, BorderLayout.CENTER);
        return container;
    }

    private JPanel createBillRow(JLabel left, JLabel right, boolean isTotal) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        left.setFont(new Font("Segoe UI", isTotal ? Font.BOLD : Font.PLAIN, isTotal ? 16 : 14));
        right.setFont(new Font("Segoe UI", isTotal ? Font.BOLD : Font.PLAIN, isTotal ? 16 : 14));
        if(isTotal) {
            left.setForeground(new Color(15, 23, 42));
            right.setForeground(new Color(13, 71, 161));
        } else {
            left.setForeground(new Color(71, 85, 105));
            right.setForeground(new Color(100, 116, 139));
        }
        p.add(left, BorderLayout.WEST);
        p.add(right, BorderLayout.EAST);
        return p;
    }

    private void updateOrderSummary() {
        lblTicketCount.setText("Tickets (" + selectedSeatsCount + "x)");
        
        double ticketPriceVND = 110000.0; 
        double totalVND = selectedSeatsCount * ticketPriceVND;
        
        // Cập nhật giá trị VND cho cả 2 label
        lblTicketPriceValue.setText(vndFormat.format(totalVND)); 
        lblTotalValue.setText(vndFormat.format(totalVND));
        
        if (selectedSeatsList.isEmpty()) {
            lblMSeats.setText("Please select seats");
        } else {
            lblMSeats.setText("Seat: " + String.join(", ", selectedSeatsList));
        }
    }
}