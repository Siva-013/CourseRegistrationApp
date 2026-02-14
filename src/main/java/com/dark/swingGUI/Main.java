package com.dark.swingGUI;

import com.dark.entity.Course;
import com.dark.entity.Student;
import com.dark.entity.Teacher;
import com.dark.service.LoginService;
import com.dark.service.StudentService;
import com.dark.service.TeacherService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.util.List;

public class Main extends JFrame {

    // Services
    private final LoginService loginService = new LoginService();
    private final StudentService studentService = new StudentService();
    private final TeacherService teacherService = new TeacherService();

    // Layout Manager to swap screens
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);

    // Panel Keys
    private static final String LOGIN_PANEL = "LOGIN";
    private static final String STUDENT_PANEL = "STUDENT";
    private static final String TEACHER_PANEL = "TEACHER";

    // Panels
    private final StudentPanel studentPanel;
    private final TeacherPanel teacherPanel;

    public Main() {
        setTitle("Course Registration System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen

        // Initialize Panels
        LoginPanel loginPanel = new LoginPanel();
        studentPanel = new StudentPanel();
        teacherPanel = new TeacherPanel();

        // Add to Card Layout
        mainPanel.add(loginPanel, LOGIN_PANEL);
        mainPanel.add(studentPanel, STUDENT_PANEL);
        mainPanel.add(teacherPanel, TEACHER_PANEL);

        add(mainPanel);
    }

    // ==========================================
    // 1. LOGIN PANEL
    // ==========================================
    class LoginPanel extends JPanel {
        JTextField userField = new JTextField(15);
        JPasswordField passField = new JPasswordField(15);
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"Student", "Teacher"});
        JButton loginBtn = new JButton("Login");

        public LoginPanel() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // Title
            JLabel title = new JLabel("University Login");
            title.setFont(new Font("Arial", Font.BOLD, 24));
            title.setHorizontalAlignment(SwingConstants.CENTER);
            gbc.gridwidth = 2; gbc.gridx = 0; gbc.gridy = 0;
            add(title, gbc);

            // Inputs
            gbc.gridwidth = 1; gbc.gridy++;
            gbc.gridx = 0; add(new JLabel("User ID / Roll No:"), gbc);
            gbc.gridx = 1; add(userField, gbc);

            gbc.gridy++;
            gbc.gridx = 0; add(new JLabel("Password:"), gbc);
            gbc.gridx = 1; add(passField, gbc);

            gbc.gridy++;
            gbc.gridx = 0; add(new JLabel("Role:"), gbc);
            gbc.gridx = 1; add(roleCombo, gbc);

            // Button
            gbc.gridy++;
            gbc.gridwidth = 2; gbc.gridx = 0;
            add(loginBtn, gbc);

            // Logic
            loginBtn.addActionListener(e -> performLogin());
        }

        private void performLogin() {
            String id = userField.getText();
            String pass = new String(passField.getPassword());
            String role = (String) roleCombo.getSelectedItem();

            if ("Student".equals(role)) {
                Student s = loginService.loginStudent(id, pass);
                if (s != null) {
                    studentPanel.loadData(s); // Load student data
                    cardLayout.show(mainPanel, STUDENT_PANEL);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Student Credentials");
                }
            } else {
                Teacher t = loginService.loginTeacher(id, pass);
                if (t != null) {
                    teacherPanel.loadData(t); // Load teacher data
                    cardLayout.show(mainPanel, TEACHER_PANEL);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Teacher Credentials");
                }
            }
        }
    }

    // ==========================================
    // 2. STUDENT PANEL
    // ==========================================
    // Replace your existing StudentPanel class with this one
    class StudentPanel extends JPanel {
        // UI Components
        private final JLabel nameLbl = new JLabel();
        private final JLabel rollLbl = new JLabel();
        private final JList<Course> availableCoursesList = new JList<>();
        private final JList<Course> myCoursesList = new JList<>();
        private final DefaultListModel<Course> availableModel = new DefaultListModel<>();
        private final DefaultListModel<Course> myModel = new DefaultListModel<>();

        private Student currentStudent;

        public StudentPanel() {
            setLayout(new BorderLayout(10, 10));
            setBorder(new EmptyBorder(10, 10, 10, 10));

            // --- TOP: PROFILE ---
            JPanel profilePanel = new JPanel(new BorderLayout());
            profilePanel.setBorder(BorderFactory.createTitledBorder("Student Profile"));

            JPanel infoPanel = new JPanel(new GridLayout(2, 1));
            nameLbl.setFont(new Font("Arial", Font.BOLD, 16));
            infoPanel.add(nameLbl);
            infoPanel.add(rollLbl);

            // Photo Placeholder
            JLabel photoLbl = new JLabel("Photo", SwingConstants.CENTER);
            photoLbl.setPreferredSize(new Dimension(80, 80));
            photoLbl.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            profilePanel.add(infoPanel, BorderLayout.CENTER);
            profilePanel.add(photoLbl, BorderLayout.EAST);
            add(profilePanel, BorderLayout.NORTH);

            // --- CENTER: COURSES ---
            JPanel coursesPanel = new JPanel(new GridLayout(1, 2, 10, 0));

            // Left: Available Courses
            JPanel availablePanel = new JPanel(new BorderLayout());
            availablePanel.setBorder(BorderFactory.createTitledBorder("Available Courses"));
            availableCoursesList.setModel(availableModel);
            JButton registerBtn = new JButton("Register Selected");
            registerBtn.setBackground(new Color(144, 238, 144)); // Light Green

            availablePanel.add(new JScrollPane(availableCoursesList), BorderLayout.CENTER);
            availablePanel.add(registerBtn, BorderLayout.SOUTH);

            // Right: My Courses
            JPanel myPanel = new JPanel(new BorderLayout());
            myPanel.setBorder(BorderFactory.createTitledBorder("My Registered Courses"));
            myCoursesList.setModel(myModel);
            JButton dropBtn = new JButton("Drop Selected Course"); // <--- NEW BUTTON
            dropBtn.setBackground(new Color(255, 182, 193)); // Light Red

            myPanel.add(new JScrollPane(myCoursesList), BorderLayout.CENTER);
            myPanel.add(dropBtn, BorderLayout.SOUTH); // Add button to panel

            coursesPanel.add(availablePanel);
            coursesPanel.add(myPanel);

            add(coursesPanel, BorderLayout.CENTER);

            // --- BOTTOM: LOGOUT ---
            JButton logoutBtn = new JButton("Logout");
            logoutBtn.addActionListener(e -> cardLayout.show(mainPanel, LOGIN_PANEL));
            add(logoutBtn, BorderLayout.SOUTH);

            // Logic
            registerBtn.addActionListener(e -> registerAction());

            // <--- NEW LOGIC: DROP ACTION
            dropBtn.addActionListener(e -> {
                Course selected = myCoursesList.getSelectedValue();
                if (selected == null) {
                    JOptionPane.showMessageDialog(this, "Select a course to drop!");
                    return;
                }
                // Call the new service method
                String msg = studentService.dropCourse(currentStudent.getRollNumber(), selected.getCourseId());
                JOptionPane.showMessageDialog(this, msg);
                refreshLists(); // Refresh UI
            });
        }

        public void loadData(Student s) {
            this.currentStudent = s;
            nameLbl.setText("Name: " + s.getName());
            rollLbl.setText("Roll No: " + s.getRollNumber());
            refreshLists();
        }

        private void refreshLists() {
            availableModel.clear();
            List<Course> all = studentService.getAllCourses();
            for (Course c : all) availableModel.addElement(c);

            myModel.clear();
            // Re-fetch student to ensure latest data
            currentStudent = loginService.loginStudent(currentStudent.getRollNumber(), currentStudent.getPassword());
            for (Course c : currentStudent.getRegisteredCourses()) {
                myModel.addElement(c);
            }
        }

        private void registerAction() {
            Course selected = availableCoursesList.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Select a course first!");
                return;
            }
            String msg = studentService.registerStudentForCourse(currentStudent.getRollNumber(), selected.getCourseId());
            JOptionPane.showMessageDialog(this, msg);
            refreshLists();
        }
    }

    // ==========================================
    // 3. TEACHER PANEL
    // ==========================================
    // Replace your existing TeacherPanel class with this one
    class TeacherPanel extends JPanel {
        private final JLabel adminNameLbl = new JLabel();
        private final DefaultListModel<Student> studentListModel = new DefaultListModel<>();
        private final JList<Student> studentList = new JList<>(studentListModel);

        // Detail components
        private final JLabel sName = new JLabel("-");
        private final JLabel sRoll = new JLabel("-");
        private final DefaultListModel<String> sCoursesModel = new DefaultListModel<>();

        public TeacherPanel() {
            setLayout(new BorderLayout(10, 10));
            setBorder(new EmptyBorder(10, 10, 10, 10));

            // --- TOP: PROFILE ---
            JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
            adminNameLbl.setFont(new Font("Arial", Font.BOLD, 18));
            header.add(new JLabel("Admin Profile: "));
            header.add(adminNameLbl);

            JButton logout = new JButton("Logout");
            logout.addActionListener(e -> cardLayout.show(mainPanel, LOGIN_PANEL));

            JPanel topContainer = new JPanel(new BorderLayout());
            topContainer.add(header, BorderLayout.WEST);
            topContainer.add(logout, BorderLayout.EAST);
            add(topContainer, BorderLayout.NORTH);

            // --- CENTER: SPLIT PANE ---
            JSplitPane splitPane = new JSplitPane();

            // LEFT: Student List & Add Button
            JPanel leftPanel = new JPanel(new BorderLayout());
            leftPanel.setBorder(BorderFactory.createTitledBorder("All Students"));

            // Custom renderer
            studentList.setCellRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Student) {
                        Student s = (Student) value;
                        setText(s.getName() + " (" + s.getRollNumber() + ")");
                    }
                    return this;
                }
            });

            // Buttons Panel (Refresh + Add Student)
            JPanel leftBtnPanel = new JPanel(new GridLayout(2, 1, 5, 5));
            JButton loadBtn = new JButton("Refresh List");
            JButton addStudentBtn = new JButton("Add New Student"); // <--- NEW BUTTON

            leftBtnPanel.add(loadBtn);
            leftBtnPanel.add(addStudentBtn);

            leftPanel.add(new JScrollPane(studentList), BorderLayout.CENTER);
            leftPanel.add(leftBtnPanel, BorderLayout.SOUTH);

            // RIGHT: Student Details
            JPanel rightPanel = new JPanel(new BorderLayout());
            rightPanel.setBorder(BorderFactory.createTitledBorder("Selected Student Details"));

            JPanel infoBox = new JPanel(new GridLayout(2, 1));
            infoBox.add(new JLabel("Name: ")); infoBox.add(sName);
            infoBox.add(new JLabel("Roll No: ")); infoBox.add(sRoll);

            rightPanel.add(infoBox, BorderLayout.NORTH);
            JList<String> sCoursesList = new JList<>(sCoursesModel);
            rightPanel.add(new JScrollPane(sCoursesList), BorderLayout.CENTER);

            splitPane.setLeftComponent(leftPanel);
            splitPane.setRightComponent(rightPanel);
            splitPane.setDividerLocation(250);

            add(splitPane, BorderLayout.CENTER);

            // Logic
            loadBtn.addActionListener(e -> loadStudents());

            // <--- NEW LOGIC: ADD STUDENT
            addStudentBtn.addActionListener(e -> {
                // Simple input dialogs
                JTextField rollField = new JTextField();
                JTextField nameField = new JTextField();
                JPasswordField passField = new JPasswordField();

                Object[] message = {
                        "Roll Number:", rollField,
                        "Name:", nameField,
                        "Password:", passField
                };

                int option = JOptionPane.showConfirmDialog(this, message, "Add New Student", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    teacherService.addStudent(rollField.getText(), nameField.getText(), new String(passField.getPassword()));
                    loadStudents(); // Refresh list immediately
                }
            });

            studentList.addListSelectionListener((ListSelectionEvent e) -> {
                if (!e.getValueIsAdjusting()) {
                    Student selected = studentList.getSelectedValue();
                    if (selected != null) showStudentDetails(selected);
                }
            });
        }

        public void loadData(Teacher t) {
            adminNameLbl.setText(t.getName());
            loadStudents();
        }

        private void loadStudents() {
            studentListModel.clear();
            List<Student> list = teacherService.getAllStudents();
            for (Student s : list) studentListModel.addElement(s);
        }

        private void showStudentDetails(Student s) {
            sName.setText(s.getName());
            sRoll.setText(s.getRollNumber());

            sCoursesModel.clear();
            // Need to fetch fresh data to see current courses
            Student fresh = loginService.loginStudent(s.getRollNumber(), s.getPassword());

            if (fresh.getRegisteredCourses().isEmpty()) {
                sCoursesModel.addElement("No courses registered.");
            } else {
                for (Course c : fresh.getRegisteredCourses()) {
                    sCoursesModel.addElement(c.getCourseName() + " (" + c.getCourseId() + ")");
                }
            }
        }
    }

    public static void main(String[] args) {
        // Run on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}