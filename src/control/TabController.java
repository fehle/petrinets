package control;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;

import javax.swing.*;

import algorithmus.PetrinetAnalysis;
import model.*;
import view.*;

/**
 * Die Klasse repräsentiert einen Controller, der eine Einheit nach MVC-Schema
 * im Rahmen dieses Programms kontrolliert.
 * <p>
 * Der TabController besitzt eine Referenz auf das DatenModell, also
 * {@link PetrinetModel} und {@link ReachabilityGraphModel} um angeforderte
 * Änderungen and diesem durchzuführen.<br/>
 * Weiter besitzt er eine Referenz auf die {@link TabView} und die Graphen
 * {@link PetrinetGraph} und {@link ReachabilityGraph} um Änderungen anzeigen zu
 * lassen.<br/>
 * Außerdem enthält er eine Referenz auf den {@link FrameController} um
 * Ansichten zu diesem hinzuzufügen oder zu entfernen.
 * </p>
 * 
 * @author Fabian Ehlers
 */
public class TabController implements ActionListener {
    
    private FrameController frameController;
    private PetrinetModel pNet;
    private ReachabilityGraphModel rGraphModel;
    private PetrinetGraph pGraph;
    private ReachabilityGraph rGraph;
    
    private TabView tab;
    
    private File file;
    
    private boolean isStackAnalysisTab = false;
    
    /**
     * Der Konstruktor erzeugt einen Controller zur Anzeige von zwei Graphen.
     * 
     * @param frameController Eine Referenz auf den zugehörigen
     *                        FrameController.
     * @param file            Eine Referenz auf das geöffnete File.
     * @param pNet            Eine Referenz auf das PetrinetModel.
     * @param rGraphModel     Eine Referenz auf das ReachabilityGraphModel.
     */
    protected TabController(FrameController frameController, File file, PetrinetModel pNet,
            ReachabilityGraphModel rGraphModel) {
        this.frameController = frameController;
//        this.frame = frame;
        this.file = file;
        this.pNet = pNet;
        this.rGraphModel = rGraphModel;
        setPetrinetGraph(null);
        setReachGraph();
        newTab();
    }
    
    /**
     * Der Konstruktor erzeugt einen Controller zur Anzeige einer Stapelanalyse.
     * 
     * @param frameController Eine Referenz auf den zugehörigen FrameController.
     * @param pNetModels      Eine Referenz auf die Map mit den geladenen Dateien.
     * @param path            Ein Pfad des Verzeichnisses der geladenen Dateien.
     */
    protected TabController(FrameController frameController, Map<File, PetrinetModel> pNetModels, String path) {
        this.frameController = frameController;
        isStackAnalysisTab = true;
        newStackAnalysisTab(pNetModels.size(), path);
        analyseStack(pNetModels);
    }

    /**
     * Die Methode verarbeitet Events die in der Tableiste auftreten.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if ("closeTab".equals(e.getActionCommand())) { 
            frameController.removeTab(tab);
        }
    }
    
    /**
     * Die Methode stellt fest welcher Knoten geclickt wurde.
     * <p>
     * Wenn ein ReachabilityGraph-Knoten geclickt wurde, wird das PetrinetModel auf
     * den Zustand der im ReachabilityNode gespeicherten Markierung gesetzt und die
     * Hervorhebung des Knotens wird entfernt.
     * </p>
     * Wenn ein PetrinetGraph-Knoten geclickt wurde wird das
     * PetrinetModel geschaltet falls dieser eine aktivierte
     * Transition repräsentiert. Sonst wird nur der geclickte Knoten
     * hervorgehoben oder falls ein hervorgehobener Place geclickt wurde die
     * Hervorhebung entfernt.
     * <p>
     * Wird aufgerufen von {@link TabView}.
     * </p>
     * 
     * @param id Die id des Knotens der geclickt wurde.
     */
    public void nodeInGraphClicked(final String id) {
        if (isReachGraphNodeClicked(id)) {
            pGraph.deleteSpriteNode();
            setNetToClickedMarking(id);
        } else {
            pGraph.markLastClickedNode(id);
            if (isTransitionClicked(id)) {
                shift(id);
            }
        }
    }

    /**
     * Die Methode gibt eine Referenz auf das File eines TabController.
     * 
     * @return Eine Referenz auf ein File oder {@code null} falls der Tab eine
     *         Stapelanalyse enthält.
     */
    public File getFile() {
        return file;
    }

