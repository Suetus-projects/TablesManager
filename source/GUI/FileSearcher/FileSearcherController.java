/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package GUI.FileSearcher;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.BiFunction;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.function.Function;
import javafx.geometry.Pos;
import javafx.scene.control.TextInputDialog;


/**
 * FXML Controller class
 *
 * @author dan_deb
 */
public class FileSearcherController implements Initializable {
    
    public enum Mode {
        Save, Load
    }
    
    public static final String home_path = System.getProperty("user.home");

    @FXML
    private AnchorPane main_anchorpane;
    @FXML
    private Button forward_btn;
    @FXML
    private Button back_btn;
    @FXML
    private Pane name_size_date_pane;
    @FXML
    private Pane files_pane;
    @FXML
    private Button ok_btn;
    @FXML
    private Button cancel_btn;
    @FXML
    private TextField location_textfield;
    @FXML
    private TextField file_textfield;
    @FXML
    private Button home_btn;
    
    private final double cuarto_ancho_panel = (575 - 15) / 4;
    
    private File result = null;
    
    private File actual_directory = null;
    private VBox files_vbox = null;
    private final Map<Object, File> files_register = new HashMap<>();
    private final List<File> backs_register = new ArrayList<>();
    
    
    public static final String FILEX_EXT = "tabm";

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        name_size_date_pane.setStyle("");
        files_pane.setStyle("");
        
        main_anchorpane.getStylesheets().setAll(getClass().getResource("style.css").toExternalForm());
        //^^ De otra forma no reconoce el archivo
        
