package com.pannous.rbm.minst;

import com.pannous.rbm.DatasetReader;
import com.pannous.rbm.LabeledItem;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOError;
import java.io.IOException;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * Reads the Minst image data from
 */
public class MinstDatasetReader extends DatasetReader
{
    final DataInputStream labelsBuf;
    final DataInputStream imagesBuf;

    public MinstDatasetReader(File labelsFile, File imagesFile) throws Exception {
        super();
        try
        {
            labelsBuf = new DataInputStream(new GZIPInputStream(new FileInputStream(labelsFile)));
            imagesBuf = new DataInputStream(new GZIPInputStream(new FileInputStream(imagesFile)));

            verify();

            createTrainingSet();
        }
        catch (Exception e)
        {
            throw e;
        }
        finally
        {
        }
    }

    public void createTrainingSet() {
        boolean done = false;

        while (!done || !hasMoreElements()) {
            LabeledItem i = nextElement();

            if (random.nextDouble() > 0.3) {
                List<LabeledItem> l = testSet.get(i.label);
                if (l == null)
                    l = new ArrayList<LabeledItem>();
                testSet.put(i.label, l);

                l.add(i);
            } else {
                List<LabeledItem> l = trainingSet.get(i.label);
                if (l == null)
                    l = new ArrayList<LabeledItem>();
                trainingSet.put(i.label, l);
                l.add(i);
            }

            if (trainingSet.isEmpty())
                continue;

            boolean isDone = true;
            for (Map.Entry<String, List<LabeledItem>> entry : trainingSet.entrySet()) {
                if (entry.getValue().size() < 100) {
                    isDone = false;
                    break;
                }
            }

            done = isDone;
        }
    }

    private void verify() throws IOException
    {
        int magic = labelsBuf.readInt();
        int labelCount = labelsBuf.readInt();

        System.err.println("Labels magic=" + magic + ", count=" + labelCount);

        magic = imagesBuf.readInt();
        int imageCount = imagesBuf.readInt();
        rows = imagesBuf.readInt();
        cols = imagesBuf.readInt();

        System.err.println("Images magic=" + magic + " count=" + imageCount + " rows=" + rows + " cols=" + cols);

        if (labelCount != imageCount)
            throw new IOException("Label Image count mismatch");

        count = imageCount;
    }

    public LabeledItem nextElement()
    {
        LabeledItem m = new LabeledItem();

        try
        {
            m.label = String.valueOf(labelsBuf.readUnsignedByte());
            m.data = new int[rows * cols];

            for (int i = 0; i < m.data.length; i++)
            {
                m.data[i] = imagesBuf.readUnsignedByte();
            }

            return m;
        }
        catch (IOException e)
        {
            current = count;
            throw new IOError(e);
        }
        finally
        {
            current++;
        }

    }
}
