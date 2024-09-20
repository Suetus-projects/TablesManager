/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Functional;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.util.Pair;

/**
 *
 * @author dan
 */
public class CargadoYGuardado {
    
    private static final String start_token = "#->mj!b%5PtnW,Kr0erC7Fql%zFvHxe-\n"; //En caso de cambiar de acabar con un salto
    
    public static void guardarEn(Tabla[] tablas, String file) {
        
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
        } catch (FileNotFoundException ex) {
            System.out.println("Error al iniciar writer: ");
            ex.printStackTrace();
            return;
        } catch (UnsupportedEncodingException e) {
            System.out.println("Encoding no soportado (???)");
        }
        
        try {
            for (Tabla tabla : tablas) {
                writer.write(start_token);
                writer.write(tablaToString(tabla) + "\n");
            }
            writer.close();
        } catch (Exception e) {
            System.out.println("Error al escribir en una tabla ");
            e.printStackTrace();
        }
        
        try {
            writer.close();
        } catch (IOException e) {
            System.out.println("Error al cerrar writer");
        }
        
        System.out.println("Tabla guardada");
    }
    
    public static TablaInit[] readFrom(String file) throws IOException {
        var tablas_text = Files.readString(Paths.get(file)).split(start_token);
        
        List<TablaInit> result = new ArrayList<>();
        for (var tabla_text : tablas_text) {
            if (tabla_text.isEmpty())
                continue;
            
            var next_tabla = stringToTablaInit(tabla_text);
            
            result.add(next_tabla);
        }
        
        return result.toArray(new TablaInit[result.size()]);
    }
    
    public static String tablaToString(Tabla tabla) {
        var builder = new StringBuilder();
        //builder.append(start_token).append("\n");
        builder.append(tabla.title).append("\n");

        var coloresYIDs = new HashMap<String, Integer>();

        for (var next_color_pane : tabla.existing_colors_flowpane.getChildren()) {
            var colored_pane = (ColoredPane)next_color_pane;
            var pane_text = ((Label)colored_pane.getChildren().get(0)).getText();
            pane_text = pane_text.substring(0, pane_text.lastIndexOf('(') - 1);
            builder.append(":" + colored_pane.getColor() + "(" + pane_text + ")").append("\n");
            coloresYIDs.put(colored_pane.getColor(), coloresYIDs.size());
        }

        for (var panes_arr : tabla.cards_panes) {
            for (var next_pane : panes_arr) {
                for (int i = 0; i < next_pane.getNumeroElementos(); i++) 
                    builder.append("-" + coloresYIDs.get(next_pane.getColor(i)) + ":" + next_pane.getPorcentajeExp(i));
                //writer.println(" (" + next_pane.getName() + ")");//Debug only, change for an empty println()
                builder.append("\n");
            }
        }

        builder.append("<").append("\n");//Empieza la nota
        builder.append(tabla.notes.getText());
        //La nota debe der ser lo último en el archivo
        return builder.toString();
    }
    
    static boolean done = false; //DEBUG
    public static TablaInit stringToTablaInit(String tabla_text) {
        
        if (!done) {
            done = true;
            System.out.println("Recibiendo:···" + tabla_text + "...");
        }
        
        var tabla_text_lines = tabla_text.split("\n");
        String titulo_tabla = tabla_text_lines[0];
        /*if (!tabla_text_lines[0].isEmpty()) {
            System.out.println("Beging of tabla_text_lines not empty!!: <" + tabla_text_lines[0] + ">");
            System.exit(-1);
        }*/ //Ahora token tiene un salto de linea
        List<String> colores = new ArrayList<>();
        List<String> titulos_colores = new ArrayList<>();

        int lines_it = 1;
        for (;tabla_text_lines[lines_it].charAt(0) == ':'; ++lines_it) {
            String next_line = tabla_text_lines[lines_it];
            int parentesis = next_line.indexOf("(");
            colores.add(next_line.substring(1, parentesis));
            titulos_colores.add(next_line.substring(parentesis + 1, next_line.length() - 1));
        }

        CellPane[] cards_panes = new CellPane[13 * 13];
        for (int i = 0; i < cards_panes.length; i++) 
            cards_panes[i] = new CellPane();

        int cards_panes_it = 0;

        for(;tabla_text_lines[lines_it].charAt(0) == '-'; ++lines_it) {
            var celda_text_lines = tabla_text_lines[lines_it].split("-");
            celda_text_lines = Arrays.copyOfRange(celda_text_lines, 1, celda_text_lines.length);

            String[] colores_celda = new String[celda_text_lines.length];
            String[] expresiones_celda = new String[celda_text_lines.length];

            for (int i = 0; i < celda_text_lines.length; ++i) {
                var colorYExp = celda_text_lines[i].split(":");
                colorYExp[0] = colores.get(Integer.parseInt(colorYExp[0]));
                colores_celda[i] = colorYExp[0];
                expresiones_celda[i] = colorYExp[1];
            }


            cards_panes[cards_panes_it++].setColoresYPorcentajes(colores_celda, expresiones_celda);
        }

        ++lines_it; //Pasando '<'
        String notes = "";
        for (;lines_it < tabla_text_lines.length; ++lines_it) {
            if (!notes.isEmpty())
                notes += '\n';
            notes += tabla_text_lines[lines_it];
        }

        var result = new TablaInit();
        result.title = titulo_tabla;
        result.cards_panes = cards_panes;
        result.colores_y_titulos = new ArrayList<>(colores.size());
        for (int i = 0; i < colores.size(); i++) {
            result.colores_y_titulos.add(new Pair<>(colores.get(i), titulos_colores.get(i)));
        }
        result.notes = notes;
        
        return result;
    }
    
    public static void setTextInClipboar(String text) {
        StringSelection stringSelection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }
    
    public static String getTextFromClipboard() {
        try {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Clipboard clipboard = toolkit.getSystemClipboard();
            String result = (String) clipboard.getData(DataFlavor.stringFlavor);
            return result;
        } catch (Exception e){
            return null;
        }
    }
}
