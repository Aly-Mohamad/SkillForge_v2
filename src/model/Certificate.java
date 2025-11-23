package model;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Certificate {
    private String certificateId;
    private String studentId;
    private String courseId;
    private String issueDate;

    public Certificate(String studentId, String courseId) {
        this.studentId = studentId;
        this.certificateId = generateCertificateId();
        this.courseId = courseId;
        this.issueDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    private String generateCertificateId() {
        int number = (int)(Math.random() * 10000);
        return String.format("CERT-%04d", number);
    }

    public String getCertificateId() {
        return certificateId;
    }
    public String getStudentId() {
        return studentId;
    }
    public String getCourseId() {
        return courseId;
    }
    public String getIssueDate() {
        return issueDate;
    }
}
