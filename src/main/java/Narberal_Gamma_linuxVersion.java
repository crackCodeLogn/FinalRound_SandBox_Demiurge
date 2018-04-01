import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Vivek
 * @version 1.0
 * @since 26-03-2018
 */

public class Narberal_Gamma_linuxVersion {

    private static String policyFilePath = "security.policy";
    private static String pathway = "/home/sniperveliski/zzz.txt";
    private static String pathway2 = "/home/sniperveliski/sandboxed/zzz.txt";
    private static String pathway3 = "/home/sniperveliski/sandboxed/tmp/zzz.txt";

    public static void main(String[] args) throws IOException {

        System.setProperty("java.security.policy", policyFilePath);

        System.out.println("java.security.policy=" + System.getProperty("java.security.policy"));
        System.setSecurityManager(new SecurityManager());

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
            System.out.println("Stack trace :-");
            e1.printStackTrace();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try{
            System.out.println("PASS 2");
            PrintWriter pw = new PrintWriter(new FileWriter(new File(pathway2)));
            pw.write("23321415315353516136 - from IntelliJ Process\n");
            pw.write(String.valueOf(new java.sql.Timestamp(System.currentTimeMillis())));
            pw.close();

            System.out.println("File created successfully!");
        } catch (Exception e2){
            System.out.println("File creation failed because of the following error : ");
            System.out.println(e2);
            System.out.println("Stack trace :-");
            e2.printStackTrace();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try{
            System.out.println("PASS 3");
            PrintWriter pw = new PrintWriter(new FileWriter(new File(pathway3)));
            pw.write("23321415315353516136 - from IntelliJ Process\n");
            pw.write(String.valueOf(new java.sql.Timestamp(System.currentTimeMillis())));
            pw.close();

            System.out.println("File created successfully!");
        } catch (Exception e3){
            System.out.println("File creation failed because of the following error : ");
            System.out.println(e3);
            System.out.println("Stack trace :-");
            e3.printStackTrace();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try{
            System.out.println("PASS 4");
            Runtime.getRuntime().exec("/home/sniperveliski/sandboxed/tmp/testspam1.sh");
        } catch (Exception e4){
            System.out.println("Script exection suspended, due to lack of privilege. Error : ");
            System.out.println(e4);
            System.out.println("Stack trace --");
            e4.printStackTrace();
        }
    }
}
