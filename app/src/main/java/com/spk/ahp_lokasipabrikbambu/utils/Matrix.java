package com.spk.ahp_lokasipabrikbambu.utils;

import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by wisnu on 12/23/17.
 */
public class Matrix {
    public static final String GRAND_TOTAL_KEY = "grandTotal";
    public static double RI[] = {0.0, 0.0, 0.58, 0.9, 1.12, 1.24, 1.32, 1.41, 1.45, 1.49};

    private String[] rowLabels = new String[1];
    private String[] colLabels = new String[1];
    private float[][] rows;

    public Matrix(Set<String> rowLabels, Set<String> columnLabels) {
        this.rowLabels = rowLabels.toArray(this.rowLabels);
        this.colLabels = columnLabels.toArray(this.colLabels);

        Arrays.sort(this.rowLabels);
        Arrays.sort(this.colLabels);

        this.rows = new float[this.rowLabels.length][this.colLabels.length];
    }

    public float getValue(String rowLabel, String columnLabel) {
        int[] indices = getBarisDanKolomIndex(rowLabel, columnLabel);
        return rows[indices[0]][indices[1]];
    }


    public void setValue(String rowLabel, String columnLabel, float value) {
        int[] indices = getBarisDanKolomIndex(rowLabel, columnLabel);
        rows[indices[0]][indices[1]] = value;
    }

    public Map<String, Float> getTotalBaris() {
        Map<String, Float> totals = new HashMap<>();
        float grandTotal = 0;
        for (int i = 0; i < this.rowLabels.length; i++) {
            float total = 0;
            for (int j = 0; j < this.colLabels.length; j++) {
                total += this.rows[i][j];
            }
            totals.put(this.rowLabels[i], total);
            grandTotal += total;
        }
        totals.put(GRAND_TOTAL_KEY, grandTotal);

        return totals;
    }

    public Map<String, Float> getTotalKolom() {
        Map<String, Float> totals = new HashMap<>();
        for (int i = 0; i < this.rowLabels.length; i++) {
            float total = 0;
            for (int j = 0; j < this.colLabels.length; j++) {
                total += this.rows[j][i];
            }
            totals.put(this.rowLabels[i], total);
        }

        return totals;
    }

    public void divideMatrixBySumOfColumn(Map<String, Float> totalKolom) {
        for (String rowLabel : this.rowLabels) {
            for (int j = 0; j < this.colLabels.length; j++) {
                Float divider = totalKolom.get(rowLabel);
                setValue(this.rowLabels[j], rowLabel, getValue(this.rowLabels[j], rowLabel) / divider);
            }
        }
    }

    public Map<String, Float> getSumMatrix() {
        Map<String, Float> totals = new HashMap<>();
        for (int i = 0; i < this.rowLabels.length; i++) {
            float total = 0;
            for (int j = 0; j < this.colLabels.length; j++) {
                total += this.rows[i][j] / this.colLabels.length;
            }
            totals.put(this.rowLabels[i], total);
        }

        return totals;
    }

    private int[] getBarisDanKolomIndex(String rowLabel, String columnLabel) {
        int rowIndex = Arrays.binarySearch(this.rowLabels, rowLabel);
        int colIndex = Arrays.binarySearch(this.colLabels, columnLabel);

        if (rowIndex >= 0) {
            if (colIndex >= 0) {
                int[] indices = new int[2];
                indices[0] = rowIndex;
                indices[1] = colIndex;
                return indices;
            } else {
                Log.i(Matrix.class.getName(), "colIndex: " + colIndex);
                Log.i(Matrix.class.getName(), "columnLabel: " + columnLabel);
                Log.i(Matrix.class.getName(), "colLabels: " + Arrays.toString(this.colLabels));
                throw new RuntimeException("No se puede encontrar la columna etiquetada: " + columnLabel);
            }
        } else {
            Log.i(Matrix.class.getName(), "rowIndex: " + rowIndex);
            Log.i(Matrix.class.getName(), "rowLabel: " + rowLabel);
            Log.i(Matrix.class.getName(), "rowLabels: " + Arrays.toString(this.rowLabels));
            throw new RuntimeException("No se puede encontrar la columna etiquetada: " + rowLabel);
        }
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        int[] colWidths = calculateColumnWidths();

        sb.append("Matrix:\n");
        sb.append(pad(colWidths[0], ""));
        for (int i = 0; i < this.colLabels.length; i++) {
            sb.append(" ").append(pad(colWidths[i + 1], this.colLabels[i])).append(" ");
        }
        sb.append("\n");
        for (int i = 0; i < this.rowLabels.length; i++) {
            sb.append(pad(colWidths[0], this.rowLabels[i]));
            for (int j = 0; j < this.colLabels.length; j++) {
                sb.append(" ").append(pad(colWidths[j + 1], String.valueOf(this.rows[i][j]))).append(" ");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    private int[] calculateColumnWidths() {

        int[] widths = new int[this.colLabels.length + 1];

        int width = 0;
        for (String rowLabel : this.rowLabels) {
            if (rowLabel.length() > width) {
                width = rowLabel.length();
            }
        }
        widths[0] = width;
        for (int i = 0; i < this.colLabels.length; i++) {

            width = 4;
            if (this.colLabels.length > width) {
                width = this.colLabels.length;
            }
            widths[i + 1] = width;
        }

        return widths;
    }

    private String pad(int numChars, String str) {
        if (str.length() > numChars) {
            return str.substring(0, numChars);
        }

        StringBuilder paddedStr = new StringBuilder(str);
        for (int i = 0; i < (numChars - str.length()); i++) {
            paddedStr.insert(0, " ");
        }
        return paddedStr.toString();
    }
}

