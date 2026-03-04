/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.sv_to_v.operations;

import com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.sv.SystemVerilogBaseVisitor;
import com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.sv.SystemVerilogParser;
import com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.sv.SystemVerilogVisitor;

import java.util.ArrayList;
import org.antlr.v4.runtime.Parser;

import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 *
 * @author Alexis Martinez
 */
/*
All methods can not finish with \n the parent determines \n
 */
public class Visitor extends SystemVerilogBaseVisitor<String> {

    private final TreeHelper helper;
    private String selector_data;
    private Parser parser;
    private final ArrayList<String> inputs;

    public Visitor(Parser parser, TreeHelper helper) {
        this.helper = helper;
        this.parser = parser;
        this.inputs = new ArrayList<String>();
    }

    @Override
    public String visitProgram(SystemVerilogParser.ProgramContext program_ctx) {
        inputs.clear();
        if (program_ctx.getChildCount() == 3) {
            return visitModule_def((SystemVerilogParser.Module_defContext) program_ctx.getChild(0))
                    + visitProgram((SystemVerilogParser.ProgramContext) program_ctx.getChild(1));
        } else {
            return visitModule_def((SystemVerilogParser.Module_defContext) program_ctx.getChild(0));
        }
    }

    @Override
    public String visitModule_def(SystemVerilogParser.Module_defContext module_def) {
        String out = "";
        out += this.visitTerminal((TerminalNode) module_def.getChild(0)) + " ";
        out += this.visitTerminal((TerminalNode) module_def.getChild(1));
        out += this.visitTerminal((TerminalNode) module_def.getChild(2)) + "\n\t";
        var ports_def = (SystemVerilogParser.PortsContext) module_def.getChild(3);
        out += this.visitPorts(ports_def).replace(",", ",\n\t");

        for (int i = 0; i < ports_def.getChildCount(); i++) {
            var port_def = ports_def.getChild(i);
            if (port_def instanceof SystemVerilogParser.PortContext) {
                if (helper.get_token((TerminalNode) port_def.getChild(0)).equals("input")) {
                    // get the last item of port that are the ID and save it
                    inputs.add(helper.get_token((TerminalNode) port_def.getChild(port_def.getChildCount() - 1)));
                }
            }
        }

        out += "\n" + this.visitTerminal((TerminalNode) module_def.getChild(4));
        out += this.visitTerminal((TerminalNode) module_def.getChild(5)) + "\n\t";

        out += this.visitModule_body((SystemVerilogParser.Module_bodyContext) module_def.getChild(6)).replace("\n", "\n\t");

        out += "\n" + this.visitTerminal((TerminalNode) module_def.getChild(7));
        return out;
    }

    @Override
    public String visitPorts(SystemVerilogParser.PortsContext ports) {
        String out = "";
        for (int i = 0; i < ports.getChildCount(); i += 1) {
            var child = ports.getChild(i);

            if (child instanceof SystemVerilogParser.PortContext) {
                out += visitPort((SystemVerilogParser.PortContext) child);
            } else {
                out += this.visitTerminal((TerminalNode) child);
            }

        }
        return out;
    }

    @Override
    public String visitPort(SystemVerilogParser.PortContext port) {
        String o = "";
        o += visitTerminal((TerminalNode) port.getChild(0));
        o += " wire ";
        if (port.getChildCount() == 3) {
            o += visitTerminal((TerminalNode) port.getChild(2));

        } else if (port.getChildCount() == 8) {
            o += visitTerminal((TerminalNode) port.getChild(2));
            o += visitTerminal((TerminalNode) port.getChild(3));
            o += visitTerminal((TerminalNode) port.getChild(4));
            o += visitTerminal((TerminalNode) port.getChild(5));
            o += visitTerminal((TerminalNode) port.getChild(6));
            o += " ";
            o += visitTerminal((TerminalNode) port.getChild(7));

        }

        return o;
    }

