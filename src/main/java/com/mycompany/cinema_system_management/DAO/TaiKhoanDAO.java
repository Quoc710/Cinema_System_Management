package com.mycompany.cinema_system_management.dao;

import com.mycompany.cinema_system_management.utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TaiKhoanDAO {

    // 1. HÀM XỬ LÝ ĐĂNG KÝ
    public boolean dangKyKhachHang(String username, String password, String hoTen, String email, String sdt) {
        String sqlTK = "INSERT INTO TAIKHOAN (USERNAME, PASSWORD, MAVAITRO) VALUES (?, ?, 1)";
        String sqlKH = "INSERT INTO KHACHHANG (HOTEN, EMAIL, SDT, MATK, SODUVI) VALUES (?, ?, ?, ?, 0)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); 
            int maTKVuatao = -1;

            try (PreparedStatement pstmtTK = conn.prepareStatement(sqlTK, new String[]{"MATK"})) {
                pstmtTK.setString(1, username);
                pstmtTK.setString(2, password);
                pstmtTK.executeUpdate();

                try (ResultSet rs = pstmtTK.getGeneratedKeys()) {
                    if (rs.next()) {
                        maTKVuatao = rs.getInt(1);
                    }
                }
            }

            if (maTKVuatao != -1) {
                try (PreparedStatement pstmtKH = conn.prepareStatement(sqlKH)) {
                    pstmtKH.setString(1, hoTen);
                    pstmtKH.setString(2, email);
                    pstmtKH.setString(3, sdt);
                    pstmtKH.setInt(4, maTKVuatao);
                    pstmtKH.executeUpdate();
                }
                conn.commit(); 
                return true;
            } else {
                conn.rollback();
                return false;
            }

        } catch (SQLException e) {
            System.out.println("LỖI ĐĂNG KÝ: " + e.getMessage());
            return false;
        }
    }

    // 2. HÀM XỬ LÝ ĐĂNG NHẬP
    public int kiemTraDangNhap(String username, String password) {
        int maVaiTro = -1; 
        String sql = "SELECT MAVAITRO FROM TAIKHOAN WHERE USERNAME = ? AND PASSWORD = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                maVaiTro = rs.getInt("MAVAITRO"); 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return maVaiTro;
    }

    // 3. LẤY DANH SÁCH VÉ KHÁCH HÀNG (Đã fix lỗi hiển thị Đổi chủ/Còn hạn)
    public List<Object[]> getDanhSachVeCuaKhachHang(String username) {
        List<Object[]> list = new ArrayList<>();
        
        String sql = "SELECT p.TENPHIM, p.HINHANH, lc.TGCHIEU, pc.TENPHONG, g.TENGHE, v.GIAVE, " +
                     "CASE " +
                     "    WHEN tp.MAKH_BAN = k.MAKH AND tp.TRANGTHAI_PASS = 2 THEN 'Đã đổi chủ' " +
                     "    WHEN v.TRANGTHAIVE = 2 THEN 'Đang rao bán' " +
                     "    ELSE 'Còn hạn' " +
                     "END as TRANGTHAI " +
                     "FROM VE v " +
                     "JOIN LICHCHIEU lc ON v.MALICHCHIEU = lc.MALICHCHIEU " +
                     "JOIN PHIM p ON lc.MAPHIM = p.MAPHIM " +
                     "JOIN PHONGCHIEU pc ON lc.MAPHONG = pc.MAPHONG " +
                     "JOIN GHENGOI g ON v.MAGHE = g.MAGHE " +
                     "JOIN KHACHHANG k ON k.MATK = (SELECT MATK FROM TAIKHOAN WHERE USERNAME = ?) " +
                     "LEFT JOIN TRANGPASSVE tp ON tp.MAVE = v.MAVE AND tp.TRANGTHAI_PASS IN (1, 2) " +
                     "WHERE v.MAKHACHHANG = k.MAKH OR (tp.MAKH_BAN = k.MAKH AND tp.TRANGTHAI_PASS = 2) " +
                     "ORDER BY lc.TGCHIEU DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            
            java.text.DecimalFormat df = new java.text.DecimalFormat("#,### đ");

            while (rs.next()) {
                String tenPhim = rs.getString("TENPHIM");
                String hinhAnh = rs.getString("HINHANH");
                String tgChieu = rs.getString("TGCHIEU");
                String tenPhong = rs.getString("TENPHONG");
                String tenGhe = rs.getString("TENGHE");
                double giaVe = rs.getDouble("GIAVE");
                String trangThaiStr = rs.getString("TRANGTHAI");

                String thongTinXuatChieu = tgChieu + " | " + tenPhong;
                String giaVeStr = df.format(giaVe);

                list.add(new Object[]{
                    new Object[]{tenPhim, hinhAnh},
                    thongTinXuatChieu,
                    tenGhe,
                    giaVeStr,
                    trangThaiStr
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 4. HÀM GET VÉ ĐÃ MUA
    public List<Object[]> getVeDaMua(String username) {
        List<Object[]> dsVe = new ArrayList<>();
        String sql = "SELECT p.TENPHIM, p.HINHANH, lc.TGCHIEU, pc.TENPHONG, g.TENGHE, v.GIAVE " +
                     "FROM VE v " +
                     "JOIN KHACHHANG kh ON v.MAKHACHHANG = kh.MAKH " +
                     "JOIN TAIKHOAN t ON kh.MATK = t.MATK " +
                     "JOIN LICHCHIEU lc ON v.MALICHCHIEU = lc.MALICHCHIEU " +
                     "JOIN PHIM p ON lc.MAPHIM = p.MAPHIM " +
                     "JOIN GHENGOI g ON v.MAGHE = g.MAGHE " +
                     "JOIN PHONGCHIEU pc ON lc.MAPHONG = pc.MAPHONG " +
                     "WHERE t.USERNAME = ?";
                     
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                dsVe.add(new Object[]{
                    new Object[]{rs.getString("TENPHIM"), rs.getString("HINHANH")},
                    rs.getString("TGCHIEU") + " | " + rs.getString("TENPHONG"), 
                    rs.getString("TENGHE"),                                         
                    String.format("%,.0f đ", rs.getDouble("GIAVE")),                
                    "" 
                });
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return dsVe;
    }
}