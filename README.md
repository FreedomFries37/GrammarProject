# GrammarProject
Universal context free grammar parser

## Grammar examples
	#basic math grammar
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
