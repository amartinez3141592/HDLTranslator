/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.example.hdltranspiler;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import com.example.hdltranspiler.HDLLexer;
import com.example.hdltranspiler.HDLParser;
import com.example.hdltranspiler.operations.TreeBuilder;
import com.example.hdltranspiler.tree.InternalNode;
import com.example.hdltranspiler.tree.CustomTreeEquivalent;

/**
 *
 * @author Alexis Martinez
 */
public class HdlTranspiler {

    private static CharStream input;
    private static HDLLexer lexer;
    private static CommonTokenStream tokens;
    private static HDLParser parser;
    private static ParseTree tree;

    public static void main(String[] args) throws Exception {
        String code = """
                    MODULE: hello;
                    INPUT: touched, switch, buttons[3];
                    OUTPUT: red_led, turn_on,rgb[3];
                    MEMORY: mem_1[3], mem_2, mem_3;
                    SEQUENCE(6):
                        STEP(0):
                            red_led = !touched && switch || switch;
                            red_led = switch || touched;
                            mem_2 <= !turn_on;
                            red_led= rbg && switch;
                            mem_1<= buttons;
                        => ( touched, !touched ) / ( 1 , 0 );
                        STEP(1):
                            red_led = !touched && switch || switch;
                            red_led = switch || touched;
                            mem_2 <= !turn_on;
                            red_led= rbg && switch;
                            mem_1<= buttons;
                        => ( switch ) / ( 2 );
                      
                        STEP(2):
                            red_led = !touched && switch || switch;
                            red_led = switch || touched;
                            mem_2 <= !turn_on;
                            red_led= rbg && switch;
                            mem_1<= buttons;
                        => ( switch ) / ( 0 );
                                      
                    END_SEQUENCE;                 
                      """;
        System.out.print(transpile(code));

    }

    public static String transpile(String code) throws Exception {

        input = CharStreams.fromString(code);

        lexer = new HDLLexer(input);
        tokens = new CommonTokenStream(lexer);
        parser = new HDLParser(tokens);

        tree = parser.program();

        System.out.println(tree.toStringTree(parser));

        InternalNode editableTree = CustomTreeEquivalent.parse(tree, parser);

        TreeBuilder translated_tree = new TreeBuilder(editableTree);
        translated_tree.build();

        return translated_tree.toString();
    }

    public static String toStringTreeForLookingForSyntaxErrors() {
        return tree.toStringTree(parser);
    }
}
