
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import org.apache.pdfbox.rendering.PDFRenderer;

public class AdminAccount extends javax.swing.JFrame {

    BestSellingChart bestSellingChart;
    RevenueChart revenueChart;

    Connection conn;
    PreparedStatement pstt;
    ResultSet rss;
    String ImgPath = null;

    public AdminAccount() {
        initComponents();
        Connect();
        loadReportsForAdmin();
        loadAccounts();
        startAutoRefresh();
        loadAccounttNames();
        loadProductNames();
        Show_Products();

        HoverEffect hover = new HoverEffect(new Color(240, 240, 240), new Color(255, 77, 77));

        // apply to multiple buttons
        hover.applyTo(Notification1);
        hover.applyTo(Dashboard);
        hover.applyTo(Notification2);
        hover.applyTo(Reports);
        hover.applyTo(Reports);
        hover.applyTo(Notification);

        // Initialize label and add to accPanel (declared in NetBeans GUI builder)
        accountCountLabel = new JLabel("Total Accounts: 0");
        accPanel.setLayout(new BorderLayout());
        accPanel.add(accountCountLabel, BorderLayout.CENTER);
        accountCountLabel.setFont(new Font("Tahoma", Font.BOLD, 24));

        displayTotalAccounts(); // Load account count from DB

        productLabel = new JLabel("Total Products: 0");
        productPanel.setLayout(new BorderLayout());
        productPanel.add(productLabel, BorderLayout.CENTER);
        productLabel.setFont(new Font("Tahoma", Font.BOLD, 24));
        //productLabel.setFont(new Font("Tahoma", Font.BOLD, 14));

        displayTotalProducts();

        bestLabel = new JLabel("Total Products: 0");
        bestPanel.setLayout(new BorderLayout());
        bestPanel.add(bestLabel, BorderLayout.CENTER);
        bestLabel.setFont(new Font("Tahoma", Font.BOLD, 24));

        displayBestSellingProduct();

        // Display Best Selling Chart
        bestSellingChart = new BestSellingChart(conn);
        JPanel chartContainer = bestSellingChart.getChartPanel();
        chartPanel3.setLayout(new BorderLayout());
        chartPanel3.add(chartContainer, BorderLayout.CENTER);
        chartPanel3.revalidate();
        chartPanel3.repaint();

        // Display Revenue Chart
        revenueChart = new RevenueChart(conn);
        JPanel chartContainerr = revenueChart.getChartPanel();
        chartPanel1.setLayout(new BorderLayout());
        chartPanel1.add(chartContainerr, BorderLayout.CENTER);
        chartPanel1.revalidate();
        chartPanel1.repaint();

        // Button listener
        BTPermPs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxChanged();
            }
        });

        this.setVisible(true);
    }

    public void Connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost/information_system", "root", "");
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(StaffDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void displayTotalAccounts() {
        String query = "SELECT COUNT(*) AS account_count FROM accounts";
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int accountCount = rs.getInt("account_count");
                accountCountLabel.setText("       " + accountCount + "       ");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            accountCountLabel.setText("Error fetching account count.");
        }
    }

    // Optional: for testing or access elsewhere
    public JPanel getAccountPanel() {
        return accPanel;
    }

    private void displayTotalProducts() {
        String query = "SELECT COUNT(*) AS product FROM product_table";
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int accountCount = rs.getInt("product");
                productLabel.setText("       " + accountCount + "       ");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            productLabel.setText("Error fetching account count.");
        }
    }

    // Optional: for testing or access elsewhere
    public JPanel getProductPanel() {
        return productPanel;
    }

    private void displayBestSellingProduct() {
        String query = "SELECT vape_name, SUM(qty) AS total_sold "
                + "FROM sales_product "
                + "GROUP BY vape_name "
                + "ORDER BY total_sold DESC "
                + "LIMIT 1";

        try (PreparedStatement pst = conn.prepareStatement(query); ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                String bestProduct = rs.getString("vape_name");
                int totalSold = rs.getInt("total_sold");
                bestLabel.setText(" " + bestProduct + " (" + totalSold + " sold)");
            } else {
                bestLabel.setText("No sales data available.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            bestLabel.setText("Error fetching data.");
        }
    }

// Optional getter if you want to embed the panel somewhere else
    public JPanel getBestSellingPanel() {
        return bestPanel;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    private void loadAccounts() {
        try {
            // Prepare SQL query
            pstt = conn.prepareStatement("SELECT * FROM accounts");
            rss = pstt.executeQuery();
            java.sql.ResultSetMetaData rsss = rss.getMetaData();
            int columnCount = rsss.getColumnCount();

            // Get table model and clear existing data
            DefaultTableModel def = (DefaultTableModel) accounts.getModel();
            def.setRowCount(0);

            // Fetch and add data to table
            while (rss.next()) {
                Vector<Object> rowData = new Vector<>();
                rowData.add(rss.getInt("ID"));                 // ID (assuming it's an integer)
                rowData.add(rss.getString("Username"));
                rowData.add(rss.getString("Email")); // Recipient role
                rowData.add(rss.getString("Password"));       // File name
                rowData.add(rss.getString("Permission"));          // Status
                // Timestamp (with date and time)

                def.addRow(rowData);
            }

        } catch (SQLException ex) {
            Logger.getLogger(StaffDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void Header1() {
        JTableHeader heads = Product_table.getTableHeader();

        //to enforce font, background, and foreground
        heads.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value.toString());
                label.setOpaque(true);
                label.setBackground(Color.BLACK);  // set background color
                label.setForeground(Color.white); // kulay ng headers
                label.setFont(label.getFont().deriveFont(com.itextpdf.text.Font.BOLD, 14f));
                label.setHorizontalAlignment(SwingConstants.CENTER); // pampagitna
                return label;
            }
        });
        //header row height
        heads.setPreferredSize(new Dimension(heads.getWidth(), 30));

        //column widths
        TableColumnModel columnModel = Product_table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(20);
        columnModel.getColumn(1).setPreferredWidth(50);
        columnModel.getColumn(2).setPreferredWidth(50);
        columnModel.getColumn(3).setPreferredWidth(50);
        columnModel.getColumn(4).setPreferredWidth(50);
        columnModel.getColumn(5).setPreferredWidth(50);
    }

    public void loadReportsForAdmin() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/information_system", "root", "");
            String sql = "SELECT id, sender_role, file_name, status, timestamp FROM notifications WHERE recipient_role = 'admin' ORDER BY timestamp DESC";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            // Get JTable model and reset it before adding new data
            DefaultTableModel model = (DefaultTableModel) table5.getModel();
            model.setRowCount(0); // Clear table before inserting new rows

            // Check if there is data
            if (!rs.isBeforeFirst()) {
                System.out.println("No data found in notifications table.");
            }

            // Populate JTable with database records
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"), // Report ID
                    rs.getString("sender_role"), // Sender role (staff)
                    rs.getString("file_name"), // File name
                    rs.getString("status"), // Report status
                    rs.getTimestamp("timestamp") // Timestamp
                });

                System.out.println("Added report: " + rs.getString("file_name")); // Print file name instead of 'message'
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

