package com.pannous.rbm.graphics;

import com.pannous.net.Sheet;
import com.pannous.rbm.*;
import com.pannous.rbm.minst.RbmCanvas;
import com.pannous.util.Debugger;

import java.util.ArrayList;
import java.util.List;

public class GraphicsKDM extends RbmCanvas {

    DatasetReader dr;
    static int count = 0;
    final LayerFactory layerFactory = new LayerFactory();

    final LayerTrainer trainer;
    private String label = "?";

    public GraphicsKDM() throws Exception {
        super();
        dr = new GraphicsDatasetReader();
        cols = dr.cols;
        rows = dr.rows;
        rbm = new Sheet();
        trainer = new LayerTrainer();
//        trainer = new SimpleRBMTrainer(0.5f, 0.01f, 0.5f, 0.3f, layerFactory);
    }

    float[] learn() {
        // Get random input
        List<Layer> inputBatch = new ArrayList<Layer>();

        for (int j = 0; j < dr.trainingSet.size(); j++) {
            RbmCanvas.frame.setTitle("item list "+j);
            labeledItem = dr.getTrainingItem(j);
            int length = labeledItem.data.length;
            Layer input = layerFactory.create(length);
            for (int i = 0; i < length; i++)
                input.set(i, labeledItem.data[i]%255 / 256.0f);// NORM TO 0..1 !!
            inputBatch.add(input);
        }

        double error = trainer.learn(rbm, inputBatch, false); //up down
        if (count % 100 == 0)
            System.err.println("Error = " + error + ", Energy = " + rbm.freeEnergy());

        return inputBatch.get(inputBatch.size() - 1).get();
    }

    State evaluate() {
        labeledItem = dr.getTestItem();
        label = labeledItem.label;
        RbmCanvas.frame.setTitle("evaluate "+label);
        Layer input = layerFactory.create(labeledItem.data.length);
        for (int i = 0; i < labeledItem.data.length; i++)
            input.set(i, labeledItem.data[i]%255 / 256.0f);
        return evaluate(input);
    }

    private State evaluate(Layer input) {
        return null;
//        new State(input,rbm.;
    }

    public void update() {
        State visible=evaluate();
        synchronized (outputs) {
            outputs.clear();//!!
            //  feed this system 10 times with its own data
            for (int j = 0; j < 10; j++) {
//                State t =
//                Layer layer = t.visible;
//                visualize(layer);
            }
        }
        repaint();
    }

    public static void start() {

        GraphicsKDM rbm_canvas = null;
        try {
            rbm_canvas = new GraphicsKDM();
        } catch (Exception e) {
            System.err.print(e.getMessage());
        }

        try {
            while (true) {
                if (count++ > 1000) Thread.sleep(2000);
//                else Thread.sleep(500);
//                else
                rbm_canvas.learn();
                rbm_canvas.update();
            }
        } catch (Exception e) {
            Debugger.error(e);
        }
    }
}
