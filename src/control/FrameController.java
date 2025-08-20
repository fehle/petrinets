package control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.util.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import model.*;
import view.*;

/**
 * Die Klasse repräsentiert den Controller für den Haupt-Frame der
 * Anwendung.
 * 
 * Sie verarbeitet alle Interaktionen zwischen dem Haupt-Frame und dem User. Dies
 * umfasst alle Interaktionen mit Menüs, der ToolBar und der Tableiste.
 * 
 * @author Fabian Ehlers
 */
public class FrameController implements ActionListener {
    
    private PetrinetMainFrame frame;
    private FileNameExtensionFilter filter;
    
    /**
     * Der Konstruktor erzeugt einen Controller für den PetrinetMainFrame
     * 
     * @param frame Eine Referenz auf das Hauptfenster.
     */
    public FrameController(PetrinetMainFrame frame) {
        this.frame = frame;
        this.filter = new FileNameExtensionFilter("PNML - Petri Net Markup Language (.pnml)", "pnml");
    }
    
    /**
     * Die Methode verarbeitet alle Events die in dem PetrinetMainFrame auftreten.
     * <p>
     * Aktionen die nur in Verbindung mit einem geladenen Petrinetz nutzbar sind
     * werden ignoriert wenn in dem aktuell im PetrinetMainFrame ausgewählten Tab
     * eine Stapelanalyse dargestellt wird.
     * </p>
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if ("open".equals(e.getActionCommand()))
            openFile();
        else if ("tab".equals(e.getActionCommand()))
            addTab();
        else if ("analyseStack".equals(e.getActionCommand()))
            analyseStack();
        else if ("exit".equals(e.getActionCommand()))
            System.exit(0);
        
        else if (!frame.getSelectedTabView().getTabController().isStackAnalysisTab()) {
            if ("reload".equals(e.getActionCommand()))
                reloadFile();
            else if ("openPrev".equals(e.getActionCommand())) 
                openPrevFile();
            else if ("openNext".equals(e.getActionCommand())) 
                openNextFile();
            else if ("analyse".equals(e.getActionCommand()))
                analyse();
            else if ("incToken".equals(e.getActionCommand())) 
                increaseToken();
            else if ("decToken".equals(e.getActionCommand())) 
                decreaseToken();
            else if ("reset".equals(e.getActionCommand())) 
                resetPetrinet();
            else if ("delete".equals(e.getActionCommand())) 
                deleteReachGraph();
        }
    }

    /**
     * Die Methode parst eine PNML-Datei und gibt ein PetrinetModel zurück.
     * 
     * @param pnml Eine Referenz auf die Datei die geparst werden soll.
     * @return Eine Referenz auf das erzeugte PetrinetModel.
     */
    protected PetrinetModel parsePNMLFile(File pnml) {
        SimplePetrinetParser parser = new SimplePetrinetParser(pnml, new PetrinetModel());
        
        return parser.parseFile();
    }
    
    /**
     * Die Methode fügt der Tableiste des PetrinetMainFrame eine neue TabView hinzu
     * und wechselt die Ansicht zu dieser.
     * 
     * @param tab Eine Referenz auf die TabView.
     */
    protected void setTab(TabView tab) {
        frame.getTabbedPane().add(tab);
        frame.getTabbedPane().setSelectedComponent(tab);
        if (!frame.isControlEnabled()) {
            frame.enableMenuAndToolBar();
        }
    }
    
    /**
     * Die Methode ersetzt die aktuelle TabView in der Tableiste des
     * PetrinetMainFrame durch eine neue TabView.
     * 
     * @param tab Eine Referenz auf die TabView.
     */
    protected void setSelectedTab(TabView tab) {
        int index = frame.getTabbedPane().getSelectedIndex();
        frame.getTabbedPane().setComponentAt(index, tab);
        if (!frame.isControlEnabled()) {
            frame.enableMenuAndToolBar();
        }
    }
    
    /**
     * Die Methode setzt für eine TabView den Namen und die Steuerung in
     * der Tableiste des PetrinetMainFrame.
     * 
     * @param tab    Eine Referenz auf die TabView.
     * @param header Eine Referenz auf das JPanel
     */
    protected void setTabHeader(TabView tab, JPanel header) {
        frame.getTabbedPane().setTabComponentAt(frame.getTabbedPane().indexOfComponent(tab), header);
    }
    
    /**
     * Die Methode entfernt eine TabView von der Tableiste des PetrinetMainFrame.
     * <p>
     * Wird aufgerufen von {@link TabController}.
     * </p>
     * @param tab Eine Referenz auf die TabView.
     */
    protected void removeTab(TabView tab) {
        frame.getTabbedPane().remove(tab);
        if (frame.getTabbedPane().getTabCount() == 0) {
            frame.disableMenuAndToolBar();
        }
    }
        
