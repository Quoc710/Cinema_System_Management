package com.mycompany.cinema_system_management.dao;

import com.mycompany.cinema_system_management.models.Phim;
import com.mycompany.cinema_system_management.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhimDAO {

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
}