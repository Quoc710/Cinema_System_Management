package com.mycompany.cinema_system_management.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.mycompany.cinema_system_management.DAO.KhoDAO;
import com.mycompany.cinema_system_management.models.SanPham;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

public class QuanLyKhoHang extends JPanel {

    private CardLayout cardLayout;
    private JPanel mainCards;

    private KhoDAO khoDAO = new KhoDAO();

    private PnlDashboard dashboard;
    private PnlNhapHangForm pnlNhapHangForm;
    private PnlLichSuNhap pnlLichSuNhap;

    private final String CARD_DASHBOARD = "DASHBOARD";
    private final String CARD_NHAP_HANG = "NHAP_HANG";
    private final String CARD_LICH_SU = "LICH_SU";

    public QuanLyKhoHang() {
        cardLayout = new CardLayout();
        mainCards = new JPanel(cardLayout);

        setLayout(new BorderLayout());
        setBackground(new Color(241, 245, 249));

        dashboard = new PnlDashboard();
        pnlLichSuNhap = new PnlLichSuNhap();
        pnlNhapHangForm = new PnlNhapHangForm(pnlLichSuNhap);

        mainCards.add(dashboard, CARD_DASHBOARD);
        mainCards.add(pnlNhapHangForm, CARD_NHAP_HANG);
        mainCards.add(pnlLichSuNhap, CARD_LICH_SU);

        add(mainCards);
    }

    // =========================================================
    // TABLE STYLE
    // =========================================================
    private void setupModernTable(JTable table) {
        table.setRowHeight(70);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(false);
        table.setGridColor(new Color(241, 245, 249));
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(false);
        table.setCellSelectionEnabled(false);
        table.setSelectionBackground(new Color(239, 246, 255));
        table.setSelectionForeground(new Color(15, 23, 42));

        table.putClientProperty(
                FlatClientProperties.STYLE,
                "hoverEvents:true;" +
                        "hoverBackground:#F8FAFC;" +
                        "selectionBackground:#EFF6FF;" +
                        "selectionForeground:#0F172A"
        );

        table.setFont(new Font("Inter", Font.PLAIN, 13));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Inter", Font.BOLD, 12));
        header.setBackground(Color.WHITE);
        header.setForeground(new Color(148, 163, 184));
        header.setPreferredSize(new Dimension(0, 42));
        header.setReorderingAllowed(false);
    }

    // =========================================================
    // CUSTOM TABLE CELL RENDERER (Căn giữa)
    // =========================================================
    class CenterTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            lbl.setFont(new Font("Inter", Font.PLAIN, 13));

