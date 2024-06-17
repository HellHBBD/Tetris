package com.PD2.Tetris.App;

import com.PD2.Tetris.block.*;
import com.PD2.Tetris.shape.*;
import com.PD2.Tetris.Game.*;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;


public class Tetris {
    // loading pictures
    public static BufferedImage I;
    public static BufferedImage J;
    public static BufferedImage L;
    public static BufferedImage O;
    public static BufferedImage S;
    public static BufferedImage T;
    public static BufferedImage Z;
    public static BufferedImage background;
    public static Wall wall;

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
			wall = new Wall();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
    }

    public static void main(String[] args) {
		Menu menu = new Menu();
        menu.start_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println("game start!!!");
                menu.frame.dispose();
                JFrame game_frame = new JFrame("NCKU Tetris");
                game_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				Demo panel = new Demo();
                GameController gameController = new GameController(game_frame);
                game_frame.setSize(810, 940);
                game_frame.add(panel);
                //game_frame.add(gameController);
                game_frame.setVisible(true);
                panel.start();
            }
        });
    }
}
