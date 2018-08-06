package structure.syntacticObjects;

import misc.Tools;

public class Variable extends SyntacticObject {
    
    public enum Scope{
        global(0),
        local(1);
        
        private int level;
        Scope(int level){
            this.level = level;
        }
    
        public int getLevel() {
            return level;
        }
    }
    
    private String name;
    private Scope scope;
    
    public Variable(String name, Scope scope) {
        this.name = name;
        this.scope = scope;
    }
    
    public String getName() {
        return name;
    }
    
    public Scope getScope() {
        return scope;
    }
    
    @Override
    public String getRepresentation() {
        return String.format("$(%s:%s)",scope,name);
    }
    
    @Override
    public String getUpperRepresentation() {
        return getRepresentation();
    }
    
    @Override
    protected String getFullRepresentation(int maxLevels, int currentLevel) {
        return Tools.indent(currentLevel) + getRepresentation();
    }
    
    @Override
    public String generate() {
        return getRepresentation();
    }
    
    @Override
    protected String generate(int max_depth, int level) {
        return getFullRepresentation(max_depth, level);
    }
}
