package org.JohNils;


import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static Window window;

    public static void main(String[] args) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        if (ge.isHeadlessInstance()) {
            System.err.println("Java AWT reports headless mode! Cannot create GUI.");
            System.exit(1);
        }

        Dimension screen;
        while ((screen = Toolkit.getDefaultToolkit().getScreenSize()).height < 200) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        final Dimension finalScreen = screen;

        SwingUtilities.invokeLater(() -> {
            try {
                Thread.sleep(500); // Give WM a moment to fully initialize
            } catch (InterruptedException ignored) {}
            window = new Window(finalScreen);
        });

        ApplicationResolver.buildCaches();

        new Thread(new IPCServer()).start();

        System.out.println("Done");

        File f = new File("/home/JohNils/test.txt");

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            bw.write("Toolkit: " + Toolkit.getDefaultToolkit().getClass().getName());
            bw.newLine();

            bw.flush();

            bw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}