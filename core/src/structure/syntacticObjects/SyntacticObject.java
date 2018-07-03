package structure.syntacticObjects;

public abstract class SyntacticObject {
    
    public abstract String getRepresentation();
    public abstract String getUpperRepresentation();
    public String getFullRepresentation(int maxLevels){
        return getFullRepresentation(maxLevels, 1);
    }
    
    protected abstract String getFullRepresentation(int maxLevels, int currentLevel);
    
}
