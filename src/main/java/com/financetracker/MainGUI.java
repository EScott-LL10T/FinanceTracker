package com.financetracker;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

public class MainGUI extends JFrame {

    private final Profile profile;

    public MainGUI() {
        // get data from database.
        profile = DatabaseHelper.getProfile();
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


        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                getPieChartPanel(),
                getTransactionsPanel()
        );

        splitPane.setDividerLocation(450);
        splitPane.setDividerSize(0);
        splitPane.setEnabled(false);

        splitPane.setOneTouchExpandable(false);

        splitPane.setResizeWeight(0.7);

        add(splitPane, BorderLayout.CENTER);
    }

    public JPanel getPieChartPanel(){
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        double salary = (profile.getSalary() - (profile.getSalary() * 0.60)) / 12;

        dataset.setValue("Rent", 800);
        dataset.setValue("Food", 300);
        dataset.setValue("Transport", 150);
        dataset.setValue("Entertainment", 100);
        dataset.setValue("Available", salary - 100 - 150 - 300 - 800);

        PiePlot<String> plot = new PiePlot<>(dataset);

        PieSectionLabelGenerator labelGenerator = new StandardPieSectionLabelGenerator("{0}: £{1} ({2})");

        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setLabelGenerator(labelGenerator);


        JFreeChart pieChart = new JFreeChart(
                "Budget Distribution",
                JFreeChart.DEFAULT_TITLE_FONT,
                plot,
                true
        );

        return new ChartPanel(pieChart);

    }


    public JPanel getTransactionsPanel(){
        JPanel transactionsPanel = new JPanel();
        Font font = new Font("Arial", Font.PLAIN, 16);

        double monthlySalary = (profile.getSalary() - (profile.getSalary() * 0.60)) / 12;


        transactionsPanel.setLayout(new BoxLayout(transactionsPanel, BoxLayout.Y_AXIS));

        transactionsPanel.setBorder(BorderFactory.createTitledBorder("Financial Summary"));

        JLabel salary = new JLabel("Salary");
        salary.setFont(font);

        String monthlySalaryFormatted = String.format("£%.2f", monthlySalary);

        JLabel salaryAmount = new JLabel(monthlySalaryFormatted);
        salaryAmount.setFont(font);

        JLabel totalSpent = new JLabel("Total Spent");
        totalSpent.setFont(font);

        double totalSpend = 100 + 250 + 300 + 800;
        JLabel totalSpentAmount = new JLabel(String.format("£%.2f", totalSpend));
        totalSpentAmount.setFont(font);

        JLabel available = new JLabel("Available");
        available.setFont(font);

        JLabel availableAmount = new JLabel(String.format("£%.2f", monthlySalary - 100 - 150 - 300 - 800));
        availableAmount.setFont(font);

        JLabel taxed = new JLabel("tax");
        taxed.setFont(font);

        JLabel taxedAmount = new JLabel(String.format("£%.2f", profile.getSalary() * 0.60));
        taxedAmount.setFont(font);

        transactionsPanel.add(salary);
        transactionsPanel.add(salaryAmount);

        transactionsPanel.add(Box.createVerticalStrut(20));

        transactionsPanel.add(totalSpent);
        transactionsPanel.add(totalSpentAmount);

        transactionsPanel.add(Box.createVerticalStrut(20));

        transactionsPanel.add(available);
        transactionsPanel.add(availableAmount);

        transactionsPanel.add(Box.createVerticalStrut(20));

        transactionsPanel.add(taxed);
        transactionsPanel.add(taxedAmount);
        return transactionsPanel;

    }
}
