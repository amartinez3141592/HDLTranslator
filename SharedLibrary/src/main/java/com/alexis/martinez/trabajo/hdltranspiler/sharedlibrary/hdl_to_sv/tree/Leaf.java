/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.hdl_to_sv.tree;

/**
 *
 * @author Alexis Martinez
 */
public class Leaf extends Node {

    public Leaf(String description) {
        this.description = description;
    }

    @Override
    public Leaf clone() {
        return new Leaf(description);
    }

    @Override
    public Node clone_linked() {
        return new LeafLinked(description, this);
    }
    
    @Override
    public String toString() {
        return this.description.toString();
    }    
    @Override
    public String toStringWithSpaces() {
        return " " + this.description.toString();
    }
    @Override
    public void print() {
        System.out.print(description);
    }


}
