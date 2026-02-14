package com.dark.ui;

import com.dark.entity.Course;
import com.dark.service.TeacherService;
import com.dark.entity.Student;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TeacherDashboard extends JFrame {

    private final TeacherService teacherService = new TeacherService();
    private final JTable table;
    private final DefaultTableModel tableModel;

    public TeacherDashboard(String teacherName) {

        setTitle("Admin Dashboard - " + teacherName);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // =========================
        // TOP PANEL (Buttons)
        // =========================
        JPanel topPanel = new JPanel(new FlowLayout());

        JButton addCourseBtn = new JButton("Add Course");
        JButton removeCourseBtn = new JButton("Remove Course");
        JButton viewCoursesBtn = new JButton("View Courses");
        JButton addStudentBtn = new JButton("Add Student");
        JButton viewStudentsBtn = new JButton("View Students & Courses");
        JButton logoutBtn = new JButton("Logout");


        topPanel.add(logoutBtn);
        topPanel.add(addCourseBtn);
        topPanel.add(removeCourseBtn);
        topPanel.add(viewCoursesBtn);
        topPanel.add(addStudentBtn);
        topPanel.add(viewStudentsBtn);

        add(topPanel, BorderLayout.NORTH);

        // =========================
        // TABLE (Center)
        // =========================
        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // =========================
        // BUTTON ACTIONS
        // =========================

        // ADD COURSE
        addCourseBtn.addActionListener(e -> {

            String courseId = JOptionPane.showInputDialog(this, "Enter Course ID:");
            if (courseId == null || courseId.isEmpty()) return;

            String courseName = JOptionPane.showInputDialog(this, "Enter Course Name:");
            if (courseName == null || courseName.isEmpty()) return;

            teacherService.addCourse(courseId, courseName);
            JOptionPane.showMessageDialog(this, "Course Added Successfully.");
        });

        // REMOVE COURSE
        removeCourseBtn.addActionListener(e -> {
            String courseId = JOptionPane.showInputDialog(this, "Enter Course ID to Remove:");
            if (courseId != null && !courseId.isEmpty()) {
                teacherService.removeCourse(courseId);
                JOptionPane.showMessageDialog(this, "Operation Completed.");
            }
        });


        // VIEW COURSES
        viewCoursesBtn.addActionListener(e -> loadCourses());

        // ADD STUDENT
        addStudentBtn.addActionListener(e -> {

            String roll = JOptionPane.showInputDialog(this, "Enter Roll Number:");
            if (roll == null || roll.isEmpty()) return;

            String name = JOptionPane.showInputDialog(this, "Enter Name:");
            if (name == null || name.isEmpty()) return;

            String pass = JOptionPane.showInputDialog(this, "Set Password:");
            if (pass == null || pass.isEmpty()) return;

            teacherService.addStudent(roll, name, pass);
            JOptionPane.showMessageDialog(this, "Student Added Successfully.");
        });

        // VIEW STUDENTS & COURSES
        viewStudentsBtn.addActionListener(e -> loadStudentsWithCourses());
    }

    // =========================
    // LOAD COURSES INTO TABLE
    // =========================
    private void loadCourses() {

        List<Course> courses = teacherService.getAllCourses();

        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        tableModel.addColumn("Course ID");
        tableModel.addColumn("Course Name");

        for (Course c : courses) {
            tableModel.addRow(new Object[]{
                    c.getCourseId(),
                    c.getCourseName()
            });
        }
    }

    // =========================
    // LOAD STUDENTS + COURSES
    // =========================

    private void loadStudentsWithCourses() {

        List<Student> students = teacherService.getAllStudentsWithCourses();

        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        tableModel.addColumn("Roll No");
        tableModel.addColumn("Student Name");
        tableModel.addColumn("Registered Courses");

        System.out.println("\n===== STUDENTS & REGISTERED COURSES =====");

        for (Student s : students) {

            StringBuilder courses = new StringBuilder();

            if (s.getRegisteredCourses().isEmpty()) {
                courses.append("No Courses");
            } else {
                s.getRegisteredCourses().forEach(c ->
                        courses.append(c.getCourseName())
                                .append(" (")
                                .append(c.getCourseId())
                                .append("), ")
                );
            }

            // UI
            tableModel.addRow(new Object[]{
                    s.getRollNumber(),
                    s.getName(),
                    courses.toString()
            });

            // Console
            System.out.println("Student: " + s.getName() +
                    " (" + s.getRollNumber() + ")");
            System.out.println("Courses: " + courses);
            System.out.println("-----------------------------------");
        }
    }

    // =========================
    // MAIN METHOD (Testing)
    // =========================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new TeacherDashboard("Admin").setVisible(true)
        );
    }
}
