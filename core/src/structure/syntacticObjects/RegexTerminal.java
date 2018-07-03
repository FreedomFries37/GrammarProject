package structure.syntacticObjects;

import misc.Tools;

import java.util.regex.Pattern;

public class RegexTerminal extends SyntacticObject {
    
    
    Pattern pattern;
    
    public RegexTerminal(Pattern pattern) {
        this.pattern = pattern;
    }
    
    public RegexTerminal(String s) {
        pattern = Pattern.compile(s);
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
       return getRepresentation();
    }
}
