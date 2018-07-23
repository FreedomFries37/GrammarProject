# GrammarProject
Universal context free grammar parser

## Grammar examples

##### Basic math grammar

    #import <standard>

    expression(default):
        <group><expression_tail>

    expression_tail(optional):
        +<expression>
        -<expression>

    group:
        <factor><group_tail>

    group_tail(optional):
        *<group>
        /<group>

    factor:
        (<expression>)
        <double>
        -<factor>
        
##### Basic math grammar using extended (.eccfg file)
    
    cat expression{
        <group><expression_tail>
    }
    
    cat expression_tail(optional){
        <outer_operator><expression>
    }
    
    token outer_operator{
        "\+|-"
    }
    
    cat group{
        <factor><group_tail>
    }
    
    cat group_tail(optional){
        <inner_operator><group>
    }
    
    token inner_operator{
        "\\|*"
    }
    
    cat number{
        \regex("\d+(.\d*)?")\
    }
    
    cat factor{
        (<expression>)
        <number>
        -<factor>
    }
    
    
    
