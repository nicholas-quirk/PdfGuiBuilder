/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdfguibuilder;

import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.STYLESHEET_MODENA;
import static javafx.application.Application.launch;
import javafx.stage.Stage;

/**
 *
 * @author Nicholas Quirk
 */
public class PdfGuiBuilder extends Application {
    
    public static final Boolean DEBUG_BUILDER = true;
    public static final Boolean DEBUG_BUILDER_VERBOSE = true;
    
    private final static Logger LOGGER = Logger.getLogger(PdfGuiBuilder.class.getName());
    private static Stage primaryStage;
    
    private MainUI ui;

    @Override
    public void start(Stage primaryStage) {
        if(DEBUG_BUILDER) LOGGER.info("Starting application...");
        PdfGuiBuilder.primaryStage = primaryStage;
        
        this.ui = new MainUI(primaryStage);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if(DEBUG_BUILDER) LOGGER.info("Launching main...");
        Application.setUserAgentStylesheet(STYLESHEET_MODENA);
        launch(args);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setPrimaryStage(Stage primaryStage) {
        PdfGuiBuilder.primaryStage = primaryStage;
    }
    
}
