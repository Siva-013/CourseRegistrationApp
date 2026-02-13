package com.dark.consoleGUI;

import com.dark.entity.Course;
import com.dark.entity.Student;
import com.dark.entity.Teacher;
import com.dark.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.ArrayList;


// only run this for the first time then don't use this
public class tempMain {
    public static void main(String[] args) {
        System.out.println("Starting Data Seeding...");

        EntityManager em = JPAUtil.getEntityManager();

        EntityTransaction tx = null;
        try (em) {
            tx = em.getTransaction();
            tx.begin();

            // --- 1. Create Teacher (Admin) ---
            // ID: T01, Name: Prof. Snape, Pass: admin123
            Teacher t1 = new Teacher("T01", "Prof. Snape", "admin123");
            em.persist(t1);
            System.out.println("Added Teacher: Prof. Snape");


            // --- 2. Create Courses ---
            Course c1 = new Course("CS101", "Intro to Java");
            Course c2 = new Course("CS102", "Advanced Data Structures");
            Course c3 = new Course("DB201", "Database Systems");

            em.persist(c1);
            em.persist(c2);
            em.persist(c3);
            System.out.println("Added 3 Courses");


            // --- 3. Create Students ---
            // Note: We pass 'new ArrayList<>()' for the course list parameter
            Student s1 = new Student("S01", "Harry Potter", "magic123", new ArrayList<>());
            Student s2 = new Student("S02", "Hermione Granger", "books456", new ArrayList<>());

            em.persist(s1);
            em.persist(s2);
            System.out.println("Added 2 Students");

            tx.commit();
            System.out.println("=========================================");
            System.out.println("Data Seeding Completed Successfully!");
            System.out.println("Tables created and default data inserted.");
            System.out.println("=========================================");

        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            System.out.println("Error occurred while trying to see the tables and default data." + e.getMessage());
        }
        // Optional: Close factory if this is the only thing running
        // JPAUtil.close();
    }
}