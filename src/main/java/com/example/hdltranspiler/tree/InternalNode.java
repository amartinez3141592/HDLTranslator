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

    public void print() {
        print(this);
    }
    
    public InternalNodeLinked clone_linked() {
        InternalNodeLinked root = new InternalNodeLinked(description, parent, this);
        
        
        for (Node child : this.children) {
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

    public void print(InternalNode parent) {

        for (Node node : parent.children) {
            if (node instanceof InternalNode) {
                print((InternalNode) node);
            } else if (node instanceof Leaf) {
                System.out.print(node.description);
            }
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
        return getDescendencyByDescription(this, description);
    }

    public Node getDescendencyByDescription(InternalNode parent, String description) {
        Node n = null;

        for (Node child : parent.children) {
            if (child.description.equals(description)) {
                n = child;
            }
        }

        if (n == null) {
            for (Node child : parent.children) {
                if (child instanceof InternalNode) {
                    return getDescendencyByDescription((InternalNode) child, description);
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

    public void replaceAtIndexByDescription(String description, Node node, int index_of_result) {
        ArrayList<Node> replaced_list = getChildrensByDescription(description);
        Node replaced = replaced_list.get(index_of_result);

        int actual_idx = replaced.parent.children.indexOf(replaced);

        replaced.parent.children.remove(actual_idx);
        replaced.parent.children.add(actual_idx, node);

    }

    public ArrayList<Leaf> getAllDescendentLeaf() {
        return getAllDescendentLeaf(this);
    }

    public ArrayList<Leaf> getAllDescendentLeaf(InternalNode node) {
        ArrayList<Leaf> n = new ArrayList<>();

        for (Node child : node.children) {
            if (child instanceof Leaf) {
                n.add((Leaf) child);
            } else {
                n.addAll(getAllDescendentLeaf((InternalNode) child));
            }
        }

        return n;
    }
    public ArrayList<InternalNode> getAllDescendencyByDescription(String description) {
        return getAllDescendencyByDescription(description, this);
    }

    public ArrayList<InternalNode> getAllDescendencyByDescription(String description, InternalNode node) {
        ArrayList<InternalNode> node_list = new ArrayList<>();

        for (Node child : node.children) {
            if ((child instanceof InternalNode)) {
                
                if (child.description.equals(description)) {
                    node_list.add((InternalNode) child);
                }
                
                node_list.addAll(
                        getAllDescendencyByDescription(
                                description,
                                (InternalNode) child
                        )
                );
            }
        }

        return node_list;
    }

}
