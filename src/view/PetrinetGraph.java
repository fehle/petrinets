package view;

import java.util.Map;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

import model.*;

/**
 * Die Klasse implementiert einen Petrinetz-Graphen mittels GraphStream. Die
 * Klasse erbt von MultiGraph um auch mehrere Kanten
 * zwischen zwei Knoten zu ermöglichen. 
 * <p>
 * Das Design des Graphen wird durch eine CSS-Datei festgelegt.
 * </p>
 * Die Klasse repräsentiert lediglich die Visualisierung des Graphen auf Basis des Datenmodells,
 * also der Klasse {@link PetrinetModel}. Alle Operationen, welche die Datenstruktur verändern, erfolgen
 * auf dem Modell. Ausgenommen hiervon sind die Hervorhebungen von Knoten in dem Graphen, da diese
 * keine Zustandsänderung des Modells erfordern.
 * 
 * @author Fabian Ehlers
 */
public class PetrinetGraph extends MultiGraph {
    
    private static String CSS_FILE = "url(" + PetrinetGraph.class.getResource("/petrinetGraph.css") + ")";
    
    private SpriteManager spriteMan;
    private Sprite spriteNode;
    
    /**
     * Der Konstruktor erzeugt einen neuen Graphen, der ein Petrinetz repräsentiert
     * auf Basis der übergebenen PetrinetModel Instanz.
     * 
     * @param net           Eine Referenz auf ein PetrinetModel.
     * @param lastClickedId Die id eines Knotens des Petrinetzes.
     */
    public PetrinetGraph(PetrinetModel net, String lastClickedId) {
        super("petrinetGraph");
        this.setAttribute("ui.stylesheet", CSS_FILE);
        
        spriteMan = new SpriteManager(this);
        
        for (String p : net.getPlaces().keySet()) {
            Place place = net.getPlaces().get(p);
            String pID = place.getId();
            String name = place.getName(); 
            int token = place.getInitialToken();
            
            Node pNode = this.addNode(pID);
            setPlaceNameAttribute(pID, name, token, pNode);
            pNode.setAttribute("xy", place.getPosX(), place.getPosY());
            pNode.setAttribute("ui.class", "place");
            
            if (token > 0) {
                Sprite spriteToken = spriteMan.addSprite(p);
                spriteToken.attachToNode(pID);
                setTokenAttribute(token, spriteToken);
            }
        }
        
        for (String t : net.getTransitions().keySet()) {
            Transition transition = net.getTransitions().get(t);
            String tID = transition.getId();
            String tName = transition.getName();
           
            Node tNode = this.addNode(tID);
            if (tName != null)
                tNode.setAttribute("ui.label", "[" + tID + "] " + tName);
            else
                tNode.setAttribute("ui.label", "[" + transition.getId() + "] ");
            tNode.setAttribute("xy", transition.getPosX(), transition.getPosY());
            
            if (net.isTransitionEnabled(tID))
                tNode.setAttribute("ui.class", "transitionEnabled");
            else 
                tNode.setAttribute("ui.class", "transitionNotEnabled");
        }
        
        for (String a : net.getArcs().keySet()) {
            Arc arc = net.getArcs().get(a);
            
            Edge edge = this.addEdge(arc.getId(), arc.getSource(), arc.getTarget(), true);
            Sprite s = spriteMan.addSprite(a);
            s.attachToEdge(edge.getId());
            s.setPosition(0.5);
            s.setAttribute("ui.label", "[" + arc.getId() + "]");
            s.setAttribute("ui.class", "edgeLabel");
        }
        
        if (lastClickedId != null) {
            markLastClickedNode(lastClickedId);
        }
    }
    
    /**
     * Die Methode prüft ob ein bestimmter Knoten eines Graphen hervorgehoben ist.
     * 
     * @param placeId Die id des zu prüfenden Knotens.
     * @return {@code true} wenn der Knoten hervorgehoben ist, sonst {@code false}.
     */
    public boolean isPlaceMarked(String placeId) {
        if (spriteNode != null) 
            return spriteNode.getAttachment().getId().equals(placeId);
        else
            return false;
    }
    
    /**
     * Markiert den zuletzt angeklickten Knoten mit einem doppelten Rahmen.
     * <p>
     * Wenn der geclickte Knoten ein Place ist, wird geprüft ob dieser Place bereits hervorgehoben ist. Ist dies der Fall,
     * wird die Hervorhebung entfernt. Andernfalls wird der Knoten hervorgehoben unabhäbgig davn ob es ein Place oder eine
     * Transition ist. 
     * </p>
     * @param nodeId Die id des geclickten Knotens.
     */
    public void markLastClickedNode(String nodeId) {
        if (spriteNode == null) {
            spriteNode = spriteMan.addSprite("sNode");
        }
        if (this.getNode(nodeId).getAttribute("ui.class").equals("place")) {
            if (spriteNode.getAttachment() == null) {
                spriteNode.setAttribute("ui.class", "place");
                spriteNode.attachToNode(nodeId);
            } else {
                
                if (spriteNode.getAttachment().equals(this.getNode(nodeId))) {
                    spriteMan.removeSprite("sNode");
                    spriteNode = null;
                }
                else {
                    spriteNode.setAttribute("ui.class", "place");
                    spriteNode.attachToNode(nodeId);
                }
            }
        } else {
            spriteNode.setAttribute("ui.class", "transition");
            spriteNode.attachToNode(nodeId);
        }
    }

