package com.vv.export.sandbox;

import javafx.scene.image.Image;

import java.io.*;

import static com.vv.export.sandbox.util.Utility.BASE_FILEPATH;

/**
 * @author Vivek
 * @version 1.0
 * @since 28-03-2018
 * <p>
 * Sets the security policy, and then runs simulations testing the security policies
 */
public class NarberalGamma {
    private final String policyFilePath = "security.policy";
    private final String pathway = "/home/v2k/theRange/zzz.txt";
    private final String pathway2 = "/home/v2k/theRange/sandbox_target/zzz.txt";
    private final String pathway3 = "/home/v2k/theRange/sandbox_target/tmp/zzz.txt";

    NarberalGamma() {
        final boolean launchInSandboxedMode = Boolean.valueOf(System.getProperty("launchInSandboxedMode"));
        System.out.println("launch in sandboxed mode? : " + launchInSandboxedMode);
        if (launchInSandboxedMode) {
            System.setProperty("java.security.policy", BASE_FILEPATH + policyFilePath);

            try (BufferedReader in = new BufferedReader(new FileReader(new File(BASE_FILEPATH + policyFilePath)))) {
                String intermed;
                while ((intermed = in.readLine()) != null) System.out.println(intermed);
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("java.security.policy=" + System.getProperty("java.security.policy"));
            System.setSecurityManager(new SecurityManager());

            runSimulations();
        } else {
            System.out.println("Bypassing sandboxed mode...");
        }
    }

    private void runSimulations() {
        try {
            System.out.println("PASS 1");
            PrintWriter pw = new PrintWriter(new FileWriter(new File(pathway)));
            pw.write("23321415315353516136 - from IntelliJ Process\n");
            pw.write(String.valueOf(new java.sql.Timestamp(System.currentTimeMillis())));
            pw.close();

            System.out.println("File created successfully!");
        } catch (Exception e1) {
            System.out.println("File creation failed because of the following error : ");
            System.out.println(e1);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            System.out.println("PASS 2");
            PrintWriter pw = new PrintWriter(new FileWriter(new File(pathway2)));
            pw.write("23321415315353516136 - from IntelliJ Process\n");
            pw.write(String.valueOf(new java.sql.Timestamp(System.currentTimeMillis())));
            pw.close();

            System.out.println("File created successfully!");
        } catch (Exception e2) {
            System.out.println("File creation failed because of the following error : ");
            System.out.println(e2);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            System.out.println("PASS 3");
            PrintWriter pw = new PrintWriter(new FileWriter(new File(pathway3)));
            pw.write("23321415315353516136 - from IntelliJ Process\n");
            pw.write(String.valueOf(new java.sql.Timestamp(System.currentTimeMillis())));
            pw.close();

            System.out.println("File created successfully!");
        } catch (Exception e3) {
            System.out.println("File creation failed because of the following error : ");
            System.out.println(e3);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            System.out.println("PASS 3.5");
            new Image(new FileInputStream("src/main/resources/RESIZED_ic_arrow_back_black_48dp_2x.png"));
            System.out.println("Pass 3.5 passed!");
        } catch (Exception e3) {
            System.out.println("File creation for pass 3.5 failed because of the following error : ");
            System.out.println(e3);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new NarberalGamma();

        new CZ_HttpsDownloadTrial("https://s3.amazonaws.com/tm-town-nlp-resources/ch2.pdf");
        //new com.vv.export.sandbox.CZ_HttpsDownloadTrial("http://www4.ncsu.edu/~kksivara/sfwr4c03/lectures/lecture10.pdf");
    }
}