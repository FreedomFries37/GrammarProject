package defaultGrammars;

import structure.Grammar;
import structure.syntacticObjects.SyntacticCategory;

public class ListGrammar extends Grammar {
    
    private SyntacticCategory listObject;
    private SyntacticCategory seperator;
    
    public ListGrammar(SyntacticCategory listObject){
        this(listObject, ",");
    }
    
    public ListGrammar(SyntacticCategory listObject, String seperator) {
        this.listObject = listObject;
        this.seperator = new SyntacticCategory("list_" + listObject.getName() + "_seperator");
    
        this.seperator.addRule(seperator);
        
        addCategory("list_" + listObject.getName());
        addCategory("list_" + listObject.getName() + "_object");
        addOptionalCategory("list_" + listObject.getName() + "_tail");
        
        SyntacticCategory listBase = getCategory("list_" + listObject.getName());
        SyntacticCategory lObject = getCategory("list_" + listObject.getName() + "_object");
        SyntacticCategory listTail = getCategory("list_" + listObject.getName() + "_tail");
        
        listBase.addRule(lObject, listTail);
        lObject.addRule(listObject);
        if(seperator.equals("")){
            listTail.addRule(listBase);
        }else{
            listTail.addRule(seperator, listBase);
        }
    }
    
    public ListGrammar(SyntacticCategory listObject, SyntacticCategory seperator) {
        this.listObject = listObject;
        this.seperator = seperator;
        
        addCategory("list_" + listObject.getName());
        addCategory("list_" + listObject.getName() + "_object");
        addOptionalCategory("list_" + listObject.getName() + "_tail");
        
        SyntacticCategory listBase = getCategory("list_" + listObject.getName());
        SyntacticCategory lObject = getCategory("list_" + listObject.getName() + "_object");
        SyntacticCategory listTail = getCategory("list_" + listObject.getName() + "_tail");
        
        listBase.addRule(lObject, listTail);
        lObject.addRule(listObject);
        listTail.addRule(seperator, listBase);
    }
}
