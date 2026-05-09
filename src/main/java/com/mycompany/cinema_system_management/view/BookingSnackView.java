
package com.mycompany.cinema_system_management.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;

public class BookingSnackView extends JFrame {

    private String currentUsername;
    private String tenPhim;

    public BookingSnackView(String username, String tenPhim) {
        this.currentUsername = username;
        this.tenPhim = tenPhim;

        FlatLightLaf.setup();
        setTitle("CineMarket - Choose Snacks");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        getContentPane().setBackground(new Color(248, 250, 252));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);

        // 1. NAVBAR
        mainPanel.add(createNavbar(), BorderLayout.NORTH);

        // 2. NỘI DUNG CHÍNH (Stepper + Bắp Nước + Bill)
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setOpaque(false);
        contentWrapper.setBorder(new EmptyBorder(20, 60, 40, 60));

        // Stepper (1. Seats -> 2. Snacks -> 3. Payment)
        contentWrapper.add(createStepper(), BorderLayout.NORTH);

        // Chia 2 cột: Trái (Danh sách món) - Phải (Bill)
        JPanel splitPanel = new JPanel(new BorderLayout(40, 0));
        splitPanel.setOpaque(false);
        splitPanel.setBorder(new EmptyBorder(30, 0, 0, 0));

        splitPanel.add(createSnacksPanel(), BorderLayout.CENTER);
        splitPanel.add(createSummaryPanel(), BorderLayout.EAST);

