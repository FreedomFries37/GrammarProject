package gui;

import interaction.*;
import interaction.defaultGrammars.CgfFileGrammar;
import structure.Grammars.ExtendedGrammar;
import structure.Grammars.Grammar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class MasterWindow extends JFrame implements ActionListener {
    
    private ParsableStringPanel stringPanel;
    private JProgressBar progressBar;
    private Grammar grammar;
    private boolean extended;
    private Log log;
    
    public MasterWindow(){
        super("Grammar View");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1200,900);
        setResizable(false);
        JPanel basePanel = new JPanel(new BorderLayout());
        JMenuBar menu = generateMenu();
        
        stringPanel = new ParsableStringPanel();
        JScrollPane scrollPane = new JScrollPane(stringPanel);
        basePanel.add(scrollPane, BorderLayout.CENTER);
        basePanel.add(menu, BorderLayout.PAGE_START);
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        basePanel.add(bottomPanel, BorderLayout.PAGE_END);
        //bottomPanel.setPreferredSize(new Dimension(getSize().width, 75));
        JButton apply = new JButton("Apply");
        apply.addActionListener(this);
        progressBar = new JProgressBar(0, 100);
        
        //bottomPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        bottomPanel.add(apply);
        bottomPanel.add(progressBar);
    
    
    
        GrammarLoader grammarLoader = new GrammarLoader();
        Grammar cfgGrammar = cfgGrammar = new CgfFileGrammar();
        cfgGrammar = grammarLoader.loadGrammar(new File("cfgGrammarExtendedBootstrapper.ccfg"), cfgGrammar);
        cfgGrammar = new ExtendedGrammar(cfgGrammar,
            "{",
                    "}",
                    ":",
                    "<",
                    ">",
                    ":=",
                    "->",
                    "[",
                    "]",
                    "(",
                    ")",
                    "\t",
                    ";",
                    ",",
                    "\\",
                    "\""
        );
        cfgGrammar = grammarLoader.loadTokenGrammar(new File("cfgGrammarExtended.eccfg"), cfgGrammar);
    
        setGrammar(cfgGrammar);
        add(basePanel);
        setVisible(true);
        
        try {
            log = new Log();
        }catch (IOException e){
            e.printStackTrace();
        }
        
        new GrammarEditor((ExtendedGrammar) grammar, log);
    }
    
    public void setGrammar(Grammar g){
        grammar = g;
        if(g.getClass().equals(ExtendedGrammar.class)){
            extended = true;
        }else{
            extended = false;
        }
    }
    
    private JMenuBar generateMenu(){
        JMenuBar output = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem fileOpen = new JMenuItem("Open");
        fileOpen.addActionListener(this);
        
        fileMenu.add(fileOpen);
        
        output.add(fileMenu);
        return output;
    }
    
    /**
     * Invoked when an action occurs.
     *
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        log.writeEntryln(e);
        switch (e.getActionCommand()){
            case "Apply":{
                progressBar.setValue(0);
                if(grammar != null){
                    ParseThread p;
                    if(!extended) p = new ParseThread(new Parser(grammar), stringPanel.getText());
                    else p = new ParseThread(new TokenParser((ExtendedGrammar) grammar), stringPanel.getText());
                    new Thread(p).start();
                    while (!p.isCompleted()){
                        log.writeEntryln(String.format("Progress: %g", p.progress()));
                        //System.out.println(String.format("Progress: %g", p.progress()));
                        progressBar.setValue((int) p.progress() * 100);
                    }
                    log.writeEntryln(String.format("Progress: %g", p.progress()));
                    progressBar.setValue((int) p.progress() * 100);
                }
            }
            break;
        }
    }
}
