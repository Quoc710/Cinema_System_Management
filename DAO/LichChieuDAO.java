package com.mycompany.cinema_system_management.DAO;

import com.mycompany.cinema_system_management.models.SuatChieu;
import com.mycompany.cinema_system_management.utils.DatabaseConnection; 
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LichChieuDAO {

    // 1. LẤY DANH SÁCH LỊCH CHIẾU
    public List<SuatChieu> getDanhSachLichChieu() {
        List<SuatChieu> list = new ArrayList<>();
        String sql = "SELECT lc.MALICHCHIEU, lc.MAPHIM, lc.MAPHONG, " +
                     "TO_CHAR(lc.TGCHIEU, 'YYYY-MM-DD HH24:MI:SS') as TGCHIEU, " +
                     "TO_CHAR(lc.TGKETTHUC, 'YYYY-MM-DD HH24:MI:SS') as TGKETTHUC, " +
                     "lc.TRANGTHAI, p.TENPHIM, p.THELOAI, p.HINHANH, pc.TENPHONG, pc.SUCCHUA " +
                     "FROM LICHCHIEU lc " +
                     "JOIN PHIM p ON lc.MAPHIM = p.MAPHIM " +
                     "JOIN PHONGCHIEU pc ON lc.MAPHONG = pc.MAPHONG " +
                     "ORDER BY lc.TGCHIEU DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                SuatChieu data = new SuatChieu();
                data.maLichChieu = rs.getInt("MALICHCHIEU");
                data.maPhim = rs.getInt("MAPHIM");
                data.maPhong = rs.getInt("MAPHONG");
                data.tgChieu = rs.getString("TGCHIEU");
                data.tgKetThuc = rs.getString("TGKETTHUC");
                data.trangThai = rs.getInt("TRANGTHAI");
                data.tenPhim = rs.getString("TENPHIM");
                data.theLoai = rs.getString("THELOAI");
                data.tenPhong = rs.getString("TENPHONG");
                data.tongVe = rs.getInt("SUCCHUA");
                data.veDaBan = 0; 
                data.hinhAnh = rs.getString("HINHANH"); 
                
                list.add(data);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // 2. THÊM LỊCH CHIẾU
    public boolean themLichChieu(SuatChieu sc) {
        String sql = "INSERT INTO LichChieu (MaPhim, MaPhong, TGChieu, TGKetThuc, TrangThai) VALUES (?, ?, TO_DATE(?, 'YYYY-MM-DD HH24:MI:SS'), TO_DATE(?, 'YYYY-MM-DD HH24:MI:SS'), ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sc.maPhim);
            ps.setInt(2, sc.maPhong);
            ps.setString(3, sc.tgChieu);
            ps.setString(4, sc.tgKetThuc);
            ps.setInt(5, 0); 
            return ps.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }

    // 3. XÓA SUẤT CHIẾU
    public boolean xoaLichChieu(int maLichChieu) {
        String sql = "DELETE FROM LichChieu WHERE MaLichChieu = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maLichChieu);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }

    // 4. SỬA SUẤT CHIẾU
    public boolean suaLichChieu(SuatChieu sc) {
        String sql = "UPDATE LichChieu SET MaPhim = ?, MaPhong = ?, TGChieu = TO_DATE(?, 'YYYY-MM-DD HH24:MI:SS'), TGKetThuc = TO_DATE(?, 'YYYY-MM-DD HH24:MI:SS') WHERE MaLichChieu = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sc.maPhim);
            ps.setInt(2, sc.maPhong);
            ps.setString(3, sc.tgChieu);
            ps.setString(4, sc.tgKetThuc);
            ps.setInt(5, sc.maLichChieu);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }

    // 5. KIỂM TRA TRÙNG LỊCH CHIẾU
    public boolean kiemTraTrungLich(int maPhong, String tgChieuMoi, String tgKetThucMoi, int maLichChieuHienTai) {
        String sql = "SELECT COUNT(*) FROM LICHCHIEU " +
                     "WHERE MAPHONG = ? " +
                     "AND TGCHIEU < TO_DATE(?, 'YYYY-MM-DD HH24:MI:SS') " +
                     "AND TGKETTHUC > TO_DATE(?, 'YYYY-MM-DD HH24:MI:SS')";
                     
        if (maLichChieuHienTai > 0) {
            sql += " AND MALICHCHIEU != ?";
        }
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maPhong);
            ps.setString(2, tgKetThucMoi); 
            ps.setString(3, tgChieuMoi);   
            
            if (maLichChieuHienTai > 0) {
                ps.setInt(4, maLichChieuHienTai);
            }
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}