package defaultGrammars;

import defaultGrammars.CgfFileGrammar;
import jdk.jfr.Category;
import modules.IConvertModule;
import structure.Grammar;
import structure.parse.ParseNode;
import structure.parse.ParseTree;
import structure.parse.Parser;
import structure.syntacticObjects.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GrammarLoader {
    
    private HashMap<String, Grammar> hashMap;
    private Grammar cfgGrammar;
    private Parser parser;
    
    public GrammarLoader(){
        hashMap = new HashMap<>();
        cfgGrammar = new CgfFileGrammar();
        parser = new Parser(cfgGrammar);
    }
    
    public Grammar loadGrammar(String s){
        
        String parsableString = s.replaceAll("#\\w* [\\w<>.]*\\s", "")
                .replaceAll("\r", "")
                .replaceAll("//.*\\n","");
        ParseTree tree = parser.parse(parsableString);
        if(tree == null) return null;
        Pattern preOptions = Pattern.compile("#\\w* [\\w<>.]*\n");
        
        Grammar output = new Grammar();
        Matcher matcher = preOptions.matcher(s.replaceAll("\r", ""));
    
        while (matcher.find()) {
            String optionFull = matcher.group().replaceAll("[\n\r\t]", "");
            String optionName = optionFull.split(" ")[0].substring(1);
            String optionVariables = optionFull.split(" ")[1];
            switch (optionName){
                case "import":{
                    Pattern specialReference = Pattern.compile("<\\w*>");
                    Matcher m = specialReference.matcher(optionVariables);
                    if(m.matches()){
                        switch (optionVariables.substring(1, optionVariables.length()-1)){
                            case "standard":
                                output.inherit(new StandardGrammar());
                                break;
                        }
                    }else{
                        output.inherit(loadGrammar(new File(optionVariables)));
                    }
                }
                    break;
            }
        }
       
        
        return convertParseTreeToGrammar(tree, output);
    }
    
    public Grammar loadGrammar(File f){
        try {
            if(!f.getName().endsWith(".ccfg")) return null;
            String grammar = new String(Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8);
            return loadGrammar(grammar);
        }catch (IOException e){
            System.err.println("File not found");
            return null;
        }
    }
    
    public Grammar convertParseTreeToGrammar(ParseTree g, Grammar pregenerated){
        if(!g.baseType().equals("grammar")) return null;
        g.removeEmptyNodes();
        g.clean("string");
        g.clean("sentence");
        g.print();
        
        Grammar output = pregenerated;
    
        List<ParseNode> categoryNodes = new ListGrammar.ListGrammarConverter().convertParseNode(g.getHead().getChild("list_category"));
        HashMap<String, Boolean> useRegex = new HashMap<>();
        /*
            first add all categories into the grammar
         */
        for (ParseNode categoryNode : categoryNodes) {
            String name =  categoryNode.getChild("string").getChildTerminals();
            if(!output.containsCategory(name)) output.addCategory(name);
            if(categoryNode.contains("options")){
                List<ParseNode> options = new ListGrammar.ListGrammarConverter().convertParseNode(
                        categoryNode.getChild("options").getChild("list_string"));
    
                for (ParseNode option : options) {
                    String optionChildTerminals = option.getChildTerminals().toLowerCase();
                    switch (optionChildTerminals){
                        case "optional":
                            output.getCategory(name).setOptional(true);
                            break;
                        case "default":
                        case "head":
                            output.setDefault(output.getCategory(name));
                            break;
                        case "regex":
                        case "pattern":
                            useRegex.put(name, true);
                            break;
                    }
                }
                
                
            }
            if(!useRegex.containsKey(name)) useRegex.put(name, false);
        }
    
        for (ParseNode categoryNode : categoryNodes) {
            String name =  categoryNode.getChild("string").getChildTerminals();
            SyntacticCategory category = output.getCategory(name);
            ParseNode ruleList = categoryNode.getChild("list_rule");
            List<ParseNode> ruleNodes = new ListGrammar.ListGrammarConverter().convertParseNode(ruleList);
            for (ParseNode ruleNode : ruleNodes) {
                List<ParseNode> ruleParts = convertModuleRuleParts.convertParseNode(ruleNode.getChild("rule_part"));
                
                StringBuilder regex = new StringBuilder();
                ArrayList<SyntacticObject> syntacticObjects = new ArrayList<>();
                
                for (ParseNode rulePart : ruleParts) {
                    if(rulePart.getData().equals("rule_char")){
                        if(useRegex.get(name)){
                            regex.append(rulePart.getChildTerminals());
                        }else{
                            syntacticObjects.addAll(Grammar.createTerminals(rulePart.getChildTerminals()));
                        }
                    }else if(rulePart.getData().equals("rule_reference")){
                        String builtRegex = regex.toString();
                        if(builtRegex.length() > 0) {
                            RegexTerminal regexTerminal = new RegexTerminal(builtRegex);
                            regex = new StringBuilder();
                            syntacticObjects.add(regexTerminal);
                        }
                        
                        String catName = rulePart.getChild("string").getChildTerminals();
                        syntacticObjects.add(output.getCategory(catName));
                    }
                }
    
                String builtRegex = regex.toString();
                if(builtRegex.length() > 0) {
                    RegexTerminal regexTerminal = new RegexTerminal(builtRegex);
                    syntacticObjects.add(regexTerminal);
                }
                
                category.addRule(new Rule(syntacticObjects));
            }
        }
        
        
        return output;
    }
    
    private IConvertModule<List<ParseNode>> convertModuleRuleParts = new IConvertModule<>() {
        @Override
        public List<ParseNode> convertParseNode(ParseNode p) {
            ArrayList<ParseNode> output = new ArrayList<>();
            ParseNode ptr =p;
            while(ptr != null){
                if(ptr.getData().equals("rule_tail")) ptr = ptr.getChild("rule_part");
                ParseNode object= ptr.getChild("rule_head").getChild(0);
                output.add(object);
                ptr = ptr.getChild("rule_tail");
            }
    
            return output;
        }
        
        @Override
        public List<ParseNode> convertParseTree(ParseTree p) {
            return null;
        }
    };
}
