package misc;

import structure.parse.ParseNode;
import structure.parse.ParseTree;

public class TreeInfo {
    
    private int height;
    private int maxLeft;
    private int maxRight;
    
    public TreeInfo(ParseTree tree){
        height = getHeight(tree.getHead());
        maxLeft = getMaxLeft(tree.getHead());
        maxRight = getMaxRight(tree.getHead());
    }
    
    private int getHeight(ParseNode tree){
        if(tree == null) return 0;
        int maxIndex = 0;
        int max = 0;
        int index = 0;
        for (ParseNode child : tree.getChildren()) {
            int height = getHeight(child) + 1;
            if(height > max){
                max = height;
                maxIndex = index;
            }
            index++;
        }
        return getHeight(tree.getChild(maxIndex)) + 1;
    }
    
    private int getMaxLeft(ParseNode tree){
        return 0;
    }
    
    private int getMaxRight(ParseNode tree){
        return 0;
    }
    
    public int getHeight() {
        return height;
    }
    
    public int getMaxLeft() {
        return maxLeft;
    }
    
    public int getMaxRight() {
        return maxRight;
    }
}
