package structure.syntacticObjects;

import interaction.TokenParser;
import misc.Tools;
import structure.Reference;
import structure.syntacticObjects.Terminals.tokenBased.Token;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.ToLongBiFunction;
import java.util.regex.Pattern;

public class SyntacticFunction extends SyntacticObject {
    
    private interface IPrintableTree{
        String printTree(int indent);
    }
    public interface IBooleanNode extends IPrintableTree{
        boolean getValue();
    }
    private abstract class FunctionNode implements IPrintableTree{
        private FunctionNode children[];
        
        FunctionNode(int childSize){
            children = new FunctionNode[childSize];
        }
    
        FunctionNode[] getChildren() {
            return children;
        }
        
        FunctionNode getChild(int i){
            return children[i];
        }
        
        public abstract Rule calculateRule();
    }
    
    
    public class RuleNode extends FunctionNode{
        private Rule rule;
    
        RuleNode(Rule rule) {
            super(0);
            this.rule = rule;
        }
        
        RuleNode(Object... objects){
            super(0);
            try {
                this.rule = new Rule(name, objects);
            }catch (Rule.IncorrectTypeException e){
                e.printStackTrace();
            }
        }
    
        public Rule getRule() {
            return rule;
        }
    
        @Override
        public Rule calculateRule() {
            return getRule();
        }
    
        @Override
        public String toString() {
            return rule.toString();
        }
    
        @Override
        public String printTree(int indent) {
            return Tools.indent(indent) + rule.toString();
        }
    }
    
    public class ControlNode extends FunctionNode{
        private IBooleanNode booleanTree;
    
        public ControlNode(IBooleanNode booleanNode, FunctionNode nextNode) {
            super(2);
            this.booleanTree = booleanNode;
            getChildren()[0] = nextNode;
        }
        
        public ControlNode(IBooleanNode booleanNode, FunctionNode nextNode, FunctionNode elseNode) {
            super(2);
            this.booleanTree = booleanNode;
            getChildren()[0] = nextNode;
            getChildren()[1] = elseNode;
        }
    
        @Override
        public Rule calculateRule() {
            if(booleanTree.getValue()) return getChild(0).calculateRule();
            if(getChild(1) != null) return getChild(1).calculateRule();
            else return null;
        }
    
        @Override
        public String printTree(int indent) {
            String output = String.format("%sif (\n%s%s) {\n%s\n%s}",
                    Tools.indent(indent),
                    booleanTree.printTree(indent+1),
                    Tools.indent(indent),
                    getChild(0).printTree(indent+1),
                    Tools.indent(indent));
            if(getChild(1) != null) output += " else\n" + getChild(1).printTree(indent+1);
            
            return output;
        }
    }
    
   
    
    // TODO: check if references are needed
    private class ConditionalNode<T, K> implements IBooleanNode{
        protected ConditionalType conditionalType;
        protected boolean strictlyNumeric;
        protected T obj1;
        protected K obj2;
    
        public ConditionalNode(ConditionalType conditionalType, T obj1, K obj2) {
            this.conditionalType = conditionalType;
            this.obj1 = obj1;
            this.obj2 = obj2;
            if(conditionalType == ConditionalType.lessThan || conditionalType == ConditionalType.greaterThan) {
                strictlyNumeric = true;
            }
            
        }
        
        @Override
        public boolean getValue(){
            if(strictlyNumeric) return conditionalType.getValue((Number) obj1, (Number) obj2);
            return conditionalType.getValue(obj1, obj2);
        }
    
        @Override
        public String printTree(int indent) {
            return Tools.indent(indent) + obj1.toString() + " " + conditionalType.identifier + " " + obj2.toString() + "\n";
        }
    }
    
    private class HalfMethodConditionalNode<T,K> extends ConditionalNode<IMethodNode<T,K>,T>{
        private Collection<Object> args;
        private Reference<K> base;
        
        public HalfMethodConditionalNode(ConditionalType conditionalType, IMethodNode<T, K> obj1, Reference<K> base,
                                         T obj2,
                                         Collection<Object> args) {
            super(conditionalType, obj1, obj2);
            this.args = args;
            this.base = base;
        }
    
