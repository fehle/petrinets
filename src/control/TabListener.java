package control;

import org.graphstream.ui.view.ViewerListener;

import view.TabView;

/**
 * Die Klasse reagiert auf Mausclicks in den Graphen der TabView.
 * 
 * Um Clicks weiterleiten zu können benötigt der TabListener eine Refernz auf
 * eine TabView Instanz.
 * 
 * @author Fabian Ehlers
 */
public class TabListener implements ViewerListener {
    
    private TabView tabView;

    /**
     * Erzeugt einen neuen TabListener, der auf verschiedene Mausaktionen reagieren kann.
     * 
     * @param tabView Referenz auf die TabView Instanz
     */
    public TabListener(TabView tabView) {
        this.tabView = tabView;
    }

    @Override
    public void viewClosed(String viewName) {
    }

    /**
     * Die Methode informiert die TabView darüber welcher Knoten geclickt wurde.
     */
    @Override
    public void buttonPushed(String id) {
        System.out.println("TabListener - buttonPushed: " + id);

        tabView.nodeInGraphClicked(id);
    }

    @Override
    public void buttonReleased(String id) {
    }

    @Override
    public void mouseOver(String id) {
        
    }

    @Override
    public void mouseLeft(String id) {
    }
}
