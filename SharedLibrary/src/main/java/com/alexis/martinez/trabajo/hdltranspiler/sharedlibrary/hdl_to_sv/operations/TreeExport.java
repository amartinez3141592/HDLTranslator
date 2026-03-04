/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.hdl_to_sv.operations;

import com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.hdl_to_sv.tree.InternalNode;
import com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.hdl_to_sv.tree.InternalNodeLinked;
import com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.hdl_to_sv.tree.Leaf;
import com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.hdl_to_sv.tree.Node;
import java.util.ArrayList;

/**
 * This class is something between Visitor design pattern because i export all
 * nodes on each method and Template method because i have hardcoded all child
 * calls, instead of call it on build()
 *
 * @since 1.0.0
 * @author Alexis Martinez
 */
public class TreeExport {

    private final InternalNodeLinked editableTree;

    /**
     * @since 1.0.0
     * @param referenceTree
     */
    public TreeExport(InternalNode referenceTree) {
        this.editableTree = referenceTree.clone_linked();
    }

    /**
     * @since 1.0.0
     */
    public void print() {
        editableTree.print();
        System.out.println();
    }

    /**
     * @since 1.0.0
     * @return the actual state of the tree that are going to be translated
     */
    @Override
    public String toString() {
        return editableTree.toString();
    }

    /**
     * return n \t in a string
     *
     * @param n
     * @return
     */
    public String add_tab(int n) {
        String a = "";
        for (int i = 0; i < n; i++) {
            a += "\t";
        }
        return a;
    }

    /**
     * Transpile module_def production
     *
     * @since 1.0.0
     * @param module_def
     */
    public void module_def_transpile(InternalNode module_def) {
        module_def.children.get(0).description = "module";
        module_def.children.get(1).description = " ";

        // delete first ; and put (
        module_def.children.get(3).description = "(\n";
        module_def.children.get(5).description = ",\n";
        module_def.children.get(7).description = "\n);\n";

        module_def.getChildrenByDescription("END_MODULE").description = "endmodule\n";

        InternalNodeLinked input_def = (InternalNodeLinked) module_def.getChildrenByDescription("input_def");
        input_def_transpile(input_def, add_tab(1) + "input logic", ",\n");

        InternalNodeLinked output_def = (InternalNodeLinked) module_def.getChildrenByDescription("output_def");
        input_def_transpile(output_def, add_tab(1) + "output logic", ",\n");

        InternalNodeLinked body_def = (InternalNodeLinked) module_def.getChildrenByDescription("body_def");

        body_def_transpile(body_def, output_def);
        module_def.children.get(10).description = "\n";

    }

    public void body_def_transpile(InternalNode body_def, InternalNodeLinked output_def) {
        InternalNodeLinked sequence_def = (InternalNodeLinked) body_def.getChildrenByDescription("sequence_def");
        sequence_def_transpile(
                sequence_def,
                (InternalNodeLinked) body_def.getChildrenByDescription("memory_def"),
                output_def.reference_tree.clone(),
                (InternalNodeLinked) body_def.getChildrenByDescription("control_reset_def")
        );

        body_def.children.get(3).description = "\n";
        body_def.children.get(1).description = ";\n";

        // TEMP: remove if you use control reset
        body_def.children.remove(5);
        body_def.children.remove(4);
    }

    /**
     * Transpile program production this method have recursion on program on the
     * last position, so you can call build multiples modules at once input
     *
     * @see
     * #input_def_transpile(com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.tree.InternalNode,
     * java.lang.String, java.lang.String)
     * @see
     * #module_def_transpile(com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.tree.InternalNode)
     * @see
     * #sequence_def_transpile(com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.tree.InternalNode,
     * com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.tree.InternalNodeLinked,
     * com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.tree.InternalNode)
     *
     * @since 1.0.0
     * @param program_def
     */
    public void program_transpile(InternalNode program_def) {
        InternalNodeLinked module_def = (InternalNodeLinked) program_def.getChildrenByDescription("module_def");
        module_def_transpile(module_def);

        if (program_def.children.size() == 2) {
            var next_program_def = (InternalNode) program_def.children.get(1);
            if (next_program_def.children.size() != 0) {
                program_transpile(next_program_def);
            }

        }
    }

    /**
     * Transpile input_def production
     *
     * @since 1.0.0
     * @see
     * #input_def_transpile(com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.tree.InternalNode,
     * com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.tree.InternalNode,
     * java.lang.String, java.lang.String)
     * @param input_def
     * @param tag_of_elements
     * @param input_divider
     */
    public void input_def_transpile(
            InternalNode input_def,
            String tag_of_elements,
            String input_divider) {

        input_def.children.remove(1);
        input_def.children.add(1, new Leaf(" "));

        input_def.children.get(0).description = tag_of_elements;

        input_def_transpile(input_def, ((InternalNode) input_def.children.get(2)), tag_of_elements, input_divider);

    }

