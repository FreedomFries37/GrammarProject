package modules;

import structure.parse.ParseNode;
import structure.parse.ParseTree;

public interface IConvertModule<T> {
    
    T convertParseNode(ParseNode p);
    
    T convertParseTree(ParseTree p);
}
