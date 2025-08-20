package view;

import java.awt.*;
import java.awt.event.*;
import java.io.File;

import javax.swing.*;

import org.graphstream.ui.swing_viewer.*;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerPipe;

import control.*;

/**
 * Die Klasse repräsentiert einen Tab der im Hauptfenster des Programms
 * dargestellt wird.
 * <p>
 * Eine Instanz dieser Klasse besteht aus einem Panel zur Anzeige der Graphen,
 * einem TextFeld und einer Statusleiste. Wenn es sich um einen Tab zur Anzeige
 * der Ergebnisse einer Stapelanalyse handelt besteht die Instanz nur aus einem
 * Texfeld und einer Statusleiste.
 * </p>
 * Um die Interaktion zwischen User und Programm herzustellen benötigt die
 * Klasse eine Referenz auf einen Controller für den Tab. Hierüber können Clicks
 * die in den Graphen auftreten an den Controller weitergeleitet werden um die
 * entsprechenden Änderungen in den Datenmodelles oder den Graphen
 * durchzuführen.
 * 
 * @author Fabian Ehlers
 */
public class TabView extends JPanel {
    
    private TabController tabController;
    
    private JSplitPane tabSplitPane;
    private JSplitPane graphSplitPane;
    private JPanel pGraphPanel;
    private JPanel rGraphPanel;
    private ViewPanel pGraphViewPanel;
    private ViewPanel rGraphViewPanel;
    
    private JScrollPane textScrollPane;
    private JTextArea textArea;
    private JLabel statusLabel;
    
    /**
     * Der Konstruktor erzeugt einen Tab zur Anzeige eines Petrinetz-Graphen und
     * eines Erreichbarkeitsgraphen.
     * 
     * @param tabController Eine Referenz auf den Controller der auf Interaktion in
     *                      diesem Tab reagiert.
     * @param pGraph        Eine Referenz auf den Petrinetz-Graphen der in diesem
     *                      Tab dargestellt werden soll.
     * @param rGraph        Eine Referenz auf den Erreichbarkeitsgraphen der in
     *                      diesem Tab dargestellt werden soll.
     * @param fileName      Der Name der in diesem Tab geöffneten Datei als String.
     */
    public TabView(TabController tabController, PetrinetGraph pGraph, ReachabilityGraph rGraph, String fileName) {
        this.tabController = tabController;
        setLayout(new BorderLayout());

        initPanelPetrinetGraph(pGraph);
        initPanelReachGraph(rGraph);
        setView(fileName);
    }

    /**
     * Der Konstruktor erzeugt einen Tab zur Anzeige des Ergebnisses einer
     * Stapelanalyse.
     * 
     * @param tabController Eine Referenz auf den Controller der auf Interaktion in
     *                      diesem Tab reagiert.
     * @param fileCount Die anzahl der Dateien die analysiert werden.
     * @param path Der Pfad für das Verzeichnis der analysierten Dateien.
     */
     public TabView(TabController tabController, int fileCount, String path) {
         this.tabController = tabController;
         setLayout(new BorderLayout());
         textArea = new JTextArea();
         textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
         textScrollPane = new JScrollPane(textArea);
         
         statusLabel = new JLabel("Analyse von " + fileCount + " Dateien aus Verzeichnis: " + path);
         
         add(textScrollPane, BorderLayout.CENTER);
         add(statusLabel, BorderLayout.SOUTH);
    }

    /**
      * Die Methode gibt einen Click auf einen Knoten in einem Graphen an den
      * Controller weiter.
      * <p>
      * Wird aufgerufen von {@link TabListener}.
      * </p>
      * 
      * @param id Die id string des Knotens der geclickt.
      */
    public void nodeInGraphClicked(String id) {
        tabController.nodeInGraphClicked(id);
    }

    /**
      * Die Methode gibt einen String in Textfeld dieses Tabs aus.
      * 
      * @param text Ein String der im Textfeld ausgegeben werden soll.
      */
     public void outputText(String text) {
         textArea.append(text);
     }

     /**
     * Die Methode gibt die Datei zurück die in dieser TabView geöffnet ist.
     * 
     * @return Eine Referenz ein File.
     */
    public File getFile() {
        return tabController.getFile();
    }
    
    /**
     * Die Methode gibt den Controller einer TabView Instanz zurück.
     * 
     * @return Eine Referenz auf einen TabController.
     */
    public TabController getTabController() {
        return tabController;
    }
    
    /**
     * Die Methode gibt das Statuslabel einer TabView Instanz zurück.
     * 
     * @return Eine Referenz auf ein JLabel.
     */
    public JLabel getLabel() {
        return statusLabel;
    }
    
