package com.pannous.rbm.graphics;

import com.pannous.rbm.*;
import com.pannous.rbm.minst.MinstDatasetReader;
import com.pannous.rbm.minst.RbmCanvas;
import com.pannous.util.Debugger;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GraphicsRBM extends RbmCanvas {

    DatasetReader dr;
    static int count = 0;
    final LayerFactory layerFactory = new LayerFactory();


    final SimpleRBMTrainer trainer;
    private String label = "?";

    public GraphicsRBM() throws Exception {
        super();
        dr = new GraphicsDatasetReader();
        cols = dr.cols;
        rows = dr.rows;
        rbm = new SimpleRBM(cols * rows, 10 * 10, false, layerFactory);
//        trainer = new SimpleRBMTrainer(0.2f, 0.001f, 0.2f, 0.1f, layerFactory);
        trainer = new SimpleRBMTrainer(0.5f, .01f, 0.5f, 0.3f, layerFactory);
//    trainer = new SimpleRBMTrainer(0.1f, .05f, 0.1f, 0.05f, layerFactory);

    }

    float[] learn() {
        // Get random input
        List<Layer> inputBatch = new ArrayList<Layer>();

        for (int j = 0; j < dr.trainingSet.size(); j++) {
            RbmCanvas.frame.setTitle("item list "+j);
            labeledItem = dr.getTrainingItem(j);
            int length = labeledItem.data.length;
            Layer input = layerFactory.create(length);
            for (int i = 0; i < length; i++) {
                int i1 = labeledItem.data[i] & 255;
                float f = i1 / 256.0f;
                input.set(i, f);
            }
            inputBatch.add(input);
        }

        double error = trainer.learn(rbm, inputBatch, false); //up down
        if (count % 100 == 0)
            System.err.println("Error = " + error + ", Energy = " + rbm.freeEnergy());
        if (count % 100 == 0)iterations++;

        return inputBatch.get(inputBatch.size() - 1).get();
    }

    Iterator<State> evaluate() {
        labeledItem = dr.getTestItem();
        label = labeledItem.label;
        RbmCanvas.frame.setTitle("evaluate "+label);
        Layer input = layerFactory.create(labeledItem.data.length);
        for (int i = 0; i < labeledItem.data.length; i++) {
            int i1 = labeledItem.data[i] & 255;
            float f = i1 / 256.0f;
            input.set(i, f);
        }
        return rbm.iterator(input);
    }

    int iterations = 1;
    public void update() {
        Iterator<State> it = evaluate();
        synchronized (outputs) {
            outputs.clear();//!!
            for (int j = 0; j < iterations; j++) {
            //  feed this system 10 times with its own data
                State t = it.next();
                Layer layer = t.visible;
                visualize(layer);
            }
        }
        repaint();
    }

    public static void start() {

        GraphicsRBM rbm_canvas = null;
        try {
            rbm_canvas = new GraphicsRBM();
        } catch (Exception e) {
            System.err.print(e.getMessage());
        }

        try {
            while (true) {
                if (count++ > 1000) Thread.sleep(2000);
                else Thread.sleep(20);
//                else
                rbm_canvas.learn();
                rbm_canvas.update();
            }
        } catch (Exception e) {
            Debugger.error(e);
        }
    }
}
