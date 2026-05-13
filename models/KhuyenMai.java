package com.mycompany.cinema_system_management.models;

import java.util.Date;

public class KhuyenMai {
    private int maKM;
    private String tenKM;
    private Date ngayBD;
    private Date ngayKT;
    private int soLuongDaDung;
    private int soLuongConLai;
    private double giaTriGiam;
    private String trangThai; // Dùng để hiển thị lên Table

    // Constructor không tham số (Dành cho các framework hoặc khởi tạo trống)
    public KhuyenMai() {
    }

    // Constructor đầy đủ tham số để DAO đổ dữ liệu vào nhanh
    public KhuyenMai(int maKM, String tenKM, Date ngayBD, Date ngayKT, int soLuongDaDung, int soLuongConLai, double giaTriGiam, String trangThai) {
        this.maKM = maKM;
        this.tenKM = tenKM;
        this.ngayBD = ngayBD;
        this.ngayKT = ngayKT;
        this.soLuongDaDung = soLuongDaDung;
        this.soLuongConLai = soLuongConLai;
        this.giaTriGiam = giaTriGiam;
        this.trangThai = trangThai;
    }

    // --- GETTER VÀ SETTER ---

    public int getMaKM() {
        return maKM;
    }

    public void setMaKM(int maKM) {
        this.maKM = maKM;
    }

    public String getTenKM() {
        return tenKM;
    }

    public void setTenKM(String tenKM) {
        this.tenKM = tenKM;
    }

    public Date getNgayBD() {
        return ngayBD;
    }

    public void setNgayBD(Date ngayBD) {
        this.ngayBD = ngayBD;
    }

    public Date getNgayKT() {
        return ngayKT;
    }

    public void setNgayKT(Date ngayKT) {
        this.ngayKT = ngayKT;
    }

    public int getSoLuongDaDung() {
        return soLuongDaDung;
    }

    public void setSoLuongDaDung(int soLuongDaDung) {
        this.soLuongDaDung = soLuongDaDung;
    }

    public int getSoLuongConLai() {
        return soLuongConLai;
    }

    public void setSoLuongConLai(int soLuongConLai) {
        this.soLuongConLai = soLuongConLai;
    }

    public double getGiaTriGiam() {
        return giaTriGiam;
    }

    public void setGiaTriGiam(double giaTriGiam) {
        this.giaTriGiam = giaTriGiam;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    // Ghi đè phương thức toString để hỗ trợ debug nếu cần
    @Override
    public String toString() {
        return "KhuyenMai{" + "maKM=" + maKM + ", tenKM=" + tenKM + ", trangThai=" + trangThai + '}';
    }
}