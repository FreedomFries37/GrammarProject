package structure;

import structure.syntacticObjects.Rule;
import structure.syntacticObjects.SyntacticCategory;
import structure.syntacticObjects.Terminal;

import java.util.ArrayList;
import java.util.HashMap;

public class Grammar {
    
    private HashMap<String, SyntacticCategory> hashMap;
    
    public Grammar() {
        hashMap = new HashMap<>();
    }
    
    public static ArrayList<Terminal> createTerminals(String s){
        ArrayList<Terminal> output = new ArrayList<>();
        for (char c : s.toCharArray()) {
            output.add(new Terminal(c));
        }
        return output;
    }
    
    public boolean containsCategory(String name){
        return hashMap.containsKey(name);
    }
    
    public SyntacticCategory getCategory(String name){
        if(!hashMap.containsKey(name)) return null;
        return hashMap.get(name);
    }
    
    public boolean addCategory(String name){
        if(hashMap.containsKey(name)) return false;
        hashMap.put(name, new SyntacticCategory(name));
        return true;
    }
    
    public boolean addOptionalCategory(String name){
        if(containsCategory(name)) return false;
        addCategory(name);
        getCategory(name).setOptional(true);
        return true;
    }
    
    public boolean addCategory(String name, Rule... rules){
        if(!addCategory(name)) return false;
        getCategory(name).addRules(rules);
        return true;
    }
    
    public boolean addCategory(SyntacticCategory category){
        if(hashMap.containsKey(category.getName())) return false;
        hashMap.put(category.getName(), category);
        return true;
    }
    
    public boolean addCategory(SyntacticCategory category, Rule...rules){
        if(!addCategory(category)) return false;
        getCategory(category.getName()).addRules(rules);
        return true;
    }
    
    public boolean addRule(String cat, Rule rule){
        if(!containsCategory(cat)) return false;
        getCategory(cat).addRule(rule);
        return true;
    }
}
