/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdfguibuilder;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.json.JSONObject;

/**
 *
 * @author Nicholas Quirk
 */
public class MainUI {
    
    private static final Logger LOGGER = Logger.getLogger(MainUI.class.getName());
    private static final String APP_TITLE = "PDF Gui Builder";
    
    private final org.jpedal.PdfDecoderFX pdf = new org.jpedal.PdfDecoderFX();
    
    private MenuBar menuBar;
    
    private Menu fileMenu;
    private Menu componentMenu;
    
    private ScrollPane pdfPreviewPane;
    private ScrollPane pdfBuilderPane;
    
    private Group pdfPreviewGroupContainer;
    private Stage stage;
    private Pane pdfBuilderPaneContainer;
    
    private Pen pen;
    
    private static boolean IS_MOVABLE = false;
    final Text source = new Text(50, 100, "DRAG ME");
    final Text target = new Text(300, 100, "DROP HERE");
    
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
    
    public MainUI(Stage primaryStage) {
        
        this.stage = primaryStage;
        
        this.pen = new Pen();
        
        // Initialize menubar
        this.menuBar = new MenuBar();
        this.menuBar.prefWidthProperty().bind(stage.widthProperty());
        
        JSONObject json = new JSONObject();
        
        if(PdfGuiBuilder.DEBUG_BUILDER) LOGGER.info("Initializing menu options...");
        createFileMenu();
        createFileMenuChoices(primaryStage);
        
        createComponentMenu();
        createComponentMenuChoices(primaryStage);
        
        if(PdfGuiBuilder.DEBUG_BUILDER) LOGGER.info("Initializing HBox...");
        HBox hBoxMenu = new HBox();
        
        loadPanes();
        
        HBox hbox = new HBox();
        
        pdfPreviewPane = new ScrollPane();
        pdfBuilderPane = new ScrollPane();
        pdfBuilderPane.setMinWidth(PDRectangle.A4.getWidth());
        pdfBuilderPane.setMinHeight(PDRectangle.A4.getHeight());
        
        pdfBuilderPaneContainer = new Pane();
        
        //pdfBuilderPaneContainer.getChildren().add(source);
        //pdfBuilderPaneContainer.getChildren().add(target);
        
        /**
        source.setOnMousePressed((event) -> {
            if(PdfGuiBuilder.DEBUG_BUILDER) LOGGER.info("setOnMousePressed...");
            IS_MOVABLE = true;
        });
        
        source.setOnMouseReleased((event) -> {
            if(PdfGuiBuilder.DEBUG_BUILDER) LOGGER.info("setOnMouseReleased...");
            IS_MOVABLE = false;
        });
        
        source.setOnMouseDragged((event) -> {
            if(PdfGuiBuilder.DEBUG_BUILDER) LOGGER.info("setOnMouseMoved...");
            if(IS_MOVABLE) {
                source.setX(event.getX());
                source.setY(event.getY());
            }
        });
        **/
        
        pdfBuilderPane.setOnMouseClicked((event) -> {
            double x = event.getX();
            double y = event.getY();
            
            if(pen.getPenMode() == PenMode.STICKY
                    && pen.getDocumentComponent() == DocumentComponent.TEXTFIELD) {
                
                TextField textField = new TextField();
                pdfBuilderPaneContainer.getChildren().add(textField);
                textField.setLayoutX(x);
                textField.setLayoutY(y);
                
                pen.setPenMode(PenMode.CURSOR);
                pen.setDocumentComponent(DocumentComponent.NONE);
            }
            
        });
        
        pdfBuilderPane.setContent(pdfBuilderPaneContainer);
        
        pdfPreviewGroupContainer = new Group();
        pdfPreviewGroupContainer.getChildren().add(pdf);
        pdfPreviewPane.setContent(pdfPreviewGroupContainer);
        
        BorderPane root = new BorderPane();
        HBox centerContent = new HBox();
        
        HBox componentContainer = new HBox();
        componentContainer.getChildren().add(new Label("Components Go Here"));
        
        centerContent.getChildren().add(pdfPreviewPane);
        centerContent.getChildren().add(pdfBuilderPane);
        
        root.setCenter(centerContent);
        root.setBottom(componentContainer);
        
        if(PdfGuiBuilder.DEBUG_BUILDER) LOGGER.info("Adding top menu...");
        hBoxMenu.getChildren().add(menuBar);
        root.setTop(hBoxMenu);

        pdfBuilderPane.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
            loadPanes();
        });
        
        Scene scene = new Scene(root);
    
        // Set the window to the entire size of the screen.
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX(primaryScreenBounds.getMinX());
        primaryStage.setY(primaryScreenBounds.getMinY());
        primaryStage.setWidth(primaryScreenBounds.getWidth());
        primaryStage.setHeight(primaryScreenBounds.getHeight());
        
        primaryStage.setTitle(APP_TITLE);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        adjustPagePosition(pdfPreviewPane.getViewportBounds());
    }
    
    // Used for previews
    private void adjustPagePosition(final Bounds nb){
        // (new scrollbar width / 2) - (page width / 2)
        double adjustment = ((nb.getWidth() / 2) - (pdfPreviewGroupContainer.getBoundsInLocal().getWidth() /2));
        // Keep the group within the viewport of the scrollpane
        if(adjustment < 0) {
            adjustment = 0;
        }
        pdfPreviewGroupContainer.setTranslateX(adjustment);
    }
    
    // Simple function to create PDFs
    private static void createPdf() throws IOException {

        // Create a new document with an empty page.
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        
        // Adobe Acrobat uses Helvetica as a default font and 
        // stores that under the name '/Helv' in the resources dictionary
        PDFont font = PDType1Font.HELVETICA;
        PDResources resources = new PDResources();
        resources.put(COSName.getPDFName("Helv"), font);
        
        // Add a new AcroForm and add that to the document
        PDAcroForm acroForm = new PDAcroForm(document);
        document.getDocumentCatalog().setAcroForm(acroForm);
        
        // Add and set the resources and default appearance at the form level
        acroForm.setDefaultResources(resources);
        
        // Acrobat sets the font size on the form level to be
        // auto sized as default. This is done by setting the font size to '0'
        String defaultAppearanceString = "/Helv 0 Tf 0 g";
        acroForm.setDefaultAppearance(defaultAppearanceString);
        
        // Add a form field to the form.
        PDTextField textBox = new PDTextField(acroForm);
        textBox.setPartialName("SampleField");
        // Acrobat sets the font size to 12 as default
        // This is done by setting the font size to '12' on the
        // field level.
        defaultAppearanceString = "/Helv 12 Tf 0 g";
        textBox.setDefaultAppearance(defaultAppearanceString);
        
        // add the field to the acroform
        acroForm.getFields().add(textBox);
        
        // Specify the annotation associated with the field
        PDAnnotationWidget widget = textBox.getWidgets().get(0);
        PDRectangle rect = new PDRectangle(50, 750, 200, 50);
        widget.setRectangle(rect);
        widget.setPage(page);
        
        // Add the annotation to the page
        page.getAnnotations().add(widget);
        
        // set the field value
        textBox.setValue("Sample field");
        
        // Save the results and ensure that the document is properly closed:
        document.save("Hello World.pdf");
        document.close();
    }
    
    private void createFileMenu() {
        
        if(PdfGuiBuilder.DEBUG_BUILDER) LOGGER.info("Creating file menu...");
        fileMenu = new Menu("File");
        menuBar.getMenus().add(fileMenu);
    }
    
    private void createComponentMenu() {
        componentMenu = new Menu("Components");
        menuBar.getMenus().add(componentMenu);
    }
    
    private void createComponentMenuChoices(final Stage stage) {
        
        MenuItem textFieldMenuItem = new MenuItem();
        textFieldMenuItem.setText("Text Field");
        textFieldMenuItem.setOnAction((event) -> {
            pen.setPenMode(PenMode.STICKY);
            pen.setDocumentComponent(DocumentComponent.TEXTFIELD);
        });
        
        componentMenu.getItems().add(textFieldMenuItem);
    }
    
    private void createFileMenuChoices(final Stage stage) {
        
        if(PdfGuiBuilder.DEBUG_BUILDER) LOGGER.info("Creating file menu choices...");
        
        MenuItem menuItemExit = new MenuItem();
        menuItemExit.setText("Exit");
        menuItemExit.setOnAction((event) -> System.exit(0));
        
        fileMenu.getItems().add(menuItemExit);
    }
    
}
