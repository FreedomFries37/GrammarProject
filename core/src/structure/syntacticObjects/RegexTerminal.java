package structure.syntacticObjects;

import misc.Tools;

import java.util.regex.Pattern;

public class RegexTerminal extends SyntacticObject {
    
    
    private Pattern pattern;
    
    public RegexTerminal(Pattern pattern) {
        this.pattern = pattern;
    }
    
    public RegexTerminal(String s) {
        pattern = Pattern.compile(s);
    }
    
    public Pattern getPattern() {
        return pattern;
    }
    
    @Override
    public String getRepresentation() {
        return pattern.pattern();
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
        return Tools.createMatchingString(pattern);
    }
    
   
}
