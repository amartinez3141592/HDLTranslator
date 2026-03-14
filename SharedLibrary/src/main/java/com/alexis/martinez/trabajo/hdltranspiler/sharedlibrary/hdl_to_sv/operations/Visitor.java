/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.hdl_to_sv.operations;

import com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.hdl.HDLParser;
import com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.hdl.HDLParser.Sequence_defContext;
import com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.hdl.HDLVisitor;
import com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.hdl_to_sv.tree.CustomTreeEquivalent;
import com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.hdl_to_sv.tree.InternalNode;
import com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.hdl_to_sv.tree.InternalNodeLinked;
import com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.hdl_to_sv.tree.Leaf;
import com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.hdl_to_sv.tree.Node;
import java.util.ArrayList;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.antlr.v4.runtime.Parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 *
 * @author Alexis Martinez
 */
public class Visitor implements HDLVisitor {

    /*
    public ArrayList<String> output = new ArrayList<>();
    public ArrayList<String> memory = new ArrayList<>();
    public ArrayList<String> input = new ArrayList<>();*/
    public ArrayList<String> memory_def_used_here_to_add_next_prefix = new ArrayList<>();
    public InternalNode memory_def_list;
    private final HDLParser parser;
    private InternalNode input_def_list;
    private InternalNode output_def_list;
    private InternalNode control_reset_def;
    private InternalNode sequence_def;

    public Visitor(HDLParser parser) {
        this.parser = parser;
    }

    /*
    program
    : module_def (program)? EOF
    ;
     */
    @Override
    public String visitProgram(HDLParser.ProgramContext program) {
        String o = "";
        o += visitModule_def((HDLParser.Module_defContext) program.getChild(0));
        o += "\n";
        if (program.getChildCount() == 3) {
            o += visitProgram((HDLParser.ProgramContext) program.getChild(1));
        }
        return o;

    }

    @Override
    public String visitModule_def(HDLParser.Module_defContext module_def) {
        String o = "";
        o += "module ";
        o += visitTerminal((TerminalNode) module_def.getChild(2));
        o += "(\n\t";
        o += this.visitInput_def(
                (HDLParser.Input_defContext) module_def.getChild(4)
        ).replace("\n", "\n\t");
        o += ",\n\t";
        o += this.visitOutput_def(
                (HDLParser.Output_defContext) module_def.getChild(6)
        ).replace("\n", "\n\t");
        o += "\n);\n";
        o += "\t";
        o += this.visitBody_def(
                (HDLParser.Body_defContext) module_def.getChild(8)).replace("\n", "\n\t");
        o += "\nendmodule";

        return o;
    }

    /*
    body_def
        : (memory_def SEMI)? sequence_def SEMI control_reset_def SEMI
        ;
     */
    @Override
    public String visitBody_def(HDLParser.Body_defContext body_def) {
        String o = "";
        if (body_def.getChild(0) instanceof HDLParser.Memory_defContext) {
            var mem_context = (HDLParser.Memory_defContext) body_def.getChild(0);

            try {
                this.memory_def_list = CustomTreeEquivalent.parse(mem_context, parser);
            } catch (Exception ex) {
                Logger.getLogger(Visitor.class.getName()).log(Level.SEVERE, null, ex);
            }
            o += this.visitMemory_def(mem_context);
            o += "\n";

            visitControl_reset_def((HDLParser.Control_reset_defContext) body_def.getChild(4));
            o += this.visitSequence_def(
                    (HDLParser.Sequence_defContext) body_def.getChild(2)
            );

        } else {
            visitControl_reset_def((HDLParser.Control_reset_defContext) body_def.getChild(2));
            o += this.visitSequence_def(
                    (HDLParser.Sequence_defContext) body_def.getChild(0)
            );
        }

        return o;
    }

