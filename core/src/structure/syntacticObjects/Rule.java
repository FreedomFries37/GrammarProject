package structure.syntacticObjects;

import structure.Grammar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

public class Rule {
    
    private ArrayList<SyntacticObject> syntacticObjects;
    
    public Rule(ArrayList<SyntacticObject> SyntacticObjects) {
        this.syntacticObjects = SyntacticObjects;
    }
    
    public Rule(SyntacticObject... objects){
        syntacticObjects = new ArrayList<>(Arrays.asList(objects));
    }
    
    public Rule(Object... objects) throws IncorrectTypeException{
        syntacticObjects = new ArrayList<>();
        for (Object object : objects) {
            if(object.getClass().equals(String.class)){
                String s = (String) object;
                if(!s.contains("\\")) syntacticObjects.addAll(Grammar.createTerminals((String) object));
                else{
                    for (int i = 0; i < s.toCharArray().length; i++) {
                        char c = s.charAt(i);
                        if(c == '\\'){
                            char escapeCharacter = s.charAt(++i);
                            if(escapeCharacter == 'p'){
                                if(s.charAt(i++) != '=') throw new IncorrectTypeException();
                                if(s.charAt(i++) != '"') throw new IncorrectTypeException();
                                StringBuilder pattern = new StringBuilder();
                                while(s.charAt(i) != '"'){
                                    pattern.append(s.charAt(i++));
                                }
                                syntacticObjects.add(new RegexTerminal(pattern.toString()));
                            }else{
                                syntacticObjects.add(specialPatterns(escapeCharacter));
                            }
                        }else{
                            syntacticObjects.add(new Terminal(c));
                        }
                    }
                }
            }else if(object.getClass().equals(char.class)){
                syntacticObjects.add(new Terminal((char) object));
            }else if(SyntacticObject.class.isAssignableFrom(object.getClass())){
                syntacticObjects.add((SyntacticObject) object);
            }else{
                throw new IncorrectTypeException();
            }
        }
    }
    
    public ArrayList<Pattern> lookaheads(){
        ArrayList<Pattern> output = new ArrayList<>();
        SyntacticObject first = syntacticObjects.get(0);
        if(first.getClass().equals(SyntacticCategory.class)){
            SyntacticCategory category = (SyntacticCategory) first;
            output.addAll(category.allLookAheads());
        }else if(first.getClass().equals(Terminal.class)){
            Terminal terminal = (Terminal) first;
            output.add(Pattern.compile(Pattern.quote("" + terminal.getRepresentation())));
        }else if(first.getClass().equals(RegexTerminal.class)){
            RegexTerminal terminal = (RegexTerminal) first;
            output.add(terminal.getPattern());
        }
        return output;
    }
    
    public ArrayList<SyntacticObject> getSyntacticObjects() {
        return syntacticObjects;
    }
    
    public boolean ruleHasSyntacticCategories(){
       return syntacticCategoryCount() > 0;
    }
    
    public int syntacticCategoryCount(){
        int output = 0;
        for (SyntacticObject syntacticObject : syntacticObjects) {
            if(syntacticObject.getClass().equals(SyntacticCategory.class)) output++;
        }
        return output;
    }
    
    public int terminalCount(){
        int output = 0;
        for (SyntacticObject syntacticObject : syntacticObjects) {
            if(syntacticObject.getClass().equals(Terminal.class) || syntacticObject.getClass().equals(RegexTerminal.class)) output++;
        }
        return output;
    }
    
    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (SyntacticObject SyntacticObject : syntacticObjects) {
            output.append(SyntacticObject.getRepresentation());
        }
        
        return output.toString();
    }
    
    public class IncorrectTypeException extends Exception{
    
        @Override
        public String getMessage() {
            return super.getMessage();
        }
    
        public IncorrectTypeException() {
            super("Type must be String, char, or SyntacticObject");
        }
    }
    
    public RegexTerminal specialPatterns(char escapeCharacter){
        switch (escapeCharacter){
            case 'c':
                return new RegexTerminal("[0-9a-zA-Z_]");
            case '.':
                return new RegexTerminal(".");
            default:
                return new RegexTerminal("\\" + escapeCharacter);
        }
    }
    
}
