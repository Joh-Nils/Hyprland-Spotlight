package org.johnils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Window extends JFrame {

    public static Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

    public Panel panel;
    public KeyHandler kHandler;

    public BufferedImage search;
    public JTextField textField;

    public JTextPane out;

    public Window() {
        try {
            search = ImageIO.read(getClass().getResource("/search.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setLayout(null);
        setSize(700,74);
        setLocation(screen.width/2 - getWidth()/2,screen.height/4 - getHeight()/2);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);


        //panel = new Panel(getWidth(), getHeight());
        //add(panel);
        //panel.setSize(getWidth(),getHeight());
        //panel.setVisible(true);

        JLabel searchIcon = new JLabel();
        searchIcon.setIcon(new ImageIcon(search));
        searchIcon.setBounds(0,1,64,64);

        add(searchIcon);


        out = new JTextPane() {

            @Override
            public void paint(Graphics g) {
                ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                super.paint(g);
            }
        };
        out.setFont(new Font("",Font.PLAIN,getHeight() - 35));
        out.setForeground(new Color(25,25,25));
        out.setBorder(null);
        out.setBounds(64 + 5, 10,getWidth() - 64,getHeight() - 20);
        out.setVisible(true);
        out.setFocusable(false);
        add(out);

        textField = new JTextField() {

            @Override
            public void paint(Graphics g) {
                ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                super.paint(g);
            }
        };
        textField.setFont(new Font("",Font.PLAIN,getHeight() - 35));
        textField.setForeground(new Color(5,5,5));
        textField.setBorder(null);
        textField.setBounds(64 + 5, 10,getWidth() - 64,getHeight() - 20);

        add(textField);


        textField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {

            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                evalNew();
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {

            }
        });


        kHandler = new KeyHandler();
        addKeyListener(kHandler);
    }

    public void reCenter() {
        setLocation(screen.width/2 - getWidth()/2,screen.height/4 - getHeight()/2);
    }

    public void reSize(int width, int height) {
        setSize(width,height);
        panel.setSize(width,height);

        reCenter();
    }

    public void evalNew() {
        Eval.evaluate();
    }

}
