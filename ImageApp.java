/*
  ImageApp: 
 */
import java.awt.Color;

public class ImageApp
{
  public static void main(String[] args)
  {

    // use any file from the lib folder
    String pictureFile = "lib/beach.jpg";

    // Get an image, get 2d array of pixels, show a color of a pixel, and display the image
    Picture origImg = new Picture(pictureFile);
    Pixel[][] origPixels = origImg.getPixels2D();
    System.out.println(origPixels[0][0].getColor());
    origImg.explore();

    // Image #1 Using the original image and pixels, recolor an image by changing the RGB color of each Pixel
    Picture recoloredImg = new Picture(pictureFile);
    Pixel[][] recoloredPixels = recoloredImg.getPixels2D();

    /* to be implemented */
    for (int r = 0; r < recoloredPixels.length; r++) {
      for (int c = 0; c < recoloredPixels[0].length; c++) {
      Pixel p = recoloredPixels[r][c];
      Color col = p.getColor();

      int red = col.getRed();
      int green = col.getGreen();
      int blue = col.getBlue();

      // example recolor: warmer look
      red = Math.min(255, red + 40);
      blue = Math.max(0, blue - 30);

      p.setColor(new Color(red, green, blue));
    }
  }
recoloredImg.explore();
    // Image #2 Using the original image and pixels, create a photographic negative of the image
    Picture negImg = new Picture(pictureFile);
    Pixel[][] negPixels = negImg.getPixels2D();

    /* to be implemented */
    for (int r = 0; r < negPixels.length; r++) {
      for (int c = 0; c < negPixels[0].length; c++) {
        Pixel p = negPixels[r][c];
        Color col = p.getColor();

        int red = 255 - col.getRed();
        int green = 255 - col.getGreen();
        int blue = 255 - col.getBlue();

        p.setColor(new Color(red, green, blue));
      }
    }
negImg.explore();
    // Image #3 Using the original image and pixels, create a grayscale version of the image
    Picture grayscaleImg = new Picture(pictureFile);
    Pixel[][] grayscalePixels = grayscaleImg.getPixels2D();

    /* to be implemented */
    for (int r = 0; r < grayscalePixels.length; r++) {
      for (int c = 0; c < grayscalePixels[0].length; c++) {
        Pixel p = grayscalePixels[r][c];
        Color col = p.getColor();

        int avg = (col.getRed() + col.getGreen() + col.getBlue()) / 3;
        p.setColor(new Color(avg, avg, avg));
      }
    }
grayscaleImg.explore();
    // Image #4 Using the original image and pixels, rotate it 180 degrees
    Picture upsidedownImage = new Picture(pictureFile);
    Pixel[][] upsideDownPixels = upsidedownImage.getPixels2D();

    /* to be implemented */
    int h = upsideDownPixels.length;
    int w = upsideDownPixels[0].length;

    for (int r = 0; r < h / 2; r++) {
      for (int c = 0; c < w; c++) {
        Color temp = upsideDownPixels[r][c].getColor();
        upsideDownPixels[r][c].setColor(upsideDownPixels[h - 1 - r][w - 1 - c].getColor());
        upsideDownPixels[h - 1 - r][w - 1 - c].setColor(temp);
      }
    }

// if height is odd, flip the middle row
if (h % 2 == 1) {
  int mid = h / 2;
  for (int c = 0; c < w / 2; c++) {
    Color temp = upsideDownPixels[mid][c].getColor();
    upsideDownPixels[mid][c].setColor(upsideDownPixels[mid][w - 1 - c].getColor());
    upsideDownPixels[mid][w - 1 - c].setColor(temp);
  }
}

upsidedownImage.explore();
    // Image #5 Using the original image and pixels, rotate image 90
    Picture rotateImg = new Picture(pictureFile);
    Pixel[][] rotatePixels = rotateImg.getPixels2D();

    /* to be implemented */
    int origH = origPixels.length;
    int origW = origPixels[0].length;

    // rotated 90 clockwise has height = origW, width = origH
    Picture rotated90 = new Picture(origW, origH);
    Pixel[][] rot90Pix = rotated90.getPixels2D();

    for (int r = 0; r < origH; r++) {
      for (int c = 0; c < origW; c++) {
        rot90Pix[c][origH - 1 - r].setColor(origPixels[r][c].getColor());
      }
    }

rotated90.explore();
    // Image #6 Using the original image and pixels, rotate image -90
    Picture rotateImg2 = new Picture(pictureFile);
    Pixel[][] rotatePixels2 = rotateImg2.getPixels2D();

    /* to be implemented */
    int origH2 = origPixels.length;
    int origW2 = origPixels[0].length;

    // rotated 270 clockwise
    Picture rotated270 = new Picture(origW2, origH2);
    Pixel[][] rot270Pix = rotated270.getPixels2D();

    for (int r = 0; r < origH2; r++) {
      for (int c = 0; c < origW2; c++) {
        rot270Pix[origW2 - 1 - c][r].setColor(origPixels[r][c].getColor());
      }
    }

rotated270.explore();

    // Final Image: Add a small image to a larger one

    /* to be implemented */
    String bigFile = "lib/bridge.jpg";   
    String smallFile = "lib2/bird.png";    

    Picture big = new Picture(bigFile);
    Picture small = new Picture(smallFile);

    Pixel[][] bigPix = big.getPixels2D();
    Pixel[][] smallPix = small.getPixels2D();

    int startRow = 50;  // change placement
    int startCol = 50;

    for (int r = 0; r < smallPix.length; r++) {
      for (int c = 0; c < smallPix[0].length; c++) {

        int bigR = startRow + r;
        int bigC = startCol + c;

        if (bigR >= 0 && bigR < bigPix.length && bigC >= 0 && bigC < bigPix[0].length) {

          Color sc = smallPix[r][c].getColor();
          int red = sc.getRed();
          int green = sc.getGreen();
          int blue = sc.getBlue();

          // remove white background (treat near-white as transparent)
          if (!(red > 240 && green > 240 && blue > 240)) {
            bigPix[bigR][bigC].setColor(sc);
          }
        }
      }
    }

big.explore();



    // for testing  2D algorithms
    int[][] test1 = { { 1, 2, 3, 4 },
        { 5, 6, 7, 8 },
        { 9, 10, 11, 12 },
        { 13, 14, 15, 16 } };
    int[][] test2 = new int[4][4];


  }
}
