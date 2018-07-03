package structure.syntacticObjects;

import misc.Tools;

import java.util.Arrays;

public class Terminal extends SyntacticObject {
    
    private char terminal;
    
    public Terminal(char c){
        terminal = c;
    }
    
    @Override
    public String getRepresentation() {
        return "" + terminal;
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
        return "" + terminal;
    }
    
    /**
     * Assume there are methods: advancePointer() matchChar() matchString() matchPattern() consumeChar() consumeString()
     * consumePattern()
     *
     * @return
     */
    @Override
    public String createParseMethodBody() {
        return String.format(
                "if(!consumeChar('%c')) return false;\n",
                terminal);
    }
    
    @Override
    public String createParseMethodName() {
        return null;
    }
    
    @Override
    public String createParseMethodCall() {
        return String.format(
                "consumeChar('%c')",
                terminal);
    }
}
