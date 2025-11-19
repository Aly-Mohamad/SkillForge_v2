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
    private List<Lesson> lessons;
    private List<Student> students;
    private String approvalStatus;
    private Map<String, List<String>> progress;

    public Course(String title, String description, String instructorId) {
        this.courseId = generateCourseId();
        this.title = title;
        this.description = description;
        this.instructorId = instructorId;
        this.lessons = new ArrayList<>();
        this.students = new ArrayList<>();
        this.progress = new HashMap<>();
        this.approvalStatus = "PENDING";
    }

    public String getCourseId() { return courseId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getInstructorId() { return instructorId; }

    public List<Lesson> getLessons() {
        if (lessons == null) lessons = new ArrayList<>();
        return lessons;
    }

    public List<Student> getStudents() {
        if (students == null) students = new ArrayList<>();
        return students;
    }

    public Map<String, List<String>> getProgress() {
        if (progress == null) progress = new HashMap<>();
        return progress;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setLessons(List<Lesson> lessons) { this.lessons = (lessons != null) ? lessons : new ArrayList<>(); }
    public void setStudents(List<Student> students) { this.students = (students != null) ? students : new ArrayList<>(); }
    public void setProgress(Map<String, List<String>> progress) { this.progress = (progress != null) ? progress : new HashMap<>(); }

    public void addLesson(Lesson lesson) {
        if (lesson != null) getLessons().add(lesson);
    }

    public void removeLesson(String lessonId) {
        getLessons().removeIf(l -> l.getLessonId().equals(lessonId));
    }

    public void enrollStudent(Student student) {
        if (student != null && !getStudents().contains(student)) {
            getStudents().add(student);
        }
    }

    public void markLessonCompleted(Student student, String lessonId) {
        String studentId = student.getUsername(); // or getEmail(), depending on your unique key
        getProgress().putIfAbsent(studentId, new ArrayList<>());
        List<String> completed = getProgress().get(studentId);
        if (!completed.contains(lessonId)) completed.add(lessonId);
    }

    public boolean isLessonCompleted(Student student, String lessonId) {
        String studentId = student.getUsername();
        return getProgress().containsKey(studentId) && getProgress().get(studentId).contains(lessonId);
    }

    public int countCompletedLessons(Student student) {
        String studentId = student.getUsername();
        return getProgress().getOrDefault(studentId, new ArrayList<>()).size();
    }

    public int getCompletionPercentage(Student student) {
        int total = getLessons().size();
        if (total == 0) return 0;
        return countCompletedLessons(student) * 100 / total;
    }

    private String generateCourseId() {
        int number = (int)(Math.random() * 10000);
        return String.format("C%04d", number);
    }
}