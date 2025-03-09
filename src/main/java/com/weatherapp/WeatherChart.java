package com.weatherapp;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;

public class WeatherChart {
    public static void createChart(String city, double[] temps) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < temps.length; i++) {
            dataset.addValue(temps[i], "Temperature", "Day " + (i + 1));
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Temperature Trends in " + city,
                "Day", "Temperature (°C)", dataset,
                PlotOrientation.VERTICAL, true, true, false
        );

        // Customize the chart appearance
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.WHITE);

        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.RED);
        plot.setRenderer(renderer);

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Weather Chart for " + city);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.add(new ChartPanel(chart));
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
