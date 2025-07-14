package org.JohNils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Window extends JFrame {

    public static Dimension screen;

    public MouseHandler mouseHandler;
    public WindowHandler windowHandler;

    public BufferedImage search;
    public EvalTextField textField;

    public JLabel[] Apps = new JLabel[5];

    public int index = 0;

    public Window(Dimension screen) {
        Window.screen = screen;

        try {
            search = ImageIO.read(getClass().getResource("/search.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setLayout(null);
        setSize(new Dimension((int) (screen.getWidth()/5) * 2,74));
        setLocation(screen.width/2 - getWidth()/2,screen.height/5 - getHeight()/2);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);


        JLabel searchIcon = new JLabel();
        searchIcon.setIcon(new ImageIcon(search));
        searchIcon.setBounds(0,1,64,64);

        add(searchIcon);


        textField = new EvalTextField() {

            @Override
            public void paint(Graphics g) {
                ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                super.paint(g);
            }
        };
        textField.setFocusTraversalKeysEnabled(false);
        textField.setFont(new Font("",Font.PLAIN,getHeight() - 35));
        textField.setForeground(new Color(5,5,5));
        textField.setBorder(null);
        textField.setBounds(64 + 5, 10,getWidth() - 64,getHeight() - 20);

        add(textField);


        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateResult();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateResult();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateResult(); // usually not needed for plain text fields
            }

            private void updateResult() {
                evalNew(textField.getText());
            }
        });

        windowHandler = new WindowHandler();
        addWindowListener(windowHandler);

        mouseHandler = new MouseHandler();
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
        addMouseWheelListener(mouseHandler);

        textField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {

            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                    Eval.runApp(index);
                }
                if (keyEvent.getKeyCode() == KeyEvent.VK_TAB) {
                    textField.setText("'" + Eval.apps.get(index)[0]); //TODO: change ' based on .properties
                }
                if (keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {
                    index = (index + 1) % Eval.apps.size();

                    refreshIndex();
                }

                if (keyEvent.getKeyCode() == KeyEvent.VK_UP) {
                    index--;
                    if (index < 0) index = (Eval.apps.size() + index) % Eval.apps.size();

                    refreshIndex();
                }

            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    setVisible(false);
                    getWindowListeners()[0].windowClosing(null);
                }
            }
        });


        for (int i = 0; i < Apps.length; i++) {
            JLabel label = getJLabel(i);

            add(label);
            Apps[i] = label;
        }
    }

    private JLabel getJLabel(int i) {
        JLabel label = new JLabel() {
            @Override
            public void paint(Graphics g) {
                ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                super.paint(g);
            }
        };
        label.setOpaque(true);
        if (i == 0)
            label.setBackground(new Color(200,200,200));
        else
            label.setBackground(Color.white);

        label.setBounds(0,74 * (i + 1), getWidth(),74);
        label.setFont(new Font("",Font.PLAIN,39));
        return label;
    }

    public void reCenter() {
        setLocation(screen.width/2 - getWidth()/2,screen.height/5 - 74/2);
    }

    public void reSize(int width, int height) {
        setSize(new Dimension(width < 0 ? getWidth() : width, height < 0 ? getHeight() : height));

        reCenter();
    }

    public void evalNew(String text) {
        Eval.evaluate(text);
    }

    public void refreshIndex() {

        int yIndex = index / 5;
        int y = yIndex * 5;

        if (Eval.apps != null && !Eval.apps.get(y)[0].equals(Apps[0].getText())) {

            for (int i = 0;i < Apps.length;i++) {
                if (i >= Eval.apps.size()) {
                    Main.window.Apps[i].setVisible(false);
                    continue;
                }

                Apps[i].setText(Eval.apps.get(y + i)[0]);
                Apps[i].setIcon(ApplicationResolver.resolveIcon(Eval.apps.get(y + i)[1]));


                if (i == index % 5) {
                    Apps[i].setBackground(new Color(200,200,200));
                    Main.window.Apps[i].setVisible(true);
                    continue;
                }

                Apps[i].setBackground(Color.white);
                Main.window.Apps[i].setVisible(true);
            }

            return;
        }


        for (int i = 0;i < Apps.length;i++) {
            if (i == index % 5) {
                Apps[i].setBackground(new Color(200,200,200));
                continue;
            }

            Apps[i].setBackground(Color.white);
        }

    }

}
