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
                     "WHERE tp.TRANGTHAI_PASS = 1 AND lc.TGCHIEU > SYSDATE";

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
            e.printStackTrace(); 
        }
        return list;
    }

    public String thanhToanVeCho(int maPass, String buyerUsername) {
        String sqlGetNguoiMua = "SELECT MAKH, SODUVI FROM KHACHHANG WHERE MATK = (SELECT MATK FROM TAIKHOAN WHERE USERNAME = ?)";
        String sqlGetPass = "SELECT MAVE, MAKH_BAN, GIAPASS FROM TRANGPASSVE WHERE MAPASS = ? AND TRANGTHAI_PASS = 1";
        
        /* =========================================================================
           NOTE BÁO CÁO ĐỒ ÁN (KIẾN TRÚC DATABASE):
           Toàn bộ các lệnh UPDATE và INSERT dưới đây sau này nên được gộp vào 1 STORED PROCEDURE 
           trên Oracle (ví dụ: PR_MUA_VE_CHO). 
           Từ Java chỉ cần gọi: CallableStatement cs = conn.prepareCall("{call PR_MUA_VE_CHO(?, ?, ?)}");
           Điều này giúp bảo mật giao dịch tài chính, tránh đứt mạng giữa chừng làm sai lệch số dư.
           =========================================================================
        */
        String sqlTruTien = "UPDATE KHACHHANG SET SODUVI = SODUVI - ? WHERE MAKH = ?";
        String sqlCongTien = "UPDATE KHACHHANG SET SODUVI = SODUVI + ? WHERE MAKH = ?";
        String sqlUpdatePass = "UPDATE TRANGPASSVE SET MAKH_MUA = ?, TRANGTHAI_PASS = 2 WHERE MAPASS = ?";
        String sqlUpdateVe = "UPDATE VE SET MAKHACHHANG = ?, TRANGTHAIVE = 1 WHERE MAVE = ?";
        String sqlInsertLog = "INSERT INTO LICHSUGIAODICH (MAKH, LOAIGD, SOTIEN, NOIDUNG, SODUSAUGD, MA_THAM_CHIEU) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            int maKhMua = -1; double soDuMua = 0;
            try (PreparedStatement ps = conn.prepareStatement(sqlGetNguoiMua)) {
                ps.setString(1, buyerUsername);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) { maKhMua = rs.getInt("MAKH"); soDuMua = rs.getDouble("SODUVI"); }
            }

            int maVe = -1, maKhBan = -1; double giaPass = 0;
            try (PreparedStatement ps = conn.prepareStatement(sqlGetPass)) {
                ps.setInt(1, maPass);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) { maVe = rs.getInt("MAVE"); maKhBan = rs.getInt("MAKH_BAN"); giaPass = rs.getDouble("GIAPASS"); }
                else return "Vé không còn tồn tại hoặc đã bị mua!";
            }

            if (maKhMua == maKhBan) return "Không thể tự mua vé của chính mình!";
            if (soDuMua < giaPass) return "Số dư trong ví không đủ!";

            double soDuBan = 0;
            try (PreparedStatement ps = conn.prepareStatement("SELECT SODUVI FROM KHACHHANG WHERE MAKH = ?")) {
                ps.setInt(1, maKhBan);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) soDuBan = rs.getDouble("SODUVI");
            }

            try (PreparedStatement ps1 = conn.prepareStatement(sqlTruTien);
                 PreparedStatement ps2 = conn.prepareStatement(sqlCongTien)) {
                ps1.setDouble(1, giaPass); ps1.setInt(2, maKhMua); ps1.executeUpdate();
                ps2.setDouble(1, giaPass); ps2.setInt(2, maKhBan); ps2.executeUpdate();
            }
            
            try (PreparedStatement ps1 = conn.prepareStatement(sqlUpdatePass);
                 PreparedStatement ps2 = conn.prepareStatement(sqlUpdateVe)) {
                ps1.setInt(1, maKhMua); ps1.setInt(2, maPass); ps1.executeUpdate();
                ps2.setInt(1, maKhMua); ps2.setInt(2, maVe); ps2.executeUpdate();
            }

            try (PreparedStatement psL = conn.prepareStatement(sqlInsertLog)) {
                // Ghi sao kê cho Người Mua (LOAIGD = 4)
                psL.setInt(1, maKhMua); 
                psL.setInt(2, 4); 
                psL.setDouble(3, giaPass);
                psL.setString(4, "Mua vé xem phim trên Chợ (Mã vé: " + maVe + ")");
                psL.setDouble(5, soDuMua - giaPass); 
                psL.setInt(6, maPass); 
                psL.executeUpdate();

                // Ghi sao kê cho Người Bán (LOAIGD = 3)
                psL.setInt(1, maKhBan); 
                psL.setInt(2, 3); 
                psL.setDouble(3, giaPass);
                psL.setString(4, "Bán vé xem phim trên Chợ thành công (Mã vé: " + maVe + ")");
                psL.setDouble(5, soDuBan + giaPass); 
                psL.setInt(6, maPass); 
                psL.executeUpdate();
            }

            conn.commit();
            return "SUCCESS";
        } catch (Exception e) { 
            e.printStackTrace(); 
            return "Lỗi Server: " + e.getMessage(); 
        }
    }
}