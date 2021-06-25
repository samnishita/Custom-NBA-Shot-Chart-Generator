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

import controllers.SimpleController;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author samnishita
 */
public class Main extends Application {

    private FXMLLoader loader;
    private static SimpleController sc;
    private String hostName;
    private int portNumber;
    private ResourceBundle reader = null;
    private static Socket socket = null;
//    private static PrintWriter out = null;
    private static BufferedReader in = null;
    private static Scene scene;

    @Override
    public void start(Stage stage) throws Exception {
        try {
            reader = ResourceBundle.getBundle("dbconfig");
            hostName = reader.getString("server.host");
            portNumber = Integer.parseInt(reader.getString("server.port"));
            try {
                socket = new Socket(hostName, portNumber);
//                out = new PrintWriter(socket.getOutputStream(), true);
            } catch (UnknownHostException e) {
                System.err.println("Don't know about host " + hostName);
            } catch (IOException e) {
                System.err.println("Couldn't get I/O for the connection to "
                        + hostName);
            }
            loader = new FXMLLoader(getClass().getResource("/fxml/simplegrid.fxml"));
            Parent root = (Parent) loader.load();
            scene = new Scene(root);
            scene.getStylesheets().add("/css/controllercss.css");
            stage.setScene(scene);
            stage.setTitle("Custom NBA Shot Charts");
            stage.setMinHeight(750.0 / 1.1);
            stage.setMinWidth(900.0 / 1.1);

            stage.show();
            sc = loader.getController();
            GridPane gp = sc.getGridPane();
            VBox vbox = sc.getVBox();
            vbox.maxWidthProperty().bind(scene.widthProperty());
            vbox.maxHeightProperty().bind(scene.heightProperty());
            gp.maxHeightProperty().bind(vbox.maxHeightProperty().multiply(0.9));
            gp.maxWidthProperty().bind(vbox.maxWidthProperty());
            ImageView iv = sc.getIV();
            iv.fitWidthProperty().bind(scene.widthProperty().divide(900.0 / 500));
            iv.fitHeightProperty().bind(scene.heightProperty().divide(750.0 / 470));
            iv.setPreserveRatio(true);
            sc.setAllViewTypeButtonsOnMouseActions();
            sc.populateUnchangingComboBoxes();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

//    public static PrintWriter getPrintWriterOut() throws IOException {
//        out = new PrintWriter(socket.getOutputStream(), true);
//        return out;
//    }

    public static BufferedReader getServerResponse() throws IOException {
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        return in;
    }

    public static void setRoot(String fxml) throws IOException {
        FXMLLoader newLoader = new FXMLLoader(Main.class.getResource("/fxml/" + fxml));
        scene.setRoot(newLoader.load());
    }

    @Override
    public void stop() {
        System.exit(0);
    }

    public static SimpleController getSC() {
        return sc;
    }
}
