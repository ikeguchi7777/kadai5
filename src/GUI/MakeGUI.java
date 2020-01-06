package GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * MakeGUI
 */
public class MakeGUI {
    public static void main(String[] args) {
        MakePlannerGUI(DrawCanvas.initState(),DrawCanvas.goalList());
    }

    public static void MakePlannerGUI(Vector<String> initState,Vector<String> goalList) {
        // Draw Space
        DrawCanvas panelDraw = new DrawCanvas();
        panelDraw.setBackground(Color.WHITE);
        panelDraw.init(initState, goalList);

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
                    panelDraw.init(SetInitial.getInit(), new Vector<>(goalList));
                    break;
                default:
                    System.out.println("can't resolve cmd:(");
                    break;
                }
                label1.setText(panelDraw.getState());
            }
        };
        JPanel panelCtrl = new JPanel();
        panelCtrl.setLayout(new FlowLayout());
        panelCtrl.setPreferredSize(new Dimension(300,50));

        String btnName[] = { "Next Step", "Skip ALL", "Exit", "Reset" };
        for (int i = 0; i < btnName.length; i++) {
            JButton btn = new JButton(btnName[i]);
            btn.addActionListener(listener);
            btn.setActionCommand(btnName[i]);
            panelCtrl.add(btn);
        }
        // bottom of Control button

        FrameBase frame = new FrameBase("Planner", panelDraw.sizeX(), panelDraw.sizeY()+120);
        // Set panel position

        frame.getContentPane().add(panelCtrl,BorderLayout.PAGE_START);
        frame.getContentPane().add(panelDraw, BorderLayout.CENTER);
        frame.getContentPane().add(stateLabel, BorderLayout.PAGE_END);
        frame.setVisible(true);
    }

}