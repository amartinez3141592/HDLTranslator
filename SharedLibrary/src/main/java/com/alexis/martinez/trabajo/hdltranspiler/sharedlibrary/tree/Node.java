/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.alexis.martinez.trabajo.hdltranspiler.sharedlibrary.tree;

/**
 *
 * @author Alexis Martinez
 */
public abstract class Node {

    public String description;

    public abstract Node clone();
    public abstract Node clone_linked();
    public abstract void print();
    
}
