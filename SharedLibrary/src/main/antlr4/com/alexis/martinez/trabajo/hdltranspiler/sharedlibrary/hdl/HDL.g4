
// parser grammar Parser;
// options { tokenVocab=ExprLexer; }
// uncomment if the gramar is in another file, common in website test 
grammar HDL;
// TODO: test usign ? in recursion and see if performance improves
program
    : module_def (program)? EOF
    ;


module_def
    : MODULE DESCRIBE ID SEMI
    input_def SEMI
    output_def SEMI
    body_def
    END_MODULE
    SEMI
    ;

body_def
    : (memory_def SEMI)? sequence_def SEMI control_reset_def SEMI
    ;

control_reset_def
    : CONTROL_RESET LPAREN NUMBER RPAREN DESCRIBE
           step_def?
      END_CONTROL
    ;

variable_def
    : ID (SELECTOR NUMBER END_SELECTOR) ?
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
    : SEQUENCE LPAREN NUMBER RPAREN DESCRIBE
        steps_def?
    END_SEQUENCE
    //    | SEQUENCE DESCRIBE END_SEQUENCE
    ;


steps_def
    : STEP LPAREN NUMBER RPAREN DESCRIBE step_def? step_transition SEMI steps_def?
    ;

step_transition
    : TRANSITION LPAREN conditions RPAREN TRANSITION_TO LPAREN goto RPAREN 
    ;

step_def
    : ( assign_memory | assign_output ) SEMI step_def?
    ; 


assign_output
    : ID EQ ( expr |
    assignation_conditions )
    ;

assignation_conditions
    : LCURLY conditions RCURLY
    ;

assign_memory
    : ID (CONDITIONED_BY expr)? MEM_ASSIGN (expr | assignation_conditions)
    ;

conditions
    : expr (COMMA conditions) ?
    ;
goto
    : NUMBER (COMMA goto) ?
    ;

expr
    :   const_expr
    |   variable_def
    |   module_call
    |   (NOT expr)
    |   left=expr OR right=expr
    |   left=expr AND right=expr
    |   left=expr EQUALS right=expr
    |   left=expr NOT_EQUALS right=expr
    |   (LPAREN expr RPAREN)
    ;


// 2'b01 or 3'hAF
const_expr
    : NUMBER
    (CONST_DEF_SIGN
    ID)?
    ;

call_input_list : expr (COMMA call_input_list ) ?;

module_call : ID LPAREN call_input_list RPAREN;

// lexer grammar ExprLexer;
// uncomment on testing on a website, maybe it can be in diff files
CONST_DEF_SIGN : '\'';
CONTROL_RESET : 'CONTROL RESET';
END_CONTROL : 'END_CONTROL';
EQUALS : '==';
NOT_EQUALS: '!=';
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