// Method to open the selected report and display the PDF from the database
    public void openSelectedReport() {
        int selectedRow = table5.getSelectedRow(); // Get selected row

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a report to open.");
            return; // No row selected
        }

        int reportId = (int) table5.getValueAt(selectedRow, 0); // Column 0 = ID (primary key)

        // Load PDF from database (as BLOB)
        byte[] fileData = getFileFromDatabase(reportId);

        if (fileData == null) {
            JOptionPane.showMessageDialog(null, "No PDF data found for this report.");
            return;
        }

        try {
            // Load PDF document from byte array
            PDDocument document = PDDocument.load(new ByteArrayInputStream(fileData));
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            // Render the first page as an image
            BufferedImage image = pdfRenderer.renderImageWithDPI(0, 100, ImageType.RGB); // 100 DPI

            // Convert BufferedImage to ImageIcon
            ImageIcon icon = new ImageIcon(image);
            JLabel label = new JLabel(icon);

            // Create JFrame for the preview
            JFrame previewFrame = new JFrame("PDF Preview");
            previewFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            previewFrame.setSize(800, 600);
            previewFrame.setLocationRelativeTo(null);

            // Add image to a scrollable panel
            JScrollPane scrollPane = new JScrollPane(label);
            previewFrame.add(scrollPane, BorderLayout.CENTER);

            previewFrame.setVisible(true);

            // Close PDF document when frame closes
            previewFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    try {
                        document.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            // Update the status to 'read' after opening the file
            updateReportStatusToRead(reportId);

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading the PDF.");
        }
    }

// Method to retrieve the file data (BLOB) from the database
    private byte[] getFileFromDatabase(int reportId) {
        byte[] fileData = null;
        String dbUrl = "jdbc:mysql://localhost:3306/information_system";
        String dbUsername = "root";
        String dbPassword = "";

        try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword); PreparedStatement pst = con.prepareStatement("SELECT file_data FROM notifications WHERE id = ?")) {

            pst.setInt(1, reportId);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    fileData = rs.getBytes("file_data"); // Make sure this is the correct BLOB column
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fileData;
    }

