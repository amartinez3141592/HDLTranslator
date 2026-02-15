/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.hdltranspiler.tree;

/**
 *
 * @author Alexis Martinez
 */
public class InternalNodeLinked extends InternalNode {
    public InternalNode linked_to;
    
    public InternalNodeLinked(
            String description,
            InternalNode parent, 
            InternalNode linked_to
    ) {
        super(description, parent);
        this.linked_to = linked_to;
    }

}
