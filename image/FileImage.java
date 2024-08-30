package image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * A package-private class of the package image.
 * @author Dan Nirel
 */
class FileImage implements Image {
    private static final Color DEFAULT_COLOR = Color.WHITE;

    private final Color[][] pixelArray;

    public FileImage(String filename) throws IOException {
        java.awt.image.BufferedImage im = ImageIO.read(new File(filename));
        int origWidth = im.getWidth(), origHeight = im.getHeight();

        int newWidth = getPowerOfTwoGreaterThan(origWidth);
        int newHeight = getPowerOfTwoGreaterThan(origHeight);

        pixelArray = new Color[newHeight][newWidth];
        float xMargin = (newWidth-origWidth)/2.0f;
        float yMargin = (newHeight-origHeight)/2.0f;

        for(int y = 0 ; y < pixelArray.length ; y++) {
            for(int x = 0 ; x < pixelArray[y].length ; x++) {
                //if this pixel is not in the margins, take value from image
                if(x >= (int)Math.floor(xMargin) && x < pixelArray[y].length-Math.ceil(xMargin) &&
                   y >= (int)Math.floor(yMargin) && y < pixelArray.length-Math.ceil(yMargin)) {
                pixelArray[y][x] = new Color(im.getRGB(
                        x-(int)Math.floor(xMargin),
                        y-(int)Math.floor(yMargin)));
                }
                else { //otherwise, color with default background color
                    pixelArray[y][x] = DEFAULT_COLOR;
                }
            }
        }
    }

    @Override
    public int getWidth() {
        return pixelArray[0].length;
    }

    @Override
    public int getHeight() {
        return pixelArray.length;
    }

    @Override
    public Color getPixel(int x, int y) {
        return pixelArray[y][x]; //if invalid indices, let the array throw the exception
    }

    private static int getPowerOfTwoGreaterThan(int num) {
        return (int)Math.pow(2, Math.ceil(Math.log(num)/Math.log(2)));
    }
}