    @Override
    public String visitModule_body(SystemVerilogParser.Module_bodyContext module_body) {
        String o = "";
        for (int i = 0; i < module_body.getChildCount() - 1; i++) {
            o += this.visitModule_block((SystemVerilogParser.Module_blockContext) module_body.getChild(i));
            o += "\n";
        }
        o += this.visitModule_block((SystemVerilogParser.Module_blockContext) module_body.getChild(module_body.getChildCount() - 1));

        return o;
    }

    @Override
    public String visitModule_block(SystemVerilogParser.Module_blockContext module_block) {
        String o = "";
        TerminalNode t = (TerminalNode) module_block.getChild(0);
        if (helper.get_token(t).equals("logic")) {
            o += "reg ";
            for (int i = 1; i < module_block.getChildCount() - 2; i++) {
                o += visitTerminal((TerminalNode) module_block.getChild(i));
            }
            if (module_block.getChildCount() > 3) {
                o += " ";
            }
            o += visitTerminal((TerminalNode) module_block.getChild(module_block.getChildCount() - 2));
            o += visitTerminal((TerminalNode) module_block.getChild(module_block.getChildCount() - 1));
        } else if (helper.get_token(t).equals("always_ff")) {
            o += "always @";

            o += this.visitSignal_trans((SystemVerilogParser.Signal_transContext) module_block.getChild(2));
            o += this.visitTerminal((TerminalNode) module_block.getChild(3)) + "\n\t";
            for (int i = 4; i < module_block.getChildCount() - 1; i++) {
                o += this.visitBlock_body((SystemVerilogParser.Block_bodyContext) module_block.getChild(i)).replace("\n", "\n\t");
                o += "\n";
            }
            o += this.visitTerminal((TerminalNode) module_block.getChild(module_block.getChildCount() - 1));

        } else if (helper.get_token(t).equals("always_comb")) {
            var aux_inp = (ArrayList<String>) inputs.clone();
            o += "always @(";

            aux_inp.remove("VCC");
            aux_inp.remove("GND");
            aux_inp.remove("reset");
            aux_inp.remove("clk");

            o += aux_inp.get(0);
            aux_inp.remove(0);

            for (String input : aux_inp) {
                o += " or " + input;
            }

            o += ") ";
            o += "begin\n";

            var case_def = module_block.getChild(module_block.getChildCount() - 2);
            if (case_def instanceof SystemVerilogParser.Case_defContext) {
                for (int i = 2; i < module_block.getChildCount() - 2; i++) {
                    o += "\t";
                    o += visitAsync_body((SystemVerilogParser.Async_bodyContext) module_block.getChild(i)).replace("\n", "\n\t");
                    o += "\n";
                }
                o += "\t";
                o += visitCase_def((SystemVerilogParser.Case_defContext) case_def).replace("\n", "\n\t");
                o += "\n";

            } else {
                for (int i = 2; i < module_block.getChildCount() - 1; i++) {
                    o += "\t";
                    o += visitAsync_body((SystemVerilogParser.Async_bodyContext) module_block.getChild(i)).replace("\n", "\n\t");
                    o += "\n";
                }
            }
            o += visitTerminal((TerminalNode) module_block.getChild(module_block.getChildCount() - 1));

        } else if (helper.get_token(t).equals("typedef")) {
            int len = module_block.getChildCount();
            o += "localparam\n\t";

            for (int i = 9; i < module_block.getChildCount() - 3; i++) {
                var child = module_block.getChild(i);
                if (child instanceof SystemVerilogParser.Enum_stateContext) {
                    o += this.visitEnum_state((SystemVerilogParser.Enum_stateContext) child);
                } else if (child instanceof TerminalNode) {
                    o += this.visitTerminal((TerminalNode) child) + "\n\t";
                }
            }
            o += this.visitTerminal((TerminalNode) module_block.getChild(module_block.getChildCount() - 1));

            // i put it as variable because this is going to be defined first
            // this should be passed to another class following Visitor pattern
            selector_data = "";
            selector_data += helper.get_token((TerminalNode) module_block.getChild(3));
            selector_data += helper.get_token((TerminalNode) module_block.getChild(4));
            selector_data += helper.get_token((TerminalNode) module_block.getChild(5));
            selector_data += helper.get_token((TerminalNode) module_block.getChild(6));
            selector_data += helper.get_token((TerminalNode) module_block.getChild(7));
        } else {
            // ID ID SEMI case
            o += "reg " + selector_data + " ";
            o += helper.get_token((TerminalNode) module_block.getChild(1));
            o += ";";

        }

        return o;
    }

