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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.List;

public class CustomerHome extends JFrame {

    private String currentUsername;
    private JTextField txtSearch;
    private JPanel movieGrid; 
    private List<Phim> dsPhimKhoGoc;

    public CustomerHome(String username) {
        this.currentUsername = username;
        FlatLightLaf.setup();
        setTitle("CineMarket - Trang chủ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1000, 750));
        setLocationRelativeTo(null);
        
        PhimDAO phimDao = new PhimDAO();
        dsPhimKhoGoc = phimDao.getDanhSachPhim();

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(new Color(248, 250, 252));
        
        JPanel fixedTopPanel = new JPanel();
        fixedTopPanel.setLayout(new BoxLayout(fixedTopPanel, BoxLayout.Y_AXIS));
        fixedTopPanel.setBackground(new Color(248, 250, 252));
        
        fixedTopPanel.add(createNavbar());
        fixedTopPanel.add(createHeroBanner());
        
        mainContainer.add(fixedTopPanel, BorderLayout.NORTH);
        mainContainer.add(createMoviesSection(), BorderLayout.CENTER);

        setContentPane(mainContainer);

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filterMovies(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filterMovies(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filterMovies(); }
        });
    }

    private void filterMovies() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        movieGrid.removeAll(); 

        if (dsPhimKhoGoc != null) {
            for (Phim p : dsPhimKhoGoc) {
                if (p.getTenPhim().toLowerCase().contains(keyword)) {
                    JPanel card = createMovieCard(p);
                    card.setPreferredSize(new Dimension(180, 280)); 
                    movieGrid.add(card);
                }
            }
        }
        
        movieGrid.revalidate();
        movieGrid.repaint();
    }

    private BufferedImage loadImage(String path) {
        try {
            URL imageUrl = getClass().getResource(path);
            if (imageUrl != null) {
                return ImageIO.read(imageUrl);
            }
            return null;
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
        
        JLabel lblMarketplace = createNavLink("Marketplace", false);
        lblMarketplace.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new MarketplaceView(currentUsername).setVisible(true); 
                dispose(); 
            }
        });
        nav.add(lblMarketplace);
        nav.add(Box.createRigidArea(new Dimension(20, 0)));
        
        JLabel lblMyTickets = createNavLink("My Tickets", false);
        lblMyTickets.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new MyTicketsView(currentUsername).setVisible(true);
                dispose(); 
            }
        });
        nav.add(lblMyTickets);
        nav.add(Box.createRigidArea(new Dimension(20, 0)));
        
        // --- THÊM NÚT MY WALLET Ở ĐÂY ---
        JLabel lblMyWallet = createNavLink("My Wallet", false);
        lblMyWallet.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new WalletHistoryView(currentUsername).setVisible(true); // Gắn đúng trang WalletHistoryView
                dispose(); 
            }
        });
        nav.add(lblMyWallet);
        // ----------------------------------

        nav.add(Box.createHorizontalGlue());

        txtSearch = new JTextField(20);
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search films, cinemas...");
        txtSearch.putClientProperty(FlatClientProperties.STYLE, "arc: 20; padding: 5,15,5,15");
        txtSearch.setMaximumSize(new Dimension(250, 35));
        nav.add(txtSearch);
        nav.add(Box.createRigidArea(new Dimension(20, 0)));

        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.putClientProperty(FlatClientProperties.STYLE, "arc: 20; borderWidth: 0");
        btnLogout.setBackground(new Color(220, 53, 69)); 
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận đăng xuất khỏi hệ thống?", "Đăng xuất", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new DangNhapFrame().setVisible(true);
            }
        });
        
        nav.add(btnLogout);
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

        final BufferedImage bannerImg = loadImage("/images/onepiecebanner.png");

        JPanel heroBg = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bannerImg != null) {
                    Graphics2D g2 = (Graphics2D) g.create();
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

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));
        header.setOpaque(false);
        JLabel lblTitle = new JLabel("Current Releases");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.add(lblTitle);
        header.add(Box.createHorizontalGlue());
        section.add(header);
        section.add(Box.createRigidArea(new Dimension(0, 20)));

        movieGrid = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        movieGrid.setOpaque(false);

        if (dsPhimKhoGoc != null) {
            for (Phim p : dsPhimKhoGoc) {
                JPanel card = createMovieCard(p);
                card.setPreferredSize(new Dimension(180, 280)); 
                movieGrid.add(card);
            }
        }

        JScrollPane scrollPane = new JScrollPane(movieGrid);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER); 
        scrollPane.setBorder(null); 
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().putClientProperty(FlatClientProperties.STYLE, 
            "trackArc: 999; thumbArc: 999; trackInsets: 3,3,3,3; thumbInsets: 3,3,3,3; hoverTrackColor: #00000000");

        section.add(scrollPane);
        return section;
    }

    private JPanel createMovieCard(Phim phim) {
        String imagePath = "/images/" + phim.getHinhAnh();
        final BufferedImage posterImg = loadImage(imagePath);
        
        String ratingStr = phim.getDoTuoi() > 0 ? "T" + phim.getDoTuoi() : "P"; 

        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (posterImg != null) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
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
        
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new MovieDetailView(currentUsername, phim.getTenPhim(), phim.getHinhAnh()).setVisible(true);
                dispose();
            }
        });

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        topPanel.setOpaque(false);
        
        JLabel lblRating = new JLabel(" " + ratingStr + " ");
        lblRating.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblRating.setBackground(new Color(220, 38, 38)); 
        lblRating.setForeground(Color.WHITE);
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
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 14));
        
        SwingUtilities.invokeLater(() -> new CustomerHome("Guest_test").setVisible(true));
    }
}