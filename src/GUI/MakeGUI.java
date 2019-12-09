package GUI;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
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

        JLabel label1 = new JLabel("");
        label1.setText(panelDraw.getState());

        // top of Control Button
        ActionListener listener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                switch (e.getActionCommand()) {
                case "nextStep":
                    panelDraw.parseOperation();
                    break;
                case "skipAll":
                    panelDraw.skipAll();
                    break;
                case "exit":
                    System.exit(0);
                    break;
                case "reset":
                    panelDraw.init();
                    panelDraw.repaint();
                    break;
                default:
                    System.out.println("can't resolve cmd:(");
                    break;
                }
                label1.setText(panelDraw.getState());
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

        JButton btnReset = new JButton("Reset");
        btnReset.addActionListener(listener);
        btnReset.setActionCommand("reset");

        panelCtrl.add(btnNext);
        panelCtrl.add(btnSkip);
        panelCtrl.add(btnExit);
        panelCtrl.add(btnReset);
        // bottom of Control button

        FrameBase frame = new FrameBase("Planner", panelDraw.sizeX(), panelDraw.sizeY());
        // Set panel position
        Container contentPane = frame.getContentPane();
        contentPane.add(panelCtrl, BorderLayout.NORTH);
        contentPane.add(label1, BorderLayout.SOUTH);
        contentPane.add(panelDraw, BorderLayout.CENTER);

        frame.setVisible(true);
    }

}