    @Override
    public String visitCase_def(SystemVerilogParser.Case_defContext case_def) {
        String o = "";
        o += visitTerminal((TerminalNode) case_def.getChild(0));
        // o += " ";

        o += visitTerminal((TerminalNode) case_def.getChild(1));
        o += visitTerminal((TerminalNode) case_def.getChild(2));
        o += visitTerminal((TerminalNode) case_def.getChild(3));
        o += "\n";

        for (int i = 4; i < case_def.getChildCount() - 1; i++) {
            o += "\t";
            o += visitSpecific_case_def(
                    (SystemVerilogParser.Specific_case_defContext) case_def.getChild(i)
            ).replace("\n", "\n\t");
            o += "\n";
        }

        o += visitTerminal((TerminalNode) case_def.getChild(case_def.getChildCount() - 1));
        return o;
    }

    @Override
    public String visitSpecific_case_def(SystemVerilogParser.Specific_case_defContext specific_case_def) {
        String o = "";
        o += visitTerminal((TerminalNode) specific_case_def.getChild(0));
//        o += " ";
        o += visitTerminal((TerminalNode) specific_case_def.getChild(1));
        o += " ";
        o += visitTerminal((TerminalNode) specific_case_def.getChild(2));
        o += "\n";
        for (int i = 3; i < specific_case_def.getChildCount() - 1; i++) {
            o += "\t";
            o += visitAsync_body(
                    (SystemVerilogParser.Async_bodyContext) specific_case_def.getChild(i)
            ).replace("\n", "\n\t");
            o += "\n";
        }
        o += visitTerminal((TerminalNode) specific_case_def.getChild(specific_case_def.getChildCount() - 1));

        return o;
    }

    @Override
    public String visitEnum_state(SystemVerilogParser.Enum_stateContext enum_state) {
        String o = "";
        o += visitTerminal((TerminalNode) enum_state.getChild(0)) + " ";
        o += visitTerminal((TerminalNode) enum_state.getChild(1)) + " ";
        o += visitTerminal((TerminalNode) enum_state.getChild(2));
        o += visitTerminal((TerminalNode) enum_state.getChild(3));
        o += visitTerminal((TerminalNode) enum_state.getChild(4));

        return o;
    }

    @Override
    public String visitSignal_trans(SystemVerilogParser.Signal_transContext ctx) {
        String o = "";
        o += visitTerminal((TerminalNode) ctx.getChild(0));

        for (int i = 1; i < ctx.getChildCount() - 2; i++) {
            o += visitTerminal((TerminalNode) ctx.getChild(i)) + " ";
        }
        o += visitTerminal((TerminalNode) ctx.getChild(ctx.getChildCount() - 2));
        o += visitTerminal((TerminalNode) ctx.getChild(ctx.getChildCount() - 1));
        o += " ";
        return o;
    }

    @Override
    public String visitBlock_body(SystemVerilogParser.Block_bodyContext ctx) {
        String o = "";
        if (ctx.getChild(0) instanceof SystemVerilogParser.If_block_defContext) {
            o += this.visitIf_block_def((SystemVerilogParser.If_block_defContext) ctx.getChild(0));
        } else if (ctx.getChild(0) instanceof TerminalNode) {
            o += this.visitTerminal((TerminalNode) ctx.getChild(0));
            o += " ";
            o += this.visitTerminal((TerminalNode) ctx.getChild(1));
            o += " ";
            o += this.visitExpr((SystemVerilogParser.ExprContext) ctx.getChild(2));
            o += this.visitTerminal((TerminalNode) ctx.getChild(3));
        } else {
            o += visitErrorNode((ErrorNode) ctx);
        }
        return o;
    }

