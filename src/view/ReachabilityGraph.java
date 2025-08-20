package view;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

import model.*;

/**
 * Die Klasse implementiert einen Erreichabrkeitsgraphen mittels GraphStream.
 * Die Klasse erbt von MultiGraph um auch mehrere Kanten zwischen zwei Knoten zu
 * ermöglichen.
 * <p>
 * Das Design des Graphen wird durch eine CSS-Datei festgelegt.
 * </p>
 * Die Klasse repräsentiert lediglich die Visualisierung des Graphen auf Basis
 * des Datenmodells, also der Klasse {@link ReachabilityGraphModel}. Alle Operationen,
 * welche die Datenstruktur verändern, erfolgen auf dem Modell. Ausgenommen
 * hiervon sind die Hervorhebungen von Knoten in dem Graphen, da diese keine
 * Zustandsänderung des Modells erfordern.
 * 
 * @author Fabian Ehlers
 */
public class ReachabilityGraph extends MultiGraph {
    
    private static String CSS_FILE = "url(" + PetrinetGraph.class.getResource("/reachabilityGraph.css") + ")";
    
    private Sprite spriteNode;
    private Sprite spriteDNode;
    private Sprite spriteUNode;
    private SpriteManager spriteMan;
    
    /**
     * Der Konstruktor erzeugt einen Graphen und seinen Wurzelknoten.
     *  
     * @param node Der Wurzelknoten des Graphen.
     */
    public ReachabilityGraph(ReachabilityNode node) {
        super("reachGraph");
    
        this.setAttribute("ui.stylesheet", CSS_FILE);
        
        spriteMan = new SpriteManager(this);
        
        addNode(node);
        highlightNode(node.getId());
    }
    
    /**
     * Die Methode fügt einen Knoten mit einem Label ein. Wenn der erste Knoten
     * eingefügt wurde, wird dieser als Wurzelknoten hervorgehoben.
     * 
     * @param newNode Eine Referenz auf einen ReachabilityNode.
     */
    public void addNode(ReachabilityNode newNode) {
        Node node = this.addNode(newNode.getId());
        String label = "(";
        for (int i = 0; i < newNode.getMarking().length; i++) {
            label = label.concat(Integer.toString(newNode.getMarking()[i]));
            if (i < newNode.getMarking().length - 1)
                label = label.concat("|");
        }
        label = label.concat(")");
        node.setAttribute("ui.label", label);
        if (getNodeCount() == 1)
            node.setAttribute("ui.class", "root");
        //TODO code löschen wenn funktion gegeben.
//        return node;
    }

    /**
     * Die Methode fügt eine Kante mit einem Label ein.
     * 
     * @param newEdge Eine Referenz auf eine ReachabilityEdge.
     */
    public void addEdge(ReachabilityEdge newEdge) {
            Edge edge = this.addEdge(newEdge.getEdgeId(), newEdge.getSource().getId(), newEdge.getTarget().getId(), true);
            Sprite s = spriteMan.addSprite(edge.getId());
            s.attachToEdge(edge.getId());
            s.setPosition(0.4);
            s.setAttribute("ui.label", "[" + newEdge.getEdgeName() + "] " + newEdge.getTransitionName());
            s.setAttribute("ui.class", "edgeLabel");
    }

    /**
     * Die Methode hebt einen Knoten hervor.
     * 
     * @param nodeId Die id des Knotens. 
     */
    public void highlightNode(String nodeId) {
        Node node = this.getNode(nodeId);
        if (spriteNode == null) {
            spriteNode = spriteMan.addSprite("sNode");
            spriteNode.setAttribute("ui.class", "highlightNode");
        }        
        spriteNode.attachToNode(node.getId());
    }

    /**
     * Die Methode schaltet die hervorgehobene Kante um, wenn die Id der aktuell
     * hervorgehobenen Kante eine andere ist als die id der übergebenen
     * ReachabilityEdge.
     * 
     * Wird der Methode {@code null} übergeben, wird nur die aktuelle Hervorhebung
     * entfernt ohne dass eine andere Kante hervorgehoben wird.
     * 
     * @param recentEdge Eine Referenz auf eine ReachabilityEdge oder {@code null}.
     */
    public void toggleEdgeHighlight(ReachabilityEdge recentEdge) {
        int count = 0;
        while (count < this.getEdgeCount()) {
            String attr = (String) this.getEdge(count).getAttribute("ui.class");
            if (attr != null && attr.equals("highlight")) {
                if ( (recentEdge != null) && this.getEdge(count).getId() == recentEdge.getEdgeId()) {
                    return;
                } else {
                    this.getEdge(count).removeAttribute("ui.class");
                    break;
                }
            }
            ++count;
        }
        if (recentEdge != null) {
            Edge edge = this.getEdge(recentEdge.getEdgeId());
            edge.setAttribute("ui.class", "highlight");
        }
    }

    /**
     * Die Methode vervollständigt einen Graphen, der als einziges Element den
     * Wurzelnoten enthält, anhand des ReachabilityGraphModel.
     * <p>
     * Achtung<br/>
     * Es findet keine Prüfung statt ob andere Elemente bereits vorhanden sind.
     * </p>
     * @param rGraphModel Eine Referenz auf das ReachabilityGraphModel.
     */
    public void paintReachGraph(ReachabilityGraphModel rGraphModel) {
        for (int i = 1; i < rGraphModel.getNodes().size(); i++) {
            addNode(rGraphModel.getNodes().get(i));
        }
        for (ReachabilityEdge edge : rGraphModel.getEdges()) {
            addEdge(edge);
        }
    }

    /**
     * Die Methode hebt eine Kante hervor die zum Pfad von der Wurzel bis zum Knoten
     * der das Petrinetz als unbeschränkt markiert gehört.
     * 
     * @param pathEdge Eine Referenz auf eine ReachabilityEdge.
     */
    public void highlightPathEdge(ReachabilityEdge pathEdge) {
        Edge edge = this.getEdge(pathEdge.getEdgeId());
        edge.setAttribute("ui.class", "path");
    }

    /**
     * Die Methode hebt die beiden Knoten, welche die Markierung enthalten die das
     * Petrinetz als unbeschränkt identifizieren, hervor.
     * 
     * @param decisionNode  Eine Referenz auf einen ReachabilityNode.
     * @param unboundedNode Eine Referenz auf einen ReachabilityNode
     */
    public void highlightPathNodes(ReachabilityNode decisionNode, ReachabilityNode unboundedNode) {
        Node dNode = this.getNode(decisionNode.getId());
            spriteDNode = spriteMan.addSprite("sDecisionNode");
            spriteDNode.setAttribute("ui.class", "highlightPath");
        spriteDNode.attachToNode(dNode.getId());
        
        Node uNode = this.getNode(unboundedNode.getId());
            spriteUNode = spriteMan.addSprite("sUnboundedNode");
            spriteUNode.setAttribute("ui.class", "highlightPath");
        spriteUNode.attachToNode(uNode.getId());
    }
}
