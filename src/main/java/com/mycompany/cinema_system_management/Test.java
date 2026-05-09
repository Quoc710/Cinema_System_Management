import java.sql.*;

public class Test {
    public static void main(String[] args) {
        String URL = "jdbc:oracle:thin:@localhost:1521:XE";
        String USER = "BTTHUCHANH_01_IS210";
        String PASSWORD = "123456";
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Kết nối Oracle thành công!");
            
            String sql = "SELECT tp.MAPASS, tp.GIAPASS, p.TENPHIM, p.HINHANH, " +
                         "lc.TGCHIEU, g.TENGHE, g.LOAIGHE, pc.TENPHONG " +
                         "FROM TRANGPASSVE tp " +
                         "LEFT JOIN VE v ON tp.MAVE = v.MAVE " +
                         "LEFT JOIN LICHCHIEU lc ON v.MALICHCHIEU = lc.MALICHCHIEU " +
                         "LEFT JOIN PHIM p ON lc.MAPHIM = p.MAPHIM " +
                         "LEFT JOIN PHONGCHIEU pc ON lc.MAPHONG = pc.MAPHONG " +
                         "LEFT JOIN GHENGOI g ON v.MAGHE = g.MAGHE " +
                         "WHERE tp.TRANGTHAI_PASS = 1";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                System.out.println("Row: " + rs.getInt("MAPASS") + ", " + rs.getString("TENPHIM"));
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
