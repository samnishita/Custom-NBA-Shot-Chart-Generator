/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author samnishita
 */
public class SimpleController implements Initializable {

    @FXML
    ImageView imageview;
    @FXML
    BorderPane borderpane;
//    @FXML
//    Pane pane;
//    @FXML
//    Button buttonspace;
//    @FXML
//    ImageView ivspace;
    @FXML
    GridPane gridpane;
    @FXML
    Label label;
    @FXML
    VBox vbox;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public BorderPane getBP() {
        return this.borderpane;
    }

    public ImageView getIV(){
        return this.imageview;
    }
    
//    public Pane getPane(){
//        return this.pane;
//    }
//    public Button getButtonSpace(){
//        return this.buttonspace;
//    }
//    public ImageView getIVSpace(){
//        return this.ivspace;
//    }
    public GridPane getGridPane(){
        return this.gridpane;
    }
    public Label getLabel(){
        return this.label;
    }
    public VBox getVBox(){
        return this.vbox;
    }
}