// Method to update the report status to 'read'
    private void updateReportStatusToRead(int reportId) {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/information_system", "root", ""); PreparedStatement pst = con.prepareStatement("UPDATE notifications SET status = 'read' WHERE id = ?")) {

            pst.setInt(1, reportId);
            pst.executeUpdate();

            // Update the JTable to reflect the status change
            DefaultTableModel model = (DefaultTableModel) table5.getModel();
            model.setValueAt("read", table5.getSelectedRow(), 3);

            System.out.println("Report status updated to 'read' for ID: " + reportId);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating report status.");
        }
    }

    private void Header4() {
        JTableHeader heads = accounts.getTableHeader();

        //to enforce font, background, and foreground
        heads.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value.toString());
                label.setOpaque(true);
                label.setBackground(Color.RED);  // set background color
                label.setForeground(Color.white); // kulay ng headers
                label.setFont(label.getFont().deriveFont(com.itextpdf.text.Font.BOLD, 14f));
                label.setHorizontalAlignment(SwingConstants.CENTER); // pampagitna
                return label;
            }
        });
        //header row height
        heads.setPreferredSize(new Dimension(heads.getWidth(), 30));

        //column widths
        TableColumnModel columnModel = accounts.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);
        columnModel.getColumn(1).setPreferredWidth(50);
        columnModel.getColumn(2).setPreferredWidth(50);
        columnModel.getColumn(3).setPreferredWidth(50);
        columnModel.getColumn(4).setPreferredWidth(50);
    }

    public void fetchAccountDetails() {
        try {
            Object selectedObject = comboo.getSelectedItem(); // Get selected item safely

            if (selectedObject == null || selectedObject.toString().equals("-- Select a User --")) {
                return; // Exit if no valid product is selected
            }

            String selectedProduct = selectedObject.toString();

            pstt = conn.prepareStatement("SELECT * FROM accounts WHERE Username=?");
            pstt.setString(1, selectedProduct);
            rss = pstt.executeQuery();

            if (rss.next()) {
                BTidAcc.setText(rss.getString("ID"));
                BTUserN.setText(rss.getString("Username"));
                BTEmailE.setText(rss.getString("Email"));
                BTPassP.setText(rss.getString("Password"));
                BTPermP.setText(rss.getString("permission"));
                comboo.setSelectedItem(rss.getString("permission"));

            } else {
                JOptionPane.showMessageDialog(null, "No User found.");
            }
        } catch (SQLException ex) {
            Logger.getLogger(StaffDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadAccounttNames() {
        try {

            pstt = conn.prepareStatement("SELECT Username FROM accounts");
            rss = pstt.executeQuery();

            comboo.removeAllItems(); // Clear existing items
            comboo.addItem("-- Select a User --"); // Add placeholder
            comboo.revalidate();
            comboo.repaint();

            while (rss.next()) {
                comboo.addItem(rss.getString("Username")); // Add product name
            }

            // Only call fetchProductDetails if products exist
            if (comboo.getItemCount() > 1) {
                comboo.setSelectedIndex(1); // Select the first actual product
                fetchAccountDetails(); // Fetch details for the first product
            }
        } catch (SQLException ex) {
            Logger.getLogger(StaffDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable() {
        try {
            DefaultTableModel model = (DefaultTableModel) accounts.getModel();
            model.setRowCount(0); // Clear the table before reloading data

            pstt = conn.prepareStatement("SELECT * FROM accounts");
            rss = pstt.executeQuery();

            while (rss.next()) {
                Object[] rowData = {
                    rss.getString("ID"),
                    rss.getString("Username"),
                    rss.getString("Email"),
                    rss.getString("Password"),
                    rss.getString("permission"),};
                model.addRow(rowData);
            }

        } catch (SQLException ex) {
            Logger.getLogger(AdminAccount.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void ClearText() {
        BTidAcc.setText("");
        BTPassP.setText("");
        BTEmailE.setText("");
        BTUserN.setText("");
        BTPermP.setText("");
    }

    private void clearFields() {
        btnID.setText("");
        btnNAME.setText("");
        btnPRICE.setText("");
        btnCOST.setText("");
        btnDESC.setText("");
        Stock.setText("");
        btnIMAGE.setIcon(null);
        ImgPath = null;
    }

    private void comboBoxChanged() {
        String selectedItem = (String) BTPermPs.getSelectedItem();

        // Avoid confirming on the default item
        if (!selectedItem.equals("Select an option")) {
            int result = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure that you want this user to set permission as " + selectedItem + "?",
                    "Confirm Permission Change",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (result == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, "Permission set to: " + selectedItem);
            } else {
                // Optional: Reset to default if user cancels
                BTPermPs.setSelectedIndex(0);
            }
        }
    }

    public void Account() {
        try {
            DefaultTableModel model = (DefaultTableModel) Product_table.getModel();
            model.setRowCount(0); // Clear the table before reloading data

            pstt = conn.prepareStatement("SELECT * FROM product_table");
            rss = pstt.executeQuery();

            while (rss.next()) {
                Object[] rowData = {
                    rss.getString("id"),
                    rss.getString("Product_Name"),
                    rss.getString("Price"),
                    rss.getString("Cost"),
                    rss.getString("Description"),
                    rss.getString("Stock")
                };
                model.addRow(rowData);
            }

        } catch (SQLException ex) {
            Logger.getLogger(StaffDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ImageIcon ResizeImage(String imagePath, byte[] pic) {
        ImageIcon myImage = null;

        if (imagePath != null) {
            myImage = new ImageIcon(imagePath);
        } else {
            myImage = new ImageIcon(pic);
        }
        Image img = myImage.getImage();
        Image img2 = img.getScaledInstance(btnIMAGE.getWidth(), btnIMAGE.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon image = new ImageIcon(img2);
        return image;

    }

    public void loadProductNames() {
        try {

            pstt = conn.prepareStatement("SELECT Product_Name FROM product_table");
            rss = pstt.executeQuery();

            coomboo.removeAllItems(); // Clear existing items
            coomboo.addItem("-- Select a Product --"); // Add placeholder

            while (rss.next()) {
                coomboo.addItem(rss.getString("Product_Name")); // Add product name
            }

            // Only call fetchProductDetails if products exist
            if (coomboo.getItemCount() > 1) {
                coomboo.setSelectedIndex(1); // Select the first actual product
                fetchProductDetails(); // Fetch details for the first product
            }
        } catch (SQLException ex) {
            Logger.getLogger(StaffDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void fetchProductDetails() {
        try {
            Object selectedObject = coomboo.getSelectedItem(); // Get selected item safely

            if (selectedObject == null || selectedObject.toString().equals("-- Select a Product --")) {
                return; // Exit if no valid product is selected
            }

            String selectedProduct = selectedObject.toString();

            pstt = conn.prepareStatement("SELECT * FROM product_table WHERE Product_Name=?");
            pstt.setString(1, selectedProduct);
            rss = pstt.executeQuery();

            if (rss.next()) {
                btnID.setText(rss.getString("id"));
                btnNAME.setText(rss.getString("Product_Name"));
                btnPRICE.setText(rss.getString("Price"));
                btnCOST.setText(rss.getString("Cost"));
                btnDESC.setText(rss.getString("Description"));
                Stock.setText(String.valueOf(rss.getInt("Stock")));
                // Handle image data
                byte[] imgData = rss.getBytes("Image");
                if (imgData != null) {
                    ImageIcon imageIcon = new ImageIcon(imgData);
                    Image img = imageIcon.getImage().getScaledInstance(btnIMAGE.getWidth(), btnIMAGE.getHeight(), Image.SCALE_SMOOTH);
                    btnIMAGE.setIcon(new ImageIcon(img)); // Set the image to the JLabel
                } else {
                    btnIMAGE.setIcon(null);
                    btnIMAGE.setText("No Image");
                }

                // Refresh the JTable with the latest data
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(null, "No record found.");
            }
        } catch (SQLException ex) {
            Logger.getLogger(StaffDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Show_Products() {
        ArrayList<Product> list = getProductList();
        DefaultTableModel model = (DefaultTableModel) Product_table.getModel();
        Product_table.setDefaultRenderer(Object.class, new LowStockTableRenderer());
        model.setRowCount(0);  // Clear existing rows in the table

        Object[] row = new Object[5];
        for (int i = 0; i < list.size(); i++) {
            row[0] = list.get(i).getId();
            row[1] = list.get(i).getName();
            row[2] = list.get(i).getPrice();
            row[3] = list.get(i).getDescription();
            row[4] = list.get(i).getStock();

            model.addRow(row);  // Add the row to the table
        }
    }

    public ArrayList<Product> getProductList() {
        ArrayList<Product> productList = new ArrayList<Product>();
        String query = "SELECT * FROM product_table";

        Statement st;
        ResultSet rs;
        try {

            st = conn.createStatement();
            rs = st.executeQuery(query);
            Product product;

            while (rs.next()) {

                product = new Product(rs.getInt("id"), rs.getString("Product_Name"), rs.getString("Price"), rs.getString("Description"), rs.getBytes("Image"), rs.getInt("Stock"));
                productList.add(product);
            }

        } catch (SQLException ex) {
            Logger.getLogger(StaffDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }

        return productList;
    }

    private void Header() {
        JTableHeader heads = table5.getTableHeader();

        //to enforce font, background, and foreground
        heads.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value.toString());
                label.setOpaque(true);
                label.setBackground(Color.RED);  // set background color
                label.setForeground(Color.white); // kulay ng headers
                label.setFont(label.getFont().deriveFont(com.itextpdf.text.Font.BOLD, 14f));
                label.setHorizontalAlignment(SwingConstants.CENTER); // pampagitna
                return label;
            }
        });
        //header row height
        heads.setPreferredSize(new Dimension(heads.getWidth(), 30));

        //column widths
        TableColumnModel columnModel = table5.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(20);
        columnModel.getColumn(1).setPreferredWidth(50);
        columnModel.getColumn(2).setPreferredWidth(50);
        columnModel.getColumn(3).setPreferredWidth(50);
        columnModel.getColumn(4).setPreferredWidth(60);
    }

    private void startAutoRefresh() {
        Timer timer = new Timer(4000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chartPanel3.revalidate();
                chartPanel1.revalidate();
                loadReportsForAdmin();
                refreshTable();
                Account();
            }
        });
        timer.start();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        navPanel = new javax.swing.JPanel();
        Dashboard = new javax.swing.JButton();
        Reports = new javax.swing.JButton();
        Notification = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        Notification1 = new javax.swing.JButton();
        Notification2 = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        accPanel = new javax.swing.JPanel();
        totalUsersLabel = new javax.swing.JLabel();
        accountCountLabel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        accounts = new javax.swing.JTable();
        BTPassP = new javax.swing.JTextField();
        BTUserN = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        BTPermP = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        BTaddAcc = new javax.swing.JButton();
        BTupdAcc = new javax.swing.JButton();
        BTDelAcc = new javax.swing.JButton();
        comboo = new javax.swing.JComboBox<>();
        BTidAcc = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        ClearText = new javax.swing.JButton();
        BTPermPs = new javax.swing.JComboBox<>();
        BTEmailE = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table5 = new javax.swing.JTable();
        jButton3 = new javax.swing.JButton();
        pogi = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        chartPanel3 = new javax.swing.JPanel();
        bestPanel = new javax.swing.JPanel();
        bestLabel = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        chartPanel1 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Product_table = new javax.swing.JTable();
        jButton2 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        btnID = new javax.swing.JTextField();
        btnNAME = new javax.swing.JTextField();
        btnPRICE = new javax.swing.JTextField();
        Stock = new javax.swing.JTextField();
        jButton6 = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        btnDESC = new javax.swing.JTextArea();
        btnIMAGE = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        btIMG = new javax.swing.JButton();
        coomboo = new javax.swing.JComboBox<>();
        jLabel17 = new javax.swing.JLabel();
        btnCOST = new javax.swing.JTextField();
        productPanel = new javax.swing.JPanel();
        productLabel = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        Home = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(102, 102, 102));

        navPanel.setBackground(new java.awt.Color(0, 0, 0));

        Dashboard.setBackground(java.awt.Color.white);
        Dashboard.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        Dashboard.setForeground(java.awt.Color.black);
        Dashboard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/data-management_9688633 (1).png"))); // NOI18N
        Dashboard.setText("Users");
        Dashboard.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        Dashboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DashboardActionPerformed(evt);
            }
        });

        Reports.setBackground(java.awt.Color.white);
        Reports.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        Reports.setForeground(java.awt.Color.black);
        Reports.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/report (1).png"))); // NOI18N
        Reports.setText("Reports");
        Reports.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        Reports.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ReportsActionPerformed(evt);
            }
        });

        Notification.setBackground(java.awt.Color.white);
        Notification.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        Notification.setForeground(java.awt.Color.black);
        Notification.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/award_16553148 (1).png"))); // NOI18N
        Notification.setText("Best Selling");
        Notification.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        Notification.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NotificationActionPerformed(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(255, 0, 0));
        jButton1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButton1.setForeground(java.awt.Color.white);
        jButton1.setText("Log Out");
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        Notification1.setBackground(java.awt.Color.white);
        Notification1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        Notification1.setForeground(java.awt.Color.black);
        Notification1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/data-analysis (1).png"))); // NOI18N
        Notification1.setText("Dashboard");
        Notification1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        Notification1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Notification1ActionPerformed(evt);
            }
        });

        Notification2.setBackground(java.awt.Color.white);
        Notification2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        Notification2.setForeground(java.awt.Color.black);
        Notification2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/electronic-cigarette (1).png"))); // NOI18N
        Notification2.setText("Products");
        Notification2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        Notification2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Notification2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout navPanelLayout = new javax.swing.GroupLayout(navPanel);
        navPanel.setLayout(navPanelLayout);
        navPanelLayout.setHorizontalGroup(
            navPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(navPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(navPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Notification1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Dashboard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Notification2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Reports, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Notification, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, navPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );
        navPanelLayout.setVerticalGroup(
            navPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(navPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Notification1, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Dashboard, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Notification2, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Reports, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Notification, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);

        jPanel5.setBackground(new java.awt.Color(0, 0, 0));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/collab.jpg"))); // NOI18N
        jLabel2.setText("jLabel2");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("tab2", jPanel5);

        jPanel6.setBackground(new java.awt.Color(50, 50, 50));
        jPanel6.setAutoscrolls(true);

        accPanel.setBackground(java.awt.Color.white);
        accPanel.setForeground(java.awt.Color.black);

        totalUsersLabel.setForeground(java.awt.Color.black);

        accountCountLabel.setForeground(java.awt.Color.black);

        javax.swing.GroupLayout accPanelLayout = new javax.swing.GroupLayout(accPanel);
        accPanel.setLayout(accPanelLayout);
        accPanelLayout.setHorizontalGroup(
            accPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(accPanelLayout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(totalUsersLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(accountCountLabel)
                .addContainerGap(52, Short.MAX_VALUE))
        );
        accPanelLayout.setVerticalGroup(
            accPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(accPanelLayout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(accPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(accountCountLabel)
                    .addComponent(totalUsersLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setForeground(java.awt.Color.white);
        jLabel3.setText("Total Users:");

        accounts.setBackground(java.awt.Color.black);
        accounts.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        accounts.setForeground(java.awt.Color.white);
        accounts.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID", "Username", "Email", "Password", "Permission"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        accounts.setName(""); // NOI18N
        accounts.setPreferredSize(new java.awt.Dimension(375, 1000));
        accounts.setRowHeight(30);
        accounts.setSelectionBackground(java.awt.Color.white);
        accounts.setSelectionForeground(java.awt.Color.red);
        accounts.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                accountsPropertyChange(evt);
            }
        });
        jScrollPane3.setViewportView(accounts);
        if (accounts.getColumnModel().getColumnCount() > 0) {
            accounts.getColumnModel().getColumn(0).setResizable(false);
            accounts.getColumnModel().getColumn(1).setResizable(false);
            accounts.getColumnModel().getColumn(2).setResizable(false);
            accounts.getColumnModel().getColumn(3).setResizable(false);
            accounts.getColumnModel().getColumn(4).setResizable(false);
        }

        BTUserN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BTUserNActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel7.setForeground(java.awt.Color.white);
        jLabel7.setText("Password:");

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel8.setForeground(java.awt.Color.white);
        jLabel8.setText("Username:");

        BTPermP.setEnabled(false);

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel9.setForeground(java.awt.Color.white);
        jLabel9.setText("Permission:");

        BTaddAcc.setBackground(new java.awt.Color(78, 255, 51));
        BTaddAcc.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        BTaddAcc.setForeground(java.awt.Color.white);
        BTaddAcc.setText("Add");
        BTaddAcc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BTaddAccActionPerformed(evt);
            }
        });

        BTupdAcc.setBackground(new java.awt.Color(61, 193, 255));
        BTupdAcc.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        BTupdAcc.setForeground(java.awt.Color.white);
        BTupdAcc.setText("Update");
        BTupdAcc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BTupdAccActionPerformed(evt);
            }
        });

        BTDelAcc.setBackground(new java.awt.Color(255, 0, 0));
        BTDelAcc.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        BTDelAcc.setForeground(java.awt.Color.white);
        BTDelAcc.setText("Delete");
        BTDelAcc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BTDelAccActionPerformed(evt);
            }
        });

        comboo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combooActionPerformed(evt);
            }
        });

        BTidAcc.setEnabled(false);
        BTidAcc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BTidAccActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel10.setForeground(java.awt.Color.white);
        jLabel10.setText("ID:");

        ClearText.setBackground(new java.awt.Color(50, 50, 50));
        ClearText.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        ClearText.setForeground(java.awt.Color.white);
        ClearText.setText("New");
        ClearText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ClearTextActionPerformed(evt);
            }
        });

        BTPermPs.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "staff", "admin" }));

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel11.setForeground(java.awt.Color.white);
        jLabel11.setText("Email:");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(133, 133, 133)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 371, Short.MAX_VALUE)
                                .addComponent(jLabel10))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel8)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(BTUserN, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(BTidAcc, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(accPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel11)
                                .addGap(18, 18, 18)))
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(BTPermP, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(BTPermPs, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(BTPassP)
                            .addComponent(BTEmailE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BTDelAcc, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BTupdAcc, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BTaddAcc, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ClearText))
                .addContainerGap(456, Short.MAX_VALUE))
            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BTidAcc, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(comboo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(BTUserN, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)
                            .addComponent(BTaddAcc, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                        .addComponent(jLabel11)
                        .addGap(13, 13, 13))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(BTupdAcc, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(BTEmailE, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(accPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BTDelAcc, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(BTPassP, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(BTPermPs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(BTPermP, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)))
                    .addComponent(ClearText, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(75, 75, 75))
        );

        jTabbedPane1.addTab("tab3", jPanel6);

        jPanel4.setBackground(new java.awt.Color(50, 50, 50));

        table5.setBackground(java.awt.Color.black);
        table5.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        table5.setForeground(java.awt.Color.white);
        table5.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Id", "Sender", "FIle Name", "Status", "Time"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table5.setRowHeight(30);
        table5.setSelectionBackground(java.awt.Color.white);
        table5.setSelectionForeground(java.awt.Color.red);
        table5.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                table5PropertyChange(evt);
            }
        });
        jScrollPane1.setViewportView(table5);
        if (table5.getColumnModel().getColumnCount() > 0) {
            table5.getColumnModel().getColumn(0).setResizable(false);
            table5.getColumnModel().getColumn(1).setResizable(false);
            table5.getColumnModel().getColumn(2).setResizable(false);
            table5.getColumnModel().getColumn(3).setResizable(false);
            table5.getColumnModel().getColumn(4).setResizable(false);
        }

        jButton3.setBackground(new java.awt.Color(0, 51, 51));
        jButton3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton3.setForeground(java.awt.Color.white);
        jButton3.setText("View PDF");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        pogi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                pogiKeyReleased(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel6.setForeground(java.awt.Color.white);
        jLabel6.setText("Search reports here:");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1216, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(pogi, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(jButton3)))
                .addContainerGap(77, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(pogi, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 544, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(83, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("tab1", jPanel4);

        jPanel7.setBackground(java.awt.Color.black);

        bestPanel.setBackground(java.awt.Color.white);
        bestPanel.setForeground(java.awt.Color.black);
        bestPanel.setToolTipText("");

        javax.swing.GroupLayout bestPanelLayout = new javax.swing.GroupLayout(bestPanel);
        bestPanel.setLayout(bestPanelLayout);
        bestPanelLayout.setHorizontalGroup(
            bestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bestPanelLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(bestLabel)
                .addContainerGap(250, Short.MAX_VALUE))
        );
        bestPanelLayout.setVerticalGroup(
            bestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bestPanelLayout.createSequentialGroup()
                .addComponent(bestLabel)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel4.setForeground(java.awt.Color.white);
        jLabel4.setText("BEST PRODUCT:");

        javax.swing.GroupLayout chartPanel3Layout = new javax.swing.GroupLayout(chartPanel3);
        chartPanel3.setLayout(chartPanel3Layout);
        chartPanel3Layout.setHorizontalGroup(
            chartPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, chartPanel3Layout.createSequentialGroup()
                .addGap(0, 766, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addGap(2, 2, 2)
                .addComponent(bestPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        chartPanel3Layout.setVerticalGroup(
            chartPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(chartPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(chartPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(bestPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE))
                .addContainerGap(431, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chartPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(127, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chartPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(235, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("tab4", jPanel7);

        chartPanel1.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout chartPanel1Layout = new javax.swing.GroupLayout(chartPanel1);
        chartPanel1.setLayout(chartPanel1Layout);
        chartPanel1Layout.setHorizontalGroup(
            chartPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 442, Short.MAX_VALUE)
        );
        chartPanel1Layout.setVerticalGroup(
            chartPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 330, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(chartPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 851, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chartPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(366, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("tab5", jPanel11);

        jPanel12.setBackground(new java.awt.Color(50, 50, 50));

        Product_table.setBackground(java.awt.Color.black);
        Product_table.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        Product_table.setForeground(java.awt.Color.white);
        Product_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Product Name", "Total Price", "Cost", "Description", "Stocks"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Product_table.setPreferredSize(new java.awt.Dimension(375, 1000));
        Product_table.setRowHeight(30);
        Product_table.setSelectionBackground(java.awt.Color.white);
        Product_table.setSelectionForeground(java.awt.Color.red);
        Product_table.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                Product_tablePropertyChange(evt);
            }
        });
        jScrollPane2.setViewportView(Product_table);
        if (Product_table.getColumnModel().getColumnCount() > 0) {
            Product_table.getColumnModel().getColumn(0).setResizable(false);
            Product_table.getColumnModel().getColumn(1).setResizable(false);
            Product_table.getColumnModel().getColumn(2).setResizable(false);
            Product_table.getColumnModel().getColumn(3).setResizable(false);
            Product_table.getColumnModel().getColumn(4).setResizable(false);
            Product_table.getColumnModel().getColumn(5).setResizable(false);
        }

        jButton2.setBackground(new java.awt.Color(78, 255, 51));
        jButton2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton2.setForeground(java.awt.Color.white);
        jButton2.setText("Add");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(61, 193, 255));
        jButton4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton4.setForeground(java.awt.Color.white);
        jButton4.setText("Update");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(255, 0, 0));
        jButton5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton5.setForeground(java.awt.Color.white);
        jButton5.setText("Delete");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        btnID.setEnabled(false);

        jButton6.setBackground(new java.awt.Color(50, 50, 50));
        jButton6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton6.setForeground(java.awt.Color.white);
        jButton6.setText("New");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel12.setForeground(java.awt.Color.white);
        jLabel12.setText("ID:");

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel13.setForeground(java.awt.Color.white);
        jLabel13.setText("Product:");

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel14.setForeground(java.awt.Color.white);
        jLabel14.setText("Price:");

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel15.setForeground(java.awt.Color.white);
        jLabel15.setText("Stocks:");

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel16.setForeground(java.awt.Color.white);
        jLabel16.setText("Description:");

        btnDESC.setColumns(20);
        btnDESC.setRows(5);
        jScrollPane4.setViewportView(btnDESC);

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel18.setForeground(java.awt.Color.white);
        jLabel18.setText("Product Image");

        btIMG.setBackground(new java.awt.Color(0, 102, 102));
        btIMG.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btIMG.setForeground(java.awt.Color.white);
        btIMG.setText("Insert Image");
        btIMG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btIMGActionPerformed(evt);
            }
        });

        coomboo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        coomboo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                coombooActionPerformed(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel17.setForeground(java.awt.Color.white);
        jLabel17.setText("Cost:");

        productPanel.setBackground(java.awt.Color.white);

        javax.swing.GroupLayout productPanelLayout = new javax.swing.GroupLayout(productPanel);
        productPanel.setLayout(productPanelLayout);
        productPanelLayout.setHorizontalGroup(
            productPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(productPanelLayout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(productLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        productPanelLayout.setVerticalGroup(
            productPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(productPanelLayout.createSequentialGroup()
                .addContainerGap(38, Short.MAX_VALUE)
                .addComponent(productLabel))
        );

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel5.setForeground(java.awt.Color.white);
        jLabel5.setText("TOTAL PRODUCTS:");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1197, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 96, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel13)
                            .addComponent(jLabel12)))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel16))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel15)))
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel17)
                    .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(Stock, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel12Layout.createSequentialGroup()
                            .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(btnNAME, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                                .addComponent(btnID, javax.swing.GroupLayout.Alignment.LEADING))
                            .addGap(29, 29, 29)
                            .addComponent(jLabel14))
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnCOST)
                            .addComponent(productPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(108, 108, 108)
                        .addComponent(btnIMAGE, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(btnPRICE, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(132, 132, 132)
                        .addComponent(jLabel18)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btIMG)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(coomboo, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(141, 141, 141))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(jLabel14)
                    .addComponent(btnPRICE, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btIMG, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(103, 103, 103))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel12Layout.createSequentialGroup()
                                        .addComponent(coomboo, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(btnIMAGE, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(btnCOST, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel17))
                                .addGap(44, 44, 44)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(productPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(btnNAME, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel13))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(Stock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel15))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel16))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)))
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(106, 106, 106))
        );

        jTabbedPane1.addTab("tab6", jPanel12);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(navPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jTabbedPane1))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(navPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 740, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 23, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/mainlogo.jpg.jpg"))); // NOI18N

        Home.setBackground(new java.awt.Color(51, 51, 51));
        Home.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        Home.setForeground(new java.awt.Color(255, 255, 255));
        Home.setText("Home");
        Home.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                HomeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(82, 82, 82)
                .addComponent(Home, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel1))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Home, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void DashboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DashboardActionPerformed
        // TODO add your handling code here:
        jTabbedPane1.setSelectedIndex(1);
    }//GEN-LAST:event_DashboardActionPerformed

    private void ReportsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ReportsActionPerformed
        // TODO add your handling code here:
        jTabbedPane1.setSelectedIndex(2);

    }//GEN-LAST:event_ReportsActionPerformed

    private void NotificationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NotificationActionPerformed
        // TODO add your handling code here:
        jTabbedPane1.setSelectedIndex(3);
    }//GEN-LAST:event_NotificationActionPerformed

    private void HomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HomeActionPerformed
        // TODO add your handling code here:
        jTabbedPane1.setSelectedIndex(0);
    }//GEN-LAST:event_HomeActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:

        int i = JOptionPane.showConfirmDialog(null, "Are you sure you want to Logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (i == 0) {
            LoginForm first = new LoginForm();
            JOptionPane.showMessageDialog(null, "Logout successfully!");
            first.setVisible(true);

            dispose();
        } else {
            LoginForm first = new LoginForm();
            JOptionPane.showMessageDialog(null, "Logout cancelled.");
            first.setVisible(false);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        openSelectedReport();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void pogiKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pogiKeyReleased
        // TODO add your handling code here:
        DefaultTableModel check = (DefaultTableModel) table5.getModel();
        TableRowSorter<DefaultTableModel> obj = new TableRowSorter<>(check);
        table5.setRowSorter(obj);
        obj.setRowFilter(RowFilter.regexFilter(pogi.getText()));
    }//GEN-LAST:event_pogiKeyReleased

    private void Notification1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Notification1ActionPerformed
        // TODO add your handling code here:
        jTabbedPane1.setSelectedIndex(4);
    }//GEN-LAST:event_Notification1ActionPerformed

    private void BTUserNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BTUserNActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_BTUserNActionPerformed

    private void BTaddAccActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BTaddAccActionPerformed
        // TODO add your handling code here:
        if (BTPassP.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username name is required!");
        } else if (BTUserN.getText() == null) {
            JOptionPane.showMessageDialog(this, "Password is required!");
        } else if (BTPermP.getText() == null) {
            JOptionPane.showMessageDialog(this, "Permission is required!");
        } else if (BTEmailE.getText() == null) {
            JOptionPane.showMessageDialog(this, "Email is required!");
        } else {
            try {
                // TODO add your handling code here:
                String btuser = BTUserN.getText();
                String btemail = BTEmailE.getText();
                String btpass = BTPassP.getText();
                String btperm = BTPermPs.getSelectedItem().toString();

                pstt = conn.prepareStatement("INSERT INTO accounts (Username, Email, Password, permission)VALUES(?, ?, ?, ?)");
                pstt.setString(1, btuser);
                pstt.setString(2, btemail);
                pstt.setString(3, btpass);
                pstt.setString(4, btperm);

                int k = pstt.executeUpdate();

                if (k == 1) {
                    JOptionPane.showMessageDialog(this, "Record Successfully");
                    BTPassP.setText("");
                    BTUserN.setText("");
                    BTPermP.setText("");
                    BTEmailE.setText("");
                    loadAccounttNames();
                    fetchAccountDetails();
                } else {
                    JOptionPane.showMessageDialog(this, "Unsuccessfully Recorded");
                }
            } catch (SQLException ex) {
                Logger.getLogger(StaffDashboard.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_BTaddAccActionPerformed

    private void BTupdAccActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BTupdAccActionPerformed
        // TODO add your handling code here:
        try {
            // Validate required fields
            if (BTPassP.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "USername is required!");
            } else if (BTUserN.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Password is required!");
            } else if (BTEmailE.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Email is required!");
            } else if (BTPermP.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Permission is required!");
            } else {
                // Retrieve values from input fields
                String BTuserN = BTUserN.getText();
                String BTemailE = BTEmailE.getText();
                String BTpassP = BTPassP.getText();
                String btperm = BTPermPs.getSelectedItem().toString();
                String BTID = BTidAcc.getText();

                //  Debug: Print values before updating
                System.out.println("Updating ID: " + BTID);
                System.out.println("Product Name: " + BTuserN);
                System.out.println("Email: " + BTpassP);
                System.out.println("Total Price: " + BTPermPs);

                //  Correct SQL statement
                pstt = conn.prepareStatement(
                        "UPDATE accounts SET Username=?, Email=?, Password=?, permission=? WHERE ID=?"
                );
                pstt.setString(1, BTuserN);
                pstt.setString(2, BTemailE);
                pstt.setString(3, BTpassP);
                pstt.setString(4, btperm);
                pstt.setString(5, BTID);

                int k = pstt.executeUpdate();
                if (k == 1) {
                    JOptionPane.showMessageDialog(this, "The selected Account is updated successfully!");

                    // Clear input fields
                    BTPassP.setText("");
                    BTEmailE.setText("");
                    BTUserN.setText("");
                    BTPermP.setText("");
                    loadAccounttNames();
                    fetchAccountDetails();

                    // Reload transaction numbers & refresh table
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(StaffDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_BTupdAccActionPerformed

    private void combooActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combooActionPerformed
        // TODO add your handling code here:
        fetchAccountDetails();
    }//GEN-LAST:event_combooActionPerformed

    private void BTidAccActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BTidAccActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_BTidAccActionPerformed

    private void BTDelAccActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BTDelAccActionPerformed
        // TODO add your handling code here:
        try {
            String BTID = BTidAcc.getText();

            // show confirmation dialog before executing delete
            int confirm = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to delete this Account?\n\nAccount: " + BTID,
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                pstt = conn.prepareStatement("DELETE FROM accounts WHERE ID=?");
                pstt.setString(1, BTID);

                int a = pstt.executeUpdate();

                if (a == 1) {
                    JOptionPane.showMessageDialog(this, "The selected Account has been deleted successfully!");

                    // Clear input fields
                    BTPassP.setText("");
                    BTEmailE.setText("");
                    BTUserN.setText("");
                    BTPermP.setText("");
                    BTUserN.requestFocus();
                    loadAccounttNames();
                    fetchAccountDetails();

                    // Set the first available item in the combo box after deletion
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete the record.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Deletion canceled.");
            }
        } catch (SQLException ex) {
            Logger.getLogger(StaffDashboard.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Database error occurred while deleting the product.");
        }

    }//GEN-LAST:event_BTDelAccActionPerformed

    private void ClearTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClearTextActionPerformed
        // TODO add your handling code here:
        ClearText();
    }//GEN-LAST:event_ClearTextActionPerformed

    private void Notification2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Notification2ActionPerformed
        // TODO add your handling code here:
        jTabbedPane1.setSelectedIndex(5);
    }//GEN-LAST:event_Notification2ActionPerformed
    public boolean checkInputs() {
        if (btnID.getText() == null
                || btnPRICE.getText() == null
                || btnDESC.getText() == null) {
            return false;
        } else {
            try {
                Float.valueOf(btnPRICE.getText());
                return true;
            } catch (Exception ex) {
                return false;
            }
        }
    }
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        if (checkInputs() && ImgPath != null)
            try {
            pstt = conn.prepareStatement("INSERT INTO product_table (Product_Name, Price, Cost, Description, Image, Stock)"
                    + " VALUES (?, ?, ?, ?, ?, ?)");
            pstt.setString(1, btnNAME.getText());
            pstt.setString(2, btnPRICE.getText().trim());
            pstt.setString(3, btnCOST.getText().trim());
            pstt.setString(4, btnDESC.getText());
            InputStream img = new FileInputStream(new File(ImgPath));
            pstt.setBlob(5, img);
            pstt.setString(6, Stock.getText().trim());
            pstt.executeUpdate();
            loadProductNames();
            Account();
            JOptionPane.showMessageDialog(null, "Product is added successfully!");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Invalid ID format. Please enter a number.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        } else {
            JOptionPane.showMessageDialog(null, "One or more field are Empty.");
        }

    }//GEN-LAST:event_jButton2ActionPerformed

    private void btIMGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btIMGActionPerformed
        // TODO add your handling code here:
        JFileChooser FILE = new JFileChooser();
        FILE.setCurrentDirectory(new File(System.getProperty("user.home")));

        FileNameExtensionFilter filter = new FileNameExtensionFilter("*.image", "jpg", "png");
        FILE.addChoosableFileFilter(filter);
        int result = FILE.showSaveDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = FILE.getSelectedFile();
            String path = selectedFile.getAbsolutePath();
            btnIMAGE.setIcon(ResizeImage(path, null));
            ImgPath = path;
        } else {
            System.out.println("No File Selected");
        }
    }//GEN-LAST:event_btIMGActionPerformed

    private void coombooActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_coombooActionPerformed
        // TODO add your handling code here:
        fetchProductDetails();
    }//GEN-LAST:event_coombooActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        clearFields();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        if (checkInputs() && !btnID.getText().trim().isEmpty()) {
            String UpdateQuery;
            PreparedStatement pst = null;

            try {
                // Update without image
                if (ImgPath == null) {
                    UpdateQuery = "UPDATE product_table SET Product_Name = ?, Price = ?, Cost = ?, Description = ?, Stock = ? WHERE id = ?";
                    pstt = conn.prepareStatement(UpdateQuery);

                    pstt.setString(1, btnNAME.getText());
                    pstt.setString(2, btnPRICE.getText());
                    pstt.setString(3, btnCOST.getText());
                    pstt.setString(4, btnDESC.getText());
                    pstt.setInt(6, Integer.parseInt(btnID.getText()));
                    pstt.setInt(5, Integer.parseInt(Stock.getText()));
                } else {
                    // Update with image
                    File imageFile = new File(ImgPath);
                    if (!imageFile.exists()) {
                        JOptionPane.showMessageDialog(null, "Image file not found!");
                        return;
                    }

                    InputStream img = new FileInputStream(imageFile);
                    UpdateQuery = "UPDATE product_table SET Product_Name = ?, Price = ?, Cost = ?, Description = ?, Image = ?, Stock = ? WHERE id = ?";
                    pstt = conn.prepareStatement(UpdateQuery);

                    pstt.setString(1, btnNAME.getText());
                    pstt.setString(2, btnPRICE.getText());
                    pstt.setString(3, btnCOST.getText());
                    pstt.setString(4, btnDESC.getText());
                    pstt.setBlob(5, img);
                    pstt.setInt(6, Integer.parseInt(Stock.getText()));
                }

                int rowsUpdated = pstt.executeUpdate();
                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(null, "Product updated successfully!");

                    // Always refresh product list and combo box after updating
                    Account(); // Refresh product table
                    loadProductNames(); // Refresh combo box
                    fetchProductDetails(); // Update UI

                    //  Explicitly update the combo box selection
                    String selectedProduct = btnNAME.getText();
                    coomboo.setSelectedItem(selectedProduct);
                    coomboo.repaint(); // Force refresh if needed

                } else {
                    JOptionPane.showMessageDialog(null, "Product update failed!");
                }

            } catch (SQLException | NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                Logger.getLogger(StaffDashboard.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Unexpected Error: " + ex.getMessage());
            } finally {
                try {
                    if (pst != null) {
                        pst.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(StaffDashboard.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "One or more fields are empty or invalid!");
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        try {
            String PID = coomboo.getSelectedItem().toString();

            // show confirmation dialog before executing delete
            int confirm = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to delete this product?\n\nProduct: " + PID,
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                pstt = conn.prepareStatement("DELETE FROM product_table WHERE Product_name=?");
                pstt.setString(1, PID);

                int a = pstt.executeUpdate();

                if (a == 1) {
                    JOptionPane.showMessageDialog(this, "The selected product has been deleted successfully!");

                    // Clear input fields
                    btnNAME.setText("");
                    btnPRICE.setText("");
                    btnCOST.setText("");
                    btnDESC.setText("");
                    btnNAME.requestFocus();

                    // Refresh product list
                    Account();

                    // Remove the deleted item from the combo box
                    coomboo.removeItem(PID);

                    // Set the first available item in the combo box after deletion
                    if (coomboo.getItemCount() > 0) {
                        comboo.setSelectedIndex(0);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete the record.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Deletion canceled.");
            }
        } catch (SQLException ex) {
            Logger.getLogger(StaffDashboard.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Database error occurred while deleting the product.");
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void table5PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_table5PropertyChange
        // TODO add your handling code here:
        Header();
    }//GEN-LAST:event_table5PropertyChange

    private void Product_tablePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_Product_tablePropertyChange
        // TODO add your handling code here:
        Header1();
    }//GEN-LAST:event_Product_tablePropertyChange

    private void accountsPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_accountsPropertyChange
        // TODO add your handling code here:
        Header4();
    }//GEN-LAST:event_accountsPropertyChange

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AdminAccount.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AdminAccount.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AdminAccount.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AdminAccount.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AdminAccount().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BTDelAcc;
    private javax.swing.JTextField BTEmailE;
    private javax.swing.JTextField BTPassP;
    private javax.swing.JTextField BTPermP;
    private javax.swing.JComboBox<String> BTPermPs;
    private javax.swing.JTextField BTUserN;
    private javax.swing.JButton BTaddAcc;
    private javax.swing.JTextField BTidAcc;
    private javax.swing.JButton BTupdAcc;
    private javax.swing.JButton ClearText;
    private javax.swing.JButton Dashboard;
    private javax.swing.JButton Home;
    private javax.swing.JButton Notification;
    private javax.swing.JButton Notification1;
    private javax.swing.JButton Notification2;
    private javax.swing.JTable Product_table;
    private javax.swing.JButton Reports;
    private javax.swing.JTextField Stock;
    private javax.swing.JPanel accPanel;
    private javax.swing.JLabel accountCountLabel;
    private javax.swing.JTable accounts;
    private javax.swing.JLabel bestLabel;
    private javax.swing.JPanel bestPanel;
    private javax.swing.JButton btIMG;
    private javax.swing.JTextField btnCOST;
    private javax.swing.JTextArea btnDESC;
    private javax.swing.JTextField btnID;
    private javax.swing.JLabel btnIMAGE;
    private javax.swing.JTextField btnNAME;
    private javax.swing.JTextField btnPRICE;
    private javax.swing.JPanel chartPanel1;
    private javax.swing.JPanel chartPanel3;
    private javax.swing.JComboBox<String> comboo;
    private javax.swing.JComboBox<String> coomboo;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel navPanel;
    private javax.swing.JTextField pogi;
    private javax.swing.JLabel productLabel;
    private javax.swing.JPanel productPanel;
    private javax.swing.JTable table5;
    private javax.swing.JLabel totalUsersLabel;
    // End of variables declaration//GEN-END:variables
}
