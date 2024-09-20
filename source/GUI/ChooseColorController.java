/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package GUI;

import Functional.ColoredPane;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author dan_deb
 */
public class ChooseColorController implements Initializable {

    @FXML
    private Button btn_ok;
    @FXML
    private Button btn_cancel;
    @FXML
    private Pane colors_pane;

    private List<String> colores_elegidos = null;
    private Map<String, ColoredPane> colores_y_btns;
    
    /**
     * Initializes the controller class.
     */
    
    private HashMap<String, Boolean> colors_used;
    
    private final static String[][] paleta_colores = {
        {"7E1806", "331900", "999900", "990099", "003300", "0000CC", "000000", "CC0066"},
        {"CC0000", "994C00", "ffff00", "FF00FF", "009900", "0066CC", "707070", "FF66B2"},
        {"FF9999", "FF8000", "ffff66", "FF99FF", "A1FFA1", "99CCFF", "D0D0D0", "FFCCE5"},
    };
    
    /*private final static HashSet<String> dark_colors = new HashSet<String>(
            Set.of("7E1806", "331900", "003300", "0000CC", "000000", "999900")
    );*/ //ON MAIN!
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        colors_pane.setStyle("");
        crearCuadriculaColores();
        crearMapaColoresUsados();
    }    

    @FXML
    private void onOkBtn(ActionEvent event) {
        var colors = new ArrayList<String>();
        for (var entry : colors_used.entrySet())
            if (entry.getValue() == true)
                colors.add(entry.getKey());
        colores_elegidos = colors;
        ((Stage)btn_ok.getScene().getWindow()).close();
    }

    @FXML
    private void onCancelButton(ActionEvent event) {
        ((Stage)btn_ok.getScene().getWindow()).close();
    }
    
    private void crearCuadriculaColores() {
        colores_y_btns = new HashMap<>();
        
        HBox hbox = new HBox();
        hbox.setMaxSize(colors_pane.getPrefWidth(), colors_pane.getPrefHeight());
        for (int i = 0; i < paleta_colores[0].length; i++) {
            var vbox = new VBox();
            for (int j = 0; j < paleta_colores.length; j++) {
                String chosen_color = paleta_colores[j][i];
                var colored_pane = new ColoredPane(chosen_color);
                colored_pane.setStyle("-fx-background-color: #" + chosen_color + ";" + 
                                      "-fx-background-radius: 2");
                colored_pane.setPrefSize(99999, 999999);
                colored_pane.setOnMouseClicked((ev) -> {btnAction(colored_pane);});
                vbox.getChildren().add(colored_pane);
                
                colores_y_btns.put(chosen_color, colored_pane);
            }
            hbox.getChildren().add(vbox);
        }
        
        colors_pane.getChildren().add(hbox);
    }
    
    public void blockColors(List<String> colors) {
        for (var color : colors) {
            colores_y_btns.get(color).setVisible(false);
        }
    }
    
    private void btnAction(ColoredPane btn) {
        if (colors_used.get(btn.getColor())){
            btn.setStyle("-fx-background-color: #" + btn.getColor() + ";" + 
                         "-fx-background-radius: 2");
            colors_used.put(btn.getColor(), false);
        } else {
            btn.setStyle(""
                    + "-fx-background-color: #" + btn.getColor() + ";"
                    + "-fx-background-radius: 4;"
                    + "-fx-border-color: white;"
                    + "-fx-border-radius: 2;"
                    + "-fx-border-width: 4;"
            );
            colors_used.put(btn.getColor(), true);
        }
    }
    
    private void crearMapaColoresUsados() {
        colors_used = new HashMap<>();
        for (var i : paleta_colores) {
            for (var j : i) {
                colors_used.put(j, false);
            }
        }
    }
    
    ////
    
    public List<String> getAnswer() {
        return colores_elegidos;
    }
}
