package com.dark.service;

import com.dark.entity.*;
import com.dark.util.JPAUtil;
import jakarta.persistence.*;

import java.util.List;

public class TeacherService {

    public void addCourse(String courseId, String courseName) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = null;

        try (em) {
            tx = em.getTransaction();
            tx.begin();

            // Check if course exists to avoid duplicate errors
            if (em.find(Course.class, courseId) != null) {
                System.out.println("Error: Course ID " + courseId + " already exists.");
                return; // em closes automatically
            }

            Course course = new Course(courseId, courseName);
            em.persist(course);

            tx.commit();
            System.out.println("Course Added: " + courseName);

        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            System.out.println("Exception in addCourse: " + e.getMessage());
        }
    }

    public void removeCourse(String courseId) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = null;

        try (em) {
            tx = em.getTransaction();
            tx.begin();

            Course c = em.find(Course.class, courseId);
            if (c != null) {
                em.remove(c);
                System.out.println("Course Removed Successfully.");
            } else {
                System.out.println("Error: Course with ID " + courseId + " not found.");
            }

            tx.commit();

        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            System.out.println("Exception in removeCourse: " + e.getMessage());
        }
    }

    public List<Course> getAllCourses() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery("SELECT c FROM Course c", Course.class).getResultList();
        }
    }

    public void addStudent(String rollNumber, String name, String password) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = null;

        try (em) {
            // Check existence BEFORE starting transaction (Optimization)
            if (em.find(Student.class, rollNumber) != null) {
                System.out.println("Error: Student with Roll No " + rollNumber + " already exists.");
                return;
            }

            tx = em.getTransaction();
            tx.begin();

            Student s = new Student();
            s.setRollNumber(rollNumber);
            s.setName(name);
            s.setPassword(password);

            em.persist(s);
            tx.commit();
            System.out.println("Student Added Successfully: " + name);

        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            System.out.println("Error adding student: " + e.getMessage());
        }
    }
    public void viewAllStudentsAndCourses() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            // SIMPLIFIED: Just "from EntityName" works in Hibernate
            List<Student> students = em.createQuery("from Student", Student.class).getResultList();

            if (students.isEmpty()) {
                System.out.println("No students found.");
                return;
            }

            System.out.println("\n--- All Students & Registered Courses ---");
            for (Student s : students) {
                System.out.println("Student: " + s.getName() + " (" + s.getRollNumber() + ")");

                // Using a simple one-liner check for cleaner code
                if (s.getRegisteredCourses().isEmpty()) {
                    System.out.println("   - No courses registered.");
                } else {
                    s.getRegisteredCourses().forEach(c ->
                            System.out.println("   - " + c.getCourseName() + " (" + c.getCourseId() + ")")
                    );
                }
                System.out.println("-----------------------------------");
            }
        }
    }
}