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
import structure.syntacticObjects.tokenBased.TokenRegexTerminal;
import structure.syntacticObjects.tokenBased.TokenTerminal;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GrammarLoader {
    
    private HashMap<String, Grammar> hashMap;
    private Grammar cfgGrammar;
    private boolean extended;
    private boolean firstPass;
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
                ")",
                "\t",
                ";",
                ",",
                "\\",
                "\""
        };
    }
    
    public GrammarLoader(){
        hashMap = new HashMap<>();
        cfgGrammar = new CgfFileGrammar();
        extended = false;
        firstPass = true;
        parser = new Parser(cfgGrammar);
    }
    
    public Grammar loadGrammar(String s){
        return loadGrammar(s, new Grammar());
    }
    
    public Grammar loadGrammar(String s, Grammar passOff){
        return loadGrammar(s, passOff, false);
    }
    
    public Grammar loadGrammar(String s, Grammar passOff, boolean autoExtended){
        
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
                        if(!extended) {
                            useExtended = true;
                            if(firstPass) {
                                cfgGrammar = loadGrammar(new File("cfgGrammarExtendedBootstrapper.ccfg"), cfgGrammar);
                                cfgGrammar = new TokenGrammar(cfgGrammar, grammarDelimeters);
                                firstPass = false;
                            }
                            
                            cfgGrammar = loadTokenGrammar(new File("cfgGrammarExtended.eccfg"), cfgGrammar);
                            extended = true;
                        }
                       
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
            if(!autoExtended) {
                if (extended) cfgGrammar = new CgfFileGrammar();
                firstPass = true;
                ParseTree tree = parser.parse(parsableString);
                if (tree == null) return null;
                return convertParseTreeToGrammar(tree, output);
            }
            if(!extended) {
                if (firstPass) {
                    cfgGrammar = loadGrammar(new File("cfgGrammarExtendedBootstrapper.ccfg"), cfgGrammar);
                    cfgGrammar = new TokenGrammar(cfgGrammar, grammarDelimeters);
                    firstPass = false;
                    cfgGrammar = loadTokenGrammar(new File("cfgGrammarExtended.eccfg"), cfgGrammar);
                    extended = true;
                }
    
               
            }
            
        }
        List<String> delimiterPassOf;
        if(passOff.getClass().equals(TokenGrammar.class)){
            delimiterPassOf = ((TokenGrammar) passOff).getDelimiters();
        }else delimiterPassOf = new ArrayList<>();
        parser = new TokenParser((TokenGrammar) cfgGrammar);
        ParseTree tree = parser.parse(parsableString);
        if(tree == null) return null;
        return convertTokenizerParseTreeToGrammar(tree, output, delimiterPassOf);
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
    
    public TokenGrammar loadTokenGrammar(File f){
        return loadTokenGrammar(f, new Grammar());
    }
    public TokenGrammar loadTokenGrammar(File f, Grammar passOff){
        try {
            if(!f.getName().endsWith(".eccfg")) return null;
            String grammar = new String(Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8);
            return (TokenGrammar) loadGrammar(grammar, passOff, true);
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
                        case "invisible":
                            output.getCategory(name).setInvisible(true);
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
                            case "unordered":{
                            
                            }
                            break;
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
    
    public TokenGrammar convertTokenizerParseTreeToGrammar(ParseTree g, Grammar pregenerated, Collection<String> delimiters){
        if(!g.baseType().equals("grammar")) return null;
        g.removeEmptyNodes();
        g.clean("string");
        g.clean("sentence");
        g.print();
        TokenGrammar output = new TokenGrammar(pregenerated);
        List<ParseNode> categoryNodes;
        if(extended )categoryNodes =
                new ListGrammar.ListGrammarConverter().convertParseNodeMustHaveChild(g.getHead().getChild(
                "list_block"), "category");
        else categoryNodes = new ListGrammar.ListGrammarConverter().convertParseNode(g.getHead().getChild(
                "list_category"));
    
        HashMap<String, Boolean> useRegex = new HashMap<>();
        
        categoryNodes.sort(new Comparator<>() {
            private int converter(ParseNode p){
               if(p.contains("cat")){
                   return 1;
               }
               else return -1;
            }
            
            @Override
            public int compare(ParseNode o1, ParseNode o2) {
                return converter(o1) - converter(o2);
            }
        });
        
        
        for (ParseNode categoryNode : categoryNodes) {
            String name;
            boolean isToken;
            if(categoryNode.contains("cat")) {
                name = categoryNode.getChild("string").getChildTerminals();
                isToken = false;
            }else if(categoryNode.contains("token")){
                name = categoryNode.getChild("token_specifier").getChildTerminals();
                isToken = true;
                
            }else continue;
            
            
            if(!isToken && !output.containsCategory(name)) output.addCategory(name);
            else if (isToken) output.addToken(name);
            
            
            if(categoryNode.contains("options")){
                List<ParseNode> options = new ListGrammar.ListGrammarConverter().convertParseNode(
                        categoryNode.getChild("options").getChild("list_string"));
            
                for (ParseNode option : options) {
                    String optionChildTerminals = option.getChildTerminals().toLowerCase();
                    switch (optionChildTerminals){
                        case "override":
                            if(!isToken) {
                                output.resetCategory(output.getCategory(name));
                            }else{
                                if(output.containsCategory(name)) output.removeCategory(name);
                                //output.addToken(name);
                            }
                            break;
                        case "optional":
                            if (isToken){
                                System.err.println("Not an available option for tokens");
                                break;
                            }
                            output.getCategory(name).setOptional(true);
                            break;
                        case "default":
                        case "head":
                            if (isToken){
                                System.err.println("Not an available option for tokens");
                                break;
                            }
                            output.setDefault(output.getCategory(name));
                            break;
                        case "regex":
                        case "pattern":
                            if (isToken){
                                System.err.println("Not an available option for tokens");
                                break;
                            }
                            useRegex.put(name, true);
                            break;
                        case "clean":
                            if (isToken){
                                System.err.println("Not an available option for tokens");
                                break;
                            }
                            output.addAutoClean(name);
                            break;
                        case "whitespace":
                            if (isToken){
                                System.err.println("Not an available option for tokens");
                                break;
                            }
                            output.getCategory(name).setIgnoreWhitespace(false);
                            break;
                        case "invisible":
                            if (isToken){
                                System.err.println("Not an available option for tokens");
                                break;
                            }
                            output.getCategory(name).setInvisible(true);
                            break;
                    }
                }
            
            
            }
            if(!useRegex.containsKey(name)) useRegex.put(name, false);
        }
    
        for (ParseNode categoryNode : categoryNodes) {
            String name;
            boolean isToken;
            if(categoryNode.contains("cat")) {
                name = categoryNode.getChild("string").getChildTerminals();
                isToken = false;
            }else if(categoryNode.contains("token")){
                name = categoryNode.getChild("token_specifier").getChildTerminals();
                isToken = true;
        
            }else continue;
    
        
            if(isToken){
                ParseNode tokenEndingNode = categoryNode.getChild("token_endings");
                if(tokenEndingNode.contains("sentence")){
                    String str = tokenEndingNode.getChild("sentence").getChildTerminals();
                    str = str.substring(1, str.length()-1);
                    output.changeToken(name,
                            new TokenTerminal(str));
                }else if(tokenEndingNode.contains("regex_wrapper")){
                    String str = tokenEndingNode.getChild("regex_wrapper").getChild("sentence").getChildTerminals();
                    str = str.substring(1, str.length()-1);
                    output.changeToken(name,
                            new TokenRegexTerminal(str));
                }else{
                    output.changeToken(name, new TokenTerminal());
                }
            }else{
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
                            if(output.containsCategory(catName)) syntacticObjects.add(output.getCategory(catName));
                            else if(output.containsToken(catName)){
                                syntacticObjects.add(output.getToken(catName));
                            }
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
                                        
                                        boolean isListPartToken = output.containsToken(objects.get(0));
                                        
                                        if(!isListPartToken) {
                                            if (objects.size() == 1) {
                                                output.inheritAndReplace(new ListGrammar(output.getCategory(objects.get(0))));
                                            } else if (objects.size() == 2) {
                                                output.inheritAndReplace(new ListGrammar(output.getCategory(objects.get(0)),
                                                        objects.get(1)));
                                            }
                                        }else{
                                            if (objects.size() == 1) {
                                                output.inheritAndReplace(new ListGrammar(output.getToken(objects.get(0))));
                                            } else if (objects.size() == 2) {
                                                output.inheritAndReplace(new ListGrammar(output.getToken(objects.get(0)),
                                                        objects.get(1)));
                                            }
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
        }
    
        for (SyntacticCategory category : output.getAllCategories()) {
            for (Rule rule : category.getRules()) {
                for (int i = 0; i < rule.getSyntacticObjects().size(); i++) {
                    SyntacticObject o = rule.getSyntacticObjects().get(i);
                    if(o.getClass().equals(SyntacticCategory.class)){
                        SyntacticCategory syntacticCategory = (SyntacticCategory) o;
                        if(!output.containsCategory(syntacticCategory.getName())){
                            rule.getSyntacticObjects().set(i, output.getToken(syntacticCategory.getName()));
                        }
                    }
                }
            }
        }
        
        
        output.setDelimiters(new ArrayList<>(delimiters));
        List<ParseNode> delimiterList = new ListGrammar.ListGrammarConverter().convertParseNode(g.getHead().getChild(
                "delimiters").getChild("list_delimiter"));
        for (ParseNode parseNode : delimiterList) {
            String delim = parseNode.getChild("sentence").getChildTerminals();
            delim = delim.substring(1, delim.length()-1);
            output.getDelimiters().add(
                   delim
            );
        }
        return output;
    }
}
