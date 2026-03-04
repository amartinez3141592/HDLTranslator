/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.alexis.martinez.trabajo.hdltranspiler.ui.file;

import java.util.ArrayList;

/**
 *
 * @author Alexis Martinez
 */
public class History {

    private ArrayList<String> history = new ArrayList<String>();
    private int index;
    private static final int TOP_OF_LIST = 7;

    public History() {
        reset();
    }

    public void reset() {
        this.history.clear();
        this.index = -1;
    }

    public void add(String value) {
        if (history.size() > TOP_OF_LIST) {
            this.history.removeFirst();
        }

        this.history.add(value);
        if (TOP_OF_LIST > index + 1) {
            this.index++;
        }
    }

    public String get() {
        if (this.history.size() > 0) {
            return this.history.get(this.index);
        } else {
            return null;
        }
    }

    public void undo() {
        if (this.index - 1 >= 0) {
            this.index--;
        }
    }

    public void redo() {
        if (this.index + 1 < this.history.size()) {
            this.index++;
        }
    }

    public int size() {
        return history.size();
    }
}
