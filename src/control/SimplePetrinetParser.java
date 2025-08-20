package control;

import java.io.File;

import model.*;
import propra.pnml.PNMLWopedParser;

/**
 * Die Klasse repräsentiert einen Parser für einfache PNML-Dateien im Rahmen
 * dieses Programms. 
 * 
 * Sie erweitert den {@link PNMLWopedParser}
 * 
 * @author Fabian Ehlers
 */
public class SimplePetrinetParser extends PNMLWopedParser {
    
    private PetrinetModel pNet;

    /**
     * Der Konstruktor erzeugt einen Parser für pnml-Dateien.
     * 
     * @param pnml Die Referenz auf ein File.
     * @param net  Die Referenz auf ein PetrinetModel.
     */
    protected SimplePetrinetParser(File pnml, PetrinetModel net) {
        super(pnml);
        this.pNet = net;
    }
    
    /**
     * Die Methode überschreibt die Methode der Superklasse um einem
     * PetrinetModel eine neue Arc hinzuzufügen.
     */
    @Override
    public void newArc(final String id, final String source, final String target) {
        if (id.isEmpty()) {
            System.err.println("Vorsicht! Eine id sollte mindestens aus einem Zeichen bestehen.");
        } else {
            pNet.addArc(id, source, target);
        }
    }
    
    /**
     * Die Methode überschreibt die Methode der Superklasse um einem
     * PetrinetModel einen neuen Place hinzuzufügen.
     */
    @Override
    public void newPlace(final String id) {
        if (id.isEmpty()) {
            System.err.println("Vorsicht! Eine id sollte mindestens aus einem Zeichen bestehen.");
        } else {
            pNet.addPlace(id);
        }
    }
    
    /**
     * Die Methode überschreibt die Methode der Superklasse einem
     * PetrinetModel eine neue Transition hinzuzufügen.
     */
    @Override
    public void newTransition(final String id) {
        if (id.isEmpty()) {
            System.err.println("Vorsicht! Eine id sollte mindestens aus einem Zeichen bestehen.");
        } else {
            pNet.addTransition(id);
        }
    }
    
    /**
     * Die Methode überschreibt die Methode der Superklasse um einem Place oder
     * einer Trasition eine Postition zuzuweisen.
     */
    @Override
    public void setPosition(String id, String x, String y) {
        pNet.setPosition(id, x, y);
    }

    /**
     * Die Methode überschreibt die Methode der Superklasse um einem Place oder
     * einer Transition einen Namen zuzuweisen.
     */
    @Override
    public void setName(String id, String name) {
        pNet.setName(id, name);
    }
    
    /**
     * Die Methode überschreibt die Methode der Superklasse um einem Place eine
     * Anzahl an Marken zuzuweisen.
     */
    @Override
    public void setTokens(String id, String token) {
        pNet.setTokens(id, token);
    }

    /**
     * Die Methode ruft Klassen des Super-Typs auf um eine Datei einzulesen und
     * veranlasst die Prüfung des PetrinetModels ob alle
     * Bedingungen im Rahmen dieses Programms erfüllt sind.
     * 
     * @return Eine Referenz auf das erzeugte PetrinetModel.
     */
    protected PetrinetModel parseFile() {
        this.initParser();
        this.parse();
        pNet.validatePetrinet();
        return pNet;
    }
}
