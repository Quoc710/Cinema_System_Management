package com.mycompany.cinema_system_management.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StatCard extends JPanel {
    private final JLabel lblValue, lblSub;

    public StatCard(String title, String value, String subText, String iconName, Color color) {
        setLayout(new BorderLayout());
        putClientProperty(FlatClientProperties.STYLE, "arc: 16; background: #FFFFFF");
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel pnlText = new JPanel(new GridLayout(3, 1, 0, 4));
        pnlText.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Inter", Font.BOLD, 12));
        lblTitle.setForeground(new Color(100, 116, 139));

        lblValue = new JLabel(value);
        lblValue.setFont(new Font("Inter", Font.BOLD, 22));
        lblValue.setForeground(new Color(15, 23, 42));

        lblSub = new JLabel(subText);
        lblSub.setFont(new Font("Inter", Font.BOLD, 12));
        lblSub.setForeground(new Color(34, 197, 94));

        pnlText.add(lblTitle); pnlText.add(lblValue); pnlText.add(lblSub);

        try {
            FlatSVGIcon icon = new FlatSVGIcon("icons/" + iconName + ".svg", 24, 24);
            icon.setColorFilter(new FlatSVGIcon.ColorFilter(c -> color));
            add(new JLabel(icon), BorderLayout.EAST);
        } catch (Exception e) {}
        
        add(pnlText, BorderLayout.CENTER);
    }

    public void setData(String value, String subText) {
        lblValue.setText(value);
        lblSub.setText(subText);
        lblSub.setForeground(subText.startsWith("-") ? new Color(239, 68, 68) : new Color(34, 197, 94));
    }
}