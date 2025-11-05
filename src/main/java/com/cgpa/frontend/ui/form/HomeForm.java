package com.cgpa.frontend.ui.form;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import com.cgpa.backend.dao.StudentDao;
import com.cgpa.backend.dao.SubjectDao;
import com.cgpa.backend.model.Student;
import com.cgpa.backend.service.CgpaService;
import com.cgpa.frontend.ui.component.Form;

/**
 * Simplified, cleaner home/dashboard form.
 * Removed fragile custom painting and replaced with solid, opaque cards
 * and consistent spacing so the main dashboard looks tidy.
 */
public class HomeForm extends Form {
    private final StudentDao studentDao = new StudentDao();
    private final SubjectDao subjectDao = new SubjectDao();
    private final CgpaService cgpaService = new CgpaService(subjectDao);

    private JLabel totalStudentsLabel;
    private JLabel totalSubjectsLabel;
    private JLabel averageCgpaLabel;

    private final DecimalFormat cgpaFormat = new DecimalFormat("0.00");

    public HomeForm() {
        initComponents();
        loadStatsAsync();
    }

    @Override
    public void changeColor(Color color) {
        // HomeForm will respect the mainBody changeColor via its components if needed.
    }

    private void initComponents() {
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));
        setOpaque(false);

        // Add extra top space so cards sit lower
        add(javax.swing.Box.createVerticalStrut(40));

        // Glassmorphism welcome card (gradient + translucent)
        JPanel welcomeCard = new GlassCard();
        welcomeCard.setLayout(new javax.swing.BoxLayout(welcomeCard, javax.swing.BoxLayout.Y_AXIS));
        welcomeCard.setBorder(BorderFactory.createEmptyBorder(26, 40, 26, 40));
        welcomeCard.setAlignmentX(CENTER_ALIGNMENT);

        JLabel welcomeLabel = new JLabel("Welcome to GradeHub Management System");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setAlignmentX(CENTER_ALIGNMENT);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subtitleLabel = new JLabel("Academic Excellence Management System");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(230, 230, 230));
        subtitleLabel.setAlignmentX(CENTER_ALIGNMENT);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        welcomeCard.add(welcomeLabel);
        welcomeCard.add(javax.swing.Box.createVerticalStrut(8));
        welcomeCard.add(subtitleLabel);

        // Stats row centered and spaced lower
        JPanel statsRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 22, 18));
        statsRow.setOpaque(false);
        statsRow.setAlignmentX(CENTER_ALIGNMENT);

        // Prepare labels and glossy cards
        totalStudentsLabel = new JLabel("0");
        totalSubjectsLabel = new JLabel("0");
        averageCgpaLabel = new JLabel("0.00");

        statsRow.add(createGlossyCard("Total Students", totalStudentsLabel, new Color(52, 152, 219)));
        statsRow.add(createGlossyCard("Total Subjects", totalSubjectsLabel, new Color(46, 204, 113)));
        statsRow.add(createGlossyCard("Average CGPA", averageCgpaLabel, new Color(155, 89, 182)));

        // Add spacing and components
        add(welcomeCard);
        add(javax.swing.Box.createVerticalStrut(28));
        add(statsRow);
        add(javax.swing.Box.createVerticalGlue());
    }

    private JPanel createGlossyCard(String title, JLabel valueLabel, Color baseColor) {
        JPanel card = new GlossyCard(baseColor);
        card.setLayout(new javax.swing.BoxLayout(card, javax.swing.BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        card.setOpaque(false);
        card.setPreferredSize(new java.awt.Dimension(240, 120));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setAlignmentX(CENTER_ALIGNMENT);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(245, 245, 245));
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(valueLabel);
        card.add(javax.swing.Box.createVerticalStrut(6));
        card.add(titleLabel);

        return card;
    }

    private void loadStatsAsync() {
        new SwingWorker<Void, Void>() {
            private int totalStudents;
            private int totalSubjects;
            private double avgCgpa;

            @Override
            protected Void doInBackground() throws Exception {
                List<Student> students = studentDao.findAll();
                totalStudents = students.size();
                totalSubjects = subjectDao.findAll().size();

                double sum = 0.0;
                int countWithCgpa = 0;
                for (Student s : students) {
                    double cgpa = cgpaService.computeCgpaForStudent(s.getRollNumber());
                    if (cgpa > 0) {
                        sum += cgpa;
                        countWithCgpa++;
                    }
                }
                avgCgpa = countWithCgpa == 0 ? 0.0 : sum / countWithCgpa;
                return null;
            }

            @Override
            protected void done() {
                totalStudentsLabel.setText(String.valueOf(totalStudents));
                totalSubjectsLabel.setText(String.valueOf(totalSubjects));
                averageCgpaLabel.setText(cgpaFormat.format(avgCgpa));
            }
        }.execute();
    }

    // Decorative card with soft glass effect
    private static class GlassCard extends JPanel {
        public GlassCard() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            RoundRectangle2D rr = new RoundRectangle2D.Float(0, 0, w, h, 18, 18);
                // Gradient background (dark, blackish glassmorphism)
                java.awt.GradientPaint gp = new java.awt.GradientPaint(0, 0, new Color(30, 30, 30, 200), 0, h, new Color(10, 10, 10, 160));
                g2.setPaint(gp);
                g2.fill(rr);
                // translucent overlay to mimic frosted glass (slightly lightened)
                g2.setColor(new Color(255, 255, 255, 20));
                g2.fill(new RoundRectangle2D.Float(1, 1, w-2, h-2, 18, 18));
                // subtle border (soft white)
                g2.setColor(new Color(255, 255, 255, 45));
            g2.setStroke(new java.awt.BasicStroke(1f));
            g2.draw(rr);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // Glossy card background painting
    private static class GlossyCard extends JPanel {
        private final Color base;

        public GlossyCard(Color base) {
            this.base = base;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            RoundRectangle2D rr = new RoundRectangle2D.Float(0, 0, w, h, 16, 16);
            // main gradient - use darker tones so cards appear moody/dark
            Color top = base.darker().darker();
            Color bottom = base.darker();
            java.awt.GradientPaint gp = new java.awt.GradientPaint(0, 0, top, 0, h, bottom);
            g2.setPaint(gp);
            g2.fill(rr);
            // glossy sheen at top
            RoundRectangle2D sheen = new RoundRectangle2D.Float(4, 4, w-8, (h/2)-4, 14, 14);
            // make the sheen softer and slightly tinted so it doesn't read too bright
            g2.setPaint(new java.awt.GradientPaint(0, 0, new Color(255,255,255,90), 0, h/2, new Color(255,255,255,6)));
            g2.fill(sheen);
            // stronger inner shadow for depth
            g2.setColor(new Color(0,0,0,60));
            g2.draw(new RoundRectangle2D.Float(1, 1, w-2, h-2, 16, 16));
            g2.dispose();
            super.paintComponent(g);
        }
    }
}