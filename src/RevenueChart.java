import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.title.TextTitle;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Date;
import java.util.Vector;

public class RevenueChart {

    private Connection con;
    private JPanel panel;
    private JPanel buttonPanel;
    private JButton dailyButton;
    private JButton weeklyButton;
    private JButton monthlyButton;
    private JButton yearlyButton;
    private JLabel currentDateLabel;
    private String chartType = "Daily";
    private JButton viewProfitButton;
    private JComboBox<String> monthSelector;
    private JComboBox<String> yearSelector;

    private ChartPanel currentChartPanel;

    public RevenueChart(Connection con) {
        this.con = con;
        panel = new JPanel(new BorderLayout());

        buttonPanel = new JPanel(new FlowLayout());
        dailyButton = new JButton("Daily Sales");
        weeklyButton = new JButton("Weekly Sales");
        monthlyButton = new JButton("Monthly Sales");
        yearlyButton = new JButton("Yearly Sales");
        viewProfitButton = new JButton("View Total Profit");

        monthSelector = new JComboBox<>(getMonths());
        monthSelector.setSelectedItem(String.valueOf(04));
        
        yearSelector = new JComboBox<>(getYears());
        yearSelector.setSelectedItem(String.valueOf(2025));
        
        buttonPanel.add(viewProfitButton);
        buttonPanel.add(new JLabel("Select Month:"));
        buttonPanel.add(monthSelector);
        buttonPanel.add(new JLabel("Select Year:"));
        buttonPanel.add(yearSelector);
        currentDateLabel = new JLabel("Current Date: " + getCurrentDate());
        buttonPanel.add(currentDateLabel);

        buttonPanel.add(dailyButton);
        buttonPanel.add(weeklyButton);
        buttonPanel.add(monthlyButton);
        buttonPanel.add(yearlyButton);

        dailyButton.addActionListener(e -> {
            chartType = "Daily";
            updateChart();
        });
        weeklyButton.addActionListener(e -> {
            chartType = "Weekly";
            updateChart();
        });
        monthlyButton.addActionListener(e -> {
            chartType = "Monthly";
            updateChart();
        });
        yearlyButton.addActionListener(e -> {
            chartType = "Yearly";
            updateChart();
        });

        viewProfitButton.addActionListener(e -> showTotalProfit());

        panel.add(buttonPanel, BorderLayout.NORTH);
        updateChart();
    }

    private String[] getMonths() {
        return new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
    }

    private String[] getYears() {
        Vector<String> years = new Vector<>();
        int currentYear = 2025; // Default year
        for (int y = currentYear - 5; y <= currentYear + 1; y++) {
            years.add(String.valueOf(y));
        }
        return years.toArray(new String[0]);
    }

