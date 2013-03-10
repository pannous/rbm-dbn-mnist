package com.pannous.rbm.minst;

import com.pannous.rbm.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MinstRBM extends RbmCanvas {

    DatasetReader dr;
    static int count = 0;
    //    final SimpleRBM rbm;
//    List<int[]> outputs = new ArrayList<int[]>();
    final LayerFactory layerFactory = new LayerFactory();


    final SimpleRBMTrainer trainer;
    private String label = "?";

    public MinstRBM(File labels, File images) throws Exception {
        super();
        dr = new MinstDatasetReader(labels, images);
        cols = dr.cols;
        rows = dr.rows;
        rbm = new SimpleRBM(cols * rows, 10 * 10, false, layerFactory);
        trainer = new SimpleRBMTrainer(0.2f, 0.001f, 0.2f, 0.1f, layerFactory);
    }

    float[] learn() throws Exception {
        // Get random input
        List<Layer> inputBatch = new ArrayList<Layer>();

        for (int j = 0; j < 30; j++) {
            labeledItem = dr.getTrainingItem();
            Layer input = layerFactory.create(labeledItem.data.length);

            for (int i = 0; i < labeledItem.data.length; i++)
                input.set(i, labeledItem.data[i] / 255.0f);// NORM TO 0..1 !!
            inputBatch.add(input);
        }

        double error = trainer.learn(rbm, inputBatch, false); //up down
        if (count % 100 == 0)
            System.err.println("Error = " + error + ", Energy = " + rbm.freeEnergy());

        return inputBatch.get(inputBatch.size() - 1).get();
    }

    Iterator<State> evaluate() {
        LabeledItem test = dr.getTestItem();
        label = test.label;
        Layer input = layerFactory.create(test.data.length);
        for (int i = 0; i < labeledItem.data.length; i++)
            input.set(i, labeledItem.data[i]);
        return rbm.iterator(input);
    }

    public void update() {
        Iterator<State> it = evaluate();
        synchronized (outputs) {
            outputs.clear();//!!
            //  feed this system 10 times with its own data
            for (int j = 0; j < 10; j++) {
                State t = it.next();
                Layer layer = t.visible;
                visualize(layer);
            }
        }
        repaint();
    }

    public static void start(File labels, File images) {

        MinstRBM rbm_canvas = null;
        try {
            rbm_canvas = new MinstRBM(labels, images);
        } catch (Exception e) {
            System.err.print(e.getMessage());
        }

        try {
            while (true) {
                if (count++ > 1000) Thread.sleep(2000);
//                else
                rbm_canvas.learn();
                rbm_canvas.update();
            }
        } catch (InterruptedException e) {
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
