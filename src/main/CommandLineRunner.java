package main;

import structure.Grammar;
import structure.syntacticObjects.Rule;
import structure.syntacticObjects.SyntacticCategory;

public class CommandLineRunner {
    
    public static void main(String[] args){
        try {
            Grammar grammar = new Grammar();
            grammar.addOptionalCategory("T");
            SyntacticCategory cat = grammar.getCategory("T");
            cat.addRule(new Rule("a", cat, "b"));
    
            System.out.println(cat.getFullRepresentation(2));
    
        }catch (Rule.IncorrectTypeException e){
            e.printStackTrace();
        }
    }
}
