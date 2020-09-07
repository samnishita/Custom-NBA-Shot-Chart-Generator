/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainapp;

import controllers.SimpleController;
import java.awt.Button;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
public class Main extends Application {

    private FXMLLoader loader;
    private SimpleController sc;

    @Override
    public void start(Stage stage) throws Exception {
        loader = new FXMLLoader(getClass().getResource("/fxml/simplegrid.fxml"));
        Parent root = (Parent) loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Dynamic NBA Shot Charts");
        stage.setMinHeight(650);
        stage.setMinWidth(900);

        stage.show();
        sc = loader.getController();
        GridPane gp = sc.getGridPane();
        VBox vbox = sc.getVBox();
        vbox.maxWidthProperty().bind(scene.widthProperty());
        vbox.maxHeightProperty().bind(scene.heightProperty());
        gp.maxHeightProperty().bind(vbox.maxHeightProperty());
        gp.maxWidthProperty().bind(vbox.maxWidthProperty());
        ImageView iv = sc.getIV();
        iv.setFitHeight(1776);
        iv.setFitWidth(1890);
        iv.fitWidthProperty().bind(scene.widthProperty().divide(1.7));
        iv.fitHeightProperty().bind(scene.heightProperty().divide(1.7));
        iv.setPreserveRatio(true);

        vbox.maxWidthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                System.out.println("VBOX Width: " + vbox.getWidth());
                System.out.println("VBOX MAX Width: " + vbox.getMaxWidth());
                System.out.println("Scene Width: " + scene.getWidth());
                System.out.println("GP Width: " + gp.getWidth());
                System.out.println("");
            }
        });
        vbox.maxHeightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                System.out.println("VBOX Height: " + vbox.getHeight());
                System.out.println("VBOX MAX Height: " + vbox.getMaxHeight());
                System.out.println("Scene Height: " + scene.getHeight());
                System.out.println("GP Height: " + gp.getHeight());
                System.out.println("");
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

}
