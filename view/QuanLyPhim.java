package com.mycompany.cinema_system_management.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.mycompany.cinema_system_management.DAO.PhimDAO;
import com.mycompany.cinema_system_management.models.Phim;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QuanLyPhim extends JPanel {

    private JPanel pnlFilmContainer;
    private JTextField txtSearch;
    private JButton btnAdd;
    private List<FilmData> allFilms;
    private PhimDAO phimDAO; 

    public QuanLyPhim() {
        setLayout(new BorderLayout(0, 20)); // Đổi thành BorderLayout(0, 20) để chia khung trên/dưới
        setBackground(new Color(245, 247, 251));
        setBorder(BorderFactory.createEmptyBorder(25, 30, 30, 30));

        allFilms = new ArrayList<>();
        phimDAO = new PhimDAO(); 
        
        // ==========================================
        // 1. TIÊU ĐỀ
        // ==========================================
        JPanel pnlTitle = new JPanel(new BorderLayout());
        pnlTitle.setOpaque(false);
        JLabel lblTitle = new JLabel("Quản lý Phim");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 24));
        lblTitle.setForeground(new Color(15, 23, 42));
        pnlTitle.add(lblTitle, BorderLayout.WEST);

        // ==========================================
        // 2. THỐNG KÊ
        // ==========================================
        JPanel pnlStats = new JPanel(new GridLayout(1, 2, 20, 0));
        pnlStats.setOpaque(false); 
        pnlStats.setPreferredSize(new Dimension(0, 90));
        
        JPanel cardDangChieu = createMiniStatCard("PHIM ĐANG CHIẾU", "Xem danh sách", new Color(34, 197, 94));
        JPanel cardSapChieu = createMiniStatCard("PHIM SẮP CHIẾU", "Xem danh sách", new Color(249, 115, 22));

        cardDangChieu.addMouseListener(new MouseAdapter() { 
            @Override public void mouseClicked(MouseEvent e) { renderFilteredList(true); } 
        });
        
        cardSapChieu.addMouseListener(new MouseAdapter() { 
            @Override public void mouseClicked(MouseEvent e) { renderFilteredList(false); } 
        });

        pnlStats.add(cardDangChieu); 
        pnlStats.add(cardSapChieu);

        // ==========================================
        // 3. THANH CÔNG CỤ (TÌM KIẾM & THÊM - ÉP RA GIỮA)
        // ==========================================
        // Dùng FlowLayout.CENTER để đẩy mọi thứ ra giữa
        JPanel pnlToolBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0)); 
        pnlToolBar.setOpaque(false);
        pnlToolBar.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        txtSearch = new JTextField();
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập tên phim để tìm kiếm nhanh...");
        txtSearch.putClientProperty(FlatClientProperties.STYLE, "arc: 15; focusWidth: 2;");
        txtSearch.setPreferredSize(new Dimension(450, 42));
        txtSearch.addKeyListener(new KeyAdapter() { 
            @Override public void keyReleased(KeyEvent e) { renderFilmList(txtSearch.getText().trim()); } 
        });

        btnAdd = new JButton("+");
        btnAdd.putClientProperty(FlatClientProperties.STYLE, "arc: 15; background: #22c55e; foreground: #ffffff; borderWidth: 0;");
        btnAdd.setFont(new Font("Inter", Font.BOLD, 26)); 
        btnAdd.setPreferredSize(new Dimension(45, 42)); 
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.addActionListener(e -> showFilmForm(null));

        pnlToolBar.add(txtSearch); 
        pnlToolBar.add(btnAdd);
        
        // ==========================================
        // GOM HEADER LẠI VÀ KHÓA LÊN TRÊN CÙNG (NORTH)
        // ==========================================
        JPanel headerContainer = new JPanel(new BorderLayout(0, 20));
        headerContainer.setOpaque(false); 

        JPanel topHalf = new JPanel(new BorderLayout(0, 20));
        topHalf.setOpaque(false);
        topHalf.add(pnlTitle, BorderLayout.NORTH);
        topHalf.add(pnlStats, BorderLayout.CENTER);

        headerContainer.add(topHalf, BorderLayout.NORTH);
        headerContainer.add(pnlToolBar, BorderLayout.CENTER);

        add(headerContainer, BorderLayout.NORTH);

        // ==========================================
        // DANH SÁCH CUỘN BÊN DƯỚI
        // ==========================================
        pnlFilmContainer = new JPanel(); 
        pnlFilmContainer.setLayout(new BoxLayout(pnlFilmContainer, BoxLayout.Y_AXIS)); 
        pnlFilmContainer.setOpaque(false);
        
        JScrollPane scrollPane = new JScrollPane(pnlFilmContainer); 
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); 
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); 
        scrollPane.setOpaque(false); 
        scrollPane.getViewport().setOpaque(false);

        add(scrollPane, BorderLayout.CENTER); 
        
        // Gọi hàm load dữ liệu từ DB
        loadDataFromDatabase();
    }

    // ====================================================
    // HÀM CHUYÊN KÉO DATA TỪ DB VÀ REFRESH LẠI MÀN HÌNH
    // ====================================================
    private void loadDataFromDatabase() {
        allFilms.clear();
        List<Phim> danhSachPhimDB = phimDAO.getDanhSachPhim();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date today = new Date();

        for (Phim p : danhSachPhimDB) {
            String ngayChieu = p.getNgayCongChieu() != null ? sdf.format(p.getNgayCongChieu()) : "";
            String ngayKetThuc = p.getNgayKetThuc() != null ? sdf.format(p.getNgayKetThuc()) : "";
            boolean isDangChieu = true;
            
            if (p.getNgayCongChieu() != null && today.before(p.getNgayCongChieu())) {
                isDangChieu = false;
            }

            FilmData filmGiaoDien = new FilmData(p.getMaPhim(), p.getTenPhim(), p.getTheLoai(), p.getThoiLuong(), p.getDaoDien(), ngayChieu, ngayKetThuc, isDangChieu);
            filmGiaoDien.tomTat = p.getTomTat() != null ? p.getTomTat() : "";
            filmGiaoDien.doTuoi = p.getDoTuoi();
            filmGiaoDien.hinhAnh = p.getHinhAnh() != null ? p.getHinhAnh() : "";
            allFilms.add(filmGiaoDien);
        }
        renderFilmList(""); // Tải xong thì vẽ lên màn hình
    }

    private void renderFilmList(String query) {
        pnlFilmContainer.removeAll();
        for (FilmData film : allFilms) {
            if (query.isEmpty() || film.tenPhim.toLowerCase().contains(query.toLowerCase())) { 
                pnlFilmContainer.add(createFilmRow(film)); 
                pnlFilmContainer.add(Box.createVerticalStrut(15)); 
            }
        }
        pnlFilmContainer.revalidate(); 
        pnlFilmContainer.repaint();
    }

    private void renderFilteredList(boolean dangChieu) {
        pnlFilmContainer.removeAll();
        for (FilmData film : allFilms) {
            if (film.isDangChieu == dangChieu) { 
                pnlFilmContainer.add(createFilmRow(film)); 
                pnlFilmContainer.add(Box.createVerticalStrut(15)); 
            }
        }
        pnlFilmContainer.revalidate(); 
        pnlFilmContainer.repaint();
    }

    private JPanel createFilmRow(FilmData data) {
        JPanel row = new JPanel(new BorderLayout(20, 0));
        row.setBackground(Color.WHITE); 
        row.putClientProperty(FlatClientProperties.STYLE, "arc: 20");
        row.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20)); 
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        // --- ĐOẠN HIỂN THỊ ẢNH TRÊN DANH SÁCH ---
        JLabel lblImg = new JLabel(); 
        lblImg.setPreferredSize(new Dimension(75, 110)); 
        lblImg.setBackground(new Color(241, 245, 249)); 
        lblImg.setOpaque(true); 
        lblImg.setHorizontalAlignment(SwingConstants.CENTER);
        
        if (data.hinhAnh != null && !data.hinhAnh.trim().isEmpty()) {
            File imgFile = new File("src/main/resources/images/" + data.hinhAnh);
            if (imgFile.exists()) {
                ImageIcon icon = new ImageIcon(imgFile.getAbsolutePath());
                lblImg.setIcon(new ImageIcon(icon.getImage().getScaledInstance(75, 110, Image.SCALE_SMOOTH)));
                lblImg.setText("");
            } else {
                lblImg.setText("IMG");
            }
        } else {
            lblImg.setText("IMG");
        }
        // ----------------------------------------
        
        JPanel pnlInfo = new JPanel(new GridLayout(2, 1, 0, 5)); 
        pnlInfo.setOpaque(false);
        
        JLabel lblName = new JLabel("[Mã:" + data.maPhim + "] " + data.tenPhim);
        lblName.setFont(new Font("Inter", Font.BOLD, 17));
        String status = data.isDangChieu ? "<b style='color:#22c55e;'>Đang chiếu</b>" : "<b style='color:#f59e0b;'>Sắp chiếu</b>";
        
        String detailText = String.format("<html><body style='color: #64748b;'>%s | Thể loại: <b>%s</b> | Thời lượng: <b>%d phút</b><br>Đạo diễn: <b>%s</b> | Khởi chiếu: %s - Kết thúc: %s | Độ tuổi: T%d</body></html>", 
                                          status, data.theLoai, data.thoiLuong, data.daoDien, data.ngayChieu, data.ngayKetThuc != null ? data.ngayKetThuc : "Chưa rõ", data.doTuoi);
        JLabel lblDetails = new JLabel(detailText); 
        lblDetails.setFont(new Font("Inter", Font.PLAIN, 13));
        pnlInfo.add(lblName); 
        pnlInfo.add(lblDetails);

        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 30)); 
        pnlActions.setOpaque(false);
        
        JButton btnEdit = new JButton("✎"); 
        JButton btnDelete = new JButton("🗑");
        
        btnEdit.addActionListener(e -> showFilmForm(data));
        
        // CÚ CLICK XÓA PHIM GỌI DATABASE
        btnDelete.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(null, "Xóa phim: " + data.tenPhim + "?\nCảnh báo: Hành động này sẽ xóa vĩnh viễn trên Database!", "Xác nhận", JOptionPane.YES_NO_OPTION) == 0) { 
                boolean thanhCong = phimDAO.xoaPhim(data.maPhim); 
                if (thanhCong) {
                    JOptionPane.showMessageDialog(null, "Đã xóa thành công!");
                    loadDataFromDatabase(); 
                } else {
                    JOptionPane.showMessageDialog(null, "Xóa thất bại!");
                }
            }
        });

        styleActionButton(btnEdit, "#F1F5F9", "#3b82f6"); 
        styleActionButton(btnDelete, "#FFF1F2", "#E11D48");
        
        pnlActions.add(btnEdit); 
        pnlActions.add(btnDelete);
        
        row.add(lblImg, BorderLayout.WEST); 
        row.add(pnlInfo, BorderLayout.CENTER); 
        row.add(pnlActions, BorderLayout.EAST);
        
        return row;
    }

    private void showFilmForm(FilmData data) {
        JDialog dialog = new JDialog((Window) SwingUtilities.getWindowAncestor(this), 
                data == null ? "Thêm Phim Mới" : "Cập Nhật Thông Tin", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout());
        
        final File[] selectedFile = {null}; 
        final ImageIcon[] tempIcon = { (data != null) ? data.poster : null };

        JPanel pnlMain = new JPanel(new BorderLayout(30, 0)); 
        pnlMain.setBackground(Color.WHITE); 
        pnlMain.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // --- PANEL BÊN TRÁI: XỬ LÝ ẢNH ---
        JPanel pnlLeft = new JPanel(new BorderLayout(0, 15)); 
        pnlLeft.setOpaque(false);
        
        JLabel lblPreview = new JLabel(tempIcon[0] == null ? "Chưa có ảnh" : "", SwingConstants.CENTER);
        lblPreview.setPreferredSize(new Dimension(200, 280)); 
        lblPreview.setOpaque(true); 
        lblPreview.setBackground(new Color(248, 250, 252)); 
        lblPreview.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1)); 
        lblPreview.putClientProperty(FlatClientProperties.STYLE, "arc: 15");
        
        if (data != null && data.hinhAnh != null && !data.hinhAnh.isEmpty()) {
            File oldImg = new File("src/main/resources/images/" + data.hinhAnh);
            if (oldImg.exists()) {
                lblPreview.setText("");
                lblPreview.setIcon(new ImageIcon(new ImageIcon(oldImg.getAbsolutePath())
                        .getImage().getScaledInstance(200, 280, Image.SCALE_SMOOTH)));
            }
        }

        JButton btnUpload = new JButton("Chọn ảnh poster"); 
        btnUpload.putClientProperty(FlatClientProperties.STYLE, "arc: 10; background: #e2e8f0; foreground: #0f172a; borderWidth: 0"); 
        btnUpload.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
        btnUpload.setPreferredSize(new Dimension(0, 35));
        
        btnUpload.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                selectedFile[0] = chooser.getSelectedFile(); 
                ImageIcon icon = new ImageIcon(selectedFile[0].getAbsolutePath());
                lblPreview.setText(""); 
                lblPreview.setIcon(new ImageIcon(icon.getImage().getScaledInstance(200, 280, Image.SCALE_SMOOTH))); 
            }
        });
        
        pnlLeft.add(lblPreview, BorderLayout.CENTER); 
        pnlLeft.add(btnUpload, BorderLayout.SOUTH);

        // --- PANEL BÊN PHẢI: CÁC TRƯỜNG NHẬP LIỆU ---
        JPanel pnlFields = new JPanel(new GridBagLayout()); 
        pnlFields.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints(); 
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        gbc.insets = new Insets(8, 10, 15, 10); 
        gbc.weightx = 1.0;

        JTextField fMaPhim = createStyledField(data != null ? String.valueOf(data.maPhim) : "ID Tự động tạo"); 
        fMaPhim.setEditable(false); 
        fMaPhim.setBackground(new Color(241, 245, 249));
        
        JTextField fName = createStyledField(data != null ? data.tenPhim : "");
        JTextField fType = createStyledField(data != null ? data.theLoai : "");
        JTextField fDir = createStyledField(data != null ? data.daoDien : "");
        JTextField fAge = createStyledField(data != null ? String.valueOf(data.doTuoi) : "");
        JTextField fTime = createStyledField(data != null ? String.valueOf(data.thoiLuong) : "");
        
        JLabel lblPhut = new JLabel("phút"); 
        lblPhut.setForeground(new Color(148, 163, 184)); 
        lblPhut.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10)); 
        fTime.putClientProperty("JTextField.trailingComponent", lblPhut);
        
        CustomDatePicker fDate = new CustomDatePicker(); 
        fDate.putClientProperty(FlatClientProperties.STYLE, "arc: 8; padding: 5,10,5,10");
        
        CustomDatePicker fEndDate = new CustomDatePicker(); 
        fEndDate.putClientProperty(FlatClientProperties.STYLE, "arc: 8; padding: 5,10,5,10");
        
        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Đang chiếu", "Sắp chiếu"}); 
        cbStatus.putClientProperty(FlatClientProperties.STYLE, "arc: 8; padding: 5,10,5,10"); 
        cbStatus.setEnabled(false); 

        Runnable updateStatus = () -> {
            try {
                Date selectedDate = new SimpleDateFormat("dd/MM/yyyy").parse(fDate.getText());
                if (new SimpleDateFormat("yyyyMMdd").format(selectedDate).compareTo(new SimpleDateFormat("yyyyMMdd").format(new Date())) > 0) 
                    cbStatus.setSelectedIndex(1); 
                else 
                    cbStatus.setSelectedIndex(0);
            } catch (Exception ex) {}
        };
        
        fDate.setOnDateSelected(updateStatus);
        
        if (data != null && !data.ngayChieu.isEmpty()) fDate.setDate(data.ngayChieu);
        if (data != null && data.ngayKetThuc != null && !data.ngayKetThuc.isEmpty()) fEndDate.setDate(data.ngayKetThuc);
        updateStatus.run();

        JTextArea fTomTat = new JTextArea(data != null ? data.tomTat : "", 4, 20); 
        fTomTat.setLineWrap(true); 
        fTomTat.putClientProperty(FlatClientProperties.STYLE, "arc: 10; border: 1,1,1,1,#e2e8f0; padding: 8,10,8,10");

        int r = 0;
        addFormRow(pnlFields, "Mã phim", fMaPhim, gbc, r++, 0, 2); 
        addFormRow(pnlFields, "Tên phim", fName, gbc, r++, 0, 2);
        addFormRow(pnlFields, "Thể loại", fType, gbc, r, 0, 1);
        addFormRow(pnlFields, "Thời lượng", fTime, gbc, r++, 1, 1);
        addFormRow(pnlFields, "Đạo diễn", fDir, gbc, r, 0, 1);
        addFormRow(pnlFields, "Độ tuổi", fAge, gbc, r++, 1, 1);
        addFormRow(pnlFields, "Khởi chiếu", fDate, gbc, r, 0, 1);
        addFormRow(pnlFields, "Kết thúc", fEndDate, gbc, r++, 1, 1); 
        addFormRow(pnlFields, "Trạng thái", cbStatus, gbc, r++, 0, 2); 
        
        gbc.gridy = r++; 
        gbc.gridx = 0; 
        gbc.gridwidth = 2; 
        JLabel lblTT = new JLabel("Tóm tắt nội dung"); 
        lblTT.setFont(new Font("Inter", Font.BOLD, 12)); 
        lblTT.setForeground(new Color(100, 116, 139));
        
        pnlFields.add(lblTT, gbc); 
        gbc.gridy = r++; 
        pnlFields.add(new JScrollPane(fTomTat), gbc);

        // --- FOOTER: NÚT BẤM ---
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15)); 
        pnlFooter.setBackground(new Color(248, 250, 252));
        
        JButton btnCancel = new JButton("Hủy bỏ"); 
        btnCancel.putClientProperty(FlatClientProperties.STYLE, "arc: 10; background: #ffffff; foreground: #64748b; borderWidth: 1; borderColor: #e2e8f0"); 
        btnCancel.setPreferredSize(new Dimension(100, 40)); 
        btnCancel.addActionListener(e -> dialog.dispose());

        JButton btnSave = new JButton(data == null ? "Tạo phim mới" : "Cập nhật phim"); 
        btnSave.putClientProperty(FlatClientProperties.STYLE, "arc: 10; background: #2563eb; foreground: #ffffff;"); 
        btnSave.setPreferredSize(new Dimension(160, 40)); 
        
        btnSave.addActionListener(e -> {
            // ==========================================
            // BƯỚC 1: KIỂM TRA DỮ LIỆU RỖNG VÀ HỢP LỆ (Dựa theo Activity Diagram)
            // ==========================================
            String tenPhim = fName.getText().trim();
            String theLoai = fType.getText().trim();
            String thoiLuongStr = fTime.getText().trim();
            String daoDien = fDir.getText().trim();
            String doTuoiStr = fAge.getText().trim();

            if (tenPhim.isEmpty() || theLoai.isEmpty() || thoiLuongStr.isEmpty() || daoDien.isEmpty() || fDate.getText().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập đầy đủ thông tin vào các trường bắt buộc!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                return; // Dừng lại không cho đi tiếp
            }

            int thoiLuong = 0;
            int doTuoi = 0;
            
            try {
                thoiLuong = Integer.parseInt(thoiLuongStr);
                if (thoiLuong <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Thời lượng phải là một số nguyên dương!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                if (!doTuoiStr.isEmpty()) {
                    doTuoi = Integer.parseInt(doTuoiStr);
                    if (doTuoi < 0) throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Độ tuổi không hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date ngayChieu = null;
            Date ngayKetThuc = null;
            try {
                ngayChieu = sdf.parse(fDate.getText());
                if (!fEndDate.getText().isEmpty()) {
                    ngayKetThuc = sdf.parse(fEndDate.getText());
                    // Logic nghiệp vụ: Ngày kết thúc không thể trước ngày chiếu
                    if (ngayKetThuc.before(ngayChieu)) {
                        JOptionPane.showMessageDialog(dialog, "Ngày kết thúc phải sau hoặc cùng ngày với Khởi chiếu!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi định dạng ngày tháng!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // ==========================================
            // BƯỚC 2: KIỂM TRA PHIM TỒN TẠI TRONG DATABASE
            // ==========================================
            int currentId = (data != null) ? data.maPhim : -1;
            if (phimDAO.kiemTraPhimTonTai(tenPhim, currentId)) {
                JOptionPane.showMessageDialog(dialog, "Phim '" + tenPhim + "' đã tồn tại trong hệ thống!", "Cảnh báo trùng lặp", JOptionPane.ERROR_MESSAGE);
                return; // Dừng, hiển thị thông báo lỗi như sơ đồ
            }

            // ==========================================
            // BƯỚC 3: XỬ LÝ LƯU (Đã vượt qua mọi bài kiểm tra)
            // ==========================================
            Phim p = new Phim();
            p.setTenPhim(tenPhim);
            p.setTheLoai(theLoai);
            p.setDaoDien(daoDien);
            p.setTomTat(fTomTat.getText());
            p.setThoiLuong(thoiLuong);
            p.setDoTuoi(doTuoi);
            p.setNgayCongChieu(ngayChieu);
            p.setNgayKetThuc(ngayKetThuc);
            
            // Xử lý hình ảnh trước khi lưu
            String fileNameDB = (data != null) ? data.hinhAnh : ""; 
            if (selectedFile[0] != null) {
                fileNameDB = luuAnhVaoThuMuc(selectedFile[0]); 
            }
            p.setHinhAnh(fileNameDB);

            boolean isSuccess = false;
            if (data == null) {
                isSuccess = phimDAO.themPhim(p);
                if(isSuccess) JOptionPane.showMessageDialog(dialog, "Thêm phim mới thành công!");
            } else {
                p.setMaPhim(data.maPhim);
                isSuccess = phimDAO.suaPhim(p);
                if(isSuccess) JOptionPane.showMessageDialog(dialog, "Cập nhật thông tin phim thành công!");
            }
            
            if (isSuccess) {
                loadDataFromDatabase(); 
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Database đang bận hoặc có lỗi rớt mạng, vui lòng thử lại!", "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
            }
        });

        pnlFooter.add(btnCancel); 
        pnlFooter.add(btnSave); 
        pnlMain.add(pnlLeft, BorderLayout.WEST); 
        pnlMain.add(pnlFields, BorderLayout.CENTER);
        
        dialog.add(pnlMain, BorderLayout.CENTER); 
        dialog.add(pnlFooter, BorderLayout.SOUTH); 
        dialog.pack(); 
        dialog.setLocationRelativeTo(this); 
        dialog.setVisible(true);
    }

    private void addFormRow(JPanel p, String label, JComponent field, GridBagConstraints gbc, int row, int col, int width) {
        JPanel cell = new JPanel(new BorderLayout(0, 5)); 
        cell.setOpaque(false); 
        JLabel lbl = new JLabel(label); 
        lbl.setFont(new Font("Inter", Font.BOLD, 12)); 
        lbl.setForeground(new Color(100, 116, 139));
        cell.add(lbl, BorderLayout.NORTH); 
        cell.add(field, BorderLayout.CENTER); 
        gbc.gridy = row; 
        gbc.gridx = col; 
        gbc.gridwidth = width; 
        p.add(cell, gbc);
    }

    private JTextField createStyledField(String text) { 
        JTextField f = new JTextField(text, 20); 
        f.putClientProperty(FlatClientProperties.STYLE, "arc: 8; padding: 5,10,5,10"); 
        return f; 
    }

    private void styleActionButton(JButton btn, String bg, String fg) { 
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 12; background: " + bg + "; foreground: " + fg + "; borderWidth: 0"); 
        btn.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 18)); 
        btn.setPreferredSize(new Dimension(45, 45)); 
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
    }

    private JPanel createMiniStatCard(String title, String val, Color c) { 
        JPanel card = new JPanel(new BorderLayout()); 
        card.setBackground(Color.WHITE); 
        card.setPreferredSize(new Dimension(280, 80)); 
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 20"); 
        card.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20)); 
        card.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
        JLabel t = new JLabel(title); 
        t.setFont(new Font("Inter", Font.BOLD, 11)); 
        t.setForeground(new Color(148, 163, 184)); 
        JLabel v = new JLabel(val); 
        v.setFont(new Font("Inter", Font.BOLD, 22)); 
        v.setForeground(c); 
        card.add(t, BorderLayout.NORTH); 
        card.add(v, BorderLayout.CENTER); 
        return card; 
    }
    
    // ====================================================
    // HÀM COPY ẢNH VÀO THƯ MỤC PROJECT
    // ====================================================
    private String luuAnhVaoThuMuc(File sourceFile) {
        try {
            File destDir = new File("src/main/resources/images");
            if (!destDir.exists()) destDir.mkdirs(); 

            String fileName = sourceFile.getName();
            String extension = fileName.substring(fileName.lastIndexOf("."));
            String newFileName = "poster_" + System.currentTimeMillis() + extension;
            
            File destFile = new File(destDir, newFileName);
            Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            
            return newFileName; 
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    class FilmData {
        int maPhim, thoiLuong, doTuoi; 
        String tenPhim, theLoai, daoDien, ngayChieu, ngayKetThuc, tomTat, hinhAnh;
        ImageIcon poster; 
        boolean isDangChieu;

        public FilmData(int id, String t, String tl, int th, String d, String n, String nk, boolean dc) {
            this.maPhim = id; this.tenPhim = t; this.theLoai = tl; this.thoiLuong = th; this.daoDien = d; 
            this.ngayChieu = n; this.ngayKetThuc = nk; this.isDangChieu = dc; this.doTuoi = 13; 
            this.tomTat = ""; this.hinhAnh = "";
        }
    }

    class CustomDatePicker extends JTextField {
        private LocalDate selectedDate = LocalDate.now(); 
        private YearMonth currentView = YearMonth.now(); 
        private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); 
        private JPopupMenu popup; 
        private Runnable onDateSelected;

        public CustomDatePicker() { 
            setEditable(false); 
            setCursor(new Cursor(Cursor.HAND_CURSOR)); 
            setBackground(Color.WHITE); 
            setText(selectedDate.format(formatter)); 
            addMouseListener(new MouseAdapter() { 
                @Override public void mousePressed(MouseEvent e) { showCalendar(); } 
            }); 
        }

        public void setDate(String dateStr) { 
            try { 
                selectedDate = LocalDate.parse(dateStr, formatter); 
                currentView = YearMonth.from(selectedDate); 
                setText(dateStr); 
            } catch (Exception e) {} 
        }

        public void setOnDateSelected(Runnable action) { this.onDateSelected = action; }

        private void showCalendar() {
            popup = new JPopupMenu(); 
            popup.setBorder(new LineBorder(new Color(60, 60, 60), 1)); 
            
            JPanel mainPanel = new JPanel(new BorderLayout(0, 10)); 
            mainPanel.setBackground(new Color(35, 35, 35)); 
            mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); 
            
            JPanel header = new JPanel(new BorderLayout()); 
            header.setBackground(new Color(35, 35, 35)); 
            
            JButton btnPrev = createCalButton("<"); 
            JButton btnNext = createCalButton(">"); 
            
            JLabel lblMonth = new JLabel("Tháng " + currentView.getMonthValue() + " Năm " + currentView.getYear(), SwingConstants.CENTER); 
            lblMonth.setForeground(Color.WHITE); 
            lblMonth.setFont(new Font("Inter", Font.BOLD, 14));
            
            header.add(btnPrev, BorderLayout.WEST); 
            header.add(lblMonth, BorderLayout.CENTER); 
            header.add(btnNext, BorderLayout.EAST); 
            mainPanel.add(header, BorderLayout.NORTH); 
            
            JPanel grid = new JPanel(new GridLayout(7, 7, 2, 2)); 
            grid.setBackground(new Color(35, 35, 35)); 
            String[] days = {"T2", "T3", "T4", "T5", "T6", "T7", "CN"};
            
            Runnable updateGrid = () -> { 
                grid.removeAll(); 
                for(String d : days) { 
                    JLabel l = new JLabel(d, SwingConstants.CENTER); 
                    l.setForeground(new Color(150, 150, 150)); 
                    l.setFont(new Font("Inter", Font.BOLD, 12)); 
                    grid.add(l); 
                } 
                
                LocalDate firstDay = currentView.atDay(1); 
                int startDay = firstDay.getDayOfWeek().getValue(); 
                for(int i = 1; i < startDay; i++) { grid.add(new JLabel("")); }
                
                int daysInMonth = currentView.lengthOfMonth();
                for(int i = 1; i <= daysInMonth; i++) { 
                    int day = i; 
                    JButton btnDay = new JButton(String.valueOf(day)); 
                    btnDay.setFont(new Font("Inter", Font.PLAIN, 13)); 
                    btnDay.setFocusPainted(false); btnDay.setContentAreaFilled(false); 
                    btnDay.setBorder(null); btnDay.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    
                    if(selectedDate.getYear() == currentView.getYear() && selectedDate.getMonthValue() == currentView.getMonthValue() && selectedDate.getDayOfMonth() == day) { 
                        btnDay.setOpaque(true); btnDay.setBackground(new Color(66, 172, 255)); btnDay.setForeground(Color.BLACK); 
                    } else { 
                        btnDay.setForeground(Color.WHITE); 
                    }
                    
                    btnDay.addActionListener(e -> { 
                        selectedDate = currentView.atDay(day); 
                        setText(selectedDate.format(formatter)); 
                        popup.setVisible(false); 
                        if(onDateSelected != null) onDateSelected.run(); 
                    }); 
                    grid.add(btnDay);
                } 
                grid.revalidate(); grid.repaint();
            };
            
            updateGrid.run(); 
            btnPrev.addActionListener(e -> { currentView = currentView.minusMonths(1); updateGrid.run(); lblMonth.setText("Tháng " + currentView.getMonthValue() + " Năm " + currentView.getYear()); }); 
            btnNext.addActionListener(e -> { currentView = currentView.plusMonths(1); updateGrid.run(); lblMonth.setText("Tháng " + currentView.getMonthValue() + " Năm " + currentView.getYear()); });
            
            mainPanel.add(grid, BorderLayout.CENTER); 
            popup.add(mainPanel); popup.setPreferredSize(new Dimension(280, 260)); 
            popup.show(this, 0, getHeight() + 2);
        }

        private JButton createCalButton(String text) { 
            JButton b = new JButton(text); b.setForeground(Color.WHITE); b.setFocusPainted(false); b.setContentAreaFilled(false); b.setBorder(null); b.setCursor(new Cursor(Cursor.HAND_CURSOR)); return b; 
        }
    }

    public static void main(String[] args) {
        com.formdev.flatlaf.FlatLightLaf.setup();
        EventQueue.invokeLater(() -> {
            JFrame f = new JFrame("Quản Lý Phim");
            f.setSize(1100, 800);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.add(new QuanLyPhim());
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}