/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import mainapp.Main;

/**
 *
 * @author samnishita
 */
public class AdvancedController implements Initializable {

    @FXML
    private Button button;

    private Connection conn3;
    private Connection conn2;
    private ResourceBundle reader = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            reader = ResourceBundle.getBundle("dbconfig");
            conn3 = DriverManager.getConnection(reader.getString("db.urlplayer"), reader.getString("db.username"), reader.getString("db.password"));
            conn2 = DriverManager.getConnection(reader.getString("db.urlshot"), reader.getString("db.username"), reader.getString("db.password"));

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        button.setOnMouseClicked((Event t) -> {
            try {
                long startTime = System.nanoTime();
                ResultSet rs = conn2.prepareStatement("SELECT x,y FROM all_shots WHERE playerfirst = 'Kobe' AND playerlast = 'Bryant' AND seasontype = 'Regular Season'").executeQuery();
                long finishTime = System.nanoTime();
                double timeElapsed = (finishTime - startTime) / 1000000000.0; //seconds
                System.out.println(timeElapsed + " seconds to retrieve data");
                int counter = 0;
                while (rs.next()) {
//                    System.out.println(rs.getInt("distance"));
                    counter++;
                }
                System.out.println("total count: " + counter);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

}
