package com.mycompany.cinema_system_management.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import com.mycompany.cinema_system_management.DAO.GiaoDichDAO;
import com.mycompany.cinema_system_management.models.GiaoDich;
import com.mycompany.cinema_system_management.utils.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.Connection;
import java.util.List;

public class AdminDashboard extends JFrame {

    // --- 1. QUẢN LÝ CHUYỂN TRANG ---
    private CardLayout cardLayout = new CardLayout();
    private JPanel pnlCards;
    private Sidebar sidebar;

    // --- 2. KHAI BÁO CÁC TRANG CHỨC NĂNG THẬT ---
    private QuanLySuatChieu pnlSuatChieu = new QuanLySuatChieu();
    private QuanLyKhuyenMai pnlKhuyenMai = new QuanLyKhuyenMai();
    private QuanLyKhoHang pnlKhoHang = new QuanLyKhoHang();
    private QuanLyPhanQuyen pnlPhanQuyen = new QuanLyPhanQuyen();

    // --- 3. BIẾN GIAO DIỆN DASHBOARD (HOME) ---
    private JTable table;
    private DefaultTableModel tableModel;
    private StatCard cardRevenue, cardSold, cardMarket, cardPromo;
    private final GiaoDichDAO giaoDichDAO = new GiaoDichDAO();

    private final Font interBold24 = new Font("Inter", Font.BOLD, 24);
    private final Font interBold18 = new Font("Inter", Font.BOLD, 18);
    private final Font interSemiBold14 = new Font("Inter", Font.BOLD, 14);
    private final Font interMedium12 = new Font("Inter", Font.PLAIN, 12);

    public AdminDashboard() {
        initComponents();
        setupNavigation(); // Quan trọng: Nối dây cho Sidebar
        SwingUtilities.invokeLater(this::loadData);
    }

    private void initComponents() {
        setTitle("Hệ thống Quản lý Cinema - UIT");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1350, 850);
        setLayout(new BorderLayout());

        // Khởi tạo Sidebar
        sidebar = new Sidebar();
        add(sidebar, BorderLayout.WEST);

        // Khởi tạo vùng chứa CardLayout
        pnlCards = new JPanel(cardLayout);
        
        // ĐĂNG KÝ CÁC TRANG VÀO CARDLAYOUT (Nối biến thật vào tên định danh)
        pnlCards.add(createPnlHome(), "CARD_DASHBOARD");
        pnlCards.add(pnlSuatChieu, "CARD_SUATCHIEU");
        pnlCards.add(pnlKhuyenMai, "CARD_KHUYENMAI");
        pnlCards.add(pnlKhoHang, "CARD_KHOHANG");
        pnlCards.add(pnlPhanQuyen, "CARD_PHANQUYEN");

