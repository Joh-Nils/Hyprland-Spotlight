package org.JohNils;

import java.awt.event.*;

public class MouseHandler implements MouseListener, MouseMotionListener, MouseWheelListener {
    int beforeDragIndex = -1;


    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        beforeDragIndex = mouseEvent.getY() / 74 - 1;
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

        if ((int) (mouseEvent.getY() / 74 - 1) == beforeDragIndex) {
            Eval.runApp(Main.window.index);
        }

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {
        Main.window.index = mouseEvent.getY() / 74 - 1;
        Main.window.refreshIndex();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
        if (Eval.apps == null) return;

        Main.window.index = (Main.window.index + mouseWheelEvent.getWheelRotation()) % Eval.apps.size();
        if (Main.window.index < 0) Main.window.index = (Eval.apps.size() + Main.window.index) % Eval.apps.size();
        Main.window.refreshIndex();
    }
}
