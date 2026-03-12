grammar SystemVerilog;

program
    : module_def (program)? EOF
    ;


module_def
    : MODULE ID LPAREN ports RPAREN SEMI module_body ENDMODULE;

ports : (port COMMA)* port;

port : (INPUT LOGIC (SELECTOR NUMBER DESCRIBE NUMBER END_SELECTOR)? ID)
    | (OUTPUT LOGIC (SELECTOR NUMBER DESCRIBE NUMBER END_SELECTOR)? ID)
    ;

module_body : (module_block)*;

module_block : ALWAYS_FF AT signal_trans BEGIN block_body* END // sync_proc
    | ALWAYS_COMB BEGIN async_body+ case_def? END // sync_proc
    | LOGIC (SELECTOR NUMBER DESCRIBE NUMBER END_SELECTOR)? ID SEMI // reg_decl
    | TYPEDEF ENUM LOGIC SELECTOR NUMBER DESCRIBE NUMBER END_SELECTOR LCURLY (enum_state COMMA)* enum_state  RCURLY ID SEMI
    | ID ID SEMI // wire_decl
    ;


case_def
    : CASE LPAREN ID RPAREN (specific_case_def)* END_CASE
    ;
specific_case_def
    : ID DESCRIBE BEGIN (async_body)* END
    ;
enum_state : 
    ID EQ NUMBER CONST_DEF_SIGN ID
    ;
signal_trans : LPAREN (POSEDGE ID OR NEGEDGE ID) RPAREN;



// block
block_body : if_block_def
    | left=ID ASSIGN right=expr SEMI;

if_block_def
    : IF LPAREN expr RPAREN BEGIN (block_body)* END (if_block_def)?
    | ELSE if_block_def
    | ELSE BEGIN (block_body)* END
    ;
// ---



// async
async_body : if_async_def
    | left=ID EQ right=expr SEMI;

if_async_def
    : IF LPAREN expr RPAREN BEGIN (async_body)* END (if_async_def)?
    | ELSE if_async_def
    | ELSE BEGIN (async_body)* END
    ;
//---


expr:   TILDA expr // bitwise_not
    |   NOT LPAREN expr RPAREN // bit
    |   LPAREN left=expr LOGIC_OR right=expr RPAREN // or
    |   LPAREN left=expr LOGIC_AND right=expr RPAREN // and 
    |   LPAREN left=expr LOGIC_EQUAL right=expr RPAREN //expr == expr
    |   LCURLY (expr COMMA)+ expr RCURLY
    |   NUMBER CONST_DEF_SIGN ID
    |   ID SELECTOR NUMBER END_SELECTOR
    |   ID LPAREN (expr COMMA)? expr RPAREN
    |   ID // variable
    |   NUMBER // literal
    ;

WS: [ \t\r\n\f]+ -> channel(HIDDEN);

MODULE : 'module';
ENDMODULE : 'endmodule';

ALWAYS_FF : 'always_ff';
ALWAYS_COMB : 'always_comb';


BEGIN : 'begin';
END : 'end';

POSEDGE : 'posedge';
NEGEDGE : 'negedge';

LOGIC : 'logic';
WIRE : 'wire';

IF: 'if';
ELSE: 'else';

INPUT       : 'input';
OUTPUT      : 'output';
CASE : 'case';
END_CASE : 'endcase';
CONST_DEF_SIGN : '\'';

TILDA       : '~';
NOT : '!' ;
LOGIC_OR : '||';
LOGIC_AND : '&&';
LOGIC_EQUAL : '==';

ASSIGN   : '<=';
EQ : '=' ;
COMMA : ',' ;

TYPEDEF : 'typedef';
ENUM : 'enum';

NEWLINE     : [\r\n]+ ;
NUMBER         : [0-9]+ ;

SELECTOR: '[';
END_SELECTOR: ']';
DESCRIBE : ':';

LCURLY : '{' ;
RCURLY : '}' ;
LPAREN : '(' ;
RPAREN : ')' ;

OR: 'or';
AT : '@';
SEMI   : ';';
ID   : [a-zA-Z_] [a-zA-Z_0-9]*; 
