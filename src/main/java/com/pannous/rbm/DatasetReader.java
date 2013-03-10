package com.pannous.rbm;

import java.security.SecureRandom;
import java.util.*;

/**
 * Reads data
 */
public abstract class DatasetReader implements Enumeration<LabeledItem>
{

    protected SecureRandom random = new SecureRandom();

    protected final Map<String, List<LabeledItem>> trainingSet = new HashMap<String, List<LabeledItem>>();
    protected final Map<String, List<LabeledItem>> testSet = new HashMap<String, List<LabeledItem>>();

    public int rows = 0;
    public int cols = 0;
    protected int count = 0;
    protected int current = 0;


    public DatasetReader() {
    }

    public LabeledItem getTestItem()
    {
        int i = random.nextInt(10);
        List<LabeledItem> list = testSet.get(String.valueOf(i));// Random digit / number
        return list.get(random.nextInt(list.size()));// random sample
    }

    public LabeledItem getTrainingItem() {
        Object[] keys = trainingSet.keySet().toArray();
//        if(keys.length==0) throw new Exception("trainingSet must contain at least one list");
        if(keys.length==0) System.err.println("trainingSet must contain at least one list");
        List<LabeledItem> list = trainingSet.get(keys[random.nextInt(keys.length)]);
        return list.get(random.nextInt(list.size()));

    }

    public LabeledItem getTrainingItem(int i)
    {
        List<LabeledItem> list = trainingSet.get(String.valueOf(i));
        return list.get(random.nextInt(list.size()));

    }

    public boolean hasMoreElements()
    {
        return current < count;
    }

    public abstract LabeledItem nextElement();
}
