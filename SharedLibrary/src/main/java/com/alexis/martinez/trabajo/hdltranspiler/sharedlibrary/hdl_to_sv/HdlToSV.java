/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.hdl_to_sv;

import com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.hdl.HDLLexer;
import com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.hdl.HDLParser;
import com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.hdl_to_sv.control.ExampleGenerator;
import com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.hdl_to_sv.operations.TreeExport;
import com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.hdl_to_sv.tree.CustomTreeEquivalent;
import com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.hdl_to_sv.tree.InternalNode;
import java.util.Arrays;
import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 *
 * @author Alexis Martinez
 */
public class HdlToSV {
    
    
    public static CharStream input;
    public static HDLLexer lexer;
    public static CommonTokenStream tokens;
    public static HDLParser parser;
    public static ParseTree tree;
    public static ExampleGenerator example;
        /**
     * This method return a gui tree viewer that shows the abstract syntax tree
     * See also: TreeViewer
     */
    public static void visualize() {
        var viewer = new TreeViewer(Arrays.asList(parser.getRuleNames()), tree);
        viewer.open();
    }

    /**
     * This method recieves the hdl informal code and process it, throws an
     * Exception if there is an error and call TreeExport.build() to transpile
     * it
     *
     * @see
     * com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.operations.TreeExport#build()
     * @see com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.HDLLexer
     * @see com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.HDLParser
     *
     * @param code
     * @return the transpiled System Verilog output
     * @throws Exception
     */
    public static String transpile_hdl_to_sv(String code) throws Exception {

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