        @Override
        public boolean getValue() {
            try {
                if (strictlyNumeric) return conditionalType.getValue(
                        (Number) obj1.getValue(base.getRef(), args),
                        (Number) obj2);
                return conditionalType.getValue(obj1.getValue(base.getRef(), args), obj2);
            }catch (IncorrectAmountOfArgumentsException e){
                e.printStackTrace();
            }
            
            return false;
        }
    
        @Override
        public String printTree(int indent) {
            StringBuilder str = new StringBuilder("(");
            for (int i = 0; i < args.size()-1; i++) {
                str.append(args.toArray()[i].toString());
                str.append(",");
            }
            str.append(args.toArray()[args.size()-1].toString());
            str.append(")");
            return Tools.indent(indent) + obj1.toString() + str.toString() + " " + conditionalType.identifier + " " + obj2.toString() + "\n";
        }
    }
    
    private class FullMethodConditionalNode<T,K> extends ConditionalNode<IMethodNode<T,K>, IMethodNode<T,K>>{
        private Collection<Object> args1;
        private Collection<Object> args2;
        private Reference<K> base;
        
        public FullMethodConditionalNode(ConditionalType type,
                                         Reference<K> base,
                                         IMethodNode<T, K> obj1, Collection<Object> args1,
                                         IMethodNode<T, K> obj2, Collection<Object> args2){
            super(type,obj1,obj2);
            this.args1 = args1;
            this.args2 = args2;
            this.base =base;
        }
    
        @Override
        public boolean getValue() {
            try {
                if (strictlyNumeric) return conditionalType.getValue(
                        (Number) obj1.getValue(base.getRef(), args1),
                        (Number) obj2.getValue(base.getRef(), args2));
                return conditionalType.getValue(obj1.getValue(base.getRef(), args2), obj2.getValue(base.getRef(), args2));
            }catch (IncorrectAmountOfArgumentsException e){
                e.printStackTrace();
            }
    
            return false;
        }
    
        @Override
        public String printTree(int indent) {
            StringBuilder str1 = new StringBuilder("(");
            for (int i = 0; i < args1.size()-1; i++) {
                str1.append(args1.toArray()[i].toString());
                str1.append(",");
            }
            str1.append(args1.toArray()[args1.size()-1].toString());
            str1.append(")");
            StringBuilder str2 = new StringBuilder("(");
            for (int i = 0; i < args2.size()-1; i++) {
                str2.append(args2.toArray()[i].toString());
                str2.append(",");
            }
            str2.append(args2.toArray()[args2.size()-1].toString());
            str2.append(")");
            return Tools.indent(indent) + obj1.toString() + str1.toString() + " " + conditionalType.identifier + " " + obj2.toString() + str2.toString() + "\n";
    
        }
    }
    
    
    public enum ConditionalType{
        equal("=="){
            @Override
            <T, K> boolean getValue(T obj1, K obj2) {
                System.out.printf("Checking if %s == %s -> %s\n", obj1, obj2, obj1.equals(obj2));
                return obj1.equals(obj2);
            }
    
            @Override
            <T extends Number, K extends Number> boolean getValue(T obj1, K obj2) {
                System.out.printf("Checking if %s == %s -> %s\n", obj1, obj2, obj1.doubleValue() == obj2.doubleValue());
                return obj1.doubleValue() == obj2.doubleValue();
            }
        },
        lessThan("<"){
            @Override
            <T, K> boolean getValue(T obj1, K obj2) {
                System.out.println("INCORRECT TYPES");
                return false;
            }
    
            @Override
            <T extends Number, K extends Number> boolean getValue(T obj1, K obj2) {
                System.out.printf("Checking if %s < %s -> %s\n", obj1, obj2, obj1.doubleValue() < obj2.doubleValue());
                return obj1.doubleValue() < obj2.doubleValue();
            }
        },
        greaterThan(">"){
            @Override
            <T, K> boolean getValue(T obj1, K obj2) {
                System.out.println("INCORRECT TYPES");
                return false;
            }
    
            @Override
            <T extends Number, K extends Number> boolean getValue(T obj1, K obj2) {
                System.out.printf("Checking if %s > %s -> %s\n", obj1, obj2, obj1.doubleValue() > obj2.doubleValue());
                return obj1.doubleValue() > obj2.doubleValue();
            }
        };
        
