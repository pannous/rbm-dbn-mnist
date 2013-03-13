package com.pannous.rbm;

import java.security.SecureRandom;
import java.util.*;

/**
 * Reads data
 */
public abstract class DatasetReader implements Enumeration<LabeledItem>
{

    protected SecureRandom random = new SecureRandom();

    public final Map<String, List<LabeledItem>> trainingSet = new HashMap<String, List<LabeledItem>>();
    protected final Map<String, List<LabeledItem>> testSet = new HashMap<String, List<LabeledItem>>();

    public int rows = 0;
    public int cols = 0;
    protected int count = 0;
    protected int current = 0;


    public DatasetReader() {
    }

    public LabeledItem getTestItem() {
        return getTestItem(-1);
    }

    public LabeledItem getTestItem(int i)
    {
        Object[] keys = testSet.keySet().toArray();
        if(i==-1) i = random.nextInt(keys.length);
        List<LabeledItem> list = testSet.get(keys[i]);
        return list.get(random.nextInt(list.size()));

    }

    public LabeledItem getTrainingItem() {
        return getTrainingItem(-1);// random
    }

    public LabeledItem getTrainingItem(int i)
    {
        Object[] keys = trainingSet.keySet().toArray();
        if(i==-1) i = random.nextInt(keys.length);
        List<LabeledItem> list = trainingSet.get(keys[i]);
        return list.get(random.nextInt(list.size()));
    }

    public boolean hasMoreElements()
    {
        return current < count;
    }

    public abstract LabeledItem nextElement();
}
