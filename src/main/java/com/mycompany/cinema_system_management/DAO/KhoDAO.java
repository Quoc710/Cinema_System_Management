package com.mycompany.cinema_system_management.DAO;

import com.mycompany.cinema_system_management.models.SanPham;
import com.mycompany.cinema_system_management.utils.DatabaseConnection;

import java.sql.*;
import java.util.*;

/**
 * DAO xử lý kho hàng
 * @author Nguyen Huu Tai
 */
public class KhoDAO {

    // =========================================================
    // LOAD DANH SÁCH KHO
    // =========================================================
    public List<SanPham> getDanhSachKho(
            String search,
            String filter
    ) {

        List<SanPham> list = new ArrayList<>();

        String sql =
                "SELECT * FROM SANPHAM " +
                "WHERE (LOWER(TENSP) LIKE LOWER(?) " +
                "OR TO_CHAR(MASP) LIKE ?)";

        if (filter != null &&
                filter.equalsIgnoreCase("Sắp hết")) {

            sql += " AND SOLUONGTONKHO <= NGUONG";
        }

        sql += " ORDER BY MASP DESC";

        try (
                Connection conn =
                        DatabaseConnection.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setString(1, "%" + search + "%");
            ps.setString(2, "%" + search + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                SanPham sp = new SanPham(
                        rs.getInt("MASP"),
                        rs.getString("TENSP"),
                        rs.getString("LOAISP"),
                        rs.getString("DONVITINH"),
                        rs.getDouble("GIABAN"),
                        rs.getInt("SOLUONGTONKHO"),
                        rs.getInt("NGUONG")
                );

                list.add(sp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // =========================================================
    // LOAD TẤT CẢ SẢN PHẨM
    // =========================================================
    public List<SanPham> getAllSanPham() {

        List<SanPham> list = new ArrayList<>();

        String sql =
                "SELECT * FROM SANPHAM " +
                "ORDER BY TENSP ASC";

        try (
                Connection conn =
                        DatabaseConnection.getConnection();

                Statement st =
                        conn.createStatement();

                ResultSet rs =
                        st.executeQuery(sql)
        ) {

            while (rs.next()) {

                SanPham sp = new SanPham(
                        rs.getInt("MASP"),
                        rs.getString("TENSP"),
                        rs.getString("LOAISP"),
                        rs.getString("DONVITINH"),
                        rs.getDouble("GIABAN"),
                        rs.getInt("SOLUONGTONKHO"),
                        rs.getInt("NGUONG")
                );

                list.add(sp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // =========================================================
    // LOAD 1 SẢN PHẨM
    // =========================================================
    public SanPham getSanPhamById(int maSP) {

        String sql =
                "SELECT * FROM SANPHAM " +
                "WHERE MASP = ?";

        try (
                Connection conn =
                        DatabaseConnection.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setInt(1, maSP);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                return new SanPham(
                        rs.getInt("MASP"),
                        rs.getString("TENSP"),
                        rs.getString("LOAISP"),
                        rs.getString("DONVITINH"),
                        rs.getDouble("GIABAN"),
                        rs.getInt("SOLUONGTONKHO"),
                        rs.getInt("NGUONG")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // =========================================================
    // NHẬP KHO
    // =========================================================
    public boolean nhapKho(
            int maSP,
            int soLuong
    ) {

        String sqlPhieuNhap =
                "INSERT INTO PHIEUNHAPKHO " +
                "(TONG_SL_NHAP) " +
                "VALUES (?)";

        String sqlChiTiet =
                "INSERT INTO CTPHIEUNHAP " +
                "(MAPN, MASP, SOLUONGNHAP) " +
                "VALUES (?, ?, ?)";

        String sqlUpdateKho =
                "UPDATE SANPHAM " +
                "SET SOLUONGTONKHO = " +
                "SOLUONGTONKHO + ? " +
                "WHERE MASP = ?";

        try (
                Connection conn =
                        DatabaseConnection.getConnection()
        ) {

            conn.setAutoCommit(false);

            try {

                int maPN = -1;

                // =====================================
                // TẠO PHIẾU NHẬP
                // =====================================

                PreparedStatement psPN =
                        conn.prepareStatement(
                                sqlPhieuNhap,
                                new String[]{"MAPN"}
                        );

                psPN.setInt(1, soLuong);

                psPN.executeUpdate();

                ResultSet rs =
                        psPN.getGeneratedKeys();

                if (rs.next()) {

                    maPN = rs.getInt(1);
                }

                // =====================================
                // CHI TIẾT PHIẾU NHẬP
                // =====================================

                PreparedStatement psCT =
                        conn.prepareStatement(sqlChiTiet);

                psCT.setInt(1, maPN);
                psCT.setInt(2, maSP);
                psCT.setInt(3, soLuong);

                psCT.executeUpdate();

                // =====================================
                // UPDATE TỒN KHO
                // =====================================

                PreparedStatement psUpdate =
                        conn.prepareStatement(sqlUpdateKho);

                psUpdate.setInt(1, soLuong);
                psUpdate.setInt(2, maSP);

                psUpdate.executeUpdate();

                conn.commit();

                return true;

            } catch (Exception e) {

                conn.rollback();

                e.printStackTrace();

                return false;
            }

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }
    }

    // =========================================================
    // CẬP NHẬT GIÁ BÁN SẢN PHẨM
    // =========================================================
    public boolean capNhatGiaBan(int maSP, double giaMoi) {

        String sql = "UPDATE SANPHAM SET GIABAN = ? WHERE MASP = ?";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setDouble(1, giaMoi);
            ps.setInt(2, maSP);

            int rowsAffected = ps.executeUpdate();

            return rowsAffected > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // =========================================================
    // THỐNG KÊ DASHBOARD
    // =========================================================
    public Map<String, Object> getThongKeKho() {

        Map<String, Object> stats =
                new HashMap<>();

        String sql =
                "SELECT " +

                "(SELECT COUNT(*) " +
                "FROM SANPHAM) tongSP, " +

                "(SELECT COUNT(*) " +
                "FROM SANPHAM " +
                "WHERE SOLUONGTONKHO <= NGUONG) sapHet, " +

                "(SELECT NVL(SUM(SOLUONGTONKHO * GIABAN),0) " +
                "FROM SANPHAM) giaTri, " +

                "(SELECT COUNT(*) " +
                "FROM PHIEUNHAPKHO " +
                "WHERE TO_CHAR(NGAYNHAPHANG,'MM-YYYY') = " +
                "TO_CHAR(SYSDATE,'MM-YYYY')) donNhap " +

                "FROM DUAL";

        try (
                Connection conn =
                        DatabaseConnection.getConnection();

                Statement st =
                        conn.createStatement();

                ResultSet rs =
                        st.executeQuery(sql)
        ) {

            if (rs.next()) {

                stats.put(
                        "tongSP",
                        rs.getInt("tongSP")
                );

                stats.put(
                        "sapHet",
                        rs.getInt("sapHet")
                );

                stats.put(
                        "giaTri",
                        rs.getDouble("giaTri")
                );

                stats.put(
                        "donNhap",
                        rs.getInt("donNhap")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stats;
    }

    // =========================================================
    // LOAD SẢN PHẨM SẮP HẾT
    // =========================================================
    public List<SanPham> getSanPhamSapHet() {

        List<SanPham> list =
                new ArrayList<>();

        String sql =
                "SELECT * FROM SANPHAM " +
                "WHERE SOLUONGTONKHO <= NGUONG " +
                "ORDER BY SOLUONGTONKHO ASC";

        try (
                Connection conn =
                        DatabaseConnection.getConnection();

                Statement st =
                        conn.createStatement();

                ResultSet rs =
                        st.executeQuery(sql)
        ) {

            while (rs.next()) {

                SanPham sp = new SanPham(
                        rs.getInt("MASP"),
                        rs.getString("TENSP"),
                        rs.getString("LOAISP"),
                        rs.getString("DONVITINH"),
                        rs.getDouble("GIABAN"),
                        rs.getInt("SOLUONGTONKHO"),
                        rs.getInt("NGUONG")
                );

                list.add(sp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // =========================================================
    // LOAD 3 LỊCH SỬ NHẬP GẦN NHẤT
    // =========================================================
    public List<String[]> getTop3LichSuNhap() {

        List<String[]> list =
                new ArrayList<>();

        String sql =
                "SELECT * FROM ( " +
                "   SELECT sp.TENSP, " +
                "          ct.SOLUONGNHAP, " +
                "          pn.NGAYNHAPHANG " +
                "   FROM PHIEUNHAPKHO pn " +
                "   JOIN CTPHIEUNHAP ct " +
                "       ON pn.MAPN = ct.MAPN " +
                "   JOIN SANPHAM sp " +
                "       ON sp.MASP = ct.MASP " +
                "   ORDER BY pn.NGAYNHAPHANG DESC " +
                ") WHERE ROWNUM <= 3";

        try (
                Connection conn =
                        DatabaseConnection.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                String tenSP =
                        rs.getString("TENSP");

                String soLuong =
                        rs.getInt("SOLUONGNHAP")
                                + "";

                String ngayNhap =
                        rs.getTimestamp("NGAYNHAPHANG")
                                .toLocalDateTime()
                                .toString()
                                .replace("T", " ");

                list.add(new String[]{
                        tenSP,
                        soLuong,
                        ngayNhap
                });
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return list;
    }
    
    // =========================================================
    // LOAD LỊCH SỬ NHẬP GẦN NHẤT
    // =========================================================
    public List<Map<String, Object>> getRecentNhapKho() {

        List<Map<String, Object>> list =
                new ArrayList<>();

        String sql =
                "SELECT " +
                "pn.MAPN, " +
                "sp.TENSP, " +
                "ct.SOLUONGNHAP, " +
                "pn.NGAYNHAPHANG " +
                "FROM PHIEUNHAPKHO pn " +
                "JOIN CTPHIEUNHAP ct " +
                "ON pn.MAPN = ct.MAPN " +
                "JOIN SANPHAM sp " +
                "ON sp.MASP = ct.MASP " +
                "ORDER BY pn.NGAYNHAPHANG DESC";

        try (

                Connection conn =
                        DatabaseConnection.getConnection();

                Statement st =
                        conn.createStatement();

                ResultSet rs =
                        st.executeQuery(sql)

        ) {

            while (rs.next()) {

                Map<String, Object> item =
                        new HashMap<>();

                item.put(
                        "mapn",
                        rs.getInt("MAPN")
                );

                item.put(
                        "tenSP",
                        rs.getString("TENSP")
                );

                item.put(
                        "soLuong",
                        rs.getInt("SOLUONGNHAP")
                );

                item.put(
                        "ngayNhap",
                        rs.getTimestamp("NGAYNHAPHANG")
                );

                list.add(item);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return list;
    }
}