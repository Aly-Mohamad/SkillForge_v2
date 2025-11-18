package model;

import java.util.ArrayList;
import java.util.List;

public class Student extends model.User {

    private List<String> enrolledCourses = new ArrayList<>();

    public Student(String username, String email, String passwordHash) {
        super("student", username, email, passwordHash);
    }


    public List<String> getCourses() {
        return enrolledCourses;
    }

    public void enroll(String courseId) {
        if (!enrolledCourses.contains(courseId)) {
            enrolledCourses.add(courseId);
        }
    }

    public void unenroll(String courseId) {
        enrolledCourses.remove(courseId);
    }

    public boolean isEnrolled(String courseId) {
        return enrolledCourses.contains(courseId);
    }

    public void setEnrolledCourses(List<String> courses) {
        this.enrolledCourses = new ArrayList<>(courses);
    }
}
