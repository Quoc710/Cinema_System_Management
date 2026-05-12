package com.mycompany.cinema_system_management.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import com.mycompany.cinema_system_management.dao.MarketplaceDAO;
import com.mycompany.cinema_system_management.models.TrangPassVe;
import com.mycompany.cinema_system_management.utils.DatabaseConnection;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Vector;
import java.util.List;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MarketplaceView extends JFrame {

    private String currentUsername;
    private MarketplaceDAO dao;
    private JTable table;
    private DefaultTableModel model;
    private List<TrangPassVe> listTickets;

    public MarketplaceView(String username) {
        this.currentUsername = username;
        this.dao = new MarketplaceDAO();
        
        FlatLightLaf.setup();
        setTitle("CineMarket - Resale Marketplace");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1000, 700));
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(30, 50, 30, 50));

        JPanel topWrapper = new JPanel(new BorderLayout());
        topWrapper.setOpaque(false);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        titlePanel.setOpaque(false);

        JButton btnBack = new JButton("<- Back");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnBack.setForeground(new Color(2, 62, 138));
        btnBack.setBackground(new Color(239, 246, 255));
        btnBack.putClientProperty(FlatClientProperties.STYLE, "arc: 25; borderWidth: 1; borderColor: #BFDBFE; focusWidth: 0;");
        btnBack.setPreferredSize(new Dimension(110, 40));
        btnBack.addActionListener(e -> {
            new CustomerHome(currentUsername).setVisible(true);
            this.dispose();
        });

        JLabel lblTitle = new JLabel("Resale Marketplace");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitle.setForeground(new Color(2, 62, 138));

        titlePanel.add(btnBack);
        titlePanel.add(lblTitle);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionPanel.setOpaque(false);

        JTextField txtSearch = new JTextField(25);
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search movies...");
        txtSearch.putClientProperty(FlatClientProperties.STYLE, "arc: 15; padding: 5,10,5,10");

        actionPanel.add(txtSearch);
        topWrapper.add(titlePanel, BorderLayout.WEST);
        topWrapper.add(actionPanel, BorderLayout.EAST);
        mainPanel.add(topWrapper, BorderLayout.NORTH);

        String[] columns = {"Asset / Movie", "Showtime", "Seat / Tier", "Price", "Action"};
        model = new DefaultTableModel(new Vector<>(), new Vector<>(Arrays.asList(columns))) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(100);
        table.getTableHeader().setPreferredSize(new Dimension(0, 50));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setForeground(new Color(2, 62, 138));
        table.setSelectionBackground(new Color(241, 245, 249));

        loadDataToTable();

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        ((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        table.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSel, boolean hasF, int row, int col) {
                JPanel cell = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
                cell.setBackground(isSel ? table.getSelectionBackground() : Color.WHITE);
                if (value instanceof Object[]) {
                    Object[] cellData = (Object[]) value;
                    JLabel img = new JLabel(); img.setPreferredSize(new Dimension(50, 70));
                    if (cellData[1] != null) img.setIcon((ImageIcon) cellData[1]);
                    JLabel name = new JLabel((String) cellData[0]); name.setFont(new Font("Segoe UI", Font.BOLD, 15));
                    cell.add(img); cell.add(name);
                }
                return cell;
            }
        });

        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSel, boolean hasF, int row, int col) {
                JButton btn = new JButton("Buy Ticket");
                btn.setBackground(new Color(37, 99, 235));
                btn.setForeground(Color.WHITE);
                btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
                btn.putClientProperty(FlatClientProperties.STYLE, "arc: 12; borderWidth: 0");
                btn.setPreferredSize(new Dimension(120, 38));
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                JPanel p = new JPanel(new GridBagLayout());
                p.setBackground(isSel ? table.getSelectionBackground() : Color.WHITE);
                p.add(btn);
                return p;
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = table.columnAtPoint(e.getPoint());
                int viewRow = table.rowAtPoint(e.getPoint());
                
                if (viewRow >= 0 && col == 4) {
                    int modelRow = table.convertRowIndexToModel(viewRow);
                    
                    // Lấy dữ liệu SẠCH (Clean Data) trực tiếp từ Object Model thay vì bóc HTML trên giao diện
                    TrangPassVe ticket = listTickets.get(modelRow);
                    int maPass = ticket.getMaPass();

                    if (isTicketOwnedByCurrentUser(maPass)) {
                        JOptionPane.showMessageDialog(MarketplaceView.this, "Bạn không thể tự mua vé do chính mình đăng bán!", "Lỗi giao dịch", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    String tenPhim = ticket.getTenPhim();
                    String hinhAnh = ticket.getHinhAnh();
                    String thongTinSuat = ticket.getNgayChieu() + " | " + ticket.getTenPhong(); // Chuỗi chuẩn không có <br>
                    String viTriGhe = ticket.getTenGhe();
                    double giaPass = ticket.getGiaPass();

                    // Gọi thẳng Dialog
                    BuyTicketDialog dialog = new BuyTicketDialog(MarketplaceView.this, currentUsername, maPass, tenPhim, hinhAnh, thongTinSuat, viTriGhe, giaPass);
                    dialog.setVisible(true);

                    // Mua xong thì load lại rổ hàng
                    loadDataToTable();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            private void filter() {
                String k = txtSearch.getText().trim().toLowerCase();
                sorter.setRowFilter(k.isEmpty() ? null : RowFilter.regexFilter("(?i)" + k));
            }
        });

        setContentPane(mainPanel);
    }

    private boolean isTicketOwnedByCurrentUser(int maPass) {
        String sql = "SELECT t.USERNAME FROM TRANGPASSVE p " +
                     "JOIN KHACHHANG k ON p.MAKH_BAN = k.MAKH " +
                     "JOIN TAIKHOAN t ON k.MATK = t.MATK " +
                     "WHERE p.MAPASS = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maPass);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return currentUsername.equals(rs.getString("USERNAME"));
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return false;
    }

    public void loadDataToTable() {
        model.setRowCount(0); 
        listTickets = dao.getTicketsOnMarketplace();
        
        System.out.println("DEBUG: Số lượng vé load được lên chợ = " + (listTickets != null ? listTickets.size() : 0));
        
        if (listTickets != null && !listTickets.isEmpty()) {
            for (TrangPassVe item : listTickets) {
                Vector<Object> row = new Vector<>();
                ImageIcon icon = null;
                try {
                    URL imgUrl = getClass().getResource("/images/" + item.getHinhAnh());
                    if (imgUrl != null) icon = new ImageIcon(new ImageIcon(imgUrl).getImage().getScaledInstance(50, 70, Image.SCALE_SMOOTH));
                } catch (Exception e) {}
                
                row.add(new Object[]{item.getTenPhim(), icon});
                row.add("<html><div style='text-align: center;'>" + item.getNgayChieu() + "<br><font color='gray'>" + item.getTenPhong() + "</font></div></html>");
                row.add(item.getTenGhe() + " (" + item.getLoaiGhe() + ")");
                row.add(String.format("%,.0f đ", item.getGiaPass()));
                row.add("Buy Ticket");
                model.addRow(row);
            }
        }
    }
}