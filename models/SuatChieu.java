package com.mycompany.cinema_system_management.models;

public class SuatChieu {
    public int maLichChieu, maPhim, maPhong, trangThai, veDaBan, tongVe;
    public String tgChieu, tgKetThuc, tenPhim, theLoai, tenPhong, hinhAnh;

    public SuatChieu() {}

    public SuatChieu(int maLC, int maP, int maPhg, String tgC, String tgK, int st, String ten, String tl, String p, int veDB, int tongV) {
        this.maLichChieu = maLC; this.maPhim = maP; this.maPhong = maPhg;
        this.tgChieu = tgC; this.tgKetThuc = tgK; this.trangThai = st;
        this.tenPhim = ten; this.theLoai = tl; this.tenPhong = p;
        this.veDaBan = veDB; this.tongVe = tongV;
    }

    public String getGioChieuUI() { try { return tgChieu.split(" ")[1].substring(0, 5); } catch (Exception e) { return "00:00"; } }
    public String getGioKetThucUI() { try { return tgKetThuc.split(" ")[1].substring(0, 5); } catch (Exception e) { return "00:00"; } }
    public String getNgayChieuUI() {
        try {
            String[] parts = tgChieu.split(" ")[0].split("-");
            return parts[2] + "/" + parts[1] + "/" + parts[0];
        } catch (Exception e) { return tgChieu; }
    }
    public String getTrangThaiText() {
        if (this.trangThai == 1) return "Đang chiếu";
        if (this.trangThai == 0) return "Sắp chiếu";
        return "Đã kết thúc";
    }
}