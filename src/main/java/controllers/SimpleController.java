/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
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
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import mainapp.Coordinate;
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

    private String firstname;
    private String lastname;
    private LinkedHashMap<String, String[]> nameHash;
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
    private double squareSize = 10.0;
    private LinkedHashMap<Coordinate, LinkedList<Double>> coordAverages;
    private int maxShotsPerMaxSquare = 0;
    private LinkedHashMap<Coordinate, Double> coordValue;
    private final int offset = 10;
    private final int maxDistanceBetweenNodes = 30;
    private LinkedList<Rectangle> allTiles;
    private String activeDisplay = "";
    private double min;
    private ArrayList<Thread> threads = new ArrayList();

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
    @FXML
    Button buttonswitch;
    @FXML
    Button traditionalbutton;
    @FXML
    Button heatmapbutton;
    @FXML
    Button gridbutton;
    @FXML
    Button zonebutton;
    @FXML
    Label lastupdatedlabel;
    @FXML
    GridPane topgridpane;
    @FXML
    HBox tophbox;
    @FXML
    Label namelabel;
    @FXML
    Line line;
    @FXML
    Label charttitle;
    @FXML
    Label dateaccuracy;
    @FXML
    Label updatelabel;
    @FXML
    Rectangle gridbackground;
    @FXML
    VBox centralvbox;
    @FXML
    HBox buttonbox;
    @FXML
    GridPane imagegrid;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        JSONArray jsonArrayInit = new JSONArray();
        threads = new ArrayList();

        try {
            jsonArrayInit = getInitData();
            JSONObject jsonObjMisc1 = jsonArrayInit.getJSONObject(1);
            dateaccuracy.setText(jsonObjMisc1.getString("value"));
            JSONObject jsonObjMisc0 = jsonArrayInit.getJSONObject(0);
            updatelabel.setText(jsonObjMisc0.getString("value"));
        } catch (IOException ex) {
            Logger.getLogger(SimpleController.class.getName()).log(Level.SEVERE, null, ex);
        }
        imageview.minHeight(470);
        imageview.minWidth(500);
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
        this.line.endXProperty().bind(this.gridpane.widthProperty());
        this.charttitle.setVisible(false);
        gridbackground.setWidth(imageview.localToParent(imageview.getBoundsInLocal()).getWidth());
        gridbackground.setHeight(imageview.localToParent(imageview.getBoundsInLocal()).getHeight());
        imagegrid.setMaxWidth(imageview.localToParent(imageview.getBoundsInLocal()).getWidth());
        imagegrid.setMaxHeight(imageview.localToParent(imageview.getBoundsInLocal()).getHeight());
        imagegrid.setMinWidth(imageview.localToParent(imageview.getBoundsInLocal()).getWidth());
        imagegrid.setMinHeight(imageview.localToParent(imageview.getBoundsInLocal()).getHeight());
        imagegrid.setPrefWidth(imageview.localToParent(imageview.getBoundsInLocal()).getWidth());
        imagegrid.setPrefHeight(imageview.localToParent(imageview.getBoundsInLocal()).getHeight());
        gridbackground.setVisible(false);

        VBox.setMargin(this.introlabel, new Insets(10, 0, 0, 0));
        VBox.setMargin(this.yearcombo, new Insets(20, 0, 0, 0));
        VBox.setMargin(this.playercombo, new Insets(20, 0, 0, 0));
        VBox.setMargin(this.seasoncombo, new Insets(20, 0, 0, 0));
        VBox.setMargin(this.searchbutton, new Insets(20, 0, 0, 0));
        this.shotgrid.maxWidthProperty().bind(this.gridpane.widthProperty().divide(3));
        this.shotgrid.maxHeightProperty().bind(this.gridpane.heightProperty().divide(5.25));

        try {
            reader = ResourceBundle.getBundle("dbconfig");
            namelabel.setText("Version " + reader.getString("version"));
            String[] nameArray = new String[3];
            JSONArray jsonArray = getInitAllPlayersData();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject eachPlayer = jsonArray.getJSONObject(i);
                nameArray = new String[3];
                nameArray[0] = eachPlayer.getInt("id") + "";
                nameArray[1] = eachPlayer.getString("firstname");
                nameArray[2] = eachPlayer.getString("lastname");
                nameHash.put((eachPlayer.getString("firstname") + " " + eachPlayer.getString("lastname")).trim(), nameArray);
            }
        } catch (IOException ex) {
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

        try {
            setPlayerComboBox();
        } catch (IOException ex) {
            Logger.getLogger(SimpleController.class.getName()).log(Level.SEVERE, null, ex);
        }

//        playercombo.setItems(FXCollections.observableArrayList());
        ArrayList<String> seasons = new ArrayList();
        seasons.add("Preseason");
        seasons.add("Regular Season");
        seasons.add("Playoffs");
        seasoncombo.setItems(FXCollections.observableArrayList(seasons));

        try {
            setSeasonsComboBox();
        } catch (IOException ex) {
            Logger.getLogger(SimpleController.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.buttonswitch.setOnMouseClicked((Event t) -> {
            try {
                Main.setRoot("advanced.fxml");
            } catch (IOException ex) {
                Logger.getLogger(SimpleController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        this.searchbutton.setOnMouseClicked((Event t) -> {
            createTraditionalThreadAndRun();
            this.errorlabel.setVisible(false);
            try {
                this.previousYear = this.yearcombo.getValue().toString();
                this.previousPlayer = this.playercombo.getValue().toString();
                this.previousSeason = this.seasoncombo.getValue().toString();
                JSONArray jsonArray = doSimpleSearch();
                plotTraditionalShots(jsonArray);
            } catch (SQLException ex) {
                ex.printStackTrace();
            } catch (NullPointerException ex) {
                this.errorlabel.setText("Please try again");
                this.errorlabel.setVisible(true);
            } catch (IOException ex) {
                Logger.getLogger(SimpleController.class.getName()).log(Level.SEVERE, null, ex);
            }
            activeDisplay = "simpletraditional";
            plotCircles();

        });
        this.gridbutton.setOnMouseClicked((Event t) -> {
            createGridThreadAndRun();
            this.errorlabel.setVisible(false);
            try {
                this.previousYear = this.yearcombo.getValue().toString();
                this.previousPlayer = this.playercombo.getValue().toString();
                this.previousSeason = this.seasoncombo.getValue().toString();
                JSONArray jsonArray = doSimpleGridSearch();
                plotGrid(jsonArray);
            } catch (SQLException ex) {
                ex.printStackTrace();
            } catch (NullPointerException ex) {
                this.errorlabel.setText("Please try again");
                this.errorlabel.setVisible(true);
            } catch (IOException ex) {
                Logger.getLogger(SimpleController.class.getName()).log(Level.SEVERE, null, ex);
            }
            gridbackground.setVisible(true);
            activeDisplay = "simplegrid";

            gridbackground.setWidth(imageview.localToParent(imageview.getBoundsInLocal()).getWidth());
            gridbackground.setHeight(imageview.localToParent(imageview.getBoundsInLocal()).getHeight());
            gridbackground.setVisible(true);
            imageview.setImage(new Image("/images/transparent.png"));
            imageview.setVisible(true);

//            imageview.toFront();
//            gridgrid.toFront();
//            buttonbox.toFront();
        });
        imageview.fitHeightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                resize();
            }
        });
        imageview.fitWidthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                resize();
            }
        });
        this.yearcombo.setOnAction((Event t) -> {
            try {
                setPlayerComboBox();
            } catch (IOException ex) {
                Logger.getLogger(SimpleController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        this.playercombo.setOnAction((Event t) -> {
            try {
                setSeasonsComboBox();
            } catch (IOException ex) {
                Logger.getLogger(SimpleController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    private void plotCircles() {
        Circle locator = new Circle(10);
        imagegrid.getChildren().add(locator);
        locator.setManaged(false);
        locator.setTranslateX(centralvbox.getWidth() / 2);
        System.out.println(centralvbox.getWidth() / 2 + "," + centralvbox.getHeight() / 2);
        locator.setTranslateY(centralvbox.getHeight() / 2);
        locator.toFront();
        Circle locator2 = new Circle(10);
        imagegrid.getChildren().add(locator2);
        locator2.setManaged(false);
        System.out.println((imageview.localToParent(imageview.getBoundsInLocal()).getMinX() + imageview.localToParent(imageview.getBoundsInLocal()).getWidth() / 2) + "," + (imageview.localToParent(imageview.getBoundsInLocal()).getMinY() + imageview.localToParent(imageview.getBoundsInLocal()).getHeight() / 2 - (180 * imageview.localToParent(imageview.getBoundsInLocal()).getHeight() / 470)));
        locator2.setTranslateX(imageview.localToParent(imageview.getBoundsInLocal()).getMinX() + imageview.localToParent(imageview.getBoundsInLocal()).getWidth() / 2);
        locator2.setTranslateY(imageview.localToParent(imageview.getBoundsInLocal()).getMinY() + imageview.localToParent(imageview.getBoundsInLocal()).getHeight() / 2 - (180.0 * imageview.localToParent(imageview.getBoundsInLocal()).getHeight() / 470));
        locator2.setFill(Color.WHITE);
        locator2.toFront();
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

    private JSONArray doSimpleSearch() throws SQLException, IOException {
        removeTradShots();
        JSONArray jsonArray = getSimpleTraditionalShotData();
        return jsonArray;
    }

    private JSONArray doSimpleGridSearch() throws SQLException, IOException {
        removeTradShots();
        System.out.println("Doing SimpleGridSearch");
        JSONArray jsonArray = getSimpleGridShotData();
        System.out.println("End Doing SimpleGridSearch");
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
    private void plotTraditionalShots(JSONArray jsonArray) throws SQLException {
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
            yBig = BigDecimal.valueOf(eachShot.getInt("y"));
            if (eachShot.getInt("make") == 1) {
                circle = new Circle(imageview.getLayoutBounds().getHeight() * shotmadeRadius.divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue());
                circle.setFill(Color.TRANSPARENT);
//                circle.setTranslateX(imageview.getLayoutBounds().getHeight() * xBig.divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue());
//                circle.setTranslateY(imageview.getLayoutBounds().getHeight() * yBig.divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue());
                circle.setTranslateX(xBig.intValue() * imageview.getLayoutBounds().getHeight() / 470 + imageview.localToParent(imageview.getBoundsInLocal()).getMinX() + imageview.localToParent(imageview.getBoundsInLocal()).getWidth() / 2);
                circle.setTranslateY(yBig.intValue() * imageview.getLayoutBounds().getHeight() / 470 + imageview.localToParent(imageview.getBoundsInLocal()).getMinY() + imageview.localToParent(imageview.getBoundsInLocal()).getHeight() / 2 - (180.0 * imageview.localToParent(imageview.getBoundsInLocal()).getHeight() / 470));
                circle.setStrokeWidth(imageview.getLayoutBounds().getHeight() * shotLineThickness.divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue());
                circle.setStroke(Color.LIMEGREEN);
                circle.setManaged(false);
                allShots.put(shot, circle);
            } else {
//                msi = new MissedShotIcon(xBig.divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue(),
//                        yBig.divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue(),
//                        imageview.getLayoutBounds().getHeight(),
//                        shotmissStartEnd.divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue(),
//                        shotLineThickness.divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue(),
//                        0,
//                        0); 
                msi = new MissedShotIcon((xBig.intValue()) / 470,
                        ((yBig.intValue() - 55) / 470),
                        imageview.getLayoutBounds().getHeight(),
                        shotmissStartEnd.divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue(),
                        shotLineThickness.divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue(),
                        imageview.localToParent(imageview.getBoundsInLocal()).getMinX() + imageview.localToParent(imageview.getBoundsInLocal()).getWidth() / 2,
                        imageview.localToParent(imageview.getBoundsInLocal()).getMinY());
                msi.getLine1().setManaged(false);
                msi.getLine2().setManaged(false);
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
//                gridpane.add(msiTemp.getLine1(), 1, 1);
//                gridpane.add(msiTemp.getLine2(), 1, 1);
                msiTemp.getLine1().setManaged(false);
                msiTemp.getLine2().setManaged(false);
                msiTemp.getLine1().setTranslateX(BigDecimal.valueOf(each.getX()).doubleValue() * imageview.getLayoutBounds().getHeight() / 470 + imageview.localToParent(imageview.getBoundsInLocal()).getMinX() + imageview.localToParent(imageview.getBoundsInLocal()).getWidth() / 2);// 50/470
                msiTemp.getLine2().setTranslateX(BigDecimal.valueOf(each.getX()).doubleValue() * imageview.getLayoutBounds().getHeight() / 470 + imageview.localToParent(imageview.getBoundsInLocal()).getMinX() + imageview.localToParent(imageview.getBoundsInLocal()).getWidth() / 2);
                msiTemp.getLine1().setTranslateY(BigDecimal.valueOf(each.getY()).doubleValue() * imageview.getLayoutBounds().getHeight() / 470 + imageview.localToParent(imageview.getBoundsInLocal()).getMinY() + imageview.localToParent(imageview.getBoundsInLocal()).getHeight() / 2 - (180.0 * imageview.localToParent(imageview.getBoundsInLocal()).getHeight() / 470));
                msiTemp.getLine2().setTranslateY(BigDecimal.valueOf(each.getY()).doubleValue() * imageview.getLayoutBounds().getHeight() / 470 + imageview.localToParent(imageview.getBoundsInLocal()).getMinY() + imageview.localToParent(imageview.getBoundsInLocal()).getHeight() / 2 - (180.0 * imageview.localToParent(imageview.getBoundsInLocal()).getHeight() / 470));
                imagegrid.getChildren().add(msiTemp.getLine1());
                imagegrid.getChildren().add(msiTemp.getLine2());
            } else {
//                gridpane.add((Circle) allShots.get(each), 1, 1);
                imagegrid.getChildren().add((Circle) allShots.get(each));

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
        this.charttitle.setText(this.playercombo.getValue().toString() + ", " + this.yearcombo.getValue().toString() + " " + this.seasoncombo.getValue().toString());
        this.charttitle.setVisible(true);

    }

    private void resizeShots() {
        System.out.println("resizing shots");
        double height = imageview.localToParent(imageview.getBoundsInLocal()).getHeight();
        double scaledLineThickness = shotLineThickness.divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue();
        double scaledLineLength = shotmissStartEnd.divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue();
        double minX = imageview.localToParent(imageview.getBoundsInLocal()).getMinX();
        double minY = imageview.localToParent(imageview.getBoundsInLocal()).getMinY();
        double width = imageview.localToParent(imageview.getBoundsInLocal()).getWidth();
        if (!allShots.keySet().isEmpty()) {
            for (Shot each : allShots.keySet()) {
                Circle circle;
                Line line1;
                Line line2;
                if (each.getMake() == 1) {
                    circle = (Circle) allShots.get(each);
//                    circle.setTranslateX(height * BigDecimal.valueOf(each.getX()).divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue());
//                    circle.setTranslateY(height * BigDecimal.valueOf(each.getY()).divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue());
                    circle.setTranslateX(BigDecimal.valueOf(each.getX()).doubleValue() * height / 470 + minX + width / 2);
                    circle.setTranslateY(BigDecimal.valueOf(each.getY()).doubleValue() * height / 470 + minY + height / 2 - (180.0 * height / 470));
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
//                    line1.setTranslateX(height * BigDecimal.valueOf(each.getX()).divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue());// 50/470
//                    line2.setTranslateX(height * BigDecimal.valueOf(each.getX()).divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue());
//                    line1.setTranslateY(height * BigDecimal.valueOf(each.getY()).divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue());
//                    line2.setTranslateY(height * BigDecimal.valueOf(each.getY()).divide(origHeight, 6, RoundingMode.HALF_UP).doubleValue());
                    line1.setTranslateX(BigDecimal.valueOf(each.getX()).doubleValue() * height / 470 + minX + width / 2);// 50/470
                    line2.setTranslateX(BigDecimal.valueOf(each.getX()).doubleValue() * height / 470 + minX + width / 2);
                    line1.setTranslateY(BigDecimal.valueOf(each.getY()).doubleValue() * height / 470 + minY + height / 2 - (180.0 * height / 470));
                    line2.setTranslateY(BigDecimal.valueOf(each.getY()).doubleValue() * height / 470 + minY + height / 2 - (180.0 * height / 470));
                    line2.setRotate(180);

                }
            }
        }
    }

    private void setPlayerComboBox() throws IOException {
        this.activePlayers = new HashMap();
        String year = yearcombo.getValue().toString();
        JSONArray jsonArray = getActivePlayersData();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject eachPlayer = jsonArray.getJSONObject(i);
            this.activePlayers.put(eachPlayer.getInt("id"), eachPlayer.getString("firstname") + " " + eachPlayer.getString("lastname"));
        }
        ArrayList<String> activeList = new ArrayList();

        for (int each : this.activePlayers.keySet()) {
            activeList.add(activePlayers.get(each).trim());
        }
        Collections.sort(activeList);
        if (seasoncombo.getValue() != null) {
            this.previousSeason = seasoncombo.getValue().toString();
        }
        this.playercombo.setItems(FXCollections.observableArrayList(activeList));
        if (previousSeason != null && playercombo.getValue() != null && seasoncombo.getItems().contains(previousSeason)) {
            this.seasoncombo.getSelectionModel().select(previousSeason);
        } else {
            this.seasoncombo.getSelectionModel().clearSelection();
        }
        if (activeList.contains(previousPlayer)) {
            this.playercombo.getSelectionModel().select(previousPlayer);
        }
    }

    private void setSeasonsComboBox() throws IOException {
        int id = 0;
        ArrayList<String> seasons = new ArrayList();
        seasons.add("Preseason");
        seasons.add("Regular Season");
        seasons.add("Playoffs");
        if (this.playercombo.getValue() == null) {
            this.seasoncombo.setItems(FXCollections.observableArrayList(seasons));
            this.seasoncombo.getSelectionModel().clearSelection();
        } else {
            ArrayList<String> actives = new ArrayList();
            JSONArray jsonArraySeasons = getSeasonsData();
            for (int i = 0; i < jsonArraySeasons.length(); i++) {
                JSONObject eachSeason = jsonArraySeasons.getJSONObject(i);
                if (Integer.parseInt(eachSeason.get("preseason").toString()) == 1) {
                    actives.add("Preseason");
                }
                if (Integer.parseInt(eachSeason.get("reg").toString()) == 1) {
                    actives.add("Regular Season");
                }
                if (Integer.parseInt(eachSeason.get("playoffs").toString()) == 1) {
                    actives.add("Playoffs");
                }
            }
            this.seasoncombo.setItems(FXCollections.observableArrayList(actives));
            if (actives.contains(previousSeason)) {
                this.seasoncombo.getSelectionModel().select(previousSeason);
            }
        }
    }

    private JSONArray getInitData() throws IOException {
        JSONObject jsonObjOut = new JSONObject();
        jsonObjOut.put("selector", "init");
        Main.getPrintWriterOut().println(jsonObjOut.toString());
        return new JSONArray(Main.getServerResponse().readLine());
    }

    private JSONArray getInitAllPlayersData() throws IOException {
        JSONObject jsonObjOut = new JSONObject();
        jsonObjOut.put("selector", "initallplayers");
        Main.getPrintWriterOut().println(jsonObjOut.toString());
        return new JSONArray(Main.getServerResponse().readLine());
    }

    private JSONArray getActivePlayersData() throws IOException {
        JSONObject jsonObjOut = new JSONObject();
        jsonObjOut.put("selector", "activeplayers");
        jsonObjOut.put("year", this.yearcombo.getValue().toString());
        Main.getPrintWriterOut().println(jsonObjOut.toString());
        return new JSONArray(Main.getServerResponse().readLine());
    }

    private JSONArray getGridAveragesData() throws IOException {
        JSONObject jsonObjOut = new JSONObject();
        jsonObjOut.put("selector", "gridaverages");
        Main.getPrintWriterOut().println(jsonObjOut.toString());
        return new JSONArray(Main.getServerResponse().readLine());
    }

    private JSONArray getSeasonsData() throws IOException {
        JSONObject jsonObjOut = new JSONObject();
        jsonObjOut.put("selector", "seasons");
        jsonObjOut.put("year", this.yearcombo.getValue().toString());
        jsonObjOut.put("playerid", nameHash.get(this.playercombo.getValue().toString())[0]);
        jsonObjOut.put("playerfirstname", nameHash.get(this.playercombo.getValue().toString())[1]);
        jsonObjOut.put("playerlastname", nameHash.get(this.playercombo.getValue().toString())[2]);
        Main.getPrintWriterOut().println(jsonObjOut.toString());
        return new JSONArray(Main.getServerResponse().readLine());

    }

    private JSONArray getSimpleTraditionalShotData() throws IOException {
        JSONObject jsonObjOut = new JSONObject();
        jsonObjOut.put("selector", "simpletraditional");
        jsonObjOut.put("year", this.yearcombo.getValue().toString());
        jsonObjOut.put("playername", nameHash.get(this.playercombo.getValue().toString()));
        jsonObjOut.put("seasontype", this.seasoncombo.getValue().toString());
        Main.getPrintWriterOut().println(jsonObjOut.toString());
        return new JSONArray(Main.getServerResponse().readLine());
    }

    private JSONArray getSimpleHeatShotData() throws IOException {
        JSONObject jsonObjOut = new JSONObject();
        jsonObjOut.put("selector", "simpleheat");
        jsonObjOut.put("year", this.yearcombo.getValue().toString());
        jsonObjOut.put("playername", nameHash.get(this.playercombo.getValue().toString()));
        jsonObjOut.put("seasontype", this.seasoncombo.getValue().toString());
        Main.getPrintWriterOut().println(jsonObjOut.toString());
        return new JSONArray(Main.getServerResponse().readLine());
    }

    private JSONArray getSimpleGridShotData() throws IOException {
        System.out.println("Getting SimpleGridShotData");
        JSONObject jsonObjOut = new JSONObject();
        jsonObjOut.put("selector", "simplegrid");
        jsonObjOut.put("year", this.yearcombo.getValue().toString());
        jsonObjOut.put("playername", nameHash.get(this.playercombo.getValue().toString()));
        jsonObjOut.put("seasontype", this.seasoncombo.getValue().toString());
        Main.getPrintWriterOut().println(jsonObjOut.toString());
        System.out.println("End Getting SimpleGridShotData");
        return new JSONArray(Main.getServerResponse().readLine());
    }

    private JSONArray getSimpleZonedShotData() throws IOException {
        JSONObject jsonObjOut = new JSONObject();
        jsonObjOut.put("selector", "simplezoned");
        jsonObjOut.put("year", this.yearcombo.getValue().toString());
        jsonObjOut.put("playername", nameHash.get(this.playercombo.getValue().toString()));
        jsonObjOut.put("seasontype", this.seasoncombo.getValue().toString());
        Main.getPrintWriterOut().println(jsonObjOut.toString());
        return new JSONArray(Main.getServerResponse().readLine());
    }

    private void plotGrid(JSONArray jsonArray) throws IOException {
        System.out.println("Plotting Grid");
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
        Coordinate coord;
        coordAverages = new LinkedHashMap();
        for (int j = -55; j < 400; j = j + (int) squareSize) {
            for (int i = -250; i < 250; i = i + (int) squareSize) {
                coord = new Coordinate(i, j);
                LinkedList info = new LinkedList();
                info.add(0.0);
                info.add(0.0);
                info.add(0.0);
                coordAverages.put(coord, info);
            }
        }
        System.out.println("keyset: " + coordAverages.keySet().size());
        double factor = 0.007;
        int shotCounter = 0;
        HashMap<String, BigDecimal> averages = useGridAverages();
        double x;
        int y;
        int make;
        int counter = 0;
        allShots = new LinkedHashMap();
        JSONObject eachShot;
        for (int i = 0; i < jsonArray.length(); i++) {
            eachShot = jsonArray.getJSONObject(i);
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

            counter++;
            if (eachShot.getInt("y") >= 400) {
                continue;
            }
            shotCounter++;
            y = eachShot.getInt("y");
            x = eachShot.getInt("x");
            make = eachShot.getInt("make");
            for (Coordinate each : coordAverages.keySet()) {
                if (x < each.getX() + 5 + squareSize * 1.5 && x >= each.getX() + 5 - squareSize * 1.5 && y < each.getY() + 5 + squareSize * 1.5 && y >= each.getY() + 5 - squareSize * 1.5) {
                    coordAverages.get(each).set(1, coordAverages.get(each).get(1) + 1);
                    if (make == 1) {
                        coordAverages.get(each).set(0, coordAverages.get(each).get(0) + 1);
                    }
                }
            }

        }
        for (Coordinate each : coordAverages.keySet()) {
            if (coordAverages.get(each).get(1) != 0) {
                coordAverages.get(each).set(2, coordAverages.get(each).get(0) * 1.0 / coordAverages.get(each).get(1) * 1.0);
            }
        }
        idwGrid();
        System.out.println("ShotCounter : " + shotCounter);
        min = 1;
        double minFactor = 0.00045;
        if (shotCounter * minFactor > 1) {
            min = shotCounter * minFactor;
        } else {
            factor = 4.1008 * Math.pow(shotCounter, -0.798);
        }
        System.out.println("min = " + min);
        System.out.println("Factor: " + factor);
        maxShotsPerMaxSquare = (int) (factor * shotCounter);
        allTiles = new LinkedList();
        for (Coordinate each2 : coordValue.keySet()) {
            Rectangle square = new Rectangle();

            if (coordAverages.get(each2).get(1) < maxShotsPerMaxSquare && coordAverages.get(each2).get(1) > min) {
                square.setHeight((coordAverages.get(each2).get(1) / maxShotsPerMaxSquare * squareSize) * 0.9);
                square.setWidth((coordAverages.get(each2).get(1) / maxShotsPerMaxSquare * squareSize) * 0.9);
            } else if (coordAverages.get(each2).get(1) >= maxShotsPerMaxSquare) {
                square.setHeight(squareSize * 0.9);
                square.setWidth(squareSize * 0.9);
            }
            String temp = "(" + each2.getX() + "," + each2.getY() + ")";
            double avg = averages.get(temp).doubleValue();
            if (coordValue.get(each2) > avg + 0.07) {
                square.setFill(Color.web("#fc2121"));
            } else if (coordValue.get(each2) > avg + 0.05 && coordValue.get(each2) <= avg + 0.07) {
                square.setFill(Color.web("#ff6363"));
            } else if (coordValue.get(each2) > avg + 0.015 && coordValue.get(each2) <= avg + 0.05) {
                square.setFill(Color.web("#ff9c9c"));
            } else if (coordValue.get(each2) > avg - 0.015 && coordValue.get(each2) <= avg + 0.015) {
                square.setFill(Color.WHITE);
            } else if (coordValue.get(each2) > avg - 0.05 && coordValue.get(each2) <= avg - 0.015) {
                square.setFill(Color.web("#aed9ff"));
            } else if (coordValue.get(each2) > avg - 0.07 && coordValue.get(each2) <= avg - 0.05) {
                square.setFill(Color.web("#8bc9ff"));
            } else {
                square.setFill(Color.web("#7babff"));
            }
            square.setOpacity(0.85);
            square.setTranslateX((each2.getX()+5) * imageview.getLayoutBounds().getHeight() / 470);
            //+ imageview.localToParent(imageview.getBoundsInLocal()).getMinX() + imageview.localToParent(imageview.getBoundsInLocal()).getWidth() / 2
            square.setTranslateY(each2.getY() * imageview.getLayoutBounds().getHeight() / 470 + -(180.0 * imageview.localToParent(imageview.getBoundsInLocal()).getHeight() / 470));
//imageview.localToParent(imageview.getBoundsInLocal()).getMinY() + imageview.localToParent(imageview.getBoundsInLocal()).getHeight() / 2
//            square.setOnMouseEntered((MouseEvent t) -> {
//                Label label = new Label();
//                label.setText((square.getLayoutX() - 250) + "," + (square.getLayoutY() - 70));
////                label.setLayoutX(10);
////                label.setLayoutY(420);
//                label.setVisible(true);
//                label.setStyle("-fx-font: " + 30 + "px \"Serif\"; ");
//                tophbox.getChildren().add(label);
//            });
//            square.setOnMouseExited((MouseEvent t) -> {
//                tophbox.getChildren().remove(tophbox.getChildren().size() - 1);
//            });
            imagegrid.add(square, 0, 0);
            allTiles.add(square);
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
        this.charttitle.setText(this.playercombo.getValue().toString() + ", " + this.yearcombo.getValue().toString() + " " + this.seasoncombo.getValue().toString());
        this.charttitle.setVisible(true);

    }

    private void idwGrid() {
        System.out.println("Starting idwGrid");
        coordValue = new LinkedHashMap();
        double predictedValue = 0;
        double aSum = 0;
        double bSum = 0;
        int p = 2;
        double valueI = 0;
        int counter = 0;
        for (Coordinate each : coordAverages.keySet()) {
            if (each.getX() % offset == 0 && (each.getY() - 5) % offset == 0) {
                counter++;
                aSum = 0;
                bSum = 0;
                for (Coordinate each2 : coordAverages.keySet()) {
                    if (!each.equals(each2) && getDistance(each, each2) < maxDistanceBetweenNodes) {
                        valueI = coordAverages.get(each2).get(2);
                        aSum = aSum + (valueI / Math.pow(getDistance(each, each2), p));
                        bSum = bSum + (1 / Math.pow(getDistance(each, each2), p));
                    }
                }
                predictedValue = aSum / bSum;
                coordValue.put(each, predictedValue);
            }
        }
        System.out.println("End idwGrid");

    }

    private double getDistance(Coordinate coordOrig, Coordinate coordI) {
        double a = coordOrig.getX() - coordI.getX();
        double b = coordOrig.getY() - coordI.getY();
        if (Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2)) < 0) {
            System.err.print("NEGATIVE ERROR");
            System.exit(1);
        }
        return Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
    }

    private HashMap<String, BigDecimal> useGridAverages() throws IOException {
        HashMap<String, BigDecimal> hashmap = new HashMap();
        JSONArray jsonArray = getGridAveragesData();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject eachShot = jsonArray.getJSONObject(i);
            hashmap.put(eachShot.getString("uniqueid"), eachShot.getBigDecimal("average"));
        }
        System.out.println("Used Grid Hashmap");
        return hashmap;
    }

    private void resizeGrid() {
        System.out.println("Resizing Grid");
        gridbackground.setWidth(imageview.localToParent(imageview.getBoundsInLocal()).getWidth());
        gridbackground.setHeight(imageview.localToParent(imageview.getBoundsInLocal()).getHeight());
        imagegrid.setMaxWidth(imageview.localToParent(imageview.getBoundsInLocal()).getWidth());
        imagegrid.setMaxHeight(imageview.localToParent(imageview.getBoundsInLocal()).getHeight());
        imagegrid.setMinWidth(imageview.localToParent(imageview.getBoundsInLocal()).getWidth());
        imagegrid.setMinHeight(imageview.localToParent(imageview.getBoundsInLocal()).getHeight());
        imagegrid.setPrefWidth(imageview.localToParent(imageview.getBoundsInLocal()).getWidth());
        imagegrid.setPrefHeight(imageview.localToParent(imageview.getBoundsInLocal()).getHeight());
        System.out.println("imageview: " + imageview.getLayoutBounds().getWidth() + "," + imageview.getLayoutBounds().getHeight());
        double height = imageview.getLayoutBounds().getHeight();
        double width = imageview.getLayoutBounds().getWidth();
        squareSize = width / 50;
        System.out.println("squareSize: " + squareSize);
        double tempHeight;
        double tempWidth;
        Rectangle rect;
        int counter = 0;
        Rectangle square;

        for (Coordinate each2 : coordValue.keySet()) {
            square = allTiles.get(counter);
            if (coordAverages.get(each2).get(1) < maxShotsPerMaxSquare && coordAverages.get(each2).get(1) > min) {
                square.setHeight((coordAverages.get(each2).get(1) / maxShotsPerMaxSquare * squareSize) * 0.9);
                square.setWidth((coordAverages.get(each2).get(1) / maxShotsPerMaxSquare * squareSize) * 0.9);
            } else if (coordAverages.get(each2).get(1) >= maxShotsPerMaxSquare) {
                square.setHeight(squareSize * 0.9);
                square.setWidth(squareSize * 0.9);
            }
            square.setTranslateX((each2.getX()+5) * height / 470 );
            square.setTranslateY(each2.getY() * height / 470 -(180.0 * height / 470));

            counter++;
        }
        double max = 0.0;
        for (Rectangle each : allTiles) {
            if (each.getWidth() > max) {
                max = each.getWidth();
            }
        }
        System.out.println("Actual Max: " + max);
    }

    private void resize() {
        switch (activeDisplay) {
            case "simpletraditional":
                resizeShots();
                break;
            case "simplegrid":
                resizeGrid();
                break;
            case "simpleheat":
                break;
            case "simplezoned":
                break;

        }
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
        traditionalbutton.setStyle("-fx-font: " + font + "px \"Serif\";");
        heatmapbutton.setStyle("-fx-font: " + font + "px \"Serif\";");
        gridbutton.setStyle("-fx-font: " + font + "px \"Serif\";");
        zonebutton.setStyle("-fx-font: " + font + "px \"Serif\";");
        titlelabel.setMinWidth(Region.USE_PREF_SIZE);
        titlelabel.setStyle("-fx-font: " + fontGrid * 3 + "px \"Serif\"; ");
        charttitle.setStyle("-fx-font: " + fontGrid + "px \"Arial Italic\";");
        charttitle.setMinHeight(imageview.getLayoutBounds().getHeight() / 10);
        charttitle.setMinWidth(imageview.getLayoutBounds().getWidth());
        charttitle.setMaxHeight(imageview.getLayoutBounds().getHeight() / 10);
        charttitle.setMaxWidth(imageview.getLayoutBounds().getWidth());
        gridbackground.setWidth(imageview.localToParent(imageview.getBoundsInLocal()).getWidth());
        gridbackground.setHeight(imageview.localToParent(imageview.getBoundsInLocal()).getHeight());

        imagegrid.setMaxWidth(imageview.localToParent(imageview.getBoundsInLocal()).getWidth());
        imagegrid.setMaxHeight(imageview.localToParent(imageview.getBoundsInLocal()).getHeight());
        imagegrid.setMinWidth(imageview.localToParent(imageview.getBoundsInLocal()).getWidth());
        imagegrid.setMinHeight(imageview.localToParent(imageview.getBoundsInLocal()).getHeight());
        imagegrid.setPrefWidth(imageview.localToParent(imageview.getBoundsInLocal()).getWidth());
        imagegrid.setPrefHeight(imageview.localToParent(imageview.getBoundsInLocal()).getHeight());
        VBox.setMargin(introlabel, new Insets(new BigDecimal(imageview.getLayoutBounds().getHeight()).multiply(new BigDecimal("20")).divide(new BigDecimal("475"), 6, RoundingMode.HALF_UP).doubleValue(), 0, 0, 0));
        VBox.setMargin(yearcombo, new Insets(30, 0, 0, 0));
        VBox.setMargin(playercombo, new Insets(30, 0, 0, 0));
        VBox.setMargin(seasoncombo, new Insets(30, 0, 0, 0));
        VBox.setMargin(searchbutton, new Insets(20, 0, 0, 0));

    }

    private void interruptAllThreads() {
        for (Thread each : threads) {
            try {
                each.interrupt();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    private void resizeHeat() {

    }

    private void resizeZone() {

    }

    private void createTraditionalThreadAndRun() {
        interruptAllThreads();
        Thread tTrad = new Thread(new Runnable() {
            @Override
            public void run() {
                Bounds startBounds = vbox.getLayoutBounds();
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        startBounds = vbox.getLayoutBounds();
                        Thread.sleep(1500);
                        if (!startBounds.equals(vbox.getLayoutBounds())) {
                            resizeShots();
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        threads.clear();
        threads.add(tTrad);
        tTrad.start();
    }

    private void createGridThreadAndRun() {
        interruptAllThreads();
        Thread tGrid = new Thread(new Runnable() {
            @Override
            public void run() {
                Bounds startBounds = vbox.getLayoutBounds();
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        startBounds = vbox.getLayoutBounds();
                        Thread.sleep(1500);
                        if (!startBounds.equals(vbox.getLayoutBounds())) {
                            resizeGrid();
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        threads.clear();
        threads.add(tGrid);
        tGrid.start();
    }

    private void createHeatThreadAndRun() {
        interruptAllThreads();
        Thread tHeat = new Thread(new Runnable() {
            @Override
            public void run() {
                Bounds startBounds = vbox.getLayoutBounds();
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        startBounds = vbox.getLayoutBounds();
                        Thread.sleep(1500);
                        if (!startBounds.equals(vbox.getLayoutBounds())) {
                            resizeHeat();
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        threads.clear();
        threads.add(tHeat);
        tHeat.start();
    }

    private void createZoneThreadAndRun() {
        interruptAllThreads();
        Thread tZone = new Thread(new Runnable() {
            @Override
            public void run() {
                Bounds startBounds = vbox.getLayoutBounds();
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        startBounds = vbox.getLayoutBounds();
                        Thread.sleep(1500);
                        if (!startBounds.equals(vbox.getLayoutBounds())) {
                            resizeZone();
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        threads.clear();
        threads.add(tZone);
        tZone.start();
    }

    private void removeTradShots() {
        ArrayList<Node> toRemove = new ArrayList();
        for (Node each : centralvbox.getChildren()) {
            if (each.getClass().equals(Circle.class) || each.getClass().equals(Line.class)) {
                toRemove.add(each);
            }
        }
        for (Node each : toRemove) {
            centralvbox.getChildren().remove(each);
        }
    }

}
