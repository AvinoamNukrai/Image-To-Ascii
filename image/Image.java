package image;

import java.awt.*;
import java.io.IOException;

/**
 * Facade for the image module and an interface representing an image.
 * @author Dan Nirel
 */
public interface Image {
    Color getPixel(int x, int y);
    int getWidth();
    int getHeight();

    /**
     * Open an image from file. Each dimensions of the returned image is guaranteed
     * to be a power of 2, but the dimensions may be different.
     * @param filename a path to an image file on disk
     * @return an object implementing Image if the operation was successful,
     * null otherwise
     */
    static Image fromFile(String filename) {
        try {
            return new FileImage(filename);
        } catch(IOException ioe) {
            return null;
        }
    }

    /**
     * Allows iterating the pixels' colors by order (first row, second row and so on).
     * @return an Iterable<Color> that can be traversed with a foreach loop
     */
    default Iterable<Color> pixels() {
        return new ImageIterableProperty<>(
                this, this::getPixel,1,1);
    }

    /**
     * Breaks the image down to square sub-images of a specified width/height in pixels.
     * @param pixels the width and height, in pixels, of each square sub-image
     * @return an Iterable of the sub-images
     */
    default Iterable<Image> squareSubImagesOfSize(int pixels) {
        return new ImageIterableProperty<>(
                this,
                (x,y)->new ImageView(this,x,y,pixels,pixels),
                pixels,
                pixels);
    }
}
