package structure;

import structure.syntacticObjects.SyntacticCategory;
import structure.syntacticObjects.SyntacticObject;
import structure.syntacticObjects.tokenBased.TokenRegexTerminal;
import structure.syntacticObjects.tokenBased.TokenTerminal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TokenGrammar extends Grammar {
    
    private List<String> delimiters;
    private HashMap<String, SyntacticObject> tokenMap;
    
    public TokenGrammar(String... delimeters) {
        super();
        this.delimiters = Arrays.asList(delimeters);
        tokenMap = new HashMap<>();
    }
    
    public TokenGrammar(Grammar inherit, String... delimeters) {
        super(inherit);
        ensureTokenized();
        this.delimiters = Arrays.asList(delimeters);
        tokenMap = new HashMap<>();
    }
    
    public TokenGrammar(TokenGrammar inherit, String... delimeters) {
        super(inherit);
        this.delimiters = Arrays.asList(delimeters);
        tokenMap = inherit.tokenMap;
    }
    
    public void inherit(TokenGrammar g){
        this.delimiters =g.delimiters;
        this.tokenMap = g.tokenMap;
        super.inherit(g);
    }
    
    private void addToken(String s, SyntacticObject o){
        if(tokenMap.containsKey(s)){
            tokenMap.replace(s, o);
        }else{
            tokenMap.put(s, o);
        }
    }
    public void addToken(String name, TokenRegexTerminal t){
        addToken(name,(SyntacticObject) t);
    }
    public void addToken(String name, TokenTerminal t){
        addToken(name, (SyntacticObject) t);
    }
    
    public boolean containsToken(String name){
        return tokenMap.containsKey(name);
    }
    
    public void removeCategory(String name){
        if(containsCategory(name)){
            hashMap.remove(name);
        }
    }
    
    private void changeToken(String name, SyntacticObject s){
        if(containsToken(name)){
            tokenMap.replace(name, s);
        }
    }
    public void changeToken(String name, TokenRegexTerminal t){
        changeToken(name,(SyntacticObject) t);
    }
    public void changeToken(String name, TokenTerminal t){
        changeToken(name, (SyntacticObject) t);
    }
    
    public void ensureTokenized(){
        for (SyntacticCategory value : hashMap.values()) {
            value.convertToTokenized();
        }
    }
    
    
    public void addToken(String s){
        if(tokenMap.containsKey(s)){
            tokenMap.replace(s, null);
        }else{
            tokenMap.put(s, null);
        }
    }
    
    public HashMap<String, SyntacticObject> getTokenMap() {
        return tokenMap;
    }
    
    public List<String> getDelimiters() {
        return delimiters;
    }
}
