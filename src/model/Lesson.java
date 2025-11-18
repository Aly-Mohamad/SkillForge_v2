package model;

import java.util.ArrayList;
import java.util.List;

public class Lesson {

    private String lessonId;
    private String title;
    private String content;
    private List<String> resources = new ArrayList<>();

    public Lesson(String title, String content) {
        this.lessonId = generateLessonId();
        this.title = title;
        this.content = content;
    }

    public String getLessonId() { return lessonId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public List<String> getResources() { return resources; }

    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setResources(List<String> resources) { this.resources = new ArrayList<>(resources); }
    public void addResource(String r) { if (r != null && !r.isEmpty()) resources.add(r); }

    private String generateLessonId() {
        int number = (int)(Math.random() * 10000);
        return String.format("L%04d", number);
    }
}
