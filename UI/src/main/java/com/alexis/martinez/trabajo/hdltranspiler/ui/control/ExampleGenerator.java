/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.alexis.martinez.trabajo.hdltranspiler.ui.control;

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

    private final String[] files = {
        "RTL_X.hdl",
        "RTL_comparador.hdl",
        "RTL_empty_sequence_and_control.hdl",
        "RTL_serializer.hdl",
        "RTL_tick_led.hdl",
    };
    
    public void load() {
    
    }
    
    public void next() {
        if (i +1 >= files.length)
            i= 0;
        else
            i++;
    }
    
    public String getContent() {
        InputStream is = getClass().getResourceAsStream("/examples/" + files[i]);
        try {
            String example_content = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            return example_content;

        } catch (IOException ex) {
            Logger.getLogger(ExampleGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
}
