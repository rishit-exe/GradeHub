package com.cgpa.frontend.ui.swing;

import com.cgpa.frontend.ui.theme.SystemTheme;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class ButtomImage extends JButton {

    private boolean selected = false;

    public ButtomImage() {
        setContentAreaFilled(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(null);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {
                if (!selected) {
                    // Use the system theme main color for hover border so previews match the palette
                    setBorder(javax.swing.BorderFactory.createLineBorder(SystemTheme.mainColor, 2));
                }
            }

            @Override
            public void mouseExited(MouseEvent me) {
                if (!selected) {
                    setBorder(null);
                }
            }
        });
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
            setBorder(javax.swing.BorderFactory.createLineBorder(SystemTheme.mainColor, 2));
        } else {
            setBorder(null);
        }
    }

    @Override
    protected void paintComponent(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (getIcon() != null) {
            g2.drawImage(((ImageIcon) getIcon()).getImage(), 0, 0, getWidth(), getHeight(), null);
        }
        g2.dispose();
        super.paintComponent(grphcs);
    }
} 