    /**
     * Die Methode gibt den im Graphen hervorgehobenen Knoten zurück.
     * 
     * @return Die id des Knotens oder {@code null} wenn kein Knoten hervorgehoben
     *         ist.
     */
    public String getClickedNode() {
        if (spriteNode== null || spriteNode.getAttachment() == null) {
            return null;
        } else { 
            if (spriteNode.getAttribute("ui.class").equals("place")) {
                return spriteNode.getAttachment().getId();
            } else { 
                return null;
            }
        }
    }
    
    /**
     * Die Methode entfernt, falls vorhanden, die Hervorhebung eines Knotens.
     */
    public void deleteSpriteNode() {
        if (spriteNode != null) {
            spriteMan.removeSprite("sNode");
            spriteNode = null;
        }
    }

    /**
     * Die Methode aktualisiert einen Graph auf den aktuellen Zustand des übergebenen PetrinetModel.
     * 
     * @param net Eine Referenz auf ein PetrinetModel.
     */
    public void updateGraph(PetrinetModel net) {
        updatePlaces(net.getPlaces());
        updateTransitions(net);
    }
    
    /*
     * Die Methode setzt ein Label auf den ihr übergebenen Knoten eines Graphen.
     */
    private void setPlaceNameAttribute(String id, String name, int token, Node pNode) {
        if (name != null) {
            pNode.setAttribute("ui.label", "[" + id + "] " + name + " <" + token + ">");
        } else {
            pNode.setAttribute("ui.label", "[" + id + "] " + " <" + token + ">");
        }
    }

    /*
     * Die Methode setzt ein Label, welches die Anzahl der Token eines Place-Objekts
     * darstellt, auf einen Sprite.
     */
    private void setTokenAttribute(int token, Sprite spriteToken) {
        if (token > 9) {
            spriteToken.setAttribute("ui.label", ">9");
            spriteToken.setAttribute("ui.class", "token");
        } else {
            spriteToken.setAttribute("ui.label", token);
            spriteToken.setAttribute("ui.class", "token");
        }
    }

    /*
     * Die Methode aktualisiert die Visualisierung aller im PetrinetModel vorhandenen Place-Objekte.
     * Für jeden Place wird geprüft ob ein Sprite mit der Anzahl der Token auf ihm exisitert. Wenn es existiert, wird
     * geprüft ob die aktuelle Anzahl Token für diesen Place 0 ist und in dem Fall gelöscht damit die 0 nicht im Graphen angezeigt wird.
     * Ist die aktuelle Token Anzahl größer als 0, wird der Sprite aktualisiert. 
     */
    private void updatePlaces(Map<String, Place> places) {
            for (String p : places.keySet()) {
                Place place = places.get(p);
                int token = place.getCurrentToken();
                Node pNode = this.getNode(place.getId());
                setPlaceNameAttribute(place.getId(), place.getName(), token, pNode);
                
                Sprite spriteToken = spriteMan.getSprite(p);
                if (spriteToken != null) {                                                      // der initialToken war > 0, sprite existiert
                    if (token == 0) {                                                               // wenn aktuelle tokenzahl = 0, sprite löschen
                        spriteToken.removeAttribute("ui.label");
                    }
                } else {                                                                                // tokenzahl war 0, token existiert nicht
                    if (token > 0) {                                                                // wenn aktuelle tokenzahl > 0, sprite hinzufügen
                        spriteToken = spriteMan.addSprite(p);                   
                        spriteToken.attachToNode(pNode.getId());
                    }
                }
                
                if (token > 0) {
                    setTokenAttribute(token, spriteToken);
                }
            }
        }

        /*
         * Die Methode prüft für alle Transition-Objekte ob sie im aktuellen Zustand des
         * PetrinetzModel aktiviert oder nicht aktiviert sind und aktualisiert die
         * Visualisierung entsprechend.
         */
        private void updateTransitions(PetrinetModel net) {
            for (String t : net.getTransitions().keySet()) {
                Transition transition = net.getTransitions().get(t);
                Node tNode = this.getNode(transition.getId());
                if (net.isTransitionEnabled(transition.getId())) {
                    tNode.setAttribute("ui.class", "transitionEnabled");
                } else {
                    tNode.setAttribute("ui.class", "transitionNotEnabled");
                }
            }
        }

}
