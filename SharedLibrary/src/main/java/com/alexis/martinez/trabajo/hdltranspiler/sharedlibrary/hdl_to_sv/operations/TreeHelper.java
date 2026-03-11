/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.hdl_to_sv.operations;

import com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.hdl_to_sv.tree.InternalNode;
import com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.hdl_to_sv.tree.InternalNodeLinked;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 * This class is something between Visitor design pattern because i export all
 * nodes on each method and Template method because i have hardcoded all child
 * calls, instead of call it on build()
 *
 * @since 1.0.0
 * @author Alexis Martinez
 */
public class TreeHelper {

    private final ParseTree intermedianTree;
    private final String[] rule_names;

    public TreeHelper(ParseTree tree, Parser parser) {
        this.intermedianTree = tree;
        this.rule_names = parser.getRuleNames();

    }

    public void print() {
        intermedianTree.getText();
    }

    public String getText(CommonTokenStream tokens) {
        return tokens.getText(intermedianTree.getSourceInterval());
    }
    
    public String get_rule(ParseTree node) {
        int idx = ((ParserRuleContext) node).getRuleIndex();
        if (idx > rule_names.length) {
            Logger.getLogger(com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.sv_to_v.operations.TreeHelper.class.getName()).log(Level.SEVERE, "Invalid rule");
        }
        return rule_names[idx];
    }
}
