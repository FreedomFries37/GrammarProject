package main;

import main.defaultGrammars.CgfFileGrammar;
import main.defaultGrammars.StandardGrammar;
import structure.Grammar;
import structure.TokenGrammar;
import structure.parse.ParseTree;
import structure.syntacticObjects.Rule;
import structure.syntacticObjects.SyntacticCategory;

import java.io.File;

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
            GrammarLoader grammarLoader = new GrammarLoader();
            /*
            TokenGrammar math = grammarLoader.loadTokenGrammar(new File("mathGrammar.eccfg"));
            math.printCategoryNames();
            System.out.println();
            math.printGrammar();
            TokenParser mathParser = new TokenParser(math);
            ParseTree v = mathParser.parse(".let y := 3+5*278.7-6");
            String orginal = v.getHead().getChildTerminals();
            v.print();
            v.removeAllTerminalsExceptForChildrenOf("named_string", "named_string_init", "double", "integer",
                    "method_name","group_tail",
                    "expression_tail","factor");
            v.removeEmptyNodes();
            v.print();
            System.out.println(orginal);
            v.printTerminals();
            math.printExamples(1, 11);
            */
            TokenGrammar basic = grammarLoader.loadTokenGrammar(new File("radinBasic.eccfg"));
            basic.printGrammar();
            basic.printExamples(5, "boolean_expression", 8);
            TokenParser basicParser = new TokenParser(basic);
            //basicParser.parse("int aa;aa=TRUE;aa+=2").print();
        }catch (Rule.IncorrectTypeException e){
            e.printStackTrace();
        }
    }
}
