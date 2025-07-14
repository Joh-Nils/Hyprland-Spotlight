package org.JohNils;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Eval {
    public static List<String[]> apps;


    public static void evaluate(String value) {

        char method = check(value);

        apps = null;

        switch (method) {
            case 'M' -> {
                Main.window.textField.setEval("");
                JMath.eval(value);
                Main.window.repaint();
            }

            case 'A' -> {
                if (value.substring(1).isBlank()) {
                    for (int i = 0;i < Main.window.Apps.length;i++) {
                        Main.window.Apps[i].setVisible(false);
                    }
                    Main.window.reSize(-1, 74);
                    return;
                }

                apps = ApplicationResolver.resolveApplication(value.substring(1).split(":")[0]);

                Main.window.reSize(-1, (Math.min(apps.size(), 5) + 1) * 74);

                Main.window.index = 0;

                for (int i = 0;i < Main.window.Apps.length;i++) {
                    if (i >= apps.size()) {
                        Main.window.Apps[i].setVisible(false);
                        continue;
                    }

                    Main.window.Apps[i].setText(apps.get(i)[0]);
                    Main.window.Apps[i].setIcon(ApplicationResolver.resolveIcon(apps.get(i)[1]));


                    if (i == Main.window.index % 5) {
                        Main.window.Apps[i].setBackground(new Color(200,200,200));
                        Main.window.Apps[i].setVisible(true);
                        continue;
                    }

                    Main.window.Apps[i].setBackground(Color.white);
                    Main.window.Apps[i].setVisible(true);
                }

            }

            case ' ' -> {
                for (JLabel app: Main.window.Apps) {
                    app.setVisible(false);
                }

                Main.window.reSize(-1, 74);
                Main.window.textField.setEval("");
                Main.window.repaint();
            }
        }

    }

    public static void runApp(int index) {
        if (apps == null || apps.size() <= index) return;

        String[] App = apps.get(index);

        String[] RawCMD = App[2].replaceAll(" %\\w", "").split(" ");
        String[] UserRawCMD = Main.window.textField.getText().split(":");

        String[] UserCMD = new String[0];

        if (UserRawCMD.length >= 2) {
            UserCMD = UserRawCMD[1].split(" ");
        }

        String[] cmd = new String[RawCMD.length + UserCMD.length];

        for (int i = 0; i < cmd.length; i++) {
            cmd[i] = i < RawCMD.length ? RawCMD[i] : UserCMD[i - RawCMD.length];
        }

        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Main.window.setVisible(false);
        Main.window.getWindowListeners()[0].windowClosing(null);
    }

    private static char check(String value) {
        char ret = ' ';

        if (JMath.containsMath(value)) ret = 'M';

        if (value.startsWith("'")) ret = 'A';


        return ret;
    }


}