    /**
     * Die Methode ersetzt die aktuelle TabView mit einer neuen und übergibt diese
     * an den FrameController damit er die aktuell im PetrinetMainFrame angezeigte
     * ersetzt.
     * <p>
     * Wird aufgerufen von {@link FrameController}.
     * </p>
     * @param newPetrinetModel   Eine Referenz auf ein PetrinetModel.
     * @param newReachGraphModel Eine Referenz auf ein ReachabilityGraphModel.
     * @param newFile            Eine Referenz auf ein File.
     */
    protected void reloadTab(PetrinetModel newPetrinetModel, ReachabilityGraphModel newReachGraphModel, File newFile) {
        pNet = newPetrinetModel;
        rGraphModel = newReachGraphModel;
        setPetrinetGraph(null);
        setReachGraph();
        file = newFile;
        tab = new TabView(this, pGraph, rGraph, file.getName());
        frameController.setSelectedTab(tab);
        createTabHeader();
        tab.outputText("Die Datei '" + file.getName() + "' wurde geladen.\n");
    }

    /**
     * Die Methode ersetzt eine vorhandene TabView zur Anzeige Stapelanalyse durch
     * eine neue TabView zur Anzeige einer Stapelanalyse und startet die
     * Stapelanalyse.
     * <p>
     * Wird aufgerufen von {@link FrameController}.
     * </p>
     * 
     * @param pNetModels Eine Referenz auf die Map die alle geladenen Dateien
     *                   enthält.
     * @param path       Ein Pfad der das Verzeichnis der geladenen Dateien enthält.
     */
    protected void updateStackAnalyseTab(Map<File, PetrinetModel> pNetModels, String path) {
        tab = new TabView(this, pNetModels.size(), path);
        frameController.setSelectedTab(tab);
        analyseStack(pNetModels);
    }

    /**
     * Die Methode setzt vor der Analyse des Petrinetzes beide Models zurück.
     * <p>
     * Führt die Analyse durch und aktualisiert die Graphen.
     * Das Ergebnis der Analyse wird im Textfeld angezeigt.
     * </p>
     * @return Das Ergebnis der Analyse.
     */
    protected String analyse() {
            resetModels();
            PetrinetAnalysis analysis = new PetrinetAnalysis(pNet, rGraphModel);
            tab.outputText("Das geladene Petrinetz wird analysiert...\n");
            analysis.analyseBoundedness();
            String boundedness = "beschränkt";
    
            pGraph.updateGraph(pNet);
            pGraph.deleteSpriteNode();
            rGraph. paintReachGraph(rGraphModel);
            
            if (analysis.isNetUnbounded()) {
                boundedness = "unbeschränkt";
                rGraph.highlightNode(analysis.getUnboundedNode().getId());
                rGraph.highlightPathNodes(analysis.getDecisionNode(), analysis.getUnboundedNode());
                for (ReachabilityEdge pathEdge : analysis.getReachEdges()) {
                    rGraph.highlightPathEdge(pathEdge);
                }
                tab.outputText("Das Petrinetz ist " + boundedness + ". Die Länge des gefunden Pfades ist " + analysis.getReachEdges().size() + ". Die entsprechenden"
                        + " Knoten und Kanten sind im partiellen Erreichbarkeitsgraph hervorgehoben.\n");
            } else {
                rGraph.highlightNode(rGraphModel.getNodes().getFirst().getId());
                tab.outputText("Das Petrinetz ist " + boundedness + ". Der Erreichbarkeitsgraph besteht aus " + rGraphModel.getNodes().size() + " Knoten und " 
                        + rGraphModel.getEdges().size() + " Kanten.\n");
            }
            return boundedness;
    }

