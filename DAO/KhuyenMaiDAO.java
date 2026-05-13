    package com.mycompany.cinema_system_management.DAO;

    import com.mycompany.cinema_system_management.utils.DatabaseConnection; // Khớp tên file của cha
    import com.mycompany.cinema_system_management.models.KhuyenMai;
    import java.sql.*;
    import java.util.ArrayList;
    import java.util.List;

    public class KhuyenMaiDAO {

        // 1. Lấy danh sách khuyến mãi (Dùng cho bảng)
        public List<KhuyenMai> getAllKhuyenMai() {
            List<KhuyenMai> list = new ArrayList<>();
            // SQL dùng CASE WHEN để tính trạng thái trực tiếp từ Database
            String sql = "SELECT MAKM, TENKM, NGAYBD, NGAYKT, SOLUONGDADUNG, SOLUONGCONLAI, GIATRIGIAM, "
                       + "CASE "
                       + "  WHEN NGAYKT < SYSDATE THEN 'Hết hạn' "
                       + "  WHEN SOLUONGCONLAI <= 0 THEN 'Hết lượt' "
                       + "  ELSE 'Đang chạy' "
                       + "END AS TRANG_THAI "
                       + "FROM KHUYENMAI ORDER BY MAKM DESC";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    KhuyenMai km = new KhuyenMai();
                    km.setMaKM(rs.getInt("MAKM"));
                    km.setTenKM(rs.getString("TENKM"));
                    km.setNgayBD(rs.getDate("NGAYBD"));
                    km.setNgayKT(rs.getDate("NGAYKT"));
                    km.setSoLuongDaDung(rs.getInt("SOLUONGDADUNG"));
                    km.setSoLuongConLai(rs.getInt("SOLUONGCONLAI"));
                    km.setGiaTriGiam(rs.getDouble("GIATRIGIAM"));
                    km.setTrangThai(rs.getString("TRANG_THAI"));
                    list.add(km);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return list;
        }

        // 2. Thêm mới khuyến mãi
        public boolean insertKhuyenMai(KhuyenMai km) {
            String sql = "INSERT INTO KHUYENMAI (TENKM, NGAYBD, NGAYKT, SOLUONGDADUNG, SOLUONGCONLAI, GIATRIGIAM) "
                       + "VALUES (?, ?, ?, 0, ?, ?)";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, km.getTenKM());
                // Oracle yêu cầu java.sql.Date
                ps.setDate(2, new java.sql.Date(km.getNgayBD().getTime()));
                ps.setDate(3, new java.sql.Date(km.getNgayKT().getTime()));
                ps.setInt(4, km.getSoLuongConLai());
                ps.setDouble(5, km.getGiaTriGiam());

                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Lỗi Insert: " + e.getMessage());
                return false;
            }
        }

        // 3. Xóa khuyến mãi (Xóa cứng trong DB)
        public boolean deleteKhuyenMai(int maKM) {
            String sql = "DELETE FROM KHUYENMAI WHERE MAKM = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, maKM);
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                // Lỗi này thường do mã KM đang được dùng ở bảng HOA_DON (Khóa ngoại)
                System.err.println("Lỗi Delete: Không thể xóa mã đang có dữ liệu liên quan!");
                return false;
            }
        }

        // 4. Cập nhật khuyến mãi (Dùng cho chức năng Sửa)
        public boolean updateKhuyenMai(KhuyenMai km) {
            String sql = "UPDATE KHUYENMAI SET TENKM = ?, NGAYBD = ?, NGAYKT = ?, "
                       + "SOLUONGCONLAI = ?, GIATRIGIAM = ? WHERE MAKM = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, km.getTenKM());
                ps.setDate(2, new java.sql.Date(km.getNgayBD().getTime()));
                ps.setDate(3, new java.sql.Date(km.getNgayKT().getTime()));
                ps.setInt(4, km.getSoLuongConLai());
                ps.setDouble(5, km.getGiaTriGiam());
                ps.setInt(6, km.getMaKM());

                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Lỗi Update: " + e.getMessage());
                return false;
            }
        }
    }