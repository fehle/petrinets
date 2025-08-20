package view;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;

import javax.swing.*;

import control.FrameController;

/**
 * Die Klasse repräsentiert den Frame für dieses Programm.
 * <p>
 * Der PetrinetMainFrame erweitert einen JFrame und besteht aus einer Menüleiste,
 * einer Toolbar und einer Tableiste.
 * </p>
 * 
 * @author Fabian Ehlers
 */
public class PetrinetMainFrame extends JFrame {
    
    private FrameController frameController;
    
    private JMenuBar menuBar;
    private JToolBar toolBar;
    private JPanel toolBarPanel;
    private JTabbedPane tabPane;
    
    private JMenuItem reloadFile;
    
    private JButton openPrev;
    private JButton openNext;
    private JButton analyseButton;
    private JButton incTokenButton;
    private JButton decTokenButton;
    private JButton resetPetrinetButton;
    private JButton deleteGraphsButton;
    
    private boolean isControlEnabled = false;
    
    /**
     * Der Kontruktor erzeugt das Hauptfenster mit einer Größe abhängig von der
     * Bildschirmauflösung und erzeugt alle Komponenten und fügt sie dem Frame
     * hinzu.
     * 
     * @param titel Der Titel des Fensters.
     */
    public PetrinetMainFrame(String titel) {
        super(titel);
        frameController = new FrameController(this);
        
        ImageIcon titleIcon = new ImageIcon(this.getClass().getResource("/icons/Petri-Dish-Flat.48.png"));
        setIconImage(titleIcon.getImage());
        
        setLayout(new BorderLayout());
        
        initMenuBar();
        initToolBar();
        
        add(toolBarPanel, BorderLayout.NORTH);
        
        tabPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        tabPane.setFocusable(false);

        add(tabPane, BorderLayout.CENTER);
        
        double heightPerc = 0.7;
        double aspectRatio = 16.0 / 10.0;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int h = (int) (screenSize.height * heightPerc);
        int w = (int) (h * aspectRatio);
        setBounds((screenSize.width - w) / 2, (screenSize.height - h) / 2, w, h);
        
        setLocationRelativeTo(null);
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        
    }
    
    /**
     * Die Methode aktiviert alle Menüeinträge und Buttons für deren Verwendung eine
     * geladene Datei notwendig ist.
     */
    public void enableMenuAndToolBar() {
        reloadFile.setEnabled(true);
        
        openPrev.setEnabled(true);
        openNext.setEnabled(true);
        analyseButton.setEnabled(true);
        incTokenButton.setEnabled(true);
        decTokenButton.setEnabled(true);
        resetPetrinetButton.setEnabled(true);
        deleteGraphsButton.setEnabled(true);
        
        isControlEnabled = true;
    }
    
    /**
     * Die Methode deaktiviert alle Menüeinträge und Buttons für deren Verwendung
     * eine geladene Datei notwendig ist.
     */
    public void disableMenuAndToolBar() {
        reloadFile.setEnabled(false);
    
        openPrev.setEnabled(false);
        openNext.setEnabled(false);
        analyseButton.setEnabled(false);
        incTokenButton.setEnabled(false);
        decTokenButton.setEnabled(false);
        resetPetrinetButton.setEnabled(false);
        deleteGraphsButton.setEnabled(false);
        
        isControlEnabled = false;
    }
    
    /**
     * Die Methode gibt zurück ob die Menüeinträge und Buttons für deren Verwendung
     * eine geldadene Datei aktiviert sind.
     * 
     * @return {@code true} wenn aktiviert, {@code false} wenn deaktiviert.
     */
    public boolean isControlEnabled() {
        return isControlEnabled;
    }
    
    /**
     * Die Methode gibt das JTabbedPane zurück.
     * 
     * @return Eine Referenz auf die Tableiste.
     */
    public JTabbedPane getTabbedPane() {
        return this.tabPane;
    }
    
