
package com.cgpa.frontend.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.cgpa.backend.dao.StudentDao;
import com.cgpa.backend.dao.SubjectDao;
import com.cgpa.backend.service.CgpaService;
import com.cgpa.frontend.ui.components.CgpaPanel;
import com.cgpa.frontend.ui.components.StudentFormPanel;
import com.cgpa.frontend.ui.components.SubjectFormPanel;

public class ModernStudentDashboard extends JPanel {
    private JFrame parentFrame;
    JTabbedPane tabs;
    /**
     * Set the selected tab by index (0: Student Management, 1: Subject Management, 2: CGPA Calculator)
     */
    public void setSelectedTab(int index) {
        if (tabs != null && index >= 0 && index < tabs.getTabCount()) {
            tabs.setSelectedIndex(index);
        }
    }
    
    // Enhanced colors for modern theme
    private static final Color CARD_BG = Color.WHITE;
    private static final Color ACCENT_COLOR = new Color(52, 152, 219);
    private static final Color ACCENT_DARK = new Color(41, 128, 185);
    private static final Color TEXT_COLOR = new Color(52, 73, 94);
    private static final Color GRADIENT_START = new Color(52, 152, 219);
    private static final Color GRADIENT_END = new Color(41, 128, 185);
    
    public ModernStudentDashboard() {
        setLayout(new BorderLayout());
        // Use an opaque white background for the dashboard so tab content doesn't show through
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setOpaque(true);
        
        createHeader();
        createTabs();
    }
    
    private void createHeader() {
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                // Clear background first
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create rounded rectangle background
                int arc = 20;
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                
                // Add subtle gradient overlay (very light)
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(52, 152, 219, 20),
                    getWidth(), 0, new Color(41, 128, 185, 20)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                
                // Add subtle border
                g2d.setColor(new Color(52, 152, 219, 100));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
                
                g2d.dispose();
            }
        };
        
        headerPanel.setLayout(new BorderLayout(20, 20));
    headerPanel.setOpaque(true);
    headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        headerPanel.setPreferredSize(new Dimension(0, 120));
        
        // Left side with icon and title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        leftPanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel("🎓");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Student Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(new Color(40, 40, 40));
        
        JLabel subtitleLabel = new JLabel("Manage your academic information and calculate CGPA");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(80, 80, 80));
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);
        
        leftPanel.add(iconLabel);
        leftPanel.add(titlePanel);
        
        // Right side with stats or quick info
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);
        
        // Add some quick stats cards
        rightPanel.add(createQuickStatCard("📚", "Subjects", "Manage"));
        rightPanel.add(createQuickStatCard("📊", "CGPA", "Calculate"));
        rightPanel.add(createQuickStatCard("👤", "Profile", "Update"));
        
        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
    }
    
    private JPanel createQuickStatCard(String emoji, String title, String action) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create rounded rectangle
                int arc = 15;
                g2d.setColor(new Color(255, 255, 255, 180));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                
                // Add subtle border
                g2d.setColor(new Color(52, 152, 219, 80));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(80, 70));
        card.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel iconLabel = new JLabel(emoji);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        iconLabel.setForeground(Color.BLACK);

        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        titleLabel.setForeground(new Color(60, 60, 60));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel actionLabel = new JLabel(action);
        actionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        actionLabel.setForeground(new Color(100, 100, 100));
        actionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(iconLabel);
        card.add(Box.createVerticalStrut(2));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(2));
        card.add(actionLabel);
        
        // Add hover effect
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setPreferredSize(new Dimension(85, 75));
                card.revalidate();
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setPreferredSize(new Dimension(80, 70));
                card.revalidate();
            }
        });
        
        return card;
    }
    
    private void createTabs() {
        tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));
    tabs.setBackground(Color.WHITE);
    tabs.setOpaque(true);
        
        StudentDao studentDao = new StudentDao();
        SubjectDao subjectDao = new SubjectDao();
        CgpaService cgpaService = new CgpaService(subjectDao);

    // Create modern tab panels and wrap each in an opaque white panel to ensure
    // the selected tab fully covers underlying content (prevents bleed-through)
    JPanel studentWrapper = new JPanel(new BorderLayout());
    studentWrapper.setOpaque(true);
    studentWrapper.setBackground(Color.WHITE);
    studentWrapper.add(new StudentFormPanel(studentDao), BorderLayout.CENTER);

    JPanel subjectWrapper = new JPanel(new BorderLayout());
    subjectWrapper.setOpaque(true);
    subjectWrapper.setBackground(Color.WHITE);
    subjectWrapper.add(new SubjectFormPanel(studentDao, subjectDao), BorderLayout.CENTER);

    JPanel cgpaWrapper = new JPanel(new BorderLayout());
    cgpaWrapper.setOpaque(true);
    cgpaWrapper.setBackground(Color.WHITE);
    cgpaWrapper.add(new CgpaPanel(studentDao, cgpaService), BorderLayout.CENTER);

    tabs.addTab("Student Management", studentWrapper);
    tabs.addTab("Subject Management", subjectWrapper);
    tabs.addTab("CGPA Calculator", cgpaWrapper);
        // Ensure only the selected tab's component is visible to avoid any painting or
        // hover side-effects from non-selected tabs (prevents buttons from appearing
        // when hovering their screen area while a different tab is active).
        tabs.addChangeListener(e -> {
            int sel = tabs.getSelectedIndex();
            for (int i = 0; i < tabs.getTabCount(); i++) {
                Component comp = tabs.getComponentAt(i);
                if (comp != null) {
                    comp.setVisible(i == sel);
                    // force layout/paint on the selected component
                    if (i == sel) {
                        comp.revalidate();
                        comp.repaint();
                    }
                }
            }
        });
        
        add(tabs, BorderLayout.CENTER);
        // Ensure double buffering and force a clean repaint once shown to avoid stale artifacts
        tabs.setDoubleBuffered(true);
        javax.swing.SwingUtilities.invokeLater(() -> {
            // Ensure only the current tab is visible at startup
            int sel = tabs.getSelectedIndex();
            for (int i = 0; i < tabs.getTabCount(); i++) {
                Component comp = tabs.getComponentAt(i);
                if (comp != null) comp.setVisible(i == sel);
            }
            tabs.revalidate();
            tabs.repaint();
        });
    }
    
    public void setParentFrame(JFrame frame) {
        this.parentFrame = frame;
    }
} 