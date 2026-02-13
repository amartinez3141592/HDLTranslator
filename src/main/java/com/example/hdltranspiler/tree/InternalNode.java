/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.hdltranspiler.tree;

import java.util.ArrayList;

/**
 *
 * @author Alexis Martinez
 */
public class InternalNode implements Node {

    public ArrayList<Node> children;
    public String description;

    public InternalNode(String description) {
        this.description = description;
        this.children = new ArrayList<Node>();
    }

    public void print() {
        print(this);
    }
    
    public InternalNode clone() {
        InternalNode root = new InternalNode(description);
        for(Node child :  this.children){
            if (child instanceof InternalNode) {
                root.children.add(((InternalNode) child).clone());
            } else {
                root.children.add(((Leaf) child).clone());
            }
        }
        return root;
    }
    
    public void print(InternalNode parent) {

        for (Node node : parent.children) {
            if (node instanceof InternalNode) {
                print((InternalNode) node);
            } else if (node instanceof Leaf) {
                System.out.print(((Leaf) node).value);
            }
        }
    }
}