        public String identifier;
        ConditionalType(String identifier){
            this.identifier = identifier;
        }
        
        abstract <T, K> boolean getValue(T obj1, K obj2);
        abstract <T extends Number, K extends Number> boolean getValue(T obj1, K obj2);
    }
    
   
    private class BooleanNode implements IBooleanNode{
        private BooleanTypes type;
        private IBooleanNode first;
        private IBooleanNode[] after;
    
        public BooleanNode(BooleanTypes type, IBooleanNode first, IBooleanNode... after){
            this.type = type;
            this.first = first;
            this.after = after;
        }
        
        
        @Override
        public boolean getValue() {
            try{
                return type.getValue(first, after);
            }catch (IncorrectAmountOfArgumentsException e){
                e.printStackTrace();
            }
            return false;
        }
    
        @Override
        public String printTree(int indent) {
            return type.toString(indent, first, after);
        }
    }
    
    private enum BooleanTypes{
        not(1, "!"){
            @Override
            public boolean getValue(IBooleanNode n1, IBooleanNode... n2) throws IncorrectAmountOfArgumentsException{
                if(n2.length > 0) throw new IncorrectAmountOfArgumentsException(1, 1 + n2.length);
                boolean value = n1.getValue();
                System.out.printf("Checking if !(%s) -> %s\n", n1.printTree(0), !value);
                return !value;
            }
    
            @Override
            public String toString(int indent, IBooleanNode n1, IBooleanNode... n2) {
                
                return Tools.indent(indent) + "!(\n" + n1.printTree(indent+1) + Tools.indent(indent) + ")\n";
            }
        },
        and(2, "&&"){
            @Override
            public boolean getValue(IBooleanNode n1, IBooleanNode... n2) throws IncorrectAmountOfArgumentsException {
                if(n2.length > 1) throw new IncorrectAmountOfArgumentsException(2, 1 + n2.length);
                boolean left, right;
                left = n1.getValue();
                if(!left) return false;
                right = n2[0].getValue();
                System.out.printf("Checking if %s && %s -> %s\n", n1.printTree(0), n2[0].printTree(0), left && right);
                return right;
            }
        },
        or(2, "||"){
            @Override
            public boolean getValue(IBooleanNode n1, IBooleanNode... n2) throws IncorrectAmountOfArgumentsException {
                if(n2.length > 1) throw new IncorrectAmountOfArgumentsException(2, 1 + n2.length);
                boolean left, right;
                left = n1.getValue();
                if(left) return true;
                right = n2[0].getValue();
                System.out.printf("Checking if %s || %s -> %s\n", n1.printTree(0), n2[0].printTree(0), left || right);
                return right;
            }
        };
        
        private IBooleanNode children[];
        public String identifier;
        BooleanTypes(int i, String identifier){
            children = new IBooleanNode[i];
            this.identifier = identifier;
        }
        public abstract boolean getValue(IBooleanNode n1, IBooleanNode... n2) throws IncorrectAmountOfArgumentsException;
        public String toString(int indent, IBooleanNode n1, IBooleanNode... n2){
            return n1.printTree(indent+1) +  Tools.indent(indent) + identifier + "\n" +
                    n2[0].printTree(indent+1);
        }
        
       
    }
    
    public static class IncorrectAmountOfArgumentsException extends Exception{
        IncorrectAmountOfArgumentsException(int correctAmount, int putAmount){
            super(String.format("Incorrect amount of arguments. Expected: %d Recieved: %d", correctAmount, putAmount));
        }
    }
    
    public class TrueNode implements IBooleanNode{
        @Override
        public boolean getValue() {
            return true;
        }
        @Override
        public String printTree(int indent) {
            return Tools.indent(indent) + "TRUE\n";
        }
    }
    public class FalseNode implements IBooleanNode{
        @Override
        public boolean getValue() {
            return false;
        }
    
