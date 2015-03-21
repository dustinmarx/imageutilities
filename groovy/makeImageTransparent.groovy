#!/usr/bin/env groovy
// makeImageTransparent.groovy

import java.awt.Color
import java.awt.Graphics2D
import java.awt.Image
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.awt.image.FilteredImageSource
import java.awt.image.RGBImageFilter
import javax.imageio.ImageIO

/*
 * Script containing code for converting an image's white background to be
 * transparent, adapted with minor changes from StackOverflow thread "How to
 * make a color transparent in a BufferedImage and save as PNG"
 * (http://stackoverflow.com/questions/665406/how-to-make-a-color-transparent-in-a-bufferedimage-and-save-as-png).
 *
 * Command-line arguments: only one argument is required
 * with the first (required) argument being the path/name of the source
 * image and the second (optional) argument being the path/name of the
 * destination file.
 */

if (args.length < 1)
{
   println "A source image file must be provided."
   System.exit(-1);
}

def inputFileName = args[0];
def decimalPosition = inputFileName.lastIndexOf(".")
def outputFileName = args.length > 1 ? args[1] : inputFileName.substring(0,decimalPosition)+".copy.png"

println "Copying file ${inputFileName} to ${outputFileName}"

def input = new File(inputFileName)
def source = ImageIO.read(input)

def color = source.getRGB(0, 0)

def imageWithTransparency = makeColorTransparent(source, new Color(color))

def final BufferedImage transparentImage = imageToBufferedImage(imageWithTransparency)

def out = new File(outputFileName)
ImageIO.write(transparentImage, "PNG", out)



/**
 * Convert Image to BufferedImage.
 *
 * @param image Image to be converted to BufferedImage.
 * @return BufferedImage corresponding to provided Image.
 */
def BufferedImage imageToBufferedImage(final Image image)
{
   def bufferedImage =
      new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
   def g2 = bufferedImage.createGraphics();
   g2.drawImage(image, 0, 0, null);
   g2.dispose();
   return bufferedImage;
}

/**
 * Make provided image transparent wherever color matches the provided color.
 *
 * @param im BufferedImage whose color will be made transparent.
 * @param color Color in provided image which will be made transparent.
 * @return Image with transparency applied.
 */
public static Image makeColorTransparent(final BufferedImage im, final Color color)
{
   def filter = new CustomImageFilter(color)
   def ip = new FilteredImageSource(im.getSource(), filter)
   return Toolkit.getDefaultToolkit().createImage(ip)
}

/**
 * Was 'anonymous inner class' in Java version.
 */
public class CustomImageFilter extends RGBImageFilter
{
   private int markerRGB

   public CustomImageFilter(final Color color)
   {
      // the color we are looking for (white)... Alpha bits are set to opaque
      markerRGB = color.getRGB() | 0xFFFFFFFF
   }

   public int filterRGB(final int x, final int y, final int rgb)
   {
      if ((rgb | 0xFF000000) == markerRGB)
      {
         // Mark the alpha bits as zero - transparent
         return 0x00FFFFFF & rgb
      }
      else
      {
         // nothing to do
         return rgb
      }
   }   
}