    @Override
    public String visitIf_block_def(SystemVerilogParser.If_block_defContext if_block_def) {
        String a = """ 
                same structure
 
                  if_block_def
            : IF LPAREN expr RPAREN BEGIN (block_body)* END (if_block_def)?
            | ELSE if_block_def
            | ELSE BEGIN (block_body)* END
        """;
        String o = "";
        if (if_block_def.getChild(1) instanceof TerminalNode) {
            String token = helper.get_token((TerminalNode) if_block_def.getChild(1));
            if (token.equals("(")) {
                // ok
                o += visitTerminal((TerminalNode) if_block_def.getChild(0));
                o += " ";
                o += visitTerminal((TerminalNode) if_block_def.getChild(1));
                o += this.visitExpr((SystemVerilogParser.ExprContext) if_block_def.getChild(2));

                o += visitTerminal((TerminalNode) if_block_def.getChild(3));
                o += " ";

                o += visitTerminal((TerminalNode) if_block_def.getChild(4));
                o += "\n";

                if (if_block_def.children.getLast() instanceof SystemVerilogParser.If_block_defContext) {
                    //ok
                    for (int i = 5; i < if_block_def.children.size() - 2; i++) {
                        o += "\t";
                        o += this.visitBlock_body((SystemVerilogParser.Block_bodyContext) if_block_def.getChild(i));
                        o += "\n";
                    }

                    o += visitTerminal((TerminalNode) if_block_def.getChild(if_block_def.getChildCount() - 2));
                    o += " ";

                    o += this.visitIf_block_def((SystemVerilogParser.If_block_defContext) if_block_def.getChild(if_block_def.getChildCount() - 1));
                } else {
                    //
                    for (int i = 5; i < if_block_def.children.size() - 1; i++) {
                        o += "\t";

                        o += this.visitBlock_body((SystemVerilogParser.Block_bodyContext) if_block_def.getChild(i));
                        o += "\n";
                    }
                    o += visitTerminal((TerminalNode) if_block_def.getChild(if_block_def.getChildCount() - 1));

                }
            } else if (token.equals("begin")) {
                // ok
                o += visitTerminal((TerminalNode) if_block_def.getChild(0));
                o += " ";
                o += visitTerminal((TerminalNode) if_block_def.getChild(1));
                o += "\n";

                for (int i = 2; i < if_block_def.children.size() - 1; i++) {
                    o += "\t";
                    o += this.visitBlock_body((SystemVerilogParser.Block_bodyContext) if_block_def.getChild(i));
                    o += "\n";

                }

                o += visitTerminal((TerminalNode) if_block_def.getChild(if_block_def.getChildCount() - 1));

            }
        } else if (if_block_def.getChild(1) instanceof SystemVerilogParser.If_block_defContext) {
            o += visitTerminal((TerminalNode) if_block_def.getChild(0));
            o += " ";
            o += this.visitIf_block_def((SystemVerilogParser.If_block_defContext) if_block_def.getChild(1));
        }
        return o;
    }

    @Override
    public String visitAsync_body(SystemVerilogParser.Async_bodyContext async_body) {
        String o = "";
        if (async_body.getChild(0) instanceof SystemVerilogParser.If_async_defContext) {
            o += this.visitIf_async_def((SystemVerilogParser.If_async_defContext) async_body.getChild(0));
        } else if (async_body.getChild(0) instanceof TerminalNode) {
            o += this.visitTerminal((TerminalNode) async_body.getChild(0));
            o += " ";
            o += this.visitTerminal((TerminalNode) async_body.getChild(1));
            o += " ";
            o += this.visitExpr((SystemVerilogParser.ExprContext) async_body.getChild(2));
            o += this.visitTerminal((TerminalNode) async_body.getChild(3));
        } else {
            o += visitErrorNode((ErrorNode) async_body);
        }
        return o;
    }

