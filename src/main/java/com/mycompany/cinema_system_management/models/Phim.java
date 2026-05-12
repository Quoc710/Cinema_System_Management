package com.mycompany.cinema_system_management.models;

import java.util.Date;

public class Phim {
    private int maPhim;
    private String tenPhim;
    private String theLoai;
    private int thoiLuong;
    private String daoDien;
    private Date ngayCongChieu;
    private Date ngayKetThuc;
    private int doTuoi;
    private String hinhAnh;
    private String tomTat;

    public Phim() {}

    public Phim(int maPhim, String tenPhim, String theLoai, int thoiLuong, String daoDien, Date ngayCongChieu, Date ngayKetThuc, int doTuoi, String hinhAnh, String tomTat) {
        this.maPhim = maPhim;
        this.tenPhim = tenPhim;
        this.theLoai = theLoai;
        this.thoiLuong = thoiLuong;
        this.daoDien = daoDien;
        this.ngayCongChieu = ngayCongChieu;
        this.ngayKetThuc = ngayKetThuc;
        this.doTuoi = doTuoi;
        this.hinhAnh = hinhAnh;
        this.tomTat = tomTat;
    }

    public int getMaPhim() { return maPhim; }
    public void setMaPhim(int maPhim) { this.maPhim = maPhim; }
    public String getTenPhim() { return tenPhim; }
    public void setTenPhim(String tenPhim) { this.tenPhim = tenPhim; }
    public String getTheLoai() { return theLoai; }
    public void setTheLoai(String theLoai) { this.theLoai = theLoai; }
    public int getThoiLuong() { return thoiLuong; }
    public void setThoiLuong(int thoiLuong) { this.thoiLuong = thoiLuong; }
    public String getDaoDien() { return daoDien; }
    public void setDaoDien(String daoDien) { this.daoDien = daoDien; }
    public Date getNgayCongChieu() { return ngayCongChieu; }
    public void setNgayCongChieu(Date ngayCongChieu) { this.ngayCongChieu = ngayCongChieu; }
    public Date getNgayKetThuc() { return ngayKetThuc; }
    public void setNgayKetThuc(Date ngayKetThuc) { this.ngayKetThuc = ngayKetThuc; }
    public int getDoTuoi() { return doTuoi; }
    public void setDoTuoi(int doTuoi) { this.doTuoi = doTuoi; }
    public String getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }
    public String getTomTat() { return tomTat; }
    public void setTomTat(String tomTat) { this.tomTat = tomTat; }
    
}