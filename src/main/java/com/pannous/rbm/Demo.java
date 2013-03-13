package com.pannous.rbm;


import com.pannous.rbm.graphics.GraphicsDBM;
import com.pannous.rbm.graphics.GraphicsRBM;
import com.pannous.rbm.minst.GenerativeMinstDBN;
import com.pannous.rbm.minst.MinstDBN;
import com.pannous.rbm.minst.MinstRBM;
import com.pannous.util.Debugger;

import java.io.File;

public class Demo {
    public static void main(String[] args) {
        try {
//            while (true) {
//                System.out.print("OK");
//                AudioCapture.start();
                Thread.sleep(1);
//            Thread.sleep(100000000);

//            }
        } catch (InterruptedException e) {
        }

        if (args.length < 2) {
            usage("Starting examples");
//            GraphicsNet.start();
//            GraphicsRBM.start();
            GraphicsDBM.start();
//            RBM_Demo();
//            DBM_Demo();
            Debugger.info("DONE");
            System.exit(0);
        }

        if (args[0].equalsIgnoreCase("rbm")) {
            File labels = new File(args[1]);
            File images = new File(args[2]);


            if (!labels.isFile())
                usage("invalid minst labels file: " + args[1]);

            if (!images.isFile())
                usage("invalid minst images file: " + args[2]);

            MinstRBM.start(labels, images);
        } else if (args[0].equalsIgnoreCase("dbn")) {
            File labels = new File(args[1]);
            File images = new File(args[2]);
            File saveto = new File(args[3]);
            if (!labels.isFile())
                usage("invalid minst labels file: " + args[1]);
            if (!images.isFile())
                usage("invalid minst images file: " + args[2]);

            MinstDBN.start(labels, images, saveto);
        } else if (args[0].equalsIgnoreCase("gen")) {
            File load = new File(args[1]);
            if (!load.isFile())
                usage("invalid dbn file: " + args[1]);
            GenerativeMinstDBN.start(load);
        }

    }


    private static void DBM_Demo() {
//            System.exit(-1);
        File labels = new File("target/minst/train-labels-idx1-ubyte.gz");
        File images = new File("target/minst/train-images-idx3-ubyte.gz");
        File out = new File("/tmp/dbn.bin");
//        BinaryMinstDBN.start(labels, images, out);
        MinstDBN.start(labels, images, out);
    }

    private static void RBM_Demo() {
//            System.exit(-1);
        File labels = new File("target/minst/train-labels-idx1-ubyte.gz");
        File images = new File("target/minst/train-images-idx3-ubyte.gz");
//            BinaryMinstRBM.start(labels,images);
        MinstRBM.start(labels, images);
    }

    private static void usage(String err) {
        System.err.println("Usage: \t[rbm minst-labels.gz minst-images.gz]\n\t [dbn minst-images.gz minst-labels.gz dbn.bin]\n\t [gen dbn.bin]");
        if (err != null && err.length() > 0)
            System.err.println(err);
//        System.exit(-1);
    }
}
