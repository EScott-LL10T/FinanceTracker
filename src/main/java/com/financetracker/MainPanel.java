package com.financetracker;


import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;

public class MainPanel extends JPanel {

    private final Profile profile;
    private ArrayList<Transaction> transactions;
    private static final Color FUNDS_COLOUR = new Color(128, 0, 128);
    private ArrayList<Transaction> periodTransactions;
    private String selectedPeriod = "Month";
    private JPanel splitPane;
    private static final String[] PERIODS = {"Day", "Month", "Year", "All time"};

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
        periodTransactions = getTransactionsForPeriod();

        // Main layout
        setLayout(new BorderLayout());

        // Header Panel
        JLabel headerLabel = new JLabel(profile.getName() + " Budget Dashboard", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 30));

        Font periodFont = new Font("Arial", Font.PLAIN, 16);

        JLabel periodLabel = new JLabel("Show:");
        periodLabel.setFont(periodFont);

        JComboBox<String> periodComboBox = new JComboBox<>(PERIODS);
        periodComboBox.setFont(periodFont);
        periodComboBox.setSelectedItem(selectedPeriod);

        JPanel periodPanel = new JPanel();
        periodPanel.add(periodLabel);
        periodPanel.add(periodComboBox);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(headerLabel, BorderLayout.CENTER);
        headerPanel.add(periodPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        splitPane = getSplitPane();   // assigns the field, no "JPanel" in front
        add(splitPane, BorderLayout.CENTER);

        // Added after setSelectedItem so it doesn't fire on startup
        periodComboBox.addActionListener(e -> {
            selectedPeriod = (String) periodComboBox.getSelectedItem();
            refreshDashboard();
        });


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

    private JPanel getSplitPane(){
        final int gap = 20; // fixed distance between the chart and the side panel

        JPanel contentPanel = new JPanel(new GridBagLayout());

        JPanel pieChartPanel = getPieChartPanel();
        JPanel transactionsPanel = getTransactionsPanel();

        // Preferred sizes set the starting 70/30 proportion; zero minimums let the frame shrink freely
        pieChartPanel.setPreferredSize(new Dimension(700, 500));
        transactionsPanel.setPreferredSize(new Dimension(300, 500));
        pieChartPanel.setMinimumSize(new Dimension(0, 0));
        transactionsPanel.setMinimumSize(new Dimension(0, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        gbc.gridy = 0;

        gbc.gridx = 0;
        gbc.weightx = 0.7;
        gbc.insets = new Insets(0, 0, 0, gap); // the gap lives here
        contentPanel.add(pieChartPanel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.3;
        gbc.insets = new Insets(0, 0, 0, 0);
        contentPanel.add(transactionsPanel, gbc);

        return contentPanel;
    }

    private JPanel getPieChartPanel() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

        // Income chunks are used up in this order after salary; unlisted ones come after, alphabetically
        List<String> incomePriority = List.of("Student Loan Payment income", "Other income");
        Comparator<String> incomeOrder = Comparator
                .comparingInt((String key) -> {
                    int i = incomePriority.indexOf(key);
                    return i == -1 ? incomePriority.size() : i;
                })
                .thenComparing(Comparator.naturalOrder());

        double totalSpent = 0.0;
        Map<String, Double> spentByCategory = new LinkedHashMap<>();
        Map<String, Double> incomeByCategory = new TreeMap<>(incomeOrder);

        for (Transaction t : periodTransactions) {
            if (t.getTransactionType() == TransactionType.INCOME) {
                incomeByCategory.merge(t.getCategory() + " income", t.getAmount(), Double::sum);
            } else {
                spentByCategory.merge(t.getCategory(), t.getAmount(), Double::sum);
                totalSpent += t.getAmount();
            }
        }

        double remainingSpend = totalSpent;

        // 1) Salary absorbs spending first
        double salary = Math.max(0, getPeriodFunds());
        double salaryUsed = Math.min(salary, remainingSpend);
        remainingSpend -= salaryUsed;
        double salaryLeft = salary - salaryUsed;

        // 2) Then income chunks, in priority order
        Map<String, Double> incomeLeft = new LinkedHashMap<>();
        for (Map.Entry<String, Double> e : incomeByCategory.entrySet()) {
            double used = Math.min(e.getValue(), remainingSpend);
            remainingSpend -= used;
            double left = e.getValue() - used;
            if (left > 0.005) {
                incomeLeft.put(e.getKey(), left);
            }
        }

        // 3) Whatever is still unaccounted for is the overspend
        double overspent = remainingSpend > 0.005 ? remainingSpend : 0.0;

        // Funds slices FIRST
        if (salaryLeft > 0.005) {
            dataset.setValue("Available", salaryLeft);
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

    private JPanel getTransactionsPanel(){
        JPanel transactionsPanel = new JPanel();

        Font transactionsFont = new Font("Arial", Font.PLAIN, 16);
        transactionsPanel.setLayout(new BoxLayout(transactionsPanel, BoxLayout.Y_AXIS));
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Financial Summary");
        titledBorder.setTitleFont(new Font("Arial", Font.BOLD, 20));
        transactionsPanel.setBorder(titledBorder);

        boolean isStudent = profile.getRole().equals("Student");

        double periodFunds = getPeriodFunds();
        double totalSpend = calculateExpenseTransactionsTotal(periodTransactions);
        double totalIncome = calculateIncomeTransactionsTotal(periodTransactions);
        double availableFunds = periodFunds + totalIncome - totalSpend;

        if(isStudent){
            double loanPayments = calculateStudentLoanIncomeTotal(periodTransactions);
            double loanAmount = periodFunds + loanPayments;
            totalIncome = totalIncome - loanPayments; // leave only non-loan income for the "Other income" row

            addSummaryRow(transactionsPanel, "Loan amount", loanAmount, transactionsFont, null);
        }else{
            double monthlyGross = profile.getSalary() / 12;
            double periodTax = profile.getSalary() * profile.calculateTax() / 12 * getPeriodMultiplier();
            double periodNationalInsurance = monthlyGross * profile.calculateNationalInsurance() * getPeriodMultiplier();
            addSummaryRow(transactionsPanel, "Salary", getPeriodSalary(), transactionsFont, null);
            addSummaryRow(transactionsPanel, "Tax", periodTax, transactionsFont, null);
            addSummaryRow(transactionsPanel, "National Insurance", periodNationalInsurance, transactionsFont, null);
        }
        if(totalIncome > 0.005){
            addSummaryRow(transactionsPanel, "Other income", totalIncome, transactionsFont, null);
        }

        addSummaryRow(transactionsPanel, "Total Spent", totalSpend, transactionsFont, null);
        addSummaryRow(transactionsPanel, "Available", availableFunds, transactionsFont,
                availableFunds < 0 ? Color.RED : null);

        return transactionsPanel;
    }

    // Loan for students, monthly take-home pay for everyone else
    private double getMonthlyFunds(){
        double salary = profile.getSalary();
        if(profile.getRole().equals("Student")){
            return salary;
        }
        double deductions = profile.calculateTax() + profile.calculateNationalInsurance();
        return (salary - (salary * deductions)) / 12;
    }

    private void addSummaryRow(JPanel panel, String labelText, double amount, Font font, Color valueColour){
        if(panel.getComponentCount() > 0){
            panel.add(Box.createVerticalStrut(5));
        }

        JLabel label = new JLabel(labelText);
        label.setFont(font);

        String amountText = (amount < 0 ? "-" : "") + String.format("£%.2f", Math.abs(amount));
        JLabel amountLabel = new JLabel(amountText);
        amountLabel.setFont(font);
        if(valueColour != null){
            amountLabel.setForeground(valueColour);
        }

        panel.add(label);
        panel.add(amountLabel);
    }

    private static double calculateStudentLoanIncomeTotal(ArrayList<Transaction> transactions){
        double total = 0.0;
        for(Transaction t : transactions){
            if(t.getTransactionType() == TransactionType.INCOME
                    && t.getCategory().equals("Student Loan Payment")){
                total = total + t.getAmount();
            }
        }
        return total;
    }

    private static double calculateIncomeTransactionsTotal(ArrayList<Transaction> transactions){
        double total = 0.0;
        for(Transaction t : transactions){
            if(t.getTransactionType() == TransactionType.INCOME){
                total = total + t.getAmount();
            }
        }
        return total;
    }

    private void refreshDashboard(){
        periodTransactions = getTransactionsForPeriod();

        remove(splitPane);
        splitPane = getSplitPane();
        add(splitPane, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private ArrayList<Transaction> getTransactionsForPeriod(){
        return switch(selectedPeriod){
            case "Day" -> getDailyTransactions();
            case "Year" -> getYearlyTransactions();
            case "All time" -> new ArrayList<>(transactions);
            default -> getMonthlyTransactions();
        };
    }

    private ArrayList<Transaction> getDailyTransactions(){
        ArrayList<Transaction> dailyTransactions = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for(Transaction t: transactions){
            if(t.getTimeOfTransaction().toLocalDate().equals(today)){
                dailyTransactions.add(t);
            }
        }
        return dailyTransactions;
    }

    private ArrayList<Transaction> getYearlyTransactions(){
        ArrayList<Transaction> yearlyTransactions = new ArrayList<>();
        int year = LocalDate.now().getYear();
        for(Transaction t: transactions){
            if(t.getTimeOfTransaction().getYear() == year){
                yearlyTransactions.add(t);
            }
        }
        return yearlyTransactions;
    }

    // How many "months of funds" the selected period covers
    private double getPeriodMultiplier(){
        return switch(selectedPeriod){
            case "Day" -> 1.0 / LocalDate.now().lengthOfMonth();
            case "Year" -> LocalDate.now().getMonthValue();   // months so far this year
            case "All time" -> getMonthsOfHistory();
            default -> 1.0;
        };
    }

    private double getPeriodFunds(){
        return getMonthlyFunds() * getPeriodMultiplier();
    }

    private double getPeriodSalary(){
        return profile.getSalary() / 12 * getPeriodMultiplier();
    }

    // Months from the earliest transaction to now, counting both ends
    private int getMonthsOfHistory(){
        YearMonth earliest = YearMonth.now();
        for(Transaction t: transactions){
            YearMonth month = YearMonth.from(t.getTimeOfTransaction());
            if(month.isBefore(earliest)){
                earliest = month;
            }
        }
        return (int) ChronoUnit.MONTHS.between(earliest, YearMonth.now()) + 1;
    }
}
