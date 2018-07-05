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
    public String generate(int max_depth){
        return generate(max_depth-1, 0);
    }
    protected abstract String generate(int max_depth, int level);
    
}
