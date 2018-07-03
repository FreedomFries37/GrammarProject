package structure.syntacticObjects;

import misc.Tools;
import structure.parse.ParseNode;

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
        if(isOptional() && Math.random() > .66d){
            return "";
        }
        
        int ruleIndex = (int) (Math.random() * (double) rules.size());
        StringBuilder single = new StringBuilder();
        for (SyntacticObject syntacticObject : rules.get(ruleIndex).getSyntacticObjects()) {
            single.append(syntacticObject.generate());
        }
       
        return single.toString();
    }
    
    /**
     * Assume there are methods: advancePointer() matchChar() matchString() matchPattern() consumeChar() consumeString()
     * consumePattern()
     *
     * @return
     */
    @Override
    public String createParseMethodBody() {
        String output = String.format("       ParseNode next = new ParseNode(\"<%s>\");\n", name);
        for (Rule rule : rules) {
        
        }
    }
    
    @Override
    public String createParseMethodName() {
        return "parse" + name.substring(0, 1).toUpperCase() + name.substring(1);
    }
    
    @Override
    public String createParseMethodCall() {
        return null;
    }
}
