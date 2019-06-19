package com.vv.export.sandbox.menu;

import com.vv.export.sandbox.AlbedoBrowser;
import com.vv.export.sandbox.menu.core.DownloadRecord;
import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * @author Vivek
 * @version 1.0
 * @since 30-03-2018
 */
public class AlbedoBrowserDownloads extends Application {

    private static int localCounter = 0;
    private static Map<Integer, File> fileLocationMapper = new HashMap<>();

    private static ObservableList<DownloadRecord> data = null;
    //FXCollections.observableArrayList(new DownloadRecord(1, "Titleeeeeeeeeeeeeeeeeeeeeeeeeee", "Visited Time"));
    private static Map<Button, Integer> buttonMapper = new HashMap<>();

    public static void fillData() {

        int tr = 1;
        if (Objects.nonNull(data)) data.clear();

        localCounter = -2;

        final File file = new File(downloadFileDirectoryPath);
        final File[] filesList = file.listFiles();

        for (File f1 : filesList) {
            System.out.println("File name --> " + f1);

            final DownloadRecord node = new DownloadRecord();
            node.setNumber(tr);
            node.setTitle(f1.getName());

            try {
                //Path localFile = Paths.get("resources/RESIZED_ic_refresh_black_48dp_2x.png");
                final Path localFile = Paths.get(f1.getPath());
                final BasicFileAttributes attributes = Files.readAttributes(localFile, BasicFileAttributes.class);
                System.out.println("File creation time : " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format((attributes.lastModifiedTime().toMillis())));
                System.out.println("file ccr : " + attributes.lastModifiedTime().toMillis());

                node.setDownloadTimeStamp(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format((attributes.lastModifiedTime().toMillis())));
                node.setDownloadTimeStampLong(attributes.lastModifiedTime().toMillis());
            } catch (Exception e) {
                System.out.println("Error encountered while reading file attribute...\n\tException" + e);
                node.setDownloadTimeStamp("N.A.");
                node.setDownloadTimeStampLong(0);
            }

            node.setCommitStatus(false);

            if (Objects.isNull(data)) data = FXCollections.observableArrayList(node);
            else data.add(node);

            //mapping the number to the file downloaded
            fileLocationMapper.put(tr, f1);
            tr++;
        }
    }

    public static void clearDownloads() {
        //wiping out the downloads folder
        final File file = new File(downloadFileDirectoryPath);
        final File[] filesList = file.listFiles();

        int i = 0;
        for (File f1 : filesList) {
            System.out.println("\t\tFile " + (++i) + " to be deleted : " + f1);
            f1.delete();
        }
        System.out.println("Exiting the clear Downloads function");
    }

    private static String downloadFileDirectoryPath = "/home/v2k/theRange/sandbox_target/tmp/";
    private static String downloadFileDirectory_OutBoxPath = "/home/v2k/theRange/sandbox_outbox/";

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("DOWNLOADS");
        primaryStage.setWidth(800);
        final VBox vBox = new VBox();

        final TableView table = new TableView();
        table.setEditable(false);

        final TableColumn recordNumberCol = new TableColumn("Record number");
        recordNumberCol.setCellValueFactory(new PropertyValueFactory<>("number"));
        final TableColumn siteNameCol = new TableColumn("Site name");
        siteNameCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        final TableColumn lastVisitedTimeCol = new TableColumn("Time of visit");
        lastVisitedTimeCol.setCellValueFactory(new PropertyValueFactory<>("downloadTimeStamp"));

        final TableColumn commitButtonCol = new TableColumn("Commit Status");
        commitButtonCol.setCellValueFactory(
                (Callback<TableColumn.CellDataFeatures<DownloadRecord, Boolean>, ObservableValue<Boolean>>) p -> new SimpleBooleanProperty(p.getValue() != null));
        commitButtonCol.setCellFactory(
                (Callback<TableColumn<DownloadRecord, Boolean>, TableCell<DownloadRecord, Boolean>>) p -> new ButtonCell());

        table.getColumns().addAll(recordNumberCol, siteNameCol, lastVisitedTimeCol, commitButtonCol);

        try {
            Collections.sort(data, (o1, o2) -> (int) (o2.getDownloadTimeStampLong() - o1.getDownloadTimeStampLong()));
            int tr = data.size();
            for (DownloadRecord record : data) {
                record.setNumber(tr--);
            }
        } catch (Exception e1) {
            System.out.println("Nothing in the downloads list...");
        }

        table.setItems(data);

        vBox.getChildren().addAll(table);
        primaryStage.setScene(new Scene(vBox));
        primaryStage.show();

        primaryStage.setOnCloseRequest(windowEvent -> {
            System.out.println("Stage is closing");
            AlbedoBrowser.downloadsFrameOpen = false;
        });
    }

    private class ButtonCell extends TableCell<DownloadRecord, Boolean> {
        final Button cellButton = new Button();

        ButtonCell() {
            localCounter++;
            //cellButton.setText("Commit"+localCounter);
            buttonMapper.put(cellButton, localCounter);
            cellButton.setText("Commit");
            cellButton.setOnAction(actionEvent -> {
                // do something when button clicked
                System.out.println("fileLocationMapper : " + fileLocationMapper);
                System.out.println("Firing up from the button click of commit, src : " + actionEvent.getSource());
                //System.out.println("\tlocalcounter : "+localCounter+", fileLocationMapper val : "+fileLocationMapper.get(localCounter));
                //the above approach failed...so trying a new format of fileLocationMapper and button
                //the localCounter is apt only for the time of init the buttons, and not for accessing them afterwards

                final Button caller = (Button) actionEvent.getSource();
                System.out.println("Button mapping : " + (buttonMapper.get(caller)));
                final File srcFile = fileLocationMapper.get(buttonMapper.get(caller));
                System.out.println("\tFile loc mapping : " + srcFile.getName() + " --> " + srcFile.toPath());

                System.out.println("Firing the file committing ~ transferring");
                cellButton.setDisable(true);

                new Thread(() -> {
                    try {
                        Files.copy(srcFile.toPath(), Paths.get(downloadFileDirectory_OutBoxPath + srcFile.getName()), StandardCopyOption.REPLACE_EXISTING);
                        srcFile.delete();
                        System.out.println("Completed file transfer and deleted the source file!");
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("FAILURE IN COPYING / DELETING THE FILE WHICH REQUESTED COMMIT");
                    }
                }).start();
            });
        }

        //Display button if the row is not empty
        @Override
        protected void updateItem(Boolean t, boolean empty) {
            super.updateItem(t, empty);
            if (!empty) setGraphic(cellButton);
        }
    }

    public static void main(String[] args) {
        fillData();
        launch(args);
    }
}
