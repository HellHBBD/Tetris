package PlanB.App;

import PlanB.block.Cell;
import PlanB.block.Tetromino;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.cert.Certificate;


public class Tetris extends JPanel {


    private Tetromino currentOne = Tetromino.randomOne();//正在下落的方塊

    private Tetromino nextOne = Tetromino.randomOne();// 下一個

    private Cell[][] wall = new Cell[18][9];//遊戲區域

    private static final int CELL_SIZE = 48;


    int[] scores_pool = {0, 1, 2, 5, 10}; // 一次消不同數量行的分數

    private int totalScore = 0;//目前分數

    private int totalLine = 0;//目前消掉的行數


    public static final int PLING = 0;
    public static final int STOP = 1;
    public static final int OVER = 2;

    private int game_state; //當前狀態

    String[] show_state = {"P[pause]", "C[continue]", "S[replay]"};//顯示遊戲狀態



    public static BufferedImage I;
    public static BufferedImage J;
    public static BufferedImage L;
    public static BufferedImage O;
    public static BufferedImage S;
    public static BufferedImage T;
    public static BufferedImage Z;
    public static BufferedImage background;

    static {
        try {

            I = ImageIO.read(new File("img/I.png"));
            J = ImageIO.read(new File("img/J.png"));
            L = ImageIO.read(new File("img/L.png"));
            O = ImageIO.read(new File("img/O.png"));
            S = ImageIO.read(new File("img/S.png"));
            T = ImageIO.read(new File("img/T.png"));
            Z = ImageIO.read(new File("img/Z.png"));
            background = ImageIO.read(new File("img/background.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override//繪製
    public void paint(Graphics g) {
        g.drawImage(background, 0, 0, null);

        g.translate(22, 15);//平移座標軸

        paintWall(g);//遊戲區域

        paintCurrentOne(g);//現在的方塊

        paintNextOne(g);//下一個方塊

        paintSource(g);//遊戲得分

        paintState(g);//遊戲狀態
    }

    public void start() {
        game_state = PLING;
        KeyListener l = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                switch (code) {
                    case KeyEvent.VK_DOWN:
                        sortDropActive();
                        break;
                    case KeyEvent.VK_LEFT:
                        moveleftActive();
                        break;
                    case KeyEvent.VK_RIGHT:
                        moveRightActive();
                        break;
                    case KeyEvent.VK_UP:
                        rotateRightActive();
                        break;
                    case KeyEvent.VK_SPACE:
                            hadnDropActive();
                        break;
                    case KeyEvent.VK_P:
                        //判斷當時的狀態
                        if (game_state == PLING) {
                            game_state = STOP;
                        }
                        break;
                    case KeyEvent.VK_C:
                        if (game_state == STOP) {
                            game_state = PLING;
                        }
                        break;
                    case KeyEvent.VK_S:
                        //重開一局
                        game_state = PLING;
                        wall = new Cell[18][9];
                        currentOne = Tetromino.randomOne();
                        nextOne = Tetromino.randomOne();
                        totalScore = 0;
                        totalLine = 0;
                        break;
                }
            }
        };
        this.addKeyListener(l);
        this.requestFocus();

        while (true) {
            if (game_state == PLING) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (camDrop()) {
                    currentOne.moveDrop();
                } else {
                    landToWall();
                    destroyLine();
                    if (isGameOver()) {
                        game_state = OVER;
                    } else {
                        currentOne = nextOne;
                        nextOne = Tetromino.randomOne();
                    }
                }
            }
            repaint();
        }
    }

    //方塊旋轉
    public void rotateRightActive() {
        currentOne.rotateRight();
        if (outOFBounds() || coincide()) {
            currentOne.rotateLeft();
        }
    }

    //按space下落
    public void hadnDropActive() {
        while (true) {
            //判斷能否下落
            if (camDrop()) {
                currentOne.moveDrop();
            } else {
                break;
            }
        }
        landToWall();
        destroyLine();
        if (isGameOver()) {
            game_state = OVER;
        } else {
            currentOne = nextOne;
            nextOne = Tetromino.randomOne();
        }
    }

    //按下會往下一格
    public void sortDropActive() {
        if (camDrop()) {
            currentOne.moveDrop();
        } else {
            landToWall();
            destroyLine();
            if (isGameOver()) {
                game_state = OVER;
            } else {
                currentOne = nextOne;
                nextOne = Tetromino.randomOne();
            }
        }
    }

    //單元格嵌入wall裡面 ，我不太懂
    private void landToWall() {
        Cell[] cells = currentOne.cells;
        for (Cell cell : cells) {
            int row = cell.getRow();
            int col = cell.getCol();
            wall[row][col] = cell;
        }
    }

    //判斷能否下落
    public boolean camDrop() {
        Cell[] cells = currentOne.cells;
        for (Cell cell : cells) {
            int row = cell.getRow();
            int col = cell.getCol();
            //能不能下落到底部
            if (row == wall.length - 1) {
                return false;
            } else if (wall[row + 1][col] != null) {
                return false;
            }
        }
        return true;
    }

    //消除行
    public void destroyLine() {
        int line = 0;
        Cell[] cells = currentOne.cells;
        for (Cell cell : cells) {
            int row = cell.getRow();
            if (isFullLine(row)) {
                line++;
                for (int i = row; i > 0; i--) {
                    System.arraycopy(wall[i - 1], 0, wall[i], 0, wall[0].length);
                }
                wall[0] = new Cell[9];
            }
        }
        //從score pool的分數加到總分
        totalScore += scores_pool[line];
        //总行数
        totalLine += line;
    }

    //判斷某行是否滿了
    public boolean isFullLine(int row) {
        Cell[] cells = wall[row];
        for (Cell cell : cells) {
            if (cell == null) {
                return false;
            }
        }
        return true;
    }

    //判斷遊戲結束
    public boolean isGameOver() {
        Cell[] cells = nextOne.cells;
        for (Cell cell : cells) {
            int row = cell.getRow();
            int col = cell.getCol();
            if (wall[row][col] != null) {
                return true;
            }
        }
        return false;
    }

    private void paintState(Graphics g) {
        if (game_state == PLING) {
            g.drawString(show_state[PLING], 500, 660);
        } else if (game_state == STOP) {
            g.drawString(show_state[STOP], 500, 660);
        } else {
            g.drawString(show_state[OVER], 500, 660);
            g.setColor(Color.RED);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 60));
            g.drawString("GAME OVER!", 30, 400);
        }
    }

    private void paintSource(Graphics g) {
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
        g.drawString("Score: " + totalScore, 500, 250);
        g.drawString("line: " + totalLine, 500, 430);
    }

    private void paintNextOne(Graphics g) {
        Cell[] cells = nextOne.cells;
        for (Cell cell : cells) {
            int x = cell.getCol() * CELL_SIZE + 370;
            int y = cell.getRow() * CELL_SIZE + 25;
            g.drawImage(cell.getImage(), x, y, null);
        }
    }

    private void paintCurrentOne(Graphics g) {
        Cell[] cells = currentOne.cells;
        for (Cell cell : cells) {
            int x = cell.getCol() * CELL_SIZE;
            int y = cell.getRow() * CELL_SIZE;
            g.drawImage(cell.getImage(), x, y, null);
        }
    }

    private void paintWall(Graphics g) {
        for (int i = 0; i < wall.length; i++) {
            for (int j = 0; j < wall[i].length; j++) {
                int x = j * CELL_SIZE;
                int y = i * CELL_SIZE;
                Cell cell = wall[i][j];
                if (cell == null) {
                    g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
                } else {
                    g.drawImage(cell.getImage(), x, y, null);
                }
            }
        }
    }

    //判斷會不會出界
    public boolean outOFBounds() {
        Cell[] cells = currentOne.cells;
        for (Cell cell : cells) {
            int col = cell.getCol();
            int row = cell.getRow();
            if (row < 0 || row > wall.length - 1 || col < 0 || col > wall[0].length-1) {
                return true;
            }
        }
        return false;
    }

    //左移
    public void moveleftActive() {
        currentOne.moveLeft();
        //判断是否越界或重合
        if (outOFBounds() || coincide()) {
            currentOne.moveRight();
        }
    }

    //右移
    public void moveRightActive() {
        currentOne.moveRight();
        //判断是否越界或重合
        if (outOFBounds() || coincide()) {
            currentOne.moveLeft();
        }
    }

    //判斷是否重合
    public boolean coincide() {
        Cell[] cells = currentOne.cells;
        for (Cell cell : cells) {
            int row = cell.getRow();
            int col = cell.getCol();
            if (wall[row][col] != null) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        JFrame jFrame = new JFrame("NCKU Tetris");
        Tetris panel = new Tetris();
        jFrame.add(panel);
        jFrame.setVisible(true);
        jFrame.setSize(810, 940);//視窗大小
        jFrame.setLocationRelativeTo(null);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        panel.start();
    }
}
