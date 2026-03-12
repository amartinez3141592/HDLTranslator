
import com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.hdl_to_sv.HdlToSV;
import com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.sv_to_v.SystemVerilogToVerilog;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author Alexis Martinez
 */
public class InputOutputReferencedByFilesTest {

    static void testEqualResultHdlToSv(String input, String output) {

        try {
            String system_verilog_code = HdlToSV.transpile_hdl_to_sv(
                    input
            );
            String out = HdlToSV.toStringTreeForLookingForSyntaxErrors();

            Assertions.assertTrue(!out.contains("missing"));
            Assertions.assertTrue(!out.contains("mismatched"));

            String diff = StringUtils.difference(
                    system_verilog_code,
                    output
            );
            if (!diff.equals("")) {
                Logger.getLogger(InputOutputReferencedByFilesTest.class.getName()).log(Level.SEVERE, diff);
            }

            Assertions.assertTrue(system_verilog_code.equals(output));

        } catch (Exception ex) {
            Logger.getLogger(InputOutputReferencedByFilesTest.class.getName()).log(Level.SEVERE, null, ex);
            Assertions.assertTrue(false, "Exception thrown");
        }
    }

    static void testEqualResultSvToV(String input, String output) {

        try {
            String verilog_code = SystemVerilogToVerilog.transpile_sv_to_v(
                    input
            );
            String out = SystemVerilogToVerilog.toStringTreeForLookingForSyntaxErrors();

//            SystemVerilogToVerilog.visualize();
            Assertions.assertTrue(!out.contains("missing"));
            Assertions.assertTrue(!out.contains("mismatched"));

            String diff = StringUtils.difference(
                    verilog_code,
                    output
            );
            if (!diff.equals("")) {
                Logger.getLogger(InputOutputReferencedByFilesTest.class.getName()).log(Level.SEVERE, diff);
            }

            Assertions.assertTrue(verilog_code.equals(output));

        } catch (Exception ex) {
            Logger.getLogger(InputOutputReferencedByFilesTest.class.getName()).log(Level.SEVERE, null, ex);
            Assertions.assertTrue(false, "Exception thrown");
        }
    }
}
