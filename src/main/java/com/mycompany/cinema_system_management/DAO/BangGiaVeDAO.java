package com.mycompany.cinema_system_management.DAO;

import com.mycompany.cinema_system_management.models.BangGiaVe;
import com.mycompany.cinema_system_management.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BangGiaVeDAO {

    // Lấy danh sách giá vé - LẤY MABG THẬT TỪ DATABASE
    public List<BangGiaVe> getDanhSachGiaVe() {
        List<BangGiaVe> list = new ArrayList<>();
        // SỬA: Lấy MABG thật, không dùng ROWNUM
        String sql = "SELECT MABG, LOAIGHE, DINHDANG, DONGIA FROM BANGGIAVE ORDER BY MABG";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                BangGiaVe giaVe = new BangGiaVe();
                giaVe.setMaBG(rs.getInt("MABG"));        // Dùng setMaBG
                giaVe.setLoaiGhe(rs.getString("LOAIGHE"));
                giaVe.setDinhDang(rs.getString("DINHDANG"));
                giaVe.setDonGia(rs.getDouble("DONGIA"));
                list.add(giaVe);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi lấy danh sách giá vé: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    // Lấy giá vé theo MABG
    public BangGiaVe getGiaVeByMaBG(int maBG) {
        String sql = "SELECT MABG, LOAIGHE, DINHDANG, DONGIA FROM BANGGIAVE WHERE MABG = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maBG);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                BangGiaVe giaVe = new BangGiaVe();
                giaVe.setMaBG(rs.getInt("MABG"));
                giaVe.setLoaiGhe(rs.getString("LOAIGHE"));
                giaVe.setDinhDang(rs.getString("DINHDANG"));
                giaVe.setDonGia(rs.getDouble("DONGIA"));
                return giaVe;
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi lấy thông tin giá vé: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Cập nhật giá vé - DÙNG MABG
    public boolean updateGiaVe(BangGiaVe giaVe) {
        // SỬA: Update theo MABG thay vì LOAIGHE và DINHDANG
        String sql = "UPDATE BANGGIAVE SET DONGIA = ? WHERE MABG = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, giaVe.getDonGia());
            pstmt.setInt(2, giaVe.getMaBG());  // Dùng MABG để update

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Cập nhật giá vé thành công!");
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi cập nhật giá vé: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Thêm giá vé mới
    public boolean insertGiaVe(BangGiaVe giaVe) {
        String sql = "INSERT INTO BANGGIAVE (LOAIGHE, DINHDANG, DONGIA) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, giaVe.getLoaiGhe());
            pstmt.setString(2, giaVe.getDinhDang());
            pstmt.setDouble(3, giaVe.getDonGia());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Thêm giá vé thành công!");
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi thêm giá vé: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Xóa giá vé theo MABG
    public boolean deleteGiaVe(int maBG) {
        String sql = "DELETE FROM BANGGIAVE WHERE MABG = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maBG);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Xóa giá vé thành công!");
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi xóa giá vé: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}