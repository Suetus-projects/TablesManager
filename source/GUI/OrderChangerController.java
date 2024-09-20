/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package GUI;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author dan_deb
 */
public class OrderChangerController implements Initializable {

    @FXML
    private Button btn_up;
    @FXML
    private Button btn_down;
    @FXML
    private ScrollPane scrollpane_buttons;
    @FXML
    private Button btn_ok;
    @FXML
    private Button cancel_ok;
    
    private VBox scrollpane_vbox = null;
    private final ToggleGroup buttons_group = new ToggleGroup();
    
    private String[] final_order = null;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        scrollpane_vbox = new VBox();
        scrollpane_vbox.setPrefWidth(scrollpane_buttons.getPrefWidth() - 1);
        scrollpane_buttons.setContent(scrollpane_vbox);
    }  
    
    public void setContent(String[] names) {
        for (String name : names) {
            System.out.println("!!");
            var next = new ToggleButton(name);
            next.setToggleGroup(buttons_group);
            next.setPrefWidth(scrollpane_vbox.getPrefWidth() - 16);
            next.setMinWidth(scrollpane_vbox.getPrefWidth() - 16);
            next.setMaxWidth(scrollpane_vbox.getPrefWidth() - 16);
            scrollpane_vbox.getChildren().add(next);
        }
    }
    
    public String[] getResult() {
        return final_order;
    }
    
    @FXML
    private void btn_up_action(ActionEvent event) {
        movePressedXPlaces(-1);
    }

    @FXML
    private void btn_down_action(ActionEvent event) {
        movePressedXPlaces(1);
    }
    
    private void movePressedXPlaces(int x) {
        var pressed = (ToggleButton)buttons_group.getSelectedToggle();
        if (pressed == null)
            return;
        var list = scrollpane_vbox.getChildren();
        int index = list.indexOf(pressed);
        int desired_index = index + x;
        if (desired_index < 0 || desired_index >= list.size()) 
            return;
        swapNames(index, desired_index);
        ((ToggleButton)scrollpane_vbox.getChildren().get(desired_index)).fire();
    }

    @FXML
    private void btn_ok_action(ActionEvent event) {
        final_order = getContent();
        this.close();
    }

    @FXML
    private void btn_cancel_action(ActionEvent event) {
        this.close();
    }
    
    private void close() {
        ((Stage)btn_ok.getScene().getWindow()).close();
    }
    
    private String[] getContent() {
        var result = new String[scrollpane_vbox.getChildren().size()];
        for (int i = 0; i < result.length; ++i) {
            var as_tb = (ToggleButton)scrollpane_vbox.getChildren().get(i);
            result[i] = as_tb.getText();
        }
        return result;
    }
    
    private void swapNames(int x, int y) {
        var el1 = (ToggleButton)scrollpane_vbox.getChildren().get(x);
        var el2 = (ToggleButton)scrollpane_vbox.getChildren().get(y);
        
        var swapped = el1.getText();
        el1.setText(el2.getText());
        el2.setText(swapped);
    }
}
