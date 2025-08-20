package model;

import java.util.*;

/**
 * Die Klasse repräsentiert das Datenmodell für einen Erreichabrkeitsgraphen.<br/>
 * Dieses bildet den Zustand eines zugehörigen Petrinetzes ab
 * und es existiert immer mindestens der Wurzelknoten, der die Anfangsmarkierung
 * des zugehörigen Petrinetzes repräsentiert.
 * 
 * @author Fabian Ehlers
 */
public class ReachabilityGraphModel {
    
    private List<ReachabilityNode> nodes = new LinkedList<>();
    private List<ReachabilityEdge> edges = new LinkedList<>();
    
    /**
     * Der Konstruktor erzeugt ein Modell eines Erreichbarkeitsgraphen mit
     * Containern für ReachabilityNode- und ReachabilityEdge-Objekte und den
     * Wurzelknoten.
     * 
     * @param places Alle Stellen des zugehörigen Petrinetzes.
     */
    public ReachabilityGraphModel(Map<String, Place> places) {
        addNode(new ReachabilityNode(nodes.size(), places));
    }
    
    /**
     * Die Methode fügt dem Modell einen Knoten hinzu.
     * 
     * @param newNode Der Knoten der zum Modell hinzugefügt wird.
     */
    public void addNode(ReachabilityNode newNode) {
        nodes.add(newNode);
    }
    
    /**
     * Die Methode fügt dem Modell eine Kante hinzu.
     * 
     * @param newEdge Die Kante die dem Modell hinzugefügt wird.
     */
    public void addEdge(ReachabilityEdge newEdge) {
        edges.add(newEdge);
    }

    /**
     * Die Methode löscht alle Knoten und Kanten des Modells.
     * und fügt ihm einen neuen Wurzelknoten hinzu.
     * 
     * @param places Alle Stellen des zugehörigen Petrinetzes.
     */
    public void reset(Map<String, Place> places) {
        edges.clear();
        nodes.clear();
        addNode(new ReachabilityNode(nodes.size(), places));
    }
    
    /**
     * Die Methode gibt die Liste der Knoten des Modells zurück.
     * 
     * @return Die gespeicherte Liste aller Knoten des Modells.
     */
    public LinkedList<ReachabilityNode> getNodes() {
        return  (LinkedList<ReachabilityNode>) this.nodes;
    }
    
    /**
     * Die Methode gibt die Liste der Kanten des Modells zurück.
     * 
     * @return Die gespeicherte Liste alle Kanten des Modells.
     */
    public LinkedList<ReachabilityEdge> getEdges() {
        return (LinkedList<ReachabilityEdge>) this.edges;
    }

    /**
     * Die Methode prüft ob ein bestimmter Knoten im Modell vorhanden ist.
     * Die Existenz wird anhand der Markierung der Knoten gerüft.
     * 
     * @param node Der Knoten anhand dessen Markierung geprüft wird ob der Knoten schon existiert.
     * @return Der gefundene Knoten oder {@code null} wenn der Knoten nicht vorhanden ist. 
     */
    public ReachabilityNode findNode(ReachabilityNode node) {
            int index = 0;
            
            while (index < nodes.size()) {
                if (Arrays.equals(nodes.get(index).getMarking(), node.getMarking())) {
                    break;
                } else {
                    ++index;
                }
            }
            return ((index < nodes.size()) ? nodes.get(index) : null);
    }

    /**
     * Die Methode prüft ob eine bestimmte Kante im Modell vorhanden ist.
     * Die Existenz wird anhand des Namens der Kante
     * und den Markierungen von Quellknoten und Zielknoten geprüft.
     * 
     * @param transitionID Der Name der Transition die geschaltet wurde um diese Kante zu erzeugen.
     * @param source Der Quellknoten.
     * @param target Der Zielknoten.
     * @return Die gefundene Kante oder {@code null} wenn die Kante nicht vorhanden ist.
     */
    public ReachabilityEdge findEdge(String transitionID, ReachabilityNode source, ReachabilityNode target) {
        int index = 0;

        while (index < edges.size()) {
            if (edges.get(index).getEdgeName().equals(transitionID)
                    && Arrays.equals(edges.get(index).getTarget().getMarking(), target.getMarking())
                    && Arrays.equals(edges.get(index).getSource().getMarking(), source.getMarking())) {
                break;
            } else {
                ++index;
            }
        }
        return ((index < edges.size()) ? edges.get(index) : null);
    }
}
