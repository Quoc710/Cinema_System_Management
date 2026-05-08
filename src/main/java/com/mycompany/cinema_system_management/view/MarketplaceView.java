package com.mycompany.cinema_system_management.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.net.URL;

public class MarketplaceView extends JFrame {

    public MarketplaceView() {
        FlatLightLaf.setup();
        setTitle("CineMarket - Resale Marketplace");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 700); // Thu gọn chiều cao lại vì đã bỏ 3 khung dưới
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(30, 50, 30, 50));

        // --- 1. Header & Search ---
        JPanel topWrapper = new JPanel(new BorderLayout());
        topWrapper.setOpaque(false);

        JLabel lblTitle = new JLabel("Resale Marketplace");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionPanel.setOpaque(false);
        
        JTextField txtSearch = new JTextField(20);
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search assets...");
        txtSearch.putClientProperty(FlatClientProperties.STYLE, "arc: 15; focusWidth: 0");
        
        JButton btnFilter = new JButton("Filter");
        btnFilter.putClientProperty(FlatClientProperties.STYLE, "arc: 15; focusWidth: 0");
        
        actionPanel.add(txtSearch);
        actionPanel.add(btnFilter);

        topWrapper.add(lblTitle, BorderLayout.WEST);
        topWrapper.add(actionPanel, BorderLayout.EAST);
        mainPanel.add(topWrapper, BorderLayout.NORTH);

        // --- 2. Bảng dữ liệu ---
        String[] columns = {"Asset / Movie", "Showtime", "Seat / Tier", "Price", "Action"};
        Object[][] data = {
            {"Interstellar", "Oct 24, 2024", "Seat G-14 (Premium)", "100.000đ", "Buy Ticket"},
            {"The Dark Knight", "Oct 25, 2024", "Seat J-08 (Standard)", "90.000đ", "Buy Ticket"},
            {"Inception", "Oct 26, 2024", "Seat C-22 (Executive)", "130.000đ", "Buy Ticket"}
        };

        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(model);
        table.setRowHeight(100); // Tăng chiều cao lên chút cho thoáng
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(241, 245, 249));
        table.setSelectionBackground(new Color(248, 250, 252));
        table.setSelectionForeground(Color.BLACK);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setPreferredSize(new Dimension(0, 50));

        // Renderer cho Ảnh + Tên phim
        table.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JPanel cell = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
                cell.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
                
                // Load ảnh poster (đảm bảo file có trong resources/images)
                String imgName = "/images/poster_movie1.jpg"; 
                JLabel lblImg = new JLabel();
                lblImg.setPreferredSize(new Dimension(50, 70));
                try {
                    URL imgUrl = getClass().getResource(imgName);
                    if (imgUrl != null) {
                        ImageIcon icon = new ImageIcon(new ImageIcon(imgUrl).getImage().getScaledInstance(50, 70, Image.SCALE_SMOOTH));
                        lblImg.setIcon(icon);
                        lblImg.putClientProperty(FlatClientProperties.STYLE, "arc: 10"); // Bo góc ảnh
                    }
                } catch (Exception e) {}

                JLabel lblName = new JLabel(value.toString());
                lblName.setFont(new Font("Segoe UI", Font.BOLD, 15));
                
                cell.add(lblImg);
                cell.add(lblName);
                return cell;
            }
        });

        // --- Renderer cho NÚT BUY (Làm đẹp lại chỗ này) ---
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JPanel panel = new JPanel(new GridBagLayout());
                panel.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
                
                JButton btn = new JButton(value.toString());
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                // Màu xanh Royal Blue đậm chất công nghệ, bo góc 12px
                btn.setBackground(new Color(37, 99, 235));
                btn.setForeground(Color.WHITE);
                btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
                btn.putClientProperty(FlatClientProperties.STYLE, "arc: 12; borderWidth: 0; focusWidth: 0");
                btn.setPreferredSize(new Dimension(120, 35));
                
                panel.add(btn);
                return panel;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // --- ĐÃ XÓA FOOTER CŨ TẠI ĐÂY ---

        setContentPane(mainPanel);
    }
}