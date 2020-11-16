/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainapp;

import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author samnishita
 */
public class UserInputComboBox {

    private ComboBox combobox;
    private StringBuilder sb;
    private ListView lv;
    private static String alphs = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private TextField tf;
    private InputStream in;
    private int index = 0;
    private BufferedReader br;
    private Thread thread;

    public UserInputComboBox(ComboBox combobox) {
        this.combobox = combobox;
        this.sb = new StringBuilder();
        this.tf = new TextField();
        tf.setEditable(true);
        this.combobox.setOnKeyPressed((Event t) -> {

        });

        ((ComboBoxListViewSkin) combobox.getSkin()).getListView().setOnMouseExited((Event tIn) -> {
            combobox.hide();
        });
        this.combobox.setOnMouseClicked((Event t) -> {
            sb = new StringBuilder();
//            this.combobox.setOnKeyPressed(new EventHandler<KeyEvent>() {
            ((ComboBoxListViewSkin) combobox.getSkin()).getPopupContent().setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    if (sb.length() < 20) {

                        if (((ComboBoxListViewSkin) combobox.getSkin()).getListView().isVisible() && event.getCode().isLetterKey()) {
                            sb.append(event.getText());
                        } else if (((ComboBoxListViewSkin) combobox.getSkin()).getListView().isVisible() && event.getCode().equals(KeyCode.BACK_SPACE)) {
                            try {
                                sb.deleteCharAt(sb.length() - 1);
                            } catch (Exception ex) {

                            }
                        } else if (((ComboBoxListViewSkin) combobox.getSkin()).getListView().isVisible() && event.getCode() == KeyCode.SPACE) {
                            sb.append(" ");
                        }
                        if (sb.toString().length() > 0) {
                            searchForFirstInstance(sb.toString().toLowerCase());
                            ((ComboBoxListViewSkin) combobox.getSkin()).getListView().scrollTo(index);
                        } else if (sb.toString().length() == 0) {
                            ((ComboBoxListViewSkin) combobox.getSkin()).getListView().scrollTo(0);

                        }
                    }
                }
            });
        });
    }

    private void searchForFirstInstance(String beginsWith) {
        String string;
//        index = 0;
        for (Object each : ((ComboBoxListViewSkin) combobox.getSkin()).getListView().getItems()) {
            string = (String) each;
            if (string.toLowerCase().startsWith(beginsWith)) {
                index = ((ComboBoxListViewSkin) combobox.getSkin()).getListView().getItems().indexOf(each);
                break;
            }
        }
    }

    public ComboBox getComboBox() {
        return this.combobox;
    }

}
