package main;

import defaultGrammars.CgfFileGrammar;
import defaultGrammars.GrammarLoader;
import defaultGrammars.StandardGrammar;
import defaultGrammars.VarGrammar;
import misc.Tools;
import structure.Grammar;
import structure.parse.ParseTree;
import structure.parse.Parser;
import structure.syntacticObjects.Rule;
import structure.syntacticObjects.SyntacticCategory;

import java.io.File;
import java.util.regex.Pattern;

public class CommandLineRunner {
    
    public static void main(String[] args){
        try {
            Grammar grammar = new Grammar();
            grammar.addCategory("T");
            SyntacticCategory cat = grammar.getCategory("T");
            cat.addRule(new Rule("a", cat, "b"));
            cat.setOptional(true);
            Grammar.printExamples(5, cat);
    
    
            Grammar standard = new StandardGrammar();
            /*
            System.out.println(standard.getCategory("sentence").getFullRepresentation(7));
            System.out.println(standard.getCategory("integer").getFullRepresentation(7));
            System.out.println(standard.getCategory("double").getFullRepresentation(8));
            System.out.println(standard.getCategory("list_sentence").getFullRepresentation(10));
            standard.printGrammar();
            System.out.println();
            standard.printExamples(10, "sentence");
            System.out.println();
            standard.printExamples(10, "integer");
            System.out.println();
            standard.printExamples(5, "double");
            System.out.println();
            standard.printExamples(10, "list_sentence");
            System.out.println();
            
            Grammar var = new VarGrammar(standard.getCategory("string"), standard.getCategory("string_sentence"), "=>");
            var.printExamples(15, "set_string");
            
            Parser p = new Parser(standard);
            ParseTree tree = p.parse("\"hello\",\"No you!\"", standard.getCategory("list_sentence"));
            tree.clean("sentence");
            tree.print();
            */
            Grammar cfg = new CgfFileGrammar();
            cfg.printGrammar();
            System.out.println();
            cfg.printExamples(1);
            GrammarLoader grammarLoader = new GrammarLoader();
            Grammar math = grammarLoader.loadGrammar(new File("mathGrammar.ccfg"));
            math.printGrammar();
            Parser mathParser = new Parser(math);
            mathParser.parse("5+2*4").print();
        }catch (Rule.IncorrectTypeException e){
            e.printStackTrace();
        }
    }
}
