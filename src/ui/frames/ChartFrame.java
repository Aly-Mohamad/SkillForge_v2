package ui.frames;

import javax.swing.*;
import java.awt.*;

public class ChartFrame extends JFrame {
    public ChartFrame(String title, JComponent chartPanel) {
        super(title);
        setSize(700, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        add(chartPanel, BorderLayout.CENTER);

        JButton close = new JButton("Close");
        close.addActionListener(e -> dispose());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(close);
        add(bottom, BorderLayout.SOUTH);
    }
}