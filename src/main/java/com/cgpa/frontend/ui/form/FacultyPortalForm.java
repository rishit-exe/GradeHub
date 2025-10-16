package com.cgpa.frontend.ui.form;

import com.cgpa.backend.service.FacultyAuthService;
import com.cgpa.frontend.ui.component.Form;
import com.cgpa.frontend.ui.component.FacultyFlashcard;
import com.cgpa.frontend.ui.dialog.FacultyLoginDialog;
import com.cgpa.backend.dao.StudentDao;
import com.cgpa.backend.dao.SubjectDao;
import com.cgpa.backend.model.Student;
import com.cgpa.backend.model.Subject;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Map;

public class FacultyPortalForm extends Form {
    
    private FacultyAuthService authService;
    private boolean isAuthenticated = false;
    private String currentUsername = "";
    
    // UI Components
    private JPanel loginPanel;
    private JPanel dashboardPanel;
    private JPanel flashcardPanel;
    private JButton logoutButton;
    
    // Flashcards
    private FacultyFlashcard studentRecordsCard;
    private FacultyFlashcard subjectRecordsCard;
    private FacultyFlashcard resultRecordsCard;
    
    // Database and Table
    private StudentDao studentDao;
    private SubjectDao subjectDao;
    private DefaultTableModel studentTableModel;
    private JTable studentTable;
    private DefaultTableModel subjectTableModel;
    private JTable subjectTable;
    
    // Subject Records state
    private boolean isFilteredByStudent = false;
    private String currentFilteredStudentRoll = "";
    private JButton backToStudentsButton;
    private Integer currentSemesterFilter = null;
    
    public FacultyPortalForm() {
        initServices();
        initComponents();
    }
    
