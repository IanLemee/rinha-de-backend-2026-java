package com.ian.rinha_backend.model;

import java.util.List;

public class ReferencesModel {
    private List<Double> vector;
    private String label;

    public ReferencesModel(List<Double> vector, String label) {
        this.vector = vector;
        this.label = label;
    }

    public List<Double> getVector() {
        return vector;
    }

    public String getLabel() {
        return label;
    }
}
