package interaction.defaultGrammars;

import structure.Grammars.Grammar;
import structure.syntacticObjects.SyntacticCategory;

public class VarGrammar extends Grammar {
    
    private String setName;
    
    public VarGrammar(SyntacticCategory set, SyntacticCategory get, String setString) {
        setName = "set_" + set.getName();
        inherit(new StandardGrammar(), "whitespace");
        addCategory(set);
        addCategory(get);
        addCategory(setName);
        addCategory(setName + "_tail");
        addCategory(setName + "_tail_tail");
        getCategory(setName).addRule(set, getCategory(setName + "_tail"));
        getCategory(setName + "_tail").addRule(getCategory("whitespace"), setString,  getCategory(setName +
                "_tail_tail"));
        getCategory(setName + "_tail").addRule(setString, getCategory(setName +
                "_tail_tail"));
        getCategory(setName + "_tail_tail").addRule(getCategory("whitespace"), get);
        getCategory(setName + "_tail_tail").addRule(get);
    }
    public VarGrammar(SyntacticCategory set, SyntacticCategory get) {
        this(set, get, "=");
    }
    
}