    private void showTotalProfit() {
        double totalProfit = 0.0;
        String query = "";

        String selectedMonth = (String) monthSelector.getSelectedItem();
        String selectedYear = (String) yearSelector.getSelectedItem();

        try {
            if ("Daily".equals(chartType)) {
                query = "SELECT SUM(subtotal - cost) AS profit FROM sales WHERE DATE(sale_date) = CURDATE()";
            } else if ("Weekly".equals(chartType)) {
                query = "SELECT SUM(subtotal - cost) AS profit FROM sales WHERE WEEK(sale_date, 1) = WEEK(CURDATE(), 1) AND YEAR(sale_date) = YEAR(CURDATE())";
            } else if ("Monthly".equals(chartType)) {
                query = "SELECT SUM(subtotal - cost) AS profit FROM sales WHERE MONTH(sale_date) = ? AND YEAR(sale_date) = ?";
            } else if ("Yearly".equals(chartType)) {
                query = "SELECT SUM(subtotal - cost) AS profit FROM sales WHERE YEAR(sale_date) = ?";
            }

            PreparedStatement pst = con.prepareStatement(query);

            if ("Monthly".equals(chartType)) {
                pst.setInt(1, Integer.parseInt(selectedMonth));
                pst.setInt(2, Integer.parseInt(selectedYear));
            } else if ("Yearly".equals(chartType)) {
                pst.setInt(1, Integer.parseInt(selectedYear));
            }

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                totalProfit = rs.getDouble("profit");
            }

            rs.close();
            pst.close();

            JOptionPane.showMessageDialog(panel,
                    String.format("Total %s Profit: ₱%.2f", chartType, totalProfit),
                    "Total Profit",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(panel,
                    "Error calculating profit.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private DefaultCategoryDataset createDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        try {
            String query = "";
            String selectedMonth = (String) monthSelector.getSelectedItem();
            String selectedYear = (String) yearSelector.getSelectedItem();

            if ("Daily".equals(chartType)) {
                query = "SELECT DATE(sale_date) AS label, SUM(subtotal) AS total FROM sales WHERE MONTH(sale_date) = ? AND YEAR(sale_date) = ? GROUP BY DATE(sale_date)";
            } else if ("Weekly".equals(chartType)) {
                query = "SELECT WEEK(sale_date, 1) AS label, SUM(subtotal) AS total FROM sales WHERE YEAR(sale_date) = ? GROUP BY label ORDER BY label";
            } else if ("Monthly".equals(chartType)) {
                query = "SELECT DAY(sale_date) AS day, SUM(subtotal) AS total FROM sales WHERE MONTH(sale_date) = ? AND YEAR(sale_date) = ? GROUP BY day ORDER BY day";
            } else if ("Yearly".equals(chartType)) {
                query = "SELECT MONTH(sale_date) AS label, SUM(subtotal) AS total FROM sales WHERE YEAR(sale_date) = ? GROUP BY label ORDER BY label";
            }

            PreparedStatement pst = con.prepareStatement(query);

            if ("Daily".equals(chartType)) {
                pst.setInt(1, Integer.parseInt(selectedMonth));
                pst.setInt(2, Integer.parseInt(selectedYear));
            } else if ("Weekly".equals(chartType)) {
                pst.setInt(1, Integer.parseInt(selectedYear));
            } else if ("Monthly".equals(chartType)) {
                pst.setInt(1, Integer.parseInt(selectedMonth));
                pst.setInt(2, Integer.parseInt(selectedYear));
            } else if ("Yearly".equals(chartType)) {
                pst.setInt(1, Integer.parseInt(selectedYear));
            }

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String label = "";

                if ("Weekly".equals(chartType)) {
                    label = "Week " + rs.getString("label");
                } else if ("Yearly".equals(chartType)) {
                    int monthNum = rs.getInt("label");
                    label = getMonthName(monthNum);
                } else if ("Monthly".equals(chartType)) {
                    label = getMonthName(Integer.parseInt(selectedMonth)) + " " + rs.getInt("day");
                } else {
                    label = rs.getString("label");
                }

                if (label != null) {
                    dataset.addValue(rs.getDouble("total"), chartType, label);
                }
            }

            rs.close();
            pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dataset;
    }

    private String getMonthName(int monthNumber) {
        return new SimpleDateFormat("MMMM").format(new GregorianCalendar(2000, monthNumber - 1, 1).getTime());
    }

    private void updateChart() {
        DefaultCategoryDataset dataset = createDataset();

        JFreeChart chart = ChartFactory.createLineChart(
                chartType + " Sales",
                "Date/Month/Day",
                "Total Sales (₱)",
                dataset,
                org.jfree.chart.plot.PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        chart.setBackgroundPaint(new Color(30, 30, 30));

        TextTitle chartTitle = chart.getTitle();
        chartTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        chartTitle.setPaint(new Color(255, 255, 255));
        
        
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(new Color(50, 50, 50));
        plot.setOutlinePaint(Color.WHITE);
        plot.getDomainAxis().setLabelPaint(Color.WHITE);
        plot.getRangeAxis().setLabelPaint(Color.WHITE);
        plot.getDomainAxis().setTickLabelPaint(Color.WHITE);
        plot.getRangeAxis().setTickLabelPaint(Color.WHITE);

        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesPaint(0, new Color(0xD32F2F));
        plot.setRenderer(renderer);

        ChartPanel newChartPanel = new ChartPanel(chart);
        newChartPanel.setBackground(new Color(30, 30, 30));
        newChartPanel.setPreferredSize(new Dimension(1178, 582));

        
        if (currentChartPanel != null) {
            panel.remove(currentChartPanel);
        }

        panel.add(newChartPanel, BorderLayout.CENTER);
        currentChartPanel = newChartPanel;

        panel.revalidate();
        panel.repaint();
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

    public JPanel getChartPanel() {
        return panel;
    }
}

