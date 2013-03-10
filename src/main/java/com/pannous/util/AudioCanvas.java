package com.pannous.util;

import javax.swing.*;
import java.awt.*;

/**
 * Copyright 2013 Pannous GmbH
 * <p/>
 * User: info@pannous.com / me
 * Date: 3/9/13
 * Time: 4:55 PM
 */
public class AudioCanvas extends Canvas {
        int width = 1024;
        int height = 400;
        final JFrame frame;

    public AudioCanvas() {
        frame = new JFrame("MINST Draw!");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width, height);
        this.setSize(width, height);
        this.setBackground(Color.white);
        frame.add(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        int[] xs = new int[width];
        int[] ys = new int[width];
        int[] spikes = AudioCapture.spikes;
        if(spikes.length<xs.length)
            return;
        for (int i = 0; i < xs.length; i++) {
            xs[i]=i;
        }
        for (int i = 0; i < ys.length; i++) {
            int j = 4 * i;
            ys[i] = spikes[j] + spikes[j + 1] + spikes[j + 2] + spikes[j + 3];
        }
        g.setColor(Color.blue);
        g.drawPolygon(xs,ys,xs.length);
    }

}