    /*
     * Die Methode öffnet eine Datei. Wenn noch kein Tab in der Tableiste vorhanden
     * ist oder der aktuelle Tab eine Stapelverarbeitung anzeigt, öffnet sie einen
     * neuen Tab. Sonst öffnet sie die Datei in dem gerade aktiven Tab.
     */
    private void openFile() {
        if (frame.getTabbedPane().getTabCount() == 0
                || frame.getSelectedTabView().getTabController().isStackAnalysisTab()) {
            addTab();
        } else {
            File file = chooseFile(getOpenFile());
            
            if (file != null) {
                updateTab(file);
            }
        }
    }
    
    /*
     * Die Methode öffnet eine Datei in einem neuen Tab.
     */
    private void addTab() {
        File file = chooseFile(null);
        if (file != null) {
            try {
                PetrinetModel pNet = parsePNMLFile(file);
                ReachabilityGraphModel rgModel = new ReachabilityGraphModel(pNet.getPlaces());
                new TabController(this, file, pNet, rgModel);
            } catch (IllegalArgumentException iae) {
                JOptionPane.showMessageDialog(frame, "Das angegebene Petrinetz kann nicht geladen werden. \n"
                        + iae.getMessage() + "\n" + "Bitte die Eingabedatei prüfen.", "Fehler",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /*
     * Die Methode öffnet eine neue Datei in einem vorhandenen Tab.
     */
    private void updateTab(File file) {
        try {
            PetrinetModel pNet = parsePNMLFile(file);
            ReachabilityGraphModel rgModel = new ReachabilityGraphModel(pNet.getPlaces());
            TabController tabController = frame.getSelectedTabView().getTabController();
            tabController.reloadTab(pNet, rgModel, file);
        } catch (IllegalArgumentException iae) {
            JOptionPane.showMessageDialog(frame, "Das angegebene Petrinetz kann nicht geladen werden. \n"
                    + iae.getMessage() + "\n" + "Bitte die Eingabedatei prüfen.", "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /*
     * Die Methode zurück welche Datei in dem aktuellen Tab geöffnet ist.
     */
    private File getOpenFile() {
        return frame.getSelectedTabView().getFile();
    }
    
    /*
     * Die Methode öffnet einen Datei-Auswahldialog zum Öffnen einer Datei.
     * Sie gibt ein Fehlerfenster aus wenn eine Datei mit anderer Endung als '.pnml' ausgewählt wird.
     * return die Datei wenn eine gültige Datei ausgewählt wurde, sonst null.
     */
    private File chooseFile(File file) {
        JFileChooser fileChooser = new JFileChooser("../ProPra-WS23-Basis/Beispiele");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setFileFilter(filter);
        
        if (file != null) {
            fileChooser.setCurrentDirectory(file);
        }
        int returnVal = fileChooser.showOpenDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            if (fileChooser.getSelectedFile().getName().endsWith(".pnml")) {
                return fileChooser.getSelectedFile();
            } else {
                JOptionPane.showMessageDialog(frame, "Es können nur PNML-Dateien (.pnml) geöffnet werden.",
                        "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        } 
        return null;
    }
    
    /*
     * Die Methode öffnet einen Datei-Auswahldialog zum Öffnen mehrerer Dateien.
     * Sie gibt ein Fehlerfesnter aus wenn eine Datei mit anderer Endung als '.pnml' ausgewählt wird.
     * return ein Array mit allen ausgewählten Dateien wenn alle Dateien gültig sind, sonst null.
     */
    private File[] chooseMultipleFiles() {
        JFileChooser fileChooser = new JFileChooser("../ProPra-WS23-Basis/Beispiele");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setFileFilter(filter);
        fileChooser.setMultiSelectionEnabled(true);
        int returnVal =fileChooser.showOpenDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File[] files = fileChooser.getSelectedFiles();
            int size = fileChooser.getSelectedFiles().length;
            for (int i = 0; i < size; i++) {
                if (!files[i].getName().endsWith(".pnml")) {
                    JOptionPane.showMessageDialog(frame, "Es können nur PNML-Dateien (.pnml) geöffnet werden.",
                            "Fehler", JOptionPane.ERROR_MESSAGE);
                    return null;
                }
            }
            Arrays.sort(files);
            return files;
        }
        return null;
    }
    
    /*
     * Die Methode liest die Dateien für die Stapelanalyse ein und erstellt für jede
     * Datei ein Petrinetz. Diese werden in einer Map an den TabController
     * übergeben.
     */
    private void analyseStack() {
        File[] files = chooseMultipleFiles();
        
        if (files != null) {
            String path = files[0].getParent();
            Map<File, PetrinetModel> pNetModels = new TreeMap<>();
            
            try {
                for (File file : files) {
                    PetrinetModel pNet = parsePNMLFile(file);
                    pNetModels.put(file, pNet);
                }
                if (frame.getSelectedTabView() == null
                        || !frame.getSelectedTabView().getTabController().isStackAnalysisTab()) {
                    new TabController(this, pNetModels, path);
                } else {
                    frame.getSelectedTabView().getTabController().updateStackAnalyseTab(pNetModels, path);
                }
            } catch (IllegalArgumentException iae) {
                JOptionPane.showMessageDialog(frame, "Das angegebene Petrinetz kann nicht geladen werden. \n"
                        + iae.getMessage() + "\n" + "Bitte die Eingabedatei prüfen.", "Fehler",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /*
     * Die Methode öffnet eine Datei in seinem Tab neu.
     */
    private void reloadFile() {
        File file = getOpenFile();
        
        updateTab(file);
    }

    /*
     * Die Methode öffnet in dem aktuellen Tab die Datei, die in dem Verzeichnis der
     * geöffneten Datei, alphabetisch vor ihr steht.
     */
    private void openPrevFile() {
        File[] directory = getDirectory();
        if (checkDirectorySize(directory) != 1) {
            int index = findIndex(directory);
            if (index > 0) {
                updateTab(directory[index - 1]);
            } else {
                int returnVal = JOptionPane.showConfirmDialog(frame,
                        "Anfang des Verzeichnisses erreicht. Die letzte Datei des Verzeichnisses wird geladen",
                        "Wollen Sie fortfahren?", JOptionPane.YES_NO_OPTION);
                if (returnVal == JOptionPane.YES_OPTION) {
                    updateTab(directory[directory.length - 1]);
                }
            }
        }
    }
    
    /*
     * Die Methode öffnet in dem aktuellen Tab die Datei, die in dem Verzeichnis der
     * geöffneten Datei, alphabetisch hinter ihr steht.
     */
    private void openNextFile() {
        File[] directory = getDirectory();
        if (checkDirectorySize(directory) != 1) {
            int index = findIndex(directory);
            if (index < directory.length - 1) {
                updateTab(directory[index + 1]);
            } else {
                int returnVal = JOptionPane.showConfirmDialog(frame,
                        "Ende des Verzeichnisses erreicht. Die erste Datei des Verzeichnisses wird geladen",
                        "Wollen Sie fortfahren?", JOptionPane.YES_NO_OPTION);
                if (returnVal == JOptionPane.YES_OPTION) {
                    updateTab(directory[0]);
                }
            }
        }
    }
    
    /*
     * Die Methode gibt ein alphabetisch sortiertes File-Array zurück in dem alle
     * pnml-Dateien des Verzeichnisses der aktuell geöffneten Datei enthalten sind.
     */
    private File[] getDirectory() {
        File directory = getOpenFile().getParentFile();
        FileFilter ff = file -> !file.isDirectory() && file.getName().endsWith(".pnml");
        File[] files = directory.listFiles(ff);
        Arrays.sort(files);
        return files;
    }
    
    /*
     * Die Methode prüft die Größe des Verzeichnisses. Wenn das Verzeichnis nur eine
     * Datei enthält kann eine Datei eines anderen Verzeichnisses geöffnet werden.
     * return die Anzahl der Dateien des Verzeichnisses.
     */
    private int checkDirectorySize(File[] dir) {
        if (dir.length == 1) {
            int returnVal = JOptionPane.showConfirmDialog(frame,
                    "Das aktuelle Verzeichnis enthält nur die bereits geöffnete Datei. Wollen Sie ein anderes Verzeichnis auswählen?",
                    "Datei öffnen", JOptionPane.YES_NO_OPTION);
            if (returnVal == JOptionPane.YES_OPTION) {
                openFile();
            }
        }
        return dir.length;
    }
    
    /*
     * Die Methode gibt zurück an welcher Stelle des Verzeichnisses die aktuell
     * geöffnete Datei liegt.
     */
    private int findIndex(File[] files) {
        int index = 0;
        for (int i = 0; i < files.length; i++) {
            if (files[i].equals(getOpenFile())) {
                index = i;
                break;
            }
        }
        return index;
    }
    
    /*
     * Die Methode veranlasst die Beschränktheitsanalyse des aktuell geöffneten
     * Petrinetzes und gibt das Ergebnis in einem Mitteilungsdialog aus.
     */
    private void analyse() {
        String boundedness = frame.getSelectedTabView().getTabController().analyse();
        
        JOptionPane.showMessageDialog(frame, "Das Petrinetz ist " + boundedness);
    }
    
    /*
     * Die Methode veranlasst dass die Anzahl der Marken einer Stelle erhöht wird.
     */
    private void increaseToken() {
        frame.getSelectedTabView().getTabController().updateInitialMarking("+");
    }

    /*
     * Die Methode veranlasst dass die Anzahl der Marken einer Stelle verringert
     * wird.
     */
    private void decreaseToken() {
        frame.getSelectedTabView().getTabController().updateInitialMarking("-");
    }

    /*
     * Die Methode veranlasst dass die Modelle und Graphen für das Petrinetz und den
     * Erreichbarkeitgraphen zurückgesetzt werden.
     */
    private void deleteReachGraph() {
        frame.getSelectedTabView().getTabController().reset();
    }
    
    /*
     * Die Methode veranlasst dass Petrinetz Modell und Graph zurückgesetzt werden.
     */
    private void resetPetrinet() {
        frame.getSelectedTabView().getTabController().resetPetrinet();
    }
}
