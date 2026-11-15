package com.financetracker;


import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

public class MainPanel extends JPanel {

    private final Profile profile;
    private ArrayList<Transaction> transactions;
    private ArrayList<Transaction> monthlyTransactions;
    private static final Color FUNDS_COLOUR = new Color(128, 0, 128);

    private static final Map<String, Color> CATEGORY_COLOURS = Map.of(
            "Entertainment", Color.RED,
            "Rent", Color.BLUE,
            "Food", Color.GREEN,
            "Transport", Color.ORANGE,
            "Shopping", Color.YELLOW,
            "Other", Color.GRAY,
            "Student Loan Payment", new Color(0, 128, 128)
    );

    private static final Color[] FALLBACK_COLOURS = {
            Color.CYAN, Color.PINK, Color.MAGENTA, new Color(139, 69, 19)
    };

    public MainPanel(JFrame frame) {
        // get data from database.
        profile = DatabaseHelper.getProfile();
        if(profile == null){
            return;
        }
        transactions = DatabaseHelper.getTransactions(profile.getId());
        monthlyTransactions = getMonthlyTransactions();

        // Main layout
        setLayout(new BorderLayout());

        // Header Panel
        JLabel headerLabel = new JLabel(profile.getName() + " Budget Dashboard", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 30));
        add(headerLabel, BorderLayout.NORTH);

        JSplitPane splitPane = getSplitPane();

        add(splitPane, BorderLayout.CENTER);

        Font buttonFont = new Font("Arial", Font.PLAIN, 16);

        JButton exitButton = new JButton("Exit");
        exitButton.setFont(buttonFont);

        JButton addTransactionButton = new JButton("Add Transaction");
        addTransactionButton.setFont(buttonFont);

