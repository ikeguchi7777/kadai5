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
public class DrawCanvas extends JPanel implements Runnable {

    static final int rectSize = 120;
    static final int margin = 30;
    static final int holdMargin = 5;
    static Scanner stdin = new Scanner(System.in);
    static boolean isStop = true;
    public static int index = 0;

    private int holding;
    /**
     * 一番上はholding用 board[row][col]
     */
    private GraphicShape[][] board;
    private static final int boxNum = 3;

    /**
     * CanvasWidth
     */
    static final int CanvasX = margin + boxNum * (rectSize + margin) + margin;
    /**
     * CanvasHeight
     */
    static final int CanvasY = 100 + (boxNum + 1) * (rectSize + margin) + margin;

    /**
     * 初期状態の設定
     * 
     * @return initial states
     */
    public Vector<String> initState() {
        Vector<String> initialState = new Vector<String>();
        initialState.addAll(Shape.make("A", "red","triangle"));
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

    @Override
    public void run() {
        Planner plan = new Planner();
        parseOperation(plan.GUIStart(goalList(), initState()));
    }

    /**
     * 初期状態の設定後、GraphicShapeの一覧を作成。 boardに格納しつつshapeMapに登録、さらに自身に座標を記憶させる。
     * boardの初期化が終わったら、目標状態の設定に移る。
     */
    public void init() {
        Vector<GraphicShape> shapes = GraphicShape.parseState(initState());
        int num = shapes.size();
        board = new GraphicShape[num + 1][num];
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
     */
    synchronized public void parseOperation(Vector<Operator> op) {
        Hashtable<String, String> var = new Hashtable<>();
        index = 0;
        while (true) {
            System.out.println(op.get(index).getName());
            if(index>=op.size()){
                init();
                index = 0;
            }
            if (isStop) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if ((new Unifier()).unify("Place ?x on ?y", op.get(index).getName(), var)) {
                GraphicShape gs1 = GraphicShape.shapeMap.get(var.get("?x"));
                GraphicShape gs2 = GraphicShape.shapeMap.get(var.get("?y"));
                drop(gs1, gs2);
            }
            if ((new Unifier()).unify("remove ?x from on top ?y", op.get(index).getName(), var)) {
                GraphicShape gs1 = GraphicShape.shapeMap.get(var.get("?x"));
                GraphicShape gs2 = GraphicShape.shapeMap.get(var.get("?y"));
                pickUp(gs1, gs2);
            }
            if ((new Unifier()).unify("pick up ?x from the table", op.get(index).getName(), var)) {
                GraphicShape gs1 = GraphicShape.shapeMap.get(var.get("?x"));
                pickUp(gs1, null);
            }
            if ((new Unifier()).unify("put ?x down on the table", op.get(index).getName(), var)) {
                GraphicShape gs1 = GraphicShape.shapeMap.get(var.get("?x"));
                drop(gs1,null);
            }
            repaint();
        }
    }

    /**
     * on = nullでテーブル
     * @param gs
     * @param on
     */
    void drop(GraphicShape gs,GraphicShape on) {
        int col = -1;
        if(on==null){
            col = searchTable();
        }else{
            col = on.getCol();
        }
        int row = on.getRow()+1;
        board[gs.getRow()][gs.getCol()]= null;
        board[row][col] = gs;
        gs.setPoint(row, col);
        holding = col;
        repaint();
        /*
        int col = selectDrop();
        int top = holdTop(col);
        board[top + 1][col] = board[boxNum][holding];
        board[top + 1][col].setPoint(top + 1, col);
        board[boxNum][holding] = null;
        holding = col;
        repaint();
         */
    }

    /**
     * on = nullでテーブル
     * @param gs
     * @param on
     */
    void pickUp(GraphicShape gs, GraphicShape on) {
        int col = gs.getCol();
        holding = col;
        int row = gs.getRow();
        board[board[0].length][col] = gs;
        gs.setPoint(board[0].length, col);
        board[row][col] = null;
        repaint();
    }

    int searchTable(){
        for (int i = 0; i < board.length; i++) {
            if(board[0][i]==null)
            return i;
        }
        return -1;
    }

    /**
     * CUIテスト用
     */
    public void pickUp() {
        int col = selectHold();
        holding = col;
        int top = holdTop(col);
        board[boxNum][col] = board[top][col];
        board[top][col].setPoint(top, col);
        board[top][col] = null;
        repaint();
        dropDown();
    }

    /**
     * CUIテスト用
     */
    private void dropDown() {
        int col = selectDrop();
        int top = holdTop(col);
        board[top + 1][col] = board[boxNum][holding];
        board[top + 1][col].setPoint(top + 1, col);
        board[boxNum][holding] = null;
        holding = col;
        repaint();
        pickUp();
    }

    /**
     * CUIテスト用
     */
    private int selectDrop() {
        System.out.print("select drop:");
        int select = stdin.nextInt();
        while (select < 0 || select >= boxNum) {
            System.out.println("can't drop:" + select);
            System.out.print("select drop:");
            select = stdin.nextInt();
        }
        return select;
    }

    /**
     * CUIテスト用
     */
    private int selectHold() {
        System.out.print("select hold:");
        int select = stdin.nextInt();
        while (select < 0 || select >= boxNum || board[0][select] == null) {
            System.out.println("can't hold:" + select);
            System.out.print("select hold:");
            select = stdin.nextInt();
        }
        return select;
    }

    /**
     * scan board from the top
     * 
     * @param col
     * @return top index or -1 when no object
     */
    private int holdTop(int col) {
        // scan from the top
        for (int row = boxNum - 1; row >= 0; row--) {
            if (board[row][col] != null)
                return row;
        }
        return -1;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                // 描画位置の取得
                int startX = margin + col * (rectSize + margin);
                int startY = margin + (boxNum - row) * (rectSize + margin);
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
