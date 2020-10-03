/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.io.IOException;
import javafx.scene.paint.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import mainapp.Main;
import mainapp.MissedShotIcon;
import mainapp.Shot;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author samnishita
 */
public class SimpleController implements Initializable {

    private Connection conn3;
    private Connection conn2;
    private String firstname;
    private String lastname;
    private LinkedHashMap<String, String[]> nameHash;
//    private ResultSet rs;
//    private ArrayList<Shot> shotsList = new ArrayList();
    private ArrayList<Circle> makes;
    private ArrayList<Line> misses;
    private BigDecimal origWidth = new BigDecimal("500");
    private BigDecimal origHeight = new BigDecimal("470");
    private BigDecimal shotmadeRadius = new BigDecimal("5");
    private BigDecimal shotmissStartEnd = new BigDecimal("3");
    private BigDecimal shotLineThickness = new BigDecimal("2");
    private BigDecimal transY = new BigDecimal("-180");
    private LinkedHashMap<Shot, Object> allShots = new LinkedHashMap();
    private HashMap<Integer, String> activePlayers;
    private int comboFontSize = 15;
    private int statGridFontSize = 20;
    private String previousYear;
    private String previousPlayer;
    private String previousSeason;
    private ResourceBundle reader = null;

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
//    @FXML
//    Circle shotmade;
//    @FXML
//    Line shotmissedline1;
//    @FXML
//    Line shotmissedline2;
    @FXML
    Label errorlabel;
    @FXML
    VBox searchvbox;
    @FXML
    Label introlabel;
    @FXML
    Label fg;
    @FXML
    Label fgfrac;
    @FXML
    Label fgperc;
    @FXML
    Label twopoint;
    @FXML
    Label twopointfrac;
    @FXML
    Label twopointperc;
    @FXML
    Label threepoint;
    @FXML
    Label threepointfrac;
    @FXML
    Label threepointperc;
    @FXML
    GridPane shotgrid;
    @FXML
    Label titlelabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        nameHash = new LinkedHashMap();
        this.errorlabel.setVisible(false);
        this.introlabel.prefWidthProperty().bind(this.gridpane.widthProperty().divide(4));
        this.introlabel.setStyle("-fx-font: " + this.comboFontSize * 1.5 + "px \"Serif\";");
        this.yearcombo.prefWidthProperty().bind(this.gridpane.widthProperty().divide(5));
        this.yearcombo.setStyle("-fx-font: " + this.comboFontSize + "px \"Serif\";");
        this.playercombo.prefWidthProperty().bind(this.gridpane.widthProperty().divide(5));
        this.playercombo.setStyle("-fx-font: " + this.comboFontSize + "px \"Serif\";");
        this.seasoncombo.prefWidthProperty().bind(this.gridpane.widthProperty().divide(5));
        this.seasoncombo.setStyle("-fx-font: " + this.comboFontSize + "px \"Serif\";");
//        this.searchbutton.prefWidthProperty().bind(this.gridpane.widthProperty().divide(6));
        this.searchbutton.setStyle("-fx-font: " + this.comboFontSize + "px \"Serif\";");
        VBox.setMargin(this.introlabel, new Insets(10, 0, 0, 0));
        VBox.setMargin(this.yearcombo, new Insets(20, 0, 0, 0));
        VBox.setMargin(this.playercombo, new Insets(20, 0, 0, 0));
        VBox.setMargin(this.seasoncombo, new Insets(20, 0, 0, 0));
        VBox.setMargin(this.searchbutton, new Insets(20, 0, 0, 0));
        this.shotgrid.maxWidthProperty().bind(this.gridpane.widthProperty().divide(3));
        this.shotgrid.maxHeightProperty().bind(this.gridpane.heightProperty().divide(5.25));
        try {
            reader = ResourceBundle.getBundle("dbconfig");
            conn3 = DriverManager.getConnection(reader.getString("db.urlplayer"), reader.getString("db.username"), reader.getString("db.password"));
            conn2 = DriverManager.getConnection(reader.getString("db.urlshot"), reader.getString("db.username"), reader.getString("db.password"));
            String[] nameArray = new String[3];
            ResultSet rs = conn3.prepareStatement("SELECT lastname,firstname, id FROM player_relevant_data").executeQuery();
            while (rs.next()) {
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
        ArrayList<String> fullNames = new ArrayList();
        for (String each : nameHash.keySet()) {
            fullNames.add(each);
        }
        Collections.sort(fullNames);
        this.yearcombo.setValue("2019-20");
        this.playercombo.setValue("Aaron Gordon");
        this.seasoncombo.setValue("Regular Season");
        this.activePlayers = new HashMap();

        setPlayerComboBox();

//        playercombo.setItems(FXCollections.observableArrayList());
        ArrayList<String> seasons = new ArrayList();
        seasons.add("Preseason");
        seasons.add("Regular Season");
        seasons.add("Playoffs");
        seasoncombo.setItems(FXCollections.observableArrayList(seasons));

        setSeasonsComboBox();

        this.searchbutton.setOnMouseClicked((Event t) -> {
            this.errorlabel.setVisible(false);
            try {
//                rs = doSimpleSearch(this.yearcombo.getValue().toString(), nameHash.get(this.playercombo.getValue().toString()), this.seasoncombo.getValue().toString().replaceAll(" ", ""));
                this.previousYear = this.yearcombo.getValue().toString();
                this.previousPlayer = this.playercombo.getValue().toString();
                this.previousSeason = this.seasoncombo.getValue().toString();
                JSONArray jsonArray = doSimpleSearch();
                plotResults(jsonArray);
            } catch (SQLException ex) {
                ex.printStackTrace();
            } catch (NullPointerException ex) {
                this.errorlabel.setText("Please try again");
                this.errorlabel.setVisible(true);
            } catch (IOException ex) {
                Logger.getLogger(SimpleController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        imageview.fitHeightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                resizeShots();
                double font = new BigDecimal(comboFontSize).multiply(new BigDecimal(imageview.getLayoutBounds().getHeight())).divide(new BigDecimal("550"), 6, RoundingMode.HALF_UP).doubleValue();
                double fontGrid = new BigDecimal(statGridFontSize).multiply(new BigDecimal(imageview.getLayoutBounds().getHeight())).divide(new BigDecimal("550"), 6, RoundingMode.HALF_UP).doubleValue();

                introlabel.setStyle("-fx-font: " + font * 1.5 + "px \"Serif\";");
                yearcombo.setStyle("-fx-font: " + font + "px \"Serif\";");
                playercombo.setStyle("-fx-font: " + font + "px \"Serif\";");
                seasoncombo.setStyle("-fx-font: " + font + "px \"Serif\";");
                searchbutton.setStyle("-fx-font: " + font + "px \"Serif\";");
                fg.setStyle("-fx-font: " + font * 2.5 + "px \"Tahoma Bold\";");
                fgfrac.setStyle("-fx-font: " + fontGrid + "px \"Tahoma Bold\";");
                fgperc.setStyle("-fx-font: " + fontGrid + "px \"Tahoma Bold\";");
                twopoint.setStyle("-fx-font: " + font * 2.5 + "px \"Tahoma Bold\";");
                twopointfrac.setStyle("-fx-font: " + fontGrid + "px \"Tahoma Bold\";");
                twopointperc.setStyle("-fx-font: " + fontGrid + "px \"Tahoma Bold\";");
                threepoint.setStyle("-fx-font: " + font * 2.5 + "px \"Tahoma Bold\";");
                threepointfrac.setStyle("-fx-font: " + fontGrid + "px \"Tahoma Bold\";");
                threepointperc.setStyle("-fx-font: " + fontGrid + "px \"Tahoma Bold\";");

                VBox.setMargin(introlabel, new Insets(new BigDecimal(imageview.getLayoutBounds().getHeight()).multiply(new BigDecimal("20")).divide(new BigDecimal("475"), 6, RoundingMode.HALF_UP).doubleValue(), 0, 0, 0));
                VBox.setMargin(yearcombo, new Insets(20, 0, 0, 0));
                VBox.setMargin(playercombo, new Insets(20, 0, 0, 0));
                VBox.setMargin(seasoncombo, new Insets(20, 0, 0, 0));
                VBox.setMargin(searchbutton, new Insets(20, 0, 0, 0));
            }
        });
        imageview.fitWidthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                resizeShots();
                double font = new BigDecimal(comboFontSize).multiply(new BigDecimal(imageview.getLayoutBounds().getHeight())).divide(new BigDecimal("550"), 6, RoundingMode.HALF_UP).doubleValue();
                double fontGrid = new BigDecimal(statGridFontSize).multiply(new BigDecimal(imageview.getLayoutBounds().getHeight())).divide(new BigDecimal("550"), 6, RoundingMode.HALF_UP).doubleValue();
                introlabel.setStyle("-fx-font: " + font * 1.5 + "px \"Serif\";");
                yearcombo.setStyle("-fx-font: " + font + "px \"Serif\";");
                playercombo.setStyle("-fx-font: " + font + "px \"Serif\";");
                seasoncombo.setStyle("-fx-font: " + font + "px \"Serif\";");
                searchbutton.setStyle("-fx-font: " + font + "px \"Serif\";");
                fg.setStyle("-fx-font: " + font * 2.5 + "px \"Tahoma Bold\";");
                fgfrac.setStyle("-fx-font: " + fontGrid + "px \"Tahoma Bold\";");
                fgperc.setStyle("-fx-font: " + fontGrid + "px \"Tahoma Bold\";");
                twopoint.setStyle("-fx-font: " + font * 2.5 + "px \"Tahoma Bold\";");
                twopointfrac.setStyle("-fx-font: " + fontGrid + "px \"Tahoma Bold\";");
                twopointperc.setStyle("-fx-font: " + fontGrid + "px \"Tahoma Bold\";");
                threepoint.setStyle("-fx-font: " + font * 2.5 + "px \"Tahoma Bold\";");
                threepointfrac.setStyle("-fx-font: " + fontGrid + "px \"Tahoma Bold\";");
                threepointperc.setStyle("-fx-font: " + fontGrid + "px \"Tahoma Bold\";");

                VBox.setMargin(introlabel, new Insets(new BigDecimal(imageview.getLayoutBounds().getHeight()).multiply(new BigDecimal("20")).divide(new BigDecimal("475"), 6, RoundingMode.HALF_UP).doubleValue(), 0, 0, 0));
                VBox.setMargin(yearcombo, new Insets(30, 0, 0, 0));
                VBox.setMargin(playercombo, new Insets(30, 0, 0, 0));
                VBox.setMargin(seasoncombo, new Insets(30, 0, 0, 0));
                VBox.setMargin(searchbutton, new Insets(20, 0, 0, 0));
//                System.out.println(shotmade.getRadius());
            }
        });
        this.yearcombo.setOnAction((Event t) -> {
            setPlayerComboBox();
        });
        this.playercombo.setOnAction((Event t) -> {
            setSeasonsComboBox();
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

//    private ResultSet doSimpleSearch(String year, String[] playerName, String season) throws SQLException, IOException {
//        ArrayList<Node> toRemove = new ArrayList();
//        for (Node each : gridpane.getChildren()) {
//            if (each.getClass().equals(Circle.class) || each.getClass().equals(Line.class)) {
//                toRemove.add(each);
//            }
//        }
//        for (Node each : toRemove) {
//            gridpane.getChildren().remove(each);
//        }
//        System.out.println("call get jsonarray method");
//        JSONArray jsonArray = getDataAsJSON();
//        JSONObject jsonObj=null;
//        for (int i=0;i<jsonArray.length();i++){
//             jsonObj = jsonArray.getJSONObject(i);
//             System.out.println("counter: "+ jsonObj.getInt("counter"));
//             System.out.println("last name: "+ jsonObj.getString("playerlast"));
//        }
//        return conn2.prepareStatement("SELECT * FROM " + playerName[2].replaceAll("[^A-Za-z0-9]", "") + "_" + playerName[1].replaceAll("[^A-Za-z0-9]", "") + "_" + playerName[0] + "_" + year.substring(0, 4) + "_" + year.substring(5, 7) + "_" + season).executeQuery();
//    }
    private JSONArray doSimpleSearch() throws SQLException, IOException {
        ArrayList<Node> toRemove = new ArrayList();
        for (Node each : gridpane.getChildren()) {
            if (each.getClass().equals(Circle.class) || each.getClass().equals(Line.class)) {
                toRemove.add(each);
            }
        }
        for (Node each : toRemove) {
            gridpane.getChildren().remove(each);
        }
        System.out.println("call get jsonarray method");
        JSONArray jsonArray = getDataAsJSON();
        return jsonArray;
    }

//    private void plotResults() throws SQLException {
//        this.fgfrac.setText("--");
//        this.fgperc.setText("--");
//        this.twopointfrac.setText("--");
//        this.twopointperc.setText("--");
//        this.threepointfrac.setText("--");
//        this.threepointperc.setText("--");
//        int countMade = 0;
//        int countTotal = 0;
//        int count2pMade = 0;
//        int count2pTotal = 0;
//        int count3pMade = 0;
//        int count3pTotal = 0;
//        allShots = new LinkedHashMap();
//        Circle circle;
//        MissedShotIcon msi;
//        BigDecimal xBig = new BigDecimal("0");
//        BigDecimal yBig = new BigDecimal("0");
//        while (rs.next()) {
//
//            if (rs.getString("shottype").equals("3PT Field Goal")) {
//                count3pTotal++;
//                if (rs.getInt("make") == 1) {
//                    count3pMade++;
//                    countMade++;
//                }
//            } else if (rs.getString("shottype").equals("2PT Field Goal")) {
//                count2pTotal++;
//                if (rs.getInt("make") == 1) {
//                    count2pMade++;
//                    countMade++;
//                }
//            }
//            countTotal++;
//
//            Shot shot = new Shot(rs.getInt("x"), rs.getInt("y"), rs.getInt("distance"), rs.getInt("make"), rs.getString("shottype"), rs.getString("playtype"));
//            xBig = BigDecimal.valueOf(rs.getInt("x"));
//            yBig = BigDecimal.valueOf(rs.getInt("y") - 180);
//            if (rs.getInt("make") == 1) {
//                circle = new Circle(imageview.getLayoutBounds().getHeight() * shotmadeRadius.divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue());
//                circle.setFill(Color.TRANSPARENT);
//                circle.setTranslateX(imageview.getLayoutBounds().getHeight() * xBig.divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue());
//                circle.setTranslateY(imageview.getLayoutBounds().getHeight() * yBig.divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue());
//                circle.setStrokeWidth(imageview.getLayoutBounds().getHeight() * shotLineThickness.divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue());
//                circle.setStroke(Color.LIMEGREEN);
//                allShots.put(shot, circle);
//            } else {
//                msi = new MissedShotIcon(xBig.divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue(),
//                        yBig.divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue(),
//                        imageview.getLayoutBounds().getHeight(),
//                        shotmissStartEnd.divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue(),
//                        shotLineThickness.divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue());
//                allShots.put(shot, msi);
//            }
//
//        }
//        System.out.println("allShots Size: " + allShots.size());
//        for (Shot each : allShots.keySet()) {
//            if (each.getY() > 410) {
//                continue;
//            }
//            if (each.getMake() == 0) {
//                MissedShotIcon msiTemp = (MissedShotIcon) allShots.get(each);
//                gridpane.add(msiTemp.getLine1(), 1, 1);
//                gridpane.add(msiTemp.getLine2(), 1, 1);
//
//            } else {
//                gridpane.add((Circle) allShots.get(each), 1, 1);
//            }
//        }
//
//        this.fgfrac.setText(countMade + "/" + countTotal);
//        if (countTotal == 0) {
//            this.fgperc.setText("--");
//        } else {
//            this.fgperc.setText(new BigDecimal((double) countMade / countTotal * 100).setScale(2, RoundingMode.HALF_UP) + "%");
//        }
//        this.twopointfrac.setText(count2pMade + "/" + count2pTotal);
//        if (count2pTotal == 0) {
//            this.twopointperc.setText("--");
//        } else {
//            this.twopointperc.setText(new BigDecimal((double) count2pMade / count2pTotal * 100).setScale(2, RoundingMode.HALF_UP) + "%");
//        }
//        this.threepointfrac.setText(count3pMade + "/" + count3pTotal);
//        if (count3pTotal == 0) {
//            this.threepointperc.setText("--");
//        } else {
//            this.threepointperc.setText(new BigDecimal((double) count3pMade / count3pTotal * 100).setScale(2, RoundingMode.HALF_UP) + "%");
//        }
//    }
    private void plotResults(JSONArray jsonArray) throws SQLException {
        this.fgfrac.setText("--");
        this.fgperc.setText("--");
        this.twopointfrac.setText("--");
        this.twopointperc.setText("--");
        this.threepointfrac.setText("--");
        this.threepointperc.setText("--");
        int countMade = 0;
        int countTotal = 0;
        int count2pMade = 0;
        int count2pTotal = 0;
        int count3pMade = 0;
        int count3pTotal = 0;
        allShots = new LinkedHashMap();
        Circle circle;
        MissedShotIcon msi;
        BigDecimal xBig = new BigDecimal("0");
        BigDecimal yBig = new BigDecimal("0");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject eachShot = jsonArray.getJSONObject(i);
            if (eachShot.getString("shottype").equals("3PT Field Goal")) {
                count3pTotal++;
                if (eachShot.getInt("make") == 1) {
                    count3pMade++;
                    countMade++;
                }
            } else if (eachShot.getString("shottype").equals("2PT Field Goal")) {
                count2pTotal++;
                if (eachShot.getInt("make") == 1) {
                    count2pMade++;
                    countMade++;
                }
            }
            countTotal++;
            
            Shot shot = new Shot(eachShot.getInt("x"), eachShot.getInt("y"), eachShot.getInt("distance"), eachShot.getInt("make"), eachShot.getString("shottype"), eachShot.getString("playtype"));
            xBig = BigDecimal.valueOf(eachShot.getInt("x"));
            yBig = BigDecimal.valueOf(eachShot.getInt("y") - 180);
            if (eachShot.getInt("make") == 1) {
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
            if (each.getY() > 410) {
                continue;
            }
            if (each.getMake() == 0) {
                MissedShotIcon msiTemp = (MissedShotIcon) allShots.get(each);
                gridpane.add(msiTemp.getLine1(), 1, 1);
                gridpane.add(msiTemp.getLine2(), 1, 1);

            } else {
                gridpane.add((Circle) allShots.get(each), 1, 1);
            }
        }

        this.fgfrac.setText(countMade + "/" + countTotal);
        if (countTotal == 0) {
            this.fgperc.setText("--");
        } else {
            this.fgperc.setText(new BigDecimal((double) countMade / countTotal * 100).setScale(2, RoundingMode.HALF_UP) + "%");
        }
        this.twopointfrac.setText(count2pMade + "/" + count2pTotal);
        if (count2pTotal == 0) {
            this.twopointperc.setText("--");
        } else {
            this.twopointperc.setText(new BigDecimal((double) count2pMade / count2pTotal * 100).setScale(2, RoundingMode.HALF_UP) + "%");
        }
        this.threepointfrac.setText(count3pMade + "/" + count3pTotal);
        if (count3pTotal == 0) {
            this.threepointperc.setText("--");
        } else {
            this.threepointperc.setText(new BigDecimal((double) count3pMade / count3pTotal * 100).setScale(2, RoundingMode.HALF_UP) + "%");
        }
    }

    private void resizeShots() {
        double height = imageview.getLayoutBounds().getHeight();
        double scaledLineThickness = shotLineThickness.divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue();
        double scaledLineLength = shotmissStartEnd.divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue();
//        shotmade.setTranslateY(height * transY.divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue());
//        shotmade.setRadius(height * shotmadeRadius.divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue());
//        shotmade.setStrokeWidth(height * scaledLineThickness);
//        shotmissedline1.setStrokeWidth(height * scaledLineThickness);
//        shotmissedline2.setStrokeWidth(height * scaledLineThickness);
//        shotmissedline1.setStartX((height * scaledLineLength));
//        shotmissedline2.setStartX((height * scaledLineLength));
//        shotmissedline1.setEndX((height * -1 * scaledLineLength));
//        shotmissedline2.setEndX((height * -1 * scaledLineLength));
//        shotmissedline1.setStartY((height * -1 * scaledLineLength));
//        shotmissedline2.setStartY((height * -1 * scaledLineLength));
//        shotmissedline1.setEndY((height * scaledLineLength));
//        shotmissedline2.setEndY((height * scaledLineLength));
//        shotmissedline2.setRotate(180);
//        shotmissedline1.setTranslateX(height * 0.10638297872);// 50/470
//        shotmissedline2.setTranslateX(height * 0.10638297872);
//        shotmissedline1.setTranslateY(height * 0.10638297872);
//        shotmissedline2.setTranslateY(height * 0.10638297872);
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

    private void setPlayerComboBox() {
        this.activePlayers = new HashMap();
//        String previousPlayer = this.playercombo.getValue().toString();
//        try {
//            this.previousPlayer = this.playercombo.getValue().toString();
//        } catch (NullPointerException ex) {
//            System.out.println("player field is null");
//        }
////        String previousSeason = this.seasoncombo.getValue().toString();
//        try {
//            this.previousSeason = this.seasoncombo.getValue().toString();
//        } catch (NullPointerException ex) {
//            System.out.println("season field is null");
//        }
        String year = yearcombo.getValue().toString();
        ResultSet rsSpecific;
        ArrayList<String> seasons = new ArrayList();
        seasons.add("Preseason");
        seasons.add("Regular Season");
        seasons.add("Playoffs");
        for (String each : seasons) {
            try {
                rsSpecific = conn3.prepareStatement("SELECT * FROM " + year.substring(0, 4) + "_" + year.substring(5, 7) + "_" + each.replace(" ", "") + "_active_players").executeQuery();
                while (rsSpecific.next()) {
                    this.activePlayers.put(rsSpecific.getInt("id"), rsSpecific.getString("firstname") + " " + rsSpecific.getString("lastname"));
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                continue;
            }
        }
        ArrayList<String> activeList = new ArrayList();
//        System.out.println("1 " + this.seasoncombo.getValue().toString());

        for (int each : this.activePlayers.keySet()) {
            activeList.add(activePlayers.get(each).trim());
        }
//        System.out.println("2 " + this.seasoncombo.getValue().toString());

        Collections.sort(activeList);
//        System.out.println("3 " + this.seasoncombo.getValue().toString());
//        System.out.println(previousYear + " " + previousPlayer + " " + previousSeason);
        if (seasoncombo.getValue() != null) {
            this.previousSeason = seasoncombo.getValue().toString();
        }
        this.playercombo.setItems(FXCollections.observableArrayList(activeList));
//        System.out.println(previousYear + " " + previousPlayer + " " + previousSeason);

        if (previousSeason != null && playercombo.getValue() != null && seasoncombo.getItems().contains(previousSeason)) {
            this.seasoncombo.getSelectionModel().select(previousSeason);
        } else {
            this.seasoncombo.getSelectionModel().clearSelection();
        }

//        try {
//            System.out.println("4 " + this.seasoncombo.getValue().toString());
//        } catch (NullPointerException ex) {
//            System.out.println("4 error");
//        }
        if (activeList.contains(previousPlayer)) {
            this.playercombo.getSelectionModel().select(previousPlayer);
        }
//        try {
//            System.out.println("5 " + this.seasoncombo.getValue().toString());
//        } catch (NullPointerException ex) {
//            System.out.println("5 error");
//        }
//        setSeasonsComboBox();

    }

    private void setSeasonsComboBox() {
//        try {
//            previousSeason = this.seasoncombo.getValue().toString();
//        } catch (NullPointerException ex) {
//            System.out.println("season field is null");
//        }
        int id = 0;
        ArrayList<String> seasons = new ArrayList();
        seasons.add("Preseason");
        seasons.add("Regular Season");
        seasons.add("Playoffs");
        if (this.playercombo.getValue() == null) {
            this.seasoncombo.setItems(FXCollections.observableArrayList(seasons));
            this.seasoncombo.getSelectionModel().clearSelection();
        } else {
            for (int each : this.activePlayers.keySet()) {
                if (this.activePlayers.get(each).trim().equals(this.playercombo.getValue().toString())) {
                    id = each;
                }
            }
            String sqlSelect = "SELECT id,firstname,lastname FROM player_relevant_data WHERE id=" + id;
            ResultSet rsID;
            String firstname = "";
            String lastname = "";
            try {
                rsID = conn3.prepareStatement(sqlSelect).executeQuery();
                while (rsID.next()) {
                    firstname = rsID.getString("firstname").replaceAll("[^A-Za-z0-9]", "");
                    lastname = rsID.getString("lastname").replaceAll("[^A-Za-z0-9]", "");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            String sqlSeason = "SELECT * FROM " + lastname + "_" + firstname + "_" + id + "_individual_data WHERE year=\"" + this.yearcombo.getValue().toString() + "\"";
            ArrayList<String> actives = new ArrayList();
            try {
                ResultSet rsActive = conn3.prepareStatement(sqlSeason).executeQuery();
                while (rsActive.next()) {
                    if (rsActive.getInt("preseason") == 1) {
                        actives.add("Preseason");
                    }
                    if (rsActive.getInt("reg") == 1) {
                        actives.add("Regular Season");
                    }
                    if (rsActive.getInt("playoffs") == 1) {
                        actives.add("Playoffs");
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            this.seasoncombo.setItems(FXCollections.observableArrayList(actives));
            if (actives.contains(previousSeason)) {
                this.seasoncombo.getSelectionModel().select(previousSeason);
            }
        }
    }

    private JSONArray getDataAsJSON() throws IOException {
        JSONObject jsonObjOut = new JSONObject();
        jsonObjOut.put("selector", "simplesearch");
        jsonObjOut.put("year", this.yearcombo.getValue().toString());
        jsonObjOut.put("playername", nameHash.get(this.playercombo.getValue().toString()));
        jsonObjOut.put("seasontype", this.seasoncombo.getValue().toString());
        System.out.println("sending jsonString to server");
        Main.getPrintWriterOut().println(jsonObjOut.toString());
        System.out.println("retrieved server response");
        return new JSONArray(Main.getServerResponse().readLine());
    }
}
