package com.mycompany.cinema_system_management.models;

import java.sql.Timestamp;

public class LichChieu {
    private int maLichChieu;
    private int maPhim;
    private int maPhong;
    private Timestamp tgChieu;
    private Timestamp tgKetThuc;
    private int trangThai;

    public LichChieu() {}

    public int getMaLichChieu() { return maLichChieu; }
    public void setMaLichChieu(int maLichChieu) { this.maLichChieu = maLichChieu; }

    public int getMaPhim() { return maPhim; }
    public void setMaPhim(int maPhim) { this.maPhim = maPhim; }

    public Timestamp getTgChieu() { return tgChieu; }
    public void setTgChieu(Timestamp tgChieu) { this.tgChieu = tgChieu; }

    public int getTrangThai() { return trangThai; }
    public void setTrangThai(int trangThai) { this.trangThai = trangThai; }
}