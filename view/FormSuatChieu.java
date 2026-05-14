package com.mycompany.cinema_system_management.view;

import com.mycompany.cinema_system_management.DAO.PhimDAO; 
import com.mycompany.cinema_system_management.DAO.PhongDAO;
import com.mycompany.cinema_system_management.models.Phim;
import com.mycompany.cinema_system_management.models.Phong;
import com.mycompany.cinema_system_management.models.ComboItem;

import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FormSuatChieu extends JDialog {
    private Color bgColor = Color.WHITE;
    private Color textDark = new Color(15, 23, 42);

    private JTextField txtMaLichChieu;
    private JComboBox<ComboItem> cbPhim, cbPhong;
    private JComboBox<String> cbGioChieu;
    private JTextField txtGioKetThuc; 
    private CustomDatePicker txtNgayChieu;
    private boolean isSaved = false;
    
    private List<Phim> danhSachPhim; 

    public FormSuatChieu(Window owner, String title, boolean isEditMode) {
        super(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
        setSize(480, 600);
        setLocationRelativeTo(owner);
        setResizable(false);
        getContentPane().setBackground(bgColor);
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 20));
        headerPanel.setBackground(bgColor);
        headerPanel.setBorder(new MatteBorder(0, 0, 1, 0, new Color(241, 245, 249)));
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Inter", Font.BOLD, 20));
        lblTitle.setForeground(textDark);
        headerPanel.add(lblTitle);
        add(headerPanel, BorderLayout.NORTH);

        JPanel bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setBackground(bgColor);
        bodyPanel.setBorder(new EmptyBorder(25, 30, 20, 30));

        txtMaLichChieu = new JTextField(isEditMode ? "" : "Hệ thống tự động cấp");
        txtMaLichChieu.setEditable(false);
        txtMaLichChieu.setBackground(new Color(241, 245, 249));
        bodyPanel.add(createInputGroup("Mã lịch chiếu (ID)", txtMaLichChieu));
        bodyPanel.add(Box.createVerticalStrut(20));

        cbPhim = new JComboBox<>();
        cbPhim.addItem(new ComboItem(0, "-- Chọn phim --"));
        PhimDAO phimDAO = new PhimDAO();
        danhSachPhim = phimDAO.getDanhSachPhim();
        if (danhSachPhim != null) {
            for (Phim p : danhSachPhim) {
                cbPhim.addItem(new ComboItem(p.getMaPhim(), p.getTenPhim()));
            }
        }
        bodyPanel.add(createInputGroup("Phim chiếu (*)", cbPhim));
        bodyPanel.add(Box.createVerticalStrut(20));

        cbPhong = new JComboBox<>();
        cbPhong.addItem(new ComboItem(0, "-- Chọn phòng --"));
        PhongDAO phongDAO = new PhongDAO();
        List<Phong> dsPhong = phongDAO.getDanhSachPhong();
        if (dsPhong != null) {
            for (Phong p : dsPhong) {
                cbPhong.addItem(new ComboItem(p.getMaPhong(), p.getTenPhong()));
            }
        }
        bodyPanel.add(createInputGroup("Phòng chiếu (*)", cbPhong));
        bodyPanel.add(Box.createVerticalStrut(20));

        JPanel timeDatePanel = new JPanel(new GridLayout(2, 2, 20, 15));
        timeDatePanel.setBackground(bgColor);
        
        cbGioChieu = new JComboBox<>(new String[]{
            "08:00", "09:00", "09:30", "10:00", "13:15", "15:00", 
            "18:30", "19:30", "20:15", "21:45", "23:00"
        });
        timeDatePanel.add(createInputGroup("Giờ bắt đầu (*)", cbGioChieu));
        
        txtGioKetThuc = new JTextField("");
        txtGioKetThuc.setEditable(false);
        txtGioKetThuc.setBackground(new Color(241, 245, 249));
        timeDatePanel.add(createInputGroup("Giờ kết thúc (Auto)", txtGioKetThuc));
        
        txtNgayChieu = new CustomDatePicker();
        timeDatePanel.add(createInputGroup("Ngày chiếu (*)", txtNgayChieu));
        timeDatePanel.add(new JLabel(""));

        bodyPanel.add(timeDatePanel);
        add(bodyPanel, BorderLayout.CENTER);

        cbPhim.addActionListener(e -> tinhGioKetThuc());
        cbGioChieu.addActionListener(e -> tinhGioKetThuc());

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        footerPanel.setBackground(new Color(248, 250, 252));
        footerPanel.setBorder(new MatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));
        
        JButton btnCancel = new JButton("Hủy bỏ");
        btnCancel.putClientProperty(FlatClientProperties.STYLE, "arc: 10; background: #ffffff; foreground: #64748b; borderWidth: 1; borderColor: #cbd5e1");
        btnCancel.setPreferredSize(new Dimension(100, 38));
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(e -> dispose());
        
        JButton btnSave = new JButton(isEditMode ? "Cập nhật" : "Tạo suất chiếu");
        btnSave.putClientProperty(FlatClientProperties.STYLE, "arc: 10; background: #2563eb; foreground: #ffffff; borderWidth: 0");
        btnSave.setPreferredSize(new Dimension(140, 38));
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // SỰ KIỆN LƯU GẮN CHỐT CHẶN KIỂM TRA ĐỤNG GIỜ PHÒNG CHIẾU
        btnSave.addActionListener(e -> {
            if (cbPhim.getSelectedIndex() <= 0 || cbPhong.getSelectedIndex() <= 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn đầy đủ Phim và Phòng chiếu!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                return; 
            }
            
            if (txtGioKetThuc.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Hệ thống chưa tính được giờ kết thúc, vui lòng kiểm tra lại thông tin phim!", "Lỗi thời lượng", JOptionPane.ERROR_MESSAGE);
                return; 
            }

            String tgChieu = formatToSQLTimestamp(getNgay(), getGio());
            String tgKetThuc = formatToSQLTimestamp(getNgay(), getGioKetThuc());
            int maPhong = getMaPhongSelected();
            
            int maLC = -1;
            try {
                maLC = Integer.parseInt(txtMaLichChieu.getText().trim());
            } catch (Exception ex) {} 

            com.mycompany.cinema_system_management.DAO.LichChieuDAO dao = new com.mycompany.cinema_system_management.DAO.LichChieuDAO();
            
            if (dao.kiemTraTrungLich(maPhong, tgChieu, tgKetThuc, maLC)) {
                JOptionPane.showMessageDialog(this, "Trùng lịch rồi!\nPhòng [" + getPhong() + "] đã có phim khác chiếu dính vào khung giờ " + getGio() + " - " + getGioKetThuc() + ".\nVui lòng chọn phòng khác hoặc đổi giờ!", "Phát hiện đụng độ", JOptionPane.ERROR_MESSAGE);
                return; 
            }

            isSaved = true;
            dispose();
        });

        footerPanel.add(btnCancel);
        footerPanel.add(btnSave);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private void tinhGioKetThuc() {
        if (cbPhim.getSelectedIndex() <= 0 || cbGioChieu.getSelectedItem() == null) {
            txtGioKetThuc.setText("");
            return;
        }
        
        int maPhim = ((ComboItem) cbPhim.getSelectedItem()).getKey();
        int thoiLuong = 0;
        
        if (danhSachPhim != null) {
            for (Phim p : danhSachPhim) {
                if (p.getMaPhim() == maPhim) {
                    thoiLuong = p.getThoiLuong(); 
                    break;
                }
            }
        }
        
        String gioBatDau = cbGioChieu.getSelectedItem().toString();
        try {
            String[] parts = gioBatDau.split(":");
            int gio = Integer.parseInt(parts[0]);
            int phut = Integer.parseInt(parts[1]);
            
            int tongPhut = (gio * 60) + phut + thoiLuong;
            int gioKetThuc = (tongPhut / 60) % 24; 
            int phutKetThuc = tongPhut % 60;
            
            txtGioKetThuc.setText(String.format("%02d:%02d", gioKetThuc, phutKetThuc));
        } catch (Exception ex) {
            txtGioKetThuc.setText("");
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

    public void setData(String maLC, String phim, String phong, String gio, String gioKetThuc, String ngay) {
        txtMaLichChieu.setText(maLC);
        
        for (int i = 0; i < cbPhim.getItemCount(); i++) {
            if (cbPhim.getItemAt(i).getValue().equals(phim)) {
                cbPhim.setSelectedIndex(i);
                break;
            }
        }
        for (int i = 0; i < cbPhong.getItemCount(); i++) {
            if (cbPhong.getItemAt(i).getValue().equals(phong)) {
                cbPhong.setSelectedIndex(i);
                break;
            }
        }
        
        cbGioChieu.setSelectedItem(gio);
        txtGioKetThuc.setText(gioKetThuc);
        txtNgayChieu.setSelectedDate(ngay);
    }

    private JPanel createInputGroup(String labelText, JComponent inputField) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(bgColor);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Inter", Font.BOLD, 12));
        label.setForeground(new Color(71, 85, 105));
        inputField.setPreferredSize(new Dimension(inputField.getPreferredSize().width, 40));
        inputField.putClientProperty(FlatClientProperties.STYLE, "arc: 8; padding: 5,10,5,10");
        panel.add(label, BorderLayout.NORTH);
        panel.add(inputField, BorderLayout.CENTER);
        return panel;
    }

    public boolean isSaved() { return isSaved; }
    
    public int getMaPhimSelected() { 
        ComboItem item = (ComboItem) cbPhim.getSelectedItem();
        return item != null ? item.getKey() : 0;
    }
    
    public int getMaPhongSelected() { 
        ComboItem item = (ComboItem) cbPhong.getSelectedItem();
        return item != null ? item.getKey() : 0;
    }

    public String getPhim() { return cbPhim.getSelectedItem().toString(); }
    public String getPhong() { return cbPhong.getSelectedItem().toString(); }
    public String getGio() { return cbGioChieu.getSelectedItem().toString(); }
    public String getGioKetThuc() { return txtGioKetThuc.getText().trim(); }
    public String getNgay() { return txtNgayChieu.getText().trim(); }

    class CustomDatePicker extends JTextField {
        private LocalDate selectedDate = LocalDate.now();
        private YearMonth currentView = YearMonth.now();
        private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        private JPopupMenu popup;

        public CustomDatePicker() {
            setEditable(false);
            setBackground(Color.WHITE);
            setText(selectedDate.format(formatter));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) { showCalendar(); }
            });
        }

        public void setSelectedDate(String dateStr) {
            try {
                this.selectedDate = LocalDate.parse(dateStr, formatter);
                this.currentView = YearMonth.from(selectedDate);
                setText(dateStr);
            } catch (Exception e) {}
        }

        private void showCalendar() {
            popup = new JPopupMenu();
            popup.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 1));
            JPanel mainPanel = new JPanel(new BorderLayout(0, 10));
            mainPanel.setBackground(new Color(35, 35, 35));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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
                for (String d : days) {
                    JLabel l = new JLabel(d, SwingConstants.CENTER);
                    l.setForeground(new Color(150, 150, 150));
                    l.setFont(new Font("Inter", Font.BOLD, 12));
                    grid.add(l);
                }
                
                LocalDate firstDay = currentView.atDay(1);
                int startDay = firstDay.getDayOfWeek().getValue();
                for (int i = 1; i < startDay; i++) grid.add(new JLabel(""));
                
                int daysInMonth = currentView.lengthOfMonth();
                for (int i = 1; i <= daysInMonth; i++) {
                    int day = i;
                    JButton btnDay = new JButton(String.valueOf(day));
                    btnDay.setFont(new Font("Inter", Font.PLAIN, 13));
                    btnDay.setFocusPainted(false);
                    btnDay.setContentAreaFilled(false);
                    btnDay.setBorder(null);
                    btnDay.setCursor(new Cursor(Cursor.HAND_CURSOR));

                    if (selectedDate.getYear() == currentView.getYear() && 
                        selectedDate.getMonthValue() == currentView.getMonthValue() && 
                        selectedDate.getDayOfMonth() == day) {
                        btnDay.setOpaque(true);
                        btnDay.setBackground(new Color(66, 172, 255));
                        btnDay.setForeground(Color.BLACK);
                    } else {
                        btnDay.setForeground(Color.WHITE);
                    }
                    
                    btnDay.addActionListener(e -> {
                        selectedDate = currentView.atDay(day);
                        setText(selectedDate.format(formatter));
                        popup.setVisible(false);
                    });
                    grid.add(btnDay);
                }
                grid.revalidate();
                grid.repaint();
            };
            
            updateGrid.run();
            
            btnPrev.addActionListener(e -> {
                currentView = currentView.minusMonths(1);
                updateGrid.run();
                lblMonth.setText("Tháng " + currentView.getMonthValue() + " Năm " + currentView.getYear());
            });
            
            btnNext.addActionListener(e -> {
                currentView = currentView.plusMonths(1);
                updateGrid.run();
                lblMonth.setText("Tháng " + currentView.getMonthValue() + " Năm " + currentView.getYear());
            });
            
            mainPanel.add(grid, BorderLayout.CENTER);
            popup.add(mainPanel);
            popup.setPreferredSize(new Dimension(280, 260));
            popup.show(this, 0, getHeight() + 2);
        }

        private JButton createCalButton(String text) {
            JButton b = new JButton(text);
            b.setForeground(Color.WHITE);
            b.setFocusPainted(false);
            b.setContentAreaFilled(false);
            b.setBorder(null);
            b.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return b;
        }
    }
}