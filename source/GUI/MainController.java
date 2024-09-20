/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package GUI;

import Functional.CargadoYGuardado;
import Functional.ColoredPane;
import Functional.TablaInit;
import Functional.CellPane;
import Functional.Fractions;
import Functional.Tabla;
import GUI.FileSearcher.FileSearcherController;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 * FXML Controller class
 *
 * @author dan
 */
public class MainController implements Initializable {

    @FXML
    private AnchorPane main_anchorpane;
    @FXML
    private Pane cells_pane_mother;
    @FXML
    private ScrollPane tables_scroll_pane;
    @FXML
    private VBox tables_vbox;
    @FXML
    private Pane existing_colores_pane_mother;
    @FXML
    private Pane celldata_pane;
    @FXML
    private Button btn_apply;
    @FXML
    private VBox cells_parts_vbox;
    @FXML
    private Button add_btn;
    @FXML
    private CheckBox copy_cell_checkbox;
    @FXML
    private Pane notes_ta_mother;
    @FXML
    private Label messages_label;
    @FXML
    private Menu menu_copy_range;
    
    private boolean file_is_modified = false;
    private ToggleGroup tablas_group = new ToggleGroup();
    private ToggleButton tablas_group_aprensado;
    private Pane cells_pane;
    private FlowPane existing_colors_flowpane;
    private TextArea notes_ta;
    private CellPane[][] cards_panes;
    
    final private String[][] cells_names = getCellsNames();
    final static private int valores_distintos = 13;
    final static private int combos_totales = 1326;
    final static private double ancho_bordes = 1;
    final static private double MAX_SIZE = 99999;
    final static private long notifications_def_time = 3000; //milliseconds

    private String archivo_cargado = null;
    private Map<String, Tabla> mapa_tablas = new HashMap<>();
    private CellPane pane_clicked = null;
    private Timer notifications_timer = new Timer(true);
    
    /**
     * Initializes the controller class.
     */
        
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        cells_pane_mother.setStyle("");
        messages_label.setStyle("");
        
