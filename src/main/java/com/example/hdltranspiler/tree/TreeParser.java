/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.hdltranspiler.tree;

import com.example.hdltranspiler.HDLParser;
import java.util.Optional;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 *
 * @author Alexis Martinez
 */
public class TreeParser {

    // base step
    public static InternalNode parse(
            ParseTree node,
            HDLParser parser
    ) throws Exception {

        if (node instanceof TerminalNode) {
            throw new Exception("Error: bad structure");
        } else if (node instanceof ParserRuleContext) {
            String nodeDescription = node
                    .toStringTree(parser)
                    .split("\\(")[1] // get the description
                    .split(" ")[0]; // delete spaces

            return parse(
                    new InternalNode(nodeDescription),
                    node,
                    parser
            );
        } else if (node instanceof ErrorNode) {
            // It is an error node
            ErrorNode errorNode = (ErrorNode) node;
            throw new Exception("Error: " + errorNode.getText());
        }
        return null;
    }

    // inductive step
    public static InternalNode parse(
            InternalNode parent,
            ParseTree node,
            HDLParser parser
    ) throws Exception {

        if (node instanceof TerminalNode) {

            // It is a leaf node, representing a token
            // (e.g., an identifier, keyword, or literal)
            TerminalNode terminalNode = (TerminalNode) node;

            // System.out.println("Token: " + terminalNode.getSymbol().getText());
            parent.children.add(
                    new Leaf(terminalNode.getSymbol().getText())
            );

        } else if (node instanceof ParserRuleContext) {
            // It is an internal node, representing a rule invocation
            ParserRuleContext ruleContext = (ParserRuleContext) node;

            String nodeDescription = node
                    .toStringTree(parser)
                    .split("\\(")[1] // get the description
                    .split(" ")[0]; // delete spaces

            parent.children.add(
                    new InternalNode(nodeDescription)
            );

            for (ParseTree child : ruleContext.children) {
                parse(parent, child, parser);
            }
        } else if (node instanceof ErrorNode) {
            // It is an error node
            ErrorNode errorNode = (ErrorNode) node;
            throw new Exception("Error: " + errorNode.getText());
        }
        return parent;
    }
}
