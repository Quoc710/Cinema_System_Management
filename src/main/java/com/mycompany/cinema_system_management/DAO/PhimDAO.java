package com.mycompany.cinema_system_management.dao;

import com.mycompany.cinema_system_management.models.Phim;
import com.mycompany.cinema_system_management.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhimDAO {

    // 1. Lấy toàn bộ danh sách phim (Dùng cho màn hình Home)
    public List<Phim> getDanhSachPhim() {
        List<Phim> list = new ArrayList<>();
        String sql = "SELECT MAPHIM, TENPHIM, THELOAI, THOILUONG, DAODIEN, NGAYCONGCHIEU, NGAYKETTHUC, DOTUOI, HINHANH, DIEMDANHGIA FROM PHIM"; 

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Phim p = new Phim();
                p.setMaPhim(rs.getInt("MAPHIM"));
                p.setTenPhim(rs.getString("TENPHIM"));
                p.setTheLoai(rs.getString("THELOAI"));
                p.setThoiLuong(rs.getInt("THOILUONG"));
                p.setDaoDien(rs.getString("DAODIEN"));
                p.setNgayCongChieu(rs.getDate("NGAYCONGCHIEU"));
                p.setNgayKetThuc(rs.getDate("NGAYKETTHUC"));
                p.setDoTuoi(rs.getInt("DOTUOI"));
                p.setHinhAnh(rs.getString("HINHANH"));
                p.setDiemDanhGia(rs.getDouble("DIEMDANHGIA"));
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. Lấy thông tin chi tiết của 1 bộ phim
    public Phim getChiTietPhim(String tenPhim) {
        Phim p = null;
        String sql = "SELECT * FROM PHIM WHERE TENPHIM = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setString(1, tenPhim);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                p = new Phim();
                p.setTenPhim(rs.getString("TENPHIM"));
                p.setTheLoai(rs.getString("THELOAI"));
                p.setThoiLuong(rs.getInt("THOILUONG"));
                p.setDiemDanhGia(rs.getDouble("DIEMDANHGIA"));
                p.setHinhAnh(rs.getString("HINHANH"));
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return p;
    }

    // 3. Lấy danh sách Lịch chiếu của phim đó (Chỉ lấy suất chưa chiếu)
    public List<Object[]> getLichChieuCuaPhim(String tenPhim) {
    List<Object[]> list = new ArrayList<>();
    // Join 3 bảng: LICHCHIEU, PHIM, PHONGCHIEU
    String sql = "SELECT lc.TGCHIEU, pc.TENPHONG, pc.DINHDANG " +
                 "FROM LICHCHIEU lc " +
                 "JOIN PHIM p ON lc.MAPHIM = p.MAPHIM " +
                 "JOIN PHONGCHIEU pc ON lc.MAPHONG = pc.MAPHONG " +
                 "WHERE p.TENPHIM = ? AND lc.TGCHIEU >= SYSDATE " +
                 "ORDER BY lc.TGCHIEU ASC";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        
        ps.setString(1, tenPhim);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            list.add(new Object[]{
                rs.getTimestamp("TGCHIEU"), 
                rs.getString("TENPHONG"), 
                rs.getString("DINHDANG")
            });
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return list;
}
    
}