package structure.syntacticObjects;

public enum SyntacticTypes {
    
    REGEX_TERMINAL,
    TERMINAL,
    SYNTACTIC_CATEGORY,
    SPECIAL;
    
    public static SyntacticTypes getType(SyntacticObject object){
        if(object.getClass().equals(SyntacticCategory.class)){
            return SYNTACTIC_CATEGORY;
        }else if(object.getClass().equals(Terminal.class)){
            return TERMINAL;
        }else if(object.getClass().equals(RegexTerminal.class)){
            return REGEX_TERMINAL;
        }
        
        return null;
    }
}
