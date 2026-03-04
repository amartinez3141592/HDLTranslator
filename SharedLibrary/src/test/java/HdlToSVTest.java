/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

import com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.hdl_to_sv.HdlToSV;
import com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.hdl_to_sv.control.ExampleGenerator;
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
public class HdlToSVTest {

   private ExampleGenerator ex_gen = new ExampleGenerator();

    public HdlToSVTest() {
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

    @Test
    public void test_good_syntax_RTL_tick_led() {
        InputOutputReferencedByFilesTest.testEqualResultHdlToSv(
                ex_gen.getContentByFilename("hdl/RTL_tick_led_ok.hdl"),
                ex_gen.getContentByFilename("sv/RTL_tick_led_ok.sv"));
    }

    @Test
    public void test_bad_syntax_RTL_tick_led_bad() {
        try {

            HdlToSV.transpile_hdl_to_sv(
                    ex_gen.getContentByFilename("hdl/RTL_tick_led_zbad.hdl")
            );
            String out = HdlToSV.toStringTreeForLookingForSyntaxErrors();

            Assertions.assertTrue(out.contains("missing"));
        } catch (Exception ex) {
            Logger.getLogger(HdlToSVTest.class.getName()).log(Level.SEVERE, null, ex);
            Assertions.assertTrue(false, "Exception thrown");
        }
    }

    @Test
    public void test_array_good_syntax_RTL_serializer() {
        InputOutputReferencedByFilesTest.testEqualResultHdlToSv(
                ex_gen.getContentByFilename("hdl/RTL_serializer_ok.hdl"),
                ex_gen.getContentByFilename("sv/RTL_serializer_ok.sv"));
    }

    @Test
    public void test_RTL_empty_step() {
        InputOutputReferencedByFilesTest.testEqualResultHdlToSv(
                ex_gen.getContentByFilename("hdl/RTL_empty_step_ok.hdl"),
                ex_gen.getContentByFilename("sv/RTL_empty_step_ok.sv"));
    }

    @Test
    public void test_RTL_comparador() {
        InputOutputReferencedByFilesTest.testEqualResultHdlToSv(
                ex_gen.getContentByFilename("hdl/RTL_comparador.hdl"),
                ex_gen.getContentByFilename("sv/RTL_comparador.sv"));
    }

    @Test
    public void test_RTL_empty_sequence() {
        InputOutputReferencedByFilesTest.testEqualResultHdlToSv(
                ex_gen.getContentByFilename("hdl/RTL_empty_sequence_and_control.hdl"),
                ex_gen.getContentByFilename("sv/RTL_empty_sequence_and_control.sv"));
    }

    @Test
    public void test_RTL_X() {
        InputOutputReferencedByFilesTest.testEqualResultHdlToSv(
                ex_gen.getContentByFilename("hdl/RTL_X.hdl"),
                ex_gen.getContentByFilename("sv/RTL_X.sv"));
    }
}