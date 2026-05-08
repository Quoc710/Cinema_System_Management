package com.mycompany.cinema_system_management.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import com.mycompany.cinema_system_management.dao.PhimDAO;
import com.mycompany.cinema_system_management.models.Phim;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class CustomerHome extends JFrame {

    public CustomerHome() {
        FlatLightLaf.setup();
        setTitle("CineMarket - Trang chủ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 750);
        setLocationRelativeTo(null);
        
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(new Color(248, 250, 252));
        
        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        setContentPane(scrollPane);

        mainContent.add(createNavbar());
        mainContent.add(createHeroBanner());
        mainContent.add(createMoviesSection());
    }

    private BufferedImage loadImage(String path) {
        try {
            URL imageUrl = getClass().getResource(path);
            if (imageUrl != null) {
                return ImageIO.read(imageUrl);
            } else {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }

    private JPanel createNavbar() {
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.X_AXIS));
        nav.setBackground(Color.WHITE);
        nav.setBorder(new EmptyBorder(15, 40, 15, 40));
        nav.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel lblLogo = new JLabel("CineMarket");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        nav.add(lblLogo);
        nav.add(Box.createRigidArea(new Dimension(40, 0)));

        nav.add(createNavLink("Movies", true));
        nav.add(Box.createRigidArea(new Dimension(20, 0)));
        //nav.add(createNavLink("Marketplace", false));
        JLabel lblMarketplace = createNavLink("Marketplace", false);
        lblMarketplace.addMouseListener(new java.awt.event.MouseAdapter() {
    @Override
    public void mouseClicked(java.awt.event.MouseEvent evt) {
        new MarketplaceView().setVisible(true); // Mở trang Marketplace
        dispose(); // Đóng trang Home (hoặc giữ lại tùy ní)
    }
});
nav.add(lblMarketplace);
        nav.add(Box.createRigidArea(new Dimension(20, 0)));
        nav.add(createNavLink("My Tickets", false));

        nav.add(Box.createHorizontalGlue());

        JTextField txtSearch = new JTextField(20);
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search films, cinemas...");
        txtSearch.putClientProperty(FlatClientProperties.STYLE, "arc: 20; padding: 5,15,5,15");
        txtSearch.setMaximumSize(new Dimension(250, 35));
        nav.add(txtSearch);
        nav.add(Box.createRigidArea(new Dimension(20, 0)));

        JButton btnAccount = new JButton("Account");
        btnAccount.putClientProperty(FlatClientProperties.STYLE, "arc: 20");
        nav.add(btnAccount);

        return nav;
    }

    private JLabel createNavLink(String text, boolean isActive) {
        JLabel link = new JLabel(text);
        link.setFont(new Font("Segoe UI", isActive ? Font.BOLD : Font.PLAIN, 14));
        link.setForeground(isActive ? new Color(37, 99, 235) : Color.GRAY);
        link.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return link;
    }

    private JPanel createHeroBanner() {
    JPanel wrapper = new JPanel(new BorderLayout());
    wrapper.setBackground(new Color(248, 250, 252));
    wrapper.setBorder(new EmptyBorder(20, 40, 20, 40));

    // Load tấm ảnh chất lượng cao ní vừa kiếm được
    final BufferedImage bannerImg = loadImage("/images/onepiecebanner.png");

    JPanel heroBg = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (bannerImg != null) {
                Graphics2D g2 = (Graphics2D) g.create();
                // Bật chế độ làm mịn ảnh cao nhất để không bị bể
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int iw = bannerImg.getWidth(this);
                int ih = bannerImg.getHeight(this);
                int w = getWidth();
                int h = getHeight();

                double sx = (double) w / iw;
                double sy = (double) h / ih;
                double s = Math.max(sx, sy);

                int nw = (int) (iw * s);
                int nh = (int) (ih * s);
                int nx = (w - nw) / 2;
                int ny = (h - nh) / 2;

                g2.drawImage(bannerImg, nx, ny, nw, nh, this);
                g2.dispose();
            } else {
                g.setColor(new Color(15, 23, 42));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    };
    
    heroBg.setOpaque(false); 
    heroBg.putClientProperty(FlatClientProperties.STYLE, "arc: 20");
    
    
    heroBg.setPreferredSize(new Dimension(Integer.MAX_VALUE, 300)); 

    wrapper.add(heroBg, BorderLayout.CENTER);
    return wrapper;
}

private JPanel createMoviesSection() {
    JPanel section = new JPanel();
    section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
    section.setBackground(new Color(248, 250, 252));
    section.setBorder(new EmptyBorder(0, 40, 40, 40));

    // --- 1. Tiêu đề & Bộ lọc (Giữ nguyên) ---
    JPanel header = new JPanel();
    header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));
    header.setOpaque(false);
    JLabel lblTitle = new JLabel("Current Releases");
    lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
    header.add(lblTitle);
    header.add(Box.createHorizontalGlue());
    section.add(header);
    section.add(Box.createRigidArea(new Dimension(0, 20)));

    // --- 2. Container chứa các thẻ phim (GRID) ---
    // Không dùng GridLayout cố định 5 cột nữa, mà dùng FlowLayout để nó dàn hàng ngang dài vô tận
    JPanel grid = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
    grid.setOpaque(false);

    // --- 3. Lấy dữ liệu từ DAO và đổ vào ---
    PhimDAO phimDao = new PhimDAO();
    List<Phim> dsPhim = phimDao.getDanhSachPhim();

    for (Phim p : dsPhim) {
        JPanel card = createMovieCard(p);
        // Cố định kích thước thẻ phim để nó không bị co giãn khi cuộn
        card.setPreferredSize(new Dimension(180, 280)); 
        grid.add(card);
    }

    // --- 4. TẠO THANH CUỘN NGANG (SCROLLPANE) ---
    JScrollPane scrollPane = new JScrollPane(grid);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER); // Tắt cuộn dọc
    scrollPane.setBorder(null); // Xóa viền xấu xí
    scrollPane.setOpaque(false);
    scrollPane.getViewport().setOpaque(false);
    
    // Tăng tốc độ cuộn chuột cho mượt
    scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
    
    // Làm đẹp thanh cuộn theo phong cách FlatLaf (ẩn đi khi không rờ tới)
    scrollPane.getHorizontalScrollBar().putClientProperty(FlatClientProperties.STYLE, 
        "trackArc: 999; thumbArc: 999; trackInsets: 3,3,3,3; thumbInsets: 3,3,3,3; hoverTrackColor: #00000000");

    section.add(scrollPane);
    return section;
}

    private JPanel createMovieCard(Phim phim) {
        String imagePath = "/images/" + phim.getHinhAnh();
        final BufferedImage posterImg = loadImage(imagePath);
        String ratingStr = String.format("%.1f / 10", phim.getDiemDanhGia());

        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (posterImg != null) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    // THÊM 3 DÒNG NÀY ĐỂ HÌNH KHÔNG BỊ BỂ
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Vẽ ảnh lấp đầy thẻ card
                g2.drawImage(posterImg, 0, 0, getWidth(), getHeight(), this);
                g2.dispose();
                } else {
                    g.setColor(new Color(71, 85, 105));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        
        card.setOpaque(false); 
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 15");
        card.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        topPanel.setOpaque(false);
        
        JLabel lblRating = new JLabel(" " + ratingStr + " ");
        lblRating.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblRating.setBackground(Color.WHITE);
        lblRating.setOpaque(true);
        lblRating.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        
        topPanel.add(lblRating);
        card.add(topPanel, BorderLayout.NORTH);

        JLabel lblTitle = new JLabel(phim.getTenPhim(), SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setBorder(new EmptyBorder(0, 0, 5, 0));
        card.add(lblTitle, BorderLayout.SOUTH);

        return card;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CustomerHome().setVisible(true));
    }
}