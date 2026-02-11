/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.hdltranspiler.operations;

import com.example.hdltranspiler.tree.InternalNode;
import com.example.hdltranspiler.tree.Leaf;
import com.example.hdltranspiler.tree.Node;
import java.util.function.Consumer;

/**
 *
 * @author Alexis Martinez
 */
public class TreeBuilder {

    private final InternalNode editableTree;

    public TreeBuilder(InternalNode editableTree) {
        this.editableTree = editableTree;
    }

    public void print() {
        editableTree.print();
    }

    public void module_def_transpile(InternalNode node) {
        node.children.remove(1);
        node.children.add(1, new Leaf(" "));
    }

    public void program_transpile(InternalNode node) {
        node.children.remove(1);
        node.children.add(1, new Leaf("("));

        node.children.remove(5);
        node.children.add(5, new Leaf(");"));

        Node child_node;
        Leaf child_leaf;

        for (int i = 0; i < node.children.size(); i++) {
            child_node = node.children.get(i);
            if (child_node instanceof Leaf) {
                child_leaf = (Leaf) child_node ;
                if (child_leaf.value.equals(";")) {
                    child_leaf.value = ",";
                }
            }
        }

    }

    public void input_def_transpile(InternalNode node, String tag_of_elements) {
        //if (((Leaf) node.children.get(1)).value.equals(":")) {
        node.children.remove(1);
        node.children.add(1, new Leaf(" "));

        ((Leaf) node.children.get(0)).value = tag_of_elements;
        // first input_def 
        input_def_transpile(node, ((InternalNode) node.children.get(2)), tag_of_elements);
        //}
    }
/*
    (program 
        (module_def module : hello);
        (input_def input : (input_list (input_list c) , (input_list d)));
        (output_def output : (input_list (input_list (input_list c) , (input_list f)) , (input_list g [ 3 ]))) ;
    )
    */
    public void input_def_transpile(InternalNode input_def_node,
            InternalNode list_node, String tag_of_elements) {
        if (list_node.children.size() == 3) {

            InternalNode first_element = (InternalNode) list_node.children.getFirst();
            add_input(input_def_node, first_element, tag_of_elements);
            list_node.children.removeFirst();
            list_node.children.removeFirst();

            if (first_element.description.equals("input_list")) {
                //  replace actual list with the second variable
                InternalNode second_element = (InternalNode) list_node.children.getLast();
                int idx = input_def_node.children.indexOf(list_node);

                input_def_node.children.remove(idx);
                input_def_node.children.add(idx, second_element);

                input_def_transpile(input_def_node, first_element, tag_of_elements);
            }
        } else if (list_node.children.size() == 1 && list_node.description.equals("input_list")) {
            InternalNode first_element = (InternalNode) list_node.children.getFirst();
            add_input(input_def_node, first_element, tag_of_elements);
            list_node.children.removeFirst();          
        }
    }

    public void add_input(InternalNode input_def_node, Node variable_or_list_def,
            String tag_of_elements) {
        input_def_node.children.add(new Leaf(","));

        input_def_node.children.add(new Leaf(tag_of_elements));
        input_def_node.children.add(new Leaf(" "));
        //InternalNode in = new InternalNode("input_list");
        //input_def_node.children.add(in);

        //in.children.add(variable_def);
        input_def_node.children.add(variable_or_list_def);

    }

    public void array_transpile(InternalNode variable_node) {
        String variable = ((Leaf) variable_node.children.get(0)).value;
        Integer size = Integer.parseInt(
                ((Leaf) variable_node.children.get(2)).value
        ) - 1;

        variable_node.children.clear();
        variable_node.children.add(new Leaf("["));
        variable_node.children.add(new Leaf(size.toString()));
        variable_node.children.add(new Leaf(":"));
        variable_node.children.add(new Leaf("0"));
        variable_node.children.add(new Leaf("]"));
        variable_node.children.add(new Leaf(" "));
        variable_node.children.add(new Leaf(variable));
        variable_node.children.add(new Leaf(";"));

    }

    public void build() {
        build(editableTree);
    }

    public void build(InternalNode parent) {
        if (parent.description.equals("program")) {
            program_transpile(parent);

        } else if (parent.description.equals("module_def")) {
            module_def_transpile(parent);

        } else if (parent.description.equals("input_def")) {
            input_def_transpile(parent, "input wire");
        } else if (parent.description.equals("output_def")) {
            input_def_transpile(parent, "output reg");
        } else if (parent.description.equals("variable_def")) {
            if (parent.children.size() == 4) {
                array_transpile(parent);
            }
        }
        if (parent.children.size() == 4) {
            System.out.println();
            System.out.println(parent.description);
            System.out.println();

        }
        for (Node child : parent.children) {
            if (child instanceof Leaf) {
                build(parent, (Leaf) child);
            } else if (child instanceof InternalNode) {
                build((InternalNode) child);
            }
        }
    }

    public void build(InternalNode parent, Leaf node) {

    }

}
