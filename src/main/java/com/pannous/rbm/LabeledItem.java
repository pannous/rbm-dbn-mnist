package com.pannous.rbm;

/**
 * Container class that represents a Minst image and it's label
 */
public class LabeledItem
{
    public String label;
    public int[] data;

    @Override
    public String toString() {
        return label + " (int[" + data.length + "])";
    }
}
