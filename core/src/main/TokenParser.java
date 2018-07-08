package main;

import structure.Reference;
import structure.TokenGrammar;
import structure.parse.ParseNode;
import structure.parse.ParseTree;
import structure.syntacticObjects.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenParser extends Parser {
    
    private LinkedList<String> tokens;
    private List<String> delimiters;
    private int tokenIndex;
    private int tokenStringIndex;
    
    public TokenParser(TokenGrammar grammar) {
        super(grammar);
        delimiters = grammar.getDelimiters();
        delimiters.sort(Comparator.comparingInt((String s) -> -s.length()));
    }
    
    @Override
    protected char currentChar() {
        return tokens.get(tokenIndex).charAt(tokenStringIndex);
    }
    
    @Override
    protected boolean advancePointer() {
        tokenStringIndex++;
        if(tokenStringIndex == tokens.get(tokenIndex).length()){
            tokenStringIndex = 0;
            tokenIndex++;
        }
        return tokenIndex < tokens.size();
    }
    
    @Override
    protected boolean match(String s) {
        int originalIndex, originalStringIndex;
        originalIndex = tokenIndex;
        originalStringIndex = tokenStringIndex;
        boolean returnValue = true;
        for (char c : s.toCharArray()) {
            if(!consume(c)){
                returnValue = false;
            }
        }
        tokenIndex = originalIndex;
        tokenStringIndex = originalStringIndex;
        return returnValue;
    }
    
    @Override
    protected boolean match(Pattern p) {
        return super.match(p);
    }
    
    @Override
    protected boolean match(Pattern p, Reference<String> outString) {
        int originalIndex, originalStringIndex;
        originalIndex = tokenIndex;
        originalStringIndex = tokenStringIndex;
        StringBuilder check = new StringBuilder();
       
        String output = null;
        boolean found = false;
        do{
            check.append(currentChar());
            Matcher m = p.matcher(check);
        
            if(m.matches()){
                output = check.toString();
                found = true;
            }else{
                if(found){
                    outString.setRef(output);
                    tokenIndex = originalIndex;
                    tokenStringIndex = originalStringIndex;
                    return true;
                }
            }
        
        } while(advancePointer());
        if(found){
            outString.setRef(output);
            tokenIndex = originalIndex;
            tokenStringIndex = originalStringIndex;
            return true;
        }
        tokenIndex = originalIndex;
        tokenStringIndex = originalStringIndex;
        return false;
    }
    
    @Override
    protected boolean consume(String s) {
        if(!match(s)) return false;
        for (int i = 0; i < s.length(); i++) {
            advancePointer();
        }
        return true;
    }
    
    @Override
    protected boolean consume(Pattern p) {
        Reference<String> outString = new Reference<>();
        if(!match(p,outString)) return false;
        return consume(outString.getRef());
    }
    
    @Override
    protected boolean consume(Pattern p, Reference<String> ref) {
        if(!match(p,ref)) return false;
        return consume(ref.getRef());
    }
    
    @Override
    public ParseTree parse(String s) {
        return super.parse(s);
    }
    
    @Override
    public ParseTree parse(File file) throws IOException {
        return super.parse(file);
    }
    
    @Override
    public ParseTree parse(File file, SyntacticCategory base) throws IOException {
        return super.parse(file, base);
    }
    
    @Override
    public ParseTree parse(String s, SyntacticCategory base) {
        tokens = splitString(s);
        for (int i = 0; i < tokens.size(); i++) {
            String original = tokens.get(i);
            if(!delimiters.contains(original)) {
                for (String delimiter : delimiters) {
                    if (original.contains(delimiter)) {
                        tokens.set(i, original.substring(0, original.indexOf(delimiter)));
                        tokens.add(i + 1, original.substring(original.indexOf(delimiter), original.indexOf(delimiter) + delimiter.length()));
                        String next = original.substring(original.indexOf(delimiter) + delimiter.length());
                        if(next.length() > 0) tokens.add(i + 2, next);
                        original = tokens.get(i);
                    }
                }
            }
            tokens.removeIf((String test) -> test.equals(""));
        }
        
        tokenIndex = 0;
        tokenStringIndex = 0;
    
        Stack<SyntacticObject> inStack = new Stack<>();
        
        inStack.push(base);
        Reference<ParseNode> node = new Reference<>();
        if(!recursiveParseFunction(inStack, node)) return null;
        
        return new ParseTree(node.getRef());
    }
    
    LinkedList<String> splitString(String s){
        LinkedList<String> output = new LinkedList<>();
        
        StringBuilder word = new StringBuilder();
        boolean usingWhitespace = false;
        Pattern wordPattern = Pattern.compile("[^\\s]");
        for (int i = 0; i < s.length(); i++) {
            boolean check = wordPattern.matcher("" + s.charAt(i)).matches();
            if(check == usingWhitespace){
                usingWhitespace = !usingWhitespace;
                output.add(word.toString());
                word = new StringBuilder();
            }
            word.append(s.charAt(i));
        }
    
    
        output.removeIf((String test) -> test.equals(""));
        return output;
    }
    
    /**
     * Works like a single recursive rule
     * @param stack currentStack
     * @param parent parent ParseNode
     * @return if it successfully parsed
     */
    @SuppressWarnings("unchecked")
    private boolean recursiveParseFunction(Stack<SyntacticObject> stack, Reference<ParseNode> parent){
        
        
        
        while(!stack.empty()) {
            SyntacticObject current = stack.pop();
            
            
            
            if (current.getClass().equals(SyntacticCategory.class)) {
                SyntacticCategory category = (SyntacticCategory) current;
                boolean ignoreWhitespace = category.isIgnoreWhitespace();
                Reference<String> space = new Reference<>();
                while(ignoreWhitespace && match(Pattern.compile("\\s+"), space)){
                    consume(space.getRef());
                }
    
                boolean found = false;
                for (Rule rule : category.getRules()) {
                    Reference<ParseNode> next = new Reference<>(new ParseNode(category, rule));
                    //Stack<SyntacticObject> stackCopy = (Stack<SyntacticObject>) stack.clone();
                    Stack<SyntacticObject> stackCopy = new Stack<>();
                    loadStackBackwards(stackCopy, rule.getSyntacticObjects());
                    if (recursiveParseFunction(stackCopy, next)) {
                        if (parent.getRef() == null) {
                            parent.setRef(next.getRef());
                        } else {
                            parent.getRef().addChild(next.getRef());
                        }
                        found = true;
                        break;
                    }
                }
        
                if(!found){
                    if(!category.isOptional()) return false;
                }
            } else if (current.getClass().equals(Terminal.class)) {
        
                Terminal terminal = (Terminal) current;
                if (!consume(terminal.getRepresentation())){
                    return false;
                }
                if (parent.getRef() == null) {
                    parent.setRef(new ParseNode(terminal, terminal.getRepresentation()));
                } else {
                    parent.getRef().addChild(new ParseNode(terminal, terminal.getRepresentation()));
                }
                
        
            } else if (current.getClass().equals(RegexTerminal.class)) {
        
                RegexTerminal regexTerminal = (RegexTerminal) current;
                Reference<String> found = new Reference<>();
                if (!consume(regexTerminal.getPattern(), found)){
                    return false;
                }
        
                if (parent.getRef() == null) {
                    parent.setRef(new ParseNode(regexTerminal, found.getRef()));
                } else {
                    parent.getRef().addChild(new ParseNode(regexTerminal, found.getRef()));
                }
        
                
            }
        }
        
        return true;
    }
}
