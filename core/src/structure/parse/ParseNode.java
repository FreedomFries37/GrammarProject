package structure.parse;

import misc.Tools;
import structure.syntacticObjects.*;

import java.util.ArrayList;
import java.util.LinkedList;

import static structure.syntacticObjects.SyntacticTypes.TOKEN_REGEX_TERMINAL;

public class ParseNode {
    
    private String data;
    private SyntacticTypes type;
    private Rule rule;
    private LinkedList<ParseNode> children;
    
    public ParseNode(String object){
        this.data = object;
        children = new LinkedList<>();
    }
    
    public ParseNode(SyntacticObject object, String str){
        type = SyntacticTypes.getType(object);
        children = new LinkedList<>();
        children.add(new ParseNode(str));
    }
    
    public ParseNode(SyntacticCategory category, Rule rule) {
        type = SyntacticTypes.SYNTACTIC_CATEGORY;
        data = category.getName();
        this.rule = rule;
        children = new LinkedList<>();
    }
    
    public void addChild(ParseNode p){
        children.add(p);
    }
    
    public ParseNode getChild(int i){
        if(i < 0 || i >= children.size()) return null;
        return children.get(i);
    }
    public ParseNode getChild(String s){
        for (ParseNode child: children) {
            if(child.data != null && child.data.equals(s)) return child;
        }
        return null;
    }
    
    public boolean contains(String s){
        return getChild(s) != null;
    }
    
    
    public String getData() {
        return data;
    }
    
    public SyntacticTypes getType() {
        return type;
    }
    
    public LinkedList<ParseNode> getChildren() {
        return children;
    }
    
    public ArrayList<ParseNode> getAllChildren(){
        ArrayList<ParseNode> output = new ArrayList<>();
        output.add(this);
        for (ParseNode child: children) {
            output.addAll(child.getAllChildren());
        }
        return output;
    }
    
    public ArrayList<ParseNode> syntacticChildren(){
        ArrayList<ParseNode> output = new ArrayList<>();
        if(type != SyntacticTypes.SYNTACTIC_CATEGORY) return output;
        output.add(this);
        for (ParseNode child: children) {
            output.addAll(child.syntacticChildren());
        }
        return output;
        
    }
    
    public int childCount(){
        return children.size();
    }
    
    public ParseNode leftMostOpenParseNode(){
        ParseNode ptr = this;
        int maxChildren = ptr.getMaxChildren();
    
        for (ParseNode child: children) {
            ParseNode next = child.leftMostOpenParseNode();
            if(next != null){
                ptr = next;
                break;
            }
        }
        
        if(ptr == this && maxChildren == ptr.childCount()) return null;
        
        return ptr;
    }
    
    public int getMaxChildren(){
        if(type == null) return 0;
        switch (type){
            case TOKEN_TERMINAL:
            case REGEX_TERMINAL:
            case TERMINAL:
            case TOKEN_REGEX_TERMINAL:
                return 1;
            case SYNTACTIC_CATEGORY:
                if(rule == null) return 0;
                return rule.getSyntacticObjects().size();
        }
        
        return -1;
    }
    
    public boolean empty(){
        return childCount() == 0;
    }
    
    public boolean full(){
        return getMaxChildren() == childCount();
    }
    
    /**
     * Returns a string representation of the object. In general, the {@code toString} method returns a string that
     * "textually represents" this object. The result should be a concise but informative representation that is easy
     * for a person to read. It is recommended that all subclasses override this method.
     * <p>
     * The {@code toString} method for class {@code Object} returns a string consisting of the name of the class of
     * which the object is an instance, the at-sign character `{@code @}', and the unsigned hexadecimal representation
     * of the hash code of the object. In other words, this method returns a string equal to the value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        switch (type){
            case SYNTACTIC_CATEGORY:
                return "<" + data + ">";
            case TOKEN_TERMINAL:
            case TERMINAL:
            case REGEX_TERMINAL:
            case TOKEN_REGEX_TERMINAL:
                return children.get(0).data;
            case SPECIAL:
                return data;
        }
        
        return null;
    }
    
    public void print(int index){
        if(type == null) return;
        System.out.println(Tools.indent(index) + toString());
        for (ParseNode child: children) {
            child.print(index + 1);
        }
    }
    
    public void setDataToAllChildTerminals(){
        
        ParseNode special = new ParseNode(getChildTerminals());
        special.type = SyntacticTypes.SPECIAL;
        children = new LinkedList<>();
        children.add(special);
    }
    
    public String getChildTerminals(){
        StringBuilder newData = new StringBuilder();
        if(type == SyntacticTypes.TERMINAL ||
                type == SyntacticTypes.REGEX_TERMINAL ||
                type == SyntacticTypes.SPECIAL ||
                type == SyntacticTypes.TOKEN_TERMINAL ||
                type == SyntacticTypes.TOKEN_REGEX_TERMINAL) return toString();
        for (ParseNode child: children) {
            newData.append(child.getChildTerminals());
        }
        return newData.toString();
    }
    
    public void removeEmptyChildren(){
        for (int i = children.size()-1; i >= 0; i--) {
            if(children.get(i).children.size() == 0 && children.get(i).type == SyntacticTypes.SYNTACTIC_CATEGORY){
                children.remove(i);
            }else{
                children.get(i).removeEmptyChildren();
            }
        }
    }
}