    private void initServices() {
        try {
            authService = new FacultyAuthService();
            studentDao = new StudentDao();
            subjectDao = new SubjectDao();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error initializing services: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    @Override
    public void changeColor(Color color) {
        // Update colors when theme changes
    }
    
    @Override
    public void setActive(boolean active) {
        super.setActive(active);
        if (active) {
            // Check authentication when form becomes active
            if (!isAuthenticated) {
                showLoginPanel();
            }
            repaint();
            revalidate();
        }
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setOpaque(false); // Make transparent to show background image
        
        // Create login panel
        createLoginPanel();
        
        // Create dashboard panel
        createDashboardPanel();
        
        // Initially show login panel
        showLoginPanel();
    }
    
    private void createLoginPanel() {
        loginPanel = new JPanel(new BorderLayout());
        loginPanel.setOpaque(false);
        
        // Main content panel with semi-transparent black background
        JPanel mainContentPanel = new JPanel(new BorderLayout(20, 20));
        mainContentPanel.setBackground(new Color(0, 0, 0, 120)); // Semi-transparent black background
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(60, 40, 60, 40));
        
        // Title
        JLabel titleLabel = new JLabel("Faculty Portal", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Please login to access faculty features", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitleLabel.setForeground(new Color(200, 200, 200));
        
        // Login form panel
        JPanel loginFormPanel = createEmbeddedLoginForm();
        
        // Center panel for title, subtitle, and login form
        JPanel centerPanel = new JPanel(new BorderLayout(30, 0));
        centerPanel.setOpaque(false);
        
        JPanel textPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);
        
        centerPanel.add(textPanel, BorderLayout.NORTH);
        centerPanel.add(loginFormPanel, BorderLayout.CENTER);
        
        mainContentPanel.add(centerPanel, BorderLayout.CENTER);
        loginPanel.add(mainContentPanel, BorderLayout.CENTER);
    }
    
    private JPanel createEmbeddedLoginForm() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.CENTER;
        
        // Username label and field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        usernameLabel.setForeground(Color.WHITE);
        
        JTextField usernameField = new JTextField(20);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setPreferredSize(new Dimension(200, 35));
        
        // Password label and field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passwordLabel.setForeground(Color.WHITE);
        
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(200, 35));
        
        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setPreferredSize(new Dimension(120, 40));
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        
        // Add components to form
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 8, 8, 8);
        formPanel.add(loginButton, gbc);
        
        // Add action listeners
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin(usernameField.getText(), passwordField.getPassword());
            }
        });
        
        // Enter key in password field
        passwordField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    performLogin(usernameField.getText(), passwordField.getPassword());
                }
            }
        });
        
        // Enter key in username field
        usernameField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    passwordField.requestFocus();
                }
            }
        });
        
        return formPanel;
    }
    
    private void createDashboardPanel() {
        dashboardPanel = new JPanel(new BorderLayout());
        dashboardPanel.setOpaque(false);
        
        // Main content panel with semi-transparent black background
        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(new Color(0, 0, 0, 120)); // Semi-transparent black background
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header panel with logout button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Welcome label
        JLabel welcomeLabel = new JLabel("Welcome, Faculty Member!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        
        // Logout button
        logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        logoutButton.setPreferredSize(new Dimension(100, 35));
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showLogoutConfirmation();
            }
        });
        
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);
        
        // Create flashcard panel
        createFlashcardPanel();
        
        mainContentPanel.add(headerPanel, BorderLayout.NORTH);
        mainContentPanel.add(flashcardPanel, BorderLayout.CENTER);
        
        dashboardPanel.add(mainContentPanel, BorderLayout.CENTER);
    }
    
    private void createFlashcardPanel() {
        flashcardPanel = new JPanel(new BorderLayout());
        flashcardPanel.setOpaque(false);
        flashcardPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));
        
        // Top row with 2 cards
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 0));
        topRow.setOpaque(false);
        
        // Student Records Card
        studentRecordsCard = new FacultyFlashcard(
            "Student Records", 
            "View and manage student information. Personal details and academic records. Complete student management system.", 
            "👥", 
            new Color(52, 152, 219)
        );
        studentRecordsCard.setClickListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showStudentRecords();
            }
        });
        
        // Subject Records Card
        subjectRecordsCard = new FacultyFlashcard(
            "Subject Records", 
            "Manage course subjects and curriculum details. Academic programs and course structure. Complete subject management.", 
            "📚", 
            new Color(46, 204, 113)
        );
        subjectRecordsCard.setClickListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSubjectRecords();
            }
        });
        
        topRow.add(studentRecordsCard);
        topRow.add(subjectRecordsCard);
        
        // Bottom row with 1 centered card - with proper spacing
        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        bottomRow.setOpaque(false);
        bottomRow.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0)); // Add top margin for spacing
        
        // Result Records Card
        resultRecordsCard = new FacultyFlashcard(
            "Result Records", 
            "Access and manage student grades. CGPA calculations and academic performance. Complete result management system.", 
            "📊", 
            new Color(155, 89, 182)
        );
        resultRecordsCard.setClickListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showResultRecords();
            }
        });
        
        bottomRow.add(resultRecordsCard);
        
        // Add rows to flashcard panel with increased spacing
        JPanel centerPanel = new JPanel(new BorderLayout(50, 0)); // Increased spacing between rows
        centerPanel.setOpaque(false);
        centerPanel.add(topRow, BorderLayout.NORTH);
        centerPanel.add(bottomRow, BorderLayout.CENTER);
        
        flashcardPanel.add(centerPanel, BorderLayout.CENTER);
    }
    
    private void showLoginPanel() {
        removeAll();
        add(loginPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
    
    private void showDashboardPanel() {
        removeAll();
        add(dashboardPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
    
    private void performLogin(String username, char[] password) {
        if (username == null || username.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a username.", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (password == null || password.length == 0) {
            JOptionPane.showMessageDialog(this, "Please enter a password.", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Authenticate
        if (authService.authenticate(username.trim(), password)) {
            isAuthenticated = true;
            currentUsername = username.trim();
            JOptionPane.showMessageDialog(this, "Login successful! Welcome, " + currentUsername + ".", "Success", JOptionPane.INFORMATION_MESSAGE);
            showDashboardPanel();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password. Please try again.", "Login Error", JOptionPane.ERROR_MESSAGE);
        }
        
        // Clear password for security
        java.util.Arrays.fill(password, '0');
    }
    
    private void showLogoutConfirmation() {
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Logout Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            logout();
        }
    }
    
    private void logout() {
        isAuthenticated = false;
        currentUsername = "";
        showLoginPanel();
    }
    
    // Flashcard action methods (to be implemented later)
    private void showStudentRecords() {
        // Create a new panel for student records with proper black background
        JPanel studentRecordsPanel = new JPanel(new BorderLayout());
        studentRecordsPanel.setOpaque(true); // Make it opaque to show background
        studentRecordsPanel.setBackground(new Color(0, 0, 0, 180)); // More opaque black background

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(true); // Make header opaque
        headerPanel.setBackground(new Color(0, 0, 0, 200)); // Darker header background
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Student Records Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);

        JButton backButton = new JButton("← Back to Dashboard");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backButton.setPreferredSize(new Dimension(150, 35));
        backButton.setBackground(new Color(70, 130, 180));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        // Logout button for sub-menu
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        logoutButton.setPreferredSize(new Dimension(100, 35));
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        logoutButton.addActionListener(e -> showLogoutConfirmation());

        backButton.addActionListener(e -> {
            // Properly restore the dashboard
            removeAll();
            add(dashboardPanel, BorderLayout.CENTER);
            revalidate();
            repaint();
        });

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);

        // Main content panel with black background
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setOpaque(true); // Make content panel opaque
        contentPanel.setBackground(new Color(0, 0, 0, 160)); // Black background for content
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        // Top controls panel
        JPanel controlsPanel = createStudentControlsPanel();
        
        // Student table panel
        JPanel tablePanel = createStudentTablePanel();

        contentPanel.add(controlsPanel, BorderLayout.NORTH);
        contentPanel.add(tablePanel, BorderLayout.CENTER);

        studentRecordsPanel.add(headerPanel, BorderLayout.NORTH);
        studentRecordsPanel.add(contentPanel, BorderLayout.CENTER);

        // Replace current content
        removeAll();
        add(studentRecordsPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private JPanel createStudentControlsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setOpaque(true); // Make controls panel opaque
        panel.setBackground(new Color(0, 0, 0, 140)); // Dark background for controls

        // Add Student button
        JButton addButton = new JButton("➕ Add New Student");
        addButton.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14)); // Use emoji font
        addButton.setPreferredSize(new Dimension(180, 40));
        addButton.setBackground(new Color(40, 167, 69));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Search field with real-time search
        JTextField searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(300, 35));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        addButton.addActionListener(e -> showAddStudentDialog());

        // Add real-time search functionality
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { performSearch(searchField.getText()); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { performSearch(searchField.getText()); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { performSearch(searchField.getText()); }
        });

        panel.add(addButton);
        panel.add(new JLabel("Search:", SwingConstants.CENTER) {{
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
        }});
        panel.add(searchField);

        return panel;
    }

    private void performSearch(String searchText) {
        try {
            // Clear current table data
            studentTableModel.setRowCount(0);
            
            if (searchText == null || searchText.trim().isEmpty()) {
                // If search is empty, show all students
                refreshStudentTable();
                return;
            }
            
            // Load all students from database
            List<Student> allStudents = studentDao.findAll();
            String searchLower = searchText.toLowerCase().trim();
            
            // Filter students based on search text
            for (Student student : allStudents) {
                // Check if any field contains the search text
                if (student.getRollNumber().toLowerCase().contains(searchLower) ||
                    student.getName().toLowerCase().contains(searchLower) ||
                    student.getDepartment().toLowerCase().contains(searchLower) ||
                    student.getSection().toLowerCase().contains(searchLower) ||
                    student.getEmail().toLowerCase().contains(searchLower) ||
                    String.valueOf(student.getBatch()).contains(searchLower)) {
                    
                    // Add matching student to table
                    Vector<Object> row = new Vector<>();
                    row.add(student.getRollNumber());
                    row.add(student.getName());
                    row.add(student.getDepartment());
                    row.add(student.getSection());
                    row.add(student.getEmail());
                    row.add(student.getBatch());
                    row.add(""); // Actions column
                    studentTableModel.addRow(row);
                }
            }
            
            // Show search results count
            int resultCount = studentTableModel.getRowCount();
            if (resultCount == 0) {
                System.out.println("No students found matching: " + searchText);
            } else {
                System.out.println("Found " + resultCount + " students matching: " + searchText);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error during search: " + e.getMessage());
        }
    }

    private void refreshStudentTable() {
        try {
            // Clear existing data
            studentTableModel.setRowCount(0);
            
            // Load data from database
            List<Student> students = studentDao.findAll();
            
            for (Student student : students) {
                Vector<Object> row = new Vector<>();
                row.add(student.getRollNumber());
                row.add(student.getName());
                row.add(student.getDepartment());
                row.add(student.getSection());
                row.add(student.getEmail());
                row.add(student.getBatch());
                row.add(""); // Actions column - will be rendered by custom renderer
                
                studentTableModel.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading students: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createStudentTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0, 0, 0, 100)); // Semi-transparent black for better readability
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE, 2, true),
            "Student List (Double-click to view subjects)",
            TitledBorder.CENTER,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            Color.WHITE
        ));

        // Create table model with database data
        String[] columnNames = {"Roll Number", "Name", "Department", "Section", "Email", "Batch", "Actions"};
        studentTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only Actions column is editable
            }
        };

        studentTable = new JTable(studentTableModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.setRowHeight(30);
        studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        studentTable.setGridColor(new Color(200, 200, 200));
        studentTable.setShowGrid(true);
        studentTable.setBackground(Color.WHITE);
        studentTable.setForeground(Color.BLACK);
        studentTable.setOpaque(true);
        
        // Set table size to prevent page-level scrollbar
        studentTable.setPreferredScrollableViewportSize(new Dimension(800, 200));

        // Add mouse listener for row clicks to show subjects
        studentTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) { // Double click
                    int row = studentTable.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        String studentRoll = (String) studentTable.getValueAt(row, 0); // Roll Number column
                        showSubjectRecordsForStudent(studentRoll);
                    }
                }
            }
        });

        // Set custom renderer for Actions column
        studentTable.getColumnModel().getColumn(6).setCellRenderer(new javax.swing.table.TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
                actionPanel.setOpaque(false);

                JButton editBtn = new JButton("✏️");
                editBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
                editBtn.setPreferredSize(new Dimension(30, 25));
                editBtn.setBackground(new Color(255, 193, 7));
                editBtn.setForeground(Color.BLACK);
                editBtn.setFocusPainted(false);
                editBtn.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));

                JButton deleteBtn = new JButton("🗑️");
                deleteBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
                deleteBtn.setPreferredSize(new Dimension(30, 25));
                deleteBtn.setBackground(new Color(220, 53, 69));
                deleteBtn.setForeground(Color.WHITE);
                deleteBtn.setFocusPainted(false);
                deleteBtn.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));

                final int rowIndex = row;
                editBtn.addActionListener(e -> editStudent(rowIndex));
                deleteBtn.addActionListener(e -> deleteStudent(rowIndex));

                actionPanel.add(editBtn);
                actionPanel.add(deleteBtn);

                return actionPanel;
            }
        });

        // Set custom editor for Actions column
        studentTable.getColumnModel().getColumn(6).setCellEditor(new javax.swing.DefaultCellEditor(new JTextField()) {
            {
                setClickCountToStart(0);
            }
            
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
                actionPanel.setOpaque(false);

                JButton editBtn = new JButton("✏️");
                editBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
                editBtn.setPreferredSize(new Dimension(30, 25));
                editBtn.setBackground(new Color(255, 193, 7));
                editBtn.setForeground(Color.BLACK);
                editBtn.setFocusPainted(false);
                editBtn.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));

                JButton deleteBtn = new JButton("🗑️");
                deleteBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
                deleteBtn.setPreferredSize(new Dimension(30, 25));
                deleteBtn.setBackground(new Color(220, 53, 69));
                deleteBtn.setForeground(Color.WHITE);
                deleteBtn.setFocusPainted(false);
                deleteBtn.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));

                final int rowIndex = row;
                editBtn.addActionListener(e -> editStudent(rowIndex));
                deleteBtn.addActionListener(e -> deleteStudent(rowIndex));

                actionPanel.add(editBtn);
                actionPanel.add(deleteBtn);

                return actionPanel;
            }
        });

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setOpaque(true);
        scrollPane.getViewport().setOpaque(true);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.setPreferredSize(new Dimension(800, 200));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Load initial data from database
        refreshStudentTable();
        
        return panel;
    }

    private void showAddStudentDialog() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Add New Student", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(new Color(0, 0, 0, 200));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Add New Student", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);

        JButton saveButton = new JButton("Save Student");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveButton.setPreferredSize(new Dimension(120, 40));
        saveButton.setBackground(new Color(40, 167, 69));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cancelButton.setPreferredSize(new Dimension(100, 40));
        cancelButton.setBackground(new Color(108, 117, 125));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);

        // Form panel - now we can pass the save button
        JPanel formPanel = createStudentFormPanel(dialog, saveButton);

        saveButton.addActionListener(e -> {
            try {
                // Get form data from the form panel
                JPanel formPanelRef = (JPanel) mainPanel.getComponent(1); // Get the form panel
                
                // Extract form data (this is a simplified approach - in production you'd want better form handling)
                JTextField rollNumberField = (JTextField) formPanelRef.getComponent(1);
                JTextField nameField = (JTextField) formPanelRef.getComponent(3);
                JComboBox<String> deptComboBox = (JComboBox<String>) formPanelRef.getComponent(5);
                JComboBox<String> sectionComboBox = (JComboBox<String>) formPanelRef.getComponent(7);
                JTextField emailField = (JTextField) formPanelRef.getComponent(9);
                JComboBox<String> batchComboBox = (JComboBox<String>) formPanelRef.getComponent(11);
                
                // Create student object
                Student student = new Student();
                student.setRollNumber(rollNumberField.getText().trim());
                student.setName(nameField.getText().trim());
                student.setDepartment((String) deptComboBox.getSelectedItem());
                student.setSection((String) sectionComboBox.getSelectedItem());
                student.setEmail(emailField.getText().trim());
                student.setBatch(Integer.parseInt((String) batchComboBox.getSelectedItem()));

                // Validate fields
                if (student.getRollNumber().isEmpty() || student.getName().isEmpty() || student.getEmail().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill all required fields!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Save to database
                studentDao.insert(student);
                
                // Show success message
                JOptionPane.showMessageDialog(dialog, "Student saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Refresh table to show new data
                refreshStudentTable();
                
                // Close dialog
                dialog.dispose();
                
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error saving student: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private JPanel createStudentFormPanel(JDialog dialog, JButton saveButton) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Form fields
        JTextField rollNumberField = new JTextField(20);
        JTextField nameField = new JTextField(20);
        JComboBox<String> deptComboBox = new JComboBox<>(new String[]{"CTECH", "CINTEL", "DSBS"});
        JComboBox<String> sectionComboBox = new JComboBox<>(new String[]{"A1", "A2", "B1", "B2", "C1", "C2", "D1", "D2", "E1", "E2", 
                                         "F1", "F2", "G1", "G2", "H1", "H2", "I1", "I2", "J1", "J2",
                                         "K1", "K2", "L1", "L2", "M1", "M2", "N1", "N2", "O1", "O2",
                                         "P1", "P2", "Q1", "Q2", "R1", "R2", "S1", "S2", "T1", "T2"});
        JTextField emailField = new JTextField(20);
        JComboBox<String> batchComboBox = new JComboBox<>(new String[]{"2023", "2024", "2025"});

        // Set field properties
        rollNumberField.setPreferredSize(new Dimension(250, 35));
        rollNumberField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameField.setPreferredSize(new Dimension(250, 35));
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        deptComboBox.setPreferredSize(new Dimension(250, 35));
        deptComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sectionComboBox.setPreferredSize(new Dimension(250, 35));
        sectionComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailField.setPreferredSize(new Dimension(250, 35));
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        batchComboBox.setPreferredSize(new Dimension(250, 35));
        batchComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Add components to panel
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Roll Number:", SwingConstants.CENTER) {{
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
        }}, gbc);
        gbc.gridx = 1;
        panel.add(rollNumberField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Name:", SwingConstants.CENTER) {{
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
        }}, gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Department:", SwingConstants.CENTER) {{
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
        }}, gbc);
        gbc.gridx = 1;
        panel.add(deptComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Section:", SwingConstants.CENTER) {{
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
        }}, gbc);
        gbc.gridx = 1;
        panel.add(sectionComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Email:", SwingConstants.CENTER) {{
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
        }}, gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Batch:", SwingConstants.CENTER) {{
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
        }}, gbc);
        gbc.gridx = 1;
        panel.add(batchComboBox, gbc);

        // Add save button functionality
        // Note: Action listener is set in the calling method
        // saveButton.addActionListener(...) is handled externally

        return panel;
    }

    private void editStudent(int rowIndex) {
        try {
            // Get student data from the selected row
            String rollNumber = (String) studentTable.getValueAt(rowIndex, 0);
            String name = (String) studentTable.getValueAt(rowIndex, 1);
            String department = (String) studentTable.getValueAt(rowIndex, 2);
            String section = (String) studentTable.getValueAt(rowIndex, 3);
            String email = (String) studentTable.getValueAt(rowIndex, 4);
            Integer batchInt = (Integer) studentTable.getValueAt(rowIndex, 5);
            String batch = batchInt.toString(); // Convert Integer to String

            // Create edit dialog
            JDialog editDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Edit Student", Dialog.ModalityType.APPLICATION_MODAL);
            editDialog.setLayout(new BorderLayout());
            editDialog.setSize(500, 600);
            editDialog.setLocationRelativeTo(this);
            editDialog.setResizable(false);

            JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
            mainPanel.setBackground(new Color(0, 0, 0, 200));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            // Title
            JLabel titleLabel = new JLabel("Edit Student", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            titleLabel.setForeground(Color.WHITE);

            // Buttons panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
            buttonPanel.setOpaque(false);

            JButton updateButton = new JButton("Update Student");
            updateButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            updateButton.setPreferredSize(new Dimension(140, 40));
            updateButton.setBackground(new Color(40, 167, 69));
            updateButton.setForeground(Color.WHITE);
            updateButton.setFocusPainted(false);

            JButton cancelButton = new JButton("Cancel");
            cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            cancelButton.setPreferredSize(new Dimension(100, 40));
            cancelButton.setBackground(new Color(108, 117, 125));
            cancelButton.setForeground(Color.WHITE);
            cancelButton.setFocusPainted(false);

            // Form panel with pre-filled data - now we can pass the update button
            JPanel formPanel = createEditStudentFormPanel(editDialog, rollNumber, name, department, section, email, batch, updateButton);

            // Add action listeners
            updateButton.addActionListener(e -> {
                try {
                    // Get form data from the form panel
                    JPanel formPanelRef = (JPanel) mainPanel.getComponent(1); // Get the form panel
                    
                    // Extract form data (this is a simplified approach - in production you'd want better form handling)
                    JTextField rollNumberField = (JTextField) formPanelRef.getComponent(1);
                    JTextField nameField = (JTextField) formPanelRef.getComponent(3);
                    JComboBox<String> deptComboBox = (JComboBox<String>) formPanelRef.getComponent(5);
                    JComboBox<String> sectionComboBox = (JComboBox<String>) formPanelRef.getComponent(7);
                    JTextField emailField = (JTextField) formPanelRef.getComponent(9);
                    JComboBox<String> batchComboBox = (JComboBox<String>) formPanelRef.getComponent(11);
                    
                    // Create updated student object
                    Student student = new Student();
                    student.setRollNumber(rollNumberField.getText().trim());
                    student.setName(nameField.getText().trim());
                    student.setDepartment((String) deptComboBox.getSelectedItem());
                    student.setSection((String) sectionComboBox.getSelectedItem());
                    student.setEmail(emailField.getText().trim());
                    student.setBatch(Integer.parseInt((String) batchComboBox.getSelectedItem()));

                    // Validate fields
                    if (student.getRollNumber().isEmpty() || student.getName().isEmpty() || student.getEmail().isEmpty()) {
                        JOptionPane.showMessageDialog(editDialog, "Please fill all required fields!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Update in database
                    studentDao.update(student);
                    
                    // Show success message
                    JOptionPane.showMessageDialog(editDialog, "Student updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Refresh table to show updated data
                    refreshStudentTable();
                    
                    // Close dialog
                    editDialog.dispose();
                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(editDialog, "Error updating student: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            cancelButton.addActionListener(e -> editDialog.dispose());

            buttonPanel.add(updateButton);
            buttonPanel.add(cancelButton);

            mainPanel.add(titleLabel, BorderLayout.NORTH);
            mainPanel.add(formPanel, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            editDialog.add(mainPanel);
            editDialog.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error editing student: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createEditStudentFormPanel(JDialog dialog, String rollNumber, String name, String department, String section, String email, String batch, JButton updateButton) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Form fields with pre-filled data
        JTextField rollNumberField = new JTextField(rollNumber, 20);
        JTextField nameField = new JTextField(name, 20);
        JComboBox<String> deptComboBox = new JComboBox<>(new String[]{"CTECH", "CINTEL", "DSBS"});
        deptComboBox.setSelectedItem(department);
        JComboBox<String> sectionComboBox = new JComboBox<>(new String[]{"A1", "A2", "B1", "B2", "C1", "C2", "D1", "D2", "E1", "E2", 
                                         "F1", "F2", "G1", "G2", "H1", "H2", "I1", "I2", "J1", "J2",
                                         "K1", "K2", "L1", "L2", "M1", "M2", "N1", "N2", "O1", "O2",
                                         "P1", "P2", "Q1", "Q2", "R1", "R2", "S1", "S2", "T1", "T2"});
        sectionComboBox.setSelectedItem(section);
        JTextField emailField = new JTextField(email, 20);
        JComboBox<String> batchComboBox = new JComboBox<>(new String[]{"2023", "2024", "2025"});
        batchComboBox.setSelectedItem(batch);

        // Set field properties
        rollNumberField.setPreferredSize(new Dimension(250, 35));
        rollNumberField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameField.setPreferredSize(new Dimension(250, 35));
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        deptComboBox.setPreferredSize(new Dimension(250, 35));
        deptComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sectionComboBox.setPreferredSize(new Dimension(250, 35));
        sectionComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailField.setPreferredSize(new Dimension(250, 35));
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        batchComboBox.setPreferredSize(new Dimension(250, 35));
        batchComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Add components to panel
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Roll Number:", SwingConstants.CENTER) {{
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
        }}, gbc);
        gbc.gridx = 1;
        panel.add(rollNumberField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Name:", SwingConstants.CENTER) {{
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
        }}, gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Department:", SwingConstants.CENTER) {{
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
        }}, gbc);
        gbc.gridx = 1;
        panel.add(deptComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Section:", SwingConstants.CENTER) {{
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
        }}, gbc);
        gbc.gridx = 1;
        panel.add(sectionComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Email:", SwingConstants.CENTER) {{
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
        }}, gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Batch:", SwingConstants.CENTER) {{
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
        }}, gbc);
        gbc.gridx = 1;
        panel.add(batchComboBox, gbc);

        // Add update button functionality
        // Note: Action listener is set in the calling method
        // updateButton.addActionListener(...) is handled externally

        return panel;
    }

    private void deleteStudent(int rowIndex) {
        try {
            // Get student roll number from the selected row
            String rollNumber = (String) studentTable.getValueAt(rowIndex, 0);
            String name = (String) studentTable.getValueAt(rowIndex, 1);

            int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete student '" + name + "' (Roll: " + rollNumber + ")?", 
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (result == JOptionPane.YES_OPTION) {
                // Delete from database
                studentDao.delete(rollNumber);
                
                JOptionPane.showMessageDialog(this, 
                    "Student '" + name + "' deleted successfully!", 
                    "Delete Student", JOptionPane.INFORMATION_MESSAGE);
                
                // Refresh table to show updated data
                refreshStudentTable();
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting student: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showSubjectRecords() {
        showSubjectRecordsForStudent(null);
    }
    
    private void showSubjectRecordsForStudent(String studentRoll) {
        // Set filtering state
        if (studentRoll != null && !studentRoll.isEmpty()) {
            isFilteredByStudent = true;
            currentFilteredStudentRoll = studentRoll;
        } else {
            isFilteredByStudent = false;
            currentFilteredStudentRoll = "";
        }
        
        // Create a new panel for subject records with proper black background
        JPanel subjectRecordsPanel = new JPanel(new BorderLayout());
        subjectRecordsPanel.setOpaque(true); // Make it opaque to show background
        subjectRecordsPanel.setBackground(new Color(0, 0, 0, 180)); // More opaque black background

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(true); // Make header opaque
        headerPanel.setBackground(new Color(0, 0, 0, 200)); // Darker header background
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel;
        if (isFilteredByStudent && !currentFilteredStudentRoll.isEmpty()) {
            String studentName = getStudentNameByRoll(currentFilteredStudentRoll);
            titleLabel = new JLabel("Subject Records - " + studentName, SwingConstants.CENTER);
        } else {
            titleLabel = new JLabel("Subject Records Management", SwingConstants.CENTER);
        }
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);

        // Back button - either to dashboard or to student list
        JButton backButton;
        if (isFilteredByStudent) {
            backButton = new JButton("← Back to Student List");
            backButton.addActionListener(e -> {
                // Go back to student list
                isFilteredByStudent = false;
                currentFilteredStudentRoll = "";
                showStudentRecords();
            });
        } else {
            backButton = new JButton("← Back to Dashboard");
            backButton.addActionListener(e -> {
                // Properly restore the dashboard
                removeAll();
                add(dashboardPanel, BorderLayout.CENTER);
                revalidate();
                repaint();
            });
        }
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backButton.setPreferredSize(new Dimension(150, 35));
        backButton.setBackground(new Color(70, 130, 180));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        // Logout button for sub-menu
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        logoutButton.setPreferredSize(new Dimension(100, 35));
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        logoutButton.addActionListener(e -> showLogoutConfirmation());

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);

        // Main content panel with black background
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setOpaque(true); // Make content panel opaque
        contentPanel.setBackground(new Color(0, 0, 0, 160)); // Black background for content
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        // Top controls panel
        JPanel controlsPanel = createSubjectControlsPanel();
        
        // Subject table panel
        JPanel tablePanel = createSubjectTablePanel();

        contentPanel.add(controlsPanel, BorderLayout.NORTH);
        contentPanel.add(tablePanel, BorderLayout.CENTER);

        subjectRecordsPanel.add(headerPanel, BorderLayout.NORTH);
        subjectRecordsPanel.add(contentPanel, BorderLayout.CENTER);

        // Replace current content
        removeAll();
        add(subjectRecordsPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private JPanel createSubjectControlsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setOpaque(true); // Make controls panel opaque
        panel.setBackground(new Color(0, 0, 0, 140)); // Dark background for controls

        // Add Subject button
        JButton addButton = new JButton("➕ Add New Subject");
        addButton.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14)); // Use emoji font
        addButton.setPreferredSize(new Dimension(180, 40));
        addButton.setBackground(new Color(40, 167, 69));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Search field with real-time search
        JTextField searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(300, 35));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        // Semester filter dropdown
        JLabel semesterLabel = new JLabel("Semester:", SwingConstants.CENTER);
        semesterLabel.setForeground(Color.WHITE);
        semesterLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JComboBox<String> semesterFilter = new JComboBox<>(new String[]{"All Semesters", "1", "2", "3", "4", "5", "6", "7", "8"});
        semesterFilter.setPreferredSize(new Dimension(150, 35));
        semesterFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        semesterFilter.setBackground(Color.WHITE);
        semesterFilter.setForeground(Color.BLACK);

        addButton.addActionListener(e -> showAddSubjectDialog());

        // Add real-time search functionality
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { performSearchSubject(searchField.getText()); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { performSearchSubject(searchField.getText()); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { performSearchSubject(searchField.getText()); }
        });

        // Add semester filter functionality
        semesterFilter.addActionListener(e -> {
            String selectedSemester = (String) semesterFilter.getSelectedItem();
            if ("All Semesters".equals(selectedSemester)) {
                performSemesterFilter(null);
            } else {
                performSemesterFilter(Integer.parseInt(selectedSemester));
            }
        });

        panel.add(addButton);
        panel.add(new JLabel("Search:", SwingConstants.CENTER) {{
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
        }});
        panel.add(searchField);
        panel.add(semesterLabel);
        panel.add(semesterFilter);

        return panel;
    }

    private void performSearchSubject(String searchText) {
        try {
            // Clear current table data
            subjectTableModel.setRowCount(0);
            
            if (searchText == null || searchText.trim().isEmpty()) {
                // If search is empty, show all subjects
                refreshSubjectTable();
                return;
            }
            
            // Load all subjects from database
            List<Subject> allSubjects = subjectDao.findAll();
            String searchLower = searchText.toLowerCase().trim();
            
            // Apply semester filter if set
            if (currentSemesterFilter != null) {
                allSubjects = allSubjects.stream()
                    .filter(s -> s.getSemester() == currentSemesterFilter)
                    .collect(java.util.stream.Collectors.toList());
            }
            
            // Filter subjects based on search text
            List<Subject> matchingSubjects = new ArrayList<>();
            for (Subject subject : allSubjects) {
                // Check if any field contains the search text
                if (subject.getSubjectCode().toLowerCase().contains(searchLower) ||
                    subject.getSubjectName().toLowerCase().contains(searchLower) ||
                    subject.getStudentRoll().toLowerCase().contains(searchLower) ||
                    String.valueOf(subject.getCredits()).contains(searchLower) ||
                    String.valueOf(subject.getSemester()).contains(searchLower) ||
                    subject.getGrade().toLowerCase().contains(searchLower)) {
                    
                    matchingSubjects.add(subject);
                }
            }
            
            // Group matching subjects by student roll number and sort by semester within each group
            Map<String, List<Subject>> subjectsByStudent = matchingSubjects.stream()
                .collect(java.util.stream.Collectors.groupingBy(Subject::getStudentRoll));
            
            // Sort students by roll number for consistent grouping
            List<String> sortedStudentRolls = new ArrayList<>(subjectsByStudent.keySet());
            sortedStudentRolls.sort(String::compareTo);
            
            for (String studentRoll : sortedStudentRolls) {
                List<Subject> studentSubjects = subjectsByStudent.get(studentRoll);
                
                // Sort subjects by semester in increasing order
                studentSubjects.sort((s1, s2) -> Integer.compare(s1.getSemester(), s2.getSemester()));
                
                for (Subject subject : studentSubjects) {
                    // Get student name from roll number
                    String studentName = getStudentNameByRoll(subject.getStudentRoll());
                    
                    Vector<Object> row = new Vector<>();
                    row.add(studentName); // Student Name
                    row.add(subject.getSubjectName());
                    row.add(subject.getSubjectCode());
                    row.add(subject.getCredits());
                    row.add(subject.getGrade());
                    row.add(subject.getSemester());
                    row.add(""); // Actions column - will be rendered by custom renderer
                    
                    subjectTableModel.addRow(row);
                }
            }
            
            // Show search results count
            int resultCount = subjectTableModel.getRowCount();
            if (resultCount == 0) {
                System.out.println("No subjects found matching: " + searchText);
            } else {
                System.out.println("Found " + resultCount + " subjects matching: " + searchText);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error during search: " + e.getMessage());
        }
    }

    private void refreshSubjectTable() {
        try {
            // Clear existing data
            subjectTableModel.setRowCount(0);
            
            // Load data from database
            List<Subject> subjects;
            if (isFilteredByStudent && !currentFilteredStudentRoll.isEmpty()) {
                // Filter by specific student
                subjects = subjectDao.findByStudentRoll(currentFilteredStudentRoll);
            } else {
                // Load all subjects
                subjects = subjectDao.findAll();
            }
            
            // Apply semester filter if set
            if (currentSemesterFilter != null) {
                subjects = subjects.stream()
                    .filter(s -> s.getSemester() == currentSemesterFilter)
                    .collect(java.util.stream.Collectors.toList());
            }
            
            // Group subjects by student roll number and sort by semester within each group
            Map<String, List<Subject>> subjectsByStudent = subjects.stream()
                .collect(java.util.stream.Collectors.groupingBy(Subject::getStudentRoll));
            
            // Sort students by roll number for consistent grouping
            List<String> sortedStudentRolls = new ArrayList<>(subjectsByStudent.keySet());
            sortedStudentRolls.sort(String::compareTo);
            
            for (String studentRoll : sortedStudentRolls) {
                List<Subject> studentSubjects = subjectsByStudent.get(studentRoll);
                
                // Sort subjects by semester in increasing order
                studentSubjects.sort((s1, s2) -> Integer.compare(s1.getSemester(), s2.getSemester()));
                
                for (Subject subject : studentSubjects) {
                    // Get student name from roll number
                    String studentName = getStudentNameByRoll(subject.getStudentRoll());
                    
                    Vector<Object> row = new Vector<>();
                    row.add(studentName); // Student Name
                    row.add(subject.getSubjectName());
                    row.add(subject.getSubjectCode());
                    row.add(subject.getCredits());
                    row.add(subject.getGrade());
                    row.add(subject.getSemester());
                    row.add(""); // Actions column - will be rendered by custom renderer
                    
                    subjectTableModel.addRow(row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading subjects: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createSubjectTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0, 0, 0, 100)); // Semi-transparent black for better readability
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE, 2, true),
            "Subject List",
            TitledBorder.CENTER,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            Color.WHITE
        ));

        // Create table model with database data
        String[] columnNames = {"Student Name", "Subject Name", "Subject Code", "Credits", "Grade", "Semester", "Actions"};
        subjectTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only Actions column is editable (now column 6)
            }
        };

        subjectTable = new JTable(subjectTableModel);
        subjectTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        subjectTable.setRowHeight(30);
        subjectTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subjectTable.setGridColor(new Color(200, 200, 200));
        subjectTable.setShowGrid(true);
        subjectTable.setBackground(Color.WHITE);
        subjectTable.setForeground(Color.BLACK);
        subjectTable.setOpaque(true);
        
        // Set table size to prevent page-level scrollbar
        subjectTable.setPreferredScrollableViewportSize(new Dimension(800, 200));

        // Set custom renderer for Actions column
        subjectTable.getColumnModel().getColumn(6).setCellRenderer(new javax.swing.table.TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
                actionPanel.setOpaque(false);

                JButton editBtn = new JButton("✏️");
                editBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
                editBtn.setPreferredSize(new Dimension(30, 25));
                editBtn.setBackground(new Color(255, 193, 7));
                editBtn.setForeground(Color.BLACK);
                editBtn.setFocusPainted(false);
                editBtn.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));

                JButton deleteBtn = new JButton("🗑️");
                deleteBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
                deleteBtn.setPreferredSize(new Dimension(30, 25));
                deleteBtn.setBackground(new Color(220, 53, 69));
                deleteBtn.setForeground(Color.WHITE);
                deleteBtn.setFocusPainted(false);
                deleteBtn.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));

                final int rowIndex = row;
                editBtn.addActionListener(e -> editSubject(rowIndex));
                deleteBtn.addActionListener(e -> deleteSubject(rowIndex));

                actionPanel.add(editBtn);
                actionPanel.add(deleteBtn);

                return actionPanel;
            }
        });

        // Set custom editor for Actions column
        subjectTable.getColumnModel().getColumn(6).setCellEditor(new javax.swing.DefaultCellEditor(new JTextField()) {
            {
                setClickCountToStart(0);
            }
            
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
                actionPanel.setOpaque(false);

                JButton editBtn = new JButton("✏️");
                editBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
                editBtn.setPreferredSize(new Dimension(30, 25));
                editBtn.setBackground(new Color(255, 193, 7));
                editBtn.setForeground(Color.BLACK);
                editBtn.setFocusPainted(false);
                editBtn.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));

                JButton deleteBtn = new JButton("🗑️");
                deleteBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
                deleteBtn.setPreferredSize(new Dimension(30, 25));
                deleteBtn.setBackground(new Color(220, 53, 69));
                deleteBtn.setForeground(Color.WHITE);
                deleteBtn.setFocusPainted(false);
                deleteBtn.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));

                final int rowIndex = row;
                editBtn.addActionListener(e -> editSubject(rowIndex));
                deleteBtn.addActionListener(e -> deleteSubject(rowIndex));

                actionPanel.add(editBtn);
                actionPanel.add(deleteBtn);

                return actionPanel;
            }
        });

        JScrollPane scrollPane = new JScrollPane(subjectTable);
        scrollPane.setOpaque(true);
        scrollPane.getViewport().setOpaque(true);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.setPreferredSize(new Dimension(800, 200));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Load initial data from database
        refreshSubjectTable();
        
        return panel;
    }
    
    private void performSemesterFilter(Integer semester) {
        currentSemesterFilter = semester;
        refreshSubjectTable();
    }
    
    private String getStudentNameByRoll(String rollNumber) {
        try {
            List<Student> students = studentDao.findAll();
            for (Student student : students) {
                if (student.getRollNumber().equals(rollNumber)) {
                    return student.getName();
                }
            }
            return "Unknown Student"; // Fallback if student not found
        } catch (Exception e) {
            e.printStackTrace();
            return "Error Loading Student";
        }
    }
    
    private String getStudentRollByName(String studentName) {
        try {
            List<Student> students = studentDao.findAll();
            for (Student student : students) {
                if (student.getName().equals(studentName)) {
                    return student.getRollNumber();
                }
            }
            return ""; // Fallback if student not found
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private void showAddSubjectDialog() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Add New Subject", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(new Color(0, 0, 0, 200));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Add New Subject", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);

        JButton saveButton = new JButton("Save Subject");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveButton.setPreferredSize(new Dimension(120, 40));
        saveButton.setBackground(new Color(40, 167, 69));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cancelButton.setPreferredSize(new Dimension(100, 40));
        cancelButton.setBackground(new Color(108, 117, 125));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);

        // Form panel
        JPanel formPanel = createSubjectFormPanel(dialog, saveButton);

        saveButton.addActionListener(e -> {
            try {
                // Get form data from the form panel
                JPanel formPanelRef = (JPanel) mainPanel.getComponent(1); // Get the form panel
                
                // Extract form data
                JComboBox<Student> studentCombo = (JComboBox<Student>) formPanelRef.getComponent(1);
                JTextField subjectNameField = (JTextField) formPanelRef.getComponent(3);
                JTextField subjectCodeField = (JTextField) formPanelRef.getComponent(5);
                JComboBox<Integer> creditsCombo = (JComboBox<Integer>) formPanelRef.getComponent(7);
                JComboBox<String> gradeCombo = (JComboBox<String>) formPanelRef.getComponent(9);
                JComboBox<Integer> semesterCombo = (JComboBox<Integer>) formPanelRef.getComponent(11);
                
                // Get selected student
                Student selectedStudent = (Student) studentCombo.getSelectedItem();
                if (selectedStudent == null) {
                    JOptionPane.showMessageDialog(dialog, "Please select a student!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Create subject object
                Subject subject = new Subject();
                subject.setStudentRoll(selectedStudent.getRollNumber());
                subject.setSubjectName(subjectNameField.getText().trim());
                subject.setSubjectCode(subjectCodeField.getText().trim());
                subject.setCredits((Integer) creditsCombo.getSelectedItem());
                subject.setGrade((String) gradeCombo.getSelectedItem());
                subject.setSemester((Integer) semesterCombo.getSelectedItem());

                // Validate fields
                if (subject.getSubjectName().isEmpty() || subject.getSubjectCode().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill all required fields!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Save to database
                subjectDao.insert(subject);
                
                // Show success message
                JOptionPane.showMessageDialog(dialog, "Subject saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Refresh table to show new data
                refreshSubjectTable();
                
                // Close dialog
                dialog.dispose();
                
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error saving subject: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private JPanel createSubjectFormPanel(JDialog dialog, JButton saveButton) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Form fields
        JComboBox<Student> studentCombo = new JComboBox<>();
        JTextField subjectNameField = new JTextField(20);
        JTextField subjectCodeField = new JTextField(20);
        JComboBox<Integer> creditsCombo = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        JComboBox<String> gradeCombo = new JComboBox<>(new String[]{"O", "A+", "A", "B+", "B", "C+", "C", "F"});
        JComboBox<Integer> semesterCombo = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8});

        // Load students into combo box
        try {
            List<Student> students = studentDao.findAll();
            for (Student student : students) {
                studentCombo.addItem(student);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set field properties
        studentCombo.setPreferredSize(new Dimension(250, 35));
        studentCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subjectNameField.setPreferredSize(new Dimension(250, 35));
        subjectNameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subjectCodeField.setPreferredSize(new Dimension(250, 35));
        subjectCodeField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        creditsCombo.setPreferredSize(new Dimension(250, 35));
        creditsCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gradeCombo.setPreferredSize(new Dimension(250, 35));
        gradeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        semesterCombo.setPreferredSize(new Dimension(250, 35));
        semesterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Add components to panel
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Student:", SwingConstants.CENTER) {{
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
        }}, gbc);
        gbc.gridx = 1;
        panel.add(studentCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Subject Name:", SwingConstants.CENTER) {{
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
        }}, gbc);
        gbc.gridx = 1;
        panel.add(subjectNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Subject Code:", SwingConstants.CENTER) {{
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
        }}, gbc);
        gbc.gridx = 1;
        panel.add(subjectCodeField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Credits:", SwingConstants.CENTER) {{
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
        }}, gbc);
        gbc.gridx = 1;
        panel.add(creditsCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Grade:", SwingConstants.CENTER) {{
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
        }}, gbc);
        gbc.gridx = 1;
        panel.add(gradeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Semester:", SwingConstants.CENTER) {{
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
        }}, gbc);
        gbc.gridx = 1;
        panel.add(semesterCombo, gbc);

        return panel;
    }

    private void editSubject(int rowIndex) {
        try {
            // Get subject data from the selected row (new column structure)
            String studentName = (String) subjectTable.getValueAt(rowIndex, 0); // Student Name
            String subjectName = (String) subjectTable.getValueAt(rowIndex, 1); // Subject Name
            String subjectCode = (String) subjectTable.getValueAt(rowIndex, 2); // Subject Code
            Integer credits = (Integer) subjectTable.getValueAt(rowIndex, 3); // Credits
            String grade = (String) subjectTable.getValueAt(rowIndex, 4); // Grade
            Integer semester = (Integer) subjectTable.getValueAt(rowIndex, 5); // Semester
            
            // Get student roll number from student name
            String studentRoll = getStudentRollByName(studentName);

            // Create edit dialog
            JDialog editDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Edit Subject", Dialog.ModalityType.APPLICATION_MODAL);
            editDialog.setLayout(new BorderLayout());
            editDialog.setSize(500, 600);
            editDialog.setLocationRelativeTo(this);
            editDialog.setResizable(false);

            JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
            mainPanel.setBackground(new Color(0, 0, 0, 200));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            // Title
            JLabel titleLabel = new JLabel("Edit Subject", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            titleLabel.setForeground(Color.WHITE);

            // Buttons panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
            buttonPanel.setOpaque(false);

            JButton updateButton = new JButton("Update Subject");
            updateButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            updateButton.setPreferredSize(new Dimension(140, 40));
            updateButton.setBackground(new Color(40, 167, 69));
            updateButton.setForeground(Color.WHITE);
            updateButton.setFocusPainted(false);

            JButton cancelButton = new JButton("Cancel");
            cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            cancelButton.setPreferredSize(new Dimension(100, 40));
            cancelButton.setBackground(new Color(108, 117, 125));
            cancelButton.setForeground(Color.WHITE);
            cancelButton.setFocusPainted(false);

            // Form panel with pre-filled data
            JPanel formPanel = createEditSubjectFormPanel(editDialog, studentRoll, subjectName, subjectCode, credits, grade, semester, updateButton);

            // Add action listeners
            updateButton.addActionListener(e -> {
                try {
                    // Get form data from the form panel
                    JPanel formPanelRef = (JPanel) mainPanel.getComponent(1); // Get the form panel
                    
                    // Extract form data
                    JComboBox<Student> studentCombo = (JComboBox<Student>) formPanelRef.getComponent(1);
                    JTextField subjectNameField = (JTextField) formPanelRef.getComponent(3);
                    JTextField subjectCodeField = (JTextField) formPanelRef.getComponent(5);
                    JComboBox<Integer> creditsCombo = (JComboBox<Integer>) formPanelRef.getComponent(7);
                    JComboBox<String> gradeCombo = (JComboBox<String>) formPanelRef.getComponent(9);
                    JComboBox<Integer> semesterCombo = (JComboBox<Integer>) formPanelRef.getComponent(11);
                    
                    // Get selected student
                    Student selectedStudent = (Student) studentCombo.getSelectedItem();
                    if (selectedStudent == null) {
                        JOptionPane.showMessageDialog(editDialog, "Please select a student!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                                         // Create updated subject object
                     Subject subject = new Subject();
                     subject.setStudentRoll(selectedStudent.getRollNumber());
                    subject.setSubjectName(subjectNameField.getText().trim());
                    subject.setSubjectCode(subjectCodeField.getText().trim());
                    subject.setCredits((Integer) creditsCombo.getSelectedItem());
                    subject.setGrade((String) gradeCombo.getSelectedItem());
                    subject.setSemester((Integer) semesterCombo.getSelectedItem());

                    // Validate fields
                    if (subject.getSubjectName().isEmpty() || subject.getSubjectCode().isEmpty()) {
                        JOptionPane.showMessageDialog(editDialog, "Please fill all required fields!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Update in database
                    subjectDao.update(subject);
                    
                    // Show success message
                    JOptionPane.showMessageDialog(editDialog, "Subject updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Refresh table to show updated data
                    refreshSubjectTable();
                    
                    // Close dialog
                    editDialog.dispose();
                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(editDialog, "Error updating subject: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            cancelButton.addActionListener(e -> editDialog.dispose());

            buttonPanel.add(updateButton);
            buttonPanel.add(cancelButton);

            mainPanel.add(titleLabel, BorderLayout.NORTH);
            mainPanel.add(formPanel, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            editDialog.add(mainPanel);
            editDialog.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error editing subject: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createEditSubjectFormPanel(JDialog dialog, String studentRoll, String subjectName, String subjectCode, Integer credits, String grade, Integer semester, JButton updateButton) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Form fields with pre-filled data
        JComboBox<Student> studentCombo = new JComboBox<>();
        JTextField subjectNameField = new JTextField(subjectName, 20);
        JTextField subjectCodeField = new JTextField(subjectCode, 20);
        JComboBox<Integer> creditsCombo = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        JComboBox<String> gradeCombo = new JComboBox<>(new String[]{"O", "A+", "A", "B+", "B", "C+", "C", "F"});
        JComboBox<Integer> semesterCombo = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8});

        // Load students into combo box and select the current one
        try {
            List<Student> students = studentDao.findAll();
            for (Student student : students) {
                studentCombo.addItem(student);
                if (student.getRollNumber().equals(studentRoll)) {
                    studentCombo.setSelectedItem(student);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set selected values
        creditsCombo.setSelectedItem(credits);
        gradeCombo.setSelectedItem(grade);
        semesterCombo.setSelectedItem(semester);

        // Set field properties
        studentCombo.setPreferredSize(new Dimension(250, 35));
        studentCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subjectNameField.setPreferredSize(new Dimension(250, 35));
        subjectNameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subjectCodeField.setPreferredSize(new Dimension(250, 35));
        subjectCodeField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        creditsCombo.setPreferredSize(new Dimension(250, 35));
        creditsCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gradeCombo.setPreferredSize(new Dimension(250, 35));
        gradeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        semesterCombo.setPreferredSize(new Dimension(250, 35));
        semesterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Add components to panel
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Student:", SwingConstants.CENTER) {{
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
        }}, gbc);
        gbc.gridx = 1;
        panel.add(studentCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Subject Name:", SwingConstants.CENTER) {{
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
        }}, gbc);
        gbc.gridx = 1;
        panel.add(subjectNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Subject Code:", SwingConstants.CENTER) {{
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
        }}, gbc);
        gbc.gridx = 1;
        panel.add(subjectCodeField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Credits:", SwingConstants.CENTER) {{
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
        }}, gbc);
        gbc.gridx = 1;
        panel.add(creditsCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Grade:", SwingConstants.CENTER) {{
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
        }}, gbc);
        gbc.gridx = 1;
        panel.add(gradeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Semester:", SwingConstants.CENTER) {{
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
        }}, gbc);
        gbc.gridx = 1;
        panel.add(semesterCombo, gbc);

        return panel;
    }

    private void deleteSubject(int rowIndex) {
        try {
            // Get subject data from the selected row (new column structure)
            String studentName = (String) subjectTable.getValueAt(rowIndex, 0); // Student Name
            String subjectName = (String) subjectTable.getValueAt(rowIndex, 1); // Subject Name
            String subjectCode = (String) subjectTable.getValueAt(rowIndex, 2); // Subject Code
            
            // Get student roll number from student name
            String studentRoll = getStudentRollByName(studentName);

            int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete subject '" + subjectName + "'?", 
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (result == JOptionPane.YES_OPTION) {
                // Find and delete the subject from database
                try {
                    List<Subject> subjects = subjectDao.findByStudentRoll(studentRoll);
                    for (Subject subject : subjects) {
                        if (subject.getSubjectName().equals(subjectName) && 
                            subject.getSubjectCode().equals(subjectCode)) {
                            subjectDao.delete(subject.getId());
                            break;
                        }
                    }
                    
                    JOptionPane.showMessageDialog(this, 
                        "Subject '" + subjectName + "' deleted successfully!", 
                        "Delete Subject", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Refresh table to show updated data
                    refreshSubjectTable();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error deleting subject: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting subject: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showResultRecords() {
        JOptionPane.showMessageDialog(this, "Result Records functionality coming soon!", "Info", JOptionPane.INFORMATION_MESSAGE);
    }
} 