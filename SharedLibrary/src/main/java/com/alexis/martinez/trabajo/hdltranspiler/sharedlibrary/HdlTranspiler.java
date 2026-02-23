/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import org.antlr.v4.gui.TreeViewer;

import com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.HDLLexer;
import com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.HDLParser;
import com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.operations.TreeExport;
import com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.tree.InternalNode;
import com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.tree.CustomTreeEquivalent;
import java.util.Arrays;

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
        String code;
        if (args.length == 0) {
            code = """
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
                            red_led= rbg ||  switch;
                            mem_1<= buttons;
                        => ( switch ) / ( 2 );
                      
                        STEP(2):
                            red_led = touched[0] || switch && !switch;
                            red_led = touched;
                            red_led= rbg || switch;
                            mem_1<= buttons;
                        => ( switch ) / ( 0 );
                                      
                    END_SEQUENCE
                    
                    END_MODULE
                   

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
                            red_led= rbg ||  switch;
                            mem_1<= buttons;
                        => ( switch ) / ( 2 );

                        STEP(2):
                            red_led = touched[0] || switch && !switch;
                            red_led = touched;
                            mem_2 * touched[0] && fff <= GET(touched[0], touched[0]);
                            red_led= rbg || switch;
                            mem_1<= buttons;
                        => ( switch ) / ( 0 );

                    END_SEQUENCE

                    END_MODULE
                    """;
            System.out.print(transpile(code));

        } else {
            code = args[0];
            System.out.print(transpile(code));
            // visualize();
            if (args.length == 2) {
                System.out.println(tree.toStringTree(parser));
            }
        }

    }

    /**
     * This method return a gui tree viewer that shows the abstract syntax tree
     * See also: TreeViewer
     */
    public static void visualize() {
        var viewer = new TreeViewer(Arrays.asList(parser.getRuleNames()), tree);
        viewer.open();
    }

    /**
     * This method recieves the hdl informal code and process it,
     * throws an Exception if there is an error and call TreeExport.build() to transpile it
     * @see com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.operations.TreeExport#build() 
     * @see com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.HDLLexer
     * @see com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.HDLParser

     * @param code
     * @return the transpiled System Verilog output
     * @throws Exception
     */
    public static String transpile(String code) throws Exception {

        input = CharStreams.fromString(code);

        lexer = new HDLLexer(input);
        tokens = new CommonTokenStream(lexer);
        parser = new HDLParser(tokens);

        tree = parser.program();

        InternalNode editableTree = CustomTreeEquivalent.parse(tree, parser);

        TreeExport translated_tree = new TreeExport(editableTree);
        translated_tree.build();

        return translated_tree.toString();
    }

    /**
     *
     * @return the Tree in string format
     */
    public static String toStringTreeForLookingForSyntaxErrors() {
        return tree.toStringTree(parser);
    }
}
