/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainapp;

import controllers.SimpleController;
import java.awt.Button;
import javafx.application.Application;
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
//        loader = new FXMLLoader(getClass().getResource("/fxml/simple.fxml"));
//        Parent root = (Parent) loader.load();
//        Scene scene = new Scene(root);
//        stage.setScene(scene);
//        stage.setTitle("Dynamic NBA Shot Charts");
//        stage.setMinHeight(650);
//        stage.setMinWidth(800);
//        stage.show();
//        sc = loader.getController();
//        BorderPane bp = sc.getBP();
//        System.out.println(bp + ": " + bp.getWidth() + ", " + bp.getHeight());
//        ImageView iv = sc.getIV();
//        System.out.println(iv + ": " + iv.getFitWidth() + ", " + iv.getFitHeight());
//        iv.setFitHeight(1776);
//        iv.setFitWidth(1890);
//        iv.fitHeightProperty().bind(bp.heightProperty().divide(1.6));
//        iv.setPreserveRatio(true);
//        ImageView ivspace = sc.getIVSpace();
//        ivspace.setFitWidth(600);
//        ivspace.fitWidthProperty().bind(bp.widthProperty().multiply(2));
//        ivspace.setPreserveRatio(true);

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
        System.out.println(vbox);
        vbox.prefWidthProperty().bind(scene.widthProperty());
        vbox.prefHeightProperty().bind(scene.heightProperty());
        ImageView iv = sc.getIV();
        iv.setFitHeight(1776);
        iv.setFitWidth(1890);
        iv.fitWidthProperty().bind(gp.widthProperty().divide(1.75));
        iv.fitHeightProperty().bind(gp.heightProperty().divide(1.2));
        iv.setPreserveRatio(true);
    }

    public static void main(String[] args) {
        launch(args);
    }

}
