package structure.syntacticObjects;

import structure.Grammar;

import java.util.ArrayList;
import java.util.Arrays;

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
                return new RegexTerminal("\\w");
            case '.':
                return new RegexTerminal(".");
            default:
                return new RegexTerminal("\\" + escapeCharacter);
        }
    }
    
    public String toIfStatements(){
        StringBuilder output = new StringBuilder();
        output.append(String.format("if (%s){", syntacticObjects.get(0).createParseMethodCall()));
    
        output.append("}");
        return output.toString();
    }
}
