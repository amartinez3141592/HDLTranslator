/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.hdltranspiler.operations;

import com.example.hdltranspiler.tree.InternalNode;
import com.example.hdltranspiler.tree.InternalNodeLinked;
import com.example.hdltranspiler.tree.Leaf;
import com.example.hdltranspiler.tree.Node;
import java.util.function.Consumer;

/**
 *
 * @author Alexis Martinez
 */
public class TreeBuilder {

    private final InternalNode referenceTree;
    private final InternalNodeLinked editableTree;

    public TreeBuilder(InternalNode referenceTree) {
        this.referenceTree = referenceTree;
        this.editableTree = referenceTree.clone_linked();
    }

    public void print() {
        editableTree.print();
        System.out.println();
    }

    public void module_def_transpile(InternalNode node) {
        node.children.remove(1);
        node.children.add(1, new Leaf(" ", node));
    }

    public void program_transpile(InternalNode node) {
        // node.replaceAtIndexByDescription(";", new Leaf("(\n\t", node), 0);
        // node.replaceAtIndexByDescription(";", new Leaf("(\n\t", node), 2);

        node.children.remove(1);
        node.children.add(1, new Leaf("(\n\t", node));

        node.children.remove(5);
        node.children.add(5, new Leaf("\n);\n\t", node));

        Node child_node;
        Leaf child_leaf;

        for (int i = 0; i < 5; i++) {
            child_node = node.children.get(i);
            if (child_node instanceof Leaf) {
                child_leaf = (Leaf) child_node;
                if (child_leaf.description.equals(";")) {
                    child_leaf.description = ",\n\t";
                }
            }
        }

        InternalNode body = new InternalNode("body", node);
        node.children.add(body);

        body.children.add(node.children.get(6));// memory 
        body.children.add(node.children.get(7)); // ;

        body.children.add(node.children.get(8)); // sequence_def
        body.children.add(node.children.get(9)); // ;

        node.children.remove(9); // sequence_def
        node.children.remove(8); // ;
        node.children.remove(7); // memory
        node.children.remove(6); // ;

    }

