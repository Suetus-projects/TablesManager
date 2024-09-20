/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Functional;

import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

/**
 *
 * @author dan
 */
public class Tabla {
    public String title;
    public Pane cells_pane;
    public Pane choose_color_pane;
    public FlowPane existing_colors_flowpane;
    public CellPane[][] cards_panes;
    public TextArea notes;
    
    public Tabla(String title) {
        this.title = title;
    }
}
