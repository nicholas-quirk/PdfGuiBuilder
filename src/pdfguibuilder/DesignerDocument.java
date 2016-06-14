/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdfguibuilder;

import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

/**
 *
 * @author Nicholas Quirk
 */
public class DesignerDocument {
    
    private PDDocument document;
    private PDPage page;
    private String filePath;
    
    public DesignerDocument() {
        // Create a new document with an empty page.
        this.document = new PDDocument();
        this.page = new PDPage(PDRectangle.A4);
        document.addPage(page);
    }
    
    public void buildDocument() throws IOException {
        // Save the results and ensure that the document is properly closed:
        document.save(filePath);
        document.close();
    }
    
}
