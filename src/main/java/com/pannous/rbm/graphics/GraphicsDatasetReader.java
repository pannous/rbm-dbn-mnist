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
    public GraphicsDatasetReader() {
        cols = 28;
        rows = 28;
        nextElement();
    }

    @Override
    public LabeledItem nextElement() {
//        RbmCanvas canvas = new RbmCanvas();
        BufferedImage image = new BufferedImage(cols, rows,BufferedImage.TYPE_BYTE_GRAY);
        Graphics graphics = image.getGraphics();
        graphics.drawString("A",0,0);
        LabeledItem item = new LabeledItem();
        item.label = "A";
        item.data = new int[rows * cols];
        image.getRaster().getDataElements(0,0,rows, cols, item.data);
        List<LabeledItem> list =new Vector<LabeledItem>();
        list.add(item);
        trainingSet.put("A",list);
        return item;
    }
}
