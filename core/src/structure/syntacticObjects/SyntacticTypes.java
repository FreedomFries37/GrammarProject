package structure.syntacticObjects;

import structure.syntacticObjects.Terminals.RegexTerminal;
import structure.syntacticObjects.Terminals.Terminal;
import structure.syntacticObjects.Terminals.tokenBased.TokenRegexTerminal;
import structure.syntacticObjects.Terminals.tokenBased.TokenTerminal;

public enum SyntacticTypes {
    
    REGEX_TERMINAL(1),
    TERMINAL(1),
    SYNTACTIC_CATEGORY(-1),
    TOKEN_TERMINAL(1),
    TOKEN_REGEX_TERMINAL(1),
    SPECIAL(1),
    VARIABLE(1),
    FUNCTION(-1);
    
    SyntacticTypes(int i){
        maxChildren = i;
    }
    
    private int maxChildren;
    
    public int getMaxChildren() {
        return maxChildren;
    }
    
    public static SyntacticTypes getType(SyntacticObject object){
        if(object.getClass().equals(SyntacticCategory.class)){
            return SYNTACTIC_CATEGORY;
        }else if(object.getClass().equals(Terminal.class)){
            return TERMINAL;
        }else if(object.getClass().equals(RegexTerminal.class)){
            return REGEX_TERMINAL;
        }else if(object.getClass().equals(TokenTerminal.class)) {
            return TOKEN_TERMINAL;
        }else if(object.getClass().equals(SyntacticFunction.class)) {
            return FUNCTION;
        }else if(object.getClass().equals(TokenRegexTerminal.class)) {
            return TOKEN_REGEX_TERMINAL;
        }else if(object.getClass().equals(Variable.class)) {
            return VARIABLE;
        }
        
        return null;
    }
}