    /**
     * Die Methode gibt die TexArea einer TabView Instanz zurück.
     *  
     * @return Eine Referenz auf eine JTextArea.
     */
    public JTextArea getTextArea() {
        return textArea;
    }

    /*
     * Die Methode legt fest, wie die Komponenten in diesem Tab dargestellt werden
     * und fügt alle Komponenten in den Tab ein.
     */
    private void setView(String fileName) {
        pGraphPanel = new JPanel(new BorderLayout());
        pGraphPanel.add(pGraphViewPanel, BorderLayout.CENTER);
        
        rGraphPanel = new JPanel(new BorderLayout());
        rGraphPanel.add(rGraphViewPanel, BorderLayout.CENTER);
        
        graphSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pGraphPanel, rGraphPanel);
        graphSplitPane.setResizeWeight(0.5);
        graphSplitPane.setOneTouchExpandable(true);
        
        textArea = new JTextArea();
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        textScrollPane = new JScrollPane(textArea);
        textScrollPane.setPreferredSize(getSize());
        
        tabSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, graphSplitPane, textScrollPane);
        tabSplitPane.setResizeWeight(0.8);
        
        statusLabel = new JLabel("Dateiname: " + fileName);
        
        add(tabSplitPane, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
    }

    /*
     * Die Methode initialisiert ein Panel zur Anzeige des Petrinetz-Graphen.
     * Hierfür erzeugt sie einen Viewer mit passendem Threading-Model ohne ein
     * AutoLayout, fügt ihm einen TabListener hinzu der auf Interaktion in der View
     * reagieren kann und fügt Listener hinzu, die auf Maus-Ereignisse reagieren.
     */
     private void initPanelPetrinetGraph(PetrinetGraph pGraph) {
    
        SwingViewer viewer = new SwingViewer(pGraph,
                Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
    
        pGraph.setAttribute("ui.quality");
        pGraph.setAttribute("ui.antialias");
        
        viewer.disableAutoLayout();
    
        pGraphViewPanel = (ViewPanel) viewer.addDefaultView(false);
    
        ViewerPipe viewerPipe = viewer.newViewerPipe();
    
        TabListener tabListener = new TabListener(this);
    
        viewerPipe.addViewerListener(tabListener);
    
        pGraphViewPanel.addMouseListener(new MouseAdapter() {
    
            @Override
            public void mouseClicked(MouseEvent me) {
                System.out.println("TabView - mouseClicked" + me);
                viewerPipe.pump();
            }
        });
        
        pGraphViewPanel.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double zoomLevel = pGraphViewPanel.getCamera().getViewPercent();
                if (e.getWheelRotation() == -1) {
                    zoomLevel -= 0.1;
                    if (zoomLevel < 0.1) {
                        zoomLevel = 0.1;
                    }
                }
                if (e.getWheelRotation() == 1) {
                    zoomLevel += 0.1;
                }
                pGraphViewPanel.getCamera().setViewPercent(zoomLevel);
            }
        });
    }

    /*
     * Die Methode initialisiert ein Panel zur Anzeige des Erreichbarkeitsgraphen.
     * Hierfür erzeugt sie einen Viewer mit passendem Threading-Model mit einem
     * AutoLayout, fügt ihm einen TabListener hinzu der auf Interaktion in der View
     * reagieren kann und fügt Listener hinzu, die auf Maus-Ereignisse reagieren.
     */
    private void initPanelReachGraph(ReachabilityGraph rGraph) {
        
        SwingViewer viewer = new SwingViewer(rGraph,
                Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
    
        rGraph.setAttribute("ui.quality");
        rGraph.setAttribute("ui.antialias");
        
         viewer.enableAutoLayout();
    
        rGraphViewPanel = (ViewPanel) viewer.addDefaultView(false);
    
        ViewerPipe viewerPipe = viewer.newViewerPipe();
    
        TabListener tabListener = new TabListener(this);
    
        viewerPipe.addViewerListener(tabListener);
    
        rGraphViewPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                System.out.println("TabView - mouseClicked" + me);
                viewerPipe.pump();
            }
        });
        
        rGraphViewPanel.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double zoomLevel = rGraphViewPanel.getCamera().getViewPercent();
                if (e.getWheelRotation() == -1) {
                    zoomLevel -= 0.1;
                    if (zoomLevel < 0.1) {
                        zoomLevel = 0.1;
                    }
                }
                if (e.getWheelRotation() == 1) {
                    zoomLevel += 0.1;
                }
                rGraphViewPanel.getCamera().setViewPercent(zoomLevel);
            }
        });
    }
}
