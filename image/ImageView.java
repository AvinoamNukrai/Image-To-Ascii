package image;

import java.awt.*;

/**
 * A package-private class of the package image.
 * @author Dan Nirel
 */
class ImageView implements Image {
    private final Image img;
    private final int startX;
    private final int startY;
    private final int width;
    private final int height;
    private int hashCodeCache = -1;

    public ImageView(Image img, int startX, int startY, int width, int height) {
        if(startX < 0 || width <= 0 || startX+width > img.getWidth() ||
           startY < 0 || height <= 0 || startY+height > img.getHeight())
            throw new IllegalArgumentException();
        this.width  = width;
        this.height = height;
        if(img instanceof ImageView) {
            /*
                if this is an ImageView within an ImageView we need the absolute coords
                in the image, and to save the base img,
                or hashCode and equals become a headache.
                Using instanceof to check whether an object is of this
                concrete class is forgivable, since the code won't
                have to change when other types of Images are added.
             */
            var imgView = (ImageView) img;
            this.startX = startX + imgView.startX;
            this.startY = startY + imgView.startY;
            this.img    = imgView.img;
        }
        else {
            this.startX = startX;
            this.startY = startY;
            this.img    = img;
        }
    }

    @Override
    public Color getPixel(int x, int y) {
        if(x < 0 || y < 0 || x >= getWidth() || y >= getHeight())
            throw new IndexOutOfBoundsException();
        return img.getPixel(startX+x, startY+y);
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int hashCode() {
        if(hashCodeCache == -1) {
            hashCodeCache =
                    (int)(
                    startX
                    +31*startY
                    +961*width
                    +29791*height
                    +923521*img.hashCode());
        }
        return hashCodeCache;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof ImageView))
            return false;
        ImageView other = (ImageView)obj;
        return this.img.equals(other.img) &&
               this.startX == other.startX && this.startY == other.startY &&
               this.width == other.width && this.height == other.height;
    }

    /*
    for debugging
     */
    @Override
    public String toString() {
        return String.format("%d %d %d %d of ", startX, startY, width, height)+img.toString();
    }
}
