/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.hdl_to_sv.control;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alexis Martinez
 */
public class ExampleGenerator {

    private int i = 0;
    private String[] files = {"RTL_tick_led_ok.hdl", "RTL_comparador.hdl"};
    
    public ExampleGenerator() {

    }
    public String getFile() {
        return files[i];
    }
    /**
     * Move the index of the example to the next element
     */
    public void next() {
        if (i + 1 >= files.length) {
            i = 0;
        } else {
            i++;
        }
    }

    /**
     *
     * @param file 
     * @return content of the file in (test or main)/resource/examples/here
     */
    public String getContentByFilename(String file) {
        InputStream is = ExampleGenerator.class.getResourceAsStream("/" + file);
        try {
            String example_content = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            return example_content;

        } catch (IOException ex) {
            Logger.getLogger(ExampleGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

}
