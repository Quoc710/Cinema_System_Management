package com.mycompany.cinema_system_management.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import com.mycompany.cinema_system_management.dao.PhimDAO;
import com.mycompany.cinema_system_management.models.Phim;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

public class MovieDetailView extends JFrame {

    private String currentUsername;
    private Phim phimHienTai;
    private List<Object[]> danhSachLichChieu;
    
    // Biến lưu trữ trạng thái giao diện
    private String selectedDate = ""; 
    private List<String> availableDates = new ArrayList<>();
    private JPanel rightPanel; // Panel cột phải để có thể làm mới (refresh)

    public MovieDetailView(String username, String tenPhim, String hinhAnh) {
        this.currentUsername = username;
        PhimDAO dao = new PhimDAO();
        this.phimHienTai = dao.getChiTietPhim(tenPhim);
        this.danhSachLichChieu = dao.getLichChieuCuaPhim(tenPhim);

        // Lọc ra các ngày duy nhất từ danh sách lịch chiếu
        if (danhSachLichChieu != null && !danhSachLichChieu.isEmpty()) {
            SimpleDateFormat dateOnlyFmt = new SimpleDateFormat("yyyy-MM-dd");
            for (Object[] row : danhSachLichChieu) {
                String dateStr = dateOnlyFmt.format((Timestamp) row[0]);
                if (!availableDates.contains(dateStr)) {
                    availableDates.add(dateStr);
                }
            }
            // Mặc định chọn ngày đầu tiên có lịch chiếu
            if (!availableDates.isEmpty()) {
                selectedDate = availableDates.get(0);
            }
        }

        if (this.phimHienTai == null) {
            this.phimHienTai = new Phim();
            this.phimHienTai.setTenPhim(tenPhim);
            this.phimHienTai.setThoiLuong(0);
            this.phimHienTai.setTheLoai("Đang cập nhật");
        }

        FlatLightLaf.setup();
        setTitle("CineMarket - " + tenPhim);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        getContentPane().setBackground(new Color(248, 250, 252));

        JPanel mainPanel = new JPanel(new BorderLayout(40, 0));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(30, 50, 40, 50));
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        JPanel bodyPanel = new JPanel(new BorderLayout(50, 0));
        bodyPanel.setOpaque(false);
        bodyPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        bodyPanel.add(createLeftPanel(this.phimHienTai, hinhAnh), BorderLayout.WEST);
        
        // Khởi tạo cột phải
        rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        refreshRightPanel(); // Hàm tự động vẽ ngày và giờ chiếu
        bodyPanel.add(rightPanel, BorderLayout.CENTER);

        mainPanel.add(bodyPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    // --- Hàm refresh lại cột phải mỗi khi bấm chọn ngày khác ---
    private void refreshRightPanel() {
        rightPanel.removeAll();
        rightPanel.add(createDynamicRightContent(), BorderLayout.CENTER);
        rightPanel.revalidate();
        rightPanel.repaint();
    }

    private JPanel createDynamicRightContent() {
        JPanel scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setOpaque(false);

        // 1. THANH CHỌN NGÀY (Dynamic)
        JLabel lblDateTitle = new JLabel("Select Date");
        lblDateTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblDateTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        lblDateTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollContent.add(lblDateTitle);

        JPanel dateGrid = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        dateGrid.setOpaque(false);
        dateGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (availableDates.isEmpty()) {
            dateGrid.add(new JLabel("Chưa có lịch chiếu"));
        } else {
            SimpleDateFormat parseDate = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dayFmt = new SimpleDateFormat("EEE", Locale.ENGLISH); // MON, TUE
            SimpleDateFormat dateFmt = new SimpleDateFormat("dd"); // 12, 13
            SimpleDateFormat monthFmt = new SimpleDateFormat("MMM", Locale.ENGLISH); // Aug

            for (String dStr : availableDates) {
                try {
                    java.util.Date d = parseDate.parse(dStr);
                    boolean isActive = dStr.equals(selectedDate);
                    
                    JPanel dateBtn = createDateButton(
                        dayFmt.format(d).toUpperCase(), 
                        dateFmt.format(d), 
                        monthFmt.format(d), 
                        isActive
                    );
                    
                    // Sự kiện click chọn ngày
                    dateBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                        @Override
                        public void mouseClicked(java.awt.event.MouseEvent evt) {
                            selectedDate = dStr;
                            refreshRightPanel(); // Tải lại lịch chiếu theo ngày
                        }
                    });
                    dateGrid.add(dateBtn);
                } catch (Exception e) { e.printStackTrace(); }
            }
        }
        scrollContent.add(dateGrid);
        scrollContent.add(Box.createVerticalStrut(30));

