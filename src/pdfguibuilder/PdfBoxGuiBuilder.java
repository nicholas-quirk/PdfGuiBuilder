/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdfguibuilder;

import java.io.File;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 *
 * @author Nicholas Quirk
 */
public class PdfBoxGuiBuilder extends Application {
    
    private final org.jpedal.PdfDecoderFX pdf = new org.jpedal.PdfDecoderFX();
    
    public void start(Stage primaryStage) {
        
        File file = new File("C:\\TEST_PDF.pdf");
        try {
            pdf.openPdfFile(file.getAbsolutePath());
            System.out.println(pdf.getPageCount());
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        HBox hbox = new HBox();
        
        ScrollPane previewPane = new ScrollPane();
        previewPane.setPrefWidth(Screen.getPrimary().getVisualBounds().getWidth()/2);
        previewPane.setPannable(true);
        previewPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        previewPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        Group group = new Group();
        group.getChildren().add(pdf);
        previewPane.setContent(group);
        
        ScrollPane editPane = new ScrollPane();
        editPane.setPrefWidth(Screen.getPrimary().getVisualBounds().getWidth()/2);
        
        hbox.getChildren().add(previewPane);
        hbox.getChildren().add(editPane);
        
        StackPane root = new StackPane();
        root.getChildren().add(hbox);

        Scene scene = new Scene(root, 300, 250);
    
        // Set the window to the entire size of the screen.
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX(primaryScreenBounds.getMinX());
        primaryStage.setY(primaryScreenBounds.getMinY());
        primaryStage.setWidth(primaryScreenBounds.getWidth());
        primaryStage.setHeight(primaryScreenBounds.getHeight());
        
        
        primaryStage.setTitle("PDF GUI BUILDER");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
