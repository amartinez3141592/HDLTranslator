/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

import com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.HdlTranspiler;
import com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.control.ExampleGenerator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Alexis Martinez
 */
public class TranspilerTest {

    private ExampleGenerator ex_gen = new ExampleGenerator();

    public TranspilerTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {

    }

    public int count_new_lines(String input) {
        int count_new_lines = 0;

        for (char c : input.toCharArray()) {
            if (c == '\n') {
                count_new_lines++;
            }
        }
        return count_new_lines;
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void test_good_syntax_RTL_tick_led() {
        try {

            String system_verilog_code = HdlTranspiler.transpile(
                    ex_gen.getContentByFilename("RTL_tick_led_ok.hdl")
            );
            String out = HdlTranspiler.toStringTreeForLookingForSyntaxErrors();

            Assertions.assertTrue(count_new_lines(out) < 1);

            String diff = StringUtils.difference(
                    system_verilog_code,
                    ex_gen.getContentByFilename("RTL_tick_led_ok.sv")
            );

            Logger.getLogger(TranspilerTest.class.getName()).log(Level.SEVERE, diff);
            Assertions.assertTrue(system_verilog_code.equals(ex_gen.getContentByFilename("RTL_tick_led_ok.sv")));

        } catch (Exception ex) {
            Logger.getLogger(TranspilerTest.class.getName()).log(Level.SEVERE, null, ex);
            Assertions.assertTrue(false, "Exception thrown");
        }
    }

    @Test
    public void test_bad_syntax_RTL_tick_led_bad() {
        try {

            HdlTranspiler.transpile(
                    ex_gen.getContentByFilename("RTL_tick_led_zbad.hdl")
            );
            String out = HdlTranspiler.toStringTreeForLookingForSyntaxErrors();

            Assertions.assertTrue(out.contains("missing"));
        } catch (Exception ex) {
            Logger.getLogger(TranspilerTest.class.getName()).log(Level.SEVERE, null, ex);
            Assertions.assertTrue(false, "Exception thrown");
        }
    }

    @Test
    public void test_array_good_syntax_RTL_serializer() {
        try {
            String system_verilog_code = HdlTranspiler.transpile(
                    ex_gen.getContentByFilename("RTL_serializer_ok.hdl")
            );
            String out = HdlTranspiler.toStringTreeForLookingForSyntaxErrors();

            Assertions.assertTrue(!out.contains("missing"));

            String diff = StringUtils.difference(
                    system_verilog_code,
                    ex_gen.getContentByFilename("RTL_serializer_ok.sv")
            );
            Logger.getLogger(TranspilerTest.class.getName()).log(Level.SEVERE, diff);

            Assertions.assertTrue(system_verilog_code.equals(ex_gen.getContentByFilename("RTL_serializer_ok.sv")));

        } catch (Exception ex) {
            Logger.getLogger(TranspilerTest.class.getName()).log(Level.SEVERE, null, ex);
            Assertions.assertTrue(false, "Exception thrown");
        }
    }

    @Test
    public void test_empty_step_RTL_empty_step() {
        try {

            String system_verilog_code = HdlTranspiler.transpile(
                    ex_gen.getContentByFilename("RTL_empty_step_ok.hdl")
            );
            String out = HdlTranspiler.toStringTreeForLookingForSyntaxErrors();

            Assertions.assertTrue(!out.contains("missing"));

            String diff = StringUtils.difference(
                    system_verilog_code,
                    ex_gen.getContentByFilename("RTL_empty_step_ok.sv")
            );
            Logger.getLogger(TranspilerTest.class.getName()).log(Level.SEVERE, null, diff);
            Assertions.assertTrue(system_verilog_code.equals(ex_gen.getContentByFilename("RTL_empty_step_ok.sv")));

        } catch (Exception ex) {
            Logger.getLogger(TranspilerTest.class.getName()).log(Level.SEVERE, null, ex);
            Assertions.assertTrue(false, "Exception thrown");
        }
    }

    @Test
    public void test_empty_step_RTL_comparador() {
        try {

            String system_verilog_code = HdlTranspiler.transpile(
                    ex_gen.getContentByFilename("RTL_comparador.hdl")
            );
            String out = HdlTranspiler.toStringTreeForLookingForSyntaxErrors();

            Assertions.assertTrue(!out.contains("missing"));
            String diff = StringUtils.difference(
                    system_verilog_code,
                    ex_gen.getContentByFilename("RTL_comparador.sv")
            );

            Logger.getLogger(TranspilerTest.class.getName()).log(Level.SEVERE, diff);
            Assertions.assertTrue(system_verilog_code.equals(ex_gen.getContentByFilename("RTL_comparador.sv")));

        } catch (Exception ex) {
            Logger.getLogger(TranspilerTest.class.getName()).log(Level.SEVERE, null, ex);
            Assertions.assertTrue(false, "Exception thrown");
        }
    }

    @Test
    public void test_RTL_empty_sequence() {
        try {
            String system_verilog_code = HdlTranspiler.transpile(
                    ex_gen.getContentByFilename("RTL_empty_sequence_and_control.hdl")
            );
            String out = HdlTranspiler.toStringTreeForLookingForSyntaxErrors();

            Assertions.assertTrue(!out.contains("missing"));

            String diff = StringUtils.difference(
                    system_verilog_code,
                    ex_gen.getContentByFilename("RTL_empty_sequence_and_control.sv")
            );
            Logger.getLogger(TranspilerTest.class.getName()).log(Level.SEVERE, diff);

            Assertions.assertTrue(system_verilog_code.equals(ex_gen.getContentByFilename("RTL_empty_sequence_and_control.sv")));

        } catch (Exception ex) {
            Logger.getLogger(TranspilerTest.class.getName()).log(Level.SEVERE, null, ex);
            Assertions.assertTrue(false, "Exception thrown");
        }
    }

    @Test
    public void test_RTL_X() {
        try {
            String system_verilog_code = HdlTranspiler.transpile(
                    ex_gen.getContentByFilename("RTL_X.hdl")
            );
            String out = HdlTranspiler.toStringTreeForLookingForSyntaxErrors();

            Assertions.assertTrue(!out.contains("missing"));

            String diff = StringUtils.difference(
                    system_verilog_code,
                    ex_gen.getContentByFilename("RTL_X.sv")
            );
            Logger.getLogger(TranspilerTest.class.getName()).log(Level.SEVERE, diff);

            Assertions.assertTrue(system_verilog_code.equals(ex_gen.getContentByFilename("RTL_X.sv")));

        } catch (Exception ex) {
            Logger.getLogger(TranspilerTest.class.getName()).log(Level.SEVERE, null, ex);
            Assertions.assertTrue(false, "Exception thrown");
        }
    }

}
