package com.mycompany.cinema_system_management.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import com.mycompany.cinema_system_management.dao.HoaDonDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.List;

public class WalletHistoryView extends JFrame {
    private String currentUsername;

    public WalletHistoryView(String username) {
        this.currentUsername = username;
        FlatLightLaf.setup();
        setTitle("CineMarket - Lịch sử giao dịch");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(new Color(248, 250, 252));
        mainPanel.setBorder(new EmptyBorder(30, 50, 30, 50));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel lblTitle = new JLabel("Lịch sử giao dịch");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        
        JButton btnBack = new JButton("<- Quay lại trang chủ");
        btnBack.addActionListener(e -> { new CustomerHome(currentUsername).setVisible(true); dispose(); });
        header.add(lblTitle, BorderLayout.WEST);
        header.add(btnBack, BorderLayout.EAST);
        mainPanel.add(header, BorderLayout.NORTH);

        String[] columns = {"Mã Giao Dịch", "Thời gian", "Loại GD", "Biến động", "Nội dung", "Số dư cuối"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(120);
        columnModel.getColumn(1).setPreferredWidth(180);
        columnModel.getColumn(2).setPreferredWidth(150);
        columnModel.getColumn(3).setPreferredWidth(150);
        columnModel.getColumn(4).setPreferredWidth(800); 
        columnModel.getColumn(5).setPreferredWidth(200);

        // Canh lề giữa cho Mã GD, Thời gian, Loại GD
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        columnModel.getColumn(0).setCellRenderer(centerRenderer);
        columnModel.getColumn(1).setCellRenderer(centerRenderer);
        columnModel.getColumn(2).setCellRenderer(centerRenderer);

        // Canh lề phải cho Tiền (Biến động, Số dư cuối)
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        columnModel.getColumn(3).setCellRenderer(rightRenderer);
        columnModel.getColumn(5).setCellRenderer(rightRenderer);
        
        HoaDonDAO dao = new HoaDonDAO();
        List<Object[]> data = dao.getSaoKeVi(username);
        for (Object[] row : data) model.addRow(row);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.putClientProperty(FlatClientProperties.STYLE, "arc: 15; borderWidth: 1; borderColor: #E2E8F0;");
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }
}