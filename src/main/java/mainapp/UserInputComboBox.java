/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainapp;

import controllers.SimpleController;
import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
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
    private int otherIndex = 0;
    private BufferedReader br;
    private Thread thread;
    private HashSet<String> hashSet = null;
    private String selection = "";

    public UserInputComboBox(ComboBox combobox, HashSet<String> hashSet, String selection) {
        this.combobox = combobox;
        this.sb = new StringBuilder();
        this.tf = new TextField();
        tf.setEditable(true);
        if (hashSet != null) {
            this.hashSet = hashSet;
        }
        this.selection = selection;
        setOnAction();
    }

    private void searchForFirstInstance(String beginsWith) {
        String string;
//        index = 0;
        for (Object each : ((ComboBoxListViewSkin) combobox.getSkin()).getListView().getItems()) {
            string = (String) each;
            if (string.toLowerCase().startsWith(beginsWith)) {
                index = ((ComboBoxListViewSkin) combobox.getSkin()).getListView().getItems().indexOf(each);
//                ((ComboBoxListViewSkin) combobox.getSkin()).getListView().getSelectionModel().clearSelection();
//                ((ComboBoxListViewSkin) combobox.getSkin()).getListView().getSelectionModel().select(index);
                break;
            }
        }
    }

    public ComboBox getComboBox() {
        return this.combobox;
    }

    private void setOnAction() {
        ((ComboBoxListViewSkin) combobox.getSkin()).getListView().setOnMouseExited((Event tIn) -> {
            combobox.hide();
        });
        this.combobox.setOnMouseClicked((Event tIn) -> {
            System.out.println(this.combobox.getId() + " Clicked");
            lv = ((ComboBoxListViewSkin) this.combobox.getSkin()).getListView();
            lv.setOnMouseReleased(t -> {
//            ((ComboBoxListViewSkin) this.combobox.getSkin()).getPopupContent().setOnMouseClicked((t) -> {
                System.out.println(this.combobox.getId() + " listView Clicked");
                try {
//                index = ((ComboBoxListViewSkin) combobox.getSkin()).getListView().getSelectionModel().getSelectedIndex();
                    if (!this.combobox.getId().equals("playercombo") && this.hashSet != null && !this.hashSet.contains(((ComboBoxListViewSkin) combobox.getSkin()).getListView().getSelectionModel().getSelectedItem().toString())) {
                        this.hashSet.add(((ComboBoxListViewSkin) combobox.getSkin()).getListView().getSelectionModel().getSelectedItem().toString());
                        Main.getSC().addHBoxToSelectionBox(combobox.getId(), ((ComboBoxListViewSkin) combobox.getSkin()).getListView().getSelectionModel().getSelectedItem().toString());
                    } else if (combobox.getId().equals("playercombo")) {
                        selection = (((ComboBoxListViewSkin) combobox.getSkin()).getListView().getSelectionModel().getSelectedItem().toString());
                        combobox.hide();
                    } else if (!combobox.getId().equals("playercombo") && this.hashSet == null) {
                        if (selection.equals("")) {
                            Main.getSC().addHBoxToSelectionBox(combobox.getId(), selection);
                            selection = (((ComboBoxListViewSkin) combobox.getSkin()).getListView().getSelectionModel().getSelectedItem().toString());
                        } else {
                            selection = (((ComboBoxListViewSkin) combobox.getSkin()).getListView().getSelectionModel().getSelectedItem().toString());
                            Main.getSC().addHBoxToSelectionBox(combobox.getId(), selection);
                        }
                        combobox.hide();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                index = ((ComboBoxListViewSkin) combobox.getSkin()).getListView().getSelectionModel().getSelectedIndex();
                System.out.println("index: " + index);
            });
            sb = new StringBuilder();
//            ((ComboBoxListViewSkin) combobox.getSkin()).getListView().getSelectionModel().clearSelection();
//            ((ComboBoxListViewSkin) combobox.getSkin()).getListView().getSelectionModel().select(index);
//            ((ComboBoxListViewSkin) combobox.getSkin()).getListView().setVisible(true);

//            this.combobox.setOnKeyPressed(new EventHandler<KeyEvent>() {
            ((ComboBoxListViewSkin) combobox.getSkin()).getPopupContent().setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    System.out.println(combobox.getId() + " Popup Content Key Pressed");
                    if (sb.length() < 20) {
                        if (((ComboBoxListViewSkin) combobox.getSkin()).getListView().isVisible() && event.getCode().isLetterKey()) {
                            sb.append(event.getText());
                            if (sb.toString().length() > 0) {
                                searchForFirstInstance(sb.toString().toLowerCase());
                                ((ComboBoxListViewSkin) combobox.getSkin()).getListView().scrollTo(index);
                                ((ComboBoxListViewSkin) combobox.getSkin()).getListView().getSelectionModel().select(index);

                            } else if (sb.toString().length() == 0) {
                                ((ComboBoxListViewSkin) combobox.getSkin()).getListView().scrollTo(0);
                            }
                        } else if (((ComboBoxListViewSkin) combobox.getSkin()).getListView().isVisible() && event.getCode().equals(KeyCode.BACK_SPACE)) {
                            try {
                                sb.deleteCharAt(sb.length() - 1);
                            } catch (Exception ex) {

                            }
                            if (sb.toString().length() > 0) {
                                searchForFirstInstance(sb.toString().toLowerCase());
                                ((ComboBoxListViewSkin) combobox.getSkin()).getListView().scrollTo(index);
                            } else if (sb.toString().length() == 0) {
                                ((ComboBoxListViewSkin) combobox.getSkin()).getListView().scrollTo(0);
                            }
                        } else if (((ComboBoxListViewSkin) combobox.getSkin()).getListView().isVisible() && event.getCode() == KeyCode.SPACE) {
                            sb.append(" ");
                        } else if (((ComboBoxListViewSkin) combobox.getSkin()).getListView().isVisible() && event.getCode() == KeyCode.UP) {
//                            ((ComboBoxListViewSkin) combobox.getSkin()).getListView().getSelectionModel().select(((ComboBoxListViewSkin) combobox.getSkin()).getListView().getSelectionModel().getSelectedIndex());
//                            ((ComboBoxListViewSkin) combobox.getSkin()).getListView().getSelectionModel().clearSelection();
//                            ((ComboBoxListViewSkin) combobox.getSkin()).getListView().getSelectionModel().select(index);
                            index--;
                        } else if (((ComboBoxListViewSkin) combobox.getSkin()).getListView().isVisible() && event.getCode() == KeyCode.DOWN) {
//                            ((ComboBoxListViewSkin) combobox.getSkin()).getListView().getSelectionModel().select(((ComboBoxListViewSkin) combobox.getSkin()).getListView().getSelectionModel().getSelectedIndex() );
//                            ((ComboBoxListViewSkin) combobox.getSkin()).getListView().getSelectionModel().clearSelection();
//                            ((ComboBoxListViewSkin) combobox.getSkin()).getListView().getSelectionModel().select(index);
                            index++;
                        } else if (((ComboBoxListViewSkin) combobox.getSkin()).getListView().isVisible() && (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT)) {
                            event.consume();
                        } else if (((ComboBoxListViewSkin) combobox.getSkin()).getListView().isVisible() && event.getCode() == KeyCode.ENTER) {
                            System.out.println(event.getCode());
                            System.out.println(combobox.getId());
                            index = ((ComboBoxListViewSkin) combobox.getSkin()).getListView().getSelectionModel().getSelectedIndex();
                            if (!combobox.getId().equals("playercombo") && hashSet != null && !hashSet.contains(((ComboBoxListViewSkin) combobox.getSkin()).getListView().getSelectionModel().getSelectedItem().toString())) {
                                hashSet.add(((ComboBoxListViewSkin) combobox.getSkin()).getListView().getSelectionModel().getSelectedItem().toString());
                                Main.getSC().addHBoxToSelectionBox(combobox.getId(), ((ComboBoxListViewSkin) combobox.getSkin()).getListView().getSelectionModel().getSelectedItem().toString());
                            } else if (combobox.getId().equals("playercombo")) {
                                selection = (((ComboBoxListViewSkin) combobox.getSkin()).getListView().getSelectionModel().getSelectedItem().toString());
                                combobox.hide();
                            } else if (!combobox.getId().equals("playercombo") && hashSet == null) {
                                if (selection.equals("")) {
                                    Main.getSC().addHBoxToSelectionBox(combobox.getId(), selection);
                                    selection = (((ComboBoxListViewSkin) combobox.getSkin()).getListView().getSelectionModel().getSelectedItem().toString());
                                } else {
                                    selection = (((ComboBoxListViewSkin) combobox.getSkin()).getListView().getSelectionModel().getSelectedItem().toString());
                                    Main.getSC().addHBoxToSelectionBox(combobox.getId(), selection);
                                }
                                combobox.hide();
                            }
                        }

                    }
                    System.out.println("index: " + index);

                }

            });
        });
    }

    public HashSet<String> getHashSet() {
        return this.hashSet;
    }

    public void setSelection(String newSelection) {
        this.selection = newSelection;
    }

    public String getSelection() {
        return this.selection;
    }

}
