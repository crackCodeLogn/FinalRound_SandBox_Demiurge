package com.vv.export.sandbox;

import org.apache.commons.io.FileUtils;

import javax.net.ssl.HttpsURLConnection;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class CZ_HttpsDownloadTrial {

    //static String fromFile = "https://web.stanford.edu/~jurafsky/slp3/ed3book.pdf";
    //static String fromFile = "https://amtrakdowneaster.com/sites/default/files/schedule/Sept25_Schedule_11x17.pdf"; //failed
    //static String fromFile = "ftp://mail.im.tku.edu.tw/Prof_Liang/%BA%F4%B8%F4%A6w%A5%FE/%AD%5E%A4%E5%AA%A9/Crypto4e-PDF-Tables/ch09-h.pdf"; //failed
    static String fromFile = "http://www4.ncsu.edu/~kksivara/sfwr4c03/lectures/lecture10.pdf"; //PASS
    static String toFile = "lecture100x200P.pdf";
    static boolean failedProcess = false;

    public CZ_HttpsDownloadTrial(String fromFile) {
        try {
            //connectionTimeout, readTimeout = 10 seconds
            //FileUtils.copyURLToFile(new URL(fromFile), new File(toFile), 10000, 10000);

            System.out.println("Starting File download ");
            failedProcess = false;

            String contentLength = "";
            String contentName = "";
            long maxSize = 0;

            if (fromFile.startsWith("https:")) {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) new URL(fromFile).openConnection();
                contentLength = httpsURLConnection.getHeaderField("Content-Length");
                contentName = httpsURLConnection.getHeaderField("Content-Disposition");
                System.out.println("Name : " + contentName);
                httpsURLConnection.disconnect();
                maxSize = Long.parseLong(contentLength);

                System.out.println("via https Download size: " + contentLength + " bytes --> " + (maxSize / 1024) + " KB");
            } else if (fromFile.startsWith("http:")) {
                HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(fromFile).openConnection();
                contentLength = httpURLConnection.getHeaderField("Content-Length");
                contentName = httpURLConnection.getHeaderField("Content-Disposition");
                System.out.println("Name : " + contentName);
                httpURLConnection.disconnect();
                maxSize = Long.parseLong(contentLength);

                System.out.println("via http Download size : " + contentLength + " bytes --> " + (maxSize / 1024) + " KB");
            }

            if (contentName == null || contentName.length() == 0) {
                contentName = fromFile.substring(fromFile.lastIndexOf('/') + 1);
                toFile = contentName;
                System.out.println("Destination file renamed to : " + toFile);
                toFile = "/home/sniperveliski/sandbox_target/tmp/" + toFile;
                System.out.println("Rewriting the destination file renamed to : " + toFile);
            }

            System.out.println("PASS-1");
            //actual file donwload command
            try {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FileUtils.copyURLToFile(new URL(fromFile), new File(toFile), 10000, 300000);
                        } catch (IOException e) {
                            e.printStackTrace();
                            failedProcess = true;
                            System.out.println("Download failed!!!!");
                        }
                    }
                }).start();
            } catch (Exception e223) {
                System.out.println("Exceptionnnnnnnnnnnn : " + e223);
            }

            System.out.println("PASS - 2");
            //file length is returned in bytes, as is confirmed in testing arena
            final long[] current = {0};
            final long[] prev = {0};
            File file = new File(toFile);
            final String[] downloadStat = {""};
            long finalMaxSize = maxSize;
            new Thread(new Runnable() {
                @Override
                public void run() {
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
                            prev[0] = current[0];
                        }
                    }
                    if (!failedProcess) System.out.println("Download complete!!");
                    else System.out.println("Download failed!!");
                }
            }).start();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        new CZ_HttpsDownloadTrial(fromFile);
    }
}