        add(pnlCards, BorderLayout.CENTER);
        setLocationRelativeTo(null);
    }

    private void setupNavigation() {
        // Gán sự kiện chuyển trang cho từng nút bấm lấy từ Sidebar Getter
        
        sidebar.getBtnDashboard().addActionListener(e -> {
            cardLayout.show(pnlCards, "CARD_DASHBOARD");
            loadData(); // Load lại số liệu trang chủ
        });

        sidebar.getBtnSuatChieu().addActionListener(e -> {
            cardLayout.show(pnlCards, "CARD_SUATCHIEU");
            // pnlSuatChieu.loadData(); // Nếu trang con có hàm load dữ liệu thì gọi ở đây
        });

        sidebar.getBtnKhuyenMai().addActionListener(e -> {
            cardLayout.show(pnlCards, "CARD_KHUYENMAI");
        });

        sidebar.getBtnKhoHang().addActionListener(e -> {
            cardLayout.show(pnlCards, "CARD_KHOHANG");
        });

        sidebar.getBtnPhanQuyen().addActionListener(e -> {
            cardLayout.show(pnlCards, "CARD_PHANQUYEN");
        });
    }

    // --- HÀM TẠO GIAO DIỆN TRANG CHỦ (GIỮ NGUYÊN GIAO DIỆN CỦA NÍ) ---
    private JPanel createPnlHome() {
        JPanel pnlMain = new JPanel(new BorderLayout(0, 20));
        pnlMain.setBackground(new Color(241, 245, 249)); 
        pnlMain.setBorder(BorderFactory.createEmptyBorder(25, 30, 30, 30));

        JLabel lblHeader = new JLabel("Bảng điều khiển");
        lblHeader.setFont(interBold24);
        lblHeader.setForeground(new Color(15, 23, 42));
        pnlMain.add(lblHeader, BorderLayout.NORTH);

        JPanel pnlCenter = new JPanel(new BorderLayout(0, 25));
        pnlCenter.setOpaque(false);

        JPanel pnlStats = new JPanel(new GridLayout(1, 4, 20, 0));
        pnlStats.setOpaque(false);
        pnlStats.setPreferredSize(new Dimension(0, 140));

        cardRevenue = new StatCard("TỔNG DOANH THU", "0đ", "0%", "layout-dashboard", new Color(37, 99, 235));
        cardSold = new StatCard("SỐ VÉ ĐÃ BÁN", "0", "0%", "calendar", new Color(34, 197, 94));
        cardMarket = new StatCard("VÉ TRÊN CHỢ PASS", "0", "Mới", "shopping-cart", new Color(249, 115, 22));
        cardPromo = new StatCard("ÁP MÃ KHUYẾN MÃI", "0", "0 Mã mới", "tag", new Color(139, 92, 246));

        pnlStats.add(cardRevenue);
        pnlStats.add(cardSold);
        pnlStats.add(cardMarket);
        pnlStats.add(cardPromo);

        pnlCenter.add(pnlStats, BorderLayout.NORTH);
        pnlCenter.add(createTablePanel(), BorderLayout.CENTER);
        pnlMain.add(pnlCenter, BorderLayout.CENTER);

        return pnlMain;
    }

    private JPanel createTablePanel() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(Color.WHITE);
        pnl.putClientProperty(FlatClientProperties.STYLE, "arc: 20");
        pnl.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JPanel pnlTitle = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        pnlTitle.setOpaque(false);
        JLabel title = new JLabel("Giao dịch gần đây");
        title.setFont(interBold18);
        pnlTitle.add(title);
        pnlTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        pnl.add(pnlTitle, BorderLayout.NORTH);

        String[] columns = {"MÃ ĐƠN", "KHÁCH HÀNG", "PHIM", "TỔNG TIỀN", "NGÀY GIỜ"};
        tableModel = new DefaultTableModel(null, columns) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        setupTableUI();

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        pnl.add(scroll, BorderLayout.CENTER);

        return pnl;
    }

    private void setupTableUI() {
        table.setRowHeight(60);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(248, 250, 252));
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Inter", Font.BOLD, 12));
        header.setBackground(Color.WHITE);
        header.setForeground(new Color(148, 163, 184));
        header.setPreferredSize(new Dimension(0, 45));

        table.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
                l.setText("#CIN-" + (v != null ? v.toString() : "N/A"));
                l.setFont(new Font("Inter", Font.BOLD, 12));
                l.setForeground(new Color(15, 23, 42));
                return l;
            }
        });

        DefaultTableCellRenderer boldRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
                l.setFont(interSemiBold14);
                return l;
            }
        };
        table.getColumnModel().getColumn(1).setCellRenderer(boldRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(boldRenderer);
    }

    public void loadData() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return;
            double[] rev = giaoDichDAO.getRevenueStats(conn);
            int[] tkt = giaoDichDAO.getTicketStats(conn);
            int marketCount = giaoDichDAO.getMarketTicketsCount(conn);
            int[] promo = giaoDichDAO.getPromoStats(conn);
            List<GiaoDich> list = giaoDichDAO.getRecentTransactions(conn);

            cardRevenue.setData(String.format("%,.0fđ", rev[0]), "+0%");
            cardSold.setData(String.valueOf(tkt[0]), "0%");
            cardMarket.setData(String.valueOf(marketCount), "Mới");
            cardPromo.setData(String.valueOf(promo[0]), promo[1] + " Mã mới");

            SwingUtilities.invokeLater(() -> {
                tableModel.setRowCount(0);
                if (list != null) {
                    for (GiaoDich g : list) {
                        tableModel.addRow(new Object[]{g.getMaGD(), g.getTenKH(), g.getTenPhim(), String.format("%,.0fđ", g.getSoTien()), g.getThoiGian()});
                    }
                }
            });
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();
        EventQueue.invokeLater(() -> new AdminDashboard().setVisible(true));
    }
}