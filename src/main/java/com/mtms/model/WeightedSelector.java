package com.mtms.model;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class WeightedSelector {
    private final String[] values;
    private final double[] prefixSums;
    private final Random random = new Random();

    public WeightedSelector(String[] values, List<Double> weights) {
        this.values = values;
        this.prefixSums = new double[weights.size()];
        double sum = 0;
        for (int i = 0; i < weights.size(); i++) {
            sum += weights.get(i);
            this.prefixSums[i] = sum;
        }
    }

    private int pickIndex() {
        int index = Arrays.binarySearch(prefixSums, random.nextDouble());

        // If exact match isn't found, binarySearch returns (-(insertion point) - 1)
        if (index < 0) {
            index = -index - 1;
        }

        // Bound check for safety against edge-case precision issues
        return Math.min(index, prefixSums.length - 1);
    }

    public String getValue() {
        return values[pickIndex()];
    }
}
