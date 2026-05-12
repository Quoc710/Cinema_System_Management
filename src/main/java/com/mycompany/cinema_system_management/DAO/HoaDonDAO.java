package com.mycompany.cinema_system_management.dao;

import com.mycompany.cinema_system_management.utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HoaDonDAO {

    public String thanhToanGiaoDich(String username, String tenPhim, String thongTinSuat, List<String> danhSachGhe, double tongTienGoc, Map<String, Integer> snackCart, String tenKM) {
        String sqlGetKH = "SELECT KH.MAKH, KH.SODUVI FROM KHACHHANG KH JOIN TAIKHOAN TK ON KH.MATK = TK.MATK WHERE TK.USERNAME = ?";
        String sqlTruTien = "UPDATE KHACHHANG SET SODUVI = SODUVI - ? WHERE MAKH = ?";
        String sqlCheckKM = "SELECT MAKM, GIATRIGIAM FROM KHUYENMAI WHERE TENKM = ? AND SOLUONGCONLAI > 0 AND SYSDATE >= NGAYBD AND SYSDATE <= NGAYKT";
        String sqlUpdateKM = "UPDATE KHUYENMAI SET SOLUONGDADUNG = SOLUONGDADUNG + 1, SOLUONGCONLAI = SOLUONGCONLAI - 1 WHERE MAKM = ?";
        String sqlInsertHD = "INSERT INTO HOA_DON (MAKH, MAKM, PHUONGTHUCTT, TONGTIENGOC, TIENGIAM, THANHTIEN, TRANGTHAIHD) VALUES (?, ?, 'Ví CineMarket Pay', ?, ?, ?, 1)";
        String sqlGetSP = "SELECT MASP, GIABAN FROM SANPHAM WHERE TENSP = ?";
        String sqlInsertCTHD = "INSERT INTO CTHOADON (MAHD, MASP, SOLUONG, GIABAN, TRANGTHAINHANMON) VALUES (?, ?, ?, ?, 0)";
        
        // CÂU LỆNH TRỪ TỒN KHO SẢN PHẨM
        String sqlUpdateKho = "UPDATE SANPHAM SET SOLUONGTONKHO = SOLUONGTONKHO - ? WHERE MASP = ?"; 
        
        String sqlGetLichChieu = "SELECT L.MALICHCHIEU, L.MAPHONG FROM LICHCHIEU L JOIN PHIM P ON L.MAPHIM = P.MAPHIM WHERE P.TENPHIM = ? AND TO_CHAR(L.TGCHIEU, 'HH24:MI') LIKE ? AND ROWNUM = 1"; 
        String sqlGetGhe = "SELECT MAGHE FROM GHENGOI WHERE TENGHE = ? AND MAPHONG = ?";
        String sqlInsertVe = "INSERT INTO VE (MAVE, MALICHCHIEU, MAKHACHHANG, MAGHE, MAHOADON, GIAVE, TRANGTHAIVE) VALUES (SEQ_VE.NEXTVAL, ?, ?, ?, ?, ?, 1)";
        String sqlInsertLSGD = "INSERT INTO LICHSUGIAODICH (MAKH, LOAIGD, SOTIEN, NOIDUNG, SODUSAUGD, MA_THAM_CHIEU) VALUES (?, 2, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); 

            int maKH = -1;
            double soDuVi = 0;
            try (PreparedStatement psKH = conn.prepareStatement(sqlGetKH)) {
                psKH.setString(1, username);
                ResultSet rsKH = psKH.executeQuery();
                if (rsKH.next()) {
                    maKH = rsKH.getInt("MAKH");
                    soDuVi = rsKH.getDouble("SODUVI");
                }
            }
            if (maKH == -1) return "LỖI: Không tìm thấy khách hàng.";

            Integer maKM = null;
            double tienGiam = 0;
            if (tenKM != null && !tenKM.contains("Chọn mã")) {
                try (PreparedStatement psKM = conn.prepareStatement(sqlCheckKM)) {
                    psKM.setString(1, tenKM);
                    ResultSet rsKM = psKM.executeQuery();
                    if (rsKM.next()) {
                        maKM = rsKM.getInt("MAKM");
                        tienGiam = rsKM.getDouble("GIATRIGIAM");
                    } else return "LỖI: Mã KM không hợp lệ.";
                }
            }

            double tongTienSnack = 0;
            if (snackCart != null) {
                try (PreparedStatement psGetSP = conn.prepareStatement(sqlGetSP)) {
                    for (String name : snackCart.keySet()) {
                        psGetSP.setString(1, name);
                        ResultSet rsSP = psGetSP.executeQuery();
                        if (rsSP.next()) tongTienSnack += rsSP.getDouble("GIABAN") * snackCart.get(name);
                    }
                }
            }

            double thanhTien = tongTienGoc + tongTienSnack - tienGiam;
            if (soDuVi < thanhTien) return "LỖI: Số dư không đủ.";

            // --- BƯỚC 1: TRỪ TIỀN KHÁCH HÀNG ---
            /* NOTE: SAU NÀY THAY THẾ BẰNG STORED PROCEDURE 
               ĐỂ ĐẢM BẢO VIỆC KIỂM TRA SỐ DƯ VÀ TRỪ TIỀN DIỄN RA TRONG 1 TRANSACTION CỦA DB.
            */
            try (PreparedStatement psTruTien = conn.prepareStatement(sqlTruTien)) {
                psTruTien.setDouble(1, thanhTien);
                psTruTien.setInt(2, maKH);
                psTruTien.executeUpdate();
            }

            // --- BƯỚC 2: TẠO HÓA ĐƠN ---
            int maHD = -1;
            try (PreparedStatement psHD = conn.prepareStatement(sqlInsertHD, new String[]{"MAHD"})) {
                psHD.setInt(1, maKH);
                if (maKM != null) psHD.setInt(2, maKM); else psHD.setNull(2, java.sql.Types.NUMERIC);
                psHD.setDouble(3, tongTienGoc + tongTienSnack);
                psHD.setDouble(4, tienGiam);
                psHD.setDouble(5, thanhTien);
                psHD.executeUpdate();
                ResultSet rsHD = psHD.getGeneratedKeys();
                if (rsHD.next()) maHD = rsHD.getInt(1);
            }

            // --- BƯỚC 3: GHI SAO KÊ BIẾN ĐỘNG VÍ ---
            String noiDungGD = "Thanh toán vé phim: " + tenPhim;
            if (snackCart != null && !snackCart.isEmpty()) {
                noiDungGD += " | Bắp nước: ";
                List<String> snacks = new ArrayList<>();
                for (Map.Entry<String, Integer> entry : snackCart.entrySet()) {
                    snacks.add(entry.getKey() + " (x" + entry.getValue() + ")");
                }
                noiDungGD += String.join(", ", snacks);
            }

            try (PreparedStatement psLSGD = conn.prepareStatement(sqlInsertLSGD)) {
                psLSGD.setInt(1, maKH);
                psLSGD.setDouble(2, thanhTien);
                psLSGD.setString(3, noiDungGD);
                psLSGD.setDouble(4, soDuVi - thanhTien); 
                psLSGD.setInt(5, maHD);
                psLSGD.executeUpdate();
            }

            // --- BƯỚC 4: XỬ LÝ BẮP NƯỚC (CHI TIẾT & TỒN KHO) ---
            if (snackCart != null) {
                try (PreparedStatement psGetSP = conn.prepareStatement(sqlGetSP);
                     PreparedStatement psCTHD = conn.prepareStatement(sqlInsertCTHD);
                     PreparedStatement psUpdKho = conn.prepareStatement(sqlUpdateKho)) {
                    
                    for (Map.Entry<String, Integer> entry : snackCart.entrySet()) {
                        psGetSP.setString(1, entry.getKey());
                        ResultSet rsSP = psGetSP.executeQuery();
                        if (rsSP.next()) {
                            int maSP = rsSP.getInt("MASP");
                            int soLuongMua = entry.getValue();
                            
                            psCTHD.setInt(1, maHD);
                            psCTHD.setInt(2, maSP);
                            psCTHD.setInt(3, soLuongMua);
                            psCTHD.setDouble(4, rsSP.getDouble("GIABAN"));
                            psCTHD.executeUpdate();
                            
                            /* NOTE: SAU NÀY THAY THẾ BẰNG STORED PROCEDURE 
                               Gom các lệnh INSERT CTHOADON và UPDATE SANPHAM vào một Procedure.
                               Nên kiểm tra điều kiện (SOLUONGTON >= soLuongMua) trước khi trừ.
                            */
                            psUpdKho.setInt(1, soLuongMua);
                            psUpdKho.setInt(2, maSP);
                            psUpdKho.executeUpdate();
                        }
                    }
                }
            }

            // --- BƯỚC 5: XUẤT VÉ XEM PHIM ---
            String timeFilter = "";
            if (thongTinSuat != null) {
                if (thongTinSuat.contains("•")) timeFilter = thongTinSuat.split("•")[0].trim();
                else if (thongTinSuat.contains(" ")) timeFilter = thongTinSuat.split(" ")[0].trim();
                else timeFilter = thongTinSuat.trim();
            }

            int maLichChieu = -1, maPhong = -1;
            try (PreparedStatement psLC = conn.prepareStatement(sqlGetLichChieu)) {
                psLC.setString(1, tenPhim);
                psLC.setString(2, "%" + timeFilter + "%");
                ResultSet rsLC = psLC.executeQuery();
                if (rsLC.next()) {
                    maLichChieu = rsLC.getInt("MALICHCHIEU");
                    maPhong = rsLC.getInt("MAPHONG");
                }
            }

            double giaVeLe = tongTienGoc / danhSachGhe.size();
            try (PreparedStatement psGetGhe = conn.prepareStatement(sqlGetGhe);
                 PreparedStatement psVe = conn.prepareStatement(sqlInsertVe)) {
                for (String tenGhe : danhSachGhe) {
                    psGetGhe.setString(1, tenGhe);
                    psGetGhe.setInt(2, maPhong);
                    ResultSet rsGhe = psGetGhe.executeQuery();
                    if (rsGhe.next()) {
                        psVe.setInt(1, maLichChieu);
                        psVe.setInt(2, maKH);
                        psVe.setInt(3, rsGhe.getInt("MAGHE"));
                        psVe.setInt(4, maHD);
                        psVe.setDouble(5, giaVeLe);
                        psVe.executeUpdate();
                    }
                }
            }

            conn.commit(); 
            return "SUCCESS";
        } catch (Exception e) {
            return "LỖI SQL: " + e.getMessage();
        }
    }

    public List<Object[]> getLichSuGiaoDich(String username) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT h.MAHD, p.TENPHIM, TO_CHAR(lc.TGCHIEU, 'DD/MM HH24:MI') as SUAT, " +
                     "       (SELECT LISTAGG(g.TENGHE, ', ') WITHIN GROUP (ORDER BY g.TENGHE) FROM VE v2 JOIN GHENGOI g ON v2.MAGHE = g.MAGHE WHERE v2.MAHOADON = h.MAHD) as GHE, " +
                     "       (SELECT LISTAGG(sp.TENSP || ' (x' || ct.SOLUONG || ')', ', ') WITHIN GROUP (ORDER BY sp.TENSP) FROM CTHOADON ct JOIN SANPHAM sp ON ct.MASP = sp.MASP WHERE ct.MAHD = h.MAHD) as DO_AN, " +
                     "       h.THANHTIEN, h.TRANGTHAIHD " +
                     "FROM HOA_DON h " +
                     "JOIN KHACHHANG k ON h.MAKH = k.MAKH " +
                     "JOIN TAIKHOAN tk ON k.MATK = tk.MATK " +
                     "LEFT JOIN VE v ON h.MAHD = v.MAHOADON " +
                     "LEFT JOIN LICHCHIEU lc ON v.MALICHCHIEU = lc.MALICHCHIEU " +
                     "LEFT JOIN PHIM p ON lc.MAPHIM = p.MAPHIM " +
                     "WHERE tk.USERNAME = ? " +
                     "GROUP BY h.MAHD, p.TENPHIM, lc.TGCHIEU, h.THANHTIEN, h.TRANGTHAIHD " +
                     "ORDER BY h.MAHD DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{
                    "#" + rs.getInt("MAHD"),
                    rs.getString("TENPHIM") != null ? rs.getString("TENPHIM") : "Mua bắp nước lẻ",
                    rs.getString("SUAT"),
                    rs.getString("GHE"),
                    rs.getString("DO_AN") != null ? rs.getString("DO_AN") : "Không mua bắp nước",
                    String.format("%,.0f đ", rs.getDouble("THANHTIEN")),
                    rs.getInt("TRANGTHAIHD") == 1 ? "Thành công" : "Đã hủy"
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<Object[]> getSaoKeVi(String username) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT L.MAGD, TO_CHAR(L.THOIGIAN, 'DD/MM/YYYY HH24:MI:SS') AS THOIGIAN, L.LOAIGD, L.SOTIEN, L.NOIDUNG, L.SODUSAUGD FROM LICHSUGIAODICH L JOIN KHACHHANG K ON L.MAKH = K.MAKH JOIN TAIKHOAN T ON K.MATK = T.MATK WHERE T.USERNAME = ? ORDER BY L.MAGD DESC";
        try (Connection conn = DatabaseConnection.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String loai = "";
                int l = rs.getInt("LOAIGD");
                if(l==1) loai = "Nạp tiền"; else if(l==2) loai = "Thanh toán vé"; else if(l==3) loai = "Bán lại vé chợ"; else loai = "Mua vé chợ";
                String tien = (l==1 || l==3 ? "+" : "-") + String.format("%,.0f đ", rs.getDouble("SOTIEN"));
                list.add(new Object[]{"GD" + rs.getInt("MAGD"), rs.getString("THOIGIAN"), loai, tien, rs.getString("NOIDUNG"), String.format("%,.0f đ", rs.getDouble("SODUSAUGD"))});
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}