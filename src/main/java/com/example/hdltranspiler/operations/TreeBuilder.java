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
        System.out.println();
    }

    public void module_def_transpile(InternalNode node) {
        node.children.remove(1);
        node.children.add(1, new Leaf(" "));
    }

    public void program_transpile(InternalNode node) {
        node.children.remove(1);
        node.children.add(1, new Leaf("(\n\t"));

        node.children.remove(5);
        node.children.add(5, new Leaf("\n);\n\t"));

        Node child_node;
        Leaf child_leaf;

        for (int i = 0; i < 5; i++) {
            child_node = node.children.get(i);
            if (child_node instanceof Leaf) {
                child_leaf = (Leaf) child_node;
                if (child_leaf.value.equals(";")) {
                    child_leaf.value = ",\n\t";
                }
            }
        }
        InternalNode body = new InternalNode("body");
        body.children.add(node.children.get(6));// memory 
        body.children.add(node.children.get(7)); // ;

        body.children.add(node.children.get(8)); // sequence_def
        body.children.add(node.children.get(9)); // ;

        node.children.add(body);

        node.children.remove(9); // sequence_def
        node.children.remove(8); // ;
        node.children.remove(7); // memory
        node.children.remove(6); // ;

    }

    public void input_def_transpile(
            InternalNode inpud_def,
            String tag_of_elements,
            String input_divider) {
        //if (((Leaf) node.children.get(1)).value.equals(":")) {
        inpud_def.children.remove(1);
        inpud_def.children.add(1, new Leaf(" "));

        ((Leaf) inpud_def.children.get(0)).value = tag_of_elements;
        // first input_def 
        input_def_transpile(inpud_def, ((InternalNode) inpud_def.children.get(2)), tag_of_elements, input_divider);
        //}
    }

    public void input_def_transpile(
            InternalNode input_def,
            InternalNode list_node,
            String tag_of_elements,
            String input_divider
    ) {
        if (list_node.children.size() == 3) {
            /*i
         input_def             input_def
                |                |    |
                input_list       il , s or leaf  
                | |  |      -->  |    |
                | |  |           |    |   f,s are leaf or trees
                f ,  s           f    
                                    
            */
            InternalNode last_element = (InternalNode) list_node.children.getLast();
            add_input(input_def, last_element, tag_of_elements, input_divider);
            list_node.children.removeLast(); // remove list
            list_node.children.removeLast(); // remove ,
            input_def_transpile(input_def, last_element, tag_of_elements, input_divider);
         
        }
    }

    public void add_input(InternalNode input_ded, Node variable_or_list_def,
            String tag_of_elements, String input_divider) {
        input_ded.children.add(new Leaf(input_divider));

        input_ded.children.add(new Leaf(tag_of_elements));
        input_ded.children.add(new Leaf(" "));
        //InternalNode in = new InternalNode("input_list");
        //input_def_node.children.add(in);

        //in.children.add(variable_def);
        input_ded.children.add(variable_or_list_def);

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

    }

    public void sequence_def_transpile(InternalNode parent) {
        //
        
        ((Leaf) parent.children.get(0)).value = "always @(state) begin \n";
        
        
        InternalNode step_memory_and_output_def = (InternalNode) parent.children.get(2);
        step_memory_and_output_def.description = "step_memory_and_output_def";

        ((Leaf) parent.children.get(3)).value = "end;";
        
        parent.children.remove(1); // remove :
        
        //
        
        parent.children.add(new Leaf("always @(state) begin \n"));

        InternalNode step_transitions_def = step_memory_and_output_def.clone();
        step_transitions_def.description = "step_transitions_def";

        parent.children.add(step_transitions_def);
        parent.children.add(new Leaf("end;"));
        
        //
        
        Leaf step_transition_on_change_clk_or_reset = new Leaf(
        "always_ff @(posedge clk or posedge reset) begin\n" +
            "if (reset)\n" +
            "state <= S0;\n" +
            "else\n" +
            "state <= next_state;\n" +
        "end"
        );
        parent.children.add(step_transition_on_change_clk_or_reset);
        
        //parent.children.add(new Leaf(""))
    }

    public void build() {
        build(editableTree);
    }

    public void build(InternalNode parent) {

        if (parent.description.equals("program")) {
            program_transpile(parent);

            System.out.println(parent.description);
            print();

        } else if (parent.description.equals("module_def")) {
            module_def_transpile(parent
            );

            System.out.println(parent.description);
            print();

        } else if (parent.description.equals("input_def")) {
            input_def_transpile(parent, "input wire", ",\n\t");

            System.out.println(parent.description);
            print();

        } else if (parent.description.equals("output_def")) {
            input_def_transpile(parent, "output reg", ",\n\t");

            System.out.println(parent.description);
            print();

        } else if (parent.description.equals("memory_def")) {

            input_def_transpile(parent, "reg", ";\n\t");
            System.out.println(parent.description);
            print();

        } else if (parent.description.equals("variable_def")) {

            if (parent.children.size() == 4) {
                array_transpile(parent);
            }
        } else if (parent.description.equals("sequence_def")) {
            sequence_def_transpile(parent);

        } else if (parent.description.equals("step_transitions_def")) {

            step_transitions_def_transpile(parent);
        } else if (parent.description.equals("step_memory_and_output_def")) {

            step_memory_and_output_def_transpile(parent);
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

    public void delete_assign_output_def(InternalNode step_def) {
        String description = ((InternalNode) step_def.children.getFirst()).description;
        if ((step_def.children.size() == 3)
                && description.equals("assign_output")) {
            // when steps_def -> step_def with 3 length
            // and desired deletion node in first place 

            InternalNode next_step_def = (InternalNode) step_def.children.get(2);
            step_def.children = next_step_def.children;
            delete_assign_output_def(step_def);

        } else if (description.equals("assign_output")) {
            // size 1 and desired deletion node is on the only one
            step_def.children.clear();
        } else if ((!description.equals("assign_output")) && (step_def.children.size() == 3)) {
            // when the first node is not the actual desired deletion node
            // and size is 3, go to the other function because if there is
            // a desired node in the last place, i must need access to parent
            // because i must need to delete the last COMMA
            InternalNode next_step_def = (InternalNode) step_def.children.get(2);
            delete_assign_output_def(step_def, next_step_def);
        }

    }

    // step, instrucions inside that step
    public void delete_assign_output_def(InternalNode parent, InternalNode step_def) {
        String description = ((InternalNode) step_def.children.getFirst()).description;
        if ((step_def.children.size() == 3)
                && description.equals("assign_output")) {

            // when step_def -> step_def with 3 length
            // and desired deletion node in first place
            InternalNode next_step_def = (InternalNode) step_def.children.get(2);
            step_def.children = next_step_def.children;
            delete_assign_output_def(parent, step_def);

        } else if (description.equals("assign_output")) {
            // if the desired deletion node is the actual first node, delete the
            // actual node and the comma, and mantain the parent because there
            // is no problem with node parent because has already been passed
            // in the recursion
            parent.children.remove(1);
            parent.children.remove(0);
            step_def.children.clear();
        } else if(step_def.children.size() == 3) {
            // continues otherwise
            InternalNode next_step_def = (InternalNode) step_def.children.get(2);
            delete_assign_output_def(step_def, next_step_def);
        }

    }

    public void delete_assign_memory_def(InternalNode step_def) {
        String description = ((InternalNode) step_def.children.getFirst()).description;
        if ((step_def.children.size() == 3)
                && description.equals("assign_memory")) {
            InternalNode next_step_def = (InternalNode) step_def.children.get(2);
            step_def.children = next_step_def.children;
            delete_assign_memory_def(step_def);

        } else if (description.equals("assign_memory")) {
            step_def.children.clear();
        } else if ((!description.equals("assign_memory")) && step_def.children.size() == 3) {
            InternalNode next_step_def = (InternalNode) step_def.children.get(2);

            delete_assign_memory_def(step_def, next_step_def);
        }

    }

    public void delete_assign_memory_def(InternalNode parent, InternalNode step_def) {
        String description = ((InternalNode) step_def.children.getFirst()).description;
        if ((step_def.children.size() == 3)
                && description.equals("assign_memory")) {
            InternalNode next_step_def = (InternalNode) step_def.children.get(2);
            step_def.children = next_step_def.children;
            delete_assign_memory_def(parent, step_def);

        } else if (description.equals("assign_memory")) {
            parent.children.remove(1);
            parent.children.remove(0);
            step_def.children.clear();
        } else if(step_def.children.size() == 3) {
            InternalNode next_step_def = (InternalNode) step_def.children.get(2);
            delete_assign_memory_def(step_def, next_step_def);
        }

    }

    public void step_transitions_def_transpile(InternalNode parent) {
        InternalNode step_def = (InternalNode) parent.children.get(5);
        // delete output and input because i dont use it
        delete_assign_memory_def(step_def);
        delete_assign_output_def(step_def);

    }

    public void step_memory_and_output_def_transpile(InternalNode parent) {

        InternalNode step_def = (InternalNode) parent.children.get(5);
        
        // remove transitions, dont use it
        parent.children.remove(6);
        
    }

}
