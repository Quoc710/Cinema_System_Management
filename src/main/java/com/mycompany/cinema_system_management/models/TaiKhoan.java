/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.cinema_system_management.models;

/**
 *
 * @author Dell
 */
public class TaiKhoan {
    private int maTK;
    private int maVaiTro;
    private String username;
    private String password;

    // 1. Constructor rỗng (Bắt buộc phải có khi làm việc với Database)
    public TaiKhoan() {
    }

    // 2. Constructor đầy đủ tham số
    public TaiKhoan(int maTK, int maVaiTro, String username, String password) {
        this.maTK = maTK;
        this.maVaiTro = maVaiTro;
        this.username = username;
        this.password = password;
    }

    public int getMaTK() {
        return maTK;
    }

    public int getMaVaiTro() {
        return maVaiTro;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setMaTK(int maTK) {
        this.maTK = maTK;
    }

    public void setMaVaiTro(int maVaiTro) {
        this.maVaiTro = maVaiTro;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
}
