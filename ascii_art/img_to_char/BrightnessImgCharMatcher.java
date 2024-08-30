package ascii_art.img_to_char;

// Written by Avinoam Nukrai, Winter 2021 Hebrew U - OOP course

import image.Image;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;


/**
 * This class his the responsible for calculate the right ascii chars for the final
 * ascii image and in the brightness by some calculation.
 */
public class BrightnessImgCharMatcher {

    // Constants
    private static final double RED_FACTOR_GRAY = 0.2126;
    private static final double GREEN_FACTOR_GRAY = 0.7152;
    private static final double BLUE_FACTOR_GRAY = 0.0722;
    private static final int NUM_OF_PIXELS_IN_IMAGE = 16;
    private static final double MAX_VAL_PIX = 255;
    private final HashMap<Image, Double> cache = new HashMap<>();

    // Data members
    private final Image image;
    private final String fontName;

    // Ctor
    public BrightnessImgCharMatcher(Image image, String fontName){
        this.image = image;
        this.fontName = fontName;
    }

    // ~~~~~~~~~~~~~~ Methods ~~~~~~~~~~~~~~
    /**
     * This method is returning the ascii chars that build the actual ascii Image
     * @param numCharsInRow - the number of chars in each row in the final Image
     * @param charSet - array of all possible ascii chars in the final Image
     * @return char 2d array - the final (!) ascii chars that build the final Image
     */
    public char [][] chooseChars(int numCharsInRow, Character[] charSet){
        double[] linearStrechedCharValue = linearStretch(getPixelsBrightness(charSet));
        return convertImageToAscii(linearStrechedCharValue, charSet, numCharsInRow);
    }

    /**
     * This method is creates the actual chars 2d array that build the final Image
     * @param linearStretchedChars - array of the chars after linear streche
     * @param charSet - all the possible chars that will build the final Image
     * @param numCharsInRow - The number of ascii chars in each line of the final Image
     * @return char 2d array - the ascii chars that build the final Image
     */
    private char[][] convertImageToAscii(double [] linearStretchedChars, Character[] charSet, int numCharsInRow){
        int pixels = image.getWidth() / numCharsInRow;
        int col = 0;
        int row = 0;
        char[][] asciiArt = new char[image.getHeight()/pixels][image.getWidth()/pixels];
        for(Image cutImage : image.squareSubImagesOfSize(pixels)) {
            double avgCutImageBrightness = getAveragePixelsBrightness(cutImage);
            int curIndexForAsciiChar = findClosestChar(linearStretchedChars, avgCutImageBrightness);
            char charVal = charSet[curIndexForAsciiChar];
            int divisionFactor = asciiArt[0].length;
            asciiArt[row][col % divisionFactor] = charVal;
            col++;
            if (col % divisionFactor == 0) row++; // reach the end of the row
        }
        return asciiArt;
    }

    /**
     * This method is searching (and finds!) the closest index of char in terms of brightness compare to the given
     * averageBrightness that we calc already in getAveragePixelsBrightness method.
     * @param linearStretchedChars - array of doubles, represent the linear stretch of the charSet items
     * @param value - double to compare to.
     * @return - int, the index of the closest char (in brightness)
     */
    private static int findClosestChar(double[] linearStretchedChars, double value){
        int idxClosestChar = 0;
        for (int currIdx = 1; currIdx < linearStretchedChars.length; currIdx++){
            if (Math.abs(value - linearStretchedChars[idxClosestChar]) >
                    Math.abs(value - linearStretchedChars[currIdx])) {
                idxClosestChar = currIdx;
            }
        }
        return idxClosestChar;
    }


    /**
     * This method calc and returns the average pixels brightness of an image.
     * @param img - an image to work on
     * @return avg - float that represent the brightness average value
     */
    private double getAveragePixelsBrightness(Image img){
        if (!this.cache.containsKey(img)){ // Check if the given image is already in the data set of all the images
            double newGreyPixel;
            int blueAmount = 0;
            int greenAmount = 0;
            int redAmount = 0;
            double counter = 0;
            for (Color pixel : img.pixels()) {
                blueAmount += pixel.getBlue(); greenAmount += pixel.getGreen(); redAmount += pixel.getRed();
                counter++;
            }
            newGreyPixel = blueAmount * BLUE_FACTOR_GRAY + greenAmount * GREEN_FACTOR_GRAY + redAmount * RED_FACTOR_GRAY;
            double imgGrayAvg = newGreyPixel / counter / MAX_VAL_PIX;
            this.cache.put(img, imgGrayAvg);
            return imgGrayAvg;
        }
        return this.cache.get(img);
    }

    /**
     * Calculates the brightness of each char in the ascii char data set of the program
     * @param charSet - all possible ascii chars
     * @return an array with the char's brightness
     */
    private double[] getPixelsBrightness(Character[] charSet){
        double[] brightnessArray = new double[charSet.length];
        int numOfWhitePixels = 0;
        int totalPixelsCounter = 0;
        for (int i = 0; i < charSet.length; i++){
            boolean[][] booleanArrayOfChar = CharRenderer.getImg(charSet[i], NUM_OF_PIXELS_IN_IMAGE, fontName);
            for (int j = 0; j < booleanArrayOfChar.length; j++){ // needs to be equal to NUM_OF_PIXELS_IN_IMAGE
                for (int k = 0; k < booleanArrayOfChar[0].length; k++){ // same as up ^
                    if (booleanArrayOfChar[j][k]){
                        numOfWhitePixels++;
                    }
                    totalPixelsCounter++;
                }
            }
            double brightnessFactor = (double)numOfWhitePixels / (double)(totalPixelsCounter);
            brightnessArray[i] = brightnessFactor;
            numOfWhitePixels = 0;
            totalPixelsCounter = 0;
        }
        return brightnessArray;
    }

    /**
     * This method is calc and returns a linear stretch of the brightness array
     * @param brightnessArray - the brightness of each char
     * @return array of brightness after linear strecth
     */
    private static double[] linearStretch(double[] brightnessArray){
        double[] brightnessArrayCopy = brightnessArray.clone();
        double[] linearStretchArray = new double[brightnessArray.length];
        Arrays.sort(brightnessArrayCopy);
        double minBrightness = brightnessArrayCopy[0];
        double maxBrightness = brightnessArrayCopy[brightnessArray.length - 1];
        double deltaMiniMaxi = maxBrightness - minBrightness;
        for (int i = 0; i < brightnessArray.length; i++){
            double charDelta = brightnessArray[i] - minBrightness;
            double linearStretchVal = charDelta / deltaMiniMaxi;
            linearStretchArray[i] = linearStretchVal;
        }
        return linearStretchArray;
    }

}
