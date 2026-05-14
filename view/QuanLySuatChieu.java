package com.mycompany.cinema_system_management.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.mycompany.cinema_system_management.DAO.LichChieuDAO;
import com.mycompany.cinema_system_management.models.SuatChieu;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class QuanLySuatChieu extends JPanel {
    private Color bgColor = new Color(245, 247, 251);
    private Color textGray = new Color(100, 116, 139);
    private Color primaryBlue = new Color(37, 99, 235);
    private JPanel rowsPanel;
    private List<SuatChieu> danhSachSuatChieu;
    private LichChieuDAO lichChieuDAO;
    private JLabel lblTongSuat, lblLapDay, lblDangChieu, lblSapChieu;
    private JTextField txtSearch; 

    private JPanel wrapCol(JComponent comp) {
        JPanel pnl = new JPanel(new GridBagLayout()) {
            @Override
            public Dimension getPreferredSize() { return new Dimension(50, super.getPreferredSize().height); }
        };
        pnl.setOpaque(false);
        pnl.setBorder(new EmptyBorder(0, 10, 0, 10));
        GridBagConstraints g = new GridBagConstraints();
        g.anchor = GridBagConstraints.WEST;
        g.weightx = 1.0;
        g.fill = GridBagConstraints.HORIZONTAL;
        pnl.add(comp, g);
        return pnl;
    }

    public QuanLySuatChieu() {
        setBackground(bgColor);
        setLayout(new BorderLayout(0, 20)); // Đổi thành BorderLayout tổng để chia trên/dưới rõ ràng
        setBorder(new EmptyBorder(25, 30, 30, 30));
        
        danhSachSuatChieu = new ArrayList<>();
        lichChieuDAO = new LichChieuDAO();

        // ==========================================
        // 1. TIÊU ĐỀ
        // ==========================================
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); 
        titlePanel.setOpaque(false);
        JLabel lblTitle = new JLabel("Quản lý Suất chiếu");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 24));
        lblTitle.setForeground(new Color(15, 23, 42));
        titlePanel.add(lblTitle);
        
        // ==========================================
        // 2. THỐNG KÊ
        // ==========================================
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setOpaque(false);
        statsPanel.setPreferredSize(new Dimension(0, 85));
        
        lblTongSuat = createStatLabel(new Color(15, 23, 42));
        lblLapDay = createStatLabel(primaryBlue);
        lblDangChieu = createStatLabel(new Color(16, 185, 129));
        lblSapChieu = createStatLabel(new Color(245, 158, 11));
        
        statsPanel.add(createMiniStatCard("TỔNG SUẤT HÔM NAY", lblTongSuat));
        statsPanel.add(createMiniStatCard("LẤP ĐẦY TRUNG BÌNH", lblLapDay));
        statsPanel.add(createMiniStatCard("ĐANG CHIẾU", lblDangChieu));
        statsPanel.add(createMiniStatCard("SẮP CHIẾU", lblSapChieu));

        // ==========================================
        // 3. THANH CÔNG CỤ (ÉP RA GIỮA MÀN HÌNH)
        // ==========================================
        // Đổi sang FlowLayout.CENTER để đẩy tìm kiếm và nút + ra chính giữa
        JPanel pnlToolBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        pnlToolBar.setOpaque(false);
        pnlToolBar.setBorder(new EmptyBorder(10, 0, 10, 0)); // Tạo khoảng cách nhỏ trên dưới
        
        txtSearch = new JTextField();
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập tên phim để tra cứu suất chiếu...");
        txtSearch.putClientProperty(FlatClientProperties.STYLE, "arc: 15; focusWidth: 2;");
        txtSearch.setPreferredSize(new Dimension(450, 42));
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                renderList(txtSearch.getText().trim());
            }
        });

        JButton btnAdd = new JButton("+");
        btnAdd.setFont(new Font("Inter", Font.BOLD, 26));
        btnAdd.putClientProperty(FlatClientProperties.STYLE, "arc: 12; background: #10b981; foreground: #ffffff; borderWidth: 0;");
        btnAdd.setPreferredSize(new Dimension(45, 42));
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.addActionListener(e -> showForm(null));

        pnlToolBar.add(txtSearch);
        pnlToolBar.add(btnAdd);
        
        // ==========================================
        // GOM LẠI THÀNH HEADER VÀ ÉP LÊN TRÊN CÙNG (NORTH)
        // ==========================================
        JPanel headerContainer = new JPanel(new BorderLayout(0, 20)); 
        headerContainer.setOpaque(false);
        
        JPanel topHalf = new JPanel(new BorderLayout(0, 20));
        topHalf.setOpaque(false);
        topHalf.add(titlePanel, BorderLayout.NORTH);
        topHalf.add(statsPanel, BorderLayout.CENTER);

        headerContainer.add(topHalf, BorderLayout.NORTH);
        headerContainer.add(pnlToolBar, BorderLayout.CENTER);
        
        // Đặt Header dính chặt lên trên cùng
        add(headerContainer, BorderLayout.NORTH);

        // ==========================================
        // BẢNG DANH SÁCH BÊN DƯỚI (ÉP VÀO CENTER ĐỂ TỰ GIÃN)
        // ==========================================
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.putClientProperty(FlatClientProperties.STYLE, "arc: 20; background: #FFFFFF");
        tableContainer.setBorder(new EmptyBorder(10, 20, 20, 20));
        
        JPanel headerRow = new JPanel(new GridBagLayout());
        headerRow.setOpaque(false);
        headerRow.setBorder(new MatteBorder(0, 0, 2, 0, new Color(241, 245, 249)));
        headerRow.setPreferredSize(new Dimension(800, 40));
        headerRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        String[] cols = {"MÃ LC", "TÊN PHIM", "PHÒNG", "THỜI GIAN", "TỶ LỆ LẤP ĐẦY", "TRẠNG THÁI", "THAO TÁC"};
        double[] weights = {0.08, 0.25, 0.10, 0.12, 0.20, 0.12, 0.13}; 

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        for (int i = 0; i < cols.length; i++) {
            gbc.gridx = i;
            gbc.weightx = weights[i];
            JLabel lbl = new JLabel(cols[i]);
            lbl.setFont(new Font("Inter", Font.BOLD, 12));
            lbl.setForeground(textGray);
            headerRow.add(wrapCol(lbl), gbc);
        }
        tableContainer.add(headerRow, BorderLayout.NORTH);

        rowsPanel = new JPanel();
        rowsPanel.setLayout(new BoxLayout(rowsPanel, BoxLayout.Y_AXIS));
        rowsPanel.setBackground(Color.WHITE);
        
        // Bây giờ chỉ có các hàng nội dung là cuộn được, tiêu đề bảng đứng im
        JScrollPane scroll = new JScrollPane(rowsPanel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        
        tableContainer.add(scroll, BorderLayout.CENTER);
        
        // Đặt bảng vào phần giữa màn hình
        add(tableContainer, BorderLayout.CENTER);
        
        loadDataFromDB();
    }

    private void loadDataFromDB() {
        danhSachSuatChieu.clear();
        danhSachSuatChieu = lichChieuDAO.getDanhSachLichChieu();
        renderList(""); 
    }

    private JLabel createStatLabel(Color c) {
        JLabel lbl = new JLabel("0");
        lbl.setFont(new Font("Inter", Font.BOLD, 26));
        lbl.setForeground(c);
        return lbl;
    }

    private JPanel createMiniStatCard(String title, JLabel lblVal) {
        JPanel card = new JPanel(new BorderLayout(0, 5));
        card.setBackground(Color.WHITE);
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 15");
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Inter", Font.BOLD, 11));
        lblTitle.setForeground(new Color(148, 163, 184));
        
        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblVal, BorderLayout.CENTER);
        return card;
    }

    private void updateStats() {
        int tongSuat = danhSachSuatChieu.size();
        int dangChieu = 0, sapChieu = 0;
        double tongPhanTram = 0;
        
        for (SuatChieu data : danhSachSuatChieu) {
            if (data.trangThai == 1) dangChieu++;
            else if (data.trangThai == 0) sapChieu++;
            
            if (data.tongVe > 0) {
                tongPhanTram += ((double) data.veDaBan / data.tongVe) * 100;
            }
        }
        
        lblTongSuat.setText(String.valueOf(tongSuat));
        lblLapDay.setText((tongSuat > 0 ? (int) (tongPhanTram / tongSuat) : 0) + "%");
        lblDangChieu.setText(String.valueOf(dangChieu));
        lblSapChieu.setText(String.valueOf(sapChieu));
    }

    private void renderList(String query) {
        rowsPanel.removeAll();
        double[] weights = {0.08, 0.25, 0.10, 0.12, 0.20, 0.12, 0.13};

        for (SuatChieu data : danhSachSuatChieu) {
            if (query != null && !query.isEmpty() && !data.tenPhim.toLowerCase().contains(query.toLowerCase())) {
                continue;
            }

            JPanel row = new JPanel(new GridBagLayout());
            row.setOpaque(false);
            row.setBorder(new MatteBorder(0, 0, 1, 0, new Color(241, 245, 249)));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));
            row.setPreferredSize(new Dimension(800, 85));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.BOTH;

            gbc.gridx = 0; gbc.weightx = weights[0];
            JLabel lblMaLC = new JLabel("#" + data.maLichChieu);
            lblMaLC.setFont(new Font("Inter", Font.BOLD, 13));
            lblMaLC.setForeground(new Color(51, 65, 85));
            row.add(wrapCol(lblMaLC), gbc);

            gbc.gridx = 1; gbc.weightx = weights[1];
            JPanel pnlPhim = new JPanel(new BorderLayout(15, 0));
            pnlPhim.setOpaque(false);
            JLabel img = new JLabel();
            img.setPreferredSize(new Dimension(45, 60));
            img.setOpaque(true);
            img.setBackground(new Color(241, 245, 249));
            img.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
            img.setHorizontalAlignment(SwingConstants.CENTER);

            // Xử lý load ảnh từ thư mục
            if (data.hinhAnh != null && !data.hinhAnh.trim().isEmpty()) {
                java.io.File imgFile = new java.io.File("src/main/resources/images/" + data.hinhAnh);
                if (imgFile.exists()) {
                    ImageIcon icon = new ImageIcon(imgFile.getAbsolutePath());
                    img.setIcon(new ImageIcon(icon.getImage().getScaledInstance(45, 60, Image.SCALE_SMOOTH)));
                    img.setText("");
                } else {
                    img.setText("IMG");
                }
            } else {
                img.setText("IMG");
            }

            JPanel textPhim = new JPanel(new GridLayout(2, 1, 0, 4));
            textPhim.setOpaque(false);
            JLabel lblTen = new JLabel(data.tenPhim);
            lblTen.setFont(new Font("Inter", Font.BOLD, 14));
            lblTen.setForeground(new Color(15, 23, 42));
            JLabel lblTheLoai = new JLabel(data.theLoai);
            lblTheLoai.setFont(new Font("Inter", Font.PLAIN, 12));
            lblTheLoai.setForeground(textGray);

            textPhim.add(lblTen); textPhim.add(lblTheLoai);
            pnlPhim.add(img, BorderLayout.WEST); pnlPhim.add(textPhim, BorderLayout.CENTER);
            row.add(wrapCol(pnlPhim), gbc);

            gbc.gridx = 2; gbc.weightx = weights[2];
            JLabel lblPhong = new JLabel(data.tenPhong);
            lblPhong.setFont(new Font("Inter", Font.BOLD, 13));
            lblPhong.setForeground(new Color(51, 65, 85));
            row.add(wrapCol(lblPhong), gbc);

            gbc.gridx = 3; gbc.weightx = weights[3];
            JLabel lblThoiGian = new JLabel("<html><b style='color:#0f172a; font-size:11px;'>" + data.getGioChieuUI() + " - " + data.getGioKetThucUI() + "</b><br><span style='color:#64748b;'>" + data.getNgayChieuUI() + "</span></html>");
            row.add(wrapCol(lblThoiGian), gbc);

            gbc.gridx = 4; gbc.weightx = weights[4];
            int pct = (data.tongVe > 0) ? (int) (((double) data.veDaBan / data.tongVe) * 100) : 0;
            JProgressBar bar = new JProgressBar(0, 100);
            bar.setValue(pct);
            bar.putClientProperty(FlatClientProperties.STYLE, "arc: 10; height: 6");
            bar.setForeground(pct >= 90 ? new Color(239, 68, 68) : primaryBlue);
            
            JPanel pnlProgress = new JPanel(new BorderLayout(0, 5));
            pnlProgress.setOpaque(false);
            JLabel lblProgressText = new JLabel(data.veDaBan + "/" + data.tongVe + " (" + pct + "%)");
            lblProgressText.setFont(new Font("Inter", Font.BOLD, 12));
            lblProgressText.setForeground(new Color(71, 85, 105));
            pnlProgress.add(lblProgressText, BorderLayout.NORTH);
            pnlProgress.add(bar, BorderLayout.CENTER);
            row.add(wrapCol(pnlProgress), gbc);

            gbc.gridx = 5; gbc.weightx = weights[5];
            JLabel lblStatus = new JLabel(data.getTrangThaiText(), SwingConstants.CENTER);
            lblStatus.setFont(new Font("Inter", Font.BOLD, 11));
            lblStatus.setOpaque(true);
            
            if (data.trangThai == 1) {
                lblStatus.putClientProperty(FlatClientProperties.STYLE, "arc: 10; background: #dcfce7; foreground: #166534; padding: 4,8,4,8");
            } else if (data.trangThai == 0) {
                lblStatus.putClientProperty(FlatClientProperties.STYLE, "arc: 10; background: #fef3c7; foreground: #92400e; padding: 4,8,4,8");
            } else {
                lblStatus.putClientProperty(FlatClientProperties.STYLE, "arc: 10; background: #f1f5f9; foreground: #64748b; padding: 4,8,4,8");
            }
            JPanel pnlStatus = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            pnlStatus.setOpaque(false);
            pnlStatus.add(lblStatus);
            row.add(wrapCol(pnlStatus), gbc);

            gbc.gridx = 6; gbc.weightx = weights[6];
            JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            pnlActions.setOpaque(false);
            
            JButton btnEdit = new JButton("✎");
            btnEdit.putClientProperty(FlatClientProperties.STYLE, "arc: 10; background: #f1f5f9; foreground: #3b82f6; borderWidth: 0");
            btnEdit.setPreferredSize(new Dimension(35, 35));
            btnEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnEdit.addActionListener(e -> showForm(data));
            
            JButton btnDelete = new JButton("🗑");
            btnDelete.putClientProperty(FlatClientProperties.STYLE, "arc: 10; background: #fff1f2; foreground: #e11d48; borderWidth: 0");
            btnDelete.setPreferredSize(new Dimension(35, 35));
            btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnDelete.addActionListener(e -> {
                if (JOptionPane.showConfirmDialog(this, "Xóa suất chiếu phim " + data.tenPhim + "?", "Xác nhận", JOptionPane.YES_NO_OPTION) == 0) {
                    if (lichChieuDAO.xoaLichChieu(data.maLichChieu)) {
                        JOptionPane.showMessageDialog(this, "Xóa thành công!");
                        loadDataFromDB();
                    } else {
                        JOptionPane.showMessageDialog(this, "Xóa thất bại!");
                    }
                }
            });
            
            pnlActions.add(btnEdit);
            pnlActions.add(Box.createHorizontalStrut(10));
            pnlActions.add(btnDelete);
            row.add(wrapCol(pnlActions), gbc);

            rowsPanel.add(row);
        }
        updateStats();
        rowsPanel.revalidate();
        rowsPanel.repaint();
    }

    private void showForm(SuatChieu dataToEdit) {
        FormSuatChieu f = new FormSuatChieu(
            SwingUtilities.getWindowAncestor(this), 
            dataToEdit == null ? "Thêm Suất Chiếu" : "Cập Nhật Suất Chiếu", 
            dataToEdit != null
        );
        
        if (dataToEdit != null) {
            f.setData(
                String.valueOf(dataToEdit.maLichChieu), 
                dataToEdit.tenPhim, 
                dataToEdit.tenPhong, 
                dataToEdit.getGioChieuUI(), 
                dataToEdit.getGioKetThucUI(), 
                dataToEdit.getNgayChieuUI()
            );
        }
        f.setVisible(true);

        if (f.isSaved()) {
            String tgChieu = formatToSQLTimestamp(f.getNgay(), f.getGio());
            String tgKetThuc = formatToSQLTimestamp(f.getNgay(), f.getGioKetThuc());
            
            if (dataToEdit == null) {
                SuatChieu newData = new SuatChieu();
                newData.maPhim = f.getMaPhimSelected();
                newData.maPhong = f.getMaPhongSelected();
                newData.tgChieu = tgChieu;
                newData.tgKetThuc = tgKetThuc;
                
                if (lichChieuDAO.themLichChieu(newData)) {
                    JOptionPane.showMessageDialog(this, "Thêm lịch chiếu thành công!");
                } else {
                    JOptionPane.showMessageDialog(this, "Thêm thất bại!");
                }
            } else {
                dataToEdit.maPhim = f.getMaPhimSelected();
                dataToEdit.maPhong = f.getMaPhongSelected();
                dataToEdit.tgChieu = tgChieu;
                dataToEdit.tgKetThuc = tgKetThuc;
                
                if (lichChieuDAO.suaLichChieu(dataToEdit)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật suất chiếu thành công!");
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
                }
            }
            loadDataFromDB();
        }
    }

    private String formatToSQLTimestamp(String dateDDMMYYYY, String timeHHMM) {
        try {
            String[] parts = dateDDMMYYYY.split("/");
            return parts[2] + "-" + parts[1] + "-" + parts[0] + " " + timeHHMM + ":00";
        } catch (Exception e) {
            return "2026-01-01 00:00:00";
        }
    }
}