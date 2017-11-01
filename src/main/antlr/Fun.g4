grammar Fun;


file
    :   block EOF
    ;

block
    :   statements += statement (NEWLINE statements += statement)*
    ;

statement
    :   'fun' functionName = IDENTIFIER '(' functionDefinitionParameterNames ')'
        '{' functionBody = block '}'
        # functionDefinitionStatement

    |   'var' variableName = IDENTIFIER ('=' initialValueExpression = expression)?
        # variableDefinitionStatement

    |   expression
        # expressionStatement

    |   'while' '(' condition = expression ')' '{' body = block '}'
        # whileStatement

    |   'if' '(' condition = expression ')' '{' thenBody = block '}'
        ('else' '{' elseBody = block '}')?
        # ifStatement

    |   IDENTIFIER '=' expression
        # assignmentStatement

    |   'return' expression
        # returnStatement
    ;

functionDefinitionParameterNames
    :   names += IDENTIFIER (',' names += IDENTIFIER)*
    ;

expression
    :   '(' expression ')'
        # expressionInParentheses

    |   IDENTIFIER '(' functionCallArguments ')'
        # functionCallExpression

    |   LITERAL
        # literalExpression

    |   leftOperand = expression operation = ('*' | '/' | '%') rightOperand = expression
        # binaryExpression

    |   leftOperand = expression operation = ('+' | '-') rightOperand = expression
        # binaryExpression

    |   leftOperand = expression operation = ('<' | '>' | '<=' | '>=') rightOperand = expression
        # binaryExpression

    |   leftOperand = expression operation = ('==' | '!=') rightOperand = expression
        # binaryExpression

    |   leftOperand = expression operation = '&&' rightOperand = expression
        # binaryExpression

    |   leftOperand = expression operation = '||' rightOperand = expression
        # binaryExpression
    ;

functionCallArguments
    :   arguments += expression (',' arguments += expression)*
    ;


fragment ZERO_DIGIT
    :   '0'
    ;

fragment NON_ZERO_DIGIT
    :   [1-9]
    ;

fragment DIGIT
    :   ZERO_DIGIT
    |   NON_ZERO_DIGIT
    ;

fragment LETTER
    :   [a-zA-Z]
    ;

fragment UNDERSCORE
    :   '_'
    ;

IDENTIFIER
    : (LETTER | UNDERSCORE) (LETTER | UNDERSCORE | DIGIT)*
    ;

LITERAL
    :   ZERO_DIGIT
    |   '-'? NON_ZERO_DIGIT DIGIT*
    ;

NEWLINE
    :   '\r'? '\n'
    ;

COMMENTARY
    :   '//' .*? NEWLINE -> skip
    ;

WHITESPACE
    :   [ \t]+ -> skip
    ;
