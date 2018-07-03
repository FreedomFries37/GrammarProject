package defaultGrammars;

import structure.Grammar;
import structure.syntacticObjects.SyntacticCategory;

public class VarGrammar extends Grammar {
    
    private String setName;
    
    public VarGrammar(SyntacticCategory set, SyntacticCategory get, String setString) {
        setName = "set_" + set.getName();
        inherit(new StandardGrammar(), "opt_whitespace");
        addCategory(set);
        addCategory(get);
        addCategory(setName);
        getCategory(setName).addRule(set, getCategory("opt_whitespace"), setString, getCategory("opt_whitespace"), get);
    }
    public VarGrammar(SyntacticCategory set, SyntacticCategory get) {
        this(set, get, "=");
    }
    
}