        crearNuevoTablero("Open raise");
    }    
    
    @FXML
    private void addColorAction(ActionEvent ev) {
        //choose_color_pane.setVisible(!choose_color_pane.isVisible());
        
        var used_colors = new ArrayList<String>();
        used_colors.ensureCapacity(existing_colors_flowpane.getChildren().size());
        for (Node element : existing_colors_flowpane.getChildren()) {
            var as_coloredp = (ColoredPane)element;
            if (!as_coloredp.getColor().equals("FFFFFF")){
                used_colors.add(as_coloredp.getColor());
            }
        }
        
        Parent root;
        var loader = new FXMLLoader(getClass().getResource("ChooseColor.fxml"));
        try {
            root = loader.load();
        } catch (IOException e) {
            System.err.println("Error reading: " + e);
            e.printStackTrace();
            return;
        }
        var controller = (ChooseColorController)loader.getController();
        controller.blockColors(used_colors);
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setTitle("Choose color");
        
        main_anchorpane.setDisable(true);
        stage.showAndWait();
        main_anchorpane.setDisable(false);
        
        var answer = controller.getAnswer();
        if (answer == null)
            return;
        
        for (var color : answer)
            insertColor(color);
        
        setModified(true);
    }
    
    @FXML
    private void btnApplyAction(ActionEvent ev) {
        var cells_parts_vbox_children = cells_parts_vbox.getChildren();
        var colores = new String[cells_parts_vbox_children.size() - 1];
        var porcentajes_exp = new String[colores.length];
        for (int i = 0; i < colores.length; i++) {
            var next_hbox = (HBox)cells_parts_vbox.getChildren().get(i);
            var next_colored_pane = (ColoredPane)next_hbox.getChildren().get(0);
            var next_exp = ((TextField)next_hbox.getChildren().get(1)).getText();
            colores[i] = next_colored_pane.getColor();
            porcentajes_exp[i] = next_exp;
        }
        try {
            pane_clicked.setColoresYPorcentajes(colores, porcentajes_exp);
        } catch (RuntimeException e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
            return;
        }
        var nueva_box = crearHBoxDistribuida(pane_clicked, pane_clicked.getHeight(), pane_clicked.getWidth());
        pane_clicked.getChildren().set(0, nueva_box);
        
        refrescarConteoCombos();
        setColorOfPane(pane_clicked);
        setModified(true);
    }
    
    @FXML
    private void copyTableToClipboard(ActionEvent ev) {
        var tabla = getUsingTable();
        String text = CargadoYGuardado.tablaToString(tabla);
        CargadoYGuardado.setTextInClipboar(text);
    }
    
    @FXML
    private void pasteTableFromClipboard(ActionEvent ev) {
        /*showAlert(Alert.AlertType.ERROR, "Error", "Función no desarrolada");
        return;*/
        
        String text_on_clipb = CargadoYGuardado.getTextFromClipboard();
        if (text_on_clipb == null) {
            System.err.println("text on clipboard is null");
            return;
        }
        
        TablaInit tabla_init;
        try {
            tabla_init = CargadoYGuardado.stringToTablaInit(text_on_clipb);
            addTabla(tabla_init);
        } catch (Exception e) {
            System.err.println("Error en tabla");
            return;
        }
        System.out.println("Tabla pegada");
    }
    
    private static void setColorOfPane(CellPane pane) {
        var label = (Label)pane.getChildren().get(1);
        label.setTextFill(Color.web("#" + getColorRequerido(pane)));
    }
    
    private static String getColorRequerido(CellPane pane) {
        for (int i = 0; i < pane.getNumeroElementos(); ++i)
            if (dark_colors.contains(pane.getColor(i)))
                return color_alternativo_letra;
        return "000000";
    }
    
    @FXML
    private void addTableBtnAction(ActionEvent ev) {
        //main_anchorpane.setDisable(true);
        var nuevo_titulo = readAnswer("New table name", "Insert the name for the new table", null);
        //main_anchorpane.setDisable(false);
        if (nuevo_titulo == null)
            return;
        for (var entry : mapa_tablas.entrySet()) {
            if (entry.getKey().equals(nuevo_titulo)) {
                showAlert(Alert.AlertType.ERROR, "Title repeated", "There is a table with that name already");
                return;
            }
        }
        crearNuevoTablero(nuevo_titulo);
        tables_scroll_pane.setVvalue(1.0);
        setModified(true);
    }
    
    @FXML
    private void renameTableBtnAction(ActionEvent ev) {
        String original = tablas_group_aprensado.getText();
        
        var new_name = readAnswer("New name", "Set the new name for the selected table", original);
        if (new_name == null)
            return;
        
        for (var entry : mapa_tablas.entrySet()) {
            if (entry.getKey().equals(new_name)) {
                showAlert(Alert.AlertType.ERROR, "Repeated name", "There is an existing table with that name.");
                return;
            }
        }
        
        var tabla = mapa_tablas.remove(original);
        mapa_tablas.put(new_name, tabla);
        tabla.title = new_name;
        tablas_group_aprensado.setText(new_name);
        setModified(true);
    }
    
    @FXML
    private void deleteTableBtnAction(ActionEvent ev) {
        if (mapa_tablas.size() == 1) {
            showAlert(Alert.AlertType.ERROR, "There is only one table", "You can't have a file with 0 tables");
            return;
        }
        
        var titulo = tablas_group_aprensado.getText();
        if (!readConfirmation("Confirm delete", "Do you really want to delete " + titulo + " table?"))
            return;
        
        var deleted = mapa_tablas.remove(titulo);
        if (deleted == null)
            throw new RuntimeException("Error al borrar la tabla '" + titulo + "'");
        var togglebuttons_list = tables_vbox.getChildren();
        togglebuttons_list.remove(tablas_group_aprensado);
        var primer_boton = (ToggleButton)togglebuttons_list.get(0);
        primer_boton.fire();
        setModified(true);
    }
    
    @FXML
    private void deleteColorsBtnAction(ActionEvent ev) {
        final String delete_token = "del";
        
        var to_delete = new ArrayList<String>();
        for (var node : existing_colors_flowpane.getChildren()) {
            var pane = (ColoredPane)node;
            var label = (Label)pane.getChildren().get(0);
            var label_text = getSeparatedColorTextAndCombo(label.getText())[0];
            
            if (label_text.equals(delete_token))
                to_delete.add(pane.getColor());
        }
        
        if (to_delete.isEmpty()) { 
            showAlert(Alert.AlertType.INFORMATION, "Deleting colors", "Rename the color to delete as '" + delete_token + "' and click here to delete them");
            return;
        }
        
        int failed_colors = 0;
        for (var next_color : to_delete) {
            boolean success = tryDeleteColor(next_color);
            if (!success)
                ++failed_colors;
        }
        
        if (failed_colors != 0) {
            showAlert(Alert.AlertType.WARNING, "Colors not deleted", 
                    "" + failed_colors + " color(s) was not deleted because they are in use");
        }
        
        if (failed_colors < to_delete.size())
            setModified(true);
    }
    
    @FXML
    private void saveFileBtnAction(ActionEvent ev) {
        if (archivo_cargado == null) {
            saveAsBtnAction(ev);
            return;
        }
        
        showNotification("Saving...");
        CargadoYGuardado.guardarEn(getTablasEnOrden(), archivo_cargado);
        setModified(false);
        showNotification("File saved!");
    }
    
    @FXML
    private void saveAsBtnAction(ActionEvent ev) {
        Parent root;
        var loader = new FXMLLoader(getClass().getResource("FileSearcher/FileSearcher.fxml"));
        try {
            root = loader.load();
        } catch (IOException e) {
            System.err.println("Error reading: " + e);
            e.printStackTrace();
            return;
        }
        var controller = (FileSearcherController)loader.getController();
        Scene scene = new Scene(root);
        var stage = new Stage();
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setTitle("Save...");
        controller.set(FileSearcherController.Mode.Save);
        main_anchorpane.setDisable(true);
        stage.showAndWait();
        main_anchorpane.setDisable(false);
        
        File file = controller.getResult();
        if (file == null)
            return;
        
        String ext = FileSearcherController.FILEX_EXT;
        if (!file.getAbsolutePath().endsWith("." + ext))
            file = new File(file.getAbsolutePath() + "." + ext);
        
        if (file.exists()) {
            //readAnswer(archivo_cargado, archivo_cargado, archivo_cargado);//implementar?
            showAlert(Alert.AlertType.ERROR, "Error", file.getName() + " already exists");
            return;
        }
        
        CargadoYGuardado.guardarEn(getTablasEnOrden(), file.getAbsolutePath());
        archivo_cargado = file.getAbsolutePath();
        setModified(false);
        showNotification("File saved!");
    }
    
    @FXML
    private void openFileBtnAction(ActionEvent ev) {
        Parent root;
        var loader = new FXMLLoader(getClass().getResource("FileSearcher/FileSearcher.fxml"));
        try {
            root = loader.load();
        } catch (IOException e) {
            System.err.println("Error reading: " + e);
            e.printStackTrace();
            return;
        }
        var controller = (FileSearcherController)loader.getController();
        Scene scene = new Scene(root);
        var stage = new Stage();
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setTitle("Open...");
        controller.set(FileSearcherController.Mode.Load);
        main_anchorpane.setDisable(true);
        stage.showAndWait();
        main_anchorpane.setDisable(false);
        
        File file = controller.getResult();
        if (file == null) {
            showNotification("Error reading archive");
            return;
        }
        
        if (!file.exists())
            showAlert(Alert.AlertType.ERROR, "Wrong file", "File does not exist");
        
        setFromFile(file.getAbsolutePath());
        archivo_cargado = file.getAbsolutePath();
        setModified(false);
    }
    
    @FXML
    private void exportCurrentTable(ActionEvent ev) {
        //String new_file_name = readAnswer("Insert name", "Insert name of new file", "image.png");
        String home_dir = System.getProperty("user.home");
        
        String def_name = "image";
        String next_name = def_name;
        for (int i = 2; true; i++) {
            var next_file = new File("./" + next_name + ".png");
            if (next_file.exists()) {
                next_name = def_name + String.valueOf(i);
                continue;
            }
            break;
        }
        
        saveNodeImageIn(cells_pane_mother, "./" + next_name); //así no se añade .png 2 veces
        showNotification("Table exported!");
    }
    
    @FXML
    private void exportAllTables(ActionEvent ev) {
        Parent root;
        var loader = new FXMLLoader(getClass().getResource("FileSearcher/FileSearcher.fxml"));
        try {
            root = loader.load();
        } catch (IOException e) {
            System.err.println("Error reading: " + e);
            e.printStackTrace();
            return;
        }
        var controller = (FileSearcherController)loader.getController();
        Scene scene = new Scene(root);
        var stage = new Stage();
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setTitle("Select file to export");
        controller.set(FileSearcherController.Mode.Save);
        main_anchorpane.setDisable(true);
        stage.showAndWait();
        main_anchorpane.setDisable(false);
        
        var file = controller.getResult();
        if (file == null || !file.exists() || !file.isDirectory()) {
            showAlert(Alert.AlertType.ERROR, "Wrong file", "Please select a valid folder to export tables");
            return;
        }
        String save_path = file.getAbsolutePath() + "/";
        System.out.println("save path: " + save_path);
        
        var tablas = getTablasEnOrden();
        for (var tabla : tablas) {
            saveNodeImageIn(tabla.cells_pane, save_path + getProperTableTitle(tabla.title));
        }
        
        //Now just the names txt
        
        var builder = new StringBuilder();
        for (var tabla : tablas)
            builder.append(tabla.title).append('\n');
        
        try (PrintWriter out = new PrintWriter(save_path + "names_order.txt")) {
            out.print(builder.toString());
        } catch (Exception e) {
            System.out.println("Error saving the names file: " + e.getMessage());
        }
        
        showNotification("Tables exported!");
    }
    
    private static String getProperTableTitle(String title) {
        var builder = new StringBuilder();
        for (int i = 0; i < title.length(); i++) {
            if (title.charAt(i) == '/')
                builder.append('_');
            else
                builder.append(title.charAt(i));
        }
        return builder.toString();
    }
    
    @FXML
    private void changeTablesOrderBtnAction(ActionEvent ev) {
        
        var buttons_list = tables_vbox.getChildren();
        if (buttons_list.size() < 2) {
            showAlert(Alert.AlertType.ERROR, "Error", "Not enough tables to re-order");
            return;
        }
        
        var names = new String[buttons_list.size()];
        for (int i = 0; i < names.length; i++)
            names[i] = ((ToggleButton)buttons_list.get(i)).getText();
        
        
        
        Parent root;
        var loader = new FXMLLoader(getClass().getResource("OrderChanger.fxml"));
        try {
            root = loader.load();
        } catch (IOException e) {
            System.err.println("Error reading: " + e);
            return;
        }
        var controller = (OrderChangerController)loader.getController();
        controller.setContent(names);
        Stage panel_preguntas = new Stage();
        Scene scene = new Scene(root);
        panel_preguntas.setResizable(false);
        panel_preguntas.setScene(scene);
        panel_preguntas.setTitle("Change order");
        panel_preguntas.showAndWait();
        
        var result = controller.getResult();
        if (result == null)
            return;
        
        if (result.length != buttons_list.size()) 
            throw new RuntimeException("Result length is different than number of buttons!! (" + result.length + " & " + ")");
        
        for (int i = 0; i < result.length; i++) {
            ((ToggleButton)buttons_list.get(i)).setText(result[i]);
        }
        
        var btn1 = (ToggleButton)buttons_list.get(0);
        var btn2 = (ToggleButton)buttons_list.get(1);
        
        if (btn1.isSelected()) {
            btn2.fire();
        } else {
            btn1.fire();
        }
    }
    
    String getRangeOFColor(String color) {
        StringBuilder result = new StringBuilder();
        for (var line : cards_panes) {
            for (CellPane pane : line) {
                double combos = pane.getCombosOfColor(color);
                if (combos < Double.MIN_NORMAL) //is 0?
                    continue;
                
                result.append(pane.getName()).append(":").append(combos).append(',');
            }
        }
        
        if (!result.isEmpty())
            result.deleteCharAt(result.length() - 1);
        
        return result.toString();
    }
    
    @FXML
    private void setRangesOnCopyRange(Event ev) {
        var items = menu_copy_range.getItems();
        items.clear();
        
        var colors = existing_colors_flowpane.getChildren();
        for (int i = 0; i < colors.size(); i++) {
            var next_colored = (ColoredPane)colors.get(i);
            var pane_text = ((Label)next_colored.getChildren().get(0)).getText();
            var text_on_color = getSeparatedColorTextAndCombo(pane_text)[0];
            
            var next_item = new MenuItem(text_on_color);
            next_item.setStyle("-fx-background-color: #" + next_colored.getColor() + ";");
            if (dark_colors.contains(next_colored.getColor())) {
                var additional = "-fx-text-fill: #" + color_alternativo_letra + ";";
                next_item.setStyle(next_item.getStyle() + additional);
            }
            next_item.setOnAction(ev_ -> {
                CargadoYGuardado.setTextInClipboar(getRangeOFColor(next_colored.getColor()));
                showNotification("Range copied");
            });
            items.add(next_item);
        }
    }
    
    @FXML
    private void btnAboutAction(ActionEvent ev) {
        String message = "Tables Manager v" + Main.VERSION + " by Suetus";
        message += "";
        showAlert(Alert.AlertType.INFORMATION, "About", message);
    }
    
    public void onCloseAction(Event ev) {
        
        if (!file_is_modified) 
            return;
        
        if (archivo_cargado == null) {
            boolean ans = readConfirmation("Confirm exit", "The file has not been saved, do you really want to exit?");
            if (!ans)
                ev.consume();
            return;
        }
        var msg = "Save content into file " + archivo_cargado + "?";
        var alert = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.CANCEL, ButtonType.NO, ButtonType.YES);
        alert.setHeaderText(null);
        alert.setTitle("Save file");
        
        //Button yes_button = (Button)alert.getDialogPane().lookupButton(ButtonType.YES);
        //yes_button.setDefaultButton(true);
        
        var result = alert.showAndWait();
        if (result.get() == ButtonType.YES)
            CargadoYGuardado.guardarEn(getTablasEnOrden(), archivo_cargado);
        else if (result.get() == ButtonType.CANCEL)
            ev.consume();
        else
            ; //Just close without saving
        
    }
    
    private void addTabla(TablaInit tabla_init) {
        this.crearNuevoTablero(tabla_init.title);
            existing_colors_flowpane.getChildren().clear();
            for (var color : tabla_init.colores_y_titulos)
                insertColor(color.getKey(), color.getValue());
            
            int cards_panes_it = 0;
            for (int i = 0; i < valores_distintos; i++) {
                for (int j = 0; j < valores_distintos; j++) 
                    copyFromCell(tabla_init.cards_panes[cards_panes_it++], this.cards_panes[i][j]);
            }
            
            notes_ta.setText(tabla_init.notes);
            
            this.refrescarConteoCombos();
    }
    
    private void setFromFile(String file) {
        TablaInit[] tablas;
        try {
            tablas = CargadoYGuardado.readFrom(file);
        } catch (IOException e) {
            System.out.println("Error al leer el archivo " + file);
            System.err.println(e.getMessage());
            return;
        }
        
        mapa_tablas.clear();
        tables_vbox.getChildren().clear();
        
        for (var tabla_init : tablas) {
            addTabla(tabla_init);
        }
    }
    
    private ToggleButton makeTableToggleButton(String text) {
        var table_togglebutton = new ToggleButton(text);
        table_togglebutton.setToggleGroup(tablas_group);
        table_togglebutton.setPrefSize(tables_scroll_pane.getPrefWidth(), 30);
        table_togglebutton.setOnAction((ev) -> tableToggleButtonAction(table_togglebutton));
        table_togglebutton.getStyleClass().addAll("def_font", "table_togglebutton");
        return table_togglebutton;
    }
    
    private void crearNuevoTablero(String titulo) {
        var table_togglebutton = makeTableToggleButton(titulo);
        tables_vbox.getChildren().add(table_togglebutton);
        table_togglebutton.setSelected(true);
        tablas_group_aprensado = table_togglebutton;
        
        existing_colores_pane_mother.getChildren().clear();
        cells_pane_mother.getChildren().clear();
        notes_ta_mother.getChildren().clear();
        pane_clicked = null;
        
        existing_colors_flowpane = new FlowPane();
        existing_colors_flowpane.setPrefSize(existing_colores_pane_mother.getPrefWidth(), existing_colores_pane_mother.getPrefHeight());
        existing_colores_pane_mother.getChildren().add(existing_colors_flowpane);
        
        cells_pane = new Pane();
        cells_pane.setPrefSize(cells_pane_mother.getPrefWidth(), cells_pane_mother.getPrefHeight());
        cells_pane_mother.getChildren().add(cells_pane);
        
        notes_ta = createNotesTA("");
        notes_ta_mother.getChildren().add(notes_ta);
        
        crearCuadriculaCartas();
        insertColor("FFFFFF", "Fold");
        celldata_pane.setVisible(false);
        
        var tabla = new Tabla(titulo);
        tabla.cells_pane = this.cells_pane;
        tabla.existing_colors_flowpane = this.existing_colors_flowpane;
        tabla.cards_panes = cards_panes;
        tabla.notes = notes_ta;
        mapa_tablas.put(titulo, tabla);
        
        
        refrescarConteoCombos();
    }
    
    private TextArea createNotesTA(String content) {
        var result = new TextArea();
        //result.setStyle("-fx-background-color: #EEFFFF;");
        result.getStyleClass().add("notes_text_area");
        result.setWrapText(true);
        result.setPrefSize(notes_ta_mother.getPrefWidth(), notes_ta_mother.getPrefHeight());
        result.setText(content);
        return result;
    }
    
    private void tableToggleButtonAction(ToggleButton tb) {
        System.out.println("Llamando tableToggleButtonAction con nulo: " + (tb == null));
        if (tablas_group_aprensado == tb)
            return;
        tablas_group_aprensado = tb;
        var tabla = mapa_tablas.get(tb.getText());
        
        pane_clicked = null;
        
        cells_pane_mother.getChildren().set(0, tabla.cells_pane);
        existing_colores_pane_mother.getChildren().set(0, tabla.existing_colors_flowpane);
        notes_ta_mother.getChildren().set(0, tabla.notes);
        
        cells_pane = tabla.cells_pane;
        existing_colors_flowpane = tabla.existing_colors_flowpane;
        cards_panes = tabla.cards_panes;
        notes_ta = tabla.notes;
        changing_pane_of_color = null;
        celldata_pane.setVisible(false);
        copy_cell_checkbox.setSelected(false);
    }
    
    private void crearCuadriculaCartas() {
        cards_panes = new CellPane[valores_distintos][valores_distintos];
        double altura_total = cells_pane.getPrefHeight();
        double ancho_total = cells_pane.getPrefWidth();
        double divisiones = valores_distintos + 1;
        double altura_cuadro = (altura_total - divisiones * ancho_bordes) / (double)valores_distintos;
        double ancho_cuadro = (ancho_total - divisiones * ancho_bordes) / (double)valores_distintos;
        
        var vbox = new VBox();
        vbox.setMaxWidth(ancho_total);
        vbox.setMaxHeight(altura_total);
        vbox.getChildren().add(getSeparator(ancho_total, ancho_bordes));
        
        for (int i = 0; i < valores_distintos; i++) {
            HBox next_hbox = new HBox();
            next_hbox.getChildren().add(getSeparator(ancho_bordes, altura_cuadro));
            for (int j = 0; j < valores_distintos; j++) {
                var next_pane = crearPanelDistribuido(new String[]{"FFFFFF"}, new String[]{"1/1"}, altura_cuadro, ancho_cuadro, cells_names[i][j]);
                next_pane.getStyleClass().add("def_font_medium");
                next_hbox.getChildren().add(next_pane);
                next_hbox.getChildren().add(getSeparator(ancho_bordes, altura_cuadro));
                cards_panes[i][j] = next_pane;
            }
            vbox.getChildren().add(next_hbox);
            vbox.getChildren().add(getSeparator(ancho_total, ancho_bordes));
        }
        cells_pane.getChildren().add(vbox);        
    }
    
    private final static HashSet<String> dark_colors = new HashSet<String>(
            Set.of("7E1806", "331900", "003300", "0000CC", "000000", "999900", "990099", "CC0066")
    );
    
    private final static String color_alternativo_letra = "E9D833";
    
    private void insertColor(String color) {
        insertColor(color, "-");
    }
    
    private void insertColor(String color, String title) {
        var next_pane = new ColoredPane(color);
        next_pane.setStyle("-fx-background-color: #" + color + ";" +
                          "-fx-background-radius: 5;" +
                          "-fx-border-color: black;" +
                          "-fx-border-radius: 5;" +
                          "-fx-border-width: 1");
        next_pane.setOnMouseClicked((ev) -> {btnExistingColorAction(next_pane);});
        double flowpane_ancho = existing_colors_flowpane.getPrefWidth();
        double flowpane_altura = existing_colors_flowpane.getPrefHeight();
        
        next_pane.setPrefSize(flowpane_ancho / 3.0, flowpane_altura / 3.0);
        
        var title_label = new Label(title + " " + getMessageForCount(0));
        title_label.setStyle("-fx-font-size: 16");
        title_label.setPrefSize(next_pane.getPrefWidth(), next_pane.getPrefHeight());
        title_label.setLayoutX(9);
        if (dark_colors.contains(color))
            title_label.setTextFill(Color.web("#" + color_alternativo_letra));
        next_pane.getChildren().add(title_label);
        
        existing_colors_flowpane.getChildren().add(next_pane);
    }
    
    ColoredPane changing_pane_of_color = null;
    
    private void btnExistingColorAction(ColoredPane cpane) { //Solo actuará si se necesita cambiar un color, de otro modo puede cambiar su nombre
        String color = cpane.getColor();
        
        if (changing_pane_of_color == null) {
            final double margen = 6;
            
            var text_label = (Label)cpane.getChildren().get(0);
            var sep_text = getSeparatedColorTextAndCombo(text_label.getText());
            
            var tf = new TextField(sep_text[0]);
            tf.setLayoutX(margen);
            tf.setLayoutY(margen);
            tf.setPrefSize(cpane.getWidth() - (margen * 2), cpane.getHeight() - (margen * 2));
            tf.setStyle("-fx-font-size: 14");
            tf.setOnAction((ev) -> {
                if (tf.getText().isEmpty())
                    return;
                var new_title = tf.getText();
                cpane.getChildren().remove(1);
                text_label.setText(new_title + " " + sep_text[1]);
            });
            
            tf.requestFocus();
            
            
            cpane.getChildren().add(tf);
            
            return;
        }
        
        changing_pane_of_color.setColor(color);
        changing_pane_of_color.setStyle("-fx-background-color: #" + color);
        setChangingCellColor(changing_pane_of_color, false);
    }
    
    private boolean tryDeleteColor(String color) {
        /*Regresa verdadero si pudo hacer la tarea, falso si falla debido a que el color está en uso*/
        
        //Buscar el panel de dicho color 
        ColoredPane searched = null;
        for (var i : existing_colors_flowpane.getChildren()) {
            var as_coloredp = (ColoredPane)i;
            if (as_coloredp.getColor().equals(color)) {
                searched = as_coloredp;
                break;
            }
        }
        
        if (searched == null) {
            throw new RuntimeException("Color a eliminar por tryDeleteColor no existe");
        }
        
        //Buscar si está siendo usado
        if (colorBeingUsed(color))
            return false;
        
        
        //Borrar
        existing_colors_flowpane.getChildren().remove(searched);
        
        return true; 
    }
    
    private boolean colorBeingUsed(String color) {
        for (var ilera : cards_panes) {
            for (CellPane card_pane : ilera) {
                for (int i = 0; i < card_pane.getNumeroElementos(); ++i)
                    if (card_pane.getColor(i).equals(color))
                        return true;
            }
        }
        return false;
    }
    
    private Pane getSeparator(double x, double y) {
        var sep = new Pane();
        sep.setMinHeight(y);
        sep.setMinWidth(x);
        sep.setMaxHeight(y);
        sep.setMaxWidth(x);
        sep.setStyle("-fx-background-color: black");
        return sep;
    }
    
    private CellPane crearPanelDistribuido(String[] colores, String[] porcentajes, double alto, double ancho, String titulo) {
        var pane = new CellPane(titulo);
        pane.setColoresYPorcentajes(colores, porcentajes);
        var hbox_distribuida = crearHBoxDistribuida(pane, alto, ancho);
        pane.getChildren().add(hbox_distribuida);
        pane.setPrefSize(hbox_distribuida.getPrefWidth(), hbox_distribuida.getPrefHeight());
        //^ Solo es necesario para poder dibujar varios páneles a la hora de abrir
        
        //System.out.println("Creando panel distribuido");
        Label label = new Label(titulo);
        label.setPrefHeight(alto);
        label.setPrefWidth(ancho);
        label.setAlignment(Pos.CENTER);
        label.setStyle("-fx-font-size: 15; -fx-font-color: " + "#000000");
        pane.getChildren().add(label);
        
        pane.setOnKeyPressed((ev) -> {
            if (ev.getCode() == KeyCode.CONTROL)
                copyFromClickedCell(pane);
        });
                
        pane.setOnMouseClicked((ev) -> {
            cellClickedAction(pane);
        });
        
        pane.setOnMouseEntered((ev) -> {
            if (copy_cell_checkbox.isSelected())
                pane.requestFocus(); //Necesario para que funcione onKeyPressed
            if (ev.isControlDown())
                copyFromClickedCell(pane);
        });
        
        return pane;
    }
    
    private void copyFromClickedCell(CellPane destiny) {
        if (!copy_cell_checkbox.isSelected())
            return;
        
        copyFromCell(pane_clicked, destiny);
        refrescarConteoCombos();
        setModified(true);
    }
    
    public static void copyFromCell(CellPane source, CellPane destiny) {
        System.out.println("Mandando: " + destiny.getPrefHeight() + " " + destiny.getPrefWidth());
        var new_hbox = crearHBoxDistribuida(source, destiny.getPrefHeight(), destiny.getPrefWidth());
        destiny.getChildren().set(0, new_hbox);
        destiny.setColoresYPorcentajes(source);
        
        if (source.getChildren().size() >= 2) {
            var source_label = (Label)source.getChildren().get(1);
            var destiny_label = (Label)destiny.getChildren().get(1);
            destiny_label.setTextFill(source_label.getTextFill());
        } else {
            //System.out.println("Source no proveyó color de texto (copyFromCell)");
            setColorOfPane(destiny);
        }
    }
    
    private void refrescarConteoCombos() {
        var conteo = new HashMap<String, Double>();
        
        for (var cards_arr : cards_panes) {
            for (var card_pane : cards_arr) {
                for (int i = 0; i < card_pane.getNumeroElementos(); i++) {
                    double q_to_add = card_pane.getPorcentaje(i) * (double)card_pane.getCombosByName();
                    conteo.merge(card_pane.getColor(i), q_to_add, Double::sum);
                }
            }
        }
        
        for (var color_pane_node : existing_colors_flowpane.getChildren()) {
            var color_pane = (ColoredPane)color_pane_node;
            Label label = (Label)color_pane.getChildren().get(0);
            
            var sep_text = getSeparatedColorTextAndCombo(label.getText());
            
            Double res_conteo = conteo.get(color_pane.getColor());
            double combos = (res_conteo == null) ? 0 : res_conteo;
            sep_text[1] = getMessageForCount(combos);
            
            label.setText(sep_text[0] + " " + sep_text[1]);
        }
    }
    
    private String formatDoubleToReadeableString(double d) {
        String r = String.valueOf(d);
        int punto = r.indexOf('.');
        //???
        return r;
    }
    
    private String getMessageForCount(double conteo) {
        //un punto decimal:
        String val = String.valueOf(conteo);
        int punto = val.indexOf(".");
        return "(" + val.substring(0, punto + 2) + ")";
        
        //return "(" + (int)conteo + ")";
    }
    
    private String[] getSeparatedColorTextAndCombo(String text) {
        int parentesis = text.lastIndexOf('(');
        var result = new String[2];
        result[0] = text.substring(0, parentesis - 1);
        result[1] = text.substring(parentesis);
        return result;
    }
    
    public static HBox crearHBoxDistribuida(CellPane pane, double alto, double ancho) {
        HBox hbox = new HBox();
        hbox.setPrefHeight(alto);
        hbox.setPrefWidth(ancho);
        
        for (int i = 0; i < pane.getNumeroElementos(); i++) {
            var color = pane.getColor(i);
            Pane parte = new ColoredPane(color);
            parte.setStyle("-fx-background-color: #" + color);
            parte.setPrefHeight(alto);
            parte.setPrefWidth(ancho * pane.getPorcentaje(i));
            hbox.getChildren().add(parte);
        }
        
        return hbox;
    }
    
    private static final double colors_panel_height = 26;
    
    @FXML
    private Label cell_data_title_label;
    
    private void cellClickedAction(CellPane pane) {
        copy_cell_checkbox.setSelected(false);
        celldata_pane.setVisible(true);
        cell_data_title_label.setText("Cell: " + pane.getName());
        pane_clicked = pane;
        final double ancho_vbox = cells_parts_vbox.getWidth();
        cells_parts_vbox.getChildren().clear();
        for (int i = 0, len = pane.getNumeroElementos(); i < len; i++) {
            var box = makeCellPartPanel(pane.getColor(i), pane.getPorcentajeExp(i));
            cells_parts_vbox.getChildren().add(box);
        }
        var plus_btn = new Button("+");
        plus_btn.setOnAction((ev) -> {plusButtonAction();});
        plus_btn.setPrefSize(cells_parts_vbox.getWidth(), 5);
        cells_parts_vbox.getChildren().add(plus_btn);
    }
    
    private HBox makeCellPartPanel(String color, String exp) {
        final double ancho_vbox = cells_parts_vbox.getWidth();
        
        var box = new HBox();
        box.setMaxSize(cells_parts_vbox.getWidth(), colors_panel_height);
        var color_p = new ColoredPane(color);
        color_p.setStyle("-fx-background-color: #" + color + ";");
        color_p.setPrefSize(ancho_vbox * 0.75, MAX_SIZE);
        color_p.setOnMouseClicked((ev) -> {changingCellColorAction(color_p);});
        var percentaje_area = new TextField(String.valueOf(exp));
        percentaje_area.setPrefSize(ancho_vbox * 0.25, MAX_SIZE);
        percentaje_area.setOnAction((ev) -> {btn_apply.fire();});
        percentaje_area.setOnMouseClicked(e -> percentaje_area.selectAll());
        
        box.getChildren().addAll(color_p, percentaje_area);
        
        return box;
    }
    
    private void changingCellColorAction(ColoredPane pane) {
        //set index //??
        setChangingCellColor(pane, changing_pane_of_color == null);
    }
    
    private void setChangingCellColor(ColoredPane pane, boolean changing) {
        if (changing) {
            changing_pane_of_color = pane;
            var label = new Label("?");
            label.setPrefSize(pane.getWidth(), pane.getHeight());
            label.setAlignment(Pos.CENTER);
            label.setStyle("-fx-font-size: 15;");
            pane.getChildren().add(label);
        } else {
            changing_pane_of_color = null;
            var c = pane.getChildren();
            var removed = c.remove(c.size() - 1);
            if (removed instanceof Label == false)
                throw new AssertionError("setChangingCellColor eliminó un label incorrecto!");
        }
    }
    
    private void plusButtonAction() {
        int cells_parts_vbox_len = cells_parts_vbox.getChildren().size() - 1;
        String next_percentage;
        try {
            var fractions = new String[cells_parts_vbox_len];
            for (int i = 0; i < fractions.length; i++) {
                var hbox = (HBox)cells_parts_vbox.getChildren().get(i);
                var text_field = (TextField)hbox.getChildren().get(1);
                fractions[i] = text_field.getText();
            }
            next_percentage = Fractions.neededToReachOne(fractions);
        } catch (Exception e) {
            next_percentage = "0/3";
        }
        
        var last_pane_vbox = (HBox)cells_parts_vbox.getChildren().get(cells_parts_vbox_len - 1);
        var last_pane = (ColoredPane)last_pane_vbox.getChildren().get(0);
        
        var next_hbox = makeCellPartPanel(last_pane.getColor(), next_percentage);
        cells_parts_vbox.getChildren().add(cells_parts_vbox_len, next_hbox);
    }
    
    private Tabla[] getTablasEnOrden() {
        Tabla[] tablas = new Tabla[mapa_tablas.size()];
        int tablas_it = 0;
        for (var next_table_btn_node : tables_vbox.getChildren()) {
            String next_title = ((ToggleButton)next_table_btn_node).getText();
            tablas[tablas_it++] = mapa_tablas.get(next_title);
        }
        return tablas;
    }
        
    private String[][] getCellsNames() {
        final String[] names = {"A", "K", "Q", "J", "T", "9", "8", "7", "6", "5", "4", "3", "2"};
        var cell_name = new String[valores_distintos][valores_distintos];
        for (int i = 0; i < names.length; i++) {
            for (int j = 0; j < names.length; j++) {
                String next;
                if (i == j)
                    next = names[i] + names[j];
                else if (i > j)
                    next = names[j] + names[i] + "o";
                else
                    next = names[i] + names[j] + "s";
                cell_name[i][j] = next;
            }
        }
        
        return cell_name;
    }
    
    private ToggleButton getBotonAprensado() {
        return (ToggleButton)tablas_group.getSelectedToggle();
    }
    
    public static void showAlert(Alert.AlertType type, String title, String message) {
        var alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }
    
    private synchronized void showNotification(String message) { //Posiblemente poco robusto en multithreding?
        notifications_timer.cancel();
        notifications_timer.purge();
        messages_label.setText(message);
        
        var next_timer = new Timer(true);
        next_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {messages_label.setText("");});
            }
        }, notifications_def_time);
        
        notifications_timer = next_timer;
    }
    
    private static String readAnswer(String title, String content, String def_input) {
        TextInputDialog dialog = new TextInputDialog(def_input);
        dialog.getDialogPane().setContentText(content);
        dialog.setHeaderText(null);
        dialog.setTitle(title);
        var answer = dialog.showAndWait();
        if (!answer.isPresent() || answer.isEmpty())
            return null;
        return dialog.getEditor().getText();
    }
    
    private static boolean readConfirmation(String title, String content) {
        var alert = new Alert(Alert.AlertType.CONFIRMATION, content, ButtonType.NO, ButtonType.YES);
        alert.setHeaderText(null);
        alert.setTitle(title);
        //alert.setContentText(content);
        var result = alert.showAndWait();
        return result.get() == ButtonType.YES;
    }
    
    private static void saveNodeImageIn(Node node, String file_path) {
        var snapshot = node.snapshot(null, null);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", new File(file_path + ".png"));
        } catch (IOException e) {
            System.out.println("Error al guardar la imagen: " + e.toString());
        }
    }
    
    private void setModified(boolean value) {
        if (value)
            System.out.println("Modified!");
        else
            System.out.println("Not modofied ");
        file_is_modified = value;
    }
    
    //Tools for extern manipulation //Necesario?
    
    private boolean crearNuevaTablaStatic_first_time = true;
    public void crearNuevaTablaStatic(String titulo) {
        if (crearNuevaTablaStatic_first_time) {
            crearNuevaTablaStatic_first_time = false;
            this.getUsingTable().title = titulo;
            ((ToggleButton)tables_vbox.getChildren().get(0)).setText(titulo);
            return;
        }
        crearNuevoTablero(titulo);
    }
    
    public Tabla getUsingTable() {
        return mapa_tablas.get(tablas_group_aprensado.getText());
    }
    
}
