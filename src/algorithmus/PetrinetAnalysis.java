package algorithmus;

import java.util.*;

import model.*;

/**
 * Die Klasse enthält den Algorithmus zur Durchführung einer
 * Beschränktheitsanalyse für ein Petrinetz und speichert alle relevanten
 * Ergebnisse nach der Analyse.
 * 
 * @author Fabian Ehlers
 */
public class PetrinetAnalysis {
    
    private PetrinetModel pNet;
    private ReachabilityGraphModel rGraphModel;
    private boolean isUnbounded = false;
    private List<ReachabilityNode> reachNodes;
    private List<ReachabilityEdge> reachEdges;
    private ReachabilityNode unboundedNode;
    private ReachabilityNode decisionNode;

    /**
     * Der Konstruktor erzeugt ein Analyse-Objekt
     * 
     * @param net Referenz auf das PetrinetModel für das die Analyse
     *                    durchgeführt werden soll.
     * @param rGraphModel Referenz auf das ReachabilityGraphModel welches zu
     *                    dem PetrinetModel zugehörig ist.
     */
    public PetrinetAnalysis(PetrinetModel net, ReachabilityGraphModel rGraphModel) {
        this.pNet = net;
        this.rGraphModel = rGraphModel;
    }
    
    /**
     * Die Methode startet eine Beschränktheitsanalyse .
     */
    public void analyseBoundedness() {
        analyse(new ArrayList<ReachabilityNode>(), new ArrayList<ReachabilityEdge>(), rGraphModel.getNodes().getFirst());
    }
    
    /**
     * Die Methode gibt zurück ob das Ergebnis der Analyse unbeschränkt ist.
     * 
     * @return {@code true} wenn das Petrinetz unbeschränkt ist, {@code false} wenn
     *         das Petrinetz beschränkt ist.
     */
    public boolean isNetUnbounded() {
        return isUnbounded;
    }
    
    /**
     * Die Methode gibt das PetrinetModel in dem Zustand nach der Analyse zurück.
     * 
     * @return Die Referenz auf das gespeicherte PetrinetModel.
     */
    public PetrinetModel getPetrinet() {
        return pNet;
    }
    
    /**
     * Die Methode gibt das ReachabilityGraphModel in dem Zustand nach der Analyse zurück.
     * 
     * @return Die Referenz auf das gespeicherte ReachabilityGraphModel.
     */
    public ReachabilityGraphModel getReachModel() {
        return rGraphModel;
    }
    
    /**
     * Die Methode gibt eine Liste der ReachabilityNode-Objekte auf dem gefundenen
     * Pfad von der Wurzel zu dem Knoten mit der Markierung der das Petrinetz als
     * unbeschränkt markiert.
     * 
     * @return Eine Referenz auf die Liste oder {@code null} wenn das Petrinetz
     *         beschränkt ist.
     */
    public List<ReachabilityNode> getReachNodes() {
        return reachNodes;
    }
    
    /**
     * Die Methode gibt eine Liste der ReachabilityEdge-Objekte auf dem gefundenen
     * Pfad von der Wurzel zu dem Knoten mit der Markierung der das Petrinetz als
     * unbeschränkt markiert.
     * 
     * @return Eine Referenz auf die Liste oder {@code null} wenn das Petrinetz
     *         beschränkt ist.
     */
    public List<ReachabilityEdge> getReachEdges() {
        return reachEdges;
    }
    
    /**
     * Die Methode gibt den ReachabilityNode zurück der das Ergebnis der das
     * Petrinetz als unbeschränkt markiert.
     * 
     * @return Eine Referenz auf den ReachabilityNode oder {@code null} wenn das
     *         Ergebnis beschränkt ist.
     */
    public ReachabilityNode getUnboundedNode() {
        return unboundedNode;
    }
    
    /**
     * Die Methode gibt den ReachabilityNode zurück anhand dessen Markierung der
     * Vergleich mit dem ReachabilityNode der das Ergebnis als unbeschränkt markiert
     * stattfindet.
     * 
     * @return Eine Referenz auf den Reachabilitynode oder {@code null} wenn das
     *         Ergebnis beschränkt ist.
     */
    public ReachabilityNode getDecisionNode() {
        return decisionNode;
    }
    
