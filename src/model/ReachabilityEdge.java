package model;

/**
 * Die Klasse repräsentiert eine Kante eines {@link ReachabilityGraphModel}.<br/>
 * 
 * Eine Kante stellt dar, zwischen welchen Markierungen eines Petrinetzes
 * durch das Schalten welcher Transition gewechselt wird.
 * 
 * @author Fabian Ehlers
 */
public class ReachabilityEdge {
    private String edgeId;
    private String edgeName;
    private String transitionName;
    private ReachabilityNode source;
    private ReachabilityNode target;
    
    /**
     * Der Konstruktor erzeugt eine neue Kante eines ReachabilityGraphModel.
     * 
     * @param name           Die id der Kante.
     * @param id             Der Name der Kante.
     * @param transitionName Der Name der Transition die den Schaltvorgang, den
     *                       diese Kante abbildet, ausgelöst hat.
     * @param source         Der Name des Quellknotens.
     * @param target         Der Name der Zielknotens.
     */
    public ReachabilityEdge(int name, String id, String transitionName, ReachabilityNode source, ReachabilityNode target) {
        this.edgeId = Integer.toString(name);
        this.edgeName = id;
        this.transitionName = transitionName;
        this.source = source;
        this.target = target;
    }
    
    /**
     * Die Methode gibt die id der Kante zurück.
     * 
     * @return Der gespeicherte Wrt für die id der Kante.
     */
    public String getEdgeId() {
        return edgeId;
    }
    
    /**
     * Die Methode gibt den Namen der Kante zurück.
     * 
     * @return Der gespeicherte Wert für den Namen der Kante.
     */
    public String getEdgeName() {
        return edgeName;
    }
    
    /**
     * Die Methode gibt den Namen der Transition, dessen Schaltvorgang sie abbildet,
     * zurück.
     * 
     * @return Der gespeicherte Wert für den Namen der Transition.
     */
    public String getTransitionName() {
        return transitionName;
    }
    
    /**
     * Die Methode gibt den Quellknoten dieser Kante zurück.
     * 
     * @return Der gespeicherte Wert für den Quellknoten der Kante.
     */
    public ReachabilityNode getSource() {
        return source;
    }
    
    /**
     * Die Methode gibt den Zielknoten dieser Kante zurück.
     * 
     * @return Der gespeicherte Wert für den Zielknoten der Kante.
     */
    public ReachabilityNode getTarget() {
        return target;
    }
}
