package com.mycompany.cinema_system_management.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import com.mycompany.cinema_system_management.dao.TaiKhoanDAO;
import com.mycompany.cinema_system_management.utils.DatabaseConnection;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MyTicketsView extends JFrame {

    private String currentUsername;
    public JTable table;
    private JLabel lblWalletBalance;
    private JLabel lblTicketCount;
    private JLabel lblMarketCount;

    public MyTicketsView(String username) {
        this.currentUsername = username;
        FlatLightLaf.setup();
        setTitle("CineMarket - My Ticket Assets");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        getContentPane().setBackground(new Color(248, 250, 252));

        JPanel mainPanel = new JPanel(new BorderLayout(0, 30));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(40, 60, 40, 60));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JButton btnBack = new JButton("<- Back");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnBack.setForeground(new Color(2, 62, 138));
        btnBack.setBackground(new Color(239, 246, 255));
        btnBack.putClientProperty(FlatClientProperties.STYLE, "arc: 25; borderWidth: 1; borderColor: #BFDBFE; focusWidth: 0;");
        btnBack.setPreferredSize(new Dimension(110, 45));
        btnBack.addActionListener(e -> {
            new CustomerHome(currentUsername).setVisible(true);
            dispose();
        });
        
        JLabel lblTitle = new JLabel("My Ticket Assets");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTitle.setBorder(new EmptyBorder(0, 25, 0, 0));
        
        JPanel accountPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        accountPanel.setOpaque(false);
        JLabel lblUser = new JLabel(username); 
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JLabel lblIcon = new JLabel("👤", SwingConstants.CENTER);
        lblIcon.setPreferredSize(new Dimension(45, 45));
        lblIcon.setOpaque(true);
        lblIcon.setBackground(Color.WHITE);
        lblIcon.putClientProperty(FlatClientProperties.STYLE, "arc: 999; borderWidth: 1; borderColor: #E2E8F0");

        accountPanel.add(lblUser); accountPanel.add(lblIcon);
        headerPanel.add(btnBack, BorderLayout.WEST);
        headerPanel.add(lblTitle, BorderLayout.CENTER);
        headerPanel.add(accountPanel, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        TaiKhoanDAO dao = new TaiKhoanDAO();
        List<Object[]> dbData = dao.getDanhSachVeCuaKhachHang(currentUsername);

        int sellingCount = 0;
        for (Object[] row : dbData) {
            String status = row[4] != null ? row[4].toString().trim() : "";
            
            // Xử lý lấy thời gian hết hạn để đánh giá lại trạng thái thực tế
            String showtimeStr = row[1].toString().split(" \\| ")[0].replaceAll("<[^>]*>", "").trim();
            if (checkExpired(showtimeStr) && !"Đã đổi chủ".equalsIgnoreCase(status)) {
                status = "Đã hết hạn";
            }
            
            if ("Đang rao bán".equals(status)) {
                sellingCount++;
            }
        }

        lblTicketCount = new JLabel(String.format("%02d", dbData.size()));
        lblMarketCount = new JLabel(String.format("%02d", sellingCount));

        JPanel contentWrapper = new JPanel(new BorderLayout(0, 35));
        contentWrapper.setOpaque(false);

        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 25, 0));
        statsPanel.setOpaque(false);
        statsPanel.setPreferredSize(new Dimension(0, 130));
        statsPanel.add(createStatCard("TICKETS ĐANG CÓ", lblTicketCount, "/images/icon_ticket.png"));
        statsPanel.add(createStatCard("ĐANG RAO BÁN", lblMarketCount, "/images/icon_market.png"));
        statsPanel.add(createWalletCard("SỐ DƯ VÍ", "0 đ", "/images/icon_wallet.png")); 
        
        contentWrapper.add(statsPanel, BorderLayout.NORTH);
        contentWrapper.add(createTableSection(dbData), BorderLayout.CENTER);
        
        mainPanel.add(contentWrapper, BorderLayout.CENTER);
        setContentPane(mainPanel);

        loadSoDuViTuDB();
    }

    public void updateTicketStatusUI(int row, String newStatus) {
        table.setValueAt(newStatus, row, 5); // Cột trạng thái giờ là index 5
        table.repaint();

        int sellingCount = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            String status = (String) table.getValueAt(i, 5);
            if (status != null && status.trim().equals("Đang rao bán")) {
                sellingCount++;
            }
        }
        lblMarketCount.setText(String.format("%02d", sellingCount));
    }

    public void loadSoDuViTuDB() {
        String sql = "SELECT k.SODUVI FROM KHACHHANG k JOIN TAIKHOAN t ON k.MATK = t.MATK WHERE t.USERNAME = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, currentUsername);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double soDu = rs.getDouble("SODUVI");
                DecimalFormat df = new DecimalFormat("#,### đ");
                lblWalletBalance.setText(df.format(soDu)); 
            }
        } catch (Exception e) {}
    }

    private JPanel createTableSection(List<Object[]> dbData) {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.putClientProperty(FlatClientProperties.STYLE, "arc: 12; borderWidth: 1; borderColor: #E2E8F0");

        // Đã thêm cột HẠN SỬ DỤNG
        String[] columns = {"THÔNG TIN PHIM", "XUẤT CHIẾU & RẠP", "VỊ TRÍ GHẾ", "GIÁ VÉ", "HẠN SỬ DỤNG", "TRẠNG THÁI"};
        Object[][] data = new Object[dbData.size()][6];
        
        for (int i = 0; i < dbData.size(); i++) {
            Object[] row = dbData.get(i);
            data[i][0] = row[0];
            data[i][1] = row[1];
            data[i][2] = row[2];
            data[i][3] = row[3];
            
            // Tách thời gian chiếu ra làm hạn sử dụng
            String expireTime = row[1].toString().split(" \\| ")[0].replaceAll("<[^>]*>", "").trim();
            data[i][4] = expireTime;
            
            // Xử lý đè trạng thái nếu vé đã hết hạn
            String currentStatus = row[4] != null ? row[4].toString().trim() : "";
            if (checkExpired(expireTime)) {
                if (!"Đã đổi chủ".equalsIgnoreCase(currentStatus)) {
                    currentStatus = "Đã hết hạn";
                }
            }
            data[i][5] = currentStatus;
        }

        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(110); 
        table.setShowGrid(false);
        table.setSelectionBackground(new Color(241, 245, 249));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        
        ((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        table.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSel, boolean hasF, int row, int col) {
                JPanel cell = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 18)); 
                cell.setBackground(isSel ? table.getSelectionBackground() : Color.WHITE);
                if (value instanceof Object[]) {
                    Object[] cellData = (Object[]) value;
                    JLabel lblImg = new JLabel();
                    lblImg.setPreferredSize(new Dimension(55, 75));
                    try {
                        URL url = getClass().getResource("/images/" + cellData[1]);
                        if (url != null) lblImg.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(55, 75, Image.SCALE_SMOOTH)));
                        else { lblImg.setOpaque(true); lblImg.setBackground(new Color(230, 230, 230)); }
                    } catch (Exception e) {}
                    lblImg.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
                    JLabel lblName = new JLabel((String) cellData[0]);
                    lblName.setFont(new Font("Segoe UI", Font.BOLD, 15));
                    cell.add(lblImg); cell.add(lblName);
                }
                return cell;
            }
        });

        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSel, boolean hasF, int row, int col) {
                try {
                    String statusText = value != null ? value.toString().trim() : "";
                    
                    String textDisplay = statusText;
                    Color bgColor = new Color(220, 252, 231); 
                    Color fgColor = new Color(22, 163, 74);   
                    
                    if ("Đã hết hạn".equalsIgnoreCase(statusText)) {
                        bgColor = new Color(254, 226, 226); 
                        fgColor = new Color(220, 38, 38);   
                    } else if ("Đang rao bán".equalsIgnoreCase(statusText)) {
                        bgColor = new Color(255, 237, 213); 
                        fgColor = new Color(234, 88, 12);   
                    } else if ("Đã đổi chủ".equalsIgnoreCase(statusText)) {
                        bgColor = new Color(241, 245, 249);
                        fgColor = new Color(100, 116, 139);
                    }

                    JLabel tag = new JLabel(" " + textDisplay + " ", SwingConstants.CENTER);
                    tag.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    tag.setOpaque(true);
                    tag.setForeground(fgColor);
                    tag.setBackground(bgColor);
                    tag.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 4,8,4,8;");
                    
                    JPanel p = new JPanel(new GridBagLayout()); 
                    p.setBackground(isSel ? table.getSelectionBackground() : Color.WHITE);
                    p.add(tag);
                    return p;
                } catch (Exception e) {
                    return new JLabel("Lỗi");
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        container.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        bottomActionPanel.setOpaque(false);
        bottomActionPanel.setBorder(new EmptyBorder(5, 0, 0, 0));

        JButton btnSellTicket = new JButton("Thao tác / Đăng bán");
        btnSellTicket.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSellTicket.setBackground(new Color(15, 23, 42)); 
        btnSellTicket.setForeground(Color.WHITE);
        btnSellTicket.setPreferredSize(new Dimension(200, 42));
        btnSellTicket.putClientProperty(FlatClientProperties.STYLE, "arc: 8; borderWidth: 0; focusWidth: 0;");
        btnSellTicket.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnSellTicket.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 vé trong bảng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            } 
            
            // Cột trạng thái đã chuyển sang index 5
            String currentStatus = "";
            Object statusObj = table.getValueAt(selectedRow, 5);
            if (statusObj != null) {
                currentStatus = statusObj.toString().trim();
            }

            Object[] cellData = (Object[]) table.getValueAt(selectedRow, 0);
            String tenPhim = (String) cellData[0];
            String viTriGheRaw = (String) table.getValueAt(selectedRow, 2);
            String viTriGhe = viTriGheRaw.split(" ")[0]; 

            if ("Đã đổi chủ".equalsIgnoreCase(currentStatus)) {
                JOptionPane.showMessageDialog(this, "Vé đã đổi chủ, bạn không thể thao tác thêm!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if ("Đã hết hạn".equalsIgnoreCase(currentStatus)) {
                JOptionPane.showMessageDialog(this, "Vé đã quá thời gian chiếu, không thể thao tác!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if ("Đang rao bán".equalsIgnoreCase(currentStatus)) {
                int confirm = JOptionPane.showConfirmDialog(this, "Vé này đang được rao bán.\nBạn có muốn gỡ vé khỏi chợ không?", "Xác nhận gỡ vé", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (goVeKhoiCho(tenPhim, viTriGhe)) {
                        JOptionPane.showMessageDialog(this, "Đã gỡ vé thành công. Vé đã quay lại trạng thái Còn hạn.");
                        updateTicketStatusUI(selectedRow, "Còn hạn");
                    } else {
                        JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi gỡ vé!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
                return;
            }

            String hinhAnh = cellData.length > 1 ? (String) cellData[1] : "default.jpg"; 
            String thongTinSuat = (String) table.getValueAt(selectedRow, 1);
            String giaGoc = (String) table.getValueAt(selectedRow, 3);

            new SellTicketDialog(this, currentUsername, selectedRow, tenPhim, hinhAnh, thongTinSuat, viTriGhe, giaGoc).setVisible(true);
        });

        bottomActionPanel.add(btnSellTicket);
        container.add(bottomActionPanel, BorderLayout.SOUTH);

        return container;
    }

    private boolean goVeKhoiCho(String tenPhim, String viTriGhe) {
        String sqlGetIDs = "SELECT v.MAVE FROM VE v " +
                           "JOIN GHENGOI g ON v.MAGHE = g.MAGHE " +
                           "JOIN LICHCHIEU lc ON v.MALICHCHIEU = lc.MALICHCHIEU " +
                           "JOIN PHIM p ON lc.MAPHIM = p.MAPHIM " +
                           "JOIN KHACHHANG k ON v.MAKHACHHANG = k.MAKH " +
                           "JOIN TAIKHOAN t ON k.MATK = t.MATK " +
                           "WHERE t.USERNAME = ? AND p.TENPHIM = ? AND g.TENGHE = ?";
        String updateVe = "UPDATE VE SET TRANGTHAIVE = 1 WHERE MAVE = ?";
        String updatePass = "UPDATE TRANGPASSVE SET TRANGTHAI_PASS = 3 WHERE MAVE = ? AND TRANGTHAI_PASS = 1";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            int maVe = -1;
            try (PreparedStatement ps = conn.prepareStatement(sqlGetIDs)) {
                ps.setString(1, currentUsername);
                ps.setString(2, tenPhim);
                ps.setString(3, viTriGhe);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) maVe = rs.getInt("MAVE");
            }
            if (maVe != -1) {
                try (PreparedStatement ps = conn.prepareStatement(updateVe)) { 
                    ps.setInt(1, maVe); ps.executeUpdate(); 
                }
                try (PreparedStatement ps = conn.prepareStatement(updatePass)) { 
                    ps.setInt(1, maVe); ps.executeUpdate(); 
                }
                conn.commit();
                return true;
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return false;
    }

    private JPanel createStatCard(String title, JLabel lblV, String iconPath) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 15; borderWidth: 1; borderColor: #E2E8F0");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST; gbc.insets = new Insets(0, 25, 0, 0);
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        JLabel lblT = new JLabel(title); lblT.setFont(new Font("Segoe UI", Font.BOLD, 12)); lblT.setForeground(Color.GRAY);
        lblV.setFont(new Font("Segoe UI", Font.BOLD, 28));
        textPanel.add(lblT); textPanel.add(Box.createVerticalStrut(8)); textPanel.add(lblV);
        card.add(textPanel, gbc);

        gbc.gridx = 1; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST; gbc.insets = new Insets(0, 0, 0, 25);
        JLabel lblIcon = new JLabel();
        try {
            URL url = getClass().getResource(iconPath);
            if (url != null) lblIcon.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH)));
        } catch (Exception e) {}
        card.add(lblIcon, gbc);
        return card;
    }

    private JPanel createWalletCard(String title, String value, String iconPath) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 15; borderWidth: 1; borderColor: #E2E8F0");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST; gbc.insets = new Insets(0, 25, 0, 0);
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        JLabel lblT = new JLabel(title); lblT.setFont(new Font("Segoe UI", Font.BOLD, 12)); lblT.setForeground(Color.GRAY);
        
        lblWalletBalance = new JLabel(value); 
        lblWalletBalance.setFont(new Font("Segoe UI", Font.BOLD, 28));
        
        textPanel.add(lblT); textPanel.add(Box.createVerticalStrut(8)); textPanel.add(lblWalletBalance);
        card.add(textPanel, gbc);

        gbc.gridx = 1; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST; gbc.insets = new Insets(0, 0, 0, 20);
        JPanel rightAction = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightAction.setOpaque(false);

        JButton btnTopUp = new JButton("+ Nạp tiền");
        btnTopUp.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnTopUp.setBackground(new Color(239, 246, 255));
        btnTopUp.setForeground(new Color(2, 62, 138));
        btnTopUp.putClientProperty(FlatClientProperties.STYLE, "arc: 8; borderWidth: 0; focusWidth: 0; margin: 6,12,6,12;");
        btnTopUp.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnTopUp.addActionListener(e -> {
            new TopUpDialog(this, currentUsername, lblWalletBalance.getText()).setVisible(true);
        });

        JLabel lblIcon = new JLabel();
        try {
            URL url = getClass().getResource(iconPath);
            if (url != null) lblIcon.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH)));
        } catch (Exception e) {}

        rightAction.add(btnTopUp);
        rightAction.add(lblIcon);

        card.add(rightAction, gbc);
        return card;
    }

    // Đã vá lỗi Parsing. Hỗ trợ tự động nhận diện cả format có và không có phần milliseconds.
    private boolean checkExpired(String showtime) {
        try {
            DateTimeFormatter f;
            if (showtime.contains(".")) {
                f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
            } else {
                f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            }
            return LocalDateTime.parse(showtime.trim(), f).isBefore(LocalDateTime.now());
        } catch (Exception e) {
            return false;
        }
    }
}