package com.mycompany.cinema_system_management.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import com.mycompany.cinema_system_management.dao.TaiKhoanDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MyTicketsView extends JFrame {

    private String currentUsername;
    private JTable table;

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

        // --- 1. HEADER ---
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

        // --- 2. LẤY DỮ LIỆU THỰC ---
        TaiKhoanDAO dao = new TaiKhoanDAO();
        List<Object[]> dbData = dao.getDanhSachVeCuaKhachHang(currentUsername);
        
        if (dbData.isEmpty()) {
            System.out.println("DEBUG VIEW: Khong co du lieu de hien thi!");
        }

        JPanel contentWrapper = new JPanel(new BorderLayout(0, 35));
        contentWrapper.setOpaque(false);

        // Stats Cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 25, 0));
        statsPanel.setOpaque(false);
        statsPanel.setPreferredSize(new Dimension(0, 130));
        statsPanel.add(createStatCard("TICKETS ĐANG CÓ", String.format("%02d", dbData.size()), "/images/icon_ticket.png"));
        statsPanel.add(createStatCard("ĐANG RAO BÁN", "00", "/images/icon_market.png"));
        statsPanel.add(createStatCard("SỐ DƯ VÍ", "1.250.000 đ", "/images/icon_wallet.png"));
        
        contentWrapper.add(statsPanel, BorderLayout.NORTH);
        
        // --- 3. TẠO BẢNG VÀ GÁN DỮ LIỆU ---
        contentWrapper.add(createTableSection(dbData), BorderLayout.CENTER);
        
        mainPanel.add(contentWrapper, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    private JPanel createTableSection(List<Object[]> dbData) {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.putClientProperty(FlatClientProperties.STYLE, "arc: 12; borderWidth: 1; borderColor: #E2E8F0");

        String[] columns = {"THÔNG TIN PHIM", "XUẤT CHIẾU & RẠP", "VỊ TRÍ GHẾ", "GIÁ VÉ", "TRẠNG THÁI"};
        Object[][] data = dbData.toArray(new Object[0][]);

        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(110); 
        table.setShowGrid(false);
        table.setSelectionBackground(new Color(241, 245, 249));

        // Renderer Cột 0: Ảnh và Tên phim
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
                        if (url != null) {
                            lblImg.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(55, 75, Image.SCALE_SMOOTH)));
                        } else {
                            lblImg.setOpaque(true);
                            lblImg.setBackground(new Color(230, 230, 230));
                        }
                    } catch (Exception e) {}
                    lblImg.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
                    JLabel lblName = new JLabel((String) cellData[0]);
                    lblName.setFont(new Font("Segoe UI", Font.BOLD, 15));
                    cell.add(lblImg); cell.add(lblName);
                }
                return cell;
            }
        });

        // Renderer Cột 4: Trạng thái (Xanh/Đỏ)
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSel, boolean hasF, int row, int col) {
                try {
                    String rowText = table.getValueAt(row, 1).toString();
                    String fullDateTime = rowText.split(" \\| ")[0];
                    boolean isExpired = checkExpired(fullDateTime);
                    
                    JLabel tag = new JLabel(isExpired ? " Hết hạn " : " Còn hạn ", SwingConstants.CENTER);
                    tag.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    tag.setOpaque(true);
                    tag.setForeground(isExpired ? new Color(220, 38, 38) : new Color(22, 163, 74));
                    tag.setBackground(isExpired ? new Color(254, 226, 226) : new Color(220, 252, 231));
                    tag.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
                    
                    JPanel p = new JPanel(new GridBagLayout());
                    p.setBackground(isSel ? table.getSelectionBackground() : Color.WHITE);
                    p.add(tag);
                    return p;
                } catch (Exception e) {
                    return new JLabel("Lỗi định dạng");
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        container.add(scrollPane, BorderLayout.CENTER);
        return container;
    }

    private JPanel createStatCard(String title, String value, String iconPath) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 15; borderWidth: 1; borderColor: #E2E8F0");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST; gbc.insets = new Insets(0, 25, 0, 0);
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        JLabel lblT = new JLabel(title); lblT.setFont(new Font("Segoe UI", Font.BOLD, 12)); lblT.setForeground(Color.GRAY);
        JLabel lblV = new JLabel(value); lblV.setFont(new Font("Segoe UI", Font.BOLD, 28));
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

    private boolean checkExpired(String showtime) {
        try {
            DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
            return LocalDateTime.parse(showtime, f).isBefore(LocalDateTime.now());
        } catch (Exception e) {
            return false;
        }
    }
}