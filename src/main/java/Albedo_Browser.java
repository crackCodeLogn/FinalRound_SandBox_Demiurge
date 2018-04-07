import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.net.ssl.HttpsURLConnection;
import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

/**
 * @author Vivek
 * @version 1.0
 * @since 28-03-2018
 * <p>
 * Details:-
 * WebView does not support plugins like flash / adobe
 */
public class Albedo_Browser extends Application implements EventHandler<ActionEvent> {

    private static Button buttonBack;
    private static Button buttonForward;
    private static Button buttonRefresh;
    private static Button buttonHome;
    private static Button buttonAbort;
    private static Button buttonMenu;

    private static TextField textFieldStatusDisplay = new TextField("---");

    //private static java.awt.Button buttonHistory;
    //private static java.awt.Button buttonDownload;

    public static boolean historyFrameOpen = false;
    public static boolean downloadsFrameOpen = false;
    public static boolean menuFrameOpen = false;

    public static WebView browser;
    public static WebEngine webEngine;

    private static String currentURL = "";
    private static String homePageURL = "https://bing.com";

    private static String downloadDestinationPath = "/home/sniperveliski/sandbox_target/tmp/";

    @Override
    public void start(final Stage primaryStage) throws Exception {
        primaryStage.setTitle("Albedo");
        primaryStage.getIcons().add(new Image(new FileInputStream("resources//cover_thumbnail.jpg")));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        //primaryStage.setWidth(screenSize.getWidth());
        //primaryStage.setHeight(screenSize.getHeight());
        primaryStage.setMaximized(true);
        textFieldStatusDisplay.setEditable(false);
        //textFieldStatusDisplay.setDisable(true);

        if (browser == null) {
            System.out.println("initializing the engine!");
            browser = new WebView();
            webEngine = browser.getEngine();
        }

        webEngine.setJavaScriptEnabled(true);
        java.net.CookieHandler.setDefault(null);

        Scene scene = new Scene(new Group());
        VBox root = new VBox();

        HBox hBox = new HBox();
        //Image image = new Image(getClass().getResourceAsStream("E:/IdeaProjects/FinalRound_Sandbox/back.png"));
        Image image = null;

        //image = new Image(new FileInputStream("E:/IdeaProjects/FinalRound_Sandbox/back.png"));

        buttonBack = new Button();
        image = new Image(new FileInputStream("resources//RESIZED_ic_arrow_back_black_48dp_2x.png"));
        buttonBack.setGraphic(new ImageView(image));
        buttonBack.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                /*
                //this approach also works
                Platform.runLater(()->{
                    webEngine.executeScript("history.back()");
                });
                */
                System.out.println("Executing back button operation...");
                final WebHistory history = webEngine.getHistory();
                ObservableList<WebHistory.Entry> entryList = history.getEntries();
                int currentIndex = history.getCurrentIndex();

                Platform.runLater(() ->
                {
                    history.go(entryList.size() > 1
                            && currentIndex > 0
                            ? -1
                            : 0);
                });
            }
        });

        image = new Image(new FileInputStream("resources//RESIZED_ic_arrow_forward_black_48dp_2x.png"));
        buttonForward = new Button();
        buttonForward.setGraphic(new ImageView(image));
        buttonForward.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                /*
                //this approach also works
                Platform.runLater(()->{
                    webEngine.executeScript("history.forward()");
                });
                */
                System.out.println("Executing forward button operation...");
                final WebHistory history = webEngine.getHistory();
                ObservableList<WebHistory.Entry> entryList = history.getEntries();
                int currentIndex = history.getCurrentIndex();

                Platform.runLater(() ->
                {
                    history.go(entryList.size() > 1
                            && currentIndex < entryList.size() - 1
                            ? 1
                            : 0);
                });
            }
        });

        image = new Image(new FileInputStream("resources//RESIZED_ic_refresh_black_48dp_2x.png"));
        buttonRefresh = new Button();
        buttonRefresh.setGraphic(new ImageView(image));
        buttonRefresh.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Invoked from refresh handler -- CURRENT URL : " + currentURL);
                if (currentURL.length() > 1) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            webEngine.load(currentURL);
                        }
                    });

                } else {
                    System.out.println("Nothing to refresh!");
                }
            }
        });

        image = new Image(new FileInputStream("resources//RESIZED_ic_home_black_48dp_2x.png"));
        buttonHome = new Button();
        buttonHome.setGraphic(new ImageView(image));
        buttonHome.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Executing home button call");
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        webEngine.load(homePageURL);
                    }
                });

            }
        });

        image = new Image(new FileInputStream("resources//RESIZED_ic_close_black_48dp_2x.png"));
        buttonAbort = new Button();
        buttonAbort.setGraphic(new ImageView(image));
        buttonAbort.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("CANCELLING the run of the url -- " + currentURL);
                webEngine.getLoadWorker().cancel();
            }
        });

        image = new Image(new FileInputStream("resources//RESIZED_ic_more_vert_black_48dp_2x.png"));
        buttonMenu = new Button();
        buttonMenu.setGraphic(new ImageView(image));
        buttonMenu.setOnAction(this);

        javafx.scene.control.TextField textFieldController = new javafx.scene.control.TextField();
        //textFieldController.setMaxWidth(Double.MAX_VALUE);
        textFieldController.requestFocus();
        textFieldController.setMaxHeight(1000); //random value of 1000 worked xD
        HBox.setHgrow(textFieldController, Priority.ALWAYS); //to set the length of the textfield according to the screen size

        final TextField textField = textFieldController;

        //The below is the controller for the url typed in the url bar
        textFieldController.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                String finalURL = "https://" + textField.getText();

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        webEngine.load(finalURL);
                    }
                });


                System.out.println("URL loaded " + finalURL);
                //System.out.println("History : \n"+webEngine.getHistory());
                printHistory("History called from url typing", webEngine);
            }
        });

        ProgressBar progressBar = new ProgressBar();
        progressBar.progressProperty().bind(webEngine.getLoadWorker().progressProperty());
        //the above line binds the progress bar to the web enginer to the loader worker

        //textFieldStatusDisplay = new TextField("BLAH!");

        hBox.getChildren().addAll(buttonBack, buttonForward, buttonRefresh, buttonHome, textFieldController, buttonAbort, buttonMenu);

        HBox hBox2 = new HBox();
        hBox2.getChildren().addAll(progressBar, textFieldStatusDisplay);

        //root.getChildren().addAll(progressBar, textFieldController, browser);
        //root.getChildren().addAll(hBox, progressBar, browser);
        root.getChildren().addAll(hBox, hBox2, browser);
        scene.setRoot(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                //Erasing the downloads folder before closing the browser
                Albedo_Browser_Downloads.clearDownloads();
                System.out.println("SHUTTING DOWN!!");
            }
        });

        webEngine.getLoadWorker().progressProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                System.out.println("WORK DONE FROM PROGRESS LISTENER : "+webEngine.getLoadWorker().getWorkDone());
                if(textFieldStatusDisplay.getText()!=null){
                    if(textFieldStatusDisplay.getText().contains("RUN")){
                        String local = String.valueOf(webEngine.getLoadWorker().getWorkDone());
                        textFieldStatusDisplay.setText("RUNNING - "+local.substring(0, local.indexOf('.'))+"%");
                    }
                }
            }
        });

        //the below listener hooks on to the final complete load of each and every page load occuring in the browser
        webEngine.getLoadWorker().stateProperty().addListener(
                new ChangeListener<State>() {
                    @Override
                    public void changed(ObservableValue ov, State oldState, State newState) {

                        System.out.println("\t\tLoading stat : " + newState.toString());
                        System.out.println("\t\t\tLoad stat location : " + webEngine.getLocation() + ", load stat title : " + webEngine.getTitle());
                        System.out.println("WORK DONE : "+webEngine.getLoadWorker().getWorkDone());
                        currentURL = webEngine.getLocation();

                        if (newState == State.READY) {
                            textFieldStatusDisplay.setText("READY");
                        }
                        if (newState == State.SCHEDULED) {
                            textFieldStatusDisplay.setText("SCHEDULED");
                        }
                        if (newState == State.RUNNING) {
                            textFieldStatusDisplay.setText("RUNNING");
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
                            textFieldStatusDisplay.setText("FAILED");
                        }
                        if (newState == Worker.State.SUCCEEDED) {
                            //primaryStage.setTitle(webEngine.getLocation());
                            textFieldStatusDisplay.setText("SUCCEEDED");
                            //primaryStage.setTitle(webEngine.getTitle());
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
                        if (newState == Worker.State.CANCELLED) {
                            textFieldStatusDisplay.setText("CANCELLED");
                            System.out.println("\t\t\tCANCELLED execution");
                            currentURL = webEngine.getLocation();
                            System.out.println("cancelled url : " + currentURL);

                            //execute file download, as here i am considering that pdf or unsupported formats where given for download
                            //threading it out in order to keep the UI unaffected
                            if (currentURL.endsWith(".pdf") || currentURL.endsWith(".zip") || currentURL.endsWith(".tar.gz") || currentURL.endsWith(".tar") || currentURL.endsWith(".gz") || currentURL.endsWith(".zip") || currentURL.endsWith(".rar") || currentURL.endsWith(".exe") || currentURL.endsWith(".doc") || currentURL.endsWith(".docx") || currentURL.endsWith(".odt")) {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        //new CZ_HttpsDownloadTrial(currentURL);
                                        try {
                                            new CZ_HttpsDownload(currentURL, downloadDestinationPath).start(new Stage());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } else {
                                System.out.println("Else part cancelled out executed --> concerned url : " + currentURL);
                            }
                        }
                    }
                });

    }

    private static void printHistory(String message, WebEngine webEngine) {
        System.out.println(message);
        WebHistory history = webEngine.getHistory();
        ObservableList<WebHistory.Entry> list = history.getEntries();
        int tr = 1;
        for (WebHistory.Entry entry : list) {
            System.out.println("Entry " + tr + " --> " + entry.getTitle() + " :: " + entry.getUrl() + " :: " + entry.getLastVisitedDate());
            tr++;
        }
    }

    private static boolean isConnectionActive() throws IOException {
        boolean active = false;

        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) new URL("https://google.com").openConnection();
        if (httpsURLConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) active = true;
        httpsURLConnection.disconnect();


        return active;
    }

    @Override
    public void handle(ActionEvent event) {
        /*
        //shifting the entire codebase of button handling to the start f() because they will make use of webEngine, and making it global is not a good idea...
        //this change made on 31-03-2018 at 0008 hrs
        if (event.getSource() == buttonBack) {
            System.out.println("SHOUTING from button back");
        } else if (event.getSource() == buttonForward) {
            System.out.println("SHOUTING from button forward");
        } else if (event.getSource() == buttonRefresh) {
            System.out.println("SHOUTING from button refresh");
        } else if (event.getSource() == buttonHome) {
            System.out.println("SHOUTING from button home");
            */
        //} else if (event.getSource() == buttonMenu) {
        if (event.getSource() == buttonMenu) {
            System.out.println("SHOUTING from button Menu");

            if (!menuFrameOpen) {
                menuFrameOpen = true;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Stage stage = new Stage();
                        stage.setTitle("MENU");
                        stage.setWidth(250);
                        //stage.setHeight(100);

                        VBox vBox = new VBox();
                        Button buttonHistory = new Button("HISTORY");
                        buttonHistory.setMaxWidth(Double.MAX_VALUE);
                        buttonHistory.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                System.out.println("From history button click");
                                System.out.println("Bool value : " + historyFrameOpen);
                                if (!historyFrameOpen) {
                                    Albedo_Browser_History.fillData(webEngine);
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            historyFrameOpen = true;
                                            new Albedo_Browser_History().start(new Stage());
                                        }
                                    });
                                } else {
                                    System.out.println("Alerting out as old instance needs to be closed");
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            Alert alert = new Alert(Alert.AlertType.ERROR, "History window already open, close that to access the updated history!");
                                            alert.showAndWait();
                                        }
                                    });

                                }
                            }
                        });

                        Button buttonDownloads = new Button("DOWNLOADS");
                        buttonDownloads.setMaxWidth(Double.MAX_VALUE);
                        buttonDownloads.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                System.out.println("From downloads button click");
                                System.out.println("Bool value : " + downloadsFrameOpen);
                                if (!downloadsFrameOpen) {
                                    Albedo_Browser_Downloads.fillData();
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            downloadsFrameOpen = true;
                                            new Albedo_Browser_Downloads().start(new Stage());
                                        }
                                    });
                                } else {
                                    System.out.println("Alerting out as old instance needs to be closed");
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            Alert alert = new Alert(Alert.AlertType.ERROR, "Downloads window already open, close that to access the updated download list!");
                                            alert.showAndWait();
                                        }
                                    });

                                }
                            }
                        });

                        vBox.getChildren().addAll(buttonHistory, buttonDownloads);
                        stage.setScene(new Scene(vBox));
                        stage.show();

                        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                            @Override
                            public void handle(WindowEvent event) {
                                menuFrameOpen = false;
                            }
                        });
                    }
                });

                System.out.println("AFTer the runnable part");
            } else {
                System.out.println("Alerting out as old instance needs to be closed");
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Menu window already open!!");
                        alert.showAndWait();
                    }
                });

            }
        }
    }

    public static void main(String[] args) {
        //calling the security permissions setter before starting the browser
        new Narberal_Gamma();

        //calling the file restrictor before starting the browser
        //new FAD_Restrictor();

        //launching the browser now
        launch(args);
    }
}
