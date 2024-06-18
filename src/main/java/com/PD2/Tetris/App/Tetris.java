package com.PD2.Tetris.App;

import com.PD2.Tetris.block.*;
import com.PD2.Tetris.shape.*;

//import com.PD2.Tetris.Game.GameController;
import com.PD2.Tetris.block.Tetromino;

import com.PD2.Tetris.Game.*;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Font;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.util.TimerTask;
import java.util.Timer;



public class Tetris extends JPanel implements  KeyListener{
    private Timer timer;
    private final int delay = 500; 
    private boolean isPaused;
    private boolean isOver=false;
    private Tetromino currentTetromino;
    private Tetromino nextTetromino; 

    public static  Wall wall;

    private Tetromino holdTetromino;
    private boolean holdUsed;
    //private Wall wall;
    private JFrame gameFrame; 
    private scoreEstimate scoreManager; 
    Tetris(){
        nextTetromino = Tetromino.random(); 
        //wall=new Wall();
        gameFrame=new JFrame("NCKU Tetris");
        timer=new Timer();
        isPaused=false;
        setFocusable(true);
        scoreManager =new scoreEstimate();
        setFocusTraversalKeysEnabled(false);
        currentTetromino=Tetromino.random();
        addKeyListener(this);
    }

    // loading pictures
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
            wall=new Wall();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void holdCurrentTetromino() {
        if (!holdUsed) {
            if (holdTetromino == null) {
                holdTetromino = currentTetromino;
                spawnNewTetromino();
            } else {
                Tetromino temp = currentTetromino;
                currentTetromino = holdTetromino;
                holdTetromino = temp;
            }
            holdUsed = true;
            repaint();
        }
    }
    public void endGame() {
        // game over
        System.out.println("Game Over");
        gameFrame.dispose(); 

        End_Menu end_menu=new End_Menu(scoreManager.getTotalScore()); // 顯示結束
        end_menu.play_again_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println("game start!!!");
                end_menu.end_frame.dispose();
                Tetris panel=new Tetris();  //start button new 出一個新game_frame
                JFrame game_frame=panel.gameFrame;
                game_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


                //GameController gameController = new GameController(game_frame);
                game_frame.setSize(810, 940);
                game_frame.add(panel);
                //game_frame.add(gameController);
                game_frame.setVisible(true);
                wall=new Wall();    //清空方塊

                panel.start();

            }
        });
        end_menu.Menu_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                end_menu.end_frame.dispose();
                Menu menu = new Menu(); //new 出新的menu
                menu.start_button.addActionListener(new ActionListener() {  //再覆寫一次menu start button
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        System.out.println("game start!!!");
                        menu.frame.dispose();
                        Tetris panel=new Tetris();  //start button new 出一個新game_frame
                        //JFrame game_frame = new JFrame("NCKU Tetris");
                        JFrame game_frame=panel.gameFrame;
                        game_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


                        //GameController gameController = new GameController(game_frame);
                        game_frame.setSize(810, 940);
                        game_frame.add(panel);
                        //game_frame.add(gameController);
                        game_frame.setVisible(true);
                        wall=new Wall();    //清空方塊
                        panel.start();
                    }
                });
            }
        });
        //isOver=true;
    }
    public void spawnNewTetromino() {

//        if (wall.add(currentTetromino) == Wall.LOSE) {
//            currentTetromino=null;
//            endGame();
//        }
//        else {
            currentTetromino = nextTetromino; 
            nextTetromino = Tetromino.random(); 
            repaint();
//        }

    }
    public void dropCurrentTetromino() {
        int check_boundary=0;
        if (currentTetromino != null) {
            //System.out.println(currentTetromino.coincide());
            check_boundary=currentTetromino.moveDown();     //因為movedown裡有coincide()-->moveup()，所以一旦撞到方塊或邊界，會先moveup()再進行line_104的判斷，currentTetromino.coincide永遠不會為true
            System.out.println("dropcurrent");
            if (check_boundary==1) {
                check_boundary=0;
                System.out.println("reach buttom");
                int linesCleared = wall.add(currentTetromino);
                if (linesCleared > 0) {
                    scoreManager.updateScore(linesCleared, 0); // 更新分數
                    spawnNewTetromino();
                    System.out.println("score:"+scoreManager.getTotalScore());


                }
                else if(linesCleared ==Wall.LOSE){
                    currentTetromino=null;
                    endGame();
                }
                else {
                    holdUsed = false;

                    spawnNewTetromino();
                }


            }

            repaint();
        }
    }
    @Override
    public void paintComponent(Graphics g) {
        //super.paintComponent(g);
        wall.paint(g);
        if (currentTetromino != null) {
            currentTetromino.paint(g);
            //System.out.println("paintcompo");
        }
        drawNextTetromino(g); 
    }

    public void drawNextTetromino(Graphics g) {
        if (nextTetromino != null) {
            int offsetX = 400; 
            int offsetY = 50;
            int[][] blockPositions = nextTetromino.getBlockPositions();
            BufferedImage image = nextTetromino.getImage();
            for (int[] position : blockPositions) {
                int x = position[0] * Cell.SIZE + offsetX;
                int y = position[1] * Cell.SIZE + offsetY;
                g.drawImage(image, x, y, null);
            }
        }
    }
    private void paintSource(Graphics g) {
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 40));
        g.drawString("score: " + scoreManager.getTotalScore(), 490, 250);
        g.drawString("total lines:"  + scoreManager.getTotalLine(), 490, 430);
    }

    public void start() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!isPaused && !isOver) {
                    //System.out.println("sssttart");
                    dropCurrentTetromino();
                }
            }
        }, 0, delay);

        spawnNewTetromino();
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(background, 0, 0, null);
        // 平移坐标轴
        g.translate(22, 15);
        paintComponent(g);
        drawNextTetromino(g);
        paintSource(g);

        //System.out.println("draw");
    }
    //++++++++++++++++++++++++++++++++++++++++++
    public void moveCurrentTetrominoLeft() {
        if (currentTetromino != null) {
            currentTetromino.moveLeft();
            repaint();
        }
    }

    public void moveCurrentTetrominoRight() {
        if (currentTetromino != null) {
            currentTetromino.moveRight();
            repaint();
        }
    }

    public void rotateCurrentTetromino() {
        if (currentTetromino != null) {
            currentTetromino.rotate();
            repaint();
        }
    }

    public void pause() {
        isPaused = true;
        timer.cancel();
    }

    public void resume() {
        isPaused = false;
        timer = new Timer();
        start();
    }






    public static void main(String[] args) {
        Menu menu = new Menu();
        menu.start_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println("game start!!!");
                menu.frame.dispose();
                Tetris panel=new Tetris();
                //JFrame game_frame = new JFrame("NCKU Tetris");
                JFrame game_frame=panel.gameFrame;
                game_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


                //GameController gameController = new GameController(game_frame);
                game_frame.setSize(810, 940);
                game_frame.add(panel);
                //game_frame.add(gameController);
                game_frame.setVisible(true);
                panel.start();
            }
        });
    }


    @Override
    public void keyTyped(KeyEvent keyEvent) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        switch (keyCode) {
            case KeyEvent.VK_LEFT:
                moveCurrentTetrominoLeft();
                break;
            case KeyEvent.VK_RIGHT:
                moveCurrentTetrominoRight();
                break;
            case KeyEvent.VK_DOWN:
                dropCurrentTetromino();
                break;
            case KeyEvent.VK_UP:
                rotateCurrentTetromino();
                break;
            case KeyEvent.VK_SPACE:
                // 硬降代码
                //hardDropCurrentTetromino();
                break;
            case KeyEvent.VK_C:
                holdCurrentTetromino();
                break;
            case KeyEvent.VK_P:
                if (isPaused) {
                    resume();
                } else {
                    pause();
                }
                break;
            case KeyEvent.VK_Q:
                System.exit(0);
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {}
}
//我有改的地方
//修正Tetris line 102
//
//Tetromino coincide add "||y==18"
//WALL HEIGHT =19 FROM 20
//WAll line 51 變為HHEIGHT-2
//Tetromino line 84:boundary
//wall line7  width =9 from 10
//Tetris 41 game_frame 改在class 裡面new 出來 這樣endGame()才能吃到game_frame
//Tetris line 98~109 endGame時應該要把currentTetromino設為null，才能終止dropCurrentTetromino()的loop，以至於不會一直new出end_menu
//Tetris line 99  line 124 :避免add呼叫兩次，setimage 兩次
//Tetris line 124 一觸底時就要設置spawnNewTetromino();

//Tetris line 96,End_Menu line 16,End_Menu add constructor : pass in a score and build the
//Tetris line 92 ~146 play_again,再new一個game_frame,一定要新增一個new wall 把圖片區清空，不然會一直game over，Menu 回到主選單 再new一次Menu 再重新override一次Menu 選項裡的事件
//Tetris line 190 line219 :印出總分和總行數
