/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.net.URL;
import java.util.ArrayList;
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
//    @FXML
//    Label label;
    @FXML
    Label label00;
    @FXML
    Label label01;
    @FXML
    Label label02;
    @FXML
    Label label10;
    @FXML
    Label label12;
    @FXML
    Label label20;
    @FXML
    Label label21;
    @FXML
    Label label22;

    @FXML
    VBox vbox;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public BorderPane getBP() {
        return this.borderpane;
    }

    public ImageView getIV() {
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
    public GridPane getGridPane() {
        return this.gridpane;
    }
//    public Label getLabel(){
//        return this.label;
//    }

    public VBox getVBox() {
        return this.vbox;
    }

    public ArrayList getLabels() {
        ArrayList<Label> labels = new ArrayList();
        labels.add(label00);
        labels.add(label01);
        labels.add(label02);
        labels.add(label10);
        labels.add(label12);
        labels.add(label20);
        labels.add(label21);
        labels.add(label22);
        return labels;
    }
}
