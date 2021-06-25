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
import http.SearchRequester;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import mainapp.Coordinate;
import mainapp.Main;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author samnishita
 */
public class ZoneMethods implements MethodsInterface {

    private Service zoneService;
    private MapControllerInterface mci;
    private HashMap<Integer, Double> allShots, allZoneAverages;
    private HashMap<Integer, Double[]> allZones;
    private long startTime, endTime;
    private SearchRequester searchRequester;

    public ZoneMethods(MapControllerInterface mci) {
        this.mci = mci;
        searchRequester = new SearchRequester();
        zoneService = new Service() {
            @Override
            protected Task<HashMap<Integer, Double>> createTask() {
                return new Task() {
                    @Override
                    protected HashMap<Integer, Double> call() throws Exception {
                        return serviceTaskMethods();
                    }
                };
            }
        };
        zoneService.setOnSucceeded(s -> plotAfterServiceSucceeds());
        zoneService.setOnFailed(s -> setOnServiceFailed(mci));
    }

    @Override
    public HashMap<Integer, Double> serviceTaskMethods() {
        try {
            mci.notifyOfGatheringZoneShots();
            startTime = System.nanoTime();
            JSONArray jsonArray = mci.chooseJSONArray();
            mci.notifyOfZoneShotsGathered();
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        HashMap<Integer, Double> playerZones = new HashMap();
        try {
            for (Integer each : allZones.keySet()) {
                allZones.get(each)[2] = allZones.get(each)[0] * 1.0 / allZones.get(each)[1];
                playerZones.put(each, allZones.get(each)[2]);
            }
            allShots = playerZones;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return allShots;
    }

    @Override
    public Service getService() {
        return zoneService;
    }

    @Override
    public void plotAfterServiceSucceeds() {
        mci.plotZoneShots();
        endTime = System.nanoTime();
        System.out.println("ZONE: " + (endTime - startTime) * 1.0 / 1000000000 + " seconds");
    }

    @Override
    public HashMap<Integer, Double> getAllShots() {
        return allShots;
    }

    @Override
    public void setOnServiceFailed(MapControllerInterface mci) {
        mci.resetViewOnServiceFailed(zoneService);
    }

    private HashMap<Integer, Double> useZoneAverages() throws IOException {
        HashMap<Integer, Double> hashmap = new HashMap();
        JSONArray jsonArray = searchRequester.getZoneAveragesData();
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

    public HashMap<Integer, Double[]> getAllZones() {
        return allZones;
    }

    public HashMap<Integer, Double> getAllZoneAverages() {
        return allZoneAverages;
    }
}
