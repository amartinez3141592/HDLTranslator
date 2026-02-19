/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.tree;

/**
 *
 * @author Alexis Martinez
 */
public class InternalNodeLinked extends InternalNode {
    public InternalNode reference_tree;
    
    public InternalNodeLinked(
            String description,
            InternalNode reference_tree
    ) {
        super(description);
        this.reference_tree = reference_tree;
    }

}