    /**
     * Die Methode aktualisiert die Anfangsmarkierung des Petrinetzes und setzt das
     * Netz auf diese Markierung falls im Petrinetz-Graph eine Stelle hervorgehoben
     * ist.
     * <p>
     * Für alle nicht hervorgehobenen Stellen wird die initiale und aktuelle
     * Markenzahl auf die aktuell vorhandene Anzahl Marken gesetzt.<br/>
     * Wenn keine Stelle im Graph hervorgehoben ist, wird eine Textmeldung
     * ausgegeben.
     * </p>
     * Im Textfeld des Tabs wid ausgegeben für welche Stelle die initiale Tokenzahl
     * verändert wurde und im Statuslabel wird ausgegeben dass die Anfangsmarkierung
     * geändert wurde.
     * 
     * @param value Ein String anhand dessen entschieden wird wie die Anzahl
     *              verändert wird..
     */
    protected void updateInitialMarking(String value) {
        if (anyPlaceMarked()) {
            
//            String clickedNode = pGraph.getClickedNode();
//            if (!((pNet.getPlaces().get(clickedNode).getCurrentToken() == 0) && (value.equals("-") ))) {
            
            for (String p : pNet.getPlaces().keySet()) {
                Place place = pNet.getPlaces().get(p);
                if (pGraph.isPlaceMarked(place.getId())) {
                    if (pNet.updateInitialToken(place.getId(), value)) {
                        if (value.equals("+")) {
                        tab.outputText("Stelle " + place.getId() + " wurde ein Token hinzugefügt...\n");
                        } else if (value.equals("-")) {
                            tab.outputText("Stelle " + place.getId() + " wurde ein Token abgezogen...\n");
                        }
                        
                        String initLabelText = "Dateiname: " + file.getName();
                        if (initLabelText.equals(tab.getLabel().getText())) {
                            tab.getLabel().setText(tab.getLabel().getText() + "      |      >>Die Anfangsmarkierung dieses Petrinetzes wurde geändert<<");
                        }
                    } else {
                        tab.outputText("Stelle " + place.getId() + " enthält keine Token, somit kann kein Token abgezogen werden. "
                                + "Die Markierung wurde nicht geändert.\n");
                        return;
                    }
                }
                else {
                    pNet.updateInitialToken(place.getId(), null);
                    }
            }
            resetModels();
            tab.outputText("...und das Petrinetz und der Erreichbarkeitsgraph wurden auf die neue Anfangsmarkierung gesetzt.\n");

//            } else {
//                tab.outputText("Stelle " + clickedNode + " enthält keine Token, somit kann kein Token abgezogen werden. "
//                        + "Die Markierung wurde nicht geändert.\n");
//                return;
//            }
        
        } else
            tab.outputText("Bitte eine Stelle auswählen bei der die Token Anzahl verändert werden soll.\n");
    }

    /**
     * Die Methode setzt das Modell und den Graphen auf die aktuelle
     * Anfangsmarkierung zurück. Falls ein Knoten hervorgehoben ist, wird diese
     * entfernt.
     * <p>
     * Im Erreichbarkeitsgraph wird der Knoten mit der Markierung die die
     * Anfangsmarkierung repräsentiert hervorgehoben und die Kantenhervorhebung
     * entfernt.
     * </p>
     * Im Textfeld wird eine Information über das Zurücksetzen ausgegeben.
     */
    protected void resetPetrinet() {
        pNet.reset();
        pGraph.deleteSpriteNode();
        pGraph.updateGraph(pNet);
        rGraph.highlightNode(rGraphModel.getNodes().getFirst().getId());
        rGraph.toggleEdgeHighlight(null);
        tab.outputText("Das Petrinetz wurde auf die aktuelle Anfangsmarkierung zurückgesetzt.\n");
    }

    /**
     * Die Methode setzt die Modelle und Graphen auf die aktuelle Anfangsmarkierung
     * zurück.
     */
    protected void reset() {
        resetModels();
        tab.outputText(
                "Das Petrinetz und der (partielle)-Erreichbarkeitsgraph wurden auf die aktuelle Anfangsmarkierung zurückgesetzt.\n");
    }

    /**
     * Die Methode gibt zurück ob die TabView eines TabController-Objekts eine Stapelanalyse 
     * oder Graphen anzeigt.
     * 
     * @return {@code true} wenn eine Stapelanalyse angezeigt wird, sonst {@code false}.
     */
    protected boolean isStackAnalysisTab() {
        return isStackAnalysisTab;
    }

    /*
     * Die Methode erzeugt einen neuen PetrinetGraph. Die übergebene id hebt einen
     * Place hervor wenn ein Place mit dieser id vorhanden ist.
     */
    private void setPetrinetGraph(String lastClickedId) {
        pGraph = new PetrinetGraph(pNet, lastClickedId);
    }

