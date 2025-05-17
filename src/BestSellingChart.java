import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.category.CategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Calendar;
import org.jfree.chart.axis.CategoryLabelPositions;

public class BestSellingChart {
    private Connection con;
    private JPanel mainPanel;
    private JPanel chartPanel;
    private String selectedMonth = "01"; // Default to January
    private String selectedYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR)); // Default to current year

    public BestSellingChart(Connection con) {
        this.con = con;
        mainPanel = new JPanel(new BorderLayout());
        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBackground(new Color(18, 18, 18));
        mainPanel.setBackground(new Color(18, 18, 18));

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBackground(new Color(30, 30, 30));

        // Add month buttons
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                           "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        for (int i = 0; i < months.length; i++) {
            final int monthIndex = i + 1;
            JButton btn = new JButton(months[i]);
            btn.setFocusPainted(false);
            btn.setBackground(new Color(40, 40, 40));
            btn.setForeground(Color.WHITE);
            btn.addActionListener(e -> {
                selectedMonth = String.format("%02d", monthIndex);
                updateChart();
            });
            controlPanel.add(btn);
        }

        // Year selector
        JComboBox<String> yearBox = new JComboBox<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int y = currentYear; y >= currentYear - 5; y--) {
            yearBox.addItem(String.valueOf(y));
        }
        yearBox.setSelectedItem(selectedYear);
        yearBox.addActionListener(e -> {
            selectedYear = (String) yearBox.getSelectedItem();
            updateChart();
        });

        JLabel yearLabel = new JLabel("Year:");
        yearLabel.setForeground(Color.WHITE);
        controlPanel.add(yearLabel);
        controlPanel.add(yearBox);

        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(chartPanel, BorderLayout.CENTER);

        updateChart(); // Initial load
    }

    private CategoryDataset createDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try {
            System.out.println("Selected Month: " + selectedMonth);  // Debugging line
            System.out.println("Selected Year: " + selectedYear);    // Debugging line

            String query = "SELECT sp.vape_name, SUM(sp.qty) AS total_qty " +
                           "FROM sales_product sp " +
                           "JOIN sales s ON sp.sales_id = s.id " +
                           "WHERE MONTH(s.sale_date) = ? AND YEAR(s.sale_date) = ? " +
                           "GROUP BY sp.vape_name " +
                           "ORDER BY total_qty DESC " +
                           "LIMIT 10";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, Integer.parseInt(selectedMonth));
            pst.setInt(2, Integer.parseInt(selectedYear));
            ResultSet rs = pst.executeQuery();

            boolean hasData = false; // Flag to track if we get data
            while (rs.next()) {
                dataset.addValue(rs.getInt("total_qty"), "Quantity Sold", rs.getString("vape_name"));
                hasData = true;
            }

            if (!hasData) {
                System.out.println("No data found for the selected month and year.");
            }

            rs.close();
            pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (dataset.getColumnCount() == 0) {
            System.out.println("No data in the dataset!");  // Debugging line
        } else {
            System.out.println("Dataset has " + dataset.getColumnCount() + " columns.");
        }

        return dataset;
    }
private void createChart() {
    JFreeChart chart = ChartFactory.createBarChart(
            "Best-Selling Products (" + selectedMonth + "/" + selectedYear + ")",
            "Product Name",
            "Quantity Sold",
            createDataset(),
            PlotOrientation.VERTICAL,
            false,
            true,
            false
    );

    chart.setBackgroundPaint(new Color(18, 18, 18));
    TextTitle chartTitle = chart.getTitle();
    chartTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
    chartTitle.setPaint(Color.WHITE);

    CategoryPlot plot = chart.getCategoryPlot();
    plot.setBackgroundPaint(new Color(30, 30, 30));
    plot.setRangeGridlinePaint(Color.GRAY);
    plot.setOutlineVisible(false);
    plot.getDomainAxis().setTickLabelPaint(Color.WHITE);
    plot.getDomainAxis().setLabelPaint(Color.WHITE);
    plot.getRangeAxis().setTickLabelPaint(Color.WHITE);
    plot.getRangeAxis().setLabelPaint(Color.WHITE);

    // Fix to ensure each product has a separate bar
    BarRenderer renderer = (BarRenderer) plot.getRenderer();
    renderer.setSeriesPaint(0, new Color(0xF44336)); // neon red
    renderer.setDrawBarOutline(false);
    renderer.setShadowVisible(false);

    // adjust the category axis to ensure all labels fit
    plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.STANDARD);

    chartPanel.setPreferredSize(new Dimension(1175, 582));  // adjust the size to make space for more bars

    chartPanel.removeAll();
    ChartPanel chartPanelDisplay = new ChartPanel(chart);
    chartPanelDisplay.setPreferredSize(new Dimension(1050, 500));
    chartPanel.add(chartPanelDisplay, BorderLayout.CENTER);
    chartPanel.revalidate();
    chartPanel.repaint();
}


    public void updateChart() {
        // Debugging the data
        System.out.println("Updating chart for: " + selectedMonth + "/" + selectedYear);
        createChart();
    }

    public JPanel getChartPanel() {
        return mainPanel;
    }
}
