package com.pannous.rbm.graphics;


import com.pannous.rbm.*;
import com.pannous.rbm.minst.RbmCanvas;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GraphicsDBM extends RbmCanvas{
    static DatasetReader datasetReader;
    StackedRBM stackedRBM;
    final StackedRBMTrainer trainer;
    final LayerFactory layerFactory = new LayerFactory();
    private int iterations=2;// CD2

    public GraphicsDBM() throws Exception {
        datasetReader = new GraphicsDatasetReader();
        rows = datasetReader.rows;
        cols = datasetReader.cols;

        stackedRBM = new StackedRBM();
        trainer = new StackedRBMTrainer(stackedRBM, 0.5f, 0.001f, 0.2f, 0.2f, layerFactory);
    }

    void learn(int iterations, boolean addLabels, int stopAt) {
        rbm = stackedRBM.getInnerRBMs().get(0);

        for (int p = 0; p < iterations; p++) {

            // Get random input
            List<Layer> inputBatch = new ArrayList<Layer>();
            List<Layer> labelBatch = addLabels ? new ArrayList<Layer>() : null;


            for (int j = 0; j < 30; j++) {
                labeledItem = datasetReader.getTrainingItem();
                Layer input = layerFactory.create(labeledItem.data.length);

                for (int i = 0; i < labeledItem.data.length; i++)
                    input.set(i, labeledItem.data[i] % 255 / 255.0f);// Norm grayscales to [0.0,1.0]

                inputBatch.add(input);

                if (addLabels) {
                    float[] labelInput = new float[10];
                    labelInput[Integer.valueOf(labeledItem.label)] = 1.0f;
                    labelBatch.add(layerFactory.create(labelInput));
                }
            }
                repaint();

            double error = trainer.learn(inputBatch, labelBatch, stopAt);

//            if (p % 100 == 0)
            System.err.println("Iteration " + p + ", Error = " + error + ", Energy = " + stackedRBM.freeEnergy());
        }
    }

    Iterator<State> evaluate(LabeledItem test) {

        Layer input = layerFactory.create(test.data.length);

        for (int i = 0; i < test.data.length; i++)
            input.set(i, test.data[i]/255.0f);

        int stackNum = stackedRBM.getInnerRBMs().size();

        rbm = stackedRBM.getInnerRBMs().get(0);
        for (int i = 0; i < stackNum; i++) {

            SimpleRBM iRBM = stackedRBM.getInnerRBMs().get(i);

            if (iRBM.biasVisible.size() > input.size()) {
                Layer newInput = new Layer(iRBM.biasVisible.size());

                System.arraycopy(input.get(), 0, newInput.get(), 0, input.size());
                for (int j = input.size(); j < newInput.size(); j++)
                    newInput.set(j, 0.1f);

                input = newInput;
            }

            if (i == (stackNum - 1)) {
                return iRBM.iterator(input);
            }

            input = iRBM.activateHidden(input, null);
        }

        return null;
    }



    public static void start() {
        GraphicsDBM dbn = null;
        try {
            dbn = new GraphicsDBM();
        } catch (Exception e) {
            System.err.print(e.getMessage());
        }

        File saveto = new File("/tmp/GraphicsDBM.net");
        if (saveto.exists()){
            System.out.println("LOADING NET FROM "+saveto);
            dbn.loadPreviousState(saveto);// !! ++
        }
        else {
            int numIterations = 1000;

            dbn.stackedRBM.setLayerFactory(dbn.layerFactory).addLayer(datasetReader.rows * datasetReader.cols, false).addLayer(500, false).addLayer(500, false).addLayer(2000, false).withCustomInput(510).build();

            System.err.println("Training level 1");
            dbn.learn(numIterations, false, 1);
            System.err.println("Training level 2");
            dbn.learn(numIterations, false, 2);
            System.err.println("Training level 3");
            dbn.learn(numIterations, true, 3);

            try {
                System.out.println("SAVING!");
                DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(saveto)));
                dbn.stackedRBM.save(out);

                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        while (true) {
            try {
                dbn.update();
                Thread.sleep(1000);
            } catch (InterruptedException e) {

            }
        }
    }

    private boolean loadPreviousState(File saveto) {
        {
            try {
                DataInput input = new DataInputStream(new BufferedInputStream(new FileInputStream(saveto)));
                stackedRBM.load(input, layerFactory);
                return true;

            } catch (IOException e) {
                System.err.println("ERROR loading "+saveto);
                e.printStackTrace();
            }
        }
        return false;
    }

    private void update() {

//        synchronized (outputs) {
            outputs.clear();//!!
            test();

//            outputs.clear();//!!

            //  feed this system 10 times with its own data
//            for (int j = 0; j < 10; j++) {
//                State t = it.next();
//                Layer layer = t.visible;
//                visualize(layer);
//            }
//        }
        repaint();
    }

    private void test() {

        double numCorrect = 0;
        double numWrong = 0;
        double numAlmost = 0.0;
        LabeledItem testCase = datasetReader.getTestItem();
        labeledItem = testCase;// draw

        Iterator<State> it = evaluate(testCase);

        float[] labeld = new float[10];
        synchronized (outputs) {
        for (int i = 0; i < 2; i++) {
            State t = it.next();
            Layer layer = t.visible;
            visualize(layer);
            for (int j = (t.visible.size() - 10), k = 0; j < t.visible.size() && k < 10; j++, k++) {
                labeld[k] += t.visible.get(j);
            }
        }
        }

        float max1 = 0.0f;
        float max2 = 0.0f;
        int p1 = -1;
        int p2 = -1;

        System.err.print("Label is: " + testCase.label);


        for (int i = 0; i < labeld.length; i++) {
            labeld[i] /= 2;
            if (labeld[i] > max1) {
                max2 = max1;
                max1 = labeld[i];

                p2 = p1;
                p1 = i;
            }
        }
        frame.setTitle( "Winner is " + p1);

        System.err.print(", Winner is " + p1 + "(" + max1 + ") second is " + p2 + "(" + max2 + ")");
        if (p1 == Integer.valueOf(testCase.label)) {
            System.err.println(" CORRECT!");
            numCorrect++;

        } else if (p2 == Integer.valueOf(testCase.label)) {
            System.err.println(" Almost!");
            numAlmost++;
            numWrong++;
        } else {
            System.err.println(" wrong :(");
            numWrong++;
        }

        System.err.println("Error Rate = " + ((numWrong / (numAlmost + numCorrect + numWrong)) * 100+"%"));

    }
}
