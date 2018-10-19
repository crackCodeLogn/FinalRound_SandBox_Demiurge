package com.vv.export.sandbox;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static com.vv.export.sandbox.Utility.*;

/**
 * @author Vivek
 * @version 1.0
 * @lastMod 14-10-2018
 * <p>
 * Details:-
 * WebView does not support plugins like flash / adobe
 * @since 28-03-2018
 */
public class AlbedoBrowser extends Application implements EventHandler<ActionEvent> {

    protected static boolean historyFrameOpen = false;
    protected static boolean downloadsFrameOpen = false;
    protected static boolean menuFrameOpen = false;
    protected static WebView browser;
    protected static WebEngine webEngine;
    protected static Properties props = new Properties();
    private static TextField textFieldStatusDisplay = new TextField("---");
    private static String currentURL = "";
    private Button buttonBack;
    private Button buttonForward;
    private Button buttonRefresh;
    private Button buttonHome;
    private Button buttonAbort;
    private Button buttonMenu;

    private static List<String> downloadFormats = new ArrayList<>();
    private static List<String> siteExtensions = new ArrayList<>();

    protected static void setUpProps() {
        try (FileReader fileReader = new FileReader(BASE_FILEPATH + FILEPATH_PROPERTIES)) {
            props.load(fileReader);
        } catch (FileNotFoundException e) {
            System.out.println("FNF exception encountered!");
        } catch (IOException e) {
            System.out.println("Error while reading the properties file. Error : " + e);
        }

        String formats[] = getProperty(DOWNLOAD_FORMATS, "").split(",");
        for (int i = 0; i < formats.length; i++) {
            formats[i] = String.format(FORMAT_STRING_EXTENSION, formats[i]).trim();
        }
        downloadFormats = Arrays.asList(formats);

        formats = getProperty(HTTP_SITE_EXTENSIONS, "").split(",");
        for (int i = 0; i < formats.length; i++) {
            formats[i] = String.format(FORMAT_STRING_EXTENSION, formats[i]).trim();
        }
        siteExtensions = Arrays.asList(formats);
    }

    protected static String getProperty(String key, String def) {
        if (props.containsKey(key)) return props.getProperty(key);
        return def;
    }

    private static void printHistory(String message, WebEngine webEngine) {
        System.out.println(message);
        WebHistory history = webEngine.getHistory();
        ObservableList<WebHistory.Entry> list = history.getEntries();
        int tr = 1;
        for (WebHistory.Entry entry : list) {
            System.out.println(String.format("Entry %d --> %s :: %s %s", tr, entry.getTitle(), entry.getUrl(), entry.getLastVisitedDate()));
            tr++;
        }
    }

    protected String getActualImagePath(String image) {
        return String.format(FORMAT_STRING_IMG_PATH, BASE_FILEPATH, image);
    }

    public static void main(String[] args) {
        setUpProps();
        //calling the security permissions setter before starting the browser
        new NarberalGamma();

        //calling the file restrictor before starting the browser
        //new com.vv.export.sandbox.FAD_Restrictor();

        //launching the browser now
        launch(args);
    }

    protected Image getImage(String imageFilePath) {
        try {
            return new Image(new FileInputStream(getActualImagePath(imageFilePath)));
        } catch (FileNotFoundException e) {
            System.out.println("Image not found at : " + imageFilePath + ", error : " + e);
        }
        return null;
    }

    protected Button setImageButton(Image image) {
        Button button = new Button();
        button.setGraphic(new ImageView(image));
        button.setOnAction(this);
        return button;
    }

    protected void initializeAllButtons() {
        Image image;
        image = getImage(RES_ARROW_BACK);
        buttonBack = setImageButton(image);

        image = getImage(RES_ARROW_FORWARD);
        buttonForward = setImageButton(image);

        image = getImage(RES_REFRESH);
        buttonRefresh = setImageButton(image);

        image = getImage(RES_HOME);
        buttonHome = setImageButton(image);

        image = getImage(RES_CLOSE);
        buttonAbort = setImageButton(image);

        image = getImage(RES_MORE_VERT);
        buttonMenu = setImageButton(image);
    }

    protected boolean httpToBePrefixed(String data) {
        for (String extension : siteExtensions) {
            if (data.contains(extension)) {
                return true;
            }
        }
        return false;
    }

