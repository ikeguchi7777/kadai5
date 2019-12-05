package GUI;

import javax.swing.JPanel;

import java.awt.Graphics;
import java.util.Scanner;
import java.awt.Color;

/**
 * DrawCanvas
 */
public class DrawCanvas extends JPanel {

    static final int rectSize = 50;
    static Scanner stdin = new Scanner(System.in);

    private GraphicShape holding;
    // 一番上はholding用 board[row][col]
    private GraphicShape[][] board;
    private final int boxNum = 3;

    public void init() {
        board = new GraphicShape[boxNum + 1][boxNum];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (i == 0)
                    board[i][j] = new GraphicShape("No." + j, "red", "rect", "table");
                else
                    board[i][j] = null;
            }
        }
    }

    public void pickUp() {
        int col = selectHold();
        holding = board[holdTop(col)][col];
        board[holdTop(col)][col] = null;
        repaint();
        dropDown();
    }

    private void dropDown() {
        int col = selectDrop();
        board[holdTop(col) + 1][col] = holding;
        holding = null;
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
                if (board[row][col] != null) {
                    g.setColor(Color.getColor(board[row][col].getColor()));
                    putShape(board[row][col], row, col, g);
                }
            }
        }
    }

    private void putShape(GraphicShape gs, int i, int j, Graphics g) {
        g.fillRect(100 + j * 250, 100 + (boxNum-i) * 150, rectSize, rectSize);
    }
}