        @Override
        public String printTree(int indent) {
            return Tools.indent(indent) + "FALSE\n";
        }
    }
    public class BooleanReferenceNode implements IBooleanNode{
    
        private Reference<Boolean> ref;
    
        public BooleanReferenceNode(Reference<Boolean> ref) {
            this.ref = ref;
        }
    
        @Override
        public boolean getValue() {
            return ref.getRef();
        }
    
        @Override
        public String printTree(int indent) {
            return Tools.indent(indent) + "ref:" + (ref.getRef() ? "TRUE" : "FALSE") + "\n";
        }
    }
    
    
    /**
     * A node that returns a value from an object based on arguments
     * @param <T> Return type
     * @param <K> Object Type to check
     */
    private interface IMethodNode<T, K> extends IPrintableTree{
        T getValue(K base, Collection<Object> args) throws IncorrectAmountOfArgumentsException;
    
        @Override
        default String printTree(int indent) {
            return Tools.indent(indent) + "method\n";
        }
    }
    
    
    enum BooleanMethods implements IMethodNode<Boolean,TokenParser>{
        existsIn{
            @Override
            public Boolean getValue(TokenParser base, Collection<Object> args) throws IncorrectAmountOfArgumentsException{
               if(args.size() != 2) throw new IncorrectAmountOfArgumentsException(2, args.size());
               return base.getGroups().get(args.toArray()[0]).contains(args.toArray()[1]);
            }
        },
        groupExists{
            @Override
            public Boolean getValue(TokenParser base, Collection<Object> args) throws IncorrectAmountOfArgumentsException {
                if(args.size() != 1) throw new IncorrectAmountOfArgumentsException(1, args.size());
                return base.getGroups().containsKey(args.toArray()[0]);
            }
        },
        varDefined{
            @Override
            public Boolean getValue(TokenParser base, Collection<Object> args) throws IncorrectAmountOfArgumentsException {
                if(args.size() != 1) throw new IncorrectAmountOfArgumentsException(1, args.size());
                if(args.toArray()[0].equals(Variable.Scope.global)) {
                    return base.getGlobalVars().containsKey(args.toArray()[0]);
                }
                
                return false;
            }
        },
        matchPattern{
            @Override
            public Boolean getValue(TokenParser base, Collection<Object> args) throws IncorrectAmountOfArgumentsException {
                if(args.size() != 1) throw new IncorrectAmountOfArgumentsException(1, args.size());
                Pattern p = (Pattern) args.toArray()[0];
                return base.match(p);
            }
        }
    }
    
    enum NumberValueMethods implements IMethodNode<Number,TokenParser>{
        groupSize {
            @Override
            public Number getValue(TokenParser base, Collection<Object> args) throws IncorrectAmountOfArgumentsException {
                if(args.size() != 1) throw new IncorrectAmountOfArgumentsException(1, args.size());
                return base.getGroups().get(args.toArray()[0]).size();
            }
        }
    }
    
    enum StringMethods implements IMethodNode<String,TokenParser>{
        currentToken{
           
            public String getValue(TokenParser base, Collection<Object> args) throws IncorrectAmountOfArgumentsException {
                if(args.size() != 0) throw new IncorrectAmountOfArgumentsException(0, args.size());
                return base.currentToken();
            }
        },
        currentChar{
            @Override
            public String getValue(TokenParser base, Collection<Object> args) throws IncorrectAmountOfArgumentsException {
                if(args.size() != 0) throw new IncorrectAmountOfArgumentsException(0, args.size());
                return "" + base.currentChar();
            }
        },
        varValue{
            @Override
            public String getValue(TokenParser base, Collection<Object> args) throws IncorrectAmountOfArgumentsException {
                if(args.size() != 1) throw new IncorrectAmountOfArgumentsException(1, args.size());
                return base.getGlobalVars().get(args.toArray()[0]).getChildTerminals();
            }
        }
    }
    
    
    
    private String name;
    private SyntacticCategory[][] parameters;
    private Rule baseRule;
    private FunctionNode tree;
    private Reference<TokenParser> creator;
    
