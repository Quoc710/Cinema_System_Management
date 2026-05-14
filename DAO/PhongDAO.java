package com.mycompany.cinema_system_management.DAO;

import com.mycompany.cinema_system_management.models.Phong;
import com.mycompany.cinema_system_management.utils.DatabaseConnection; // Gọi file kết nối của m vô
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhongDAO {
    // Đã xóa mấy dòng url, user, pass rườm rà

    public List<Phong> getDanhSachPhong() {
        List<Phong> list = new ArrayList<>();
        String sql = "SELECT MaPhong, TenPhong FROM PhongChieu"; 
        
        // Gọi trực tiếp DatabaseConnection.getConnection()
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
             
            while (rs.next()) {
                list.add(new Phong(rs.getInt("MaPhong"), rs.getString("TenPhong")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}