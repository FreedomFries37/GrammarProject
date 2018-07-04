package defaultGrammars;

import structure.Grammar;
import structure.syntacticObjects.RegexTerminal;
import structure.syntacticObjects.Rule;

public class StandardGrammar extends Grammar {
    
    public StandardGrammar(){
        try {
            addCategory("char", new Rule("\\c"));
            addOptionalCategory("string_tail");
            addCategory("string", new Rule(getCategory("char"), getCategory("string_tail")));
            getCategory("string_tail").addRule(new Rule(getCategory("string")));
            
            addCategory("sentence");
            addCategory("sentence'");
            addCategory("sentence''");
            addCategory("sentence_char", new Rule(new RegexTerminal("[^\"]")));
            addCategory("sentence_char'", new Rule(new RegexTerminal("[^']")));
            addOptionalCategory("sentence_tail");
            addOptionalCategory("sentence_tail'");
            getCategory("sentence_tail").addRule(new Rule(getCategory("sentence'")));
            getCategory("sentence_tail'").addRule(new Rule(getCategory("sentence''")));
            getCategory("sentence'").addRule(new Rule(getCategory("sentence_char"), getCategory("sentence_tail")));
            getCategory("sentence''").addRule(getCategory("sentence_char'"), getCategory("sentence_tail'"));
            getCategory("sentence").addRule("\"", getCategory("sentence'"), "\"");
            getCategory("sentence").addRule("'", getCategory("sentence''"), "'");
            
            addCategory("string_sentence", new Rule(getCategory("string")), new Rule(getCategory("sentence")));
            
            addCategory("digit", new Rule("\\d"));
            
            addCategory("integer");
            addOptionalCategory("integer_tail");
            getCategory("integer").addRule(getCategory("digit"), getCategory("integer_tail"));
            getCategory("integer_tail").addRule(getCategory("integer"));
            
            addCategory("double");
            addOptionalCategory("double_first_tail");
            addCategory("double_decimal");
            addOptionalCategory("double_decimal_tail");
            getCategory("double").addRule(getCategory("digit"), getCategory("double_first_tail"));
            getCategory("double_first_tail").addRule(getCategory("double"));
            getCategory("double_first_tail").addRule(".", getCategory("double_decimal"));
            getCategory("double_decimal").addRule(getCategory("digit"), getCategory("double_decimal_tail"));
            getCategory("double_decimal_tail").addRule(getCategory("double_decimal"));
            
            addCategory("whitespace", new Rule(new RegexTerminal("\\s+")));
            addCategory("opt_whitespace", new Rule(new RegexTerminal("\\s*")));
            
            inherit(new ListGrammar(getCategory("sentence")));
        }catch (Rule.IncorrectTypeException e){
            e.printStackTrace();
        }
    }
}