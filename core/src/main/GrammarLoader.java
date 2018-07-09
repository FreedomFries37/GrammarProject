package main;

import main.defaultGrammars.CgfFileGrammar;
import main.defaultGrammars.ListGrammar;
import main.defaultGrammars.StandardGrammar;
import main.defaultGrammars.VarGrammar;
import modules.IConvertModule;
import structure.Grammar;
import structure.TokenGrammar;
import structure.parse.ParseNode;
import structure.parse.ParseTree;
import structure.syntacticObjects.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GrammarLoader {
    
    private HashMap<String, Grammar> hashMap;
    private Grammar cfgGrammar;
    private Parser parser;
    private static String[] grammarDelimeters;
    
    static {
        grammarDelimeters = new String[]{
                "{",
                "}",
                ":",
                "<",
                ">",
                ":=",
                "->",
                "[",
                "]",
                "(",
                ")"
        };
    }
    
    public GrammarLoader(){
        hashMap = new HashMap<>();
        cfgGrammar = new CgfFileGrammar();
        parser = new Parser(cfgGrammar);
    }
    
    public Grammar loadGrammar(String s){
        return loadGrammar(s, new Grammar());
    }
    
    public Grammar loadGrammar(String s, Grammar passOff){
        
        String parsableString = s.replaceAll("#\\w* [\\w<>.]*\\s", "")
                .replaceAll("\r", "")
                .replaceAll("//.*\\n","");
        while (parsableString.endsWith("\n")){
            parsableString = parsableString.substring(0, parsableString.length()-1);
        }
        parsableString += "\n";
     
        Pattern preOptions = Pattern.compile("#\\w* [\\w<>.]*\n");
        
        Grammar output = new Grammar();
        output.inherit(passOff);
        Matcher matcher = preOptions.matcher(s.replaceAll("\r", ""));
        boolean useExtended = false;
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
                        passOff.inherit(loadGrammar(new File(optionVariables), passOff));
                    }
                }
                break;
                case "standard":
                    if(optionVariables.equals("extended")){
                        useExtended = true;
                        cfgGrammar = loadGrammar(new File("cfgGrammarExtended.ccfg"), cfgGrammar);
                        cfgGrammar = new TokenGrammar(cfgGrammar, grammarDelimeters);
                    }
                    break;
                case "parse":
                    if(optionVariables.equals("token")){
                        output.setTokenParse(true);
                    }
                    break;
            }
        }
       
        
        if(!useExtended){
            ParseTree tree = parser.parse(parsableString);
            if(tree == null) return null;
            return convertParseTreeToGrammar(tree, output);
        }
        parser = new TokenParser((TokenGrammar) cfgGrammar);
        ParseTree tree = parser.parse(parsableString);
        if(tree == null) return null;
        return convertTokenizedParseTreeToGrammar(tree, output);
    }
    
    public Grammar loadGrammar(File f){
        return loadGrammar(f, new Grammar());
    }
    
    public Grammar loadGrammar(File f, Grammar passOff){
        try {
            if(!f.getName().endsWith(".ccfg")) return null;
            String grammar = new String(Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8);
            return loadGrammar(grammar, passOff);
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
                        case "override":
                            output.resetCategory(output.getCategory(name));
                            break;
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
                        case "clean":
                            output.addAutoClean(name);
                            break;
                        case "whitespace":
                            output.getCategory(name).setIgnoreWhitespace(false);
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
                    }else if(rulePart.getData().equals("rule_escape")){
                        String builtRegex = regex.toString();
                        if(builtRegex.length() > 0) {
                            RegexTerminal regexTerminal = new RegexTerminal(builtRegex);
                            regex = new StringBuilder();
                            syntacticObjects.add(regexTerminal);
                        }
                        
                        
                        ParseNode namedAction = rulePart.getChild("named_action");
                        String namedActionName = namedAction.getChild("string").getChildTerminals();
                        switch (namedActionName){
                            case "list":{
                                ArrayList<ParseNode> params =
                                        new ListGrammar.ListGrammarConverter().convertParseNode(namedAction.getChild(
                                                "named_action_parameters").getChild("list_parameter"));
                                if(params.size() == 1 || params.size() == 2){
                                    List<String> objects = new ArrayList<>();
                                    for (ParseNode param :
                                            params) {
                                        if(param.contains("rule_reference")){
                                            String catName = param.getChild("rule_reference").getChild(
                                                    "string").getChildTerminals();
                                            objects.add(catName);
                                        }else if(param.contains("sentence")){
                                            String string = param.getChild("sentence").getChildTerminals();
                                            objects.add(string.substring(1, string.length()-1));
                                        }
                                    }
                                    if(objects.size() == 1) {
                                        output.inheritAndReplace(new ListGrammar(output.getCategory(objects.get(0))));
                                    }else if(objects.size() == 2){
                                        output.inheritAndReplace(new ListGrammar(output.getCategory(objects.get(0)),
                                                objects.get(1)));
                                    }
                                    SyntacticCategory list = output.getCategory("list_" + objects.get(0));
                                    syntacticObjects.add(list);
                                   
                                } else{
                                    System.err.println("Incorrect amount of parameters");
                                }
                            }
                            break;
                            case "var": {
                                ArrayList<ParseNode> params =
                                        new ListGrammar.ListGrammarConverter().convertParseNode(namedAction.getChild(
                                                "named_action_parameters").getChild("list_parameter"));
                                SyntacticCategory cat1 = output.getCategory(params.get(0).getChild("rule_reference").getChild(
                                        "string").getChildTerminals());
                                
                                if(params.size() == 2){
                                    SyntacticCategory cat2 = output.getCategory(params.get(1).getChild(
                                            "rule_reference").getChild(
                                            "string").getChildTerminals());
                                    output.inherit(new VarGrammar(cat1,cat2));
                                }else if(params.size() == 3){
                                    String string =
                                            StandardGrammar.convertSentence.convertParseNode(params.get(2).getChild(
                                                    "sentence"));
    
                                    SyntacticCategory cat2 = output.getCategory(params.get(1).getChild(
                                            "rule_reference").getChild(
                                            "string").getChildTerminals());
                                    output.inherit(new VarGrammar(cat1,cat2,string));
                                }else{
                                    System.err.println("Incorrect amount of parameters");
                                }
                                syntacticObjects.add(output.getCategory("set_" + cat1.getName()));
                            }
                                break;
                            case "regex":
                            case "pattern":
                                ArrayList<ParseNode> params =
                                        new ListGrammar.ListGrammarConverter().convertParseNode(namedAction.getChild(
                                                "named_action_parameters").getChild("list_parameter"));
                                for (ParseNode param : params) {
                                    String string =
                                            StandardGrammar.convertSentence.convertParseNode(param.getChild(
                                                    "sentence"));
                                    syntacticObjects.add(new RegexTerminal(Pattern.compile(string)));
                                }
                        }
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
    
    public Grammar convertTokenizedParseTreeToGrammar(ParseTree g, Grammar pregenerated){
        
        return convertParseTreeToGrammar(g, pregenerated);
    }
}
