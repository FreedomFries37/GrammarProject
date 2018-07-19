package structure.syntacticObjects.tokenBased;

import misc.Tools;
import structure.syntacticObjects.SyntacticObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenRegexTerminal extends Token {
    
    private Pattern patternMatch;
    
    public TokenRegexTerminal(Pattern patternMatch) {
        this.patternMatch = patternMatch;
    }
    
    public TokenRegexTerminal(String pattern){
        patternMatch = Pattern.compile(pattern);
    }
    
    public Pattern getPatternMatch() {
        return patternMatch;
    }
    
    public boolean isMatch(String s){
        Matcher m = patternMatch.matcher(s);
        return m.matches();
    }
    
    @Override
    public String getRepresentation() {
        return "%" + patternMatch.pattern() + "%";
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
    
    @Override
    protected String generate(int max_depth, int level) {
        return generate();
    }
}
