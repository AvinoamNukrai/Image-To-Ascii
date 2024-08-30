//package ascii_art;

// Written by Avinoam Nukrai, Winter 2021 Hebrew U - OOP course

package ascii_art;
import image.Image;
import java.util.logging.Logger;

/**
 * This class is the main class of the program, parsing the cmd args and execute the Shell.run()
 */
public class Driver {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("USAGE: java asciiArt ");
            return;
        }
        Image img = Image.fromFile(args[0]);
        if (img == null) {
            Logger.getGlobal().severe("Failed to open image file " + args[0]);
            return;
        }
        new Shell(img).run();
    }
}


