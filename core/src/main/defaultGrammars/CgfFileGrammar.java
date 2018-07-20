package main.defaultGrammars;

import structure.Grammar;
import structure.syntacticObjects.RegexTerminal;
import structure.syntacticObjects.Rule;

import java.util.regex.Pattern;

public class CgfFileGrammar extends Grammar {
    
    public CgfFileGrammar(){
        try {
            inherit(new StandardGrammar());
            inherit(new ListGrammar(getCategory("string")));
            
            addCategory("import");
            
            addCategory("category");
            inherit(new ListGrammar(getCategory("category"), getCategory("whitespace")));
            addCategory("grammar");
            getCategory("grammar").addRule(getCategory("whitespace"),
                    getCategory("list_category")
            );
            getCategory("grammar").addRule(
                    getCategory("list_category")
            );
            head = getCategory("grammar");
            getCategory("list_category").setIgnoreWhitespace(false);
            
            addCategory("rule_head");
            addCategory("rule_reference");
            addCategory("rule_char", new Rule(new RegexTerminal(Pattern.compile("[^\\s<>\\\\]|(\\\\<)|(\\\\>)| "))));
            addCategory("rule_escape");
            addCategory("named_action");
            addOptionalCategory("named_action_parameters");
            addCategory("parameter");
            getCategory("parameter").addRules(new Rule(getCategory("rule_reference")), new Rule(getCategory("sentence")));
            addOptionalCategory("rule_tail");
            inherit(new ListGrammar(getCategory("parameter")));
            getCategory("named_action_parameters").addRule("(", getCategory("list_parameter"), ")");
            getCategory("named_action").addRule(getCategory("string"), getCategory("named_action_parameters"));
            getCategory("rule_escape").addRule("\\", getCategory("named_action"), "\\");
            
            
            addCategory("rule_part", new Rule(getCategory("rule_head"), getCategory("rule_tail")));
            getCategory("rule_reference").addRule("<", getCategory("string"), ">");
            //getCategory("rule_reference").addRule("{", getCategory("string"),":", getCategory("tag"), "}");
            getCategory("rule_head").addRule(getCategory("rule_reference"));
            getCategory("rule_head").addRule(getCategory("rule_char"));
            getCategory("rule_head").addRule(getCategory("rule_escape"));
            getCategory("rule_tail").addRule(getCategory("rule_part"));
            getCategory("rule_tail").setIgnoreWhitespace(false);
            getCategory("rule_head").setIgnoreWhitespace(false);
            getCategory("rule_part").setIgnoreWhitespace(false);
            getCategory("rule_reference").setIgnoreWhitespace(false);
            getCategory("rule_escape").setIgnoreWhitespace(false);
            getCategory("rule_char").setIgnoreWhitespace(false);
    
            addOptionalCategory("opt_space_and_newline");
            getCategory("opt_space_and_newline").addRule( new RegexTerminal("(\\n| )+"));
            getCategory("opt_space_and_newline").setIgnoreWhitespace(false);
            addCategory("rule", new Rule("\t", getCategory("rule_part"), "\n"));
            getCategory("rule").setIgnoreWhitespace(false);
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
