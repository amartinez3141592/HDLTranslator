/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.tree;

import java.util.ArrayList;

/**
 *
 * @author Alexis Martinez
 */
public class InternalNode extends Node {

    public ArrayList<Node> children;

    public InternalNode(String description) {
        this.children = new ArrayList<Node>();
        this.description = description;
    }

    
    public InternalNodeLinked clone_linked() {
        InternalNodeLinked root = new InternalNodeLinked(description, this);
        
        for (Node child : children) {
            root.children.add(child.clone_linked());
        
        }
        return root;
    }
    
    public InternalNode clone() {
        InternalNode root = new InternalNode(description);
        
        for (Node child : this.children) {
            root.children.add(child.clone());
        }
        return root;
    }

    public String toString() {
        String r = "";
        for (Node node : children) {
            r+= node.toString();
        }
        return r;
    }
    
    public void print() {

        for (Node node : this.children) {
            node.print();
        }
    }

    public Node getChildrenByDescription(String description) {
        Node n = null;
        for (Node child : children) {
            if (child.description.equals(description)) {
                n = child;
            }
        }
        return n;
    }

    public Node getDescendencyByDescription(String description) {
        Node n = null;

        for (Node child : children) {
            if (child.description.equals(description)) {
                n = child;
            }
        }

        if (n == null) {
            for (Node child : children) {
                if (child instanceof InternalNode) {
                    return ((InternalNode) child)
                            .getDescendencyByDescription(description);
                }
            }
        } else {
            return n;
        }
        return null;
    }

    public void removeChildrenByDescription(String description) {
        Node n = getChildrenByDescription(description);
        children.remove(n);
    }

    public ArrayList<Node> getChildrensByDescription(String description) {

        ArrayList<Node> n = new ArrayList<>();
        for (Node child : children) {
            if (child.description.equals(description)) {
                n.add(child);
            }
        }
        return n;

    }

    public ArrayList<Leaf> getAllDescendentLeaf() {
        ArrayList<Leaf> n = new ArrayList<>();

        for (Node child : children) {
            if (child instanceof Leaf) {
                n.add((Leaf) child);
            } else {
                n.addAll(((InternalNode) child).getAllDescendentLeaf());
            }
        }

        return n;
    }
    
    public ArrayList<InternalNode> getAllDescendencyByDescription(String description) {
        ArrayList<InternalNode> node_list = new ArrayList<>();

        for (Node child : children) {
            if ((child instanceof InternalNode)) {
                
                if (child.description.equals(description)) {
                    node_list.add((InternalNode) child);
                }
                
                node_list.addAll(((InternalNode) child).getAllDescendencyByDescription(
                                description)
                );
            }
        }

        return node_list;
    }
}
