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
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
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
import javafx.scene.control.ProgressIndicator;
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

    enum Search {
        TRADITIONAL,
        GRID,
        ZONE,
        HEAT,
        NONE
    }

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
    private HashMap<Coordinate, ArrayList<Double>> coordAverages;
    private int maxShotsPerMaxSquare = 0;
    private ConcurrentHashMap<Coordinate, Double> coordValue;
    private final int OFFSET = 10;
    private final int maxDistanceBetweenNodes = 20;
    private LinkedList<Rectangle> allTiles;
    private double min;
//    private String currentSearchModeSelection = "";
    private int shotCounter = 0;
    private double maxCutoff = 0.0;
    private double diff = maxCutoff / 10;
    private int offsetHeat = 15;
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
//    private String currentSearchModeSelectionAdvanced = "";
    private LinkedHashMap<String, Integer> relevantTeamNameIDHashMap = new LinkedHashMap();
    private double font = 0.0;
    private double fontGrid = 0.0;
    private LinkedList<Button> viewButtons = new LinkedList();
    private Search currentSimpleSearch = Search.NONE;
    private Search currentAdvancedSearch = Search.NONE;
    private ArrayList<Label> simpleFGLabels = new ArrayList();
    private ArrayList<Label> advancedFGLabels = new ArrayList();
    private JSONArray lastJsonArray;
    private Service tradService;
    private Service gridService;
    private Service heatService;
    private Service zoneService;
    private long start;
    private long end;

    //General Features
    @FXML
    private BorderPane borderpane;
    @FXML
    private VBox vbox, centralvbox, progressvbox;
    @FXML
    private ImageView imageview;
    @FXML
    private Label titlelabel, lastupdatedlabel, charttitle, dateaccuracy, updatelabel, namelabel, progresslabel;
    @FXML
    private HBox tophbox;
    @FXML
    private Line line;
    @FXML
    private Button simplelayoutbutton, advancedlayoutbutton, comparelayoutbutton;
    @FXML
    private ProgressIndicator progressindicator;
    //GridPanes
    @FXML
    private GridPane gridpane, topgridpane, imagegrid, shotgrid, shotgridadv;
    //Chart Types
    @FXML
    private HBox buttonbox;
    @FXML
    private Button traditionalbutton, heatmapbutton, gridbutton, zonebutton;
    @FXML
    private Rectangle gridbackground, loadingoverlay;
    @FXML
    private Group group1, group2, group3, group5, group6, group10, group12, group14;
    @FXML
    private Rectangle rect1, rect2, rect3, rect5, rect6, rect10, rect11, rect12, rect14, rect15;
    @FXML
    private Arc arc1, arc2, arc3, arc4, arc5, arc6, arc7, arc8, arc9, arc10, arc12, arc13, arc14;
    @FXML
    private Label label1, label2, label3, label4, label5, label6, label7, label8, label9, label10,
            label11, label12, label13, label14, label15, labelpercent1, labelpercent2, labelpercent3,
            labelpercent4, labelpercent5, labelpercent6, labelpercent7, labelpercent8, labelpercent9,
            labelpercent10, labelpercent11, labelpercent12, labelpercent13, labelpercent14, labelpercent15;
    //Legends
    @FXML
    private VBox gridlegendsize, gridlegendcolor, heatlegend, zonelegend;
    @FXML
    private Rectangle gridcolorlegendgradient, heatlegendgradient, zonelegendgradient;
    @FXML
    private HBox gridsizelegendgradient;
    @FXML
    private Label gridcolorlegendtoplabel, gridcolorlegendlowerlabel, gridcolorlegendupperlabel,
            gridsizelegendtoplabel, gridsizelegendlowerlabel, gridsizelegendupperlabel;
    @FXML
    private Label heatlegendtoplabel, heatlegendlowerlabel, heatlegendupperlabel;
    @FXML
    private Label zonelegendtoplabel, zonelegendlowerlabel, zonelegendupperlabel;
    //Simple Search Elements
    @FXML
    private VBox searchvbox;
    @FXML
    private Label introlabel, errorlabel;
    @FXML
    private ComboBox yearcombo, playercombo, seasoncombo;
    @FXML
    private Label fg, fgfrac, fgperc, twopoint, twopointfrac, twopointperc, threepoint, threepointfrac, threepointperc;
    @FXML
    private Button searchbutton;
    //Advanced Search Elements
    @FXML
    private ScrollPane searchscrollpane, selectionscrollpane;
    @FXML
    private Label advancedintrolabel, errorlabeladvanced;
    @FXML
    private Button searchbuttonadvanced;
    @FXML
    private VBox advancedvbox, advancedvboxinner, selectionvbox;
    @FXML
    private Label seasonslabel, playerslabel, seasontypeslabel, shotdistancelabel, shotsuccesslabel, shotvaluelabel,
            shottypeslabel, teamslabel, hometeamslabel, awayteamslabel, courtareaslabel, courtsideslabel,
            seasondash, distancedash;
    @FXML
    private ComboBox seasonsbegincombo, seasonsendcombo, playercomboadvanced, seasontypescomboadvanced,
            distancebegincombo, distanceendcombo, shotsuccesscombo, shotvaluecombo, shottypescombo,
            teamscombo, hometeamscombo, awayteamscombo, courtareascombo, courtsidescombo;
    @FXML
    private Label fgadv, fgfracadv, fgpercadv, twopointadv, twopointfracadv, twopointpercadv, threepointadv, threepointfracadv, threepointpercadv;
    @FXML
    private TextArea notestextarea;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Collections.addAll(viewButtons, traditionalbutton, gridbutton, heatmapbutton, zonebutton);
        Collections.addAll(simpleFGLabels, fgfrac, fgperc, twopointfrac, twopointperc, threepointfrac, threepointperc);
        Collections.addAll(advancedFGLabels, fgfracadv, fgpercadv, twopointfracadv, twopointpercadv, threepointfracadv, threepointpercadv);
        createResponsiveComboBoxes();
        organizeZoneFXMLElements();
        initSizing();
        createServices();
        endLoadingTransition();
        progressvbox.setStyle("-fx-background: transparent;-fx-background-color: transparent;");
        //Set Updates Box
        try {
            JSONArray jsonArrayInit = getInitData();
            JSONObject jsonObjMisc2 = jsonArrayInit.getJSONObject(2);
            dateaccuracy.setText(jsonArrayInit.getJSONObject(1).getString("value"));
            updatelabel.setText(jsonArrayInit.getJSONObject(0).getString("value"));
        } catch (IOException ex) {
            System.out.println("Error caught in Misc Initialization");
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
            System.out.println("Error caught in creation of nameHash");
        }
        try {
            Thread warmupThread = new Thread(() -> warmupHeat());
            warmupThread.start();
            System.out.println("Started Warmup");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        yearcombo.setItems(FXCollections.observableArrayList(makeYears()));
        this.yearcombo.setValue("2019-20");
        this.playercombo.setValue("Aaron Gordon");
        this.seasoncombo.setValue("Regular Season");
        this.activePlayers = new HashMap();
        try {
            setPlayerComboBox();
            setSeasonsComboBox();
        } catch (IOException ex) {
            System.out.println("Error caught setting comboboxes");
        }
        this.searchbutton.setOnMouseClicked(t -> {
            try {
                this.yearcombo.getValue().toString();
                this.playercombo.getValue().toString();
                this.seasoncombo.getValue().toString();
                runSearch();
            } catch (NullPointerException ex) {
                this.errorlabel.setText("Please select one from each category");
                this.errorlabel.setVisible(true);
            }
        });
        setAllViewTypeButtonsOnMouseActions();
        imageview.fitHeightProperty().addListener((ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) -> resize());
        imageview.fitWidthProperty().addListener((ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) -> resize());
        this.yearcombo.setOnAction(t -> {
            try {
                setPlayerComboBox();
            } catch (IOException ex) {
                System.out.println("Error setting player combobox");
            }
        });
        this.playercombo.setOnAction(t -> {
            try {
                setSeasonsComboBox();
            } catch (IOException ex) {
                System.out.println("Error setting seasons combobox");
            }
        });
        this.simplelayoutbutton.setOnMouseClicked(t -> {
            initSizing();
            try {
                setPlayerComboBox();
                setSeasonsComboBox();
            } catch (IOException ex) {
                System.out.println("Error setting comboboxes");
            }
            this.charttitle.setVisible(false);
            searchvbox.setVisible(true);
            advancedvbox.setVisible(false);
            removeAllShotsFromView();
            resetView();
            simpleFGLabels.forEach(each -> each.setText("--"));
            imageview.setImage(new Image("/images/transparent.png"));
            changeButtonStyles();
            allShots.clear();
        });
        this.advancedlayoutbutton.setOnMouseClicked(t -> {
            try {
                initAdvanced();
            } catch (IOException ex) {
                System.out.println("Error caught initializing advanced");
            }
            this.charttitle.setVisible(false);
            searchvbox.setVisible(false);
            advancedvbox.setVisible(true);
            resize();
            removeAllShotsFromView();
            resetView();
            advancedFGLabels.forEach(each -> each.setText("--"));
            imageview.setImage(new Image("/images/transparent.png"));
            changeButtonStyles();
            allShots.clear();
        });
        this.searchbuttonadvanced.setOnMouseClicked(t -> {
            if (checkForEmptyAdvancedSearch()) {
                runSearch();
            } else {
                this.errorlabeladvanced.setText("Please include at least one search parameter");
                this.errorlabeladvanced.setVisible(true);
            }
        });
        createAlwaysRunningResizer();
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

    private ArrayList<String> makeYears() {
        int year = 2019;
        ArrayList<String> years = new ArrayList(30);
        String subYearString;
        while (year >= 1996) {
            if ((year - 1899) % 100 < 10) {
                subYearString = "0" + (year - 1899) % 100;
            } else {
                subYearString = "" + (year - 1899) % 100;
            }
            years.add(year + "-" + subYearString);
            year--;
        }

        return years;
    }

    private void resizeShots() {
        if (!allShots.keySet().isEmpty()) {
            font = new BigDecimal(COMBO_FONT_SIZE).multiply(new BigDecimal(imageview.getLayoutBounds().getHeight())).divide(new BigDecimal("550"), 6, RoundingMode.HALF_UP).doubleValue();
            setViewTypeButtonStyle(0);
            double height = imageview.localToParent(imageview.getBoundsInLocal()).getHeight();
            double scaledLineThickness = SHOT_LINE_THICKNESS.divide(ORIG_HEIGHT, 6, RoundingMode.HALF_UP).doubleValue();
            double scaledLineLength = SHOT_MISS_START_END.divide(ORIG_HEIGHT, 6, RoundingMode.HALF_UP).doubleValue();
            double minX = imageview.localToParent(imageview.getBoundsInLocal()).getMinX();
            double minY = imageview.localToParent(imageview.getBoundsInLocal()).getMinY();
            double width = imageview.localToParent(imageview.getBoundsInLocal()).getWidth();
            MissedShotIcon msi;
            Circle circle;
            Line line1;
            Line line2;
            for (Shot each : allShots.keySet()) {
                if (each.getMake() == 1) {
                    circle = (Circle) allShots.get(each);
                    circle.setTranslateX(BigDecimal.valueOf(each.getX()).doubleValue() * height / 470 + minX + width / 2);
                    circle.setTranslateY(BigDecimal.valueOf(each.getY()).doubleValue() * height / 470 + minY + height / 2 - (185.0 * height / 470));
                    circle.setRadius(height * SHOT_MADE_RADIUS.divide(ORIG_HEIGHT, 6, RoundingMode.HALF_UP).doubleValue());
                    circle.setStrokeWidth(height * scaledLineThickness);
                } else {
                    msi = (MissedShotIcon) allShots.get(each);
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
                    line1.setTranslateX(BigDecimal.valueOf(each.getX()).doubleValue() * height / 470 + minX + width / 2);
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
        JSONArray jsonArray = getActivePlayersData();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject eachPlayer = jsonArray.getJSONObject(i);
            this.activePlayers.put(eachPlayer.getInt("id"), eachPlayer.getString("firstname") + " " + eachPlayer.getString("lastname"));
        }
        HashMap<String, String> names = new HashMap();
        ArrayList<String> fixedNames = new ArrayList();
        LinkedList<String> realNames = new LinkedList();

        for (int each : this.activePlayers.keySet()) {
            names.put(activePlayers.get(each).replaceAll("[^A-Za-z0-9]", "").toLowerCase(), activePlayers.get(each));
            fixedNames.add(activePlayers.get(each).replaceAll("[^A-Za-z0-9]", "").toLowerCase());
        }
        Collections.sort(fixedNames);
        for (String each : fixedNames) {
            realNames.add(names.get(each));
        }
        if (seasoncombo.getValue() != null) {
            this.previousSeason = seasoncombo.getValue().toString();
        }
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
            this.activePlayers.put(eachPlayer.getInt("id"), (eachPlayer.getString("firstname") + " " + eachPlayer.getString("lastname")).trim());
        }
        HashMap<String, String> names = new HashMap();
        ArrayList<String> fixedNames = new ArrayList();
        for (int each : this.activePlayers.keySet()) {
            names.put(activePlayers.get(each).replaceAll("[^A-Za-z0-9]", "").toLowerCase().trim(), activePlayers.get(each));
            fixedNames.add(activePlayers.get(each).replaceAll("[^A-Za-z0-9]", "").toLowerCase().trim());
        }
        Collections.sort(fixedNames);
        LinkedList<String> realNames = new LinkedList();
        for (String each : fixedNames) {
            realNames.add(names.get(each));
        }
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
        jsonObjOut.put("selector", "simple" + currentSimpleSearch.toString().toLowerCase());
        jsonObjOut.put("year", this.yearcombo.getValue().toString());
        jsonObjOut.put("playername", nameHash.get(this.playercombo.getValue().toString()));
        jsonObjOut.put("seasontype", this.seasoncombo.getValue().toString());
        Main.getPrintWriterOut().println(jsonObjOut.toString());
        return new JSONArray(Main.getServerResponse().readLine());
    }

    private void idwGrid() {
        coordValue = new ConcurrentHashMap();
        double predictedValue = 0;
        double aSum = 0;
        double bSum = 0;
        int p = 2;
        double valueI = 0;
        for (Coordinate each : coordAverages.keySet()) {
            if (each.getX() % OFFSET == 0 && (each.getY() - 5) % OFFSET == 0) {
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
    }

    private double getDistance(Coordinate coordOrig, Coordinate coordI) {
        return Math.sqrt(Math.pow(coordOrig.getX() - coordI.getX(), 2) + Math.pow(coordOrig.getY() - coordI.getY(), 2));
    }

    private HashMap<String, BigDecimal> useGridAverages() throws IOException {
        HashMap<String, BigDecimal> hashmap = new HashMap();
        JSONArray jsonArray = getGridAveragesData();
        for (int i = 0; i < jsonArray.length(); i++) {
            hashmap.put(jsonArray.getJSONObject(i).getString("uniqueid"), jsonArray.getJSONObject(i).getBigDecimal("average"));
        }
        return hashmap;
    }

    private void resizeGrid() {
        double height = (imageview.localToParent(imageview.getBoundsInLocal()).getHeight());
        double width = imageview.localToParent(imageview.getBoundsInLocal()).getWidth();
        font = new BigDecimal(COMBO_FONT_SIZE).multiply(new BigDecimal(height)).divide(new BigDecimal("550"), 6, RoundingMode.HALF_UP).doubleValue();
        setViewTypeButtonStyle(1);
        gridbackground.setWidth(width);
        gridbackground.setHeight(height);
        imagegrid.setMaxWidth(width);
        imagegrid.setMaxHeight(height);
        imagegrid.setMinWidth(width);
        imagegrid.setMinHeight(height);
        imagegrid.setPrefWidth(width);
        imagegrid.setPrefHeight(height);
        gridlegendcolor.setTranslateX(height * (-155.0 / 470));
        gridlegendcolor.setTranslateY(height * (185.0 / 470));
        gridlegendsize.setTranslateX(height * (155.0 / 470));
        gridlegendsize.setTranslateY(height * (185.0 / 470));
        gridlegendcolor.setMaxWidth(height * 170.0 / 470);
        gridlegendcolor.setMinWidth(height * 170.0 / 470);
        gridlegendcolor.setPrefWidth(height * 170.0 / 470);
        gridlegendsize.setMaxWidth(height * 170.0 / 470);
        gridlegendsize.setMinWidth(height * 170.0 / 470);
        gridlegendsize.setPrefWidth(height * 170.0 / 470);
        Rectangle tempRect;
        double nodeCounter = 0;
        for (Node each : gridsizelegendgradient.getChildren()) {
            tempRect = (Rectangle) each;
            tempRect.setWidth(((nodeCounter * 1.5) + 2) * height / 470);
            tempRect.setHeight(((nodeCounter * 1.5) + 2) * height / 470);
            nodeCounter++;
        }
        gridcolorlegendtoplabel.setStyle("-fx-font: " + height * 13.0 / 470 + "px \"Lucida Sans\"; ");
        gridsizelegendtoplabel.setStyle("-fx-font: " + height * 13.0 / 470 + "px \"Lucida Sans\";");
        gridcolorlegendlowerlabel.setStyle("-fx-font: " + height * 11.0 / 470 + "px \"Lucida Sans\";");
        gridcolorlegendupperlabel.setStyle("-fx-font: " + height * 11.0 / 470 + "px \"Lucida Sans\";");
        gridsizelegendlowerlabel.setStyle("-fx-font: " + height * 11.0 / 470 + "px \"Lucida Sans\";");
        gridsizelegendupperlabel.setStyle("-fx-font: " + height * 11.0 / 470 + "px \"Lucida Sans\";");
        gridcolorlegendgradient.setWidth(height * 153.0 / 470);
        gridcolorlegendgradient.setHeight(height * 17.0 / 470);
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
        double height = (imageview.localToParent(imageview.getBoundsInLocal()).getHeight());
        double width = imageview.localToParent(imageview.getBoundsInLocal()).getWidth();
        font = new BigDecimal(COMBO_FONT_SIZE).multiply(new BigDecimal(imageview.getLayoutBounds().getHeight())).divide(new BigDecimal("550"), 6, RoundingMode.HALF_UP).doubleValue();
        fontGrid = new BigDecimal(STAT_GRID_FONT_SIZE).multiply(new BigDecimal(imageview.getLayoutBounds().getHeight())).divide(new BigDecimal("550"), 6, RoundingMode.HALF_UP).doubleValue();
        setViewTypeButtonStyle(10);
        try {
            if (searchvbox.isVisible()) {
                switch (currentSimpleSearch) {
                    case TRADITIONAL:
                        resizeShots();
                        break;
                    case GRID:
                        resizeGrid();
                        break;
                    case HEAT:
                        resizeHeat();
                        break;
                    case ZONE:
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
//                createThreadAndRun(currentSimpleSearch);

            } else {
                switch (currentAdvancedSearch) {
                    case TRADITIONAL:
                        resizeShots();
                        break;
                    case GRID:
                        resizeGrid();
                        break;
                    case HEAT:
                        resizeHeat();
                        break;
                    case ZONE:
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
                Label label;
                for (Node each : selectionvbox.getChildren()) {
                    try {
                        hbox = (HBox) each;
                        for (Node eachInner : hbox.getChildren()) {
                            if (eachInner.getClass().equals(Label.class)) {
                                label = (Label) eachInner;
                                label.setStyle("-fx-font: " + font * 0.85 + "px \"Arial\";");
                            }
                        }
                    } catch (Exception ex) {

                    }
                }
//                createThreadAndRun(currentAdvancedSearch);
            }
            mask.setWidth(width);
            mask.setHeight(height);
            imagegrid.setClip(mask);
            this.simplelayoutbutton.setStyle("-fx-font: " + font + "px \"Serif\";");
            this.advancedlayoutbutton.setStyle("-fx-font: " + font + "px \"Serif\";");
            this.comparelayoutbutton.setStyle("-fx-font: " + font + "px \"Serif\";");
            titlelabel.setMinWidth(Region.USE_PREF_SIZE);
            titlelabel.setStyle("-fx-font: " + fontGrid * 3 + "px \"Serif\"; ");
            charttitle.setStyle("-fx-font: " + fontGrid * 1.15 + "px \"Arial Italic\";");
            charttitle.setMinHeight(height / 10);
            charttitle.setMinWidth(width);
            charttitle.setMaxHeight(height / 10);
            charttitle.setMaxWidth(width);
            gridbackground.setWidth(width);
            gridbackground.setHeight(height);
            imagegrid.setMaxWidth(width);
            imagegrid.setMaxHeight(height);
            imagegrid.setMinWidth(width);
            imagegrid.setMinHeight(height);
            imagegrid.setPrefWidth(width);
            imagegrid.setPrefHeight(height);
            VBox.setMargin(introlabel, new Insets(new BigDecimal(height).multiply(new BigDecimal("20")).divide(new BigDecimal("475"), 6, RoundingMode.HALF_UP).doubleValue(), 0, 0, 0));
            VBox.setMargin(yearcombo, new Insets(20, 0, 0, 0));
            VBox.setMargin(playercombo, new Insets(20, 0, 0, 0));
            VBox.setMargin(seasoncombo, new Insets(20, 0, 0, 0));
            VBox.setMargin(searchbutton, new Insets(20, 0, 0, 0));
            loadingoverlay.setWidth(width);
            loadingoverlay.setHeight(height);
            progresslabel.setStyle("-fx-font: " + height * 16.0 / 470 + "px \"Arial Black\";");
            progressindicator.setPrefHeight(height * 70.0 / 470);
            progressindicator.setPrefWidth(height * 70.0 / 470);
        } catch (Exception ex) {

        }
    }

    private void resizeHeat() {
        double height = (imageview.localToParent(imageview.getBoundsInLocal()).getHeight());
        font = new BigDecimal(COMBO_FONT_SIZE).multiply(new BigDecimal(height)).divide(new BigDecimal("550"), 6, RoundingMode.HALF_UP).doubleValue();
        setViewTypeButtonStyle(2);
        Circle tempCircle;
        int keyCounter = 0;
        for (Coordinate each : coordValue.keySet()) {
            tempCircle = (Circle) allHeatCircles.get(keyCounter);
            tempCircle.setTranslateX(each.getX() * 1.0 * imageview.getLayoutBounds().getHeight() / 470);
            tempCircle.setTranslateY(each.getY() * 1.0 * imageview.getLayoutBounds().getHeight() / 470 - (185.0 * height / 470));
            tempCircle.setRadius(25.0 * height / 470.0);
            keyCounter++;
        }

        heatlegend.setTranslateX(height * (-155.0 / 470));
        heatlegend.setTranslateY(height * (185.0 / 470));
        heatlegend.setMaxWidth(height * 170.0 / 470);
        heatlegend.setMinWidth(height * 170.0 / 470);
        heatlegend.setPrefWidth(height * 170.0 / 470);
        heatlegendgradient.setWidth(height * 153.0 / 470);
        heatlegendgradient.setHeight(height * 17.0 / 470);
        heatlegendtoplabel.maxWidthProperty().bind(heatlegend.maxWidthProperty());
        heatlegendlowerlabel.maxWidthProperty().bind(heatlegend.maxWidthProperty().multiply(0.5));
        heatlegendupperlabel.maxWidthProperty().bind(heatlegend.maxWidthProperty().multiply(0.5));
        heatlegendlowerlabel.minWidthProperty().bind(heatlegend.maxWidthProperty().multiply(0.45));
        heatlegendupperlabel.minWidthProperty().bind(heatlegend.maxWidthProperty().multiply(0.45));
        heatlegendtoplabel.setStyle("-fx-font: " + height * 13.0 / 470 + "px \"Lucida Sans\";");
        heatlegendlowerlabel.setStyle("-fx-font: " + height * 11.0 / 470 + "px \"Lucida Sans\";");
        heatlegendupperlabel.setStyle("-fx-font: " + height * 11.0 / 470 + "px \"Lucida Sans\";");
    }

    private void resizeZone() {
        double height = (imageview.localToParent(imageview.getBoundsInLocal()).getHeight());
        double width = imageview.localToParent(imageview.getBoundsInLocal()).getWidth();
        font = new BigDecimal(COMBO_FONT_SIZE).multiply(new BigDecimal(height)).divide(new BigDecimal("550"), 6, RoundingMode.HALF_UP).doubleValue();
        setViewTypeButtonStyle(3);
        mask = new Rectangle(width, height);
        imagegrid.setClip(mask);
        Node each;
        Label eachLabel;
        Label eachLabelPercent;
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
        zonelegend.setTranslateX(height * (-155.0 / 470));
        zonelegend.setTranslateY(height * (185.0 / 470));
        zonelegend.setMaxWidth(height * 170.0 / 470);
        zonelegend.setMinWidth(height * 170.0 / 470);
        zonelegend.setPrefWidth(height * 170.0 / 470);
        zonelegendgradient.setWidth(height * 153.0 / 470);
        zonelegendgradient.setHeight(height * 17.0 / 470);
        zonelegendtoplabel.maxWidthProperty().bind(zonelegend.maxWidthProperty());
        zonelegendlowerlabel.maxWidthProperty().bind(zonelegend.maxWidthProperty().multiply(0.5));
        zonelegendupperlabel.maxWidthProperty().bind(zonelegend.maxWidthProperty().multiply(0.5));
        zonelegendlowerlabel.minWidthProperty().bind(zonelegend.maxWidthProperty().multiply(0.45));
        zonelegendupperlabel.minWidthProperty().bind(zonelegend.maxWidthProperty().multiply(0.45));
        zonelegendtoplabel.setStyle("-fx-font: " + height * 13.0 / 470 + "px \"Lucida Sans\";");
        zonelegendlowerlabel.setStyle("-fx-font: " + height * 11.0 / 470 + "px \"Lucida Sans\";");
        zonelegendupperlabel.setStyle("-fx-font: " + height * 11.0 / 470 + "px \"Lucida Sans\";");
    }

    private void createThreadAndRun(Search selector) {
        Search givenSearch = selector;
        switch (givenSearch) {
            case TRADITIONAL:
                if (tTrad.isAlive()) {
                    tTrad.interrupt();
                }
                tTrad = new Thread(() -> {
                    Platform.runLater(() -> threadRunner(givenSearch));
                });
                tTrad.start();
                break;
            case GRID:
                if (tGrid.isAlive()) {
                    tGrid.interrupt();
                }
                tGrid = new Thread(() -> {
                    Platform.runLater(() -> threadRunner(givenSearch));
                });
                tGrid.start();
                break;
            case HEAT:
                if (tHeat.isAlive()) {
                    tHeat.interrupt();
                }
                tHeat = new Thread(() -> {
                    Platform.runLater(() -> threadRunner(givenSearch));
                });
                tHeat.start();
                break;
            case ZONE:
                if (tZone.isAlive()) {
                    tZone.interrupt();
                }
                tZone = new Thread(() -> {
                    Platform.runLater(() -> threadRunner(givenSearch));
                });
                tZone.start();
                break;
        }
    }

    private void removeAllShotsFromView() {
        ArrayList<Node> toRemove = new ArrayList();
        for (Node each : imagegrid.getChildren()) {
            if (!each.equals(gridbackground) && !each.equals(rect11) && !each.equals(rect15) && !each.equals(loadingoverlay) && (each.getClass().equals(Rectangle.class) || each.getClass().equals(Circle.class) || each.getClass().equals(Line.class))) {
                toRemove.add(each);
            }
        }
        for (Node each : toRemove) {
            imagegrid.getChildren().remove(each);
        }
    }

    private void traditional() {
        if (searchvbox.isVisible()) {
            currentSimpleSearch = Search.TRADITIONAL;
            try {
                this.previousYear = this.yearcombo.getValue().toString();
                this.previousPlayer = this.playercombo.getValue().toString();
                this.previousSeason = this.seasoncombo.getValue().toString();
            } catch (NullPointerException ex) {
                this.errorlabel.setText("Please try again");
                this.errorlabel.setVisible(true);
            }
        } else {
            currentAdvancedSearch = Search.TRADITIONAL;
        }
        tradService.reset();
        tradService.start();
        startLoadingTransition();
    }

    private void grid() {
        if (searchvbox.isVisible()) {
            currentSimpleSearch = Search.GRID;
            try {
                this.previousYear = this.yearcombo.getValue().toString();
                this.previousPlayer = this.playercombo.getValue().toString();
                this.previousSeason = this.seasoncombo.getValue().toString();
            } catch (NullPointerException ex) {
                this.errorlabel.setText("Please try again");
                this.errorlabel.setVisible(true);
            }
        } else {
            currentAdvancedSearch = Search.GRID;
        }

        gridService.reset();
        gridService.start();
        startLoadingTransition();
    }

    private void heat() {
        if (searchvbox.isVisible()) {
            currentSimpleSearch = Search.HEAT;
            try {
                this.previousYear = this.yearcombo.getValue().toString();
                this.previousPlayer = this.playercombo.getValue().toString();
                this.previousSeason = this.seasoncombo.getValue().toString();
            } catch (NullPointerException ex) {
                this.errorlabel.setText("Please try again");
                this.errorlabel.setVisible(true);
            }
        } else {
            this.currentAdvancedSearch = Search.HEAT;
        }

        heatService.reset();
        heatService.start();
        startLoadingTransition();
    }

    private void setCircle(Circle circle, int x, int y) {
        circle.setTranslateX(x * 1.0 * imageview.getLayoutBounds().getHeight() / 470);
        circle.setTranslateY(y * 1.0 * imageview.getLayoutBounds().getHeight() / 470 - (185.0 * imageview.localToParent(imageview.getBoundsInLocal()).getHeight() / 470));
        circle.setOpacity(0.75);
    }

    private void organizeZoneFXMLElements() {
        Collections.addAll(allZoneFXML, group1, rect1, arc1, group2, rect2, arc2, group3, rect3,
                arc3, arc4, group5, rect5, arc5, group6, rect6, arc6, arc7, arc8, arc9, group10,
                rect10, arc10, rect11, group12, rect12, arc12, arc13, group14, rect14, arc14, rect15,
                label1, label2, label3, label4, label5, label6, label7, label8, label9, label10, label11,
                label12, label13, label14, label15, labelpercent1, labelpercent2, labelpercent3,
                labelpercent4, labelpercent5, labelpercent6, labelpercent7, labelpercent8, labelpercent9,
                labelpercent10, labelpercent11, labelpercent12, labelpercent13, labelpercent14,
                labelpercent15, zonelegend, zonelegendtoplabel, zonelegendlowerlabel,
                zonelegendupperlabel, zonelegendgradient);
        allLabels = new LinkedList();
        Collections.addAll(allLabels, label1, label2, label3, label4, label5, label6, label7, label8, label9,
                label10, label11, label12, label13, label14, label15);
        allPercentLabels = new LinkedList();
        Collections.addAll(allPercentLabels, labelpercent1, labelpercent2, labelpercent3,
                labelpercent4, labelpercent5, labelpercent6, labelpercent7, labelpercent8,
                labelpercent9, labelpercent10, labelpercent11, labelpercent12,
                labelpercent13, labelpercent14, labelpercent15);
        Shape shape1 = Shape.union(rect1, arc1);
        Shape shape2 = Shape.union(rect2, arc2);
        Shape shape3 = Shape.union(rect3, arc3);
        Shape shape5 = Shape.union(rect5, arc5);
        Shape shape6 = Shape.union(rect6, arc6);
        Shape shape10 = Shape.union(rect10, arc10);
        Shape shape12 = Shape.union(rect12, arc12);
        Shape shape14 = Shape.union(rect14, arc14);
        allShapes = new LinkedList();
        Collections.addAll(allShapes, shape1, shape2, shape3, arc4, shape5, shape6, arc7, arc8, arc9,
                shape10, rect11, shape12, arc13, shape14, rect15);
        double strokeWidth = 3.0;
        Paint strokeColor = Color.web("#434343");
        Arc tempArc;
        Shape tempShape;
        for (Node node : allShapes) {
            if (node.getClass().equals(Arc.class)) {
                tempArc = (Arc) node;
                tempArc.setStroke(strokeColor);
                tempArc.setStrokeWidth(strokeWidth);
                tempArc.setStrokeType(StrokeType.OUTSIDE);
            } else if (!node.getClass().equals(Rectangle.class)) {
                tempShape = (Shape) node;
                tempShape.setStroke(strokeColor);
                tempShape.setStrokeWidth(strokeWidth);
                tempShape.setStrokeType(StrokeType.OUTSIDE);
                imagegrid.getChildren().add(node);
            }
        }
        rect11.toFront();
        rect15.toFront();
        shape12.toFront();
        shape14.toFront();
        arc13.toFront();
        shape6.toFront();
        shape10.toFront();
        arc8.toFront();
        arc7.toFront();
        arc9.toFront();
        shape3.toFront();
        shape5.toFront();
        arc4.toFront();
        shape2.toFront();
        shape1.toFront();
        shape1.setStrokeType(StrokeType.INSIDE);
        allShapes.forEach(eachNode -> eachNode.setVisible(false));
    }

    private void zone() {
        if (searchvbox.isVisible()) {
            currentSimpleSearch = Search.ZONE;
            try {
                this.previousYear = this.yearcombo.getValue().toString();
                this.previousPlayer = this.playercombo.getValue().toString();
                this.previousSeason = this.seasoncombo.getValue().toString();
            } catch (NullPointerException ex) {
                this.errorlabel.setText("Please try again");
                this.errorlabel.setVisible(true);
            }

        } else {
            currentAdvancedSearch = Search.ZONE;
        }
        zoneService.reset();
        zoneService.start();
        startLoadingTransition();
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
            diff = playerValue - allZoneAverages.get(i);
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
        endLoadingTransition();
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
        fgfrac.setText(countMade + "/" + countTotal);
        if (countTotal == 0) {
            fgperc.setText("--");
        } else {
            fgperc.setText(new BigDecimal((double) countMade / countTotal * 100).setScale(2, RoundingMode.HALF_UP) + "%");
        }
        twopointfrac.setText(count2pMade + "/" + count2pTotal);
        if (count2pTotal == 0) {
            twopointperc.setText("--");
        } else {
            twopointperc.setText(new BigDecimal((double) count2pMade / count2pTotal * 100).setScale(2, RoundingMode.HALF_UP) + "%");
        }
        threepointfrac.setText(count3pMade + "/" + count3pTotal);
        if (count3pTotal == 0) {
            threepointperc.setText("--");
        } else {
            threepointperc.setText(new BigDecimal((double) count3pMade / count3pTotal * 100).setScale(2, RoundingMode.HALF_UP) + "%");
        }
        charttitle.setText(this.playercombo.getValue().toString() + ", " + this.yearcombo.getValue().toString() + " " + this.seasoncombo.getValue().toString());
        charttitle.setVisible(true);

    }

    private void threadRunner(Search search) {
        try {

            for (int i = 0; i < 3; i++) {
                try {
                    Thread.sleep(300);
                    switch (search) {
                        case TRADITIONAL:
                            resizeShots();
                            break;
                        case GRID:
                            resizeGrid();
                            break;
                        case HEAT:
                            resizeHeat();
                            break;
                        case ZONE:
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
            System.out.println("Error caught running resize threads");
        }
    }

    private void ultraFineHeatMapThreader() throws InterruptedException {
        allUltraFineHeatThreads = new ArrayList();
//        offsetHeat = 10;
        int maxThreads = 6;
        Thread thread;
        for (int i = 0; i < maxThreads; i++) {
            final int iFinal = i;
            final int iMaxFinal = maxThreads;
            thread = new Thread(() -> {
                double aSum = 0;
                double bSum = 0;
                int p = 2;
                int eachCounter = 0;
                int iFinalThread = iFinal;
                for (Coordinate each : coordAverages.keySet()) {
                    if (each.getY() >= (452 / iMaxFinal) * iFinalThread - 52 && each.getY() < (452 / iMaxFinal) * (iFinalThread + 1) - 52
                            && each.getX() % offsetHeat == 0 && each.getY() % offsetHeat == 0) {
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

            });
            allUltraFineHeatThreads.add(thread);
        }
//        long start = System.nanoTime();
        allUltraFineHeatThreads.forEach(eachThread -> eachThread.start());
        boolean done = false;
        while (!done) {
            try {
                for (Thread eachThread : allUltraFineHeatThreads) {
                    eachThread.join();
                }
//                Thread.sleep(1000);
                done = true;
            } catch (InterruptedException ex) {

            }

        }
//        long end = System.nanoTime();
//        System.out.println("ultraFineThreader: " + (end - start) * 1.0 / 1000000000 + " seconds");

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
            deleteButton.setStyle("-fx-text-fill: red;-fx-background-color: transparent; ");
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
        Label label;
        Button deleteButton = null;
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
        Collections.addAll(tempList, "Makes", "Misses");
        shotsuccesscombo.setItems(FXCollections.observableArrayList(tempList));
        tempList = new ArrayList();
        Collections.addAll(tempList, "2PT", "3PT");
        shotvaluecombo.setItems(FXCollections.observableArrayList(tempList));
        tempList = getShotTypesList();
        UserInputComboBox shotTypeComboUser = new UserInputComboBox(shottypescombo);
        shotTypeComboUser.getComboBox().setItems(FXCollections.observableArrayList(tempList));
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
        UserInputComboBox teamComboUser = new UserInputComboBox(teamscombo);
        UserInputComboBox homeTeamComboUser = new UserInputComboBox(hometeamscombo);
        UserInputComboBox awayTeamComboUser = new UserInputComboBox(awayteamscombo);
        teamComboUser.getComboBox().setItems(FXCollections.observableArrayList(relevantTeamNameIDHashMap.keySet()));
        homeTeamComboUser.getComboBox().setItems(FXCollections.observableArrayList(relevantTeamNameIDHashMap.keySet()));
        awayTeamComboUser.getComboBox().setItems(FXCollections.observableArrayList(relevantTeamNameIDHashMap.keySet()));
        tempList = new ArrayList();
        Collections.addAll(tempList, "Restricted Area", "In The Paint (Non-RA)", "Mid-Range",
                "Left Corner 3", "Right Corner 3", "Above the Break 3", "Backcourt");
        courtareascombo.setItems(FXCollections.observableArrayList(tempList));
        tempList = new ArrayList();
        Collections.addAll(tempList, "Left", "Left-Center", "Center",
                "Right-Center", "Right", "Back Court");
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

    private JSONArray createAdvancedJSONOutput(Search searchTypeSelector) throws IOException, Exception {
        JSONObject obj = new JSONObject();
        obj.put("selector", "advanced" + searchTypeSelector.toString().toLowerCase());
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
                cb.valueProperty().addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
                    try {
                        if (cb.getValue() != null) {
                            addHBoxToSelectionBox(cb.getId());
                        }
                    } catch (Exception ex) {
                        System.out.println("Error caught adding to selection box");
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
                        cb.valueProperty().addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
                            try {
                                if (cb.getValue() != null) {
                                    addHBoxToSelectionBox(cb.getId());
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        });
                    }
                }
            }
        }
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

    private void setEachViewTypeButtonOnClicked(int selector, Search currentSearch) {
        setViewTypeButtonStyle(selector);
        if (searchvbox.isVisible()) {
            try {
                this.previousYear = this.yearcombo.getValue().toString();
                this.previousPlayer = this.playercombo.getValue().toString();
                this.previousSeason = this.seasoncombo.getValue().toString();
                currentSimpleSearch = currentSearch;
                if (!currentSimpleSearch.equals(Search.NONE)) {
                    runSearch();
                }
            } catch (NullPointerException ex) {
                this.errorlabel.setText("Please select one from each category");
                this.errorlabel.setVisible(true);
            }

        } else {
            if (checkForEmptyAdvancedSearch()) {
                currentAdvancedSearch = currentSearch;
                if (!currentAdvancedSearch.equals(Search.NONE)) {
                    runSearch();
                }
            } else {
                this.errorlabeladvanced.setText("Please include at least one search parameter");
                this.errorlabeladvanced.setVisible(true);
            }
        }

    }

    private void setEachViewTypeButtonsOnMouseEntered(int selector, Search currentSearch) {
        if ((searchvbox.isVisible() && currentSimpleSearch.equals(currentSearch)) || (advancedvbox.isVisible() && currentAdvancedSearch.equals(currentSearch))) {
            viewButtons.get(selector).setStyle("-fx-font: " + font + "px \"Arial Black\";-fx-background-color: transparent;-fx-underline: true;");
        } else {
            viewButtons.get(selector).setStyle("-fx-font: " + font + "px \"Arial\";-fx-background-color: transparent;-fx-underline: true;");
        }
    }

    private void setEachViewTypeButtonsOnMouseExited(int selector, Search currentSearch) {
        if ((searchvbox.isVisible() && currentSimpleSearch.equals(currentSearch)) || (advancedvbox.isVisible() && currentAdvancedSearch.equals(currentSearch))) {
            viewButtons.get(selector).setStyle("-fx-font: " + font + "px \"Arial Black\";-fx-background-color: transparent;-fx-underline: false;");
        } else {
            viewButtons.get(selector).setStyle("-fx-font: " + font + "px \"Arial\";-fx-background-color: transparent;-fx-underline: false;");
        }
    }

    private void setAllViewTypeButtonsOnMouseActions() {
        font = new BigDecimal(COMBO_FONT_SIZE).multiply(new BigDecimal(imageview.getLayoutBounds().getHeight())).divide(new BigDecimal("550"), 6, RoundingMode.HALF_UP).doubleValue();
        this.traditionalbutton.setOnMouseClicked(t -> setEachViewTypeButtonOnClicked(0, Search.TRADITIONAL));
        this.gridbutton.setOnMouseClicked(t -> setEachViewTypeButtonOnClicked(1, Search.GRID));
        this.heatmapbutton.setOnMouseClicked(t -> setEachViewTypeButtonOnClicked(2, Search.HEAT));
        this.zonebutton.setOnMouseClicked(t -> setEachViewTypeButtonOnClicked(3, Search.ZONE));
        this.traditionalbutton.setOnMouseEntered(t -> setEachViewTypeButtonsOnMouseEntered(0, Search.TRADITIONAL));
        this.traditionalbutton.setOnMouseExited(t -> setEachViewTypeButtonsOnMouseExited(0, Search.TRADITIONAL));
        this.gridbutton.setOnMouseEntered(t -> setEachViewTypeButtonsOnMouseEntered(1, Search.GRID));
        this.gridbutton.setOnMouseExited(t -> setEachViewTypeButtonsOnMouseExited(1, Search.GRID));
        this.heatmapbutton.setOnMouseEntered(t -> setEachViewTypeButtonsOnMouseEntered(2, Search.HEAT));
        this.heatmapbutton.setOnMouseExited(t -> setEachViewTypeButtonsOnMouseExited(2, Search.HEAT));
        this.zonebutton.setOnMouseEntered(t -> setEachViewTypeButtonsOnMouseEntered(3, Search.ZONE));
        this.zonebutton.setOnMouseExited(t -> setEachViewTypeButtonsOnMouseExited(3, Search.ZONE));
    }

    private void runSearch() {
        Search tempSearch;
        if (searchvbox.isVisible()) {
            tempSearch = currentSimpleSearch;
        } else {
            tempSearch = currentAdvancedSearch;
        }
        viewButtons.forEach(button -> {
            button.setDisable(true);
            button.setOpacity(0.5);
        });
        simplelayoutbutton.setDisable(true);
        simplelayoutbutton.setOpacity(0.5);
        advancedlayoutbutton.setDisable(true);
        advancedlayoutbutton.setOpacity(0.5);
        if (searchvbox.isVisible()) {
            searchbutton.setDisable(true);
            searchbutton.setOpacity(0.5);
        } else {
            searchbuttonadvanced.setDisable(true);
            searchbuttonadvanced.setOpacity(0.5);
        }
        start = System.nanoTime();

        switch (tempSearch) {
            case TRADITIONAL:
                traditional();
                break;
            case GRID:
                grid();
                break;
            case HEAT:
                heat();
                break;
            case ZONE:
                zone();
                break;
            default:
                traditional();
                break;
        }

    }

    private void changeButtonStyles() {
        Search tempSearch;
        if (searchvbox.isVisible()) {
            tempSearch = currentSimpleSearch;
        } else {
            tempSearch = currentAdvancedSearch;
        }
        switch (tempSearch) {
            case TRADITIONAL:
                setViewTypeButtonStyle(0);
                break;
            case GRID:
                setViewTypeButtonStyle(1);
                break;
            case HEAT:
                setViewTypeButtonStyle(2);
                break;
            case ZONE:
                setViewTypeButtonStyle(3);
                break;
            default:
                setViewTypeButtonStyle(10);
                break;
        }
    }

    private void plotHeatAfterServiceSucceeds(JSONArray jsonArray) {
        resetView();
        removeAllShotsFromView();
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

        if (searchvbox.isVisible()) {
            setShotGrid(jsonArray);
        } else {
            setShotGridAdvanced(jsonArray);
        }

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
        circles1.forEach(circle -> imagegrid.getChildren().add(circle));
        circles2.forEach(circle -> imagegrid.getChildren().add(circle));
        circles3.forEach(circle -> imagegrid.getChildren().add(circle));
        circles4.forEach(circle -> imagegrid.getChildren().add(circle));
        circles5.forEach(circle -> imagegrid.getChildren().add(circle));
        circles6.forEach(circle -> imagegrid.getChildren().add(circle));
        circles7.forEach(circle -> imagegrid.getChildren().add(circle));
        endLoadingTransition();
        enableButtons();
        end = System.nanoTime();
        System.out.println("HEAT: " + (end - start) * 1.0 / 1000000000 + " seconds");
    }

    private ConcurrentHashMap<Coordinate, Double> serviceTaskMethodsHeat(boolean isSearchVboxVisible) throws IOException {
        Platform.runLater(() -> progresslabel.setText("Gathering Shots"));
        try {
            coordAverages = new HashMap();
            ArrayList info = new ArrayList();
            info.add(0.0);
            info.add(0.0);
            info.add(0.0);
            for (int x = -250; x < 251; x++) {
                for (int y = -52; y < 400; y++) {
                    coordAverages.put(new Coordinate(x, y), new ArrayList(info));
                }
            }
            JSONArray jsonArray;
            if (isSearchVboxVisible) {
                jsonArray = getSimpleShotData();
            } else {
                jsonArray = createAdvancedJSONOutput(currentAdvancedSearch);
            }
            lastJsonArray = jsonArray;
            shotCounter = 0;
            Platform.runLater(() -> progresslabel.setText("Generating Heat Map"));
            Coordinate tempCoord;
            JSONObject eachShot;
            for (int i = 0; i < jsonArray.length(); i++) {
                eachShot = jsonArray.getJSONObject(i);
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
            coordValue = new ConcurrentHashMap();
            allUltraFineHeatThreads = new ArrayList();
            int maxThreads = 5;
            Thread thread;
            final ArrayList<Coordinate> coordsList = new ArrayList(300000);
            coordsList.addAll(coordAverages.keySet());
            for (int i = 0; i < maxThreads; i++) {
                final int iFinal = i;
                final int iMaxFinal = maxThreads;
                thread = new Thread(() -> {
                    double aSum = 0;
                    double bSum = 0;
                    int p = 2;
                    int eachCounter = 0;
                    int iFinalThread = iFinal;
                    int maxSurroundingCoords = (int) Math.pow(MAX_DISTANCE_BETWEEN_NODES_HEAT * 2, 2);
                    int surroundingCounter;
//                    for (Coordinate each : coordAverages.keySet()) {
                    for (Coordinate each : coordsList) {
                        if (each.getX() % offsetHeat == 0 && each.getY() % offsetHeat == 0 && each.getY() >= (452 / iMaxFinal) * iFinalThread - 52
                                && each.getY() < (452 / iMaxFinal) * (iFinalThread + 1) - 52) {
                            aSum = 0;
                            bSum = 0;
                            surroundingCounter = 0;
//                            for (Coordinate each2 : coordAverages.keySet()) {
                            for (Coordinate each2 : coordsList) {
                                if (surroundingCounter >= maxSurroundingCoords) {
                                    break;
                                } else if (!each.equals(each2) && getDistance(each, each2) < MAX_DISTANCE_BETWEEN_NODES_HEAT) {
                                    surroundingCounter++;
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

                });
                allUltraFineHeatThreads.add(thread);
            }
            allUltraFineHeatThreads.forEach(eachThread -> eachThread.start());
            boolean done = false;
            while (!done) {
                try {
                    for (Thread eachThread : allUltraFineHeatThreads) {
                        eachThread.join();
                    }
                    done = true;
                } catch (InterruptedException ex) {

                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return coordValue;
    }

    private void createServices() {
        tradService = new Service() {
            @Override
            protected Task<LinkedHashMap<Shot, Object>> createTask() {
                return new Task() {
                    @Override
                    protected LinkedHashMap<Shot, Object> call() throws Exception {
                        return serviceTaskMethodsTrad(searchvbox.isVisible());
                    }

                };
            }
        };
        tradService.setOnSucceeded(s -> {
            plotTradAfterServiceSucceeds(lastJsonArray);
        });
        tradService.setOnFailed(s -> {
            System.out.println("Failed");
            endLoadingTransition();
            enableButtons();
        });
        gridService = new Service() {
            @Override
            protected Task<ConcurrentHashMap<Coordinate, Double>> createTask() {
                return new Task() {
                    @Override
                    protected ConcurrentHashMap<Coordinate, Double> call() throws Exception {
                        return serviceTaskMethodsGrid(searchvbox.isVisible());
                    }
                };
            }
        };
        gridService.setOnSucceeded(s -> {
            plotGridAfterServiceSucceeds(lastJsonArray);
        });
        gridService.setOnFailed(s -> {
            System.out.println("Failed");
            endLoadingTransition();
            enableButtons();
        });
        heatService = new Service() {
            @Override
            protected Task<ConcurrentHashMap<Coordinate, Double>> createTask() {
                return new Task() {
                    @Override
                    protected ConcurrentHashMap<Coordinate, Double> call() throws Exception {
                        return serviceTaskMethodsHeat(searchvbox.isVisible());
                    }
                };
            }
        };
        heatService.setOnSucceeded(s -> {
            plotHeatAfterServiceSucceeds(lastJsonArray);
        });
        heatService.setOnFailed(s -> {
            System.out.println("Failed");
            endLoadingTransition();
            enableButtons();
        });
        zoneService = new Service() {
            @Override
            protected Task<HashMap<Integer, Double>> createTask() {
                return new Task() {
                    @Override
                    protected HashMap<Integer, Double> call() throws Exception {
                        return serviceTaskMethodsZone(searchvbox.isVisible());
                    }
                };
            }
        };
        zoneService.setOnSucceeded(s -> {
            plotZoneAfterServiceSucceeds(lastJsonArray, (HashMap<Integer, Double>) zoneService.getValue());
        });
        zoneService.setOnFailed(s -> {
            System.out.println("Failed");
            endLoadingTransition();
            enableButtons();
        });
    }

    private LinkedHashMap<Shot, Object> serviceTaskMethodsTrad(boolean isSearchVboxVisible) throws IOException, Exception {
        Platform.runLater(() -> progresslabel.setText("Gathering Shots"));

        JSONArray jsonArray;
        try {
            if (isSearchVboxVisible) {
                jsonArray = getSimpleShotData();
            } else {
                jsonArray = createAdvancedJSONOutput(currentAdvancedSearch);
            }
            Platform.runLater(() -> progresslabel.setText("Generating Traditional Shot Map"));

            lastJsonArray = jsonArray;
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return allShots;
    }

    private void plotTradAfterServiceSucceeds(JSONArray jsonArray) {
        resetView();
        removeAllShotsFromView();
        imageview.setImage(new Image("/images/newbackcourt.png"));
        allShots.keySet().stream()
                .filter((each) -> (each.getY() <= 410))
                .forEachOrdered((each) -> {
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
                });
        if (searchvbox.isVisible()) {
            setShotGrid(jsonArray);
        } else {
            setShotGridAdvanced(jsonArray);
        }
        endLoadingTransition();

        enableButtons();
        end = System.nanoTime();
        System.out.println("TRADITIONAL: " + (end - start) * 1.0 / 1000000000 + " seconds");
    }

    private ConcurrentHashMap<Coordinate, Double> serviceTaskMethodsGrid(boolean isSearchVboxVisible) throws IOException, Exception {
        Platform.runLater(() -> progresslabel.setText("Gathering Shots"));
        JSONArray jsonArray;
        if (isSearchVboxVisible) {
            jsonArray = getSimpleShotData();
        } else {
            jsonArray = createAdvancedJSONOutput(currentAdvancedSearch);
        }
        lastJsonArray = jsonArray;
        Platform.runLater(() -> progresslabel.setText("Generating Grid"));
        Coordinate coord;
        coordAverages = new LinkedHashMap();
        for (int j = -55; j < 400; j = j + (int) SQUARE_SIZE_ORIG) {
            for (int i = -250; i < 250; i = i + (int) SQUARE_SIZE_ORIG) {
                coord = new Coordinate(i, j);
                ArrayList info = new ArrayList();
                info.add(0.0);
                info.add(0.0);
                info.add(0.0);
                coordAverages.put(coord, info);
            }
        }
        double factor = 0.007;
        shotCounter = 0;
        HashMap<String, BigDecimal> averages = null;
        try {
            averages = useGridAverages();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        allShots = new LinkedHashMap();
        JSONObject eachShot;
        for (int i = 0; i < jsonArray.length(); i++) {
            eachShot = jsonArray.getJSONObject(i);
            if (eachShot.getInt("y") >= 400) {
                continue;
            }
            shotCounter++;
            for (Coordinate each : coordAverages.keySet()) {
                if (eachShot.getInt("x") < each.getX() + 5 + SQUARE_SIZE_ORIG * 1.5 && eachShot.getInt("x") >= each.getX() + 5 - SQUARE_SIZE_ORIG * 1.5 && eachShot.getInt("y") < each.getY() + 5 + SQUARE_SIZE_ORIG * 1.5 && eachShot.getInt("y") >= each.getY() + 5 - SQUARE_SIZE_ORIG * 1.5) {
                    coordAverages.get(each).set(1, coordAverages.get(each).get(1) + 1);
                    if (eachShot.getInt("make") == 1) {
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
            allTiles.add(square);
        }
        return coordValue;
    }

    private void plotGridAfterServiceSucceeds(JSONArray jsonArray) {
        resetView();
        removeAllShotsFromView();
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
        if (searchvbox.isVisible()) {
            setShotGrid(jsonArray);
        } else {
            setShotGridAdvanced(jsonArray);
        }
        allTiles.forEach(square -> imagegrid.add(square, 0, 0));
        endLoadingTransition();
        enableButtons();
        end = System.nanoTime();
        System.out.println("GRID: " + (end - start) * 1.0 / 1000000000 + " seconds");
    }

    private HashMap<Integer, Double> serviceTaskMethodsZone(boolean isSearchVboxVisible) throws IOException, Exception {
        Platform.runLater(() -> progresslabel.setText("Gathering Shots"));
        JSONArray jsonArray;
        if (isSearchVboxVisible) {
            jsonArray = getSimpleShotData();
        } else {
            jsonArray = createAdvancedJSONOutput(currentAdvancedSearch);
        }
        Platform.runLater(() -> progresslabel.setText("Generating Zones"));
        lastJsonArray = jsonArray;
        allZones = new HashMap();
        Double[] doubles;
        for (int i = 1; i < 16; i++) {
            doubles = new Double[3];
            doubles[0] = 0.0;
            doubles[1] = 0.0;
            doubles[2] = 0.0;
            allZones.put(i, doubles);
        }
        try {
            allZoneAverages = useZoneAverages();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        JSONObject eachShot;
        for (int i = 0; i < jsonArray.length(); i++) {
            eachShot = jsonArray.getJSONObject(i);

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
        for (Integer each : allZones.keySet()) {
            allZones.get(each)[2] = allZones.get(each)[0] * 1.0 / allZones.get(each)[1];
            playerZones.put(each, allZones.get(each)[2]);
        }
        end = System.nanoTime();
        System.out.println("ZONE: " + (end - start) * 1.0 / 1000000000 + " seconds");

        return playerZones;
    }

    private void plotZoneAfterServiceSucceeds(JSONArray jsonArray, HashMap<Integer, Double> playerZones) {
        resizeZone();
        resetView();
        removeAllShotsFromView();

        imageview.setImage(new Image("/images/transparent.png"));
        for (Node each : allShapes) {
            each.setVisible(true);
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
        if (searchvbox.isVisible()) {
            setShotGrid(jsonArray);
        } else {
            setShotGridAdvanced(jsonArray);
        }
        Shape tempShape;
        Rectangle tempRect;
        Arc tempArc;
        for (int i = 1; i < 16; i++) {
            if (allShapes.get(i - 1).getClass().equals(Arc.class)) {
                tempArc = (Arc) allShapes.get(i - 1);
                changeArcColor(tempArc, playerZones.get(i), i);
            } else if (allShapes.get(i - 1).getClass().equals(Rectangle.class)) {
                tempRect = (Rectangle) allShapes.get(i - 1);
                changeRectColor(tempRect, playerZones.get(i), i);
            } else {
                tempShape = (Shape) allShapes.get(i - 1);
                changeShapeColor(tempShape, playerZones.get(i), i);
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
        }
        zonelegend.setVisible(true);
        zonelegend.toFront();
        endLoadingTransition();
        enableButtons();
    }

    private void enableButtons() {
        viewButtons.forEach(button -> {
            button.setDisable(false);
            button.setOpacity(1);
        });
        if (searchvbox.isVisible()) {
            searchbutton.setDisable(false);
            searchbutton.setOpacity(1);
        } else {
            searchbuttonadvanced.setDisable(false);
            searchbuttonadvanced.setOpacity(1);
        }
        simplelayoutbutton.setDisable(false);
        simplelayoutbutton.setOpacity(1);
        advancedlayoutbutton.setDisable(false);
        advancedlayoutbutton.setOpacity(1);
    }

    private void startLoadingTransition() {
        loadingoverlay.toFront();
        loadingoverlay.setVisible(true);
        progressvbox.setVisible(true);
        progressvbox.toFront();
        progressindicator.setVisible(true);
    }

    private void endLoadingTransition() {
        loadingoverlay.setVisible(false);
        progressvbox.setVisible(false);
    }

    private void warmupHeat() {
        Coordinate coord;
        HashMap<Coordinate, ArrayList<Double>> coordAveragesTemp = new HashMap();
        for (int x = -250; x < 251; x++) {
            for (int y = -52; y < 400; y++) {
                coord = new Coordinate(x, y);
                ArrayList info = new ArrayList();
                info.add(0.0);
                info.add(0.0);
                info.add(0.0);
                coordAveragesTemp.put(coord, info);
            }
        }
        JSONObject jsonObjOut = new JSONObject();
        jsonObjOut.put("selector", "simpleheat");
        jsonObjOut.put("year", "2019-20");
        jsonObjOut.put("playername", nameHash.get("Aaron Gordon"));
        jsonObjOut.put("seasontype", "Regular Season");
        try {
            Main.getPrintWriterOut().println(jsonObjOut.toString());
            JSONArray jsonArray = new JSONArray(Main.getServerResponse().readLine());

            Coordinate tempCoord;
            JSONObject eachShot;
            for (int i = 0; i < jsonArray.length(); i++) {
                eachShot = jsonArray.getJSONObject(i);
                if (eachShot.getInt("y") >= 400) {
                    continue;
                }
                tempCoord = new Coordinate(eachShot.getInt("x"), eachShot.getInt("y"));
                coordAveragesTemp.get(tempCoord).set(1, coordAveragesTemp.get(tempCoord).get(1) + 1);
                if (eachShot.getInt("make") == 1) {
                    coordAveragesTemp.get(tempCoord).set(0, coordAveragesTemp.get(tempCoord).get(0) + 1);
                }
            }
            for (Coordinate each : coordAveragesTemp.keySet()) {
                if (coordAveragesTemp.get(each).get(1) != 0) {
                    coordAveragesTemp.get(each).set(2, coordAveragesTemp.get(each).get(0) * 1.0 / coordAveragesTemp.get(each).get(1) * 1.0);
                }

            }
            int maxThreads = 4;
            ArrayList<Thread> threads;
            for (int iterations = 0; iterations < 1; iterations++) {
                threads = new ArrayList();
                Thread thread;
                for (int i = 0; i < maxThreads; i++) {
                    final int iFinal = i;
                    final int iMaxFinal = maxThreads;
                    thread = new Thread(() -> {
                        double aSum = 0;
                        double bSum = 0;
                        int p = 2;
                        int iFinalThread = iFinal;
                        for (Coordinate each : coordAveragesTemp.keySet()) {
                            if (each.getY() >= (452 / iMaxFinal) * iFinalThread - 52 && each.getY() < (452 / iMaxFinal) * (iFinalThread + 1) - 52
                                    && each.getX() % offsetHeat == 0 && each.getY() % offsetHeat == 0) {
                                aSum = 0;
                                bSum = 0;
                                for (Coordinate each2 : coordAveragesTemp.keySet()) {
                                    if (!each.equals(each2) && getDistance(each, each2) < MAX_DISTANCE_BETWEEN_NODES_HEAT) {
                                        aSum = aSum + ((coordAveragesTemp.get(each2).get(1).intValue() * getDistance(each, each2)) / Math.pow(getDistance(each, each2), p));
                                        bSum = bSum + (1 / Math.pow(getDistance(each, each2), p));
                                    }
                                }
                            }
                        }
                    });
                    threads.add(thread);
                }
                threads.forEach(eachThread -> eachThread.start());
                System.out.println("Threads Running");
                boolean done = false;
                while (!done) {
                    try {
                        for (Thread eachThread : threads) {
                            eachThread.join();
                        }
                        done = true;
                    } catch (InterruptedException ex) {

                    }
                }
                System.out.println("Threads Finished");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void createAlwaysRunningResizer() {
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (true) {
                    Platform.runLater(() -> {
                        resize();
                    });
                    Thread.sleep(100);
                }
            }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
}
