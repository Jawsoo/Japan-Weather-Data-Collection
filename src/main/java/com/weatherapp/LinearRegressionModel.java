package com.weatherapp;

public class LinearRegressionModel {
    public static double[] predict(double[] pastTemps) {
        int n = pastTemps.length;
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;

        for (int i = 0; i < n; i++) {
            sumX += i;
            sumY += pastTemps[i];
            sumXY += i * pastTemps[i];
            sumX2 += i * i;
        }

        double slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        double intercept = (sumY - slope * sumX) / n;

        double[] predictedTemps = new double[n + 5]; // Predict next 5 days
        System.arraycopy(pastTemps, 0, predictedTemps, 0, n);

        for (int i = n; i < predictedTemps.length; i++) {
            predictedTemps[i] = slope * i + intercept;

            // Adding slight variations to simulate real weather changes
            if (i % 2 == 0) {
                predictedTemps[i] += Math.random() * 0.5;  // Small increase
            } else {
                predictedTemps[i] -= Math.random() * 0.5;  // Small decrease
            }
        }
        return predictedTemps;
    }
}
