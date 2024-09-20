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
public class CellPane extends Pane {
    public CellPane() {super();}
    
    private String name;
    private String[] colores;
    private String[] porcentajes_exp;
    private double[] porcentajes;
    
    public CellPane(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public void setColor(int index, String value) {
        colores[index] = value;
    }
    
    public int getNumeroElementos() {
        return colores.length;
    }
    
    public void setColoresYPorcentajes(String[] colores, String[] porcentajes_exp) {
        if (colores.length != porcentajes_exp.length)
            throw new RuntimeException("El largo de 'colores' y 'porcentajes' no coincide (" + colores.length + " y " + porcentajes.length + ")");
        
        var porcentajes = new double[porcentajes_exp.length];
        for (int i = 0; i < porcentajes.length; i++) {
            try {
                porcentajes[i] = interpretar(porcentajes_exp[i]);
            } catch (Exception e) {
                throw new RuntimeException("Error reading the value \"" + porcentajes_exp[i] + "\"");
            }
        }
        
        //Asegurar que suma de todos sea 1
        double suma = 0;
        for (double p : porcentajes) suma += p;
        if (Math.abs(suma - 1) > 0.0001)
            throw new RuntimeException("Sum of the fractions is not equal to 1");
        
        this.colores = colores;
        this.porcentajes = porcentajes;
        this.porcentajes_exp = porcentajes_exp;
    }
    
    public void setColoresYPorcentajes(CellPane other) {
        this.colores = other.colores;
        this.porcentajes_exp = other.porcentajes_exp;
        this.porcentajes = other.porcentajes;
    }
    
    public String getColor(int i){
        return colores[i];
    }
    
    public double getPorcentaje(int i) {
        return porcentajes[i];
    }
    
    public String getPorcentajeExp(int i) {
        return porcentajes_exp[i];
    }
    
    public double getCombosOfColor(String color) {
        for (int i = 0; i < colores.length; i++) {
            if (colores[i].equals(color))
                return porcentajes[i];
        }
        return 0.0;
    }
    
    
    
    public int getCombosByName() {
        switch (name.charAt(name.length() - 1)) {
            case 'o' -> {return 12;}
            case 's' -> {return 4;}
            default -> {return 6;}
        }
    }
    
    private static double interpretar(String valor) {
        var valores = valor.split("/");
        return Double.parseDouble(valores[0]) / Double.parseDouble(valores[1]);
    }
}

class ColorYPorcentaje {
    public String color;
    public double porcentaje;
    public ColorYPorcentaje(String color, double porcentaje) {
        this.color = color;
        this.porcentaje = porcentaje;
    }
}
