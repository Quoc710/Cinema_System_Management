package com.mycompany.cinema_system_management.models;

/**
 * Model SanPham chuẩn hóa theo Database Oracle
 * @author Nguyen Huu Tai
 */
public class SanPham {
    private int maSP;            // Khớp với NUMBER GENERATED ALWAYS AS IDENTITY
    private String tenSP;        // NVARCHAR2(255)
    private String loaiSP;       // LOAISP
    private String donViTinh;    // DONVITINH
    private double giaBan;       // NUMBER(15, 2)
    private int soLuongTonKho;   // SOLUONGTONKHO
    private int nguong;          // NGUONG (Mặc định 20)

    // 1. Constructor không tham số (Bắt buộc cho Java Beans)
    public SanPham() {
    }

    // 2. Constructor đầy đủ tham số (Để fix lỗi tại KhoDAO.java:[21,26])
    public SanPham(int maSP, String tenSP, String loaiSP, String donViTinh, double giaBan, int soLuongTonKho, int nguong) {
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.loaiSP = loaiSP;
        this.donViTinh = donViTinh;
        this.giaBan = giaBan;
        this.soLuongTonKho = soLuongTonKho;
        this.nguong = nguong;
    }

    // --- SETTER & GETTER ĐẦY ĐỦ ---

    public int getMaSPInt() {
        return maSP;
    }

    public String getMaSP() {
        return String.valueOf(maSP);
    }

    public void setMaSP(int maSP) {
        this.maSP = maSP;
    }

    public String getTenSP() {
        return tenSP;
    }

    public void setTenSP(String tenSP) {
        this.tenSP = tenSP;
    }

    public String getLoaiSP() {
        return loaiSP;
    }

    public void setLoaiSP(String loaiSP) {
        this.loaiSP = loaiSP;
    }

    public String getDonViTinh() {
        return donViTinh;
    }

    public void setDonViTinh(String donViTinh) {
        this.donViTinh = donViTinh;
    }

    public double getGiaBan() {
        return giaBan;
    }

    public void setGiaBan(double giaBan) {
        this.giaBan = giaBan;
    }

    public int getSoLuongTonKho() {
        return soLuongTonKho;
    }

    public void setSoLuongTonKho(int soLuongTonKho) {
        this.soLuongTonKho = soLuongTonKho;
    }

    public int getNguong() {
        return nguong;
    }

    public void setNguong(int nguong) {
        this.nguong = nguong;
    }

    /**
     * Alias để khớp với gọi hàm sp.getTonKho() trong View
     */
    public int getTonKho() {
        return this.soLuongTonKho;
    }

    /**
     * Logic hiển thị Badge trạng thái dựa trên ngưỡng trong DB
     */
    public String getStatusString() {
        if (this.soLuongTonKho <= 0) {
            return "HẾT HÀNG";
        }
        if (this.soLuongTonKho <= this.nguong) {
            return "SẮP HẾT";
        }
        return "CÒN HÀNG";
    }

    @Override
    public String toString() {
        return tenSP; // Phục vụ hiển thị JComboBox
    }
}