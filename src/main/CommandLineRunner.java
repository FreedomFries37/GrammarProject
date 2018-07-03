package main;

import misc.StandardGrammar;
import structure.Grammar;
import structure.syntacticObjects.Rule;
import structure.syntacticObjects.SyntacticCategory;

public class CommandLineRunner {
    
    public static void main(String[] args){
        try {
            Grammar grammar = new Grammar();
            grammar.addCategory("T");
            SyntacticCategory cat = grammar.getCategory("T");
            cat.addRule(new Rule("a", cat, "b"));
            cat.setOptional(true);
            System.out.println(cat.getFullRepresentation(2));
    
    
            Grammar standard = new StandardGrammar();
            System.out.println(standard.getCategory("sentence").getFullRepresentation(7));
            System.out.println(standard.getCategory("integer").getFullRepresentation(7));
            System.out.println(standard.getCategory("double").getFullRepresentation(15));
            standard.printExamples(10, "sentence");
            standard.printExamples(10, "integer");
            System.out.println();
            standard.printExamples(5, "double");
        }catch (Rule.IncorrectTypeException e){
            e.printStackTrace();
        }
    }
}
