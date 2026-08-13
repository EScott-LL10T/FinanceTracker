package com.financetracker;

import javax.swing.*;
import java.awt.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

public class MainGUI extends JFrame {

    public MainGUI() {
        setTitle("Personal Finance Tracker");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centers window on screen

        // Main layout
        setLayout(new BorderLayout());

        // Header Panel
        JLabel headerLabel = new JLabel("My Budget Dashboard", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(headerLabel, BorderLayout.NORTH);

        // Chart Integration (Using JFreeChart dependency)
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        dataset.setValue("Food", 45.50);
        dataset.setValue("Rent", 600.00);
        dataset.setValue("Entertainment", 30.00);

        JFreeChart pieChart = ChartFactory.createPieChart(
                "Spending Distribution",
                dataset,
                true, true, false
        );

        // ChartPanel is a Swing component provided by JFreeChart
        ChartPanel chartPanel = new ChartPanel(pieChart);
        add(chartPanel, BorderLayout.CENTER);
    }
}