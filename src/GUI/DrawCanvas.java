package GUI;

import javax.swing.JPanel;

import kadai5.Operator;
import kadai5.Planner;
import kadai5.Shape;
import kadai5.Unifier;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Vector;

/**
 * DrawCanvas
 */
public class DrawCanvas extends JPanel {

    static final int rectSize = 100;
    static final int margin = 20;
    static final int holdMargin = 3;
    static Scanner stdin = new Scanner(System.in);
    private boolean isStop;
    private int index;
    private int holding;
    private String state;
    private Vector<Operator> op;
    /**
     * 一番上はholding用 board[row][col]
     */
    private GraphicShape[][] board;
    private int boxNum;

    /**
     * CanvasWidth
     */
    public int sizeX() {
        return margin + boxNum * (rectSize + margin) + margin;
    }

    /**
     * CanvasHeight
     */
    public int sizeY() {
        return 100 + (boxNum + 1) * (rectSize) + margin * 2;
    }

    public String getState() {
        return state;
    }

    /**
     * 初期状態の設定
     * 
     * @return initial states
     */
    public Vector<String> initState() {
        Vector<String> initialState = new Vector<String>();
        initialState.addAll(Shape.make("A", "red", "triangle"));
        initialState.addAll(Shape.make("B", "blue"));
        initialState.addAll(Shape.make("C", "green"));
        initialState.addElement("handEmpty");
        return initialState;
    }

    /**
     * 目標状態の設定
     * 
     * @return goal states
     */
    public Vector<String> goalList() {
        Vector<String> goalList = new Vector<String>();
        goalList.addElement("triangle on B");
        goalList.addElement("B on green");
        return goalList;
    }

    /**
     * 初期状態の設定後、GraphicShapeの一覧を作成。 boardに格納しつつshapeMapに登録、さらに自身に座標を記憶させる。
     * boardの初期化が終わったら、目標状態の設定に移る。
     */
    public void init() {
        Vector<GraphicShape> shapes = GraphicShape.parseState(initState());
        boxNum = shapes.size();
        board = new GraphicShape[boxNum + 1][boxNum];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (i == 0) {
                    GraphicShape gs = shapes.get(j);
                    gs.setPoint(i, j);
                    board[i][j] = gs;
                } else
                    board[i][j] = null;
            }
        }
        isStop = true;
        holding = 0;
        index = 0;
        state = "board was initialized!";
        System.out.println("board was initialized!");
        Planner plan = new Planner();
        op = plan.GUIStart(goalList(), initState());
    }

    /**
     * Operator
     * <p>
     * 1. Place ?x on ?y -> drop ?x on ?y
     * </p>
     * <p>
     * 2. remove ?x from on top ?y -> pickup ?x
     * </p>
     * <p>
     * 3. pick up ?x from the table -> pickup ?x
     * </p>
     * <p>
     * 4. put ?x down on the table -> drop ?x
     * </p>
     * <p>
     * etc->invaild
     * </p>
     * 
     * @param op
     * @return if operation has finished
     */
    public boolean parseOperation() {
        if (op == null) {
            state = "Can't find plan :(";
            repaint();
            return true;
        }
        Hashtable<String, String> var = new Hashtable<>();
        boolean oparated = false;
        while (!oparated) {
            if (index >= op.size()) {
                state = "This is the Goal!";
                repaint();
                System.out.println("This is the Goal!");
                return true;
            }
            String oparation = op.get(index).getName();
            state = oparation;
            System.out.println(oparation);
            var = new Hashtable<>();
            oparated = true;
            if ((new Unifier()).unify("Place ?x on ?y", oparation, var)) {
                GraphicShape gs1 = GraphicShape.shapeMap.get(var.get("?x"));
                GraphicShape gs2 = GraphicShape.shapeMap.get(var.get("?y"));
                drop(gs1, gs2);
            } else if ((new Unifier()).unify("remove ?x from on top ?y", oparation, var)) {
                GraphicShape gs1 = GraphicShape.shapeMap.get(var.get("?x"));
                pickUp(gs1);
            } else if ((new Unifier()).unify("pick up ?x from the table", oparation, var)) {
                GraphicShape gs1 = GraphicShape.shapeMap.get(var.get("?x"));
                pickUp(gs1);
            } else if ((new Unifier()).unify("put ?x down on the table", oparation, var)) {
                GraphicShape gs1 = GraphicShape.shapeMap.get(var.get("?x"));
                drop(gs1, null);
            } else {
                // skip no oparation
                oparated = false;
            }
            index++;
        }
        return false;
    }

    /**
     * on = nullでテーブル
     * 
     * @param gs
     * @param on
     */
    void drop(GraphicShape gs, GraphicShape on) {
        int col = -1;
        int row = 0;
        if (on == null) {
            col = searchTable();
        } else {
            col = on.getCol();
            row = on.getRow() + 1;
        }
        board[gs.getRow()][gs.getCol()] = null;
        board[row][col] = gs;
        gs.setPoint(row, col);
        holding = col;
        repaint();
    }

    /**
     * 正直、何の上かって関係なくない？Plannerで調べてる訳だし...
     * 
     * @param gs
     */
    void pickUp(GraphicShape gs) {
        int col = gs.getCol();
        holding = col;
        int row = gs.getRow();
        board[board.length - 1][col] = gs;
        gs.setPoint(board.length - 1, col);
        board[row][col] = null;
        repaint();
    }

    int searchTable() {
        for (int i = 0; i < board.length; i++) {
            if (board[0][i] == null)
                return i;
        }
        return -1;
    }

    public boolean nextStep() {
        return parseOperation();
    }

    /**
     * for skipAll button
     */
    public void skipAll() {
        while(!nextStep());
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                // 描画位置の取得
                int startX = margin + col * (rectSize + margin);
                int startY = margin + (boxNum - row) * (rectSize);
                if (board[row][col] != null) {
                    g.setColor(board[row][col].getColor());
                    putShape(board[row][col], startX, startY, g);
                }
                // holding用の手みたいなやつ、pickerの描画
                if (row == board.length - 1 && holding == col) {
                    g.setColor(Color.BLACK);
                    int pointX[] = { startX - holdMargin, startX + rectSize + holdMargin };
                    int pointY[] = { startY - holdMargin, startY - holdMargin };
                    g.drawPolyline(pointX, pointY, pointX.length);
                }
            }
        }
    }

    private void putShape(GraphicShape gs, int x, int y, Graphics g) {
        switch (gs.getShape()) {
        case "triangle":
            // 三角形の描画
            int[] xPoints = { x + rectSize / 2, x + rectSize, x };
            int[] yPoints = { y, y + rectSize, y + rectSize };
            g.fillPolygon(xPoints, yPoints, 3);
            break;
        default:
            g.fillRect(x, y, rectSize, rectSize);
            break;
        }
        g.setColor(Color.black);
        g.drawString(gs.getName(), x + rectSize, y + rectSize);
        g.drawString(gs.getRow() + "," + gs.getCol(), x + margin, y + margin);
    }
}
