import java.sql.*;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.JFileChooser;
import javax.swing.RowFilter;
import javax.swing.table.TableRowSorter;
import java.io.FileOutputStream;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.awt.BorderLayout;
import java.io.FileNotFoundException;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.sql.ResultSet;
import java.util.List;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.text.SimpleDateFormat;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.util.Date;
import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import org.apache.pdfbox.rendering.PDFRenderer;
import java.awt.event.*;
import javax.swing.Timer;
import javax.swing.UIManager;
    

public class StaffDashboard extends javax.swing.JFrame {
    private List<File> attachmentFiles = new ArrayList<>();
    private double totalAccumulatedCost = 0.0;

   

    public StaffDashboard() {
        initComponents();
        Connect();
        LoadTransactionNo();
        Show_Products();
        loadProductNames(); 
        FetchTransaction();
        Fetch3();
        startAutoRefresh();
        
        HoverEffect hover = new HoverEffect(new Color(240, 240, 240), new Color(255, 77, 77));

        // Apply to multiple buttons
        hover.applyTo(btnCALCULATOR);
        hover.applyTo(PDetails);
        hover.applyTo(btnTRANSACTION);
        hover.applyTo(SendEmail);
        hover.applyTo(CRecord1);
     
    }
    Connection con;
    PreparedStatement pst;
    PreparedStatement pst1;
    ResultSet rs;
    DefaultTableModel df;
    String ImgPath = null;
    int pos = 0;

