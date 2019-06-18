package com.vv.export.sandbox;

import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Vivek
 * @version 1.0
 * @since 30-03-2018
 */
public class AlbedoBrowserDownloads extends Application {

    public static class DownloadRecord {
        int number;
        String title;
        String downloadTimeStamp;
        long downloadTimeStampLong;
        boolean commitStatus;

        public DownloadRecord() {
        }

        public DownloadRecord(int number, String title, String downloadTimeStamp, long downloadTimeStampLong) {
            this.number = number;
            this.title = title;
            this.downloadTimeStamp = downloadTimeStamp;
            this.downloadTimeStampLong = downloadTimeStampLong;
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

        public String getDownloadTimeStamp() {
            return downloadTimeStamp;
        }

        public void setDownloadTimeStamp(String downloadTimeStamp) {
            this.downloadTimeStamp = downloadTimeStamp;
        }

        public boolean isCommitStatus() {
            return commitStatus;
        }

        public void setCommitStatus(boolean commitStatus) {
            this.commitStatus = commitStatus;
        }

        public long getDownloadTimeStampLong() {
            return downloadTimeStampLong;
        }

        public void setDownloadTimeStampLong(long downloadTimeStampLong) {
            this.downloadTimeStampLong = downloadTimeStampLong;
        }
    }

    private class ButtonCell extends TableCell<DownloadRecord, Boolean> {
        final Button cellButton = new Button();

        ButtonCell() {
            localCounter++;
            //cellButton.setText("Commit"+localCounter);
            buttonMapper.put(cellButton, localCounter);
            cellButton.setText("Commit");
            cellButton.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent t) {
                    // do something when button clicked
                    System.out.println("fileLocationMapper : " + fileLocationMapper);
                    System.out.println("Firing up from the button click of commit, src : " + t.getSource());
                    //System.out.println("\tlocalcounter : "+localCounter+", fileLocationMapper val : "+fileLocationMapper.get(localCounter));
                    //the above approach failed...so trying a new format of fileLocationMapper and button
                    //the localCounter is apt only for the time of init the buttons, and not for accessing them afterwards

                    Button caller = (Button) t.getSource();
                    System.out.println("Button mapping : " + (buttonMapper.get(caller)));
                    File srcFile = fileLocationMapper.get(buttonMapper.get(caller));
                    System.out.println("\tFile loc mapping : " + srcFile.getName() + " --> " + srcFile.toPath());

                    System.out.println("Firing the file commiting ~ transferring");
                    cellButton.setDisable(true);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Files.copy(srcFile.toPath(), Paths.get(downloadFileDirectory_OutBoxPath + srcFile.getName()), StandardCopyOption.REPLACE_EXISTING);
                                srcFile.delete();
                                System.out.println("Completed file transfer and deleted the source file!");
                            } catch (IOException e) {
                                e.printStackTrace();
                                System.out.println("FAILURE IN COPYING / DELETING THE FILE WHICH REQUESTED COMMIT");
                            }
                        }
                    }).start();
                }
            });
        }

        //Display button if the row is not empty
        @Override
        protected void updateItem(Boolean t, boolean empty) {
            super.updateItem(t, empty);
            if (!empty) {
                setGraphic(cellButton);
            }
        }
    }

    private static ObservableList<DownloadRecord> data = null;
    //FXCollections.observableArrayList(new DownloadRecord(1, "Titleeeeeeeeeeeeeeeeeeeeeeeeeee", "Visited Time"));

    static int localCounter = 0;
    static Map<Integer, File> fileLocationMapper = new HashMap<>();
    static Map<Button, Integer> buttonMapper = new HashMap<>();

    private static String downloadFileDirectoryPath = "/home/v2k/theRange/sandbox_target/tmp/";
    private static String downloadFileDirectory_OutBoxPath = "/home/v2k/theRange/sandbox_outbox/";

    public static void fillData() {

        int tr = 1;
        if (data != null) data.clear();

        localCounter = -2;

        File file = new File(downloadFileDirectoryPath);
        File[] filesList = file.listFiles();

        for (File f1 : filesList) {
            System.out.println("File name --> " + f1);

            DownloadRecord node = new DownloadRecord();
            node.setNumber(tr);
            node.setTitle(f1.getName());

            try {
                //Path localFile = Paths.get("resources/RESIZED_ic_refresh_black_48dp_2x.png");
                Path localFile = Paths.get(f1.getPath());
                BasicFileAttributes attributes = Files.readAttributes(localFile, BasicFileAttributes.class);
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

            if (data == null) data = FXCollections.observableArrayList(node);
            else data.add(node);

            //mapping the number to the file downloaded
            fileLocationMapper.put(tr, f1);

            tr++;
        }
    }

    public static void clearDownloads() {
        //wiping out the downloads folder
        File file = new File(downloadFileDirectoryPath);
        File[] filesList = file.listFiles();

        int i = 0;
        //File filesListArray[] = new File[filesList.length];
        for (File f1 : filesList) {
            System.out.println("\t\tFile " + (++i) + " to be deleted : " + f1);
            f1.delete();
            //filesListArray[i++] = f1;
        }
        /*
        for(i=0;i<filesListArray.length;i++){
            filesListArray[i].delete();
        }
        */
        System.out.println("Exiting the clear Downloads function");
    }

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("DOWNLOADS");
        primaryStage.setWidth(800);
        VBox vBox = new VBox();

        TableView table = new TableView();
        table.setEditable(false);

        TableColumn recordNumberCol = new TableColumn("Record number");
        recordNumberCol.setCellValueFactory(new PropertyValueFactory<>("number"));
        TableColumn siteNameCol = new TableColumn("Site name");
        siteNameCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        TableColumn lastVisitedTimeCol = new TableColumn("Time of visit");
        lastVisitedTimeCol.setCellValueFactory(new PropertyValueFactory<>("downloadTimeStamp"));

        TableColumn commitButtonCol = new TableColumn("Commit Status");
        commitButtonCol.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<DownloadRecord, Boolean>,
                        ObservableValue<Boolean>>() {
                    @Override
                    public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<DownloadRecord, Boolean> p) {
                        return new SimpleBooleanProperty(p.getValue() != null);
                    }
                });
        commitButtonCol.setCellFactory(
                new Callback<TableColumn<DownloadRecord, Boolean>, TableCell<DownloadRecord, Boolean>>() {
                    @Override
                    public TableCell<DownloadRecord, Boolean> call(TableColumn<DownloadRecord, Boolean> p) {

                        return new ButtonCell();
                    }

                });

        table.getColumns().addAll(recordNumberCol, siteNameCol, lastVisitedTimeCol, commitButtonCol);

        try {
            Collections.sort(data, new Comparator<DownloadRecord>() {
                @Override
                public int compare(DownloadRecord o1, DownloadRecord o2) {
                    return (int) (o2.getDownloadTimeStampLong() - o1.getDownloadTimeStampLong());
                }
            });
        } catch (Exception e1) {
            System.out.println("Nothing in the downloads list...");
        }

        int tr = data.size();
        for (DownloadRecord record : data) {
            record.setNumber(tr--);
        }

        table.setItems(data);

        vBox.getChildren().addAll(table);
        primaryStage.setScene(new Scene(vBox));
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                System.out.println("Stage is closing");
                AlbedoBrowser.downloadsFrameOpen = false;
            }
        });
    }

    public static void main(String[] args) {
        fillData();
        launch(args);
    }
}
