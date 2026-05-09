package com.mycompany.cinema_system_management.dao;

import com.mycompany.cinema_system_management.models.TrangPassVe;
import com.mycompany.cinema_system_management.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MarketplaceDAO {
    
    public List<TrangPassVe> getTicketsOnMarketplace() {
    List<TrangPassVe> list = new ArrayList<>();
    String sql = "SELECT tp.MAPASS, tp.GIAPASS, p.TENPHIM, p.HINHANH, " +
                 "lc.TGCHIEU, g.TENGHE, g.LOAIGHE, pc.TENPHONG " +
                 "FROM TRANGPASSVE tp " +
                 "LEFT JOIN VE v ON tp.MAVE = v.MAVE " +
                 "LEFT JOIN LICHCHIEU lc ON v.MALICHCHIEU = lc.MALICHCHIEU " +
                 "LEFT JOIN PHIM p ON lc.MAPHIM = p.MAPHIM " +
                 "LEFT JOIN PHONGCHIEU pc ON lc.MAPHONG = pc.MAPHONG " +
                 "LEFT JOIN GHENGOI g ON v.MAGHE = g.MAGHE " +
                 "WHERE tp.TRANGTHAI_PASS = 1";

    // try-with-resources: tự động đóng conn, pstmt, rs
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

        while (rs.next()) {
            TrangPassVe item = new TrangPassVe();
            item.setMaPass(rs.getInt("MAPASS"));
            item.setGiaPass(rs.getDouble("GIAPASS"));
            item.setTenPhim(rs.getString("TENPHIM") != null ? rs.getString("TENPHIM") : "");
            item.setHinhAnh(rs.getString("HINHANH") != null ? rs.getString("HINHANH") : "");

            Timestamp tgChieu = rs.getTimestamp("TGCHIEU");
            item.setNgayChieu(tgChieu != null ? tgChieu.toString() : "");

            item.setTenGhe(rs.getString("TENGHE") != null ? rs.getString("TENGHE") : "");
            item.setLoaiGhe(rs.getString("LOAIGHE") != null ? rs.getString("LOAIGHE") : "");
            item.setTenPhong(rs.getString("TENPHONG") != null ? rs.getString("TENPHONG") : "");
            list.add(item);
        }

    } catch (SQLException e) {
        System.out.println("❌ LỖI SQL: " + e.getMessage());
    }

    return list;
}
}