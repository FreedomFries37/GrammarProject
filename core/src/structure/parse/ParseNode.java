package structure.parse;

import structure.syntacticObjects.SyntacticObject;

import java.util.LinkedList;

public class ParseNode {
    
    String object;
    LinkedList<ParseNode> children;
    
    public ParseNode(String object){
        this.object = object;
        children = new LinkedList<>();
    }
    
    public void addChild(ParseNode p){
        children.add(p);
    }
}
