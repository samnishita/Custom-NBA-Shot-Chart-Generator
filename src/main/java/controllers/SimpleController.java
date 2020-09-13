/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

/**
 *
 * @author samnishita
 */
public class SimpleController implements Initializable {
    
    private String username = "root";
    private String password = "BoneAppleTea2020";
    private String jdbc3 = "jdbc:mysql://localhost:3306/nbaplayerinfo?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    private Connection conn3;
    private Connection conn2;
    private String jdbc2 = "jdbc:mysql://localhost:3306/nbashots?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    private String firstname;
    private String lastname;
    private HashMap<String, String[]> nameHash;
    private ResultSet rs;
    private ArrayList shotsList;
    
    @FXML
    ImageView imageview;
    @FXML
    BorderPane borderpane;
    @FXML
    GridPane gridpane;
    @FXML
    VBox vbox;
    @FXML
    ComboBox yearcombo;
    @FXML
    ComboBox playercombo;
    @FXML
    ComboBox seasoncombo;
    @FXML
    Button searchbutton;
    @FXML
    Circle shotmade;
    @FXML
    Line shotmissedline1;
    @FXML
    Line shotmissedline2;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
//        ArrayList<String> players = new ArrayList();
        nameHash = new HashMap();
        try {
            conn3 = DriverManager.getConnection(jdbc3, username, password);
            conn2 = DriverManager.getConnection(jdbc2, username, password);
            String[] nameArray = new String[3];
            ResultSet rs = conn3.prepareStatement("SELECT lastname,firstname, id FROM player_all_data").executeQuery();
            while (rs.next()) {
//                players.add((rs.getString("firstname") + " " + rs.getString("lastname")).trim());
                nameArray = new String[3];
                nameArray[0] = rs.getInt("id") + "";
                nameArray[1] = rs.getString("firstname");
                nameArray[2] = rs.getString("lastname");
                nameHash.put((rs.getString("firstname") + " " + rs.getString("lastname")).trim(), nameArray);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        yearcombo.setItems(FXCollections.observableArrayList(makeYears()));
//        playercombo.setItems(FXCollections.observableArrayList(players));
        playercombo.setItems(FXCollections.observableArrayList(nameHash.keySet()));
        ArrayList<String> seasons = new ArrayList();
        seasons.add("Preseason");
        seasons.add("Regular Season");
        seasons.add("Playoffs");
        seasoncombo.setItems(FXCollections.observableArrayList(seasons));
        this.searchbutton.setOnMouseClicked((Event t) -> {
            try {
//            System.out.println(this.yearcombo.getValue().toString());
//            System.out.println(this.playercombo.getValue().toString());
//            System.out.println(nameHash.get(this.playercombo.getValue().toString())[2]);
//            System.out.println(this.seasoncombo.getValue().toString());

                rs = doSimpleSearch(this.yearcombo.getValue().toString(), nameHash.get(this.playercombo.getValue().toString()), this.seasoncombo.getValue().toString().replaceAll(" ", ""));
                int count = 0;
                while (rs.next()) {
                    count++;
                }
                System.out.println(count);
            } catch (SQLException ex) {
                Logger.getLogger(SimpleController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        imageview.fitHeightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                shotmade.setTranslateY(imageview.getLayoutBounds().getHeight()*(-0.3829787234));
                System.out.println(shotmade.getTranslateY());
            }
        });
        imageview.fitWidthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                shotmade.setTranslateY(imageview.getLayoutBounds().getHeight()*(-0.3829787234));
                System.out.println(shotmade.getTranslateY());
            }
        });
        
    }
    
    public BorderPane getBP() {
        return this.borderpane;
    }
    
    public ImageView getIV() {
        return this.imageview;
    }
    
    public GridPane getGridPane() {
        return this.gridpane;
    }
    
    public VBox getVBox() {
        return this.vbox;
    }
    
    private ArrayList makeYears() {
        int year = 2019;
        ArrayList<String> years = new ArrayList(30);
        int subYear;
        String subYearString;
        while (year >= 1996) {
            subYear = (year - 1899) % 100;
            if (subYear < 10) {
                subYearString = "0" + subYear;
            } else {
                subYearString = "" + subYear;
            }
            years.add(year + "-" + subYearString);
            year--;
        }
        
        return years;
    }
    
    protected static Connection getConnection(String url) throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection(url);
        } catch (ClassNotFoundException ex) {
        }
        return null;
    }
    
    private ResultSet doSimpleSearch(String year, String[] playerName, String season) throws SQLException {
        return conn2.prepareStatement("SELECT * FROM " + playerName[2].replaceAll("[^A-Za-z0-9]", "") + "_" + playerName[1].replaceAll("[^A-Za-z0-9]", "") + "_" + playerName[0] + "_" + year.substring(0, 4) + "_" + year.substring(5, 7) + "_" + season).executeQuery();
    }
    
    private void plotResults(ArrayList shotsList) {
        
    }
    
    private void createX() {
        
    }
    
}
