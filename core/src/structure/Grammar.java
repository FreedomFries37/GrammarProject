package structure;

import structure.parse.ParseTree;
import structure.syntacticObjects.Rule;
import structure.syntacticObjects.SyntacticCategory;
import structure.syntacticObjects.SyntacticObject;
import structure.syntacticObjects.Terminal;

import java.util.ArrayList;
import java.util.HashMap;

public class Grammar {
    
    private HashMap<String, SyntacticCategory> hashMap;
    private SyntacticCategory head;
    
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
    
    public ArrayList<String> generateExamples(int count){
        return generateExamples(count, head);
    }
    
    public static ArrayList<String> generateExamples(int count, SyntacticObject object){
        if(object == null) return null;
        ArrayList<String> output = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            output.add(object.generate());
        }
        return output;
    }
    
    public ArrayList<String> generateExamples(int count, String name){
        if(!hashMap.containsKey(name)) return null;
        return generateExamples(count, getCategory(name));
    }
    
    public void printExamples(int count){
        for (String s : generateExamples(count)) {
            System.out.println(s);
        }
    }
    
    public static void printExamples(int count, SyntacticCategory object){
        for (String s : generateExamples(count, object)) {
            System.out.println(s);
        }
    }
    
    public void printExamples(int count, String name){
        if(!hashMap.containsKey(name)) return;
        for (String s : generateExamples(count, name)) {
            System.out.println(s);
        }
    }
}
