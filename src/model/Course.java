package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Course {

    private String courseId;
    private String title;
    private String description;
    private String instructorId;
    private List<model.Lesson> lessons;
    private List<String> students;

    private Map<String, List<String>> progress;

    public Course(String title, String description, String instructorId) {
        this.courseId = generateCourseId();
        this.title = title;
        this.description = description;
        this.instructorId = instructorId;
        this.lessons = new ArrayList<>();
        this.students = new ArrayList<>();
        this.progress = new HashMap<>();
    }

    public String getCourseId() { return courseId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getInstructorId() { return instructorId; }

    public List<model.Lesson> getLessons() {
        if (lessons == null) lessons = new ArrayList<>();
        return lessons;
    }

    public List<String> getStudents() {
        if (students == null) students = new ArrayList<>();
        return students;
    }

    public Map<String, List<String>> getProgress() {
        if (progress == null) progress = new HashMap<>();
        return progress;
    }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setLessons(List<model.Lesson> lessons) { this.lessons = (lessons != null) ? lessons : new ArrayList<>(); }
    public void setStudents(List<String> students) { this.students = (students != null) ? students : new ArrayList<>(); }
    public void setProgress(Map<String, List<String>> progress) { this.progress = (progress != null) ? progress : new HashMap<>(); }

    public void addLesson(model.Lesson lesson) {
        if (lesson != null) getLessons().add(lesson);
    }

    public void removeLesson(String lessonId) {
        getLessons().removeIf(l -> l.getLessonId().equals(lessonId));
    }

    public void enrollStudent(String studentId) {
        if (!getStudents().contains(studentId)) getStudents().add(studentId);
    }

    public void markLessonCompleted(String studentId, String lessonId) {
        getProgress().putIfAbsent(studentId, new ArrayList<>());
        List<String> completed = getProgress().get(studentId);
        if (!completed.contains(lessonId)) completed.add(lessonId);
    }

    public boolean isLessonCompleted(String studentId, String lessonId) {
        return getProgress().containsKey(studentId) && getProgress().get(studentId).contains(lessonId);
    }

    public int countCompletedLessons(String studentId) {
        return getProgress().getOrDefault(studentId, new ArrayList<>()).size();
    }

    public int getCompletionPercentage(String studentId) {
        int total = getLessons().size();
        if (total == 0) return 0;
        return countCompletedLessons(studentId) * 100 / total;
    }

    private String generateCourseId() {
        int number = (int)(Math.random() * 10000);
        return String.format("C%04d", number);
    }
}
