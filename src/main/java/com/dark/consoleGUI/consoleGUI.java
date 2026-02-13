package com.dark.consoleGUI;

import com.dark.entity.*;
import com.dark.service.*;

import java.util.List;
import java.util.Scanner;

public class consoleGUI {
    // Scanner is static so we can use it everywhere
    private static final Scanner sc = new Scanner(System.in);

    // Initialize Services
    private static final LoginService loginService = new LoginService();
    private static final StudentService studentService = new StudentService();
    private static final TeacherService teacherService = new TeacherService();

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n=========================================");
            System.out.println("   COLLEGE COURSE REGISTRATION SYSTEM    ");
            System.out.println("=========================================");
            System.out.println("1. Student Login");
            System.out.println("2. Teacher (Admin) Login");
            System.out.println("3. Exit");
            System.out.print("Enter Choice: ");

            int choice = getIntInput(); // Safe input method

            switch (choice) {
                case 1 -> handleStudentLogin();
                case 2 -> handleTeacherLogin();
                case 3 -> {
                    System.out.println("Exiting... Goodbye!");
                    System.exit(0);
                }
                default -> System.out.println("Invalid Choice. Please try again.");
            }
        }
    }

    // ==========================================
    // STUDENT SECTION
    // ==========================================

    private static void handleStudentLogin() {
        System.out.println("\n--- STUDENT LOGIN ---");
        System.out.print("Enter Roll Number: ");
        String roll = sc.nextLine();
        System.out.print("Enter Password: ");
        String pass = sc.nextLine();

        Student student = loginService.loginStudent(roll, pass);

        if (student != null) {
            System.out.println("Login Successful! Welcome " + student.getName());
            studentDashboard(student);
        } else {
            System.out.println("Login Failed! Incorrect Roll No or Password.");
        }
    }

    private static void studentDashboard(Student student) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n=== DASHBOARD: " + student.getName() + " ===");
            System.out.println("1. View Available Courses");
            System.out.println("2. Register for a Course");
            System.out.println("3. View My Registered Courses");
            System.out.println("4. Logout");
            System.out.print("Choose Option: ");

            int choice = getIntInput();

            switch (choice) {
                case 1 -> {
                    List<Course> list = studentService.getAllCourses();
                    System.out.println("\n--- Available Courses ---");
                    if(list.isEmpty()) System.out.println("No courses available.");
                    else list.forEach(System.out::println);
                }
                case 2 -> {
                    System.out.print("Enter Course ID to Register (e.g., CS101): ");
                    String cid = sc.nextLine();
                    // Call the registration service
                    String msg = studentService.registerStudentForCourse(student.getRollNumber(), cid);
                    System.out.println(msg);
                }
                case 3 -> {
                    // CRITICAL: We must re-fetch the student from DB to see the latest updates
                    Student freshData = loginService.loginStudent(student.getRollNumber(), student.getPassword());
                    System.out.println("\n--- My Courses ---");
                    if (freshData.getRegisteredCourses().isEmpty()) {
                        System.out.println("You have not registered for any courses yet.");
                    } else {
                        freshData.getRegisteredCourses().forEach(System.out::println);
                    }
                }
                case 4 -> {
                    loggedIn = false;
                    System.out.println("Logging out...");
                }
                default -> System.out.println("Invalid Option.");
            }
        }
    }

    // ==========================================
    // TEACHER (ADMIN) SECTION
    // ==========================================

    private static void handleTeacherLogin() {
        System.out.println("\n--- TEACHER LOGIN ---");
        System.out.print("Enter Emp ID: ");
        String id = sc.nextLine();
        System.out.print("Enter Password: ");
        String pass = sc.nextLine();

        Teacher teacher = loginService.loginTeacher(id, pass);

        if (teacher != null) {
            System.out.println("Login Successful! Welcome " + teacher.getName());
            teacherDashboard(teacher);
        } else {
            System.out.println("Login Failed! Incorrect ID or Password.");
        }
    }

    // ... inside teacherDashboard method ...

    private static void teacherDashboard(Teacher teacher) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n=== ADMIN DASHBOARD: " + teacher.getName() + " ===");
            System.out.println("1. Add New Course");
            System.out.println("2. Remove Course");
            System.out.println("3. View All Courses");
            System.out.println("4. Add New Student");
            System.out.println("5. View All Students & Courses"); // <--- NEW OPTION
            System.out.println("6. Logout");
            System.out.print("Choose Option: ");

            int choice = getIntInput();

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter Course ID: ");
                    String cid = sc.nextLine();
                    System.out.print("Enter Course Name: ");
                    String cname = sc.nextLine();
                    teacherService.addCourse(cid, cname);
                }
                case 2 -> {
                    System.out.print("Enter Course ID to Remove: ");
                    String cid = sc.nextLine();
                    teacherService.removeCourse(cid);
                }
                case 3 -> {
                    System.out.println("\n--- All Courses ---");
                    teacherService.getAllCourses().forEach(System.out::println);
                }
                case 4 -> {
                    System.out.print("Enter Student Roll No: ");
                    String roll = sc.nextLine();
                    System.out.print("Enter Name: ");
                    String name = sc.nextLine();
                    System.out.print("Set Password: ");
                    String pass = sc.nextLine();
                    teacherService.addStudent(roll, name, pass);
                }
                case 5 -> {
                    // Call the new function
                    teacherService.viewAllStudentsAndCourses();
                }
                case 6 -> {
                    loggedIn = false;
                    System.out.println("Logging out...");
                }
                default -> System.out.println("Invalid Option.");
            }
        }
    }

    // ==========================================
    // UTILITY HELPER
    // ==========================================

    // This method prevents the "Infinite Loop" bug if user types letters instead of numbers
    private static int getIntInput() {
        try {
            String input = sc.nextLine(); // Read the whole line
            if (input.trim().isEmpty()) return -1;
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1; // Return -1 if input is not a number
        }
    }
}