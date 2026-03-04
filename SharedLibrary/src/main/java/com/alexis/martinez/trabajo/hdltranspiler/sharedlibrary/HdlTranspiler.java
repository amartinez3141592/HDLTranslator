/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary;

import com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.hdl_to_sv.HdlToSV;
import static com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.hdl_to_sv.HdlToSV.example;
import com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.hdl_to_sv.control.ExampleGenerator;

/**
 *
 * @author Alexis Martinez
 */
public class HdlTranspiler {


    
    public HdlTranspiler() {
        example = new ExampleGenerator();
    }

    public static void main(String[] args) throws Exception {
        String code;
        if (args.length == 0) {
            code = example.getContentByFilename(example.getFile());
            System.out.print(HdlToSV.transpile_hdl_to_sv(code));

        } else {
            code = args[0];
            System.out.print(HdlToSV.transpile_hdl_to_sv(code));
            // visualize();
            if (args.length == 2 && args[1].equals("log")) {
                System.out.println(HdlToSV.tree.toStringTree(HdlToSV.parser));
            }
        }

    }
}
