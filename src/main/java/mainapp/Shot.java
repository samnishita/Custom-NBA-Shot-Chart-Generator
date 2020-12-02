/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