    public void Connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/information_system","root","");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(StaffDashboard.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(StaffDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void Fetch3() {
    try {
        // Prepare SQL query
        pst = con.prepareStatement("SELECT * FROM notifications");
        rs = pst.executeQuery();
        ResultSetMetaData rss = rs.getMetaData();
        int columnCount = rss.getColumnCount();

        // Get table model and clear existing data
        DefaultTableModel def = (DefaultTableModel) stafftable.getModel();
        def.setRowCount(0);

        // Fetch and add data to table
        while (rs.next()) {
            Vector<Object> rowData = new Vector<>();
            rowData.add(rs.getInt("id"));                 // ID (assuming it's an integer)
            rowData.add(rs.getString("recipient_role"));  // Recipient role
            rowData.add(rs.getString("file_name"));       // File name
            rowData.add(rs.getString("status"));          // Status
            rowData.add(rs.getTimestamp("timestamp"));    // Timestamp (with date and time)

            def.addRow(rowData);
        }

    } catch (SQLException ex) {
        Logger.getLogger(StaffDashboard.class.getName()).log(Level.SEVERE, null, ex);
    }
}

       
public void sales() {
    String totalcost = txttprice.getText();
    String payment = txtpay.getText();
    String balance = txtbalance.getText();
    String cost = txtcost.getText();
    
    int lastid = 0;
    double totalSales = Double.parseDouble(totalcost); // assuming this is the total cost
    java.sql.Date today = new java.sql.Date(System.currentTimeMillis());

    try {
        // Insert into sales with total_sales and sale_date
        String query = "INSERT INTO sales(subtotal, cost, pay, balance, total_sales, sale_date) VALUES (?, ?, ?, ?, ?, ?)";
        pst = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        pst.setString(1, totalcost);
        pst.setString(2, cost);
        pst.setString(3, payment);
        pst.setString(4, balance);
        pst.setDouble(5, totalSales);
        pst.setDate(6, today);

        pst.executeUpdate();
        rs = pst.getGeneratedKeys();

        if (rs.next()) {
            lastid = rs.getInt(1);
        }

        // Insert into sales_product
        String query1 = "INSERT INTO sales_product(sales_id, vape_name, price, qty, total) VALUES (?, ?, ?, ?, ?)";
        pst1 = con.prepareStatement(query1);

        for (int i = 0; i < table4.getRowCount(); i++) {
            String vname = (String) table4.getValueAt(i, 0);
            String price = (String) table4.getValueAt(i, 1);
            String quan = (String) table4.getValueAt(i, 2);
            double totalp = (double) table4.getValueAt(i, 3);

            pst1.setInt(1, lastid);
            pst1.setString(2, vname);
            pst1.setString(3, price);
            pst1.setString(4, quan);
            pst1.setDouble(5, totalp);
            pst1.executeUpdate();
        }

    } catch (SQLException ex) {
        Logger.getLogger(StaffDashboard.class.getName()).log(Level.SEVERE, null, ex);
    }
}

    // check input fields 
    
    public boolean checkInputs()
            {
                if(btnID.getText() == null
                  || btnPRICE.getText() == null
                  || btnDESC.getText() == null
                 ){
                return false;
                }
                   else {
                    try{
                        Float.valueOf(btnPRICE.getText());
                        return true;
                    }catch(Exception ex)
                    {
                        return false;
                    }
                }
            }           
 //resize image
    public ImageIcon ResizeImage(String imagePath, byte [] pic)
    {
        ImageIcon myImage = null;
        
        if(imagePath != null)
        {
            myImage = new ImageIcon(imagePath);
        }else{
            myImage = new ImageIcon(pic);
        }
        Image img = myImage.getImage();
        Image img2 = img.getScaledInstance(btnImg.getWidth(), btnImg.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon image = new ImageIcon(img2);
        return  image;
        
    }

    //Display table data
    // fill arraylist with the data
    
    public ArrayList<Product> getProductList()
    {
                    ArrayList<Product> productList = new ArrayList<Product>();
            String query = "SELECT * FROM product_table";
            
            Statement st;
            ResultSet rs;
        try {
          
            
            st = con.createStatement();
            rs = st.executeQuery(query);
            Product product;
            
            while(rs.next())
            {
                
                product = new Product(rs.getInt("id"), rs.getString("Product_Name"),rs.getString("Price"), rs.getString("Description"),rs.getBytes("Image"),rs.getInt("Stock"));
                productList.add(product);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(StaffDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return productList;
    }
   
    //2-Populate The Code
    private int getLatestSaleId() {
    try {
        String query = "SELECT MAX(id) FROM sales"; // Get the latest sale ID
        PreparedStatement pst = con.prepareStatement(query);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            return rs.getInt(1); // Return the highest sale ID
        }
    } catch (SQLException ex) {
        Logger.getLogger(StaffDashboard.class.getName()).log(Level.SEVERE, null, ex);
    }
    return 1; // Default value if no sales exist
}
private void receiptPDF() {
    try {
        int saleid = getLatestSaleId();

        // 🧠 Check stock levels first
        PreparedStatement stockCheckStmt = con.prepareStatement(
            "SELECT sp.vape_name, sp.qty, pt.Stock FROM sales_product sp " +
            "JOIN product_table pt ON sp.vape_name = pt.Product_Name " +
            "WHERE sp.sales_id = ?"
        );
        stockCheckStmt.setInt(1, saleid);
        ResultSet stockCheckRs = stockCheckStmt.executeQuery();

        while (stockCheckRs.next()) {
            double currentStock = stockCheckRs.getDouble("Stock");
            double qty = stockCheckRs.getDouble("qty");
            String name = stockCheckRs.getString("vape_name");

            if (currentStock <= 0 || currentStock - qty < 0) {
                DefaultTableModel modelll = (DefaultTableModel) table4.getModel();
                modelll.setRowCount(0);
                JOptionPane.showMessageDialog(
                    null,
                    "Cannot print receipt!\n" +
                    name + " has insufficient stock.\n" +
                    "Current Stock: " + currentStock + ", Your Added Quantity: " + qty,
                    "Out of Stock!",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }
        }
        stockCheckRs.close();
        stockCheckStmt.close();

        // Fetch data and build PDF
        PreparedStatement pst = con.prepareStatement("SELECT * FROM sales WHERE id=?");
        pst.setInt(1, saleid);
        ResultSet rs = pst.executeQuery();

        PreparedStatement pst2 = con.prepareStatement("SELECT * FROM sales_product WHERE sales_id=?");
        pst2.setInt(1, saleid);
        ResultSet rs2 = pst2.executeQuery();

        Rectangle receiptSize = new Rectangle(58 * 2.834f, 210 * 2.834f);
        Document PDFreport = new Document(receiptSize, 5, 10, 10, 10);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = dateFormat.format(new Date());
        String invoiceNumber = "INVOICE-" + System.currentTimeMillis();
        String filePath = "C:\\PDF\\" + invoiceNumber + ".pdf";

        PdfWriter.getInstance(PDFreport, new FileOutputStream(filePath));
        PDFreport.open();

        Font textFont = new Font(Font.FontFamily.COURIER, 8, Font.NORMAL);
        Font boldFont = new Font(Font.FontFamily.COURIER, 9, Font.BOLD);

        Paragraph title = new Paragraph("DOCVAPE SHOP RECEIPT\n", boldFont);
        title.setAlignment(Element.ALIGN_CENTER);
        PDFreport.add(title);

        Paragraph invoiceInfo = new Paragraph(
            " 1291 SH Loyola \n St. Sampaloc Manila,\n Metro Manila, Philippines\n" +
            "Invoice No: " + invoiceNumber +
            "\nTr. Date: " + timestamp + "\nCashier Name: Sample S. Sample" +
            "\n----------------------------\n",
            textFont
        );
        invoiceInfo.setAlignment(Element.ALIGN_CENTER);
        PDFreport.add(invoiceInfo);

        PDFreport.add(new Paragraph(" QTY | PRODUCT |  TOTAL", boldFont));
        PDFreport.add(new Paragraph("-------------------------------", textFont));

        double totalAmount = 0;
        String email = "";

        while (rs2.next()) {
            String vapeName = rs2.getString("vape_name");
            double qty = rs2.getDouble("qty");
            double totalVal = rs2.getDouble("total");

            // Always show two decimals
            String formattedTotal = String.format("%.2f", totalVal);
            String formattedProduct = String.format("%-10s", vapeName);
            String formattedQty = String.format("%.2f", qty);
            String formattedLine = formattedQty + "| " + formattedProduct + "| " + formattedTotal;

            totalAmount += totalVal;
            PDFreport.add(new Paragraph(formattedLine, textFont));

            // 🔽 Auto Decrease Stock
            PreparedStatement stockCheck = con.prepareStatement("SELECT Stock FROM product_table WHERE Product_Name = ?");
            stockCheck.setString(1, vapeName);
            ResultSet stockRs = stockCheck.executeQuery();

            if (stockRs.next()) {
                double currentStock = stockRs.getDouble("Stock");
                double newStock = currentStock - qty;

                PreparedStatement stockUpdate = con.prepareStatement("UPDATE product_table SET Stock = ? WHERE Product_Name = ?");
                stockUpdate.setDouble(1, newStock);
                stockUpdate.setString(2, vapeName);
                stockUpdate.executeUpdate();

                if (newStock < 10) {
                    JOptionPane.showMessageDialog(
                        null,
                        "⚠️ Warning: " + vapeName + " is low on stock (" + newStock + " left)!",
                        "Low Stock Alert",
                        JOptionPane.WARNING_MESSAGE
                    );
                }
                stockUpdate.close();
            }
            stockRs.close();
            stockCheck.close();
        }

        PDFreport.add(new Paragraph("\n-------------------------------", textFont));

        if (rs.next()) {
            double sub = Double.parseDouble(rs.getString("subtotal"));
            double p = Double.parseDouble(rs.getString("pay"));
            double bal = Double.parseDouble(rs.getString("balance"));

            // Always show two decimals
            String formattedSubtotal = String.format("%.2f", sub);
            String formattedPay = String.format("%.2f", p);
            String formattedBalance = String.format("%.2f", bal);

            PDFreport.add(new Paragraph("Subtotal:  " + "  PHP  " + formattedSubtotal, textFont));
            PDFreport.add(new Paragraph("Cash:      " + "  PHP  " + formattedPay, textFont));
            PDFreport.add(new Paragraph("Change:    " + "  PHP  " + formattedBalance, textFont));
        }

        PDFreport.add(new Paragraph("\n-------------------------------", textFont));
        PDFreport.add(new Paragraph("Thank you for your purchase!", boldFont));

        try {
            PDFreport.add(new Paragraph("\n-------------------------------", textFont));
            String imagePath = "C:\\images\\qr_1743488864866.png";
            com.itextpdf.text.Image logo = com.itextpdf.text.Image.getInstance(imagePath);
            logo.scaleToFit(140, 100);
            logo.setAlignment(com.itextpdf.text.Image.ALIGN_CENTER);
            PDFreport.add(logo);
            PDFreport.add(new Paragraph("\n-------------------------------", textFont));
        } catch (Exception e) {
            System.out.println("Logo not found or failed to load.");
        }

        PDFreport.close();
        rs.close();
        rs2.close();
        pst.close();
        pst2.close();

        previewAndPrintPDF(filePath);

        UIManager.put("OptionPane.yesButtonText", "Print");
        UIManager.put("OptionPane.noButtonText", "Cancel");
        int confirm = JOptionPane.showConfirmDialog(
            null,
            "Are you sure you want to print this receipt?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            System.out.println("Ganda mo tasha");
            printPDF(filePath);
            JOptionPane.showMessageDialog(null, "Printed successfully.");
        } else {
            JOptionPane.showMessageDialog(null, "Printing cancelled.");
            return;
        }

        insertTransaction(invoiceNumber, email, totalAmount);
        refreshTable2();
        sendReportToAdmin(filePath);

    } catch (SQLException | DocumentException | FileNotFoundException ex) {
        Logger.getLogger(StaffDashboard.class.getName()).log(Level.SEVERE, null, ex);
        JOptionPane.showMessageDialog(null, "Error exporting data: " + ex.getMessage());
    }
}

public void sendReportToAdmin(String filePath) {
    try {
        File selectedFile = new File(filePath);
        String fileName = selectedFile.getName();

        try (FileInputStream fileInputStream = new FileInputStream(selectedFile)) {
            byte[] fileBytes = new byte[(int) selectedFile.length()];
            fileInputStream.read(fileBytes);

            // Save file data and file name to the database
            saveReportToDatabase(fileName, fileBytes);

        }
    } catch (IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error uploading the file.");
    }
}


private void insertTransaction(String invoiceNumber, String email, double totalPrice) {
    try {
        String query = "INSERT INTO transaction_history(Inv_Number, email, total_price, date) VALUES (?, ?, ?, NOW())";
        PreparedStatement pst = con.prepareStatement(query);
        pst.setString(1, invoiceNumber);
        pst.setString(2, email);
        pst.setDouble(3, totalPrice);
    
        int rowsInserted = pst.executeUpdate();
        if (rowsInserted > 0) {
            System.out.println("Transaction saved successfully!");
            LoadTransactionNo(); // Refresh transaction history table
        }
        pst.close();
    } catch (SQLException ex) {
        Logger.getLogger(StaffDashboard.class.getName()).log(Level.SEVERE, null, ex);
        JOptionPane.showMessageDialog(null, "Error saving transaction: " + ex.getMessage());
    }
}
private void clearFieldsAfterPrint() {
    txtpay.setText("");
    txttprice.setText("");
    txtbalance.setText("");
    // Clear other fields as needed, like:
    // txtCustomerName.setText("");
    // txtProduct.setText("");
    // etc.
}


private void previewAndPrintPDF(String pdfPath) {
    try {
        PDDocument document = PDDocument.load(new File(pdfPath));
        PDFRenderer renderer = new PDFRenderer(document);
        BufferedImage image = renderer.renderImageWithDPI(0, 150);

        // Create the image label and make it scrollable
        JLabel label = new JLabel(new ImageIcon(image));
        JScrollPane scrollPane = new JScrollPane(label);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // Create main frame
        JFrame frame = new JFrame("Receipt Preview");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        
        // Add components
        frame.add(scrollPane, BorderLayout.CENTER);
        

        // Set preferred size, pack, and center
        frame.setSize(new Dimension(400, 600)); // Limit height to ensure button is always visible
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        document.close();
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Failed to preview receipt: " + e.getMessage());
    }
}

private void printPDF(String pdfPath) {
    try {
        PDDocument document = PDDocument.load(new File(pdfPath));
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPageable(new PDFPageable(document));

        // Automatically print to default printer WITHOUT showing dialog
        job.print(); // No dialog shown here
        DefaultTableModel model = (DefaultTableModel) table4.getModel();
        model.setRowCount(0);  // Clears all rows
        document.close();
    } catch (PrinterException | IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Printing failed: " + e.getMessage());
    }
}

public void Show_Products() {
    ArrayList<Product> list = getProductList();
    DefaultTableModel model = (DefaultTableModel) table2.getModel();
    table2.setDefaultRenderer(Object.class, new LowStockTableRenderer());
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

private void clearFields() {
    btnID.setText("");
    btnNAME.setText("");
    btnPRICE.setText("");
    btnDESC.setText("");
    Stock.setText("");
    btnImg.setIcon(null);
    ImgPath = null;
}

    public void ShowItem(int index) {
    btnID.setText(Integer.toString(getProductList().get(index).getId()));
    btnNAME.setText(getProductList().get(index).getName());
    btnPRICE.setText(getProductList().get(index).getPrice());
    btnDESC.setText(getProductList().get(index).getDescription());
    btnImg.setIcon(ResizeImage(null, getProductList().get(index).getImage()));
   
    }
    public void LoadTransactionNo(){
        try {
            pst = con.prepareStatement("SELECT id FROM transaction_history");
            rs = pst.executeQuery();
            comboo.removeAllItems();
            while(rs.next()){
            comboo.addItem(rs.getString(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(StaffDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   public void loadProductNames() {
    try {
        
        pst = con.prepareStatement("SELECT Product_Name FROM product_table");
        rs = pst.executeQuery();
         

        COMBO.removeAllItems(); // Clear existing items
        COMBO.addItem("-- Select a Product --"); // Add placeholder

        while (rs.next()) {
            COMBO.addItem(rs.getString("Product_Name")); // Add product name
        }

        // Only call fetchProductDetails if products exist
        if (COMBO.getItemCount() > 1) { 
            COMBO.setSelectedIndex(1); // Select the first actual product
            fetchProductDetails(); // Fetch details for the first product
        }
    } catch (SQLException ex) {
        Logger.getLogger(StaffDashboard.class.getName()).log(Level.SEVERE, null, ex);
    }
}

public void fetchProductDetails() {
    try {
        Object selectedObject = COMBO.getSelectedItem(); // Get selected item safely

        if (selectedObject == null || selectedObject.toString().equals("-- Select a Product --")) {
            return; // Exit if no valid product is selected
        }

        String selectedProduct = selectedObject.toString();

        pst = con.prepareStatement("SELECT * FROM product_table WHERE Product_Name=?");
        pst.setString(1, selectedProduct);
        rs = pst.executeQuery();

        if (rs.next()) {
            btnID.setText(rs.getString("id"));
            btnNAME.setText(rs.getString("Product_Name"));
            btnPRICE.setText(rs.getString("Price"));
            btnDESC.setText(rs.getString("Description"));
            Stock.setText(String.valueOf(rs.getInt("Stock")));
            // Handle image data
            byte[] imgData = rs.getBytes("Image"); 
            if (imgData != null) {
                ImageIcon imageIcon = new ImageIcon(imgData);
                Image img = imageIcon.getImage().getScaledInstance(btnImg.getWidth(), btnImg.getHeight(), Image.SCALE_SMOOTH);
                btnImg.setIcon(new ImageIcon(img)); // Set the image to the JLabel
            } else {
                btnImg.setIcon(null);
                btnImg.setText("No Image");
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
public void refreshTable() {
    try {
        DefaultTableModel model = (DefaultTableModel) table2.getModel();
        model.setRowCount(0); // Clear the table before reloading data

        pst = con.prepareStatement("SELECT * FROM product_table");
        rs = pst.executeQuery();

        while (rs.next()) {
            Object[] rowData = {
                rs.getString("id"),
                rs.getString("Product_Name"),
                rs.getString("Price"),
                rs.getString("Cost"),
                rs.getString("Description"),
                rs.getString("Stock")
            };
            model.addRow(rowData);
        }

    } catch (SQLException ex) {
        Logger.getLogger(StaffDashboard.class.getName()).log(Level.SEVERE, null, ex);
    }
}
public void refreshTable2() {
    try {
        DefaultTableModel model = (DefaultTableModel) table3.getModel();
        model.setRowCount(0); // Clear the table before reloading data

        pst = con.prepareStatement("SELECT * FROM transaction_history");
        rs = pst.executeQuery();
        comboo.removeAllItems();
        while (rs.next()) {
            Object[] rowData = {
                rs.getString("id"),
                rs.getString("inv_Number"),
                rs.getDate("Date"),
                rs.getString("Email"),
                rs.getString("Total_Price")
            };
            model.addRow(rowData);
            comboo.addItem(rs.getString("id"));
        }

    } catch (SQLException ex) {
        Logger.getLogger(StaffDashboard.class.getName()).log(Level.SEVERE, null, ex);
    }
}

public void exportProductToPDF() {
    try {
        if (COMBO.getItemCount() == 0) { // check if combo has items
            JOptionPane.showMessageDialog(null, "No products available.");
            return;
        }

        Object selectedObject = COMBO.getSelectedItem(); // get selected product
        if (selectedObject == null || selectedObject.toString().equals("Select a Product")) {
            JOptionPane.showMessageDialog(null, "Please select a product first.");
            return;
        }

        String selectedProduct = selectedObject.toString();

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to export the selected product as a PDF?",
            "Confirm Export",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, "Exporting cancelled.");
            return;
        }

        //  papangalanan yung file name
        String newFileName = JOptionPane.showInputDialog(this, "Enter file name:", selectedProduct);
        if (newFileName == null || newFileName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Export cancelled. No file name provided.");
            return;
        }

        //  kunin yung product details
        pst = con.prepareStatement("SELECT * FROM product_table WHERE Product_Name=?");
        pst.setString(1, selectedProduct);
        rs = pst.executeQuery();

        if (!rs.next()) {
            JOptionPane.showMessageDialog(null, "No record found for the selected product.");
            return;
        }

        // create the PDF 
        String directoryPath = "C:\\PDF\\"; // directory to save the PDF
        String filePath = directoryPath + newFileName + ".pdf"; // Use user-defined file name
        
        Document PDFreport = new Document();
        PdfWriter.getInstance(PDFreport, new FileOutputStream(filePath));
        PDFreport.open();

        // title Styling
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Paragraph title = new Paragraph("PRODUCT INFORMATION REPORT", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingBefore(40);
        title.setSpacingAfter(10);
        PDFreport.add(title);

        // Fetch product image and add it to the PDF
        try {
            byte[] imageBytes = rs.getBytes("Image");
            if (imageBytes != null && imageBytes.length > 0) {
                com.itextpdf.text.Image productImage = com.itextpdf.text.Image.getInstance(imageBytes);
                productImage.scaleToFit(150, 150); // resize 
                productImage.setAlignment(Element.ALIGN_LEFT); // align to the left
                PDFreport.add(productImage);
            } else {
                System.out.println("No product image found.");
            }
        } catch (Exception e) {
            System.out.println("Failed to load product image.");
        }

        // create table with product details
        PdfPTable PDFTable = new PdfPTable(2);
        PDFTable.setWidthPercentage(100);
        PDFTable.setSpacingBefore(25);

        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font valueFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);

        // add table rows
        String[] fields = {"ID", "Product Name", "Price", "Cost", "Description", "Stock"};
        String[] values = {
            rs.getString("id"),
            rs.getString("Product_Name"),
            rs.getString("Price"),
            rs.getString("Cost"),
            rs.getString("Description"),
            rs.getString("Stock")
        };

        for (int i = 0; i < fields.length; i++) {
            PdfPCell headerCell = new PdfPCell(new Phrase(fields[i], headerFont));
            headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            headerCell.setFixedHeight(30f);
            PDFTable.addCell(headerCell);

            PdfPCell valueCell = new PdfPCell(new Phrase(values[i], valueFont));
            PDFTable.addCell(valueCell);
        }

        PDFreport.add(PDFTable);
        PDFreport.close();

        JOptionPane.showMessageDialog(this, "PDF Exported Successfully! \nSaved as: " + filePath);

    } catch (SQLException | DocumentException | FileNotFoundException e) {
        Logger.getLogger(StaffDashboard.class.getName()).log(Level.SEVERE, null, e);
        JOptionPane.showMessageDialog(null, "Error exporting product: " + e.getMessage());
    }
}
 private void FetchTransaction() {
        try {
            int q;
            pst = con.prepareStatement("SELECT * FROM transaction_history");
            rs = pst.executeQuery();
            ResultSetMetaData rss = rs.getMetaData();
            q = rss.getColumnCount();
            
            DefaultTableModel def = (DefaultTableModel)table3.getModel();
            def.setRowCount(0);
            while(rs.next()) {
                Vector v3 = new Vector();
                for(int a=1; a<=q; a++){
                    v3.add(rs.getString("id"));
                    v3.add(rs.getString("Inv_Number"));
                    v3.add(rs.getString("Date"));
                    v3.add(rs.getString("Email"));
                    v3.add(rs.getString("Total_Price"));

                }
                def.addRow(v3);
            }
            
            
        } catch (SQLException ex) {
            Logger.getLogger(StaffDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
       }
private void Header2() {
    JTableHeader heads = table2.getTableHeader();

    //to enforce font, background, and foreground
    heads.setDefaultRenderer(new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = new JLabel(value.toString());
            label.setOpaque(true);
            label.setBackground(Color.BLACK);  // set background color
            label.setForeground(Color.WHITE); // kulay ng headers
            label.setFont(label.getFont().deriveFont(Font.BOLD, 14f));

            label.setHorizontalAlignment(SwingConstants.CENTER); // pampagitna
            return label;
        }
    });

    //header row height
    heads.setPreferredSize(new Dimension(heads.getWidth(), 30));

    //column widths
    TableColumnModel columnModel = table2.getColumnModel();
    columnModel.getColumn(0).setPreferredWidth(20);
    columnModel.getColumn(1).setPreferredWidth(60);
    columnModel.getColumn(2).setPreferredWidth(40);
    columnModel.getColumn(3).setPreferredWidth(40);
    columnModel.getColumn(4).setPreferredWidth(80);
    columnModel.getColumn(5).setPreferredWidth(160);
}
private void Header3() {
    JTableHeader headss = table3.getTableHeader();

    //to enforce font, background, and foreground
    headss.setDefaultRenderer(new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = new JLabel(value.toString());
            label.setOpaque(true);
            label.setBackground(Color.RED);  // set background color
            label.setForeground(Color.WHITE); // kulay ng headers
            label.setFont(label.getFont().deriveFont(Font.BOLD, 14f));

            label.setHorizontalAlignment(SwingConstants.CENTER); // pampagitna
            return label;
        }
    });

    //header row height
    headss.setPreferredSize(new Dimension(headss.getWidth(), 30));

    //column widths
    TableColumnModel columnModel = table3.getColumnModel();
    columnModel.getColumn(0).setPreferredWidth(10); //for id
    columnModel.getColumn(1).setPreferredWidth(200); //for invoice number
    columnModel.getColumn(2).setPreferredWidth(50); //date
    columnModel.getColumn(3).setPreferredWidth(100); //for email
    columnModel.getColumn(4).setPreferredWidth(100); //for total price
    
}
private void Header4() {
    JTableHeader heads = table4.getTableHeader();

    //to enforce font, background, and foreground
    heads.setDefaultRenderer(new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = new JLabel(value.toString());
            label.setOpaque(true);
            label.setBackground(Color.BLACK);  // set background color
            label.setForeground(Color.white); // kulay ng headers
            label.setFont(label.getFont().deriveFont(Font.BOLD, 14f));
            label.setHorizontalAlignment(SwingConstants.CENTER); // pampagitna
            return label;
        }
    });
    //header row height
    heads.setPreferredSize(new Dimension(heads.getWidth(), 30));

    //column widths
    TableColumnModel columnModel = table4.getColumnModel();
    columnModel.getColumn(0).setPreferredWidth(50);
    columnModel.getColumn(1).setPreferredWidth(50);
    columnModel.getColumn(2).setPreferredWidth(50);
    columnModel.getColumn(3).setPreferredWidth(50);
}
// Save the file data (as a BLOB) into the database, including file name
private void saveReportToDatabase(String fileName, byte[] fileData) {
    String dbUrl = "jdbc:mysql://localhost:3306/information_system";
    String dbUsername = "root";
    String dbPassword = "";

    // Ensure the "file_name" column exists in the database
    String sql = "INSERT INTO notifications (sender_role, recipient_role, file_name, file_data, status, timestamp) VALUES (?, ?, ?, ?, ?, ?)";

    try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
         PreparedStatement pst = con.prepareStatement(sql)) {

        // Set the parameters
        pst.setString(1, "staff");  // Sender role
        pst.setString(2, "admin");  // Recipient role
        pst.setString(3, fileName);  // PDF file name
        pst.setBytes(4, fileData);  // PDF file data (BLOB)
        pst.setString(5, "unread");  // Status
        pst.setTimestamp(6, new java.sql.Timestamp(System.currentTimeMillis()));  // Timestamp

        // Execute the insert
        int rowsInserted = pst.executeUpdate();
        if (rowsInserted > 0) {
            System.out.println("Report successfully saved to the database with file name: " + fileName);
        } else {
            System.out.println("Failed to insert report into database.");
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error saving the file to the database.");
    }
}
public void showLowStockAlerts() {
    try {
        PreparedStatement ps = con.prepareStatement("SELECT Product_Name, Stock FROM product_table WHERE Stock < 10");
        ResultSet rs = ps.executeQuery();

        StringBuilder lowStockMsg = new StringBuilder();

        while (rs.next()) {
            String name = rs.getString("Product_Name");
            int stock = rs.getInt("Stock");
            lowStockMsg.append("- ").append(name).append(" (").append(stock).append(" left)\n");
        }

        if (lowStockMsg.length() > 0) {
            JOptionPane.showMessageDialog(
                null,
                "The following products are low on stock:\n\n" + lowStockMsg.toString(),
                "Low Stock Warning",
                JOptionPane.WARNING_MESSAGE
            );
        }

        rs.close();
        ps.close();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

private void startAutoRefresh() {
        Timer timer = new Timer(4000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               refreshTable2();
              
                Fetch3();
            }
        });
        timer.start();
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenu1 = new javax.swing.JMenu();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        Home = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        SendEmail = new javax.swing.JButton();
        PDetails = new javax.swing.JButton();
        btnTRANSACTION = new javax.swing.JButton();
        btnLOGOUT = new javax.swing.JButton();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel7 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jPanel18 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        fromBtn = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        toBtn = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        subjectBtn = new javax.swing.JTextField();
        jScrollPane5 = new javax.swing.JScrollPane();
        path_attach = new javax.swing.JTextArea();
        attachbtn = new javax.swing.JButton();
        emailbtn = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        textBtn = new javax.swing.JTextArea();
        jLabel25 = new javax.swing.JLabel();
        jPanel19 = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        BTNid = new javax.swing.JTextField();
        BTNtotal = new javax.swing.JTextField();
        BTNemail = new javax.swing.JTextField();
        BTNdate = new com.toedter.calendar.JDateChooser();
        jLabel19 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        PRObtn = new javax.swing.JTextArea();
        jPanel17 = new javax.swing.JPanel();
        BTNadd = new javax.swing.JButton();
        BTNupdate = new javax.swing.JButton();
        BTNdelete = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        table3 = new javax.swing.JTable();
        LOCATE = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        comboo = new javax.swing.JComboBox<>();
        PogiArcel = new javax.swing.JButton();
        jLabel32 = new javax.swing.JLabel();
        jPanel20 = new javax.swing.JPanel();
        jPanel21 = new javax.swing.JPanel();
        first = new javax.swing.JLabel();
        txtvcode = new javax.swing.JTextField();
        txtvname = new javax.swing.JTextField();
        txttprice = new javax.swing.JTextField();
        txtpay = new javax.swing.JTextField();
        txtprice = new javax.swing.JTextField();
        txtbalance = new javax.swing.JTextField();
        txtquantity = new javax.swing.JSpinner();
        jScrollPane7 = new javax.swing.JScrollPane();
        table4 = new javax.swing.JTable();
        add = new javax.swing.JButton();
        btnprint = new javax.swing.JButton();
        jLabel29 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        txtcost = new javax.swing.JTextField();
        btnprint1 = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        btnID = new javax.swing.JTextField();
        btnNAME = new javax.swing.JTextField();
        btnPRICE = new javax.swing.JTextField();
        COMBO = new javax.swing.JComboBox<>();
        jPanel13 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        btnADD = new javax.swing.JButton();
        btnUPDATE = new javax.swing.JButton();
        btnDELETE = new javax.swing.JButton();
        btnPDF = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        btnImg = new javax.swing.JLabel();
        btnIMAGE = new javax.swing.JButton();
        btnFirst = new javax.swing.JButton();
        btnLast = new javax.swing.JButton();
        btnPrev = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();
        btnSEARCH = new javax.swing.JTextField();
        jScrollPane6 = new javax.swing.JScrollPane();
        btnDESC = new javax.swing.JTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        table2 = new javax.swing.JTable();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        Stock = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jPanel22 = new javax.swing.JPanel();
        jScrollPane10 = new javax.swing.JScrollPane();
        stafftable = new javax.swing.JTable();
        btnCALCULATOR = new javax.swing.JButton();
        CRecord1 = new javax.swing.JButton();

        jMenu1.setText("jMenu1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel1.setBackground(java.awt.Color.black);

        jPanel2.setBackground(java.awt.Color.black);

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
                .addGap(85, 85, 85)
                .addComponent(Home, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addComponent(Home, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.setBackground(java.awt.Color.black);
        jTabbedPane1.setForeground(java.awt.Color.white);
        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        jTabbedPane1.setToolTipText("");
        jTabbedPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jPanel4.setBackground(new java.awt.Color(255, 153, 102));

        jPanel9.setBackground(new java.awt.Color(0, 0, 0));

        SendEmail.setBackground(java.awt.Color.white);
        SendEmail.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        SendEmail.setForeground(java.awt.Color.black);
        SendEmail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/paper-plane (1).png"))); // NOI18N
        SendEmail.setText("Send Email");
        SendEmail.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        SendEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SendEmailActionPerformed(evt);
            }
        });

        PDetails.setBackground(new java.awt.Color(255, 255, 255));
        PDetails.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        PDetails.setForeground(new java.awt.Color(0, 0, 0));
        PDetails.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/electronic-cigarette (1).png"))); // NOI18N
        PDetails.setText("Products");
        PDetails.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        PDetails.setIconTextGap(0);
        PDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PDetailsActionPerformed(evt);
            }
        });

        btnTRANSACTION.setBackground(java.awt.Color.white);
        btnTRANSACTION.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnTRANSACTION.setForeground(java.awt.Color.black);
        btnTRANSACTION.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/order-history (1).png"))); // NOI18N
        btnTRANSACTION.setText("Transactions");
        btnTRANSACTION.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnTRANSACTION.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTRANSACTIONActionPerformed(evt);
            }
        });

        btnLOGOUT.setBackground(new java.awt.Color(255, 0, 0));
        btnLOGOUT.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnLOGOUT.setForeground(new java.awt.Color(255, 255, 255));
        btnLOGOUT.setText("Log Out");
        btnLOGOUT.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLOGOUT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLOGOUTActionPerformed(evt);
            }
        });

        jTabbedPane2.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);

        jPanel7.setBackground(java.awt.Color.black);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/HOMEVAPE.jpg"))); // NOI18N
        jLabel2.setToolTipText("");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 1226, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(34, 34, 34))
        );

        jTabbedPane2.addTab("tab1", jPanel7);

        jPanel5.setBackground(new java.awt.Color(50, 50, 50));

        jPanel18.setBackground(new java.awt.Color(102, 102, 102));

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 15)); // NOI18N
        jLabel10.setForeground(java.awt.Color.white);
        jLabel10.setText("From:");

        fromBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fromBtnActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 15)); // NOI18N
        jLabel12.setForeground(java.awt.Color.white);
        jLabel12.setText("To:");

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 15)); // NOI18N
        jLabel11.setForeground(java.awt.Color.white);
        jLabel11.setText("Subject:");

        path_attach.setColumns(20);
        path_attach.setRows(5);
        path_attach.setEnabled(false);
        jScrollPane5.setViewportView(path_attach);

        attachbtn.setBackground(new java.awt.Color(0, 102, 102));
        attachbtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        attachbtn.setForeground(java.awt.Color.white);
        attachbtn.setText("Attach File");
        attachbtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        attachbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                attachbtnActionPerformed(evt);
            }
        });

        emailbtn.setBackground(new java.awt.Color(78, 255, 51));
        emailbtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        emailbtn.setForeground(java.awt.Color.white);
        emailbtn.setText("Send Email");
        emailbtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        emailbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                emailbtnActionPerformed(evt);
            }
        });

        textBtn.setColumns(20);
        textBtn.setLineWrap(true);
        textBtn.setRows(5);
        textBtn.setToolTipText("");
        textBtn.setWrapStyleWord(true);
        jScrollPane3.setViewportView(textBtn);

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 15)); // NOI18N
        jLabel25.setForeground(java.awt.Color.white);
        jLabel25.setText("Message:");

        jPanel19.setBackground(new java.awt.Color(211, 0, 59));

        jLabel26.setBackground(new java.awt.Color(50, 50, 50));
        jLabel26.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel26.setForeground(java.awt.Color.white);
        jLabel26.setText("SEND EMAIL");

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addGap(152, 152, 152)
                .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(157, Short.MAX_VALUE))
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(8, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel18Layout.createSequentialGroup()
                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(66, 66, 66)
                                .addComponent(fromBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel18Layout.createSequentialGroup()
                                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel11))
                                .addGap(50, 50, 50)
                                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(subjectBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel18Layout.createSequentialGroup()
                                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(attachbtn))))
                            .addGroup(jPanel18Layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addGap(101, 101, 101)
                                .addComponent(toBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addGap(207, 207, 207)
                        .addComponent(emailbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fromBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addGap(18, 18, 18)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(toBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addGap(16, 16, 16)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(subjectBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addGap(62, 62, 62)
                        .addComponent(jLabel25)))
                .addGap(18, 18, 18)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel18Layout.createSequentialGroup()
                        .addComponent(attachbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(45, 45, 45)))
                .addComponent(emailbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(143, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(368, 368, 368)
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(363, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel18, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("tab2", jPanel5);

        jPanel15.setBackground(new java.awt.Color(50, 50, 50));

        jPanel16.setBackground(new java.awt.Color(50, 50, 50));

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel18.setForeground(java.awt.Color.white);
        jLabel18.setText("ID:");

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel21.setForeground(java.awt.Color.white);
        jLabel21.setText("Date:");

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel23.setForeground(java.awt.Color.white);
        jLabel23.setText("Email:");

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel24.setForeground(java.awt.Color.white);
        jLabel24.setText("Total Price:");

        BTNid.setEnabled(false);
        BTNid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BTNidActionPerformed(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel19.setForeground(java.awt.Color.white);
        jLabel19.setText("Purchased Products:");

        PRObtn.setColumns(20);
        PRObtn.setLineWrap(true);
        PRObtn.setRows(5);
        PRObtn.setWrapStyleWord(true);
        jScrollPane8.setViewportView(PRObtn);

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(BTNtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(BTNdate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel16Layout.createSequentialGroup()
                                .addComponent(BTNid, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel16Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(BTNemail, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel19)
                            .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(17, 17, 17))))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(BTNid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel21)
                            .addComponent(BTNdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel23)
                            .addComponent(BTNemail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel24)
                            .addComponent(BTNtotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(85, Short.MAX_VALUE))
                    .addComponent(jScrollPane8)))
        );

        jPanel17.setBackground(new java.awt.Color(50, 50, 50));
        jPanel17.setForeground(new java.awt.Color(80, 80, 80));

        BTNadd.setBackground(new java.awt.Color(78, 255, 51));
        BTNadd.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        BTNadd.setForeground(new java.awt.Color(255, 255, 255));
        BTNadd.setText("Add");
        BTNadd.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        BTNadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BTNaddActionPerformed(evt);
            }
        });

        BTNupdate.setBackground(new java.awt.Color(61, 193, 255));
        BTNupdate.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        BTNupdate.setForeground(new java.awt.Color(255, 255, 255));
        BTNupdate.setText("Update");
        BTNupdate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        BTNupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BTNupdateActionPerformed(evt);
            }
        });

        BTNdelete.setBackground(new java.awt.Color(255, 0, 0));
        BTNdelete.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        BTNdelete.setForeground(new java.awt.Color(255, 255, 255));
        BTNdelete.setText("Delete");
        BTNdelete.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        BTNdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BTNdeleteActionPerformed(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(50, 50, 50));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton1.setForeground(java.awt.Color.white);
        jButton1.setText("New");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(215, 0, 0));
        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Export to PDF");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(BTNadd)
                .addGap(18, 18, 18)
                .addComponent(BTNupdate)
                .addGap(18, 18, 18)
                .addComponent(BTNdelete)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1)
                .addGap(18, 18, 18)
                .addComponent(jButton3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BTNadd, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BTNupdate, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BTNdelete, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18))
        );

        table3.setBackground(java.awt.Color.black);
        table3.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        table3.setForeground(java.awt.Color.white);
        table3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID", "Invoice Number", "Date", "Email (Optional)", "Total Price"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table3.setPreferredSize(new java.awt.Dimension(525, 1000));
        table3.setRowHeight(25);
        table3.setSelectionBackground(java.awt.Color.white);
        table3.setSelectionForeground(java.awt.Color.red);
        table3.getTableHeader().setReorderingAllowed(false);
        table3.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                table3PropertyChange(evt);
            }
        });
        jScrollPane2.setViewportView(table3);
        if (table3.getColumnModel().getColumnCount() > 0) {
            table3.getColumnModel().getColumn(0).setResizable(false);
            table3.getColumnModel().getColumn(1).setResizable(false);
            table3.getColumnModel().getColumn(2).setResizable(false);
            table3.getColumnModel().getColumn(3).setResizable(false);
            table3.getColumnModel().getColumn(4).setResizable(false);
        }

        LOCATE.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                LOCATEKeyReleased(evt);
            }
        });

        jLabel27.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel27.setForeground(java.awt.Color.white);
        jLabel27.setText("Search:");

        comboo.setForeground(new java.awt.Color(255, 255, 255));
        comboo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combooActionPerformed(evt);
            }
        });

        PogiArcel.setBackground(new java.awt.Color(0, 102, 255));
        PogiArcel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        PogiArcel.setForeground(new java.awt.Color(255, 255, 255));
        PogiArcel.setText("Find");
        PogiArcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PogiArcelActionPerformed(evt);
            }
        });

        jLabel32.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel32.setForeground(java.awt.Color.white);
        jLabel32.setText("Find ID:");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel27)
                                    .addComponent(jLabel32))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel15Layout.createSequentialGroup()
                                        .addComponent(comboo, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(PogiArcel))
                                    .addComponent(LOCATE)))))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1228, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 4, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                        .addGap(67, 67, 67)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel27)
                            .addComponent(LOCATE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(comboo, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(PogiArcel, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel32))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 71, Short.MAX_VALUE)
                        .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 368, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane2.addTab("tab5", jPanel15);

        jPanel20.setBackground(new java.awt.Color(50, 50, 50));

        jPanel21.setBackground(new java.awt.Color(50, 50, 50));

        first.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        first.setForeground(java.awt.Color.white);
        first.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        txtvcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtvcodeActionPerformed(evt);
            }
        });
        txtvcode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtvcodeKeyPressed(evt);
            }
        });

        txttprice.setBackground(java.awt.Color.white);
        txttprice.setFont(new java.awt.Font("Segoe UI", 0, 65)); // NOI18N
        txttprice.setForeground(java.awt.Color.black);
        txttprice.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txttprice.setCaretColor(java.awt.Color.white);
        txttprice.setDisabledTextColor(java.awt.Color.black);
        txttprice.setEnabled(false);
        txttprice.setSelectedTextColor(new java.awt.Color(255, 255, 255));
        txttprice.setSelectionColor(new java.awt.Color(255, 0, 0));

        txtpay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtpayActionPerformed(evt);
            }
        });
        txtpay.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtpayKeyPressed(evt);
            }
        });

        txtprice.setEnabled(false);
        txtprice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtpriceActionPerformed(evt);
            }
        });

        txtbalance.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtbalanceKeyPressed(evt);
            }
        });

        txtquantity.setToolTipText("");
        txtquantity.setRequestFocusEnabled(false);

        table4.setBackground(java.awt.Color.white);
        table4.setFont(new java.awt.Font("Arial", 1, 15)); // NOI18N
        table4.setForeground(java.awt.Color.black);
        table4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Vape Name", "Price", "Quantity", "Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table4.setRowHeight(25);
        table4.setSelectionBackground(java.awt.Color.red);
        table4.setSelectionForeground(java.awt.Color.white);
        table4.getTableHeader().setReorderingAllowed(false);
        table4.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                table4PropertyChange(evt);
            }
        });
        jScrollPane7.setViewportView(table4);
        if (table4.getColumnModel().getColumnCount() > 0) {
            table4.getColumnModel().getColumn(0).setResizable(false);
            table4.getColumnModel().getColumn(1).setResizable(false);
            table4.getColumnModel().getColumn(2).setResizable(false);
            table4.getColumnModel().getColumn(3).setResizable(false);
        }

        add.setBackground(java.awt.Color.white);
        add.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        add.setForeground(java.awt.Color.black);
        add.setText("ADD");
        add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addActionPerformed(evt);
            }
        });

        btnprint.setBackground(java.awt.Color.white);
        btnprint.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnprint.setForeground(java.awt.Color.black);
        btnprint.setText("Print Receipt");
        btnprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnprintActionPerformed(evt);
            }
        });

        jLabel29.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel29.setForeground(java.awt.Color.white);
        jLabel29.setText("Vape ID:");

        jLabel33.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(255, 255, 255));
        jLabel33.setText("Vape Name:");

        jLabel34.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(255, 255, 255));
        jLabel34.setText("Price:");

        jLabel35.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(255, 255, 255));
        jLabel35.setText("Quantity:");

        jLabel36.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel36.setForeground(new java.awt.Color(255, 255, 255));
        jLabel36.setText("Total Price:");

        jLabel37.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel37.setForeground(new java.awt.Color(255, 255, 255));
        jLabel37.setText("Change:");

        jLabel38.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel38.setForeground(new java.awt.Color(255, 255, 255));
        jLabel38.setText("Cash:");

        btnprint1.setBackground(java.awt.Color.white);
        btnprint1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnprint1.setForeground(java.awt.Color.black);
        btnprint1.setText("Clear Table");
        btnprint1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnprint1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel33)
                    .addComponent(jLabel29)
                    .addComponent(jLabel34)
                    .addComponent(jLabel35))
                .addGap(57, 57, 57)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addComponent(txtquantity, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(add, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtprice, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(txtvname, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txtvcode, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)))
                .addGap(9, 9, 9)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel38)
                    .addComponent(jLabel37))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtpay, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtbalance, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtcost, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39))
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addGap(62, 62, 62)
                        .addComponent(jLabel36)
                        .addGap(20, 20, 20)
                        .addComponent(txttprice, javax.swing.GroupLayout.PREFERRED_SIZE, 442, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(first, javax.swing.GroupLayout.PREFERRED_SIZE, 322, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(88, 88, 88))))
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 1025, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnprint1, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnprint))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel21Layout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtvcode, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtpay, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel38))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtvname, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel37))
                                    .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel36)
                                        .addComponent(txtbalance, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jPanel21Layout.createSequentialGroup()
                                .addGap(84, 84, 84)
                                .addComponent(first, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtprice, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(50, 50, 50)
                        .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel21Layout.createSequentialGroup()
                                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel35)
                                    .addComponent(add, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel21Layout.createSequentialGroup()
                                .addComponent(txtquantity, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(21, 21, 21))))
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(txttprice, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addComponent(btnprint, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(154, 154, 154)
                        .addComponent(btnprint1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(363, 363, 363)
                        .addComponent(txtcost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(433, 433, 433))
        );

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, 1515, Short.MAX_VALUE)
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, 643, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane2.addTab("tab6", jPanel20);

        jPanel10.setBackground(new java.awt.Color(50, 50, 50));

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("ID:");

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Product Name:");

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("Price:");

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("Description:");

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("Image:");

        btnID.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        btnID.setEnabled(false);

        btnNAME.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));

        COMBO.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        COMBO.setToolTipText("");
        COMBO.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        COMBO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                COMBOActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 247, Short.MAX_VALUE)
        );

        jPanel14.setBackground(new java.awt.Color(50, 50, 50));

        btnADD.setBackground(new java.awt.Color(78, 255, 51));
        btnADD.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnADD.setForeground(java.awt.Color.white);
        btnADD.setText("Add");
        btnADD.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnADD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnADDActionPerformed(evt);
            }
        });

        btnUPDATE.setBackground(new java.awt.Color(61, 193, 255));
        btnUPDATE.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnUPDATE.setForeground(java.awt.Color.white);
        btnUPDATE.setText("Update");
        btnUPDATE.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUPDATE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUPDATEActionPerformed(evt);
            }
        });

        btnDELETE.setBackground(new java.awt.Color(255, 0, 0));
        btnDELETE.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnDELETE.setForeground(java.awt.Color.white);
        btnDELETE.setText("Delete");
        btnDELETE.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDELETE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDELETEActionPerformed(evt);
            }
        });

        btnPDF.setBackground(new java.awt.Color(215, 0, 0));
        btnPDF.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnPDF.setForeground(java.awt.Color.white);
        btnPDF.setText("Export as PDF");
        btnPDF.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPDFActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(50, 50, 50));
        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton2.setForeground(java.awt.Color.white);
        jButton2.setText("New");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(btnADD)
                .addGap(18, 18, 18)
                .addComponent(btnUPDATE)
                .addGap(18, 18, 18)
                .addComponent(btnDELETE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnPDF)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addContainerGap(15, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnADD, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUPDATE, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDELETE, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPDF, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnImg.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        btnIMAGE.setBackground(new java.awt.Color(0, 102, 102));
        btnIMAGE.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnIMAGE.setForeground(java.awt.Color.white);
        btnIMAGE.setText("ATTACH FILE");
        btnIMAGE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIMAGEActionPerformed(evt);
            }
        });

        btnFirst.setBackground(new java.awt.Color(0, 102, 102));
        btnFirst.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnFirst.setForeground(java.awt.Color.white);
        btnFirst.setText("First");
        btnFirst.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnFirst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFirstActionPerformed(evt);
            }
        });

        btnLast.setBackground(new java.awt.Color(215, 0, 0));
        btnLast.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLast.setForeground(java.awt.Color.white);
        btnLast.setText("Last");
        btnLast.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLastActionPerformed(evt);
            }
        });

        btnPrev.setBackground(new java.awt.Color(215, 0, 0));
        btnPrev.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnPrev.setForeground(java.awt.Color.white);
        btnPrev.setText("Previous");
        btnPrev.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPrev.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrevActionPerformed(evt);
            }
        });

        btnNext.setBackground(new java.awt.Color(0, 102, 102));
        btnNext.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnNext.setForeground(java.awt.Color.white);
        btnNext.setText("Next");
        btnNext.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextActionPerformed(evt);
            }
        });

        btnSEARCH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSEARCHActionPerformed(evt);
            }
        });
        btnSEARCH.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                btnSEARCHKeyReleased(evt);
            }
        });

        btnDESC.setColumns(20);
        btnDESC.setLineWrap(true);
        btnDESC.setRows(5);
        btnDESC.setWrapStyleWord(true);
        jScrollPane6.setViewportView(btnDESC);

        table2.setBackground(java.awt.Color.black);
        table2.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        table2.setForeground(java.awt.Color.white);
        table2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Product Name", "Price", "Cost", "Description", "Stocks"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table2.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        table2.setAutoscrolls(false);
        table2.setFocusable(false);
        table2.setPreferredSize(new java.awt.Dimension(300, 1000));
        table2.setRowHeight(25);
        table2.setSelectionBackground(java.awt.Color.white);
        table2.setSelectionForeground(java.awt.Color.red);
        table2.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        table2.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        table2.setShowGrid(true);
        table2.setShowHorizontalLines(false);
        table2.setShowVerticalLines(false);
        table2.getTableHeader().setReorderingAllowed(false);
        table2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table2MouseClicked(evt);
            }
        });
        table2.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                table2PropertyChange(evt);
            }
        });
        jScrollPane4.setViewportView(table2);

        jLabel30.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel30.setForeground(java.awt.Color.white);
        jLabel30.setText("Search:");

        jLabel31.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel31.setForeground(java.awt.Color.white);
        jLabel31.setText("Find Product:");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setForeground(java.awt.Color.white);
        jLabel3.setText("Stocks:");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(10, 10, 10)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnPRICE, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                            .addComponent(btnNAME)
                            .addComponent(btnID))
                        .addGap(105, 105, 105)
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(169, 169, 169)
                        .addComponent(btnIMAGE)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addGap(90, 90, 90)
                                .addComponent(jLabel31))
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addGap(121, 121, 121)
                                .addComponent(jLabel30)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnSEARCH, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(COMBO, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Stock, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addGap(137, 137, 137)
                                .addComponent(btnImg, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(158, 158, 158)
                                .addComponent(btnNext, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnPrev)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnLast, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnFirst, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))))
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 1219, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(290, 290, 290))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(btnID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14)
                            .addComponent(btnNAME, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(btnPRICE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Stock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 69, Short.MAX_VALUE)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                                .addComponent(jLabel16)
                                .addGap(57, 57, 57))))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(btnImg, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnSEARCH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel30))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnIMAGE, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel31)
                                .addComponent(COMBO, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(40, 40, 40)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnPrev, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnLast, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnNext, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnFirst, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 329, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        jTabbedPane2.addTab("tab4", jPanel10);

        stafftable.setBackground(java.awt.Color.black);
        stafftable.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        stafftable.setForeground(java.awt.Color.white);
        stafftable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "id", "Sender Role", "File Name", "Status", "Date and Time"
            }
        ));
        stafftable.setEnabled(false);
        stafftable.setRowHeight(32);
        jScrollPane10.setViewportView(stafftable);

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 1276, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 655, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("tab7", jPanel22);

        btnCALCULATOR.setBackground(java.awt.Color.white);
        btnCALCULATOR.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnCALCULATOR.setForeground(java.awt.Color.black);
        btnCALCULATOR.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/printing (3) (1).png"))); // NOI18N
        btnCALCULATOR.setText("Print Receipt");
        btnCALCULATOR.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCALCULATOR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCALCULATORActionPerformed(evt);
            }
        });

        CRecord1.setBackground(java.awt.Color.white);
        CRecord1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        CRecord1.setForeground(java.awt.Color.black);
        CRecord1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/report (1).png"))); // NOI18N
        CRecord1.setText("Reports");
        CRecord1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        CRecord1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CRecord1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(CRecord1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnTRANSACTION, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(PDetails, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnCALCULATOR, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(SendEmail, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(btnLOGOUT, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, 0)
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1232, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(btnCALCULATOR, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PDetails, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnTRANSACTION, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SendEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CRecord1, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnLOGOUT, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(265, 265, 265))
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 649, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("1", jPanel4);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 684, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void HomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HomeActionPerformed
        // TODO add your handling code here:
        jTabbedPane1.setSelectedIndex(0);
        jTabbedPane2.setSelectedIndex(0);
    }//GEN-LAST:event_HomeActionPerformed

    private void SendEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SendEmailActionPerformed
        // TODO add your handling code here:
        jTabbedPane2.setSelectedIndex(1);
    }//GEN-LAST:event_SendEmailActionPerformed

    private void PDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PDetailsActionPerformed
        // TODO add your handling code here:
        jTabbedPane2.setSelectedIndex(4);
    }//GEN-LAST:event_PDetailsActionPerformed

    private void emailbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_emailbtnActionPerformed
        // TODO add your handling code here:

  String From = fromBtn.getText();
    String to = toBtn.getText();
    String subject = subjectBtn.getText();
    String text = textBtn.getText();

    Properties props = new Properties();
    props.put("mail.smtp.host", "smtp.gmail.com");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.port", "587");
    props.put("mail.smtp.starttls.enable", "true");

    Session session  = Session.getDefaultInstance(props,
        new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("arcelzamora068@gmail.com", "aqvsxkyuisimqsiu");
            }
        }
    );

    try {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(From));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);

        // Email body
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText(text);

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        // Attach Multiple PDFs
        if (!attachmentFiles.isEmpty()) {
            for (File file : attachmentFiles) {
                MimeBodyPart attachmentPart = new MimeBodyPart();
                FileDataSource source = new FileDataSource(file);
                attachmentPart.setDataHandler(new DataHandler(source));

                String newFileName = file.getName();
                boolean validName = false;
                // para payagan si user na mag back 
                while (!validName) {
                    JTextField textField = new JTextField(newFileName);
                    Object[] messageBox = {
                        "Enter new name for: " + file.getName(),
                        textField
                    };

                    int option = JOptionPane.showOptionDialog(
                        null, 
                        messageBox, 
                        "Rename File", 
                        JOptionPane.OK_CANCEL_OPTION, 
                        JOptionPane.QUESTION_MESSAGE, 
                        null, 
                        new String[]{"OK", "Back"}, 
                        "OK"
                    );

                    if (option == JOptionPane.OK_OPTION) {
                        if (!textField.getText().trim().isEmpty()) {
                            newFileName = textField.getText().trim() + ".pdf"; // set new name
                            validName = true;
                        }
                    }  else if (option == JOptionPane.CANCEL_OPTION || option == JOptionPane.CLOSED_OPTION) {
                    // pag clinick ni user yung cancel, mawawala na
                    JOptionPane.showMessageDialog(null, "Attachment renaming canceled.");
                    return;  // para maiwasan ang looping
                    }
                }

                attachmentPart.setFileName(newFileName);
                multipart.addBodyPart(attachmentPart);
            }
        }

        message.setContent(multipart);

        // Send email
        Transport.send(message);
        JOptionPane.showMessageDialog(null, "Email sent successfully!");

    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
    }
    }//GEN-LAST:event_emailbtnActionPerformed

    private void fromBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fromBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fromBtnActionPerformed

    private void attachbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_attachbtnActionPerformed
        // TODO add your handling code here:
    JFileChooser choose = new JFileChooser();
    choose.setMultiSelectionEnabled(true); // Allow multiple selection
    choose.setDialogTitle("Select PDF Files to Attach");

    FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Documents (*.pdf)", "pdf");
    choose.setFileFilter(filter);

    int returnValue = choose.showOpenDialog(null); 

    if (returnValue == JFileChooser.APPROVE_OPTION) {
        File[] selectedFiles = choose.getSelectedFiles();
        attachmentFiles.clear(); // Clear previous selections

        StringBuilder displayPaths = new StringBuilder();

        for (File filee : selectedFiles) {
            if (filee.getName().toLowerCase().endsWith(".pdf")) {
                attachmentFiles.add(filee);
                displayPaths.append(filee.getAbsolutePath()).append("\n"); // Show in UI
            }
        }

        // Display selected files in JTextArea (path_attach should be JTextArea)
        path_attach.setText(displayPaths.toString());
    }




    }//GEN-LAST:event_attachbtnActionPerformed

    private void btnADDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnADDActionPerformed
        // TODO add your handling code here:
        if(checkInputs() && ImgPath != null)
            try {          
            pst = con.prepareStatement("INSERT INTO product_table (Product_Name, Price, Description, Image, Stock)"
                    + " VALUES (?, ?, ?, ?, ?)");
            pst.setString(1, btnNAME.getText());
            pst.setString(2, btnPRICE.getText().trim());
            pst.setString(3, btnDESC.getText());
            InputStream img = new FileInputStream(new File(ImgPath));
            pst.setBlob(4, img);
            pst.setString(5, Stock.getText().trim());
            pst.executeUpdate();
            Show_Products();
            loadProductNames();
            fetchProductDetails();
            clearFields();
            JOptionPane.showMessageDialog(null, "Product is added successfully!");
         } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(null, "Invalid ID format. Please enter a number.");
        } catch (Exception ex) {
           JOptionPane.showMessageDialog(null, ex.getMessage());
        }else{
            JOptionPane.showMessageDialog(null, "One or more field are Empty.");
        }

    }//GEN-LAST:event_btnADDActionPerformed

    private void btnIMAGEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIMAGEActionPerformed
        // TODO add your handling code here:
        JFileChooser FILE = new JFileChooser();
        FILE.setCurrentDirectory(new File(System.getProperty("user.home")));
        
        FileNameExtensionFilter filter = new FileNameExtensionFilter("*.image", "jpg", "png");
        FILE.addChoosableFileFilter(filter);
        int result = FILE.showSaveDialog(null);
        if(result == JFileChooser.APPROVE_OPTION)
        {
            File selectedFile = FILE.getSelectedFile();
            String path = selectedFile.getAbsolutePath();
            btnImg.setIcon(ResizeImage(path, null));
            ImgPath = path;
        }else{
            System.out.println("No File Selected");
        }
    }//GEN-LAST:event_btnIMAGEActionPerformed

    private void btnUPDATEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUPDATEActionPerformed
        // TODO add your handling code here:
        
         if (checkInputs() && !btnID.getText().trim().isEmpty()) {
    String UpdateQuery;
    PreparedStatement pst = null;

    try {
        // Update without image
        if (ImgPath == null) {
            UpdateQuery = "UPDATE product_table SET Product_Name = ?, Price = ?, Description = ?, Stock = ? WHERE id = ?";
            pst = con.prepareStatement(UpdateQuery);

            pst.setString(1, btnNAME.getText());
            pst.setString(2, btnPRICE.getText());
            pst.setString(3, btnDESC.getText());
            pst.setInt(5, Integer.parseInt(btnID.getText()));
            pst.setInt(4, Integer.parseInt(Stock.getText()));
        } else { 
            // Update with image
            File imageFile = new File(ImgPath);
            if (!imageFile.exists()) {
                JOptionPane.showMessageDialog(null, "Image file not found!");
                return;
            }

            InputStream img = new FileInputStream(imageFile);
            UpdateQuery = "UPDATE product_table SET Product_Name = ?, Price = ?, Description = ?, Image = ?, Stock = ? WHERE id = ?";
            pst = con.prepareStatement(UpdateQuery);

            pst.setString(1, btnNAME.getText());
            pst.setString(2, btnPRICE.getText());
            pst.setString(3, btnDESC.getText());
            pst.setBlob(4, img);
            pst.setInt(5, Integer.parseInt(Stock.getText()));
        }

        int rowsUpdated = pst.executeUpdate();
        if (rowsUpdated > 0) {
            JOptionPane.showMessageDialog(null, "Product updated successfully!");

            // Always refresh product list and combo box after updating
            Show_Products(); // Refresh product table
            loadProductNames(); // Refresh combo box
            fetchProductDetails(); // Update UI
           

            //  Explicitly update the combo box selection
            String selectedProduct = btnNAME.getText(); 
            COMBO.setSelectedItem(selectedProduct);
            COMBO.repaint(); // Force refresh if needed

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
            if (pst != null) pst.close();
        } catch (SQLException ex) {
            Logger.getLogger(StaffDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
} else {
    JOptionPane.showMessageDialog(null, "One or more fields are empty or invalid!");
}


    }//GEN-LAST:event_btnUPDATEActionPerformed

    private void btnDELETEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDELETEActionPerformed
        // TODO add your handling code here:
        
   try {
    String PID = COMBO.getSelectedItem().toString();
    
    // show confirmation dialog before executing delete
    int confirm = JOptionPane.showConfirmDialog(null, 
        "Are you sure you want to delete this product?\n\nProduct: " + PID, 
        "Confirmation", 
        JOptionPane.YES_NO_OPTION);
    
    if (confirm == JOptionPane.YES_OPTION) {
        pst = con.prepareStatement("DELETE FROM product_table WHERE Product_name=?");
        pst.setString(1, PID);
        
        int a = pst.executeUpdate();
        
        if (a == 1) {
            JOptionPane.showMessageDialog(this, "The selected product has been deleted successfully!");

            // Clear input fields
            btnNAME.setText("");
            btnPRICE.setText("");
            btnDESC.setText("");
            btnNAME.requestFocus();

            // Refresh product list
            Show_Products();
            
            // Remove the deleted item from the combo box
            COMBO.removeItem(PID);

            // Set the first available item in the combo box after deletion
            if (COMBO.getItemCount() > 0) {
                COMBO.setSelectedIndex(0);
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



    }//GEN-LAST:event_btnDELETEActionPerformed

    private void table2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table2MouseClicked
        // TODO add your handling code here:
     int index = table2.getSelectedRow(); // Get selected row index
ShowItem(index); // Show details of selected item

int selectedRow = table2.getSelectedRow(); // Store selected row index
loadProductNames(); // Reload product names

// Restore selection after table update
if (selectedRow >= 0 && selectedRow < table2.getRowCount()) {
    table2.setRowSelectionInterval(selectedRow, selectedRow); 
}     
    }//GEN-LAST:event_table2MouseClicked

    private void btnFirstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFirstActionPerformed
        // TODO add your handling code here:
      pos = 0;  // Reset to first row
ShowItem(pos);  // Display the first item
Show_Products();  // Refresh the product list

// Ensure the combo box is updated
String selectedProduct = getProductList().get(pos).getName(); 
COMBO.setSelectedItem(selectedProduct);

    }//GEN-LAST:event_btnFirstActionPerformed

    private void btnLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLastActionPerformed
        // TODO add your handling code here:
       pos = getProductList().size() - 1;  // Set position to last item
ShowItem(pos);  // Display last item
Show_Products();  // Refresh product list

// Explicitly update the combo box
String selectedProduct = getProductList().get(pos).getName(); 
COMBO.setSelectedItem(selectedProduct);

    }//GEN-LAST:event_btnLastActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        // TODO add your handling code here:
     pos++; // Move to the next position

if (pos >= getProductList().size()) { 
    pos = 0; // If exceeded last row, go back to the first row
}

ShowItem(pos); // Display the selected item
Show_Products(); // Refresh product list

// Update the combo box selection based on the new position
String selectedProduct = getProductList().get(pos).getName(); // Assuming getName() returns the product name
COMBO.setSelectedItem(selectedProduct);

    }//GEN-LAST:event_btnNextActionPerformed

    private void btnSEARCHKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnSEARCHKeyReleased
        // TODO add your handling code here:
         DefaultTableModel check = (DefaultTableModel) table2.getModel();
        TableRowSorter<DefaultTableModel> obj = new TableRowSorter<>(check);
        table2.setRowSorter(obj);
        obj.setRowFilter(RowFilter.regexFilter(btnSEARCH.getText()));
    }//GEN-LAST:event_btnSEARCHKeyReleased

    private void COMBOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_COMBOActionPerformed
        // TODO add your handling code here:
        fetchProductDetails();
        
    }//GEN-LAST:event_COMBOActionPerformed

    private void btnPrevActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrevActionPerformed
        // TODO add your handling code here:
        pos--; // Move to the previous position
