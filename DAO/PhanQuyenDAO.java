package com.mycompany.cinema_system_management.DAO;

import com.mycompany.cinema_system_management.models.TaiKhoan;
import com.mycompany.cinema_system_management.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhanQuyenDAO {

    // Lấy danh sách tài khoản
    public List<TaiKhoan> getAllTaiKhoan() {
        List<TaiKhoan> list = new ArrayList<>();
        String sql = "SELECT * FROM TAIKHOAN WHERE MAVAITRO != 1 ORDER BY MATK DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                // Thứ tự khớp Model: maTK, maVaiTro, username, password
                list.add(new TaiKhoan(
                    rs.getInt("MATK"), 
                    rs.getInt("MAVAITRO"),
                    rs.getString("USERNAME"), 
                    rs.getString("PASSWORD")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Hàm cập nhật (Sửa)
    public boolean updateNhanVien(int maTK, String pass, int maVaiTro) {
        String sql = "UPDATE TAIKHOAN SET PASSWORD = ?, MAVAITRO = ? WHERE MATK = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pass);
            ps.setInt(2, maVaiTro);
            ps.setInt(3, maTK);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    // Hàm xóa
    public boolean deleteTaiKhoan(int maTK) {
        String sql = "DELETE FROM TAIKHOAN WHERE MATK = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maTK);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }
    public boolean insertNhanVien(String user, String pass, int maVaiTro, String hoTen, String email, String sdt) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            String sqlTK = "INSERT INTO TAIKHOAN (USERNAME, PASSWORD, MAVAITRO) VALUES (?, ?, ?)";
            PreparedStatement psTK = conn.prepareStatement(sqlTK, new String[]{"MATK"});
            psTK.setString(1, user); psTK.setString(2, pass); psTK.setInt(3, maVaiTro);
            psTK.executeUpdate();
            ResultSet rs = psTK.getGeneratedKeys();
            int maTK = rs.next() ? rs.getInt(1) : 0;

            String sqlNV = "INSERT INTO NHANVIEN (HOTEN, EMAIL, SDT, MATK, TRANGTHAI) VALUES (?, ?, ?, ?, 1)";
            PreparedStatement psNV = conn.prepareStatement(sqlNV);
            psNV.setString(1, hoTen); psNV.setString(2, email); psNV.setString(3, sdt); psNV.setInt(4, maTK);
            psNV.executeUpdate();
            conn.commit();
            return true;
        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
            return false;
        }
    }
}