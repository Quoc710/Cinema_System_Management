package com.mycompany.cinema_system_management.models;

public class BangGiaVe {
    private int maBG;          // Khớp với MABG trong database
    private String loaiGhe;
    private String dinhDang;
    private double donGia;

    public BangGiaVe() {}

    public BangGiaVe(int maBG, String loaiGhe, String dinhDang, double donGia) {
        this.maBG = maBG;
        this.loaiGhe = loaiGhe;
        this.dinhDang = dinhDang;
        this.donGia = donGia;
    }

    public int getMaBG() { return maBG; }
    public void setMaBG(int maBG) { this.maBG = maBG; }

    public String getLoaiGhe() { return loaiGhe; }
    public void setLoaiGhe(String loaiGhe) { this.loaiGhe = loaiGhe; }

    public String getDinhDang() { return dinhDang; }
    public void setDinhDang(String dinhDang) { this.dinhDang = dinhDang; }

    public double getDonGia() { return donGia; }
    public void setDonGia(double donGia) { this.donGia = donGia; }

    @Override
    public String toString() {
        return String.format("%s + %s: %.0f đ", loaiGhe, dinhDang, donGia);
    }
}