/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.alexis.martinez.trabajo.hdltranspiler.ui.input_state;

/**
 *
 * @author Alexis Martinez
 */
public class None implements InputState {

    @Override
    public InputState close() {
        return new None();
    }

    @Override
    public InputState new_file() {
        return new FileExists();

    }

    @Override
    public InputState save() {
        return new FileExists();

    }

}
