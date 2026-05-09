package com.mycompany.cinema_system_management.DAO;

import com.mycompany.cinema_system_management.models.GiaoDich;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GiaoDichDAO {

    /**
     * 1. Lấy danh sách giao dịch gần nhất
     * Sửa MA_GD thành MAGD để khớp với Database
     */
    public List<GiaoDich> getRecentTransactions(Connection conn) {
        List<GiaoDich> list = new ArrayList<>();
        // SQL FIXED: MAGD và THOIGIAN
        String sql = "SELECT L.MAGD, K.HOTEN, P.TENPHIM, L.SOTIEN, " +
                     "TO_CHAR(L.THOIGIAN, 'DD/MM/YYYY HH24:MI') " +
                     "FROM LICHSUGIAODICH L " +
                     "JOIN KHACHHANG K ON L.MAKH = K.MAKH " +
                     "LEFT JOIN VE V ON L.MA_THAM_CHIEU = V.MAVE " +
                     "LEFT JOIN LICHCHIEU LC ON V.MALICHCHIEU = LC.MALICHCHIEU " +
                     "LEFT JOIN PHIM P ON LC.MAPHIM = P.MAPHIM " +
                     "ORDER BY L.THOIGIAN DESC"; 
        
        try (PreparedStatement ps = conn.prepareStatement(sql); 
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                String maGD = rs.getString(1); // Sẽ lấy giá trị từ cột MAGD
                String tenKH = rs.getString(2);
                String tenPhim = rs.getString(3);
                double soTien = rs.getDouble(4);
                String ngayGio = rs.getString(5);
                
                if (tenPhim == null) tenPhim = "Giao dịch hệ thống";

                list.add(new GiaoDich(maGD, tenKH, tenPhim, soTien, ngayGio));
            }
            System.out.println("SQL Debug: Đã tìm thấy " + list.size() + " dòng giao dịch.");
        } catch (SQLException e) { 
            System.out.println("SQL Error: " + e.getMessage());
            e.printStackTrace(); 
        }
        return list;
    }

    /**
     * 2. Thống kê Doanh thu
     */
    public double[] getRevenueStats(Connection conn) {
        double total = 0, last24h = 0;
        String sqlTotal = "SELECT SUM(SOTIEN) FROM LICHSUGIAODICH WHERE LOAIGD = 2";
        String sql24h = "SELECT SUM(SOTIEN) FROM LICHSUGIAODICH WHERE LOAIGD = 2 AND THOIGIAN >= SYSDATE - 1";
        
        try (Statement st = conn.createStatement()) {
            ResultSet rs1 = st.executeQuery(sqlTotal);
            if (rs1.next()) total = rs1.getDouble(1);
            
            ResultSet rs2 = st.executeQuery(sql24h);
            if (rs2.next()) last24h = rs2.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new double[]{total, last24h};
    }

    /**
     * 3. Thống kê Vé đã bán
     */
    public int[] getTicketStats(Connection conn) {
        int total = 0, last24h = 0;
        String sqlTotal = "SELECT COUNT(*) FROM VE WHERE TRANGTHAIVE = 1";
        String sql24h = "SELECT COUNT(*) FROM VE V " +
                        "JOIN LICHSUGIAODICH L ON V.MAVE = L.MA_THAM_CHIEU " +
                        "WHERE L.THOIGIAN >= SYSDATE - 1 AND L.LOAIGD = 2";
        
        try (Statement st = conn.createStatement()) {
            ResultSet rs1 = st.executeQuery(sqlTotal);
            if (rs1.next()) total = rs1.getInt(1);
            
            ResultSet rs2 = st.executeQuery(sql24h);
            if (rs2.next()) last24h = rs2.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new int[]{total, last24h};
    }

    /**
     * 4. Thống kê Khuyến mãi
     * FIX: Vì bảng LICHSUGIAODICH của ní không có cột MAKM, mình sẽ trả về 0 để tránh lỗi SQL.
     */
    public int[] getPromoStats(Connection conn) {
        return new int[]{0, 0}; 
    }

    /**
     * 5. Thống kê Vé chợ Pass
     */
    public int getMarketTicketsCount(Connection conn) {
        String sql = "SELECT COUNT(*) FROM TRANGPASSVE WHERE TRANGTHAI_PASS = 1";
        try (Statement st = conn.createStatement(); 
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}