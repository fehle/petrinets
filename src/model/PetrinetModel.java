package model;

import java.util.*;

import control.SimplePetrinetParser;
/**
 * Die Klasse repräsentiert das Datenmodell für ein Petrinetz.
 * <p>
 * Das Modell verwaltet alle Elemente des Petrinetzes. Es besteht aus
 * {@link Place}-, {@link Transition}- und {@link Arc}-Objekten.
 * </p>
 * Weiter enthält die Klasse Methoden für Operationen auf dem Petrinetz für die
 * Informationen des gesamten Petrinetzes und nicht nur eines Elements des Modells
 * nötig sind.
 * 
 * @author Fabian Ehlers
 */
public class PetrinetModel {
    private Map<String, Place> places;
    private Map<String, Transition> transitions;
    private Map<String, Arc> arcs;
    
    /**
     * Der Konstruktor erzeugt das Modell eines Petrinetzes mit Containern für Arc-, Place- und Transition-Objekte.
     */
    public PetrinetModel() {
        this.places = new TreeMap<>();
        this.transitions = new TreeMap<>();
        this.arcs = new TreeMap<>();
    }

    /**
     * Die Methode fügt einem Modell einen neuen Arc hinzu wenn die id noch nicht
     * in dem Modell vorhanden ist und noch kein Arc mit gleichem Quell- und
     * Zielknoten existiert.
     * <p>
     * Die Methode wird aufgerufen von {@link SimplePetrinetParser}.
     * </P>
     * @param id Die id der Kante.
     * @param source Der Name des Quellknotens.
     * @param target Der Name der Zielknotens.
     * @throws IllegalArgumentException wenn die id im Modell
     *               bereits vorhanden ist.
     * @throws IllegalArgumentException wenn bereits eine Kante
     *               mit gleichem Quell- und Zielknoten vorhanden ist.
     */
    public void addArc(final String id, final String source, final String target) {
        if (isValidId(id)) {
            for (String a : arcs.keySet()) {
                Arc arc = arcs.get(a);
                if (arc.getSource().equals(source) && arc.getTarget().equals(target)) {
                    throw new IllegalArgumentException(
                            " Die Kante mit der id: " + id + " kann nicht erzeugt werden da bereits eine Kante von \n"
                                    + source + " nach " + target + " exisitiert, diese hat die id: " + arc.getId());
                }
            }
            arcs.put(id, new Arc(id, source, target));
        } else {
            throw new IllegalArgumentException("Die id der Kante " + id + " existiert bereits. \n");
        }
    }
        
    /**
     * Die Methode fügt einem Modell einen neuen Place hinzu wenn die id 
     * noch nicht in dem Modell vorhanden ist.
     * <p>
     * Die Methode wird aufgerufen von {@link SimplePetrinetParser}.
     * </p>
     * @param id Die id der neuen Stelle.
     * @throws IllegalArgumentException wenn die id im Modell
     *               bereits vorhanden ist.
     */
    public void addPlace(final String id) {
        if (isValidId(id)) {
            places.put(id, new Place(id));
        } else {
            throw new IllegalArgumentException("Die id der Stelle " + id + " existiert bereits.");
        }
    }

    /**
     * Die Methode fügt einem Modell eine neue Transition hinzu wenn die id 
     * noch nicht in dem Modell vorhanden ist.
     * <p>
     * Die Methode wird aufgerufen von {@link SimplePetrinetParser}.
     * </p>
     * @param id Die id der neuen Stelle.
     * @throws IllegalArgumentException wenn die id im Modell
     *               bereits vorhanden ist.
     */
    public void addTransition(final String id) {
        if (isValidId(id)) {
            transitions.put(id, new Transition(id));
        } else {
            throw new IllegalArgumentException("Die id der Transition " + id + " existiert bereits.");
        }
    }

    /**
     * Die Methode legt den Namen für einen Place oder eine
     * Transition fest wenn die id im Modell vorhanden ist.
     * 
     * @param id   Die id des zu benennen Objekts.
     * @param name Der Name den das Objekt erhalten soll.
     */
    public void setName(final String id, final String name) {
        if (places.containsKey(id)) {
            places.get(id).setName(name);
        } else if (transitions.containsKey(id)) {
            transitions.get(id).setName(name);
        }
    }

