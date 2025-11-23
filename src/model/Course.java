package model;

import com.google.gson.annotations.Expose;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
public class Course {

    private final String courseId;
    private String title;
    private String description;
    private final String instructorId;
    private List<Lesson> lessons;
    private List<String> studentIds;
    private String approvalStatus;
    private Map<String, List<String>> progress;
    private Map<String, List<LessonProgress>> lessonProgressMap = new HashMap<>();

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
    public void recordLessonProgress(String studentId, String lessonId, int score, boolean passed) {
        LessonProgress progress = new LessonProgress(studentId, score, passed, true);
        lessonProgressMap.computeIfAbsent(lessonId, k -> new ArrayList<>()).add(progress);
    }
    public double getAverageScorePerLesson(String lessonId) {
        if (lessonProgressMap == null) return 0;
        List<LessonProgress> progresses = lessonProgressMap.get(lessonId);
        if (progresses == null || progresses.isEmpty()) return 0;
        return progresses.stream().mapToInt(LessonProgress::getScore).average().orElse(0);
    }

    public double getCompletionRate(String lessonId, List<Student> enrolledStudents) {
        if (enrolledStudents == null || enrolledStudents.isEmpty()) return 0;

        int total = enrolledStudents.size();
        int completed = 0;

        for (Student student : enrolledStudents) {
            if (student.hasCompleted(lessonId)) {
                completed++;
            }
        }

        return (completed * 100.0) / total;
    }

    public boolean isLessonCompleted(Student student, String lessonId) {
        String studentId = student.getUsername();
        return getProgress().containsKey(studentId) && getProgress().get(studentId).contains(lessonId);
    }

    public int getCompletionPercentage(Student student) {
        java.util.List<Lesson> lessons = getLessons();
        int total = (lessons == null) ? 0 : lessons.size();
        if (total == 0 || student == null) return 0;

        int completed = 0;
        for (Lesson l : lessons) {
            String lessonId = l.getLessonId();
            if (student.hasCompleted(lessonId)) {
                completed++;
            } else if (isLessonCompleted(student, lessonId)) {
                completed++;
            }
        }

        return (int) Math.round((completed * 100.0) / total);

    }
    public void ensureProgressMapInitialized() {
        if (lessonProgressMap == null) {
            lessonProgressMap = new HashMap<>();
        }
    }

    private String generateCourseId() {
        int number = (int)(Math.random() * 10000);
        return String.format("C%04d", number);
    }
}