    public SyntacticFunction(String name, SyntacticCategory[][] parameters, Rule baseRule, TokenParser creator) {
        this(name,parameters,creator,baseRule.getSyntacticObjects().toArray());
    }
    
    public SyntacticFunction(String name, SyntacticCategory[][] parameters, TokenParser creator, Object... obj) {
        try {
            this.name = name;
            this.parameters = parameters;
            this.baseRule = new Rule(name, obj);
            this.creator = new Reference<>(creator);
        }catch (Rule.IncorrectTypeException e){
            e.printStackTrace();
        }
    }
    
    public SyntacticFunction(String name, SyntacticCategory[][] parameters) {
        this.name = name;
        this.parameters = parameters;
        this.creator = new Reference<>();
    }
    
    public SyntacticFunction(String name, SyntacticCategory[][] parameters, Rule baseRule) {
        this(name,parameters,baseRule.getSyntacticObjects().toArray());
    }
    
    public SyntacticFunction(String name, SyntacticCategory[][] parameters, Object... obj) {
        try {
            this.name = name;
            this.parameters = parameters;
            this.baseRule = new Rule(name, obj);
        }catch (Rule.IncorrectTypeException e){
            e.printStackTrace();
        }
    }
    
    public SyntacticFunction(String name, SyntacticCategory[][] parameters, TokenParser creator) {
        this.name = name;
        this.parameters = parameters;
        this.creator = new Reference<>(creator);
    }
    
    public void setTree(FunctionNode tree){
        this.tree = tree;
    }
    
   
    public int getMaxSize(){
        int output = 0;
        for (SyntacticCategory[] parameter : parameters) {
            output += parameter.length;
        }
        return output + baseRule.getSyntacticObjects().size();
    }
    
    public String getName() {
        return name;
    }
    
    public SyntacticCategory[][] getParameters() {
        return parameters;
    }
    
    public Rule getRule() {
        if(tree != null) return tree.calculateRule();
        return baseRule;
    }
    
    public RuleNode createRuleNode(Object... objects){
        try {
            return new RuleNode(new Rule(name, Arrays.asList(objects)));
        }catch (Rule.IncorrectTypeException e){
            e.printStackTrace();
        }
        return null;
    }
    
    public  IBooleanNode createOrNode(IBooleanNode left, IBooleanNode right){
        return new BooleanNode(BooleanTypes.or, left, right);
    }
    
    public  IBooleanNode createAndNode(IBooleanNode left, IBooleanNode right){
        return new BooleanNode(BooleanTypes.and, left, right);
    }
    
    public  IBooleanNode createNotNode(IBooleanNode left){
        return new BooleanNode(BooleanTypes.not, left);
    }
    
    public  <T, K> IBooleanNode createEqualToNode(T left, K right){
        return new ConditionalNode<>(ConditionalType.equal, left, right);
    }
    
    public  <T, K> IBooleanNode createNotEqualToNode(T left, K right){
        IBooleanNode equalTo = createEqualToNode(left, right);
        return createNotNode(equalTo);
    }
    
    public  <T extends Number, K extends Number> IBooleanNode createGreaterThanNode(T left, K right){
        return new ConditionalNode<>(ConditionalType.greaterThan, left, right);
    }
    
    public  <T extends Number, K extends Number> IBooleanNode createLessThanNode(T left, K right){
        return new ConditionalNode<>(ConditionalType.lessThan, left, right);
    }
    
    public  <T extends Number, K extends Number> IBooleanNode createGreaterThanOrEqualToTree(T left, K right){
        IBooleanNode lessThan = createLessThanNode(left, right);
        return createNotNode(lessThan);
    }
    
    public <T extends Number, K extends Number> IBooleanNode createLessThanOrEqualToTree(T left, K right){
        IBooleanNode lessThan = createGreaterThanNode(left, right);
        return createNotNode(lessThan);
    }
    
    public IBooleanNode createTrueNode(){
        return new TrueNode();
    }
    
    public IBooleanNode createFalseNode(){
        return new FalseNode();
    }
    
