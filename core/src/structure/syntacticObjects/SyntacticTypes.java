package structure.syntacticObjects;

import structure.syntacticObjects.tokenBased.TokenRegexTerminal;
import structure.syntacticObjects.tokenBased.TokenTerminal;

public enum SyntacticTypes {
    
    REGEX_TERMINAL,
    TERMINAL,
    SYNTACTIC_CATEGORY,
    TOKEN_TERMINAL,
    TOKEN_REGEX_TERMINAL,
    SPECIAL;
    
    public static SyntacticTypes getType(SyntacticObject object){
        if(object.getClass().equals(SyntacticCategory.class)){
            return SYNTACTIC_CATEGORY;
        }else if(object.getClass().equals(Terminal.class)){
            return TERMINAL;
        }else if(object.getClass().equals(RegexTerminal.class)){
            return REGEX_TERMINAL;
        }else if(object.getClass().equals(TokenTerminal.class)) {
            return TOKEN_TERMINAL;
        }else if(object.getClass().equals(TokenRegexTerminal.class)) {
            return TOKEN_REGEX_TERMINAL;
        }
        
        return null;
    }
}
