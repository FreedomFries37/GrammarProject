package structure.syntacticObjects;

public class SyntacticFunction extends SyntacticObject {
    
    private String name;
    private SyntacticCategory[][] parameters;
    private Rule baseRule;
    
    
    public int getMaxSize(){
        int output = 0;
        for (SyntacticCategory[] parameter : parameters) {
            output += parameter.length;
        }
        return output + baseRule.getSyntacticObjects().size();
    }
    
    @Override
    public String getRepresentation() {
        return "<" + name + "()>";
    }
    
    @Override
    public String getUpperRepresentation() {
        return null;
    }
    
    @Override
    protected String getFullRepresentation(int maxLevels, int currentLevel) {
        return null;
    }
    
    @Override
    public String generate() {
        return null;
    }
    
    @Override
    protected String generate(int max_depth, int level) {
        return null;
    }
}
