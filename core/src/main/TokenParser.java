package main;

import structure.Reference;
import structure.TokenGrammar;
import structure.parse.ParseNode;
import structure.parse.ParseTree;
import structure.syntacticObjects.*;
import structure.syntacticObjects.tokenBased.TokenRegexTerminal;
import structure.syntacticObjects.tokenBased.TokenTerminal;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * TODO: Change this and token grammar so that certain syntactic categories must be marked as tokens
 * TODO: token categories can only accept one token at a time
 *
 */
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
    protected String currentToken(){
        return tokens.get(tokenIndex);
    }
    protected String nextToken(){
        if(tokenIndex + 1 == tokens.size()) return null;
        return tokens.get(tokenIndex + 1);
    }
    
    @Override
    protected boolean advancePointer() {
        if(tokenIndex == tokens.size()) return false;
        tokenStringIndex++;
        if(tokenStringIndex == tokens.get(tokenIndex).length()){
            tokenStringIndex = 0;
            tokenIndex++;
        }
        return true;
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
            if(tokenIndex == tokens.size()) break;
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
    public ParseTree parse(String s, SyntacticCategory base) {
        tokens = splitString(s);
        for (int i = 0; i < tokens.size(); i++) {
            String original = tokens.get(i);
            if(!delimiters.contains(original)) {
                for (String delimiter : delimiters) {
                    if (original.contains(delimiter)
                        && (original.indexOf(delimiter) == 0 ||
                            original.indexOf(delimiter) > 0 && original.charAt(original.indexOf(delimiter)-1) != '\\')) {
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
        
        /*
        for (int i = 0; i < tokens.size()-1; i++) {
            String original = tokens.get(i);
            if(original.equals("\\") && delimiters.contains(tokens.get(tokenIndex+1))){
                tokens.set(i, tokens.get(i+1));
                tokens.remove(i+1);
            }
        }
        */
        
        
        tokenIndex = 0;
        tokenStringIndex = 0;
    
        Stack<SyntacticObject> inStack = new Stack<>();
        
        inStack.push(base);
        Reference<ParseNode> node = new Reference<>();
        
        ((TokenGrammar) grammar).ensureTokenized();
        grammar.printGrammar();
        System.out.println("Using recursive parser...");
        if(!recursiveParseFunction(inStack, node)){
            return null;
        }
    
        
        ParseTree output = new ParseTree(node.getRef());
        for (String autoClean : grammar.getAutoCleans()) {
            output.clean(autoClean);
        }
        output.print();
        return output;
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
            if(usingWhitespace){
                output.add(word.toString());
                word = new StringBuilder();
            }
        }
        if(word.length()>0) output.add(word.toString());
        
        Pattern empty = Pattern.compile("\\s+");
        output.removeIf((String test) -> test.equals(""));
        output.removeIf((String test) -> !delimiters.contains(test) && empty.matcher(test).matches());
        return output;
    }
    
    private boolean tryAbsorbToken(String token){
        if(!currentToken().equals(token)) return false;
        tokenIndex++;
        tokenStringIndex = 0;
        return true;
    }
    
    private String absorbToken(){
        if(tokenIndex == tokens.size()) return null;
        String output = tokens.get(tokenIndex);
        tokenIndex++;
        tokenStringIndex = 0;
        return output;
    }
    
    private boolean absorbToken(Reference<String> ref){
        if(tokenIndex == tokens.size()) return false;
        ref.setRef(tokens.get(tokenIndex));
        tokenIndex++;
        tokenStringIndex = 0;
        return true;
    }
    
    boolean insideBreakerExpression = false;
    /**
     * Works like a single recursive rule
     * @param stack currentStack
     * @param parent parent ParseNode
     * @return if it successfully parsed
     */
    @SuppressWarnings("unchecked")
    private boolean recursiveParseFunction(Stack<SyntacticObject> stack, Reference<ParseNode> parent){
        
        
        
        while(!stack.empty() && tokenIndex < tokens.size()) {
            System.out.print(String.format("Lookahead(%d): %10s Stack: ", tokenIndex, currentToken().substring(0,
                    tokenStringIndex) +
                    "^" + currentToken().substring(tokenStringIndex)));
            printStack(stack);
            
            SyntacticObject current = stack.pop();
            
            
            
            if (current.getClass().equals(SyntacticCategory.class)) {
                SyntacticCategory category = (SyntacticCategory) current;
                //boolean ignoreWhitespace = category.isIgnoreWhitespace();
                //Reference<String> space = new Reference<>();
                /*while(ignoreWhitespace && match(Pattern.compile("\\s+"), space)){
                    consume(space.getRef());
                }
                */
    
                boolean found = false;
                for (Rule rule : category.getRules()) {
                    //System.out.println(rule);
                    int originalIndex, originalStringIndex;
                    originalIndex = tokenIndex;
                    originalStringIndex = tokenStringIndex;
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
                    }else if(!category.isOptional()){
                        tokenIndex = originalIndex;
                        tokenStringIndex = originalStringIndex;
                    }
                }
        
                if(!found){
                    if(!category.isOptional()){
                        if(parent.getRef() != null) parent.getRef().print(0);
                        return false;
                    }
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
        
                
            } else if (current.getClass().equals(TokenTerminal.class)){
                TokenTerminal tokenTerminal = (TokenTerminal) current;
                Reference<String> found = new Reference<>();
                if(tokenTerminal.isWildcardToken()){
                    absorbToken(found);
                }else{
                    String fixed;
                    if(!tokenTerminal.getRepresentation().equals("\\") &&
                            currentToken().equals("\\") &&
                            nextToken() != null &&
                            delimiters.contains(nextToken()) &&
                            !insideBreakerExpression){
                        absorbToken();
                        fixed = currentToken();
                    }else{
                        if(currentToken().equals("\\") && tokenTerminal.getRepresentation().equals("\\")){
                            insideBreakerExpression = !insideBreakerExpression;
                            System.out.println("INSIDE BREAKER EXPRESSION: " + insideBreakerExpression);
                        }
                        fixed =tokenTerminal.getRepresentation();
                    }
                    //String fixed = tokenTerminal.getRepresentation().replaceAll("\\\\(.)", "$1");
                    if(!fixed.equals(currentToken())) return false;
                    absorbToken(found);
                }
    
                if (parent.getRef() == null) {
                    parent.setRef(new ParseNode(tokenTerminal, found.getRef()));
                } else {
                    parent.getRef().addChild(new ParseNode(tokenTerminal, found.getRef()));
                }
            } else if(current.getClass().equals(TokenRegexTerminal.class)){
                TokenRegexTerminal tokenTerminal = (TokenRegexTerminal) current;
                Reference<String> found = new Reference<>();
                if(!consume(tokenTerminal.getPatternMatch(), found)) return false;
                //absorbToken(found);
                
                if (parent.getRef() == null) {
                    parent.setRef(new ParseNode(tokenTerminal, found.getRef()));
                } else {
                    parent.getRef().addChild(new ParseNode(tokenTerminal, found.getRef()));
                }
            }
        }
        
        return true;
    }
}
