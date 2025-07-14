package org.JohNils;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class IPCServer implements Runnable {
    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(5555)) {
            while (true) {
                try (Socket client = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))) {

                    String line = in.readLine();
                    if (!"show".equalsIgnoreCase(line)) {
                        Main.window.textField.setText(line);
                        Main.window.textField.setCaretPosition(line.length());
                    }
                    Main.window.setVisible(true);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
