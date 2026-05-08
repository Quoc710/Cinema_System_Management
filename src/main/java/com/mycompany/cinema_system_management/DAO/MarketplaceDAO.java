package com.mycompany.cinema_system_management.dao;

import com.mycompany.cinema_system_management.utils.DatabaseConnection;
import java.sql.*;
import java.util.Vector;

public class MarketplaceDAO {
    public Vector<Vector<Object>> getTicketsOnSale() {
        Vector<Vector<Object>> data = new Vector<>();
        // Câu lệnh SQL "thần thánh" kết nối 3 bảng để lấy đủ thông tin
        String sql = "SELECT p.TENPHIM, p.HINHANH, v.NGAYCHIEU, v.VITRIGHE, tp.GIAPASS " +
                     "FROM TRANGPASSVE tp " +
                     "JOIN VE v ON tp.MAVE = v.MAVE " +
                     "JOIN PHIM p ON v.MAPHIM = p.MAPHIM " +
                     "WHERE tp.TRANGTHAI_PASS = 1"; // Chỉ lấy những vé Đang bán

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                // Gộp Tên và Ảnh bằng dấu | để Renderer ở View tách ra vẽ
                row.add(rs.getString("TENPHIM") + "|" + rs.getString("HINHANH"));
                row.add(rs.getString("NGAYCHIEU"));
                row.add(rs.getString("VITRIGHE"));
                row.add(String.format("%,.0f đ", rs.getDouble("GIAPASS")));
                row.add("Buy Ticket");
                data.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }
}