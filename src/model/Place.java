package model;

import java.awt.Point;

/**
 * Diese Klasse repräsentiert eine Stelle eines Petrinetzes.
 * 
 * Alle Stellen werden von der Klasse {@link PetrinetModel} verwaltet.
 * 
 * @author Fabian Ehlers
 */
public class Place {
    
    final private String id;
    private String name;
    private Point position;
    
    /**
     * Die initiale Anzahl von Marken der Stelle.
     */
    private int initialToken;
    
    /**
     * Die aktuelle Anzahl von Marken der Stelle.
     */
    private int currentToken;

    /**
     * Der Konstruktor erzeugt eine neue Stelle. Die id dieser Stelle wird
     * festgelegt, weitere Attribute werden in separaten Methoden festgelegt.
     * 
     * @param id Die id dieser Stelle.
     */
    protected Place(final String id) {
        this.id = id;
    }
    
    /**
     * Die Methode gibt die id der Stelle zurück.
     * 
     * @return Der gespeicherte Wert für die id.
     */
    public String getId() {
        return id;
    }
    
    /**
     * Die Methode gibt den Namen der Stelle zurück.
     * 
     * @return Der gespeicherte Wert für den Namen.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Die Methode gibt die x-Ordinate der Stelle zurück.
     * 
     * @return Der gespeicherte Wert der x-Ordinate.
     */
    public double getPosX() {
        return position.getX();
    }
    
    /**
     * Die Methode gibt die y-Ordinate der Stelle zurück.
     * 
     * @return Der gespeicherte Wert der y-Ordinate.
     */
    public double getPosY() {
        return position.getY();
    }
    
    /**
     * Die Methode gibt die Anzahl der initialen Token der Stelle zurück.
     * 
     * @return Der gespeicherte Wert für die initiale Tokenanzahl. 
     */
    public int getInitialToken() {
        return initialToken;
    }
    
    /**
     * Die Methode gibt die Anzahl der aktuellen Token der Stelle zurück.
     * 
     * @return Der gespeicherte Wert für die aktuelle Tokenanzahl.
     */
    public int getCurrentToken() {
        return currentToken;
    }
    
    /**
     * Diese Methode legt den Namen dieser Stelle fest.
     * 
     * @param name Der Name dieser Stelle.
     */
    protected void setName(final String name) {
        this.name = name;
    }

    /**
     * Die Methode legt die Position dieser Stelle fest und speichert sie in einem Point.
     * Der y-Wert der Position wird für die korrekte Ausgabe auf dem Bildeschirm negiert. 
     * 
     * @param x Die Zeichenkette welche die x-Ordinate repräsentiert.
     * @param y Die Zeichenkette welche die  y-Ordinate repräsentiert.
     * 
     */
    protected void setPosition(String x, String y) {
        try {
            double posX = Double.parseDouble(x);
            double posY = Double.parseDouble(y);
            position = new Point();
            position.setLocation(posX, -posY);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Die Stelle " + id + " hat eine unzulässige Angabe für das Attribut 'position', hier sind nur Zahlen zulässig.");
        }
    }
    
    /**
     * Die Methode legt die Anzahl der initialen Marken und der aktuellen Token
     * dieser Stelle fest.
     * 
     * @param id    Die id dieser Stelle.
     * @param token Der Wert für die Anzahl der Marken als Zeichenkette.
     * 
     * @throws IllegalArgumentException wenn ein negativer Wert übergeben wird.
     * @throws IllegalArgumentException wenn der Wert für token keine Ganzzahl
     *                                  übergeben ist.
     * 
     */
    protected void setInitialToken(final String id, final String token) {
        try {
            int tok = Integer.parseInt(token);
            if (tok >= 0) {
                this.initialToken = tok;
                this.currentToken = initialToken;
            } else
                throw new IllegalArgumentException(
                        "Negative Werte für das Attribut 'initialMarking' sind nicht zulässig");
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Die Stelle " + id + " hat eine unzulässige Angabe für das Attribut 'initialToken', hier sind nur positive Ganzzahlen zulässig.");
        }
    }
    
    /**
     * Die Methode erhöht die Anzahl der aktuellen Marken um 1.
     */
    protected void increaseCurrentToken() {
        ++currentToken;
    }

    /**
     * Die Methode verringert die Anzahl der aktuellen Marken um 1 wenn der Wert
     * größer als 0 ist, sonst bleibt er 0.
     */
    protected void decreaseCurrentToken() {
        if (currentToken > 0)
            --currentToken;
    }
    
    /**
     * Die Methode setzt die Anzahl der aktuellen Marken dieser Stelle auf die Anzahl
     * der initialen Marken dieser Stelle.
     */
    protected void resetCurrentToken() {
        currentToken = initialToken;
    }
    
    /**
     * Die Methode aktualisiert die Anzahl der aktuellen Marken dieser Stelle.
     * 
     * @param token Die neue Anzahl der Marken.
     */
    protected void updateCurrentToken(int token) {
        currentToken = token;
    }

    /**
     * Die Methode entscheidet wie die Anzahl der initialen Marken dieser Stelle verändert werden soll
     * und veranlasst die Änderung.
     * @see #increaseToken()
     * @see #decreaseToken()
     * @see #updateInitialToken()
     * 
     * @param value Der Wert entscheidet ob die Tokananzahl verändert wird und ob sie erhöht oder verringert wird.
     * @return {@code true} wenn der Wert aktualisiert wurde, {@code false} sonst.
     */
    protected boolean updateInitialToken(String value) {
        if (value == null) {
            updateInitialToken();
            return true;
        }
        else if (value.equals("+")) {
            increaseToken();
            return true;
        }
        else if (value.equals("-")) {
            if (decreaseToken())
                return true;
            else
                return false;
        }
        return false;
    }

    /**
     * Der Wert für {@link #currentToken} wird um 1 erhöht und der Wert für
     * {@link #initialToken} wird auf den gleichen Wert gesetzt.
     */
    private void increaseToken() {
        ++currentToken;
        updateInitialToken();
    }

    /**
     * Falls der Wert für {@link #currentToken} größer als 0 ist, wird er um 1
     * verringert und der Wert für {@link #initialToken} wird auf den gleichen Wert
     * gesetzt.
     * @see #updateInitialToken()
     */
    private boolean decreaseToken() {
        if (currentToken > 0) {
            --currentToken;
            updateInitialToken();
            return true;
        }
        return false;
    }

    /**
     * Die Methode setzt den Wert für {@link #initialToken} auf den gleichen Wert
     * wie {@link #currentToken}.
     */
    private void updateInitialToken() {
        initialToken = currentToken;
    }
    
}
