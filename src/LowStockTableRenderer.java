import java.awt.Color;
import java.awt.Component;
import javax.swing.*;
import javax.swing.table.*;

public class LowStockTableRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Get the value of the "Stock" column (assuming column index is 5)
        String stockString = table.getValueAt(row, 5).toString();  // Ensure it's converted to String
        int stock;

        try {
            stock = Integer.parseInt(stockString);
            if (stock < 0) {
                stock = 0; // Prevent negative stock
                table.setValueAt("0", row, 5); // Optionally update the value shown in table
            }
        } catch (NumberFormatException e) {
            stock = 0; // Treat invalid data as 0
        }

        // Highlight low stock
        if (stock < 10) {
            cellComponent.setBackground(Color.RED);
            cellComponent.setForeground(Color.WHITE);
        } else {
            cellComponent.setBackground(Color.WHITE);
            cellComponent.setForeground(Color.BLACK);
        }

        return cellComponent;
    }
}
