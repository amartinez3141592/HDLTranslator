/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package history;

import com.alexis.martinez.trabajo.hdltranspiler.ui.file.History;
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
public class HistoryTest {

    private History hist;

    public HistoryTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
        this.hist = new History();

    }

    @AfterEach
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    @Test
    public void test_dont_throw_error() {
        this.hist.get();
    }

    @Test
    public void test_get_last() {
        this.hist.add("1");
        this.hist.add("2");
        this.hist.add("3");
        this.hist.add("4");
        this.hist.add("5");
        Assertions.assertTrue(this.hist.get().equals("5"));

    }

    @Test
    public void test_undo_redo() {
        this.hist.add("1");
        this.hist.add("2");
        this.hist.add("3");
        this.hist.add("4");
        this.hist.add("5");
        this.hist.undo();
        this.hist.redo();

        Assertions.assertTrue(this.hist.get().equals("5"));

    }

    @Test
    public void test_undo() {
        this.hist.add("1");
        this.hist.add("2");
        this.hist.add("3");
        this.hist.add("4");
        this.hist.add("5");
        this.hist.undo();
        Assertions.assertTrue(this.hist.size() == 5);

        Assertions.assertTrue(this.hist.get().equals("4"));

    }

    @Test
    public void test_a_lot_of_undo() {
        this.hist.add("1");
        this.hist.add("2");
        this.hist.add("3");
        this.hist.undo();
        this.hist.undo();
        this.hist.undo();
        this.hist.undo();
        this.hist.undo();
        Assertions.assertTrue(this.hist.size() == 3);

        Assertions.assertTrue(this.hist.get().equals("1"));

    }

    @Test
    public void test_a_lot_of_redo() {
        this.hist.add("1");
        this.hist.add("2");
        this.hist.add("3");
        this.hist.add("4");
        this.hist.add("5");
        Assertions.assertTrue(this.hist.size() == 5);

        this.hist.undo();
        this.hist.undo();
        this.hist.undo();
        this.hist.undo();
        this.hist.undo();

        this.hist.redo();
        this.hist.redo();
        this.hist.redo();
        this.hist.redo();
        this.hist.redo();
        Assertions.assertTrue(this.hist.size() == 5);

        Assertions.assertTrue(this.hist.get().equals("5"));

        Assertions.assertTrue(this.hist.get().equals("5"));

    }

    @Test
    public void test_top() {
        this.hist.add("1");
        this.hist.add("2");
        this.hist.add("3");
        this.hist.add("4");
        this.hist.add("5");
        this.hist.add("6");
        this.hist.add("7");
        this.hist.add("8");
        this.hist.add("9");
        this.hist.add("10");
        this.hist.add("11");

    }

}