if (pos < 0) { 
    pos = getProductList().size() - 1; // If before first row, go to last row
}

ShowItem(pos); // Display the selected item
Show_Products(); // Refresh product list

// Update the combo box selection based on the new position
String selectedProduct = getProductList().get(pos).getName(); // Assuming getName() returns the product name
COMBO.setSelectedItem(selectedProduct);

    }//GEN-LAST:event_btnPrevActionPerformed

    private void btnPDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPDFActionPerformed
        // TODO add your handling code here:
              exportProductToPDF();
    }//GEN-LAST:event_btnPDFActionPerformed

    private void btnTRANSACTIONActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTRANSACTIONActionPerformed
        // TODO add your handling code here:
        jTabbedPane2.setSelectedIndex(2);
    }//GEN-LAST:event_btnTRANSACTIONActionPerformed

    private void btnLOGOUTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLOGOUTActionPerformed
        // TODO add your handling code here:
        
        int i = JOptionPane.showConfirmDialog(null, "Are you sure you want to Logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (i==0) {
            LoginForm first = new LoginForm();
            JOptionPane.showMessageDialog(null, "Logout successfully!");
             first.setVisible(true);
       
       dispose();
        } else {
         LoginForm first = new LoginForm();
         JOptionPane.showMessageDialog(null, "Logout cancelled.");
       first.setVisible(false);
        }
    }//GEN-LAST:event_btnLOGOUTActionPerformed

    private void BTNaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BTNaddActionPerformed
      if(PRObtn.getText().isEmpty()){
            JOptionPane.showMessageDialog(this, "Product name is required!");
        }else if(BTNdate.getDate() == null){
            JOptionPane.showMessageDialog(this, "Date is required!");
        }else if(BTNdate.getDate() == null){
            JOptionPane.showMessageDialog(this, "Email is required!");
        }else if(BTNtotal.getText().isEmpty()){
            JOptionPane.showMessageDialog(this, "Price total is required!");
        }else{
        try {
            // TODO add your handling code here:
            String btprod = PRObtn.getText();
       
            SimpleDateFormat  dateformat = new SimpleDateFormat("yyyy-MM-dd");
            String btdate = dateformat.format(BTNdate.getDate());
            
            String btmail = BTNemail.getText();
            String btprice = BTNtotal.getText();
            
            pst = con.prepareStatement("INSERT INTO transaction_history (Inv_Number, Date, Email, Total_Price)VALUES(?, ?, ?, ?)");
            pst.setString(1, btprod);     
            pst.setString(2, btdate);
            pst.setString(3, btmail);
            pst.setString(4, btprice);
            
            int k = pst.executeUpdate();
            
            if(k==1)  {
                JOptionPane.showMessageDialog(this, "Record Successfully");
                PRObtn.setText("");
                dateformat.format(BTNdate.getDate());
                BTNemail.setText("");
                BTNtotal.setText("");
                FetchTransaction();
            }else {
                JOptionPane.showMessageDialog(this, "Unsuccessfully Recorded");
            }
        } catch (SQLException ex) {
            Logger.getLogger(StaffDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
    }//GEN-LAST:event_BTNaddActionPerformed

    private void LOCATEKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_LOCATEKeyReleased
        // TODO add your handling code here:
         DefaultTableModel check = (DefaultTableModel) table3.getModel();
        TableRowSorter<DefaultTableModel> obj = new TableRowSorter<>(check);
        table3.setRowSorter(obj);
        obj.setRowFilter(RowFilter.regexFilter(LOCATE.getText()));
    }//GEN-LAST:event_LOCATEKeyReleased

    private void BTNidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BTNidActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_BTNidActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        clearFields();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void btnCALCULATORActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCALCULATORActionPerformed
        // TODO add your handling code here:
        jTabbedPane2.setSelectedIndex(3);
    }//GEN-LAST:event_btnCALCULATORActionPerformed

    private void table2PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_table2PropertyChange
        // TODO add your handling code here:
        Header2();
    }//GEN-LAST:event_table2PropertyChange

    private void btnSEARCHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSEARCHActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSEARCHActionPerformed

    private void table3PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_table3PropertyChange
        // TODO add your handling code here:
        Header3();
    }//GEN-LAST:event_table3PropertyChange

    private void BTNdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BTNdeleteActionPerformed
        // TODO add your handling code here:
  try {
        String transactionID = comboo.getSelectedItem().toString();

        // Ask for delete confirmation
        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to request deletion for this record?", 
                                                    "Delete Request Confirmation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Ask for reason
            String reason = JOptionPane.showInputDialog(null, "Enter reason for deletion:", "Reason Required", JOptionPane.QUESTION_MESSAGE);

            if (reason != null && !reason.trim().isEmpty()) {
                // Insert the delete request into loaddeleterequest table
                PreparedStatement pst = con.prepareStatement("INSERT INTO loaddeleterequest (transaction_id, reason, status) VALUES (?, ?, 'Pending')");
                pst.setString(1, transactionID);
                pst.setString(2, reason);

                int k = pst.executeUpdate();

                if (k == 1) {
                    JOptionPane.showMessageDialog(this, "Delete request sent to admin.");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to send delete request.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Deletion request canceled. Reason is required.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Deletion request canceled.");
        }

    } catch (SQLException ex) {
        Logger.getLogger(StaffDashboard.class.getName()).log(Level.SEVERE, null, ex);
    }

        
    }//GEN-LAST:event_BTNdeleteActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
         try {
     
    int confirm = JOptionPane.showConfirmDialog(
        this,
        "Are you sure you want to export the data as a PDF?",
        "Confirm Export",
        JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) {
            pst = con.prepareStatement("SELECT * FROM transaction_history");
            rs = pst.executeQuery();

            Document PDFreport = new Document();
            String filePath = "C:\\PDF\\TRANSACTION_" + System.currentTimeMillis() + ".pdf";
            PdfWriter.getInstance(PDFreport, new FileOutputStream(filePath));
           

            PDFreport.open();
         
    // Correct path (Change it according to your system)
   

   try {
            String imagePath = "C:\\images\\logo.png"; // Change to your logo's path
            com.itextpdf.text.Image logo = com.itextpdf.text.Image.getInstance(imagePath);
            logo.scaleToFit(140, 100); // Resize the logo
            logo.setAlignment(com.itextpdf.text.Image.ALIGN_LEFT);
            PDFreport.add(logo);
        } catch (Exception e) {
            System.out.println("Logo not found or failed to load.");
        }



            
  //  Title Styling
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Paragraph title = new Paragraph("TRANSACTIONS RECORD REPORT", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingBefore(20);
        title.setSpacingAfter(10); // Adds space between the title and the table

        // Add title to PDF
        PDFreport.add(title);
        PdfPTable PDFTable = new PdfPTable(7);
        PDFTable.setWidthPercentage(110); // Set table width
        PDFTable.setSpacingBefore(10); // Space before table

            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            PdfPCell table_cell;
            
            String[] headers = {"ID", "Inv_Number", "Date", "Email", "Total_Price"};
        for (String header : headers) {
            table_cell = new PdfPCell(new Phrase(header, headerFont));
            table_cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            PDFTable.addCell(table_cell);
        }
            

            while(rs.next()) {
                String pid = rs.getString("id");
                table_cell = new PdfPCell(new Phrase(pid));
                PDFTable.addCell(table_cell);

                String ppprod = rs.getString("Inv_Number");
                table_cell = new PdfPCell(new Phrase(ppprod));
                PDFTable.addCell(table_cell);
                
                String ppcontact = rs.getString("Date");
                table_cell = new PdfPCell(new Phrase(ppcontact));
                PDFTable.addCell(table_cell);

                String pppemail = rs.getString("Email");
                table_cell = new PdfPCell(new Phrase(pppemail));
                PDFTable.addCell(table_cell);
                
                String ppprice = rs.getString("Total_Price");
                table_cell = new PdfPCell(new Phrase(ppprice));
                PDFTable.addCell(table_cell);
               }               
                
                PDFreport.add(PDFTable);
                

                PDFreport.close();
                JOptionPane.showMessageDialog(this, "PDF File Exported Successfully! \nSaved as: " + filePath);
    }else {
                        JOptionPane.showMessageDialog(this, "Exporting cancelled.");

    }
               
     } catch (SQLException ex) {
            Logger.getLogger(StaffDashboard.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(StaffDashboard.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DocumentException ex) {
            Logger.getLogger(StaffDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        BTNid.setText("");
        BTNdate.setDate(null);
        PRObtn.setText("");
        BTNtotal.setText("");
        BTNemail.setText("");
    }//GEN-LAST:event_jButton1ActionPerformed

    private void PogiArcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PogiArcelActionPerformed
        // TODO add your handling code here:
        try {
            // TODO add your handling code here:
            String piid = comboo.getSelectedItem().toString();
            
            pst = con.prepareStatement("SELECT * FROM transaction_history WHERE id=?");
            pst.setString(1,piid);
            rs = pst.executeQuery();
            if(rs.next() == true){
                
                BTNtotal.setText(rs.getString(5));
                PRObtn.setText(rs.getString(2));
                BTNemail.setText(rs.getString(4));
                BTNdate.setDate(rs.getDate(3));
                
                
            }else {
                JOptionPane.showMessageDialog(this, "No record found.");
            }
        } catch (SQLException ex) {
            Logger.getLogger(StaffDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_PogiArcelActionPerformed

    private void combooActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combooActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_combooActionPerformed

    private void BTNupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BTNupdateActionPerformed
        // TODO add your handling code here:
      try {
    // Validate required fields
    if (PRObtn.getText().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Product Name is required!");
    } else if (BTNtotal.getText().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Total Cost is required!");
    } else if (BTNemail.getText().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Email is required!");
    } else if (BTNdate.getDate() == null) {
        JOptionPane.showMessageDialog(this, "Date is required!");
    } else {
        // Retrieve values from input fields
        String PNAME = PRObtn.getText();
        String PTOTAL = BTNtotal.getText();
        String PEMAIL = BTNemail.getText();
        SimpleDateFormat dateformat2 = new SimpleDateFormat("yyyy-MM-dd");
        String PDATE = dateformat2.format(BTNdate.getDate());  // Date
        String PID = comboo.getSelectedItem().toString();

        //  Debug: Print values before updating
        System.out.println("Updating ID: " + PID);
        System.out.println("Product Name: " + PNAME);
        System.out.println("Date: " + PDATE);
        System.out.println("Email: " + PEMAIL);
        System.out.println("Total Price: " + PTOTAL);

        //  Correct SQL statement
        pst = con.prepareStatement(
            "UPDATE transaction_history SET Inv_Number=?, Date=?, Email=?, Total_Price=? WHERE id=?"
        );

        pst.setString(1, PNAME);
        pst.setString(2, PDATE); 
        pst.setString(3, PEMAIL);
        pst.setString(4, PTOTAL);
        pst.setString(5, PID);

        int k = pst.executeUpdate();
        if (k == 1) {
            JOptionPane.showMessageDialog(this, "The selected customer is updated successfully!");
            
            // Clear input fields
            PRObtn.setText("");
            BTNemail.setText("");
            BTNtotal.setText("");
            BTNdate.setDate(null);
            PRObtn.requestFocus();

            // Reload transaction numbers & refresh table
            
            LoadTransactionNo();
            refreshTable2();
        }
    }
} catch (SQLException ex) {
    Logger.getLogger(StaffDashboard.class.getName()).log(Level.SEVERE, null, ex);
}


    }//GEN-LAST:event_BTNupdateActionPerformed

    private void txtvcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtvcodeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtvcodeActionPerformed

    private void txtvcodeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtvcodeKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            String vcode = txtvcode.getText();
            try {
                pst = con.prepareStatement("SELECT * FROM product_table WHERE id = ?");
                pst.setString(1, vcode);
                rs = pst.executeQuery();
                
                if(rs.next() == false)
                {
                  JOptionPane.showMessageDialog(this, "Vape id not found.");
                }
                else{
                     String vname = rs.getString("Product_name");
                     txtvname.setText(vname.trim());
                     
                     String price = rs.getString("Price");
                     txtprice.setText(price.trim());
                     
                     String cost = rs.getString("Cost");
                     txtcost.setText(cost.trim());
                     
                     txtquantity.requestFocus();
                }
            } catch (SQLException ex) {
                Logger.getLogger(StaffDashboard.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_txtvcodeKeyPressed

    private void btnprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnprintActionPerformed
      try {
    double pay = Double.parseDouble(txtpay.getText());
    double totalprice = Double.parseDouble(txttprice.getText());
    double balance = pay - totalprice;

    if (balance < 0) {
        JOptionPane.showMessageDialog(null, "Insufficient cash. Please enter enough amount.");
        return; // Do not proceed with sale or print
    }

    txtbalance.setText(String.valueOf(balance));

    // Proceed with sale and print
    sales();
    receiptPDF();
    clearFieldsAfterPrint();
} catch (NumberFormatException e) {
    JOptionPane.showMessageDialog(null, "Invalid input. Please enter numeric values only.");
}

    }//GEN-LAST:event_btnprintActionPerformed

    private void addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addActionPerformed
        // TODO add your handling code here:
    double price = Double.parseDouble(txtprice.getText());
double qty = Double.parseDouble(txtquantity.getValue().toString());
double total = price * qty;

// fetch the cost per item
double costPerItem = 0.0;
try {
    String query = "SELECT cost FROM product_table WHERE id = ?";
    PreparedStatement pst = con.prepareStatement(query);
    pst.setString(1, txtvcode.getText()); // Use vape_code or adjust as needed
    ResultSet rs = pst.executeQuery();

    if (rs.next()) {
        costPerItem = rs.getDouble("cost");
    }

    rs.close();
    pst.close();
} catch (SQLException e) {
    e.printStackTrace();
}

double totalCostForItem = costPerItem * qty; // calculate internal cost

// Add to table (without cost)
df = (DefaultTableModel) table4.getModel();
df.addRow(new Object[] {
    txtvname.getText(),
    txtprice.getText(),
    txtquantity.getValue().toString(),
    total
});

// Recalculate grand total price
double sum = 0;
for (int i = 0; i < table4.getRowCount(); i++) {
    sum += Double.parseDouble(table4.getValueAt(i, 3).toString());
}
txttprice.setText(String.valueOf(sum));

// 🧠 Accumulate totalCost internally
// (Optional: store in a class-level variable)
totalAccumulatedCost += totalCostForItem; // <-- Create this variable at the top of your class

// Reset fields
txtvcode.setText("");
txtvname.setText("");
txtprice.setText("");
txtquantity.setValue(1);
txtvcode.requestFocus();

    }//GEN-LAST:event_addActionPerformed

    private void txtpriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtpriceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtpriceActionPerformed

    private void CRecord1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CRecord1ActionPerformed
        // TODO add your handling code here:
        jTabbedPane2.setSelectedIndex(5);
    }//GEN-LAST:event_CRecord1ActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        showLowStockAlerts();
    }//GEN-LAST:event_formWindowOpened

    private void btnprint1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnprint1ActionPerformed
        // TODO add your handling code here:
         DefaultTableModel modelll = (DefaultTableModel) table4.getModel();
         modelll.setRowCount(0);
    }//GEN-LAST:event_btnprint1ActionPerformed

    private void txtpayKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtpayKeyPressed
        // TODO add your handling code here:
       if(evt.getKeyCode() == KeyEvent.VK_ENTER) {
    try {
        double pay = Double.parseDouble(txtpay.getText()); // the amount the customer paid
        double total = Double.parseDouble(txttprice.getText()); // the total cost (you must have this field)

        double balance = pay - total; // subtract total from pay
        txtbalance.setText(String.valueOf(balance)); // show the result in the balance field

        txtquantity.requestFocus(); // move focus to quantity field
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Please enter valid numeric values.");
    }
}

    }//GEN-LAST:event_txtpayKeyPressed

    private void txtpayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtpayActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtpayActionPerformed

    private void txtbalanceKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtbalanceKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtbalanceKeyPressed

    private void table4PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_table4PropertyChange
        // TODO add your handling code here:
        Header4();
    }//GEN-LAST:event_table4PropertyChange

    /**}
        });

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
            java.util.logging.Logger.getLogger(StaffDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(StaffDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(StaffDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(StaffDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoginForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BTNadd;
    private com.toedter.calendar.JDateChooser BTNdate;
    private javax.swing.JButton BTNdelete;
    private javax.swing.JTextField BTNemail;
    private javax.swing.JTextField BTNid;
    private javax.swing.JTextField BTNtotal;
    private javax.swing.JButton BTNupdate;
    private javax.swing.JComboBox<String> COMBO;
    private javax.swing.JButton CRecord1;
    private javax.swing.JButton Home;
    private javax.swing.JTextField LOCATE;
    private javax.swing.JButton PDetails;
    private javax.swing.JTextArea PRObtn;
    private javax.swing.JButton PogiArcel;
    private javax.swing.JButton SendEmail;
    private javax.swing.JTextField Stock;
    private javax.swing.JButton add;
    private javax.swing.JButton attachbtn;
    private javax.swing.JButton btnADD;
    private javax.swing.JButton btnCALCULATOR;
    private javax.swing.JButton btnDELETE;
    private javax.swing.JTextArea btnDESC;
    private javax.swing.JButton btnFirst;
    private javax.swing.JTextField btnID;
    private javax.swing.JButton btnIMAGE;
    private javax.swing.JLabel btnImg;
    private javax.swing.JButton btnLOGOUT;
    private javax.swing.JButton btnLast;
    private javax.swing.JTextField btnNAME;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnPDF;
    private javax.swing.JTextField btnPRICE;
    private javax.swing.JButton btnPrev;
    private javax.swing.JTextField btnSEARCH;
    private javax.swing.JButton btnTRANSACTION;
    private javax.swing.JButton btnUPDATE;
    private javax.swing.JButton btnprint;
    private javax.swing.JButton btnprint1;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> comboo;
    private javax.swing.JButton emailbtn;
    private javax.swing.JLabel first;
    private javax.swing.JTextField fromBtn;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
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
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTextArea path_attach;
    private javax.swing.JTable stafftable;
    private javax.swing.JTextField subjectBtn;
    private javax.swing.JTable table2;
    private javax.swing.JTable table3;
    private javax.swing.JTable table4;
    private javax.swing.JTextArea textBtn;
    private javax.swing.JTextField toBtn;
    private javax.swing.JTextField txtbalance;
    private javax.swing.JTextField txtcost;
    private javax.swing.JTextField txtpay;
    private javax.swing.JTextField txtprice;
    private javax.swing.JSpinner txtquantity;
    private javax.swing.JTextField txttprice;
    private javax.swing.JTextField txtvcode;
    private javax.swing.JTextField txtvname;
    // End of variables declaration//GEN-END:variables

    private File file;
    String date1, date2,possiblereaction, file_path, filename1;
   
}