    /**
     * Die Methode legt die Position für einen Place ode eine
     * Transition fest wenn die id im Modell vorhanden ist.
     * 
     * @param id Die id des zu positionierenden Objekts.
     * @param x  Die x-Ordinate.
     * @param y  Die y-Ordinate.
     */
    public void setPosition(final String id, String x, String y) {
        if (places.containsKey(id)) {
            places.get(id).setPosition(x, y);
        } else if (transitions.containsKey(id)) {
            transitions.get(id).setPosition(x, y);
        }
    }
    
    /**
     * Die Methode legt die Anzahl der Token für einen Place 
     * fest wenn die id im Modell vorhanden ist.
     * 
     * @param id    Die id des Knotens dessen Tokenzahl festgelegt werden
     *              soll.
     * @param token Die Anzahl der Token als Zeichenkette.
     */
    public void setTokens(final String id, final String token) {
        if (places.containsKey(id)) {
            places.get(id).setInitialToken(id, token);
        }
    }
    
    /**
     * Die Methode validiert das gesamte PetrinetModel indem sie für alle
     * enthaltenen Arc-, Place- und Transition-Objekte prüft
     * ob diese den Definitionen eines Petrinetzes im Rahmen dieses Programms
     * entsprechen.
     */
    public void validatePetrinet() {
        validateArcs();
        validate();
    }

