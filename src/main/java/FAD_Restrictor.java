import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * @author Vivek
 * @version 1.0
 * @since 02-04-2018
 */
public class FAD_Restrictor {

    private static CLibrary libc = (CLibrary) Native.loadLibrary("c", CLibrary.class);

    private static String path1 = "/home/sniperveliski/IdeaProjects/FinalRound_Sandbox_Demiurge/";
    private static String path2 = "/home/sniperveliski/sandbox_target/tmp/";

    public FAD_Restrictor(){
        System.out.println("Path run - FAD");

        libc.chmod(path1, 0700); //full access to admin only
        System.out.println("First path permissions changed");
        libc.chmod(path2, 0700); //full access to admin only
        System.out.println("Second path permissions changed");
    }

    public static void main(String[] args) {
        new FAD_Restrictor();
    }

    interface CLibrary extends Library {
        public int chmod(String path, int mode);
    }
}
