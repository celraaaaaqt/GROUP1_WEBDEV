import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;


    public class preview extends JPanel {
    private PDDocument document;

    public preview(PDDocument document) {
        this.document = document; // Accept PDDocument object
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (document != null) {
            Graphics2D g2d = (Graphics2D) g;
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            try {
                // Render the first page of the PDF (you can iterate over multiple pages if needed)
                pdfRenderer.renderPageToGraphics(0, g2d, getWidth(), getHeight());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        try {
            if (document != null) {
                document.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
