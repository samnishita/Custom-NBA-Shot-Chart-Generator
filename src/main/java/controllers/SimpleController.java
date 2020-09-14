/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import javafx.scene.paint.Color;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
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
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import mainapp.MissedShotIcon;
import mainapp.Shot;

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
//    private ArrayList<Shot> shotsList = new ArrayList();
    private ArrayList<Circle> makes;
    private ArrayList<Line> misses;
    private BigDecimal origWidth = new BigDecimal("500");
    private BigDecimal origHeight = new BigDecimal("470");
    private BigDecimal shotmadeRadius = new BigDecimal("5");
    private BigDecimal shotmissStartEnd = new BigDecimal("3");
    private BigDecimal shotLineThickness = new BigDecimal("1.5");
    private BigDecimal transY = new BigDecimal("-180");
    private HashMap<Shot, Object> allShots = new HashMap();

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
                plotResults();
//                int count = 0;
//                while (rs.next()) {
//                    count++;
//                }
//                System.out.println("count: "+count);
            } catch (SQLException ex) {
                Logger.getLogger(SimpleController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        imageview.fitHeightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                resizeShots();

//                System.out.println(shotmade.getRadius());
            }
        });
        imageview.fitWidthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                resizeShots();
//                System.out.println(shotmade.getRadius());
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
        ArrayList<Node> toRemove = new ArrayList();
        for (Node each : gridpane.getChildren()) {
            if (each.getClass().equals(Circle.class) || each.getClass().equals(Line.class)) {
                toRemove.add(each);
            }
        }
        for (Node each : toRemove) {
            gridpane.getChildren().remove(each);
        }
        return conn2.prepareStatement("SELECT * FROM " + playerName[2].replaceAll("[^A-Za-z0-9]", "") + "_" + playerName[1].replaceAll("[^A-Za-z0-9]", "") + "_" + playerName[0] + "_" + year.substring(0, 4) + "_" + year.substring(5, 7) + "_" + season).executeQuery();
    }

    private void plotResults() throws SQLException {

        allShots = new HashMap();
        Circle circle;
        MissedShotIcon msi;
        BigDecimal xBig = new BigDecimal("0");
        BigDecimal yBig = new BigDecimal("0");
        while (rs.next()) {
            Shot shot = new Shot(rs.getInt("x"), rs.getInt("y"), rs.getInt("distance"), rs.getInt("make"), rs.getString("shottype"), rs.getString("playtype"));
            xBig = BigDecimal.valueOf(rs.getInt("x"));
            yBig = BigDecimal.valueOf(rs.getInt("y") - 180);
            if (rs.getInt("make") == 1) {
                circle = new Circle(imageview.getLayoutBounds().getHeight() * shotmadeRadius.divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue());
                circle.setFill(Color.TRANSPARENT);
                circle.setTranslateX(imageview.getLayoutBounds().getHeight() * xBig.divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue());
                circle.setTranslateY(imageview.getLayoutBounds().getHeight() * yBig.divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue());
                circle.setStrokeWidth(imageview.getLayoutBounds().getHeight() * shotLineThickness.divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue());
                circle.setStroke(Color.LIMEGREEN);
                allShots.put(shot, circle);
            } else {
                msi = new MissedShotIcon(xBig.divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue(),
                        yBig.divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue(),
                        imageview.getLayoutBounds().getHeight(),
                        shotmissStartEnd.divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue(),
                        shotLineThickness.divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue());
                allShots.put(shot, msi);
            }

        }
        System.out.println("allShots Size: " + allShots.size());
        for (Shot each : allShots.keySet()) {
            if (each.getMake() == 0) {
                MissedShotIcon msiTemp = (MissedShotIcon) allShots.get(each);
                gridpane.add(msiTemp.getLine1(), 1, 1);
                gridpane.add(msiTemp.getLine2(), 1, 1);

            } else {
                gridpane.add((Circle) allShots.get(each), 1, 1);
            }
        }

    }

    private void resizeShots() {
        double height = imageview.getLayoutBounds().getHeight();
        double scaledLineThickness = shotLineThickness.divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue();
        double scaledLineLength = shotmissStartEnd.divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue();
        shotmade.setTranslateY(height * transY.divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue());
        shotmade.setRadius(height * shotmadeRadius.divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue());
        shotmade.setStrokeWidth(height * scaledLineThickness);
        shotmissedline1.setStrokeWidth(height * scaledLineThickness);
        shotmissedline2.setStrokeWidth(height * scaledLineThickness);
        shotmissedline1.setStartX((height * scaledLineLength));
        shotmissedline2.setStartX((height * scaledLineLength));
        shotmissedline1.setEndX((height * -1 * scaledLineLength));
        shotmissedline2.setEndX((height * -1 * scaledLineLength));
        shotmissedline1.setStartY((height * -1 * scaledLineLength));
        shotmissedline2.setStartY((height * -1 * scaledLineLength));
        shotmissedline1.setEndY((height * scaledLineLength));
        shotmissedline2.setEndY((height * scaledLineLength));
        shotmissedline2.setRotate(180);
        shotmissedline1.setTranslateX(height * 0.10638297872);// 50/470
        shotmissedline2.setTranslateX(height * 0.10638297872);
        shotmissedline1.setTranslateY(height * 0.10638297872);
        shotmissedline2.setTranslateY(height * 0.10638297872);
        if (!allShots.keySet().isEmpty()) {
            for (Shot each : allShots.keySet()) {
                Circle circle;
                Line line1;
                Line line2;
                if (each.getMake() == 1) {
                    circle = (Circle) allShots.get(each);
                    circle.setTranslateX(height * BigDecimal.valueOf(each.getX()).divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue());
                    circle.setTranslateY(height * BigDecimal.valueOf(each.getY() - 180).divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue());
                    circle.setRadius(height * shotmadeRadius.divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue());
                    circle.setStrokeWidth(height * scaledLineThickness);
                } else {
                    MissedShotIcon msi = (MissedShotIcon) allShots.get(each);
                    line1 = msi.getLine1();
                    line2 = msi.getLine2();
                    line1.setStrokeWidth(height * scaledLineThickness);
                    line2.setStrokeWidth(height * scaledLineThickness);
                    line1.setStartX((height * scaledLineLength));
                    line2.setStartX((height * scaledLineLength));
                    line1.setEndX((height * -1 * scaledLineLength));
                    line2.setEndX((height * -1 * scaledLineLength));
                    line1.setStartY((height * -1 * scaledLineLength));
                    line2.setStartY((height * -1 * scaledLineLength));
                    line1.setEndY((height * scaledLineLength));
                    line2.setEndY((height * scaledLineLength));
                    line1.setTranslateX(height * BigDecimal.valueOf(each.getX()).divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue());// 50/470
                    line2.setTranslateX(height * BigDecimal.valueOf(each.getX()).divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue());
                    line1.setTranslateY(height * BigDecimal.valueOf(each.getY() - 180).divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue());
                    line2.setTranslateY(height * BigDecimal.valueOf(each.getY() - 180).divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue());
                    line2.setRotate(180);

                }
            }
        }
    }
}
