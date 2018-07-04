import defaultGrammars.CgfFileGrammar;
import structure.Grammar;
import structure.parse.ParseNode;
import structure.parse.ParseTree;
import structure.parse.Parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public class GrammarLoader {
    
    private HashMap<String, Grammar> hashMap;
    private Grammar cfgGrammar;
    private Parser parser;
    
    public GrammarLoader(){
        hashMap = new HashMap<>();
        cfgGrammar = new CgfFileGrammar();
        parser = new Parser(cfgGrammar);
    }
    
    public boolean loadGrammar(String s){
        ParseTree tree = parser.parse(s);
        if(tree == null) return false;
        
        return true;
    }
    
    public boolean loadGrammar(File f){
        try {
            ParseTree tree = parser.parse(f);
            if (tree == null) return false;
    
            
            
            return true;
        }catch (IOException e){
            System.err.println("File not found");
            return false;
        }
    }
    
    public Grammar convertParseTreeToGrammar(ParseTree g){
        if(!g.baseType().equals("grammar")) return null;
        
        
        return new Grammar();
    }
}
