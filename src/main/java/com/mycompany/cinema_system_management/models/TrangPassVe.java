package com.mycompany.cinema_system_management.models;

public class TrangPassVe {
    private int maPass;
    private int maVe;
    
    public int getMaVe() { return maVe;}
    public void setMaVe(int maVe) { this.maVe = maVe;}
    
    
    private double giaPass;
    private String tenPhim;
    private String hinhAnh;
    private String ngayChieu;
    private String tenGhe;
    private String loaiGhe;
    private String tenPhong;
    public TrangPassVe() {}

    public int getMaPass() { return maPass; }
    public void setMaPass(int maPass) { this.maPass = maPass; }
    public double getGiaPass() { return giaPass; }
    public void setGiaPass(double giaPass) { this.giaPass = giaPass; }
    public String getTenPhim() { return tenPhim; }
    public void setTenPhim(String tenPhim) { this.tenPhim = tenPhim; }
    public String getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }
    public String getNgayChieu() { return ngayChieu; }
    public void setNgayChieu(String ngayChieu) { this.ngayChieu = ngayChieu; }
    public String getTenGhe() { return tenGhe; }
    public void setTenGhe(String tenGhe) { this.tenGhe = tenGhe; }
    public String getLoaiGhe() { return loaiGhe; }
    public void setLoaiGhe(String loaiGhe) { this.loaiGhe = loaiGhe; }
    public String getTenPhong() { return tenPhong; }
    public void setTenPhong(String tenPhong) { this.tenPhong = tenPhong; }
}