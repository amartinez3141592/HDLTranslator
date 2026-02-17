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

/**
 *
 * @author Alexis Martinez
 */
public class TreeBuilder {

    private final InternalNodeLinked editableTree;

    public TreeBuilder(InternalNode referenceTree) {
        this.editableTree = referenceTree.clone_linked();
    }

    public void print() {
        editableTree.print();
        System.out.println();
    }

    public String toString() {
        return editableTree.toString();
    }

    public String add_tab(int n) {
        String a = "";
        for (int i = 0; i < n; i++) {
            a += "\t";
        }
        return a;
    }

    public void module_def_transpile(InternalNode module_def) {
        module_def.children.remove(1);
        module_def.children.add(1, new Leaf(" "));
    }
    
    public void program_transpile(InternalNode program_def) {
        // delete first ; and put (
        program_def.children.get(1).description = "(\n";
        program_def.children.get(3).description = ",\n";

        program_def.getChildrenByDescription(";").description = ",\n";
        program_def.getChildrenByDescription(";").description = ",\n";

        InternalNodeLinked input_def = (InternalNodeLinked) program_def.getChildrenByDescription("input_def");
        input_def_transpile(input_def, add_tab(1) + "input logic", ",\n");

        InternalNodeLinked output_def = (InternalNodeLinked) program_def.getChildrenByDescription("output_def");
        input_def_transpile(output_def, add_tab(1) + "output logic", ",\n");

        program_def.children.remove(5);
        program_def.children.add(5, new Leaf("\n);\n"));

        InternalNodeLinked module_def = (InternalNodeLinked) program_def.getChildrenByDescription("module_def");
        module_def_transpile(module_def);

        InternalNodeLinked sequence_def = (InternalNodeLinked) program_def.getChildrenByDescription("sequence_def");
        sequence_def_transpile(
                sequence_def,
                (InternalNodeLinked) program_def.getChildrenByDescription("memory_def"),
                output_def.reference_tree.clone());

        program_def.children.get(9).description = "endmodule\n";
        
        if (program_def.children.size() == 12) {
            var next_program_def = (InternalNode) program_def.children.get(10);
            if (next_program_def.children.size() != 0) {
                program_transpile(next_program_def);
            }
            
        }
    }

    public void input_def_transpile(
            InternalNode input_def,
            String tag_of_elements,
            String input_divider) {

        input_def.children.remove(1);
        input_def.children.add(1, new Leaf(" "));

        input_def.children.get(0).description = tag_of_elements;
        input_def_transpile(input_def, ((InternalNode) input_def.children.get(2)), tag_of_elements, input_divider);

    }

    public void input_def_transpile(
            InternalNode input_def,
            InternalNode list_node,
            String tag_of_elements,
            String input_divider
    ) {
        if (list_node.children.size() == 3) {
//            i
//         input_def             input_def
//                |                |    |
//                input_list       il , s or leaf  
//                | |  |      -->  |    |
//                | |  |           |    |   f,s are leaf or trees
//                f ,  s           f    

            InternalNode last_element = (InternalNode) list_node.children.getLast();
            add_input(input_def, last_element, tag_of_elements, input_divider);
            list_node.children.removeLast(); // remove list
            list_node.children.removeLast(); // remove ,
            input_def_transpile(input_def, last_element, tag_of_elements, input_divider);

        }
    }

    public void add_input(InternalNode input_def, InternalNode list_def,
            String tag_of_elements, String input_divider) {
        input_def.children.add(new Leaf(input_divider));

        input_def.children.add(new Leaf(tag_of_elements));
        input_def.children.add(new Leaf(" "));
        input_def.children.add(list_def);

        InternalNode variable_def = (InternalNode) list_def.getDescendencyByDescription("variable_def");

        if (variable_def.children.size() == 4) {
            array_transpile(variable_def);
        }
    }

    public void array_transpile(InternalNode variable_def) {
        String variable = variable_def.children.get(0).description;
        Integer size = Integer.parseInt(
                variable_def.children.get(2).description
        ) - 1;

        variable_def.children.clear();
        variable_def.children.add(new Leaf("["));
        variable_def.children.add(new Leaf(size.toString()));
        variable_def.children.add(new Leaf(":"));
        variable_def.children.add(new Leaf("0"));
        variable_def.children.add(new Leaf("]"));
        variable_def.children.add(new Leaf(" "));
        variable_def.children.add(new Leaf(variable));

    }

