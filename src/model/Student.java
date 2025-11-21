package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Student extends User {

    private List<String> enrolledCourseIds;
    private Map<String, LessonProgress> lessonProgressMap;

    // No-arg constructor for deserialization
    public Student() {
        super();
        initCollections();
    }

    public Student(String username, String email, String password) {
        super("student", username, email, password);
        initCollections();
    }

    private void initCollections() {
        if (enrolledCourseIds == null) enrolledCourseIds = new ArrayList<>();
        if (lessonProgressMap == null) lessonProgressMap = new HashMap<>();
    }

    private void ensureCollections() {
        if (enrolledCourseIds == null) enrolledCourseIds = new ArrayList<>();
        if (lessonProgressMap == null) lessonProgressMap = new HashMap<>();
    }

    // Course enrollment
    public List<String> getCourseIds() {
        ensureCollections();
        return enrolledCourseIds;
    }

    public void enroll(String courseId) {
        ensureCollections();
        if (!enrolledCourseIds.contains(courseId)) {
            enrolledCourseIds.add(courseId);
        }
    }

    public void unenroll(String courseId) {
        ensureCollections();
        enrolledCourseIds.remove(courseId);
    }

    public boolean isEnrolled(String courseId) {
        ensureCollections();
        if(enrolledCourseIds.contains(courseId)) return false;
        return enrolledCourseIds.contains(courseId);
    }

    public void setEnrolledCourseIds(List<String> courseIds) {
        enrolledCourseIds = (courseIds != null) ? new ArrayList<>(courseIds) : new ArrayList<>();
    }

    // Lesson progress tracking
    public void markLessonCompleted(String lessonId, int score, boolean passed) {
        ensureCollections();
        LessonProgress progress = lessonProgressMap.getOrDefault(lessonId, new LessonProgress());
        progress.setScore(score);
        progress.setPassed(passed);
        progress.setCompleted(passed); // only mark as completed if passed
        progress.incrementTries();
        lessonProgressMap.put(lessonId, progress);
    }

    public boolean hasCompleted(String lessonId) {
        ensureCollections();
        LessonProgress progress = lessonProgressMap.get(lessonId);
        return progress != null && progress.isCompleted();
    }

    public LessonProgress getLessonProgress(String lessonId) {
        ensureCollections();
        return lessonProgressMap.getOrDefault(lessonId, new LessonProgress());
    }

    public Map<String, LessonProgress> getAllLessonProgress() {
        ensureCollections();
        return lessonProgressMap;
    }
}