    /**
     * Die Methode gibt die aktuell in der Tableiste ausgewählte TabView zurück.
     * 
     * @return Eine Referenz auf den Tab.
     */
    public TabView getSelectedTabView() {
        return (TabView) tabPane.getSelectedComponent();
    }

    /**
     * Die Methode erzeugt ein ImageIcon und gibt es zurück.
     * 
     * @param path Der Pfad zu einer Bilddatei.
     * @return Das ImageIcon oder {@code null} wenn die Datei nicht gefunden wurde.
     */
    public static ImageIcon createImageIcon(String path) {
        URL imgURL = PetrinetMainFrame.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Folgende Datei konnte nicht gefunden werden: " + path);
            return null;
        }
    }

    /*
     * Die Methode erzeugt und initialisiert die Menüleiste und fügt sie dem Fenster
     * hinzu.
     * 
     * Allen Menüeinträgen bekommen ActionListener.
     */
    private void initMenuBar() {
            menuBar = new JMenuBar();
            menuBar.setOpaque(true);
            menuBar.setBackground(Color.GRAY);
            
            JMenu fileMenu = new JMenu("Datei");
    
            ImageIcon openFileIcon = createImageIcon("/icons/document-open.16.png");
            JMenuItem openFile = new JMenuItem("Öffnen", openFileIcon);
            openFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
            openFile.setActionCommand("open");
            openFile.addActionListener(frameController);
            fileMenu.add(openFile);
            
            ImageIcon openTabIcon = createImageIcon("/icons/open-tab.16.png");
            JMenuItem openFileTab = new JMenuItem("Öffnen in neuem Tab", openTabIcon);
            openFileTab.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
            openFileTab.setActionCommand("tab");
            openFileTab.addActionListener(frameController);
            fileMenu.add(openFileTab);
            
            ImageIcon reloadIcon = createImageIcon("/icons/document-revert.16.png");
            reloadFile = new JMenuItem("Neu Laden", reloadIcon);
            reloadFile.setEnabled(false);
            reloadFile.setActionCommand("reload");
            reloadFile.addActionListener(frameController);
            fileMenu.add(reloadFile);
    
            fileMenu.addSeparator();
            
            ImageIcon stackAnalysisIcon = createImageIcon("/icons/wizard-stack.16.png");
            JMenuItem analyse = new JMenuItem("Analyse mehrerer Dateien", stackAnalysisIcon);
            analyse.setActionCommand("analyseStack");
            analyse.addActionListener(frameController);
            fileMenu.add(analyse);
            
            fileMenu.addSeparator();
    
            ImageIcon exitIcon = createImageIcon("/icons/window-close.16.png");
            JMenuItem exit = new JMenuItem("Beenden", exitIcon);
            exit.addActionListener(e -> System.exit(0));
            fileMenu.add(exit);
            
            menuBar.add(fileMenu);
            
            JMenu helpMenu = new JMenu("Hilfe");
            ImageIcon infoIcon = createImageIcon("/icons/system-info.16.png");
            JMenuItem info= new JMenuItem("Info", infoIcon);
            
            info.addActionListener(e -> {
                JPanel infoPanel = new JPanel(new BorderLayout());
                JLabel version = new JLabel("Aktuelle Java-Version: " + System.getProperty("java.version"));
                ImageIcon javaIcon = createImageIcon("/icons/java.32.png");
                version.setIcon(javaIcon);
                JLabel directory = new JLabel("Aktuelles Arbeitsverzeichnis: " + System.getProperty("user.dir"));
                ImageIcon dirIcon = createImageIcon("/icons/directory.32.png");
                directory.setIcon(dirIcon);
                infoPanel.add(version, BorderLayout.NORTH);
                infoPanel.add(directory, BorderLayout.SOUTH);
                JOptionPane.showMessageDialog(this, infoPanel, "Info", JOptionPane.PLAIN_MESSAGE);
            });
            helpMenu.add(info);
            
            menuBar.add(helpMenu);
            
            this.setJMenuBar(menuBar);
        }

        /*
         * Die Methode erzeugt und initialisiert die Toolbar in einem neuen Panel und
         * fügt dies dem Fenster hinzu.
         * 
         * Alle Buttons bekommen ActionListener.
         */
    private void initToolBar() {
            
            toolBar = new JToolBar();
            toolBar.setOpaque(true);
            toolBar.setBackground(Color.LIGHT_GRAY);
            
            ImageIcon openPrevIcon = createImageIcon("/icons/go-previous.24.png");
            openPrev = new JButton(openPrevIcon);
            openPrev.setEnabled(false);
            openPrev.setToolTipText("Vorherige Datei - Öffnet die vorherige Datei des aktuellen Verzeichnisses.");
            openPrev.setFocusable(false);
            openPrev.setActionCommand("openPrev");
            openPrev.addActionListener(frameController);
            toolBar.add(openPrev);
            
            ImageIcon openNextIcon = createImageIcon("/icons/go-next.24.png");
            openNext = new JButton(openNextIcon);
            openNext.setEnabled(false);
            openNext.setToolTipText("Nächste Datei - Öffnet die nächste Datei des aktuellen Verzeichnisses.");
            openNext.setFocusable(false);
            openNext.setActionCommand("openNext");
            openNext.addActionListener(frameController);
            toolBar.add(openNext);
            
            ImageIcon analyseIcon = createImageIcon("/icons/wizard.24.png");
            analyseButton = new JButton(analyseIcon);
            analyseButton.setEnabled(false);
            analyseButton.setToolTipText("Analyse - Führt eine Beschränktheitsanalyse für das aktuelle Petrinetz durch.");
            analyseButton.setFocusable(false);
            analyseButton.setActionCommand("analyse");
            analyseButton.addActionListener(frameController);
            toolBar.add(analyseButton);
            
            ImageIcon incTokenIcon = createImageIcon("/icons/add.24.png");
            incTokenButton = new JButton(incTokenIcon);
            incTokenButton.setEnabled(false);
            incTokenButton.setToolTipText("Marke Plus - Erhöht die Token der aktuell ausgewählten Stelle um 1.");
            incTokenButton.setFocusable(false);
            incTokenButton.setActionCommand("incToken");
            incTokenButton.addActionListener(frameController);
            toolBar.add(incTokenButton);
            
            ImageIcon decTokenIcon = createImageIcon("/icons/remove.24.png");
            decTokenButton = new JButton(decTokenIcon);
            decTokenButton.setEnabled(false);
            decTokenButton.setToolTipText("Marke Minus - Verringert die Token der aktuell ausgewählten Stelle um 1.");
            decTokenButton.setFocusable(false);
            decTokenButton.setActionCommand("decToken");
            decTokenButton.addActionListener(frameController);
            toolBar.add(decTokenButton);
            
            ImageIcon resetPetrinetIcon = createImageIcon("/icons/clear-history.24.png");
            resetPetrinetButton = new JButton(resetPetrinetIcon);
            resetPetrinetButton.setEnabled(false);
            resetPetrinetButton.setToolTipText("Reset Petrinetz - Setzt das Petrinetz auf die aktuelle Anfangsmarkierung zurück.");
            resetPetrinetButton.setFocusable(false);
            resetPetrinetButton.setActionCommand("reset");
            resetPetrinetButton.addActionListener(frameController);
            toolBar.add(resetPetrinetButton);
            
            ImageIcon deleteGraphsIcon = createImageIcon("/icons/edit-clear.24.png");
            deleteGraphsButton = new JButton(deleteGraphsIcon);
            deleteGraphsButton.setEnabled(false);
            deleteGraphsButton.setToolTipText(
                    "Lösche EG - Setzt den Erreichbarkeitsgraph und das Petrinetz auf die aktuelle Anfangsmarkierung zurück.");
            deleteGraphsButton.setFocusable(false);
            deleteGraphsButton.setActionCommand("delete");
            deleteGraphsButton.addActionListener(frameController);
            toolBar.add(deleteGraphsButton);
    
            this.toolBarPanel = new JPanel(new BorderLayout());
            toolBarPanel.add(toolBar, BorderLayout.CENTER);
        }
}
