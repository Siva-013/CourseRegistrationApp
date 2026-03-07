package com.dark.service;

import com.dark.entity.*;
import com.dark.util.JPAUtil;
import jakarta.persistence.EntityManager;

public class LoginService {

    public Student loginStudent(String rollNumber, String password) {
        try (EntityManager em = JPAUtil.getEntityManager()) {

            Student s = em.find(Student.class, rollNumber);

            if (s != null && s.getPassword().equals(password)) {
                // Force-initialize lazy courses while EM is open
                s.getRegisteredCourses().size();
                return s; // Login Success
            }
            return null; // Login Failed
        }
    }

    public Teacher loginTeacher(String employmentId, String password) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            Teacher t = em.find(Teacher.class, employmentId);

            if (t != null && t.getPassword().equals(password)) {
                return t; // Login Success
            }
            return null; // Login Failed
        }
    }
}