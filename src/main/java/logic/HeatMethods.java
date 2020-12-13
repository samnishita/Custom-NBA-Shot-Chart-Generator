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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import mainapp.Coordinate;
import mainapp.Search;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author samnishita
 */
public class HeatMethods implements MethodsInterface {

    private Service heatService;
    private MapControllerInterface mci;
    private HashMap<Coordinate, ArrayList<Double>> allShots = new HashMap();
    private long startTime;
    private long endTime;
    private int shotCounter = 0;
    private ConcurrentHashMap<Coordinate, Double> coordValue = new ConcurrentHashMap();
    private ArrayList<Coordinate> coordsList;
    private ExecutorService exServ;
    private List<Callable<String>> callables;
    private final int MAX_DISTANCE_BETWEEN_NODES_HEAT = 30;
    private int offsetHeat = 15;

    public HeatMethods(MapControllerInterface mci) {
        this.mci = mci;
        heatService = new Service() {
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
        heatService.setOnSucceeded(s -> plotAfterServiceSucceeds());
        heatService.setOnFailed(s -> setOnServiceFailed(mci));
    }

    @Override
    public ConcurrentHashMap<Coordinate, Double> serviceTaskMethods() {
        try {
            mci.notifyOfGatheringHeatShots();
            startTime = System.nanoTime();
            allShots = new HashMap();
            ArrayList info = new ArrayList();
            info.add(0.0);
            info.add(0.0);
            info.add(0.0);
            for (int x = -250; x < 251; x++) {
                for (int y = -52; y < 400; y++) {
                    allShots.put(new Coordinate(x, y), new ArrayList(info));
                }
            }
            JSONArray jsonArray = mci.chooseJSONArray();
            mci.notifyOfHeatShotsGathered();
            shotCounter = 0;
            Coordinate tempCoord;
            JSONObject eachShot;
            for (int i = 0; i < jsonArray.length(); i++) {
                eachShot = jsonArray.getJSONObject(i);
                if (eachShot.getInt("y") >= 400) {
                    continue;
                }
                shotCounter++;
                tempCoord = new Coordinate(eachShot.getInt("x"), eachShot.getInt("y"));
                allShots.get(tempCoord).set(1, allShots.get(tempCoord).get(1) + 1);
                if (eachShot.getInt("make") == 1) {
                    allShots.get(tempCoord).set(0, allShots.get(tempCoord).get(0) + 1);
                }
            }
            for (Coordinate each : allShots.keySet()) {
                if (allShots.get(each).get(1) != 0) {
                    allShots.get(each).set(2, allShots.get(each).get(0) * 1.0 / allShots.get(each).get(1) * 1.0);
                }
            }
            coordValue = new ConcurrentHashMap();
            coordsList = new ArrayList(300000);
            coordsList.addAll(allShots.keySet());
            createHeatCallables();
            List<Future<String>> futures = exServ.invokeAll(callables);
            boolean allDone = false;
            while (!allDone) {
                for (Future each : futures) {
                    if (!each.isDone()) {
                        allDone = false;
                        Thread.sleep(100);
                        break;
                    } else {
                        allDone = true;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return coordValue;
    }

    @Override
    public Service getService() {
        return heatService;
    }

    @Override
    public void plotAfterServiceSucceeds() {
        mci.plotHeatShots();
        endTime = System.nanoTime();
        System.out.println("HEAT: " + (endTime - startTime) * 1.0 / 1000000000 + " seconds");
    }

    @Override
    public Object getAllShots() {
        return allShots;
    }

    @Override
    public void setOnServiceFailed(MapControllerInterface mci) {
        mci.resetViewOnServiceFailed(heatService);
    }

    private void createHeatCallables() {
        int maxThreads = Runtime.getRuntime().availableProcessors();
//        coordsList = new ArrayList(300000);
        exServ = Executors.newFixedThreadPool(maxThreads);
        Callable<String> heatRunCallable;
        callables = new ArrayList();
        for (int i = 0; i < maxThreads; i++) {
            final int iFinal = i;
            heatRunCallable = () -> {
                double aSum = 0;
                double bSum = 0;
                int p = 2;
                int eachCounter = 0;
                int iFinalThread = iFinal;
                int maxSurroundingCoords = (int) Math.pow(MAX_DISTANCE_BETWEEN_NODES_HEAT * 2, 2);
                int surroundingCounter;
                int x;
                int y;
                int minY;
                int maxY;
                minY = (452 / maxThreads) * iFinalThread - 52;
                maxY = (452 / maxThreads) * (iFinalThread + 1) - 52;
                for (Coordinate each : coordsList) {
                    x = each.getX();
                    y = each.getY();
//                    if (each.getX() % offsetHeat == 0 && each.getY() % offsetHeat == 0 && each.getY() >= (452 / maxThreads) * iFinalThread - 52
//                            && each.getY() < (452 / maxThreads) * (iFinalThread + 1) - 52) {
                    if (x % offsetHeat == 0 && y % offsetHeat == 0 && y >= minY && y < maxY) {
                        aSum = 0;
                        bSum = 0;
                        surroundingCounter = 0;
                        for (Coordinate each2 : coordsList) {
                            if (surroundingCounter >= maxSurroundingCoords) {
                                break;
                            } else if (!each.equals(each2) && mci.getDistance(each, each2) < MAX_DISTANCE_BETWEEN_NODES_HEAT) {
                                surroundingCounter++;
                                aSum = aSum + ((allShots.get(each2).get(1).intValue() * mci.getDistance(each, each2)) / Math.pow(mci.getDistance(each, each2), p));
                                bSum = bSum + (1 / Math.pow(mci.getDistance(each, each2), p));
                                if (allShots.get(each2).get(1).intValue() != 0) {
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
                return "Done running";
            };
            callables.add(heatRunCallable);
        }
    }

    public ConcurrentHashMap<Coordinate, Double> getCoordValue() {
        return coordValue;
    }

    public int getShotCounter() {
        return shotCounter;
    }
}
