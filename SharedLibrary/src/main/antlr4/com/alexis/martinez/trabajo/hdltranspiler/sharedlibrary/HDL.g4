
// parser grammar Parser;
// options { tokenVocab=ExprLexer; }
// uncomment if the gramar is in another file, common in website test 
grammar HDL;
// TODO: test usign ? in recursion and see if performance improves
program
    : module_def SEMI input_def SEMI output_def SEMI memory_def SEMI sequence_def END_MODULE program? EOF
    ;

module
    : 
    ;

module_def
    : MODULE DESCRIBE ID
    ;

variable_def
    : | ID (SELECTOR NUMBER END_SELECTOR) ?
    ;
    
input_def
    : INPUT DESCRIBE input_list
    ;

input_list
    : variable_def (COMMA input_list ) ?
    ;
 
output_def
    : OUTPUT DESCRIBE input_list 
    ;

memory_def
    : MEMORY DESCRIBE input_list 
    ;

sequence_def
    : SEQUENCE LPAREN NUMBER RPAREN DESCRIBE steps_def END_SEQUENCE
//    | SEQUENCE DESCRIBE END_SEQUENCE
    ;


steps_def
    : STEP LPAREN NUMBER RPAREN DESCRIBE step_def step_transition SEMI steps_def?
    ;

step_transition
    : TRANSITION LPAREN conditions RPAREN TRANSITION_TO LPAREN goto RPAREN   
    ;

step_def
    : ( assign_memory | assign_output ) SEMI step_def?
    ; 


assign_output
    : ID EQ (expr | input_list)
    ;

assign_memory
    : ID (CONDITIONED_BY expr)? MEM_ASSIGN (expr | input_list)
    ;

conditions
    : expr (COMMA conditions) ?
    ;
goto
    : NUMBER (COMMA goto) ?
    ;

expr
    :
    variable_def
    | module_call
    | (NOT expr)
    | (variable_def OR expr)
    | (variable_def AND expr)
    ;

module_call : ID LPAREN input_list RPAREN;

// lexer grammar ExprLexer;
// uncomment on testing on a website, maybe it can be in diff files

CONDITIONED_BY: '*';
NUMBER : [0-9]+ ;
MEM_ASSIGN : '<=';
TRANSITION : '=>';
TRANSITION_TO : '/';
DOT : '.';
SEQUENCE : 'SEQUENCE';
END_SEQUENCE : 'END_SEQUENCE';
END_MODULE : 'END_MODULE';
MODULE : 'MODULE';
INPUT : 'INPUT';
OUTPUT : 'OUTPUT';
MEMORY : 'MEMORY';
DESCRIBE : ':';
SELECTOR: '[';
END_SELECTOR: ']';
STEP: 'STEP';

AND : '&&' ;
OR : '||' ;
NOT : '!' ;
EQ : '=' ;
COMMA : ',' ;
SEMI : ';' ;
LPAREN : '(' ;
RPAREN : ')' ;
LCURLY : '{' ;
RCURLY : '}' ;


ID   : [a-zA-Z_] [a-zA-Z_0-9]* ; 
// ID : [a-zA-Z_0-9] [a-zA-Z_0-9]* ; 
// ID: [a-zA-Z_][a-zA-Z_-1-9]* ;
WS: [ \t\n\r\f]+ -> skip ;



