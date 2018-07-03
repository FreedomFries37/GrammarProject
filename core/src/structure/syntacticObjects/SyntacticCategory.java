package structure.syntacticObjects;

import misc.Tools;

import java.util.ArrayList;
import java.util.Arrays;

public class SyntacticCategory extends SyntacticObject {
    
    private String name;
    private boolean optional;
    private ArrayList<Rule> rules;
    
    
    public SyntacticCategory(String name) {
        this.name = name;
        optional = false;
        rules = new ArrayList<>();
    }
    
    public SyntacticCategory(String name, boolean optional) {
        this.name = name;
        this.optional = optional;
    }
    
    public SyntacticCategory(String name, boolean optional, ArrayList<Rule> rules) {
        this.name = name;
        this.optional = optional;
        this.rules = rules;
    }
    
    public SyntacticCategory(String name, ArrayList<Rule> rules) {
        this.name = name;
        this.rules = rules;
    }
    
    public SyntacticCategory(String name, boolean optional, Rule... rules) {
       this(name, optional, new ArrayList<>(Arrays.asList(rules)));
    }
    
    public SyntacticCategory(String name, Rule... rules) {
        this(name, new ArrayList<>(Arrays.asList(rules)));
    }
    
    public void addRule(Rule r){
        rules.add(r);
    }
    
    public void addRules(Rule... rules){
        for (Rule rule : rules) {
            addRule(rule);
        }
    }
    
    public boolean isOptional() {
        return optional;
    }
    
    public void setOptional(boolean optional) {
        this.optional = optional;
    }
    
    public ArrayList<Rule> getRules() {
        return rules;
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public String getRepresentation() {
        return String.format("<%s>", name);
    }
    
    @Override
    public String getUpperRepresentation() {
        StringBuilder output = new StringBuilder();
        for (Rule rule : rules) {
            output.append(rule.toString());
        }
        return output.toString();
    }
    
    @Override
    protected String getFullRepresentation(int maxLevels, int currentLevel) {
        if(currentLevel >= maxLevels) return Tools.indent(currentLevel-1) + getRepresentation();
        StringBuilder output = new StringBuilder();
        output.append(Tools.indent(currentLevel-1));
        output.append(getRepresentation() + "\n");
        if(optional){
            output.append(Tools.indent(currentLevel));
            output.append("$\n");
        }
        for (Rule rule : rules) {
            output.append(Tools.indent(currentLevel));
            output.append(rule.toString() + "\n");
            for (SyntacticObject syntacticObject : rule.getSyntacticObjects()) {
                output.append(syntacticObject.getFullRepresentation(maxLevels, currentLevel+2));
                output.append('\n');
            }
        }
        
    
        return output.toString();
    }
}
