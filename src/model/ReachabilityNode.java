package model;

import java.util.Map;

/**
 * Die Klasse repräsentiert einen Knoten eines
 * {@link ReachabilityGraphModel}.<br/>
 * 
 * Ein Knoten des ReachabilityGraphModel stellt immer genau einen Zustand des
 * zugehörigen Petrinetzes dar.
 * 
 * @author Fabian Ehlers
 */
public class ReachabilityNode {
    private String id;
    
    /**
     * Das Attribut speichert die Markierung eines Petrinetzes.<br/>
     * 
     * Jeder Wert des Arrays repräsentiert die Anzahl von Marken einer Stelle.
     */
    private int[] marking;
    
    /**
     * Der Konstruktor erzeugt einen neuen Knoten eines ReachabilityGraphModel.<br/>
     * 
     * Die Markenzahl jeder Stelle des zugehörigen Petrinetzes wird gespeichert,
     * also seine Markierung.
     * 
     * @param name   Die id des Knotens.
     * @param places Alle Stellen eines Petrinetzes.
     */
    public ReachabilityNode(int name, Map<String, Place> places) {
        this.id = Integer.toString(name);
        this.marking = new int[places.size()];
        setMarking(places);
    }
    
    /**
     * Die Methode gibt die id dieses Knotens zurück.
     * 
     * @return Der gespeicherte Wert für die id.
     */
    public String getId() {
        return id;
    }
    
    /**
     * Die Methode gibt die Markierung dieses Knotens zurück.
     * 
     * @return Der gespeicherte Wert der Markierung.
     */
    public int[] getMarking() {
        return marking;
    }
    
    /*
     * Speichert für jede Stelle des Petrinetzes die aktuelle Anzahl der Token in
     * diesem Knoten.
     */
    private void setMarking(Map<String, Place> places) {
        int index = 0;
        for (String p : places.keySet()) {
            Place place = places.get(p);
            marking[index] = place.getCurrentToken();
            index++;
            ;
        }
    }
}