        contentWrapper.add(splitPanel, BorderLayout.CENTER);
        mainPanel.add(contentWrapper, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    // =========================================================================
    // NAVBAR 
    // =========================================================================
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
        nav.add(Box.createRigidArea(new Dimension(20, 0)));
        nav.add(createNavLink("My Tickets", false));
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

    // =========================================================================
    // THANH TIẾN TRÌNH (STEPPER)
    // =========================================================================
    private JPanel createStepper() {
        JPanel stepper = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        stepper.setOpaque(false);

        stepper.add(createStepIndicator("1", "SEATS", false));
        
        JPanel line = new JPanel();
        line.setPreferredSize(new Dimension(100, 2));
        line.setBackground(new Color(226, 232, 240));
        stepper.add(line);

        stepper.add(createStepIndicator("2", "SNACKS", true));

        JPanel line2 = new JPanel();
        line2.setPreferredSize(new Dimension(100, 2));
        line2.setBackground(new Color(226, 232, 240));
        stepper.add(line2);

        stepper.add(createStepIndicator("3", "PAYMENT", false));

        return stepper;
    }

    private JPanel createStepIndicator(String number, String text, boolean isActive) {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setOpaque(false);

        JLabel lblNum = new JLabel(number, SwingConstants.CENTER);
        lblNum.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNum.setOpaque(true);
        lblNum.setPreferredSize(new Dimension(32, 32));
        
        if (isActive) {
            lblNum.setBackground(new Color(37, 99, 235)); 
            lblNum.setForeground(Color.WHITE);
            lblNum.putClientProperty(FlatClientProperties.STYLE, "arc: 999;");
        } else {
            lblNum.setBackground(new Color(241, 245, 249)); 
            lblNum.setForeground(new Color(148, 163, 184));
            lblNum.putClientProperty(FlatClientProperties.STYLE, "arc: 999;");
        }

        JPanel numWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        numWrapper.setOpaque(false);
        numWrapper.add(lblNum);

        JLabel lblText = new JLabel(text, SwingConstants.CENTER);
        lblText.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblText.setForeground(isActive ? new Color(37, 99, 235) : new Color(148, 163, 184));

        p.add(numWrapper, BorderLayout.CENTER);
        p.add(lblText, BorderLayout.SOUTH);
        return p;
    }

    // =========================================================================
    // CỘT TRÁI: DANH SÁCH SNACKS
    // =========================================================================
    private JPanel createSnacksPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setOpaque(false);

        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setOpaque(false);
        JLabel lblTitle = new JLabel("Enhance your experience");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        JLabel lblSub = new JLabel("Skip the line and pre-order your refreshments now.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblSub.setForeground(new Color(100, 116, 139));
        header.add(lblTitle);
        header.add(lblSub);
        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        filterPanel.setOpaque(false);
        filterPanel.setBorder(new EmptyBorder(15, 0, 15, 0));
        filterPanel.add(createFilterButton("All", true));
        filterPanel.add(createFilterButton("Combos", false));
        filterPanel.add(createFilterButton("Popcorn", false));
        filterPanel.add(createFilterButton("Drinks", false));

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setOpaque(false);
        topSection.add(header, BorderLayout.NORTH);
        topSection.add(filterPanel, BorderLayout.SOUTH);
        panel.add(topSection, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 2, 20, 20));
        grid.setOpaque(false);

        grid.add(createSnackCard("Classic Large Popcorn", "Buttered salty popcorn (130oz)", "$12.50", "popcorn.jpg"));
        grid.add(createSnackCard("Fountain Soda XL", "Choice of cola, lemon, or orange", "$7.00", "soda.jpg"));
        grid.add(createSnackCard("Artisan Chocolate", "72% Cacao Belgian Dark", "$9.50", "choco.jpg"));
        grid.add(createSnackCard("Gourmet Nachos", "With warm cheddar dip", "$14.00", "nacho.jpg"));

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JButton createFilterButton(String text, boolean isActive) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(90, 35));
        if (isActive) {
            btn.setBackground(new Color(15, 23, 42)); 
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(Color.WHITE);
            btn.setForeground(new Color(71, 85, 105));
        }
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 8; borderWidth: 1; borderColor: #E2E8F0; focusWidth: 0;");
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel createSnackCard(String name, String desc, String price, String imgPath) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(Color.WHITE);
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 12; borderWidth: 1; borderColor: #E2E8F0;");
        card.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lblImg = new JLabel();
        lblImg.setPreferredSize(new Dimension(80, 80));
        try {
            URL url = getClass().getResource("/images/" + imgPath);
            if (url != null) lblImg.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
            else { lblImg.setOpaque(true); lblImg.setBackground(new Color(226, 232, 240)); }
        } catch (Exception e) {}
        lblImg.putClientProperty(FlatClientProperties.STYLE, "arc: 8");

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel lblName = new JLabel(name);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 15));
        JLabel lblDesc = new JLabel(desc);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc.setForeground(new Color(100, 116, 139));
        
        info.add(lblName);
        info.add(Box.createVerticalStrut(5));
        info.add(lblDesc);

        JPanel bottomInfo = new JPanel(new BorderLayout());
        bottomInfo.setOpaque(false);
        bottomInfo.setBorder(new EmptyBorder(15, 0, 0, 0));

        JLabel lblPrice = new JLabel(price);
        lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JPanel counter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        counter.setOpaque(false);
        JButton btnMinus = new JButton("-");
        JLabel lblCount = new JLabel("0");
        JButton btnPlus = new JButton("+");
        
        btnMinus.setPreferredSize(new Dimension(30, 30));
        btnPlus.setPreferredSize(new Dimension(30, 30));
        lblCount.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        String btnStyle = "arc: 8; borderWidth: 1; borderColor: #E2E8F0; background: #F8FAFC; focusWidth: 0;";
        btnMinus.putClientProperty(FlatClientProperties.STYLE, btnStyle);
        btnPlus.putClientProperty(FlatClientProperties.STYLE, btnStyle);

        counter.add(btnMinus); counter.add(lblCount); counter.add(btnPlus);

        bottomInfo.add(lblPrice, BorderLayout.WEST);
        bottomInfo.add(counter, BorderLayout.EAST);
        info.add(bottomInfo);

        card.add(lblImg, BorderLayout.WEST);
        card.add(info, BorderLayout.CENTER);
        return card;
    }

    // =========================================================================
    // CỘT PHẢI: BOOKING SUMMARY (BILL)
    // =========================================================================
    private JPanel createSummaryPanel() {
        JPanel summary = new JPanel(new BorderLayout());
        summary.setBackground(Color.WHITE);
        summary.setPreferredSize(new Dimension(380, 0));
        summary.putClientProperty(FlatClientProperties.STYLE, "arc: 15; borderWidth: 1; borderColor: #E2E8F0;");
        summary.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JLabel lblTitle = new JLabel("Booking Summary");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        JLabel lblMovie = new JLabel((tenPhim + " • STANDARD 2D").toUpperCase());
        lblMovie.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblMovie.setForeground(new Color(100, 116, 139));
        
        content.add(lblTitle);
        content.add(Box.createVerticalStrut(5));
        content.add(lblMovie);
        content.add(Box.createVerticalStrut(25));

        content.add(createBillRow("Adult Standard", "$48.00", "Row G, Seat 14, 15", true));
        content.add(Box.createVerticalStrut(20));

        content.add(createBillRow("Fountain Soda XL x1", "$7.00", "", false));
        content.add(Box.createVerticalStrut(10));
        content.add(createBillRow("Gourmet Nachos x1", "$14.00", "", false));
        content.add(Box.createVerticalStrut(25));

        content.add(createBillRow("Subtotal", "$69.00", "", false));
        content.add(Box.createVerticalStrut(10));
        content.add(createBillRow("Processing Fee", "$2.50", "", false));
        content.add(Box.createVerticalStrut(20));

        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);
        JLabel lblTotalText = new JLabel("Total");
        lblTotalText.setFont(new Font("Segoe UI", Font.BOLD, 24));
        JLabel lblTotalVal = new JLabel("$71.50");
        lblTotalVal.setFont(new Font("Segoe UI", Font.BOLD, 24));
        totalRow.add(lblTotalText, BorderLayout.WEST);
        totalRow.add(lblTotalVal, BorderLayout.EAST);
        content.add(totalRow);
        
        summary.add(content, BorderLayout.NORTH);

        JPanel bottomAction = new JPanel(new GridLayout(2, 1, 0, 15));
        bottomAction.setOpaque(false);
        
        JButton btnNext = new JButton("Next: Checkout ->");
        btnNext.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnNext.setBackground(new Color(13, 71, 161)); 
        btnNext.setForeground(Color.WHITE);
        btnNext.setPreferredSize(new Dimension(0, 45));
        btnNext.putClientProperty(FlatClientProperties.STYLE, "arc: 8; borderWidth: 0; focusWidth: 0;");
        btnNext.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnBack = new JButton("Go Back to Seats");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBack.setForeground(new Color(100, 116, 139));
        btnBack.setContentAreaFilled(false);
        btnBack.setBorderPainted(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> {
            dispose(); 
        });

        bottomAction.add(btnNext);
        bottomAction.add(btnBack);
        
        summary.add(bottomAction, BorderLayout.SOUTH);

        return summary;
    }

    private JPanel createBillRow(String title, String price, String subText, boolean isBold) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", isBold ? Font.BOLD : Font.PLAIN, 14));
        lblTitle.setForeground(isBold ? new Color(15, 23, 42) : new Color(71, 85, 105));
        
        JLabel lblPrice = new JLabel(price);
        lblPrice.setFont(new Font("Segoe UI", isBold ? Font.BOLD : Font.PLAIN, 14));
        lblPrice.setForeground(isBold ? new Color(15, 23, 42) : new Color(100, 116, 139));

        p.add(lblTitle, BorderLayout.WEST);
        p.add(lblPrice, BorderLayout.EAST);

        if (!subText.isEmpty()) {
            JLabel lblSub = new JLabel(subText);
            lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblSub.setForeground(new Color(148, 163, 184));
            p.add(lblSub, BorderLayout.SOUTH);
        }
        return p;
    }
}