    /**
     * Die Methode prüft ob eine Transition eines Petrinetzes aktiviert ist. Anhand
     * aller Stellen im Vorbereich einer Transition wird geprüft ob diese alle
     * mindstens eine Marke haben.
     * 
     * @param id Die id der Transition für die geprüft wird ob sie aktiviert ist.
     * @return {@code true} wenn die Transition aktiviert ist, {@code false} wenn
     *         die Transition nicht aktiviert ist.
     */
    public boolean isTransitionEnabled(String id) {
        Transition transition = transitions.get(id);
        for (String a : arcs.keySet()) {
            Arc arc = arcs.get(a);
            if (arc.getTarget().equals(transition.getId())) {
                Place place = places.get(arc.getSource());
                if (place.getCurrentToken() == 0) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Die Methode schaltet eine Transition. 
     * <p>
     * Achtung<br/>
     * Prüft nicht ob die Transition aktiviert ist, dies muss vor dem Schalten durch
     * {@link #isTransitionEnabled(String)} sichergestellt werden. Entfernt eine
     * Marke aus jedem Place des Vorbereichs der Transition und fügt jedem
     * Place des Nachbereichs eine Marke hinzu.
     * </p>
     * @param id Die id der Transition die geschaltet werden soll.
     */
    public void shiftTransition(String id) {
        Transition transition = transitions.get(id);
        for (String a : arcs.keySet()) {
            Arc arc = arcs.get(a);

            /* Alle Stellen im Vorbereich der Transition werden um 1 Token dekrementiert. */
            if (arc.getTarget().equals(transition.getId())) {
                Place forePlace = places.get(arc.getSource());
                forePlace.decreaseCurrentToken();
            }

            /* Alle Stellen im Nachbereich der Transition werden um 1 Token inkremetiert. */
            if (arc.getSource().equals(transition.getId())) {
                Place aftPlace = places.get(arc.getTarget());
                aftPlace.increaseCurrentToken();
            }
        }
    }
    
    /**
     * Die Methode gibt eine Map mit allen Place-Objekten eines Modells zurück.
     * 
     * @return Die gespeicherte Map mit allen Stellen.
     */
    public Map<String, Place> getPlaces() {
        return places;
    }
    
    /**
     * Die Methode gibt eine Map mit allen Transition-Objekten eines Modells zurück.
     * 
     * @return Die gespeicherte Map mit allen Transitionen.
     */
    public Map<String, Transition> getTransitions() {
        return transitions;
    }
    
    /**
     * Die Methode gibt eine Map mit allen Arc-Objekten eines Modells zurück.
     * 
     * @return Die gespeicherte Map mit allen Kanten.
     */
    public Map<String, Arc> getArcs() {
        return arcs;
    }

    /**
     * Die Methode übergibt einem Place die Anweisung wie er seine initialen Token 
     * aktualiseren soll.  
     * 
     * @param placeID Die id der Stelle dessen initiale Markenzahl akualisiert werden
     *                soll.
     * @param value   Der Wert der der Stelle übergibt wie die Anzahl
     *                geändert werden soll.
     * @return {@code true} wenn die Anzahl der Marken aktualisiert wurde,
     *         {@code false} wenn der Wert nicht angepasst wurde.
     */
    public boolean updateInitialToken(String placeID, String value) {
        Place place = places.get(placeID);
        
        return place.updateInitialToken(value);
    }

    /**
     * Die Methode setzt die Marken aller Place-Objekte in einem Modell auf ihre
     * initiale Anzahl.
     */
    public void reset() {
        for (String p : places.keySet()) {
            Place place = places.get(p);
            place.resetCurrentToken();
        }
    }

    /**
     * Die Methode setzt die Marken aller Place-Objekte in einem Modell
     * auf die Anzahl einer bestimmten Markierung.
     * 
     * @param marking Die Markierung, die angibt welche Stelle wie viele
     *                Marken bekommt.
     */
    public void jumpToMarking(int[] marking) {
        int count = 0;
        
        for (String p : places.keySet()) {
            Place place = places.get(p);
            place.updateCurrentToken(marking[count++]);
        }
    }

    /*
     * Die Methode prüft ob eine id schon in einem Modell vorhanden ist.
     */
    private boolean isValidId(String id) {
        if (places.containsKey(id) || transitions.containsKey(id) || arcs.containsKey(id)) {
            return false;
        } else {
            return true;
        }
    }
    
    /*
     * Die Methode validiert ob alle eingelesenen Kanten der Definition von Kanten
     * im Rahmen dieses Programms entsprechen. Wenn eine nicht zulässige Kante
     * gefunden wird, wird eine IllegalArgumentException geworfen.
     */
    private void validateArcs() {
        for (String a : arcs.keySet()) {
            Arc arc = arcs.get(a);
            
            if (!(places.containsKey(arc.getSource()) && transitions.containsKey(arc.getTarget()))
                    && !(transitions.containsKey(arc.getSource()) && places.containsKey(arc.getTarget()))) {
                throw new IllegalArgumentException(
                        "Kanten können nur zwischen vorhandenen Stellen und Transitionen existieren. \n" + "Die Kante: "
                                + arc.getId() + " ist nicht mit jeweils einer Stelle und einer Transition verbunden.");
            }
        }
    }
    
    /*
     * Die Methode validiert das geparste Petrinetz. Hierfür wird geprüft ob
     * alle Stellen und Transitionen miteinander verbunden sind, also ob alle
     * Elemente des Petrinetzes zusammenhängend sind.
     */
    private void validate() {
        if (places.size() == 0) {
            throw new IllegalArgumentException(
                    "Ein Petrinetz muss mindestens eine Stelle enthalten.");
        } else {
            List<String> net = new ArrayList<String>();
            int index = 0;

            for (String placeId : places.keySet()) {
                net.add(placeId);
                break;
            }
            while (index < net.size()) {
                for (String arcId : arcs.keySet()) {
                    Arc arc = arcs.get(arcId);
                    if ((arc.getSource().equals(net.get(index))) && (!net.contains(arc.getTarget()))) {
                        net.add(arc.getTarget());
                    } else if ((arc.getTarget().equals(net.get(index))) && (!net.contains(arc.getSource()))) {
                        net.add(arc.getSource());
                    }
                }
                ++index;
            }
            if ((net.size()) != (places.size() + transitions.size())) {
                throw new IllegalArgumentException(
                        "Das Petrinetz ist nicht verbunden.");
            }
        }
    }
} 