    /*
     * Die Methode erzeugt einen neuen ReachabilityGraph mit dem Wurzelknoten.
     */
    private void setReachGraph() {
        rGraph = new ReachabilityGraph(rGraphModel.getNodes().getFirst());
    }

    /*
     * Die Methode erzeugt einen neuen Tab zur Anzeige zweier Graphen und übergibt
     * ihn an den frameController zum Einfügen in den Frame.
     */
    private void newTab() {
        tab = new TabView(this, pGraph, rGraph, file.getName());
        frameController.setTab(tab);
        createTabHeader();
        tab.outputText("Die Datei '" + file.getName() + "' wurde geladen.\n");
    }

    /*
     * Die Methode erzeugt einen neuen Tab zur Anzeige der Ergebnisse der
     * Stapelanalyse und übergibt den Tab an den frameController.
     */
    private void newStackAnalysisTab(int fileCount, String path) {
        tab = new TabView(this, fileCount, path);
        frameController.setTab(tab);
        createTabHeader();
    }
    
    /*
     * Die Methode erzeugt ein Panel, das den Namen der geladenen Datei und den
     * Button zum Schließen des Tabs enthält. Dieses wird an den frameController
     * übergeben um es auf den Tab zu setzen.
     */
    private void createTabHeader() {
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel();
        if (file != null)
            titleLabel.setText(file.getName());
        else
            titleLabel.setText("Stapelverarbeitung");
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        titlePanel.add(titleLabel);
        ImageIcon tabCloseIcon = PetrinetMainFrame.createImageIcon("/icons/tab-close.16.png");
        String buttonText = "  x ";
        JButton closeTabButton = new JButton(buttonText);
        closeTabButton.setFocusable(false);
        closeTabButton.setToolTipText("Tab schließen");
        closeTabButton.setActionCommand("closeTab");
        Font font = new Font("Arial", Font.BOLD, 12);
        closeTabButton.setFont(font);
        closeTabButton.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));
        closeTabButton.setContentAreaFilled(false);
        closeTabButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                closeTabButton.setText(null);
                closeTabButton.setIcon(tabCloseIcon);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                closeTabButton.setIcon(null);
                closeTabButton.setText(buttonText);
            }
        });
        closeTabButton.addActionListener(this);
        titlePanel.add(closeTabButton);
        
        frameController.setTabHeader(tab, titlePanel);
    }

    /*
     * Die Methode prüft ob eine id zu einem Knoten des Erreichbarkeitsgraphen
     * gehört. 
     * return true wenn die id zu einem Knoten des Erreichbarkeitsgraphen
     * gehört, sonst false.
     */
    private boolean isReachGraphNodeClicked(final String id) {
        List<ReachabilityNode> nodeList = rGraphModel.getNodes();
    
        for (ReachabilityNode node : nodeList) {
            if (node.getId().equals(id))
                return true;
        }
        return false;
    }

    /*
     * Die Methode prüft ob eine id zu einer Transition des Petrinetzes gehört.
     * return true wenn die id zu einer Transition gehört, sonst false.
     */
    private boolean isTransitionClicked(String id) {
        if (pNet.getTransitions().containsKey(id))
            return true;
        return false;
    }

    /*
     * Die Methode schaltet eine Transition des Petrinetzes. Es wird geschaltet wenn
     * die geclickte Transition aktiviert ist. Weiter wird der Graph des Petrinetzes
     * aktualisiert und die Aktualisierung des Modells des Erreichabrkeitsgraphen
     * wird veranlasst.
     */
    private void shift(final String transitionId) {
            ReachabilityNode currentState = rGraphModel.findNode(new ReachabilityNode(0, pNet.getPlaces()));
            if (pNet.isTransitionEnabled(transitionId)) {
                pNet.shiftTransition(transitionId);
                ReachabilityNode targetState = new ReachabilityNode(rGraphModel.getNodes().size(), pNet.getPlaces());
                
                pGraph.updateGraph(pNet);
                updateReachGraphModel(transitionId, currentState, targetState);
            }
    }

    /*
     * Die Methode prüft ob für den aktuellen Zustand des Petrinetzes bereits ein
     * Knoten im Modell des Erreichbarkeitsgraphen existiert. Falls kein solcher
     * Knoten vorhanden ist wird er in Modell und Graph eingefügt. Wenn bereits ein
     * Knoten vorhanden ist, wird dieser als aktueller Knoten gesetzt. Weiter prüft
     * die Methode ob bereits eine Kante zwischen dem vorherigen Knoten und dem
     * aktuellen Knoten existiert und fügt eine Kante ein falls nicht vorhanden. Im
     * Erreichbarkeitsgraph werden der aktuelle Knoten und die Kante die zu diesem
     * führt hervor.
     */
    private void updateReachGraphModel(String transitionId, ReachabilityNode prevState, ReachabilityNode currentState) {
        Transition transition = pNet.getTransitions().get(transitionId);
        if (rGraphModel.findNode(currentState) == null) {
            rGraphModel.addNode(currentState);
            rGraph.addNode(currentState);
        } else {
            currentState = rGraphModel.findNode(currentState);
        }
        if (rGraphModel.findEdge(transitionId, prevState, currentState) == null) {
            ReachabilityEdge newEdge = new ReachabilityEdge(rGraphModel.getEdges().size(), transitionId, transition.getName(), prevState, currentState);
            rGraphModel.addEdge(newEdge);
            rGraph.addEdge(newEdge);
        }
        rGraph.highlightNode(currentState.getId());
        rGraph.toggleEdgeHighlight(rGraphModel.findEdge(transitionId, prevState, currentState));
    }

    /*
     * Die Methode findet den Knoten mit der Markierung auf die das Petrinetz
     * gesetzt werden soll, veranlasst den Zustandswechsel des Petrinetzes und
     * aktualisiert die Graphen.
     */
    private void setNetToClickedMarking(String id) {
        ReachabilityNode node = null;
        
        int count = 0;
        while (count < rGraphModel.getNodes().size()) {
            node = rGraphModel.getNodes().get(count);
            if (node.getId().equals(id)) 
                break;
            ++count;
            }
            pNet.jumpToMarking(node.getMarking());

            pGraph.updateGraph(pNet);
            rGraph.highlightNode(node.getId());
            rGraph.toggleEdgeHighlight(null);
    }

    /*
     * Die Methode prüft ob eine Stelle in dem Graph des Petrinetzes akutell
     * hervorgehoben ist.
     * return true wenn eine Stelle hervorgehoben ist, sonst false.
     */
    private boolean anyPlaceMarked() {
        for (String placeId : pNet.getPlaces().keySet()) {
            if (pGraph.isPlaceMarked(placeId)) return true;
        }
        return false;
    }
    
    /*
     * Die Methode führt die Beschränktheitsanalyse für alle in der übergebenen Map
     * vorhandenen Petrietze durch und gibt alle Ergebnisse formatiert in einer
     * Tabelle im Textfeld aus.
     */
    private void analyseStack(Map<File, PetrinetModel> pNetModels) {
        Map<File, PetrinetAnalysis> results = new TreeMap<>();
        
        for (File analysisFile : pNetModels.keySet()) {
            tab.outputText("Das Petrinetz '" + analysisFile.getName() + "' wird auf Beschränktheit analysiert...\n");
            PetrinetModel net = pNetModels.get(analysisFile);
            ReachabilityGraphModel reachModel = new ReachabilityGraphModel(net.getPlaces());
            PetrinetAnalysis analyser = analyseFile(net, reachModel);
            results.put(analysisFile, analyser);
        }
        int fileColWidth = "Dateiname ".length();
        int boundedColWidth = " beschränkt ".length();
        int pathColWidth = " Pfadlänge:Pfad; m, m'".length();
        int markingColWidth = 0;
        for (File analysisFile : results.keySet()) {
            fileColWidth = setFileColumnWidth(analysisFile, fileColWidth);
            PetrinetAnalysis analyser = results.get(analysisFile);
            if (analyser.isNetUnbounded()) {
                pathColWidth = setPathColumnWidth(analyser, pathColWidth);
                markingColWidth = setMarkingColumnWidth(analyser, markingColWidth);
            }
        }
        String fileColFormat = "%-" + fileColWidth + "s";
        String bounedColFormat = "|%-" + boundedColWidth + "s|";
        String pathColFormat = "%-" + pathColWidth + "s";
        String markingColFormat = null;
        if (markingColWidth > 0) {
            markingColFormat = "%-" + markingColWidth + "s";
        }
        setTable(fileColFormat, fileColWidth, bounedColFormat, boundedColWidth, pathColFormat, pathColWidth, markingColFormat, markingColWidth);
        
        for (File analysisFile : results.keySet()) {
            PetrinetAnalysis analysis = results.get(analysisFile);
            String row = setResultRow(analysisFile, analysis, fileColFormat, bounedColFormat, pathColFormat, markingColFormat);
            tab.outputText(row);
        }
    }
    
    /*
     * Die Methode analysiert ein übergebenes Petrinetz und
     * informiert über das Ergebnis im Textfeld des Tabs.
     * return Das erzeugte PetrinetzAnalyse-Objekt.
     */
    private PetrinetAnalysis analyseFile(PetrinetModel net, ReachabilityGraphModel reachModel) {
        PetrinetAnalysis analysis = new PetrinetAnalysis(net, reachModel);
    
        analysis.analyseBoundedness();
        String boundedness = "beschränkt"; 
        if (analysis.isNetUnbounded()) {
            boundedness = "unbeschränkt";
            tab.outputText("Das Petrinetz ist " + boundedness +".\n");
        } else {
            tab.outputText("Das Petrinetz ist " + boundedness + ".\n");
        }
        return analysis;
    }

    /*
     * Die Methode legt die Breite für die Spalte die den Namen der Petrinetze in
     * der Ergebnistabelle fest. 
     * return Die Breite der Spalte als Integer.
     */
    private int setFileColumnWidth(File file, int width) {
        if (file.getName().length() >= width) {
            return file.getName().length() + 1;
        } else {
            return width;
        }
    }

    /*
     * Die Methode legt die Breite für die Spalte die den Pfad bzw. die Anzahle der
     * Knoten und Kanten in der Ergebnistabelle darstellt fest.
     * return Die Breite der Spalte als Integer.
     */
    private int setPathColumnWidth(PetrinetAnalysis an, int pathColWidth) {
            String path = String.format("%2s", "");
            path = path.concat(":(");
            for (int i = 0; i < an.getReachEdges().size(); i++) {
                path = path.concat(an.getReachEdges().get(i).getEdgeName());
                if (i < an.getReachEdges().size() - 1) 
                    path = path.concat(", ");
            }
            path = path.concat(");");
            if (path.length() >= pathColWidth) {
                return path.length() + 1;
            } else {
                return pathColWidth;
            }
    }

    /*
     * Die Methode legt die Breite für die Spalte die eine Markierung in der Ergebnistabelle darstellt fest.
     * return Die Breite der Spalte als Integer.
     */
    private int setMarkingColumnWidth(PetrinetAnalysis analysis, int markingColWidth) {
        String decisionMarking = "(";
        for (int i = 0; i < analysis.getDecisionNode().getMarking().length; i++) {
            decisionMarking = decisionMarking.concat(Integer.toString(analysis.getDecisionNode().getMarking()[i]));
            if (i < analysis.getDecisionNode().getMarking().length - 1)
                decisionMarking = decisionMarking.concat("|");
        }
        decisionMarking = decisionMarking.concat("),");
        
        if (decisionMarking.length() >= markingColWidth) {
            return decisionMarking.length() + 1;
        } else {
            return markingColWidth;
        }
    }

    /*
     * Die Methode erzeugt eine formatierte Zeile mit dem Ergebnis der Analyse für
     * ein Petrinetz.
     * return Ein String der eine Zeile der Ergebnistabelle repräsentiert.
     */
    private String setResultRow(File file, PetrinetAnalysis an, String fileColFormat, String boundedColFormat,
            String pathColFormat, String markingColFormat) {
        int nodeCount;
        int edgeCount;
        String fileName = String.format(fileColFormat, file.getName());
        
        if (an.isNetUnbounded()) {
            nodeCount = an.getReachNodes().size();
            edgeCount = an.getReachEdges().size();
            String pathLength = String.format("%2s", Integer.toString(edgeCount));
            String bounded = String.format(boundedColFormat, " nein");
            
            String path = " " + pathLength + ":(";
            for (int i = 0; i < an.getReachEdges().size(); i++) {
                path = path.concat(an.getReachEdges().get(i).getEdgeName());
                if (i < an.getReachEdges().size() - 1) path = path.concat(", ");
            }
            path = path.concat(");");
            path = String.format(pathColFormat, path);
           
            String decisionMarking = " (";
            for (int i = 0; i < an.getDecisionNode().getMarking().length; i++) {
                decisionMarking = decisionMarking.concat(Integer.toString(an.getDecisionNode().getMarking()[i]));
                if (i < an.getDecisionNode().getMarking().length - 1) decisionMarking = decisionMarking.concat("|");
            }
            decisionMarking = decisionMarking.concat("),");
            decisionMarking = String.format(markingColFormat, decisionMarking);

            String unboundMarking = " (";
            for (int i = 0; i < an.getUnboundedNode().getMarking().length; i++) {
                unboundMarking = unboundMarking.concat(Integer.toString(an.getUnboundedNode().getMarking()[i]));
                if (i < an.getUnboundedNode().getMarking().length - 1) unboundMarking = unboundMarking.concat("|");
            }
            unboundMarking = unboundMarking.concat(")");
            unboundMarking = String.format(markingColFormat, unboundMarking);
            
            return fileName + bounded + path + decisionMarking + unboundMarking + "\n";
        } else {
            nodeCount = an.getReachModel().getNodes().size();
            edgeCount = an.getReachModel().getEdges().size();
            String nodes = String.format("%2s", Integer.toString(nodeCount));
            String edges = String.format("%2s", Integer.toString(edgeCount));
            String nodesAndEdges = " " + nodes + " / " + edges;
            String bounded = String.format(boundedColFormat, " ja");
            String path = String.format(pathColFormat, nodesAndEdges);
            return fileName + bounded + path + "\n";
        }
    }

    /*
     * Die Methode setzt den Tabellenkopf der Ergebnistabelle und gibt ihn im Textfeld aus.
     */
    private void setTable(String fileColFormat, int fileColWidth, String boundedColFormat, int boundedColWidth,
            String pathColFormat, int pathColWidth, String markingColFormat, int markingColWidth) {
        
        String fileColDashline = String.format(fileColFormat, createDashline(fileColWidth));
        String boundedColDashline = String.format(boundedColFormat, createDashline(boundedColWidth));
        String pathColDashline = String.format(pathColFormat, createDashline(pathColWidth));
       
        String markingColDashline = "";
        if (markingColFormat != null) {
            markingColDashline = String.format(markingColFormat, createDashline(markingColWidth));
        }
        String tableHeaderDashline = fileColDashline + boundedColDashline + pathColDashline + markingColDashline
                + markingColDashline + "\n";
        tab.outputText(tableHeaderDashline);

        String tableHeaderRow1 = String.format(fileColFormat, "").concat(String.format(boundedColFormat, ""))
                .concat(String.format(pathColFormat, " Knoten / Kanten bzw.")).concat("\n");
        tab.outputText(tableHeaderRow1);
        
        String tableHeaderRow2 = String.format(fileColFormat, "Dateiname")
                .concat(String.format(boundedColFormat, " beschränkt "))
                .concat(String.format(pathColFormat, " Pfadlänge:Pfad; m, m'")).concat("\n");
        tab.outputText(tableHeaderRow2);
        
        tab.outputText(tableHeaderDashline);
    }

    /*
     * Die Methode erzeugt eine Strichlinie der übergebenen Länge.
     * return Die Strichlinie als String.
     */
    private String createDashline(int length) {
        String line = "";
        for (int i = 0; i < length; i++)
            line = line.concat("-");
        return line;
    }

        /*
         * Die Methode setzt PetrinetModel und ReachabilityGraphModel auf die aktuelle
         * Anfangsmarkierung zurück.
         * Veranlasst dass die Graphen zurückgesetzt werden.
         */
        private void resetModels() {
                pNet.reset();
                rGraphModel.reset(pNet.getPlaces());
    
                resetGraphs();
        }

    /*
         * Die Methode setzte beide Graphen zurück und stellt diese in einem neuen Tab
         * dar. Der Inhalt des Textfeldes und es Statuslabels wird in den neuen Tab
         * übernommen.
         */
        private void resetGraphs() {
            setPetrinetGraph(pGraph.getClickedNode());
            setReachGraph();
            
            String text = tab.getTextArea().getText();
            String[] textArea = text.split("\n");
            String labelText = tab.getLabel().getText();
            
            tab = new TabView(this, pGraph, rGraph, file.getName());
            frameController.setSelectedTab(tab);
        
            for (String line : textArea) {
                tab.outputText(line.concat("\n"));
            }
            tab.getLabel().setText(labelText);
        }
}
