package structure;

import structure.syntacticObjects.SyntacticCategory;
import structure.syntacticObjects.SyntacticObject;
import structure.syntacticObjects.tokenBased.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TokenGrammar extends Grammar {
    
    private ArrayList<String> delimiters;
    private HashMap<String, Token> tokenMap;
    
    public TokenGrammar(String... delimeters) {
        super();
        this.delimiters = new ArrayList<>(Arrays.asList(delimeters));
        tokenMap = new HashMap<>();
    }
    
    public TokenGrammar(Grammar inherit, String... delimeters) {
        super(inherit);
        ensureTokenized();
        this.delimiters = new ArrayList<>(Arrays.asList(delimeters));
        tokenMap = new HashMap<>();
    }
    
    public TokenGrammar(TokenGrammar inherit, String... delimeters) {
        super(inherit);
        this.delimiters = new ArrayList<>(Arrays.asList(delimeters));
        tokenMap = inherit.tokenMap;
    }
    
    public void inherit(TokenGrammar g){
        this.delimiters =g.delimiters;
        this.tokenMap = g.tokenMap;
        super.inherit(g);
    }
    
    public void addToken(String name, Token t){
        
       if(!tokenMap.containsKey(name)){
           tokenMap.put(name, t);
       }
    }
    
    public boolean containsToken(String name){
        return tokenMap.containsKey(name);
    }
    
    public Token getToken(String name){
        return tokenMap.get(name);
    }
    
    public void removeCategory(String name){
        if(containsCategory(name)){
            hashMap.remove(name);
        }
    }
    
    public void changeToken(String name, Token s){
        if(containsToken(name)){
            tokenMap.replace(name, s);
        }
    }
    
    public void ensureTokenized(){
        for (SyntacticCategory value : hashMap.values()) {
            value.convertToTokenized(delimiters);
        }
    }
    
    
    public void addToken(String s){
        if(tokenMap.containsKey(s)){
            tokenMap.replace(s, null);
        }else{
            tokenMap.put(s, null);
        }
    }
    
    public SyntacticObject get(String s){
        if(containsCategory(s)) return getCategory(s);
        else if(containsToken(s)) return getToken(s);
        return null;
    }
    
    public HashMap<String, Token> getTokenMap() {
        return tokenMap;
    }
    
    public List<String> getDelimiters() {
        return delimiters;
    }
    
    public void setDelimiters(ArrayList<String> delimiters) {
        this.delimiters = delimiters;
    }
    
    @Override
    public void printGrammar() {
        super.printGrammar();
        System.out.println(
                "DELIMITERS:"
        );
        for (String delimiter : delimiters) {
            System.out.print("\t\"" + delimiter + "\"\n");
        }
    }
}