        JButton editProfileButton = new JButton("Edit Profile");
        editProfileButton.setFont(buttonFont);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 20, 20));

        buttonPanel.add(exitButton);
        buttonPanel.add(addTransactionButton);
        buttonPanel.add(editProfileButton);

        add(buttonPanel, BorderLayout.SOUTH);

        exitButton.addActionListener(e -> frame.dispose());

        addTransactionButton.addActionListener(e ->
                setNewFrameContent(frame, new AddTransactionPanel(frame, this))
        );

        editProfileButton.addActionListener(e ->
            setNewFrameContent(frame, new EditProfilePanel(frame, this))
        );

    }

    private JSplitPane getSplitPane(){
        JSplitPane splitPane;
        splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                getPieChartPanel(),
                getTransactionsPanel()
        );

        splitPane.setDividerLocation(450);
        splitPane.setDividerSize(1);
        splitPane.setEnabled(false);

        splitPane.setOneTouchExpandable(false);

        splitPane.setResizeWeight(0.7);
        return splitPane;
    }

    private JPanel getPieChartPanel() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

        double totalSpent = 0.0;
        Map<String, Double> spentByCategory = new HashMap<>();
        Map<String, Double> incomeByCategory = new HashMap<>();

        for (Transaction t : monthlyTransactions) {
            if (t.getTransactionType() == TransactionType.INCOME) {
                incomeByCategory.merge(t.getCategory() + " income", t.getAmount(), Double::sum);
            } else {
                spentByCategory.merge(t.getCategory(), t.getAmount(), Double::sum);
                totalSpent += t.getAmount();
            }
        }

        // Spending comes out of income first, then salary
        double remainingSpend = totalSpent;
        Map<String, Double> incomeLeft = new  HashMap<>();
        for (Map.Entry<String, Double> e : incomeByCategory.entrySet()) {
            double used = Math.min(e.getValue(), remainingSpend);
            remainingSpend -= used;
            double left = e.getValue() - used;
            if (left > 0.005) {
                incomeLeft.put(e.getKey(), left);
            }
        }

        double salaryLeft = profile.getSalary() - remainingSpend;
        double overspent = 0.0;

        // Funds slices FIRST (so expense slices draw on top of the shared edges)
        if (salaryLeft > 0.005) {
            dataset.setValue("Available", salaryLeft);
        } else if (salaryLeft < -0.005) {
            overspent = -salaryLeft;
        }
        for (Map.Entry<String, Double> e : incomeLeft.entrySet()) {
            dataset.setValue(e.getKey(), e.getValue());
        }

        // Expense slices LAST
        for (Map.Entry<String, Double> e : spentByCategory.entrySet()) {
            dataset.setValue(e.getKey(), e.getValue());
        }

        String title = overspent > 0
                ? String.format("Budget Distribution - Overspent by £%.2f", overspent)
                : "Budget Distribution";

        return new ChartPanel(getPieChart(dataset, title));
    }


    private static boolean isFunds(String key) {
        return key.equals("Available") || key.endsWith(" income");
    }

    private static JFreeChart getPieChart(DefaultPieDataset<String> dataset, String title) {
        PiePlot<String> plot = new PiePlot<>(dataset);

        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: £{1} ({2})"));
        plot.setLabelFont(new Font("Arial", Font.PLAIN, 12));

        plot.setSectionOutlinesVisible(true);

        int fallbackIndex = 0;
        for (String key : dataset.getKeys()) {
            if (isFunds(key)) {
                // Thin black divider, only visible between funds slices
                plot.setSectionPaint(key, FUNDS_COLOUR);
                plot.setSectionOutlinePaint(key, Color.BLACK);
                plot.setSectionOutlineStroke(key, new BasicStroke(1f));
            } else {
                // Outline matches the fill, so it's invisible between expenses and
                // slightly thicker than the black line so it covers it on shared edges
                Color colour = CATEGORY_COLOURS.get(key);
                if (colour == null) {
                    colour = FALLBACK_COLOURS[fallbackIndex++ % FALLBACK_COLOURS.length];
                }
                plot.setSectionPaint(key, colour);
                plot.setSectionOutlinePaint(key, colour);
                plot.setSectionOutlineStroke(key, new BasicStroke(1.5f));
            }
        }

        return new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, true);
    }


    private JPanel getTransactionsPanel(){
        JPanel transactionsPanel = new JPanel();

        Font transactionsFont = new Font("Arial", Font.PLAIN, 16);
        transactionsPanel.setLayout(new BoxLayout(transactionsPanel, BoxLayout.Y_AXIS));
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Financial Summary");
        titledBorder.setTitleFont(new Font("Ariel", Font.BOLD, 20));
        transactionsPanel.setBorder(titledBorder);

        boolean isStudent = profile.getRole().equals("Student");
        if(isStudent){


            double loanFunds = profile.getSalary();


            JLabel salary = new JLabel("Loan amount");
            salary.setFont(transactionsFont);

            String monthlySalaryFormatted = String.format("£%.2f", loanFunds);

            JLabel loanAmount = new JLabel(monthlySalaryFormatted);
            loanAmount.setFont(transactionsFont);

            JLabel totalSpent = new JLabel("Total Spent");
            totalSpent.setFont(transactionsFont);

            double totalSpend = calculateExpenseTransactionsTotal(monthlyTransactions);
            JLabel totalSpentAmount = new JLabel(String.format("£%.2f", totalSpend));
            totalSpentAmount.setFont(transactionsFont);

            JLabel available = new JLabel("Available");
            available.setFont(transactionsFont);

            JLabel availableAmount = new JLabel(String.format("£%.2f", loanFunds - totalSpend));
            availableAmount.setFont(transactionsFont);

            transactionsPanel.add(salary);
            transactionsPanel.add(loanAmount);

            transactionsPanel.add(Box.createVerticalStrut(5));

            transactionsPanel.add(totalSpent);
            transactionsPanel.add(totalSpentAmount);

            transactionsPanel.add(Box.createVerticalStrut(5));

            transactionsPanel.add(available);
            transactionsPanel.add(availableAmount);

        }else{

            double monthlySalary = (profile.getSalary() - (profile.getSalary() * profile.calculateTax())) / 12;

            JLabel salary = new JLabel("Salary");
            salary.setFont(transactionsFont);

            String monthlySalaryFormatted = String.format("£%.2f", monthlySalary);

            JLabel salaryAmount = new JLabel(monthlySalaryFormatted);
            salaryAmount.setFont(transactionsFont);

            JLabel totalSpent = new JLabel("Total Spent");
            totalSpent.setFont(transactionsFont);

            double totalSpend = calculateExpenseTransactionsTotal(transactions);
            JLabel totalSpentAmount = new JLabel(String.format("£%.2f", totalSpend));
            totalSpentAmount.setFont(transactionsFont);

            JLabel available = new JLabel("Available");
            available.setFont(transactionsFont);

            JLabel availableAmount = new JLabel(String.format("£%.2f", monthlySalary - totalSpend));
            availableAmount.setFont(transactionsFont);

            JLabel taxed = new JLabel("tax");
            taxed.setFont(transactionsFont);

            JLabel taxedAmount = new JLabel(String.format("£%.2f", profile.getSalary() * profile.calculateTax() / 12));
            taxedAmount.setFont(transactionsFont);

            transactionsPanel.add(salary);
            transactionsPanel.add(salaryAmount);

            transactionsPanel.add(Box.createVerticalStrut(5));

            transactionsPanel.add(totalSpent);
            transactionsPanel.add(totalSpentAmount);

            transactionsPanel.add(Box.createVerticalStrut(5));

            transactionsPanel.add(available);
            transactionsPanel.add(availableAmount);

            transactionsPanel.add(Box.createVerticalStrut(5));

            transactionsPanel.add(taxed);
            transactionsPanel.add(taxedAmount);
        }

        return transactionsPanel;

    }

    private ArrayList<Transaction> getMonthlyTransactions(){
        ArrayList<Transaction> monthlyTransactions = new ArrayList<>();
        LocalDateTime date = LocalDateTime.now();
        int month = date.getMonthValue();
        int year = date.getYear();
        for(Transaction t: transactions){
            LocalDateTime transactionDate = t.getTimeOfTransaction();
            if(t.getTimeOfTransaction().getMonthValue() == month &&
                    transactionDate.getYear() == year){
                monthlyTransactions.add(t);
            }

        }
        return monthlyTransactions;
    }

    public double getStudentLoanPayments(){
        double total = 0;
        for(Transaction t: transactions){
            if(t.getCategory().equals("Student Loan Payment")){
                total += t.getAmount();
            }
        }
        return total;
    }

    private void setNewFrameContent(JFrame frame, JPanel panel){
        frame.setContentPane(panel);
        frame.revalidate();
        frame.repaint();
    }

    private static double calculateExpenseTransactionsTotal(ArrayList<Transaction> transactions){
        double total = 0.0;
        for(Transaction t : transactions){
            if(t.getTransactionType() == TransactionType.EXPENSE) {
                total = total + t.getAmount();
            }
        }
        return total;
    }
}
