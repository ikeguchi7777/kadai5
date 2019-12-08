package GUI;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * MakeGUI
 */
public class MakeGUI {
    public static void main(String[] args) {
        MakePlannerGUI();
    }

    public static void MakePlannerGUI() {
        // Draw Space
        DrawCanvas panelDraw = new DrawCanvas();
        panelDraw.init();
        FrameBase frame = new FrameBase("Planner", panelDraw.sizeX(), panelDraw.sizeY());

        // top of Control Button
        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (e.getActionCommand()) {
                case "nextStep":
                    synchronized (panelDraw) {
                        panelDraw.notifyAll();
                    }
                    break;
                case "skipAll":
                    panelDraw.skipAll();
                    synchronized (panelDraw) {
                        panelDraw.notifyAll();
                    }
                    break;
                case "exit":
                    System.exit(0);
                    break;
                default:
                    break;
                }
            }
        };
        JPanel panelCtrl = new JPanel();

        JButton btnNext = new JButton("Next Step");
        btnNext.addActionListener(listener);
        btnNext.setActionCommand("nextStep");

        JButton btnSkip = new JButton("Skip ALL");
        btnSkip.addActionListener(listener);
        btnSkip.setActionCommand("skipAll");

        JButton btnExit = new JButton("Exit");
        btnExit.addActionListener(listener);
        btnExit.setActionCommand("exit");

        panelCtrl.add(btnNext);
        panelCtrl.add(btnSkip);
        panelCtrl.add(btnExit);
        // bottom of Control button

        // Set panel position
        Container contentPane = frame.getContentPane();
        contentPane.add(panelCtrl, BorderLayout.NORTH);
        contentPane.add(panelDraw, BorderLayout.CENTER);

        frame.setVisible(true);
        Thread th = new Thread(panelDraw);
        th.start();
    }

}