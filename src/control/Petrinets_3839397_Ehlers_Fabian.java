package control;

import java.util.TreeSet;

import view.PetrinetMainFrame;

/**
 * Diese Klasse enthält die main Methode zum Starten des Programms.
 * 
 * @author Fabian Ehlers
 */
public class Petrinets_3839397_Ehlers_Fabian {
    
    /**
     * Die main Methode legt die UI-Skalierung  und die Nutzung der Graphstream-Bibliothek fest.
     * Sie erzeugt das Hauptfenster als Benutzeroberfläche dieses Programms.
     * 
     * @param args Wird nicht benutzt.
     */
    public static void main(String[] args) {
        
        System.setProperty("sun.java2d.uiScale", "1.0");

        System.setProperty("org.graphstream.ui", "swing");
        
        System.out.println("Besonders wichtige System Properties");
        System.out.println("------------------------------------");
        System.out.println("user.dir     = " + System.getProperty("user.dir"));
        System.out.println("java.version = " + System.getProperty("java.version"));
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PetrinetMainFrame("Fabian Ehlers - q3839397");
            }
        });
    }
    
    /**
     * Liefert einen String zurück, der alle System Properties (und ihre aktuellen
     * Werte) zeilenweise und in alphabetischer Reihenfolge enthält.
     * 
     * @return String mit allen System Properties in alphabetischer Reihenfolge.
     */
    public static String getAllSystemProperties() {
        TreeSet<String> propSet = new TreeSet<String>();
        
        for (Object propName : System.getProperties().keySet()) {
            propSet.add((String) propName + " = " + System.getProperty((String) propName));
        }
        String propertiesString = "";
        for (String prop : propSet) {
            propertiesString += prop + "\n";
        }
        return propertiesString;
    }
}
