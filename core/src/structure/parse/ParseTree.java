package structure.parse;

import structure.syntacticObjects.SyntacticTypes;

import java.util.ArrayList;

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
    
    public void clean(String catName){
        for (ParseNode child: children()) {
            if(child.getType() == SyntacticTypes.SYNTACTIC_CATEGORY &&
            catName.equals(child.getData())){
                child.setDataToAllChildTerminals();
            }
        }
    }
    
    public void removeEmptyNodes(){
        head.removeEmptyChildren();
    }
    
    public ArrayList<ParseNode> children(){
        return getHead().getAllChildren();
    }
    
    public int childCount(){
        return children().size();
    }
    
    public void print(){
        getHead().print(0);
    }
    
    public ParseNode getHead() {
        return head;
    }
    
    public String baseType(){
        return head.getData();
    }
}
