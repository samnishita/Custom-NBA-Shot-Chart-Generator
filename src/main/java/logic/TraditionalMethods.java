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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.LinkedHashMap;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import mainapp.MissedShotIcon;
import mainapp.Shot;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author samnishita
 */
public class TraditionalMethods implements MethodsInterface {

    private Service tradService;
    private MapControllerInterface mci;
    private int max = 7500;
    private LinkedHashMap<Shot, Object> allShots;
    private long startTime;
    private long endTime;

    public TraditionalMethods(MapControllerInterface mci) {
        this.mci = mci;
        tradService = new Service() {
            @Override
            protected Task createTask() {
                return new Task() {
                    @Override
                    protected HashMap call() throws Exception {
                        return serviceTaskMethods();
                    }

                };
            }
        };
        tradService.setOnSucceeded(s -> plotAfterServiceSucceeds());
        tradService.setOnFailed(s -> setOnServiceFailed(mci));
    }

    @Override
    public HashMap serviceTaskMethods() {
        startTime = System.nanoTime();
        allShots = new LinkedHashMap();
        try {
            JSONArray jsonArray = mci.chooseJSONArray();
            mci.notifyOfTradShotsGathered();
            max = 7500;
            if (jsonArray.length() < max) {
                max = jsonArray.length();
            }
            JSONObject eachShotRaw;
            Shot shot;
            for (int i = 0; i < max; i++) {
                eachShotRaw = jsonArray.getJSONObject(i);
                shot = new Shot(eachShotRaw.getInt("x"), eachShotRaw.getInt("y"), eachShotRaw.getInt("distance"), eachShotRaw.getInt("make"), eachShotRaw.getString("shottype"), eachShotRaw.getString("playtype"));
                if (eachShotRaw.getInt("make") == 1) {
                    mci.setMadeShot(shot, allShots);
                } else {
                    mci.setMissedShot(shot, allShots);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return allShots;
    }

    public void plotAfterServiceSucceeds() {
        mci.plotTradShots();
        endTime = System.nanoTime();
        System.out.println("TRADITIONAL: " + (endTime - startTime) * 1.0 / 1000000000 + " seconds");
    }

    public void setOnServiceFailed(MapControllerInterface mci) {
        mci.resetViewOnServiceFailed(tradService);
    }

    @Override
    public Service getService() {
        return this.tradService;
    }
    
    public LinkedHashMap<Shot, Object> getAllShots(){
        return allShots;
    }
}
