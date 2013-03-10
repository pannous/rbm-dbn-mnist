package com.pannous.rbm.minst;

import com.pannous.rbm.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.*;

/**
 * Copyright 2013 Pannous GmbH
 * <p/>
 * User: info@pannous.com / me
 * Date: 3/8/13
 * Time: 12:02 PM
 */
public class RbmCanvas extends Canvas {
    static int border = 10; // 10px
    public java.util.List<int[]> outputs = new ArrayList<int[]>();

    public LabeledItem labeledItem = null;
    public int cols;
    public int rows;
    public SimpleRBM rbm;
    final JFrame frame;

    protected RbmCanvas() {
        frame = new JFrame("MINST Draw!");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        this.setSize(1024, 768);
        frame.add(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }



    public void visualize(Layer layer) {
        synchronized (outputs) {
        int[] output = new int[cols*rows];// layer.size()];
        float[] visible = layer.layer;

        for (int i = 0; i < visible.length; i++) {
            output[i] = (int) (visible[i]*255);
        }
        outputs.add(output);
        }
    }


    public void paint(Graphics g) {
        if(cols==0)return;// not ready yet

        BufferedImage in = new BufferedImage(cols, rows, BufferedImage.TYPE_INT_RGB);

        WritableRaster r = in.getRaster();
        r.setDataElements(0, 0, cols, rows, labeledItem.data);
        g.drawImage(in, border, border, null);
        int offset = border;

        synchronized (outputs) {
            drawOutputs(r, g);
        }

            int buf = 28 + border + border;
            for (int i = 0; i < rbm.weights.length; i++) {
                if (i % 10 == 0) {
                    offset = border;
                    buf += border + 56;
                }

                int[] start = new int[cols * rows];
                for (int j = 0; j < start.length; j++)
                    start[j] = rbm.weights[i].get(j) > 0 ? (Math.round(rbm.weights[i].get(j) * 255)) << 8 : ((Math.round(Math.abs(rbm.weights[i].get(j)) * 255)) << 16);

                BufferedImage out = new BufferedImage(cols, rows, BufferedImage.TYPE_INT_RGB);

                r = out.getRaster();
                r.setDataElements(0, 0, cols, rows, start);

                //Resize
                BufferedImage newImage = new BufferedImage(56, 56, BufferedImage.TYPE_INT_RGB);

                Graphics2D g2 = newImage.createGraphics();
                try {
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                    g2.clearRect(0, 0, 56, 56);
                    g2.drawImage(out, 0, 0, 56, 56, null);
                } finally {
                    g2.dispose();
                }
                g.drawImage(newImage, buf, offset, null);

                offset += border + rows * 2;
            }
        }

    private void drawOutputs(WritableRaster r, Graphics g) {
        int offset = border;
        for (int[] output : outputs) {
            BufferedImage out = new BufferedImage(cols, rows, BufferedImage.TYPE_INT_RGB);

            r = out.getRaster();
            r.setDataElements(0, 0, cols, rows, output);

            //Resize
            BufferedImage newImage = new BufferedImage(56, 56, BufferedImage.TYPE_INT_RGB);

            Graphics2D g2 = newImage.createGraphics();
            try {
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2.clearRect(0, 0, 56, 56);
                g2.drawImage(out, 0, 0, 56, 56, null);
            } finally {
                g2.dispose();
            }
            g.drawImage(newImage, border * 2 + 28, offset, null);

            offset += border + rows * 2;

        }
}

}