        initBasicComponents();
    }    
    
    private void initBasicComponents() {
        var files_sp = new ScrollPane();
        files_pane.getChildren().add(files_sp);
        files_sp.setPrefSize(files_pane.getPrefWidth(), files_pane.getPrefHeight());
        files_vbox = new VBox();
        //files_sp.setContent(files_vbox);
        var not_set_warning = new Label();
        not_set_warning.setText("Error: mode not set!");
        not_set_warning.setStyle("-fx-font-size: 16");
        files_sp.setContent(not_set_warning);
        
        var nsd_hbox = new HBox();
        nsd_hbox.setPrefSize(name_size_date_pane.getPrefWidth(), name_size_date_pane.getPrefHeight());
        nsd_hbox.setMaxSize(name_size_date_pane.getPrefWidth(), name_size_date_pane.getPrefHeight());
        
        BiFunction<String, Double, Pane> createNSD_Pane = (titulo, ancho) -> {
            var panel = new Pane();
            panel.setMinSize(ancho, nsd_hbox.getPrefHeight());
            panel.setMaxSize(ancho, nsd_hbox.getPrefHeight());
            var label = new Label(titulo);
            label.setMinSize(ancho, nsd_hbox.getPrefHeight());
            label.setMaxSize(ancho, nsd_hbox.getPrefHeight());
            label.getStyleClass().add("names_size_date_label");
            label.setAlignment(Pos.BOTTOM_LEFT);
            panel.getChildren().add(label);
            return panel;
        };
        
        nsd_hbox.getChildren().add(createNSD_Pane.apply("Name", cuarto_ancho_panel * 2));
        nsd_hbox.getChildren().add(createNSD_Pane.apply("Size", cuarto_ancho_panel));
        nsd_hbox.getChildren().add(createNSD_Pane.apply("Last access", cuarto_ancho_panel));
        name_size_date_pane.getChildren().add(nsd_hbox);
    }

    @FXML
    private void action_forward_btn(ActionEvent event) {
        if (backs_register.isEmpty())
            return;

        int final_el = backs_register.size() - 1;
        this.setLocationTo(backs_register.get(final_el));
        backs_register.remove(final_el);
    }

    @FXML
    private void action_back_btn(ActionEvent event) {
        backs_register.add(actual_directory);
        this.setLocationTo(actual_directory.getParentFile());
    }

    @FXML
    private void action_ok_btn(ActionEvent event) {
        String res_path = actual_directory.getAbsolutePath() + "/" + file_textfield.getText();
        this.result = new File(res_path);
        this.close();
    }

    @FXML
    private void action_cancel_btn(ActionEvent event) {
        this.close();
    }
    
    private void close() {
        ((Stage)main_anchorpane.getScene().getWindow()).close();
    }

    @FXML
    private void action_home_btn(ActionEvent event) {
        this.setLocationTo(home_path);
    }
    
    @FXML
    private void locationTfEnter(ActionEvent ev) {
        var file = new File(location_textfield.getText());
        if (file.isDirectory()) {
            setLocationTo(file);
        }
    }
    
    @FXML
    private void action_add_file_btn(ActionEvent ev) {
        TextInputDialog dialog = new TextInputDialog("New File");
        dialog.getDialogPane().setContentText("Insert the name of the new file");
        dialog.setHeaderText(null);
        dialog.setTitle("New of new file");
        var answer = dialog.showAndWait();
        if (!answer.isPresent() || answer.isEmpty())
            return;
        String name = dialog.getEditor().getText();
        
        //String new_path = actual_directory.getAbsolutePath() + "/" + name;
        File new_file = new File(actual_directory, name);
        if (new_file.mkdir())
            System.out.println("Created");
        else
            System.out.println("Failed");
        
        this.setLocationTo(actual_directory);
    }
    
    public File getResult() {
        return this.result;
    }
    
    public void set(Mode mode) {
        this.set(home_path, mode);
    }
    
    public void set(String path, Mode mode) {
        ((ScrollPane)files_pane.getChildren().get(0)).setContent(files_vbox);
        setLocationTo(path);
    }
    
    private void setLocationTo(String path) {
        setLocationTo(new File(path));
    }
    
    private void setLocationTo(File file) {
        this.actual_directory = file;
        var subfiles = filterFiles(file.listFiles());
        files_register.clear();
        files_vbox.getChildren().clear();
        for (File subfile : subfiles) {
            var added = addFilePanel(subfile);
            files_register.put(added, subfile);
        }
        location_textfield.setText(file.getAbsolutePath());
        location_textfield.positionCaret(location_textfield.getLength());
    }
    
    private Pane addFilePanel(File file) {
        final double alto_panel = 12;
        var file_pane = new Pane();
        var pane_hbox = new HBox();
        file_pane.getChildren().add(pane_hbox);
        //pane_hbox.getStylesheets().setAll(getClass().getResource("style.css").toExternalForm());
        file_pane.getStyleClass().setAll("file_pane");
        
        var name_lb = new Label(file.getName());
        name_lb.setPrefWidth(cuarto_ancho_panel * 2);
        name_lb.setPrefHeight(alto_panel);
        pane_hbox.getChildren().add(name_lb);
        
        var size_lb = new Label(String.valueOf(getProperSizeFormat(file)));
        size_lb.setPrefWidth(cuarto_ancho_panel);
        size_lb.setPrefHeight(alto_panel);
        pane_hbox.getChildren().add(size_lb);
        
        var acces_lb = new Label(String.valueOf(getDatecorrectFormat(file.lastModified())));
        acces_lb.setPrefWidth(cuarto_ancho_panel);
        acces_lb.setPrefHeight(alto_panel);
        pane_hbox.getChildren().add(acces_lb);
        
        file_pane.setOnMouseClicked(ev -> {
            filePaneAction((Pane)ev.getSource());
        });
        
        files_vbox.getChildren().add(file_pane);
        
        return file_pane;
    }
    
    private String getProperSizeFormat(File f) {
        if (f.isDirectory())
            return "";
        
        double size_d = (double)f.length();
        String[] unidades = {"b", "Kb", "Mb", "Gb", "Tb"};
        int unidad_a_usar = 0;
        while (size_d >= 1024) {
            ++unidad_a_usar;
            size_d /= 1024;
        }
        
        return String.valueOf((int)size_d) + unidades[unidad_a_usar];
    }
    
    private String getDatecorrectFormat(long time) {
        var format = new SimpleDateFormat("dd MMM yy");
        return format.format(new Date(time));
    }
    
    //private Pane last_pressed = null;
    private void filePaneAction(Pane file_pane) {
        File file = files_register.get(file_pane);
        if (file.isDirectory()) {
            setLocationTo(file);
            backs_register.clear();
            file_textfield.clear();
        } else {
            file_textfield.setText(file.getName());
            file_textfield.positionCaret(file_textfield.getLength());
        }
    }
    
    public static File[] filterFiles(File[] files) {
        var filtred = new ArrayList<File>();
        
        for (File f : files) {
            if (f.isFile() && !getExtension(f).equals(FILEX_EXT)) {
                continue;
            }
            filtred.add(f);
        }
        
        return filtred.toArray(File[]::new);
    }
    
    public static String getExtension(File file) {
        String name = file.getName();
        int point = name.lastIndexOf('.');
        if (point > 0)
            return name.substring(point + 1);
        else
            return "";
    }
    
}
