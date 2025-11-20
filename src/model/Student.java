package model;

import java.util.ArrayList;
import java.util.List;

public class Student extends User {

    private List<String> enrolledCourseIds = new ArrayList<>();

    public Student(String username, String email, String password) {
        super("student", username, email, password);
    }

    public List<String> getCourseIds() {
        return enrolledCourseIds;
    }

    public void enroll(String courseId) {
        if (!enrolledCourseIds.contains(courseId)) {
            enrolledCourseIds.add(courseId);
        }
    }

    public void unenroll(String courseId) {
        enrolledCourseIds.remove(courseId);
    }

    public boolean isEnrolled(String courseId) {
        if(enrolledCourseIds.contains(courseId)) return false;
        return enrolledCourseIds.contains(courseId);
    }

    public void setEnrolledCourseIds(List<String> courseIds) {
        this.enrolledCourseIds = new ArrayList<>(courseIds);
    }
}