package com.cgpa.frontend.ui.component;

import com.cgpa.frontend.ui.theme.ThemeColorChange;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import net.miginfocom.swing.MigLayout;
 

public class ImageBackgroundOption extends javax.swing.JPanel {

    private MigLayout layout;

    public ImageBackgroundOption() {
        initComponents();
        layout = new MigLayout("fill, wrap 1, inset 0", "[fill]", "[]0[68!]");
        setLayout(layout);
        // Previews are always shown; no enable/disable switch.
        addEvent();
    }

    public void changeColorLabel(Color color) {
        lbBack.setForeground(color);
        // Update any selected preview border to the new theme color so previews match
        for (Component com : panel.getComponents()) {
            if (com instanceof javax.swing.JButton) {
                javax.swing.JButton btn = (javax.swing.JButton) com;
                if (btn.isSelected()) {
                    btn.setBorder(javax.swing.BorderFactory.createLineBorder(color, 2));
                }
            }
        }
    }

    private void addEvent() {
        int image = 1;
        for (Component com : panel.getComponents()) {
            JButton cmd = (JButton) com;
            cmd.setName("bg_" + image++ + ".jpg");
            cmd.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    clearSelected();
                    cmd.setSelected(true);
                    ThemeColorChange.getInstance().changeBackgroundImage(cmd.getName());
                    // Save to properties later
                }
            });
        }
    }

    private void clearSelected() {
        for (Component com : panel.getComponents()) {
            JButton cmd = (JButton) com;
            cmd.setSelected(false);
        }
    }

    public void init(String imageSelected) {
        // Always expand previews and select the provided image (if any).
        setSelected(imageSelected);
        layout.setRowConstraints("[]0[68!]");
        revalidate();
    }

    public void setSelected(String imageSelected) {
        clearSelected();
        for (Component com : panel.getComponents()) {
            JButton cmd = (JButton) com;
            if (cmd.getName().equals(imageSelected)) {
                cmd.setSelected(true);
                break;
            }
        }
    }

    private void initComponents() {
    jPanel1 = new javax.swing.JPanel();
    lbBack = new javax.swing.JLabel();
    jLabel2 = new javax.swing.JLabel();
    panel = new javax.swing.JPanel();
        buttomImage1 = new com.cgpa.frontend.ui.swing.ButtomImage();
        buttomImage2 = new com.cgpa.frontend.ui.swing.ButtomImage();
        buttomImage3 = new com.cgpa.frontend.ui.swing.ButtomImage();
        buttomImage4 = new com.cgpa.frontend.ui.swing.ButtomImage();
        buttomImage5 = new com.cgpa.frontend.ui.swing.ButtomImage();

        setOpaque(false);

        jPanel1.setOpaque(false);

        lbBack.setFont(new java.awt.Font("sansserif", 1, 16));
        lbBack.setForeground(new java.awt.Color(230, 230, 230));
        lbBack.setText("Background Image");

    // Make descriptive subtext white so it reads on dark backgrounds
    jLabel2.setForeground(Color.WHITE);
    jLabel2.setText("Use simple image as background of system");

        // No enable/disable switch panel: previews are always visible.

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbBack)
                    .addComponent(jLabel2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lbBack)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)))
                .addContainerGap())
        );

        panel.setOpaque(false);

        buttomImage1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/backgrounds/bg_1_small.jpg")));
        buttomImage1.setSelected(true);

        buttomImage2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/backgrounds/bg_2_small.jpg")));

        buttomImage3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/backgrounds/bg_3_small.jpg")));

        buttomImage4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/backgrounds/bg_4_small.jpg")));

        buttomImage5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/backgrounds/bg_5_small.jpg")));

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(buttomImage1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttomImage2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttomImage3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttomImage4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttomImage5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(283, Short.MAX_VALUE))
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(buttomImage5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttomImage4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttomImage3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttomImage2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttomImage1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

    javax.swing.GroupLayout groupLayout = new javax.swing.GroupLayout(this);
        this.setLayout(groupLayout);
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }

    private com.cgpa.frontend.ui.swing.ButtomImage buttomImage1;
    private com.cgpa.frontend.ui.swing.ButtomImage buttomImage2;
    private com.cgpa.frontend.ui.swing.ButtomImage buttomImage3;
    private com.cgpa.frontend.ui.swing.ButtomImage buttomImage4;
    private com.cgpa.frontend.ui.swing.ButtomImage buttomImage5;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lbBack;
    private javax.swing.JPanel panel;
    
} 