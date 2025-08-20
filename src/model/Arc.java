package model;

/**
 * Diese Klasse beschreibt eine Kante eines Petrinetzes. 
 * 
 * Alle Kanten werden in der Klasse {@link PetrinetModel} verwaltet.
 * 
 * @author Fabian Ehlers
 */
public class Arc {
    
    final private String id;
    final private String source;
    final private String target;
    
    /**
     * Der Konstruktor erzeugt eine Kante und belegt alle Attribute dieser.
     * 
     * @param id     Der Name der id dieser Kante.
     * @param source Der Name der Quelle dieser Kante.
     * @param target Der Name des Ziels dieser Kante.
     */
    protected Arc(String id, String source, String target) {
        this.id = id;
        this.source = source;
        this.target = target;
    }
    
    /**
     * Die Methode gibt die id der Kante zurück.
     * 
     * @return Die gespeicherte id.
     */
    public String getId() {
        return id;
    }
    
    /**
     * Die Methode gibt den Namen der Quelle dieser Kante zurück
     * 
     * @return Der gespeicherte Name der Quelle.
     */
    public String getSource() {
        return source;
    }
    
    /**
     * Die Methode gibt den Namen des Ziels dieser Kante zurück.
     * 
     * @return Der gespeicherte Name des Ziels. 
     */
    public String getTarget() {
        return target;
    }
}
