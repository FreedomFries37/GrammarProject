package structure;

import java.util.Arrays;
import java.util.List;

public class TokenGrammar extends Grammar {
    
    private List<String> delimiters;
    
    public TokenGrammar(String... delimeters) {
        super();
        this.delimiters = Arrays.asList(delimeters);
    }
    
    public TokenGrammar(Grammar inherit, String... delimeters) {
        super(inherit);
        this.delimiters = Arrays.asList(delimeters);
    }
    
    public List<String> getDelimiters() {
        return delimiters;
    }
}
