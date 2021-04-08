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
package logic;

import controllers.MapControllerInterface;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import mainapp.Coordinate;
import mainapp.Main;
import mainapp.Search;
import mainapp.Shot;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author samnishita
 */
public class GridMethods implements MethodsInterface {

    private Service gridService;
    private MapControllerInterface mci;
    private HashMap<Coordinate, ArrayList<Double>> allShots = new HashMap();
    private long startTime;
    private long endTime;
    private double squareSizeOrig = 10.0;
    private int shotCounter = 0;
    private ConcurrentHashMap<Coordinate, Double> coordValue = new ConcurrentHashMap();
    private double min;
    private int maxShotsPerMaxSquare = 0;
    private double squareSize = 10.0;
    private LinkedList<Rectangle> allTiles = new LinkedList();
    private int offset = 10;
    private int maxDistanceBetweenNodes = 20;

    public GridMethods(MapControllerInterface mci, double squareSizeOrig) {
        this.mci = mci;
        this.squareSizeOrig = squareSizeOrig;
        gridService = new Service() {
            @Override
            protected Task<ConcurrentHashMap<Coordinate, Double>> createTask() {
                return new Task() {
                    @Override
                    protected ConcurrentHashMap<Coordinate, Double> call() throws Exception {
                        return serviceTaskMethods();
                    }
                };
            }
        };
        gridService.setOnSucceeded(s -> plotAfterServiceSucceeds());
        gridService.setOnFailed(s -> setOnServiceFailed(mci));
    }

    @Override
    public ConcurrentHashMap<Coordinate, Double> serviceTaskMethods() {
        mci.notifyOfGatheringGridShots();
        startTime = System.nanoTime();
        JSONArray jsonArray = mci.chooseJSONArray();
        mci.notifyOfGridShotsGathered();
        Coordinate coord;
        allShots = new LinkedHashMap();
        for (int j = -55; j < 400; j = j + (int) squareSizeOrig) {
            for (int i = -250; i < 250; i = i + (int) squareSizeOrig) {
                coord = new Coordinate(i, j);
                ArrayList info = new ArrayList();
                info.add(0.0);
                info.add(0.0);
                info.add(0.0);
                allShots.put(coord, info);
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
//        allShots = new LinkedHashMap();
        JSONObject eachShot;
        for (int i = 0; i < jsonArray.length(); i++) {
            eachShot = jsonArray.getJSONObject(i);
            if (eachShot.getInt("y") >= 400) {
                continue;
            }
            shotCounter++;
            for (Coordinate each : allShots.keySet()) {
                if (eachShot.getInt("x") < each.getX() + 5 + squareSizeOrig * 1.5 && eachShot.getInt("x") >= each.getX() + 5 - squareSizeOrig * 1.5 && eachShot.getInt("y") < each.getY() + 5 + squareSizeOrig * 1.5 && eachShot.getInt("y") >= each.getY() + 5 - squareSizeOrig * 1.5) {
                    allShots.get(each).set(1, allShots.get(each).get(1) + 1);
                    if (eachShot.getInt("make") == 1) {
                        allShots.get(each).set(0, allShots.get(each).get(0) + 1);
                    }
                }
            }

        }
        for (Coordinate each : allShots.keySet()) {
            if (allShots.get(each).get(1) != 0) {
                allShots.get(each).set(2, allShots.get(each).get(0) * 1.0 / allShots.get(each).get(1) * 1.0);
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
//        squareSize = imageview.getLayoutBounds().getWidth() / 50;
        squareSize = mci.getWidth() / 50;
        allTiles = new LinkedList();
        String temp;
        double avg;
        for (Coordinate each2 : coordValue.keySet()) {
            Rectangle square = new Rectangle();
            if (allShots.get(each2).get(1) < maxShotsPerMaxSquare && allShots.get(each2).get(1) > min) {
                square.setHeight((allShots.get(each2).get(1) / maxShotsPerMaxSquare * squareSize) * 0.9);
                square.setWidth((allShots.get(each2).get(1) / maxShotsPerMaxSquare * squareSize) * 0.9);
            } else if (allShots.get(each2).get(1) >= maxShotsPerMaxSquare) {
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
//            square.setTranslateX((each2.getX() + 5) * imageview.getLayoutBounds().getHeight() / 470);
            square.setTranslateY(each2.getY() * mci.getHeight() / 470 - (175.0 * mci.getHeight() / 470));
//            
            square.setTranslateX((each2.getX() + 5) * mci.getHeight() / 470);
            square.setTranslateY(each2.getY() * mci.getHeight() / 470 - (175.0 * mci.getHeight() / 470));
            allTiles.add(square);
        }
        return coordValue;
    }

    @Override
    public Service getService() {
        return this.gridService;
    }

    @Override
    public void plotAfterServiceSucceeds() {
        mci.plotGridShots();
        endTime = System.nanoTime();
        System.out.println("GRID: " + (endTime - startTime) * 1.0 / 1000000000 + " seconds");
    }

    @Override
    public HashMap<Coordinate, ArrayList<Double>> getAllShots() {
        return allShots;
    }

    @Override
    public void setOnServiceFailed(MapControllerInterface mci) {
        mci.resetViewOnServiceFailed(gridService);
    }

    private HashMap<String, BigDecimal> useGridAverages() throws IOException {
        HashMap<String, BigDecimal> hashmap = new HashMap();
        JSONArray jsonArray = getGridAveragesData();
        for (int i = 0; i < jsonArray.length(); i++) {
            hashmap.put(jsonArray.getJSONObject(i).getString("uniqueid"), jsonArray.getJSONObject(i).getBigDecimal("average"));
        }
        return hashmap;
    }

    private JSONArray getGridAveragesData() throws IOException {
        JSONObject jsonObjOut = new JSONObject();
        jsonObjOut.put("selector", "gridaverages");
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
        for (Coordinate each : allShots.keySet()) {
            if (each.getX() % offset == 0 && (each.getY() - 5) % offset == 0) {
                aSum = 0;
                bSum = 0;
                for (Coordinate each2 : allShots.keySet()) {
                    if (!each.equals(each2) && mci.getDistance(each, each2) < maxDistanceBetweenNodes) {
                        valueI = allShots.get(each2).get(2);
                        aSum = aSum + (valueI / Math.pow(mci.getDistance(each, each2), p));
                        bSum = bSum + (1 / Math.pow(mci.getDistance(each, each2), p));
                    }
                }
                predictedValue = aSum / bSum;
                coordValue.put(each, predictedValue);
            }
        }
    }

    public ConcurrentHashMap<Coordinate, Double> getCoordValue() {
        return this.coordValue;
    }

    public LinkedList<Rectangle> getAllTiles() {
        return allTiles;
    }

    public int getMaxShotsPerMaxSquare() {
        return maxShotsPerMaxSquare;
    }

    public int getMaxDistanceBetweenNodes() {
        return maxDistanceBetweenNodes;
    }

    public double getMin() {
        return min;
    }

}
