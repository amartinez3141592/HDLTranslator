
// parser grammar Parser;
// options { tokenVocab=ExprLexer; }
// uncomment if the gramar is in another file, common in website test 
grammar HDL;

program
    : module_def ';' input_def
        ';' output_def ';' memory_def ';' sequence_def ';' // 'endmodule' ';'
    ;
module_def
    : MODULE DESCRIBE ID
    ;

variable_def
    : (ID SELECTOR NUMBER END_SELECTOR)
    | ID
    ;
    
input_def
    : INPUT DESCRIBE input_list
    ;
// recursion order was inverted
input_list
    : variable_def
    | ( variable_def COMMA
        input_list
    )
    ;
 
output_def
    : OUTPUT DESCRIBE input_list 
    ;

memory_def
    : MEMORY DESCRIBE input_list 
    ;

sequence_def
    : SEQUENCE DESCRIBE steps_def END_SEQUENCE
//    | SEQUENCE DESCRIBE END_SEQUENCE si quiero agregarlo, modificar codigo
    ;

// IMPORTANT CHANGES HERE
steps_def
    : (STEP LPAREN NUMBER RPAREN DESCRIBE step_def step_transition ';' )
    | (STEP LPAREN NUMBER RPAREN DESCRIBE step_def step_transition ';' steps_def)
    ;

step_transition
    : TRANSITION LPAREN conditions RPAREN TRANSITION_TO LPAREN goto RPAREN   
    ;

step_def
    : (( assign_memory | assign_output ) ';') 
    | (( assign_memory | assign_output ) ';' step_def) 
    ; 


assign_output: ID EQ ID
    ;

assign_memory
    : ID MEM_ASSIGN ID
    ;
// order changed
conditions
    : expr
    | (expr COMMA conditions)
    ;
goto
    : NUMBER
    | (NUMBER COMMA goto)
    ;

expr: 
//  | bit
    | variable_def
    | (NOT expr)
    | (variable_def AND expr)
    | (variable_def OR expr)
    ;
/*
bit
    : '0'
    | '1'
    ;
*/
// lexer grammar ExprLexer;
// uncomment on testing on a website, maybe it can be in diff files


NUMBER : [0-9]+ ;
MEM_ASSIGN : '<=';
TRANSITION : '=>';
TRANSITION_TO : '/';
DOT : '.';
SEQUENCE : 'SEQUENCE';
END_SEQUENCE : 'END_SEQUENCE';
MODULE : 'module';
INPUT : 'INPUT';
OUTPUT : 'OUTPUT';
MEMORY : 'MEMORY';
DESCRIBE : ':';
SELECTOR: '[';
END_SELECTOR: ']';
STEP: 'STEP';

AND : 'AND' ;
OR : 'OR' ;
NOT : 'NOT' ;
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