    /**
     * Die Methode enthält den DFS-Algorithmus um die Beschränktheitsanalyse
     * durchzuführen.<br/>
     * Der Algorithmus läuft rekursiv.
     * <p>
     * Jede Iteration des Algorithmus enthält die bis hierhin erreichten
     * Markierungen als Knoten und die, um diese Markierungen zu erreichen,
     * geschalteten Transitionen als Kanten. Es werden für jede Iteration die, für
     * diese Markierung, aktivierten Transitionen gespeichert und der Reihe nach
     * geschaltet. Nach einem Schaltvorgang wird entschieden ob Elemente hinzukommen
     * und geprüft ob das Petrinetz mit der neuen Markierung als unbeschränkt
     * identifiziert werden kann. Wird es so identifiziert, wird der erreichte Pfad
     * gespeichert und diese und alle vorherigien Iterationen, und damit der
     * Algorithmus beendet. Ist das Petrinetz bis hier beschränkt, wird die im
     * aktuellen Zustand mögliche nächte Transition geschaltet oder zu der
     * vorherigen Iteration zurückgekehrt. Wenn alle möglichen Markierungen erreicht
     * wurden und das Petrinetz unbeschränkt ist, wurde der vollständige
     * Erreichabrkeitsgraph erzeugt und der Algorithmus wird beendet.
     * </p>
     * 
     * @param pathNodes    Liste der ReachabilityNode-Objekte die das
     *                     ReachabilityGraphModel enthält.
     * @param pathEdges    Die Liste ReachabilityEdge-Objekte die das
     *                     ReachabilityGraphModel enthält.
     * @param currentState Der ReachabilityNode der den aktuellen Zustand des
     *                     PetrinetModel repräsentiert.
     */
    private void analyse(List<ReachabilityNode> pathNodes, List<ReachabilityEdge> pathEdges, ReachabilityNode currentState) {
        List<Transition> enabledTransitions =  initializeEnabledTransitions();
        
        if (enabledTransitions.size() > 0) {
            List<ReachabilityNode> nodes = new ArrayList<>();
            List<ReachabilityEdge> edges = new ArrayList<>();
            
            for (ReachabilityNode node : pathNodes) {
                nodes.add(node);
            }
            nodes.add(currentState);
            
            for (ReachabilityEdge edge : pathEdges) {
                edges.add(edge);
            }
            if (rGraphModel.getEdges().size() > 0) {
                edges.add(rGraphModel.getEdges().getLast());
            }
            for (Transition transition : enabledTransitions) {
                pNet.shiftTransition(transition.getId());
                ReachabilityNode nextState = new ReachabilityNode(rGraphModel.getNodes().size(), pNet.getPlaces());
                
                if (rGraphModel.findNode(nextState) == null) {
                    rGraphModel.addNode(nextState);
                    ReachabilityEdge pathEdge = new ReachabilityEdge(rGraphModel.getEdges().size(), transition.getId(),
                            transition.getName(), currentState, nextState);
                    rGraphModel.addEdge(pathEdge);
                    
                    if (isNodeMarkNetAsUnbounded(nodes, nextState)) {
                        isUnbounded = true;
                        nodes.add(nextState);
                        reachNodes = nodes;
                        edges.add(pathEdge);
                        reachEdges = edges;
                        return;
                    } else {
                        analyse(nodes, edges, nextState);
                        
                        if (isUnbounded) {
                            return;
                        }
                    }
                } else {
                    ReachabilityNode targetState = rGraphModel.findNode(nextState);
                    ReachabilityEdge edge = new ReachabilityEdge(rGraphModel.getEdges().size(), transition.getId(),
                            transition.getName(), currentState, targetState);
                    rGraphModel.addEdge(edge);
                }
                
                /*
                 * Wenn von einer höheren Iteration zurückgekehrt wird ohne dass das Petrinetz
                 * als unbeschränkt markiert wurde oder die Markierung schon in einem Knoten
                 * vorhanden war, wird das Petrinetz Modell auf den Zustand dieser Iteration
                 * gesetzt um die nächste Transition aus der Liste der aktivierten Transitionen
                 * schalten zu können.
                 * 
                 * Damit wird bei unbeschränkten Netzen der Zustand nach der Analyse auf die
                 * Anfangsmarkierung gesetzt.
                 */
                pNet.jumpToMarking(currentState.getMarking());
            }
        }
    }
    
    /*
     * Die Methode gibt eine Liste der aktivierten und damit schaltbaren Transitionen zurück.
     */
    private List<Transition> initializeEnabledTransitions() {
        List<Transition> transitionsEnabledList = new ArrayList<>(); 
        for (String tId : pNet.getTransitions().keySet()) {
            if (pNet.isTransitionEnabled(tId))
                transitionsEnabledList.add(pNet.getTransitions().get(tId));
        }
        return transitionsEnabledList;
    }
    
    /*
     * Die Methode prüft ob die Markierung eines Knoten das Petrinetz anhand der
     * bisher auf dem durchlaufenen Pfad liegenden Knoten als unbeschränkt markiert.
     * Für return true ist das Petrinetz unbeschränkt, 
     * für false ist es bis zu disem Knoten beschränkt.
     */
    private boolean isNodeMarkNetAsUnbounded(List<ReachabilityNode> path, ReachabilityNode currentState) {
        boolean unbounded = false;
        
        for (ReachabilityNode decisionState : path) {
            for (int pos = 0; pos < pNet.getPlaces().size(); pos++) {
                if (currentState.getMarking()[pos] < decisionState.getMarking()[pos]) {
                    unbounded = false;
                    break;
                } else {
                    if (currentState.getMarking()[pos] > decisionState.getMarking()[pos]) {
                        unbounded = true;
                    }
                }
            }
            if (unbounded) {
                unboundedNode = currentState;
                decisionNode = decisionState;
                return true;
            }
        }
        return false;
    }
}
