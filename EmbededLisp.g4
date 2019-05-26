grammar EmbededLisp;

/*------------------------------------------------------------------
 * PARSER RULES
 *------------------------------------------------------------------*/
program : expression* EOF ;
expression: OP IDENTIFIER (IDENTIFIER | STRING | NUMBER | expression)* CP;

/*------------------------------------------------------------------
 * LEXER RULES
 *------------------------------------------------------------------*/
IDENTIFIER : (STATIC_METHOD | INSTANCE_METHOD | FORM | PLUS | MINUS | MULT | DIV) ;

FORM : LETTER (DOT | LETTER | DIGIT | UNDERSCORE)*;
INSTANCE_METHOD : DOT (LETTER | DIGIT)*;
STATIC_METHOD : LETTER (DOT | LETTER | DIGIT | UNDERSCORE)* '/' LETTER (LETTER | DIGIT | UNDERSCORE)*;

PLUS : '+';
MINUS : '-';
MULT : '*';
DIV : '/';
OP : '(';
CP : ')';
DOT : '.';
UNDERSCORE: '_';

STRING : '"' (' ' | LETTER | DIGIT)+ '"';

NUMBER : (DIGIT)+ ;

WHITESPACE : [ \r\n\t] + -> channel (HIDDEN);

DIGIT : '0'..'9';

LETTER : LOWER | UPPER ;

LOWER : ('a'..'z') ;
UPPER : ('A'..'Z') ;
