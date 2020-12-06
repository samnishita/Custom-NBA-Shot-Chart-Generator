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
package mainapp;

/**
 *
 * @author samnishita
 */
public class Shot {

    private int x;
    private int y;
    private int distance;
    private int make;
    private String shottype;
    private String playtype;

    public Shot(int x, int y, int distance, int make, String shottype, String playtype) {
        this.x = x;
        this.y = y;
        this.distance = distance;
        this.make = make;
        this.shottype = shottype;
        this.playtype = playtype;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getShottype() {
        return shottype;
    }

    public void setShottype(String shottype) {
        this.shottype = shottype;
    }

    public String getPlaytype() {
        return playtype;
    }

    public void setPlaytype(String playtype) {
        this.playtype = playtype;
    }

    public int getMake() {
        return make;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
