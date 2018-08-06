package structure.syntacticObjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuleReference extends SyntacticObject {
    
    private SyntacticCategory category;
    private HashMap<String, ArrayList<String>> instanceTags;
    
    public RuleReference(SyntacticCategory category){
        this.category = category;
        instanceTags = new HashMap<>();
    }
    
    public void addTag(String tag, ArrayList<String> options){
        if(!instanceTags.containsKey(tag)) instanceTags.put(tag, options);
    }
    
    public List<Map.Entry<String, ArrayList<String>>> getTagInfo(){
        return new ArrayList<>(instanceTags.entrySet());
    }
    
    public SyntacticCategory getCategory() {
        return category;
    }
    
    @Override
    public String getRepresentation() {
        return category.getRepresentation();
    }
    
    @Override
    public String getUpperRepresentation() {
        return category.getUpperRepresentation();
    }
    
    @Override
    protected String getFullRepresentation(int maxLevels, int currentLevel) {
        return category.getFullRepresentation(maxLevels,currentLevel);
    }
    
    @Override
    public String generate() {
        return category.generate();
    }
    
    @Override
    protected String generate(int max_depth, int level) {
        return generate(max_depth,level);
    }
}
