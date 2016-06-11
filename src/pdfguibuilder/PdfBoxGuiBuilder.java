/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdfguibuilder;

import java.io.File;
import java.io.IOException;
import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 *
 * @author Nicholas Quirk
 */
public class PdfBoxGuiBuilder extends Application {
    
    private final org.jpedal.PdfDecoderFX pdf = new org.jpedal.PdfDecoderFX();
    
    private ScrollPane pdfPreviewPane;
    private ScrollPane pdfBuilderPane;
    private Group pdfPreviewGroupContainer;
    
    private static String pdfDocument = "Hello World, ";
    
    private void loadPanes() {
        try {
          createPdf();
        } catch (Exception e) {
            e.printStackTrace();
        }
        File file = new File("Hello World.pdf");
        
        try {
            pdf.openPdfFile(file.getAbsolutePath());
            pdf.setPageParameters(1.0f, 1);
            pdf.decodePage(1);
            pdf.waitForDecodingToFinish();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void start(Stage primaryStage) {
        
        loadPanes();
        
        HBox hbox = new HBox();
        
        pdfPreviewPane = new ScrollPane();
        pdfBuilderPane = new ScrollPane();
        
        pdfPreviewGroupContainer = new Group();
        pdfPreviewGroupContainer.getChildren().add(pdf);
        pdfPreviewPane.setContent(pdfPreviewGroupContainer);
        
        BorderPane root = new BorderPane();
        HBox centerContent = new HBox();
        
        HBox compontentContainer = new HBox();
        compontentContainer.getChildren().add(new Label("Label"));
        
        centerContent.getChildren().add(pdfPreviewPane);
        centerContent.getChildren().add(pdfBuilderPane);
        
        root.setCenter(centerContent);
        root.setBottom(compontentContainer);

        pdfBuilderPane.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
            pdfDocument = pdfDocument + pdfDocument;
            loadPanes();
        });
        
        Scene scene = new Scene(root);
    
        // Set the window to the entire size of the screen.
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX(primaryScreenBounds.getMinX());
        primaryStage.setY(primaryScreenBounds.getMinY());
        primaryStage.setWidth(primaryScreenBounds.getWidth());
        primaryStage.setHeight(primaryScreenBounds.getHeight());
        
        primaryStage.setTitle("PDF GUI BUILDER");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        adjustPagePosition(pdfPreviewPane.getViewportBounds());
    }
    
    private void adjustPagePosition(final Bounds nb){
        // (new scrollbar width / 2) - (page width / 2)
        double adjustment = ((nb.getWidth() / 2) - (pdfPreviewGroupContainer.getBoundsInLocal().getWidth() /2));
        // Keep the group within the viewport of the scrollpane
        if(adjustment < 0) {
            adjustment = 0;
        }
        pdfPreviewGroupContainer.setTranslateX(adjustment);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    private static void createPdf() throws IOException {
        // Create a document and add a page to it
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage( page );

        // Create a new font object selecting one of the PDF base fonts
        PDFont font = PDType1Font.HELVETICA_BOLD;

        // Start a new content stream which will "hold" the to be created content
        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        // Define a text content stream using the selected font, moving the cursor and drawing the text "Hello World"
        contentStream.beginText();
        contentStream.setFont( font, 12 );
        contentStream.moveTextPositionByAmount( 100, 700 );
        contentStream.drawString(pdfDocument);
        contentStream.endText();

        // Make sure that the content stream is closed:
        contentStream.close();

        // Save the results and ensure that the document is properly closed:
        document.save( "Hello World.pdf");
        document.close();
    }
    
}
