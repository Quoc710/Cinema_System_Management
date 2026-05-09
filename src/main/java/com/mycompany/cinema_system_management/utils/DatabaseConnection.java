/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.cinema_system_management.utils;

/**
 *
 * @author Dell
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Sửa lại thông tin này cho khớp với máy ní nha
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:XE"; // XE hoặc ORCL tùy máy
    private static final String USER = "HuuTai"; // Tên user Oracle ní tạo
    private static final String PASSWORD = "12345"; // Mật khẩu Oracle

    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Nạp driver Oracle
            Class.forName("oracle.jdbc.driver.OracleDriver");
            // Mở kết nối
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Kết nối Oracle thành công rồi ní ơi!");
        } catch (ClassNotFoundException e) {
            System.out.println("Lỗi: Không tìm thấy file ojdbc.jar!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Lỗi: Sai username, password hoặc chưa bật Oracle!");
            e.printStackTrace();
        }
        return conn;
    }

    // Hàm main để test chạy thử ngay tại chỗ
    public static void main(String[] args) {
        getConnection();
    }
}