        // 2. KHUNG GIỜ CHIẾU (Lọc theo ngày đã chọn)
        JLabel lblShowTitle = new JLabel("Available Showtimes");
        lblShowTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblShowTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        lblShowTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollContent.add(lblShowTitle);

        JPanel timeHBox = new JPanel();
        timeHBox.setLayout(new BoxLayout(timeHBox, BoxLayout.X_AXIS));
        timeHBox.setOpaque(false);
        
        boolean hasShowsForDate = false;
        SimpleDateFormat dateOnlyFmt = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        if (danhSachLichChieu != null) {
            for (Object[] show : danhSachLichChieu) {
                Timestamp ts = (Timestamp) show[0];
                // Lọc: Chỉ hiển thị các suất chiếu đúng với ngày đang chọn
                if (dateOnlyFmt.format(ts).equals(selectedDate)) {
                    hasShowsForDate = true;
                    String dinhDang = show[2] != null ? (String) show[2] : "2D";
                    String price = dinhDang.contains("IMAX") ? "150.000 đ" : "85.000 đ";
                    
                    JPanel timeBtn = new JPanel(new GridLayout(2, 1));
                    timeBtn.setPreferredSize(new Dimension(140, 60));
                    timeBtn.setMaximumSize(new Dimension(140, 60));
                    timeBtn.putClientProperty(FlatClientProperties.STYLE, "arc: 12; borderWidth: 1; borderColor: #E2E8F0; background: #FFFFFF;");
                    
                    JLabel lblTime = new JLabel(timeFormat.format(ts) + " (" + dinhDang + ")", SwingConstants.CENTER);
                    lblTime.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    lblTime.setForeground(new Color(15, 23, 42));
                    
                    JLabel lblPrice = new JLabel(price, SwingConstants.CENTER);
                    lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    lblPrice.setForeground(new Color(148, 163, 184));
                    
                    timeBtn.add(lblTime); timeBtn.add(lblPrice);
                    timeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    
                    // --- ĐÂY LÀ SỰ KIỆN CLICK CHUYỂN SANG TRANG SNACKS ---
                    timeBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        // Trích xuất tên phòng từ vị trí số 1 của mảng dữ liệu show
                        String tenPhong = (String) show[1];
                        String thongTin = timeFormat.format(ts) + " • " + dinhDang;

                        // Truyền đầy đủ tham số sang màn hình BookingSeatView
                        new BookingSeatView(currentUsername, phimHienTai.getTenPhim(), thongTin, tenPhong,phimHienTai.getHinhAnh()).setVisible(true);
                        dispose();
                    }

                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        timeBtn.putClientProperty(FlatClientProperties.STYLE, "arc: 12; borderWidth: 1; borderColor: #3B82F6; background: #F8FAFC;");
                    }

                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        timeBtn.putClientProperty(FlatClientProperties.STYLE, "arc: 12; borderWidth: 1; borderColor: #E2E8F0; background: #FFFFFF;");
                        }
                    });
                    
                    timeHBox.add(timeBtn);
                    timeHBox.add(Box.createHorizontalStrut(15));
                }
            }
        }

        if (!hasShowsForDate) {
            JLabel lblEmpty = new JLabel("Không có suất chiếu nào cho ngày này.");
            lblEmpty.setForeground(Color.GRAY);
            timeHBox.add(lblEmpty);
        }

        JScrollPane timeScroll = new JScrollPane(timeHBox);
        timeScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        timeScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        timeScroll.setBorder(null);
        timeScroll.setOpaque(false);
        timeScroll.getViewport().setOpaque(false);
        timeScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollContent.add(timeScroll);
        
        scrollContent.add(Box.createVerticalStrut(30));

        // 3. TÓM TẮT PHIM
        JPanel synopsisWrapper = new JPanel(new BorderLayout());
        synopsisWrapper.setOpaque(false);
        synopsisWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblSynopTitle = new JLabel("Tóm tắt phim");
        lblSynopTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblSynopTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        JTextArea txtSynopsis = new JTextArea("Hành trình khám phá kho báu vĩ đại nhất với những màn hải chiến đỉnh cao. Đặt vé ngay để nhận ưu đãi từ CineMarket!");
        txtSynopsis.setWrapStyleWord(true);
        txtSynopsis.setLineWrap(true);
        txtSynopsis.setEditable(false);
        txtSynopsis.setOpaque(false);
        txtSynopsis.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSynopsis.setForeground(new Color(71, 85, 105));
        synopsisWrapper.add(lblSynopTitle, BorderLayout.NORTH);
        synopsisWrapper.add(txtSynopsis, BorderLayout.CENTER);
        scrollContent.add(synopsisWrapper);

        return scrollContent;
    }

    // Các hàm Header, LeftPanel, MetaLabel giữ nguyên không đổi
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JButton btnBack = new JButton("<- Back");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnBack.setForeground(new Color(2, 62, 138));
        btnBack.setBackground(new Color(239, 246, 255));
        btnBack.putClientProperty(FlatClientProperties.STYLE, "arc: 20; borderWidth: 0; focusWidth: 0;");
        btnBack.setPreferredSize(new Dimension(100, 40));
        btnBack.addActionListener(e -> {
            new CustomerHome(currentUsername).setVisible(true);
            dispose();
        });
        JLabel lblTitle = new JLabel("Movie Detail & Showtimes");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(btnBack, BorderLayout.WEST);
        header.add(lblTitle, BorderLayout.CENTER);
        header.add(Box.createRigidArea(new Dimension(100, 40)), BorderLayout.EAST);
        return header;
    }

    private JPanel createLeftPanel(Phim p, String imgPath) {
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);
        left.setPreferredSize(new Dimension(350, 0));
        JLabel lblPoster = new JLabel();
        lblPoster.setPreferredSize(new Dimension(350, 500));
        lblPoster.setMaximumSize(new Dimension(350, 500));
        try {
            URL url = getClass().getResource("/images/" + imgPath);
            if (url != null) lblPoster.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(350, 500, Image.SCALE_SMOOTH)));
        } catch (Exception e) {}
        lblPoster.putClientProperty(FlatClientProperties.STYLE, "arc: 15");
        left.add(lblPoster);
        left.add(Box.createVerticalStrut(20));
        JLabel lblTen = new JLabel(p.getTenPhim());
        lblTen.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTen.setForeground(new Color(15, 23, 42));
        left.add(lblTen);
        left.add(Box.createVerticalStrut(15));
        JPanel metaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        metaPanel.setOpaque(false);
        metaPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        metaPanel.add(createMetaLabel("DURATION", p.getThoiLuong() + " Phút"));
        metaPanel.add(createMetaLabel("GENRE", p.getTheLoai() != null ? p.getTheLoai() : "Đang cập nhật"));
        metaPanel.add(createMetaLabel("LANGUAGE", "Vietnamese"));
        left.add(metaPanel);
        return left;
    }

    private JPanel createMetaLabel(String title, String value) {
        JPanel p = new JPanel(new GridLayout(2, 1, 0, 5));
        p.setOpaque(false);
        JLabel lblT = new JLabel(title);
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblT.setForeground(new Color(148, 163, 184));
        JLabel lblV = new JLabel(value);
        lblV.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblV.setForeground(new Color(15, 23, 42));
        p.add(lblT); p.add(lblV);
        return p;
    }

    private JPanel createDateButton(String day, String date, String month, boolean isActive) {
        JPanel btn = new JPanel(new GridLayout(3, 1));
        btn.setPreferredSize(new Dimension(75, 95));
        String style = "arc: 12; borderWidth: 1; ";
        if (isActive) style += "borderColor: #3B82F6; background: #EFF6FF;";
        else style += "borderColor: #E2E8F0; background: #FFFFFF;";
        btn.putClientProperty(FlatClientProperties.STYLE, style);
        JLabel lblDay = new JLabel(day, SwingConstants.CENTER);
        lblDay.setFont(new Font("Segoe UI", Font.BOLD, 11));
        JLabel lblDate = new JLabel(date, SwingConstants.CENTER);
        lblDate.setFont(new Font("Segoe UI", Font.BOLD, 22));
        JLabel lblMonth = new JLabel(month, SwingConstants.CENTER);
        lblMonth.setFont(new Font("Segoe UI", Font.BOLD, 11));
        Color txtColor = isActive ? new Color(37, 99, 235) : new Color(148, 163, 184);
        Color dateColor = isActive ? new Color(37, 99, 235) : new Color(15, 23, 42);
        lblDay.setForeground(txtColor);
        lblDate.setForeground(dateColor);
        lblMonth.setForeground(txtColor);
        btn.add(lblDay); btn.add(lblDate); btn.add(lblMonth);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        if(!isActive) {
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    btn.putClientProperty(FlatClientProperties.STYLE, "arc: 12; borderWidth: 1; borderColor: #94A3B8; background: #F8FAFC;");
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    btn.putClientProperty(FlatClientProperties.STYLE, "arc: 12; borderWidth: 1; borderColor: #E2E8F0; background: #FFFFFF;");
                }
            });
        }
        return btn;
    }
}