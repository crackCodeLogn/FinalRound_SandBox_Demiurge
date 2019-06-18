package com.vv.export.sandbox.menu;

import com.vv.export.sandbox.AlbedoBrowser;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author Vivek
 * @version 1.0
 * @since 29-03-2018
 */
public class AlbedoBrowserHistory extends Application {

    public static void fillData(WebEngine webEngine) {
        final ObservableList<WebHistory.Entry> list = webEngine.getHistory().getEntries();
        final AtomicInteger recordNumber = new AtomicInteger(1);
        if (Objects.nonNull(data)) data.clear();

        list.forEach(entry -> {
            System.out.println("Entry " + recordNumber.get() + " --> " + entry.getTitle() + " :: " + entry.getUrl() + " :: " + entry.getLastVisitedDate());

            final HistoryRecord node = new HistoryRecord();
            node.setNumber(recordNumber.getAndIncrement());
            try {
                node.setTitle(entry.getTitle());
            } catch (NullPointerException e1) {
                node.setTitle("null");
            }
            try {
                node.setVisitTimeStamp(entry.getLastVisitedDate().toString());
            } catch (NullPointerException e1) {
                node.setVisitTimeStamp("null");
            }

            if (Objects.isNull(data)) {
                data = FXCollections.observableArrayList(node);
            } else {
                data.add(node);
            }
        });
    }

    private static ObservableList<HistoryRecord> data = null;
    //FXCollections.observableArrayList(new DownloadRecord(1, "Titleeeeeeeeeeeeeeeeeeeeeeeeeee", "Visited Time"));

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("HISTORY");
        primaryStage.setWidth(800);
        final VBox vBox = new VBox();

        final TableView table = new TableView();
        table.setEditable(false);

        final TableColumn recordNumberCol = new TableColumn("Record number");
        recordNumberCol.setCellValueFactory(new PropertyValueFactory<>("number"));
        final TableColumn siteNameCol = new TableColumn("Site name");
        siteNameCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        final TableColumn lastVisitedTimeCol = new TableColumn("Time of visit");
        lastVisitedTimeCol.setCellValueFactory(new PropertyValueFactory<>("visitTimeStamp"));

        table.getColumns().addAll(recordNumberCol, siteNameCol, lastVisitedTimeCol);
        table.setItems(data);

        vBox.getChildren().addAll(table);
        primaryStage.setScene(new Scene(vBox));
        primaryStage.show();

        primaryStage.setOnCloseRequest(windowEvent -> {
            System.out.println("Stage is closing");
            AlbedoBrowser.historyFrameOpen = false;
        });
    }

    public static class HistoryRecord {
        int number;
        String title;
        String visitTimeStamp;

        HistoryRecord() {
        }

        void setNumber(int number) {
            this.number = number;
        }

        void setTitle(String title) {
            this.title = title;
        }

        void setVisitTimeStamp(String visitTimeStamp) {
            this.visitTimeStamp = visitTimeStamp;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
