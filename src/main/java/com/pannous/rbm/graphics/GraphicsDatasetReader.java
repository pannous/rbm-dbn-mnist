package com.pannous.rbm.graphics;

import com.pannous.rbm.DatasetReader;
import com.pannous.rbm.LabeledItem;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

/**
 * Copyright 2013 Pannous GmbH
 * <p/>
 * User: info@pannous.com / me
 * Date: 3/10/13
 * Time: 9:16 PM
 */
public class GraphicsDatasetReader extends DatasetReader {
    private Graphics graphics;
    private BufferedImage image;

    public GraphicsDatasetReader() {
        cols = 28;
        rows = 28;
        fillSet();
//        nextElement();
    }

    private void fillSet() {
        image = new BufferedImage(cols, rows, BufferedImage.TYPE_INT_RGB);
        graphics = image.getGraphics();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontNames = ge.getAvailableFontFamilyNames();
        Font font = Font.decode("Helvetica 30");
        byte[] bytes = "1234567890".getBytes();// abcdefghij
        graphics.setColor(Color.BLUE);
        graphics.setFont(font);
        int examples = 30;
        for (byte b : bytes) {
            String letter = "" + (char) b;
            List<LabeledItem> list = new Vector<LabeledItem>();
            for (int i = 0; i < examples; i++) {
                list.add(nextElement(letter, i));
            }
//            if (random.nextDouble() > 0.3)
                testSet.put(letter, list);
//            else
                trainingSet.put(letter, list);
        }
        count = trainingSet.size() * examples;
    }

    private LabeledItem nextElement(String letter, int i) {


//        for (int index = 0; index < fontNames.length; index++){
//            Font font = Font.decode("Helvetica 20");
//            System.out.println( Font.decode( fontNames[index] ) );
//        }
//        graphics.fillRect(5,5,10,10);
        LabeledItem item = new LabeledItem();
        item.label = letter;
//                graphics.drawString(letter, 2, 26);
//        graphics.setColor(Color.BLACK);
//        graphics.fillRect(0, 0, cols, rows);
        graphics.clearRect(0, 0, cols, rows);
//        graphics.drawString(letter, random.nextInt(2)+2, 26 - random.nextInt(2));
        graphics.drawString(letter, random.nextInt(8)+2, 26 - random.nextInt(6));
        graphics.drawLine(random.nextInt(28), random.nextInt(28), random.nextInt(28), random.nextInt(28));
        graphics.drawLine(random.nextInt(28), random.nextInt(28), random.nextInt(28), random.nextInt(28));
        item.data = new int[rows * cols];
//        Arrays.fill(item.data,0);
        image.getRaster().getDataElements(0, 0, rows, cols, item.data);
        for (int j = 0; j < item.data.length; j++) {
                item.data[j]=item.data[j] & 255;
        }
        return item;
    }

    @Override
    public LabeledItem nextElement() {
        current++;
        return nextElement("a", 0);
    }
}
