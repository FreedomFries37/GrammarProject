package interaction.defaultGrammars;

import structure.Grammars.ExtendedGrammar;
import structure.syntacticObjects.Rule;
import structure.syntacticObjects.Terminals.RegexTerminal;
import structure.syntacticObjects.Terminals.tokenBased.TokenRegexTerminal;

import java.util.regex.Pattern;

public class DataSheetGrammar extends ExtendedGrammar {
    
    private final static String[] delims;
    
    static{
        delims = new String[]{
                "\"",
                "{",
                "}",
                "[",
                "]",
                ")",
                "("
        };
    }
    
    public DataSheetGrammar(){
        super(delims);
        
        
        addToken("data_name", new TokenRegexTerminal(Pattern.compile("\".+\"")));
        addCategory("data");
        getCategory("data").addRule(new RegexTerminal(Pattern.compile("\".+\"")));
        addCategory("data_entry");
        addCategory("data_information");
        inherit(new ListGrammar(getCategory("data_entry")));
        getCategory("data_information").addRule("=","{",getCategory("list_data_entry"),"}");
        getCategory("data_information").addRule("=",getCategory("data"));
        getCategory("data_entry").addRule(getCategory("data_name"), getCategory("data_information"));
        setDefault(getCategory("data_entry"));
        
    }
}