            if (isSelected) {
                lbl.setBackground(new Color(239, 246, 255));
                lbl.setForeground(new Color(15, 23, 42));
            } else {
                lbl.setBackground(Color.WHITE);
                lbl.setForeground(new Color(15, 23, 42));
            }
            return lbl;
        }
    }

    // =========================================================
    // CUSTOM TABLE CELL RENDERER (Căn trái cho tên sản phẩm)
    // =========================================================
    class LeftTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setHorizontalAlignment(SwingConstants.LEFT);
            lbl.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 10));
            lbl.setFont(new Font("Inter", Font.PLAIN, 13));

            if (isSelected) {
                lbl.setBackground(new Color(239, 246, 255));
                lbl.setForeground(new Color(15, 23, 42));
            } else {
                lbl.setBackground(Color.WHITE);
                lbl.setForeground(new Color(15, 23, 42));
            }
            return lbl;
        }
    }

    // =========================================================
    // DASHBOARD
    // =========================================================
    class PnlDashboard extends JPanel {
        private JTable table;
        private DefaultTableModel tableModel;

        public PnlDashboard() {
            setLayout(new BorderLayout(0, 20));
            setBackground(new Color(241, 245, 249));
            setBorder(new EmptyBorder(25, 25, 25, 25));
            reloadUI();
        }

        public void refreshDashboard() {
            reloadUI();
        }

        private void reloadUI() {
            removeAll();
            add(createHeader(), BorderLayout.NORTH);

            JPanel center = new JPanel(new BorderLayout(0, 20));
            center.setOpaque(false);
            center.add(createStatisticCards(), BorderLayout.NORTH);
            center.add(createTablePanel(), BorderLayout.CENTER);

            add(center, BorderLayout.CENTER);
            revalidate();
            repaint();
        }

        private JPanel createHeader() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setOpaque(false);

            JPanel left = new JPanel();
            left.setOpaque(false);
            left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

            JLabel title = new JLabel("Quản Lý Kho Hàng");
            title.setFont(new Font("Inter", Font.BOLD, 30));

            JLabel sub = new JLabel("Theo dõi tồn kho thực phẩm, đồ uống và vật tư.");
            sub.setFont(new Font("Inter", Font.PLAIN, 14));
            sub.setForeground(new Color(100, 116, 139));

            left.add(title);
            left.add(Box.createVerticalStrut(5));
            left.add(sub);

            JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            right.setOpaque(false);

            JButton btnTraCuu = createWhiteButton("Tra cứu sản phẩm sắp hết");
            JButton btnNhap = createBlueButton("+ Nhập hàng");

            btnNhap.addActionListener(e -> cardLayout.show(mainCards, CARD_NHAP_HANG));
            btnTraCuu.addActionListener(e -> openTraCuuSanPhamSapHetDialog());

            right.add(btnTraCuu);
            right.add(btnNhap);

            panel.add(left, BorderLayout.WEST);
            panel.add(right, BorderLayout.EAST);
            return panel;
        }

        private JPanel createStatisticCards() {
            JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
            panel.setOpaque(false);

            Map<String, Object> stats = khoDAO.getThongKeKho();
            int tongSP = (int) stats.getOrDefault("tongSP", 0);
            int sapHet = (int) stats.getOrDefault("sapHet", 0);
            double giaTri = (double) stats.getOrDefault("giaTri", 0.0);
            int donNhap = (int) stats.getOrDefault("donNhap", 0);

            panel.add(createCard("TỔNG SẢN PHẨM", String.valueOf(tongSP), "Trong kho", new Color(37, 99, 235)));
            panel.add(createCard("SẮP HẾT HÀNG", String.valueOf(sapHet), "Cần nhập", new Color(249, 115, 22)));
            panel.add(createCard("GIÁ TRỊ KHO", String.format("%,.0f", giaTri), "VNĐ", new Color(15, 23, 42)));
            panel.add(createCard("ĐƠN NHẬP THÁNG", String.valueOf(donNhap), "Phiếu", new Color(34, 197, 94)));

            return panel;
        }

        private JPanel createCard(String title, String value, String sub, Color color) {
            RoundedPanel card = new RoundedPanel(25, Color.WHITE);
            card.setLayout(new BorderLayout());
            card.setBorder(new EmptyBorder(18, 20, 18, 20));
            card.setPreferredSize(new Dimension(250, 120));

            JPanel content = new JPanel();
            content.setOpaque(false);
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel lblTitle = new JLabel(title);
            lblTitle.setForeground(new Color(148, 163, 184));
            lblTitle.setFont(new Font("Inter", Font.BOLD, 12));
            lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

            JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            valuePanel.setOpaque(false);
            valuePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel lblValue = new JLabel(value);
            lblValue.setFont(new Font("Inter", Font.BOLD, 28));
            lblValue.setForeground(color);

            JLabel lblSub = new JLabel(sub);
            lblSub.setFont(new Font("Inter", Font.PLAIN, 14));
            lblSub.setForeground(new Color(100, 116, 139));

            valuePanel.add(lblValue);
            valuePanel.add(lblSub);

            content.add(lblTitle);
            content.add(Box.createVerticalStrut(15));
            content.add(valuePanel);

            card.add(content, BorderLayout.WEST);
            return card;
        }

        private JPanel createTablePanel() {
            RoundedPanel wrapper = new RoundedPanel(25, Color.WHITE);
            wrapper.setLayout(new BorderLayout());
            wrapper.setBorder(new EmptyBorder(20, 20, 20, 20));

            String[] cols = {"TÊN SẢN PHẨM", "MÃ SP", "GIÁ BÁN", "TỒN KHO", "TRẠNG THÁI", "HÀNH ĐỘNG"};

            tableModel = new DefaultTableModel(cols, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 5;
                }
            };

            table = new JTable(tableModel);
            setupModernTable(table);

            table.getColumnModel().getColumn(0).setCellRenderer(new LeftTableCellRenderer());
            table.getColumnModel().getColumn(1).setCellRenderer(new CenterTableCellRenderer());
            table.getColumnModel().getColumn(2).setCellRenderer(new CenterTableCellRenderer());
            table.getColumnModel().getColumn(3).setCellRenderer(new CenterTableCellRenderer());
            table.getColumnModel().getColumn(4).setCellRenderer(new StatusRenderer());
            table.getColumnModel().getColumn(5).setCellRenderer(new ActionButtonRenderer());
            table.getColumnModel().getColumn(5).setCellEditor(new ActionButtonEditor());

            table.getColumnModel().getColumn(5).setPreferredWidth(200);
            table.getColumnModel().getColumn(5).setMinWidth(180);

            loadData();

            JScrollPane scroll = new JScrollPane(table);
            scroll.setBorder(null);
            wrapper.add(scroll, BorderLayout.CENTER);

            JPanel container = new JPanel(new BorderLayout());
            container.setOpaque(false);
            container.add(wrapper);
            return container;
        }

        private void loadData() {
            tableModel.setRowCount(0);
            List<SanPham> list = khoDAO.getDanhSachKho("", "Tất cả");
            for (SanPham sp : list) {
                tableModel.addRow(new Object[]{
                        sp.getTenSP(),
                        sp.getMaSP(),
                        String.format("%,.0fđ", sp.getGiaBan()),
                        sp.getTonKho(),
                        sp.getStatusString(),
                        "Nhập ngay"
                });
            }
        }
    }

    // =========================================================
    // NHẬP HÀNG
    // =========================================================
    class PnlNhapHangForm extends JPanel {
        private JComboBox<String> cboSanPham;
        private JTextField txtSoLuong;
        private JTextField txtDVT;
        private JTextField txtTonKho;
        private JPanel historyContainer;
        private PnlLichSuNhap pnlLichSuNhap;

        public PnlNhapHangForm(PnlLichSuNhap pnlLichSuNhap) {
            this.pnlLichSuNhap = pnlLichSuNhap;

            setLayout(new BorderLayout(20, 0));
            setBackground(new Color(241, 245, 249));
            setBorder(new EmptyBorder(25, 25, 25, 25));

            // Left Panel
            RoundedPanel leftPanel = new RoundedPanel(30, Color.WHITE);
            leftPanel.setLayout(new BorderLayout());
            leftPanel.setBorder(new EmptyBorder(40, 50, 50, 50));

            JPanel contentWrapper = new JPanel();
            contentWrapper.setOpaque(false);
            contentWrapper.setLayout(new BoxLayout(contentWrapper, BoxLayout.Y_AXIS));

            // Title
            JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            titlePanel.setOpaque(false);
            JPanel titleTextGroup = new JPanel();
            titleTextGroup.setOpaque(false);
            titleTextGroup.setLayout(new BoxLayout(titleTextGroup, BoxLayout.Y_AXIS));

            JLabel title = new JLabel("Nhập hàng vào kho");
            title.setFont(new Font("Inter", Font.BOLD, 28));
            JLabel sub = new JLabel("Thêm sản phẩm hoặc cập nhật số lượng tồn kho.");
            sub.setFont(new Font("Inter", Font.PLAIN, 14));
            sub.setForeground(new Color(100, 116, 139));

            titleTextGroup.add(title);
            titleTextGroup.add(Box.createVerticalStrut(6));
            titleTextGroup.add(sub);
            titlePanel.add(titleTextGroup);

            // Form Grid
            JPanel grid = new JPanel(new GridBagLayout());
            grid.setOpaque(false);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(10, 0, 15, 30);

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 0.5;
            cboSanPham = new JComboBox<>();
            cboSanPham.setPreferredSize(new Dimension(220, 40));
            grid.add(createFieldPanel("Sản phẩm", cboSanPham), gbc);

            gbc.gridx = 1;
            txtDVT = new JTextField();
            styleTextField(txtDVT);
            txtDVT.setEditable(false);
            txtDVT.setEnabled(false);
            txtDVT.setBackground(new Color(245, 245, 245));
            txtDVT.setDisabledTextColor(new Color(100, 116, 139));
            grid.add(createFieldPanel("Đơn vị tính", txtDVT), gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            grid.add(createFieldPanel("Số lượng nhập", createQuantitySpinner()), gbc);

            gbc.gridx = 1;
            txtTonKho = new JTextField();
            styleTextField(txtTonKho);
            txtTonKho.setEditable(false);
            txtTonKho.setEnabled(false);
            txtTonKho.setBackground(new Color(245, 245, 245));
            txtTonKho.setDisabledTextColor(new Color(100, 116, 139));
            grid.add(createFieldPanel("Tồn kho hiện tại", txtTonKho), gbc);

            // Buttons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            buttonPanel.setOpaque(false);
            JButton btnSave = createBlueButton("Xác nhận nhập kho");
            JButton btnCancel = createWhiteButton("Hủy");
            btnSave.setPreferredSize(new Dimension(180, 42));
            btnCancel.setPreferredSize(new Dimension(100, 42));

            buttonPanel.add(btnSave);
            buttonPanel.add(Box.createHorizontalStrut(12));
            buttonPanel.add(btnCancel);

            contentWrapper.add(titlePanel);
            contentWrapper.add(Box.createVerticalStrut(35));
            contentWrapper.add(grid);
            contentWrapper.add(Box.createVerticalStrut(30));
            contentWrapper.add(buttonPanel);
            contentWrapper.add(Box.createVerticalStrut(20));

            leftPanel.add(contentWrapper, BorderLayout.NORTH);

            // Right Panel - History
            RoundedPanel historyPanel = new RoundedPanel(30, Color.WHITE);
            historyPanel.setPreferredSize(new Dimension(320, 0));
            historyPanel.setLayout(new BorderLayout());
            historyPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

            JPanel topHis = new JPanel(new BorderLayout());
            topHis.setOpaque(false);
            topHis.setBorder(new EmptyBorder(25, 25, 15, 25));

            JLabel hisTitle = new JLabel("LỊCH SỬ VỪA NHẬP");
            hisTitle.setFont(new Font("Inter", Font.BOLD, 18));

            JLabel viewAll = new JLabel("Xem tất cả →");
            viewAll.setForeground(new Color(37, 99, 235));
            viewAll.setCursor(new Cursor(Cursor.HAND_CURSOR));
            viewAll.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    cardLayout.show(mainCards, CARD_LICH_SU);
                }
            });

            topHis.add(hisTitle, BorderLayout.WEST);
            topHis.add(viewAll, BorderLayout.EAST);
            historyPanel.add(topHis, BorderLayout.NORTH);

            historyContainer = new JPanel();
            historyContainer.setOpaque(false);
            historyContainer.setLayout(new BoxLayout(historyContainer, BoxLayout.Y_AXIS));

            JScrollPane scroll = new JScrollPane(historyContainer);
            scroll.setBorder(null);
            scroll.getVerticalScrollBar().setUnitIncrement(16);
            scroll.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
            historyPanel.add(scroll, BorderLayout.CENTER);

            add(leftPanel, BorderLayout.CENTER);
            add(historyPanel, BorderLayout.EAST);

            // Events
            loadComboSanPham();
            loadHistory();
            cboSanPham.addActionListener(e -> updateProductInfo());

            btnSave.addActionListener(e -> handleSave());
            btnCancel.addActionListener(e -> cardLayout.show(mainCards, CARD_DASHBOARD));

            if (cboSanPham.getItemCount() > 0) {
                cboSanPham.setSelectedIndex(0);
                updateProductInfo();
            }
        }

        private JPanel createFieldPanel(String label, JComponent comp) {
            JPanel p = new JPanel();
            p.setOpaque(false);
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

            JLabel lbl = new JLabel(label);
            lbl.setFont(new Font("Inter", Font.BOLD, 14));
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

            comp.setAlignmentX(Component.LEFT_ALIGNMENT);
            comp.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));

            p.add(lbl);
            p.add(Box.createVerticalStrut(8));
            p.add(comp);
            return p;
        }

        private JPanel createQuantitySpinner() {
            JPanel p = new JPanel(new BorderLayout());
            p.setPreferredSize(new Dimension(140, 40));
            p.setBackground(new Color(248, 250, 252));
            p.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));

            JButton btnMinus = new JButton("−");
            JButton btnPlus = new JButton("+");

            btnMinus.setFont(new Font("Segoe UI", Font.BOLD, 16));
            btnMinus.setForeground(new Color(100, 116, 139));
            btnMinus.setBackground(new Color(241, 245, 249));
            btnMinus.setFocusPainted(false);
            btnMinus.setBorderPainted(false);
            btnMinus.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnMinus.setPreferredSize(new Dimension(40, 38));

            btnPlus.setFont(new Font("Segoe UI", Font.BOLD, 16));
            btnPlus.setForeground(new Color(100, 116, 139));
            btnPlus.setBackground(new Color(241, 245, 249));
            btnPlus.setFocusPainted(false);
            btnPlus.setBorderPainted(false);
            btnPlus.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnPlus.setPreferredSize(new Dimension(40, 38));

            txtSoLuong = new JTextField("1");
            txtSoLuong.setHorizontalAlignment(JTextField.CENTER);
            txtSoLuong.setFont(new Font("Inter", Font.BOLD, 15));
            txtSoLuong.setBorder(null);
            txtSoLuong.setOpaque(false);
            txtSoLuong.setBackground(new Color(248, 250, 252));

            // DocumentFilter - chỉ cho phép nhập số
            ((AbstractDocument) txtSoLuong.getDocument()).setDocumentFilter(new DocumentFilter() {
                @Override
                public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                    String newText = fb.getDocument().getText(0, fb.getDocument().getLength()) + string;
                    if (newText.matches("\\d*")) {
                        super.insertString(fb, offset, string, attr);
                    } else {
                        Toolkit.getDefaultToolkit().beep();
                    }
                }

                @Override
                public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                    String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                    String newText = currentText.substring(0, offset) + text + currentText.substring(offset + length);
                    if (newText.matches("\\d*")) {
                        super.replace(fb, offset, length, text, attrs);
                    } else {
                        Toolkit.getDefaultToolkit().beep();
                    }
                }
            });

            txtSoLuong.addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusLost(java.awt.event.FocusEvent evt) {
                    validateQuantity();
                }
            });

            txtSoLuong.addActionListener(e -> validateQuantity());

            btnMinus.addActionListener(e -> {
                try {
                    String text = txtSoLuong.getText().trim();
                    if (text.isEmpty()) {
                        showQuantityError("Số lượng không được để trống!");
                        return;
                    }
                    int sl = Integer.parseInt(text);
                    if (sl > 1) {
                        txtSoLuong.setText(String.valueOf(sl - 1));
                        resetTextFieldStyle();
                    } else {
                        showQuantityError("Số lượng không thể nhỏ hơn 1!");
                    }
                } catch (NumberFormatException ex) {
                    showQuantityError("Vui lòng nhập số hợp lệ!");
                }
            });

            btnPlus.addActionListener(e -> {
                try {
                    String text = txtSoLuong.getText().trim();
                    if (text.isEmpty()) {
                        showQuantityError("Số lượng không được để trống!");
                        return;
                    }
                    int sl = Integer.parseInt(text);
                    int newSl = sl + 1;
                    if (newSl <= 9999) {
                        txtSoLuong.setText(String.valueOf(newSl));
                        resetTextFieldStyle();
                    } else {
                        showQuantityError("Số lượng không được vượt quá 9999!");
                    }
                } catch (NumberFormatException ex) {
                    showQuantityError("Vui lòng nhập số hợp lệ!");
                }
            });

            p.add(btnMinus, BorderLayout.WEST);
            p.add(txtSoLuong, BorderLayout.CENTER);
            p.add(btnPlus, BorderLayout.EAST);
            return p;
        }

        private void validateQuantity() {
            try {
                String text = txtSoLuong.getText().trim();
                if (text.isEmpty()) {
                    showQuantityError("Số lượng không được để trống!");
                    return;
                }
                int sl = Integer.parseInt(text);
                if (sl <= 0) {
                    showQuantityError("Số lượng phải lớn hơn 0!");
                } else if (sl > 9999) {
                    showQuantityError("Số lượng không được vượt quá 9999!");
                } else {
                    resetTextFieldStyle();
                }
            } catch (NumberFormatException ex) {
                showQuantityError("Vui lòng chỉ nhập số!");
            }
        }

        private void showQuantityError(String message) {
            txtSoLuong.setBackground(new Color(254, 226, 226));
            txtSoLuong.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 38, 38), 2),
                    new EmptyBorder(0, 12, 0, 12)
            ));

            JOptionPane.showMessageDialog(this, message, "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);

            txtSoLuong.setText("");
            txtSoLuong.requestFocus();
            resetTextFieldStyle();
        }

        private void resetTextFieldStyle() {
            txtSoLuong.setBackground(new Color(248, 250, 252));
            txtSoLuong.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(226, 232, 240)),
                    new EmptyBorder(0, 12, 0, 12)
            ));
        }

        private void styleTextField(JTextField txt) {
            txt.setFont(new Font("Inter", Font.PLAIN, 14));
            txt.setBackground(new Color(248, 250, 252));
            txt.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(226, 232, 240)),
                    new EmptyBorder(0, 12, 0, 12)
            ));
        }

        public void selectProduct(int maSP) {
            for (int i = 0; i < cboSanPham.getItemCount(); i++) {
                String item = cboSanPham.getItemAt(i);
                if (item.startsWith(maSP + " -")) {
                    cboSanPham.setSelectedIndex(i);
                    updateProductInfo();
                    break;
                }
            }
        }

        private void updateProductInfo() {
            try {
                String item = cboSanPham.getSelectedItem().toString();
                int maSP = Integer.parseInt(item.split(" - ")[0]);
                SanPham sp = khoDAO.getSanPhamById(maSP);
                if (sp != null) {
                    txtDVT.setText(sp.getDonViTinh());
                    txtTonKho.setText(String.valueOf(sp.getTonKho()));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        private void loadComboSanPham() {
            cboSanPham.removeAllItems();
            List<SanPham> list = khoDAO.getDanhSachKho("", "Tất cả");
            for (SanPham sp : list) {
                cboSanPham.addItem(sp.getMaSP() + " - " + sp.getTenSP());
            }
        }

        private void loadHistory() {
            historyContainer.removeAll();
            List<String[]> list = khoDAO.getTop3LichSuNhap();
            for (String[] row : list) {
                JPanel item = new JPanel();
                item.setLayout(new BoxLayout(item, BoxLayout.Y_AXIS));
                item.setOpaque(false);
                item.setBorder(new EmptyBorder(15, 20, 15, 20));

                JLabel ten = new JLabel(row[0]);
                ten.setFont(new Font("Inter", Font.BOLD, 14));

                JLabel sl = new JLabel("+" + row[1]);
                sl.setFont(new Font("Inter", Font.BOLD, 15));
                sl.setForeground(new Color(37, 99, 235));

                JLabel time = new JLabel(row[2]);
                time.setForeground(new Color(100, 116, 139));

                item.add(ten);
                item.add(Box.createVerticalStrut(8));
                item.add(sl);
                item.add(Box.createVerticalStrut(4));
                item.add(time);

                historyContainer.add(item);
                historyContainer.add(new JSeparator());
            }
            historyContainer.revalidate();
            historyContainer.repaint();
        }

        private void refreshLichSuNhap() {
            if (pnlLichSuNhap != null) {
                pnlLichSuNhap.refreshData();
            }
        }

        private void handleSave() {
            try {
                String item = cboSanPham.getSelectedItem().toString();
                int maSP = Integer.parseInt(item.split(" - ")[0]);
                String soLuongText = txtSoLuong.getText().trim();

                if (soLuongText.isEmpty()) {
                    showQuantityError("Số lượng không được để trống!");
                    return;
                }

                int soLuong = Integer.parseInt(soLuongText);

                if (soLuong <= 0) {
                    showQuantityError("Số lượng nhập phải lớn hơn 0!");
                    return;
                }

                if (soLuong > 9999) {
                    showQuantityError("Số lượng không được vượt quá 9999!");
                    return;
                }

                boolean success = khoDAO.nhapKho(maSP, soLuong);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Nhập kho thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    dashboard.refreshDashboard();
                    loadHistory();
                    refreshLichSuNhap();
                    txtSoLuong.setText("");
                    updateProductInfo();
                    cardLayout.show(mainCards, CARD_DASHBOARD);
                } else {
                    JOptionPane.showMessageDialog(this, "Nhập kho thất bại!\nVui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                showQuantityError("Dữ liệu không hợp lệ! Vui lòng nhập số.");
            }
        }

        private JButton createBlueButton(String text) {
            JButton btn = new JButton(text);
            btn.putClientProperty(FlatClientProperties.STYLE,
                    "arc:14;background:#2563EB;foreground:#FFFFFF;font:bold +1;borderWidth:0");
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return btn;
        }

        private JButton createWhiteButton(String text) {
            JButton btn = new JButton(text);
            btn.putClientProperty(FlatClientProperties.STYLE,
                    "arc:14;background:#FFFFFF;borderColor:#E2E8F0;font:bold +1");
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return btn;
        }
    }

    // =========================================================
    // LỊCH SỬ
    // =========================================================
    class PnlLichSuNhap extends JPanel {
        private JTable table;
        private DefaultTableModel model;

        public PnlLichSuNhap() {
            setLayout(new BorderLayout(0, 20));
            setBackground(new Color(241, 245, 249));
            setBorder(new EmptyBorder(25, 25, 25, 25));

            JPanel top = new JPanel(new BorderLayout());
            top.setOpaque(false);

            JLabel back = new JLabel("← Quay lại");
            back.setCursor(new Cursor(Cursor.HAND_CURSOR));
            back.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    cardLayout.show(mainCards, CARD_NHAP_HANG);
                }
            });

            JLabel title = new JLabel("Lịch sử nhập kho");
            title.setFont(new Font("Inter", Font.BOLD, 30));

            top.add(back, BorderLayout.WEST);
            top.add(title, BorderLayout.SOUTH);

            RoundedPanel content = new RoundedPanel(25, Color.WHITE);
            content.setLayout(new BorderLayout());
            content.setBorder(new EmptyBorder(20, 20, 20, 20));

            model = new DefaultTableModel(new String[]{"MÃ PHIẾU", "SẢN PHẨM", "SỐ LƯỢNG", "NGÀY NHẬP"}, 0);

            table = new JTable(model);
            setupModernTable(table);

            table.getColumnModel().getColumn(0).setCellRenderer(new CenterTableCellRenderer());
            table.getColumnModel().getColumn(1).setCellRenderer(new LeftTableCellRenderer());
            table.getColumnModel().getColumn(2).setCellRenderer(new CenterTableCellRenderer());

            loadData();

            JScrollPane scroll = new JScrollPane(table);
            scroll.setBorder(null);
            content.add(scroll, BorderLayout.CENTER);

            add(top, BorderLayout.NORTH);
            add(content, BorderLayout.CENTER);
        }

        public void refreshData() {
            loadData();
        }

        private void loadData() {
            model.setRowCount(0);
            List<Map<String, Object>> list = khoDAO.getRecentNhapKho();
            for (Map<String, Object> item : list) {
                model.addRow(new Object[]{
                        item.get("mapn"),
                        item.get("tenSP"),
                        item.get("soLuong"),
                        item.get("ngayNhap")
                });
            }
        }
    }

    // =========================================================
    // KIỂM KÊ
    // =========================================================
    private void openTraCuuSanPhamSapHetDialog() {
        JDialog dlg = new JDialog((Frame) null, "Tra cứu sản phẩm sắp hết", true);
        dlg.setSize(700, 500);
        dlg.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(new Color(241, 245, 249));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Danh sách sản phẩm sắp hết");
        title.setFont(new Font("Inter", Font.BOLD, 24));

        DefaultTableModel model = new DefaultTableModel(new String[]{"MÃ SP", "SẢN PHẨM", "TỒN KHO", "NGƯỠNG"}, 0);

        JTable table = new JTable(model);
        setupModernTable(table);

        table.getColumnModel().getColumn(0).setCellRenderer(new CenterTableCellRenderer());
        table.getColumnModel().getColumn(2).setCellRenderer(new CenterTableCellRenderer());
        table.getColumnModel().getColumn(3).setCellRenderer(new CenterTableCellRenderer());

        List<SanPham> list = khoDAO.getSanPhamSapHet();
        for (SanPham sp : list) {
            model.addRow(new Object[]{sp.getMaSP(), sp.getTenSP(), sp.getTonKho(), sp.getNguong()});
        }

        panel.add(title, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        dlg.setContentPane(panel);
        dlg.setVisible(true);
    }

    // =========================================================
    // BUTTON HELPERS
    // =========================================================
    private JButton createBlueButton(String text) {
        JButton btn = new JButton(text);
        btn.putClientProperty(FlatClientProperties.STYLE,
                "arc:14;background:#2563EB;foreground:#FFFFFF;font:bold +1;borderWidth:0");
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createWhiteButton(String text) {
        JButton btn = new JButton(text);
        btn.putClientProperty(FlatClientProperties.STYLE,
                "arc:14;background:#FFFFFF;borderColor:#E2E8F0;font:bold +1");
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // =========================================================
    // STATUS RENDERER
    // =========================================================
    class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setOpaque(true);

            if (isSelected)
                panel.setBackground(table.getSelectionBackground());
            else
                panel.setBackground(Color.WHITE);

            JLabel lbl = new JLabel(value.toString());
            lbl.setOpaque(true);
            lbl.setFont(new Font("Inter", Font.BOLD, 11));
            lbl.setBorder(new EmptyBorder(6, 14, 6, 14));

            if (value.toString().equals("CÒN HÀNG")) {
                lbl.setBackground(new Color(220, 252, 231));
                lbl.setForeground(new Color(22, 163, 74));
            } else {
                lbl.setBackground(new Color(254, 226, 226));
                lbl.setForeground(new Color(220, 38, 38));
            }

            panel.add(lbl);
            return panel;
        }
    }

    // =========================================================
    // ACTION BUTTON RENDERER & EDITOR
    // =========================================================
    class ActionButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton btnNhap;
        private JButton btnSuaGia;

        public ActionButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 12));
            setOpaque(true);

            btnNhap = new JButton("Nhập ngay");
            btnNhap.putClientProperty(FlatClientProperties.STYLE,
                    "arc:12;background:#EFF6FF;foreground:#2563EB;borderWidth:0;font:bold");
            btnNhap.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnNhap.setPreferredSize(new Dimension(95, 34));

            btnSuaGia = new JButton("Sửa giá");
            btnSuaGia.putClientProperty(FlatClientProperties.STYLE,
                    "arc:12;background:#FEF3C7;foreground:#D97706;borderWidth:0;font:bold");
            btnSuaGia.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnSuaGia.setPreferredSize(new Dimension(85, 34));

            add(btnNhap);
            add(btnSuaGia);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(Color.WHITE);
            }
            return this;
        }
    }

    class ActionButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel;
        private JButton btnNhap;
        private JButton btnSuaGia;
        private JTable table;
        private int currentRow;

        public ActionButtonEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 12));
            panel.setOpaque(true);

            btnNhap = new JButton("Nhập ngay");
            btnNhap.putClientProperty(FlatClientProperties.STYLE,
                    "arc:12;background:#EFF6FF;foreground:#2563EB;borderWidth:0;font:bold");
            btnNhap.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnNhap.setPreferredSize(new Dimension(95, 34));

            btnSuaGia = new JButton("Sửa giá");
            btnSuaGia.putClientProperty(FlatClientProperties.STYLE,
                    "arc:12;background:#FEF3C7;foreground:#D97706;borderWidth:0;font:bold");
            btnSuaGia.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnSuaGia.setPreferredSize(new Dimension(85, 34));

            btnNhap.addActionListener(e -> {
                if (currentRow >= 0) {
                    int maSP = Integer.parseInt(table.getValueAt(currentRow, 1).toString());
                    pnlNhapHangForm.selectProduct(maSP);
                    fireEditingStopped();
                    SwingUtilities.invokeLater(() -> cardLayout.show(mainCards, CARD_NHAP_HANG));
                }
            });

            btnSuaGia.addActionListener(e -> {
                if (currentRow >= 0) {
                    int maSP = Integer.parseInt(table.getValueAt(currentRow, 1).toString());
                    String tenSP = table.getValueAt(currentRow, 0).toString();
                    String giaCu = table.getValueAt(currentRow, 2).toString().replace("đ", "").trim();
                    openSuaGiaDialog(maSP, tenSP, giaCu);
                    fireEditingStopped();
                }
            });

            panel.add(btnNhap);
            panel.add(btnSuaGia);
        }

        private void openSuaGiaDialog(int maSP, String tenSP, String giaCu) {
            JDialog dialog = new JDialog((Frame) null, "Sửa giá sản phẩm", true);
            dialog.setSize(400, 250);
            dialog.setLocationRelativeTo(null);
            dialog.setLayout(new BorderLayout());

            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
            formPanel.setBackground(Color.WHITE);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(10, 10, 10, 10);

            JLabel lblTen = new JLabel("Sản phẩm:");
            lblTen.setFont(new Font("Inter", Font.BOLD, 14));
            JLabel lblTenValue = new JLabel(tenSP);
            lblTenValue.setFont(new Font("Inter", Font.PLAIN, 14));

            JLabel lblGiaCu = new JLabel("Giá hiện tại:");
            lblGiaCu.setFont(new Font("Inter", Font.BOLD, 14));
            JLabel lblGiaCuValue = new JLabel(giaCu);
            lblGiaCuValue.setFont(new Font("Inter", Font.PLAIN, 14));

            JLabel lblGiaMoi = new JLabel("Giá mới:");
            lblGiaMoi.setFont(new Font("Inter", Font.BOLD, 14));
            JTextField txtGiaMoi = new JTextField(15);
            txtGiaMoi.setFont(new Font("Inter", Font.PLAIN, 14));
            txtGiaMoi.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(226, 232, 240)),
                    new EmptyBorder(8, 12, 8, 12)));

            gbc.gridx = 0;
            gbc.gridy = 0;
            formPanel.add(lblTen, gbc);
            gbc.gridx = 1;
            formPanel.add(lblTenValue, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            formPanel.add(lblGiaCu, gbc);
            gbc.gridx = 1;
            formPanel.add(lblGiaCuValue, gbc);

            gbc.gridx = 0;
            gbc.gridy = 2;
            formPanel.add(lblGiaMoi, gbc);
            gbc.gridx = 1;
            formPanel.add(txtGiaMoi, gbc);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            JButton btnLuu = new JButton("Lưu");
            JButton btnHuy = new JButton("Hủy");
            btnLuu.setBackground(new Color(37, 99, 235));
            btnLuu.setForeground(Color.WHITE);
            btnLuu.setFocusPainted(false);
            btnLuu.setPreferredSize(new Dimension(100, 38));
            btnHuy.setBackground(Color.WHITE);
            btnHuy.setFocusPainted(false);
            btnHuy.setPreferredSize(new Dimension(100, 38));

            btnLuu.addActionListener(e -> {
                try {
                    double giaMoi = Double.parseDouble(txtGiaMoi.getText().trim());
                    if (giaMoi <= 0) {
                        JOptionPane.showMessageDialog(dialog, "Giá phải lớn hơn 0!");
                        return;
                    }
                    boolean success = khoDAO.capNhatGiaBan(maSP, giaMoi);
                    if (success) {
                        JOptionPane.showMessageDialog(dialog, "Cập nhật giá thành công!");
                        dashboard.refreshDashboard();
                        dialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Cập nhật giá thất bại!");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng nhập số hợp lệ!");
                }
            });

            btnHuy.addActionListener(e -> dialog.dispose());

            buttonPanel.add(btnLuu);
            buttonPanel.add(btnHuy);

            dialog.add(formPanel, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            dialog.setVisible(true);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.table = table;
            this.currentRow = row;
            if (isSelected) {
                panel.setBackground(table.getSelectionBackground());
            } else {
                panel.setBackground(Color.WHITE);
            }
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }

    // =========================================================
    // ROUNDED PANEL
    // =========================================================
    class RoundedPanel extends JPanel {
        private int radius;
        private Color bg;

        public RoundedPanel(int radius, Color bg) {
            this.radius = radius;
            this.bg = bg;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}