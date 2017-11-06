grammar Fun;


file
    :   block EOF
    ;

block
    :   (NEWLINE)* statements += statement (NEWLINE+ statements += statement)* (NEWLINE)*
    ;

statement
    :   'fun' functionName = IDENTIFIER '(' parameterNames += IDENTIFIER
                                            (',' parameterNames += IDENTIFIER)* ')'
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

expression
    :   '(' expression ')'
        # expressionInParentheses

    |   IDENTIFIER
        # variableAccessExpression

    |   IDENTIFIER '(' arguments += expression (',' arguments += expression)* ')'
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
