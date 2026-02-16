/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.hdltranspiler.operations;

import com.example.hdltranspiler.tree.InternalNode;
import com.example.hdltranspiler.tree.InternalNodeLinked;
import com.example.hdltranspiler.tree.Leaf;
import com.example.hdltranspiler.tree.Node;
import java.util.ArrayList;
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

    public void module_def_transpile(InternalNode module_def) {
        module_def.children.remove(1);
        module_def.children.add(1, new Leaf(" ", module_def));
    }

    public void program_transpile(InternalNode program_def) {
        // node.replaceAtIndexByDescription(";", new Leaf("(\n\t", node), 0);
        // node.replaceAtIndexByDescription(";", new Leaf("(\n\t", node), 2);

        program_def.children.remove(1);
        program_def.children.add(1, new Leaf("(\n\t", program_def));

        program_def.children.remove(5);
        program_def.children.add(5, new Leaf("\n);\n\t", program_def));

        Node child_node;
        Leaf child_leaf;

        for (int i = 0; i < 5; i++) {
            child_node = program_def.children.get(i);
            if (child_node instanceof Leaf) {
                child_leaf = (Leaf) child_node;
                if (child_leaf.description.equals(";")) {
                    child_leaf.description = ",\n\t";
                }
            }
        }

        InternalNode body = new InternalNode("body", program_def);
        program_def.children.add(body);

        body.children.add(program_def.children.get(6));// memory 
        body.children.add(program_def.children.get(7)); // ;

        body.children.add(program_def.children.get(8)); // sequence_def
        body.children.add(program_def.children.get(9)); // ;

        program_def.children.remove(9); // sequence_def
        program_def.children.remove(8); // ;
        program_def.children.remove(7); // memory
        program_def.children.remove(6); // ;

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

    public void array_transpile(InternalNode variable_def) {
        String variable = variable_def.children.get(0).description;
        Integer size = Integer.parseInt(
                variable_def.children.get(2).description
        ) - 1;

        variable_def.children.clear();
        variable_def.children.add(new Leaf("[", variable_def));
        variable_def.children.add(new Leaf(size.toString(), variable_def));
        variable_def.children.add(new Leaf(":", variable_def));
        variable_def.children.add(new Leaf("0", variable_def));
        variable_def.children.add(new Leaf("]", variable_def));
        variable_def.children.add(new Leaf(" ", variable_def));
        variable_def.children.add(new Leaf(variable, variable_def));

    }

    public void create_type_def_state(
            InternalNode type_def_state,
            int n_steps
    ) {

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
                    "\t\tS" + i + " = " + n_steps + "'b" + str_binary + ",\n", type_def_state
            ));
            str_binary = "0" + str_binary.substring(0, n_steps - 1);
        }
        /*
        type_def_state.children.add(new Leaf(
                + "S3 = b'011,\n"
         */
        type_def_state.children.add(new Leaf(
                "\t} state_t;\n", type_def_state
        ));
        type_def_state.children.add(new Leaf(
                "\tstate_t next_state;\n", type_def_state
        ));

        type_def_state.children.add(new Leaf(
                "\tstate_t state;\n", type_def_state
        ));

    }

    private void create_always_ff(InternalNode sequence_def, InternalNode memory_def) {

        sequence_def.children.add(new Leaf(
                "always_ff @(posedge clk or negedge reset) begin\n"
                + "\tif (!reset) begin\n", sequence_def
        ));

        for (InternalNode node : memory_def.getAllDescendencyByDescription("variable_def")) {
            String aux_str_binary = "";
            String var_name = node.children.get(0).description;
            if (node.children.size() == 1) {
                // variable
                aux_str_binary = "0";
                sequence_def.children.add(new Leaf(
                        "\t\t" + var_name + " <= 1'b" + aux_str_binary + ";\n",
                        sequence_def
                ));

            } else if (node.children.size() == 4) {
                //array
                int size_array = Integer.parseInt(node.children.get(2).description);
                for (int i = 0; i < size_array; i++) {
                    aux_str_binary += "0";
                }
                sequence_def.children.add(new Leaf(
                        "\t\t" + var_name + " <= " + size_array + "'b" + aux_str_binary + ";\n",
                        sequence_def
                ));
            }
        }
        sequence_def.children.add(new Leaf(
                "\t\tstate <= S0;\n"
                + "\tend else begin\n", sequence_def
        ));

        for (InternalNode node : memory_def.getAllDescendencyByDescription("variable_def")) {
            String var_name = node.children.get(0).description;
            sequence_def.children.add(new Leaf(
                    "\t\t" + var_name + " <= next_" + var_name + ";\n",
                    sequence_def
            ));
        }

        sequence_def.children.add(new Leaf(
                "\t\tstate <= next_state;\n"
                + "\tend;\n"
                + "end;", sequence_def
        ));
    }

    private void create_memory_declaration(InternalNode sequence_def, InternalNode memory_def_clone) {
        // get memory_def from reference and add it to the sequence 
        // as memory definition of next, needed because i made it inspirated by
        // Mealy 
        // we add a memory def with some modifications
        memory_def_clone.children.get(0).description = "logic";
        memory_def_clone.children.get(1).description = " ";

        sequence_def.children.add(memory_def_clone);
        sequence_def.children.add(new Leaf(";", sequence_def));

        for (InternalNode node : memory_def_clone.getAllDescendencyByDescription("variable_def")) {
            Leaf leaf = (Leaf) node.children.get(0);
            leaf.description = "next_" + leaf.description;
        }
    }

    public void sequence_def_transpile(InternalNode sequence_def) {

        InternalNode sequence_ref = ((InternalNodeLinked) sequence_def).reference_tree;
        InternalNode program_ref = sequence_ref.findAscendency("program");
        InternalNode memory_ref = (InternalNode) program_ref
                .getChildrenByDescription("memory_def");
        InternalNode output_ref = (InternalNode) program_ref
                .getChildrenByDescription("output_def");
        // InternalNode steps_memory_def = (InternalNode) sequence_ref.getChildrenByDescription("steps_def");

        int n_steps = Integer.parseInt(sequence_ref.children.get(2).description);

        InternalNode steps_memory_def = (InternalNode) sequence_def.children.get(5);
        // InternalNode steps_memory_def = 
        steps_memory_def.description = "steps_on_always_def";

        /* commented because of exists build */
        for (Node steps_def : steps_memory_def.getAllDescendencyByDescription("steps_def")) {
            steps_def.description = "steps_on_always_def";
        }

        /**/
        InternalNode type_def_state = new InternalNode("type_def_state", sequence_def);

        sequence_def.children.clear();

        create_memory_declaration(sequence_def, memory_ref.clone());
        create_type_def_state(type_def_state, n_steps);
        sequence_def.children.add(type_def_state);
        create_always_ff(sequence_def, memory_ref.clone());
        create_always_comb(sequence_def, memory_ref.clone(), output_ref.clone(), sequence_ref.clone());

        sequence_def.children.add(new Leaf("end;", sequence_def));

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
        } else if (parent.description.equals("steps_on_always_def")) {

            steps_def_on_always_transpile(parent);
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

    public void steps_def_on_always_transpile(InternalNode parent) {

        InternalNode step_def = (InternalNode) parent.children.get(5);

        parent.children.get(1).description = "S";
        parent.children.get(4).description = ": begin\n";
        parent.children.get(7).description = "end\n";

        // remove transitions, dont use it
        parent.children.remove(6);
        parent.children.remove(3);
        parent.children.remove(0);
    }
    // OK
    public void step_comb_def_transpile(InternalNode step_comb_def) {
        InternalNode assign = (InternalNode) step_comb_def.children.get(0);
        assign.children.get(1).description = "=";

        step_comb_def.children.get(1).description = ";\n";
        /*
        if (step_comb_def.children.size() == 3) {
            step_comb_def_transpile(
                    (InternalNode) (step_comb_def.children.get(2)));
        }*/

    }

    public void step_comb_variable_def_transpile(InternalNode variable) {

    }

    private void create_always_comb(
            InternalNode sequence_def, InternalNode memory_ref,
            InternalNode output_ref, InternalNode sequence_ref) {

        sequence_def.children.add(new Leaf("\nalways_comb begin \n", sequence_def));
        sequence_def.children.add(new Leaf(
                "\t\tnext_state = state;\n",
                sequence_def
        ));
        for (InternalNode node : memory_ref.getAllDescendencyByDescription("variable_def")) {
            String var_name = node.children.get(0).description;
            sequence_def.children.add(new Leaf(
                    "\t\tnext_" + var_name + " = " + var_name + ";\n",
                    sequence_def
            ));
        }

        for (InternalNode node : output_ref.getAllDescendencyByDescription("variable_def")) {
            String aux_str_binary = "";
            String var_name = node.children.get(0).description;
            if (node.children.size() == 1) {
                // variable

                sequence_def.children.add(new Leaf(
                        "\t\t" + var_name + " = 1'b0;\n",
                        sequence_def
                ));

            } else if (node.children.size() == 4) {
                //array
                int size_array = Integer.parseInt(node.children.get(2).description);
                for (int i = 0; i < size_array; i++) {
                    aux_str_binary += "0";
                }
                sequence_def.children.add(new Leaf(
                        "\t\t" + var_name + " = " + size_array + "'b" + aux_str_binary + ";\n",
                        sequence_def
                ));
            }
        }
        sequence_def.children.add(new Leaf("case(state)\n", sequence_def));

        InternalNode real_steps_ref = (InternalNode) sequence_ref.getDescendencyByDescription("steps_def").clone();

        for (Node child : real_steps_ref.getAllDescendencyByDescription("assign_memory")) {
            Leaf memory_affected = (Leaf) ((InternalNode) child).children.get(0);
            memory_affected.description = "next_" + memory_affected.description;

        }

        sequence_def.children.add(real_steps_ref);

        real_steps_ref.description = "steps_on_always_def";

        /* commented because of exists build*/
        for (Node step_def_node : real_steps_ref.getAllDescendencyByDescription("step_def")) {
            InternalNode step_def = (InternalNode) step_def_node;
            step_def.description = "step_comb_def";
            step_comb_def_transpile(step_def);


        }/**/
        
        
        for (Node steps_def_node : sequence_ref.getAllDescendencyByDescription("steps_def")) {
            InternalNode steps_def = (InternalNode) steps_def_node;
            for (Node step_transition_def
                    : steps_def.getAllDescendencyByDescription("step_transition")) {
                ArrayList<InternalNode> conditions = ((InternalNode) steps_def).getAllDescendencyByDescription("conditions");
                ArrayList<InternalNode> goto_def = ((InternalNode) steps_def).getAllDescendencyByDescription("goto");
                sequence_def.children.add(new Leaf("if (", steps_def));
                sequence_def.children.add(conditions.get(0));
                sequence_def.children.add(new Leaf(") next_state = S" + ((InternalNode) goto_def.get(0)).children.get(0).description + ";\n", steps_def));

                for (int i = 1; i < conditions.size(); i++) {
                    sequence_def.children.add(new Leaf("else if (", sequence_def));
                    sequence_def.children.add(conditions.get(i));
                    sequence_def.children.add(new Leaf(") next_state = S" + ((InternalNode) goto_def.get(i)).children.get(0).description + ";\n", sequence_def));

                }
                sequence_def.children.add(new Leaf("end\n", sequence_def));

            }

        }

        sequence_def.children.add(new Leaf("endcase;", sequence_def));
        sequence_def.children.add(new Leaf("end;", sequence_def));
    }

}
// 577