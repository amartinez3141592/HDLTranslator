/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

import com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.hdl_to_sv.control.ExampleGenerator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


/**
 *
 * @author Alexis Martinez
 */
public class SVToVTest {

    private ExampleGenerator ex_gen = new ExampleGenerator();

    public SVToVTest() {
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
    public void test_RTL_tick_led__ok_control() {
        InputOutputReferencedByFilesTest.testEqualResultSvToV(
                ex_gen.getContentByFilename("sv/RTL_tick_led_ok_control.sv"),
                ex_gen.getContentByFilename("v/RTL_tick_led_ok_control.v"));
    }
 
    @Test
    public void test_RTL_tick_led() {
        InputOutputReferencedByFilesTest.testEqualResultSvToV(
                ex_gen.getContentByFilename("sv/RTL_tick_led_ok.sv"),
                ex_gen.getContentByFilename("v/RTL_tick_led_ok.v"));
    }

    @Test
    public void test_RTL_serializer() {
        InputOutputReferencedByFilesTest.testEqualResultSvToV(
                ex_gen.getContentByFilename("sv/RTL_serializer_ok.sv"),
                ex_gen.getContentByFilename("v/RTL_serializer_ok.v"));
    }

    @Test
    public void test_RTL_empty_step() {
        InputOutputReferencedByFilesTest.testEqualResultSvToV(
                ex_gen.getContentByFilename("sv/RTL_empty_step_ok.sv"),
                ex_gen.getContentByFilename("v/RTL_empty_step_ok.v"));
    }

    @Test
    public void test_RTL_comparador() {
        InputOutputReferencedByFilesTest.testEqualResultSvToV(
                ex_gen.getContentByFilename("sv/RTL_comparador.sv"),
                ex_gen.getContentByFilename("v/RTL_comparador.v"));
    }

    @Test
    public void test_RTL_empty_sequence() {
        InputOutputReferencedByFilesTest.testEqualResultSvToV(
                ex_gen.getContentByFilename("sv/RTL_empty_sequence_and_control.sv"),
                ex_gen.getContentByFilename("v/RTL_empty_sequence_and_control.v"));
    }

    @Test
    public void test_RTL_X() {
        InputOutputReferencedByFilesTest.testEqualResultSvToV(
                ex_gen.getContentByFilename("sv/RTL_X.sv"),
                ex_gen.getContentByFilename("v/RTL_X.v"));
    }

}
