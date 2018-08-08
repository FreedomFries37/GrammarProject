package structure.syntacticObjects;

import structure.Grammars.Grammar;
import structure.syntacticObjects.Terminals.RegexTerminal;
import structure.syntacticObjects.Terminals.Terminal;
import structure.syntacticObjects.Terminals.tokenBased.TokenTerminal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
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
                        syntacticObjects.add(new Terminal(c));
                        
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
    
    public Rule(String functionName, Object... objects) throws IncorrectTypeException{
        this(functionName, Arrays.asList(objects));
    }
    public Rule(String functionName, Collection<Object> objects) throws IncorrectTypeException{
        syntacticObjects = new ArrayList<>();
        for (Object object : objects) {
            if(object.getClass().equals(String.class)){
                String s = (String) object;
                
                Pattern variablePattern = Pattern.compile("\\$(?<num>\\d+)");
                Matcher m = variablePattern.matcher(s);
                if(m.matches()){
                    String varNum = m.group("num");
                    String varNameFull = functionName + "_" + varNum;
                    syntacticObjects.add(new Variable(varNameFull, Variable.Scope.local));
                }else if(!s.contains("\\")) syntacticObjects.addAll(Grammar.createTerminals((String) object));
                else{
                    for (int i = 0; i < s.toCharArray().length; i++) {
                        char c = s.charAt(i);
                        syntacticObjects.add(new Terminal(c));
                    
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
    
    public void convertToTokenized(List<String> delimiters){
        ArrayList<SyntacticObject> converted = new ArrayList<>();
        
        StringBuilder token = new StringBuilder();
        for (SyntacticObject syntacticObject : syntacticObjects) {
            if(syntacticObject.getClass().equals(Terminal.class)){
                Terminal t = (Terminal) syntacticObject;
                token.append(t.getRepresentation());
            }else{
                if(!token.toString().equals("")) converted.add(new TokenTerminal(token.toString()));
                token = new StringBuilder();
                converted.add(syntacticObject);
            }
        }
        if(!token.toString().equals("")) converted.add(new TokenTerminal(token.toString()));
        for (int i = 0; i < converted.size(); i++) {
            if(converted.get(i).getClass().equals(TokenTerminal.class)) {
                String original = converted.get(i).getRepresentation();
                if (delimiters != null && !delimiters.contains(original)) {
                    for (String delimiter : delimiters) {
                        if (original.contains(delimiter)) {
                            converted.set(i, new TokenTerminal(original.substring(0,
                                    original.indexOf(delimiter))));
                            converted.add(i + 1, new TokenTerminal(original.substring(original.indexOf(delimiter),
                                    original.indexOf(delimiter) + delimiter.length())));
                            String next = original.substring(original.indexOf(delimiter) + delimiter.length());
                            if (next.length() > 0) converted.add(i + 2, new TokenTerminal(next));
                            original = converted.get(i).getRepresentation();
                        }
                    }
                }
                converted.removeIf((SyntacticObject test) -> test.getRepresentation().equals(""));
            }
        }
        for (int i = converted.size()-2; i > 0; i-- ){
            if(converted.get(i).getClass().equals(TokenTerminal.class)) {
                String original = converted.get(i).getRepresentation();
                if(original.equals("\\") &&
                    converted.get(i+1).getClass().equals(TokenTerminal.class) &&
                    delimiters.contains(converted.get(i+1).getRepresentation())){
                    converted.remove(i);
                }
            }
        }
        
        syntacticObjects = converted;
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
