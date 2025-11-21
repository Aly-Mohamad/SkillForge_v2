package ui.frames;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import model.Certificate;
import model.Student;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class CertificateListFrame extends JFrame{
    private Student student;

    private JPanel listPanel;
    private JScrollPane listScrollPane;

    public CertificateListFrame(Student student) {
        this.student = student;
        setTitle("Certificate List");
        setSize(900, 500);
        setLocationRelativeTo(null);
        init();
    }

    private void init() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);

        listScrollPane = new JScrollPane(listPanel);
        listScrollPane.getVerticalScrollBar().setUnitIncrement(12);

        mainPanel.add(new JLabel("Certificates", SwingConstants.CENTER), BorderLayout.NORTH);
        mainPanel.add(listScrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton backButton = new JButton("Go Back");
        bottomPanel.add(backButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        add(mainPanel);
        loadCertificates();
    }

    private void loadCertificates() {
        listPanel.removeAll();

        for (Certificate cert : student.getCertificateList()) {
            JPanel row = new JPanel(new GridLayout(1, 3, 10, 10));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            row.setBackground(Color.LIGHT_GRAY);
            row.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            JButton downloadButton = new JButton("Download");

            row.add(new JLabel("Certificate ID: " + cert.getCertificateId()));
            row.add(new JLabel("Student ID: " + cert.getStudentId()));
            row.add(new JLabel("Course ID: " + cert.getCourseId()));
            row.add(new JLabel("Issue Date: " + cert.getIssueDate().toString()));
            row.add(downloadButton);

            listPanel.add(row);
            listPanel.add(Box.createRigidArea(new Dimension(0, 5)));

            downloadButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String pdfPath = "src/Certificate.pdf";
                    Document document = new Document();
                    try {
                        PdfWriter.getInstance(document, new FileOutputStream(pdfPath));
                        document.open();
                        document.add(new Paragraph("Certificate of Completion"));
                        document.add(new Paragraph("CertificateId: " + cert.getCertificateId()));
                        document.add(new Paragraph("Student ID: " + cert.getStudentId()));
                        document.add(new Paragraph("Course ID: " + cert.getCourseId()));
                        document.add(new Paragraph("Issue Date: " + cert.getIssueDate().toString()));
                        document.close();
                    } catch (DocumentException ex) {
                        throw new RuntimeException(ex);
                    } catch (FileNotFoundException ex) {
                        throw new RuntimeException(ex);
                    }
                    JOptionPane.showMessageDialog(CertificateListFrame.this, "Certificate saved as PDF!");
                }
            });
        }
        listPanel.revalidate();
        listPanel.repaint();
    }
}