    public void input_def_transpile(
            InternalNode input_def,
            String tag_of_elements,
            String input_divider) {
        //if (((Leaf) node.children.get(1)).description.equals(":")) {
        input_def.children.remove(1);
        input_def.children.add(1, new Leaf(" ", input_def));

        input_def.children.get(0).description = tag_of_elements;
        // first input_def 
        input_def_transpile(input_def, ((InternalNode) input_def.children.get(2)), tag_of_elements, input_divider);
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

    public void add_input(InternalNode input_def, Node variable_or_list_def,
            String tag_of_elements, String input_divider) {
        input_def.children.add(new Leaf(input_divider, input_def));

        input_def.children.add(new Leaf(tag_of_elements, input_def));
        input_def.children.add(new Leaf(" ", input_def));
        //InternalNode in = new InternalNode("input_list");
        //input_def_node.children.add(in);

        //in.children.add(variable_def);
        input_def.children.add(variable_or_list_def);

    }

    public void array_transpile(InternalNode variable_node) {
        String variable = variable_node.children.get(0).description;
        Integer size = Integer.parseInt(
                variable_node.children.get(2).description
        ) - 1;

        variable_node.children.clear();
        variable_node.children.add(new Leaf("[", variable_node));
        variable_node.children.add(new Leaf(size.toString(), variable_node));
        variable_node.children.add(new Leaf(":", variable_node));
        variable_node.children.add(new Leaf("0", variable_node));
        variable_node.children.add(new Leaf("]", variable_node));
        variable_node.children.add(new Leaf(" ", variable_node));
        variable_node.children.add(new Leaf(variable, variable_node));

    }

    public void sequence_def_transpile(InternalNode parent) {
        // default structure positins
        Node steps_def_number = parent.children.get(2);
        InternalNode step_memory_def = (InternalNode) parent.children.get(5);
        Node end_leaf = parent.children.get(6);
        Node sequence_title = parent.children.get(0);
        
        // remove what is not being used
        parent.children.remove(4); // remove (
        parent.children.remove(3); // remove number
        parent.children.remove(2); // remove )
        parent.children.remove(1); // remove :
        
        int n_steps = Integer.parseInt(steps_def_number.description);

       
        // depend on state then on clk but indirectly
        sequence_title.description = "always @(state) begin \n"
                + "case(state)\n";

        step_memory_def.description = "step_memory_def";

        end_leaf.description = "end;";


        InternalNode step_output_def = step_memory_def.clone();
        step_output_def.description = "step_output_def";
   
        InternalNode step_transitions_def = step_memory_def.clone();
        step_transitions_def.description = "step_transitions_def";

 

        // 
        InternalNode type_def_state = new InternalNode("type_def_state", parent);

        type_def_state.children.add(new Leaf(
                "typedef enum logic [" + (n_steps - 1) + ":0] {\n", type_def_state
        ));

        String str_binary = "";

        for (int i = 0; i < (n_steps - 1); i++) {
            str_binary += "0";
        }

        // 0000000 size n_steps - 1
        str_binary = "1" + str_binary;

        // 10000000 size n_steps
        for (int i = 0; i < n_steps; i++) {
            type_def_state.children.add(new Leaf(
                    "S" + i + " = " + n_steps + "'b" + str_binary + ",\n", type_def_state
            ));
            str_binary = "0" + str_binary.substring(0, n_steps - 1);
        }
        /*
        type_def_state.children.add(new Leaf(
                "S0 = " + n_steps + "'b100,\n"
                + "S1 = 3'b001,\n"
                + "S2 = 3'b010,\n"
                + "S3 = 3'b011,\n"
                + "S4 = 3'b100,\n"
                + "S5 = 3'b101,\n"
                + "S6 = 3'b110\n"
        ));
         */
        type_def_state.children.add(new Leaf(
                "} state_t;\n", type_def_state
        ));
        type_def_state.children.add(new Leaf(
                "state_t next_state;\n", type_def_state
        ));

        type_def_state.children.add(new Leaf(
                "state_t state;\n", type_def_state
        ));

 
        // get memory_def from reference and add it to the sequence 
        // as memory definition of next, needed because i made it inspirated by
        // Mealy 
        // we add a memory def with some modifications
        InternalNode memory_def = (InternalNode) referenceTree.getDescendencyByDescription("memory_def");

        memory_def.children.get(0).description = "logic";
        memory_def.children.get(1).description = " ";

        for (InternalNode node : memory_def.getAllDescendencyByDescription("variable_def")) {
            Leaf leaf = (Leaf) node.children.get(0);
            leaf.description = "next_" + leaf.description;

        }

                // output changes independently of clock
        parent.children.add(new Leaf("always @(state) begin \n"
                + "case(state)\n", parent));
        parent.children.add(step_output_def);
        
        parent.children.add(new Leaf("end;", parent));

        // depend on state then on clk but indirectly
        parent.children.add(new Leaf("always @(state) begin \n"
                + "case(state)\n", parent));
       parent.children.add(step_transitions_def);
        parent.children.add(new Leaf("end;", parent));

        //
        parent.children.add(new Leaf(
                "always_ff @(posedge clk or posedge reset) begin\n"
                + "\tif (reset)\n"
                + "\t\tstate <= S0;\n"
                + "\telse\n"
                + "\t\tstate <= next_state;\n"
                + "end;", parent
        ));
        
        parent.children.addFirst(type_def_state);
        parent.children.addFirst(new Leaf(";", parent));
        parent.children.addFirst(memory_def);
        

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
            module_def_transpile(parent);

            System.out.println(parent.description);
            print();

        } else if (parent.description.equals("input_def")) {
            input_def_transpile(parent, "input logic", ",\n\t");

            System.out.println(parent.description);
            print();

        } else if (parent.description.equals("output_def")) {
            input_def_transpile(parent, "output logic", ",\n\t");

            System.out.println(parent.description);
            print();

        } else if (parent.description.equals("memory_def")) {

            input_def_transpile(parent, "logic", ";\n\t");
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
        } else if (parent.description.equals("step_memory_def")) {

            step_memory_def_transpile(parent);
        } else if (parent.description.equals("step_output_def")) {

            step_output_def_transpile(parent);
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
        } else if (step_def.children.size() == 3) {
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
        } else if (step_def.children.size() == 3) {
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

    public void step_memory_def_transpile(InternalNode parent) {

        InternalNode step_def = (InternalNode) parent.children.get(5);
        delete_assign_output_def(step_def);

        parent.children.get(1).description = "S";
        parent.children.get(4).description = ": begin\n";
        parent.children.get(7).description = "end\n";

        // remove transitions, dont use it
        parent.children.remove(6);
        parent.children.remove(3);
        parent.children.remove(0);
    }

    public void step_output_def_transpile(InternalNode parent) {

        InternalNode step_def = (InternalNode) parent.children.get(5);

        // remove transitions, dont use it
        parent.children.remove(6);
        delete_assign_memory_def(step_def);

    }

}
