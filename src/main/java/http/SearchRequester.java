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
package http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Scanner;
import mainapp.Main;
import mainapp.Search;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

/**
 *
 * @author samnishita
 */
public class SearchRequester {
    
    private final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SearchRequester.class);
    
    private JSONObject doSimpleSearch(String path) {
        StringBuilder content = new StringBuilder();
        try {
            ResourceBundle reader = ResourceBundle.getBundle("dbconfig");
            //String host = reader.getString("server.host")+":"+reader.getString("server.port");
            String host = reader.getString("server.host");
            URL url = new URL("https://" + host + path.replace(" ", "%20"));
            LOGGER.info("Requesting " + url.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Host", host);
            InputStream response = new URL(url.toString()).openStream();
            try (Scanner scanner = new Scanner(response)) {
                String responseBody = scanner.useDelimiter("\\A").next();
                content.append(responseBody);
            }
            LOGGER.info("Response from " + url + " : " + content.toString());
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return new JSONObject(content.toString());
    }
    
    private StringBuilder addParametersToUrlPath(StringBuilder sb, Map<String, String> inputMap) {
        int counter = 0;
        for (Map.Entry<String, String> param : inputMap.entrySet()) {
            try {
                if (counter != 0) {
                    sb.append('&');
                }
                sb.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                sb.append('=');
                sb.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                counter++;
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
        }
        return sb;
    }
    
    public JSONArray getInitData() throws IOException {
//        JSONObject jsonObjOut = new JSONObject();
//        jsonObjOut.put("selector", "init");
//        Main.getPrintWriterOut().println(jsonObjOut.toString());
//        return new JSONArray(Main.getServerResponse().readLine());
//        JSONObject test = simpleSearchRequester.doSimpleSearch("/shots_request?year=2020-21&seasontype=Regular%20Season&simplesearch=true&playerid=203932&playerlastname=Gordon&playerfirstname=Aaron");
//        System.out.println("TEST: "+ test.toString());
        HashMap<String, String> inputMap = new HashMap<>();
        inputMap.put("init", "true");
        StringBuilder sb = addParametersToUrlPath(new StringBuilder("/shots_request?"), inputMap);
        JSONObject response = doSimpleSearch(sb.toString());
        return new JSONArray(response.get("init").toString());
    }
    
    public JSONArray getInitAllPlayersData() throws IOException {
//        JSONObject jsonObjOut = new JSONObject();
//        jsonObjOut.put("selector", "initallplayers");
//        Main.getPrintWriterOut().println(jsonObjOut.toString());
//        return new JSONArray(Main.getServerResponse().readLine());
        HashMap<String, String> inputMap = new HashMap<>();
        inputMap.put("initallplayers", "true");
        StringBuilder sb = addParametersToUrlPath(new StringBuilder("/shots_request?"), inputMap);
        JSONObject response = doSimpleSearch(sb.toString());
        return new JSONArray(response.get("initallplayers").toString());
    }
    
    public JSONArray getActivePlayersData(String yearComboValue) throws IOException {
//        JSONObject jsonObjOut = new JSONObject();
//        jsonObjOut.put("selector", "activeplayers");
//        jsonObjOut.put("year", this.yearcombo.getValue().toString());
//        Main.getPrintWriterOut().println(jsonObjOut.toString());
//        return new JSONArray(Main.getServerResponse().readLine());
        HashMap<String, String> inputMap = new HashMap<>();
        inputMap.put("activeplayers", yearComboValue);
        StringBuilder sb = addParametersToUrlPath(new StringBuilder("/shots_request?"), inputMap);
        JSONObject response = doSimpleSearch(sb.toString());
        return new JSONArray(response.get("activeplayers").toString());
    }
    
    public JSONArray getSeasonsData(String yearComboValue, String playerId, String playerFirstName, String playerLastName) throws IOException {
//        JSONObject jsonObjOut = new JSONObject();
//        jsonObjOut.put("selector", "seasons");
//        jsonObjOut.put("year", this.yearcombo.getValue().toString());
//        jsonObjOut.put("playerid", nameHash.get(this.playercombo.getValue().toString())[0]);
//        jsonObjOut.put("playerfirstname", nameHash.get(this.playercombo.getValue().toString())[1]);
//        jsonObjOut.put("playerlastname", nameHash.get(this.playercombo.getValue().toString())[2]);
//        Main.getPrintWriterOut().println(jsonObjOut.toString());
//        return new JSONArray(Main.getServerResponse().readLine());
        HashMap<String, String> inputMap = new HashMap<>();
        inputMap.put("singleseasonactivity", "true");
        inputMap.put("year", yearComboValue);
        inputMap.put("playerid", playerId);
        inputMap.put("playerfirstname", playerFirstName);
        inputMap.put("playerlastname", playerLastName);
        StringBuilder sb = addParametersToUrlPath(new StringBuilder("/shots_request?"), inputMap);
        JSONObject response = doSimpleSearch(sb.toString());
        return new JSONArray(response.get("singleseason").toString());
    }
    
    public JSONArray getShotTypesData() throws IOException {
//        JSONObject jsonObjOut = new JSONObject();
//        jsonObjOut.put("selector", "shottypes");
//        Main.getPrintWriterOut().println(jsonObjOut.toString());
//        return new JSONArray(Main.getServerResponse().readLine());
        HashMap<String, String> inputMap = new HashMap<>();
        inputMap.put("shottypes", "true");
        StringBuilder sb = addParametersToUrlPath(new StringBuilder("/shots_request?"), inputMap);
        JSONObject response = doSimpleSearch(sb.toString());
        return new JSONArray(response.get("shottypes").toString());
    }
    
    public JSONArray getGridAveragesData() throws IOException {
//        JSONObject jsonObjOut = new JSONObject();
//        jsonObjOut.put("selector", "gridaverages");
//        Main.getPrintWriterOut().println(jsonObjOut.toString());
//        return new JSONArray(Main.getServerResponse().readLine());
        HashMap<String, String> inputMap = new HashMap<>();
        inputMap.put("gridaverages", "true");
        StringBuilder sb = addParametersToUrlPath(new StringBuilder("/shots_request?"), inputMap);
        JSONObject response = doSimpleSearch(sb.toString());
        return new JSONArray(response.get("gridaverages").toString());
    }
    
    public JSONArray getZoneAveragesData() throws IOException {
//        JSONObject jsonObjOut = new JSONObject();
//        jsonObjOut.put("selector", "zoneaverages");
//        Main.getPrintWriterOut().println(jsonObjOut.toString());
//        return new JSONArray(Main.getServerResponse().readLine());
        HashMap<String, String> inputMap = new HashMap<>();
        inputMap.put("zoneaverages", "true");
        StringBuilder sb = addParametersToUrlPath(new StringBuilder("/shots_request?"), inputMap);
        JSONObject response = doSimpleSearch(sb.toString());
        return new JSONArray(response.get("zoneaverages").toString());
    }
    
    public JSONArray getSimpleShotData(String yearComboValue, String playerId, String playerFirstName, String playerLastName, String seasonType) throws IOException {
//        JSONObject jsonObjOut = new JSONObject();
//        jsonObjOut.put("selector", "simple" + currentSimpleSearch.toString().toLowerCase());
//        jsonObjOut.put("year", this.yearcombo.getValue().toString());
//        jsonObjOut.put("playername", nameHash.get(this.playercombo.getValue().toString()));
//        jsonObjOut.put("seasontype", this.seasoncombo.getValue().toString());
//        previousSimpleSearchJSON = jsonObjOut;
//        Main.getPrintWriterOut().println(jsonObjOut.toString());
//        return new JSONArray(Main.getServerResponse().readLine());HashMap<String, String> inputMap = new HashMap<>();

        HashMap<String, String> inputMap = new HashMap<>();
        inputMap.put("simplesearch", "true");
        inputMap.put("year", yearComboValue);
        inputMap.put("playerid", playerId);
        inputMap.put("playerfirstname", playerFirstName);
        inputMap.put("playerlastname", playerLastName);
        inputMap.put("seasontype", seasonType);
        StringBuilder sb = addParametersToUrlPath(new StringBuilder("/shots_request?"), inputMap);
        JSONObject response = doSimpleSearch(sb.toString());
        return new JSONArray(response.get("simplesearch").toString());
    }
    
    public JSONArray createAdvancedJSONOutput(Map<String, ArrayList<String>> inputMap) throws IOException, Exception {
//        JSONObject obj = createJsonObjectOutput();
//        previousAdvancedSearchJSON = obj;
//        JSONObject newObj = createJsonObjectOutput();
//        newObj.put("selector", "advanced" + searchTypeSelector.toString().toLowerCase());
//        Main.getPrintWriterOut().println(newObj.toString());
//        return new JSONArray(Main.getServerResponse().readLine());
        Main.getSC().setPreviousAdvancedSearchJSON(Main.getSC().createJsonObjectOutput());
        StringBuilder sb = new StringBuilder("/shots_request_advanced?");
        int counter = 0;
        ArrayList<String> eachList;
        for (String eachKey : inputMap.keySet()) {
            eachList = inputMap.get(eachKey);
            for (String eachValue : eachList) {
                if (counter != 0) {
                    sb.append('&');
                }
                sb.append(URLEncoder.encode(eachKey, "UTF-8"));
                sb.append('=');
                sb.append(URLEncoder.encode(eachValue, "UTF-8"));
                counter++;
            }
        }
        JSONObject response = doSimpleSearch(sb.toString());
        return new JSONArray(response.get("advancedsearch").toString());
    }
    
}
