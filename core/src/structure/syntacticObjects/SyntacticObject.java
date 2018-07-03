package structure.syntacticObjects;

import structure.Reference;
import structure.parse.ParseNode;

public abstract class SyntacticObject {
    
    public abstract String getRepresentation();
    public abstract String getUpperRepresentation();
    public String getFullRepresentation(int maxLevels){
        return getFullRepresentation(maxLevels, 1);
    }
    protected abstract String getFullRepresentation(int maxLevels, int currentLevel);
    
    public abstract String generate();
    
    /**
     * Assume there are methods:
     *      advancePointer()
     *      matchChar()
     *      matchString()
     *      matchPattern()
     *      consumeChar()
     *      consumeString()
     *      consumePattern()
     * @return
     */
    public abstract String createParseMethodBody();
    public abstract String createParseMethodName();
    public abstract String createParseMethodCall();
}
