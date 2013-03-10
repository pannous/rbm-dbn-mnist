package com.pannous.rbm;

import java.util.Iterator;
import java.util.List;

public class SimpleRBMTrainer {
    public float momentum;
    final Float targetSparsity;
    final float learningRate2;
    public float learningRate;
    private LayerFactory layerFactory;

    Layer[] gweights;
    Layer gvisible;
    Layer ghidden;

    public SimpleRBMTrainer(float momentum, float learningRate2, Float targetSparsity, Float learningRate, LayerFactory layerFactory) {
        this.momentum = momentum;
        this.learningRate2 = learningRate2;
        this.targetSparsity = targetSparsity;
        this.learningRate = learningRate;
        this.layerFactory = layerFactory;
    }

    public double learn(final SimpleRBM rbm, List<Layer> inputBatch, boolean reverse) {
        initialize(rbm, inputBatch, reverse);

        contrastiveDivergence(rbm, inputBatch, reverse);

        adjustWeights(rbm, inputBatch, reverse);

        double error = calculateError(rbm, inputBatch, reverse);

        updateHidden(rbm, inputBatch, reverse);

        return error;
    }

    private void initialize(SimpleRBM rbm, List<Layer> inputBatch, boolean reverse) {

        if (gweights == null || gweights.length != rbm.biasHidden.size() || gweights[0].size() != rbm.biasVisible.size()) {
            gweights = new Layer[rbm.biasHidden.size()];
            for (int i = 0; i < gweights.length; i++)
                gweights[i] = layerFactory.create(rbm.biasVisible.size());

            gvisible = layerFactory.create(rbm.biasVisible.size());
            ghidden = layerFactory.create(rbm.biasHidden.size());
        } else {
            for (int i = 0; i < gweights.length; i++)
                gweights[i].clear();

            gvisible.clear();
            ghidden.clear();
        }
    }


    private void contrastiveDivergence(SimpleRBM rbm, List<Layer> inputBatch, boolean reverse) {
        for (Layer input : inputBatch) {
            try {
                Iterator<State> it = reverse ? rbm.reverseIterator(input) : rbm.iterator(input);

                State t1 = it.next();    //UP
                State t2 = it.next();    //Down

                for (int i = 0; i < gweights.length; i++)
                    for (int j = 0; j < gweights[i].size(); j++)
                        gweights[i].add(j, (t1.hidden.get(i) * t1.visible.get(j)) - (t2.hidden.get(i) * t2.visible.get(j)));

                for (int i = 0; i < gvisible.size(); i++)
                    gvisible.add(i, t1.visible.get(i) - t2.visible.get(i));

                for (int i = 0; i < ghidden.size(); i++)
                    ghidden.add(i, targetSparsity == null ? t1.hidden.get(i) - t2.hidden.get(i) : targetSparsity - t1.hidden.get(i));

            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    private void adjustWeights(SimpleRBM rbm, List<Layer> inputBatch, boolean reverse) {
        int batchSize = inputBatch.size();
        // w(i,j)= w(i,j)/n*(1-momentum)

        for (int i = 0; i < gweights.length; i++) {
            for (int j = 0; j < gweights[i].size(); j++) {
                gweights[i].div(j, batchSize);

                gweights[i].mult(j, 1 - momentum);
                gweights[i].add(j, momentum * (gweights[i].get(j) - learningRate2 * rbm.weights[i].get(j)));

                rbm.weights[i].add(j, learningRate * gweights[i].get(j));
            }
        }

    }

    private double calculateError(SimpleRBM rbm, List<Layer> inputBatch, boolean reverse) {
        int batchSize = inputBatch.size();

        double error = 0.0;

        for (int i = 0; i < gvisible.size(); i++) {
            gvisible.div(i, batchSize);

            error += Math.pow(gvisible.get(i), 2);

            gvisible.mult(i, 1 - momentum);
            gvisible.add(i, momentum * (gvisible.get(i) * rbm.biasVisible.get(i)));

            rbm.biasVisible.add(i, learningRate * gvisible.get(i));
        }

        error = Math.sqrt(error / gvisible.size());
        return error;
    }


    private void updateHidden(SimpleRBM rbm, List<Layer> inputBatch, boolean reverse) {
        int batchSize = inputBatch.size();

        if (targetSparsity != null) {
            for (int i = 0; i < ghidden.size(); i++) {
                ghidden.div(i, batchSize);
                ghidden.set(i, targetSparsity - ghidden.get(i));
            }
        } else {
            for (int i = 0; i < ghidden.size(); i++) {
                ghidden.div(i, batchSize);

                ghidden.mult(i, 1 - momentum);
                ghidden.add(i, momentum * (ghidden.get(i) * rbm.biasHidden.get(i)));

                rbm.biasHidden.add(i, learningRate * ghidden.get(i));
            }
        }
    }
}
