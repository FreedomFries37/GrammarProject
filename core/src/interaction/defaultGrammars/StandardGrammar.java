package interaction.defaultGrammars;

import modules.IConvertModule;
import structure.Grammars.Grammar;
import structure.parse.ParseNode;
import structure.parse.ParseTree;
import structure.syntacticObjects.Terminals.RegexTerminal;
import structure.syntacticObjects.Rule;

public class StandardGrammar extends Grammar {
    
    public StandardGrammar() {
        addCategory("char", new Rule(new RegexTerminal("\\w")));
        addOptionalCategory("string_tail");
        addCategory("string", new Rule(getCategory("char"), getCategory("string_tail")));
        getCategory("string_tail").addRule(new Rule(getCategory("string")));
        
        addCategory("sentence");
        addOptionalCategory("sentence_prime");
        addOptionalCategory("sentence_prime_prime");
        addCategory("sentence_char", new Rule(new RegexTerminal("[^\"]")));
        addCategory("sentence_char'", new Rule(new RegexTerminal("[^']")));
        addOptionalCategory("sentence_tail");
        addOptionalCategory("sentence_tail'");
        getCategory("sentence_tail").addRule(new Rule(getCategory("sentence_prime")));
        getCategory("sentence_tail'").addRule(new Rule(getCategory("sentence_prime_prime")));
        getCategory("sentence_prime").addRule(new Rule(getCategory("sentence_char"), getCategory("sentence_tail")));
        getCategory("sentence_prime_prime").addRule(getCategory("sentence_char'"), getCategory("sentence_tail'"));
        getCategory("sentence").addRule("\"", getCategory("sentence_prime"), "\"");
        getCategory("sentence").addRule("'", getCategory("sentence_prime_prime"), "'");
        
        addCategory("string_sentence", new Rule(getCategory("string")), new Rule(getCategory("sentence")));
        
        addCategory("digit", new Rule(new RegexTerminal("\\d")));
        
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
        getCategory("whitespace").setIgnoreWhitespace(false);
        addOptionalCategory("opt_whitespace");
        getCategory("opt_whitespace").addRule(new Rule(new RegexTerminal("\\s+")));
        getCategory("opt_whitespace").setIgnoreWhitespace(false);
        
        inherit(new ListGrammar(getCategory("sentence")));
        addAutoClean("double");
        addAutoClean("string");
        addAutoClean("sentence'");
        addAutoClean("integer");
    }
    
    public static IConvertModule<String> convertSentence = new IConvertModule<String>() {
        @Override
        public String convertParseNode(ParseNode p) {
            return p.getChildTerminals().substring(1, p.getChildTerminals().length()-1);
        }
    
        @Override
        public String convertParseTree(ParseTree p) {
           return convertParseNode(p.getHead());
        }
    };
}
