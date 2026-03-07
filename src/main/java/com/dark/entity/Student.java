package com.dark.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.BatchSize;

@Entity
@Cacheable
public class Student {
    @Id
    @Column(name = "roll_no")
    private String rollNumber; // This is your String ID

    private String name;
    private String password;

    // Join Table for Many-to-Many
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "student_course", joinColumns = @JoinColumn(name = "student_id"), inverseJoinColumns = @JoinColumn(name = "course_id"))
    @BatchSize(size = 16)
    private List<Course> registeredCourses = new ArrayList<>();

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Course> getRegisteredCourses() {
        return registeredCourses;
    }

    public void setRegisteredCourses(List<Course> registeredCourses) {
        this.registeredCourses = registeredCourses;
    }

    public Student(String rollNumber, String name, String password, List<Course> registeredCourses) {
        this.rollNumber = rollNumber;
        this.name = name;
        this.password = password;
        this.registeredCourses = registeredCourses;
    }

    public Student() {
    }
}