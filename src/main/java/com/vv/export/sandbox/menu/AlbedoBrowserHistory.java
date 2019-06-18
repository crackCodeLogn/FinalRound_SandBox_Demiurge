package com.vv.export.sandbox.menu;

import com.vv.export.sandbox.AlbedoBrowser;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


/**
 * @author Vivek
 * @version 1.0
 * @since 29-03-2018
 */
public class AlbedoBrowserHistory extends Application {

    public static class HistoryRecord {
        int number;
        String title;
        String visitTimeStamp;

        public HistoryRecord() {
        }

        public HistoryRecord(int number, String title, String visitTimeStamp) {
            this.number = number;
            this.title = title;
            this.visitTimeStamp = visitTimeStamp;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getVisitTimeStamp() {
            return visitTimeStamp;
        }

        public void setVisitTimeStamp(String visitTimeStamp) {
            this.visitTimeStamp = visitTimeStamp;
        }
    }

    private static ObservableList<HistoryRecord> data = null;
    //FXCollections.observableArrayList(new DownloadRecord(1, "Titleeeeeeeeeeeeeeeeeeeeeeeeeee", "Visited Time"));

    public static void fillData(WebEngine webEngine) {
        WebHistory history = webEngine.getHistory();
        ObservableList<WebHistory.Entry> list = history.getEntries();
        int tr = 1;
        if(data!=null) data.clear();

        for (WebHistory.Entry entry : list) {
            System.out.println("Entry " + tr + " --> " + entry.getTitle() + " :: " + entry.getUrl() + " :: " + entry.getLastVisitedDate());

            HistoryRecord node = new HistoryRecord();
            node.setNumber(tr);
            try {
                node.setTitle(entry.getTitle());
            } catch (NullPointerException e1){
                node.setTitle("null");
            }
            try {
                node.setVisitTimeStamp(entry.getLastVisitedDate().toString());
            } catch (NullPointerException e1){
                node.setVisitTimeStamp("null");
            }

            if (data == null) {
                data = FXCollections.observableArrayList(node);
            } else {
                data.add(node);
            }

            tr++;
        }
    }

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("HISTORY");
        primaryStage.setWidth(800);
        VBox vBox = new VBox();

        TableView table = new TableView();
        table.setEditable(false);

        TableColumn recordNumberCol = new TableColumn("Record number");
        recordNumberCol.setCellValueFactory(new PropertyValueFactory<>("number"));
        TableColumn siteNameCol = new TableColumn("Site name");
        siteNameCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        TableColumn lastVisitedTimeCol = new TableColumn("Time of visit");
        lastVisitedTimeCol.setCellValueFactory(new PropertyValueFactory<>("visitTimeStamp"));

        table.getColumns().addAll(recordNumberCol, siteNameCol, lastVisitedTimeCol);
        table.setItems(data);

        vBox.getChildren().addAll(table);
        primaryStage.setScene(new Scene(vBox));
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                System.out.println("Stage is closing");
                AlbedoBrowser.historyFrameOpen = false;
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