    @Override
    public String visitControl_reset_def(HDLParser.Control_reset_defContext control_reset_def) {
        try {
            this.control_reset_def = CustomTreeEquivalent.parse(control_reset_def, parser);
        } catch (Exception ex) {
            Logger.getLogger(Visitor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    @Override
    public String visitVariable_def(HDLParser.Variable_defContext variable_def) {
        String o = "";
        if (variable_def.children.size() == 4) {
            String variable = this.visitTerminal((TerminalNode) variable_def.children.get(0));
            Integer size = Integer.parseInt(
                    this.visitTerminal((TerminalNode) variable_def.children.get(2))
            ) - 1;

            variable_def.children.clear();
            o += "[";
            o += size.toString();
            o += ":";
            o += "0";
            o += "]";
            o += " ";
            o += variable;
        } else {
            o += this.visitTerminal((TerminalNode) variable_def.getChild(0));
        }
        return o;
    }

    @Override
    public String visitInput_def(HDLParser.Input_defContext input_def) {
        try {
            this.input_def_list = CustomTreeEquivalent.parse(input_def.getChild(2), parser);
        } catch (Exception ex) {
            Logger.getLogger(Visitor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return input_def_transpile((ParserRuleContext) input_def.getChild(2), "input logic ", ",\n");
    }

    @Deprecated
    @Override
    public String visitInput_list(HDLParser.Input_listContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String visitOutput_def(HDLParser.Output_defContext output_def) {
        try {
            this.output_def_list = CustomTreeEquivalent.parse(output_def.getChild(2), parser);
        } catch (Exception ex) {
            Logger.getLogger(Visitor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return input_def_transpile((ParserRuleContext) output_def.getChild(2), "output logic ", ",\n");
    }

    @Override
    public String visitMemory_def(HDLParser.Memory_defContext memory_def) {
        try {
            this.memory_def_list = CustomTreeEquivalent.parse(memory_def.getChild(2), parser);
        } catch (Exception ex) {
            Logger.getLogger(Visitor.class.getName()).log(Level.SEVERE, null, ex);
        }

        String o = input_def_transpile(
                (ParserRuleContext) memory_def.getChild(2),
                "logic ",
                ";");
        o += ";";
        o += o
                .replace("] ", "] next_")
                .replace("c ", "c next_")// causes next_[
                .replace("next_[", "[");
        o = o.replace(";", ";\n");
        o = o.substring(0, o.length() - 1);
        return o;
    }

    @Override
    public String visitSequence_def(HDLParser.Sequence_defContext sequence_def) {
        String o = "";
        try {
            this.sequence_def = CustomTreeEquivalent.parse(sequence_def, parser);
        } catch (Exception ex) {
            Logger.getLogger(Visitor.class.getName()).log(Level.SEVERE, null, ex);
        }

        int n_steps = Integer.parseInt(
                visitTerminal((TerminalNode) sequence_def.getChild(2))
        );

        String aux = create_type_def_state(sequence_def, n_steps);

        if (!aux.equals("")) {
            o += aux;
            o += "\n";
        }

        o += create_always_ff(sequence_def, n_steps);
        o += "\n";

        o += create_always_comb(sequence_def, n_steps);

        return o;
    }

    @Override
    public String visitSteps_def(HDLParser.Steps_defContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String visitStep_transition(HDLParser.Step_transitionContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String visitStep_def(HDLParser.Step_defContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String visitAssign_output(HDLParser.Assign_outputContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String visitAssign_memory(HDLParser.Assign_memoryContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String visitConditions(HDLParser.ConditionsContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String visitGoto(HDLParser.GotoContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String visitExpr(HDLParser.ExprContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String visitConst_expr(HDLParser.Const_exprContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String visitCall_input_list(HDLParser.Call_input_listContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String visitModule_call(HDLParser.Module_callContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String visit(ParseTree tree) {
        return this.visitProgram((HDLParser.ProgramContext) tree);
    }

    @Override
    public String visitChildren(RuleNode node) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String visitTerminal(TerminalNode node) {
        return node.getSymbol().getText();
    }

    @Override
    public String visitErrorNode(ErrorNode node) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    /**
     * Transpile input_def production
     *
     * @since 1.0.0
     * @see
     * #input_def_transpile(com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.tree.InternalNode,
     * com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.tree.InternalNode,
     * java.lang.String, java.lang.String)
     * @param input_def_like
     * @param tag_of_elements
     * @param input_divider
     */
    public String input_def_transpile(
            ParserRuleContext list_node,
            String tag_of_elements,
            String separator
    ) {
        String o = "";
        o += tag_of_elements;
        o += this.visitVariable_def((HDLParser.Variable_defContext) list_node.getChild(0));
        if (list_node.children.size() == 3) {
            o += separator;
            o += input_def_transpile((ParserRuleContext) list_node.getChild(2), tag_of_elements, separator);
        }
        return o;
    }

    public String create_always_comb(HDLParser.Sequence_defContext sequence_def, int n_steps) {
        String out = "";
        out += "always_comb begin \n";
        
        ArrayList<String> control_variables_def = new ArrayList();

        // set default output and memory assignation on control reset and save 
        // cache of control_variables to avoid variable replicant
        for (InternalNode step_def : this.control_reset_def.getAllDescendencyByDescription("step_def")) {
            InternalNode variable_def = (InternalNode) step_def.children.get(0);

            String var_name = variable_def.children.get(0).description;
            if (variable_def.description.equals("assign_memory")) {
                out += "\tnext_" + var_name + "=";
            } else if (variable_def.description.equals("assign_output")) {
                out += "\t" + var_name + "=";
            }

            out += variable_def.children.get(2);

            out += ";\n";

            // save it because i use it on the next output initialization for
            control_variables_def.add(var_name);

        }

        // set default output and memory that is not on control reset to 0
        for (InternalNode node : this.output_def_list.getAllDescendencyByDescription("variable_def")) {
            String aux_str_binary = "";
            String var_name = node.children.get(0).description;
            if (!control_variables_def.contains(var_name)) {
                if (node.children.size() == 1) {
                    // variable
                    out += "\t" + var_name + " = 1'b0;\n";

                } else if (node.children.size() == 4) {
                    //array
                    int size_array = Integer.parseInt(node.children.get(2).description);
                    for (int i = 0; i < size_array; i++) {
                        aux_str_binary += "0";
                    }
                    out += "\t" + var_name + " = " + size_array + "'b" + aux_str_binary + ";\n";
                }
            }
        }

        if (n_steps > 0) {
            InternalNode steps_ref = (InternalNode) this.sequence_def.getDescendencyByDescription("steps_def");
            // state changes
            out += "\tcase(state)\n";

            for (Node child : steps_ref.getAllDescendencyByDescription("assign_memory")) {
                Leaf memory_affected = (Leaf) ((InternalNode) child).children.get(0);
                memory_affected.description = "next_" + memory_affected.description;
            }

            // optional step_def
            for (Node step_def_node : steps_ref.getAllDescendencyByDescription("step_def")) {
                InternalNode step_def = (InternalNode) step_def_node;
                InternalNode assign = (InternalNode) step_def.children.get(0);
                assign.children.get(0).description = "\t\t\t" + assign.children.get(0).description;

                // if it is conditionated assign to memory
                if (assign.description.equals("assign_memory") && assign.children.size() == 5) {
                    Node condition = assign.children.get(2);
                    // remove conditional specific syntax
                    assign.children.remove(2);
                    assign.children.remove(1);
                    assign.children.get(0).description = "\t" + assign.children.get(0).description;

                    step_def.children.add(2, new Leaf("\n\t\t\t" + "end\n"));
                    step_def.children.addFirst(new Leaf(") begin\n"));
                    step_def.children.addFirst(condition);

                    step_def.children.addFirst(new Leaf("\t\t\tif("));
                }

                assign.children.get(1).description = "=";
                step_def.children.get(1).description = ";\n";

            }

            // transitions
            for (Node steps_def_node : this.sequence_def.getAllDescendencyByDescription("steps_def")) {
                InternalNode steps_def = (InternalNode) steps_def_node;

                steps_def.children.get(1).description = "\t\tS";
                steps_def.children.get(3).description = ":";
                steps_def.children.get(4).description = " begin\n";

                // i use this method because steps can be unexistent
                steps_def.getChildrenByDescription(";").description = "\t\tend\n";
                steps_def.children.remove(0);

                InternalNode step_transition = (InternalNode) steps_def.getChildrenByDescription("step_transition");

                ArrayList<InternalNode> conditions = step_transition.getAllDescendencyByDescription("conditions");
                ArrayList<InternalNode> goto_def = step_transition.getAllDescendencyByDescription("goto");

                // we have all info so i clear the step transition info
                step_transition.children.clear();

                step_transition.children.add(new Leaf("\t\t\tif ("));

                step_transition.children.add(conditions.get(0).getChildrenByDescription("expr").clone());
                step_transition.children.add(new Leaf(") begin next_state = S" + ((InternalNode) goto_def.get(0)).children.get(0).description + ";\n"));

                for (int i = 1; i < conditions.size(); i++) {
                    step_transition.children.add(new Leaf("\t\t\tend else if ("));
                    step_transition.children.add(conditions.get(i).getChildrenByDescription("expr").clone());
                    step_transition.children.add(new Leaf(") begin next_state = S" + ((InternalNode) goto_def.get(i)).children.get(0).description + ";\n"));

                }
                step_transition.children.add(new Leaf("\t\t\tend\n"));

            }
            out += steps_ref.toString();

            out += "\tendcase\n";
        }
        out += "end";
        return out;

    }

    public String create_always_ff(HDLParser.Sequence_defContext sequence_def, int n_steps) {
        String o = "";

        o += "always_ff @( posedge clk or negedge reset ) begin\n";
        o += "\tif (!(reset)) begin\n";
        if (memory_def_list != null) {

            for (InternalNode node : memory_def_list.getAllDescendencyByDescription("variable_def")) {
                String aux_str_binary = "";
                String var_name = node.children.get(0).description;
                if (node.children.size() == 1) {
                    // variable
                    aux_str_binary = "0";
                    o += "\t\t" + var_name + " <= 1'b" + aux_str_binary + ";\n";

                } else if (node.children.size() == 4) {
                    //array
                    int size_array = Integer.parseInt(node.children.get(2).description);
                    for (int i = 0; i < size_array; i++) {
                        aux_str_binary += "0";
                    }
                    o += "\t\t" + var_name + " <= " + size_array + "'b" + aux_str_binary + ";\n";
                }
            }
        }
        if (n_steps > 0) {
            o += "\t\tstate <= S0;\n";
        }

        o += "\tend else begin\n";
        if (memory_def_list != null) {

            for (InternalNode node : memory_def_list.getAllDescendencyByDescription("variable_def")) {
                String var_name = node.children.get(0).description;
                o += "\t\t" + var_name + " <= next_" + var_name + ";\n";
            }
        }

        if (n_steps > 0) {
            o += "\t\tstate <= next_state;\n";
        }

        o += "\t" + "end\n";

        o += "end";
        return o;

    }

    public String create_type_def_state(
            HDLParser.Sequence_defContext sequence_def, int n_steps) {
        String o = "";
        if (n_steps > 0) {
            o += "typedef enum logic [" + (n_steps - 1) + ":0] {\n";

            String str_binary = "";

            for (int i = 0; i < (n_steps - 1); i++) {
                str_binary += "0";
            }

            // 0000000 size n_steps - 1
            str_binary = "1" + str_binary;

            // 10000000 size n_steps
            for (int i = 0; i < n_steps - 1; i++) {
                o += "\tS" + i + " = " + n_steps + "'b" + str_binary + ",\n";
                str_binary = "0" + str_binary.substring(0, n_steps - 1);
            }

            o += "\tS" + (n_steps - 1) + " = " + n_steps + "'b" + str_binary + "\n";

            o += "} state_t;\n";
            o += "state_t next_state;\n";

            o += "state_t state;";
        }
        return o;

    }

    @Override
    public Object visitAssignation_conditions(HDLParser.Assignation_conditionsContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
