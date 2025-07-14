package org.johnils;


import com.tulskiy.keymaster.common.Provider;

import javax.swing.*;


public class Main {
    public static Window window = new Window();
    public static StringBuilder UserInput = new StringBuilder("asdasd");
    public static HotKeyHandler hkHandler = new HotKeyHandler();

    public static void main(String[] args) {

        Provider.getCurrentProvider(true).register(KeyStroke.getKeyStroke("alt space"), hkHandler);
    }
}