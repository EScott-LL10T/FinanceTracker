package com.financetracker;

import javax.swing.*;
import java.awt.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.RingPlot;
import org.jfree.data.general.DefaultPieDataset;

public class MainGUI extends JFrame {

    public MainGUI() {
        // get data from database.
        Profile profile = DatabaseHelper.getProfile();
        if(profile == null){
            return;
        }


        setTitle("Personal Finance Tracker");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centers window on screen

        // Main layout
        setLayout(new BorderLayout());

        // Header Panel
        JLabel headerLabel = new JLabel(profile.getName() + " Budget Dashboard", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(headerLabel, BorderLayout.NORTH);

        // Chart Integration (Using JFreeChart dependency)
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

        dataset.setValue("Rent", 800);
        dataset.setValue("Food", 300);
        dataset.setValue("Transport", 150);
        dataset.setValue("Entertainment", 100);
        dataset.setValue("Available", 1150);


        JFreeChart pieChart = ChartFactory.createPieChart(
                "Budget Distribution",
                dataset,
                true,
                true,
                false
        );


        PiePlot plot = (PiePlot) pieChart.getPlot();

        PieSectionLabelGenerator labelGenerator =
                new StandardPieSectionLabelGenerator(
                        "{0}: £{1} ({2})"
                );

        plot.setLabelGenerator(labelGenerator);


        // ChartPanel is a Swing component provided by JFreeChart
        ChartPanel chartPanel = new ChartPanel(pieChart);

        add(chartPanel);


        add(chartPanel, BorderLayout.CENTER);
    }
}
