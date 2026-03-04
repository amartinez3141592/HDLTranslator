/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.hdl_to_sv.tree;

/**
 *
 * @author Alexis Martinez
 */
public class LeafLinked extends Leaf {
    public Leaf reference_tree;
    public LeafLinked(String description, Leaf reference_tree) {
        super(description);
        this.reference_tree = reference_tree;
    }

}
