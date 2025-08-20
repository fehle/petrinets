package model;

import java.awt.Point;

/**
 * Die Klasse repräsentiert eine Transition eines Petrinetzes.
 * 
 * Alle Transitionen werden von der Klasse {@link PetrinetModel} verwaltet.
 * 
 * @author Fabian Ehlers
 */
public class Transition {
    private final String id;
    private String name;
    private Point position;
    
    /**
     * Der Konstruktor erzeugt eine neue Transition. Die id dieser Transition wird
     * festgelegt, weitere Attribute werden in separaten Methoden festgelegt.
     * 
     * @param id Die id dieser Transition.
     */
    protected Transition(final String id) {
        this.id = id;
    }
    
    /**
     * Die Methode gibt die id der Transition zurück.
     * 
     * @return Der gespeicherte Wert für die id.
     */
    public String getId() {
        return id;
    }
    
    /**
     * Die Methode gibt den Namen für der Transition zurück.
     * 
     * @return Der gespeicherte Wert für den Namen.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Die Methode gibt die x-Ordinate zurück.
     * 
     * @return Der gespeicherte Wert für die x-Ordinate.
     */
    public double getPosX() {
        return position.getX();
    }
    
    /**
     * Die Methode gibt die y-Ordinate zurück.
     * 
     * @return Der gespeicherte Wert für die y-Ordinate.
     */
    public double getPosY() {
        return position.getY();
    }
    
    /**
     * Die Methode legt den Namen dieser Transition fest.
     * 
     * @param name Der Name dieser Transition.
     */
    protected void setName(final String name) {
        this.name = name;
    }
    
   /**
    * Die Methode legt die Position dieser Transition fest und speichert sie in einem Point.
    * Der y-Wert der Position wird für die korrekte Ausgabe auf dem Bildeschirm negiert. 
    * 
    * @param x Die Zeichenkette welche die x-Ordinate repräsentiert.
    * @param y Die Zeichenkette welche die  y-Ordinate repräsentiert.
    * @throws IllegalArgumentException wenn für mindestens eine Koordinate keine Dezimalzahl übergeben wird.
    */
    protected void setPosition(String x, String y) {
        try {
            double posX = Double.parseDouble(x);
            double posY = Double.parseDouble(y);
            position = new Point();
            position.setLocation(posX, -posY);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Die Transition " + id + " hat eine unzulässige Angabe für das Attribut 'position', hier sind nur Zahlen zulässig");
        }
    }
}
