package main;

import structure.Grammar;
import structure.Reference;
import structure.parse.ParseNode;
import structure.parse.ParseTree;
import structure.syntacticObjects.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    
    protected Grammar grammar;
    private String parsableString;
    private int index;
    
    public Parser(Grammar grammar) {
        this.grammar = grammar;
    }
    
    protected  char currentChar(){
        if(index == parsableString.length()) return '\0';
        return parsableString.charAt(index);
    }
    
    protected  boolean advancePointer(){
        if(index == parsableString.length()) return false;
        index++;
        return true;
    }
    
    protected boolean match(char c){
        return currentChar() == c;
    }
    
    protected boolean match(String s){
        return parsableString.substring(index).startsWith(s);
    }
    
    protected boolean match(Pattern p){
        int length = 1;
        String check = parsableString.substring(index, index+length);
        while(index + length < parsableString.length()){
            Matcher m = p.matcher(check);
            
            if(m.matches()) return true;
            
            length++;
        }
        
        return false;
    }
    
    protected boolean match(Pattern p, Reference<String> outString){
        int length = 1;
        String check;
        String output = null;
        boolean found = false;
        while(index + length <= parsableString.length()){
            check = parsableString.substring(index, index+length);
            Matcher m = p.matcher(check);
        
            if(m.matches()){
                output = check;
                found = true;
            }else{
                if(found){
                    outString.setRef(output);
                    return true;
                }
            }
        
            length++;
            
        }
        if(found){
            outString.setRef(output);
            return true;
        }
        return false;
    }
    
    protected boolean consume(char c){
        if(!match(c)) return false;
        return advancePointer();
    }
    
    protected boolean consume(String s){
        if(!match(s)) return false;
        for (int i = 0; i < s.length(); i++) {
            if(!advancePointer()) return false;
        }
        return true;
    }
    
    protected boolean consume(Pattern p){
        Reference<String> consumeString = new Reference<>();
        if(!match(p, consumeString)) return false;
        return consume(consumeString.getRef());
    }
    
    protected boolean consume(Pattern p, Reference<String> ref){
        if(!match(p, ref)) return false;
        return consume(ref.getRef());
    }
    
    public ParseTree parse(String s){
        if(!grammar.hasDefault()){
            System.err.println("No Default Grammar Rule");
            return null;
        }
        return parse(s, grammar.getDefault());
    }
    
    public ParseTree parse(File file) throws IOException{
        if(!grammar.hasDefault()){
            System.err.println("No Default Grammar Rule");
            return null;
        }
        return parse(new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8));
    }
    
    public ParseTree parse(File file, SyntacticCategory base) throws IOException {
        return parse(new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8), base);
    }
    
    public ParseTree parse(String s, SyntacticCategory base){
        Stack<SyntacticObject> stack = new Stack<>();
        Reference<ParseNode> head = new Reference<>();
        stack.push(base);
        parsableString = s;
        index = 0;
        
        while(!stack.empty()){
            System.out.print("Lookahead: " + currentChar() + "  Stack: ");
            printStack(stack);
            SyntacticObject current = stack.pop();
        
            if(current.getClass().equals(SyntacticCategory.class)){
                SyntacticCategory category = (SyntacticCategory) current;
                Rule next = getRuleFromLookaheadAndHashMap(currentChar(), category.lookAheadToRuleMap());
                if(category.isOptional()){
                    if (next != null){
                        loadStackBackwards(stack, next.getSyntacticObjects());
                    }
                }else {
                    if (next == null) return null;
                    loadStackBackwards(stack, next.getSyntacticObjects());
                }
                
                if(head.getRef() == null){
                    head.setRef(new ParseNode(category, next));
                }else{
                    head.getRef().leftMostOpenParseNode().addChild(new ParseNode(category, next));
                }
            }else if(current.getClass().equals(Terminal.class)){
                
                Terminal terminal = (Terminal) current;
                if(!consume(terminal.getRepresentation())) return null;
                if(head.getRef() == null){
                    head.setRef(new ParseNode(terminal, terminal.getRepresentation()));
                }else{
                    head.getRef().leftMostOpenParseNode().addChild(new ParseNode(terminal, terminal.getRepresentation()));
                }
                
                
            }else if(current.getClass().equals(RegexTerminal.class)){
                
                RegexTerminal regexTerminal = (RegexTerminal) current;
                Reference<String> found = new Reference<>();
                if(!consume(regexTerminal.getPattern(), found)) return null;
    
                if(head.getRef() == null){
                    head.setRef(new ParseNode(regexTerminal, found.getRef()));
                }else{
                    head.getRef().leftMostOpenParseNode().addChild(new ParseNode(regexTerminal, found.getRef()));
                }
            }
           
            
        }
        System.out.println("Lookahead: " + currentChar() + "  Stack: ");
        printStack(stack);
        if(index != parsableString.length()) return null;
        ParseTree output = new ParseTree(head.getRef(), grammar);
        return output;
    }
    
    protected static void printStack(Stack<SyntacticObject> stack){
        for (int i = stack.size() - 1; i >= 0; i--) {
            System.out.print(stack.get(i).getRepresentation());
        }
        System.out.println();
    }
    
    @SuppressWarnings("unchecked")
    protected static <T> void loadStackBackwards(Stack<T> stack, Collection<? extends T> items){
        T[] array = (T[]) items.toArray();
        for (int i = items.size() - 1; i >= 0; i--) {
            stack.push(array[i]);
        }
    }
    
    protected static Rule getRuleFromLookaheadAndHashMap(char lookAhead, HashMap<Pattern, Rule> map){
        if(map.containsKey(Pattern.compile(Pattern.quote("" + lookAhead)))){
            return map.get(Pattern.compile(Pattern.quote("" + lookAhead)));
        }else{
            for (Pattern pattern: map.keySet()) {
                if(pattern.matcher("" + lookAhead).matches()){
                    return map.get(pattern);
                }
            }
        }
        return null;
    }
    
    
    
}
