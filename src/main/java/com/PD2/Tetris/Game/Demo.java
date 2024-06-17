package com.PD2.Tetris.Game;

import com.PD2.Tetris.block.*;
import com.PD2.Tetris.shape.*;
import com.PD2.Tetris.Game.*;
import com.PD2.Tetris.App.*;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class Demo extends JPanel {
	private Tetromino currentOne = Tetromino.random();
    //将要下落的方块
    // private Tetromino nextOne = Tetromino.random();
	// int[] scores_pool = {0, 1, 2, 5, 10};
    //当前游戏的分数
    // private int totalScore = 0;
    //当前消除的行数
    // private int totalLine = 0;
	public static final int PLING = 0;
    public static final int STOP = 1;
    public static final int OVER = 2;
	private final int delay = 200;
    //当前游戏状态值
    private int game_state;
    //显示游戏状态
    String[] show_state = {"P[pause]", "C[continue]", "S[replay]"};
	private Timer timer;

	public Demo() {
		timer = new Timer();
	}

	@Override
    public void paint(Graphics g) {
        g.drawImage(Tetris.background, 0, 0, null);
        //平移坐标轴
        g.translate(22, 15);
        //绘制游戏主区域
		Tetris.wall.paint(g);
        //绘制正在下落的四方格
        currentOne.paint(g);
        //绘制下一个将要下落的四方格
        // paintNextOne(g);
        //绘制游戏得分
        // paintSource(g);
        //绘制当前游戏状态
        // paintState(g);
    }

	public void game() {
		game_state = PLING;
        KeyListener l = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                switch (code) {
                    case KeyEvent.VK_DOWN:
                        currentOne.moveDown();
                        break;
                    case KeyEvent.VK_LEFT:
                        currentOne.moveLeft();
                        break;
                    case KeyEvent.VK_RIGHT:
                        currentOne.moveRight();
                        break;
                    case KeyEvent.VK_UP:
                        currentOne.rotate();
                        break;
                    // case KeyEvent.VK_SPACE:
                    //         hadnDropActive();
                    //     break;
                    // case KeyEvent.VK_P:
                    //     //判断当前游戏状态
                    //     if (game_state == PLING) {
                    //         game_state = STOP;
                    //     }
                    //     break;
                    // case KeyEvent.VK_C:
                    //     if (game_state == STOP) {
                    //         game_state = PLING;
                    //     }
                    //     break;
                    // case KeyEvent.VK_S:
                    //     //重新开始
                    //     game_state = PLING;
                    //     wall = new Cell[18][9];
                    //     currentOne = Tetromino.random();
                    //     nextOne = Tetromino.random();
                    //     totalScore = 0;
                    //     totalLine = 0;
                    //     break;
                }
            }
		};
		//将窗口设置为焦点
        this.addKeyListener(l);
        this.requestFocus();

        while (true) {
            if (game_state == PLING) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
				currentOne.moveDown();
				int signal = Tetris.wall.add(currentOne);
				if (signal == Wall.FAIL) {
					continue;
				}
				else if (signal == Wall.LOSE) {
					game_state = OVER;
				}
				else {
					System.out.println("eliminate " + signal + " lines");
					currentOne = Tetromino.random();
				}
            }
            repaint();
        }
	}

	public void start() {
		timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
				game();
            }
        }, 0, delay);
	}
}
