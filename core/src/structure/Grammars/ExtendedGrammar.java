package structure.Grammars;

import structure.syntacticObjects.SyntacticCategory;
import structure.syntacticObjects.SyntacticFunction;
import structure.syntacticObjects.SyntacticObject;
import structure.syntacticObjects.Terminals.tokenBased.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ExtendedGrammar extends Grammar {
    
    private ArrayList<String> delimiters;
    private HashMap<String, Token> tokenMap;
    private HashMap<String, SyntacticFunction> functionMap;
    private HashMap<String, ArrayList<String>> groups;
    
    public ExtendedGrammar(String... delimeters) {
        super();
        this.delimiters = new ArrayList<>(Arrays.asList(delimeters));
        tokenMap = new HashMap<>();
        functionMap = new HashMap<>();
        groups = new HashMap<>();
    }
    
    public ExtendedGrammar(Grammar inherit, String... delimeters) {
        super(inherit);
        ensureTokenized();
        this.delimiters = new ArrayList<>(Arrays.asList(delimeters));
        tokenMap = new HashMap<>();
        functionMap = new HashMap<>();
        groups = new HashMap<>();
    }
    
    public ExtendedGrammar(ExtendedGrammar inherit, String... delimeters) {
        super(inherit);
        this.delimiters = new ArrayList<>(Arrays.asList(delimeters));
        tokenMap = inherit.tokenMap;
        functionMap = inherit.functionMap;
    }
    
    public void inherit(ExtendedGrammar g){
        this.delimiters =g.delimiters;
        this.tokenMap = g.tokenMap;
        super.inherit(g);
    }
    
    public boolean containsFunction(String name){
        return getFunction(name) != null;
    }
    
    public void addFunction(SyntacticFunction function){
        if(!functionMap.containsKey(function.getName())){
            functionMap.put(function.getName(), function);
        }
    }
    
    public SyntacticFunction getFunction(String name){
        if(functionMap.containsKey(name)){
            return functionMap.get(name);
        }
        return null;
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
    
    
    public void changeToken(String name, Token s){
        if(containsToken(name)){
            tokenMap.replace(name, s);
        }
    }
    
    public void removeCategory(String name){
        if(containsCategory(name)){
            hashMap.remove(name);
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
    
    public boolean containsGroup(String name){
        return groups.containsKey(name);
    }
    
    public void addGroup(String name){
        if(!containsGroup(name)){
            groups.put(name, new ArrayList<>());
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
    
    public HashMap<String, SyntacticFunction> getFunctionMap() {
        return functionMap;
    }
    
    public HashMap<String, ArrayList<String>> getGroups() {
        return groups;
    }
    
    @Override
    public void printGrammar() {
        System.out.println(
                "CATEGORIES:"
        );
        super.printGrammar();
        System.out.println(
                "TOKENS:"
        );
        for (String s : tokenMap.keySet()) {
            System.out.printf("%s:\n\t%s\n",s,tokenMap.get(s).getRepresentation());
        }
        System.out.println(
                "FUNCTIONS:"
        );
        for (String s : functionMap.keySet()) {
            System.out.printf("%s:\n%s\n",s,functionMap.get(s).getUpperRepresentation());
        }
        System.out.println(
                "DELIMITERS:"
        );
        for (String delimiter : delimiters) {
            System.out.print("\t\"" + delimiter + "\"\n");
        }
    }
}
