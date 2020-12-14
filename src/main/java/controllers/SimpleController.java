/*
 * Copyright 2020 Sam Nishita.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package controllers;

import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;
import java.io.IOException;
import javafx.scene.paint.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.application.Platform;
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
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
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
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import logic.GridMethods;
import logic.HeatMethods;
import logic.MethodsInterface;
import logic.TraditionalMethods;
import logic.ZoneMethods;
import mainapp.Search;

/**
 *
 * @author samnishita
 */
public class SimpleController implements Initializable, MapControllerInterface {

    private LinkedHashMap<String, String[]> nameHash;
    private final BigDecimal ORIG_HEIGHT = new BigDecimal("470");
    private final BigDecimal SHOT_MADE_RADIUS = new BigDecimal("5");
    private final BigDecimal SHOT_MISS_START_END = new BigDecimal("3");
    private final BigDecimal SHOT_LINE_THICKNESS = new BigDecimal("2");
    private HashMap<Integer, String> activePlayers;
    private final int COMBO_FONT_SIZE = 18;
    private final int STAT_GRID_FONT_SIZE = 20;
    private String previousYear, previousPlayer, previousSeason;
    private ResourceBundle reader = null;
    private double maxCutoff = 0.0;
    private double diff = maxCutoff / 10;
    private LinkedList<Circle> allHeatCircles;
    private ArrayList<Node> allZoneFXML = new ArrayList();
    private LinkedList<Label> allLabels, allPercentLabels;
    private LinkedList<Node> allShapes;
    private DecimalFormat df = new DecimalFormat("##.#");
    private Rectangle mask;
    private LinkedHashMap<String, Integer> relevantTeamNameIDHashMap = new LinkedHashMap();
    private double font = 0.0;
    private double fontGrid = 0.0;
    private LinkedList<Button> viewButtons = new LinkedList();
    private Search currentSimpleSearch = Search.TRADITIONAL;
    private Search currentAdvancedSearch = Search.TRADITIONAL;
    private ArrayList<Label> simpleFGLabels = new ArrayList();
    private ArrayList<Label> advancedFGLabels = new ArrayList();
    private UserInputComboBox playerComboUser, playerComboUserAdvanced, seasonsBeginComboUser, seasonsEndComboUser,
            distanceBeginComboUser, distanceEndComboUser, shotSuccessComboUser, shotValueComboUser,
            shotTypeComboUser, teamComboUser, homeTeamComboUser, awayTeamComboUser,
            courtAreasComboUser, courtSidesComboUser, seasonTypesComboUser;
    private JSONObject previousSimpleSearchJSON, previousAdvancedSearchJSON;
    private JSONArray previousSimpleSearchResults, previousAdvancedSearchResults;
    private boolean alreadyInitializedAdv = false;
    private Font overallFont, boldFont, titleFont;
    private ArrayList<Label> allGridLegendLabels = new ArrayList();
    private ArrayList<Label> allSimpleFGLabels = new ArrayList();
    private ArrayList<Label> allAdvancedFGLabels = new ArrayList();
    private Shape tradBubble, tradBubbleNS, tradBubbleSWNE, tradBubbleWE, tradBubbleNWSE;
    private Label tradShotInfo = new Label();
    private ArrayList<Shape> allBubbles = new ArrayList();
    private TraditionalMethods tradMeth;
    private GridMethods gridMeth;
    private HeatMethods heatMeth;
    private ZoneMethods zoneMeth;
    //General Features
    @FXML
    private BorderPane borderpane;
    @FXML
    private VBox vbox, centralvbox, progressvbox;
    @FXML
    private ImageView imageview;
    @FXML
    private Label titlelabel, lastupdatedlabel, charttitle, dateaccuracy, updatelabel, updatestitlelabel, namelabel, progresslabel;
    @FXML
    private HBox tophbox;
    @FXML
    private Line line;
    @FXML
    private Button simplelayoutbutton, advancedlayoutbutton;
    @FXML
    private ProgressIndicator progressindicator;
    @FXML
    Polygon triangle;
    @FXML
    Rectangle bubble;
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
        try {
            reader = ResourceBundle.getBundle("dbconfig");
            overallFont = Font.loadFont(SimpleController.class.getResourceAsStream("/fonts/MontserratLight-ywBvq.ttf"), 12);
            titleFont = Font.loadFont(SimpleController.class.getResourceAsStream("/fonts/JosefinSansLight-ZVEll.ttf"), 12);
            boldFont = Font.loadFont(SimpleController.class.getResourceAsStream("/fonts/MontserratSemibold-8M8PB.otf"), 12);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        createCustomShapes();
        tradShotInfo.setFont(overallFont);
        Collections.addAll(viewButtons, traditionalbutton, gridbutton, heatmapbutton, zonebutton);
        Collections.addAll(simpleFGLabels, fgfrac, fgperc, twopointfrac, twopointperc, threepointfrac, threepointperc);
        Collections.addAll(advancedFGLabels, fgfracadv, fgpercadv, twopointfracadv, twopointpercadv, threepointfracadv, threepointpercadv);
        Collections.addAll(allGridLegendLabels, gridcolorlegendtoplabel, gridcolorlegendlowerlabel, gridcolorlegendupperlabel, gridsizelegendtoplabel, gridsizelegendlowerlabel, gridsizelegendupperlabel);
        Collections.addAll(allSimpleFGLabels, fg, fgfrac, fgperc, twopoint, twopointfrac, twopointperc, threepoint, threepointfrac, threepointperc);
        Collections.addAll(allAdvancedFGLabels, fgadv, fgfracadv, fgpercadv, twopointadv, twopointfracadv, twopointpercadv, threepointadv, threepointfracadv, threepointpercadv);
        Collections.addAll(allBubbles, tradBubble, tradBubbleNS, tradBubbleNWSE, tradBubbleSWNE, tradBubbleWE);
        createResponsiveComboBoxes();
        organizeZoneFXMLElements();
        initSizing();
        createServices();
        endLoadingTransition();
        progressvbox.setStyle("-fx-background: transparent;-fx-background-color: transparent;");
        //Set Updates Box
        try {
            JSONArray jsonArrayInit = getInitData();
            String toDisplay = "";
//            String announcement = jsonArrayInit.getJSONObject(0).getString("value");
            String announcement = "Test announcement";
            String lastshotsadded = jsonArrayInit.getJSONObject(1).getString("value");
            String latestVersion = jsonArrayInit.getJSONObject(2).getString("value");
//            
            //4 lines max
            if (!latestVersion.equals(reader.getString("version"))) {
                toDisplay += "Version " + latestVersion + " update is available!\n";
            }
//            
            if (!toDisplay.equals("")) {
                toDisplay +=  announcement+"\n";
            }
            dateaccuracy.setText(lastshotsadded);
            if (toDisplay.equals("")) {
                updatestitlelabel.setVisible(false);
                updatelabel.setVisible(false);
                updatestitlelabel.setManaged(false);
                updatelabel.setManaged(false);
            } else {
                updatelabel.setText(toDisplay);
                updatelabel.setAlignment(Pos.TOP_CENTER);
                updatelabel.setStyle("-fx-font: 16.0px \"" + overallFont.getName() + "\";");
                updatestitlelabel.setStyle("-fx-font: 20.0px \"" + boldFont.getName() + "\";");
            }
        } catch (IOException ex) {
            System.out.println("Error caught in Misc Initialization");
        }
        nameHash = new LinkedHashMap();
        try {
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
        yearcombo.setItems(FXCollections.observableArrayList(makeYears()));
        this.yearcombo.setValue("2019-20");
        this.playercombo.setValue("Aaron Gordon");
        this.seasoncombo.setValue("Regular Season");
        this.activePlayers = new HashMap();
        try {
            playerComboUser = new UserInputComboBox(playercombo, null, "");
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
        imageview.fitHeightProperty().addListener((ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) -> resize());
        imageview.fitWidthProperty().addListener((ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) -> resize());
        this.yearcombo.setOnAction(t -> {
            try {
                setPlayerComboBox();
            } catch (IOException ex) {
                System.out.println("Error setting player combobox");
            }
        });
        this.playercombo.valueProperty().addListener(t -> {
            try {
                setSeasonsComboBox();
            } catch (IOException ex) {
                System.out.println("Error setting seasons combobox");
            }
        });
        this.simplelayoutbutton.setOnMouseClicked(t -> {
            if (!searchvbox.isVisible()) {
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
            }
        });
        this.advancedlayoutbutton.setOnMouseClicked(t -> {
            if (!advancedvbox.isVisible()) {
                if (!alreadyInitializedAdv) {
                    try {
                        initAdvanced();
                        alreadyInitializedAdv = true;
                    } catch (IOException ex) {
                        System.out.println("Error caught initializing advanced");
                    }
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
            }
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
        if (!tradMeth.getAllShots().keySet().isEmpty()) {
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
            for (Shot each : tradMeth.getAllShots().keySet()) {
                if (each.getMake() == 1) {
                    circle = (Circle) tradMeth.getAllShots().get(each);
                    circle.setTranslateX(BigDecimal.valueOf(each.getX()).doubleValue() * height / 470 + minX + width / 2);
                    circle.setTranslateY(BigDecimal.valueOf(each.getY()).doubleValue() * height / 470 + minY + height / 2 - (185.0 * height / 470));
                    circle.setRadius(height * SHOT_MADE_RADIUS.divide(ORIG_HEIGHT, 6, RoundingMode.HALF_UP).doubleValue());
                    circle.setStrokeWidth(height * scaledLineThickness);
                } else {
                    msi = (MissedShotIcon) tradMeth.getAllShots().get(each);
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
                    msi.getRect().setTranslateX(BigDecimal.valueOf(each.getX()).doubleValue() * height / 470 + minX);
                    msi.getRect().setTranslateY(BigDecimal.valueOf(each.getY()).doubleValue() * height / 470 + minY - (180.0 * height / 470));
                    line2.setRotate(180);
                }
            }
        }
    }

    private void setPlayerComboBox() throws IOException {
        this.activePlayers = new HashMap();
        JSONArray jsonArray = getActivePlayersData();
        JSONObject eachPlayer;
        for (int i = 0; i < jsonArray.length(); i++) {
            eachPlayer = jsonArray.getJSONObject(i);
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
            realNames.add(names.get(each).trim());
        }
        if (seasoncombo.getValue() != null) {
            this.previousSeason = seasoncombo.getValue().toString();
        }
        playerComboUser.getComboBox().setItems(FXCollections.observableArrayList(realNames));
        if (previousSeason != null && playerComboUser.getComboBox().getValue() != null && seasoncombo.getItems().contains(previousSeason)) {
            this.seasoncombo.getSelectionModel().select(previousSeason);
        } else {
            this.seasoncombo.getSelectionModel().clearSelection();
        }
//        if (realNames.contains(previousPlayer)) {
//            playerComboUser.getComboBox().getSelectionModel().select(previousPlayer);
//        }
    }

    private void setAdvancedPlayerComboBox() throws IOException {
        playerComboUserAdvanced = new UserInputComboBox(playercomboadvanced, new HashSet(), "");
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
            JSONObject eachSeason;
            for (int i = 0; i < jsonArraySeasons.length(); i++) {
                eachSeason = jsonArraySeasons.getJSONObject(i);
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
        seasonTypesComboUser = new UserInputComboBox(seasontypescomboadvanced, new HashSet(), "");
        seasonTypesComboUser.getComboBox().setItems(FXCollections.observableArrayList(seasons));
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
        previousSimpleSearchJSON = jsonObjOut;
        Main.getPrintWriterOut().println(jsonObjOut.toString());
        return new JSONArray(Main.getServerResponse().readLine());
    }

    @Override
    public double getDistance(Coordinate coordOrig, Coordinate coordI) {
        return Math.sqrt(Math.pow(coordOrig.getX() - coordI.getX(), 2) + Math.pow(coordOrig.getY() - coordI.getY(), 2));
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
        for (Label each : allGridLegendLabels) {
            if (each.getId().equals("gridcolorlegendtoplabel") || each.getId().equals("gridsizelegendtoplabel")) {
                each.setStyle("-fx-font: " + height * 13.0 / 470 + "px \"" + overallFont.getName() + "\";");
            } else {
                each.setStyle("-fx-font: " + height * 11.0 / 470 + "px \"" + overallFont.getName() + "\";");
            }
        }
        gridcolorlegendgradient.setWidth(height * 153.0 / 470);
        gridcolorlegendgradient.setHeight(height * 17.0 / 470);
        double squareSize = width / 50;
        int counter = 0;
        Rectangle square;
        for (Coordinate each2 : gridMeth.getCoordValue().keySet()) {
            square = gridMeth.getAllTiles().get(counter);
            if (gridMeth.getAllShots().get(each2).get(1) < gridMeth.getMaxShotsPerMaxSquare() && gridMeth.getAllShots().get(each2).get(1) > gridMeth.getMin()) {
                square.setHeight((gridMeth.getAllShots().get(each2).get(1) / gridMeth.getMaxShotsPerMaxSquare() * squareSize) * 0.9);
                square.setWidth((gridMeth.getAllShots().get(each2).get(1) / gridMeth.getMaxShotsPerMaxSquare() * squareSize) * 0.9);
            } else if (gridMeth.getAllShots().get(each2).get(1) >= gridMeth.getMaxShotsPerMaxSquare()) {
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
        try {
            if (searchvbox.isVisible()) {
                if (!loadingoverlay.isVisible() || (loadingoverlay.isVisible() && progresslabel.getText().equals("Gathering Shots"))) {
                    switch (currentSimpleSearch) {
                        case TRADITIONAL:
                            resizeShots();
                            setViewTypeButtonStyle(0);
                            break;
                        case GRID:
                            resizeGrid();
                            setViewTypeButtonStyle(1);
                            break;
                        case HEAT:
                            resizeHeat();
                            setViewTypeButtonStyle(2);
                            break;
                        case ZONE:
                            resizeZone();
                            setViewTypeButtonStyle(3);
                            break;
                        default:
                            traditionalbutton.setStyle("-fx-font: " + font + "px \"" + boldFont.getName() + "\";-fx-background-color: transparent; ");
                    }
                    if (heatlegend.isVisible()) {
                        resizeHeat();
                    } else if (zonelegend.isVisible()) {
                        resizeZone();
                    } else if (gridlegendcolor.isVisible()) {
                        resizeGrid();
                    } else {
                        for (Node each : imagegrid.getChildren()) {
                            if (each.getClass().equals(Line.class)) {
                                resizeShots();
                                break;
                            }
                        }
                    }
                } else {
                    switch (currentSimpleSearch) {
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
                            traditionalbutton.setStyle("-fx-font: " + font + "px \"" + boldFont.getName() + "\";-fx-background-color: transparent; ");
                    }
                }
                introlabel.setStyle("-fx-font: " + font * 1.5 + "px \"" + overallFont.getName() + "\";");
                yearcombo.setStyle("-fx-font: " + font + "px \"" + overallFont.getName() + "\";");
                playercombo.setStyle("-fx-font: " + font + "px \"" + overallFont.getName() + "\";");
                seasoncombo.setStyle("-fx-font: " + font + "px \"" + overallFont.getName() + "\";");
                searchbutton.setStyle("-fx-font: " + font * 0.8 + "px \"" + overallFont.getName() + "\";");
                errorlabel.setStyle("-fx-font: " + font * 0.75 + "px \"" + overallFont.getName() + "\"; ");
                for (Label each : allSimpleFGLabels) {
                    if (each.getId().equals("fg") || each.getId().equals("twopoint") || each.getId().equals("threepoint")) {
                        each.setStyle("-fx-font: " + font * 2.5 + "px \"" + boldFont.getName() + "\";");
                    } else {
                        each.setStyle("-fx-font: " + fontGrid + "px \"" + boldFont.getName() + "\";");
                    }
                }
            } else {
                if (!loadingoverlay.isVisible() || (loadingoverlay.isVisible() && progresslabel.getText().equals("Gathering Shots"))) {
                    switch (currentAdvancedSearch) {
                        case TRADITIONAL:
                            resizeShots();
                            setViewTypeButtonStyle(0);
                            break;
                        case GRID:
                            resizeGrid();
                            setViewTypeButtonStyle(1);
                            break;
                        case HEAT:
                            resizeHeat();
                            setViewTypeButtonStyle(2);
                            break;
                        case ZONE:
                            resizeZone();
                            setViewTypeButtonStyle(3);
                            break;
                        default:
                            traditionalbutton.setStyle("-fx-font: " + font + "px \"" + boldFont.getName() + "\";-fx-background-color: transparent; ");
                    }
                    if (heatlegend.isVisible()) {
                        resizeHeat();
                    } else if (zonelegend.isVisible()) {
                        resizeZone();
                    } else if (gridlegendcolor.isVisible()) {
                        resizeGrid();
                    } else {
                        for (Node each : imagegrid.getChildren()) {
                            if (each.getClass().equals(Line.class)) {
                                resizeShots();
                                break;
                            }
                        }
                    }
                } else {
                    switch (currentAdvancedSearch) {
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
                            traditionalbutton.setStyle("-fx-font: " + font + "px \"" + boldFont.getName() + "\";-fx-background-color: transparent; ");
                    }
                }
                for (Label each : allAdvancedFGLabels) {
                    if (each.getId().equals("fgadv") || each.getId().equals("twopointadv") || each.getId().equals("threepointadv")) {
                        each.setStyle("-fx-font: " + font * 2 + "px \"" + boldFont.getName() + "\";");
                    } else {
                        each.setStyle("-fx-font: " + fontGrid * 0.75 + "px \"" + boldFont.getName() + "\";");
                    }
                }
                HBox tempHBox;
                for (Node each : advancedvboxinner.getChildren()) {
                    if (!each.getClass().equals(HBox.class) && !each.getId().contains("dash")) {
                        each.setStyle("-fx-font: " + font + "px \"" + overallFont.getName() + "\";");
                    } else if (each.getClass().equals(HBox.class)) {
                        tempHBox = (HBox) each;
                        for (Node each2 : tempHBox.getChildren()) {
                            each2.setStyle("-fx-font: " + font + "px \"" + overallFont.getName() + "\";");
                        }
                    } else {
                        each.setStyle("-fx-font: " + font * 1.5 + "px \"" + overallFont.getName() + "\";");
                    }
                }
                advancedintrolabel.setStyle("-fx-font: " + fontGrid + "px \"" + boldFont.getName() + "\";");
                notestextarea.setStyle("-fx-font: " + font * 0.65 + "px \"" + overallFont.getName() + "\"; ");
                searchscrollpane.setMinHeight(advancedvbox.getLayoutBounds().getHeight() * 0.4);
                searchscrollpane.setMaxHeight(advancedvbox.getLayoutBounds().getHeight() * 0.4);
                searchbuttonadvanced.setStyle("-fx-font: " + font * 0.8 + "px \"" + overallFont.getName() + "\";");
                errorlabeladvanced.setStyle("-fx-font: " + font * 0.75 + "px \"" + overallFont.getName() + "\"; ");
                seasondash.setStyle("-fx-font: " + font + "px \"" + overallFont.getName() + "\"; ");
                distancedash.setStyle("-fx-font: " + font + "px \"" + overallFont.getName() + "\"; ");
                HBox hbox;
                Label label;
                Button button;
                for (Node each : selectionvbox.getChildren()) {
                    try {
                        hbox = (HBox) each;
                        for (Node eachInner : hbox.getChildren()) {
                            if (eachInner.getClass().equals(Label.class)) {
                                label = (Label) eachInner;
                                label.setStyle("-fx-font: " + font * 0.9 + "px \"" + overallFont.getName() + "\";");
                            } else if (eachInner.getClass().equals(Button.class)) {
                                button = (Button) eachInner;
                                if (button.isHover()) {
                                    button.setStyle("-fx-font: " + font * 0.8 + "px \"" + overallFont.getName() + "\"; -fx-text-fill: red;-fx-background-color: transparent;-fx-underline: true;");
                                } else {
                                    button.setStyle("-fx-font: " + font * 0.8 + "px \"" + overallFont.getName() + "\"; -fx-text-fill: red;-fx-background-color: transparent;");
                                }
                            }
                        }
                    } catch (Exception ex) {

                    }
                }
            }
            if (updatelabel.isVisible()){
                updatelabel.setStyle("-fx-font: "+16.0*height/470+"px \"" + overallFont.getName() + "\";");
                updatestitlelabel.setStyle("-fx-font: "+20.0*height/470+"px \"" + boldFont.getName() + "\";");
                updatelabel.setPrefWidth(300.0*height/470);
                updatelabel.setPrefHeight(88.0*height/470);
                updatestitlelabel.setPrefWidth(200.0*height/470);
            }
            mask.setWidth(width * 0.999);
            mask.setHeight(height * 0.999);
            imagegrid.setClip(mask);
            this.simplelayoutbutton.setStyle("-fx-font: " + font + "px \"" + overallFont.getName() + "\";");
            this.advancedlayoutbutton.setStyle("-fx-font: " + font + "px \"" + overallFont.getName() + "\";");
            titlelabel.setMinWidth(Region.USE_PREF_SIZE);
            titlelabel.setStyle("-fx-font: " + fontGrid * 3 + "px \"" + titleFont.getName() + "\"; ");
            namelabel.setStyle("-fx-font: " + fontGrid * 0.75 + "px \"" + overallFont.getName() + "\"; ");
            lastupdatedlabel.setStyle("-fx-font: " + font * 0.8 + "px \"" + overallFont.getName() + "\"; ");
            dateaccuracy.setStyle("-fx-font: " + font * 1.4 + "px \"" + overallFont.getName() + "\"; ");
            charttitle.setStyle("-fx-font: " + fontGrid * 1.15 + "px \"" + overallFont.getName() + "\";");
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
            progresslabel.setStyle("-fx-font: " + height * 16.0 / 470 + "px \"" + boldFont.getName() + "\";");
            progressindicator.setPrefHeight(height * 70.0 / 470);
            progressindicator.setPrefWidth(height * 70.0 / 470);
            allBubbles.forEach(each -> {
                each.setScaleX(height / 470.0);
                each.setScaleY(height / 470.0);
            });
            tradShotInfo.setMaxSize(bubble.getLayoutBounds().getWidth() * tradBubble.getScaleX() * 0.9, bubble.getLayoutBounds().getHeight() * tradBubble.getScaleY() * 0.9);
            Label eachLabel;
            Label eachLabelPercent;
            double topFontSize = 17.0;
            double bottomFontSize = 15.0;
            for (int i = 1; i < 16; i++) {
                eachLabel = allLabels.get(i - 1);
                eachLabelPercent = allPercentLabels.get(i - 1);
                eachLabel.setStyle("-fx-font: " + height * topFontSize / 470 + "px \"" + boldFont.getName() + "\"; -fx-font-weight: bold;");
                eachLabelPercent.setStyle("-fx-font: " + height * bottomFontSize / 470 + "px \"" + boldFont.getName() + "\";-fx-font-weight: bold;");
                if (i < 3) {
                    eachLabel.setMinWidth(width * 120.0 / 470);
                } else {
                    eachLabel.setMinWidth(width * 90.0 / 470);
                }
                eachLabelPercent.setMinWidth(width * 90.0 / 470);
            }
        } catch (Exception ex) {

        }
    }

    private void resizeHeat() {
        double height = (imageview.localToParent(imageview.getBoundsInLocal()).getHeight());
        font = new BigDecimal(COMBO_FONT_SIZE).multiply(new BigDecimal(height)).divide(new BigDecimal("550"), 6, RoundingMode.HALF_UP).doubleValue();
        Circle tempCircle;
        int keyCounter = 0;
        for (Coordinate each : heatMeth.getCoordValue().keySet()) {
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
        heatlegendtoplabel.setStyle("-fx-font: " + height * 13.0 / 470 + "px \"" + overallFont.getName() + "\";");
        heatlegendlowerlabel.setStyle("-fx-font: " + height * 11.0 / 470 + "px \"" + overallFont.getName() + "\";");
        heatlegendupperlabel.setStyle("-fx-font: " + height * 11.0 / 470 + "px \"" + overallFont.getName() + "\";");
    }

    private void resizeZone() {
        double height = (imageview.localToParent(imageview.getBoundsInLocal()).getHeight());
        double width = imageview.localToParent(imageview.getBoundsInLocal()).getWidth();
        font = new BigDecimal(COMBO_FONT_SIZE).multiply(new BigDecimal(height)).divide(new BigDecimal("550"), 6, RoundingMode.HALF_UP).doubleValue();
        mask = new Rectangle(width * 0.999, height * 0.999);
        imagegrid.setClip(mask);
        Node each;
        Label eachLabel;
        Label eachLabelPercent;
        double topFontSize = 17.0;
        double bottomFontSize = 15.0;
        for (int i = 1; i < 16; i++) {
            each = allShapes.get(i - 1);
            eachLabel = allLabels.get(i - 1);
            eachLabelPercent = allPercentLabels.get(i - 1);
            each.setScaleX(width / 500);
            each.setScaleY(height / 470);
            eachLabel.setStyle("-fx-font: " + height * topFontSize / 470 + "px \"" + boldFont.getName() + "\"; -fx-font-weight: bold;");
            eachLabelPercent.setStyle("-fx-font: " + height * bottomFontSize / 470 + "px \"" + boldFont.getName() + "\";-fx-font-weight: bold;");
            if (i < 3) {
                eachLabel.setMinWidth(width * 120.0 / 470);
            } else {
                eachLabel.setMinWidth(width * 90.0 / 470);
            }
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
        zonelegendtoplabel.setStyle("-fx-font: " + height * 13.0 / 470 + "px \"" + overallFont.getName() + "\";");
        zonelegendlowerlabel.setStyle("-fx-font: " + height * 11.0 / 470 + "px \"" + overallFont.getName() + "\";");
        zonelegendupperlabel.setStyle("-fx-font: " + height * 11.0 / 470 + "px \"" + overallFont.getName() + "\";");
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
        tradMeth.getService().reset();
        tradMeth.getService().start();
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
        gridMeth.getService().reset();
        gridMeth.getService().start();
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
        heatMeth.getService().reset();
        heatMeth.getService().start();
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
        zoneMeth.getService().reset();
        zoneMeth.getService().start();
        startLoadingTransition();
    }

    private JSONArray getZoneAveragesData() throws IOException {
        JSONObject jsonObjOut = new JSONObject();
        jsonObjOut.put("selector", "zoneaverages");
        Main.getPrintWriterOut().println(jsonObjOut.toString());
        return new JSONArray(Main.getServerResponse().readLine());
    }

    private void changeShapeColor(Shape shape, Double playerValue, int i) {
        if (zoneMeth.getAllZones().get(i)[1] == 0) {
            shape.setFill(Color.web("#b2b2b2"));
        } else {
            Double diff = playerValue - zoneMeth.getAllZoneAverages().get(i);
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
        if (zoneMeth.getAllZones().get(i)[1] == 0) {
            rect.setFill(Color.web("#b2b2b2"));
        } else {
            diff = playerValue - zoneMeth.getAllZoneAverages().get(i);
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
        if (zoneMeth.getAllZones().get(i)[1] == 0) {
            arc.setFill(Color.web("#b2b2b2"));
        } else {
            Double diff = playerValue - zoneMeth.getAllZoneAverages().get(i);
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
        titlelabel.setStyle("-fx-font: " + fontGrid * 3 + "px \"" + titleFont.getName() + "\"; ");
        namelabel.setStyle("-fx-font: " + fontGrid * 0.75 + "px \"" + overallFont.getName() + "\"; ");
        lastupdatedlabel.setStyle("-fx-font: " + font * 0.8 + "px \"" + overallFont.getName() + "\"; ");
        dateaccuracy.setStyle("-fx-font: " + font * 1.4 + "px \"" + overallFont.getName() + "\"; ");
        errorlabel.setStyle("-fx-font: " + font * 0.75 + "px \"" + overallFont.getName() + "\"; ");
        this.errorlabel.setVisible(false);
        this.introlabel.prefWidthProperty().bind(this.gridpane.widthProperty().divide(4));
        this.introlabel.setStyle("-fx-font: " + font * 1.5 + "px \"" + overallFont.getName() + "\";");
        this.yearcombo.prefWidthProperty().bind(this.gridpane.widthProperty().divide(5));
        this.yearcombo.setStyle("-fx-font: " + font + "px \"" + overallFont.getName() + "\";");
        this.playercombo.prefWidthProperty().bind(this.gridpane.widthProperty().divide(5));
        this.playercombo.setStyle("-fx-font: " + font + "px \"" + overallFont.getName() + "\";");
        this.seasoncombo.prefWidthProperty().bind(this.gridpane.widthProperty().divide(5));
        this.seasoncombo.setStyle("-fx-font: " + font + "px \"" + overallFont.getName() + "\";");
        this.searchbutton.setStyle("-fx-font: " + font * 0.8 + "px \"" + overallFont.getName() + "\";");
        this.simplelayoutbutton.setStyle("-fx-font: " + font + "px \"" + overallFont.getName() + "\";");
        this.advancedlayoutbutton.setStyle("-fx-font: " + font + "px \"" + overallFont.getName() + "\";");
        this.simplelayoutbutton.prefWidthProperty().bind(this.gridpane.widthProperty().divide(8));
        this.advancedlayoutbutton.prefWidthProperty().bind(this.gridpane.widthProperty().divide(8));

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
        mask = new Rectangle(imageview.getLayoutBounds().getWidth() * 0.999, imageview.getLayoutBounds().getHeight() * 0.999);
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
        selectionvbox.setStyle("-fx-background: transparent;-fx-background-color: transparent;");
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

    private void initAdvanced() throws IOException {
        seasonsBeginComboUser = new UserInputComboBox(seasonsbegincombo, null, "");
        seasonsBeginComboUser.getComboBox().setItems(FXCollections.observableArrayList(makeYears()));
        seasonsEndComboUser = new UserInputComboBox(seasonsendcombo, null, "");
        seasonsEndComboUser.getComboBox().setItems(FXCollections.observableArrayList(makeYears()));
        setAdvancedPlayerComboBox();
        setAdvancedSeasonsComboBox();
        setShotDistanceCombo();
        notestextarea.setStyle("-fx-font: " + font * 0.65 + "px \"" + overallFont.getName() + "\"; ");
        searchbuttonadvanced.setStyle("-fx-font: " + font * 0.8 + "px \"" + overallFont.getName() + "\";");
        errorlabeladvanced.setStyle("-fx-font: " + font * 0.75 + "px \"" + overallFont.getName() + "\"; ");
        seasondash.setStyle("-fx-font: " + font + "px \"" + overallFont.getName() + "\"; ");
        distancedash.setStyle("-fx-font: " + font + "px \"" + overallFont.getName() + "\"; ");
    }

    private void setShotDistanceCombo() {
        ArrayList<Integer> distances = new ArrayList();
        for (int i = 0; i < 90; i++) {
            distances.add(i);
        }
        distanceBeginComboUser = new UserInputComboBox(distancebegincombo, null, "");
        distanceBeginComboUser.getComboBox().setItems(FXCollections.observableArrayList(distances));
        distanceEndComboUser = new UserInputComboBox(distanceendcombo, null, "");
        distanceEndComboUser.getComboBox().setItems(FXCollections.observableArrayList(distances));
    }

    public void addHBoxToSelectionBox(String selector, String input) {
        switch (selector) {
            case "seasonsbegincombo":
                singleSelectionHBoxCreationMethods(input, "Seasons after and including ", seasonsbegincombo);
                break;
            case "seasonsendcombo":
                singleSelectionHBoxCreationMethods(input, "Seasons before and including ", seasonsendcombo);
                break;
            case "playercomboadvanced":
                multipleSelectionHBoxCreationMethods(playerComboUserAdvanced.getHashSet(), "Player: ", playercomboadvanced, input);
                break;
            case "seasontypescomboadvanced":
                multipleSelectionHBoxCreationMethods(seasonTypesComboUser.getHashSet(), "Season Type: ", seasontypescomboadvanced, input);
                break;
            case "distancebegincombo":
                singleSelectionHBoxCreationMethods(input, "Minimum Distance: ", distancebegincombo);
                break;
            case "distanceendcombo":
                singleSelectionHBoxCreationMethods(input, "Maximum Distance: ", distanceendcombo);
                break;
            case "shotsuccesscombo":
                singleSelectionHBoxCreationMethods(input, "Shot Success: ", shotsuccesscombo);
                break;
            case "shotvaluecombo":
                singleSelectionHBoxCreationMethods(input, "Shot Value: ", shotvaluecombo);
                break;
            case "shottypescombo":
                multipleSelectionHBoxCreationMethods(shotTypeComboUser.getHashSet(), "Shot Type: ", shottypescombo, input);
                break;
            case "teamscombo":
                multipleSelectionHBoxCreationMethods(teamComboUser.getHashSet(), "Team: ", teamscombo, input);
                break;
            case "hometeamscombo":
                multipleSelectionHBoxCreationMethods(homeTeamComboUser.getHashSet(), "Home Team: ", hometeamscombo, input);
                break;
            case "awayteamscombo":
                multipleSelectionHBoxCreationMethods(awayTeamComboUser.getHashSet(), "Away Team: ", awayteamscombo, input);
                break;
            case "courtareascombo":
                multipleSelectionHBoxCreationMethods(courtAreasComboUser.getHashSet(), "Court Area: ", courtareascombo, input);
                break;
            case "courtsidescombo":
                multipleSelectionHBoxCreationMethods(courtSidesComboUser.getHashSet(), "Court Side: ", courtsidescombo, input);
                break;
        }
        selectionscrollpane.setVvalue(1.0);
    }

    private void singleSelectionHBoxCreationMethods(String alreadySelected, String labelPreText, ComboBox combo) {
        font = new BigDecimal(COMBO_FONT_SIZE).multiply(new BigDecimal(imageview.getLayoutBounds().getHeight())).divide(new BigDecimal("550"), 6, RoundingMode.HALF_UP).doubleValue();
        Label tempLabel;
        Label labelToReplace;
        Label label;
        Button deleteButton;
        HBox tempHBox;
        HBox newHBox;
        Insets insets = new Insets(5, 5, 5, 5);
        Insets insetsHBox = new Insets(0, 0, 0, 20);
        if (!alreadySelected.equals("")) {
            for (Node each : selectionvbox.getChildren()) {
                if (each.getClass().equals(HBox.class)) {
                    tempHBox = (HBox) each;
                    for (Node innerEach : tempHBox.getChildren()) {
                        if (!innerEach.getClass().equals(Button.class)) {
                            tempLabel = (Label) innerEach;
                            if (tempLabel.getText().startsWith(labelPreText)) {
                                labelToReplace = tempLabel;
                                labelToReplace.setText(labelPreText + alreadySelected);
                                break;
                            }
                        }
                    }
                }
            }
        } else {
            newHBox = new HBox();
            newHBox.setAlignment(Pos.CENTER_LEFT);
            newHBox.setMinHeight(25.0);
            newHBox.setMaxHeight(25.0);
            newHBox.setMinWidth(100.0);
            newHBox.setPadding(insetsHBox);
            final String SELECTED = combo.getId();
            deleteButton = new Button("X");
            deleteButton.setStyle("-fx-font: " + font * 0.8 + "px \"" + overallFont.getName() + "\"; -fx-text-fill: red;-fx-background-color: transparent;");
            newHBox.getChildren().add(deleteButton);
            final HBox HBOX = (HBox) deleteButton.getParent();
            final Scene FINALSCENE = selectionvbox.getScene();
            deleteButton.setPrefSize(35.0, 35.0);
            deleteButton.setAlignment(Pos.CENTER);
            deleteButton.setPadding(insets);
            deleteButton.setOnMouseClicked((Event t) -> {
                deleteButton.setOnMouseEntered(t2 -> {
                    FINALSCENE.setCursor(Cursor.DEFAULT);
                    deleteButton.setStyle("-fx-font: " + font * 0.8 + "px \"" + overallFont.getName() + "\"; -fx-text-fill: red;-fx-background-color: transparent;-fx-underline: true;");
                });
                deleteButtonInner(SELECTED, HBOX, labelPreText);
                selectionvbox.getChildren().remove(newHBox);
            });
            deleteButton.setOnMouseEntered(t -> {
                FINALSCENE.setCursor(Cursor.HAND);
                deleteButton.setStyle("-fx-font: " + font * 0.8 + "px \"" + overallFont.getName() + "\"; -fx-text-fill: red;-fx-background-color: transparent;-fx-underline: true;");
            });
            deleteButton.setOnMouseExited(t -> {
                deleteButton.setStyle("-fx-font: " + font * 0.8 + "px \"" + overallFont.getName() + "\"; -fx-text-fill: red;-fx-background-color: transparent;");
                FINALSCENE.setCursor(Cursor.DEFAULT);
            });
            deleteButton.prefHeightProperty().bind(newHBox.prefHeightProperty());
            deleteButton.prefWidthProperty().bind(deleteButton.prefHeightProperty());
            alreadySelected = combo.getValue().toString();
            label = new Label();
            label.setText(labelPreText + alreadySelected);
            label.setStyle("-fx-font: " + font * 0.9 + "px \"" + overallFont.getName() + "\";");
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
                seasonsBeginComboUser.setSelection("");
                seasonsbegincombo.getSelectionModel().clearSelection();
                break;
            case "seasonsendcombo":
                seasonsEndComboUser.setSelection("");
                seasonsendcombo.getSelectionModel().clearSelection();
                break;
            case "playercomboadvanced":
                for (Node each : hbox.getChildren()) {
                    if (each.getClass().equals(Label.class)) {
                        Label label = (Label) each;
                        playerComboUserAdvanced.getHashSet().remove(label.getText().replace(preText, ""));
                        playercomboadvanced.getSelectionModel().clearSelection();
                    }
                }
                break;
            case "seasontypescomboadvanced":
                for (Node each : hbox.getChildren()) {
                    if (each.getClass().equals(Label.class)) {
                        Label label = (Label) each;
                        seasonTypesComboUser.getHashSet().remove(label.getText().replace(preText, ""));
                        seasontypescomboadvanced.getSelectionModel().clearSelection();
                    }
                }
                break;
            case "distancebegincombo":
                distanceBeginComboUser.setSelection("");
                distancebegincombo.getSelectionModel().clearSelection();
                break;
            case "distanceendcombo":
                distanceEndComboUser.setSelection("");
                distanceendcombo.getSelectionModel().clearSelection();
                break;
            case "shotsuccesscombo":
                shotSuccessComboUser.setSelection("");
                shotsuccesscombo.getSelectionModel().clearSelection();
                break;
            case "shotvaluecombo":
                shotValueComboUser.setSelection("");
                shotvaluecombo.getSelectionModel().clearSelection();
                break;
            case "shottypescombo":
                for (Node each : hbox.getChildren()) {
                    if (each.getClass().equals(Label.class)) {
                        Label label = (Label) each;
                        shotTypeComboUser.getHashSet().remove(label.getText().replace(preText, ""));
                        shottypescombo.getSelectionModel().clearSelection();
                    }
                }
                break;
            case "teamscombo":
                for (Node each : hbox.getChildren()) {
                    if (each.getClass().equals(Label.class)) {
                        Label label = (Label) each;
                        teamComboUser.getHashSet().remove(label.getText().replace(preText, ""));
                        teamscombo.getSelectionModel().clearSelection();
                    }
                }
                break;
            case "hometeamscombo":
                for (Node each : hbox.getChildren()) {
                    if (each.getClass().equals(Label.class)) {
                        Label label = (Label) each;
                        homeTeamComboUser.getHashSet().remove(label.getText().replace(preText, ""));
                        hometeamscombo.getSelectionModel().clearSelection();
                    }
                }
                break;
            case "awayteamscombo":
                for (Node each : hbox.getChildren()) {
                    if (each.getClass().equals(Label.class)) {
                        Label label = (Label) each;
                        awayTeamComboUser.getHashSet().remove(label.getText().replace(preText, ""));
                        awayteamscombo.getSelectionModel().clearSelection();
                    }
                }
                break;
            case "courtareascombo":
                for (Node each : hbox.getChildren()) {
                    if (each.getClass().equals(Label.class)) {
                        Label label = (Label) each;
                        courtAreasComboUser.getHashSet().remove(label.getText().replace(preText, ""));
                        courtareascombo.getSelectionModel().clearSelection();
                    }
                }
                break;
            case "courtsidescombo":
                for (Node each : hbox.getChildren()) {
                    if (each.getClass().equals(Label.class)) {
                        Label label = (Label) each;
                        courtSidesComboUser.getHashSet().remove(label.getText().replace(preText, ""));
                        courtsidescombo.getSelectionModel().clearSelection();
                    }
                }
                break;
        }
    }

    private void multipleSelectionHBoxCreationMethods(HashSet hashSet, String labelPreText, ComboBox combo, String input) {
        font = new BigDecimal(COMBO_FONT_SIZE).multiply(new BigDecimal(imageview.getLayoutBounds().getHeight())).divide(new BigDecimal("550"), 6, RoundingMode.HALF_UP).doubleValue();
        Insets insets = new Insets(5, 5, 5, 5);
        Insets insetsHBox = new Insets(0, 0, 0, 20);
        HBox newHBox = new HBox();
        newHBox.setAlignment(Pos.CENTER_LEFT);
        newHBox.setMinHeight(25.0);
        newHBox.setMaxHeight(25.0);
        newHBox.setMinWidth(100.0);
        newHBox.setPadding(insetsHBox);
        final String SELECTED = combo.getId();
        Button deleteButton = new Button("X");
        deleteButton.setPrefSize(35.0, 35.0);
        deleteButton.setAlignment(Pos.CENTER);
        deleteButton.setStyle("-fx-font: " + font * 0.8 + "px \"" + overallFont.getName() + "\"; -fx-text-fill: red;-fx-background-color: transparent;");
        newHBox.getChildren().add(deleteButton);
        final HBox HBOX = (HBox) deleteButton.getParent();
        final Scene FINALSCENE = selectionvbox.getScene();
        deleteButton.setPadding(insets);
        deleteButton.setOnMouseClicked(t -> {
            deleteButton.setOnMouseEntered(t2 -> {
                FINALSCENE.setCursor(Cursor.DEFAULT);
                deleteButton.setStyle("-fx-font: " + font * 0.8 + "px \"" + overallFont.getName() + "\"; -fx-text-fill: red;-fx-background-color: transparent;-fx-underline: true;");
            });
            deleteButtonInner(SELECTED, HBOX, labelPreText);
            selectionvbox.getChildren().remove(newHBox);
        });
        deleteButton.setOnMouseEntered(t -> {
            FINALSCENE.setCursor(Cursor.HAND);
            deleteButton.setStyle("-fx-font: " + font * 0.8 + "px \"" + overallFont.getName() + "\"; -fx-text-fill: red;-fx-background-color: transparent;-fx-underline: true;");
        });
        deleteButton.setOnMouseExited(t -> {
            deleteButton.setStyle("-fx-font: " + font * 0.8 + "px \"" + overallFont.getName() + "\"; -fx-text-fill: red;-fx-background-color: transparent;");
            FINALSCENE.setCursor(Cursor.DEFAULT);
        });
        deleteButton.prefHeightProperty().bind(newHBox.prefHeightProperty());
        deleteButton.prefWidthProperty().bind(deleteButton.prefHeightProperty());
        Label label = new Label();
        label.setText(labelPreText + input);
        label.setStyle("-fx-font: " + font * 0.9 + "px \"" + overallFont.getName() + "\";");
        label.setPadding(insets);
        label.prefHeightProperty().bind(newHBox.prefHeightProperty());
        label.setAlignment(Pos.CENTER_LEFT);
        newHBox.getChildren().add(label);
        selectionvbox.getChildren().add(newHBox);
    }

    public void populateUnchangingComboBoxes() throws IOException {
        ArrayList<String> tempList = new ArrayList();
        Collections.addAll(tempList, "Makes", "Misses");
        shotSuccessComboUser = new UserInputComboBox(shotsuccesscombo, null, "");
        shotSuccessComboUser.getComboBox().setItems(FXCollections.observableArrayList(tempList));
        tempList = new ArrayList();
        Collections.addAll(tempList, "2PT", "3PT");
        shotValueComboUser = new UserInputComboBox(shotvaluecombo, null, "");
        shotValueComboUser.getComboBox().setItems(FXCollections.observableArrayList(tempList));
        tempList = new ArrayList();
        for (String each : (ArrayList<String>) getShotTypesList()) {
            tempList.add(each.replace("shot", "Shot"));
        }
        shotTypeComboUser = new UserInputComboBox(shottypescombo, new HashSet<String>(), "");
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
        teamComboUser = new UserInputComboBox(teamscombo, new HashSet<String>(), "");
        homeTeamComboUser = new UserInputComboBox(hometeamscombo, new HashSet<String>(), "");
        awayTeamComboUser = new UserInputComboBox(awayteamscombo, new HashSet<String>(), "");
        teamComboUser.getComboBox().setItems(FXCollections.observableArrayList(relevantTeamNameIDHashMap.keySet()));
        homeTeamComboUser.getComboBox().setItems(FXCollections.observableArrayList(relevantTeamNameIDHashMap.keySet()));
        awayTeamComboUser.getComboBox().setItems(FXCollections.observableArrayList(relevantTeamNameIDHashMap.keySet()));
        tempList = new ArrayList();
        Collections.addAll(tempList, "Restricted Area", "In The Paint (Non-RA)", "Mid-Range",
                "Left Corner 3", "Right Corner 3", "Above the Break 3", "Backcourt");
        courtAreasComboUser = new UserInputComboBox(courtareascombo, new HashSet<String>(), "");
        courtAreasComboUser.getComboBox().setItems(FXCollections.observableArrayList(tempList));
        tempList = new ArrayList();
        Collections.addAll(tempList, "Left", "Left-Center", "Center",
                "Right-Center", "Right", "Back Court");
        courtSidesComboUser = new UserInputComboBox(courtsidescombo, new HashSet<String>(), "");
        courtSidesComboUser.getComboBox().setItems(FXCollections.observableArrayList(tempList));
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
        JSONObject obj = createJsonObjectOutput();
        previousAdvancedSearchJSON = obj;
        JSONObject newObj = createJsonObjectOutput();
        newObj.put("selector", "advanced" + searchTypeSelector.toString().toLowerCase());
        Main.getPrintWriterOut().println(newObj.toString());
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
        int max = 0;
        if (currentAdvancedSearch == Search.TRADITIONAL) {
            max = 7500;
        } else {
            max = 50000;
        }
        if (jsonArray.length() < max) {
            max = jsonArray.length();
        }
        for (int i = 0; i < max; i++) {
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
        playercombo.setSkin(comboBoxListViewSkinPlayer);
        for (Node each : advancedvboxinner.getChildren()) {
            if (each.getClass().equals(ComboBox.class)) {
                final ComboBox cb = (ComboBox) each;
                if (!cb.getId().equals("shotsuccesscombo") && !cb.getId().equals("shotvaluecombo")) {
                    ComboBoxListViewSkin<String> comboBoxListViewSkin = new ComboBoxListViewSkin<String>(cb) {
                        @Override
                        protected boolean isHideOnClickEnabled() {
                            return false;
                        }
                    };
                    cb.setSkin(comboBoxListViewSkin);
                }
            }
        }
    }

    private void setViewTypeButtonStyle(int selector) {
        for (int i = 0; i < viewButtons.size(); i++) {
            if (i == selector && viewButtons.get(i).isHover()) {
                viewButtons.get(i).setStyle("-fx-font: " + font + "px \"" + boldFont.getName() + "\";-fx-background-color: transparent;-fx-underline: true;");
            } else if (i == selector && !viewButtons.get(i).isHover()) {
                viewButtons.get(i).setStyle("-fx-font: " + font + "px \"" + boldFont.getName() + "\";-fx-background-color: transparent;");
            } else if (i != selector && viewButtons.get(i).isHover()) {
                viewButtons.get(i).setStyle("-fx-font: " + font + "px \"" + overallFont.getName() + "\";-fx-background-color: transparent;-fx-underline: true;");
            } else {
                viewButtons.get(i).setStyle("-fx-font: " + font + "px \"" + overallFont.getName() + "\";-fx-background-color: transparent;");
            }
        }
    }

    private boolean checkForEmptyAdvancedSearch() {
        if (!seasonsBeginComboUser.getSelection().equals("") || !seasonsEndComboUser.getSelection().equals("")
                || !distanceBeginComboUser.getSelection().equals("") || !distanceEndComboUser.getSelection().equals("")
                || !shotSuccessComboUser.getSelection().equals("") || !shotValueComboUser.getSelection().equals("")
                || !playerComboUserAdvanced.getHashSet().isEmpty() || !seasonTypesComboUser.getHashSet().isEmpty()
                || !shotTypeComboUser.getHashSet().isEmpty() || !teamComboUser.getHashSet().isEmpty()
                || !homeTeamComboUser.getHashSet().isEmpty() || !awayTeamComboUser.getHashSet().isEmpty()
                || !courtSidesComboUser.getHashSet().isEmpty() || !courtAreasComboUser.getHashSet().isEmpty()) {
            return true;
        }
        return false;
    }

    private void setEachViewTypeButtonOnClicked(int selector, Search currentSearch) {
        if (searchvbox.isVisible()) {
            try {
                this.previousYear = this.yearcombo.getValue().toString();
                this.previousPlayer = this.playercombo.getValue().toString();
                this.previousSeason = this.seasoncombo.getValue().toString();
                currentSimpleSearch = currentSearch;
                setViewTypeButtonStyle(selector);
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
                setViewTypeButtonStyle(selector);
                if (!currentAdvancedSearch.equals(Search.NONE)) {
                    runSearch();
                }
            } else {
                this.errorlabeladvanced.setText("Please include at least one search parameter");
                this.errorlabeladvanced.setVisible(true);
            }
        }

    }

    private void setEachViewTypeButtonsOnMouseEntered(int selector, Search currentSearch, Scene scene) {
        if ((searchvbox.isVisible() && currentSimpleSearch.equals(currentSearch)) || (advancedvbox.isVisible() && currentAdvancedSearch.equals(currentSearch))) {
            viewButtons.get(selector).setStyle("-fx-font: " + font + "px \"" + boldFont.getName() + "\";-fx-background-color: transparent;-fx-underline: true;");
        } else {
            viewButtons.get(selector).setStyle("-fx-font: " + font + "px \"" + overallFont.getName() + "\";-fx-background-color: transparent;-fx-underline: true;");
        }
        scene.setCursor(Cursor.HAND);
    }

    private void setEachViewTypeButtonsOnMouseExited(int selector, Search currentSearch, Scene scene) {
        if ((searchvbox.isVisible() && currentSimpleSearch.equals(currentSearch)) || (advancedvbox.isVisible() && currentAdvancedSearch.equals(currentSearch))) {
            viewButtons.get(selector).setStyle("-fx-font: " + font + "px \"" + boldFont.getName() + "\";-fx-background-color: transparent;-fx-underline: false;");
        } else {
            viewButtons.get(selector).setStyle("-fx-font: " + font + "px \"" + overallFont.getName() + "\";-fx-background-color: transparent;-fx-underline: false;");
        }
        scene.setCursor(Cursor.DEFAULT);
    }

    public void setAllViewTypeButtonsOnMouseActions() {
        final Scene scene = selectionvbox.getScene();
        font = new BigDecimal(COMBO_FONT_SIZE).multiply(new BigDecimal(imageview.getLayoutBounds().getHeight())).divide(new BigDecimal("550"), 6, RoundingMode.HALF_UP).doubleValue();
        this.traditionalbutton.setOnMouseClicked(t -> setEachViewTypeButtonOnClicked(0, Search.TRADITIONAL));
        this.gridbutton.setOnMouseClicked(t -> setEachViewTypeButtonOnClicked(1, Search.GRID));
        this.heatmapbutton.setOnMouseClicked(t -> setEachViewTypeButtonOnClicked(2, Search.HEAT));
        this.zonebutton.setOnMouseClicked(t -> setEachViewTypeButtonOnClicked(3, Search.ZONE));
        this.traditionalbutton.setOnMouseEntered(t -> setEachViewTypeButtonsOnMouseEntered(0, Search.TRADITIONAL, scene));
        this.traditionalbutton.setOnMouseExited(t -> setEachViewTypeButtonsOnMouseExited(0, Search.TRADITIONAL, scene));
        this.gridbutton.setOnMouseEntered(t -> setEachViewTypeButtonsOnMouseEntered(1, Search.GRID, scene));
        this.gridbutton.setOnMouseExited(t -> setEachViewTypeButtonsOnMouseExited(1, Search.GRID, scene));
        this.heatmapbutton.setOnMouseEntered(t -> setEachViewTypeButtonsOnMouseEntered(2, Search.HEAT, scene));
        this.heatmapbutton.setOnMouseExited(t -> setEachViewTypeButtonsOnMouseExited(2, Search.HEAT, scene));
        this.zonebutton.setOnMouseEntered(t -> setEachViewTypeButtonsOnMouseEntered(3, Search.ZONE, scene));
        this.zonebutton.setOnMouseExited(t -> setEachViewTypeButtonsOnMouseExited(3, Search.ZONE, scene));
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

    private void createServices() {
        tradMeth = new TraditionalMethods(this);
        gridMeth = new GridMethods(this, 10.0);
        heatMeth = new HeatMethods(this);
        zoneMeth = new ZoneMethods(this);
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

    private void createAlwaysRunningResizer() {
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (true) {
                    Platform.runLater(() -> resize());
                    Thread.sleep(100);
                }
            }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private boolean checkForSameSimpleSearch() {
        if (previousSimpleSearchJSON == null) {
            return false;
        } else if (!previousSimpleSearchJSON.getString("year").equals(this.yearcombo.getValue().toString())
                || !previousSimpleSearchJSON.get("playername").equals(nameHash.get(this.playercombo.getValue().toString()))
                || !previousSimpleSearchJSON.getString("seasontype").equals(this.seasoncombo.getValue().toString())) {
            return false;
        }
        return true;
    }

    private boolean checkForSameAdvancedSearch() {
        if (previousAdvancedSearchJSON == null) {
            return false;
        } else {
            if (!createJsonObjectOutput().toString().equals(previousAdvancedSearchJSON.toString())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public JSONArray chooseJSONArray() {
        try {
            if (searchvbox.isVisible()) {
                if (!checkForSameSimpleSearch()) {
                    previousSimpleSearchResults = getSimpleShotData();
                }
                return previousSimpleSearchResults;
            } else {
                if (!checkForSameAdvancedSearch()) {
                    previousAdvancedSearchResults = createAdvancedJSONOutput(currentAdvancedSearch);
                }
                return previousAdvancedSearchResults;
            }
        } catch (Exception ex) {
            System.out.println("Exception caught in chooseJSONArray");
            ex.printStackTrace();
        }
        return null;
    }

    private JSONObject createJsonObjectOutput() {
        JSONObject obj = new JSONObject();
        obj.put("beginSeason", seasonsBeginComboUser.getSelection());
        obj.put("endSeason", seasonsEndComboUser.getSelection());
        ArrayList<Integer> allSelectedPlayerIDs = new ArrayList();
        for (String each : playerComboUserAdvanced.getHashSet()) {
            allSelectedPlayerIDs.add(Integer.parseInt(nameHash.get(each)[0]));
        }
        obj.put("allSelectedPlayers", allSelectedPlayerIDs);
        obj.put("allSelectedSeasonTypes", seasonTypesComboUser.getHashSet());
        obj.put("beginDistance", distanceBeginComboUser.getSelection());
        obj.put("endDistance", distanceEndComboUser.getSelection());
        obj.put("shotSuccess", shotSuccessComboUser.getSelection());
        obj.put("shotValue", shotValueComboUser.getSelection());
        obj.put("allSelectedShotTypes", shotTypeComboUser.getHashSet());
        ArrayList<Integer> teamIds = new ArrayList();
        for (String each : teamComboUser.getHashSet()) {
            teamIds.add(relevantTeamNameIDHashMap.get(each));
        }
        obj.put("allSelectedTeams", teamIds);
        teamIds = new ArrayList();
        for (String each : homeTeamComboUser.getHashSet()) {
            teamIds.add(relevantTeamNameIDHashMap.get(each));
        }
        obj.put("allSelectedHomeTeams", teamIds);
        teamIds = new ArrayList();
        for (String each : awayTeamComboUser.getHashSet()) {
            teamIds.add(relevantTeamNameIDHashMap.get(each));
        }
        obj.put("allSelectedAwayTeams", teamIds);
        obj.put("allSelectedCourtAreas", courtAreasComboUser.getHashSet());
        obj.put("allSelectedCourtSides", courtSidesComboUser.getHashSet());
        return obj;
    }

    private void createCustomShapes() {
        tradBubbleNS = Shape.union(bubble, triangle);
        tradBubble = tradBubbleNS;
        imagegrid.getChildren().add(tradBubbleNS);
        imagegrid.getChildren().add(tradShotInfo);
        tradShotInfo.setMaxSize(tradBubble.getLayoutBounds().getWidth(), tradBubble.getLayoutBounds().getHeight() / 2);
        tradShotInfo.wrapTextProperty().setValue(true);
        tradShotInfo.setStyle("-fx-text-fill: WHITE;");
        tradShotInfo.setAlignment(Pos.CENTER);
        tradShotInfo.setTextAlignment(TextAlignment.CENTER);
        tradBubbleNS.setFill(Color.GRAY);
        tradBubbleNS.setStroke(Color.WHITE);
        tradBubbleNS.setStrokeWidth(1);
        triangle.setManaged(false);
        bubble.setManaged(false);
        triangle.setTranslateX(-60);
        triangle.setTranslateY(-20);
        triangle.setRotate(45);
        tradBubbleSWNE = Shape.union(bubble, triangle);
        imagegrid.getChildren().add(tradBubbleSWNE);
        tradBubbleSWNE.setFill(Color.GRAY);
        tradBubbleSWNE.setStroke(Color.WHITE);
        tradBubbleSWNE.setStrokeWidth(1);
        triangle.setTranslateX(-80);
        triangle.setTranslateY(-60);
        triangle.setRotate(90);
        tradBubbleWE = Shape.union(bubble, triangle);
        imagegrid.getChildren().add(tradBubbleWE);
        tradBubbleWE.setFill(Color.GRAY);
        tradBubbleWE.setStroke(Color.WHITE);
        tradBubbleWE.setStrokeWidth(1);
        triangle.setTranslateX(-60);
        triangle.setTranslateY(-100);
        triangle.setRotate(135);
        tradBubbleNWSE = Shape.union(bubble, triangle);
        imagegrid.getChildren().add(tradBubbleNWSE);
        tradBubbleNWSE.setFill(Color.GRAY);
        tradBubbleNWSE.setStroke(Color.WHITE);
        tradBubbleNWSE.setStrokeWidth(1);
        tradShotInfo.setVisible(false);
        tradBubbleNS.setVisible(false);
        tradBubbleSWNE.setVisible(false);
        tradBubbleWE.setVisible(false);
        tradBubbleNWSE.setVisible(false);
        triangle.setVisible(false);
        bubble.setVisible(false);
    }

    @Override
    public void resetViewOnServiceFailed(Service service) {
        System.out.println("Failed");
        endLoadingTransition();
        enableButtons();
    }

    @Override
    public void setMadeShot(Shot shot, HashMap hashmap) {
        Circle circle = new Circle(imageview.getLayoutBounds().getHeight() * SHOT_MADE_RADIUS.divide(ORIG_HEIGHT, 6, RoundingMode.HALF_UP).doubleValue());
        circle.setFill(Color.TRANSPARENT);
        circle.setTranslateX(shot.getX() * imageview.getLayoutBounds().getHeight() / 470 + imageview.localToParent(imageview.getBoundsInLocal()).getMinX() + imageview.localToParent(imageview.getBoundsInLocal()).getWidth() / 2);
        circle.setTranslateY(shot.getY() * imageview.getLayoutBounds().getHeight() / 470 + imageview.localToParent(imageview.getBoundsInLocal()).getMinY() + imageview.localToParent(imageview.getBoundsInLocal()).getHeight() / 2 - (185.0 * imageview.localToParent(imageview.getBoundsInLocal()).getHeight() / 470));
        circle.setStrokeWidth(imageview.getLayoutBounds().getHeight() * SHOT_LINE_THICKNESS.divide(ORIG_HEIGHT, 6, RoundingMode.HALF_UP).doubleValue());
        circle.setStroke(Color.LIMEGREEN);
        circle.setManaged(false);
        hashmap.put(shot, circle);
        final Circle finalCircle = circle;
        circle.setOnMouseEntered((t) -> {
            tradShotInfo.setStyle("-fx-text-fill: WHITE; -fx-font: " + imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 12.0 / 470 + "px \"" + boldFont.getName() + "\";");
            if (shot.getX() < -175 && shot.getY() < 80) {//1
                tradBubble = tradBubbleNWSE;
                tradBubble.setRotate(0);
                tradBubble.setTranslateX(finalCircle.getTranslateX() - imagegrid.getLayoutBounds().getWidth() / 2 + (tradBubble.getLayoutBounds().getWidth() * tradBubble.getScaleX()) / 1.75);
                tradBubble.setTranslateY(finalCircle.getTranslateY() - imagegrid.getLayoutBounds().getHeight() / 2 + (tradBubble.getLayoutBounds().getHeight() * tradBubble.getScaleY()) / 1.75);
                tradShotInfo.setTranslateX(finalCircle.getTranslateX() - imagegrid.getLayoutBounds().getWidth() / 2 + tradBubble.getLayoutBounds().getWidth() * tradBubble.getScaleX() / 1.55);
                tradShotInfo.setTranslateY(finalCircle.getTranslateY() - imagegrid.getLayoutBounds().getHeight() / 2 + tradBubble.getLayoutBounds().getHeight() * tradBubble.getScaleY() / 1.55);
            } else if (shot.getX() >= -175 && shot.getX() <= 175 && shot.getY() < 80) {//2
                tradBubble = tradBubbleNS;
                tradBubble.setRotate(180);
                tradBubble.setTranslateX(finalCircle.getTranslateX() - imagegrid.getLayoutBounds().getWidth() / 2);
                tradBubble.setTranslateY(finalCircle.getTranslateY() - imagegrid.getLayoutBounds().getHeight() / 2 + tradBubble.getLayoutBounds().getHeight() * tradBubble.getScaleY() / 1.75);
                tradShotInfo.setTranslateX(finalCircle.getTranslateX() - imagegrid.getLayoutBounds().getWidth() / 2);
                tradShotInfo.setTranslateY(finalCircle.getTranslateY() - imagegrid.getLayoutBounds().getHeight() / 2 + tradBubble.getLayoutBounds().getHeight() * tradBubble.getScaleY() / 1.35);
            } else if (shot.getX() >= 175 && shot.getY() < 80) {//3
                tradBubble = tradBubbleSWNE;
                tradBubble.setRotate(180);
                tradBubble.setTranslateX(finalCircle.getTranslateX() - imagegrid.getLayoutBounds().getWidth() / 2 - tradBubble.getLayoutBounds().getWidth() * tradBubble.getScaleX() / 1.75);
                tradBubble.setTranslateY(finalCircle.getTranslateY() - imagegrid.getLayoutBounds().getHeight() / 2 + tradBubble.getLayoutBounds().getHeight() * tradBubble.getScaleY() / 1.75);
                tradShotInfo.setTranslateX(finalCircle.getTranslateX() - imagegrid.getLayoutBounds().getWidth() / 2 - tradBubble.getLayoutBounds().getWidth() * tradBubble.getScaleX() / 1.6);
                tradShotInfo.setTranslateY(finalCircle.getTranslateY() - imagegrid.getLayoutBounds().getHeight() / 2 + tradBubble.getLayoutBounds().getHeight() * tradBubble.getScaleY() / 1.55);
            } else if (shot.getX() < -175 && shot.getY() >= 80 && shot.getY() <= 335) {//4
                tradBubble = tradBubbleWE;
                tradBubble.setRotate(0);
                tradBubble.setTranslateX(finalCircle.getTranslateX() - imagegrid.getLayoutBounds().getWidth() / 2 + tradBubble.getLayoutBounds().getWidth() * tradBubble.getScaleX() / 1.75);
                tradBubble.setTranslateY(finalCircle.getTranslateY() - imagegrid.getLayoutBounds().getHeight() / 2);
                tradShotInfo.setTranslateX(finalCircle.getTranslateX() - imagegrid.getLayoutBounds().getWidth() / 2 + tradBubble.getLayoutBounds().getWidth() * tradBubble.getScaleX() / 1.4);
                tradShotInfo.setTranslateY(finalCircle.getTranslateY() - imagegrid.getLayoutBounds().getHeight() / 2);
            } else if (shot.getX() > 175 && shot.getY() >= 80 && shot.getY() <= 335) {//6
                tradBubble = tradBubbleWE;
                tradBubble.setRotate(180);
                tradBubble.setTranslateX(finalCircle.getTranslateX() - imagegrid.getLayoutBounds().getWidth() / 2 - tradBubble.getLayoutBounds().getWidth() * tradBubble.getScaleX() / 1.75);
                tradBubble.setTranslateY(finalCircle.getTranslateY() - imagegrid.getLayoutBounds().getHeight() / 2);
                tradShotInfo.setTranslateX(finalCircle.getTranslateX() - imagegrid.getLayoutBounds().getWidth() / 2 - tradBubble.getLayoutBounds().getWidth() * tradBubble.getScaleX() / 1.45);
                tradShotInfo.setTranslateY(finalCircle.getTranslateY() - imagegrid.getLayoutBounds().getHeight() / 2);
            } else if (shot.getX() < -175 && shot.getY() > 335) {//7
                tradBubble = tradBubbleSWNE;
                tradBubble.setRotate(0);
                tradBubble.setTranslateX(finalCircle.getTranslateX() - imagegrid.getLayoutBounds().getWidth() / 2 + tradBubble.getLayoutBounds().getWidth() * tradBubble.getScaleX() / 1.75);
                tradBubble.setTranslateY(finalCircle.getTranslateY() - imagegrid.getLayoutBounds().getHeight() / 2 - tradBubble.getLayoutBounds().getHeight() * tradBubble.getScaleY() / 1.75);
                tradShotInfo.setTranslateX(finalCircle.getTranslateX() - imagegrid.getLayoutBounds().getWidth() / 2 + tradBubble.getLayoutBounds().getWidth() * tradBubble.getScaleX() / 1.6);
                tradShotInfo.setTranslateY(finalCircle.getTranslateY() - imagegrid.getLayoutBounds().getHeight() / 2 - tradBubble.getLayoutBounds().getHeight() * tradBubble.getScaleY() / 1.5);
            } else if (shot.getX() >= 175 && shot.getY() > 335) {//9
                tradBubble = tradBubbleNWSE;
                tradBubble.setRotate(180);
                tradBubble.setTranslateX(finalCircle.getTranslateX() - imagegrid.getLayoutBounds().getWidth() / 2 - tradBubble.getLayoutBounds().getWidth() * tradBubble.getScaleX() / 1.75);
                tradBubble.setTranslateY(finalCircle.getTranslateY() - imagegrid.getLayoutBounds().getHeight() / 2 - tradBubble.getLayoutBounds().getHeight() * tradBubble.getScaleY() / 1.75);
                tradShotInfo.setTranslateX(finalCircle.getTranslateX() - imagegrid.getLayoutBounds().getWidth() / 2 - tradBubble.getLayoutBounds().getWidth() * tradBubble.getScaleX() / 1.6);
                tradShotInfo.setTranslateY(finalCircle.getTranslateY() - imagegrid.getLayoutBounds().getHeight() / 2 - tradBubble.getLayoutBounds().getHeight() * tradBubble.getScaleY() / 1.5);
            } else {//5
                tradBubble = tradBubbleNS;
                tradBubble.setRotate(0);
                tradBubble.setTranslateX(finalCircle.getTranslateX() - imagegrid.getLayoutBounds().getWidth() / 2);
                tradBubble.setTranslateY(finalCircle.getTranslateY() - imagegrid.getLayoutBounds().getHeight() / 2 - tradBubble.getLayoutBounds().getHeight() * tradBubble.getScaleX() / 1.75);
                tradShotInfo.setTranslateX(finalCircle.getTranslateX() - imagegrid.getLayoutBounds().getWidth() / 2);
                tradShotInfo.setTranslateY(finalCircle.getTranslateY() - imagegrid.getLayoutBounds().getHeight() / 2 - tradBubble.getLayoutBounds().getHeight() * tradBubble.getScaleX() / 1.3);
            }
            if (shot.getShottype().equals("2PT Field Goal")) {
                tradShotInfo.setText("Made " + shot.getDistance() + "' " + shot.getPlaytype().replace("shot", "Shot"));
            } else {
                tradShotInfo.setText("Made " + shot.getDistance() + "' 3-Point " + shot.getPlaytype().replace("shot", "Shot"));
            }
            tradBubble.toFront();
            tradBubble.setVisible(true);
            tradShotInfo.toFront();
            tradShotInfo.setVisible(true);
        });
        circle.setOnMouseExited((t) -> {
            for (Shape each : allBubbles) {
                each.setVisible(false);
            }
            tradShotInfo.setVisible(false);
        });
    }

    @Override
    public void setMissedShot(Shot shot, HashMap hashmap) {
        MissedShotIcon msi = new MissedShotIcon((shot.getX()) / 470,
                ((shot.getY() - 55) / 470),
                imageview.getLayoutBounds().getHeight(),
                SHOT_MISS_START_END.divide(ORIG_HEIGHT, 6, RoundingMode.HALF_UP).doubleValue(),
                SHOT_LINE_THICKNESS.divide(ORIG_HEIGHT, 6, RoundingMode.HALF_UP).doubleValue(),
                imageview.localToParent(imageview.getBoundsInLocal()).getMinX() + imageview.localToParent(imageview.getBoundsInLocal()).getWidth() / 2,
                imageview.localToParent(imageview.getBoundsInLocal()).getMinY(),
                shot);
        msi.getLine1().setManaged(false);
        msi.getLine2().setManaged(false);
        hashmap.put(shot, msi);
    }

    @Override
    public void plotTradShots() {
        resetView();
        removeAllShotsFromView();
        imageview.setImage(new Image("/images/newbackcourt.png"));
        tradMeth.getAllShots().keySet().stream()
                .filter((each) -> (each.getY() <= 410))
                .forEachOrdered((each) -> {
                    if (each.getMake() == 0) {
                        final MissedShotIcon msiTemp = (MissedShotIcon) tradMeth.getAllShots().get(each);
                        msiTemp.getLine1().setManaged(false);
                        msiTemp.getLine2().setManaged(false);
                        msiTemp.getLine1().setTranslateX(BigDecimal.valueOf(each.getX()).doubleValue() * imageview.getLayoutBounds().getHeight() / 470 + imageview.localToParent(imageview.getBoundsInLocal()).getMinX() + imageview.localToParent(imageview.getBoundsInLocal()).getWidth() / 2);// 50/470
                        msiTemp.getLine2().setTranslateX(BigDecimal.valueOf(each.getX()).doubleValue() * imageview.getLayoutBounds().getHeight() / 470 + imageview.localToParent(imageview.getBoundsInLocal()).getMinX() + imageview.localToParent(imageview.getBoundsInLocal()).getWidth() / 2);
                        msiTemp.getLine1().setTranslateY(BigDecimal.valueOf(each.getY()).doubleValue() * imageview.getLayoutBounds().getHeight() / 470 + imageview.localToParent(imageview.getBoundsInLocal()).getMinY() + imageview.localToParent(imageview.getBoundsInLocal()).getHeight() / 2 - (180.0 * imageview.localToParent(imageview.getBoundsInLocal()).getHeight() / 470));
                        msiTemp.getLine2().setTranslateY(BigDecimal.valueOf(each.getY()).doubleValue() * imageview.getLayoutBounds().getHeight() / 470 + imageview.localToParent(imageview.getBoundsInLocal()).getMinY() + imageview.localToParent(imageview.getBoundsInLocal()).getHeight() / 2 - (180.0 * imageview.localToParent(imageview.getBoundsInLocal()).getHeight() / 470));
                        msiTemp.getRect().setTranslateX(BigDecimal.valueOf(each.getX()).doubleValue() * imageview.getLayoutBounds().getHeight() / 470 + imageview.localToParent(imageview.getBoundsInLocal()).getMinX());
                        msiTemp.getRect().setTranslateY(BigDecimal.valueOf(each.getY()).doubleValue() * imageview.getLayoutBounds().getHeight() / 470 + imageview.localToParent(imageview.getBoundsInLocal()).getMinY() - (180.0 * imageview.localToParent(imageview.getBoundsInLocal()).getHeight() / 470));
                        imagegrid.getChildren().add(msiTemp.getLine1());
                        imagegrid.getChildren().add(msiTemp.getLine2());
                        imagegrid.getChildren().add(msiTemp.getRect());
                        msiTemp.getRect().setOnMouseEntered((t) -> {
                            try {
                                tradShotInfo.setStyle("-fx-text-fill: WHITE; -fx-font: " + imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 12.0 / 470 + "px \"" + boldFont.getName() + "\";");
                                if (msiTemp.getShot().getX() < -175 && msiTemp.getShot().getY() < 80) {//1
                                    tradBubble = tradBubbleNWSE;
                                    tradBubble.setRotate(0);
                                    tradBubble.setTranslateX(msiTemp.getRect().getTranslateX() + tradBubble.getLayoutBounds().getWidth() * tradBubble.getScaleX() / 1.75);
                                    tradBubble.setTranslateY(msiTemp.getRect().getTranslateY() + tradBubble.getLayoutBounds().getHeight() * tradBubble.getScaleY() / 1.75);
                                    tradShotInfo.setTranslateX(msiTemp.getRect().getTranslateX() + tradBubble.getLayoutBounds().getWidth() * tradBubble.getScaleX() / 1.55);
                                    tradShotInfo.setTranslateY(msiTemp.getRect().getTranslateY() + tradBubble.getLayoutBounds().getHeight() * tradBubble.getScaleY() / 1.55);
                                } else if (msiTemp.getShot().getX() >= -175 && msiTemp.getShot().getX() <= 175 && msiTemp.getShot().getY() < 80) {//2
                                    tradBubble = tradBubbleNS;
                                    tradBubble.setRotate(180);
                                    tradBubble.setTranslateX(msiTemp.getRect().getTranslateX());
                                    tradBubble.setTranslateY(msiTemp.getRect().getTranslateY() + tradBubble.getLayoutBounds().getHeight() * tradBubble.getScaleY() / 1.75);
                                    tradShotInfo.setTranslateX(msiTemp.getRect().getTranslateX());
                                    tradShotInfo.setTranslateY(msiTemp.getRect().getTranslateY() + tradBubble.getLayoutBounds().getHeight() * tradBubble.getScaleY() / 1.35);
                                } else if (msiTemp.getShot().getX() >= 175 && msiTemp.getShot().getY() < 80) {//3
                                    tradBubble = tradBubbleSWNE;
                                    tradBubble.setRotate(180);
                                    tradBubble.setTranslateX(msiTemp.getRect().getTranslateX() - tradBubble.getLayoutBounds().getWidth() * tradBubble.getScaleX() / 1.75);
                                    tradBubble.setTranslateY(msiTemp.getRect().getTranslateY() + tradBubble.getLayoutBounds().getHeight() * tradBubble.getScaleY() / 1.75);
                                    tradShotInfo.setTranslateX(msiTemp.getRect().getTranslateX() - tradBubble.getLayoutBounds().getWidth() * tradBubble.getScaleX() / 1.6);
                                    tradShotInfo.setTranslateY(msiTemp.getRect().getTranslateY() + tradBubble.getLayoutBounds().getHeight() * tradBubble.getScaleY() / 1.55);
                                } else if (msiTemp.getShot().getX() < -175 && msiTemp.getShot().getY() >= 80 && msiTemp.getShot().getY() <= 335) {//4
                                    tradBubble = tradBubbleWE;
                                    tradBubble.setRotate(0);
                                    tradBubble.setTranslateX(msiTemp.getRect().getTranslateX() + tradBubble.getLayoutBounds().getWidth() * tradBubble.getScaleX() / 1.75);
                                    tradBubble.setTranslateY(msiTemp.getRect().getTranslateY());
                                    tradShotInfo.setTranslateX(msiTemp.getRect().getTranslateX() + tradBubble.getLayoutBounds().getWidth() * tradBubble.getScaleX() / 1.4);
                                    tradShotInfo.setTranslateY(msiTemp.getRect().getTranslateY());
                                } else if (msiTemp.getShot().getX() > 175 && msiTemp.getShot().getY() >= 80 && msiTemp.getShot().getY() <= 335) {//6
                                    tradBubble = tradBubbleWE;
                                    tradBubble.setRotate(180);
                                    tradBubble.setTranslateX(msiTemp.getRect().getTranslateX() - tradBubble.getLayoutBounds().getWidth() * tradBubble.getScaleX() / 1.75);
                                    tradBubble.setTranslateY(msiTemp.getRect().getTranslateY());
                                    tradShotInfo.setTranslateX(msiTemp.getRect().getTranslateX() - tradBubble.getLayoutBounds().getWidth() * tradBubble.getScaleX() / 1.45);
                                    tradShotInfo.setTranslateY(msiTemp.getRect().getTranslateY());
                                } else if (msiTemp.getShot().getX() < -175 && msiTemp.getShot().getY() > 335) {//7
                                    tradBubble = tradBubbleSWNE;
                                    tradBubble.setRotate(0);
                                    tradBubble.setTranslateX(msiTemp.getRect().getTranslateX() + tradBubble.getLayoutBounds().getWidth() * tradBubble.getScaleX() / 1.75);
                                    tradBubble.setTranslateY(msiTemp.getRect().getTranslateY() - tradBubble.getLayoutBounds().getHeight() * tradBubble.getScaleY() / 1.75);
                                    tradShotInfo.setTranslateX(msiTemp.getRect().getTranslateX() + tradBubble.getLayoutBounds().getWidth() * tradBubble.getScaleX() / 1.6);
                                    tradShotInfo.setTranslateY(msiTemp.getRect().getTranslateY() - tradBubble.getLayoutBounds().getHeight() * tradBubble.getScaleY() / 1.5);
                                } else if (msiTemp.getShot().getX() >= 175 && msiTemp.getShot().getY() > 335) {//9
                                    tradBubble = tradBubbleNWSE;
                                    tradBubble.setRotate(180);
                                    tradBubble.setTranslateX(msiTemp.getRect().getTranslateX() - tradBubble.getLayoutBounds().getWidth() * tradBubble.getScaleX() / 1.75);
                                    tradBubble.setTranslateY(msiTemp.getRect().getTranslateY() - tradBubble.getLayoutBounds().getHeight() * tradBubble.getScaleY() / 1.75);
                                    tradShotInfo.setTranslateX(msiTemp.getRect().getTranslateX() - tradBubble.getLayoutBounds().getWidth() * tradBubble.getScaleX() / 1.6);
                                    tradShotInfo.setTranslateY(msiTemp.getRect().getTranslateY() - tradBubble.getLayoutBounds().getHeight() * tradBubble.getScaleY() / 1.5);
                                } else {//5
                                    tradBubble = tradBubbleNS;
                                    tradBubble.setRotate(0);
                                    tradBubble.setTranslateX(msiTemp.getRect().getTranslateX());
                                    tradBubble.setTranslateY(msiTemp.getRect().getTranslateY() - tradBubble.getLayoutBounds().getHeight() * tradBubble.getScaleX() / 1.75);
                                    tradShotInfo.setTranslateX(msiTemp.getRect().getTranslateX());
                                    tradShotInfo.setTranslateY(msiTemp.getRect().getTranslateY() - tradBubble.getLayoutBounds().getHeight() * tradBubble.getScaleX() / 1.3);
                                }
                                if (msiTemp.getShot().getShottype().equals("2PT Field Goal")) {
                                    tradShotInfo.setText("Missed " + msiTemp.getShot().getDistance() + "' " + msiTemp.getShot().getPlaytype().replace("shot", "Shot"));
                                } else {
                                    tradShotInfo.setText("Missed " + msiTemp.getShot().getDistance() + "' 3-Point " + msiTemp.getShot().getPlaytype().replace("shot", "Shot"));
                                }
                                tradBubble.toFront();
                                tradShotInfo.toFront();
                                tradBubble.setVisible(true);
                                tradShotInfo.setVisible(true);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        });
                        msiTemp.getRect().setOnMouseExited((t) -> {
                            for (Shape eachBub : allBubbles) {
                                eachBub.setVisible(false);
                            }
                            tradShotInfo.setVisible(false);
                        });
                    } else {
                        imagegrid.getChildren().add((Circle) tradMeth.getAllShots().get(each));
                    }
                });
        if (searchvbox.isVisible()) {
            setShotGrid(previousSimpleSearchResults);
        } else {
            setShotGridAdvanced(previousAdvancedSearchResults);
        }
        endLoadingTransition();
        enableButtons();
    }

    @Override
    public void notifyOfTradShotsGathered() {
        Platform.runLater(() -> progresslabel.setText("Generating Traditional Shot Map"));
    }

    @Override
    public void notifyOfGatheringTradShots() {
        Platform.runLater(() -> errorlabeladvanced.setVisible(searchvbox.isVisible()));
        Platform.runLater(() -> progresslabel.setText("Gathering Shots"));
    }

    @Override
    public void notifyOfGatheringGridShots() {
        Platform.runLater(() -> progresslabel.setText("Gathering Shots"));
        Platform.runLater(() -> errorlabeladvanced.setVisible(searchvbox.isVisible()));
    }

    @Override
    public void notifyOfGatheringHeatShots() {
        Platform.runLater(() -> errorlabeladvanced.setVisible(searchvbox.isVisible()));
        Platform.runLater(() -> progresslabel.setText("Gathering Shots"));
    }

    @Override
    public void notifyOfGatheringZoneShots() {
        Platform.runLater(() -> errorlabeladvanced.setVisible(searchvbox.isVisible()));
        Platform.runLater(() -> progresslabel.setText("Gathering Shots"));
    }

    @Override
    public void notifyOfGridShotsGathered() {
        Platform.runLater(() -> progresslabel.setText("Generating Grid"));
    }

    @Override
    public void notifyOfHeatShotsGathered() {
        Platform.runLater(() -> progresslabel.setText("Generating Heat Map"));
    }

    @Override
    public void notifyOfZoneShotsGathered() {
        Platform.runLater(() -> progresslabel.setText("Generating Zones"));
    }

    @Override
    public void plotGridShots() {
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
        gridcolorlegendtoplabel.setStyle("-fx-font: " + imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 13.0 / 470 + "px \"" + overallFont.getName() + "\";");
        gridsizelegendtoplabel.setStyle("-fx-font: " + imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 13.0 / 470 + "px \"" + overallFont.getName() + "\";");
        gridcolorlegendlowerlabel.setStyle("-fx-font: " + imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 11.0 / 470 + "px \"" + overallFont.getName() + "\";");
        gridcolorlegendupperlabel.setStyle("-fx-font: " + imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 11.0 / 470 + "px \"" + overallFont.getName() + "\";");
        gridsizelegendlowerlabel.setStyle("-fx-font: " + imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 11.0 / 470 + "px \"" + overallFont.getName() + "\";");
        gridsizelegendupperlabel.setStyle("-fx-font: " + imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 11.0 / 470 + "px \"" + overallFont.getName() + "\";");
        imageview.setImage(new Image("/images/transparent.png"));
        if (searchvbox.isVisible()) {
            setShotGrid(previousSimpleSearchResults);
        } else {
            setShotGridAdvanced(previousAdvancedSearchResults);
        }
        gridMeth.getAllTiles().forEach(square -> imagegrid.add(square, 0, 0));
        endLoadingTransition();
        enableButtons();
    }

    @Override
    public void plotHeatShots() {
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
        heatlegendtoplabel.setStyle("-fx-font: " + imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 13.0 / 470 + "px \"" + overallFont.getName() + "\";");
        heatlegendlowerlabel.setStyle("-fx-font: " + imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 11.0 / 470 + "px \"" + overallFont.getName() + "\";");
        heatlegendupperlabel.setStyle("-fx-font: " + imageview.localToParent(imageview.getBoundsInLocal()).getHeight() * 11.0 / 470 + "px \"" + overallFont.getName() + "\";");

        if (searchvbox.isVisible()) {
            setShotGrid(previousSimpleSearchResults);
        } else {
            setShotGridAdvanced(previousAdvancedSearchResults);
        }

        double weight = 0.5;
        double radius = 25 * imageview.localToParent(imageview.getBoundsInLocal()).getHeight() / 470.0;
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
        for (Coordinate each : heatMeth.getCoordValue().keySet()) {
            if (heatMeth.getCoordValue().get(each) > maxValue) {
                maxValue = heatMeth.getCoordValue().get(each);
            }
        }
        if (maxValue != 0) {
            maxValue = maxValue * (500 * 1.0 / heatMeth.getShotCounter());
            maxCutoff = 0.00004 * heatMeth.getShotCounter() / maxValue + 0.3065;
            diff = maxCutoff / 7;
            allHeatCircles = new LinkedList();
            for (Coordinate each : heatMeth.getCoordValue().keySet()) {
                double value = heatMeth.getCoordValue().get(each);
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
    }

    @Override
    public void plotZoneShots() {
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
            setShotGrid(previousSimpleSearchResults);
        } else {
            setShotGridAdvanced(previousAdvancedSearchResults);
        }
        Shape tempShape;
        Rectangle tempRect;
        Arc tempArc;
        for (int i = 1; i < 16; i++) {
            if (allShapes.get(i - 1).getClass().equals(Arc.class)) {
                tempArc = (Arc) allShapes.get(i - 1);
                changeArcColor(tempArc, zoneMeth.getAllShots().get(i), i);
            } else if (allShapes.get(i - 1).getClass().equals(Rectangle.class)) {
                tempRect = (Rectangle) allShapes.get(i - 1);
                changeRectColor(tempRect, zoneMeth.getAllShots().get(i), i);
            } else {
                tempShape = (Shape) allShapes.get(i - 1);
                changeShapeColor(tempShape, zoneMeth.getAllShots().get(i), i);
            }
        }
        imageview.toFront();
        for (int j = 0; j < 15; j++) {
            allLabels.get(j).setText(zoneMeth.getAllZones().get(j + 1)[0].intValue() + "/" + zoneMeth.getAllZones().get(j + 1)[1].intValue());
            allLabels.get(j).toFront();
            if (zoneMeth.getAllZones().get(j + 1)[1].intValue() != 0 && zoneMeth.getAllZones().get(j + 1)[1].intValue() == zoneMeth.getAllZones().get(j + 1)[0].intValue()) {
                allPercentLabels.get(j).setText("100%");
            } else if (zoneMeth.getAllZones().get(j + 1)[1].intValue() == 0 && zoneMeth.getAllZones().get(j + 1)[1].intValue() == zoneMeth.getAllZones().get(j + 1)[0].intValue()) {
                allPercentLabels.get(j).setText("0%");
            } else {
                allPercentLabels.get(j).setText(df.format(zoneMeth.getAllZones().get(j + 1)[2] * 100) + "%");
            }
            allPercentLabels.get(j).toFront();
        }
        zonelegend.setVisible(true);
        zonelegend.toFront();
        endLoadingTransition();
        enableButtons();
    }

    @Override
    public double getWidth() {
        return this.imageview.localToParent(imageview.getBoundsInLocal()).getWidth();
    }

    @Override
    public double getHeight() {
        return this.imageview.localToParent(imageview.getBoundsInLocal()).getHeight();
    }

}
