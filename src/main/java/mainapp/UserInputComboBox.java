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

import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;
import java.io.BufferedReader;
import java.io.InputStream;
import java.util.HashSet;
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
    private TextField tf;
    private int index = 0;
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

    private void setOnAction() {
        ((ComboBoxListViewSkin) combobox.getSkin()).getListView().setOnMouseExited((Event tIn) -> {
            combobox.hide();
        });
        this.combobox.setOnMouseClicked((Event tIn) -> {
            lv = ((ComboBoxListViewSkin) this.combobox.getSkin()).getListView();
            lv.setOnMouseReleased(t -> {
                try {
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
            });
            sb = new StringBuilder();
            ((ComboBoxListViewSkin) combobox.getSkin()).getPopupContent().setOnKeyPressed((KeyEvent event) -> {
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
                        index--;
                    } else if (((ComboBoxListViewSkin) combobox.getSkin()).getListView().isVisible() && event.getCode() == KeyCode.DOWN) {
                        index++;
                    } else if (((ComboBoxListViewSkin) combobox.getSkin()).getListView().isVisible() && (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT)) {
                        event.consume();
                    } else if (((ComboBoxListViewSkin) combobox.getSkin()).getListView().isVisible() && event.getCode() == KeyCode.ENTER) {
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
