import com.sun.javafx.application.PlatformImpl;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

import javax.net.ssl.HttpsURLConnection;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Vivek
 * @version 1.0
 * @since 02-04-2018
 */
public class CZ_HttpsDownload extends Application {

    private boolean failedProcess = false;
    private String downloadSrcFilePath = "";
    private String downloadSrcFileName = "";

    private final int connectionTimeOut = 10000;
    private final int readTimeOut = 300000;

    public TextArea displayDownloadStatus;

    public CZ_HttpsDownload(String downloadSrcFilePath) {
        this.downloadSrcFilePath = downloadSrcFilePath;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        System.out.println("Starting File download ");
        failedProcess = false;

        String contentLength = "";
        String contentName = "";
        long maxSize = 0;

        if (downloadSrcFilePath.startsWith("https:")) {
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) new URL(downloadSrcFilePath).openConnection();
            contentLength = httpsURLConnection.getHeaderField("Content-Length");
            contentName = httpsURLConnection.getHeaderField("Content-Disposition");
            System.out.println("Name : " + contentName);
            httpsURLConnection.disconnect();
            maxSize = Long.parseLong(contentLength);

            System.out.println("via https Download size: " + contentLength + " bytes --> " + (maxSize / 1024) + " KB");
        } else if (downloadSrcFilePath.startsWith("http:")) {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(downloadSrcFilePath).openConnection();
            contentLength = httpURLConnection.getHeaderField("Content-Length");
            contentName = httpURLConnection.getHeaderField("Content-Disposition");
            System.out.println("Name : " + contentName);
            httpURLConnection.disconnect();
            maxSize = Long.parseLong(contentLength);

            System.out.println("via http Download size : " + contentLength + " bytes --> " + (maxSize / 1024) + " KB");
        }

        if (contentName == null || contentName.length() == 0) {
            contentName = downloadSrcFilePath.substring(downloadSrcFilePath.lastIndexOf('/') + 1);
            downloadSrcFileName = contentName;
            System.out.println("Destination file renamed to : " + downloadSrcFileName);
            downloadSrcFileName = "e:\\sandbox_target\\tmp\\" + downloadSrcFileName;
            System.out.println("Rewriting the destination file renamed to : " + downloadSrcFileName);
        }

        primaryStage.setTitle("Downloading : "+downloadSrcFileName+" ~ "+(maxSize/1024)+" KB");

        System.out.println("PASS-1");
        //actual file donwload command

        /*
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    FileUtils.copyURLToFile(new URL(downloadSrcFilePath), new File(downloadSrcFileName), connectionTimeOut, readTimeOut);
                } catch (IOException e) {
                    e.printStackTrace();
                    failedProcess = true;
                    System.out.println("Download failed!!!!");
                }
            }
        });
        */
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FileUtils.copyURLToFile(new URL(downloadSrcFilePath), new File(downloadSrcFileName), connectionTimeOut, readTimeOut);
                } catch (IOException e) {
                    e.printStackTrace();
                    failedProcess = true;
                    System.out.println("Download failed!!!!");
                }
            }
        }).start();

        VBox vBox = new VBox();
        displayDownloadStatus = new TextArea();
        displayDownloadStatus.setEditable(false);

        vBox.getChildren().addAll(displayDownloadStatus);
        primaryStage.setScene(new Scene(vBox));
        primaryStage.show();

        System.out.println("PASS - 2");
        //file length is returned in bytes, as is confirmed in testing arena
        final long[] current = {0};
        final long[] prev = {0};
        File file = new File(downloadSrcFileName);
        final String[] downloadStat = {""};
        long finalMaxSize = maxSize;

        Service<String> bgthread;

        bgthread = new Service<String>() {
            @Override
            protected Task<String> createTask() {
                return new Task<String>() {
                    String displayData = "";

                    @Override
                    protected String call() throws Exception {
                        while (current[0] < finalMaxSize && !failedProcess) {
                            current[0] = file.length();
                            if (current[0] != prev[0]) {
                                downloadStat[0] = Double.toString((current[0] / (finalMaxSize * 1.0)) * 100);
                                try {
                                    downloadStat[0] = downloadStat[0].substring(0, downloadStat[0].indexOf('.') + 3);
                                } catch (Exception e) {
                                    downloadStat[0] = downloadStat[0].substring(0, downloadStat[0].indexOf('.') + 2);
                                }
                                System.out.println("Destination file length : " + current[0] + " bytes, download stat : " + downloadStat[0] + "%" + ", failedProcess? : " + failedProcess);
                                String local = "Destination file length : " + current[0] + " bytes, download stat : " + downloadStat[0] + "%" + ", failedProcess? : " + failedProcess + "\n";

                                //displayData.append("destination file length : ").append(current[0]).append(", download stat : ").append(downloadStat[0]).append("%").append(", failedProcess? : ").append(failedProcess).append("\n");
                                if((local + displayData).length() > Integer.MAX_VALUE) {
                                    System.out.println("\t\tCutting off string now to limit it....");
                                    displayData = local + displayData.substring(0, Integer.MAX_VALUE - 2000);
                                } else{
                                    displayData = local + displayData;
                                }

                                updateValue(String.valueOf(displayData));
                                prev[0] = current[0];
                            }
                        }
                        return displayData;
                    }
                };
            }
        };
        /*
        Task<String> task = new Task<String>() {

            @Override
            protected String call() throws Exception {
                StringBuffer displayData = new StringBuffer();

                while (current[0] < finalMaxSize && !failedProcess) {
                    current[0] = file.length();
                    if (current[0] != prev[0]) {
                        downloadStat[0] = Double.toString((current[0] / (finalMaxSize * 1.0)) * 100);
                        try {
                            downloadStat[0] = downloadStat[0].substring(0, downloadStat[0].indexOf('.') + 3);
                        } catch (Exception e) {
                            downloadStat[0] = downloadStat[0].substring(0, downloadStat[0].indexOf('.') + 2);
                        }
                        System.out.println("destination file length : " + current[0] + ", download stat : " + downloadStat[0] + "%" + ", failedProcess? : " + failedProcess);

                        displayData.append("destination file length : ").append(current[0]).append(", download stat : ").append(downloadStat[0]).append("%").append(", failedProcess? : ").append(failedProcess).append("\n");

                        //displayDownloadStatus.setText(String.valueOf(displayData));
                        updateValue(String.valueOf(displayData));

                        //progressBar.setProgress(Double.parseDouble(downloadStat[0]));

                        prev[0] = current[0];
                    }
                }

                if (!failedProcess) System.out.println("Download complete!!");
                else System.out.println("Download failed!!");

                //return null;
                return String.valueOf(displayData);
            }
        };
        */

        displayDownloadStatus.textProperty().bind(bgthread.valueProperty());
        bgthread.start();
    }
}
