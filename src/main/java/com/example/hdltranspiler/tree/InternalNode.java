/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.hdltranspiler.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 *
 * @author Alexis Martinez
 */
public class InternalNode extends Node {

    public ArrayList<Node> children;

    public InternalNode(String description, InternalNode parent) {
        this.parent = parent;
        this.children = new ArrayList<Node>();
        this.description = description;
    }

    
    public InternalNodeLinked clone_linked() {
        InternalNodeLinked root = new InternalNodeLinked(description, parent, this);
        
        for (Node child : children) {
            root.children.add(child.clone_linked());
        
        }
        return root;
    }
    
    public InternalNode clone() {
        InternalNode root = new InternalNode(description, parent);
        
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

        // TODO: verify if this works i mean if i return before this
        // return are not going to be executed
        return null;

    }

    public void removeChildrenByDescription(String description) {
        Node n = getChildrenByDescription(description);
        n.parent.children.remove(n);
        n.parent = null;

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

    @Deprecated
    public void replaceAtIndexByDescription(String description, Node node, int index_of_result) {
        ArrayList<Node> replaced_list = getChildrensByDescription(description);
        Node replaced = replaced_list.get(index_of_result);

        int actual_idx = replaced.parent.children.indexOf(replaced);

        replaced.parent.children.remove(actual_idx);
        replaced.parent.children.add(actual_idx, node);

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
    
    public InternalNode findAscendency(String description) {
        if(this.description.equals(description)) {
            return this;
        } else {
            return this.parent.findAscendency(description);
        }
    }
}
