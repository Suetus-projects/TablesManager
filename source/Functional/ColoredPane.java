/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Functional;

import javafx.scene.layout.Pane;

/**
 *
 * @author dan
 */
public class ColoredPane extends Pane {
    public ColoredPane() {super();}
    
    private String color;
    
    public ColoredPane(String color) {
        this.color = color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public String getColor() {
        return color;
    }
    
}
