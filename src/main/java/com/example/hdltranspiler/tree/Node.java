/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.hdltranspiler.tree;

/**
 *
 * @author Alexis Martinez
 */
public abstract class Node {

    public String description;
    public InternalNode parent;

    public abstract Node clone();
    public abstract Node clone_linked();
    public abstract void print();
    
    public InternalNode search_parent_with_description(String description) {
        return search_parent_with_description(description, parent);
    }

    public InternalNode search_parent_with_description(String description, Node node) {
        if (node.description.equals(description)) {

            return (InternalNode) node;
        } else {
            return search_parent_with_description(description, node.parent);
        }
    }
}
