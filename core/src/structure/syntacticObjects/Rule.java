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
                syntacticObjects.addAll(Grammar.createTerminals((String) object));
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
}
