/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.hdltranspiler.tree;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 *
 * @author Alexis Martinez
 */
public class TreeParser {
    public static InternalNode parse(ParseTree node) {
        if (node instanceof TerminalNode) {
            // It is a leaf node, representing a token (e.g., an identifier, keyword, or literal)
            TerminalNode terminalNode = (TerminalNode) node;
            System.out.println("Token: " + terminalNode.getSymbol().getText());
        } else if (node instanceof ParserRuleContext) {
            // It is an internal node, representing a rule invocation
            ParserRuleContext ruleContext = (ParserRuleContext) node;
            
            for(ParseTree child : ruleContext.children) {
                String nodeChildValue = child.getText();
                System.out.println("Rule index: " + nodeChildValue);
                parse(child);
            
            }
            
        } else if (node instanceof ErrorNode) {
            // It is an error node
            ErrorNode errorNode = (ErrorNode) node;
            System.out.println("Error: " + errorNode.getText());
        }
        System.out.println(node.toStringTree());
        return new InternalNode();
    }
}
