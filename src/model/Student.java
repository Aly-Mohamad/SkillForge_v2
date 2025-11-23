package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Student extends User {

    private List<String> enrolledCourseIds;
    private Map<String, LessonProgress> lessonProgressMap;
    private ArrayList<Certificate> certificateList;

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
        if (certificateList == null) certificateList = new ArrayList<>();
    }

    public void enroll(String courseId) {
        ensureCollections();
        if (!enrolledCourseIds.contains(courseId)) {
            enrolledCourseIds.add(courseId);
        }
    }

    public boolean isEnrolled(String courseId) {
        ensureCollections();
        if(enrolledCourseIds.contains(courseId)) return true;
        return false;
    }

    public void markLessonCompleted(String lessonId, int score, boolean passed) {
        ensureCollections();
        LessonProgress progress = lessonProgressMap.getOrDefault(lessonId, new LessonProgress());
        progress.setScore(score);
        progress.setPassed(passed);
        progress.setCompleted(passed);
        progress.incrementTries();
        lessonProgressMap.put(lessonId, progress);
    }

    public boolean hasCompleted(String lessonId) {
        ensureCollections();
        LessonProgress progress = lessonProgressMap.get(lessonId);
        return progress != null && progress.isCompleted();
    }

    public void generateCertificate(Course course) {
        ensureCollections();
        certificateList.add(new Certificate(getUserId(), course.getCourseId()));
    }

    public ArrayList<Certificate> getCertificateList() {
        return certificateList;
    }
}