    protected boolean toBeDownloaded(String data) {
        for (String extension : downloadFormats) {
            if (data.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    protected void textFieldControllerOnKeyPressed(TextField textField) {
        textField.setOnKeyPressed(event -> {
            if (event.isControlDown() && !event.getCharacter().equals("v") && event.getCode() == KeyCode.V) {
                System.out.println("Should be pasting now!!");
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                DataFlavor dataFlavor = DataFlavor.stringFlavor;
                if (clipboard.isDataFlavorAvailable(dataFlavor)) {
                    Object text;
                    try {
                        text = clipboard.getData(dataFlavor);
                        System.out.println("The date from the sys clipboard : " + text);
                    } catch (UnsupportedFlavorException | IOException e1) {
                        System.out.println("Error occurred whilst getting the data from the clipboard. Error : " + event);
                    }
                }
            }
        });
    }

    protected void textFieldControllerSetOnAction(TextField textFieldController) {
        textFieldController.setOnAction(e -> {
            String textFromBar = textFieldController.getText();
            String finalURL;

            if (textFromBar.startsWith(HTTP_URL_PART) || textFromBar.startsWith(HTTPS_URL_PART))
                finalURL = textFromBar;
                //else if (textFromBar.contains(".com") || textFromBar.contains(".co.in") || textFromBar.contains(".in") || textFromBar.contains(".ca") || textFromBar.contains(".ru")) {
            else if (httpToBePrefixed(textFromBar)) {
                if (textFromBar.startsWith(WWW))
                    finalURL = String.format(FORMAT_STRING_HTTP_WITHOUT_WWW, HTTP_URL_PART, textFromBar);
                else
                    finalURL = String.format(FORMAT_STRING_HTTP_WITH_WWWW, HTTP_URL_PART, WWW, textFromBar);
            } else { //it means that the entered item is to be searched
                finalURL = String.format(FORMAT_STRING_SEARCH_WEB, getProperty(HOME_PAGE_URL, DEFAULT_HOME_PAGE_URL), textFromBar);
            }

            String finalURL1 = finalURL;
            Platform.runLater(() -> webEngine.load(finalURL1));

            System.out.println("URL loaded " + finalURL);
            printHistory("History called from url typing", webEngine);
        });
    }

    protected void webEngineProgressPropertyAddListener(WebEngine webEngine) {
        webEngine.getLoadWorker().progressProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("WORK DONE FROM PROGRESS LISTENER : " + webEngine.getLoadWorker().getWorkDone());
            if (textFieldStatusDisplay.getText() != null) {
                if (textFieldStatusDisplay.getText().contains("RUN")) {
                    String local = String.valueOf(webEngine.getLoadWorker().getWorkDone());
                    textFieldStatusDisplay.setText(String.format("RUNNING - %s%c", local.substring(0, local.indexOf('.')), '%'));
                }
            }
        });
    }

    protected void webEngineStatePropertyAddListener(WebEngine webEngine, TextField textFieldController) {
        webEngine.getLoadWorker().stateProperty().addListener(
                (ov, oldState, newState) -> {

                    System.out.println("\t\tLoading stat : " + newState.toString());
                    System.out.println("\t\t\tLoad stat location : " + webEngine.getLocation() + ", load stat title : " + webEngine.getTitle());
                    System.out.println("WORK DONE : " + webEngine.getLoadWorker().getWorkDone());
                    currentURL = webEngine.getLocation();

                    if (newState == State.READY) {
                        textFieldStatusDisplay.setText(READY);
                    }
                    if (newState == State.SCHEDULED) {
                        textFieldStatusDisplay.setText(SCHEDULED);
                        if (currentURL.endsWith(".mp3") || currentURL.endsWith(".txt")) {
                            try {
                                new CZ_HttpDownload(currentURL, getProperty(DOWNLOAD_DESTINATION_PATH, DEFAULT_DOWNLOAD_DEST_PATH)).start(new Stage());
                            } catch (Exception e) {
                                System.out.println("Error occured -- " + e);
                            }
                        }
                    }
                    if (newState == State.RUNNING) {
                        textFieldStatusDisplay.setText(RUNNING);
                        currentURL = webEngine.getLocation();
                        String localURL = "";
                        try {
                            localURL = currentURL.substring(currentURL.indexOf('/') + 2);
                        } catch (Exception e1) {
                            System.out.println("URL substringing failed!");
                        }
                        textFieldController.setText(localURL);
                    }
                    if (newState == State.FAILED) {
                        textFieldStatusDisplay.setText(FAILED);
                    }
                    if (newState == State.SUCCEEDED) {
                        textFieldStatusDisplay.setText(SUCCEEDED);
                        currentURL = webEngine.getLocation();
                        String localURL = "";
                        try {
                            localURL = currentURL.substring(currentURL.indexOf('/') + 2);
                        } catch (Exception e1) {
                            System.out.println("URL substringing failed!");
                        }
                        textFieldController.setText(localURL);
                        System.out.println("called INTERNAL : " + webEngine.getTitle());

                        printHistory("History called internally", webEngine);
                        //The above usage here confirmed that the history is saved even if there is an internal page load
                    }
                    if (newState == State.CANCELLED) {
                        textFieldStatusDisplay.setText(CANCELLED);
                        System.out.println("\t\t\tCANCELLED execution");
                        currentURL = webEngine.getLocation();
                        System.out.println("cancelled url : " + currentURL);

                        //execute file download, as here i am considering that pdf or unsupported formats where given for download
                        //threading it out in order to keep the UI unaffected

                        //if (currentURL.endsWith(".pdf") || currentURL.endsWith(".PDF") || currentURL.endsWith(".zip") || currentURL.endsWith(".tar.gz") || currentURL.endsWith(".tar") || currentURL.endsWith(".gz") || currentURL.endsWith(".rar") || currentURL.endsWith(".exe") || currentURL.endsWith(".doc") || currentURL.endsWith(".docx") || currentURL.endsWith(".odt") || currentURL.endsWith(".txt") || currentURL.endsWith(".mp3") || currentURL.endsWith(".wma")) {
                        if (toBeDownloaded(currentURL)) {
                            Platform.runLater(() -> {
                                //new com.vv.export.sandbox.CZ_HttpsDownloadTrial(currentURL);
                                try {
                                    if (currentURL.startsWith(HTTPS_URL_PART))
                                        new CZ_HttpsDownload(currentURL, getProperty(DOWNLOAD_DESTINATION_PATH, DEFAULT_DOWNLOAD_DEST_PATH)).start(new Stage());
                                    else {
                                        if (currentURL.startsWith(WWW))
                                            currentURL = String.format("%s%s", HTTPS_URL_PART, currentURL);
                                        new CZ_HttpDownload(currentURL, getProperty(DOWNLOAD_DESTINATION_PATH, DEFAULT_DOWNLOAD_DEST_PATH)).start(new Stage());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        } else {
                            System.out.println("Else part cancelled out executed --> concerned url : " + currentURL);
                        }
                    }
                });
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        primaryStage.setTitle(BROWSER_TITLE);
        primaryStage.getIcons().add(new Image(new FileInputStream(getActualImagePath(RES_COVER_THUMBNAIL))));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        primaryStage.setMaximized(true);
        textFieldStatusDisplay.setEditable(false);

        if (browser == null) {
            System.out.println("initializing the engine!");
            browser = new WebView();
            webEngine = browser.getEngine();
            webEngine.setJavaScriptEnabled(true);
            java.net.CookieHandler.setDefault(null);
        }

        Scene scene = new Scene(new Group());
        VBox root = new VBox();
        HBox hBox = new HBox();

        initializeAllButtons();

        TextField textFieldController = new TextField();
        textFieldController.requestFocus();
        textFieldController.setMaxHeight(HEIGHT_OF_TEXT_FIELD_CONTROLLER); //random value of 1000 worked xD
        HBox.setHgrow(textFieldController, Priority.ALWAYS); //to set the length of the textfield according to the screen size
        textFieldControllerOnKeyPressed(textFieldController);

        //The below is the controller for the url typed in the url bar
        textFieldControllerSetOnAction(textFieldController);

        ProgressBar progressBar = new ProgressBar();
        progressBar.progressProperty().bind(webEngine.getLoadWorker().progressProperty());
        //the above line binds the progress bar to the web engine to the loader worker

        hBox.getChildren().addAll(buttonBack, buttonForward, buttonRefresh, buttonHome, textFieldController, buttonAbort, buttonMenu);

        HBox hBox2 = new HBox();
        hBox2.getChildren().addAll(progressBar, textFieldStatusDisplay);

        root.getChildren().addAll(hBox, hBox2, browser);
        scene.setRoot(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            //Erasing the downloads folder before closing the browser
            AlbedoBrowserDownloads.clearDownloads();
            System.out.println("SHUTTING DOWN!!");
        });

        webEngineProgressPropertyAddListener(webEngine);

        //the below listener hooks on to the final complete load of each and every page load occuring in the browser
        webEngineStatePropertyAddListener(webEngine, textFieldController);
    }

    protected void goBackOrForward(int choice) {
        final WebHistory history = webEngine.getHistory();
        ObservableList<WebHistory.Entry> entryList = history.getEntries();
        int currentIndex = history.getCurrentIndex();
        //System.out.println("From the back press button! curent index : "+currentIndex);
        //System.out.println("entryList : "+entryList);

        Platform.runLater(() -> {
            try {
                history.go(entryList.size() > 1
                        && currentIndex < entryList.size()
                        ? choice
                        : 0);
            } catch (Exception e) {
                System.out.println("Lower or upper bound hit. Error : " + e);
            }
        });
    }

    @Override
    public void handle(ActionEvent event) {
        Object eventSrc = event.getSource();
        if (eventSrc == buttonBack) {
            /*
            //this approach also works
            Platform.runLater(()->{
                webEngine.executeScript("history.back()");
            });
            */
            goBackOrForward(GO_BACK);
        } else if (eventSrc == buttonForward) {
            /*
            //this approach also works
            Platform.runLater(()->{
                webEngine.executeScript("history.forward()");
            });
            */
            goBackOrForward(GO_FORWARD);
        } else if (eventSrc == buttonRefresh) {
            System.out.println("Invoked from refresh handler -- CURRENT URL : " + currentURL);
            if (currentURL.length() > 1) {
                Platform.runLater(() -> webEngine.load(currentURL));
            } else {
                System.out.println("Nothing to refresh!");
            }
        } else if (eventSrc == buttonHome) {
            System.out.println("Executing home button call");
            Platform.runLater(() -> webEngine.load(getProperty(HOME_PAGE_URL, DEFAULT_HOME_PAGE_URL)));
        } else if (eventSrc == buttonAbort) {
            System.out.println("CANCELLING the run of the url -- " + currentURL);
            webEngine.getLoadWorker().cancel();
        } else if (eventSrc == buttonMenu) {
            System.out.println("SHOUTING from button Menu");

            if (!menuFrameOpen) {
                menuFrameOpen = true;
                Platform.runLater(() -> {
                    Stage stage = new Stage();
                    stage.setTitle(MENU_TITLE);
                    stage.setWidth(WIDTH_MENU_WINDOW);

                    VBox vBox = new VBox();
                    Button buttonHistory = new Button(HISTORY_TITLE);
                    buttonHistory.setMaxWidth(Double.MAX_VALUE);
                    buttonHistory.setOnAction(event1 -> {
                        System.out.println("From history button click");
                        System.out.println("Bool value : " + historyFrameOpen);
                        if (!historyFrameOpen) {
                            AlbedoBrowserHistory.fillData(webEngine);
                            Platform.runLater(() -> {
                                historyFrameOpen = true;
                                new AlbedoBrowserHistory().start(new Stage());
                            });
                        } else {
                            System.out.println("Alerting out as old instance needs to be closed");
                            Platform.runLater(() -> {
                                Alert alert = new Alert(Alert.AlertType.ERROR, "History window already open, close that to access the updated history!");
                                alert.showAndWait();
                            });

                        }
                    });

                    Button buttonDownloads = new Button(DOWNLOADS_TITLE);
                    buttonDownloads.setMaxWidth(Double.MAX_VALUE);
                    buttonDownloads.setOnAction(event12 -> {
                        System.out.println("From downloads button click");
                        System.out.println("Bool value : " + downloadsFrameOpen);
                        if (!downloadsFrameOpen) {
                            AlbedoBrowserDownloads.fillData();
                            Platform.runLater(() -> {
                                downloadsFrameOpen = true;
                                new AlbedoBrowserDownloads().start(new Stage());
                            });
                        } else {
                            System.out.println("Alerting out as old instance needs to be closed");
                            Platform.runLater(() -> {
                                Alert alert = new Alert(Alert.AlertType.ERROR, "Downloads window already open, close that to access the updated download list!");
                                alert.showAndWait();
                            });

                        }
                    });

                    vBox.getChildren().addAll(buttonHistory, buttonDownloads);
                    stage.setScene(new Scene(vBox));
                    stage.show();

                    stage.setOnCloseRequest(event13 -> menuFrameOpen = false);
                });

            } else {
                System.out.println("Alerting out as old instance needs to be closed");
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Menu window already open!!");
                    alert.showAndWait();
                });

            }
        }
    }

}
