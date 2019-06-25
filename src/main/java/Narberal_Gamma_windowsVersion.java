import java.io.*;

/**
 * @author Vivek
 * @version 1.0
 * @since 28-03-2018
 */
public class Narberal_Gamma_windowsVersion {
    private static String policyFilePath = "security.policy";
    private static String pathway = "e:\\zzz.txt";
    private static String pathway2 = "e:\\sandbox_target\\zzz.txt";
    private static String pathway3 = "e:\\sandbox_target\\tmp\\zzz.txt";

    public Narberal_Gamma_windowsVersion(){
        System.setProperty("java.security.policy", policyFilePath);

        try {
            BufferedReader in = new BufferedReader(new FileReader(new File(policyFilePath)));
            String intermed = "";
            while((intermed = in.readLine())!=null){
                System.out.println(intermed);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
            //System.out.println("Stack trace :-");
            //e1.printStackTrace();
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
            //System.out.println("Stack trace :-");
            //e2.printStackTrace();
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
            //System.out.println("Stack trace :-");
            //e3.printStackTrace();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try{
            System.out.println("PASS 4");
            Runtime.getRuntime().exec("e:\\sandbox_target\\tmp\\testspam1.sh");
        } catch (Exception e4){
            System.out.println("Script exection suspended, due to lack of privilege. Error : ");
            System.out.println(e4);
            //System.out.println("Stack trace --");
            //e4.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        new Narberal_Gamma_windowsVersion();

        new CZ_HttpsDownloadTrial("https://s3.amazonaws.com/tm-town-nlp-resources/ch2.pdf");
        //new CZ_HttpsDownloadTrial("http://www4.ncsu.edu/~kksivara/sfwr4c03/lectures/lecture10.pdf");
    }
}