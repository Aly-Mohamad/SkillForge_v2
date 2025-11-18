package model;

import java.util.ArrayList;
import java.util.List;

public class Instructor extends User {
    private List<String> createdCourses = new ArrayList<String>();

    public Instructor() { super(); this.role = "instructor"; }

    public Instructor(String username, String email, String passwordHash) {
        super("instructor", username, email, passwordHash);
    }

    public List<String> getCreatedCourses() { return createdCourses; }
    public void addCourse(String courseId) { if (!createdCourses.contains(courseId)) createdCourses.add(courseId); }
}
