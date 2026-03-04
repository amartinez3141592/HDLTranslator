/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.sv_to_v;

import com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.sv.SystemVerilogLexer;
import com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.sv.SystemVerilogParser;
import com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.sv_to_v.operations.TreeHelper;
import com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.sv_to_v.operations.Visitor;

import java.util.Arrays;
import org.antlr.v4.gui.TreeViewer;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 *
 * @author Alexis Martinez
 */
public class SystemVerilogToVerilog {

    private static CodePointCharStream input;
    private static SystemVerilogLexer lexer;
    private static SystemVerilogParser parser;
    private static CommonTokenStream tokens;
    private static ParseTree tree;

    public static String transpile_sv_to_v(String code) throws Exception {

        input = CharStreams.fromString(code);

        lexer = new SystemVerilogLexer(input);
        tokens = new CommonTokenStream(lexer);
        parser = new SystemVerilogParser(tokens);

        tree = parser.program();

       /* InternalNode editableTree = CustomTreeEquivalent.parse(tree, parser);
        
        */
        TreeHelper helper = new TreeHelper(tree, parser);
        
        var v = new Visitor(parser, helper);
        String visit = v.visit(tree);
        return visit;

    }

    public static String toStringTreeForLookingForSyntaxErrors() {
        return tree.toStringTree(parser);
    }

    public static void visualize() {
        var viewer = new TreeViewer(Arrays.asList(parser.getRuleNames()), tree);
        viewer.open();
    }
}
