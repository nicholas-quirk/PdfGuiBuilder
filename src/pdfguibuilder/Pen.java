/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdfguibuilder;

/**
 *
 * @author Nicholas Quirk
 */
public class Pen {

    private PenMode penMode;
    private DocumentComponent documentComponent;

    public Pen() {
        penMode = PenMode.CURSOR;
        documentComponent = DocumentComponent.NONE;
    }

    public PenMode getPenMode() {
        return penMode;
    }

    public void setPenMode(PenMode penMode) {
        this.penMode = penMode;
    }

    public DocumentComponent getDocumentComponent() {
        return documentComponent;
    }

    public void setDocumentComponent(DocumentComponent documentComponent) {
        this.documentComponent = documentComponent;
    }

}