    public ControlNode createControlNode(IBooleanNode booleanNode, FunctionNode nextNode){
        return new ControlNode(booleanNode, nextNode);
    }
    
    public ControlNode createControlNode(IBooleanNode booleanNode, FunctionNode nextNode, FunctionNode elseNode){
        return new ControlNode(booleanNode, nextNode, elseNode);
    }
    
    public IBooleanNode createExistsInNode(String group, String str){
        return new HalfMethodConditionalNode<Boolean,TokenParser>(
                ConditionalType.equal,
                BooleanMethods.existsIn,
                creator,
                true,
                Arrays.asList(group, str)
        );
    }
    
    public IBooleanNode createGroupExistsNode(String group){
        return new HalfMethodConditionalNode<>(
                ConditionalType.equal,
                BooleanMethods.groupExists,
                creator,
                true,
                Arrays.asList(group)
        );
    }
    
    public IBooleanNode createVarDefinedNode(String var){
        return new HalfMethodConditionalNode<>(
                ConditionalType.equal,
                BooleanMethods.varDefined,
                creator,
                true,
                Arrays.asList(var)
        );
    }
    
    public IBooleanNode createVarEqualsNode(String var, String equal){
        return new HalfMethodConditionalNode<>(
                ConditionalType.equal,
                StringMethods.varValue,
                creator,
                equal,
                Arrays.asList(var)
        );
    }
    
    public IBooleanNode createGroupSizeNode(String group, ConditionalType type, Number size){
        return new HalfMethodConditionalNode<>(
                type,
                NumberValueMethods.groupSize,
                creator,
                size,
                Arrays.asList(group)
        );
    }
    
    public IBooleanNode createGroupSizeNode(String group1, ConditionalType type, String group2){
        return new FullMethodConditionalNode<>(
                type,
                creator,
                NumberValueMethods.groupSize,
                Arrays.asList(group1),
                NumberValueMethods.groupSize,
                Arrays.asList(group2)
        );
    }
    
    public IBooleanNode createCurrentTokenNode(String str){
        return new HalfMethodConditionalNode<>(
                ConditionalType.equal,
                StringMethods.currentToken,
                creator,
                str,
                new ArrayList<>()
        );
    }
    
    public IBooleanNode createCurrentCharNode(char str){
        return new HalfMethodConditionalNode<>(
                ConditionalType.equal,
                StringMethods.currentChar,
                creator,
                "" + str,
                new ArrayList<>()
        );
    }
    
    public IBooleanNode createPatternMatchNode(String s){
        return createPatternMatchNode(Pattern.compile(s));
    }
    public IBooleanNode createPatternMatchNode(Pattern pattern) {
        return new HalfMethodConditionalNode<>(
                ConditionalType.equal,
                BooleanMethods.matchPattern,
                creator,
                true,
                Arrays.asList(pattern)
        );
    }
    
    @Override
    public String getRepresentation() {
        return String.format("<%s()>", name);
    }
    
    @Override
    public String getUpperRepresentation() {
        StringBuilder params = new StringBuilder();
        for (SyntacticCategory[] parameter : parameters) {
            StringBuilder param = new StringBuilder();
            for (SyntacticCategory syntacticCategory : parameter) {
                param.append(syntacticCategory.getRepresentation());
            }
            param.append(",");
            params.append(param);
        }
        if(params.length() > 0) params.deleteCharAt(params.length() - 1);
        if(baseRule != null) {
            return String.format("\t<%s(%s)> -> %s", name, params.toString(), baseRule.toString());
        }
    
        return String.format("\t<%s(%s)> ->\n%s", name, params.toString(), tree.printTree(2));
    }
    
    @Override
    protected String getFullRepresentation(int maxLevels, int currentLevel) {
        return null;
    }
    
    @Override
    public String generate() {
        return null;
    }
    
    @Override
    protected String generate(int max_depth, int level) {
        return null;
    }
    
    public TokenParser getCreator() {
        return creator.getRef();
    }
    
    public void setCreator(TokenParser creator) {
        this.creator.setRef(creator);
    }
}
