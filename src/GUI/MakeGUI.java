package GUI;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
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
        Vector<String> initState = panelDraw.initState();
        Vector<String> goalList = panelDraw.goalList();
        panelDraw.init(new Vector<>(initState), new Vector<>(goalList));

        JPanel stateLabel = new JPanel();
        JLabel label1 = new JLabel("");
        label1.setText(panelDraw.getState());
        stateLabel.add(label1, BorderLayout.CENTER);

        // top of Control Button
        ActionListener listener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                switch (e.getActionCommand()) {
                case "Next Step":
                    panelDraw.parseOperation();
                    break;
                case "Skip ALL":
                    panelDraw.skipAll();
                    break;
                case "Exit":
                    System.exit(0);
                    break;
                case "Reset":
                    panelDraw.init(new Vector<>(initState), new Vector<>(goalList));
                    break;
                default:
                    System.out.println("can't resolve cmd:(");
                    break;
                }
                label1.setText(panelDraw.getState());
            }
        };
        JPanel panelCtrl = new JPanel();

        String btnName[] = { "Next Step", "Skip ALL", "Exit", "Reset" };
        for (int i = 0; i < btnName.length; i++) {
            JButton btn = new JButton(btnName[i]);
            btn.addActionListener(listener);
            btn.setActionCommand(btnName[i]);
            panelCtrl.add(btn);
        }
        // bottom of Control button

        FrameBase frame = new FrameBase("Planner", panelDraw.sizeX(), panelDraw.sizeY());
        // Set panel position
        Container contentPane = frame.getContentPane();
        contentPane.add(panelCtrl, BorderLayout.NORTH);
        contentPane.add(stateLabel, BorderLayout.SOUTH);
        contentPane.add(panelDraw, BorderLayout.CENTER);

        frame.setVisible(true);
    }

}