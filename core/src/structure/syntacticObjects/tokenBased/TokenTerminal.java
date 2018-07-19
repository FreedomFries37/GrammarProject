package structure.syntacticObjects.tokenBased;

import misc.Tools;
import structure.syntacticObjects.SyntacticObject;

public class TokenTerminal extends Token {
    
    private String tokenAbsorbString;
    
    public TokenTerminal(){
        tokenAbsorbString = null;
    }
    
    public TokenTerminal(String tokenAbsorbString) {
        this.tokenAbsorbString = tokenAbsorbString;
    }
    
    public String getTokenAbsorbString() {
        return tokenAbsorbString;
    }
    
    public boolean isWildcardToken(){
        return tokenAbsorbString == null;
    }
    
    public void setTokenAbsorbString(String tokenAbsorbString) {
        this.tokenAbsorbString = tokenAbsorbString;
    }
    
    @Override
    public String getRepresentation() {
        if(tokenAbsorbString == null) return "%wildcard%";
        return tokenAbsorbString;
    }
    
    @Override
    public String getUpperRepresentation() {
        return getRepresentation();
    }
    
    @Override
    protected String getFullRepresentation(int maxLevels, int currentLevel) {
        return Tools.indent(currentLevel-1) + getRepresentation();
    }
    
    @Override
    public String generate() {
        return tokenAbsorbString;
    }
    
    @Override
    protected String generate(int max_depth, int level) {
        return generate();
    }
}
