package structure.Grammars;

import structure.syntacticObjects.Rule;
import structure.syntacticObjects.SyntacticCategory;
import structure.syntacticObjects.SyntacticObject;
import structure.syntacticObjects.Terminals.Terminal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Grammar {
    
    protected HashMap<String, SyntacticCategory> hashMap;
    private List<String> options;
    private List<String> autoCleans;
    protected SyntacticCategory head;
    private boolean tokenParse;
    
    public Grammar() {
        hashMap = new HashMap<>();
        autoCleans = new ArrayList<>();
        options = new ArrayList<>();
    }
    
    @SuppressWarnings("unchecked")
    public Grammar(Grammar inherit){
        this();
        if(inherit != null) {
            hashMap = (HashMap<String, SyntacticCategory>) inherit.hashMap.clone();
            head = inherit.head;
            autoCleans = inherit.autoCleans;
            tokenParse = inherit.tokenParse;
        }
    }
    
    public void inherit(Grammar g){
        for (String s: g.hashMap.keySet()) {
            if(!hashMap.containsKey(s)){
                hashMap.put(s, g.hashMap.get(s));
            }
        }
        for (String autoClean : g.autoCleans) {
            if(!autoCleans.contains(autoClean)){
                autoCleans.add(autoClean);
            }
        }
        for (String option : g.options) {
            addOption(option);
        }
        if(g.hasDefault()) head = g.head;
        tokenParse = g.tokenParse;
    }
    public void inheritAndReplace(Grammar g){
        for (String s: g.hashMap.keySet()) {
            if(!hashMap.containsKey(s)){
                hashMap.put(s, g.hashMap.get(s));
            }else{
                hashMap.replace(s, g.hashMap.get(s));
            }
        }
        for (String autoClean : g.autoCleans) {
            if(!autoCleans.contains(autoClean)){
                autoCleans.add(autoClean);
            }
        }
        for (String option : g.options) {
            addOption(option);
        }
        if(g.hasDefault()) head = g.head;
    }
    
    public void inherit(Grammar g, String... cats){
        for (String cat: cats) {
            if(!hashMap.containsKey(cat) && g.containsCategory(cat)){
                hashMap.put(cat, g.hashMap.get(cat));
            }
        }
        for (String cat : cats) {
            if(g.autoCleans.contains(cat)){
               autoCleans.add(cat);
            }
        }
        for (String option : g.options) {
            addOption(option);
        }
        if(g.hasDefault()) head = g.head;
    }
    
    public void addOption(String option){
        if(!options.contains(option)) options.add(option);
    }
    
    public static ArrayList<Terminal> createTerminals(String s){
        ArrayList<Terminal> output = new ArrayList<>();
        for (char c : s.toCharArray()) {
            output.add(new Terminal(c));
        }
        return output;
    }
    
    public SyntacticCategory getDefault(){ return head; }
    public void setDefault(SyntacticCategory head){
        this.head = head;
    }
    
    public boolean containsCategory(String name){
        return hashMap.containsKey(name);
    }
    
    public List<SyntacticCategory> getAllCategories(){
        return new LinkedList<>(hashMap.values());
    }
    
    public boolean hasDefault(){
        return head != null;
    }
    
    public boolean isTokenParse() {
        return tokenParse;
    }
    
    public void setTokenParse(boolean tokenParse) {
        this.tokenParse = tokenParse;
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
    public void resetCategory(SyntacticCategory category){
        category.resetRules();
    }
    
    public boolean addRule(String cat, Rule rule){
        if(!containsCategory(cat)) return false;
        getCategory(cat).addRule(rule);
        return true;
    }
    
    public void addAutoClean(String clean){
        if(!autoCleans.contains(clean)){
            autoCleans.add(clean);
        }
    }
    
    public List<String> getAutoCleans(){
        return autoCleans;
    }
    
    public LinkedList<SyntacticCategory> getInvisibleCategories(){
        LinkedList<SyntacticCategory> output = new LinkedList<>();
        for (SyntacticCategory allCategory : getAllCategories()) {
            if(allCategory.isInvisible()){
                output.add(allCategory);
            }
        }
        return output;
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
    
    public ArrayList<String> generateExamples(int count, String name, int maxDepth){
        if(!hashMap.containsKey(name)) return null;
        return generateExamples(count, getCategory(name), maxDepth);
    }
    public ArrayList<String> generateExamples(int count, int maxDepth){
        return generateExamples(count, head, maxDepth);
    }
    
    public static ArrayList<String> generateExamples(int count, SyntacticObject object, int maxDepth){
        if(object == null) return null;
        ArrayList<String> output = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            output.add(object.generate(maxDepth));
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
    
    public void printExamples(int count, int maxDepth){
        for (String s : generateExamples(count,maxDepth)) {
            System.out.println(s);
        }
    }
    
    public static void printExamples(int count, SyntacticCategory object, int maxDepth){
        for (String s : generateExamples(count, object, maxDepth)) {
            System.out.println(s);
        }
    }
    
    public void printExamples(int count, String name, int maxDepth){
        if(!hashMap.containsKey(name)) return;
        for (String s : generateExamples(count, name, maxDepth)) {
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
            String modifierText = "";
            if(head != null && head.getName().equals(categoryName)) modifierText += "head ";
            if(getCategory(categoryName).isInvisible()) modifierText += "invisible ";
            if(getCategory(categoryName).isOptional()) modifierText += "optional ";
            System.out.println(modifierText + categoryName + ":");
            for (Rule rule: getCategory(categoryName).getRules()) {
                System.out.println("\t" + rule.toString());
            }
        }
    }
}
