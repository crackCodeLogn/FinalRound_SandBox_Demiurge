package com.vv.export.sandbox.downloader;

import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import static com.vv.export.sandbox.util.Helper.getProperty;
import static com.vv.export.sandbox.util.Utility.DOWNLOAD_TIMEOUT_UPPER;

public class CZ_HttpDownload extends Application {
    private final int connectionTimeOut = 10000;
    private final int readTimeOut = 300000;

    public TextArea displayDownloadStatus;

    private boolean failedProcess;
    private String downloadSrcFilePath;
    private String downloadSrcFileName;
    private String downloadDestinationPath;

    public CZ_HttpDownload(String downloadSrcFilePath, String downloadDestinationPath) {
        this.downloadSrcFilePath = downloadSrcFilePath;
        this.downloadDestinationPath = downloadDestinationPath;

        downloadSrcFileName = "";
        failedProcess = false;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        System.out.println("Starting File download ");
        failedProcess = false;

        String contentLength = "";
        String contentName = "";
        long maxSize = 0;


        URLConnection httpURLConnection = new URL(downloadSrcFilePath).openConnection();
        httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.29 Safari/537.36");
        httpURLConnection.connect();
        /*
        System.out.println("httpurlconnection details :- ");
        Field field[] = httpURLConnection.getClass().getDeclaredFields();
        for (Field field1 : field) {
            try {
                field1.setAccessible(true);
            } catch (Exception e1) {
                System.out.println("failed to set the property of the field - " + field1.getName());
            }
            try {
                System.out.println(field1.getName() + " -- " + field1.getType() + " -- " + field1.get(field));
            } catch (Exception e1) {
                System.out.println("Error while going through val of  obj : " + e1);
            }
        }
        */
        contentLength = httpURLConnection.getHeaderField("Content-Length");
        contentName = httpURLConnection.getHeaderField("Content-Disposition");
        System.out.println("Name : " + contentName);
        //httpURLConnection.disconnect();
        try {
            maxSize = Long.parseLong(contentLength);
        } catch (Exception e) {
            System.out.println("Exception at contentLength from the header - " + e);
            maxSize = -1;
        }

        System.out.println("via http Download size : " + contentLength + " bytes --> " + (maxSize / 1024) + " KB");


        if (contentName == null || contentName.length() == 0) {
            contentName = downloadSrcFilePath.substring(downloadSrcFilePath.lastIndexOf('/') + 1);
            downloadSrcFileName = contentName.replaceAll("%20", "_");
            System.out.println("Destination file renamed to : " + downloadSrcFileName);
            downloadSrcFileName = downloadDestinationPath + downloadSrcFileName;
            System.out.println("Rewriting the destination file renamed to : " + downloadSrcFileName);
        }

        primaryStage.setTitle("Downloading : " + downloadSrcFileName + " ~ " + (maxSize / 1024) + " KB");

        System.out.println("PASS-1");
        //actual file donwload command

        new Thread(() -> {
            try {
                //FileUtils.copyURLToFile(new URL(downloadSrcFilePath), new File(downloadSrcFileName), connectionTimeOut, readTimeOut);
                FileUtils.copyInputStreamToFile(httpURLConnection.getInputStream(), new File(downloadSrcFileName));
            } catch (IOException e) {
                e.printStackTrace();
                failedProcess = true;
                System.out.println("Download failed!!!!");
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

                        if (finalMaxSize == -1) {
                            int timeOutBase = 75, timeOutLocal = timeOutBase, upperTimeOut = Integer.parseInt(getProperty(DOWNLOAD_TIMEOUT_UPPER, String.valueOf(201)));
                            System.out.println("Entering un-chartered territory in the downloading space!");
                            while (!failedProcess) {
                                current[0] = file.length();
                                if (current[0] != prev[0]) {
                                    String local = String.format("Downloaded file length : %d bytes -- %.2f KB -- %.2f MB", current[0], (current[0] / 1024.0), (current[0] / (1024 * 1024.0)));
                                    System.out.println(local + " -- timeout : " + timeOutLocal);
                                    local += "\n";
                                    displayData = local + displayData;
                                    updateValue(displayData);
                                    prev[0] = current[0];
                                    timeOutLocal = timeOutBase;
                                } else {
                                    System.out.println("cur[0] : " + current[0] + ", prev[0] : " + prev[0] + " -- timeout : " + timeOutLocal);
                                    timeOutLocal *= 1.05;
                                }
                                if (timeOutLocal > upperTimeOut) break;
                                Thread.sleep(timeOutLocal);
                            }
                            return displayData;

                        } else {
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
                                    if ((local + displayData).length() > Integer.MAX_VALUE) {
                                        System.out.println("\t\tCutting off string now to limit it....");
                                        displayData = local + displayData.substring(0, Integer.MAX_VALUE - 2000);
                                    } else {
                                        displayData = local + displayData;
                                    }

                                    updateValue(displayData);
                                    prev[0] = current[0];
                                }
                            }
                            return displayData;
                        }
                    }
                };
            }
        };

        displayDownloadStatus.textProperty().bind(bgthread.valueProperty());
        bgthread.start();
    }
}
