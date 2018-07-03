package defaultGrammars;

import structure.Grammar;
import structure.syntacticObjects.RegexTerminal;
import structure.syntacticObjects.Rule;

import java.util.regex.Pattern;

public class CgfFileGrammar extends Grammar {
    
    public CgfFileGrammar(){
        super(new StandardGrammar());
        try {
            inherit(new ListGrammar(getCategory("string")));
            addCategory("category");
            inherit(new ListGrammar(getCategory("category"), getCategory("whitespace")));
            addCategory("grammar");
            getCategory("grammar").addRule(
                    getCategory("list_category")
            );
            head = getCategory("grammar");
            
            addCategory("rule_head");
            addCategory("rule_reference");
            addCategory("rule_char", new Rule(new RegexTerminal(Pattern.compile("[^\\s<>]|(\\\\<)|(\\\\>)"))));
            addOptionalCategory("rule_tail");
            addCategory("rule_part", new Rule(getCategory("rule_head"), getCategory("rule_tail")));
            getCategory("rule_reference").addRule("<", getCategory("string"), ">");
            getCategory("rule_head").addRule(getCategory("rule_reference"));
            getCategory("rule_head").addRule(getCategory("rule_char"));
            getCategory("rule_tail").addRule(getCategory("rule_part"));
    
            addOptionalCategory("opt_space_and_newline");
            getCategory("opt_space_and_newline").addRule( new RegexTerminal("(\\n| )+"));
            addCategory("rule", new Rule("\t", getCategory("rule_part"), "\n"));
            inherit(new ListGrammar(getCategory("rule"), ""));
    
            addOptionalCategory("options");
            getCategory("options").addRule("(", getCategory("list_string"), ")");
            getCategory("category").addRule(
                    getCategory("string"),
                    getCategory("options"),
                    ":",
                    getCategory("opt_space_and_newline"),
                    getCategory("list_rule"));
        }catch (Rule.IncorrectTypeException e){
            e.printStackTrace();
        }
    }
}
