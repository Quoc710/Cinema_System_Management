package com.mycompany.cinema_system_management.dao;

import com.mycompany.cinema_system_management.utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TaiKhoanDAO {

    // 1. HÀM XỬ LÝ ĐĂNG KÝ (Thêm vào 2 bảng cùng lúc)
    public boolean dangKyKhachHang(String username, String password, String hoTen, String email, String sdt) {
        Connection conn = null;
        PreparedStatement pstmtTK = null;
        PreparedStatement pstmtKH = null;
        ResultSet rs = null;
        boolean isSuccess = false;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Bật chế độ Giao dịch (Transaction)

            // Bước 1.1: Insert vào bảng TAIKHOAN (Mặc định MAVAITRO = 1 là Khách)
            String sqlTK = "INSERT INTO TAIKHOAN (USERNAME, PASSWORD, MAVAITRO) VALUES (?, ?, 1)";
            pstmtTK = conn.prepareStatement(sqlTK, new String[]{"MATK"}); // Yêu cầu Oracle trả về MATK
            pstmtTK.setString(1, username);
            pstmtTK.setString(2, password);
            pstmtTK.executeUpdate();

            // Bước 1.2: Lấy cái MATK vừa được tạo ra
            rs = pstmtTK.getGeneratedKeys();
            int maTKVuatao = -1;
            if (rs.next()) {
                maTKVuatao = rs.getInt(1);
            }

            // Bước 2: Dùng MATK đó để Insert vào bảng KHACHHANG
            String sqlKH = "INSERT INTO KHACHHANG (HOTEN, EMAIL, SDT, MATK, SODUVI) VALUES (?, ?, ?, ?, 0)";
            pstmtKH = conn.prepareStatement(sqlKH);
            pstmtKH.setString(1, hoTen);
            pstmtKH.setString(2, email);
            pstmtKH.setString(3, sdt);
            pstmtKH.setInt(4, maTKVuatao);
            pstmtKH.executeUpdate();

            // Lưu thành công cả 2 bảng -> Chốt đơn!
            conn.commit();
            isSuccess = true;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                // Nếu 1 trong 2 bảng bị lỗi (VD: trùng Username) -> Rút lại toàn bộ
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            // Đóng kết nối để không bị tràn bộ nhớ
            try { if(rs != null) rs.close(); if(pstmtTK != null) pstmtTK.close(); if(pstmtKH != null) pstmtKH.close(); } catch(Exception e){}
        }
        return isSuccess;
    }

    // 2. HÀM XỬ LÝ ĐĂNG NHẬP
    public int kiemTraDangNhap(String username, String password) {
        int maVaiTro = -1; // Mặc định -1 là sai tài khoản/mật khẩu
        String sql = "SELECT MAVAITRO FROM TAIKHOAN WHERE USERNAME = ? AND PASSWORD = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                maVaiTro = rs.getInt("MAVAITRO"); // Nếu đúng, trả về 1 (Khách) hoặc 2 (Admin)
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return maVaiTro;
    }
        public List<Object[]> getDanhSachVeCuaKhachHang(String username) {
    List<Object[]> list = new ArrayList<>();
    String sql = "SELECT p.TENPHIM, p.HINHANH, lc.TGCHIEU, pc.TENPHONG, g.TENGHE, v.GIAVE " +
                 "FROM TAIKHOAN tk " +
                 "JOIN KHACHHANG kh ON tk.MATK = kh.MATK " +
                 "JOIN VE v ON kh.MAKH = v.MAKHACHHANG " +
                 "JOIN LICHCHIEU lc ON v.MALICHCHIEU = lc.MALICHCHIEU " +
                 "JOIN PHIM p ON lc.MAPHIM = p.MAPHIM " +
                 "JOIN GHENGOI g ON v.MAGHE = g.MAGHE " +
                 "JOIN PHONGCHIEU pc ON lc.MAPHONG = pc.MAPHONG " +
                 "WHERE tk.USERNAME = ?";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        
        while (rs.next()) {
            Object[] movieInfo = new Object[]{rs.getString("TENPHIM"), rs.getString("HINHANH")};
            String showtime = rs.getString("TGCHIEU") + " | " + rs.getString("TENPHONG");
            String seat = rs.getString("TENGHE");
            String price = String.format("%,.0f đ", rs.getDouble("GIAVE"));
            
            list.add(new Object[]{movieInfo, showtime, seat, price, ""});
        }
        // Dòng này để ní check xem Java có thấy vé nào không
        System.out.println("DEBUG DAO: Username '" + username + "' co " + list.size() + " ve.");
        
    } catch (Exception e) {
        System.out.println("DEBUG DAO LỖI: " + e.getMessage());
        e.printStackTrace();
    }
    return list;
}
        public List<Object[]> getVeDaMua(String username) {
        List<Object[]> dsVe = new ArrayList<>();
        String sql = "SELECT p.TENPHIM, p.HINHANH, lc.TGCHIEU, lc.MAPHONG, g.TENGHE, v.GIAVE " +
                 "FROM KHACHHANG kh " +
                 "JOIN VE v ON kh.MAKH = v.MAKHACHHANG " +
                 "JOIN LICHCHIEU lc ON v.MALICHCHIEU = lc.MALICHCHIEU " +
                 "JOIN PHIM p ON lc.MAPHIM = p.MAPHIM " +
                 "JOIN GHE g ON v.MAGHE = g.MAGHE " +
                 "JOIN PHONGCHIEU pc ON lc.MAPHONG = pc.MAPHONG " +
                 "WHERE kh.USERNAME = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                dsVe.add(new Object[]{
                    new Object[]{rs.getString("TENPHIM"), rs.getString("HINHANH")}, // Cột 0 (Object mảng)
                    rs.getString("TGCHIEU") + " | Phòng " + rs.getString("MAPHONG"), // Cột 1
                    rs.getString("TENGHE"),                                         // Cột 2
                    String.format("%,.0f đ", rs.getDouble("GIAVE")),               // Cột 3
                    "" // Cột trạng thái (Để Renderer tự check ngày)
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
        return dsVe;
    }
}