    /**
     * <pre>
     *   //            i
     *   //         input_def             input_def
     *   //                |                |    |
     *
     *   //                input_list       il , s or leaf
     *   //                | |  |      -->  |    |
     *   //                | |  |           |    |   f,s are leaf or trees
     *   //                f ,  s           f
     * </pre>
     *
     * @see
     * #add_input(com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.tree.InternalNode,
     * com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.tree.InternalNode,
     * java.lang.String, java.lang.String)
     * @param input_def
     * @param list_node
     * @param tag_of_elements
     * @param input_divider
     */
    public void input_def_transpile(
            InternalNode input_def,
            InternalNode list_node,
            String tag_of_elements,
            String input_divider
    ) {
        if (list_node.children.size() == 3) {

            InternalNode input_list = (InternalNode) list_node.getChildrenByDescription("input_list");
            InternalNode variable_def = (InternalNode) list_node.getChildrenByDescription("variable_def");

            add_input(input_def, variable_def, tag_of_elements, input_divider);

            input_def.children.add(input_list);

            list_node.children.remove(input_list);
            list_node.children.remove(1); // remove ,

            input_def_transpile(input_def, input_list, tag_of_elements, input_divider);

        } else {
            InternalNode variable_def = (InternalNode) list_node.getChildrenByDescription("variable_def");
            if (variable_def.children.size() == 4) {
                array_transpile(variable_def);
            }
        }
    }

    /**
     * Append an input in that way
     * <pre>
     * input_def
     * | ...
     * |__input_divider ( example ; )
     * |__tag_of_elements ( example input logic )
     * |__" "
     * |__list_def production
     *    |__ ...
     *    ... get first variable_def and if it is an array,
     *          transpile array because arrays have different syntax
     *
     * </pre>
     *
     * @since 1.0.0
     * @see
     * #array_transpile(com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.tree.InternalNode)
     * @param input_def
     * @param list_def
     * @param tag_of_elements
     * @param input_divider
     */
    public void add_input(InternalNode input_def, InternalNode variable_def,
            String tag_of_elements, String input_divider) {
        input_def.children.add(new Leaf(input_divider));

        input_def.children.add(new Leaf(tag_of_elements));
        input_def.children.add(new Leaf(" "));

        if (variable_def.children.size() == 4) {
            array_transpile(variable_def);
        }
    }

    /**
     * Transpile from "A[2]" to "[1:0] A" modifying nodes order
     *
     * @since 1.0.0
     * @param variable_def
     */
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

