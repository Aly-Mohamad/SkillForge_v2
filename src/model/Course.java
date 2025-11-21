package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Course {

    private final String courseId;
    private String title;
    private String description;
    private final String instructorId;
    private List<Lesson> lessons;
    private List<String> studentIds; // Store student IDs instead of full objects
    private String approvalStatus;
    private Map<String, List<String>> progress;

    public Course(String title, String description, String instructorId) {
        this.courseId = generateCourseId();
        this.title = title;
        this.description = description;
        this.instructorId = instructorId;
        this.lessons = new ArrayList<>();
        this.studentIds = new ArrayList<>();
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

    public List<String> getStudentIds() {
        if (studentIds == null) studentIds = new ArrayList<>();
        return studentIds;
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
    public void setStudentIds(List<String> studentIds) { this.studentIds = (studentIds != null) ? studentIds : new ArrayList<>(); }
    public void setProgress(Map<String, List<String>> progress) { this.progress = (progress != null) ? progress : new HashMap<>(); }
    public void setApprovalStatus(String approvalStatus) {this.approvalStatus = approvalStatus;}

    public void addLesson(Lesson lesson) {
        if (lesson != null) getLessons().add(lesson);
    }

    public void removeLesson(String lessonId) {
        getLessons().removeIf(l -> l.getLessonId().equals(lessonId));
    }

    public void enrollStudent(Student student) {
        String studentId = student.getUsername(); // or getEmail()
        if (studentId != null && !getStudentIds().contains(studentId)) {
            getStudentIds().add(studentId);
        }
    }

    public void markLessonCompleted(Student student, String lessonId) {
        String studentId = student.getUsername();
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

    // java
// Only counts lessons that still exist in the course
    public int getCompletionPercentage(Student student) {
        java.util.List<Lesson> lessons = getLessons();
        int total = (lessons == null) ? 0 : lessons.size();
        if (total == 0 || student == null) return 0;

        int completed = 0;
        for (Lesson l : lessons) {
            String lessonId = l.getLessonId();
            // Count completion only if this student completes THIS existing lesson
            if (student.hasCompleted(lessonId)) {
                completed++;
            } else if (isLessonCompleted(student, lessonId)) {
                // Optional fallback if you track per-course per-student progress
                completed++;
            }
        }

        return (int) Math.round((completed * 100.0) / total);
    }

    private String generateCourseId() {
        int number = (int)(Math.random() * 10000);
        return String.format("C%04d", number);
    }
}