package structure.syntacticObjects;

public class SyntacticFunction extends SyntacticObject {
    
    private String name;
    private SyntacticCategory[][] parameters;
    private Rule baseRule;
    
    
    public SyntacticFunction(String name, SyntacticCategory[][] parameters, Rule baseRule) {
        this(name,parameters,baseRule.getSyntacticObjects().toArray());
    }
    
    public SyntacticFunction(String name, SyntacticCategory[][] parameters, Object... obj) {
        try {
            this.name = name;
            this.parameters = parameters;
            this.baseRule = new Rule(name, obj);
        }catch (Rule.IncorrectTypeException e){
            e.printStackTrace();
        }
    }
    
   
    public int getMaxSize(){
        int output = 0;
        for (SyntacticCategory[] parameter : parameters) {
            output += parameter.length;
        }
        return output + baseRule.getSyntacticObjects().size();
    }
    
    public String getName() {
        return name;
    }
    
    public SyntacticCategory[][] getParameters() {
        return parameters;
    }
    
    public Rule getBaseRule() {
        return baseRule;
    }
    
    @Override
    public String getRepresentation() {
        StringBuilder params = new StringBuilder();
        for (SyntacticCategory[] parameter : parameters) {
            StringBuilder param = new StringBuilder();
            for (SyntacticCategory syntacticCategory : parameter) {
                param.append(syntacticCategory.getRepresentation());
            }
            param.append(",");
            params.append(param);
        }
        params.deleteCharAt(params.length()-1);
        
        
        
        
        return String.format("<%s(%s)> -> %s", name, params.toString(), baseRule.toString());
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
