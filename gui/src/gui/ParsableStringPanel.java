package gui;

import structure.parse.ParseTree;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.util.HashMap;

public class ParsableStringPanel extends JTextPane {
    
    
    private StyledDocument styledDocument;
    /**
     * Creates a new <code>JTextPane</code>.  A new instance of
     * <code>StyledEditorKit</code> is
     * created and set, and the document model set to <code>null</code>.
     */
    public ParsableStringPanel() {
        super();
        styledDocument = getStyledDocument();
        
        Style base = addStyle("Base", null);
        StyleConstants.setForeground(base, Color.GRAY);
        StyleConstants.setFontSize(base, 20);
        
        try {
            styledDocument.insertString(styledDocument.getLength(), "Hello World", base);
        } catch (BadLocationException e){
            e.printStackTrace();
        }
        
        
    }
    
    public void apply(ParseTree tree, HashMap<String, Style> styleMap){
    
    }
    
    public void write(String str){
        try {
            styledDocument.insertString(styledDocument.getLength(), str, styledDocument.getStyle("Base"));
        }catch (BadLocationException e){
            e.printStackTrace();
        }
    }
    
    public void writeln(String str){
        write(str + "\n");
    }
    public void writeln(){
        write("\n");
    }
    
}
