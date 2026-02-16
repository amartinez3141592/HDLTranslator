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

    public static void main(String[] args) throws Exception {

//        String code = "module: hello; input: c,a ,b, j[3]; output:c,a ,b, j[3];";
        String code = """
                    MODULE: hello;
                    INPUT: touched, switch, buttons[3];
                    OUTPUT: red_led, turn_on,rgb[3];
                    MEMORY: mem_1[3], mem_2, mem_3;
                    SEQUENCE(6):
                        STEP(0):
                          red_led = touched;
                          red_led = switch;
                          mem_2 <= switch;
                          red_led= rbg;
                          mem_1<= buttons;
                        => ( touched ) / ( 1 );
                      STEP(1):
                        red_led = touched;
                        red_led = switch;
                        mem_2 <= switch;
                        red_led= rbg;
                        mem_1<= buttons;
                      => ( touched ) / ( 1 );
                    END_SEQUENCE;                 
                      """;
        CharStream input = CharStreams.fromString(code);

        HDLLexer lexer = new HDLLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        HDLParser parser = new HDLParser(tokens);

        ParseTree tree = parser.program();

        System.out.println(tree.toStringTree(parser));

        InternalNode editableTree = CustomTreeEquivalent.parse(tree, parser);
        editableTree.print();
        System.out.println();

        TreeBuilder translated_tree = new TreeBuilder(editableTree);
        translated_tree.build();
        translated_tree.print();
    }
}
