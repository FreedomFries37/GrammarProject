package interaction.defaultGrammars;

import modules.IConvertModule;
import structure.Grammars.ExtendedGrammar;
import structure.parse.ParseNode;
import structure.parse.ParseTree;
import structure.syntacticObjects.Rule;
import structure.syntacticObjects.SyntacticCategory;
import structure.syntacticObjects.Terminals.tokenBased.Token;

import java.util.ArrayList;

public class ListGrammar extends ExtendedGrammar {
    
    private SyntacticCategory listObject;
    private SyntacticCategory seperator;
    
    public ListGrammar(SyntacticCategory listObject){
        this(listObject, ",");
    }
    
    public ListGrammar(SyntacticCategory listObject, String seperator) {
        this.listObject = listObject;
        this.seperator = new SyntacticCategory("list_" + listObject.getName() + "_seperator");
    
        this.seperator.addRule(seperator);
        
        addCategory(listObject);
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
        listBase.setIgnoreWhitespace(listObject.isIgnoreWhitespace());
        lObject.setIgnoreWhitespace(listObject.isIgnoreWhitespace());
        listTail.setIgnoreWhitespace(listObject.isIgnoreWhitespace());
        getDelimiters().add(seperator);
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
    
    public ListGrammar(Token t){
        this(wrapper(t));
    }
    
    public ListGrammar(Token t, String seperator){
        this(wrapper(t),seperator);
    }
   
    private static SyntacticCategory wrapper(Token t){
        return new SyntacticCategory(t.getRepresentation() + "_wrapper", new Rule(t));
    }
    
    public static class ListGrammarConverter implements IConvertModule<ArrayList<ParseNode>>{
    
        @Override
        public ArrayList<ParseNode> convertParseNode(ParseNode p) {
            ArrayList<ParseNode> output = new ArrayList<>();
            ParseNode ptr =p;
            while(ptr != null){
                if(ptr.getData().endsWith("_tail")) ptr = ptr.getChild(ptr.childCount()-1);
                ParseNode object= ptr.getChild(0).getChild(0);
                output.add(object);
                ptr = ptr.getChild(1);
            }
            
            return output;
        }
    
        public ArrayList<ParseNode> convertParseNodeMustHaveChild(ParseNode p, String cat) {
            ArrayList<ParseNode> output = new ArrayList<>();
            ParseNode ptr =p;
            while(ptr != null){
                if(ptr.getData().endsWith("_tail")) ptr = ptr.getChild(ptr.childCount()-1);
                ParseNode object= ptr.getChild(0).getChild(0);
                if(object.contains(cat)) output.add(object.getChild(cat));
                ptr = ptr.getChild(1);
            }
        
            return output;
        }
    
        @Override
        public ArrayList<ParseNode> convertParseTree(ParseTree p) {
            return convertParseNode(p.getHead());
        }
    }
}
