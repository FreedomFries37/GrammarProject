package structure.parse;

import structure.Grammar;
import structure.syntacticObjects.SyntacticTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParseTree {
    
    private ParseNode head;
    
    public ParseTree(ParseNode head) {
        this.head = head;
    }
    
    public ParseTree(ParseNode head, String... clean){
        this.head = head;
        for (String s: clean) {
            clean(s);
        }
    }
    
    
    public ParseTree(ParseNode head, Grammar grammar){
        this(head);
        head.refactorInvisibleChildren(grammar);
        for (String autoClean : grammar.getAutoCleans()) {
            clean(autoClean);
        }
        
    }
    
    public void clean(String catName){
        for (ParseNode child: children()) {
            if(child.getType() == SyntacticTypes.SYNTACTIC_CATEGORY &&
            catName.equals(child.getData())){
                child.setDataToAllChildTerminals();
            }
        }
    }
    
    public void removeAllTerminalsExceptForChildrenOf(String... names){
        List<String> list = Arrays.asList(names);
        for (ParseNode syntacticChild : syntacticChildren()) {
            if(!list.contains(syntacticChild.getData())){
                for (int i = syntacticChild.getChildren().size()-1; i >= 0; i--) {
                    if(syntacticChild.getChildren().get(i).getType() != SyntacticTypes.SYNTACTIC_CATEGORY){
                        syntacticChild.getChildren().remove(i);
                    }
                }
            }
        }
        
    }
    
    public void removeEmptyNodes(){
        head.removeEmptyChildren();
    }
    
    public ArrayList<ParseNode> children(){
        return getHead().getAllChildren();
    }
    
    public ArrayList<ParseNode> syntacticChildren(){
        return getHead().syntacticChildren();
    }
    
    public int childCount(){
        return children().size();
    }
    
    public void print(){
        getHead().print(0);
    }
    
    public void printTerminals(){
        System.out.println(getHead().getChildTerminals());
    }
    
    public ParseNode getHead() {
        return head;
    }
    
    public String baseType(){
        return head.getData();
    }
}
