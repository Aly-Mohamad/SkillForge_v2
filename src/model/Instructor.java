package model;

import java.util.ArrayList;
import java.util.List;

public class Instructor extends User {
    private List<String> createdCourses = new ArrayList<String>();

    public Instructor(String username, String email, String password) {
        super("instructor", username, email, password);
    }

    public List<String> getCreatedCourses() { return createdCourses; }
    public void addCourse(String courseId) { if (!createdCourses.contains(courseId)) createdCourses.add(courseId); }
}
