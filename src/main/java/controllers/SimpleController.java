/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;
import java.io.IOException;
import javafx.scene.paint.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;
import mainapp.Coordinate;
import mainapp.Main;
import mainapp.MissedShotIcon;
import mainapp.Shot;
import mainapp.UserInputComboBox;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author samnishita
 */
public class SimpleController implements Initializable {

    private LinkedHashMap<String, String[]> nameHash;
    private final BigDecimal ORIG_HEIGHT = new BigDecimal("470");
    private final BigDecimal SHOT_MADE_RADIUS = new BigDecimal("5");
    private final BigDecimal SHOT_MISS_START_END = new BigDecimal("3");
    private final BigDecimal SHOT_LINE_THICKNESS = new BigDecimal("2");
    private LinkedHashMap<Shot, Object> allShots = new LinkedHashMap();
    private HashMap<Integer, String> activePlayers;
    private final int COMBO_FONT_SIZE = 18;
    private final int STAT_GRID_FONT_SIZE = 20;
    private String previousYear;
    private String previousPlayer;
    private String previousSeason;
    private ResourceBundle reader = null;
    private double squareSize = 10.0;
    private final double SQUARE_SIZE_ORIG = 10.0;
    private LinkedHashMap<Coordinate, LinkedList<Double>> coordAverages;
    private int maxShotsPerMaxSquare = 0;
    private ConcurrentHashMap<Coordinate, Double> coordValue;
    private final int OFFSET = 10;
    private final int maxDistanceBetweenNodes = 20;
    private LinkedList<Rectangle> allTiles;
    private double min;
    private String currentSearchModeSelection = "";
    private int shotCounter = 0;
    private double maxCutoff = 0.0;
    private double diff = maxCutoff / 10;
    private int offsetHeat = 15;//15 is balanced
    //5 is ultrafine but needs opacity fix and is time intensive
    //10 is a little better than 15
    //20 is faster than 15 but more fuzzy, on the verge of bad scaling
    //25 is pretty grainy, decently quick, does not scale well
    //30 is awful
    //smoothness
    private final int MAX_DISTANCE_BETWEEN_NODES_HEAT = 30;
    private LinkedList<Circle> allHeatCircles;
    private ArrayList<Node> allZoneFXML = new ArrayList();
    private LinkedList<Label> allLabels;
    private LinkedList<Label> allPercentLabels;
    private LinkedList<Node> allShapes;
    private HashMap<Integer, Double[]> allZones;
    private HashMap<Integer, Double> allZoneAverages;
    private DecimalFormat df = new DecimalFormat("##.#");
    private Rectangle mask;
    private Thread tTrad = new Thread();
    private Thread tGrid = new Thread();
    private Thread tHeat = new Thread();
    private Thread tZone = new Thread();
    private ArrayList<Thread> allUltraFineHeatThreads;
    private String beginSeason = "";
    private String endSeason = "";
    private HashSet<String> allSelectedPlayers = new HashSet();
    private HashSet<String> allSelectedSeasonTypes = new HashSet();
    private String beginDistance = "";
    private String endDistance = "";
    private String shotSuccess = "";
    private String shotValue = "";
    private HashSet<String> allSelectedShotTypes = new HashSet();
    private HashSet<String> allSelectedTeams = new HashSet();
    private HashSet<String> allSelectedHomeTeams = new HashSet();
    private HashSet<String> allSelectedAwayTeams = new HashSet();
    private HashSet<String> allSelectedCourtAreas = new HashSet();
    private HashSet<String> allSelectedCourtSides = new HashSet();
    private String currentSearchModeSelectionAdvanced = "";
    private LinkedHashMap<String, Integer> relevantTeamNameIDHashMap = new LinkedHashMap();
    private double font = 0.0;
    private double fontGrid = 0.0;
    private LinkedList<Button> viewButtons = new LinkedList();

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
    @FXML
    VBox gridlegendsize;
    @FXML
    VBox gridlegendcolor;
    @FXML
    Label gridcolorlegendtoplabel;
    @FXML
    Label gridcolorlegendlowerlabel;
    @FXML
    Label gridcolorlegendupperlabel;
    @FXML
    Label gridsizelegendtoplabel;
    @FXML
    Label gridsizelegendlowerlabel;
    @FXML
    Label gridsizelegendupperlabel;
    @FXML
    Rectangle gridcolorlegendgradient;
    @FXML
    HBox gridsizelegendgradient;
    @FXML
    VBox heatlegend;
    @FXML
    Label heatlegendtoplabel;
    @FXML
    Label heatlegendlowerlabel;
    @FXML
    Label heatlegendupperlabel;
    @FXML
    Rectangle heatlegendgradient;
    @FXML
    VBox zonelegend;
    @FXML
    Label zonelegendtoplabel;
    @FXML
    Label zonelegendlowerlabel;
    @FXML
    Label zonelegendupperlabel;
    @FXML
    Rectangle zonelegendgradient;
    @FXML
    Group group1;
    @FXML
    Rectangle rect1;
    @FXML
    Arc arc1;
    @FXML
    Group group2;
    @FXML
    Rectangle rect2;
    @FXML
    Arc arc2;
    @FXML
    Group group3;
    @FXML
    Rectangle rect3;
    @FXML
    Arc arc3;
    @FXML
    Arc arc4;
    @FXML
    Group group5;
    @FXML
    Rectangle rect5;
    @FXML
    Arc arc5;
    @FXML
    Group group6;
    @FXML
    Rectangle rect6;
    @FXML
    Arc arc6;
    @FXML
    Arc arc7;
    @FXML
    Arc arc8;
    @FXML
    Arc arc9;
    @FXML
    Group group10;
    @FXML
    Rectangle rect10;
    @FXML
    Arc arc10;
    @FXML
    Rectangle rect11;
    @FXML
    Group group12;
    @FXML
    Rectangle rect12;
    @FXML
    Arc arc12;
    @FXML
    Arc arc13;
    @FXML
    Group group14;
    @FXML
    Rectangle rect14;
    @FXML
    Arc arc14;
    @FXML
    Rectangle rect15;
    @FXML
    Label label1;
    @FXML
    Label label2;
    @FXML
    Label label3;
    @FXML
    Label label4;
    @FXML
    Label label5;
    @FXML
    Label label6;
    @FXML
    Label label7;
    @FXML
    Label label8;
    @FXML
    Label label9;
    @FXML
    Label label10;
    @FXML
    Label label11;
    @FXML
    Label label12;
    @FXML
    Label label13;
    @FXML
    Label label14;
    @FXML
    Label label15;
    @FXML
    Label labelpercent1;
    @FXML
    Label labelpercent2;
    @FXML
    Label labelpercent3;
    @FXML
    Label labelpercent4;
    @FXML
    Label labelpercent5;
    @FXML
    Label labelpercent6;
    @FXML
    Label labelpercent7;
    @FXML
    Label labelpercent8;
    @FXML
    Label labelpercent9;
    @FXML
    Label labelpercent10;
    @FXML
    Label labelpercent11;
    @FXML
    Label labelpercent12;
    @FXML
    Label labelpercent13;
    @FXML
    Label labelpercent14;
    @FXML
    Label labelpercent15;
    @FXML
    Button simplelayoutbutton;
    @FXML
    Button advancedlayoutbutton;
    @FXML
    Button comparelayoutbutton;
    @FXML
    ScrollPane searchscrollpane;
    @FXML
    VBox advancedvbox;
    @FXML
    VBox advancedvboxinner;
    @FXML
    Label seasonslabel;
    @FXML
    ComboBox seasonsbegincombo;
    @FXML
    ComboBox seasonsendcombo;
    @FXML
    Label playerslabel;
    @FXML
    ComboBox playercomboadvanced;
    @FXML
    Label seasontypeslabel;
    @FXML
    ComboBox seasontypescomboadvanced;
    @FXML
    Label shotdistancelabel;
    @FXML
    ComboBox distancebegincombo;
    @FXML
    ComboBox distanceendcombo;
    @FXML
    Label shotsuccesslabel;
    @FXML
    ComboBox shotsuccesscombo;
    @FXML
    Label shotvaluelabel;
    @FXML
    ComboBox shotvaluecombo;
    @FXML
    Label shottypeslabel;
    @FXML
    ComboBox shottypescombo;
    @FXML
    Label teamslabel;
    @FXML
    ComboBox teamscombo;
    @FXML
    Label hometeamslabel;
    @FXML
    ComboBox hometeamscombo;
    @FXML
    Label awayteamslabel;
    @FXML
    ComboBox awayteamscombo;
    @FXML
    Label courtareaslabel;
    @FXML
    ComboBox courtareascombo;
    @FXML
    Label courtsideslabel;
    @FXML
    ComboBox courtsidescombo;
    @FXML
    ScrollPane selectionscrollpane;
    @FXML
    VBox selectionvbox;
    @FXML
    Button searchbuttonadvanced;
    @FXML
    Label errorlabeladvanced;
    @FXML
    Label fgadv;
    @FXML
    Label fgfracadv;
    @FXML
    Label fgpercadv;
    @FXML
    Label twopointadv;
    @FXML
    Label twopointfracadv;
    @FXML
    Label twopointpercadv;
    @FXML
    Label threepointadv;
    @FXML
    Label threepointfracadv;
    @FXML
    Label threepointpercadv;
    @FXML
    GridPane shotgridadv;
    @FXML
    TextArea notestextarea;
    @FXML
    Label seasondash;
    @FXML
    Label distancedash;
    @FXML
    Label advancedintrolabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        viewButtons.add(traditionalbutton);
        viewButtons.add(gridbutton);
        viewButtons.add(heatmapbutton);
        viewButtons.add(zonebutton);
        createResponsiveComboBoxes();
        organizeZoneFXMLElements();
        initSizing();
        JSONArray jsonArrayInit;
        try {
            jsonArrayInit = getInitData();
            JSONObject jsonObjMisc2 = jsonArrayInit.getJSONObject(2);

            JSONObject jsonObjMisc1 = jsonArrayInit.getJSONObject(1);
            dateaccuracy.setText(jsonObjMisc1.getString("value"));
            JSONObject jsonObjMisc0 = jsonArrayInit.getJSONObject(0);
            updatelabel.setText(jsonObjMisc0.getString("value")
            );
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        nameHash = new LinkedHashMap();
        try {
            reader = ResourceBundle.getBundle("dbconfig");
            namelabel.setText("Version " + reader.getString("version"));
            String[] nameArray;
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
            ex.printStackTrace();
        }
        try {
            setSeasonsComboBox();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        this.searchbutton.setOnMouseClicked((Event t) -> {
            try {
                this.yearcombo.getValue().toString();
                this.playercombo.getValue().toString();
                this.seasoncombo.getValue().toString();
                switch (currentSearchModeSelection) {
                    case "simpletraditional":
                        traditional();
                        break;
                    case "simplegrid":
                        grid();
                        break;
                    case "simpleheat":
                        heat();
                        break;
                    case "simplezone":
                        zone();
                        break;
                    default:
                        traditional();
                        break;
                }
            } catch (NullPointerException ex) {
                this.errorlabel.setText("Please select one from each category");
                this.errorlabel.setVisible(true);
            }

        });
        setViewTypeButtonsOnMouseClicked();
        this.traditionalbutton.setOnMouseEntered((Event t) -> {
            font = new BigDecimal(COMBO_FONT_SIZE).multiply(new BigDecimal(imageview.getLayoutBounds().getHeight())).divide(new BigDecimal("550"), 6, RoundingMode.HALF_UP).doubleValue();
            if ((searchvbox.isVisible() && currentSearchModeSelection.equals("simpletraditional")) || (advancedvbox.isVisible() && currentSearchModeSelectionAdvanced.equals("advancedtraditional"))) {
                traditionalbutton.setStyle("-fx-font: " + font + "px \"Arial Black\";-fx-background-color: transparent;-fx-underline: true;");
            } else {
                traditionalbutton.setStyle("-fx-font: " + font + "px \"Arial\";-fx-background-color: transparent;-fx-underline: true;");
            }

        });
        this.traditionalbutton.setOnMouseExited((Event t) -> {
            font = new BigDecimal(COMBO_FONT_SIZE).multiply(new BigDecimal(imageview.getLayoutBounds().getHeight())).divide(new BigDecimal("550"), 6, RoundingMode.HALF_UP).doubleValue();
            if ((searchvbox.isVisible() && currentSearchModeSelection.equals("simpletraditional")) || (advancedvbox.isVisible() && currentSearchModeSelectionAdvanced.equals("advancedtraditional"))) {
                traditionalbutton.setStyle("-fx-font: " + font + "px \"Arial Black\";-fx-background-color: transparent;-fx-underline: false;");
            } else {
                traditionalbutton.setStyle("-fx-font: " + font + "px \"Arial\";-fx-background-color: transparent;-fx-underline: false;");
            }
        });
        this.gridbutton.setOnMouseEntered((Event t) -> {
            font = new BigDecimal(COMBO_FONT_SIZE).multiply(new BigDecimal(imageview.getLayoutBounds().getHeight())).divide(new BigDecimal("550"), 6, RoundingMode.HALF_UP).doubleValue();
            if ((searchvbox.isVisible() && currentSearchModeSelection.equals("simplegrid")) || (advancedvbox.isVisible() && currentSearchModeSelectionAdvanced.equals("advancedgrid"))) {
                gridbutton.setStyle("-fx-font: " + font + "px \"Arial Black\";-fx-background-color: transparent;-fx-underline: true;");
            } else {
                gridbutton.setStyle("-fx-font: " + font + "px \"Arial\";-fx-background-color: transparent;-fx-underline: true;");
            }

        });
        this.gridbutton.setOnMouseExited((Event t) -> {
            font = new BigDecimal(COMBO_FONT_SIZE).multiply(new BigDecimal(imageview.getLayoutBounds().getHeight())).divide(new BigDecimal("550"), 6, RoundingMode.HALF_UP).doubleValue();
            if ((searchvbox.isVisible() && currentSearchModeSelection.equals("simplegrid")) || (advancedvbox.isVisible() && currentSearchModeSelectionAdvanced.equals("advancedgrid"))) {
                gridbutton.setStyle("-fx-font: " + font + "px \"Arial Black\";-fx-background-color: transparent;-fx-underline: false;");
            } else {
                gridbutton.setStyle("-fx-font: " + font + "px \"Arial\";-fx-background-color: transparent;-fx-underline: false;");
            }
        });
        this.heatmapbutton.setOnMouseEntered((Event t) -> {
            font = new BigDecimal(COMBO_FONT_SIZE).multiply(new BigDecimal(imageview.getLayoutBounds().getHeight())).divide(new BigDecimal("550"), 6, RoundingMode.HALF_UP).doubleValue();
            if ((searchvbox.isVisible() && currentSearchModeSelection.equals("simpleheat")) || (advancedvbox.isVisible() && currentSearchModeSelectionAdvanced.equals("advancedheat"))) {
                heatmapbutton.setStyle("-fx-font: " + font + "px \"Arial Black\";-fx-background-color: transparent;-fx-underline: true;");
            } else {
                heatmapbutton.setStyle("-fx-font: " + font + "px \"Arial\";-fx-background-color: transparent;-fx-underline: true;");
            }

        });
        this.heatmapbutton.setOnMouseExited((Event t) -> {
            font = new BigDecimal(COMBO_FONT_SIZE).multiply(new BigDecimal(imageview.getLayoutBounds().getHeight())).divide(new BigDecimal("550"), 6, RoundingMode.HALF_UP).doubleValue();
            if ((searchvbox.isVisible() && currentSearchModeSelection.equals("simpleheat")) || (advancedvbox.isVisible() && currentSearchModeSelectionAdvanced.equals("advancedheat"))) {
                heatmapbutton.setStyle("-fx-font: " + font + "px \"Arial Black\";-fx-background-color: transparent;-fx-underline: false;");
            } else {
                heatmapbutton.setStyle("-fx-font: " + font + "px \"Arial\";-fx-background-color: transparent;-fx-underline: false;");
            }
        });
        this.zonebutton.setOnMouseEntered((Event t) -> {
            font = new BigDecimal(COMBO_FONT_SIZE).multiply(new BigDecimal(imageview.getLayoutBounds().getHeight())).divide(new BigDecimal("550"), 6, RoundingMode.HALF_UP).doubleValue();
            if ((searchvbox.isVisible() && currentSearchModeSelection.equals("simplezone")) || (advancedvbox.isVisible() && currentSearchModeSelectionAdvanced.equals("advancedzone"))) {
                zonebutton.setStyle("-fx-font: " + font + "px \"Arial Black\";-fx-background-color: transparent;-fx-underline: true;");
            } else {
                zonebutton.setStyle("-fx-font: " + font + "px \"Arial\";-fx-background-color: transparent;-fx-underline: true;");
            }

        });
        this.zonebutton.setOnMouseExited((Event t) -> {
            font = new BigDecimal(COMBO_FONT_SIZE).multiply(new BigDecimal(imageview.getLayoutBounds().getHeight())).divide(new BigDecimal("550"), 6, RoundingMode.HALF_UP).doubleValue();
            if ((searchvbox.isVisible() && currentSearchModeSelection.equals("simplezone")) || (advancedvbox.isVisible() && currentSearchModeSelectionAdvanced.equals("advancedzone"))) {
                zonebutton.setStyle("-fx-font: " + font + "px \"Arial Black\";-fx-background-color: transparent;-fx-underline: false;");
            } else {
                zonebutton.setStyle("-fx-font: " + font + "px \"Arial\";-fx-background-color: transparent;-fx-underline: false;");
            }
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
                ex.printStackTrace();
            }
        });
        this.playercombo.setOnAction((Event t) -> {
            try {
                setSeasonsComboBox();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        this.simplelayoutbutton.setOnMouseClicked((Event t) -> {
            initSizing();
            try {
                setPlayerComboBox();
                setSeasonsComboBox();
            } catch (IOException ex) {
                Logger.getLogger(SimpleController.class.getName()).log(Level.SEVERE, null, ex);
            }

            this.charttitle.setVisible(false);
            searchvbox.setVisible(true);
            advancedvbox.setVisible(false);
            removeAllShotsFromView();
            resetView();
            font = new BigDecimal(COMBO_FONT_SIZE).multiply(new BigDecimal(imageview.getLayoutBounds().getHeight())).divide(new BigDecimal("550"), 6, RoundingMode.HALF_UP).doubleValue();
            this.fgfrac.setText("--");
            this.fgperc.setText("--");
            this.twopointfrac.setText("--");
            this.twopointperc.setText("--");
            this.threepointfrac.setText("--");
            this.threepointperc.setText("--");
            imageview.setImage(new Image("/images/transparent.png"));

            switch (currentSearchModeSelection) {
                case "simpletraditional":
//                    traditionalbutton.setStyle("-fx-font: " + font + "px \"Arial Black\";-fx-background-color: transparent;");
//                    gridbutton.setStyle("-fx-font: " + font + "px \"Arial\";-fx-background-color: transparent;");
//                    heatmapbutton.setStyle("-fx-font: " + font + "px \"Arial\";-fx-background-color: transparent;");
//                    zonebutton.setStyle("-fx-font: " + font + "px \"Arial \";-fx-background-color: transparent;");
                    setViewTypeButtonStyle(0);

                    break;
                case "simplegrid":
//                    traditionalbutton.setStyle("-fx-font: " + font + "px \"Arial \";-fx-background-color: transparent;");
//                    gridbutton.setStyle("-fx-font: " + font + "px \"Arial Black\";-fx-background-color: transparent;");
//                    heatmapbutton.setStyle("-fx-font: " + font + "px \"Arial\";-fx-background-color: transparent;");
//                    zonebutton.setStyle("-fx-font: " + font + "px \"Arial \";-fx-background-color: transparent;");
                    setViewTypeButtonStyle(1);

                    break;
                case "simpleheat":
//                    traditionalbutton.setStyle("-fx-font: " + font + "px \"Arial \";-fx-background-color: transparent;");
//                    gridbutton.setStyle("-fx-font: " + font + "px \"Arial\";-fx-background-color: transparent;");
//                    heatmapbutton.setStyle("-fx-font: " + font + "px \"Arial Black\";-fx-background-color: transparent;");
//                    zonebutton.setStyle("-fx-font: " + font + "px \"Arial \";-fx-background-color: transparent;");
                    setViewTypeButtonStyle(2);

                    break;
                case "simplezone":
//                    traditionalbutton.setStyle("-fx-font: " + font + "px \"Arial \";-fx-background-color: transparent;");
//                    gridbutton.setStyle("-fx-font: " + font + "px \"Arial \";-fx-background-color: transparent;");
//                    heatmapbutton.setStyle("-fx-font: " + font + "px \"Arial\";-fx-background-color: transparent;");
//                    zonebutton.setStyle("-fx-font: " + font + "px \"Arial Black\";-fx-background-color: transparent;");
                    setViewTypeButtonStyle(3);

                    break;
                case "":
//                    traditionalbutton.setStyle("-fx-font: " + font + "px \"Arial \";-fx-background-color: transparent;");
//                    gridbutton.setStyle("-fx-font: " + font + "px \"Arial\";-fx-background-color: transparent;");
//                    heatmapbutton.setStyle("-fx-font: " + font + "px \"Arial\";-fx-background-color: transparent;");
//                    zonebutton.setStyle("-fx-font: " + font + "px \"Arial \";-fx-background-color: transparent;");
                    setViewTypeButtonStyle(10);

                    break;
            }
        });
        this.advancedlayoutbutton.setOnMouseClicked((Event t) -> {

            try {
                initAdvanced();
            } catch (IOException ex) {
                Logger.getLogger(SimpleController.class.getName()).log(Level.SEVERE, null, ex);
            }

            this.charttitle.setVisible(false);

            searchvbox.setVisible(false);
            advancedvbox.setVisible(true);
            resize();
            removeAllShotsFromView();
            resetView();
            this.fgfracadv.setText("--");
            this.fgpercadv.setText("--");
            this.twopointfracadv.setText("--");
            this.twopointpercadv.setText("--");
            this.threepointfracadv.setText("--");
            this.threepointpercadv.setText("--");
            imageview.setImage(new Image("/images/transparent.png"));
            font = new BigDecimal(COMBO_FONT_SIZE).multiply(new BigDecimal(imageview.getLayoutBounds().getHeight())).divide(new BigDecimal("550"), 6, RoundingMode.HALF_UP).doubleValue();
            switch (currentSearchModeSelectionAdvanced) {
                case "advancedtraditional":
                    setViewTypeButtonStyle(0);
                    break;
                case "advancedgrid":
                    setViewTypeButtonStyle(1);
                    break;
                case "advancedheat":
                    setViewTypeButtonStyle(2);
                    break;
                case "advancedzone":
                    setViewTypeButtonStyle(3);
                    break;
                case "":
                    setViewTypeButtonStyle(10);
                    break;
            }
        });
        this.searchbuttonadvanced.setOnMouseClicked((Event t) -> {
            if (!beginSeason.equals("") || !endSeason.equals("") || !beginDistance.equals("") || !endDistance.equals("") || !shotSuccess.equals("") || !shotValue.equals("")
                    || !allSelectedPlayers.isEmpty() || !allSelectedSeasonTypes.isEmpty() || !allSelectedShotTypes.isEmpty() || !allSelectedTeams.isEmpty()
                    || !allSelectedHomeTeams.isEmpty() || !allSelectedAwayTeams.isEmpty() || !allSelectedCourtAreas.isEmpty() || !allSelectedCourtSides.isEmpty()) {
                switch (currentSearchModeSelectionAdvanced) {
                    case "advancedtraditional":
                        traditional();
                        break;
                    case "advancedgrid":
                        grid();
                        break;
                    case "advancedheat":
                        heat();
                        break;
                    case "advancedzone":
                        zone();
                        break;
                    default:
                        traditional();
                        break;
                }
            } else {
                this.errorlabeladvanced.setText("Please include at least one search parameter");
                this.errorlabeladvanced.setVisible(true);
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

    private JSONArray doSimpleSearch() throws SQLException, IOException {
        removeAllShotsFromView();
//        JSONArray jsonArray = getSimpleTraditionalShotData();
        return getSimpleShotData();
    }

    private void plotTraditionalShots(JSONArray jsonArray) throws SQLException {
        if (searchvbox.isVisible()) {
            setShotGrid(jsonArray);
        } else {
            setShotGridAdvanced(jsonArray);
        }
        allShots = new LinkedHashMap();
        Circle circle;
        MissedShotIcon msi;
        BigDecimal xBig;
        BigDecimal yBig;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject eachShot = jsonArray.getJSONObject(i);
            Shot shot = new Shot(eachShot.getInt("x"), eachShot.getInt("y"), eachShot.getInt("distance"), eachShot.getInt("make"), eachShot.getString("shottype"), eachShot.getString("playtype"));
            xBig = BigDecimal.valueOf(eachShot.getInt("x"));
            yBig = BigDecimal.valueOf(eachShot.getInt("y"));
            if (eachShot.getInt("make") == 1) {
                circle = new Circle(imageview.getLayoutBounds().getHeight() * SHOT_MADE_RADIUS.divide(ORIG_HEIGHT, 6, RoundingMode.HALF_UP).doubleValue());
                circle.setFill(Color.TRANSPARENT);
                circle.setTranslateX(xBig.intValue() * imageview.getLayoutBounds().getHeight() / 470 + imageview.localToParent(imageview.getBoundsInLocal()).getMinX() + imageview.localToParent(imageview.getBoundsInLocal()).getWidth() / 2);
                circle.setTranslateY(yBig.intValue() * imageview.getLayoutBounds().getHeight() / 470 + imageview.localToParent(imageview.getBoundsInLocal()).getMinY() + imageview.localToParent(imageview.getBoundsInLocal()).getHeight() / 2 - (185.0 * imageview.localToParent(imageview.getBoundsInLocal()).getHeight() / 470));
                circle.setStrokeWidth(imageview.getLayoutBounds().getHeight() * SHOT_LINE_THICKNESS.divide(ORIG_HEIGHT, 6, RoundingMode.HALF_UP).doubleValue());
                circle.setStroke(Color.LIMEGREEN);
                circle.setManaged(false);
                allShots.put(shot, circle);
            } else {
                msi = new MissedShotIcon((xBig.intValue()) / 470,
                        ((yBig.intValue() - 55) / 470),
                        imageview.getLayoutBounds().getHeight(),
                        SHOT_MISS_START_END.divide(ORIG_HEIGHT, 6, RoundingMode.HALF_UP).doubleValue(),
                        SHOT_LINE_THICKNESS.divide(ORIG_HEIGHT, 6, RoundingMode.HALF_UP).doubleValue(),
                        imageview.localToParent(imageview.getBoundsInLocal()).getMinX() + imageview.localToParent(imageview.getBoundsInLocal()).getWidth() / 2,
                        imageview.localToParent(imageview.getBoundsInLocal()).getMinY());
                msi.getLine1().setManaged(false);
                msi.getLine2().setManaged(false);
                allShots.put(shot, msi);
            }

        }
        for (Shot each : allShots.keySet()) {
            if (each.getY() > 410) {
                continue;
            }
            if (each.getMake() == 0) {
                MissedShotIcon msiTemp = (MissedShotIcon) allShots.get(each);
                msiTemp.getLine1().setManaged(false);
                msiTemp.getLine2().setManaged(false);
                msiTemp.getLine1().setTranslateX(BigDecimal.valueOf(each.getX()).doubleValue() * imageview.getLayoutBounds().getHeight() / 470 + imageview.localToParent(imageview.getBoundsInLocal()).getMinX() + imageview.localToParent(imageview.getBoundsInLocal()).getWidth() / 2);// 50/470
                msiTemp.getLine2().setTranslateX(BigDecimal.valueOf(each.getX()).doubleValue() * imageview.getLayoutBounds().getHeight() / 470 + imageview.localToParent(imageview.getBoundsInLocal()).getMinX() + imageview.localToParent(imageview.getBoundsInLocal()).getWidth() / 2);
                msiTemp.getLine1().setTranslateY(BigDecimal.valueOf(each.getY()).doubleValue() * imageview.getLayoutBounds().getHeight() / 470 + imageview.localToParent(imageview.getBoundsInLocal()).getMinY() + imageview.localToParent(imageview.getBoundsInLocal()).getHeight() / 2 - (180.0 * imageview.localToParent(imageview.getBoundsInLocal()).getHeight() / 470));
                msiTemp.getLine2().setTranslateY(BigDecimal.valueOf(each.getY()).doubleValue() * imageview.getLayoutBounds().getHeight() / 470 + imageview.localToParent(imageview.getBoundsInLocal()).getMinY() + imageview.localToParent(imageview.getBoundsInLocal()).getHeight() / 2 - (180.0 * imageview.localToParent(imageview.getBoundsInLocal()).getHeight() / 470));
                imagegrid.getChildren().add(msiTemp.getLine1());
                imagegrid.getChildren().add(msiTemp.getLine2());
            } else {
                imagegrid.getChildren().add((Circle) allShots.get(each));
            }
        }
        if (searchvbox.isVisible()) {
            createThreadAndRun(currentSearchModeSelection);
        } else {
            createThreadAndRun(currentSearchModeSelectionAdvanced);
        }

    }

    private void resizeShots() {
        font = new BigDecimal(COMBO_FONT_SIZE).multiply(new BigDecimal(imageview.getLayoutBounds().getHeight())).divide(new BigDecimal("550"), 6, RoundingMode.HALF_UP).doubleValue();
        setViewTypeButtonStyle(0);

        double height = imageview.localToParent(imageview.getBoundsInLocal()).getHeight();
        double scaledLineThickness = SHOT_LINE_THICKNESS.divide(ORIG_HEIGHT, 6, RoundingMode.HALF_UP).doubleValue();
        double scaledLineLength = SHOT_MISS_START_END.divide(ORIG_HEIGHT, 6, RoundingMode.HALF_UP).doubleValue();
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
                    circle.setTranslateX(BigDecimal.valueOf(each.getX()).doubleValue() * height / 470 + minX + width / 2);
                    circle.setTranslateY(BigDecimal.valueOf(each.getY()).doubleValue() * height / 470 + minY + height / 2 - (185.0 * height / 470));
                    circle.setRadius(height * SHOT_MADE_RADIUS.divide(ORIG_HEIGHT, 6, RoundingMode.HALF_UP).doubleValue());
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
        UserInputComboBox playerComboUser = new UserInputComboBox(playercombo);
        this.activePlayers = new HashMap();
        String year = yearcombo.getValue().toString();
        JSONArray jsonArray = getActivePlayersData();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject eachPlayer = jsonArray.getJSONObject(i);
            this.activePlayers.put(eachPlayer.getInt("id"), eachPlayer.getString("firstname") + " " + eachPlayer.getString("lastname"));
        }
//        ArrayList<String> activeList = new ArrayList();
//
//        for (int each : this.activePlayers.keySet()) {
//            activeList.add(activePlayers.get(each).trim());
//        }
//        Collections.sort(activeList);
        HashMap<String, String> names = new HashMap();
        ArrayList<String> fixedNames = new ArrayList();
        for (int each : this.activePlayers.keySet()) {
            names.put(activePlayers.get(each).replaceAll("[^A-Za-z0-9]", "").toLowerCase(), activePlayers.get(each));
            fixedNames.add(activePlayers.get(each).replaceAll("[^A-Za-z0-9]", "").toLowerCase());
        }
        Collections.sort(fixedNames);
        LinkedList<String> realNames = new LinkedList();
        for (String each : fixedNames) {
            realNames.add(names.get(each));
        }
        if (seasoncombo.getValue() != null) {
            this.previousSeason = seasoncombo.getValue().toString();
        }
//        this.playercombo.setItems(FXCollections.observableArrayList(activeList));
        playerComboUser.getComboBox().setItems(FXCollections.observableArrayList(realNames));
        if (previousSeason != null && playercombo.getValue() != null && seasoncombo.getItems().contains(previousSeason)) {
            this.seasoncombo.getSelectionModel().select(previousSeason);
        } else {
            this.seasoncombo.getSelectionModel().clearSelection();
        }
        if (realNames.contains(previousPlayer)) {
            this.playercombo.getSelectionModel().select(previousPlayer);
        }
    }

    private void setAdvancedPlayerComboBox() throws IOException {
        UserInputComboBox playerComboUserAdvanced = new UserInputComboBox(playercomboadvanced);
        this.activePlayers = new HashMap();
        JSONArray jsonArray = getInitAllPlayersData();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject eachPlayer = jsonArray.getJSONObject(i);
            this.activePlayers.put(eachPlayer.getInt("id"), eachPlayer.getString("firstname") + " " + eachPlayer.getString("lastname"));
        }
        HashMap<String, String> names = new HashMap();
        ArrayList<String> fixedNames = new ArrayList();
        for (int each : this.activePlayers.keySet()) {
            names.put(activePlayers.get(each).replaceAll("[^A-Za-z0-9]", "").toLowerCase(), activePlayers.get(each));
            fixedNames.add(activePlayers.get(each).replaceAll("[^A-Za-z0-9]", "").toLowerCase());
        }
        Collections.sort(fixedNames);
        LinkedList<String> realNames = new LinkedList();
        for (String each : fixedNames) {
            realNames.add(names.get(each));
        }
//        this.playercomboadvanced.setItems(FXCollections.observableArrayList(realNames));
        playerComboUserAdvanced.getComboBox().setItems(FXCollections.observableArrayList(realNames));

    }

    private void setSeasonsComboBox() throws IOException {
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

    private void setAdvancedSeasonsComboBox() throws IOException {
        ArrayList<String> seasons = new ArrayList();
        seasons.add("Preseason");
        seasons.add("Regular Season");
        seasons.add("Playoffs");
        seasontypescomboadvanced.setItems(FXCollections.observableArrayList(seasons));
        seasontypescomboadvanced.getSelectionModel().clearSelection();

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

    private JSONArray getShotTypesData() throws IOException {
        JSONObject jsonObjOut = new JSONObject();
        jsonObjOut.put("selector", "shottypes");
        Main.getPrintWriterOut().println(jsonObjOut.toString());
        return new JSONArray(Main.getServerResponse().readLine());

    }

    private JSONArray getSimpleShotData() throws IOException {
        JSONObject jsonObjOut = new JSONObject();
        jsonObjOut.put("selector", currentSearchModeSelection);
        jsonObjOut.put("year", this.yearcombo.getValue().toString());
        jsonObjOut.put("playername", nameHash.get(this.playercombo.getValue().toString()));
        jsonObjOut.put("seasontype", this.seasoncombo.getValue().toString());
        Main.getPrintWriterOut().println(jsonObjOut.toString());
        return new JSONArray(Main.getServerResponse().readLine());
    }

    private void plotGrid(JSONArray jsonArray) throws IOException {
        if (searchvbox.isVisible()) {
            setShotGrid(jsonArray);
        } else {
            setShotGridAdvanced(jsonArray);
        }
        Coordinate coord;
        coordAverages = new LinkedHashMap();
        for (int j = -55; j < 400; j = j + (int) SQUARE_SIZE_ORIG) {
            for (int i = -250; i < 250; i = i + (int) SQUARE_SIZE_ORIG) {
                coord = new Coordinate(i, j);
                LinkedList info = new LinkedList();
                info.add(0.0);
                info.add(0.0);
                info.add(0.0);
                coordAverages.put(coord, info);
            }
        }
        double factor = 0.007;
        shotCounter = 0;
        HashMap<String, BigDecimal> averages = useGridAverages();
        double x;
        int y;
        int make;
        int counter = 0;
        allShots = new LinkedHashMap();
        JSONObject eachShot;
        for (int i = 0; i < jsonArray.length(); i++) {
            eachShot = jsonArray.getJSONObject(i);
            counter++;
            if (eachShot.getInt("y") >= 400) {
                continue;
            }
            shotCounter++;
            y = eachShot.getInt("y");
            x = eachShot.getInt("x");
            make = eachShot.getInt("make");
            for (Coordinate each : coordAverages.keySet()) {
                if (x < each.getX() + 5 + SQUARE_SIZE_ORIG * 1.5 && x >= each.getX() + 5 - SQUARE_SIZE_ORIG * 1.5 && y < each.getY() + 5 + SQUARE_SIZE_ORIG * 1.5 && y >= each.getY() + 5 - SQUARE_SIZE_ORIG * 1.5) {
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
        min = 1;
        double minFactor = 0.00045;
        if (shotCounter * minFactor > 1) {
            min = shotCounter * minFactor;
        } else {
            factor = 4.1008 * Math.pow(shotCounter, -0.798);
        }
        maxShotsPerMaxSquare = (int) (factor * shotCounter);
        if (maxShotsPerMaxSquare == 0) {
            maxShotsPerMaxSquare = 1;
        }
        squareSize = imageview.getLayoutBounds().getWidth() / 50;
        allTiles = new LinkedList();
        String temp;
        double avg;
        for (Coordinate each2 : coordValue.keySet()) {
            Rectangle square = new Rectangle();
            if (coordAverages.get(each2).get(1) < maxShotsPerMaxSquare && coordAverages.get(each2).get(1) > min) {
                square.setHeight((coordAverages.get(each2).get(1) / maxShotsPerMaxSquare * squareSize) * 0.9);
                square.setWidth((coordAverages.get(each2).get(1) / maxShotsPerMaxSquare * squareSize) * 0.9);
            } else if (coordAverages.get(each2).get(1) >= maxShotsPerMaxSquare) {
                square.setHeight(squareSize * 0.9);
                square.setWidth(squareSize * 0.9);
            }
            temp = "(" + each2.getX() + "," + each2.getY() + ")";
            avg = averages.get(temp).doubleValue();
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
            square.setTranslateX((each2.getX() + 5) * imageview.getLayoutBounds().getHeight() / 470);
            square.setTranslateY(each2.getY() * imageview.getLayoutBounds().getHeight() / 470 - (175.0 * imageview.localToParent(imageview.getBoundsInLocal()).getHeight() / 470));

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
        if (searchvbox.isVisible()) {
            createThreadAndRun(currentSearchModeSelection);
        } else {
            createThreadAndRun(currentSearchModeSelectionAdvanced);
        }
    }

    private void plotHeat(JSONArray jsonArray) throws IOException {
        if (searchvbox.isVisible()) {
            setShotGrid(jsonArray);
        } else {
            setShotGridAdvanced(jsonArray);
        }
        this.coordAverages = new LinkedHashMap();
        Coordinate coord;
        for (int x = -250; x < 251; x++) {
            for (int y = -52; y < 400; y++) {
                coord = new Coordinate(x, y);
                LinkedList info = new LinkedList();
                info.add(0.0);
                info.add(0.0);
                info.add(0.0);
                coordAverages.put(coord, info);
            }
        }
        int counter = 0;
        Coordinate tempCoord;
        JSONObject eachShot;
        shotCounter = 0;
        for (int i = 0; i < jsonArray.length(); i++) {
            eachShot = jsonArray.getJSONObject(i);
            counter++;
            if (eachShot.getInt("y") >= 400) {
                continue;
            }
            shotCounter++;
            tempCoord = new Coordinate(eachShot.getInt("x"), eachShot.getInt("y"));
            coordAverages.get(tempCoord).set(1, coordAverages.get(tempCoord).get(1) + 1);
            if (eachShot.getInt("make") == 1) {
                coordAverages.get(tempCoord).set(0, coordAverages.get(tempCoord).get(0) + 1);
            }
        }
        for (Coordinate each : coordAverages.keySet()) {
            if (coordAverages.get(each).get(1) != 0) {
                coordAverages.get(each).set(2, coordAverages.get(each).get(0) * 1.0 / coordAverages.get(each).get(1) * 1.0);
            }

        }
//        coordValue = new LinkedHashMap();
        coordValue = new ConcurrentHashMap();

        try {
            ultraFineHeatMapThreader();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
//        normalHeatLooper();
//        optimizedNormalHeatLooper();
        double weight = 0.5;
        int radius = 25;
        RadialGradient rg1 = new RadialGradient(0, 0, 0.5, 0.5, 1, true, CycleMethod.NO_CYCLE, new Stop[]{
            new Stop(0, Color.web("#bc53f8")),
            new Stop(weight, Color.TRANSPARENT)});
        RadialGradient rg2 = new RadialGradient(0, 0, 0.5, 0.5, 1, true, CycleMethod.NO_CYCLE, new Stop[]{
            new Stop(0, Color.web("#dd76ff")),
            new Stop(weight, Color.TRANSPARENT)});
        RadialGradient rg3 = new RadialGradient(0, 0, 0.5, 0.5, 1, true, CycleMethod.NO_CYCLE, new Stop[]{
            new Stop(0, Color.web("#e696fa")),
            new Stop(weight, Color.TRANSPARENT)});
        RadialGradient rg4 = new RadialGradient(0, 0, 0.5, 0.5, 1, true, CycleMethod.NO_CYCLE, new Stop[]{
            new Stop(0, Color.web("#c4b8ff")),
            new Stop(weight, Color.TRANSPARENT)});
        RadialGradient rg5 = new RadialGradient(0, 0, 0.5, 0.5, 1, true, CycleMethod.NO_CYCLE, new Stop[]{
            new Stop(0, Color.web("#6bb2f8")),
            new Stop(weight, Color.TRANSPARENT)});
        RadialGradient rg6 = new RadialGradient(0, 0, 0.5, 0.5, 1, true, CycleMethod.NO_CYCLE, new Stop[]{
            new Stop(0, Color.web("#62c8ff")),
            new Stop(weight, Color.TRANSPARENT)});
        RadialGradient rg7 = new RadialGradient(0, 0, 0.5, 0.5, 1, true, CycleMethod.NO_CYCLE, new Stop[]{
            new Stop(0, Color.web("#90ebff")),
            new Stop(weight, Color.TRANSPARENT)});
        ArrayList<Circle> circles1 = new ArrayList();
        ArrayList<Circle> circles2 = new ArrayList();
        ArrayList<Circle> circles3 = new ArrayList();
        ArrayList<Circle> circles4 = new ArrayList();
        ArrayList<Circle> circles5 = new ArrayList();
        ArrayList<Circle> circles6 = new ArrayList();
        ArrayList<Circle> circles7 = new ArrayList();
        double maxValue = 0.0;
        for (Coordinate each : coordValue.keySet()) {
            if (coordValue.get(each) > maxValue) {
                maxValue = coordValue.get(each);
            }
        }
        if (maxValue != 0) {

            maxValue = maxValue * (500 * 1.0 / shotCounter);
            maxCutoff = 0.00004 * shotCounter / maxValue + 0.3065;
            diff = maxCutoff / 7;
//            diff = maxCutoff / (7+((int)(shotCounter/5000)));
            allHeatCircles = new LinkedList();
            for (Coordinate each : coordValue.keySet()) {
                double value = coordValue.get(each);
                if (value <= maxValue * (maxCutoff - (diff * 6))) {
                    Circle circle = new Circle(0);
                    allHeatCircles.add(circle);
                } else if (value > maxValue * (maxCutoff - (diff * 6)) && value <= maxValue * (maxCutoff - (diff * 5))) {
                    Circle circle = new Circle(radius, rg1);
                    setCircle(circle, each.getX(), each.getY());
                    circles1.add(circle);
                    allHeatCircles.add(circle);
                } else if (value > maxValue * (maxCutoff - (diff * 5)) && value <= maxValue * (maxCutoff - (diff * 4))) {
                    Circle circle = new Circle(radius, rg2);
                    setCircle(circle, each.getX(), each.getY());
                    circles2.add(circle);
                    allHeatCircles.add(circle);
                } else if (value > maxValue * (maxCutoff - (diff * 4)) && value <= maxValue * (maxCutoff - (diff * 3))) {
                    Circle circle = new Circle(radius, rg3);
                    setCircle(circle, each.getX(), each.getY());
                    circles3.add(circle);
                    allHeatCircles.add(circle);
                } else if (value > maxValue * (maxCutoff - (diff * 3)) && value <= maxValue * (maxCutoff - (diff * 2))) {
                    Circle circle = new Circle(radius, rg4);
                    setCircle(circle, each.getX(), each.getY());
                    circles4.add(circle);
                    allHeatCircles.add(circle);
                } else if (value > maxValue * (maxCutoff - (diff * 2)) && value <= maxValue * (maxCutoff - (diff * 1))) {
                    Circle circle = new Circle(radius, rg5);
                    setCircle(circle, each.getX(), each.getY());
                    circles5.add(circle);
                    allHeatCircles.add(circle);
                } else if (value > maxValue * (maxCutoff - (diff * 1)) && value <= maxValue * maxCutoff) {
                    Circle circle = new Circle(radius, rg6);
                    setCircle(circle, each.getX(), each.getY());
                    circles6.add(circle);
                    allHeatCircles.add(circle);
                } else {
                    Circle circle = new Circle(radius, rg7);
                    setCircle(circle, each.getX(), each.getY());
                    circles7.add(circle);
                    allHeatCircles.add(circle);
                }
            }
        }
        for (Circle circle : circles1) {
            imagegrid.getChildren().add(circle);
        }
        for (Circle circle : circles2) {
            imagegrid.getChildren().add(circle);
        }
        for (Circle circle : circles3) {
            imagegrid.getChildren().add(circle);
        }
        for (Circle circle : circles4) {
            imagegrid.getChildren().add(circle);
        }
        for (Circle circle : circles5) {
            imagegrid.getChildren().add(circle);
        }
        for (Circle circle : circles6) {
            imagegrid.getChildren().add(circle);
        }
        for (Circle circle : circles7) {
            imagegrid.getChildren().add(circle);
        }
        if (searchvbox.isVisible()) {
            createThreadAndRun(currentSearchModeSelection);
        } else {
            createThreadAndRun(currentSearchModeSelectionAdvanced);
        }

    }

    private void idwGrid() {
        long start = System.nanoTime();
        coordValue = new ConcurrentHashMap();
//        coordValue = new LinkedHashMap();
        double predictedValue = 0;
        double aSum = 0;
        double bSum = 0;
        int p = 2;
        double valueI = 0;
        int counter = 0;
        for (Coordinate each : coordAverages.keySet()) {
            if (each.getX() % OFFSET == 0 && (each.getY() - 5) % OFFSET == 0) {
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

        long end = System.nanoTime();
//        System.out.println("idwGrid: " + (end - start) / 1000000000 + " seconds");

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
        return hashmap;
    }

    private void resizeGrid() {
        font = new BigDecimal(COMBO_FONT_SIZE).multiply(new BigDecimal(imageview.getLayoutBounds().getHeight())).divide(new BigDecimal("550"), 6, RoundingMode.HALF_UP).doubleValue();
        setViewTypeButtonStyle(1);

        gridbackground.setWidth(imageview.localToParent(imageview.getBoundsInLocal()).getWidth());
        gridbackground.setHeight(imageview.localToParent(imageview.getBoundsInLocal()).getHeight());
        imagegrid.setMaxWidth(imageview.localToParent(imageview.getBoundsInLocal()).getWidth());
        imagegrid.setMaxHeight(imageview.localToParent(imageview.getBoundsInLocal()).getHeight());
        imagegrid.setMinWidth(imageview.localToParent(imageview.getBoundsInLocal()).getWidth());
        imagegrid.setMinHeight(imageview.localToParent(imageview.getBoundsInLocal()).getHeight());
        imagegrid.setPrefWidth(imageview.localToParent(imageview.getBoundsInLocal()).getWidth());
        imagegrid.setPrefHeight(imageview.localToParent(imageview.getBoundsInLocal()).getHeight());
        gridlegendcolor.setTranslateX(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * (-155.0 / 470));
        gridlegendcolor.setTranslateY(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * (185.0 / 470));
        gridlegendsize.setTranslateX(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * (155.0 / 470));
        gridlegendsize.setTranslateY(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * (185.0 / 470));
        gridlegendcolor.setMaxWidth(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 170.0 / 470);
        gridlegendcolor.setMinWidth(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 170.0 / 470);
        gridlegendcolor.setPrefWidth(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 170.0 / 470);
//        gridlegendcolor.setMaxHeight(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 80.0 / 470);
//        gridlegendcolor.setMinHeight(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 80.0 / 470);
//        gridlegendcolor.setPrefHeight(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 80.0 / 470);
        gridlegendsize.setMaxWidth(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 170.0 / 470);
        gridlegendsize.setMinWidth(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 170.0 / 470);
        gridlegendsize.setPrefWidth(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 170.0 / 470);
//        gridlegendsize.setMaxHeight(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 80.0 / 470);
//        gridlegendsize.setMinHeight(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 80.0 / 470);
//        gridlegendsize.setPrefHeight(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 80.0 / 470);

        Rectangle tempRect;
        double nodeCounter = 0;
        for (Node each : gridsizelegendgradient.getChildren()) {
            tempRect = (Rectangle) each;
            tempRect.setWidth(((nodeCounter * 1.5) + 2) * imageview.localToParent(imageview.getBoundsInLocal()).getHeight() / 470);
            tempRect.setHeight(((nodeCounter * 1.5) + 2) * imageview.localToParent(imageview.getBoundsInLocal()).getHeight() / 470);
            nodeCounter++;
        }
        gridcolorlegendtoplabel.setStyle("-fx-font: " + imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 13.0 / 470 + "px \"Lucida Sans\"; ");
        gridsizelegendtoplabel.setStyle("-fx-font: " + imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 13.0 / 470 + "px \"Lucida Sans\";");
        gridcolorlegendlowerlabel.setStyle("-fx-font: " + imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 11.0 / 470 + "px \"Lucida Sans\";");
        gridcolorlegendupperlabel.setStyle("-fx-font: " + imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 11.0 / 470 + "px \"Lucida Sans\";");
        gridsizelegendlowerlabel.setStyle("-fx-font: " + imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 11.0 / 470 + "px \"Lucida Sans\";");
        gridsizelegendupperlabel.setStyle("-fx-font: " + imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 11.0 / 470 + "px \"Lucida Sans\";");
        gridcolorlegendgradient.setWidth(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 153.0 / 470);
        gridcolorlegendgradient.setHeight(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 17.0 / 470);

        double height = imageview.getLayoutBounds().getHeight();
        double width = imageview.getLayoutBounds().getWidth();
        squareSize = width / 50;
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
            square.setTranslateX((each2.getX() + 5) * height / 470);
            square.setTranslateY(each2.getY() * height / 470 - (175.0 * height / 470));

            counter++;
        }
    }

    private void resize() {
        font = new BigDecimal(COMBO_FONT_SIZE).multiply(new BigDecimal(imageview.getLayoutBounds().getHeight())).divide(new BigDecimal("550"), 6, RoundingMode.HALF_UP).doubleValue();
        fontGrid = new BigDecimal(STAT_GRID_FONT_SIZE).multiply(new BigDecimal(imageview.getLayoutBounds().getHeight())).divide(new BigDecimal("550"), 6, RoundingMode.HALF_UP).doubleValue();
        setViewTypeButtonStyle(10);

        try {
            if (searchvbox.isVisible()) {
                switch (currentSearchModeSelection) {
                    case "simpletraditional":
                        resizeShots();
                        break;
                    case "simplegrid":
                        resizeGrid();
                        break;
                    case "simpleheat":
                        resizeHeat();
                        break;
                    case "simplezone":
                        resizeZone();
                        break;
                    default:
                        traditionalbutton.setStyle("-fx-font: " + font + "px \"Arial Black\";-fx-background-color: transparent; ");

                }
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
                createThreadAndRun(currentSearchModeSelection);

            } else {
                switch (currentSearchModeSelectionAdvanced) {
                    case "advancedtraditional":
                        resizeShots();
                        break;
                    case "advancedgrid":
                        resizeGrid();
                        break;
                    case "advancedheat":
                        resizeHeat();
                        break;
                    case "advancedzone":
                        resizeZone();
                        break;
                    default:
                        traditionalbutton.setStyle("-fx-font: " + font + "px \"Arial Black\";-fx-background-color: transparent; ");

                }
                fgadv.setStyle("-fx-font: " + font * 2 + "px \"Tahoma Bold\";");
                fgfracadv.setStyle("-fx-font: " + fontGrid * 0.75 + "px \"Tahoma Bold\";");
                fgpercadv.setStyle("-fx-font: " + fontGrid * 0.75 + "px \"Tahoma Bold\";");
                twopointadv.setStyle("-fx-font: " + font * 2 + "px \"Tahoma Bold\";");
                twopointfracadv.setStyle("-fx-font: " + fontGrid * 0.75 + "px \"Tahoma Bold\";");
                twopointpercadv.setStyle("-fx-font: " + fontGrid * 0.75 + "px \"Tahoma Bold\";");
                threepointadv.setStyle("-fx-font: " + font * 2 + "px \"Tahoma Bold\";");
                threepointfracadv.setStyle("-fx-font: " + fontGrid * 0.75 + "px \"Tahoma Bold\";");
                threepointpercadv.setStyle("-fx-font: " + fontGrid * 0.75 + "px \"Tahoma Bold\";");
                advancedintrolabel.setStyle("-fx-font: " + fontGrid + "px \"Tahoma Bold\";");
                seasonslabel.setStyle("-fx-font: " + font + "px \"Arial\";");
                seasonsbegincombo.setStyle("-fx-font: " + font + "px \"Arial\";");
                seasondash.setStyle("-fx-font: " + font * 1.5 + "px \"Arial\";");
                seasonsendcombo.setStyle("-fx-font: " + font + "px \"Arial\";");
                shotdistancelabel.setStyle("-fx-font: " + font + "px \"Arial\";");
                distancebegincombo.setStyle("-fx-font: " + font + "px \"Arial\";");
                distancedash.setStyle("-fx-font: " + font * 1.5 + "px \"Arial\";");
                distanceendcombo.setStyle("-fx-font: " + font + "px \"Arial\";");
                playerslabel.setStyle("-fx-font: " + font + "px \"Arial\";");
                playercomboadvanced.setStyle("-fx-font: " + font + "px \"Arial\";");
                seasontypeslabel.setStyle("-fx-font: " + font + "px \"Arial\";");
                seasontypescomboadvanced.setStyle("-fx-font: " + font + "px \"Arial\";");
                shotsuccesslabel.setStyle("-fx-font: " + font + "px \"Arial\";");
                shotsuccesscombo.setStyle("-fx-font: " + font + "px \"Arial\";");
                shotvaluelabel.setStyle("-fx-font: " + font + "px \"Arial\";");
                shotvaluecombo.setStyle("-fx-font: " + font + "px \"Arial\";");
                shottypeslabel.setStyle("-fx-font: " + font + "px \"Arial\";");
                shottypescombo.setStyle("-fx-font: " + font + "px \"Arial\";");
                teamslabel.setStyle("-fx-font: " + font + "px \"Arial\";");
                teamscombo.setStyle("-fx-font: " + font + "px \"Arial\";");
                hometeamslabel.setStyle("-fx-font: " + font + "px \"Arial\";");
                hometeamscombo.setStyle("-fx-font: " + font + "px \"Arial\";");
                awayteamslabel.setStyle("-fx-font: " + font + "px \"Arial\";");
                awayteamscombo.setStyle("-fx-font: " + font + "px \"Arial\";");
                courtareaslabel.setStyle("-fx-font: " + font + "px \"Arial\";");
                courtareascombo.setStyle("-fx-font: " + font + "px \"Arial\";");
                courtsideslabel.setStyle("-fx-font: " + font + "px \"Arial\";");
                courtsidescombo.setStyle("-fx-font: " + font + "px \"Arial\";");
                searchscrollpane.setMinHeight(advancedvbox.getLayoutBounds().getHeight() * 0.4);
                searchscrollpane.setMaxHeight(advancedvbox.getLayoutBounds().getHeight() * 0.4);
                HBox hbox;
                Button button;
                Label label;
                for (Node each : selectionvbox.getChildren()) {
                    try {
                        hbox = (HBox) each;
                        for (Node eachInner : hbox.getChildren()) {
                            if (eachInner.getClass().equals(Button.class)) {
//                                button = (Button) eachInner;
//                                button.setStyle("-fx-font: " + font + "px \"Arial\"; -fx-text-fill: red;-fx-background-color: transparent; ");
                            } else if (eachInner.getClass().equals(Label.class)) {
                                label = (Label) eachInner;
                                label.setStyle("-fx-font: " + font * 0.85 + "px \"Arial\";");
                            }
                        }
                    } catch (Exception ex) {

                    }
                }
                createThreadAndRun(currentSearchModeSelectionAdvanced);
            }
            mask.setWidth(imageview.getLayoutBounds().getWidth());
            mask.setHeight(imageview.getLayoutBounds().getHeight());
            imagegrid.setClip(mask);
            this.simplelayoutbutton.setStyle("-fx-font: " + font + "px \"Serif\";");
            this.advancedlayoutbutton.setStyle("-fx-font: " + font + "px \"Serif\";");
            this.comparelayoutbutton.setStyle("-fx-font: " + font + "px \"Serif\";");
            titlelabel.setMinWidth(Region.USE_PREF_SIZE);
            titlelabel.setStyle("-fx-font: " + fontGrid * 3 + "px \"Serif\"; ");
            charttitle.setStyle("-fx-font: " + fontGrid * 1.15 + "px \"Arial Italic\";");
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
            VBox.setMargin(yearcombo, new Insets(20, 0, 0, 0));
            VBox.setMargin(playercombo, new Insets(20, 0, 0, 0));
            VBox.setMargin(seasoncombo, new Insets(20, 0, 0, 0));
            VBox.setMargin(searchbutton, new Insets(20, 0, 0, 0));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void resizeHeat() {
        font = new BigDecimal(COMBO_FONT_SIZE).multiply(new BigDecimal(imageview.getLayoutBounds().getHeight())).divide(new BigDecimal("550"), 6, RoundingMode.HALF_UP).doubleValue();
        setViewTypeButtonStyle(2);

        Circle tempCircle;
        int keyCounter = 0;
        for (Coordinate each : coordValue.keySet()) {
            tempCircle = (Circle) allHeatCircles.get(keyCounter);
            tempCircle.setTranslateX(each.getX() * 1.0 * imageview.getLayoutBounds().getHeight() / 470);
            tempCircle.setTranslateY(each.getY() * 1.0 * imageview.getLayoutBounds().getHeight() / 470 - (185.0 * imageview.localToParent(imageview.getBoundsInLocal()).getHeight() / 470));
            keyCounter++;
        }

        heatlegend.setTranslateX(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * (-155.0 / 470));
        heatlegend.setTranslateY(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * (185.0 / 470));
        heatlegend.setMaxWidth(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 170.0 / 470);
        heatlegend.setMinWidth(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 170.0 / 470);
        heatlegend.setPrefWidth(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 170.0 / 470);
        heatlegendgradient.setWidth(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 153.0 / 470);
        heatlegendgradient.setHeight(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 17.0 / 470);
        heatlegendtoplabel.maxWidthProperty().bind(heatlegend.maxWidthProperty());
        heatlegendlowerlabel.maxWidthProperty().bind(heatlegend.maxWidthProperty().multiply(0.5));
        heatlegendupperlabel.maxWidthProperty().bind(heatlegend.maxWidthProperty().multiply(0.5));
        heatlegendlowerlabel.minWidthProperty().bind(heatlegend.maxWidthProperty().multiply(0.45));
        heatlegendupperlabel.minWidthProperty().bind(heatlegend.maxWidthProperty().multiply(0.45));
        heatlegendtoplabel.setStyle("-fx-font: " + imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 13.0 / 470 + "px \"Lucida Sans\";");
        heatlegendlowerlabel.setStyle("-fx-font: " + imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 11.0 / 470 + "px \"Lucida Sans\";");
        heatlegendupperlabel.setStyle("-fx-font: " + imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 11.0 / 470 + "px \"Lucida Sans\";");
    }

    private void resizeZone() {
        font = new BigDecimal(COMBO_FONT_SIZE).multiply(new BigDecimal(imageview.getLayoutBounds().getHeight())).divide(new BigDecimal("550"), 6, RoundingMode.HALF_UP).doubleValue();
        setViewTypeButtonStyle(3);
        mask = new Rectangle(imageview.getLayoutBounds().getWidth(), imageview.getLayoutBounds().getHeight());
        imagegrid.setClip(mask);
        Node each;
        Label eachLabel;
        Label eachLabelPercent;
        double width = imageview.getLayoutBounds().getWidth();
        double height = imageview.getLayoutBounds().getHeight();
        double topFontSize = 18.0;
        double bottomFontSize = 16.0;
        for (int i = 1; i < 16; i++) {
            each = allShapes.get(i - 1);
            eachLabel = allLabels.get(i - 1);
            eachLabelPercent = allPercentLabels.get(i - 1);
            each.setScaleX(width / 500);
            each.setScaleY(height / 470);
            eachLabel.setStyle("-fx-font: " + height * topFontSize / 470 + "px \"PT Sans Narrow\"; -fx-font-weight: bold;");
            eachLabelPercent.setStyle("-fx-font: " + height * bottomFontSize / 470 + "px \"PT Sans Narrow\";-fx-font-weight: bold;");
            eachLabel.setMinWidth(width * 90.0 / 470);
            eachLabelPercent.setMinWidth(width * 90.0 / 470);
            switch (i) {
                case 1:
                    eachLabel.setMinWidth(width * 120.0 / 470);
                    eachLabelPercent.setMinWidth(width * 120.0 / 470);
                    each.setTranslateY(height / -2 + (each.getLayoutBounds().getHeight() * each.getScaleY() / 2) + 1.5 * each.getScaleY());
                    eachLabel.setTranslateY(height / -2 + eachLabel.getHeight() / 2 - 3.0 * height / 470);
                    eachLabelPercent.setTranslateY(height / -2 + eachLabel.getHeight() / 2 + 15.0 * height / 470);

                    break;
                case 2:
                    each.setScaleX(width / 500 * 0.98);
                    each.setTranslateY(height / -2 + (each.getLayoutBounds().getHeight() * each.getScaleY() / 2));
                    eachLabel.setTranslateY(height / -2 + eachLabel.getHeight() / 2 + 88.0 * height / 470);
                    eachLabelPercent.setTranslateY(height / -2 + eachLabel.getHeight() / 2 + 108.0 * height / 470);
                    break;
                case 3:
                    each.setTranslateX((each.getLayoutBounds().getWidth() * each.getScaleX() / -2) + 2.5 * each.getScaleX());
                    each.setTranslateY(height / -2 + (each.getLayoutBounds().getHeight() * each.getScaleY() / 2));
                    eachLabel.setTranslateX(width / -2 + eachLabel.getWidth() / 2 + 85.0 * width / 500);
                    eachLabel.setTranslateY(height / -2 + eachLabel.getHeight() / 2 + 55.0 * height / 470);
                    eachLabelPercent.setTranslateX(width / -2 + eachLabel.getWidth() / 2 + 85.0 * width / 500);
                    eachLabelPercent.setTranslateY(height / -2 + eachLabel.getHeight() / 2 + 75.0 * height / 470);
                    break;
                case 4:
                    each.setTranslateY(height / -2 + (each.getLayoutBounds().getHeight() * each.getScaleY() / 2) + height * 55.0 / 470 - 2.5 * each.getScaleY());
                    eachLabel.setTranslateY(height / -2 + eachLabel.getHeight() / 2 + 150.0 * height / 470);
                    eachLabelPercent.setTranslateY(height / -2 + eachLabel.getHeight() / 2 + 170.0 * height / 470);
                    break;
                case 5:
                    each.setTranslateX((each.getLayoutBounds().getWidth() * each.getScaleX() / 2) - 3 * each.getScaleX());
                    each.setTranslateY(height / -2 + (each.getLayoutBounds().getHeight() * each.getScaleY() / 2) - 1.25 * each.getScaleY());
                    eachLabel.setTranslateX(width / -2 + eachLabel.getWidth() / 2 + 325.0 * width / 500);
                    eachLabel.setTranslateY(height / -2 + eachLabel.getHeight() / 2 + 55.0 * height / 470);
                    eachLabelPercent.setTranslateX(width / -2 + eachLabel.getWidth() / 2 + 325.0 * width / 500);
                    eachLabelPercent.setTranslateY(height / -2 + eachLabel.getHeight() / 2 + 75.0 * height / 470);
                    break;
                case 6:
                    each.setTranslateX(((each.getLayoutBounds().getWidth() / -2) + 22.5) * each.getScaleX());
                    each.setTranslateY(height / -2 + (each.getLayoutBounds().getHeight() * each.getScaleY() / 2));
                    eachLabel.setTranslateX(width / -2 + eachLabel.getWidth() / 2 + 20.0 * width / 500);
                    eachLabel.setTranslateY(height / -2 + eachLabel.getHeight() / 2 + 100.0 * height / 470);
                    eachLabelPercent.setTranslateX(width / -2 + eachLabel.getWidth() / 2 + 20.0 * width / 500);
                    eachLabelPercent.setTranslateY(height / -2 + eachLabel.getHeight() / 2 + 120.0 * height / 470);
                    break;
                case 7:
                    each.setTranslateX((each.getLayoutBounds().getWidth() * each.getScaleX() / -2) + 5.25 * each.getScaleX());
                    each.setTranslateY(height / -2 + (each.getLayoutBounds().getHeight() * each.getScaleY() / 2) + height * 55.0 / 470 - 9.75 * each.getScaleY());
                    eachLabel.setTranslateX(width / -2 + eachLabel.getWidth() / 2 + 80.0 * width / 500);
                    eachLabel.setTranslateY(height / -2 + eachLabel.getHeight() / 2 + 185.0 * height / 470);
                    eachLabelPercent.setTranslateX(width / -2 + eachLabel.getWidth() / 2 + 80.0 * width / 500);
                    eachLabelPercent.setTranslateY(height / -2 + eachLabel.getHeight() / 2 + 205.0 * height / 470);
                    break;
                case 8:
                    each.setTranslateY(height / -2 + (each.getLayoutBounds().getHeight() * each.getScaleY() / 2) + height * 55.0 / 470 - 10 * each.getScaleY());
                    eachLabel.setTranslateY(height / -2 + eachLabel.getHeight() / 2 + 225.0 * height / 470);
                    eachLabelPercent.setTranslateY(height / -2 + eachLabel.getHeight() / 2 + 245.0 * height / 470);
                    break;
                case 9:
                    each.setTranslateX((each.getLayoutBounds().getWidth() * each.getScaleX() / 2) - 5.25 * each.getScaleX());
                    each.setTranslateY(height / -2 + (each.getLayoutBounds().getHeight() * each.getScaleY() / 2) + height * 55.0 / 470 - 9.75 * each.getScaleY());
                    eachLabel.setTranslateX(width / -2 + eachLabel.getWidth() / 2 + 330.0 * width / 500);
                    eachLabel.setTranslateY(height / -2 + eachLabel.getHeight() / 2 + 185.0 * height / 470);
                    eachLabelPercent.setTranslateX(width / -2 + eachLabel.getWidth() / 2 + 330.0 * width / 500);
                    eachLabelPercent.setTranslateY(height / -2 + eachLabel.getHeight() / 2 + 205.0 * height / 470);
                    break;
                case 10:
                    each.setTranslateX(((each.getLayoutBounds().getWidth() / 2) - 22) * each.getScaleX());
                    each.setTranslateY(height / -2 + (each.getLayoutBounds().getHeight() * each.getScaleY() / 2));
                    eachLabel.setTranslateX(width / -2 + eachLabel.getWidth() / 2 + 390.0 * width / 500);
                    eachLabel.setTranslateY(height / -2 + eachLabel.getHeight() / 2 + 101.0 * height / 470);
                    eachLabelPercent.setTranslateX(width / -2 + eachLabel.getWidth() / 2 + 390.0 * width / 500);
                    eachLabelPercent.setTranslateY(height / -2 + eachLabel.getHeight() / 2 + 121.0 * height / 470);
                    break;
                case 11:
                    each.setTranslateX(width / -2 + (each.getLayoutBounds().getWidth() * each.getScaleX() / 2));
                    each.setTranslateY(height / -2 + (each.getLayoutBounds().getHeight() * each.getScaleY() / 2));
                    eachLabel.setTranslateX(width / -2 + eachLabel.getWidth() / 2 + 7.0 * width / 500);
                    eachLabel.setTranslateY(height / -2 + eachLabel.getHeight() / 2 + 5.0 * height / 470);
                    eachLabelPercent.setTranslateX(width / -2 + eachLabel.getWidth() / 2 + 7.0 * width / 500);
                    eachLabelPercent.setTranslateY(height / -2 + eachLabel.getHeight() / 2 + 25.0 * height / 470);
                    break;
                case 12:
                    each.setTranslateX((each.getLayoutBounds().getWidth() * each.getScaleX() / -2) + 8 * each.getScaleX());
                    each.setTranslateY(height / -2 + (each.getLayoutBounds().getHeight() * each.getScaleY() / 2) + height * 55.0 / 470 - 3 * each.getScaleY());
                    eachLabel.setTranslateX(width / -2 + eachLabel.getWidth() / 2 + 40.0 * width / 500);
                    eachLabel.setTranslateY(height / -2 + eachLabel.getHeight() / 2 + 280.0 * height / 470);
                    eachLabelPercent.setTranslateX(width / -2 + eachLabel.getWidth() / 2 + 40.0 * width / 500);
                    eachLabelPercent.setTranslateY(height / -2 + eachLabel.getHeight() / 2 + 300.0 * height / 470);
                    break;
                case 13:
                    each.setTranslateY(height / -2 + (each.getLayoutBounds().getHeight() * each.getScaleY() / 2) + height * 55.0 / 470 - 20 * each.getScaleY());
                    eachLabel.setTranslateY(height / -2 + eachLabel.getHeight() / 2 + 310.0 * height / 470);
                    eachLabelPercent.setTranslateY(height / -2 + eachLabel.getHeight() / 2 + 330.0 * height / 470);
                    break;
                case 14:
                    each.setTranslateX((each.getLayoutBounds().getWidth() * each.getScaleX() / 2) - 8 * each.getScaleX());
                    each.setTranslateY(height / -2 + (each.getLayoutBounds().getHeight() * each.getScaleY() / 2) + height * 55.0 / 470 - 3 * each.getScaleY());
                    eachLabel.setTranslateX(width / -2 + eachLabel.getWidth() / 2 + 370.0 * width / 500);
                    eachLabel.setTranslateY(height / -2 + eachLabel.getHeight() / 2 + 280.0 * height / 470);
                    eachLabelPercent.setTranslateX(width / -2 + eachLabel.getWidth() / 2 + 370.0 * width / 500);
                    eachLabelPercent.setTranslateY(height / -2 + eachLabel.getHeight() / 2 + 300.0 * height / 470);
                    break;
                case 15:
                    each.setTranslateX(width / 2 + (each.getLayoutBounds().getWidth() * each.getScaleX() / -2));
                    each.setTranslateY(height / -2 + (each.getLayoutBounds().getHeight() * each.getScaleY() / 2));
                    eachLabel.setTranslateX(width / -2 + eachLabel.getWidth() / 2 + 396.0 * width / 500);
                    eachLabel.setTranslateY(height / -2 + eachLabel.getHeight() / 2 + 5.0 * height / 470);
                    eachLabelPercent.setTranslateX(width / -2 + eachLabel.getWidth() / 2 + 396.0 * width / 500);
                    eachLabelPercent.setTranslateY(height / -2 + eachLabel.getHeight() / 2 + 25.0 * height / 470);
                    break;

            }

        }
        zonelegend.setTranslateX(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * (-155.0 / 470));
        zonelegend.setTranslateY(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * (185.0 / 470));
        zonelegend.setMaxWidth(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 170.0 / 470);
        zonelegend.setMinWidth(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 170.0 / 470);
        zonelegend.setPrefWidth(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 170.0 / 470);
        zonelegendgradient.setWidth(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 153.0 / 470);
        zonelegendgradient.setHeight(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 17.0 / 470);
        zonelegendtoplabel.maxWidthProperty().bind(zonelegend.maxWidthProperty());
        zonelegendlowerlabel.maxWidthProperty().bind(zonelegend.maxWidthProperty().multiply(0.5));
        zonelegendupperlabel.maxWidthProperty().bind(zonelegend.maxWidthProperty().multiply(0.5));
        zonelegendlowerlabel.minWidthProperty().bind(zonelegend.maxWidthProperty().multiply(0.45));
        zonelegendupperlabel.minWidthProperty().bind(zonelegend.maxWidthProperty().multiply(0.45));
        zonelegendtoplabel.setStyle("-fx-font: " + imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 13.0 / 470 + "px \"Lucida Sans\";");
        zonelegendlowerlabel.setStyle("-fx-font: " + imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 11.0 / 470 + "px \"Lucida Sans\";");
        zonelegendupperlabel.setStyle("-fx-font: " + imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 11.0 / 470 + "px \"Lucida Sans\";");
    }

    private void createThreadAndRun(String selector) {
        final String FINALSELECTOR = selector;
        switch (FINALSELECTOR) {
            case ("simpletraditional"):
            case ("advancedtraditional"):
                if (tTrad.isAlive()) {
                    tTrad.interrupt();
                }
                tTrad = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        threadRunner(FINALSELECTOR);
                    }
                });
                tTrad.start();
                break;
            case ("simplegrid"):
            case ("advancedgrid"):
                if (tGrid.isAlive()) {
                    tGrid.interrupt();
                }
                tGrid = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        threadRunner(FINALSELECTOR);
                    }
                });
                tGrid.start();
                break;
            case ("simpleheat"):
            case ("advancedheat"):
                if (tHeat.isAlive()) {
                    tHeat.interrupt();
                }
                tHeat = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        threadRunner(FINALSELECTOR);
                    }
                });
                tHeat.start();
                break;
            case ("simplezone"):
            case ("advancedzone"):
                if (tZone.isAlive()) {
                    tZone.interrupt();
                }
                tZone = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        threadRunner(FINALSELECTOR);
                    }
                });
                tZone.start();
                break;
        }
    }

    private void removeAllShotsFromView() {
        ArrayList<Node> toRemove = new ArrayList();
        for (Node each : imagegrid.getChildren()) {
            if (!each.equals(gridbackground) && !each.equals(rect11) && !each.equals(rect15) && (each.getClass().equals(Rectangle.class) || each.getClass().equals(Circle.class) || each.getClass().equals(Line.class))) {
                toRemove.add(each);
            }
        }
        for (Node each : toRemove) {
            imagegrid.getChildren().remove(each);
        }
    }

    private void traditional() {
        if (searchvbox.isVisible()) {
            this.currentSearchModeSelection = "simpletraditional";
            resetView();
            try {
                this.previousYear = this.yearcombo.getValue().toString();
                this.previousPlayer = this.playercombo.getValue().toString();
                this.previousSeason = this.seasoncombo.getValue().toString();
                plotTraditionalShots(doSimpleSearch());
            } catch (SQLException ex) {
                ex.printStackTrace();
            } catch (NullPointerException ex) {
                this.errorlabel.setText("Please try again");
                this.errorlabel.setVisible(true);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            this.currentSearchModeSelectionAdvanced = "advancedtraditional";
            resetView();
            try {
                JSONArray jsonArray = createAdvancedJSONOutput(currentSearchModeSelectionAdvanced);
                removeAllShotsFromView();
                plotTraditionalShots(jsonArray);
            } catch (SQLException ex) {
                ex.printStackTrace();
            } catch (NullPointerException ex) {
                this.errorlabeladvanced.setText("Please try again");
                this.errorlabeladvanced.setVisible(true);
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (Exception ex) {
                this.errorlabeladvanced.setText("Please try again");
                this.errorlabeladvanced.setVisible(true);
            }

        }

        imageview.setImage(new Image("/images/newbackcourt.png"));

    }

    private void grid() {
        if (searchvbox.isVisible()) {
            this.currentSearchModeSelection = "simplegrid";
            resetView();
            try {
                this.previousYear = this.yearcombo.getValue().toString();
                this.previousPlayer = this.playercombo.getValue().toString();
                this.previousSeason = this.seasoncombo.getValue().toString();
                plotGrid(doSimpleSearch());
            } catch (SQLException ex) {
                ex.printStackTrace();
            } catch (NullPointerException ex) {
                this.errorlabel.setText("Please try again");
                this.errorlabel.setVisible(true);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            this.currentSearchModeSelectionAdvanced = "advancedgrid";
            resetView();
            try {
                JSONArray jsonArray = createAdvancedJSONOutput(currentSearchModeSelectionAdvanced);
                removeAllShotsFromView();
                plotGrid(jsonArray);
            } catch (NullPointerException ex) {
                this.errorlabeladvanced.setText("Please try again");
                this.errorlabeladvanced.setVisible(true);
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (Exception ex) {
                this.errorlabeladvanced.setText("Please try again");
                this.errorlabeladvanced.setVisible(true);
            }

        }
        gridbackground.setVisible(true);
        gridbackground.setWidth(imageview.localToParent(imageview.getBoundsInLocal()).getWidth());
        gridbackground.setHeight(imageview.localToParent(imageview.getBoundsInLocal()).getHeight());
        gridlegendcolor.setVisible(true);
        gridlegendsize.setVisible(true);
        gridlegendcolor.setTranslateX(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * (-155.0 / 470));
        gridlegendcolor.setTranslateY(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * (185.0 / 470));
        gridlegendsize.setTranslateX(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * (155.0 / 470));
        gridlegendsize.setTranslateY(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * (185.0 / 470));
        gridlegendcolor.setMaxWidth(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 170.0 / 470);
        gridlegendcolor.setMinWidth(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 170.0 / 470);
        gridlegendcolor.setPrefWidth(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 170.0 / 470);
        gridlegendsize.setMaxWidth(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 170.0 / 470);
        gridlegendsize.setMinWidth(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 170.0 / 470);
        gridlegendsize.setPrefWidth(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 170.0 / 470);
        gridcolorlegendgradient.setWidth(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 153.0 / 470);
        gridcolorlegendgradient.setHeight(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 17.0 / 470);
        gridcolorlegendtoplabel.maxWidthProperty().bind(gridlegendcolor.maxWidthProperty());
        gridcolorlegendlowerlabel.maxWidthProperty().bind(gridlegendcolor.maxWidthProperty().multiply(0.5));
        gridcolorlegendupperlabel.maxWidthProperty().bind(gridlegendcolor.maxWidthProperty().multiply(0.5));
        gridcolorlegendlowerlabel.minWidthProperty().bind(gridlegendcolor.maxWidthProperty().multiply(0.45));
        gridcolorlegendupperlabel.minWidthProperty().bind(gridlegendcolor.maxWidthProperty().multiply(0.45));
        Rectangle tempRect;
        double nodeCounter = 0;
        for (Node each : gridsizelegendgradient.getChildren()) {
            tempRect = (Rectangle) each;
            tempRect.setWidth(((nodeCounter * 1.5) + 2) * imageview.localToParent(imageview.getBoundsInLocal()).getHeight() / 470);
            tempRect.setHeight(((nodeCounter * 1.5) + 2) * imageview.localToParent(imageview.getBoundsInLocal()).getHeight() / 470);
            nodeCounter++;
        }
        gridcolorlegendtoplabel.setStyle("-fx-font: " + imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 13.0 / 470 + "px \"Lucida Sans\";");
        gridsizelegendtoplabel.setStyle("-fx-font: " + imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 13.0 / 470 + "px \"Lucida Sans\";");
        gridcolorlegendlowerlabel.setStyle("-fx-font: " + imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 11.0 / 470 + "px \"Lucida Sans\";");
        gridcolorlegendupperlabel.setStyle("-fx-font: " + imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 11.0 / 470 + "px \"Lucida Sans\";");
        gridsizelegendlowerlabel.setStyle("-fx-font: " + imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 11.0 / 470 + "px \"Lucida Sans\";");
        gridsizelegendupperlabel.setStyle("-fx-font: " + imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 11.0 / 470 + "px \"Lucida Sans\";");
        imageview.setImage(new Image("/images/transparent.png"));

    }

    private void heat() {
        if (searchvbox.isVisible()) {
            this.currentSearchModeSelection = "simpleheat";
            resetView();
            try {
                this.previousYear = this.yearcombo.getValue().toString();
                this.previousPlayer = this.playercombo.getValue().toString();
                this.previousSeason = this.seasoncombo.getValue().toString();
                plotHeat(doSimpleSearch());
            } catch (SQLException ex) {
                ex.printStackTrace();
            } catch (NullPointerException ex) {
                this.errorlabel.setText("Please try again");
                this.errorlabel.setVisible(true);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            this.currentSearchModeSelectionAdvanced = "advancedheat";
            resetView();
            try {
                JSONArray jsonArray = createAdvancedJSONOutput(currentSearchModeSelectionAdvanced);
                removeAllShotsFromView();
                plotHeat(jsonArray);
            } catch (NullPointerException ex) {
                this.errorlabeladvanced.setText("Please try again");
                this.errorlabeladvanced.setVisible(true);
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (Exception ex) {
                this.errorlabeladvanced.setText("Please try again");
                this.errorlabeladvanced.setVisible(true);
            }

        }

        heatlegend.setVisible(true);
        imageview.setImage(new Image("/images/newtransparent.png"));
        heatlegend.setTranslateX(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * (-155.0 / 470));
        heatlegend.setTranslateY(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * (185.0 / 470));
        heatlegend.setMaxWidth(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 170.0 / 470);
        heatlegend.setMinWidth(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 170.0 / 470);
        heatlegend.setPrefWidth(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 170.0 / 470);
        heatlegendgradient.setWidth(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 153.0 / 470);
        heatlegendgradient.setHeight(imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 17.0 / 470);
        heatlegendtoplabel.maxWidthProperty().bind(heatlegend.maxWidthProperty());
        heatlegendlowerlabel.maxWidthProperty().bind(heatlegend.maxWidthProperty().multiply(0.5));
        heatlegendupperlabel.maxWidthProperty().bind(heatlegend.maxWidthProperty().multiply(0.5));
        heatlegendlowerlabel.minWidthProperty().bind(heatlegend.maxWidthProperty().multiply(0.45));
        heatlegendupperlabel.minWidthProperty().bind(heatlegend.maxWidthProperty().multiply(0.45));
        heatlegendtoplabel.setStyle("-fx-font: " + imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 13.0 / 470 + "px \"Lucida Sans\";");
        heatlegendlowerlabel.setStyle("-fx-font: " + imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 11.0 / 470 + "px \"Lucida Sans\";");
        heatlegendupperlabel.setStyle("-fx-font: " + imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 11.0 / 470 + "px \"Lucida Sans\";");
    }

    private void setCircle(Circle circle, int x, int y) {
        circle.setTranslateX(x * 1.0 * imageview.getLayoutBounds().getHeight() / 470);
        circle.setTranslateY(y * 1.0 * imageview.getLayoutBounds().getHeight() / 470 - (185.0 * imageview.localToParent(imageview.getBoundsInLocal()).getHeight() / 470));
        circle.setOpacity(0.75);
        //square.setTranslateX((each2.getX() + 5) * imageview.getLayoutBounds().getHeight() / 470);
        //+ imageview.localToParent(imageview.getBoundsInLocal()).getMinX() + imageview.localToParent(imageview.getBoundsInLocal()).getWidth() / 2
//            square.setTranslateY(each2.getY() * imageview.getLayoutBounds().getHeight() / 470 - (175.0 * imageview.localToParent(imageview.getBoundsInLocal()).getHeight() / 470));

//        circle.setOnMouseEntered((MouseEvent t) -> {
//            Label label = new Label();
//            label.setText((circle.getLayoutX() - 250) + "," + (circle.getLayoutY() - 55));
//            label.setLayoutX(350);
//            label.setLayoutY(420);
//            label.setVisible(true);
//            anchorpane.getChildren().add(label);
//        });
//        circle.setOnMouseExited((MouseEvent t) -> {
//            anchorpane.getChildren().remove(anchorpane.getChildren().size() - 1);
//        });
    }

    private void organizeZoneFXMLElements() {
        allZoneFXML.add(group1);
        allZoneFXML.add(rect1);
        allZoneFXML.add(arc1);
        allZoneFXML.add(group2);
        allZoneFXML.add(rect2);
        allZoneFXML.add(arc2);
        allZoneFXML.add(group3);
        allZoneFXML.add(rect3);
        allZoneFXML.add(arc3);
        allZoneFXML.add(arc4);
        allZoneFXML.add(group5);
        allZoneFXML.add(rect5);
        allZoneFXML.add(arc5);
        allZoneFXML.add(group6);
        allZoneFXML.add(rect6);
        allZoneFXML.add(arc6);
        allZoneFXML.add(arc7);
        allZoneFXML.add(arc8);
        allZoneFXML.add(arc9);
        allZoneFXML.add(group10);
        allZoneFXML.add(rect10);
        allZoneFXML.add(arc10);
        allZoneFXML.add(rect11);
        allZoneFXML.add(group12);
        allZoneFXML.add(rect12);
        allZoneFXML.add(arc12);
        allZoneFXML.add(arc13);
        allZoneFXML.add(group14);
        allZoneFXML.add(rect14);
        allZoneFXML.add(arc14);
        allZoneFXML.add(rect15);
        allZoneFXML.add(label1);
        allZoneFXML.add(label2);
        allZoneFXML.add(label3);
        allZoneFXML.add(label4);
        allZoneFXML.add(label5);
        allZoneFXML.add(label6);
        allZoneFXML.add(label7);
        allZoneFXML.add(label8);
        allZoneFXML.add(label9);
        allZoneFXML.add(label10);
        allZoneFXML.add(label11);
        allZoneFXML.add(label12);
        allZoneFXML.add(label13);
        allZoneFXML.add(label14);
        allZoneFXML.add(label15);
        allZoneFXML.add(labelpercent1);
        allZoneFXML.add(labelpercent2);
        allZoneFXML.add(labelpercent3);
        allZoneFXML.add(labelpercent4);
        allZoneFXML.add(labelpercent5);
        allZoneFXML.add(labelpercent6);
        allZoneFXML.add(labelpercent7);
        allZoneFXML.add(labelpercent8);
        allZoneFXML.add(labelpercent9);
        allZoneFXML.add(labelpercent10);
        allZoneFXML.add(labelpercent11);
        allZoneFXML.add(labelpercent12);
        allZoneFXML.add(labelpercent13);
        allZoneFXML.add(labelpercent14);
        allZoneFXML.add(labelpercent15);
        allZoneFXML.add(zonelegend);
        allZoneFXML.add(zonelegendtoplabel);
        allZoneFXML.add(zonelegendlowerlabel);
        allZoneFXML.add(zonelegendupperlabel);
        allZoneFXML.add(zonelegendgradient);
        allLabels = new LinkedList();
        allLabels.add(label1);
        allLabels.add(label2);
        allLabels.add(label3);
        allLabels.add(label4);
        allLabels.add(label5);
        allLabels.add(label6);
        allLabels.add(label7);
        allLabels.add(label8);
        allLabels.add(label9);
        allLabels.add(label10);
        allLabels.add(label11);
        allLabels.add(label12);
        allLabels.add(label13);
        allLabels.add(label14);
        allLabels.add(label15);
        allPercentLabels = new LinkedList();
        allPercentLabels.add(labelpercent1);
        allPercentLabels.add(labelpercent2);
        allPercentLabels.add(labelpercent3);
        allPercentLabels.add(labelpercent4);
        allPercentLabels.add(labelpercent5);
        allPercentLabels.add(labelpercent6);
        allPercentLabels.add(labelpercent7);
        allPercentLabels.add(labelpercent8);
        allPercentLabels.add(labelpercent9);
        allPercentLabels.add(labelpercent10);
        allPercentLabels.add(labelpercent11);
        allPercentLabels.add(labelpercent12);
        allPercentLabels.add(labelpercent13);
        allPercentLabels.add(labelpercent14);
        allPercentLabels.add(labelpercent15);
        Shape shape1 = Shape.union(rect1, arc1);
        Shape shape2 = Shape.union(rect2, arc2);
        Shape shape3 = Shape.union(rect3, arc3);
        Shape shape5 = Shape.union(rect5, arc5);
        Shape shape6 = Shape.union(rect6, arc6);
        Shape shape10 = Shape.union(rect10, arc10);
        Shape shape12 = Shape.union(rect12, arc12);
        Shape shape14 = Shape.union(rect14, arc14);
        ArrayList<Shape> shapes = new ArrayList();
        shapes.add(shape1);
        shapes.add(shape2);
        shapes.add(shape3);
        shapes.add(shape5);
        shapes.add(shape6);
        shapes.add(shape10);
        shapes.add(shape12);
        shapes.add(shape14);
        allShapes = new LinkedList();
        allShapes.add(shape1);
        allShapes.add(shape2);
        allShapes.add(shape3);
        allShapes.add(arc4);
        allShapes.add(shape5);
        allShapes.add(shape6);
        allShapes.add(arc7);
        allShapes.add(arc8);
        allShapes.add(arc9);
        allShapes.add(shape10);
        allShapes.add(rect11);
        allShapes.add(shape12);
        allShapes.add(arc13);
        allShapes.add(shape14);
        allShapes.add(rect15);
        double strokeWidth = 3.0;
        Paint strokeColor = Color.web("#434343");
        for (Shape shape : shapes) {
            shape.setStroke(strokeColor);
            shape.setStrokeWidth(strokeWidth);
            shape.setStrokeType(StrokeType.OUTSIDE);
            imagegrid.getChildren().add(shape);
        }
        rect11.toFront();
        rect15.toFront();
        shape12.toFront();
        shape14.toFront();
        arc13.toFront();
        arc13.setStroke(strokeColor);
        arc13.setStrokeWidth(strokeWidth);
        arc13.setStrokeType(StrokeType.OUTSIDE);
        shape6.toFront();
        shape10.toFront();
        arc8.toFront();
        arc8.setStroke(strokeColor);
        arc8.setStrokeWidth(strokeWidth);
        arc8.setStrokeType(StrokeType.OUTSIDE);
        arc7.toFront();
        arc7.setStroke(strokeColor);
        arc7.setStrokeWidth(strokeWidth);
        arc7.setStrokeType(StrokeType.OUTSIDE);
        arc9.toFront();
        arc9.setStroke(strokeColor);
        arc9.setStrokeWidth(strokeWidth);
        arc9.setStrokeType(StrokeType.OUTSIDE);
        shape3.toFront();
        shape5.toFront();
        arc4.toFront();
        arc4.setStroke(strokeColor);
        arc4.setStrokeWidth(strokeWidth);
        arc4.setStrokeType(StrokeType.OUTSIDE);
        shape2.toFront();
        shape1.toFront();
        shape1.setStrokeType(StrokeType.INSIDE);
        for (Object each : allShapes) {
            Node node = (Node) each;
            node.setVisible(false);
        }
    }

    private void zone() {
        if (searchvbox.isVisible()) {
            this.currentSearchModeSelection = "simplezone";
            resetView();
            try {
                this.previousYear = this.yearcombo.getValue().toString();
                this.previousPlayer = this.playercombo.getValue().toString();
                this.previousSeason = this.seasoncombo.getValue().toString();
                plotZone(doSimpleSearch());
            } catch (SQLException ex) {
                ex.printStackTrace();
            } catch (NullPointerException ex) {
                this.errorlabel.setText("Please try again");
                this.errorlabel.setVisible(true);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        } else {
            this.currentSearchModeSelectionAdvanced = "advancedzone";
            resetView();
            try {
                JSONArray jsonArray = createAdvancedJSONOutput(currentSearchModeSelectionAdvanced);
                removeAllShotsFromView();
                plotZone(jsonArray);
            } catch (NullPointerException ex) {
                this.errorlabeladvanced.setText("Please try again");
                this.errorlabeladvanced.setVisible(true);
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (Exception ex) {
                this.errorlabeladvanced.setText("Please try again");
                this.errorlabeladvanced.setVisible(true);
            }

        }
        imageview.setImage(new Image("/images/transparent.png"));
        for (Object each : allShapes) {
            Node node = (Node) each;
            node.setVisible(true);
        }

        zonelegend.setVisible(true);
        zonelegendgradient.setVisible(true);
        zonelegendlowerlabel.setVisible(true);
        zonelegendtoplabel.setVisible(true);
        zonelegendupperlabel.setVisible(true);
        for (int i = 0; i < allLabels.size(); i++) {
            allLabels.get(i).setVisible(true);
            allPercentLabels.get(i).setVisible(true);
        }
    }

    private void plotZone(JSONArray jsonArray) throws IOException {
        resizeZone();
        if (searchvbox.isVisible()) {
            setShotGrid(jsonArray);
        } else {
            setShotGridAdvanced(jsonArray);
        }
        allZones = new HashMap();
        Double[] doubles = new Double[3];
        doubles[0] = 0.0;
        doubles[1] = 0.0;
        doubles[2] = 0.0;
        for (int i = 1; i < 16; i++) {
            doubles = new Double[3];
            doubles[0] = 0.0;
            doubles[1] = 0.0;
            doubles[2] = 0.0;
            allZones.put(i, doubles);
        }
        allZoneAverages = useZoneAverages();
        JSONObject eachShot;
        int iteration = 0;

        for (int i = 0; i < jsonArray.length(); i++) {
            eachShot = jsonArray.getJSONObject(i);
            iteration++;

            switch (eachShot.getString("shotzonebasic")) {
                case "Backcourt":
                    break;
                case "Restricted Area":
                    addShotToHashMap(1, eachShot.getInt("make"));
                    break;
                case "In The Paint (Non-RA)":
                    switch (eachShot.getString("shotzonearea")) {
                        case "Left Side(L)":
                            switch (eachShot.getString("shotzonerange")) {
                                case "8-16 ft.":
                                    addShotToHashMap(3, eachShot.getInt("make"));
                                    break;
                            }
                            break;
                        case "Center(C)":
                            switch (eachShot.getString("shotzonerange")) {
                                case "Less Than 8 ft.":
                                    addShotToHashMap(2, eachShot.getInt("make"));
                                    break;
                                case "8-16 ft.":
                                    addShotToHashMap(4, eachShot.getInt("make"));
                                    break;

                            }
                            break;

                        case "Right Side(R)":
                            switch (eachShot.getString("shotzonerange")) {
                                case "8-16 ft.":
                                    addShotToHashMap(5, eachShot.getInt("make"));
                                    break;
                            }
                            break;
                    }
                    break;
                case "Mid-Range":
                    switch (eachShot.getString("shotzonearea")) {
                        case "Left Side(L)":
                            switch (eachShot.getString("shotzonerange")) {
                                case "8-16 ft.":
                                    addShotToHashMap(3, eachShot.getInt("make"));
                                    break;
                                case "16-24 ft.":
                                    addShotToHashMap(6, eachShot.getInt("make"));
                                    break;
                            }
                            break;

                        case "Left Side Center(LC)":
                            switch (eachShot.getString("shotzonerange")) {
                                case "16-24 ft.":
                                    addShotToHashMap(7, eachShot.getInt("make"));
                                    break;
                            }
                            break;

                        case "Center(C)":
                            switch (eachShot.getString("shotzonerange")) {
                                case "8-16 ft.":
                                    addShotToHashMap(4, eachShot.getInt("make"));
                                    break;
                                case "16-24 ft.":
                                    addShotToHashMap(8, eachShot.getInt("make"));
                                    break;
                            }
                            break;

                        case "Right Side Center(RC)":
                            switch (eachShot.getString("shotzonerange")) {
                                case "16-24 ft.":
                                    addShotToHashMap(9, eachShot.getInt("make"));
                                    break;
                            }
                            break;

                        case "Right Side(R)":
                            switch (eachShot.getString("shotzonerange")) {
                                case "8-16 ft.":
                                    addShotToHashMap(5, eachShot.getInt("make"));
                                    break;
                                case "16-24 ft.":
                                    addShotToHashMap(10, eachShot.getInt("make"));
                                    break;
                            }
                            break;
                    }
                    break;
                case "Left Corner 3":
                    addShotToHashMap(11, eachShot.getInt("make"));
                    break;
                case "Right Corner 3":
                    addShotToHashMap(15, eachShot.getInt("make"));
                    break;
                case "Above the Break 3":
                    switch (eachShot.getString("shotzonearea")) {
                        case "Left Side Center(LC)":
                            switch (eachShot.getString("shotzonerange")) {
                                case "24+ ft.":
                                    addShotToHashMap(12, eachShot.getInt("make"));
                                    break;
                            }
                            break;

                        case "Center(C)":
                            switch (eachShot.getString("shotzonerange")) {
                                case "24+ ft.":
                                    addShotToHashMap(13, eachShot.getInt("make"));
                                    break;
                            }
                            break;

                        case "Right Side Center(RC)":
                            switch (eachShot.getString("shotzonerange")) {
                                case "24+ ft.":
                                    addShotToHashMap(14, eachShot.getInt("make"));
                                    break;
                                default:
                            }
                            break;
                    }
                    break;
            }
        }

        HashMap<Integer, Double> playerZones = new HashMap();
        int counter = 0;
        for (Integer each : allZones.keySet()) {
            allZones.get(each)[2] = allZones.get(each)[0] * 1.0 / allZones.get(each)[1];
            playerZones.put(each, allZones.get(each)[2]);

        }
        Shape tempShape;
        Rectangle tempRect;
        Arc tempArc;
        for (int i = 1; i < 16; i++) {
            switch (i) {
                case 1:
                case 2:
                case 3:
                case 5:
                case 6:
                case 10:
                case 12:
                case 14:
                    tempShape = (Shape) allShapes.get(i - 1);
                    changeShapeColor(tempShape, playerZones.get(i), i);
                    break;
                case 4:
                case 7:
                case 8:
                case 9:
                case 13:
                    tempArc = (Arc) allShapes.get(i - 1);
                    changeArcColor(tempArc, playerZones.get(i), i);
                    break;
                case 11:
                case 15:
                    tempRect = (Rectangle) allShapes.get(i - 1);
                    changeRectColor(tempRect, playerZones.get(i), i);
                    break;
            }
        }
        imageview.toFront();

        for (int j = 0; j < 15; j++) {
            allLabels.get(j).setText(allZones.get(j + 1)[0].intValue() + "/" + allZones.get(j + 1)[1].intValue());
            allLabels.get(j).toFront();
            if (allZones.get(j + 1)[1].intValue() != 0 && allZones.get(j + 1)[1].intValue() == allZones.get(j + 1)[0].intValue()) {
                allPercentLabels.get(j).setText("100%");
            } else if (allZones.get(j + 1)[1].intValue() == 0 && allZones.get(j + 1)[1].intValue() == allZones.get(j + 1)[0].intValue()) {
                allPercentLabels.get(j).setText("0%");
            } else {
                allPercentLabels.get(j).setText(df.format(allZones.get(j + 1)[2] * 100) + "%");
            }
            allPercentLabels.get(j).toFront();
//            allLabels.get(j).setVisible(true);
//            allPercentLabels.get(j).setVisible(true);
        }

        zonelegend.setVisible(true);
        zonelegend.toFront();
        if (searchvbox.isVisible()) {
            createThreadAndRun(currentSearchModeSelection);
        } else {
            createThreadAndRun(currentSearchModeSelectionAdvanced);

        }
    }

    private JSONArray getZoneAveragesData() throws IOException {
        JSONObject jsonObjOut = new JSONObject();
        jsonObjOut.put("selector", "zoneaverages");
        Main.getPrintWriterOut().println(jsonObjOut.toString());
        return new JSONArray(Main.getServerResponse().readLine());
    }

    private HashMap<Integer, Double> useZoneAverages() throws IOException {
        HashMap<Integer, Double> hashmap = new HashMap();
        JSONArray jsonArray = getZoneAveragesData();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject eachShot = jsonArray.getJSONObject(i);
            hashmap.put(i + 1, eachShot.getBigDecimal("average").doubleValue());
        }
        return hashmap;

    }

    private void addShotToHashMap(int selector, int make) {
        allZones.get(selector)[1] = allZones.get(selector)[1] + 1;
        if (make == 1) {
            allZones.get(selector)[0] = allZones.get(selector)[0] + 1;
        }
    }

    private void changeShapeColor(Shape shape, Double playerValue, int i) {
        if (allZones.get(i)[1] == 0) {
            shape.setFill(Color.web("#b2b2b2"));
        } else {
            Double diff = playerValue - allZoneAverages.get(i);

            if (diff > 0.06) {
                shape.setFill(Color.web("#fc2121"));
            } else if (diff < 0.06 && diff >= 0.04) {
                shape.setFill(Color.web("#ff6363"));
            } else if (diff < 0.04 && diff >= 0.02) {
                shape.setFill(Color.web("#ff9c9c"));
            } else if (diff < 0.02 && diff >= -0.02) {
                shape.setFill(Color.web("#b2b2b2"));
            } else if (diff < -0.02 && diff >= -0.04) {
                shape.setFill(Color.web("#91c6f4"));
            } else if (diff < -0.04 && diff >= -0.06) {
                shape.setFill(Color.web("#56b0ff"));
            } else if (diff < -0.06) {
                shape.setFill(Color.web("#2373ff"));
            }
        }
    }

    private void changeRectColor(Rectangle rect, Double playerValue, int i) {
        if (allZones.get(i)[1] == 0) {
            rect.setFill(Color.web("#b2b2b2"));
        } else {
            Double diff = playerValue - allZoneAverages.get(i);
            if (diff > 0.06) {
                rect.setFill(Color.web("#fc2121"));
            } else if (diff < 0.06 && diff >= 0.04) {
                rect.setFill(Color.web("#ff6363"));
            } else if (diff < 0.04 && diff >= 0.02) {
                rect.setFill(Color.web("#ff9c9c"));
            } else if (diff < 0.02 && diff >= -0.02) {
                rect.setFill(Color.web("#b2b2b2"));
            } else if (diff < -0.02 && diff >= -0.04) {
                rect.setFill(Color.web("#91c6f4"));
            } else if (diff < -0.04 && diff >= -0.06) {
                rect.setFill(Color.web("#56b0ff"));
            } else if (diff < -0.06) {
                rect.setFill(Color.web("#2373ff"));
            }
        }
    }

    private void changeArcColor(Arc arc, Double playerValue, int i) {
        if (allZones.get(i)[1] == 0) {
            arc.setFill(Color.web("#b2b2b2"));
        } else {
            Double diff = playerValue - allZoneAverages.get(i);

            if (diff > 0.06) {
                arc.setFill(Color.web("#fc2121"));
            } else if (diff < 0.06 && diff >= 0.04) {
                arc.setFill(Color.web("#ff6363"));
            } else if (diff < 0.04 && diff >= 0.02) {
                arc.setFill(Color.web("#ff9c9c"));
            } else if (diff < 0.02 && diff >= -0.02) {
                arc.setFill(Color.web("#b2b2b2"));
            } else if (diff < -0.02 && diff >= -0.04) {
                arc.setFill(Color.web("#91c6f4"));
            } else if (diff < -0.04 && diff >= -0.06) {
                arc.setFill(Color.web("#56b0ff"));
            } else if (diff < -0.06) {
                arc.setFill(Color.web("#2373ff"));
            }
        }
    }

    private void initSizing() {
        searchvbox.setVisible(true);
        advancedvbox.setVisible(false);
        font = new BigDecimal(COMBO_FONT_SIZE).multiply(new BigDecimal(imageview.getLayoutBounds().getHeight())).divide(new BigDecimal("550"), 6, RoundingMode.HALF_UP).doubleValue();
        setViewTypeButtonStyle(10);
        fontGrid = new BigDecimal(STAT_GRID_FONT_SIZE).multiply(new BigDecimal(imageview.getLayoutBounds().getHeight())).divide(new BigDecimal("550"), 6, RoundingMode.HALF_UP).doubleValue();
        titlelabel.setMinWidth(Region.USE_PREF_SIZE);
        titlelabel.setStyle("-fx-font: " + fontGrid * 3 + "px \"Serif\"; ");
        this.errorlabel.setVisible(false);
        this.introlabel.prefWidthProperty().bind(this.gridpane.widthProperty().divide(4));
        this.introlabel.setStyle("-fx-font: " + font * 1.5 + "px \"Serif\";");
        this.yearcombo.prefWidthProperty().bind(this.gridpane.widthProperty().divide(5));
        this.yearcombo.setStyle("-fx-font: " + font + "px \"Serif\";");
        this.playercombo.prefWidthProperty().bind(this.gridpane.widthProperty().divide(5));
        this.playercombo.setStyle("-fx-font: " + font + "px \"Serif\";");
        this.seasoncombo.prefWidthProperty().bind(this.gridpane.widthProperty().divide(5));
        this.seasoncombo.setStyle("-fx-font: " + font + "px \"Serif\";");
        this.searchbutton.setStyle("-fx-font: " + font + "px \"Serif\";");
        this.simplelayoutbutton.setStyle("-fx-font: " + font + "px \"Serif\";");
        this.advancedlayoutbutton.setStyle("-fx-font: " + font + "px \"Serif\";");
        this.comparelayoutbutton.setStyle("-fx-font: " + font + "px \"Serif\";");
        this.simplelayoutbutton.prefWidthProperty().bind(this.gridpane.widthProperty().divide(8));
        this.advancedlayoutbutton.prefWidthProperty().bind(this.gridpane.widthProperty().divide(8));
        this.comparelayoutbutton.prefWidthProperty().bind(this.gridpane.widthProperty().divide(8));

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

        resetView();
        VBox.setMargin(introlabel, new Insets(new BigDecimal(imageview.getLayoutBounds().getHeight()).multiply(new BigDecimal("20")).divide(new BigDecimal("475"), 6, RoundingMode.HALF_UP).doubleValue(), 0, 0, 0));
        VBox.setMargin(this.yearcombo, new Insets(20, 0, 0, 0));
        VBox.setMargin(this.playercombo, new Insets(20, 0, 0, 0));
        VBox.setMargin(this.seasoncombo, new Insets(20, 0, 0, 0));
        VBox.setMargin(this.searchbutton, new Insets(20, 0, 0, 0));
        this.shotgrid.maxWidthProperty().bind(this.gridpane.widthProperty().divide(3));
        this.shotgrid.maxHeightProperty().bind(this.gridpane.heightProperty().divide(5.25));
        mask = new Rectangle(imageview.getLayoutBounds().getWidth(), imageview.getLayoutBounds().getHeight());
        searchscrollpane.prefWidthProperty().bind(advancedvbox.widthProperty());
        advancedvboxinner.prefWidthProperty().bind(searchscrollpane.widthProperty());
        selectionvbox.prefWidthProperty().bind(selectionscrollpane.widthProperty().multiply(0.95));
        advancedintrolabel.prefWidthProperty().bind(advancedvboxinner.widthProperty());
        seasonsbegincombo.prefWidthProperty().bind(advancedvboxinner.widthProperty().multiply(0.4));
        seasonsendcombo.prefWidthProperty().bind(advancedvboxinner.widthProperty().multiply(0.4));
        seasonslabel.prefWidthProperty().bind(advancedvboxinner.widthProperty().multiply(0.9));
        distancebegincombo.prefWidthProperty().bind(advancedvboxinner.widthProperty().multiply(0.4));
        distanceendcombo.prefWidthProperty().bind(advancedvboxinner.widthProperty().multiply(0.4));
        shotdistancelabel.prefWidthProperty().bind(advancedvboxinner.widthProperty().multiply(0.9));
        playerslabel.prefWidthProperty().bind(advancedvboxinner.widthProperty().multiply(0.9));
        playercomboadvanced.prefWidthProperty().bind(advancedvboxinner.widthProperty().multiply(0.66));
        seasontypeslabel.prefWidthProperty().bind(advancedvboxinner.widthProperty().multiply(0.9));
        seasontypescomboadvanced.prefWidthProperty().bind(advancedvboxinner.widthProperty().multiply(0.66));
        shotsuccesslabel.prefWidthProperty().bind(advancedvboxinner.widthProperty().multiply(0.9));
        shotsuccesscombo.prefWidthProperty().bind(advancedvboxinner.widthProperty().multiply(0.66));
        shotvaluelabel.prefWidthProperty().bind(advancedvboxinner.widthProperty().multiply(0.9));
        shotvaluecombo.prefWidthProperty().bind(advancedvboxinner.widthProperty().multiply(0.66));
        shottypeslabel.prefWidthProperty().bind(advancedvboxinner.widthProperty().multiply(0.9));
        shottypescombo.prefWidthProperty().bind(advancedvboxinner.widthProperty().multiply(0.66));
        teamslabel.prefWidthProperty().bind(advancedvboxinner.widthProperty().multiply(0.9));
        teamscombo.prefWidthProperty().bind(advancedvboxinner.widthProperty().multiply(0.66));
        hometeamslabel.prefWidthProperty().bind(advancedvboxinner.widthProperty().multiply(0.9));
        hometeamscombo.prefWidthProperty().bind(advancedvboxinner.widthProperty().multiply(0.66));
        awayteamslabel.prefWidthProperty().bind(advancedvboxinner.widthProperty().multiply(0.9));
        awayteamscombo.prefWidthProperty().bind(advancedvboxinner.widthProperty().multiply(0.66));
        courtareaslabel.prefWidthProperty().bind(advancedvboxinner.widthProperty().multiply(0.9));
        courtareascombo.prefWidthProperty().bind(advancedvboxinner.widthProperty().multiply(0.66));
        courtsideslabel.prefWidthProperty().bind(advancedvboxinner.widthProperty().multiply(0.9));
        courtsidescombo.prefWidthProperty().bind(advancedvboxinner.widthProperty().multiply(0.66));
        imageview.setImage(new Image("/images/newtransparent.png"));

        advancedvbox.setStyle("-fx-background: transparent;-fx-background-color: transparent;");
        advancedvboxinner.setStyle("-fx-background: transparent;-fx-background-color: transparent;");
        searchscrollpane.setStyle("-fx-background: transparent;-fx-background-color: transparent;");
        selectionscrollpane.setStyle("-fx-background: transparent;-fx-background-color: transparent;");
        try {
            populateUnchangingComboBoxes();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        selectionvbox.setStyle("-fx-background: transparent;-fx-background-color: transparent;");
        Stop[] stops = new Stop[]{new Stop(0, Color.BLACK), new Stop(1, Color.RED)};
        LinearGradient lg1 = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
        vbox.setStyle("-fx-background-color: linear-gradient(from 25% 25% to 100% 100%, #434343, #cdcccc)");
        gridpane.setStyle("-fx-background-color: transparent;");
    }

    private void resetView() {
        this.errorlabel.setVisible(false);
        errorlabeladvanced.setVisible(false);
        gridbackground.setVisible(false);
        gridlegendcolor.setVisible(false);
        gridlegendsize.setVisible(false);
        heatlegend.setVisible(false);
        zonelegend.setVisible(false);
        for (Node each : allZoneFXML) {
            each.setVisible(false);
        }
        for (Node each : allShapes) {
            each.setVisible(false);
        }
        for (int i = 0; i < allLabels.size(); i++) {
            allLabels.get(i).setVisible(false);
            allPercentLabels.get(i).setVisible(false);
        }
    }

    private void setShotGrid(JSONArray jsonArray) {
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
        BigDecimal xBig;
        BigDecimal yBig;
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

    private void threadRunner(String FINALSELECTOR) {
        try {
            for (int i = 0; i < 2; i++) {
                try {
                    Thread.sleep(300);
                    switch (FINALSELECTOR) {
                        case ("simpletraditional"):
                        case ("advancedtraditional"):
                            resizeShots();
                            break;
                        case ("simplegrid"):
                        case ("advancedgrid"):
                            resizeGrid();
                            break;
                        case ("simpleheat"):
                        case ("advancedheat"):
                            resizeHeat();
                            break;
                        case ("simplezone"):
                        case ("advancedzone"):
                            resizeZone();
                            break;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            Thread.currentThread().interrupt();
        } catch (Exception ex) {
            Thread.currentThread().interrupt();
            ex.printStackTrace();
        }
    }

    private void normalHeatLooper() {
        long start = System.nanoTime();

        double aSum = 0;
        double bSum = 0;
        int p = 2;
        int counter = 0;
        int eachCounter = 0;
        Coordinate basket = new Coordinate(0, 0);
        for (Coordinate each : coordAverages.keySet()) {
            eachCounter = 0;
            if (each.getX() % offsetHeat == 0 && each.getY() % offsetHeat == 0) {
                counter++;
                aSum = 0;
                bSum = 0;
                for (Coordinate each2 : coordAverages.keySet()) {
                    if (!each.equals(each2) && getDistance(each, each2) < MAX_DISTANCE_BETWEEN_NODES_HEAT) {
                        aSum = aSum + ((coordAverages.get(each2).get(1).intValue() * getDistance(each, each2)) / Math.pow(getDistance(each, each2), p));
                        bSum = bSum + (1 / Math.pow(getDistance(each, each2), p));
                        if (coordAverages.get(each2).get(1).intValue() != 0) {
                            eachCounter++;

                        }
                    }

                }

                if (eachCounter > 1) {
                    coordValue.put(each, aSum / bSum);
                } else {
                    coordValue.put(each, 0.0);
                }
//                System.out.println("    " + counter);
            }

        }
        long end = System.nanoTime();
        System.out.println("Normal Heat Looper: " + (end - start) / 1000000000 + " seconds");

    }

    private void ultraFineHeatMapThreader() throws InterruptedException {
        LinkedList<Coordinate> organizedCoords = new LinkedList();
//        HashSet<Coordinate> organizedCoords = new HashSet();
        int x = -250;
        int y = -52;
        int iterator = (452 * (x + 250) + y + 52);
        for (Coordinate each : coordAverages.keySet()) {
            organizedCoords.add(each);
//            System.out.println(each.getX() + "," + each.getY());
        }
        long start = System.nanoTime();

        allUltraFineHeatThreads = new ArrayList();
        offsetHeat = 15;
        int iMax = 5;
        for (int i = 0; i < iMax; i++) {
            final int iFinal = i;
            final int iMaxFinal = iMax;
            final LinkedList<Coordinate> finalOrganizedCoords = organizedCoords;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    double aSum = 0;
                    double bSum = 0;
                    int p = 2;
                    int counter = 0;
                    int eachCounter = 0;
                    int iFinalThread = iFinal;
                    for (Coordinate each : coordAverages.keySet()) {
                        if (each.getY() >= (452 / iMaxFinal) * iFinalThread - 52 && each.getY() < (452 / iMaxFinal) * (iFinalThread + 1) - 52) {

                            if (each.getX() % offsetHeat == 0 && each.getY() % offsetHeat == 0) {
                                counter++;
                                aSum = 0;
                                bSum = 0;
                                for (Coordinate each2 : coordAverages.keySet()) {
                                    if (!each.equals(each2) && getDistance(each, each2) < MAX_DISTANCE_BETWEEN_NODES_HEAT) {
                                        aSum = aSum + ((coordAverages.get(each2).get(1).intValue() * getDistance(each, each2)) / Math.pow(getDistance(each, each2), p));
                                        bSum = bSum + (1 / Math.pow(getDistance(each, each2), p));
                                        if (coordAverages.get(each2).get(1).intValue() != 0) {
                                            eachCounter++;

                                        }
                                    }

                                }

                                if (eachCounter > 1) {
                                    coordValue.put(each, aSum / bSum);
                                } else {
                                    coordValue.put(each, 0.0);
                                }
                            }
                        }
                    }
//                    System.out.println("    " + counter);

                }
            });
            allUltraFineHeatThreads.add(thread);
        }
//        for (int j = 0; j < allUltraFineHeatThreads.size(); j++) {
//            allUltraFineHeatThreads.get(j).join();
//        }
        for (int k = 0; k < allUltraFineHeatThreads.size(); k++) {
            allUltraFineHeatThreads.get(k).start();
//            Thread.sleep(10000);
        }
        boolean done = false;
        while (!done) {
            try {
                for (int j = 0; j < allUltraFineHeatThreads.size(); j++) {
                    allUltraFineHeatThreads.get(j).join();
                }
                Thread.sleep(1000);
                done = true;
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

        }
        long end = System.nanoTime();
        System.out.println("ultraFineThreader: " + (end - start) / 1000000000 + " seconds");

    }

    private void optimizedNormalHeatLooper() {

        double aSum = 0;
        double bSum = 0;
        int p = 2;
        int counter = 0;
        int eachCounter = 0;
        LinkedList<Coordinate> organizedCoords = new LinkedList();
//        HashSet<Coordinate> organizedCoords = new HashSet();
        int x = -250;
        int y = -52;
        int i = (452 * (x + 250) + y + 52);
        for (Coordinate each : coordAverages.keySet()) {
            organizedCoords.add(each);
//            System.out.println(each.getX() + "," + each.getY());
        }
        long start = System.nanoTime();

        int xTemp;
        int yTemp;
        int iTemp;
        Coordinate tempCoord;
        Coordinate centerCoord;
        while (i < 226452) {
//            xTemp = x;
//            yTemp = y;
            aSum = 0;
            bSum = 0;
            eachCounter = 0;
            centerCoord = organizedCoords.get(i);
            for (int j = x - MAX_DISTANCE_BETWEEN_NODES_HEAT; j <= x + MAX_DISTANCE_BETWEEN_NODES_HEAT; j++) {
                if (j < -250) {
                    continue;
                }
                for (int k = y - MAX_DISTANCE_BETWEEN_NODES_HEAT; k <= y + MAX_DISTANCE_BETWEEN_NODES_HEAT; k++) {
                    if (k < -52) {
                        continue;
                    }
                    iTemp = (452 * (j + 250) + k + 52);
//                    System.out.println("here");
                    if (iTemp < 0) {
                        continue;
                    }
                    tempCoord = organizedCoords.get(iTemp);

                    if (iTemp != i && getDistance(centerCoord, tempCoord) < MAX_DISTANCE_BETWEEN_NODES_HEAT) {
//                        System.out.println("here2");
                        aSum = aSum + ((coordAverages.get(tempCoord).get(1).intValue() * getDistance(centerCoord, tempCoord)) / Math.pow(getDistance(centerCoord, tempCoord), p));
                        bSum = bSum + (1 / Math.pow(getDistance(centerCoord, tempCoord), p));
                        if (coordAverages.get(tempCoord).get(1).intValue() != 0) {
                            eachCounter++;
//                            System.out.println("here3");
                        }
                    }
                }
            }
//inner loop for every column
//            for (int k=)
//            while (min<max){
//                if(y>MAX_DISTANCE_BETWEEN_NODES_HEAT){
//                    y=-52;
//                    x=x+offsetHeat*452;
//                }else{
//                    
//                }
//            }
            if (eachCounter > 1) {
                coordValue.put(centerCoord, aSum / bSum);
//                System.out.println(aSum / bSum);
            } else {
                coordValue.put(centerCoord, 0.0);
            }
            if (y + offsetHeat > 399) {
                x = x + offsetHeat;
                y = -52;
            } else {
                y = y + offsetHeat;
            }
            i = (452 * (x + 250) + y + 52);
            counter++;
            System.out.println(counter);

        }
        long end = System.nanoTime();
        System.out.println("Overall Duration: " + (end - start) / 1000000000 + " seconds");

    }

    private void initAdvanced() throws IOException {
        seasonsbegincombo.setItems(FXCollections.observableArrayList(makeYears()));
        seasonsendcombo.setItems(FXCollections.observableArrayList(makeYears()));
        setAdvancedPlayerComboBox();
        setAdvancedSeasonsComboBox();
        setShotDistanceCombo();
    }

    private void setShotDistanceCombo() {
        ArrayList<Integer> distances = new ArrayList();
        for (int i = 0; i < 90; i++) {
            distances.add(i);
        }
        distancebegincombo.setItems(FXCollections.observableArrayList(distances));
        distanceendcombo.setItems(FXCollections.observableArrayList(distances));
    }

    private void addHBoxToSelectionBox(String selector) {
        switch (selector) {
            case "seasonsbegincombo":
                singleSelectionHBoxCreationMethods(beginSeason, "Seasons after and including ", seasonsbegincombo);
                beginSeason = seasonsbegincombo.getValue().toString();
                break;
            case "seasonsendcombo":
                singleSelectionHBoxCreationMethods(endSeason, "Seasons before and including ", seasonsendcombo);
                endSeason = seasonsendcombo.getValue().toString();
                break;
            case "playercomboadvanced":
                multipleSelectionHBoxCreationMethods(allSelectedPlayers, "Player: ", playercomboadvanced);
                try {
                    allSelectedPlayers.add(playercomboadvanced.getValue().toString());
                } catch (Exception ex) {

                }
                break;
            case "seasontypescomboadvanced":
                multipleSelectionHBoxCreationMethods(allSelectedSeasonTypes, "Season Type: ", seasontypescomboadvanced);
                try {
                    allSelectedSeasonTypes.add(seasontypescomboadvanced.getValue().toString());
                } catch (Exception ex) {

                }
                break;
            case "distancebegincombo":
                singleSelectionHBoxCreationMethods(beginDistance, "Minimum Distance: ", distancebegincombo);
                beginDistance = distancebegincombo.getValue().toString();
                break;
            case "distanceendcombo":
                singleSelectionHBoxCreationMethods(endDistance, "Maximum Distance: ", distanceendcombo);
                endDistance = distanceendcombo.getValue().toString();
                break;
            case "shotsuccesscombo":
                singleSelectionHBoxCreationMethods(shotSuccess, "Shot Success: ", shotsuccesscombo);
                shotSuccess = shotsuccesscombo.getValue().toString();
                break;
            case "shotvaluecombo":
                singleSelectionHBoxCreationMethods(shotValue, "Shot Value: ", shotvaluecombo);
                shotValue = shotvaluecombo.getValue().toString();
                break;
            case "shottypescombo":
                multipleSelectionHBoxCreationMethods(allSelectedShotTypes, "Shot Type: ", shottypescombo);
                try {
                    allSelectedShotTypes.add(shottypescombo.getValue().toString());
                } catch (Exception ex) {

                }
                break;
            case "teamscombo":
                multipleSelectionHBoxCreationMethods(allSelectedTeams, "Team: ", teamscombo);
                try {
                    allSelectedTeams.add(teamscombo.getValue().toString());
                } catch (Exception ex) {

                }
                break;
            case "hometeamscombo":
                multipleSelectionHBoxCreationMethods(allSelectedHomeTeams, "Home Team: ", hometeamscombo);
                try {
                    allSelectedHomeTeams.add(hometeamscombo.getValue().toString());
                } catch (Exception ex) {

                }
                break;
            case "awayteamscombo":
                multipleSelectionHBoxCreationMethods(allSelectedAwayTeams, "Away Team: ", awayteamscombo);
                try {
                    allSelectedAwayTeams.add(awayteamscombo.getValue().toString());
                } catch (Exception ex) {

                }
                break;
            case "courtareascombo":
                multipleSelectionHBoxCreationMethods(allSelectedCourtAreas, "Court Area: ", courtareascombo);
                try {
                    allSelectedCourtAreas.add(courtareascombo.getValue().toString());
                } catch (Exception ex) {

                }
                break;
            case "courtsidescombo":
                multipleSelectionHBoxCreationMethods(allSelectedCourtSides, "Court Side: ", courtsidescombo);
                try {
                    allSelectedCourtSides.add(courtsidescombo.getValue().toString());
                } catch (Exception ex) {

                }
                break;

        }
    }

    private void singleSelectionHBoxCreationMethods(String alreadySelected, String labelPreText, ComboBox combo) {
        font = new BigDecimal(COMBO_FONT_SIZE).multiply(new BigDecimal(imageview.getLayoutBounds().getHeight())).divide(new BigDecimal("550"), 6, RoundingMode.HALF_UP).doubleValue();
        Label tempLabel;
        Label labelToReplace = null;
        Label label;
        Button deleteButton = null;
        Button tempButton = null;
        HBox tempHBox;
        HBox hboxWithReplacement = null;
        HBox newHBox;
        Insets insets = new Insets(5, 5, 5, 5);
        Insets insetsHBox = new Insets(0, 0, 0, 20);

        if (!alreadySelected.equals("")) {
            for (Node each : selectionvbox.getChildren()) {
                if (each.getClass().equals(HBox.class)) {
                    tempHBox = (HBox) each;
                    for (Node innerEach : tempHBox.getChildren()) {
                        if (innerEach.getClass().equals(Button.class)) {
                            tempButton = (Button) innerEach;
                        } else {
                            tempLabel = (Label) innerEach;
                            if (tempLabel.getText().startsWith(labelPreText)) {
                                labelToReplace = tempLabel;
                                hboxWithReplacement = tempHBox;
                                deleteButton = tempButton;
                                break;
                            }
                        }

                    }

                }
            }
            alreadySelected = combo.getValue().toString();
            labelToReplace.setText(labelPreText + alreadySelected);
        } else {
            newHBox = new HBox();
            newHBox.setAlignment(Pos.CENTER_LEFT);
            newHBox.setMinHeight(25.0);
            newHBox.setMaxHeight(25.0);
            newHBox.setMinWidth(100.0);
            newHBox.setPadding(insetsHBox);

            final String SELECTED = combo.getId();
            deleteButton = new Button("X");
//            deleteButton.setStyle("-fx-text-inner-color: red;");
            deleteButton.setStyle("-fx-text-fill: red;-fx-background-color: transparent; ");
//        traditionalbutton.setStyle("-fx-font: " + font + "px \"Arial Black\";-fx-background-color: transparent;");

            newHBox.getChildren().add(deleteButton);
            final HBox HBOX = (HBox) deleteButton.getParent();
            final Scene FINALSCENE = selectionvbox.getScene();
            deleteButton.setOnMouseClicked((Event t) -> {
                deleteButtonInner(SELECTED, HBOX, labelPreText);
                selectionvbox.getChildren().remove(newHBox);
            });
            deleteButton.setOnMouseEntered((MouseEvent t) -> {
                FINALSCENE.setCursor(Cursor.HAND);
            });
            deleteButton.setOnMouseExited((MouseEvent t) -> {
                FINALSCENE.setCursor(Cursor.DEFAULT);
            });
            deleteButton.prefHeightProperty().bind(newHBox.prefHeightProperty());
            deleteButton.prefWidthProperty().bind(deleteButton.prefHeightProperty());
            alreadySelected = combo.getValue().toString();
            label = new Label();
            label.setText(labelPreText + alreadySelected);
            label.setStyle("-fx-font: " + font * 0.85 + "px \"Arial\";");
            label.setPadding(insets);
            label.prefHeightProperty().bind(newHBox.prefHeightProperty());
            label.setAlignment(Pos.CENTER_LEFT);
            newHBox.getChildren().add(label);
            selectionvbox.getChildren().add(newHBox);
        }
    }

    private void deleteButtonInner(String selector, HBox hbox, String preText) {
        switch (selector) {
            case "seasonsbegincombo":
                beginSeason = "";
                seasonsbegincombo.getSelectionModel().clearSelection();
                break;
            case "seasonsendcombo":
                endSeason = "";
                seasonsendcombo.getSelectionModel().clearSelection();
                break;
            case "playercomboadvanced":
                for (Node each : hbox.getChildren()) {
                    if (each.getClass().equals(Label.class)) {
                        Label label = (Label) each;
                        allSelectedPlayers.remove(label.getText().replace(preText, ""));
                        playercomboadvanced.getSelectionModel().clearSelection();
                    }
                }
                break;
            case "seasontypescomboadvanced":
                for (Node each : hbox.getChildren()) {
                    if (each.getClass().equals(Label.class)) {
                        Label label = (Label) each;
                        allSelectedSeasonTypes.remove(label.getText().replace(preText, ""));
                        seasontypescomboadvanced.getSelectionModel().clearSelection();
                    }
                }
                break;
            case "distancebegincombo":
                beginDistance = "";
                distancebegincombo.getSelectionModel().clearSelection();
                break;
            case "distanceendcombo":
                endDistance = "";
                distanceendcombo.getSelectionModel().clearSelection();
                break;
            case "shotsuccesscombo":
                shotSuccess = "";
                shotsuccesscombo.getSelectionModel().clearSelection();
                break;
            case "shotvaluecombo":
                shotValue = "";
                shotvaluecombo.getSelectionModel().clearSelection();
                break;
            case "shottypescombo":
                for (Node each : hbox.getChildren()) {
                    if (each.getClass().equals(Label.class)) {
                        Label label = (Label) each;
                        allSelectedShotTypes.remove(label.getText().replace(preText, ""));
                        shottypescombo.getSelectionModel().clearSelection();
                    }
                }
                break;
            case "teamscombo":
                for (Node each : hbox.getChildren()) {
                    if (each.getClass().equals(Label.class)) {
                        Label label = (Label) each;
                        allSelectedTeams.remove(label.getText().replace(preText, ""));
                        teamscombo.getSelectionModel().clearSelection();
                    }
                }
                break;
            case "hometeamscombo":
                for (Node each : hbox.getChildren()) {
                    if (each.getClass().equals(Label.class)) {
                        Label label = (Label) each;
                        allSelectedHomeTeams.remove(label.getText().replace(preText, ""));
                        hometeamscombo.getSelectionModel().clearSelection();
                    }
                }
                break;
            case "awayteamscombo":
                for (Node each : hbox.getChildren()) {
                    if (each.getClass().equals(Label.class)) {
                        Label label = (Label) each;
                        allSelectedAwayTeams.remove(label.getText().replace(preText, ""));
                        awayteamscombo.getSelectionModel().clearSelection();
                    }
                }
                break;
            case "courtareascombo":
                for (Node each : hbox.getChildren()) {
                    if (each.getClass().equals(Label.class)) {
                        Label label = (Label) each;
                        allSelectedCourtAreas.remove(label.getText().replace(preText, ""));
                        courtareascombo.getSelectionModel().clearSelection();
                    }
                }
                break;
            case "courtsidescombo":
                for (Node each : hbox.getChildren()) {
                    if (each.getClass().equals(Label.class)) {
                        Label label = (Label) each;
                        allSelectedCourtSides.remove(label.getText().replace(preText, ""));
                        courtsidescombo.getSelectionModel().clearSelection();
                    }
                }
                break;
        }
    }

    private void multipleSelectionHBoxCreationMethods(HashSet hashSet, String labelPreText, ComboBox combo) {
        font = new BigDecimal(COMBO_FONT_SIZE).multiply(new BigDecimal(imageview.getLayoutBounds().getHeight())).divide(new BigDecimal("550"), 6, RoundingMode.HALF_UP).doubleValue();
        Label tempLabel;
        Label labelToReplace = null;
        Label label;
        Button deleteButton = null;
        Button tempButton = null;
        HBox tempHBox;
        HBox newHBox;
        Insets insets = new Insets(5, 5, 5, 5);
        Insets insetsHBox = new Insets(0, 0, 0, 20);
        if (!hashSet.contains(combo.getValue().toString())) {
            newHBox = new HBox();
            newHBox.setAlignment(Pos.CENTER_LEFT);
            newHBox.setMinHeight(25.0);
            newHBox.setMaxHeight(25.0);
            newHBox.setMinWidth(100.0);
            newHBox.setPadding(insetsHBox);

            final String SELECTED = combo.getId();

            deleteButton = new Button("X");
//            deleteButton.setStyle("-fx-text-inner-color: red;");
            deleteButton.setStyle("-fx-text-fill: red;-fx-background-color: transparent;");
            newHBox.getChildren().add(deleteButton);
            final HBox HBOX = (HBox) deleteButton.getParent();
            final Scene FINALSCENE = selectionvbox.getScene();

            deleteButton.setOnMouseClicked((Event t) -> {
                deleteButtonInner(SELECTED, HBOX, labelPreText);
                selectionvbox.getChildren().remove(newHBox);
            });
            deleteButton.setOnMouseEntered((MouseEvent t) -> {
                FINALSCENE.setCursor(Cursor.HAND);
            });
            deleteButton.setOnMouseExited((MouseEvent t) -> {
                FINALSCENE.setCursor(Cursor.DEFAULT);
            });
            deleteButton.prefHeightProperty().bind(newHBox.prefHeightProperty());
            deleteButton.prefWidthProperty().bind(deleteButton.prefHeightProperty());
            label = new Label();
            label.setText(labelPreText + combo.getValue().toString());
            label.setStyle("-fx-font: " + font * 0.85 + "px \"Arial\";");
            label.setPadding(insets);
            label.prefHeightProperty().bind(newHBox.prefHeightProperty());
            label.setAlignment(Pos.CENTER_LEFT);
            newHBox.getChildren().add(label);
            selectionvbox.getChildren().add(newHBox);
        }
    }

    private void populateUnchangingComboBoxes() throws IOException {
        ArrayList<String> tempList = new ArrayList();
        tempList.add("Makes");
        tempList.add("Misses");
        shotsuccesscombo.setItems(FXCollections.observableArrayList(tempList));
        tempList = new ArrayList();
        tempList.add("2PT");
        tempList.add("3PT");
        shotvaluecombo.setItems(FXCollections.observableArrayList(tempList));
        tempList = new ArrayList();
        tempList = getShotTypesList();
        UserInputComboBox shotTypeComboUser = new UserInputComboBox(shottypescombo);
        shotTypeComboUser.getComboBox().setItems(FXCollections.observableArrayList(tempList));
//        shottypescombo.setItems(FXCollections.observableArrayList(tempList));
        tempList = new ArrayList();
        relevantTeamNameIDHashMap.put("Atlanta Hawks", 1610612737);
        relevantTeamNameIDHashMap.put("Boston Celtics", 1610612738);
        relevantTeamNameIDHashMap.put("Brooklyn Nets", 1610612751);
        relevantTeamNameIDHashMap.put("Charlotte Hornets", 1610612766);
        relevantTeamNameIDHashMap.put("Chicago Bulls", 1610612741);
        relevantTeamNameIDHashMap.put("Cleveland Cavaliers", 1610612739);
        relevantTeamNameIDHashMap.put("Dallas Mavericks", 1610612742);
        relevantTeamNameIDHashMap.put("Denver Nuggets", 1610612743);
        relevantTeamNameIDHashMap.put("Detroit Pistons", 1610612765);
        relevantTeamNameIDHashMap.put("Golden State Warriors", 1610612744);
        relevantTeamNameIDHashMap.put("Houston Rockets", 1610612745);
        relevantTeamNameIDHashMap.put("Indiana Pacers", 1610612754);
        relevantTeamNameIDHashMap.put("Los Angeles Clippers", 1610612746);
        relevantTeamNameIDHashMap.put("Los Angeles Lakers", 1610612747);
        relevantTeamNameIDHashMap.put("Memphis Grizzlies", 1610612763);
        relevantTeamNameIDHashMap.put("Miami Heat", 1610612748);
        relevantTeamNameIDHashMap.put("Milwaukee Bucks", 1610612749);
        relevantTeamNameIDHashMap.put("Minnesota Timberwolves", 1610612750);
        relevantTeamNameIDHashMap.put("New Orleans Pelicans", 1610612740);
        relevantTeamNameIDHashMap.put("New York Knicks", 1610612752);
        relevantTeamNameIDHashMap.put("Oklahoma City Thunder", 1610612760);
        relevantTeamNameIDHashMap.put("Orlando Magic", 1610612753);
        relevantTeamNameIDHashMap.put("Philadelphia 76ers", 1610612755);
        relevantTeamNameIDHashMap.put("Phoenix Suns", 1610612756);
        relevantTeamNameIDHashMap.put("Portland Trail Blazers", 1610612757);
        relevantTeamNameIDHashMap.put("Sacramento Kings", 1610612758);
        relevantTeamNameIDHashMap.put("San Antonio Spurs", 1610612759);
        relevantTeamNameIDHashMap.put("Toronto Raptors", 1610612761);
        relevantTeamNameIDHashMap.put("Utah Jazz", 1610612762);
        relevantTeamNameIDHashMap.put("Washington Wizards", 1610612764);

        relevantTeamNameIDHashMap.put("Adelaide 36ers", 15019);
        relevantTeamNameIDHashMap.put("Alba Berlin", 12323);
        relevantTeamNameIDHashMap.put("Beijing Ducks", 15021);
        relevantTeamNameIDHashMap.put("FC Barcelona", 12304);
        relevantTeamNameIDHashMap.put("Fenerbahce", 12321);
        relevantTeamNameIDHashMap.put("Flamengo", 12325);
        relevantTeamNameIDHashMap.put("Franca", 12332);
        relevantTeamNameIDHashMap.put("Maccabi Haifa", 93);
        relevantTeamNameIDHashMap.put("Maccabi Tel Aviv", 12401);
        relevantTeamNameIDHashMap.put("Melbourne United", 15016);
        relevantTeamNameIDHashMap.put("Montepaschi Siena", 12322);
        relevantTeamNameIDHashMap.put("New Zealand Breakers", 15020);
        relevantTeamNameIDHashMap.put("Olimpia Milano", 94);
        relevantTeamNameIDHashMap.put("Real Madrid", 12315);
        relevantTeamNameIDHashMap.put("San Lorenzo", 12330);
        relevantTeamNameIDHashMap.put("Shanghai Sharks", 12329);
        relevantTeamNameIDHashMap.put("Sydney Kings", 15015);

//        
//        
//        // 
//        tempList.add("Adelaide 36ers");
//        tempList.add("Alba Berlin");
//        tempList.add("Beijing Ducks");
//        tempList.add("FC Barcelona");
//        tempList.add("Fenerbahce");
//        tempList.add("Flamengo");
//        tempList.add("Franca");
//        tempList.add("Maccabi Haifa");
//        tempList.add("Maccabi Tel Aviv");
//        tempList.add("Melbourne United");
//        tempList.add("Montepaschi Siena");
//        tempList.add("New Zealand Breakers");
//        tempList.add("Olimpia Milano");
//        tempList.add("Real Madrid");
//        tempList.add("San Lorenzo");
//        tempList.add("Shanghai Sharks");
//        tempList.add("Sydney Kings");
//tempList.add("Atlanta Hawks");
//        tempList.add("Boston Celtics");
//        tempList.add("Brooklyn Nets");
//        tempList.add("Charlotte Hornets");
//        tempList.add("Chicago Bulls");
//        tempList.add("Cleveland Cavaliers");
//        tempList.add("Dallas Mavericks");
//        tempList.add("Denver Nuggets");
//        tempList.add("Detroit Pistons");
//        tempList.add("Golden State Warriors");
//        tempList.add("Houston Rockets");
//        tempList.add("Indiana Pacers");
//        tempList.add("Los Angeles Clippers");
//        tempList.add("Los Angeles Lakers");
//        tempList.add("Memphis Grizzlies");
//        tempList.add("Miami Heat");
//        tempList.add("Milwaukee Bucks");
//        tempList.add("Minnesota Timberwolves");
//        tempList.add("New Orleans Pelicans");
//        tempList.add("New York Knicks");
//        tempList.add("Oklahoma City Thunder");
//        tempList.add("Orlando Magic");
//tempList.add("Philadelphia 76ers");
//        tempList.add("Phoenix Suns");
//        tempList.add("Portland Trail Blazers");
//        tempList.add("Sacramento Kings");
//        tempList.add("San Antonio Spurs");
//        tempList.add("Toronto Raptors");
//        tempList.add("Utah Jazz");
//        tempList.add("Washington Wizards");
//        teamscombo.setItems(FXCollections.observableArrayList(tempList));
//        hometeamscombo.setItems(FXCollections.observableArrayList(tempList));
//        awayteamscombo.setItems(FXCollections.observableArrayList(tempList));
        UserInputComboBox teamComboUser = new UserInputComboBox(teamscombo);
        UserInputComboBox homeTeamComboUser = new UserInputComboBox(hometeamscombo);
        UserInputComboBox awayTeamComboUser = new UserInputComboBox(awayteamscombo);
        teamComboUser.getComboBox().setItems(FXCollections.observableArrayList(relevantTeamNameIDHashMap.keySet()));
        homeTeamComboUser.getComboBox().setItems(FXCollections.observableArrayList(relevantTeamNameIDHashMap.keySet()));
        awayTeamComboUser.getComboBox().setItems(FXCollections.observableArrayList(relevantTeamNameIDHashMap.keySet()));
//        teamscombo.setItems(FXCollections.observableArrayList(relevantTeamNameIDHashMap.keySet()));
//        hometeamscombo.setItems(FXCollections.observableArrayList(relevantTeamNameIDHashMap.keySet()));
//        awayteamscombo.setItems(FXCollections.observableArrayList(relevantTeamNameIDHashMap.keySet()));
        tempList = new ArrayList();
        tempList.add("Restricted Area");
        tempList.add("In The Paint (Non-RA)");
        tempList.add("Mid-Range");
        tempList.add("Right Corner 3");
        tempList.add("Left Corner 3");
        tempList.add("Above the Break 3");
        tempList.add("Backcourt");
        courtareascombo.setItems(FXCollections.observableArrayList(tempList));
        tempList = new ArrayList();
        tempList.add("Left");
        tempList.add("Left-Center");
        tempList.add("Center");
        tempList.add("Right-Center");
        tempList.add("Right");
        tempList.add("Back Court");
        courtsidescombo.setItems(FXCollections.observableArrayList(tempList));
    }

    private ArrayList getShotTypesList() throws IOException {
        ArrayList shotTypes = new ArrayList();
        JSONArray jsonArray = getShotTypesData();
        JSONObject eachShotType;
        for (int i = 0; i < jsonArray.length(); i++) {
            eachShotType = jsonArray.getJSONObject(i);
            shotTypes.add(eachShotType.getString("playtype"));
        }
        return shotTypes;
    }

    private JSONArray createAdvancedJSONOutput(String searchTypeSelector) throws IOException, Exception {
//        boolean isGood = false;
//        if (!beginSeason.equals("") || !endSeason.equals("") || !beginDistance.equals("") || !endDistance.equals("") || !shotSuccess.equals("") || !shotValue.equals("")
//                || !allSelectedPlayers.isEmpty() || !allSelectedSeasonTypes.isEmpty() || !allSelectedShotTypes.isEmpty() || !allSelectedTeams.isEmpty()
//                || !allSelectedHomeTeams.isEmpty() || !allSelectedAwayTeams.isEmpty() || !allSelectedCourtAreas.isEmpty() || !allSelectedCourtSides.isEmpty()) {
//            isGood = true;
//        } else {
//            throw new Exception();
//        }
        JSONObject obj = new JSONObject();
        obj.put("selector", searchTypeSelector);
        obj.put("beginSeason", beginSeason);
        obj.put("endSeason", endSeason);
        ArrayList<Integer> allSelectedPlayerIDs = new ArrayList();
        for (String each : allSelectedPlayers) {
            allSelectedPlayerIDs.add(Integer.parseInt(nameHash.get(each)[0]));
        }
        obj.put("allSelectedPlayers", allSelectedPlayerIDs);
        obj.put("allSelectedSeasonTypes", allSelectedSeasonTypes);
        obj.put("beginDistance", beginDistance);
        obj.put("endDistance", endDistance);
        obj.put("shotSuccess", shotSuccess);
        obj.put("shotValue", shotValue);
        obj.put("allSelectedShotTypes", allSelectedShotTypes);
        ArrayList<Integer> teamIds = new ArrayList();
        for (String each : allSelectedTeams) {
            teamIds.add(relevantTeamNameIDHashMap.get(each));
        }
        obj.put("allSelectedTeams", teamIds);
        teamIds = new ArrayList();
        for (String each : allSelectedHomeTeams) {
            teamIds.add(relevantTeamNameIDHashMap.get(each));
        }
        obj.put("allSelectedHomeTeams", teamIds);
        teamIds = new ArrayList();
        for (String each : allSelectedAwayTeams) {
            teamIds.add(relevantTeamNameIDHashMap.get(each));
        }
        obj.put("allSelectedAwayTeams", teamIds);
        obj.put("allSelectedCourtAreas", allSelectedCourtAreas);
        obj.put("allSelectedCourtSides", allSelectedCourtSides);
        Main.getPrintWriterOut().println(obj.toString());
        return new JSONArray(Main.getServerResponse().readLine());
    }

    private void setShotGridAdvanced(JSONArray jsonArray) {
        this.fgfracadv.setText("--");
        this.fgpercadv.setText("--");
        this.twopointfracadv.setText("--");
        this.twopointpercadv.setText("--");
        this.threepointfracadv.setText("--");
        this.threepointpercadv.setText("--");
        int countMade = 0;
        int countTotal = 0;
        int count2pMade = 0;
        int count2pTotal = 0;
        int count3pMade = 0;
        int count3pTotal = 0;
        allShots = new LinkedHashMap();
        Circle circle;
        MissedShotIcon msi;
        BigDecimal xBig;
        BigDecimal yBig;
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
        }

        this.fgfracadv.setText(countMade + "/" + countTotal);
        if (countTotal == 0) {
            this.fgpercadv.setText("--");
        } else {
            this.fgpercadv.setText(new BigDecimal((double) countMade / countTotal * 100).setScale(2, RoundingMode.HALF_UP) + "%");
        }
        this.twopointfracadv.setText(count2pMade + "/" + count2pTotal);
        if (count2pTotal == 0) {
            this.twopointpercadv.setText("--");
        } else {
            this.twopointpercadv.setText(new BigDecimal((double) count2pMade / count2pTotal * 100).setScale(2, RoundingMode.HALF_UP) + "%");
        }
        this.threepointfracadv.setText(count3pMade + "/" + count3pTotal);
        if (count3pTotal == 0) {
            this.threepointpercadv.setText("--");
        } else {
            this.threepointpercadv.setText(new BigDecimal((double) count3pMade / count3pTotal * 100).setScale(2, RoundingMode.HALF_UP) + "%");
        }
        this.charttitle.setText("Custom Search");
        this.charttitle.setVisible(true);
    }

    private void createResponsiveComboBoxes() {
        ComboBoxListViewSkin<String> comboBoxListViewSkinPlayer = new ComboBoxListViewSkin(playercombo);
        comboBoxListViewSkinPlayer.getPopupContent().addEventFilter(KeyEvent.ANY, (event) -> {
            if (event.getCode().equals(KeyCode.SPACE)) {
                event.consume();
            }
        });
        playercombo.setSkin(comboBoxListViewSkinPlayer);
        for (Node each : advancedvboxinner.getChildren()) {
            if (each.getClass().equals(ComboBox.class)) {
                final ComboBox cb = (ComboBox) each;
                cb.valueProperty().addListener(new ChangeListener() {
                    @Override
                    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                        try {
                            if (cb.getValue() != null) {
                                addHBoxToSelectionBox(cb.getId());
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                cb.setOnMouseClicked((Event t) -> {
                    ListView<?> lv = ((ComboBoxListViewSkin<?>) cb.getSkin()).getListView();
                    lv.setOnMouseExited((Event tIn) -> {
                        cb.hide();
                    });

                });
                if (!cb.getId().equals("shotsuccesscombo") && !cb.getId().equals("shotvaluecombo")) {
                    ComboBoxListViewSkin<String> comboBoxListViewSkin = new ComboBoxListViewSkin<String>(cb) {
                        @Override
                        protected boolean isHideOnClickEnabled() {
                            return false;
                        }
                    };
                    comboBoxListViewSkin.getPopupContent().addEventFilter(KeyEvent.ANY, (event) -> {
                        if (event.getCode().equals(KeyCode.SPACE)) {
                            event.consume();
                        }
                    });
                    cb.setSkin(comboBoxListViewSkin);

                }

            } else if (each.getClass().equals(HBox.class)) {
                HBox hbox = (HBox) each;
                for (Node eachHBoxNode : hbox.getChildren()) {
                    if (eachHBoxNode.getClass().equals(ComboBox.class)) {
                        final ComboBox cb = (ComboBox) eachHBoxNode;
                        eachHBoxNode.setOnMouseClicked((Event t) -> {
                            ListView<?> lv = ((ComboBoxListViewSkin<?>) cb.getSkin()).getListView();
                            lv.setOnMouseExited((Event tIn) -> {
                                cb.hide();
                            });

                        });
                        cb.valueProperty().addListener(new ChangeListener() {
                            @Override
                            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                                try {
                                    if (cb.getValue() != null) {
                                        addHBoxToSelectionBox(cb.getId());
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    private void setViewTypeButtonsOnMouseClicked() {
        font = new BigDecimal(COMBO_FONT_SIZE).multiply(new BigDecimal(imageview.getLayoutBounds().getHeight())).divide(new BigDecimal("550"), 6, RoundingMode.HALF_UP).doubleValue();
        this.traditionalbutton.setOnMouseClicked((Event t) -> {
            setEachViewButtonOnClicked(0, "simpletraditional", "advancedtraditional");
//            setViewTypeButtonStyle(0);
//            if (searchvbox.isVisible()) {
//                try {
//                    this.previousYear = this.yearcombo.getValue().toString();
//                    this.previousPlayer = this.playercombo.getValue().toString();
//                    this.previousSeason = this.seasoncombo.getValue().toString();
//                    if (this.currentSearchModeSelection.equals("")) {
//                        this.currentSearchModeSelection = "simpletraditional";
//                    } else {
//                        traditional();
//                    }
//                } catch (NullPointerException ex) {
//                    this.errorlabel.setText("Please select one from each category");
//                    this.errorlabel.setVisible(true);
//                }
//            } else {
//                if (checkForEmptyAdvancedSearch()) {
//                    if (this.currentSearchModeSelectionAdvanced.equals("")) {
//                        this.currentSearchModeSelectionAdvanced = "advancedtraditional";
//                    } else {
//                        traditional();
//                    }
//                } else {
//                    this.errorlabeladvanced.setText("Please include at least one search parameter");
//                    this.errorlabeladvanced.setVisible(true);
//                }
//
//            }
        });
        this.gridbutton.setOnMouseClicked((Event t) -> {
            setEachViewButtonOnClicked(1, "simplegrid", "advancedgrid");

//            setViewTypeButtonStyle(1);
//            if (searchvbox.isVisible()) {
//                try {
//                    this.previousYear = this.yearcombo.getValue().toString();
//                    this.previousPlayer = this.playercombo.getValue().toString();
//                    this.previousSeason = this.seasoncombo.getValue().toString();
//                    if (this.currentSearchModeSelection.equals("")) {
//                        this.currentSearchModeSelection = "simplegrid";
//                    } else {
//                        grid();
//                    }
//                } catch (NullPointerException ex) {
//                    this.errorlabel.setText("Please select one from each category");
//                    this.errorlabel.setVisible(true);
//                }
//
//            } else {
//                if (checkForEmptyAdvancedSearch()) {
//                    if (this.currentSearchModeSelectionAdvanced.equals("")) {
//                        this.currentSearchModeSelectionAdvanced = "advancedgrid";
//                    } else {
//                        grid();
//                    }
//                } else {
//                    this.errorlabeladvanced.setText("Please include at least one search parameter");
//                    this.errorlabeladvanced.setVisible(true);
//                }
//            }
        });
        this.heatmapbutton.setOnMouseClicked((Event t) -> {
            setEachViewButtonOnClicked(2, "simpleheat", "advancedheat");

//            setViewTypeButtonStyle(2);
//            if (searchvbox.isVisible()) {
//                try {
//                    this.previousYear = this.yearcombo.getValue().toString();
//                    this.previousPlayer = this.playercombo.getValue().toString();
//                    this.previousSeason = this.seasoncombo.getValue().toString();
//                    if (this.currentSearchModeSelection.equals("")) {
//                        this.currentSearchModeSelection = "simpleheat";
//                    } else {
//                        heat();
//                    }
//                } catch (NullPointerException ex) {
//                    this.errorlabel.setText("Please select one from each category");
//                    this.errorlabel.setVisible(true);
//                }
//
//            } else {
//                if (checkForEmptyAdvancedSearch()) {
//                    if (this.currentSearchModeSelectionAdvanced.equals("")) {
//                        this.currentSearchModeSelectionAdvanced = "advancedheat";
//                    } else {
//                        heat();
//                    }
//                } else {
//                    this.errorlabeladvanced.setText("Please include at least one search parameter");
//                    this.errorlabeladvanced.setVisible(true);
//                }
//            }
        });
        this.zonebutton.setOnMouseClicked((Event t) -> {
            setEachViewButtonOnClicked(3, "simplezone", "advancedzone");

//            setViewTypeButtonStyle(3);
//
//            if (searchvbox.isVisible()) {
//                try {
//                    this.previousYear = this.yearcombo.getValue().toString();
//                    this.previousPlayer = this.playercombo.getValue().toString();
//                    this.previousSeason = this.seasoncombo.getValue().toString();
//                    if (this.currentSearchModeSelection.equals("")) {
//                        this.currentSearchModeSelection = "simplezone";
//                    } else {
//                        zone();
//                    }
//                } catch (NullPointerException ex) {
//                    this.errorlabel.setText("Please select one from each category");
//                    this.errorlabel.setVisible(true);
//                }
//
//            } else {
//                if (checkForEmptyAdvancedSearch()) {
//                    if (this.currentSearchModeSelectionAdvanced.equals("")) {
//                        this.currentSearchModeSelectionAdvanced = "advancedzone";
//                    } else {
//                        zone();
//                    }
//                } else {
//                    this.errorlabeladvanced.setText("Please include at least one search parameter");
//                    this.errorlabeladvanced.setVisible(true);
//                }
//            }
        });
    }

    private void setViewTypeButtonStyle(int selector) {
        for (int i = 0; i < viewButtons.size(); i++) {
            if (i == selector) {
                viewButtons.get(i).setStyle("-fx-font: " + font + "px \"Arial Black\";-fx-background-color: transparent;");
            } else {
                viewButtons.get(i).setStyle("-fx-font: " + font + "px \"Arial\";-fx-background-color: transparent;");
            }
        }
    }

    private boolean checkForEmptyAdvancedSearch() {
        if (!beginSeason.equals("") || !endSeason.equals("") || !beginDistance.equals("") || !endDistance.equals("") || !shotSuccess.equals("") || !shotValue.equals("")
                || !allSelectedPlayers.isEmpty() || !allSelectedSeasonTypes.isEmpty() || !allSelectedShotTypes.isEmpty() || !allSelectedTeams.isEmpty()
                || !allSelectedHomeTeams.isEmpty() || !allSelectedAwayTeams.isEmpty() || !allSelectedCourtAreas.isEmpty() || !allSelectedCourtSides.isEmpty()) {
            return true;
        }
        return false;
    }

    private void setEachViewButtonOnClicked(int selector, String currentSearch, String currentSearchAdvanced) {
        setViewTypeButtonStyle(selector);

        if (searchvbox.isVisible()) {
            try {
                this.previousYear = this.yearcombo.getValue().toString();
                this.previousPlayer = this.playercombo.getValue().toString();
                this.previousSeason = this.seasoncombo.getValue().toString();
                if (this.currentSearchModeSelection.equals("")) {
                    this.currentSearchModeSelection = currentSearch;
                } else {
                    switch (selector) {
                        case 0:
                            traditional();
                            break;
                        case 1:
                            grid();
                            break;
                        case 2:
                            heat();
                            break;
                        case 3:
                            zone();
                            break;
                    }
                }
            } catch (NullPointerException ex) {
                this.errorlabel.setText("Please select one from each category");
                this.errorlabel.setVisible(true);
            }

        } else {
            if (checkForEmptyAdvancedSearch()) {
                if (this.currentSearchModeSelectionAdvanced.equals("")) {
                    this.currentSearchModeSelectionAdvanced = currentSearchAdvanced;
                } else {
                    switch (selector) {
                        case 0:
                            traditional();
                            break;
                        case 1:
                            grid();
                            break;
                        case 2:
                            heat();
                            break;
                        case 3:
                            zone();
                            break;
                    }
                }
            } else {
                this.errorlabeladvanced.setText("Please include at least one search parameter");
                this.errorlabeladvanced.setVisible(true);
            }
        }

    }
}