    /**
     * Create type_def given a node and a number of steps
     *
     * @param type_def_state
     * @param n_steps
     */
    public void create_type_def_state(
            InternalNode type_def_state,
            int n_steps
    ) {
        if (n_steps > 0) {
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
            for (int i = 0; i < n_steps - 1; i++) {
                type_def_state.children.add(new Leaf(
                        add_tab(2) + "S" + i + " = " + n_steps + "'b" + str_binary + ",\n"
                ));
                str_binary = "0" + str_binary.substring(0, n_steps - 1);
            }

            type_def_state.children.add(new Leaf(
                    add_tab(2) + "S" + (n_steps - 1) + " = " + n_steps + "'b" + str_binary + "\n"
            ));

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
    }

    /**
     * <pre>
     * Creates always_ff for state transitions and memory changes
     *
     * 1. for in all variable_def inside memory_def and initialize it depending
     *  of if  it is an array or a simple bit variable
     *
     * 2. add initial state hardcoded and initialize else if reset is on =>
     * FSM working, if there is a posedge clk, update to the next state and
     * update all variables
     *
     * </pre>
     *
     * @param sequence_def
     * @param memory_def
     * @param n_steps
     */
    public void create_always_ff(InternalNode sequence_def, InternalNode memory_def, int n_steps) {

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
        if (n_steps > 0) {
            sequence_def.children.add(new Leaf(
                    add_tab(3) + "state <= S0;\n"
            ));
        }

        sequence_def.children.add(new Leaf(
                add_tab(2) + "end else begin\n"
        ));

        for (InternalNode node : memory_def.getAllDescendencyByDescription("variable_def")) {
            String var_name = node.children.get(0).description;
            sequence_def.children.add(new Leaf(
                    add_tab(3) + var_name + " <= next_" + var_name + ";\n"
            ));
        }

        if (n_steps > 0) {
            sequence_def.children.add(new Leaf(
                    add_tab(3) + "state <= next_state;\n"
            ));
        }

        sequence_def.children.add(new Leaf(
                add_tab(2) + "end\n"
        ));

        sequence_def.children.add(new Leaf(
                add_tab(1) + "end\n"
        ));

    }

    /**
     * <pre>
     *  1. declare memory via input_def_transpile all memory "variable" list
     *  2. create new memory_def for next_"variable"
     *
     * </pre>
     *
     * @see
     * #input_def_transpile(com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.tree.InternalNode,
     * java.lang.String, java.lang.String)
     * @param sequence_def
     * @param memory_def_without_next_prefix
     */
    public void create_memory_declaration_and_declare_their_next_value(InternalNode sequence_def, InternalNode memory_def_used_here_to_add_next_prefix) {
        // get memory_def from reference and add it to the sequence 
        // as memory definition of next, needed because i made it inspirated by
        // Mealy 
        // we add a memory def with some modifications

        InternalNode memory_def_without_next_prefix = memory_def_used_here_to_add_next_prefix.clone();

        input_def_transpile(memory_def_without_next_prefix, add_tab(1) + "logic", ";\n");

        sequence_def.children.add(memory_def_without_next_prefix);

        for (InternalNode node : memory_def_used_here_to_add_next_prefix.getAllDescendencyByDescription("variable_def")) {
            Leaf leaf = (Leaf) node.children.get(0);
            leaf.description = "next_" + leaf.description;
        }

        input_def_transpile(memory_def_used_here_to_add_next_prefix, add_tab(1) + "logic", ";\n");

        sequence_def.children.add(new Leaf(";\n"));

    }

    /**
     * <pre>
     * 1. obtain info for sequenc
     * 2. get a reference tree
     * 3. create a type_def_state InternalNode
     * 4. clear seuence_def
     * 5. call all child node methods with all the info they need
     * 6. add end node
     * </pre>
     *
     * @see
     * #create_memory_declaration_and_declare_their_next_value(com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.tree.InternalNode,
     * com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.tree.InternalNode)
     *
     * @see
     * #create_type_def_state(com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.tree.InternalNode,
     * int)
     *
     * @see
     * #create_always_ff(com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.tree.InternalNode,
     * com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.tree.InternalNode)
     *
     * @see
     * #create_always_comb(com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.tree.InternalNode,
     * com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.tree.InternalNode,
     * com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.tree.InternalNode,
     * com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.tree.InternalNode)
     *
     * @param sequence_def
     * @param memory_def
     * @param output_ref
     */
    public void sequence_def_transpile(
            InternalNode sequence_def, InternalNodeLinked memory_def,
            InternalNode output_ref, InternalNodeLinked control_reset_def) {

        InternalNode sequence_ref = ((InternalNodeLinked) sequence_def).reference_tree;

        int n_steps = Integer.parseInt(sequence_ref.children.get(2).description);

        InternalNode type_def_state = new InternalNode("type_def_state");

        sequence_def.children.clear();

        create_memory_declaration_and_declare_their_next_value(sequence_def, memory_def);
        create_type_def_state(type_def_state, n_steps);
        sequence_def.children.add(type_def_state);
        create_always_ff(sequence_def, memory_def.reference_tree, n_steps);
        create_always_comb(sequence_def,
                memory_def.reference_tree,
                output_ref.clone(),
                sequence_ref.clone(), n_steps, control_reset_def);

        sequence_def.children.add(new Leaf(add_tab(1) + "end\n"));

    }

    /**
     * Main method of the TreeExport, when you call it, it translates all the
     * code like a Template Method but using recursion in
     *
     * @see
     * #program_transpile(com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.tree.InternalNode)
     * @since 1.0.1
     */
    public void build() {
        program_transpile(editableTree);
    }

    /**
     * <pre>
     * 1. define next_state after of seeing a change in clk next_state = state;
     * 2. for all variable_def in memory_def and update it all
     * 3. for all variable_def in output_def and change it to 000. in binary
     *      format
     * 4. get first steps_def on sequence_ref and add to the output and
     *      initailize step_ref
     * 5. (for) get all assign_memory of steps_ref and make it be next_VAR,
     *      the output are going to be added without problems
     * 6. (for) optional step_def, go over all step_def in the tree and add all
     *      memory updates, change from  {@code <}= to = to follow always_comb
     *      syntax and if it is conditional assignation, it are going to be
     *      deleted and transpiled to an if
     * 7. create transition (for steps_def) , get conditions and goto from
     *       step_transition, i reset step_transition, add
     *              if(cond1) begin
     *                  state=S*;
     *              end else if(cond2) begin
     *                  state=S*
     *                  ...
     *              end
     *
     * </pre>
     *
     * @param sequence_def
     * @param memory_ref
     * @param output_ref
     * @param sequence_ref
     */
    public void create_always_comb(
            InternalNode sequence_def, InternalNode memory_ref,
            InternalNode output_ref, InternalNode sequence_ref, int n_steps,
            InternalNodeLinked control_reset_def) {

        sequence_def.children.add(new Leaf(add_tab(1) + "always_comb begin \n"));

        if (n_steps > 0) {
            // define next value as actual value all variable_def on memory
            sequence_def.children.add(new Leaf(
                    add_tab(2) + "next_state = state;\n"
            ));
        }

        for (InternalNode node : memory_ref.getAllDescendencyByDescription("variable_def")) {
            String var_name = node.children.get(0).description;
            sequence_def.children.add(new Leaf(
                    add_tab(2) + "next_" + var_name + " = " + var_name + ";\n"
            ));
        }
        ArrayList<String> control_variables_def = new ArrayList();

        // set default output and memory assignation on control reset and save 
        // cache of control_variables to avoid variable replicant
        for (InternalNode step_def : control_reset_def.getAllDescendencyByDescription("step_def")) {
            InternalNode variable_def = (InternalNode) step_def.children.get(0);
            
            String var_name = variable_def.children.get(0).description;
            
            sequence_def.children.add(new Leaf(
                    add_tab(2) + var_name + "="
            ));
            sequence_def.children.add(variable_def.children.get(2));
            
            sequence_def.children.add(new Leaf(";\n"));
            // save it because i use it on the next output initialization for
            control_variables_def.add(var_name);

        }
        
        // set default output and memory that is not on control reset to 0
        for (InternalNode node : output_ref.getAllDescendencyByDescription("variable_def")) {
            String aux_str_binary = "";
            String var_name = node.children.get(0).description;
            if (!control_variables_def.contains(var_name)) {
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
        }
        
        if (n_steps > 0) {
            InternalNode steps_ref = (InternalNode) sequence_ref.getDescendencyByDescription("steps_def");
            // state changes
            sequence_def.children.add(new Leaf(add_tab(2) + "case(state)\n"));

            sequence_def.children.add(steps_ref);

            for (Node child : steps_ref.getAllDescendencyByDescription("assign_memory")) {
                Leaf memory_affected = (Leaf) ((InternalNode) child).children.get(0);
                memory_affected.description = "next_" + memory_affected.description;
            }

            // optional step_def
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

                    step_def.children.add(2, new Leaf("\n" + add_tab(4) + "end\n"));
                    step_def.children.addFirst(new Leaf(") begin\n"));
                    step_def.children.addFirst(condition);

                    step_def.children.addFirst(new Leaf(add_tab(4) + "if("));
                }

                assign.children.get(1).description = "=";
                step_def.children.get(1).description = ";\n";

            }

            // transitions
            for (Node steps_def_node : sequence_def.getAllDescendencyByDescription("steps_def")) {
                InternalNode steps_def = (InternalNode) steps_def_node;

                steps_def.children.get(1).description = add_tab(3) + "S";
                steps_def.children.get(3).description = ":";
                steps_def.children.get(4).description = " begin\n";

                // i use this method because steps can be unexistent
                steps_def.getChildrenByDescription(";").description = add_tab(3) + "end\n";
                steps_def.children.remove(0);

                InternalNode step_transition = (InternalNode) steps_def.getChildrenByDescription("step_transition");

                ArrayList<InternalNode> conditions = step_transition.getAllDescendencyByDescription("conditions");
                ArrayList<InternalNode> goto_def = step_transition.getAllDescendencyByDescription("goto");

                // we have all info so i clear the step transition info
                step_transition.children.clear();

                step_transition.children.add(new Leaf(add_tab(4) + "if ("));

                step_transition.children.add(conditions.get(0).getChildrenByDescription("expr").clone());
                step_transition.children.add(new Leaf(") begin next_state = S" + ((InternalNode) goto_def.get(0)).children.get(0).description + ";\n"));

                for (int i = 1; i < conditions.size(); i++) {
                    step_transition.children.add(new Leaf(add_tab(4) + "end else if ("));
                    step_transition.children.add(conditions.get(i).getChildrenByDescription("expr").clone());
                    step_transition.children.add(new Leaf(") begin next_state = S" + ((InternalNode) goto_def.get(i)).children.get(0).description + ";\n"));

                }
                step_transition.children.add(new Leaf(add_tab(4) + "end\n"));

            }

            sequence_def.children.add(new Leaf(add_tab(2) + "endcase\n"));
        }
    }
}
