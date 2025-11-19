package model;

import java.util.ArrayList;
import java.util.List;

public class Student extends User {

    private List<Course> enrolledCourses = new ArrayList<>();

    public Student(String username, String email, String password) {
        super("student", username, email, password);
    }

    public List<Course> getCourses() {
        return enrolledCourses;
    }

    public void enroll(Course course) {
        if (!enrolledCourses.contains(course)) {
            enrolledCourses.add(course);
        }
    }

    public void unenroll(Course course) {
        enrolledCourses.remove(course);
    }

    public boolean isEnrolled(Course course) {
        return enrolledCourses.contains(course);
    }

    public boolean isEnrolled(String courseId) {
        return enrolledCourses.stream().anyMatch(c -> c.getCourseId().equals(courseId));
    }

    public void setEnrolledCourses(List<Course> courses) {
        this.enrolledCourses = new ArrayList<>(courses);
    }
}