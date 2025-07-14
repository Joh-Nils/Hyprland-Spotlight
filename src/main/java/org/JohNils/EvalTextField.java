package org.JohNils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

public class EvalTextField extends JTextField {
    private String ghostText = "";

    public EvalTextField() {
    }

    public void setEval(String eval) {
        this.ghostText = eval;
    }

    @Override
    protected void paintComponent(Graphics g) {
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        super.paintComponent(g);


        // Don't draw ghost text if not needed
        if (ghostText.isEmpty()) return;

        // Copy original graphics context and cast to Graphics2D
        Graphics2D g2 = (Graphics2D) g.create();

        // Set light gray color and smaller font
        g2.setColor(Color.LIGHT_GRAY);
        g2.setFont(getFont().deriveFont(Font.ITALIC));

        // Get the width of the actual input text
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(getText());

        // Calculate vertical position
        int y = (getHeight() + fm.getAscent()) / 2 - 2;

        // Draw ghost text after the actual input
        g2.drawString(ghostText, getInsets().left + textWidth + 2, y);

        g2.dispose();
    }
}
