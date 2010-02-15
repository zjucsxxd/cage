import gnu.io.NoSuchPortException;
import org.jruby.embed.LocalContextScope;
import org.jruby.embed.PathType;
import org.jruby.embed.ScriptingContainer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

public class MainWindow {

    public JPanel mainPanel;
    public JButton StartAccelerometer;
    public JButton newGesture;
    public JButton MatchGesture;
    public JEditorPane AppScriptPane;
    public JButton StopAccelerometerButton;

    public ConcurrentLinkedQueue<String> events;
    public PointSource pointSource;

    private ScriptingContainer ruby;

    public MainWindow() {
        this.events = new ConcurrentLinkedQueue<String>();
        this.pointSource = new PointSource();

        StartAccelerometer.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                events.add("start_accelerometer");
                StartAccelerometer.setEnabled(false);
                StopAccelerometerButton.setEnabled(true);
            }
        });

        StopAccelerometerButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                events.add("stop_accelerometer");
                StartAccelerometer.setEnabled(true);
                StopAccelerometerButton.setEnabled(false);
            }
        });

        newGesture.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                events.add("new_gesture");
            }
        });

        MatchGesture.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                events.add("match_gesture");
            }
        });

    }

    public static void main(String[] args) {
        final JFrame window = new JFrame("CAGE");
        final MainWindow main = new MainWindow();
        window.setBounds(100, 100, 700, 400);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setContentPane(main.mainPanel);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                window.setVisible(true);
            }
        });
        main.initJRuby();
    }

    private void initJRuby() {
        ruby = new ScriptingContainer(LocalContextScope.SINGLETON);
        ruby.put("$win", this);
        // enter main loop (and never return!)
        ruby.runScriptlet(PathType.RELATIVE, "src/main.rb");
    }
}
