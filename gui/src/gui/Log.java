package gui;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;

public class Log extends PrintStream {
    
    private JTextPane textField;
    private JFrame frame;
    private static File logFile;
    
    static {
        logFile = new File("log.txt");
        try {
            logFile.createNewFile();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    
    public Log() throws IOException{
        super(logFile);
        frame = new JFrame("Log");
       
        
        
        textField = new JTextPane();
        textField.setEditable(false);
        textField.getStyledDocument().addStyle("base", null);
        ((DefaultCaret) textField.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane scrollPane = new JScrollPane(textField);
        frame.add(scrollPane);
        frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setVisible(true);
        //System.setOut(this);
        
    }
    
    public void writeEntryln(String str){
        try {
            textField.getStyledDocument().insertString(
                    textField.getStyledDocument().getLength(),
                    String.format("[%tT %tD] %s\n", new Date().getTime(), new Date().getTime(), str),
                    textField.getStyle("base")
            );
        }catch (BadLocationException e){
            e.printStackTrace();
        }
    }
    
    public void writeEntryln(Object o){
        writeEntryln(o.toString());
    }
    
    /**
     * Prints an object.  The string produced by the {@link String#valueOf(Object)} method is translated into bytes
     * according to the platform's default character encoding, and these bytes are written in exactly the manner of the
     * {@link #write(int)} method.
     *
     * @param obj The {@code Object} to be printed
     * @see Object#toString()
     */
    @Override
    public void print(Object obj) {
        writeEntryln(obj);
    }
    
    /**
     * Terminates the current line by writing the line separator string.  The line separator string is defined by the
     * system property {@code line.separator}, and is not necessarily a single newline character ({@code '\n'}).
     */
    @Override
    public void println() {
        writeEntryln("\n");
    }
    
    /**
     * Prints a String and then terminate the line.  This method behaves as though it invokes {@link #print(String)} and
     * then {@link #println()}.
     *
     * @param x The {@code String} to be printed.
     */
    @Override
    public void println(String x) {
        writeEntryln(x);
    }
}
