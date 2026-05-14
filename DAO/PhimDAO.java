package com.mycompany.cinema_system_management.DAO;

import com.mycompany.cinema_system_management.models.Phim;
import com.mycompany.cinema_system_management.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhimDAO {

    // 1. LẤY DANH SÁCH (READ)
    public List<Phim> getDanhSachPhim() {
        List<Phim> list = new ArrayList<>();
        String sql = "SELECT MAPHIM, TENPHIM, THELOAI, THOILUONG, DAODIEN, NGAYCONGCHIEU, NGAYKETTHUC, DOTUOI, HINHANH, TOMTAT FROM PHIM ORDER BY MAPHIM DESC"; 
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
                p.setTomTat(rs.getString("TOMTAT")); 
                list.add(p);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // 2. THÊM PHIM (CREATE)
    public boolean themPhim(Phim p) {
        String sql = "INSERT INTO PHIM (TENPHIM, THELOAI, THOILUONG, DAODIEN, NGAYCONGCHIEU, NGAYKETTHUC, DOTUOI, HINHANH, TOMTAT) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, p.getTenPhim());
            pstmt.setString(2, p.getTheLoai());
            pstmt.setInt(3, p.getThoiLuong());
            pstmt.setString(4, p.getDaoDien());
            // Xử lý ngày tháng cho Oracle
            if(p.getNgayCongChieu() != null) pstmt.setDate(5, new java.sql.Date(p.getNgayCongChieu().getTime())); else pstmt.setNull(5, Types.DATE);
            if(p.getNgayKetThuc() != null) pstmt.setDate(6, new java.sql.Date(p.getNgayKetThuc().getTime())); else pstmt.setNull(6, Types.DATE);
            
            pstmt.setInt(7, p.getDoTuoi());
            pstmt.setString(8, p.getHinhAnh());
            pstmt.setString(9, p.getTomTat());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // 3. CẬP NHẬT PHIM (UPDATE)
    public boolean suaPhim(Phim p) {
        String sql = "UPDATE PHIM SET TENPHIM=?, THELOAI=?, THOILUONG=?, DAODIEN=?, NGAYCONGCHIEU=?, NGAYKETTHUC=?, DOTUOI=?, HINHANH=?, TOMTAT=? WHERE MAPHIM=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, p.getTenPhim());
            pstmt.setString(2, p.getTheLoai());
            pstmt.setInt(3, p.getThoiLuong());
            pstmt.setString(4, p.getDaoDien());
            if(p.getNgayCongChieu() != null) pstmt.setDate(5, new java.sql.Date(p.getNgayCongChieu().getTime())); else pstmt.setNull(5, Types.DATE);
            if(p.getNgayKetThuc() != null) pstmt.setDate(6, new java.sql.Date(p.getNgayKetThuc().getTime())); else pstmt.setNull(6, Types.DATE);
            pstmt.setInt(7, p.getDoTuoi());
            pstmt.setString(8, p.getHinhAnh());
            pstmt.setString(9, p.getTomTat());
            pstmt.setInt(10, p.getMaPhim()); // Khóa chính để tìm đúng dòng
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // 4. XÓA PHIM (DELETE)
    public boolean xoaPhim(int maPhim) {
        String sql = "DELETE FROM PHIM WHERE MAPHIM=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maPhim);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // 5. KIỂM TRA PHIM ĐÃ TỒN TẠI HAY CHƯA (Tránh trùng lặp tên phim)
    public boolean kiemTraPhimTonTai(String tenPhim, int maPhimHienTai) {
        // Dùng COUNT(*) cho Oracle SQL để tối ưu hiệu suất truy vấn
        String sql = "SELECT COUNT(*) FROM PHIM WHERE TENPHIM = ?";
        if (maPhimHienTai > 0) {
            sql += " AND MAPHIM != ?"; // Bỏ qua bộ phim đang được sửa
        }
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, tenPhim.trim());
            if (maPhimHienTai > 0) {
                pstmt.setInt(2, maPhimHienTai);
            }
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Trả về true nếu count > 0 (nghĩa là đã tồn tại)
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return false;
    }
}