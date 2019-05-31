grammar EmbededLisp;

/*------------------------------------------------------------------
 * PARSER RULES
 *------------------------------------------------------------------*/
program : expression* EOF ;
expression: OP IDENTIFIER (IDENTIFIER | STRING | NUMBER | expression)* CP;

/*------------------------------------------------------------------
 * LEXER RULES
 *------------------------------------------------------------------*/
IDENTIFIER : (STATIC_METHOD | INSTANCE_METHOD | FORM) ;

FORM : LETTER (DOT | LETTER | DIGIT | UNDERSCORE)*;
INSTANCE_METHOD : DOT (LETTER | DIGIT)*;
STATIC_METHOD : LETTER (DOT | LETTER | DIGIT | UNDERSCORE)* '/' LETTER (LETTER | DIGIT | UNDERSCORE)*;

OP : '(';
CP : ')';
DOT : '.';
UNDERSCORE: '_';

WHITESPACE : [ \r\n\t] + -> channel (HIDDEN);

STRING : '"' (' ' | LETTER | DIGIT)+ '"';

NUMBER : (DIGIT)+ ;
DIGIT : '0'..'9';

LETTER : LOWER | UPPER ;
LOWER : ('a'..'z') ;
UPPER : ('A'..'Z') ;
