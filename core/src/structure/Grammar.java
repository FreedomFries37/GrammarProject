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
    protected SyntacticCategory head;
    
    public Grammar() {
        hashMap = new HashMap<>();
    }
    
    @SuppressWarnings("unchecked")
    public Grammar(Grammar inherit){
        hashMap = (HashMap<String, SyntacticCategory>) inherit.hashMap.clone();
        head = inherit.head;
    }
    
    public void inherit(Grammar g){
        for (String s: g.hashMap.keySet()) {
            if(!hashMap.containsKey(s)){
                hashMap.put(s, g.hashMap.get(s));
            }
        }
    }
    
    public void inherit(Grammar g, String... cats){
        for (String cat: cats) {
            if(!hashMap.containsKey(cat) && g.containsCategory(cat)){
                hashMap.put(cat, g.hashMap.get(cat));
            }
        }
    }
    
    public static ArrayList<Terminal> createTerminals(String s){
        ArrayList<Terminal> output = new ArrayList<>();
        for (char c : s.toCharArray()) {
            output.add(new Terminal(c));
        }
        return output;
    }
    
    public SyntacticCategory getDefault(){ return head; }
    
    public boolean containsCategory(String name){
        return hashMap.containsKey(name);
    }
    
    public boolean hasDefault(){
        return head != null;
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
    
    public String[] getCategoryNames(){
        return hashMap.keySet().toArray(new String[hashMap.size()]);
    }
    
    public void printCategoryNames(){
        for (String categoryName: getCategoryNames()) {
            System.out.println(categoryName);
        }
    }
    
    public void printGrammar(){
        for (String categoryName: getCategoryNames()) {
            System.out.println(categoryName + ":");
            for (Rule rule: getCategory(categoryName).getRules()) {
                System.out.println("\t" + rule.toString());
            }
        }
    }
}