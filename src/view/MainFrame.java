package view;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * MainFrame
 * ---------
 * The main application window (View layer in MVC).
 *
 * This class is responsible ONLY for displaying UI components.
 * No business logic or file access should exist here.
 */
public class MainFrame extends JFrame {

    public MainFrame() {

        // Window title
        setTitle("Healthcare Referral System");

        // Window size
        setSize(800, 500);

        // Close app when window closes
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Centre window on screen
        setLocationRelativeTo(null);

        // Simple placeholder label
        JLabel label = new JLabel(
                "Healthcare System GUI Loaded",
                SwingConstants.CENTER
        );

        add(label);
    }
}
