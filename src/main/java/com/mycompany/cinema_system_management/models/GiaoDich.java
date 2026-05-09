package com.mycompany.cinema_system_management.models;

public class GiaoDich {
    private String maGD;
    private String tenKH;
    private String tenPhim; // Thay cho loại giao dịch
    private double soTien;
    private String thoiGian;

    public GiaoDich(String maGD, String tenKH, String tenPhim, double soTien, String thoiGian) {
        this.maGD = maGD;
        this.tenKH = tenKH;
        this.tenPhim = tenPhim;
        this.soTien = soTien;
        this.thoiGian = thoiGian;
    }

    // Getters
    public String getMaGD() { return maGD; }
    public String getTenKH() { return tenKH; }
    public String getTenPhim() { return tenPhim; }
    public double getSoTien() { return soTien; }
    public String getThoiGian() { return thoiGian; }
}