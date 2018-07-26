package gui;

import interaction.GrammarLoader;
import structure.Grammars.ExtendedGrammar;
import structure.Grammars.Grammar;
import structure.syntacticObjects.SyntacticCategory;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class GrammarEditor extends JFrame implements DocumentListener, ActionListener {
    
    private static String baseString = "cat base(head){\n\thello world\n}";
    
    class CategoryColorConfig extends JPanel implements ActionListener{
        
        private SyntacticCategory category;
        private Color color;
        private JLabel label;
        private JButton colorPanel;
        private JColorChooser colorChooser;
        
        public CategoryColorConfig(SyntacticCategory category){
            super(new GridLayout(1, 2));
            this.category = category;
            label = new JLabel();
            label.setText(category.getName());
            colorChooser = new JColorChooser();
            color = Color.BLACK;
            colorPanel = new JButton();
            colorPanel.setBackground(color);
            colorPanel.setActionCommand("color");
            colorPanel.addActionListener(this);
            colorPanel.setSize(10,10);
            add(label);
            add(colorPanel);
            setVisible(true);
            color = null;
        }
    
    
        public SyntacticCategory getCategory() {
            return category;
        }
    
        public Color getColor() {
            return color;
        }
        
        public void chooseColor(){
            color = JColorChooser.showDialog(colorChooser, "color", color);
            colorPanel.setBackground(color);
            logger.writeEntryln("Category " + category.getName() + " set to color " + color.toString());
            validate();
        }
    
        /**
         * Invoked when an action occurs.
         *
         * @param e the event to be processed
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getActionCommand().equals("color")) chooseColor();
        }
    }
    
    private ExtendedGrammar grammar;
    private JPanel content;
    private JMenuBar menuBar;
    private JTextPane grammarEditor;
    private JPanel colorEditor;
    private JButton genColors;
    private Log logger;
    
    private GrammarLoader loader;
    private File file;
    
    public GrammarEditor(ExtendedGrammar g, Log logger){
        super("Grammar Editor");
        grammar = g;
        content = new JPanel(new BorderLayout());
        menuBar = new JMenuBar();
        
        grammarEditor = new JTextPane();
        grammarEditor.getStyledDocument().addDocumentListener(this);
        grammarEditor.setText(baseString);
        JScrollPane gSP = new JScrollPane(grammarEditor);
        colorEditor = new JPanel();
        colorEditor.setLayout(new BoxLayout(colorEditor, BoxLayout.Y_AXIS));
        
        JPanel sidePanel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(colorEditor);
        
        genColors = new JButton("Check Grammar");
        genColors.addActionListener(this);
        
        sidePanel.add(scrollPane, BorderLayout.CENTER);
        sidePanel.add(genColors, BorderLayout.PAGE_START);
        
        content.add(menuBar, BorderLayout.PAGE_START);
        content.add(gSP, BorderLayout.CENTER);
        content.add(sidePanel, BorderLayout.LINE_END);
        add(content);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setSize(1000, 800);
        scrollPane.setSize(100, content.getHeight());
        
        JMenu file = new JMenu("File");
        JMenuItem open = new JMenuItem("Open");
        open.addActionListener(this);
        JMenuItem save = new JMenuItem("Save");
        save.addActionListener(this);
        JMenuItem saveAs = new JMenuItem("Save as...");
        saveAs.addActionListener(this);
        file.add(open);
        file.add(save);
        file.add(saveAs);
        menuBar.add(file);
        
        //colorEditor.add(new JLabel("hello"));
        loader = new GrammarLoader();
        setVisible(true);
        this.logger = logger;
    }
    
    /**
     * Gives notification that there was an insert into the document.  The range given by the DocumentEvent bounds the
     * freshly inserted region.
     *
     * @param e the document event
     */
    @Override
    public void insertUpdate(DocumentEvent e) {
        System.out.println("Insert");
        //onDocChange();
    }
    
    /**
     * Gives notification that a portion of the document has been removed.  The range is given in terms of what the view
     * last saw (that is, before updating sticky positions).
     *
     * @param e the document event
     */
    @Override
    public void removeUpdate(DocumentEvent e) {
        System.out.println("Removed");
       // onDocChange();
    }
    
    /**
     * Gives notification that an attribute or set of attributes changed.
     *
     * @param e the document event
     */
    @Override
    public void changedUpdate(DocumentEvent e) {
        System.out.println("Changed");
    }
    
    public void onDocChange(){
        if(grammarEditor.getText().length() == 0) return;
        ExtendedGrammar g = (ExtendedGrammar) loader.loadGrammar(grammarEditor.getText(), new ExtendedGrammar(), true);
        if(g == null){
            logger.writeEntryln("No grammar present");
            return;
        }
        
        
        g.printGrammar();
        colorEditor.removeAll();
        for (SyntacticCategory allCategory : g.getAllCategories()) {
            System.out.println("Adding new CCC for " + allCategory.getName());
            colorEditor.add(new CategoryColorConfig(allCategory));
        }
        
        System.out.println("Total CCC: " + colorEditor.getComponentCount());
        validate();
    }
    
    /**
     * Invoked when an action occurs.
     *
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(e.getActionCommand());
        if(e.getActionCommand().equals("Check Grammar")){
            onDocChange();
        }
        if(e.getActionCommand().equals("Open")){
            JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
            int val = chooser.showOpenDialog(new JFrame());
            if(val == JFileChooser.APPROVE_OPTION){
                file = chooser.getSelectedFile();
                logger.writeEntryln("Load File: " + file.toPath().toString());
                try {
                    grammarEditor.setText(new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8));
                }catch (IOException ex){
                    ex.printStackTrace();
                }
            }
        }
        if(e.getActionCommand().equals("Save") && file != null && file.canWrite()){
        
        }
    }
}
