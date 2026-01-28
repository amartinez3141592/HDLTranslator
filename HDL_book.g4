
parser grammar Parser;
// options { tokenVocab=ExprLexer; }


program
    : module_def ';'
    | module_def ';' input_def ';'
    | module_def ';' input_def
        ';' output_def ';'
    ;
module_def
    : MODULE DESCRIBE ID
    ;
    
input_def
    : INPUT DESCRIBE input_list
    ;

input_list
    : ID
    | ID SELECTOR INT END_SELECTOR
    | input_list COMMA input_list
    ;
 
output_def
    : OUTPUT DESCRIBE input_list
    ;

/*
program
    : stat EOF
    | def EOF
    ;

stat: ID '=' expr ';'
    | expr ';'
    ;

def : ID '(' ID (',' ID)* ')' '{' stat* '}' ;

expr: ID
    | INT
    | func
    | 'not' expr
    | expr 'and' expr
    | expr 'or' expr
    ;

func : ID '(' expr (',' expr)* ')' ;

*/


lexer grammar ExprLexer;


MODULE : 'module';
INPUT : 'input';
OUTPUT : 'output';
MEMORY : 'memory';
DESCRIBE : ':';
SELECTOR: '[';
END_SELECTOR: ']';


AND : 'and' ;
OR : 'or' ;
NOT : 'not' ;
EQ : '=' ;
COMMA : ',' ;
SEMI : ';' ;
LPAREN : '(' ;
RPAREN : ')' ;
LCURLY : '{' ;
RCURLY : '}' ;


INT : [-1-9]+ ;
ID: [a-zA-Z_][a-zA-Z_-1-9]* ;
WS: [ \t\n\r\f]+ -> skip ;



