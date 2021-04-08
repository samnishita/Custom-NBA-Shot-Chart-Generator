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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import javafx.concurrent.Service;
import mainapp.Coordinate;
import mainapp.Search;
import mainapp.Shot;
import org.json.JSONArray;

/**
 *
 * @author samnishita
 */
public interface MapControllerInterface {

    void resetViewOnServiceFailed(Service service);

    JSONArray chooseJSONArray();

    void setMadeShot(Shot shot, HashMap hashmap);

    void setMissedShot(Shot shot, HashMap hashmap);

    void plotTradShots();

    void plotGridShots();

    void plotHeatShots();

    void plotZoneShots();

    void notifyOfGatheringTradShots();

    void notifyOfGatheringGridShots();

    void notifyOfGatheringHeatShots();

    void notifyOfGatheringZoneShots();

    void notifyOfTradShotsGathered();

    void notifyOfGridShotsGathered();

    void notifyOfHeatShotsGathered();

    void notifyOfZoneShotsGathered();

    double getWidth();

    double getHeight();

    double getDistance(Coordinate coordOrig, Coordinate coordI);
    }
