package GUI;

import kadai5.Shape;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Vector;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * SetInitial
 */
public class SetInitial extends JPanel {

    private static Vector<String> initState;
    private static Vector<String> goalList;

    public static Vector<String> getInit() {
        return new Vector<String>(initState);
    }

    public static Vector<String> getGoal() {
        return new Vector<String>(goalList);
    }

    public static void main(String[] args) {
        SetInitialGUI();
    }

    public static void SetInitialGUI() {
        FrameBase frame = new FrameBase("Init", 500, 500);

        Vector<String> colors = new Vector<>(GraphicShape.colorMap.keySet());
        String[] shapes = { "rect", "triangle" };
        String[] nameList = { "A", "B", "C" };
        Vector<String> objList = new Vector<>(colors);
        for (String string : shapes) 
            objList.add(string);
        for (String string : nameList) 
            objList.add(string);
        
        JComboBox<String> colorBox1 = new JComboBox<>(colors);
        JComboBox<String> colorBox2 = new JComboBox<>(colors);
        JComboBox<String> colorBox3 = new JComboBox<>(colors);
        JComboBox<String> shapeBox1 = new JComboBox<>(shapes);
        JComboBox<String> shapeBox2 = new JComboBox<>(shapes);
        JComboBox<String> shapeBox3 = new JComboBox<>(shapes);
        JComboBox<String> objBox1 = new JComboBox<>(objList);
        JComboBox<String> objBox2 = new JComboBox<>(objList);

        JPanel drawPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                String colStr1 = (String) colorBox1.getSelectedItem();
                String colStr2 = (String) colorBox2.getSelectedItem();
                String colStr3 = (String) colorBox3.getSelectedItem();
                String shaStr1 = (String) shapeBox1.getSelectedItem();
                String shaStr2 = (String) shapeBox2.getSelectedItem();
                String shaStr3 = (String) shapeBox3.getSelectedItem();
                initState = new Vector<>();
                initState.addAll(Shape.make("A", colStr1, shaStr1));
                initState.addAll(Shape.make("B", colStr2, shaStr2));
                initState.addAll(Shape.make("C", colStr3, shaStr3));
                initState.addElement("handEmpty");
                HashMap<String, GraphicShape> shapeMap = new HashMap<>();
                Vector<GraphicShape> shapes = GraphicShape.parseState(initState, shapeMap);
                for (int col = 0; col < 3; col++) {
                    int startX = 50 + col * 90;
                    int startY = 10;
                    GraphicShape gs = shapes.get(col);
                    g.setColor(gs.getColor());
                    switch (gs.getShape()) {
                    case "triangle":
                        int[] xPoints = { startX + 80 / 2, startX + 80, startX };
                        int[] yPoints = { startY, startY+80, startY+80 };
                        g.fillPolygon(xPoints, yPoints, 3);
                        break;
                    default:
                        g.fillRect(startX, startY, 80, 80);
                    }
                    g.setColor(Color.black);
                    g.drawString(nameList[col], startX, startY+10);
                }
            }
        };

        ActionListener boxListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.repaint();
            }
        };
        colorBox1.addActionListener(boxListener);
        colorBox2.addActionListener(boxListener);
        colorBox3.addActionListener(boxListener);
        shapeBox1.addActionListener(boxListener);
        shapeBox2.addActionListener(boxListener);
        shapeBox3.addActionListener(boxListener);
        JPanel setLabel1 = new JPanel();
        JPanel setLabel2 = new JPanel();
        JPanel setLabel3 = new JPanel();
        JPanel goalLabel = new JPanel();

        setLabel1.add(new JLabel("color1:"));
        setLabel1.add(colorBox1);
        setLabel1.add(new JLabel("shape1:"));
        setLabel1.add(shapeBox1);

        setLabel2.add(new JLabel("color2:"));
        setLabel2.add(colorBox2);
        setLabel2.add(new JLabel("shape2:"));
        setLabel2.add(shapeBox2);

        setLabel3.add(new JLabel("color3:"));
        setLabel3.add(colorBox3);
        setLabel3.add(new JLabel("shape3:"));
        setLabel3.add(shapeBox3);

        goalLabel.add(objBox1);
        goalLabel.add(new JLabel("on"));
        goalLabel.add(objBox2);

        JTextArea txtArea = new JTextArea("", 3, 15);
        txtArea.setEditable(false);
        txtArea.setLineWrap(true);
        JScrollPane scrPane = new JScrollPane(txtArea);
        scrPane.setPreferredSize(new Dimension(200, 100));

        goalList = new Vector<>();
        ActionListener addListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String objStr1 = (String) objBox1.getSelectedItem();
                String objStr2 = (String) objBox2.getSelectedItem();
                if(objStr1.equals(objStr2)){
                    JOptionPane.showMessageDialog(frame.getContentPane(), "同じものは選べません。");
                    return;
                }
                String goal = objStr1 + " on " + objStr2;
                txtArea.append(goal + "\n");
                goalList.add(goal);
            }
        };
        JButton addBtn = new JButton("ADD GOAL");
        addBtn.addActionListener(addListener);
        goalLabel.add(addBtn);

        JPanel setLabel = new JPanel();
        setLabel.add(setLabel1);
        setLabel.add(setLabel2);
        setLabel.add(setLabel3);
        setLabel.add(goalLabel);
        setLabel.add(scrPane);

        JPanel btnPanel = new JPanel();
        JButton btnInit = new JButton("START");
        ActionListener btnListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String colStr1 = (String) colorBox1.getSelectedItem();
                String colStr2 = (String) colorBox2.getSelectedItem();
                String colStr3 = (String) colorBox3.getSelectedItem();
                String shaStr1 = (String) shapeBox1.getSelectedItem();
                String shaStr2 = (String) shapeBox2.getSelectedItem();
                String shaStr3 = (String) shapeBox3.getSelectedItem();
                initState = new Vector<>();
                initState.addAll(Shape.make("A", colStr1, shaStr1));
                initState.addAll(Shape.make("B", colStr2, shaStr2));
                initState.addAll(Shape.make("C", colStr3, shaStr3));
                initState.addElement("handEmpty");
                MakeGUI.MakePlannerGUI(getInit(), getGoal());
            }
        };
        btnInit.addActionListener(btnListener);
        btnPanel.add(btnInit);

        drawPanel.setPreferredSize(new Dimension(310, 100));
        frame.getContentPane().add(drawPanel, BorderLayout.PAGE_START);
        frame.getContentPane().add(setLabel, BorderLayout.CENTER);
        frame.getContentPane().add(btnPanel, BorderLayout.PAGE_END);
        frame.setVisible(true);
    }
}