    public void create_type_def_state(
            InternalNode type_def_state,
            int n_steps
    ) {

        type_def_state.children.add(new Leaf(
                add_tab(1) + "typedef enum logic [" + (n_steps - 1) + ":0] {\n"
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
                    add_tab(2) + "S" + i + " = " + n_steps + "'b" + str_binary + ",\n"
            ));
            str_binary = "0" + str_binary.substring(0, n_steps - 1);
        }

        type_def_state.children.add(new Leaf(
                add_tab(1) + "} state_t;\n"
        ));
        type_def_state.children.add(new Leaf(
                add_tab(1) + "state_t next_state;\n"
        ));

        type_def_state.children.add(new Leaf(
                add_tab(1) + "state_t state;\n"
        ));

    }

    private void create_always_ff(InternalNode sequence_def, InternalNode memory_def) {

        sequence_def.children.add(new Leaf(
                add_tab(1) + "always_ff @(posedge clk or negedge reset) begin\n"
                + add_tab(2) + "if (!reset) begin\n"
        ));

        for (InternalNode node : memory_def.getAllDescendencyByDescription("variable_def")) {
            String aux_str_binary = "";
            String var_name = node.children.get(0).description;
            if (node.children.size() == 1) {
                // variable
                aux_str_binary = "0";
                sequence_def.children.add(new Leaf(
                        add_tab(3) + var_name + " <= 1'b" + aux_str_binary + ";\n"
                ));

            } else if (node.children.size() == 4) {
                //array
                int size_array = Integer.parseInt(node.children.get(2).description);
                for (int i = 0; i < size_array; i++) {
                    aux_str_binary += "0";
                }
                sequence_def.children.add(new Leaf(
                        add_tab(3) + var_name + " <= " + size_array + "'b" + aux_str_binary + ";\n"
                ));
            }
        }
        sequence_def.children.add(new Leaf(
                add_tab(3) + "state <= S0;\n"
                + add_tab(2) + "end else begin\n"
        ));

        for (InternalNode node : memory_def.getAllDescendencyByDescription("variable_def")) {
            String var_name = node.children.get(0).description;
            sequence_def.children.add(new Leaf(
                    add_tab(3) + var_name + " <= next_" + var_name + ";\n"
            ));
        }

        sequence_def.children.add(new Leaf(
                add_tab(3) + "state <= next_state;\n"
        ));
        sequence_def.children.add(new Leaf(
                add_tab(2) + "end\n"
        ));

        sequence_def.children.add(new Leaf(
                add_tab(1) + "end\n"
        ));

    }

    private void create_memory_declaration(InternalNode sequence_def, InternalNode new_memory_def) {
        // get memory_def from reference and add it to the sequence 
        // as memory definition of next, needed because i made it inspirated by
        // Mealy 
        // we add a memory def with some modifications
        input_def_transpile(new_memory_def, add_tab(1) + "logic", ";\n");

        // return new_memory_def ?
        InternalNode memory_def_with_next = new_memory_def.clone();

        sequence_def.children.add(memory_def_with_next);
        sequence_def.children.add(new Leaf(";\n"));

        for (InternalNode node : memory_def_with_next.getAllDescendencyByDescription("variable_def")) {
            Leaf leaf = (Leaf) node.children.get(0);
            leaf.description = "next_" + leaf.description;
        }
    }

    public void sequence_def_transpile(InternalNode sequence_def, InternalNodeLinked memory_def, InternalNode output_ref) {

        InternalNode sequence_ref = ((InternalNodeLinked) sequence_def).reference_tree;

        int n_steps = Integer.parseInt(sequence_ref.children.get(2).description);

        InternalNode type_def_state = new InternalNode("type_def_state");

        sequence_def.children.clear();

        create_memory_declaration(sequence_def, memory_def);
        create_type_def_state(type_def_state, n_steps);
        sequence_def.children.add(type_def_state);
        create_always_ff(sequence_def, memory_def.reference_tree);
        create_always_comb(sequence_def,
                memory_def.reference_tree,
                output_ref.clone(),
                sequence_ref.clone());

        sequence_def.children.add(new Leaf(add_tab(1) + "end\n"));

    }

    public void build() {
        program_transpile(editableTree);
    }

    private void create_always_comb(
            InternalNode sequence_def, InternalNode memory_ref,
            InternalNode output_ref, InternalNode sequence_ref) {

        sequence_def.children.add(new Leaf(add_tab(1) + "always_comb begin \n"));
        sequence_def.children.add(new Leaf(
                add_tab(2) + "next_state = state;\n"
        ));

        for (InternalNode node : memory_ref.getAllDescendencyByDescription("variable_def")) {
            String var_name = node.children.get(0).description;
            sequence_def.children.add(new Leaf(
                    add_tab(2) + "next_" + var_name + " = " + var_name + ";\n"
            ));
        }

        for (InternalNode node : output_ref.getAllDescendencyByDescription("variable_def")) {
            String aux_str_binary = "";
            String var_name = node.children.get(0).description;
            if (node.children.size() == 1) {
                // variable

                sequence_def.children.add(new Leaf(
                        add_tab(2) + var_name + " = 1'b0;\n"
                ));

            } else if (node.children.size() == 4) {
                //array
                int size_array = Integer.parseInt(node.children.get(2).description);
                for (int i = 0; i < size_array; i++) {
                    aux_str_binary += "0";
                }
                sequence_def.children.add(new Leaf(
                        add_tab(2) + var_name + " = " + size_array + "'b" + aux_str_binary + ";\n"
                ));
            }
        }
        sequence_def.children.add(new Leaf(add_tab(2) + "case(state)\n"));
        //
        InternalNode steps_ref = (InternalNode) sequence_ref.getDescendencyByDescription("steps_def");
        sequence_def.children.add(steps_ref);

        for (Node child : steps_ref.getAllDescendencyByDescription("assign_memory")) {
            Leaf memory_affected = (Leaf) ((InternalNode) child).children.get(0);
            memory_affected.description = "next_" + memory_affected.description;

        }
        for (Node step_def_node : steps_ref.getAllDescendencyByDescription("step_def")) {
            InternalNode step_def = (InternalNode) step_def_node;
            InternalNode assign = (InternalNode) step_def.children.get(0);
            assign.children.get(0).description = add_tab(4) + assign.children.get(0).description;
            
            // if it is conditionated assign to memory
            if (assign.description.equals("assign_memory") && assign.children.size() == 5) {
                Node condition = assign.children.get(2);
                // remove conditional specific syntax
                assign.children.remove(2);
                assign.children.remove(1);
                assign.children.get(0).description = add_tab(1) + assign.children.get(0).description;

                step_def.children.add(2, new Leaf("\n"+add_tab(4) + "end\n"));
                step_def.children.addFirst(new Leaf(")\n"));
                step_def.children.addFirst( condition);
            
                step_def.children.addFirst(new Leaf(add_tab(4) + "if("));
            }
            
            assign.children.get(1).description = "=";
            step_def.children.get(1).description = ";\n";
            
        }
        //

        for (Node steps_def_node : sequence_def.getAllDescendencyByDescription("steps_def")) {
            InternalNode steps_def = (InternalNode) steps_def_node;

            steps_def.children.get(1).description = add_tab(3) + "S";
            steps_def.children.get(3).description = ":";
            steps_def.children.get(4).description = " begin\n";
            steps_def.children.get(7).description = add_tab(3) + "end\n";
            steps_def.children.remove(0);

            InternalNode step_transition = (InternalNode) steps_def.getChildrenByDescription("step_transition");

            ArrayList<InternalNode> conditions = step_transition.getAllDescendencyByDescription("conditions");
            ArrayList<InternalNode> goto_def = step_transition.getAllDescendencyByDescription("goto");

            // we have all info so i clear the step transition info
            step_transition.children.clear();

            step_transition.children.add(new Leaf(add_tab(4) + "if ("));

            // TODO: make InternalNode an Iterator (hasNext(), next(), etc)
            InternalNode condition_with_the_list_of_conditions = conditions.get(0).clone();
            InternalNode condition_without_the_list_of_conditions = condition_with_the_list_of_conditions;

            condition_without_the_list_of_conditions.removeChildrenByDescription("conditions");
            condition_without_the_list_of_conditions.removeChildrenByDescription(",");

            step_transition.children.add(condition_without_the_list_of_conditions);
            step_transition.children.add(new Leaf(") next_state = S" + ((InternalNode) goto_def.get(0)).children.get(0).description + ";\n"));

            for (int i = 1; i < conditions.size(); i++) {
                step_transition.children.add(new Leaf(add_tab(4) + "else if ("));

                condition_with_the_list_of_conditions = conditions.get(i).clone();
                condition_without_the_list_of_conditions = condition_with_the_list_of_conditions;

                condition_without_the_list_of_conditions.removeChildrenByDescription("conditions");
                condition_without_the_list_of_conditions.removeChildrenByDescription(",");

                step_transition.children.add(condition_without_the_list_of_conditions);
                step_transition.children.add(new Leaf(") next_state = S" + ((InternalNode) goto_def.get(i)).children.get(0).description + ";\n"));

            }
            step_transition.children.add(new Leaf(add_tab(4) + "end\n"));

        }

        sequence_def.children.add(new Leaf(add_tab(2) + "endcase\n"));
    }

}
