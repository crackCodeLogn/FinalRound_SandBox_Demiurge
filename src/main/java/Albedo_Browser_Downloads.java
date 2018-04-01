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

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Vivek
 * @version 1.0
 * @since 30-03-2018
 */
public class Albedo_Browser_Downloads extends Application {

    public static class DownloadRecord {
        int number;
        String title;
        String downloadTimeStamp;
        boolean commitStatus;

        public DownloadRecord() {

        }

        public DownloadRecord(int number, String title, String downloadTimeStamp) {
            this.number = number;
            this.title = title;
            this.downloadTimeStamp = downloadTimeStamp;
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
                    //...
                    System.out.println("fileLocationMapper : "+ fileLocationMapper);
                    System.out.println("Firing up from the button click of commit, src : " + t.getSource());
                    //System.out.println("\tlocalcounter : "+localCounter+", fileLocationMapper val : "+fileLocationMapper.get(localCounter));
                    //the above approach failed...so trying a new format of fileLocationMapper and button
                    //the localCounter is apt only for the time of init the buttons, and not for accessing them afterwards

                    Button caller = (Button) t.getSource();
                    System.out.println("Button mapping : "+(buttonMapper.get(caller)));
                    System.out.println("\tFile loc mapping : "+(fileLocationMapper.get(buttonMapper.get(caller))));
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

    static int localCounter=0;
    static Map<Integer, String> fileLocationMapper = new HashMap<>();
    static Map<Button, Integer> buttonMapper = new HashMap<>();

    public static void fillData() {

        int tr = 1;
        if (data != null) data.clear();

        localCounter = -2;

        DownloadRecord node = new DownloadRecord();
        node.setNumber(1);
        node.setTitle("first.pdf");
        node.setDownloadTimeStamp(String.valueOf(new Timestamp(System.currentTimeMillis())));
        node.setCommitStatus(false);
        //localCounter++;

        if (data == null) data = FXCollections.observableArrayList(node);
        else data.add(node);

        node = new DownloadRecord();
        node.setNumber(2);
        node.setTitle("second.pdf");
        node.setDownloadTimeStamp(String.valueOf(new Timestamp(System.currentTimeMillis())));
        node.setCommitStatus(true);
        //localCounter++;

        data.add(node);

        fileLocationMapper.put(1, "firstLink");
        fileLocationMapper.put(2, "secondLink");
    }

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("DONWLOADS");
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
        table.setItems(data);

        vBox.getChildren().addAll(table);
        primaryStage.setScene(new Scene(vBox));
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                System.out.println("Stage is closing");
                Albedo_Browser.downloadsFrameOpen = false;
            }
        });
    }

    public static void main(String[] args) {
        fillData();
        launch(args);
    }
}