    @Override
    public String visitIf_async_def(SystemVerilogParser.If_async_defContext ctx) {
        String a = """ 
                same structure
 
                  if_block_def
            : IF LPAREN expr RPAREN BEGIN (block_body)* END (if_block_def)?
            | ELSE if_block_def
            | ELSE BEGIN (block_body)* END
        """;
        String o = "";
        if (ctx.getChild(1) instanceof TerminalNode) {
            String token = helper.get_token((TerminalNode) ctx.getChild(1));
            if (token.equals("(")) {
                // ok
                o += visitTerminal((TerminalNode) ctx.getChild(0));
                o += " ";
                o += visitTerminal((TerminalNode) ctx.getChild(1));
                o += this.visitExpr((SystemVerilogParser.ExprContext) ctx.getChild(2));

                o += visitTerminal((TerminalNode) ctx.getChild(3));
                o += " ";

                o += visitTerminal((TerminalNode) ctx.getChild(4));
                o += "\n";

                if (ctx.children.getLast() instanceof SystemVerilogParser.If_async_defContext) {
                    //ok
                    for (int i = 5; i < ctx.children.size() - 2; i++) {
                        o += "\t";
                        o += this.visitAsync_body((SystemVerilogParser.Async_bodyContext) ctx.getChild(i));
                        o += "\n";
                    }

                    o += visitTerminal((TerminalNode) ctx.getChild(ctx.getChildCount() - 2));
                    o += " ";

                    o += this.visitIf_async_def((SystemVerilogParser.If_async_defContext) ctx.getChild(ctx.getChildCount() - 1));
                } else {
                    //
                    for (int i = 5; i < ctx.children.size() - 1; i++) {
                        o += "\t";

                        o += this.visitAsync_body((SystemVerilogParser.Async_bodyContext) ctx.getChild(i));
                        o += "\n";
                    }
                    o += visitTerminal((TerminalNode) ctx.getChild(ctx.getChildCount() - 1));

                }
            } else if (token.equals("begin")) {
                // ok
                o += visitTerminal((TerminalNode) ctx.getChild(0));
                o += " ";
                o += visitTerminal((TerminalNode) ctx.getChild(1));
                o += "\n";

                for (int i = 2; i < ctx.children.size() - 1; i++) {
                    o += "\t";
                    o += this.visitAsync_body((SystemVerilogParser.Async_bodyContext) ctx.getChild(i));
                    o += "\n";

                }

                o += visitTerminal((TerminalNode) ctx.getChild(ctx.getChildCount() - 1));

            }
        } else if (ctx.getChild(1) instanceof SystemVerilogParser.If_async_defContext) {
            o += visitTerminal((TerminalNode) ctx.getChild(0));
            o += " ";
            o += this.visitIf_async_def((SystemVerilogParser.If_async_defContext) ctx.getChild(1));
        }
        return o;
    }

    @Override
    public String visitExpr(SystemVerilogParser.ExprContext expr) {
        String o = "";
        for (int i = 0; i < expr.getChildCount(); i++) {
            var c = expr.getChild(i);
            if (c instanceof SystemVerilogParser.ExprContext) {
                o += visitExpr((SystemVerilogParser.ExprContext) c);
            } else if (c instanceof TerminalNode) {
                o += this.visitTerminal((TerminalNode) c);
            }
        }

        return o;
    }

    @Override
    public String visitTerminal(TerminalNode ctx) {
        return ctx.getSymbol().getText();
    }

    @Override
    public String visitErrorNode(ErrorNode node) {
        throw new UnsupportedOperationException("Error node.");
    }

    @Override
    public String visit(ParseTree tree) {
        return visitProgram((SystemVerilogParser.ProgramContext) tree);
    }

}
