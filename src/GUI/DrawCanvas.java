package GUI;

import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Scanner;

/**
 * DrawCanvas
 */
public class DrawCanvas extends JPanel {

    static final int rectSize = 120;
    static final int margin = 30;
    static final int holdMargin = 5;
    static Scanner stdin = new Scanner(System.in);

    private int holding;
    /**
     * 一番上はholding用 board[row][col]
     */
    private GraphicShape[][] board;
    private HashMap<String,GraphicShape> shapeMap;
    private static final int boxNum = 3;

    /**
     * CanvasWidth
     */
    static final int CanvasX = margin + boxNum * (rectSize + margin) + margin;
    /**
     * CanvasHeight
     */
    static final int CanvasY = 100 + (boxNum + 1) * (rectSize + margin) + margin;

    public void init() {
        board = new GraphicShape[boxNum + 1][boxNum];
        shapeMap = new HashMap<>();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (i == 0) {
                    GraphicShape gs = new GraphicShape("No." + j, "red", "rect", "table");
                    gs.setPoint(i, j);
                    board[i][j] = gs;
                    shapeMap.put(gs.getName(),gs);                    
                } else
                    board[i][j] = null;
            }
        }
    }

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
                int startX = margin + col * (rectSize + margin);
                int startY = margin + (boxNum - row) * (rectSize + margin);
                if (board[row][col] != null) {
                    g.setColor(board[row][col].getColor());
                    putShape(board[row][col], startX, startY, g);
                }
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
        g.fillRect(x, y, rectSize, rectSize);
        g.setColor(Color.black);
        g.drawString(gs.getName(), x + rectSize, y + rectSize);
        g.drawString(gs.getRow()+","+gs.getCol(), x + margin, y + margin);
    }
}
