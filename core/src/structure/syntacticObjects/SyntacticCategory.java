package structure.syntacticObjects;

import misc.Tools;
import structure.parse.ParseNode;

import java.util.*;
import java.util.regex.Pattern;

public class SyntacticCategory extends SyntacticObject {
    
    private String name;
    private boolean optional;
    @Deprecated
    private boolean ignoreWhitespace;
    private boolean invisible;
    private ArrayList<Rule> rules;
    
    {ignoreWhitespace = true;}
    
    
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
    
    public void addRule(Object... o){
        try {
            addRule(new Rule(o));
        }catch (Rule.IncorrectTypeException e){
            e.printStackTrace();
        }
    }
    
    public void addRules(Rule... rules){
        for (Rule rule : rules) {
            addRule(rule);
        }
    }
    
    public boolean isInvisible() {
        return invisible;
    }
    
    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
    }
    
    public HashMap<Pattern, Rule> lookAheadToRuleMap(){
        HashMap<Pattern, Rule> output = new HashMap<>();
        for (Rule rule: rules) {
            for (Pattern lookahead: rule.lookaheads()) {
                output.put(lookahead, rule);
            }
        }
        return output;
    }
    
    public void convertToTokenized(List<String> delimiters){
        for (Rule rule : rules) {
            rule.convertToTokenized(delimiters);
        }
    }
    
    public ArrayList<Pattern> allLookAheads(){
        return new ArrayList<>(lookAheadToRuleMap().keySet());
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
    
    public void resetRules(){
        rules = new ArrayList<>();
    }
    
    public String getName() {
        return name;
    }
    
    @Deprecated
    public boolean isIgnoreWhitespace() {
        return ignoreWhitespace;
    }
    
    @Deprecated
    public void setIgnoreWhitespace(boolean ignoreWhitespace) {
        this.ignoreWhitespace = ignoreWhitespace;
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
        int ruleIndex = 0;
        for (Rule rule : rules) {
            int modifier = 1;
            if(rule.syntacticCategoryCount() > 1
                    || rule.syntacticCategoryCount() > 0 && rule.terminalCount() > 0
                    || !rule.ruleHasSyntacticCategories()) {
                output.append(Tools.indent(currentLevel));
                output.append(rule.toString());
                modifier = 2;
            }
            if (rule.ruleHasSyntacticCategories()) {
                if(!(rule.terminalCount() == 0 && rule.syntacticCategoryCount() == 1)) output.append('\n');
                
                int index = 0;
                for (SyntacticObject syntacticObject : rule.getSyntacticObjects()) {
                    output.append(syntacticObject.getFullRepresentation(maxLevels, currentLevel + modifier));
                    if(ruleIndex < rules.size()-1 || index != rule.getSyntacticObjects().size() - 1) output.append('\n');
                    index++;
                }
            }
            ruleIndex++;
        }
        
    
        return output.toString();
    }
    
    @Override
    public String generate() {
        if(isOptional() && Math.random() > .7d){
            return "";
        }
        
        int ruleIndex = (int) (Math.random() * (double) rules.size());
        StringBuilder single = new StringBuilder();
        if(rules.size() > 0) {
            for (SyntacticObject syntacticObject: rules.get(ruleIndex).getSyntacticObjects()) {
                single.append(syntacticObject.generate());
            }
        }
        return single.toString();
    }
    
    @Override
    protected String generate(int max_depth, int level) {
        if(max_depth == level) return getRepresentation();
        if (isOptional() && Math.random() > .7d*(1-(level/max_depth))) {
            return "";
        }
    
        int ruleIndex = (int) (Math.random() * (double) rules.size());
        StringBuilder single = new StringBuilder();
        if (rules.size() > 0) {
            for (SyntacticObject syntacticObject : rules.get(ruleIndex).getSyntacticObjects()) {
                single.append(syntacticObject.generate(max_depth, level + 1));
            }
        }
        return single.toString();
    }
}
